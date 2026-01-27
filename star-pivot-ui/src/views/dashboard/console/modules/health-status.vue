<!-- 系统健康状态卡片 -->
<template>
  <ElCard shadow="hover" class="health-status-card">
    <template #header>
      <div class="card-header">
        <span>系统健康状态</span>
        <ElButton text :icon="Refresh" @click="refreshHealth" :loading="loading" size="small">
          刷新
        </ElButton>
      </div>
    </template>

    <div v-loading="loading">
      <div v-if="healthReport" class="health-content">
        <!-- 整体健康状态 -->
        <div class="overall-status">
          <div class="status-icon" :class="getOverallStatusClass(healthReport.overall)">
            <ElIcon :size="48">
              <Check v-if="healthReport.overall === 'healthy'" />
              <Warning v-else-if="healthReport.overall === 'unhealthy'" />
              <CircleClose v-else />
            </ElIcon>
          </div>
          <div class="status-text">
            <div class="status-title">{{ getOverallStatusText(healthReport.overall) }}</div>
            <div class="status-time" v-if="healthReport.timestamp">
              {{ formatTime(healthReport.timestamp) }}
            </div>
          </div>
        </div>

        <!-- 各组件健康状态 -->
        <ElDivider />
        <div class="components-status">
          <div
            v-for="(component, key) in healthComponents"
            :key="key"
            class="component-item"
            @click="goToMonitor"
          >
            <div class="component-icon" :class="getComponentStatusClass(component.status)">
              <ElIcon :size="20">
                <Check v-if="component.status === 'healthy'" />
                <Warning v-else />
              </ElIcon>
            </div>
            <div class="component-info">
              <div class="component-name">{{ component.name }}</div>
              <div class="component-desc">{{ component.desc }}</div>
            </div>
            <ElIcon class="component-arrow">
              <ArrowRight />
            </ElIcon>
          </div>
        </div>

        <!-- 检查耗时 -->
        <div class="duration-info" v-if="healthReport.duration">
          检查耗时: {{ healthReport.duration }}ms
        </div>
      </div>

      <div v-else class="empty-state">
        <ElEmpty description="暂无健康检查数据" :image-size="80" />
      </div>
    </div>
  </ElCard>
</template>

