<!-- 登录日志页面 -->
<template>
  <div class="logininfor-page art-full-height">
    <ElCard class="art-table-card" shadow="never">
      <!-- 搜索栏 -->
      <div class="search-container">
        <ElForm :model="searchForm" :inline="true" class="search-form">
          <ElFormItem label="用户账号">
            <ElInput
              v-model="searchForm.userName"
              placeholder="请输入用户账号"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
          <ElFormItem label="登录IP">
            <ElInput
              v-model="searchForm.ipaddr"
              placeholder="请输入登录IP"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
          <ElFormItem label="登录状态">
            <ElSelect
              v-model="searchForm.status"
              placeholder="请选择登录状态"
              clearable
              style="width: 150px"
            >
              <ElOption label="成功" value="0" />
              <ElOption label="失败" value="1" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="登录时间">
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
              v-auth="'system:logininfor:delete'"
            >
              批量删除
            </ElButton>
            <ElButton type="danger" @click="handleClean" v-ripple v-auth="'system:logininfor:delete'">
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
      <LogininforDetail v-model:visible="detailDialogVisible" :logininfor="currentLogininfor" />
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { useTable } from '@/hooks/core/useTable'
  import { fetchGetLogininforList, fetchDeleteLogininfor, fetchCleanLogininfor } from '@/api/log/logininfor'
  import { ElMessageBox, ElMessage, ElTag, ElButton } from 'element-plus'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import { useAuth } from '@/hooks/core/useAuth'
  import type { LogininforListItem, LogininforSearchParams } from '@/types/api/logininfor'
  import LogininforDetail from './modules/logininfor-detail.vue'

  defineOptions({ name: 'Logininfor' })

  const { hasAuth } = useAuth()

  // 搜索表单
  const searchForm = ref<LogininforSearchParams>({
    userName: undefined,
    ipaddr: undefined,
    status: undefined,
    startTime: undefined,
    endTime: undefined,
    pageNum: 1,
    pageSize: 20
  })

  // 日期范围
  const dateRange = ref<[string, string] | null>(null)

  // 选中行
  const selectedRows = ref<LogininforListItem[]>([])

  // 详情对话框
  const detailDialogVisible = ref(false)
  const currentLogininfor = ref<LogininforListItem | null>(null)

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
      apiFn: fetchGetLogininforList,
      apiParams: {
        pageNum: 1,
        pageSize: 20
      },
      immediate: true, // 确保页面加载时自动获取数据
      columnsFactory: () => [
        { type: 'selection' }, // 勾选列
        { type: 'index', width: 60, label: '序号' }, // 序号
        {
          prop: 'loginTime',
          label: '登录时间',
          width: 180,
          sortable: true
        },
        {
          prop: 'userName',
          label: '用户账号',
          width: 120
        },
        {
          prop: 'ipaddr',
          label: '登录IP',
          width: 140
        },
        {
          prop: 'loginLocation',
          label: '登录地点',
          width: 150
        },
        {
          prop: 'browser',
          label: '浏览器',
          width: 180,
          showOverflowTooltip: true
        },
        {
          prop: 'os',
          label: '操作系统',
          width: 150,
          showOverflowTooltip: true
        },
        {
          prop: 'status',
          label: '登录状态',
          width: 100,
          formatter: (row: LogininforListItem) => {
            return h(
              ElTag,
              {
                type: row.status === '0' ? 'success' : 'danger',
                size: 'small'
              },
              () => (row.status === '0' ? '成功' : '失败')
            )
          }
        },
        {
          prop: 'msg',
          label: '提示消息',
          minWidth: 200,
          showOverflowTooltip: true
        },
        {
          prop: 'operation',
          label: '操作',
          width: 120,
          fixed: 'right',
          formatter: (row: LogininforListItem) => {
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
              hasAuth('system:logininfor:delete') &&
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
      userName: undefined,
      ipaddr: undefined,
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
  const showDetail = (row: LogininforListItem) => {
    currentLogininfor.value = row
    detailDialogVisible.value = true
  }

  /**
   * 删除
   */
  const handleDelete = async (row: LogininforListItem) => {
    try {
      await ElMessageBox.confirm('确定要删除这条登录日志吗？', '提示', {
        type: 'warning'
      })
      await fetchDeleteLogininfor([row.infoId!])
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
        `确定要删除选中的 ${selectedRows.value.length} 条登录日志吗？`,
        '提示',
        {
          type: 'warning'
        }
      )
      const infoIds = selectedRows.value.map((row: LogininforListItem) => row.infoId!).filter(Boolean)
      await fetchDeleteLogininfor(infoIds)
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
      await ElMessageBox.confirm('确定要清空所有登录日志吗？此操作不可恢复！', '警告', {
        type: 'warning',
        confirmButtonText: '确定清空',
        cancelButtonText: '取消'
      })
      await fetchCleanLogininfor()
      ElMessage.success('清空成功')
      refreshData()
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error(error?.message || '清空失败')
      }
    }
  }

  /**
   * 选中行变化
   */
  const handleSelectionChange = (selection: LogininforListItem[]) => {
    selectedRows.value = selection
  }
</script>

<style scoped lang="scss">
  .logininfor-page {
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
