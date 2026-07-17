<template>
  <div class="ai-config-page art-full-height">
    <ElCard shadow="never" class="search-card">
      <ElForm :inline="true" :model="searchForm">
        <ElFormItem :label="t('ai.config.name')">
          <ElInput
            v-model="searchForm.configName"
            clearable
            :placeholder="t('ai.config.namePlaceholder')"
          />
        </ElFormItem>
        <ElFormItem :label="t('ai.config.botName')">
          <ElInput
            v-model="searchForm.botName"
            clearable
            :placeholder="t('ai.config.botNamePlaceholder')"
          />
        </ElFormItem>
        <ElFormItem :label="t('common.status')">
          <ElSelect
            v-model="searchForm.status"
            clearable
            :placeholder="t('ai.common.all')"
            class="!w-28"
          >
            <ElOption :label="t('common.normal')" value="0" />
            <ElOption :label="t('common.disabled')" value="1" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem>
          <ElButton type="primary" @click="handleSearch">{{ t('ai.common.search') }}</ElButton>
          <ElButton @click="resetSearch">{{ t('common.reset') }}</ElButton>
        </ElFormItem>
      </ElForm>
    </ElCard>

    <ElCard class="art-table-card" shadow="never">
      <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
        <template #left>
          <ElButton v-auth="'ai:config:edit'" type="primary" @click="openEdit()">
            {{ t('ai.config.addConfig') }}
          </ElButton>
        </template>
      </ArtTableHeader>
      <ArtTable
        :columns="columns"
        :data="data"
        :loading="loading"
        :pagination="pagination"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      />
    </ElCard>

    <ConfigEditDialog
      v-model:visible="editVisible"
      :config-data="currentConfig"
      :saving="saving"
      @submit="handleSave"
    />
  </div>
</template>

<script lang="ts" setup>
  import { h } from 'vue'
  import { useI18n } from 'vue-i18n'
  import { ElMessage, ElMessageBox, ElTag } from 'element-plus'
  import { useTable } from '@/hooks/core/useTable'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import {
    type AiConfigItem,
    type AiConfigSavePayload,
    fetchAiConfigDetail,
    fetchAiConfigList,
    fetchAiConfigRemove,
    fetchAiConfigSave,
    fetchAiConfigSetDefault
  } from '@/api/ai/config'
  import ConfigEditDialog from './modules/config-edit-dialog.vue'
  import { handleMutationError } from '@/utils/http/mutation'

  defineOptions({ name: 'AiConfig' })

  const { t } = useI18n()

  const searchForm = ref({ configName: '', botName: '', status: '' })
  const editVisible = ref(false)
  const saving = ref(false)
  const currentConfig = ref<AiConfigItem | null>(null)

  const {
    columns,
    columnChecks,
    data,
    loading,
    pagination,
    searchParams,
    getData,
    handleSizeChange,
    handleCurrentChange,
    refreshData
  } = useTable({
    core: {
      apiFn: fetchAiConfigList,
      apiParams: { pageNum: 1, pageSize: 20, ...searchForm.value },
      columnsFactory: () => [
        { type: 'index', width: 60, label: t('ai.common.index') },
        { prop: 'configName', label: t('ai.config.name'), minWidth: 110 },
        { prop: 'botName', label: t('ai.config.botName'), minWidth: 110 },
        { prop: 'defaultModel', label: t('ai.config.defaultModel'), minWidth: 130 },
        {
          prop: 'isDefault',
          label: t('ai.config.isDefault'),
          width: 80,
          formatter: (row: AiConfigItem) =>
            h(ElTag, { type: row.isDefault === '0' ? 'success' : 'info', size: 'small' }, () =>
              row.isDefault === '0' ? t('common.yes') : t('common.no')
            )
        },
        {
          prop: 'status',
          label: t('common.status'),
          width: 80,
          formatter: (row: AiConfigItem) =>
            h(ElTag, { type: row.status === '0' ? 'success' : 'info', size: 'small' }, () =>
              row.status === '0' ? t('common.normal') : t('common.disabled')
            )
        },
        { prop: 'updateBy', label: t('ai.common.updateBy'), minWidth: 90 },
        { prop: 'updateTime', label: t('ai.common.updateTime'), minWidth: 160 },
        {
          prop: 'actions',
          label: t('common.operation'),
          width: 220,
          fixed: 'right',
          formatter: (row: AiConfigItem) =>
            h('div', { class: 'flex flex-wrap gap-2' }, [
              h(
                'a',
                { class: 'text-primary cursor-pointer', onClick: () => openEdit(row) },
                t('ai.common.edit')
              ),
              row.isDefault !== '0'
                ? h(
                    'a',
                    {
                      class: 'text-primary cursor-pointer',
                      onClick: () => handleSetDefault(row)
                    },
                    t('ai.config.setDefault')
                  )
                : null,
              row.isDefault !== '0'
                ? h(
                    'a',
                    { class: 'text-danger cursor-pointer', onClick: () => handleDelete(row) },
                    t('common.delete')
                  )
                : null
            ])
        }
      ]
    }
  })

  function handleSearch(): void {
    Object.assign(searchParams, searchForm.value)
    getData()
  }

  function resetSearch(): void {
    searchForm.value = { configName: '', botName: '', status: '' }
    handleSearch()
  }

  async function openEdit(row?: AiConfigItem): Promise<void> {
    if (row?.configId) {
      try {
        currentConfig.value = await fetchAiConfigDetail(row.configId)
      } catch (error) {
        handleMutationError(error, t('ai.config.loadFail'))
        return
      }
    } else {
      currentConfig.value = null
    }
    editVisible.value = true
  }

  async function handleSave(payload: AiConfigSavePayload): Promise<void> {
    saving.value = true
    try {
      await fetchAiConfigSave(payload)
      ElMessage.success(t('ai.common.saveSuccess'))
      editVisible.value = false
      await refreshData()
    } catch (error) {
      handleMutationError(error, t('ai.common.saveFail'))
    } finally {
      saving.value = false
    }
  }

  async function handleSetDefault(row: AiConfigItem): Promise<void> {
    if (!row.configId) return
    try {
      await fetchAiConfigSetDefault(row.configId)
      ElMessage.success(t('ai.config.setDefaultSuccess'))
      await refreshData()
    } catch (error) {
      handleMutationError(error, t('ai.config.setDefaultFail'))
    }
  }

  async function handleDelete(row: AiConfigItem): Promise<void> {
    if (!row.configId) return
    try {
      await ElMessageBox.confirm(
        t('ai.config.deleteConfirm', { name: row.configName }),
        t('common.tips'),
        {
          type: 'warning',
          confirmButtonText: t('common.delete'),
          cancelButtonText: t('common.cancel')
        }
      )
    } catch {
      return
    }
    try {
      await fetchAiConfigRemove(row.configId)
      ElMessage.success(t('ai.common.deleteSuccess'))
      await refreshData()
    } catch (error) {
      handleMutationError(error, t('ai.common.deleteFail'))
    }
  }
</script>

<style scoped lang="scss">
  .ai-config-page {
    .search-card {
      margin-bottom: 12px;
    }
  }
</style>
