<!-- 操作日志页面 -->
<template>
  <div class="oper-log-page art-full-height">
    <!-- 搜索栏 -->
    <ElCollapseTransition>
      <div v-show="showSearchBar">
        <OperLogSearch v-model="searchForm" @search="handleSearch" @reset="resetSearchParams" />
      </div>
    </ElCollapseTransition>

    <ElCard class="art-table-card" shadow="never" :style="cardStyle">
      <!-- 表格头部 -->
      <ArtTableHeader
        v-model:columns="columnChecks"
        v-model:showSearchBar="showSearchBar"
        :loading="loading"
        @refresh="refreshData"
      >
        <template #left>
          <ElSpace wrap>
            <ElButton
              type="danger"
              :disabled="selectedRows.length === 0"
              @click="handleBatchDelete"
              v-ripple
              v-auth="'system:operlog:delete'"
            >
              {{ t('common.batchDelete') }}
            </ElButton>
            <ElButton type="danger" @click="handleClean" v-ripple v-auth="'system:operlog:delete'">
              {{ t('system.operLog.clear') }}
            </ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <!-- 表格 -->
      <ArtTable
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        @selection-change="handleSelectionChange"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
      </ArtTable>

      <!-- 详情对话框 -->
      <OperLogDetail v-model:visible="detailDialogVisible" :oper-log="currentOperLog" />
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { useTable } from '@/hooks/core/useTable'
  import { fetchCleanOperLog, fetchDeleteOperLog, fetchGetOperLogList } from '@/api/log/operlog'
  import { ElButton, ElCollapseTransition, ElMessage, ElMessageBox, ElTag } from 'element-plus'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import { useAuth } from '@/hooks/core/useAuth'
  import type { OperLogListItem, OperLogSearchParams } from '@/types/api/operlog'
  import OperLogDetail from './modules/oper-log-detail.vue'
  import OperLogSearch from './modules/oper-log-search.vue'
  import { useI18n } from 'vue-i18n'
  import { getOperBusinessTypeLabel } from './constants'

  defineOptions({ name: 'OperLog' })

  const { hasAuth } = useAuth()
  const { t } = useI18n()

  // 搜索栏显示状态
  const showSearchBar = ref(true)

  // 卡片样式（根据搜索栏显示状态动态调整）
  const cardStyle = computed(() => ({
    'margin-top': showSearchBar.value ? '12px' : '0'
  }))

  // 搜索表单（包含 dateRange 用于搜索组件）
  const searchForm = ref<OperLogSearchParams & { dateRange?: [string, string] | null }>({
    title: undefined,
    businessType: undefined,
    operName: undefined,
    status: undefined,
    startTime: undefined,
    endTime: undefined,
    dateRange: null
  })

  // 选中行
  const selectedRows = ref<OperLogListItem[]>([])

  // 详情对话框
  const detailDialogVisible = ref(false)
  const currentOperLog = ref<OperLogListItem | null>(null)

  const {
    columns,
    columnChecks,
    data,
    loading,
    pagination,
    getData,
    handleSizeChange,
    handleCurrentChange,
    refreshData
  } = useTable({
    // 核心配置
    core: {
      apiFn: fetchGetOperLogList,
      apiParams: {
        pageSize: 20
      },
      immediate: true, // 确保页面加载时自动获取数据
      columnsFactory: () => [
        { type: 'selection' }, // 勾选列
        { type: 'index', width: 60, label: t('table.column.index') },
        {
          prop: 'operTime',
          label: t('system.operLog.operTime'),
          width: 180,
          sortable: true
        },
        {
          prop: 'title',
          label: t('system.operLog.title'),
          width: 150
        },
        {
          prop: 'businessType',
          label: t('system.operLog.businessType'),
          width: 100,
          formatter: (row: OperLogListItem) => {
            return getOperBusinessTypeLabel(row.businessType)
          }
        },
        {
          prop: 'operName',
          label: t('system.operLog.operName'),
          width: 120
        },
        {
          prop: 'deptName',
          label: t('system.dept.deptName'),
          width: 120
        },
        {
          prop: 'operUrl',
          label: t('system.operLog.operUrl'),
          minWidth: 200,
          showOverflowTooltip: true
        },
        {
          prop: 'requestMethod',
          label: t('system.operLog.requestMethod'),
          width: 100,
          formatter: (row: OperLogListItem) => {
            const method = row.requestMethod || ''
            const colorMap: Record<string, string> = {
              GET: 'success',
              POST: 'primary',
              PUT: 'warning',
              DELETE: 'danger',
              PATCH: 'info'
            }
            return h(
              ElTag,
              {
                type: (colorMap[method] || 'info') as
                  | 'primary'
                  | 'success'
                  | 'warning'
                  | 'info'
                  | 'danger',
                size: 'small'
              },
              () => method
            )
          }
        },
        {
          prop: 'operIp',
          label: t('system.operLog.operIp'),
          width: 140
        },
        {
          prop: 'status',
          label: t('system.operLog.status'),
          width: 100,
          formatter: (row: OperLogListItem) => {
            return h(
              ElTag,
              {
                type: row.status === 0 ? 'success' : 'danger',
                size: 'small'
              },
              () =>
                row.status === 0
                  ? t('system.operLog.statusSuccess')
                  : t('system.operLog.statusFail')
            )
          }
        },
        {
          prop: 'costTime',
          label: t('system.operLog.costTime'),
          width: 100,
          sortable: true
        },
        {
          prop: 'operation',
          label: t('common.operation'),
          width: 120,
          fixed: 'right',
          formatter: (row: OperLogListItem) => {
            return h('div', { class: 'flex gap-2' }, [
              h(
                ElButton,
                {
                  type: 'primary',
                  link: true,
                  size: 'small',
                  onClick: () => showDetail(row)
                },
                () => t('system.operLog.detail')
              ),
              hasAuth('system:operlog:delete') &&
                h(
                  ElButton,
                  {
                    type: 'danger',
                    link: true,
                    size: 'small',
                    onClick: () => handleDelete(row)
                  },
                  () => t('common.delete')
                )
            ])
          }
        }
      ]
    }
  })

  /**
   * 搜索
   * @param params 搜索参数（已由搜索组件处理日期范围转换）
   */
  const handleSearch = (params?: OperLogSearchParams) => {
    // 如果传入了参数，使用传入的参数（搜索组件已处理日期范围）
    if (params) {
      Object.assign(searchForm.value, params)
    }
    // 使用 getData 方法（实际是 getDataByPage），会自动重置到第一页并清空当前搜索条件的缓存
    getData(searchForm.value)
  }

  /**
   * 重置搜索参数
   */
  const resetSearchParams = () => {
    // 重置搜索表单
    searchForm.value = {
      title: undefined,
      businessType: undefined,
      operName: undefined,
      status: undefined,
      startTime: undefined,
      endTime: undefined,
      dateRange: null
    }
    // 直接使用重置后的搜索表单重新获取数据（getData 内部会重置到第一页）
    getData(searchForm.value)
  }

  /**
   * 显示详情
   */
  const showDetail = (row: OperLogListItem) => {
    currentOperLog.value = row
    detailDialogVisible.value = true
  }

  /**
   * 删除
   */
  const handleDelete = async (row: OperLogListItem) => {
    try {
      await ElMessageBox.confirm(t('system.operLog.deleteConfirm'), t('common.tips'), {
        type: 'warning',
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel')
      })
      await fetchDeleteOperLog([row.operId!])
      ElMessage.success(t('common.deleteSuccess'))
      refreshData()
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error(error?.message || t('common.deleteFail'))
      }
    }
  }

  /**
   * 批量删除
   */
  const handleBatchDelete = async () => {
    if (selectedRows.value.length === 0) {
      ElMessage.warning(t('common.pleaseSelect'))
      return
    }
    try {
      await ElMessageBox.confirm(t('system.operLog.deleteConfirm'), t('common.tips'), {
        type: 'warning',
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel')
      })
      const operIds = selectedRows.value.map((row: OperLogListItem) => row.operId!).filter(Boolean)
      await fetchDeleteOperLog(operIds)
      ElMessage.success(t('common.deleteSuccess'))
      refreshData()
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error(error?.message || t('common.deleteFail'))
      }
    }
  }

  /**
   * 清空日志
   */
  const handleClean = async () => {
    try {
      await ElMessageBox.confirm(t('system.operLog.clearConfirm'), t('common.tips'), {
        type: 'warning',
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel')
      })
      await fetchCleanOperLog()
      ElMessage.success(t('common.deleteSuccess'))
      refreshData()
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error(error?.message || t('common.deleteFail'))
      }
    }
  }

  /**
   * 选中行变化
   */
  const handleSelectionChange = (selection: OperLogListItem[]) => {
    selectedRows.value = selection
  }
</script>

<style scoped lang="scss">
  .oper-log-page {
    padding: 20px;
  }
</style>
