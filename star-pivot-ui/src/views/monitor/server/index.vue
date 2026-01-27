<!-- 服务器监控页面 -->
<template>
  <div class="server-monitor-page">
    <ElCard class="art-table-card server-monitor-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>服务器监控</span>
          <ElButton type="primary" :icon="Refresh" @click="refreshData" :loading="loading">
            刷新
          </ElButton>
        </div>
      </template>

      <div v-loading="loading">
        <ElRow :gutter="16" v-if="serverInfo">
          <!-- CPU 信息 -->
          <ElCol :xs="24" :sm="12" :md="8" :lg="6">
            <ElCard shadow="hover" class="info-card">
              <div class="info-item">
                <div class="info-label">CPU 核心数</div>
                <div class="info-value">{{ serverInfo.cpu?.cpuNum || 0 }}</div>
              </div>
              <div class="info-item">
                <div class="info-label">CPU 使用率</div>
                <div class="info-value" :class="getUsageClass(serverInfo.cpu?.used || 0)">
                  {{ formatPercent(serverInfo.cpu?.used || 0) }}
                </div>
              </div>
              <ElProgress
                :percentage="serverInfo.cpu?.used || 0"
                :color="getProgressColor(serverInfo.cpu?.used || 0)"
                :stroke-width="6"
              />
            </ElCard>
          </ElCol>

          <!-- 内存信息 -->
          <ElCol :xs="24" :sm="12" :md="8" :lg="6">
            <ElCard shadow="hover" class="info-card">
              <div class="info-item">
                <div class="info-label">内存总量</div>
                <div class="info-value">{{ formatSize(serverInfo.memory?.total || 0) }} MB</div>
              </div>
              <div class="info-item">
                <div class="info-label">已用内存</div>
                <div class="info-value" :class="getUsageClass(serverInfo.memory?.usage || 0)">
                  {{ formatSize(serverInfo.memory?.used || 0) }} MB
                </div>
              </div>
              <ElProgress
                :percentage="serverInfo.memory?.usage || 0"
                :color="getProgressColor(serverInfo.memory?.usage || 0)"
                :stroke-width="6"
              />
            </ElCard>
          </ElCol>

          <!-- JVM 信息 -->
          <ElCol :xs="24" :sm="12" :md="8" :lg="6">
            <ElCard shadow="hover" class="info-card">
              <div class="info-item">
                <div class="info-label">JVM 最大内存</div>
                <div class="info-value">{{ formatSize(serverInfo.jvm?.max || 0) }} MB</div>
              </div>
              <div class="info-item">
                <div class="info-label">JVM 使用率</div>
                <div class="info-value" :class="getUsageClass(serverInfo.jvm?.usage || 0)">
                  {{ formatPercent(serverInfo.jvm?.usage || 0) }}
                </div>
              </div>
              <ElProgress
                :percentage="serverInfo.jvm?.usage || 0"
                :color="getProgressColor(serverInfo.jvm?.usage || 0)"
                :stroke-width="6"
              />
            </ElCard>
          </ElCol>

          <!-- 磁盘信息 -->
          <ElCol :xs="24" :sm="12" :md="8" :lg="6">
            <ElCard shadow="hover" class="info-card">
              <div class="info-item">
                <div class="info-label">磁盘总容量</div>
                <div class="info-value">{{ formatSize(serverInfo.disk?.total || 0) }} GB</div>
              </div>
              <div class="info-item">
                <div class="info-label">磁盘使用率</div>
                <div class="info-value" :class="getUsageClass(serverInfo.disk?.usage || 0)">
                  {{ formatPercent(serverInfo.disk?.usage || 0) }}
                </div>
              </div>
              <ElProgress
                :percentage="serverInfo.disk?.usage || 0"
                :color="getProgressColor(serverInfo.disk?.usage || 0)"
                :stroke-width="6"
              />
            </ElCard>
          </ElCol>
        </ElRow>

        <!-- 健康检查状态 -->
        <ElRow :gutter="15" style="margin-top: 5px" v-if="healthReport">
          <ElCol :xs="24">
            <ElCard shadow="hover">
              <template #header>
                <div class="card-header">
                  <span>系统健康检查</span>
                  <ElTag :type="getHealthTagType(healthReport.overall)" size="large" effect="dark">
                    {{ getOverallStatusText(healthReport.overall) }}
                  </ElTag>
                </div>
              </template>
              <ElRow :gutter="16">
                <!-- 数据库健康状态 -->
                <ElCol :xs="24" :sm="12" :md="6" v-if="healthReport.database">
                  <div class="health-item">
                    <div class="health-label">数据库</div>
                    <div
                      class="health-status"
                      :class="getHealthStatusClass(healthReport.database.healthy)"
                    >
                      <ElIcon :size="20">
                        <Check v-if="healthReport.database.healthy" />
                        <Warning v-else />
                      </ElIcon>
                      <span>{{ healthReport.database.healthy ? '正常' : '异常' }}</span>
                    </div>
                    <div
                      class="health-detail"
                      v-if="healthReport.database.activeCount !== undefined"
                    >
                      连接池: {{ healthReport.database.activeCount }}/{{
                        healthReport.database.maxActive
                      }}
                    </div>
                    <div class="health-detail" v-if="healthReport.database.usage !== undefined">
                      使用率: {{ healthReport.database.usage.toFixed(1) }}%
                    </div>
                    <div class="health-error" v-if="healthReport.database.error">
                      {{ healthReport.database.error }}
                    </div>
                  </div>
                </ElCol>

                <!-- Redis健康状态 -->
                <ElCol :xs="24" :sm="12" :md="6" v-if="healthReport.redis">
                  <div class="health-item">
                    <div class="health-label">Redis</div>
                    <div
                      class="health-status"
                      :class="getHealthStatusClass(healthReport.redis.healthy)"
                    >
                      <ElIcon :size="20">
                        <Check v-if="healthReport.redis.healthy" />
                        <Warning v-else />
                      </ElIcon>
                      <span>{{ healthReport.redis.healthy ? '正常' : '异常' }}</span>
                    </div>
                    <div class="health-error" v-if="healthReport.redis.error">
                      {{ healthReport.redis.error }}
                    </div>
                    <div class="health-error" v-if="healthReport.redis.message">
                      {{ healthReport.redis.message }}
                    </div>
                  </div>
                </ElCol>

                <!-- 磁盘健康状态 -->
                <ElCol :xs="24" :sm="12" :md="6" v-if="healthReport.disk">
                  <div class="health-item">
                    <div class="health-label">磁盘</div>
                    <div
                      class="health-status"
                      :class="getHealthStatusClass(healthReport.disk.healthy)"
                    >
                      <ElIcon :size="20">
                        <Check v-if="healthReport.disk.healthy" />
                        <Warning v-else />
                      </ElIcon>
                      <span>{{ healthReport.disk.healthy ? '正常' : '异常' }}</span>
                    </div>
                    <div class="health-detail" v-if="healthReport.disk.usage !== undefined">
                      使用率: {{ healthReport.disk.usage.toFixed(1) }}%
                    </div>
                    <div class="health-detail" v-if="healthReport.disk.free !== undefined">
                      剩余: {{ formatSize(healthReport.disk.free) }} GB
                    </div>
                    <div class="health-error" v-if="healthReport.disk.error">
                      {{ healthReport.disk.error }}
                    </div>
                  </div>
                </ElCol>

                <!-- JVM健康状态 -->
                <ElCol :xs="24" :sm="12" :md="6" v-if="healthReport.jvm">
                  <div class="health-item">
                    <div class="health-label">JVM</div>
                    <div
                      class="health-status"
                      :class="getHealthStatusClass(healthReport.jvm.healthy)"
                    >
                      <ElIcon :size="20">
                        <Check v-if="healthReport.jvm.healthy" />
                        <Warning v-else />
                      </ElIcon>
                      <span>{{ healthReport.jvm.healthy ? '正常' : '异常' }}</span>
                    </div>
                    <div class="health-detail" v-if="healthReport.jvm.usage !== undefined">
                      内存使用: {{ healthReport.jvm.usage.toFixed(1) }}%
                    </div>
                    <div class="health-detail" v-if="healthReport.jvm.used !== undefined">
                      已用: {{ formatSize(healthReport.jvm.used) }} MB
                    </div>
                    <div class="health-error" v-if="healthReport.jvm.error">
                      {{ healthReport.jvm.error }}
                    </div>
                  </div>
                </ElCol>
              </ElRow>
              <ElDivider />
              <div class="health-footer">
                <span class="health-time" v-if="healthReport.timestamp">
                  检查时间: {{ formatTime(healthReport.timestamp) }}
                </span>
                <span class="health-duration" v-if="healthReport.duration">
                  检查耗时: {{ healthReport.duration }}ms
                </span>
              </div>
            </ElCard>
          </ElCol>
        </ElRow>

        <!-- 详细信息 -->
        <ElRow :gutter="20" style="margin-top: 16px" v-if="serverInfo">
          <!-- 系统信息 -->
          <ElCol :xs="24" :sm="12" :md="12">
            <ElCard shadow="hover">
              <template #header>系统信息</template>
              <ElDescriptions :column="1" border size="small">
                <ElDescriptionsItem label="服务器名称">
                  {{ serverInfo.system?.computerName || '-' }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="操作系统">
                  {{ serverInfo.system?.osName || '-' }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="系统架构">
                  {{ serverInfo.system?.osArch || '-' }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="服务器IP">
                  {{ serverInfo.system?.computerIp || '-' }}
                </ElDescriptionsItem>
              </ElDescriptions>
            </ElCard>
          </ElCol>

          <!-- JVM 详细信息 -->
          <ElCol :xs="24" :sm="12" :md="12">
            <ElCard shadow="hover">
              <template #header>JVM 信息</template>
              <ElDescriptions :column="1" border size="small">
                <ElDescriptionsItem label="JVM 名称">
                  {{ serverInfo.jvm?.name || '-' }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="JVM 版本">
                  {{ serverInfo.jvm?.version || '-' }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="运行时长">
                  {{ formatDuration(serverInfo.jvm?.runTime || 0) }}
                </ElDescriptionsItem>
                <ElDescriptionsItem label="启动时间">
                  {{ formatTime(serverInfo.jvm?.startTime || 0) }}
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
  import { Refresh, Check, Warning } from '@element-plus/icons-vue'
  import { fetchGetServerInfo } from '@/api/monitor/server'
  import { fetchGetHealthCheck } from '@/api/monitor/api'
  import type { ServerInfo } from '@/types/api/monitor'
  import type { HealthCheckReport } from '@/types/api/monitor'

  defineOptions({ name: 'ServerMonitor' })

  const loading = ref(false)
  const serverInfo = ref<ServerInfo | null>(null)
  const healthReport = ref<HealthCheckReport | null>(null)
  let refreshTimer: number | null = null

  // 格式化百分比
  const formatPercent = (value: number) => {
    return `${value.toFixed(2)}%`
  }

  // 格式化大小
  const formatSize = (value: number) => {
    return value.toLocaleString()
  }

  // 格式化时长
  const formatDuration = (millis: number) => {
    const seconds = Math.floor(millis / 1000)
    const minutes = Math.floor(seconds / 60)
    const hours = Math.floor(minutes / 60)
    const days = Math.floor(hours / 24)

    if (days > 0) {
      return `${days}天 ${hours % 24}小时 ${minutes % 60}分钟`
    } else if (hours > 0) {
      return `${hours}小时 ${minutes % 60}分钟`
    } else if (minutes > 0) {
      return `${minutes}分钟 ${seconds % 60}秒`
    } else {
      return `${seconds}秒`
    }
  }

  // 格式化时间
  const formatTime = (timestamp: number | string) => {
    if (typeof timestamp === 'string') {
      return new Date(timestamp).toLocaleString('zh-CN')
    }
    return new Date(timestamp).toLocaleString('zh-CN')
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
   * 获取服务器监控数据
   * - 成功：request 层直接返回 data，赋值给 serverInfo
   * - 失败：由 HTTP 拦截器统一 showError 提示，此处仅记录日志并确保 loading 关闭
   */
  const getData = async () => {
    loading.value = true
    try {
      const [serverData, healthData] = await Promise.all([
        fetchGetServerInfo(),
        fetchGetHealthCheck().catch(() => null) // 健康检查失败不影响服务器信息展示
      ])
      serverInfo.value = serverData
      healthReport.value = healthData
    } catch (error) {
      if (import.meta.env.DEV) {
        console.error('获取监控数据失败:', error)
      }
    } finally {
      loading.value = false
    }
  }

  // 获取健康状态标签类型
  const getHealthTagType = (overall: string) => {
    if (overall === 'healthy') return 'success'
    if (overall === 'unhealthy') return 'warning'
    return 'danger'
  }

  // 获取整体状态文本
  const getOverallStatusText = (overall: string) => {
    if (overall === 'healthy') return '系统正常'
    if (overall === 'unhealthy') return '存在异常'
    if (overall === 'timeout') return '检查超时'
    return '检查失败'
  }

  // 获取健康状态样式类
  const getHealthStatusClass = (healthy: boolean) => {
    return healthy ? 'status-healthy' : 'status-unhealthy'
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
  .server-monitor-page {
    height: 100%;
    padding: 20px;
    overflow-y: auto;
  }

  .server-monitor-card {
    .el-card__body {
      max-height: calc(100vh - 180px);
      padding: 16px;
      overflow-y: auto;
    }
  }

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 15px;
  }

  .info-card {
    margin-bottom: 12px;

    :deep(.el-card__body) {
      padding: 12px;
    }
  }

  .info-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;

    .info-label {
      font-size: 13px;
      color: var(--el-text-color-secondary);
    }

    .info-value {
      font-size: 16px;
      font-weight: bold;

      &.text-success {
        color: var(--el-color-success);
      }

      &.text-warning {
        color: var(--el-color-warning);
      }

      &.text-danger {
        color: var(--el-color-danger);
      }
    }
  }

  .health-item {
    padding: 10px;
    margin-bottom: 8px;
    background-color: var(--el-bg-color-page);
    border-radius: 6px;

    .health-label {
      margin-bottom: 6px;
      font-size: 13px;
      font-weight: 500;
      color: var(--el-text-color-primary);
    }

    .health-status {
      display: flex;
      gap: 6px;
      align-items: center;
      margin-bottom: 6px;
      font-size: 13px;
      font-weight: 500;

      &.status-healthy {
        color: var(--el-color-success);
      }

      &.status-unhealthy {
        color: var(--el-color-warning);
      }
    }

    .health-detail {
      margin-top: 4px;
      font-size: 12px;
      color: var(--el-text-color-secondary);
    }

    .health-error {
      margin-top: 4px;
      font-size: 12px;
      color: var(--el-color-danger);
    }
  }

  .health-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 12px;
    color: var(--el-text-color-secondary);

    .health-time,
    .health-duration {
      margin: 0 8px;
    }
  }
</style>
