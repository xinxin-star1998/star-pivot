<template>
  <div class="ai-statistics-page art-full-height">
    <ElCard shadow="never" class="search-card">
      <ElForm :inline="true" :model="searchForm">
        <ElFormItem :label="t('ai.statistics.beginTime')">
          <ElDatePicker
            v-model="searchForm.beginTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            :placeholder="t('ai.statistics.beginPlaceholder')"
          />
        </ElFormItem>
        <ElFormItem :label="t('ai.statistics.endTime')">
          <ElDatePicker
            v-model="searchForm.endTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            :placeholder="t('ai.statistics.endPlaceholder')"
          />
        </ElFormItem>
        <ElFormItem :label="t('ai.statistics.userId')">
          <ElInputNumber
            v-model="searchForm.userId"
            :min="1"
            class="!w-36"
            controls-position="right"
          />
        </ElFormItem>
        <ElFormItem :label="t('ai.config.model')">
          <ElInput
            v-model="searchForm.model"
            clearable
            :placeholder="t('ai.statistics.modelPlaceholder')"
            class="!w-40"
          />
        </ElFormItem>
        <ElFormItem>
          <ElButton type="primary" @click="loadAll">{{ t('ai.common.search') }}</ElButton>
          <ElButton @click="resetSearch">{{ t('common.reset') }}</ElButton>
        </ElFormItem>
      </ElForm>
    </ElCard>

    <div class="mb-3 grid grid-cols-2 gap-3 md:grid-cols-4">
      <ElCard shadow="never">
        <div class="text-xs text-g-500">{{ t('ai.statistics.totalRequests') }}</div>
        <div class="mt-1 text-2xl font-semibold">{{ summary.totalRequests ?? 0 }}</div>
      </ElCard>
      <ElCard shadow="never">
        <div class="text-xs text-g-500">{{ t('ai.statistics.totalTokens') }}</div>
        <div class="mt-1 text-2xl font-semibold">{{ summary.totalTokens ?? 0 }}</div>
      </ElCard>
      <ElCard shadow="never">
        <div class="text-xs text-g-500">{{ t('ai.statistics.successFail') }}</div>
        <div class="mt-1 text-2xl font-semibold">
          {{ summary.successRequests ?? 0 }} / {{ summary.failedRequests ?? 0 }}
        </div>
      </ElCard>
      <ElCard shadow="never">
        <div class="text-xs text-g-500">{{ t('ai.statistics.avgLatency') }}</div>
        <div class="mt-1 text-2xl font-semibold">{{ Math.round(summary.avgLatencyMs ?? 0) }}</div>
      </ElCard>
    </div>

    <ElCard class="art-table-card" shadow="never">
      <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="loadLogs" />
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
  import { h } from 'vue'
  import { useI18n } from 'vue-i18n'
  import { ElTag } from 'element-plus'
  import { useTable } from '@/hooks/core/useTable'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import {
    type AiUsageLogItem,
    type AiUsageSummary,
    fetchAiUsageLogList,
    fetchAiUsageSummary
  } from '@/api/ai/statistics'
  import { handleMutationError } from '@/utils/http/mutation'

  defineOptions({ name: 'AiStatistics' })

  const { t } = useI18n()

  const summary = ref<AiUsageSummary>({})
  const searchForm = ref<{
    beginTime?: string
    endTime?: string
    userId?: number
    model?: string
  }>({})

  const {
    columns,
    columnChecks,
    data,
    loading,
    pagination,
    searchParams,
    getData,
    handleSizeChange,
    handleCurrentChange
  } = useTable({
    core: {
      apiFn: fetchAiUsageLogList,
      apiParams: { pageNum: 1, pageSize: 20 },
      columnsFactory: () => [
        { type: 'index', width: 60, label: t('ai.common.index') },
        { prop: 'createTime', label: t('ai.statistics.time'), minWidth: 160 },
        { prop: 'userId', label: t('ai.statistics.user'), width: 80 },
        { prop: 'model', label: t('ai.config.model'), minWidth: 120 },
        { prop: 'requestType', label: t('ai.statistics.requestType'), width: 90 },
        { prop: 'totalTokens', label: 'Tokens', width: 90 },
        { prop: 'latencyMs', label: t('ai.statistics.latency'), width: 90 },
        {
          prop: 'success',
          label: t('ai.statistics.result'),
          width: 80,
          formatter: (row: AiUsageLogItem) =>
            h(ElTag, { type: row.success === '0' ? 'success' : 'danger', size: 'small' }, () =>
              row.success === '0' ? t('ai.statistics.success') : t('ai.statistics.failed')
            )
        },
        { prop: 'conversationId', label: t('ai.session.conversationId'), minWidth: 180 }
      ]
    }
  })

  async function loadSummary(): Promise<void> {
    try {
      summary.value =
        (await fetchAiUsageSummary({
          beginTime: searchForm.value.beginTime,
          endTime: searchForm.value.endTime
        })) || {}
    } catch (error) {
      handleMutationError(error, t('ai.statistics.loadFail'))
    }
  }

  function loadLogs(): void {
    Object.assign(searchParams, searchForm.value)
    getData()
  }

  async function loadAll(): Promise<void> {
    await loadSummary()
    loadLogs()
  }

  function resetSearch(): void {
    searchForm.value = {}
    loadAll()
  }

  onMounted(() => {
    loadAll()
  })
</script>

<style scoped lang="scss">
  .ai-statistics-page {
    .search-card {
      margin-bottom: 12px;
    }
  }
</style>
