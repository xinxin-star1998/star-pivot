<!-- 登录日志页面 -->
<template>
  <div class="logininfor-page art-full-height">
    <ElCollapseTransition>
      <div v-show="showSearchBar">
        <LogininforSearch v-model="searchForm" @search="handleSearch" @reset="handleReset" />
      </div>
    </ElCollapseTransition>

    <ElCard
      class="art-table-card"
      shadow="never"
      :style="{ 'margin-top': showSearchBar ? '12px' : '0' }"
    >
      <ArtTableHeader
        v-model:columns="columnChecks"
        v-model:showSearchBar="showSearchBar"
        :loading="loading"
        @refresh="handleRefresh"
      >
        <template #left>
          <ElSpace wrap>
            <ElButton
              type="danger"
              :disabled="!hasSelectedRows"
              @click="handleBatchDelete"
              v-ripple
              v-auth="AUTH_DELETE"
            >
              {{ t('common.batchDelete') }}
            </ElButton>
            <ElButton type="danger" @click="handleClean" v-ripple v-auth="AUTH_DELETE">
              {{ t('system.loginLog.clear') }}
            </ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <ArtTable
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        @selection-change="handleSelectionChange"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      />

      <LogininforDetail v-model:visible="detailDialogVisible" :logininfor="currentLogininfor" />
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { useTable } from '@/hooks/core/useTable'
  import {
    fetchCleanLogininfor,
    fetchDeleteLogininfor,
    fetchGetLogininforList
  } from '@/api/log/logininfor'
  import { ElButton, ElCollapseTransition, ElMessage, ElMessageBox, ElTag } from 'element-plus'
  import { useI18n } from 'vue-i18n'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import { useAuth } from '@/hooks/core/useAuth'
  import type { LogininforListItem, LogininforSearchParams } from '@/types/api/logininfor'
  import LogininforDetail from './modules/logininfor-detail.vue'
  import LogininforSearch from './modules/logininfor-search.vue'

  defineOptions({ name: 'Logininfor' })

  const AUTH_DELETE = 'system:logininfor:delete'

  const LOGIN_STATUS = {
    SUCCESS: '0',
    FAILED: '1'
  } as const

  const DEFAULT_PAGE_SIZE = 20

  const { hasAuth } = useAuth()
  const { t } = useI18n()

  const showSearchBar = ref(true)

  const searchForm = ref<LogininforSearchParams & { dateRange?: [string, string] | null }>({
    userName: undefined,
    ipaddr: undefined,
    status: undefined,
    startTime: undefined,
    endTime: undefined,
    dateRange: null
  })

  const selectedRows = ref<LogininforListItem[]>([])
  const detailDialogVisible = ref(false)
  const currentLogininfor = ref<LogininforListItem | null>(null)

  const hasSelectedRows = computed(() => selectedRows.value.length > 0)

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
    core: {
      apiFn: fetchGetLogininforList,
      apiParams: {
        pageSize: DEFAULT_PAGE_SIZE
      },
      immediate: true,
      columnsFactory: () => createTableColumns()
    }
  })

  function createTableColumns() {
    return [
      { type: 'selection' as const },
      { type: 'index' as const, width: 60, label: t('table.column.index') },
      {
        prop: 'loginTime',
        label: t('system.loginLog.loginTime'),
        width: 180,
        sortable: true
      },
      {
        prop: 'userName',
        label: t('system.loginLog.userName'),
        width: 120
      },
      {
        prop: 'ipaddr',
        label: t('system.loginLog.ipaddr'),
        width: 140
      },
      {
        prop: 'loginLocation',
        label: t('system.loginLog.loginLocation'),
        width: 150
      },
      {
        prop: 'browser',
        label: t('system.loginLog.browser'),
        width: 180,
        showOverflowTooltip: true
      },
      {
        prop: 'os',
        label: t('system.loginLog.os'),
        width: 150,
        showOverflowTooltip: true
      },
      {
        prop: 'status',
        label: t('system.loginLog.status'),
        width: 100,
        formatter: (row: LogininforListItem) => {
          const isSuccess = row.status === LOGIN_STATUS.SUCCESS
          return h(
            ElTag,
            {
              type: isSuccess ? 'success' : 'danger',
              size: 'small'
            },
            () => (isSuccess ? t('system.loginLog.statusSuccess') : t('system.loginLog.statusFail'))
          )
        }
      },
      {
        prop: 'msg',
        label: t('system.loginLog.msg'),
        minWidth: 200,
        showOverflowTooltip: true
      },
      {
        prop: 'operation',
        label: t('common.operation'),
        width: 120,
        fixed: 'right' as const,
        formatter: (row: LogininforListItem) => {
          return h('div', { class: 'flex gap-2' }, [
            h(
              ElButton,
              {
                type: 'primary',
                link: true,
                size: 'small',
                onClick: () => handleShowDetail(row)
              },
              () => t('system.operLog.detail')
            ),
            hasAuth(AUTH_DELETE) &&
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

  const handleSearch = (params?: LogininforSearchParams) => {
    if (params) {
      Object.assign(searchForm.value, params)
    }
    getData(searchForm.value)
  }

  const handleReset = () => {
    searchForm.value = {
      userName: undefined,
      ipaddr: undefined,
      status: undefined,
      startTime: undefined,
      endTime: undefined,
      dateRange: null
    }
    getData(searchForm.value)
  }

  const handleShowDetail = (row: LogininforListItem) => {
    currentLogininfor.value = row
    detailDialogVisible.value = true
  }

  const handleDelete = async (row: LogininforListItem) => {
    if (!row.infoId) {
      ElMessage.warning(t('common.pleaseSelect'))
      return
    }

    try {
      await ElMessageBox.confirm(t('system.loginLog.deleteConfirm'), t('common.tips'), {
        type: 'warning',
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel')
      })

      await fetchDeleteLogininfor([row.infoId])
      ElMessage.success(t('common.deleteSuccess'))
      refreshData()
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error(error?.message || t('common.deleteFail'))
      }
    }
  }

  const handleBatchDelete = async () => {
    if (!hasSelectedRows.value) {
      ElMessage.warning(t('common.pleaseSelect'))
      return
    }

    const infoIds = selectedRows.value
      .map((row: LogininforListItem) => row.infoId)
      .filter((id: number | undefined): id is number => Boolean(id))

    if (infoIds.length === 0) {
      ElMessage.warning(t('common.pleaseSelect'))
      return
    }

    try {
      await ElMessageBox.confirm(t('system.loginLog.deleteConfirm'), t('common.tips'), {
        type: 'warning',
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel')
      })

      await fetchDeleteLogininfor(infoIds)
      ElMessage.success(t('common.deleteSuccess'))
      selectedRows.value = []
      refreshData()
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error(error?.message || t('common.deleteFail'))
      }
    }
  }

  const handleClean = async () => {
    try {
      await ElMessageBox.confirm(t('system.loginLog.clearConfirm'), t('common.tips'), {
        type: 'warning',
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel')
      })

      await fetchCleanLogininfor()
      ElMessage.success(t('common.deleteSuccess'))
      refreshData()
    } catch (error: any) {
      if (error !== 'cancel') {
        ElMessage.error(error?.message || t('common.deleteFail'))
      }
    }
  }

  const handleSelectionChange = (selection: LogininforListItem[]) => {
    selectedRows.value = selection
  }

  const handleRefresh = () => {
    refreshData()
  }
</script>

<style scoped lang="scss">
  .logininfor-page {
    padding: 20px;
  }
</style>
