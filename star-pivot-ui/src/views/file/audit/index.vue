<template>
  <div class="file-audit-page art-full-height">
    <ElCard shadow="never" class="search-card">
      <ElForm class="audit-search-form" label-position="top" :model="searchForm">
        <ElFormItem :label="t('file.action')">
          <ElSelect
            v-model="searchForm.action"
            clearable
            :placeholder="t('file.all')"
            class="audit-field"
          >
            <ElOption
              v-for="item in actionOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </ElSelect>
        </ElFormItem>
        <ElFormItem :label="t('file.fileName')">
          <ElInput
            v-model="searchForm.fileName"
            clearable
            :placeholder="t('file.fileName')"
            class="audit-field"
          />
        </ElFormItem>
        <ElFormItem :label="t('file.operBy')">
          <ElInput
            v-model="searchForm.operBy"
            clearable
            :placeholder="t('file.operBy')"
            class="audit-field"
          />
        </ElFormItem>
        <ElFormItem :label="t('file.time')" class="audit-time-item">
          <ElDatePicker
            v-model="timeRange"
            type="datetimerange"
            value-format="YYYY-MM-DD HH:mm:ss"
            :start-placeholder="t('file.startTime')"
            :end-placeholder="t('file.endTime')"
            class="audit-date"
          />
        </ElFormItem>
        <ElFormItem class="audit-actions" label-width="0">
          <ElButton type="primary" @click="handleQuery">{{ t('file.query') }}</ElButton>
          <ElButton @click="handleReset">{{ t('common.reset') }}</ElButton>
        </ElFormItem>
      </ElForm>
    </ElCard>

    <ElCard class="art-table-card" shadow="never">
      <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData" />
      <ArtTable
        :columns="columns"
        :data="data"
        :loading="loading"
        :pagination="pagination"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      />
    </ElCard>
  </div>
</template>

<script lang="ts" setup>
  import { fetchFileAuditList } from '@/api/file/file'
  import type { SysFileAudit } from '@/api/file/types'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import { useTable } from '@/hooks/core/useTable'
  import { ElTag } from 'element-plus'
  import { computed, h, onMounted, ref } from 'vue'
  import { useI18n } from 'vue-i18n'

  defineOptions({ name: 'FileAudit' })

  const { t } = useI18n()

  const actionOptions = computed(() =>
    [
      'UPLOAD',
      'DOWNLOAD',
      'DELETE',
      'RESTORE',
      'PURGE',
      'MOVE',
      'RENAME',
      'SHARE',
      'SHARE_REVOKE',
      'VERSION_UPLOAD',
      'VERSION_RESTORE',
      'ZIP_DOWNLOAD'
    ].map((value) => ({
      value,
      label: t(`file.auditAction.${value}`)
    }))
  )

  const searchForm = ref({
    action: undefined as string | undefined,
    fileName: undefined as string | undefined,
    operBy: undefined as string | undefined
  })
  const timeRange = ref<string[] | undefined>()

  const {
    columns,
    columnChecks,
    data,
    loading,
    pagination,
    getData,
    refreshData,
    handleSizeChange,
    handleCurrentChange,
    searchParams
  } = useTable({
    core: {
      apiFn: (params) => fetchFileAuditList(params as never),
      apiParams: { pageNum: 1, pageSize: 20 },
      immediate: false,
      columnsFactory: () => [
        { type: 'index', width: 60, label: t('table.column.index') },
        {
          prop: 'actionLabel',
          label: t('file.action'),
          width: 120,
          formatter: (row: SysFileAudit) =>
            h(ElTag, { size: 'small' }, () => row.actionLabel || row.action)
        },
        { prop: 'fileName', label: t('file.fileName'), minWidth: 180 },
        { prop: 'detail', label: t('file.detail'), minWidth: 200 },
        { prop: 'operBy', label: t('file.operBy'), width: 110 },
        { prop: 'operIp', label: 'IP', width: 130 },
        { prop: 'operTime', label: t('file.time'), width: 168 }
      ]
    }
  })

  async function handleQuery() {
    Object.assign(searchParams, {
      pageNum: 1,
      action: searchForm.value.action,
      fileName: searchForm.value.fileName,
      operBy: searchForm.value.operBy,
      beginTime: timeRange.value?.[0],
      endTime: timeRange.value?.[1]
    })
    await getData()
  }

  async function handleReset() {
    searchForm.value = { action: undefined, fileName: undefined, operBy: undefined }
    timeRange.value = undefined
    await handleQuery()
  }

  onMounted(() => handleQuery())
</script>

<style lang="scss" scoped>
  .file-audit-page {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .audit-search-form {
    display: flex;
    flex-wrap: wrap;
    gap: 0 16px;

    .audit-field {
      width: 160px;
    }

    .audit-date {
      width: 360px;
      max-width: 100%;
    }

    .audit-actions {
      display: flex;
      align-items: flex-end;

      :deep(.el-form-item__content) {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
      }
    }
  }

  @media (width <= 768px) {
    .audit-search-form {
      display: block;

      .el-form-item {
        width: 100%;
        margin-right: 0;
      }

      .audit-field,
      .audit-date {
        width: 100%;
      }

      .audit-actions :deep(.el-button) {
        flex: 1;
      }
    }
  }
</style>
