<template>
  <div class="file-shares-page art-full-height">
    <ElCard class="art-table-card" shadow="never">
      <ArtTableHeader :loading="loading" @refresh="loadData">
        <template #left>
          <div class="shares-toolbar">
            <span class="page-title">{{ t('file.myShares') }}</span>
            <ElInput
              v-model="keyword"
              clearable
              :placeholder="t('file.searchFileNamePlaceholder')"
              class="shares-search"
            />
          </div>
        </template>
      </ArtTableHeader>

      <ElTable v-loading="loading" :data="filteredList" stripe>
        <ElTableColumn
          prop="fileName"
          :label="t('file.fileName')"
          min-width="180"
          show-overflow-tooltip
        />
        <ElTableColumn :label="t('file.shareLink')" min-width="260">
          <template #default="{ row }">
            <div class="link-cell">
              <span class="link-text">{{ displayUrl(row) }}</span>
              <ElButton link type="primary" @click="copyLink(row)">{{ t('file.copy') }}</ElButton>
            </div>
          </template>
        </ElTableColumn>
        <ElTableColumn :label="t('file.sharePassword')" width="80" align="center">
          <template #default="{ row }">
            <ElTag :type="row.hasPassword ? 'warning' : 'info'" size="small">
              {{ row.hasPassword ? t('common.yes') : t('common.no') }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn :label="t('file.accessViews')" width="100" align="center">
          <template #default="{ row }">
            {{ row.viewCount ?? 0 }}{{ row.maxViews != null ? ` / ${row.maxViews}` : '' }}
          </template>
        </ElTableColumn>
        <ElTableColumn prop="expireTime" :label="t('file.shareExpire')" width="168">
          <template #default="{ row }">{{ row.expireTime || t('file.neverExpireLong') }}</template>
        </ElTableColumn>
        <ElTableColumn prop="createTime" :label="t('common.createTime')" width="168" />
        <ElTableColumn :label="t('common.operation')" width="100" fixed="right">
          <template #default="{ row }">
            <ElButton link type="danger" @click="handleRevoke(row)">{{
              t('file.cancelShare')
            }}</ElButton>
          </template>
        </ElTableColumn>
      </ElTable>
    </ElCard>
  </div>
</template>

<script lang="ts" setup>
  import type { SysFileShare } from '@/api/file/types'
  import { fetchMyShares, revokeFileShare } from '@/api/file/share'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import { handleMutationError } from '@/utils/http/mutation'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { computed, onMounted, ref } from 'vue'
  import { useI18n } from 'vue-i18n'

  defineOptions({ name: 'FileShares' })

  const { t } = useI18n()

  const loading = ref(false)
  const list = ref<SysFileShare[]>([])
  const keyword = ref('')

  const filteredList = computed(() => {
    const kw = keyword.value.trim().toLowerCase()
    if (!kw) return list.value
    return list.value.filter((item) => (item.fileName || '').toLowerCase().includes(kw))
  })

  function displayUrl(row: SysFileShare) {
    if (row.shareUrl?.startsWith('http')) return row.shareUrl
    return `${window.location.origin}/s/${row.shareCode}`
  }

  async function loadData() {
    loading.value = true
    try {
      list.value = (await fetchMyShares()) || []
    } catch (e) {
      handleMutationError(e, t('file.shareListLoadFail'))
    } finally {
      loading.value = false
    }
  }

  async function copyLink(row: SysFileShare) {
    try {
      await navigator.clipboard.writeText(displayUrl(row))
      ElMessage.success(t('file.copiedLink'))
    } catch {
      ElMessage.error(t('file.copyFail'))
    }
  }

  async function handleRevoke(row: SysFileShare) {
    if (!row.shareId) return
    try {
      await ElMessageBox.confirm(t('file.shareRevokeConfirm'), t('common.tips'), {
        type: 'warning'
      })
      await revokeFileShare(row.shareId)
      ElMessage.success(t('file.shareRevokedSuccess'))
      await loadData()
    } catch (e) {
      if (e !== 'cancel') handleMutationError(e, t('file.shareRevokeFail'))
    }
  }

  onMounted(loadData)
</script>

<style lang="scss" scoped>
  .file-shares-page {
    display: flex;
    flex-direction: column;
  }

  .shares-toolbar {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    align-items: center;
    width: 100%;
  }

  .page-title {
    font-size: 15px;
    font-weight: 600;
  }

  .shares-search {
    width: 220px;
  }

  .link-cell {
    display: flex;
    gap: 8px;
    align-items: center;
  }

  .link-text {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    color: var(--el-text-color-regular);
  }

  @media (width <= 768px) {
    .shares-search {
      width: 100%;
    }

    .link-cell {
      flex-wrap: wrap;
    }
  }
</style>
