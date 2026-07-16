<template>
  <div class="file-audit-page art-full-height">
    <ElCard shadow="never" class="search-card">
      <ElForm :inline="true" :model="searchForm">
        <ElFormItem label="动作">
          <ElSelect v-model="searchForm.action" clearable placeholder="全部" style="width: 140px">
            <ElOption
              v-for="item in actionOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="文件名">
          <ElInput v-model="searchForm.fileName" clearable placeholder="文件名" />
        </ElFormItem>
        <ElFormItem label="操作人">
          <ElInput v-model="searchForm.operBy" clearable placeholder="操作人" />
        </ElFormItem>
        <ElFormItem label="时间">
          <ElDatePicker
            v-model="timeRange"
            type="datetimerange"
            value-format="YYYY-MM-DD HH:mm:ss"
            start-placeholder="开始"
            end-placeholder="结束"
          />
        </ElFormItem>
        <ElFormItem>
          <ElButton type="primary" @click="handleQuery">查询</ElButton>
          <ElButton @click="handleReset">重置</ElButton>
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
  import { h, onMounted, ref } from 'vue'

  defineOptions({ name: 'FileAudit' })

  const actionOptions = [
    { label: '上传', value: 'UPLOAD' },
    { label: '下载', value: 'DOWNLOAD' },
    { label: '移入回收站', value: 'DELETE' },
    { label: '恢复', value: 'RESTORE' },
    { label: '彻底删除', value: 'PURGE' },
    { label: '迁移', value: 'MOVE' },
    { label: '重命名', value: 'RENAME' },
    { label: '分享', value: 'SHARE' },
    { label: '取消分享', value: 'SHARE_REVOKE' },
    { label: '新版本', value: 'VERSION_UPLOAD' },
    { label: '恢复版本', value: 'VERSION_RESTORE' },
    { label: '打包下载', value: 'ZIP_DOWNLOAD' }
  ]

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
        { type: 'index', width: 60, label: '序号' },
        {
          prop: 'actionLabel',
          label: '动作',
          width: 120,
          formatter: (row: SysFileAudit) =>
            h(ElTag, { size: 'small' }, () => row.actionLabel || row.action)
        },
        { prop: 'fileName', label: '文件', minWidth: 180 },
        { prop: 'detail', label: '详情', minWidth: 200 },
        { prop: 'operBy', label: '操作人', width: 110 },
        { prop: 'operIp', label: 'IP', width: 130 },
        { prop: 'operTime', label: '时间', width: 168 }
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
