<!-- Redis 监控页面 -->
<template>
  <div class="redis-monitor-page art-full-height">
    <ElCard class="art-table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>Redis 监控</span>
          <ElButton type="primary" :icon="Refresh" @click="refreshData" :loading="loading">
            刷新
          </ElButton>
        </div>
      </template>

      <div v-loading="loading">
        <!-- Redis 未配置或连接失败时展示空状态 -->
        <ElEmpty
          v-if="redisInfo && redisInfo.available === false"
          :description="redisInfo.message || 'Redis 未配置或未启用'"
          class="redis-empty"
        />
        <!-- Redis 正常时展示监控卡片 -->
        <ElRow v-else-if="redisInfo && redisInfo.available !== false" :gutter="20">
          <!-- Redis 基本信息 -->
          <ElCol :xs="24" :sm="12" :md="8">
            <ElCard shadow="hover">
              <template #header>基本信息</template>
              <ElDescriptions :column="1" border>
                <ElDescriptionsItem label="Redis 版本">
                  {{ redisInfo.version || '-' }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="运行模式">
                  {{ redisInfo.mode || '-' }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="端口">
                  {{ redisInfo.port || '-' }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="连接状态">
                  <ElTag :type="redisInfo.connected ? 'success' : 'danger'">
                    {{ redisInfo.connected ? '已连接' : '未连接' }}
                  </ElTag>
                </ElDescriptionsItem>
              </ElDescriptions>
            </ElCard>
          </ElCol>

          <!-- 内存信息 -->
          <ElCol :xs="24" :sm="12" :md="8">
            <ElCard shadow="hover">
              <template #header>内存信息</template>
              <ElDescriptions :column="1" border>
                <ElDescriptionsItem label="已使用内存">
                  {{ formatSize(redisInfo.memory?.usedMemory || 0) }} MB
                </ElDescriptionsItem>
                <ElDescriptionsItem label="最大内存">
                  {{ formatSize(redisInfo.memory?.maxMemory || 0) }} MB
                </ElDescriptionsItem>
                <ElDescriptionsItem label="内存使用率">
                  <span :class="getUsageClass(redisInfo.memory?.usage || 0)">
                    {{ formatPercent(redisInfo.memory?.usage || 0) }}
                  </span>
                </ElDescriptionsItem>
              </ElDescriptions>
              <ElProgress
                :percentage="redisInfo.memory?.usage || 0"
                :color="getProgressColor(redisInfo.memory?.usage || 0)"
                :stroke-width="8"
                style="margin-top: 10px"
              />
            </ElCard>
          </ElCol>

          <!-- 客户端信息 -->
          <ElCol :xs="24" :sm="12" :md="8">
            <ElCard shadow="hover">
              <template #header>客户端信息</template>
              <ElDescriptions :column="1" border>
                <ElDescriptionsItem label="已连接客户端数">
                  {{ redisInfo.clients?.connectedClients || 0 }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="阻塞客户端数">
                  <ElTag type="warning">
                    {{ redisInfo.clients?.blockedClients || 0 }}
                  </ElTag>
                </ElDescriptionsItem>
              </ElDescriptions>
            </ElCard>
          </ElCol>
        </ElRow>

        <!-- 统计信息 -->
        <ElRow :gutter="20" style="margin-top: 20px" v-if="redisInfo">
          <ElCol :xs="24" :sm="12" :md="12">
            <ElCard shadow="hover">
              <template #header>键值统计</template>
              <ElDescriptions :column="1" border>
                <ElDescriptionsItem label="键总数">
                  {{ formatNumber(redisInfo.keys?.totalKeys || 0) }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="过期键数">
                  {{ formatNumber(redisInfo.keys?.expiredKeys || 0) }}
                </ElDescriptionsItem>
              </ElDescriptions>
            </ElCard>
          </ElCol>

          <ElCol :xs="24" :sm="12" :md="12">
            <ElCard shadow="hover">
              <template #header>命令统计</template>
              <ElDescriptions :column="1" border>
                <ElDescriptionsItem label="总命令数">
                  {{ formatNumber(redisInfo.commands?.totalCommands || 0) }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="每秒命令数">
                  {{ formatNumber(redisInfo.commands?.commandsPerSecond || 0, 2) }}
                </ElDescriptionsItem>
              </ElDescriptions>
            </ElCard>
          </ElCol>
        </ElRow>
      </div>
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { Refresh } from '@element-plus/icons-vue'
  import { fetchGetRedisMonitorInfo } from '@/api/monitor/redis'
  import type { RedisMonitorInfo } from '@/types/api/monitor'

  defineOptions({ name: 'RedisMonitor' })

  const loading = ref(false)
  const redisInfo = ref<RedisMonitorInfo | null>(null)
  let refreshTimer: number | null = null

  // 格式化百分比
  const formatPercent = (value: number) => {
    return `${value.toFixed(2)}%`
  }

  // 格式化大小
  const formatSize = (value: number) => {
    return value.toLocaleString()
  }

  // 格式化数字
  const formatNumber = (value: number, decimals: number = 0) => {
    return value.toLocaleString('zh-CN', {
      minimumFractionDigits: decimals,
      maximumFractionDigits: decimals
    })
  }

  // 获取使用率样式类
  const getUsageClass = (usage: number) => {
    if (usage >= 90) return 'text-danger'
    if (usage >= 70) return 'text-warning'
    return 'text-success'
  }

  // 获取进度条颜色
  const getProgressColor = (usage: number) => {
    if (usage >= 90) return '#f56c6c'
    if (usage >= 70) return '#e6a23c'
    return '#67c23a'
  }

  /**
   * 获取 Redis 监控数据
   * - 成功：request 层直接返回 data，赋值给 redisInfo
   * - 失败：由 HTTP 拦截器统一 showError 提示，此处仅记录日志并确保 loading 关闭
   */
  const getData = async () => {
    loading.value = true
    try {
      const data = await fetchGetRedisMonitorInfo()
      redisInfo.value = data
    } catch (error) {
      if (import.meta.env.DEV) {
        console.error('获取 Redis 监控信息失败:', error)
      }
      // 如果获取失败，设置为不可用状态
      redisInfo.value = {
        available: false,
        message: '获取 Redis 监控信息失败'
      }
    } finally {
      loading.value = false
    }
  }

  // 刷新数据
  const refreshData = () => {
    getData()
  }

  // 自动刷新（每10秒）
  const startAutoRefresh = () => {
    if (refreshTimer) {
      clearInterval(refreshTimer)
    }
    refreshTimer = window.setInterval(() => {
      getData()
    }, 10000)
  }

  // 停止自动刷新
  const stopAutoRefresh = () => {
    if (refreshTimer) {
      clearInterval(refreshTimer)
      refreshTimer = null
    }
  }

  onMounted(() => {
    getData()
    startAutoRefresh()
  })

  onBeforeUnmount(() => {
    stopAutoRefresh()
  })
</script>

<style scoped lang="scss">
  .redis-monitor-page {
    padding: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .text-success {
    color: var(--el-color-success);
  }

  .text-warning {
    color: var(--el-color-warning);
  }

  .text-danger {
    color: var(--el-color-danger);
  }

  .redis-empty {
    padding: 48px 0;
  }
</style>