<script setup lang="ts">
  import { Refresh, Check, Warning, CircleClose, ArrowRight } from '@element-plus/icons-vue'
  import { fetchGetHealthCheck } from '@/api/monitor/api'
  import type { HealthCheckReport } from '@/types/api/monitor'
  import { useRouter } from 'vue-router'
  import dayjs from 'dayjs'

  defineOptions({ name: 'HealthStatus' })

  const router = useRouter()
  const loading = ref(false)
  const healthReport = ref<HealthCheckReport | null>(null)

  // 健康组件项类型
  type HealthComponent = {
    name: string
    desc: string
    status: 'healthy' | 'unhealthy'
  }

  // 健康组件列表
  const healthComponents = computed((): HealthComponent[] => {
    if (!healthReport.value) return []
    const components: HealthComponent[] = []
    if (healthReport.value.database) {
      const status: 'healthy' | 'unhealthy' = healthReport.value.database.healthy
        ? 'healthy'
        : 'unhealthy'
      components.push({
        name: '数据库',
        desc: healthReport.value.database.healthy
          ? `连接池: ${healthReport.value.database.activeCount}/${healthReport.value.database.maxActive}`
          : '连接异常',
        status
      })
    }
    if (healthReport.value.redis) {
      const status: 'healthy' | 'unhealthy' = healthReport.value.redis.healthy
        ? 'healthy'
        : 'unhealthy'
      components.push({
        name: 'Redis',
        desc: healthReport.value.redis.healthy ? '连接正常' : '连接异常',
        status
      })
    }
    if (healthReport.value.disk) {
      const status: 'healthy' | 'unhealthy' = healthReport.value.disk.healthy
        ? 'healthy'
        : 'unhealthy'
      components.push({
        name: '磁盘',
        desc: healthReport.value.disk.healthy
          ? `使用率: ${healthReport.value.disk.usage?.toFixed(1)}%`
          : '空间不足',
        status
      })
    }
    if (healthReport.value.jvm) {
      const status: 'healthy' | 'unhealthy' = healthReport.value.jvm.healthy
        ? 'healthy'
        : 'unhealthy'
      components.push({
        name: 'JVM',
        desc: healthReport.value.jvm.healthy
          ? `内存使用: ${healthReport.value.jvm.usage?.toFixed(1)}%`
          : '内存告警',
        status
      })
    }
    return components
  })

  // 获取整体状态样式类
  const getOverallStatusClass = (status: string) => {
    if (status === 'healthy') return 'status-healthy'
    if (status === 'unhealthy') return 'status-unhealthy'
    return 'status-error'
  }

  // 获取整体状态文本
  const getOverallStatusText = (status: string) => {
    if (status === 'healthy') return '系统运行正常'
    if (status === 'unhealthy') return '系统存在异常'
    if (status === 'timeout') return '检查超时'
    return '检查失败'
  }

  // 获取组件状态样式类
  const getComponentStatusClass = (status: string) => {
    return status === 'healthy' ? 'component-healthy' : 'component-unhealthy'
  }

  // 格式化时间
  const formatTime = (timestamp: string) => {
    return dayjs(timestamp).format('YYYY-MM-DD HH:mm:ss')
  }

  // 跳转到监控页面
  const goToMonitor = () => {
    router.push('/monitor/server')
  }

  // 获取健康检查数据
  const getHealthData = async () => {
    loading.value = true
    try {
      const data = await fetchGetHealthCheck()
      healthReport.value = data
    } catch (error) {
      if (import.meta.env.DEV) {
        console.error('获取健康检查数据失败:', error)
      }
    } finally {
      loading.value = false
    }
  }

  // 刷新健康检查
  const refreshHealth = () => {
    getHealthData()
  }

  onMounted(() => {
    getHealthData()
    // 每30秒自动刷新
    const timer = setInterval(() => {
      getHealthData()
    }, 30000)
    onBeforeUnmount(() => {
      clearInterval(timer)
    })
  })
</script>

<style scoped lang="scss">
  .health-status-card {
    height: 100%;
  }

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .health-content {
    padding: 10px 0;
  }

  .overall-status {
    display: flex;
    gap: 16px;
    align-items: center;
    padding: 10px 0;

    .status-icon {
      flex-shrink: 0;

      &.status-healthy {
        color: var(--el-color-success);
      }

      &.status-unhealthy {
        color: var(--el-color-warning);
      }

      &.status-error {
        color: var(--el-color-danger);
      }
    }

    .status-text {
      flex: 1;

      .status-title {
        margin-bottom: 4px;
        font-size: 18px;
        font-weight: bold;
      }

      .status-time {
        font-size: 12px;
        color: var(--el-text-color-secondary);
      }
    }
  }

  .components-status {
    .component-item {
      display: flex;
      gap: 12px;
      align-items: center;
      padding: 12px;
      cursor: pointer;
      border-radius: 6px;
      transition: background-color 0.3s;

      &:hover {
        background-color: var(--el-bg-color-page);
      }

      .component-icon {
        flex-shrink: 0;

        &.component-healthy {
          color: var(--el-color-success);
        }

        &.component-unhealthy {
          color: var(--el-color-warning);
        }
      }

      .component-info {
        flex: 1;

        .component-name {
          margin-bottom: 4px;
          font-size: 14px;
          font-weight: 500;
        }

        .component-desc {
          font-size: 12px;
          color: var(--el-text-color-secondary);
        }
      }

      .component-arrow {
        color: var(--el-text-color-secondary);
      }
    }
  }

  .duration-info {
    padding-top: 12px;
    margin-top: 12px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
    text-align: center;
    border-top: 1px solid var(--el-border-color-lighter);
  }

  .empty-state {
    padding: 40px 0;
  }
</style>
