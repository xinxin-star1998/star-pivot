<!-- API性能监控页面 -->
<template>
  <div class="api-performance-page art-full-height">
    <ElCard class="art-table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>API接口性能监控</span>
          <ElButton :icon="Refresh" @click="getApiList" :loading="loading">刷新</ElButton>
        </div>
      </template>

      <div class="api-performance-scroll-wrap">
        <!-- 查询条件 -->
        <ElForm :model="queryForm" inline class="query-form">
          <ElFormItem label="日期范围">
            <ElDatePicker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              style="width: 300px"
            />
          </ElFormItem>
          <ElFormItem>
            <ElButton type="primary" @click="handleQuery">查询</ElButton>
            <ElButton @click="resetQuery">重置</ElButton>
          </ElFormItem>
        </ElForm>

        <!-- 统计卡片 -->
        <ElRow :gutter="20" style="margin-bottom: 20px">
          <ElCol :span="6">
            <ElCard shadow="hover">
              <div class="stat-item">
                <div class="stat-label">总请求数</div>
                <div class="stat-value">{{ totalRequests }}</div>
              </div>
            </ElCard>
          </ElCol>
          <ElCol :span="6">
            <ElCard shadow="hover">
              <div class="stat-item">
                <div class="stat-label">总错误数</div>
                <div class="stat-value text-danger">{{ totalErrors }}</div>
              </div>
            </ElCard>
          </ElCol>
          <ElCol :span="6">
            <ElCard shadow="hover">
              <div class="stat-item">
                <div class="stat-label">平均响应时间</div>
                <div class="stat-value">{{ formatTime(avgResponseTime) }} ms</div>
              </div>
            </ElCard>
          </ElCol>
          <ElCol :span="6">
            <ElCard shadow="hover">
              <div class="stat-item">
                <div class="stat-label">错误率</div>
                <div class="stat-value" :class="getErrorRateClass(errorRate)">
                  {{ formatPercent(errorRate) }}
                </div>
              </div>
            </ElCard>
          </ElCol>
        </ElRow>

        <!-- 性能排行榜 -->
        <ElTabs v-model="activeTab">
          <ElTabPane label="性能列表" name="list">
            <!-- 搜索条件 -->
            <ElForm :model="searchForm" inline class="search-form" style="margin-bottom: 20px">
              <ElFormItem label="接口路径">
                <ElInput
                  v-model="searchForm.apiPath"
                  placeholder="请输入接口路径"
                  clearable
                  style="width: 200px"
                  @keyup.enter="handleSearch"
                />
              </ElFormItem>
              <ElFormItem label="请求方法">
                <ElSelect
                  v-model="searchForm.apiMethod"
                  placeholder="请选择"
                  clearable
                  style="width: 120px"
                >
                  <ElOption label="GET" value="GET" />
                  <ElOption label="POST" value="POST" />
                  <ElOption label="PUT" value="PUT" />
                  <ElOption label="DELETE" value="DELETE" />
                  <ElOption label="PATCH" value="PATCH" />
                </ElSelect>
              </ElFormItem>
              <ElFormItem label="日期范围">
                <ElDatePicker
                  v-model="searchForm.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="YYYY-MM-DD"
                  style="width: 300px"
                />
              </ElFormItem>
              <ElFormItem>
                <ElButton type="primary" @click="handleSearch">查询</ElButton>
                <ElButton @click="resetSearchParams">重置</ElButton>
              </ElFormItem>
            </ElForm>

            <!-- 分页表格 -->
            <ArtTable
              :loading="loading"
              :data="data"
              :columns="columns"
              :pagination="pagination"
              @pagination:size-change="handleSizeChange"
              @pagination:current-change="handleCurrentChange"
            />
          </ElTabPane>
          <ElTabPane label="最慢接口" name="slowest">
            <ElTable :data="slowestApis" border v-loading="loadingRanking">
              <ElTableColumn
                prop="apiPath"
                label="接口路径"
                min-width="300"
                show-overflow-tooltip
              />
              <ElTableColumn prop="apiMethod" label="请求方法" width="100">
                <template #default="{ row }">
                  <ElTag :type="getMethodType(row.apiMethod)">{{ row.apiMethod }}</ElTag>
                </template>
              </ElTableColumn>
              <ElTableColumn prop="requestCount" label="请求次数" width="100" />
              <ElTableColumn prop="responseTimeAvg" label="平均响应时间(ms)" width="150" sortable>
                <template #default="{ row }">
                  <span :class="getTimeClass(row.responseTimeAvg)">
                    {{ formatNumber(row.responseTimeAvg) }}
                  </span>
                </template>
              </ElTableColumn>
              <ElTableColumn prop="responseTimeMax" label="最大响应时间(ms)" width="150" />
              <ElTableColumn prop="errorCount" label="错误次数" width="100">
                <template #default="{ row }">
                  <ElTag v-if="row.errorCount > 0" type="danger">{{ row.errorCount }}</ElTag>
                  <span v-else>0</span>
                </template>
              </ElTableColumn>
              <ElTableColumn label="错误率" width="100">
                <template #default="{ row }">
                  <span :class="getErrorRateClass((row.errorCount / row.requestCount) * 100)">
                    {{ formatPercent((row.errorCount / row.requestCount) * 100) }}
                  </span>
                </template>
              </ElTableColumn>
            </ElTable>
          </ElTabPane>
          <ElTabPane label="错误率最高" name="errorRate">
            <ElTable :data="highestErrorRateApis" border v-loading="loadingRanking">
              <ElTableColumn
                prop="apiPath"
                label="接口路径"
                min-width="300"
                show-overflow-tooltip
              />
              <ElTableColumn prop="apiMethod" label="请求方法" width="100">
                <template #default="{ row }">
                  <ElTag :type="getMethodType(row.apiMethod)">{{ row.apiMethod }}</ElTag>
                </template>
              </ElTableColumn>
              <ElTableColumn prop="requestCount" label="请求次数" width="100" />
              <ElTableColumn prop="errorCount" label="错误次数" width="100" sortable>
                <template #default="{ row }">
                  <ElTag type="danger">{{ row.errorCount }}</ElTag>
                </template>
              </ElTableColumn>
              <ElTableColumn label="错误率" width="100" sortable>
                <template #default="{ row }">
                  <span :class="getErrorRateClass((row.errorCount / row.requestCount) * 100)">
                    {{ formatPercent((row.errorCount / row.requestCount) * 100) }}
                  </span>
                </template>
              </ElTableColumn>
              <ElTableColumn prop="responseTimeAvg" label="平均响应时间(ms)" width="150" />
            </ElTable>
          </ElTabPane>
        </ElTabs>
      </div>
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { Refresh } from '@element-plus/icons-vue'
  import {
    fetchGetSlowestApis,
    fetchGetHighestErrorRateApis,
    fetchGetApiPerformancePageList
  } from '@/api/monitor/api'
  import type { ApiPerformance, ApiPerformanceReqBo } from '@/types/api/monitor'
  import { useTable } from '@/hooks/core/useTable'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import { ElTag, ElMessage } from 'element-plus'
  import dayjs from 'dayjs'

  defineOptions({ name: 'ApiPerformance' })

  const activeTab = ref('list')
  const apiList = ref<ApiPerformance[]>([])
  const slowestApis = ref<ApiPerformance[]>([])
  const highestErrorRateApis = ref<ApiPerformance[]>([])
  const dateRange = ref<[string, string]>([])
  const loadingRanking = ref(false) // 排行榜加载状态

  const queryForm = ref({
    startDate: '',
    endDate: ''
  })

  // 搜索表单（用于分页列表）
  const searchForm = ref<ApiPerformanceReqBo & { dateRange?: [string, string] | null }>({
    apiPath: undefined,
    apiMethod: undefined,
    startDate: undefined,
    endDate: undefined,
    dateRange: null,
    orderBy: 'responseTimeAvg',
    orderDirection: 'desc'
  })

  // 使用useTable hook管理分页列表
  const {
    data,
    loading: tableLoading,
    pagination,
    columns,
    getData,
    handleSizeChange,
    handleCurrentChange,
    searchParams,
    resetSearchParams
  } = useTable({
    core: {
      apiFn: fetchGetApiPerformancePageList,
      apiParams: {
        pageNum: 1,
        pageSize: 20,
        ...searchForm.value
      },
      immediate: false, // 不立即加载，等用户切换到列表Tab时再加载
      columnsFactory: () => [
        { type: 'index', width: 60, label: '序号' },
        {
          prop: 'apiPath',
          label: '接口路径',
          minWidth: 300,
          showOverflowTooltip: true
        },
        {
          prop: 'apiMethod',
          label: '请求方法',
          width: 100,
          formatter: (row: ApiPerformance) => {
            return row.apiMethod
          }
        },
        {
          prop: 'requestCount',
          label: '请求次数',
          width: 100,
          sortable: true
        },
        {
          prop: 'responseTimeAvg',
          label: '平均响应时间(ms)',
          width: 150,
          sortable: true,
          formatter: (row: ApiPerformance) => {
            return formatNumber(row.responseTimeAvg)
          }
        },
        {
          prop: 'responseTimeMax',
          label: '最大响应时间(ms)',
          width: 150
        },
        {
          prop: 'responseTimeMin',
          label: '最小响应时间(ms)',
          width: 150
        },
        {
          prop: 'errorCount',
          label: '错误次数',
          width: 100,
          sortable: true
        },
        {
          label: '错误率',
          width: 100,
          formatter: (row: ApiPerformance) => {
            const rate = row.requestCount > 0 ? (row.errorCount / row.requestCount) * 100 : 0
            return formatPercent(rate)
          }
        },
        {
          prop: 'statDate',
          label: '统计日期',
          width: 120
        },
        {
          prop: 'statHour',
          label: '统计小时',
          width: 100,
          formatter: (row: ApiPerformance) => {
            return row.statHour != null ? `${row.statHour}:00` : '-'
          }
        }
      ]
    },
    // 响应适配器：将后端PageResponse格式转换为useTable期望的格式
    transform: {
      responseAdapter: (response: any) => {
        // HTTP工具已经提取了response.data，所以这里response直接就是PageResponse格式
        // 格式：{ total, rows, pageNum, pageSize, pageCount }
        if (import.meta.env.DEV) {
          console.log('[API性能监控] 响应数据:', response)
        }

        // 如果response本身就是PageResponse格式（有rows字段）
        if (response && (response.rows || response.records)) {
          const result = {
            records: response.rows || response.records || [],
            total: response.total || 0,
            current: response.pageNum || 1,
            size: response.pageSize || 20
          }
          if (import.meta.env.DEV) {
            console.log('[API性能监控] 转换后的数据:', result)
          }
          return result
        }

        // 兼容处理：如果response还有嵌套的data字段（虽然不应该出现）
        if (response && response.data && (response.data.rows || response.data.records)) {
          const data = response.data
          const result = {
            records: data.rows || data.records || [],
            total: data.total || 0,
            current: data.pageNum || 1,
            size: data.pageSize || 20
          }
          if (import.meta.env.DEV) {
            console.log('[API性能监控] 转换后的数据（嵌套格式）:', result)
          }
          return result
        }

        if (import.meta.env.DEV) {
          console.warn('[API性能监控] 响应格式异常:', response)
        }
        return { records: [], total: 0 }
      }
    },
    // 添加错误处理回调
    hooks: {
      onError: (error: any) => {
        if (import.meta.env.DEV) {
          console.error('[API性能监控] 查询失败:', error)
        }
        ElMessage.error('查询API性能数据失败，请检查网络连接或联系管理员')
      },
      onSuccess: (data: any[], response: any) => {
        if (import.meta.env.DEV) {
          console.log('[API性能监控] 查询成功，数据条数:', data.length, '总数:', response.total)
        }
      }
    }
  })

  // 合并loading状态
  const loading = computed(() => tableLoading.value || loadingRanking.value)

  // 计算统计数据
  const totalRequests = computed(() => {
    return apiList.value.reduce((sum, item) => sum + item.requestCount, 0)
  })

  const totalErrors = computed(() => {
    return apiList.value.reduce((sum, item) => sum + item.errorCount, 0)
  })

  const avgResponseTime = computed(() => {
    if (apiList.value.length === 0) return 0
    const totalTime = apiList.value.reduce(
      (sum, item) => sum + Number(item.responseTimeAvg) * item.requestCount,
      0
    )
    return totalTime / totalRequests.value
  })

  const errorRate = computed(() => {
    if (totalRequests.value === 0) return 0
    return (totalErrors.value / totalRequests.value) * 100
  })

  // 格式化数字
  const formatNumber = (value: number) => {
    if (value == null || isNaN(value)) return '-'
    return value.toFixed(2)
  }

  // 格式化时间
  const formatTime = (value: number) => {
    return formatNumber(value)
  }

  // 格式化百分比
  const formatPercent = (value: number) => {
    return `${formatNumber(value)}%`
  }

  // 获取时间样式类
  const getTimeClass = (time: number) => {
    if (time >= 1000) return 'text-danger'
    if (time >= 500) return 'text-warning'
    return ''
  }

  // 获取错误率样式类
  const getErrorRateClass = (rate: number) => {
    if (rate >= 10) return 'text-danger'
    if (rate >= 5) return 'text-warning'
    return ''
  }

  // 获取请求方法类型
  const getMethodType = (method: string) => {
    const map: Record<string, 'success' | 'warning' | 'danger' | 'info'> = {
      GET: 'success',
      POST: 'warning',
      PUT: 'warning',
      DELETE: 'danger',
      PATCH: 'info'
    }
    return map[method] || 'info'
  }

  // 初始化日期范围（默认最近7天）
  const initDateRange = () => {
    const end = dayjs()
    const start = end.subtract(7, 'day')
    dateRange.value = [start.format('YYYY-MM-DD'), end.format('YYYY-MM-DD')]
    queryForm.value.startDate = start.format('YYYY-MM-DD')
    queryForm.value.endDate = end.format('YYYY-MM-DD')
  }

  // 初始化搜索表单日期范围
  const initSearchDateRange = () => {
    const end = dayjs()
    const start = end.subtract(7, 'day')
    searchForm.value.dateRange = [start.format('YYYY-MM-DD'), end.format('YYYY-MM-DD')]
    searchForm.value.startDate = start.format('YYYY-MM-DD')
    searchForm.value.endDate = end.format('YYYY-MM-DD')
  }

  // 获取API列表（排行榜）
  const getApiList = async () => {
    if (!dateRange.value || dateRange.value.length !== 2) {
      ElMessage.warning('请选择日期范围')
      return
    }

    queryForm.value.startDate = dateRange.value[0]
    queryForm.value.endDate = dateRange.value[1]

    loadingRanking.value = true
    try {
      const [slowest, errorRate] = await Promise.all([
        fetchGetSlowestApis(20, queryForm.value.startDate, queryForm.value.endDate),
        fetchGetHighestErrorRateApis(20, queryForm.value.startDate, queryForm.value.endDate)
      ])
      slowestApis.value = slowest
      highestErrorRateApis.value = errorRate
      apiList.value = [...slowest, ...errorRate]
    } catch (error) {
      if (import.meta.env.DEV) {
        console.error('获取API性能数据失败:', error)
      }
    } finally {
      loadingRanking.value = false
    }
  }

  // 查询（排行榜）
  const handleQuery = () => {
    getApiList()
  }

  // 重置查询（排行榜）
  const resetQuery = () => {
    initDateRange()
    getApiList()
  }

  // 搜索（分页列表）
  const handleSearch = async () => {
    // 处理日期范围
    if (searchForm.value.dateRange && searchForm.value.dateRange.length === 2) {
      searchForm.value.startDate = searchForm.value.dateRange[0]
      searchForm.value.endDate = searchForm.value.dateRange[1]
    } else {
      searchForm.value.startDate = undefined
      searchForm.value.endDate = undefined
    }

    // 更新搜索参数
    const params = {
      apiPath: searchForm.value.apiPath,
      apiMethod: searchForm.value.apiMethod,
      startDate: searchForm.value.startDate,
      endDate: searchForm.value.endDate,
      orderBy: searchForm.value.orderBy || 'responseTimeAvg',
      orderDirection: searchForm.value.orderDirection || 'desc'
    }
    Object.assign(searchParams, params)

    if (import.meta.env.DEV) {
      console.log('[API性能监控] 查询参数:', params)
    }

    // 执行搜索
    try {
      await getData()
    } catch (error) {
      if (import.meta.env.DEV) {
        console.error('[API性能监控] 查询异常:', error)
      }
    }
  }

  // 监听Tab切换，切换到列表Tab时加载数据
  watch(activeTab, (newTab) => {
    if (newTab === 'list') {
      // 如果还没有初始化日期范围，先初始化
      if (!searchForm.value.dateRange) {
        initSearchDateRange()
      }
      // 加载分页数据
      handleSearch()
    } else {
      // 切换到排行榜Tab时，加载排行榜数据
      if (slowestApis.value.length === 0 && highestErrorRateApis.value.length === 0) {
        getApiList()
      }
    }
  })

  onMounted(() => {
    initDateRange()
    initSearchDateRange()
    if (activeTab.value === 'list') {
      // 默认在性能列表 Tab 时，进入页面即加载分页数据
      handleSearch()
    } else {
      getApiList()
    }
  })
