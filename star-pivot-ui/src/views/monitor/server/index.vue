<!-- 服务器监控页面 -->
<template>
  <div class="server-monitor-page">
    <ElCard class="art-table-card server-monitor-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <span class="title-main">服务器监控</span>
            <span class="title-sub">实时查看主机资源与运行状态</span>
          </div>
          <ElButton type="primary" :icon="Refresh" @click="refreshData" :loading="loading">
            刷新
          </ElButton>
        </div>
      </template>

      <div v-loading="loading">
        <template v-if="serverInfo">
          <ElRow :gutter="16" class="overview-row">
            <ElCol v-for="item in overviewCards" :key="item.label" :md="6" :sm="12" :xs="24">
              <div class="overview-card">
                <div class="overview-label">{{ item.label }}</div>
                <div class="overview-value">{{ item.value }}</div>
                <div class="overview-extra">{{ item.extra }}</div>
              </div>
            </ElCol>
          </ElRow>

          <ElRow :gutter="16">
            <ElCol :md="12" :xs="24">
              <ElCard class="section-card" shadow="hover">
                <template #header>
                  <div class="section-title">
                    <span>CPU</span>
                    <ElTag effect="light" type="info">核心 {{ serverInfo.cpu?.cpuNum ?? 0 }}</ElTag>
                  </div>
                </template>
                <div class="meter-group">
                  <div v-for="item in cpuUsageMeters" :key="item.label" class="meter-item">
                    <div class="meter-label">{{ item.label }}</div>
                    <ElProgress
                        :color="item.color"
                        :percentage="item.value"
                        :show-text="false"
                        :stroke-width="12"
                    />
                    <div class="meter-value">{{ formatPercent(item.value) }}</div>
                  </div>
                </div>
                <ElTable :data="cpuRows" border size="small">
                  <ElTableColumn label="属性" min-width="140" prop="label"/>
                  <ElTableColumn label="值" min-width="120" prop="value"/>
                </ElTable>
              </ElCard>
            </ElCol>
            <ElCol :md="12" :xs="24">
              <ElCard class="section-card" shadow="hover">
                <template #header>
                  <div class="section-title">
                    <span>内存</span>
                    <ElTag effect="light" type="success">系统 / JVM</ElTag>
                  </div>
                </template>
                <div class="meter-group">
                  <div v-for="item in memoryUsageMeters" :key="item.label" class="meter-item">
                    <div class="meter-label">{{ item.label }}</div>
                    <ElProgress
                        :color="item.color"
                        :percentage="item.value"
                        :show-text="false"
                        :stroke-width="12"
                    />
                    <div class="meter-value">{{ formatPercent(item.value) }}</div>
                  </div>
                </div>
                <ElTable :data="memoryRows" border size="small">
                  <ElTableColumn label="属性" min-width="140" prop="label"/>
                  <ElTableColumn label="内存" min-width="120" prop="memory"/>
                  <ElTableColumn label="JVM" min-width="120" prop="jvm"/>
                </ElTable>
              </ElCard>
            </ElCol>
          </ElRow>

          <ElCard class="section-card" shadow="hover">
            <template #header>
              <div class="section-title">
                <span>服务器信息</span>
              </div>
            </template>
            <ElDescriptions :column="2" border size="small">
              <ElDescriptionsItem v-for="item in serverRows" :key="item.label" :label="item.label">
                {{ item.value }}
              </ElDescriptionsItem>
            </ElDescriptions>
          </ElCard>

          <ElCard class="section-card" shadow="hover">
            <template #header>
              <div class="section-title">
                <span>Java虚拟机信息</span>
              </div>
            </template>
            <ElDescriptions :column="2" border size="small">
              <ElDescriptionsItem v-for="item in jvmRows" :key="item.label" :label="item.label">
                {{ item.value }}
              </ElDescriptionsItem>
            </ElDescriptions>
          </ElCard>

          <ElCard class="section-card" shadow="hover">
            <template #header>
              <div class="section-title">
                <span>磁盘状态</span>
              </div>
            </template>
            <ElTable :data="diskRows" border size="small">
              <ElTableColumn label="盘符路径" min-width="120" prop="mount"/>
              <ElTableColumn label="文件系统" min-width="100" prop="fileSystem"/>
              <ElTableColumn label="磁盘类型" min-width="140" prop="typeName"/>
              <ElTableColumn label="总大小" min-width="90" prop="total"/>
              <ElTableColumn label="可用大小" min-width="90" prop="usable"/>
              <ElTableColumn label="已用大小" min-width="90" prop="used"/>
              <ElTableColumn label="已用百分比" min-width="120" prop="usage">
                <template #default="{ row }">
                  <ElTag :type="row.usageType" effect="light">{{ row.usage }}</ElTag>
                </template>
              </ElTableColumn>
            </ElTable>
          </ElCard>
        </template>
      </div>
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { Refresh } from '@element-plus/icons-vue'
  import { fetchGetServerInfo } from '@/api/monitor/server'
  import { usePageVisibility } from '@/hooks/core/usePageVisibility'
  import type { ServerInfo } from '@/types/api/monitor'

  defineOptions({ name: 'ServerMonitor' })

  const loading = ref(false)
  const serverInfo = ref<ServerInfo | null>(null)
  const requestInFlight = ref(false)
  const baseRefreshInterval = 10000
  const maxRefreshInterval = 60000
  const currentRefreshInterval = ref(baseRefreshInterval)
  let refreshTimer: number | null = null

  // 页面可见性检测 - 页面不可见时暂停刷新
  const { onPause, onResume } = usePageVisibility()
  onPause(() => stopAutoRefresh())
  onResume(() => startAutoRefresh())

  // 格式化百分比
  const formatPercent = (value: number) => {
    return `${value.toFixed(2)}%`
  }

  const clampPercent = (value: number) => {
    return Math.min(Math.max(value, 0), 100)
  }

  const formatNumber = (value: number) => {
    return value.toLocaleString()
  }

  const formatGb = (value: number | undefined) => {
    return `${(value ?? 0).toFixed(2)} GB`
  }

  const getUsageType = (usage: number) => {
    if (usage > 85) return 'danger'
    if (usage > 70) return 'warning'
    return 'success'
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

  const cpuRows = computed(() => {
    if (!serverInfo.value) return []
    const cpu = serverInfo.value.cpu
    return [
      {label: '核心数', value: cpu?.cpuNum ?? 0},
      {label: '用户使用率', value: formatPercent(cpu?.used ?? 0)},
      {label: '系统使用率', value: formatPercent(cpu?.sys ?? 0)},
      {label: '当前空闲率', value: formatPercent(cpu?.free ?? 0)}
    ]
  })

  const memoryRows = computed(() => {
    if (!serverInfo.value) return []
    const memory = serverInfo.value.memory
    const jvm = serverInfo.value.jvm
    return [
      {
        label: '总内存',
        memory: `${formatNumber(memory?.total ?? 0)} MB`,
        jvm: `${formatNumber(jvm?.total ?? 0)} MB`
      },
      {
        label: '已用内存',
        memory: `${formatNumber(memory?.used ?? 0)} MB`,
        jvm: `${formatNumber(jvm?.used ?? 0)} MB`
      },
      {
        label: '剩余内存',
        memory: `${formatNumber(memory?.free ?? 0)} MB`,
        jvm: `${formatNumber(jvm?.free ?? 0)} MB`
      },
      {
        label: '使用率',
        memory: formatPercent(memory?.usage ?? 0),
        jvm: formatPercent(jvm?.usage ?? 0)
      }
    ]
  })

  const cpuUsageMeters = computed(() => {
    const cpu = serverInfo.value?.cpu
    return [
      {label: '用户使用率', value: clampPercent(cpu?.used ?? 0), color: '#6366f1'},
      {label: '系统使用率', value: clampPercent(cpu?.sys ?? 0), color: '#f59e0b'},
      {label: '当前空闲率', value: clampPercent(cpu?.free ?? 0), color: '#10b981'}
    ]
  })

  const memoryUsageMeters = computed(() => {
    const memory = serverInfo.value?.memory
    const jvm = serverInfo.value?.jvm
    return [
      {label: '系统内存使用率', value: clampPercent(memory?.usage ?? 0), color: '#ef4444'},
      {label: 'JVM使用率', value: clampPercent(jvm?.usage ?? 0), color: '#06b6d4'}
    ]
  })

  const overviewCards = computed(() => {
    const cpu = serverInfo.value?.cpu
    const memory = serverInfo.value?.memory
    const jvm = serverInfo.value?.jvm
    const diskCount = serverInfo.value?.disk?.stores?.length ?? 0
    return [
      {
        label: 'CPU使用率',
        value: formatPercent(cpu?.used ?? 0),
        extra: `空闲 ${formatPercent(cpu?.free ?? 0)}`
      },
      {
        label: '系统内存使用率',
        value: formatPercent(memory?.usage ?? 0),
        extra: `总内存 ${formatNumber(memory?.total ?? 0)} MB`
      },
      {
        label: 'JVM使用率',
        value: formatPercent(jvm?.usage ?? 0),
        extra: `运行 ${formatDuration(jvm?.runTime ?? 0)}`
      },
      {
        label: '磁盘数量',
        value: `${diskCount} 个`,
        extra: `服务器 ${serverInfo.value?.system?.computerName || '-'}`
      }
    ]
  })

  const serverRows = computed(() => {
    if (!serverInfo.value) return []
    const system = serverInfo.value.system
    return [
      {label: '服务器名称', value: system?.computerName || '-'},
      {label: '操作系统', value: system?.osName || '-'},
      {label: '服务器IP', value: system?.computerIp || '-'},
      {label: '系统架构', value: system?.osArch || '-'}
    ]
  })

  const jvmRows = computed(() => {
    if (!serverInfo.value) return []
    const jvm = serverInfo.value.jvm
    return [
      {label: 'Java名称', value: jvm?.name || '-'},
      {label: 'Java版本', value: jvm?.version || '-'},
      {label: '启动时间', value: formatTime(jvm?.startTime || 0)},
      {label: '运行时长', value: formatDuration(jvm?.runTime || 0)},
      {label: '安装路径', value: jvm?.home || '-'},
      {label: '项目路径', value: jvm?.userDir || '-'},
      {label: '运行参数', value: jvm?.inputArgs || '-'}
    ]
  })

  const diskRows = computed(() => {
    const stores = serverInfo.value?.disk?.stores || []
    return stores.map((store) => ({
      mount: store.mount || '-',
      fileSystem: store.fileSystem || '-',
      typeName: store.typeName || '-',
      total: formatGb(store.totalGb),
      usable: formatGb(store.usableGb),
      used: formatGb(store.usedGb),
      usage: formatPercent(store.usage || 0),
      usageType: getUsageType(store.usage || 0)
    }))
  })

  const scheduleNextRefresh = (delay = currentRefreshInterval.value) => {
    stopAutoRefresh()
    refreshTimer = window.setTimeout(() => {
      getData()
    }, delay)
  }

  const getData = async (manual = false) => {
    if (requestInFlight.value) return
    requestInFlight.value = true
    if (manual || !serverInfo.value) {
      loading.value = true
    }
    try {
      const serverData = await fetchGetServerInfo()
      serverInfo.value = serverData
      currentRefreshInterval.value = baseRefreshInterval
    } catch (error) {
      currentRefreshInterval.value = Math.min(currentRefreshInterval.value * 2, maxRefreshInterval)
      if (import.meta.env.DEV) {
        console.error('获取监控数据失败:', error)
      }
    } finally {
      requestInFlight.value = false
      loading.value = false
      scheduleNextRefresh()
    }
  }

  const refreshData = () => {
    getData(true)
  }

  const startAutoRefresh = () => {
    scheduleNextRefresh(baseRefreshInterval)
  }

  const stopAutoRefresh = () => {
    if (refreshTimer) {
      clearTimeout(refreshTimer)
      refreshTimer = null
    }
  }

  onMounted(() => {
    getData(true)
  })

  onBeforeUnmount(() => {
    stopAutoRefresh()
  })
</script>

<style scoped lang="scss">
  .server-monitor-page {
    height: 100%;
    padding: 16px;
    overflow-y: auto;
    background: var(--default-bg-color);
  }

  :deep(.server-monitor-card) {
    border: 1px solid var(--art-card-border);
    border-radius: 12px;
    box-shadow: 0 2px 8px rgb(15 23 42 / 4%);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 12px rgb(15 23 42 / 6%);
    }

    .el-card__body {
      padding: 16px 18px;
    }
  }

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    min-height: 36px;
    gap: 12px;
  }

  .header-title {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .title-main {
    font-size: 16px;
    font-weight: 600;
    color: var(--art-gray-900);
    line-height: 1.1;
  }

  .title-sub {
    font-size: 12px;
    color: var(--art-gray-600);
  }

  .overview-row {
    margin-bottom: 8px;
  }

  .overview-card {
    margin-bottom: 10px;
    padding: 14px 16px;
    border: 1px solid var(--art-card-border);
    border-radius: 12px;
    background: rgb(255 255 255 / 96%);
    box-shadow: 0 2px 8px rgb(15 23 42 / 3%);
  }

  .overview-label {
    font-size: 12px;
    color: var(--art-gray-600);
  }

  .overview-value {
    margin: 6px 0;
    font-size: 22px;
    font-weight: 600;
    line-height: 1.2;
    color: var(--art-gray-900);
  }

  .overview-extra {
    font-size: 12px;
    color: var(--art-gray-500);
  }

  .section-title {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .meter-group {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(130px, 1fr));
    gap: 10px;
    padding: 2px 0 12px;
  }

  .meter-item {
    padding: 10px 12px;
    border-radius: 10px;
    background-color: rgb(248 250 252 / 88%);
    border: 1px solid rgb(148 163 184 / 22%);
  }

  .meter-label {
    margin-bottom: 6px;
    font-size: 12px;
    color: var(--art-gray-700);
  }

  .meter-value {
    margin-top: 6px;
    text-align: right;
    font-size: 12px;
    font-weight: 500;
    color: var(--art-gray-600);
  }

  :deep(.el-card) {
    border-radius: 12px;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 12px rgb(15 23 42 / 6%);
    }

    .el-card__header {
      padding: 14px 18px;
      font-weight: 600;
      color: var(--art-gray-800);
      border-bottom: 1px solid var(--art-card-border);
    }
  }

  .section-card {
    margin-bottom: 12px;
  }

  :deep(.el-table) {
    --el-table-header-bg-color: rgb(248 250 252);
    --el-table-row-hover-bg-color: rgb(59 130 246 / 5%);

    .cell {
      font-size: 13px;
    }
  }

  :deep(.el-descriptions__label) {
    width: 130px;
    color: var(--art-gray-700);
    font-weight: 500;
  }

  :deep(.el-button) {
    font-weight: 500;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      transform: none;
    }
  }
</style>
