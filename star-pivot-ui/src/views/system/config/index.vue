<!-- 参数配置管理页面 -->
<template>
  <div class="config-page">
    <!-- 搜索栏 -->
    <ConfigSearch
      v-model="searchForm"
      v-auth="'system:config:list'"
      @reset="resetSearchParams"
      @search="handleSearch"
    />

    <ElCard class="art-table-card config-table-card" shadow="never">
      <!-- 表格头部 -->
      <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
        <template #left>
          <ElSpace class="table-actions" wrap>
            <ElButton
              v-auth="'system:config:add'"
              v-ripple
              class="action-btn"
              @click="showDialog('add')"
            >
              {{ t('system.config.addConfig') }}
            </ElButton>
            <ElButton
              v-auth="'system:config:export'"
              v-ripple
              :loading="exporting"
              plain
              type="primary"
              @click="handleExport"
            >
              {{ t('system.config.exportConfig') }}
            </ElButton>
            <ElButton
              v-auth="'system:config:delete'"
              v-ripple
              :disabled="selectedRows.length === 0"
              class="action-btn"
              type="danger"
              @click="handleBatchDelete"
            >
              {{ t('common.batchDelete') }}
            </ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <!-- 表格 -->
      <ArtTable
        :columns="columns"
        :data="data"
        :loading="loading"
        :pagination="pagination"
        class="config-table"
        @selection-change="handleSelectionChange"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
      </ArtTable>

      <!-- 参数配置弹窗 -->
      <ConfigDialog
        v-model:visible="dialogVisible"
        :config-data="currentConfigData"
        :type="dialogType"
        @submit="handleDialogSubmit"
      />
    </ElCard>
  </div>
</template>

