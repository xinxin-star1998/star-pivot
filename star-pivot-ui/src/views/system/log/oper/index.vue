<!-- 操作日志页面 -->
<template>
  <div class="oper-log-page art-full-height">
    <ElCard class="art-table-card" shadow="never">
      <!-- 搜索栏 -->
      <div class="search-container">
        <ElForm :model="searchForm" :inline="true" class="search-form">
          <ElFormItem label="模块标题">
            <ElInput
              v-model="searchForm.title"
              placeholder="请输入模块标题"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
          <ElFormItem label="业务类型">
            <ElSelect
              v-model="searchForm.businessType"
              placeholder="请选择业务类型"
              clearable
              style="width: 150px"
            >
              <ElOption label="其它" :value="0" />
              <ElOption label="新增" :value="1" />
              <ElOption label="修改" :value="2" />
              <ElOption label="删除" :value="3" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="操作人员">
            <ElInput
              v-model="searchForm.operName"
              placeholder="请输入操作人员"
              clearable
              style="width: 150px"
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
          <ElFormItem label="操作状态">
            <ElSelect
              v-model="searchForm.status"
              placeholder="请选择操作状态"
              clearable
              style="width: 150px"
            >
              <ElOption label="正常" :value="0" />
              <ElOption label="异常" :value="1" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="操作时间">
            <ElDatePicker
              v-model="dateRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 400px"
            />
          </ElFormItem>
          <ElFormItem>
            <ElButton type="primary" @click="handleSearch" v-ripple>搜索</ElButton>
            <ElButton @click="resetSearchParams" v-ripple>重置</ElButton>
          </ElFormItem>
        </ElForm>
      </div>

      <!-- 表格头部 -->
      <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
        <template #left>
          <ElSpace wrap>
            <ElButton
              type="danger"
              :disabled="selectedRows.length === 0"
              @click="handleBatchDelete"
              v-ripple
              v-auth="'system:operlog:delete'"
            >
              批量删除
            </ElButton>
            <ElButton type="danger" @click="handleClean" v-ripple v-auth="'system:operlog:delete'">
              清空日志
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
  import { fetchGetOperLogList, fetchDeleteOperLog, fetchCleanOperLog } from '@/api/log/operlog'
  import { ElMessageBox, ElMessage, ElTag, ElButton } from 'element-plus'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import { useAuth } from '@/hooks/core/useAuth'
  import type { OperLogListItem, OperLogSearchParams } from '@/types/api/operlog'
  import OperLogDetail from './modules/oper-log-detail.vue'

  defineOptions({ name: 'OperLog' })

  const { hasAuth } = useAuth()

  // 搜索表单
  const searchForm = ref<OperLogSearchParams>({
    title: undefined,
    businessType: undefined,
    operName: undefined,
    status: undefined,
    startTime: undefined,
    endTime: undefined,
    pageNum: 1,
    pageSize: 20
  })

  // 日期范围
  const dateRange = ref<[string, string] | null>(null)

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
    resetSearchParams: resetTableSearchParams,
    handleSizeChange,
    handleCurrentChange,
    refreshData
  } = useTable({
    // 核心配置
    core: {
      apiFn: fetchGetOperLogList,
      apiParams: {
        pageNum: 1,
        pageSize: 20
      },
      immediate: true, // 确保页面加载时自动获取数据
      columnsFactory: () => [
        { type: 'selection' }, // 勾选列
        { type: 'index', width: 60, label: '序号' }, // 序号
        {
          prop: 'operTime',
          label: '操作时间',
          width: 180,
          sortable: true
        },
        {
          prop: 'title',
          label: '模块标题',
          width: 150
        },
        {
          prop: 'businessType',
          label: '业务类型',
          width: 100,
          formatter: (row: OperLogListItem) => {
            return getBusinessTypeText(row.businessType)
          }
        },
        {
          prop: 'operName',
          label: '操作人员',
          width: 120
        },
        {
          prop: 'deptName',
          label: '部门',
          width: 120
        },
        {
          prop: 'operUrl',
          label: '请求URL',
          minWidth: 200,
          showOverflowTooltip: true
        },
        {
          prop: 'requestMethod',
          label: '请求方式',
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
            return h(ElTag, { type: colorMap[method] || 'info', size: 'small' }, () => method)
          }
        },
        {
          prop: 'operIp',
          label: '操作IP',
          width: 140
        },
        {
          prop: 'status',
          label: '状态',
          width: 100,
          formatter: (row: OperLogListItem) => {
            return h(
              ElTag,
              {
                type: row.status === 0 ? 'success' : 'danger',
                size: 'small'
              },
              () => (row.status === 0 ? '正常' : '异常')
            )
          }
        },
        {
          prop: 'costTime',
          label: '耗时(ms)',
          width: 100,
          sortable: true
        },
        {
          prop: 'operation',
          label: '操作',
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
                () => '详情'
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
                  () => '删除'
                )
            ])
          }
        }
      ]
    }
  })

  /**
   * 搜索
   */
  const handleSearch = () => {
    if (dateRange.value && dateRange.value.length === 2) {
      searchForm.value.startTime = dateRange.value[0]
      searchForm.value.endTime = dateRange.value[1]
    } else {
      searchForm.value.startTime = undefined
      searchForm.value.endTime = undefined
    }
    searchForm.value.pageNum = 1
    getData(searchForm.value)
  }

  /**
   * 重置搜索参数
   */
  const resetSearchParams = () => {
    searchForm.value = {
      title: undefined,
      businessType: undefined,
      operName: undefined,
      status: undefined,
      startTime: undefined,
      endTime: undefined,
      pageNum: 1,
      pageSize: 20
    }
    dateRange.value = null
    resetTableSearchParams()
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
      await ElMessageBox.confirm('确定要删除这条操作日志吗？', '提示', {
        type: 'warning'
      })
      await fetchDeleteOperLog([row.operId!])
      ElMessage.success('删除成功')
      refreshData()
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error(error?.message || '删除失败')
      }
    }
  }

  /**
   * 批量删除
   */
  const handleBatchDelete = async () => {
    if (selectedRows.value.length === 0) {
      ElMessage.warning('请选择要删除的日志')
      return
    }
    try {
      await ElMessageBox.confirm(
        `确定要删除选中的 ${selectedRows.value.length} 条操作日志吗？`,
        '提示',
        {
          type: 'warning'
        }
      )
      const operIds = selectedRows.value.map((row: OperLogListItem) => row.operId!).filter(Boolean)
      await fetchDeleteOperLog(operIds)
      ElMessage.success('删除成功')
      refreshData()
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error(error?.message || '删除失败')
      }
    }
  }

  /**
   * 清空日志
   */
  const handleClean = async () => {
    try {
      await ElMessageBox.confirm('确定要清空所有操作日志吗？此操作不可恢复！', '警告', {
        type: 'warning',
        confirmButtonText: '确定清空',
        cancelButtonText: '取消'
      })
      await fetchCleanOperLog()
      ElMessage.success('清空成功')
      refreshData()
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error(error?.message || '清空失败')
      }
    }
  }

  /**
   * 获取业务类型文本（用于表格显示）
   */
  const getBusinessTypeText = (businessType?: number) => {
    const map: Record<number, string> = {
      0: '其它',
      1: '新增',
      2: '修改',
      3: '删除'
    }
    return map[businessType ?? 0] || '未知'
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

  .search-container {
    margin-bottom: 20px;
    padding: 20px;
    background: #f5f7fa;
    border-radius: 4px;
  }

  .search-form {
    margin: 0;
  }
</style>
