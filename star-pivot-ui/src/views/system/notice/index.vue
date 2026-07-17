<!-- 通知公告管理页面 -->
<template>
  <div class="notice-page art-full-height">
    <!-- 搜索栏 -->
    <NoticeSearch
      v-model="searchForm"
      @search="handleSearch"
      @reset="resetSearchParams"
    ></NoticeSearch>

    <ElCard class="art-table-card" shadow="never">
      <!-- 表格头部 -->
      <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
        <template #left>
          <ElSpace wrap>
            <ElButton @click="showDialog('add')" v-ripple v-auth="'system:notice:add'">
              {{ t('system.notice.addNotice') }}
            </ElButton>
            <ElButton
              type="danger"
              :disabled="selectedRows.length === 0"
              @click="handleBatchDelete"
              v-ripple
              v-auth="'system:notice:delete'"
            >
              {{ t('common.batchDelete') }}
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

      <!-- 通知公告弹窗 -->
      <NoticeDialog
        v-model:visible="dialogVisible"
        :type="dialogType"
        :notice-data="currentNoticeData"
        @submit="handleDialogSubmit"
      />
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { h, nextTick, onMounted, ref } from 'vue'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import { useTable } from '@/hooks/core/useTable'
  import { useDict } from '@/hooks/core/useDict'
  import { useI18n } from 'vue-i18n'
  import { fetchDeleteNotice, fetchGetNoticeList, type Notice } from '@/api/system/notice/notice'
  import { handleMutationError } from '@/utils/http/mutation'
  import NoticeSearch from './modules/notice-search.vue'
  import NoticeDialog from './modules/notice-dialog.vue'
  import { ElMessage, ElMessageBox, ElTag } from 'element-plus'
  import type { ColumnOption } from '@/types'
  import { DialogType } from '@/types'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import { useAuth } from '@/hooks/core/useAuth'

  defineOptions({ name: 'Notice' })

  const { hasAuth } = useAuth()
  const { t } = useI18n()
  const { getDictItem, getTagType, loadDicts } = useDict()

  const dialogType = ref<DialogType>('add')
  const dialogVisible = ref(false)
  const currentNoticeData = ref<Partial<Notice>>({})

  const selectedRows = ref<Notice[]>([])

  const searchForm = ref({
    noticeTitle: undefined,
    noticeType: undefined,
    noticeContent: undefined,
    status: undefined
  })

  const {
    columns,
    columnChecks,
    data,
    loading,
    pagination,
    getData,
    searchParams,
    resetSearchParams,
    handleSizeChange,
    handleCurrentChange,
    refreshData
  } = useTable({
    core: {
      apiFn: fetchGetNoticeList,
      apiParams: {
        pageNum: 1,
        pageSize: 20,
        ...searchForm.value
      },
      columnsFactory: (): ColumnOption<Notice>[] => [
        { type: 'selection' },
        { type: 'index', width: 60, label: t('table.column.index') },
        {
          prop: 'noticeTitle',
          label: t('system.notice.noticeTitle'),
          minWidth: 150,
          showOverflowTooltip: true
        },
        {
          prop: 'noticeType',
          label: t('system.notice.noticeType'),
          formatter: (row: Notice) => {
            const dictItem = getDictItem('sys_notice_type', row.noticeType)
            if (dictItem) {
              return h(
                ElTag,
                { type: getTagType(dictItem.cssClass) as any },
                () => dictItem.dictLabel
              )
            }
            return row.noticeType || '-'
          }
        },
        {
          prop: 'noticeContent',
          label: t('system.notice.noticeContent'),
          minWidth: 150,
          showOverflowTooltip: true
        },
        {
          prop: 'status',
          label: t('system.notice.noticeStatus'),
          formatter: (row: Notice) => {
            const dictItem = getDictItem('sys_notice_status', row.status)
            if (dictItem) {
              return h(
                ElTag,
                { type: getTagType(dictItem.cssClass) as any },
                () => dictItem.dictLabel
              )
            }
            return row.status || '-'
          }
        },
        {
          prop: 'createBy',
          label: t('common.operation'),
          width: 100,
          showOverflowTooltip: true
        },
        {
          prop: 'createTime',
          label: t('common.createTime'),
          width: 150,
          showOverflowTooltip: true
        },
        {
          prop: 'operation',
          label: t('common.operation'),
          width: 120,
          fixed: 'right',
          formatter: (row) => {
            const actions: any[] = []

            if (hasAuth('system:notice:edit')) {
              actions.push(
                h(ArtButtonTable, {
                  type: 'edit',
                  onClick: () => showDialog('edit', row)
                })
              )
            }

            if (hasAuth('system:notice:delete')) {
              actions.push(
                h(ArtButtonTable, {
                  type: 'delete',
                  onClick: () => deleteNotice(row)
                })
              )
            }

            if (actions.length === 0) {
              return h('span', { style: 'color: var(--art-gray-500)' }, '')
            }

            return h('div', actions)
          }
        }
      ]
    },
    transform: {
      dataTransformer: (records) => {
        if (!Array.isArray(records)) {
          return []
        }
        return records
      }
    }
  })

  const handleSearch = (params: Record<string, any>) => {
    Object.assign(searchParams, params)
    getData()
  }

  const showDialog = (type: DialogType, row?: Notice): void => {
    dialogType.value = type
    currentNoticeData.value = row || {}
    nextTick(() => {
      dialogVisible.value = true
    })
  }

  const deleteNotice = async (row: Notice): Promise<void> => {
    try {
      await ElMessageBox.confirm(t('system.notice.deleteConfirm'), t('common.tips'), {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'error'
      })
      const id = row.noticeId
      if (id == null) return
      await fetchDeleteNotice([id])
      refreshData()
      ElMessage.success(t('common.deleteSuccess'))
    } catch (error) {
      handleMutationError(error, t('common.deleteFail'))
    }
  }

  const handleBatchDelete = async (): Promise<void> => {
    if (selectedRows.value.length === 0) {
      ElMessage.warning(t('common.pleaseSelect'))
      return
    }
    const ids = selectedRows.value
      .map((row) => row.noticeId)
      .filter((id): id is number => id != null)
    if (ids.length === 0) return
    try {
      await ElMessageBox.confirm(t('system.notice.deleteConfirm'), t('common.tips'), {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'error'
      })
      await fetchDeleteNotice(ids)
      selectedRows.value = []
      refreshData()
      ElMessage.success(t('common.deleteSuccess'))
    } catch (error) {
      handleMutationError(error, t('common.deleteFail'))
    }
  }

  const handleDialogSubmit = async () => {
    try {
      dialogVisible.value = false
      currentNoticeData.value = {}
      refreshData()
    } catch (error) {
      console.error('提交失败:', error)
    }
  }

  const handleSelectionChange = (selection: Notice[]): void => {
    selectedRows.value = selection
  }

  const initDictData = async () => {
    await loadDicts(['sys_notice_type', 'sys_notice_status'])
  }

  onMounted(() => {
    initDictData()
  })
</script>

<style scoped lang="scss">
  .notice-page {
    padding: var(--art-page-padding);
    background-color: var(--default-bg-color);
  }

  :deep(.art-table-card) {
    border: 1px solid var(--art-card-border);
    border-radius: 12px;
    box-shadow: var(--art-shadow-card);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: var(--art-shadow-card-hover);
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
</style>
