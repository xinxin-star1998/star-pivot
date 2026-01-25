<!-- Druid 监控页面 -->
<template>
  <div class="druid-monitor-page art-full-height">
    <ElCard class="art-table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>Druid 数据库监控</span>
          <ElButton type="primary" :icon="Refresh" @click="refreshData" :loading="loading">
            刷新
          </ElButton>
        </div>
      </template>

      <div v-loading="loading">
        <!-- 数据源非 Druid 或未配置时展示空状态 -->
        <ElEmpty
          v-if="druidInfo && druidInfo.available === false"
          :description="druidInfo.message || '数据源不是 Druid 数据源或数据源未配置'"
          class="druid-empty"
        />
        <!-- 数据源为 Druid 时展示监控卡片 -->
        <ElRow v-else-if="druidInfo && druidInfo.available !== false" :gutter="20">
          <!-- 数据源信息 -->
          <ElCol :xs="24" :sm="12" :md="8">
            <ElCard shadow="hover">
              <template #header>数据源信息</template>
              <ElDescriptions :column="1" border>
                <ElDescriptionsItem label="数据源名称">
                  {{ druidInfo.name || '-' }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="数据库类型">
                  {{ druidInfo.dbType || '-' }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="驱动类名">
                  {{ druidInfo.driverClassName || '-' }}
                </ElDescriptionsItem>
              </ElDescriptions>
            </ElCard>
          </ElCol>

          <!-- 连接池信息 -->
          <ElCol :xs="24" :sm="12" :md="8">
            <ElCard shadow="hover">
              <template #header>连接池信息</template>
              <ElDescriptions :column="1" border>
                <ElDescriptionsItem label="初始连接数">
                  {{ druidInfo.connectionPool?.initialSize || 0 }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="最小空闲连接数">
                  {{ druidInfo.connectionPool?.minIdle || 0 }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="最大活跃连接数">
                  {{ druidInfo.connectionPool?.maxActive || 0 }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="当前连接数">
                  {{ druidInfo.connectionPool?.activeCount || 0 }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="活跃连接峰值">
                  {{ druidInfo.connectionPool?.activePeak || 0 }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="连接池使用率">
                  <span :class="getUsageClass(druidInfo.connectionPool?.usage || 0)">
                    {{ formatPercent(druidInfo.connectionPool?.usage || 0) }}
                  </span>
                </ElDescriptionsItem>
              </ElDescriptions>
              <ElProgress
                :percentage="druidInfo.connectionPool?.usage || 0"
                :color="getProgressColor(druidInfo.connectionPool?.usage || 0)"
                :stroke-width="8"
                style="margin-top: 10px"
              />
            </ElCard>
          </ElCol>

          <!-- SQL 统计信息 -->
          <ElCol :xs="24" :sm="12" :md="8">
            <ElCard shadow="hover">
              <template #header>SQL 统计信息</template>
              <ElDescriptions :column="1" border>
                <ElDescriptionsItem label="SQL 执行总数">
                  {{ formatNumber(druidInfo.sqlStat?.executeCount || 0) }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="SQL 执行总耗时">
                  {{ formatNumber(druidInfo.sqlStat?.executeMillisTotal || 0) }} ms
                </ElDescriptionsItem>
                <ElDescriptionsItem label="平均执行时间">
                  {{ formatNumber(druidInfo.sqlStat?.executeMillisAvg || 0, 2) }} ms
                </ElDescriptionsItem>
                <ElDescriptionsItem label="慢 SQL 数量">
                  <ElTag type="warning">
                    {{ formatNumber(druidInfo.sqlStat?.slowSqlCount || 0) }}
                  </ElTag>
                </ElDescriptionsItem>
                <ElDescriptionsItem label="错误 SQL 数量">
                  <ElTag type="danger">
                    {{ formatNumber(druidInfo.sqlStat?.errorSqlCount || 0) }}
                  </ElTag>
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
  import { fetchGetDruidMonitorInfo } from '@/api/monitor/druid'
  import type { DruidMonitorInfo } from '@/types/api/monitor'

  defineOptions({ name: 'DruidMonitor' })

  const loading = ref(false)
  const druidInfo = ref<DruidMonitorInfo | null>(null)
  let refreshTimer: number | null = null

  // 格式化百分比
  const formatPercent = (value: number) => {
    return `${value.toFixed(2)}%`
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
   * 获取 Druid 监控数据
   * - 成功：request 层直接返回 data，赋值给 druidInfo
   * - 失败：由 HTTP 拦截器统一 showError 提示，此处仅记录日志并确保 loading 关闭
   */
  const getData = async () => {
    loading.value = true
    try {
      const data = await fetchGetDruidMonitorInfo()
      druidInfo.value = data
    } catch (error) {
      if (import.meta.env.DEV) {
        console.error('获取 Druid 监控信息失败:', error)
      }
      // 如果获取失败，设置为不可用状态
      druidInfo.value = {
        available: false,
        message: '获取 Druid 监控信息失败'
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
  .druid-monitor-page {
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

  .druid-empty {
    padding: 48px 0;
  }
</style>
