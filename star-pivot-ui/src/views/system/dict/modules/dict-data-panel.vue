<template>
  <div class="dict-data-panel">
    <ArtSearchBar
      v-model="searchFilters"
      :items="formItems"
      :showExpand="false"
      @reset="handleReset"
      @search="handleSearch"
    />

    <ArtTableHeader
      :showZebra="false"
      :loading="loading"
      v-model:columns="columnChecks"
      @refresh="handleRefresh"
    >
      <template #left>
        <ElButton @click="handleAdd" v-ripple v-auth="'system:data:add'" :disabled="!dictType">
          {{ t('system.dict.addData') }}
        </ElButton>
      </template>
    </ArtTableHeader>

    <ArtTable
      ref="tableRef"
      rowKey="dictCode"
      class="dict-table"
      :loading="loading"
      :columns="columns"
      :data="tableData"
      :stripe="false"
      :pagination="pagination"
      @pagination:size-change="handleSizeChange"
      @pagination:current-change="handleCurrentChange"
    />

    <DictDataDialog
      v-model:visible="dialogVisible"
      :editData="editData"
      :dictType="dictType"
      @submit="handleSubmit"
    />
  </div>
</template>

<script setup lang="ts">
  import { safeError } from '@/utils'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import { useTableColumns } from '@/hooks/core/useTableColumns'
  import DictDataDialog from './dict-data-dialog.vue'
  import {
    type DictDataFormData,
    fetchAddDictData,
    fetchDeleteDictData,
    fetchGetDictDataList,
    fetchUpdateDictData,
    type SysDictData
  } from '@/api/dict/data'
  import { ElMessage, ElMessageBox, ElTag } from 'element-plus'
  import ArtSearchBar from '@/components/core/forms/art-search-bar/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import { useAuth } from '@/hooks/core/useAuth'
  import { handleMutationError } from '@/utils/http/mutation'
  import { useDictStore } from '@/store/modules/dict'
  import { useI18n } from 'vue-i18n'

  defineOptions({ name: 'DictDataPanel' })

  interface Props {
    dictType: string
  }

  const props = withDefaults(defineProps<Props>(), {
    dictType: ''
  })

  const { hasAuth } = useAuth()
  const { t } = useI18n()

  const loading = ref(false)
  const tableRef = ref()

  const dialogVisible = ref(false)
  const editData = ref<DictDataFormData | null>(null)

  const pagination = reactive({
    current: 1,
    size: 10,
    total: 0
  })

  const searchFilters = reactive({
    dictLabel: '',
    status: ''
  })

  const formItems = computed(() => [
    {
      label: t('system.dict.dictLabel'),
      key: 'dictLabel',
      type: 'input',
      props: { clearable: true, placeholder: t('system.dict.labelPlaceholder') }
    },
    {
      label: t('common.status'),
      key: 'status',
      type: 'select',
      props: {
        clearable: true,
        placeholder: t('common.pleaseSelect'),
        options: [
          { label: t('common.normal'), value: '0' },
          { label: t('common.disabled'), value: '1' }
        ]
      }
    }
  ])

  const STATUS_CONFIG = computed(() => ({
    '0': { text: t('common.normal'), type: 'success' as const },
    '1': { text: t('common.disabled'), type: 'danger' as const }
  }))

  const { columnChecks, columns } = useTableColumns(() => [
    { type: 'index', width: 60, label: t('common.orderNum') },
    { prop: 'dictLabel', label: t('system.dict.dictLabel'), minWidth: 120 },
    { prop: 'dictValue', label: t('system.dict.dictValue'), minWidth: 120 },
    {
      prop: 'status',
      label: t('common.status'),
      width: 100,
      formatter: (row: SysDictData) => {
        const status = (row.status || '0') as '0' | '1'
        const statusInfo = STATUS_CONFIG.value[status] || STATUS_CONFIG.value['0']
        return h(ElTag, { type: statusInfo.type }, () => statusInfo.text)
      }
    },
    {
      prop: 'remark',
      label: t('common.remark'),
      minWidth: 150,
      formatter: (row: SysDictData) =>
        row.remark || h('span', { style: 'color: var(--art-gray-500)' }, t('common.empty'))
    },
    {
      prop: 'createTime',
      label: t('common.createTime'),
      width: 180,
      formatter: (row: SysDictData) =>
        row.createTime || h('span', { style: 'color: var(--art-gray-500)' }, t('common.empty'))
    },
    {
      prop: 'operation',
      label: t('common.operation'),
      width: 180,
      align: 'right',
      formatter: (row: SysDictData) => {
        const actions: any[] = []

        if (hasAuth('system:data:edit')) {
          actions.push(h(ArtButtonTable, { type: 'edit', onClick: () => handleEdit(row) }))
        }
        if (hasAuth('system:data:delete')) {
          actions.push(h(ArtButtonTable, { type: 'delete', onClick: () => handleDelete(row) }))
        }

        if (actions.length === 0) return h('span', { style: 'color: var(--art-gray-500)' }, '')
        return h('div', { style: 'text-align: right' }, actions)
      }
    }
  ])

  const tableData = ref<SysDictData[]>([])

  const getDictDataList = async (): Promise<void> => {
    if (!props.dictType) {
      tableData.value = []
      pagination.total = 0
      return
    }

    loading.value = true
    try {
      const params: any = {
        pageNum: pagination.current,
        pageSize: pagination.size,
        dictType: props.dictType
      }
      if (searchFilters.dictLabel) params.dictLabel = searchFilters.dictLabel
      if (searchFilters.status) params.status = searchFilters.status

      const result = await fetchGetDictDataList(params)
      tableData.value = result?.rows || []
      pagination.total = result?.total || 0
    } catch (error) {
      safeError('获取字典数据列表失败:', error)
      handleMutationError(error, t('system.dict.loadFail'))
    } finally {
      loading.value = false
    }
  }

  const handleReset = (): void => {
    Object.assign(searchFilters, { dictLabel: '', status: '' })
    pagination.current = 1
    getDictDataList()
  }

  const handleSearch = (): void => {
    pagination.current = 1
    getDictDataList()
  }

  const handleSizeChange = (size: number): void => {
    pagination.size = size
    pagination.current = 1
    getDictDataList()
  }

  const handleCurrentChange = (current: number): void => {
    pagination.current = current
    getDictDataList()
  }

  const handleRefresh = (): void => {
    getDictDataList()
  }

  const handleAdd = (): void => {
    if (!props.dictType) {
      ElMessage.warning(t('common.pleaseSelect'))
      return
    }
    editData.value = null
    dialogVisible.value = true
  }

  const handleEdit = (row: SysDictData): void => {
    editData.value = {
      dictCode: row.dictCode,
      dictSort: row.dictSort || 0,
      dictLabel: row.dictLabel,
      dictValue: row.dictValue,
      dictType: row.dictType,
      cssClass: row.cssClass || '',
      listClass: row.listClass || '',
      isDefault: row.isDefault || 'N',
      status: row.status || '0',
      remark: row.remark || ''
    }
    dialogVisible.value = true
  }

  const handleDelete = async (row: SysDictData): Promise<void> => {
    if (!row.dictCode) {
      ElMessage.warning(t('common.deleteFail'))
      return
    }

    try {
      await ElMessageBox.confirm(t('system.dict.deleteConfirm'), t('common.tips'), {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      })

      await fetchDeleteDictData([row.dictCode])
      ElMessage.success(t('common.deleteSuccess'))
      if (tableData.value.length === 1 && pagination.current > 1) pagination.current--
      await getDictDataList()
    } catch (error) {
      if (error !== 'cancel' && error !== 'close') {
        safeError('删除字典数据失败:', error)
        handleMutationError(error, t('common.deleteFail'))
      }
    }
  }

  const handleSubmit = async (formData: DictDataFormData): Promise<void> => {
    try {
      const isEdit = !!formData.dictCode
      if (isEdit) {
        await fetchUpdateDictData(formData)
        ElMessage.success(t('common.updateSuccess'))
      } else {
        await fetchAddDictData(formData)
        ElMessage.success(t('common.addSuccess'))
      }
      dialogVisible.value = false
      useDictStore().clearDictCache(formData.dictType)
      await getDictDataList()
    } catch (error) {
      safeError('保存字典数据失败:', error)
      handleMutationError(error, formData.dictCode ? t('common.updateFail') : t('common.addFail'))
    }
  }

  watch(
    () => props.dictType,
    () => {
      Object.assign(searchFilters, { dictLabel: '', status: '' })
      pagination.current = 1
      getDictDataList()
    },
    { immediate: true }
  )
</script>

<style scoped lang="scss">
  .dict-data-panel {
    display: flex;
    flex-direction: column;
    gap: 10px;
    min-height: 0;
  }

  .dict-table {
    flex: 1;
    min-height: 0;
  }
</style>