</script>

<style scoped lang="scss">
  .api-performance-page {
    padding: 20px;
    background-color: var(--default-bg-color);
  }

  :deep(.art-table-card) {
    border: 1px solid var(--art-card-border);
    border-radius: 12px;
    box-shadow: 0 2px 12px 0 rgb(0 0 0 / 8%);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 16px 0 rgb(0 0 0 / 12%);
    }
  }

  :deep(.art-table-card .el-card__body) {
    display: flex;
    flex-direction: column;
    min-height: 0;
  }

  .api-performance-scroll-wrap {
    flex: 1;
    min-height: 0;
    overflow-y: auto;
  }

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .query-form {
    padding: 16px;
    margin-bottom: 20px;
    background-color: var(--art-gray-50);
    border-radius: 8px;
  }

  :deep(.el-card) {
    border-radius: 12px;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 16px 0 rgb(0 0 0 / 12%);
    }
  }

  .stat-item {
    text-align: center;

    .stat-label {
      margin-bottom: 8px;
      font-size: 14px;
      color: var(--el-text-color-secondary);
    }

    .stat-value {
      font-size: 24px;
      font-weight: bold;
      color: var(--el-color-primary);

      &.text-danger {
        color: var(--el-color-danger);
      }

      &.text-warning {
        color: var(--el-color-warning);
      }
    }
  }

  :deep(.el-tabs) {
    .el-tabs__header {
      margin-bottom: 16px;
    }

    .el-tabs__item {
      font-weight: 500;
      transition: all 0.3s ease;

      &.is-active {
        color: var(--el-color-primary);
      }

      &:hover {
        color: var(--el-color-primary-light-3);
      }
    }
  }

  :deep(.el-table) {
    border-radius: 8px;

    .el-table__header-wrapper {
      th {
        font-weight: 600;
        color: var(--art-gray-800);
        background-color: var(--art-gray-100) !important;
      }
    }

    .el-table__body-wrapper {
      tr {
        transition: all 0.2s ease;

        &:hover > td {
          background-color: var(--art-gray-50) !important;
        }
      }
    }
  }

  :deep(.el-button) {
    font-weight: 500;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-1px);
    }
  }

  :deep(.el-tag) {
    font-weight: 500;
    border-radius: 6px;
  }

  :deep(.el-form-item__label) {
    font-weight: 500;
    color: var(--art-gray-700);
  }

  :deep(.el-input__wrapper),
  :deep(.el-select .el-select__wrapper) {
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 2px 8px 0 rgb(0 0 0 / 8%);
    }
  }

  .text-warning {
    color: var(--el-color-warning);
  }

  .text-danger {
    color: var(--el-color-danger);
  }
</style>
