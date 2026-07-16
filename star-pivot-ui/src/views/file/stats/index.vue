<template>
  <div class="file-stats-page art-full-height">
    <ElCard shadow="never" class="toolbar-card">
      <ElRadioGroup v-model="groupBy" @change="loadData">
        <ElRadioButton value="user">按用户</ElRadioButton>
        <ElRadioButton value="dept">按部门</ElRadioButton>
      </ElRadioGroup>
      <ElButton class="ml-3" :loading="loading" @click="loadData">刷新</ElButton>
    </ElCard>

    <div class="summary-grid">
      <ElCard shadow="never">
        <div class="summary-label">文件数</div>
        <div class="summary-value">{{ summary.fileCount ?? 0 }}</div>
      </ElCard>
      <ElCard shadow="never">
        <div class="summary-label">逻辑用量</div>
        <div class="summary-value">{{ formatFileSize(summary.totalBytes) }}</div>
      </ElCard>
      <ElCard shadow="never">
        <div class="summary-label">去重对象数</div>
        <div class="summary-value">{{ summary.uniqueObjects ?? 0 }}</div>
      </ElCard>
    </div>

    <ElCard class="art-table-card" shadow="never">
      <ElTable v-loading="loading" :data="summary.items || []" stripe>
        <ElTableColumn
          :label="groupBy === 'dept' ? '部门' : '用户'"
          prop="groupName"
          min-width="160"
        />
        <ElTableColumn prop="groupId" label="ID" width="100" />
        <ElTableColumn prop="fileCount" label="文件数" width="120" sortable />
        <ElTableColumn label="逻辑用量" width="140" sortable :sort-method="sortByBytes">
          <template #default="{ row }">{{ formatFileSize(row.totalBytes) }}</template>
        </ElTableColumn>
        <ElTableColumn prop="uniqueObjects" label="去重对象" width="120" sortable />
        <ElTableColumn label="占比" min-width="180">
          <template #default="{ row }">
            <ElProgress
              :percentage="percentOf(row.totalBytes)"
              :stroke-width="14"
              :text-inside="true"
            />
          </template>
        </ElTableColumn>
      </ElTable>
    </ElCard>
  </div>
</template>

<script lang="ts" setup>
  import type { SysFileUsageStatItem, SysFileUsageSummary } from '@/api/file/types'
  import { fetchFileUsageStats } from '@/api/file/file'
  import { formatFileSize } from '@/utils/file/file-center'
  import { handleMutationError } from '@/utils/http/mutation'
  import { onMounted, ref } from 'vue'

  defineOptions({ name: 'FileStats' })

  const loading = ref(false)
  const groupBy = ref<'user' | 'dept'>('user')
  const summary = ref<SysFileUsageSummary>({
    fileCount: 0,
    totalBytes: 0,
    uniqueObjects: 0,
    items: []
  })

  function percentOf(bytes?: number) {
    const total = summary.value.totalBytes || 0
    if (!total || !bytes) return 0
    return Math.min(100, Math.round((bytes / total) * 1000) / 10)
  }

  function sortByBytes(a: SysFileUsageStatItem, b: SysFileUsageStatItem) {
    return (a.totalBytes || 0) - (b.totalBytes || 0)
  }

  async function loadData() {
    loading.value = true
    try {
      summary.value = (await fetchFileUsageStats(groupBy.value)) || {
        fileCount: 0,
        totalBytes: 0,
        uniqueObjects: 0,
        items: []
      }
    } catch (e) {
      handleMutationError(e, '加载统计失败')
    } finally {
      loading.value = false
    }
  }

  onMounted(loadData)
</script>

<style lang="scss" scoped>
  .file-stats-page {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .toolbar-card {
    :deep(.el-card__body) {
      display: flex;
      align-items: center;
    }
  }

  .summary-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
  }

  .summary-label {
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }

  .summary-value {
    margin-top: 6px;
    font-size: 22px;
    font-weight: 600;
  }

  @media (max-width: 768px) {
    .summary-grid {
      grid-template-columns: 1fr;
    }
  }
</style>
