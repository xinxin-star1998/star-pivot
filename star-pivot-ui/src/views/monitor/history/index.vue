<!-- 监控历史数据趋势图表页面 -->
<template>
  <div class="history-monitor-page art-full-height">
    <ElCard class="art-table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>监控历史数据趋势</span>
          <ElButton type="primary" :icon="Refresh" @click="refreshData" :loading="loading">
            刷新
          </ElButton>
        </div>
      </template>

      <!-- 查询条件 -->
      <ElForm :model="queryForm" inline class="query-form">
        <ElFormItem label="指标类型">
          <ElSelect v-model="queryForm.metricType" placeholder="请选择指标类型" style="width: 200px">
            <ElOption label="CPU使用率" value="server_cpu" />
            <ElOption label="内存使用率" value="server_memory" />
            <ElOption label="磁盘使用率" value="server_disk" />
            <ElOption label="JVM堆内存使用率" value="jvm_heap" />
            <ElOption label="Druid连接池使用率" value="druid_pool_usage" />
            <ElOption label="Redis内存使用率" value="redis_memory" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="时间范围">
          <ElDatePicker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 400px"
          />
        </ElFormItem>
        <ElFormItem>
          <ElButton type="primary" @click="handleQuery" :loading="loading">查询</ElButton>
          <ElButton @click="resetQuery">重置</ElButton>
        </ElFormItem>
      </ElForm>

      <!-- 统计信息 -->
      <ElRow :gutter="20" v-if="statistics" style="margin-bottom: 20px">
        <ElCol :span="6">
          <ElCard shadow="hover">
            <div class="stat-item">
              <div class="stat-label">平均值</div>
              <div class="stat-value">{{ formatNumber(statistics.avgValue) }}</div>
            </div>
          </ElCard>
        </ElCol>
        <ElCol :span="6">
          <ElCard shadow="hover">
            <div class="stat-item">
              <div class="stat-label">最大值</div>
              <div class="stat-value">{{ formatNumber(statistics.maxValue) }}</div>
            </div>
          </ElCard>
        </ElCol>
        <ElCol :span="6">
          <ElCard shadow="hover">
            <div class="stat-item">
              <div class="stat-label">最小值</div>
              <div class="stat-value">{{ formatNumber(statistics.minValue) }}</div>
            </div>
          </ElCard>
        </ElCol>
        <ElCol :span="6">
          <ElCard shadow="hover">
            <div class="stat-item">
              <div class="stat-label">数据点数</div>
              <div class="stat-value">{{ statistics.count }}</div>
            </div>
          </ElCard>
        </ElCol>
      </ElRow>

      <!-- 趋势图表 -->
      <ElCard shadow="hover" v-loading="loading">
        <div ref="chartRef" style="width: 100%; height: 400px"></div>
      </ElCard>
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { Refresh } from '@element-plus/icons-vue'
  import { fetchGetHistoryData, fetchGetMetricStatistics } from '@/api/monitor/history'
  import type { MonitorHistoryDataPoint, MetricStatistics } from '@/types/api/monitor'
  import { useChart } from '@/hooks/core/useChart'
  import { echarts } from '@/plugins/echarts'
  import dayjs from 'dayjs'

  defineOptions({ name: 'MonitorHistory' })

  const loading = ref(false)
  const queryForm = ref({
    metricType: 'server_cpu',
    startTime: '',
    endTime: ''
  })
  const dateRange = ref<[string, string]>([])
  const historyData = ref<MonitorHistoryDataPoint[]>([])
  const statistics = ref<MetricStatistics | null>(null)

  const { chartRef, initChart, updateChart } = useChart()

  // 格式化数字
  const formatNumber = (value: number) => {
    if (value == null || isNaN(value)) return '-'
    return value.toFixed(2)
  }

  // 初始化时间范围（默认最近24小时）
  const initDateRange = () => {
    const end = dayjs()
    const start = end.subtract(24, 'hour')
    dateRange.value = [start.format('YYYY-MM-DD HH:mm:ss'), end.format('YYYY-MM-DD HH:mm:ss')]
    queryForm.value.startTime = start.format('YYYY-MM-DD HH:mm:ss')
    queryForm.value.endTime = end.format('YYYY-MM-DD HH:mm:ss')
  }

  // 查询数据
  const handleQuery = async () => {
    if (!dateRange.value || dateRange.value.length !== 2) {
      ElMessage.warning('请选择时间范围')
      return
    }

    queryForm.value.startTime = dateRange.value[0]
    queryForm.value.endTime = dateRange.value[1]

    loading.value = true
    try {
      // 并行查询历史数据和统计信息
      const [data, stats] = await Promise.all([
        fetchGetHistoryData(queryForm.value.metricType, queryForm.value.startTime, queryForm.value.endTime),
        fetchGetMetricStatistics(queryForm.value.metricType, queryForm.value.startTime, queryForm.value.endTime)
      ])
      historyData.value = data
      statistics.value = stats
      updateChartData()
    } catch (error) {
      if (import.meta.env.DEV) {
        console.error('查询历史数据失败:', error)
      }
    } finally {
      loading.value = false
    }
  }

  // 重置查询
  const resetQuery = () => {
    queryForm.value.metricType = 'server_cpu'
    initDateRange()
    handleQuery()
  }

  // 刷新数据
  const refreshData = () => {
    handleQuery()
  }

  // 更新图表数据
  const updateChartData = () => {
    if (!historyData.value || historyData.value.length === 0) {
      return
    }

    const times = historyData.value.map(item => item.time)
    const values = historyData.value.map(item => Number(item.value))
    const unit = historyData.value[0]?.unit || ''

    const option = {
      title: {
        text: getMetricName(queryForm.value.metricType),
        left: 'center'
      },
      tooltip: {
        trigger: 'axis',
        formatter: (params: any) => {
          const param = params[0]
          return `${param.name}<br/>${param.seriesName}: ${param.value} ${unit}`
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: times,
        axisLabel: {
          formatter: (value: string) => {
            return dayjs(value).format('MM-DD HH:mm')
          }
        }
      },
      yAxis: {
        type: 'value',
        name: unit,
        axisLabel: {
          formatter: (value: number) => {
            return value + ' ' + unit
          }
        }
      },
      series: [
        {
          name: getMetricName(queryForm.value.metricType),
          type: 'line',
          smooth: true,
          data: values,
          areaStyle: {
            opacity: 0.3
          },
          lineStyle: {
            width: 2
          }
        }
      ]
    }

    updateChart(option)
  }

  // 获取指标名称
  const getMetricName = (metricType: string) => {
    const map: Record<string, string> = {
      server_cpu: 'CPU使用率',
      server_memory: '内存使用率',
      server_disk: '磁盘使用率',
      jvm_heap: 'JVM堆内存使用率',
      druid_pool_usage: 'Druid连接池使用率',
      redis_memory: 'Redis内存使用率'
    }
    return map[metricType] || metricType
  }

  onMounted(() => {
    initDateRange()
    handleQuery()
  })
</script>

<style scoped lang="scss">
  .history-monitor-page {
    padding: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .query-form {
    margin-bottom: 20px;
  }

  .stat-item {
    text-align: center;

    .stat-label {
      color: var(--el-text-color-secondary);
      font-size: 14px;
      margin-bottom: 8px;
    }

    .stat-value {
      font-size: 24px;
      font-weight: bold;
      color: var(--el-color-primary);
    }
  }
</style>
