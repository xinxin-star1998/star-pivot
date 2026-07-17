<!-- 代码生成页面 -->
<template>
  <div class="generator-page art-full-height">
    <!-- 搜索栏 -->
    <ArtSearchBar
      v-model="searchForm"
      :items="formItems"
      :span="6"
      label-width="84px"
      :showExpand="false"
      @reset="handleReset"
      @search="handleSearch"
    />

    <ElCard class="art-table-card" shadow="never">
      <!-- 表格头部 -->
      <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
        <template #left>
          <ElSpace wrap>
            <ElButton
              @click="handleBatchGenerateCode(selectedRows)"
              v-ripple
              v-auth="'tool:gen:create'"
            >
              <ArtSvgIcon icon="ri:download-line" class="mr-1" />
              {{ t('tools.gen.genCode') }}
            </ElButton>
            <ElButton @click="handleCreateTable" v-ripple v-auth="'tool:gen:add'">
              <ArtSvgIcon icon="ri:add-line" class="mr-1" />
              {{ t('tools.gen.createTable') }}
            </ElButton>
            <ElButton @click="handleImportTable" v-ripple v-auth="'tool:gen:import'">
              <ArtSvgIcon icon="ri:file-upload-line" class="mr-1" />
              {{ t('tools.gen.importTable') }}
            </ElButton>
            <ElButton
              type="danger"
              :disabled="selectedRows.length === 0"
              @click="handleDeleteTable"
              v-ripple
              v-auth="'tool:gen:delete'"
            >
              <ArtSvgIcon icon="ri:delete-bin-line" class="mr-1" />
              {{ t('common.delete') }}
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
      <!-- 创建表弹窗 -->
      <genAddDialog
        v-model:visible="dialogVisible"
        :type="dialogType"
        @submit="handleDialogSubmit"
      />
      <!-- 导入表弹窗 -->
      <ImportDialog v-model:visible="importDialogVisible" @success="handleImportSuccess" />
      <!-- 代码预览弹窗 -->
      <PreviewDialog v-model:visible="previewVisible" :table-id="previewTableId" />
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { useRouter } from 'vue-router'
  import { useTable } from '@/hooks/core/useTable'
  import { useAuth } from '@/hooks/core/useAuth'
  import { handleMutationError } from '@/utils/http/mutation'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import ArtSearchBar from '@/components/core/forms/art-search-bar/index.vue'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import { ElButton, ElMessage, ElMessageBox, ElSpace, ElTag } from 'element-plus'
  import {
    fetchBatchGenerateCode,
    fetchDeleteTable,
    fetchGenerateCode,
    fetchGetGenTableList,
    fetchSyncDatabase
  } from '@/api/generator/gen-table'
  import FileSaver from 'file-saver'
  import { DialogType } from '@/types'
  import { useI18n } from 'vue-i18n'

  const genAddDialog = defineAsyncComponent(
    () => import('@views/tools/generator/modules/gen-add-dialog.vue')
  )
  const ImportDialog = defineAsyncComponent(
    () => import('@views/tools/generator/modules/ImportDialog.vue')
  )
  const PreviewDialog = defineAsyncComponent(
    () => import('@views/tools/generator/modules/PreviewDialog.vue')
  )

  defineOptions({ name: 'Generator' })

  // 权限检查
  const { hasAuth } = useAuth()
  const { t } = useI18n()
  const router = useRouter()
  const dialogType = ref<DialogType>('add')
  const dialogVisible = ref(false)
  // 导入表弹窗可见性
  const importDialogVisible = ref(false)
  // 代码预览弹窗可见性
  const previewVisible = ref(false)
  // 当前预览的表ID
  const previewTableId = ref<number>()
  type GenTableListItem = Api.Generator.GenTableListItem

  // 搜索表单
  const searchForm = ref({
    tableName: undefined,
    tableComment: undefined,
    className: undefined
  })

  // 选中行
  const selectedRows = ref<GenTableListItem[]>([])

  // 搜索表单配置
  const formItems = computed(() => [
    {
      label: t('tools.gen.tableName'),
      key: 'tableName',
      type: 'input',
      props: { clearable: true, placeholder: t('tools.gen.searchTable') }
    },
    {
      label: t('tools.gen.tableComment'),
      key: 'tableComment',
      type: 'input',
      props: { clearable: true, placeholder: t('common.pleaseInput') }
    },
    {
      label: t('tools.gen.className'),
      key: 'className',
      type: 'input',
      props: { clearable: true, placeholder: t('common.pleaseInput') }
    }
  ])

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
    // 核心配置
    core: {
      apiFn: fetchGetGenTableList,
      apiParams: {
        pageNum: 1,
        pageSize: 10,
        ...searchForm.value
      },
      columnsFactory: () => [
        { type: 'selection' },
        { type: 'index', width: 60, label: t('table.column.index') },
        {
          prop: 'tableName',
          label: t('tools.gen.tableName'),
          minWidth: 150,
          showOverflowTooltip: true
        },
        {
          prop: 'tableComment',
          label: t('tools.gen.tableComment'),
          minWidth: 150,
          showOverflowTooltip: true
        },
        {
          prop: 'className',
          label: t('tools.gen.className'),
          minWidth: 150,
          showOverflowTooltip: true
        },
        {
          prop: 'tplCategory',
          label: t('tools.gen.tplCategory'),
          width: 120,
          formatter: (row: GenTableListItem) => {
            const tplMap: Record<string, { text: string; type: 'success' | 'info' | 'warning' }> = {
              crud: { text: t('tools.gen.tplCrud'), type: 'success' },
              tree: { text: t('tools.gen.tplTree'), type: 'info' }
            }
            const config = tplMap[row.tplCategory || ''] || {
              text: row.tplCategory || '-',
              type: 'info' as const
            }
            return h(ElTag, { type: config.type }, () => config.text)
          }
        },
        {
          prop: 'tplWebType',
          label: t('tools.gen.tplWebType'),
          width: 120,
          formatter: (row: GenTableListItem) => {
            const webTypeMap: Record<
              string,
              { text: string; type: 'success' | 'info' | 'warning' }
            > = {
              'element-ui': { text: 'Element UI', type: 'info' },
              'element-plus': { text: 'Element Plus', type: 'success' },
              'art-design-pro': { text: 'Art Design Pro', type: 'warning' }
            }
            const config = webTypeMap[row.tplWebType || ''] || {
              text: row.tplWebType || '-',
              type: 'info' as const
            }
            return h(ElTag, { type: config.type }, () => config.text)
          }
        },
        {
          prop: 'functionName',
          label: t('tools.gen.functionLabel'),
          minWidth: 120,
          showOverflowTooltip: true
        },
        {
          prop: 'createTime',
          label: t('common.createTime'),
          width: 180,
          sortable: true
        },
        {
          prop: 'action',
          label: t('common.operation'),
          width: 200,
          fixed: 'right',
          align: 'center',
          formatter: (row: GenTableListItem) => {
            const actions: any[] = []

            // 预览按钮权限：tool:gen:preview
            if (hasAuth('tool:gen:preview')) {
              actions.push(
                h(ArtButtonTable, {
                  type: 'view',
                  onClick: () => handlePreview(row.tableId as number)
                })
              )
            }

            // 编辑按钮权限：tool:gen:edit
            if (hasAuth('tool:gen:edit')) {
              actions.push(
                h(ArtButtonTable, {
                  type: 'edit',
                  onClick: () => handleEdit(row.tableId as number)
                })
              )
            }

            // 删除按钮权限：tool:gen:remove
            if (hasAuth('tool:gen:delete')) {
              actions.push(
                h(ArtButtonTable, {
                  type: 'delete',
                  onClick: () => handleDelete(row.tableId as number)
                })
              )
            }

            // 同步按钮权限：tool:gen:sync
            if (hasAuth('tool:gen:sync')) {
              actions.push(
                h(ArtButtonTable, {
                  type: 'sync',
                  onClick: () =>
                    handleSync(row.tableName as string, row.tableComment || row.tableName)
                })
              )
            }

            // 生成代码按钮权限：tool:gen:code
            if (hasAuth('tool:gen:create')) {
              actions.push(
                h(ArtButtonTable, {
                  type: 'generate',
                  onClick: () => handleGenerateCode(row)
                })
              )
            }

            if (actions.length === 0) {
              // 无任何操作权限时返回空占位
              return h('span', { style: 'color: #999' }, '')
            }

            return h('div', { class: 'flex items-center justify-center' }, actions)
          }
        }
      ]
    }
  })

  /**
   * 搜索处理
   * ArtSearchBar 的 search 事件不携带参数，这里直接使用 v-model 的 searchForm
   */
  const handleSearch = () => {
    Object.assign(searchParams, searchForm.value)
    getData()
  }

  /**
   * 重置搜索
   */
  const handleReset = () => {
    searchForm.value = {
      tableName: undefined,
      tableComment: undefined,
      className: undefined
    }
    resetSearchParams()
    getData()
  }

  /**
   * 处理表格行选择变化
   */
  const handleSelectionChange = (selection: GenTableListItem[]): void => {
    selectedRows.value = selection
  }

  /**
   * 创建表
   */
  const handleCreateTable = () => {
    dialogVisible.value = true
    dialogType.value = 'add'
  }

  /**
   * 打开导入表弹窗
   */
  const handleImportTable = () => {
    importDialogVisible.value = true
  }

  /**
   * 导入表成功后刷新列表
   */
  const handleImportSuccess = () => {
    refreshData()
  }
  /**
   * 跳转到代码生成编辑页面
   * @param tableId 表ID
   */
  const handleEdit = (tableId: number) => {
    // 跳转到编辑页，由编辑页去加载表的详细配置信息
    router.push(`/tool/gen/edit/${tableId}`)
  }
  /**
   * 处理弹窗提交（创建表成功后的回调）
   * 子组件已经处理了创建逻辑，这里只需要刷新列表
   */
  const handleDialogSubmit = () => {
    // 子组件已经调用了创建表接口并显示了成功消息
    // 这里只需要刷新表格数据
    refreshData()
  }

  /**
   * 删除单个表（单删）
   * @param tableId 表ID
   */
  const handleDelete = async (tableId: number): Promise<void> => {
    try {
      // 查找对应的表信息用于提示
      const tableList = (data.value ?? []) as GenTableListItem[]
      const table = tableList.find((item) => item.tableId === tableId)
      const tableName = table?.tableName || tableId

      await ElMessageBox.confirm(
        t('tools.gen.deleteConfirm', { name: tableName }),
        t('tools.gen.deleteTitle'),
        {
          confirmButtonText: t('common.confirm'),
          cancelButtonText: t('common.cancel'),
          type: 'warning'
        }
      )

      await fetchDeleteTable([tableId])
      ElMessage.success(t('common.deleteSuccess'))
      refreshData()
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除表失败:', error)
        handleMutationError(error, t('common.deleteFail'))
      }
    }
  }

  /**
   * 预览代码
   * @param tableId 表ID
   */
  const handlePreview = (tableId: number): void => {
    previewTableId.value = tableId
    previewVisible.value = true
  }
  /**
   * 同步数据库
   * @param tableName 表名称
   * @param tableComment 表描述（用于提示）
   */
  const handleSync = async (tableName: string, tableComment?: string): Promise<void> => {
    try {
      const displayName = tableComment || tableName
      await ElMessageBox.confirm(
        t('tools.gen.syncConfirm', { name: displayName }),
        t('tools.gen.syncTitle'),
        {
          confirmButtonText: t('common.confirm'),
          cancelButtonText: t('common.cancel'),
          type: 'warning'
        }
      )
      await fetchSyncDatabase(tableName)
      ElMessage.success(t('tools.gen.syncSuccess'))
      refreshData()
    } catch (error) {
      if (error !== 'cancel') {
        console.error('同步数据库失败:', error)
        handleMutationError(error, t('tools.gen.syncFail'))
      }
    }
  }
  /**
   * 批量生成代码（下载方式）
   * @param selectedRows 选中的表数据
   */
  const handleBatchGenerateCode = async (selectedRows: GenTableListItem[]): Promise<void> => {
    if (selectedRows.length === 0) {
      ElMessage.warning(t('tools.gen.selectGenTable'))
      return
    }

    const tableNames = selectedRows.map((row: GenTableListItem) => row.tableName)
    const tableNamesStr = tableNames.join('、')
    try {
      await ElMessageBox.confirm(
        t('tools.gen.batchGenConfirm', { count: selectedRows.length, names: tableNamesStr }),
        t('tools.gen.batchGenTitle'),
        {
          confirmButtonText: t('common.confirm'),
          cancelButtonText: t('common.cancel'),
          type: 'info'
        }
      )

      const blob = await fetchBatchGenerateCode(tableNames)
      const fileName = `starPivot_${new Date().getTime()}.zip`
      FileSaver.saveAs(blob, fileName)
      ElMessage.success(t('tools.gen.genSuccess'))
    } catch (error) {
      if (error !== 'cancel') {
        console.error('批量生成代码失败:', error)
        handleMutationError(error, t('tools.gen.batchGenFail'))
      }
    }
  }
  /**
   * 生成代码
   * @param row 表数据行
   */
  const handleGenerateCode = async (row: GenTableListItem): Promise<void> => {
    const tableName = row.tableName
    try {
      await ElMessageBox.confirm(
        t('tools.gen.genCodeConfirm', { name: tableName }),
        t('tools.gen.genCodeTitle'),
        {
          confirmButtonText: t('common.confirm'),
          cancelButtonText: t('common.cancel'),
          type: 'info'
        }
      )

      const blob = await fetchGenerateCode(tableName)
      const fileName = `${tableName}_${new Date().getTime()}.zip`
      FileSaver.saveAs(blob, fileName)
      ElMessage.success(t('tools.gen.genSuccess'))
    } catch (error) {
      handleMutationError(error, t('tools.gen.genCodeFail'))
    }
  }

  /**
   * 批量删除表
   */
  const handleDeleteTable = async (): Promise<void> => {
    if (selectedRows.value.length === 0) {
      ElMessage.warning(t('tools.gen.selectDeleteTable'))
      return
    }

    try {
      const tableNames = selectedRows.value.map((row: GenTableListItem) => row.tableName).join(',')
      await ElMessageBox.confirm(
        t('tools.gen.batchDeleteConfirm', {
          count: selectedRows.value.length,
          names: tableNames
        }),
        t('tools.gen.batchDeleteTitle'),
        {
          confirmButtonText: t('common.confirm'),
          cancelButtonText: t('common.cancel'),
          type: 'warning'
        }
      )

      const tableIds = selectedRows.value.map((row: GenTableListItem) => row.tableId as number)
      await fetchDeleteTable(tableIds)
      ElMessage.success(t('common.deleteSuccess'))
      selectedRows.value = []
      refreshData()
    } catch (error) {
      if (error !== 'cancel') {
        console.error('批量删除表失败:', error)
        handleMutationError(error, t('common.deleteFail'))
      }
    }
  }
</script>

<style scoped lang="scss">
  .generator-page {
    padding: 16px;
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

  /* 统一搜索输入框宽度，并让操作按钮与输入项基线对齐 */
  :deep(.art-search-bar .el-form-item__content > .el-input) {
    width: 100%;
  }

  :deep(.art-search-bar .action-column .action-buttons-wrapper) {
    align-items: center;
    min-height: 32px;
    margin-bottom: 0;
  }
</style>