<script lang="ts" setup>
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import { useTable } from '@/hooks/core/useTable'
  import { useI18n } from 'vue-i18n'
  import {
    type Config,
    fetchDeleteConfig,
    fetchExportConfig,
    fetchGetConfigList
  } from '@/api/system/config/config'
  import ConfigSearch from './modules/config-search.vue'
  import ConfigDialog from './modules/config-dialog.vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import type { ColumnOption } from '@/types'
  import { DialogType } from '@/types'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import { useAuth } from '@/hooks/core/useAuth'
  import { handleMutationError } from '@/utils/http/mutation'
  import { h, nextTick, ref } from 'vue'

  defineOptions({ name: 'Config' })

  const { hasAuth } = useAuth()
  const { t } = useI18n()

  const dialogType = ref<DialogType>('add')
  const dialogVisible = ref(false)
  const currentConfigData = ref<Partial<Config>>({})

  const selectedRows = ref<Config[]>([])
  const exporting = ref(false)

  const searchForm = ref({
    configName: undefined,
    configKey: undefined,
    configValue: undefined,
    configType: undefined
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
      apiFn: fetchGetConfigList,
      apiParams: {
        pageNum: 1,
        pageSize: 20,
        ...searchForm.value
      },
      columnsFactory: (): ColumnOption<Config>[] => [
        { type: 'selection' },
        { type: 'index', width: 60, label: t('table.column.index') },
        {
          prop: 'configName',
          label: t('system.config.configName'),
          width: 200,
          showOverflowTooltip: true
        },
        {
          prop: 'configKey',
          label: t('system.config.configKey'),
          width: 200,
          showOverflowTooltip: true
        },
        {
          prop: 'configValue',
          label: t('system.config.configValue'),
          minWidth: 100,
          showOverflowTooltip: true
        },
        {
          prop: 'configType',
          label: t('system.config.builtIn'),
          width: 120,
          showOverflowTooltip: true,
          formatter: (row: Config) => {
            if (row.configType === 'Y') return t('system.config.yes')
            if (row.configType === 'N') return t('system.config.no')
            return row.configType || t('common.empty')
          }
        },
        {
          prop: 'remark',
          label: t('common.remark'),
          minWidth: 120,
          showOverflowTooltip: true
        },
        {
          prop: 'operation',
          label: t('common.operation'),
          width: 120,
          fixed: 'right',
          formatter: (row) => {
            const actions: any[] = []

            if (hasAuth('system:config:edit')) {
              actions.push(
                h(ArtButtonTable, {
                  type: 'edit',
                  onClick: () => showDialog('edit', row)
                })
              )
            }

            if (hasAuth('system:config:delete')) {
              actions.push(
                h(ArtButtonTable, {
                  type: 'delete',
                  onClick: () => deleteConfig(row)
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

  const showDialog = (type: DialogType, row?: Config): void => {
    dialogType.value = type
    currentConfigData.value = row || {}
    nextTick(() => {
      dialogVisible.value = true
    })
  }

  const deleteConfig = async (row: Config): Promise<void> => {
    if (!row.configId) {
      ElMessage.warning(t('common.pleaseSelect'))
      return
    }
    try {
      await ElMessageBox.confirm(t('system.config.deleteConfirm'), t('common.tips'), {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'error'
      })
      await fetchDeleteConfig([row.configId])
      refreshData()
      ElMessage.success(t('common.deleteSuccess'))
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除参数配置失败:', error)
        handleMutationError(error, t('common.deleteFail'))
      }
    }
  }

  const handleBatchDelete = (): void => {
    if (selectedRows.value.length === 0) {
      ElMessage.warning(t('common.pleaseSelect'))
      return
    }
    ElMessageBox.confirm(t('system.config.deleteConfirm'), t('common.tips'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'error'
    })
      .then(async () => {
        const ids = selectedRows.value
          .map((row) => row.configId)
          .filter((id): id is number => typeof id === 'number')
        if (ids.length === 0) {
          ElMessage.warning(t('common.pleaseSelect'))
          return
        }
        await fetchDeleteConfig(ids)
        selectedRows.value = []
        refreshData()
        ElMessage.success(t('common.deleteSuccess'))
      })
      .catch(() => {
        // 用户取消删除
      })
  }

  const handleExport = async (): Promise<void> => {
    try {
      exporting.value = true
      await fetchExportConfig(searchParams as Record<string, any>)
      ElMessage.success(t('common.updateSuccess'))
    } catch (error) {
      console.error('导出参数配置失败:', error)
      handleMutationError(error, t('system.config.loadFail'))
    } finally {
      exporting.value = false
    }
  }

  const handleDialogSubmit = async () => {
    try {
      dialogVisible.value = false
      currentConfigData.value = {}
      refreshData()
    } catch (error) {
      console.error('提交失败:', error)
    }
  }

  const handleSelectionChange = (selection: Config[]): void => {
    selectedRows.value = selection
  }
</script>

<style lang="scss" scoped>
  .config-page {
    padding: 0 var(--art-page-padding) var(--art-page-padding);
  }

  .search-panel {
    padding: 14px 16px 2px;
    margin-bottom: 12px;
    background-color: var(--default-box-color);
    border: 1px solid var(--art-card-border);
    border-radius: 12px;
    box-shadow: var(--art-shadow-card);
  }

  .config-table-card {
    overflow: hidden;
    border: 1px solid var(--art-card-border);
    border-radius: 12px;
    box-shadow: var(--art-shadow-card);
  }

  .table-actions {
    :deep(.el-button + .el-button) {
      margin-left: 8px;
    }
  }

  .action-btn {
    font-weight: 500;
    border-radius: 8px;
    box-shadow: var(--art-shadow-sm);
  }

  .config-table {
    :deep(.el-table) {
      --el-table-header-bg-color: var(--art-gray-100);
      --el-table-row-hover-bg-color: var(--art-gray-50);
    }

    :deep(.el-table th.el-table__cell) {
      font-weight: 600;
      color: var(--art-gray-800);
    }

    :deep(.el-table td.el-table__cell) {
      padding: 10px 0;
    }
  }
</style>
