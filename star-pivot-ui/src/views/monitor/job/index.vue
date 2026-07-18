<template>
  <div class="job-page art-full-height">
    <ElCard class="art-table-card" shadow="never">
      <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
        <template #left>
          <ElSpace wrap>
            <ElButton v-auth="'monitor:job:add'" @click="showDialog('add')">{{
              t('monitor.job.addJob')
            }}</ElButton>
            <ElButton
              v-auth="'monitor:job:delete'"
              type="danger"
              :disabled="selectedRows.length === 0"
              @click="handleBatchDelete"
            >
              {{ t('common.batchDelete') }}
            </ElButton>
            <ElButton v-auth="'monitor:job:query'" @click="showLogDialog">{{
              t('monitor.job.viewLog')
            }}</ElButton>
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

      <JobDialog
        v-model:visible="dialogVisible"
        :type="dialogType"
        :job-data="currentJob"
        @submit="handleDialogSubmit"
      />
      <JobLogDialog v-model="logDialogVisible" />
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { useTable } from '@/hooks/core/useTable'
  import {
    fetchChangeJobStatus,
    fetchDeleteJob,
    fetchJobList,
    fetchRunJobOnce,
    type SysJob
  } from '@/api/monitor/job'
  import { useI18n } from 'vue-i18n'
  import JobDialog from './modules/job-dialog.vue'
  import JobLogDialog from './modules/job-log-dialog.vue'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import { useAuth } from '@/hooks/core/useAuth'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import type { DialogType } from '@/types'

  defineOptions({ name: 'MonitorJob' })

  const { hasAuth } = useAuth()
  const { t } = useI18n()

  const dialogType = ref<DialogType>('add')
  const dialogVisible = ref(false)
  const logDialogVisible = ref(false)
  const currentJob = ref<Partial<SysJob>>({})
  const selectedRows = ref<SysJob[]>([])

  const searchForm = ref({
    jobName: undefined as string | undefined,
    jobGroup: undefined as string | undefined,
    status: undefined as string | undefined
  })

  const {
    columns,
    columnChecks,
    data,
    loading,
    pagination,
    handleSizeChange,
    handleCurrentChange,
    refreshData
  } = useTable({
    core: {
      apiFn: fetchJobList,
      apiParams: {
        pageNum: 1,
        pageSize: 20,
        ...searchForm.value
      },
      immediate: true,
      columnsFactory: () => [
        { type: 'selection' },
        { type: 'index', width: 60, label: t('table.column.index') },
        {
          prop: 'jobName',
          label: t('monitor.job.jobName'),
          minWidth: 120,
          showOverflowTooltip: true
        },
        { prop: 'jobGroup', label: t('monitor.job.jobGroup'), width: 90 },
        {
          prop: 'invokeTarget',
          label: t('monitor.job.invokeTarget'),
          minWidth: 200,
          showOverflowTooltip: true
        },
        { prop: 'cronExpression', label: t('monitor.job.cronExpression'), width: 130 },
        {
          prop: 'status',
          label: t('monitor.job.status'),
          width: 80,
          formatter: (row: SysJob) =>
            row.status === '0' ? t('common.normal') : t('monitor.job.pause')
        },
        { prop: 'createTime', label: t('common.createTime'), width: 160 },
        {
          prop: 'operation',
          label: t('common.operation'),
          minWidth: 200,
          fixed: 'right',
          formatter: (row: SysJob) => {
            try {
              const actions: any[] = []
              if (hasAuth('monitor:job:edit')) {
                actions.push(
                  h(ArtButtonTable, {
                    type: 'edit',
                    label: t('common.edit'),
                    onClick: () => showDialog('edit', row)
                  })
                )
                actions.push(
                  h(ArtButtonTable, {
                    type: row.status === '0' ? 'pause' : 'resume',
                    label: row.status === '0' ? t('monitor.job.pause') : t('monitor.job.resume'),
                    onClick: () => toggleStatus(row)
                  })
                )
                actions.push(
                  h(ArtButtonTable, {
                    type: 'execute',
                    label: t('monitor.job.runOnce'),
                    onClick: () => runOnce(row)
                  })
                )
              }
              if (hasAuth('monitor:job:delete')) {
                actions.push(
                  h(ArtButtonTable, {
                    type: 'delete',
                    label: t('common.delete'),
                    onClick: () => deleteOne(row)
                  })
                )
              }
              return actions.length ? h('div', actions) : h('span', { style: 'color:#999' }, '-')
            } catch (e) {
              console.error('[MonitorJob] operation formatter error', e)
              return h('span', { style: 'color:#999' }, '-')
            }
          }
        }
      ]
    }
  })

  const showDialog = (type: DialogType, row?: SysJob) => {
    dialogType.value = type
    currentJob.value = row ? { ...row } : {}
    nextTick(() => {
      dialogVisible.value = true
    })
  }

  const showLogDialog = () => {
    logDialogVisible.value = true
  }

  const handleDialogSubmit = () => {
    dialogVisible.value = false
    currentJob.value = {}
    refreshData()
  }

  const toggleStatus = async (row: SysJob) => {
    const jobId = row.jobId
    if (jobId == null) return
    const newStatus = row.status === '0' ? '1' : '0'
    await fetchChangeJobStatus(jobId, newStatus)
    ElMessage.success(t('common.updateSuccess'))
    refreshData()
  }

  const runOnce = async (row: SysJob) => {
    if (row.jobId == null) return
    await ElMessageBox.confirm(t('monitor.job.runConfirm'), t('common.tips'), {
      type: 'warning',
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel')
    })
    await fetchRunJobOnce(row.jobId)
    ElMessage.success(t('common.updateSuccess'))
    refreshData()
  }

  const deleteOne = async (row: SysJob) => {
    await ElMessageBox.confirm(t('monitor.job.deleteConfirm'), t('common.tips'), {
      type: 'warning',
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel')
    })
    if (row.jobId == null) return
    await fetchDeleteJob([row.jobId])
    ElMessage.success(t('common.deleteSuccess'))
    refreshData()
  }

  const handleBatchDelete = async () => {
    if (selectedRows.value.length === 0) {
      ElMessage.warning(t('common.pleaseSelect'))
      return
    }
    const ids = selectedRows.value.map((r) => r.jobId).filter((id): id is number => id != null)
    await ElMessageBox.confirm(t('monitor.job.deleteConfirm'), t('common.tips'), {
      type: 'warning',
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel')
    })
    await fetchDeleteJob(ids)
    selectedRows.value = []
    ElMessage.success(t('common.deleteSuccess'))
    refreshData()
  }

  const handleSelectionChange = (selection: SysJob[]) => {
    selectedRows.value = selection
  }
</script>

<style scoped lang="scss">
  .job-page {
    padding: 20px;
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
</style>
