<template>
  <div class="file-shares-page art-full-height">
    <ElCard class="art-table-card" shadow="never">
      <ArtTableHeader :loading="loading" @refresh="loadData">
        <template #left>
          <span class="page-title">我的分享</span>
          <ElInput
            v-model="keyword"
            clearable
            placeholder="搜索文件名"
            style="width: 220px; margin-left: 12px"
          />
        </template>
      </ArtTableHeader>

      <ElTable v-loading="loading" :data="filteredList" stripe>
        <ElTableColumn prop="fileName" label="文件" min-width="180" show-overflow-tooltip />
        <ElTableColumn label="分享链接" min-width="260">
          <template #default="{ row }">
            <div class="link-cell">
              <span class="link-text">{{ displayUrl(row) }}</span>
              <ElButton link type="primary" @click="copyLink(row)">复制</ElButton>
            </div>
          </template>
        </ElTableColumn>
        <ElTableColumn label="密码" width="80" align="center">
          <template #default="{ row }">
            <ElTag :type="row.hasPassword ? 'warning' : 'info'" size="small">
              {{ row.hasPassword ? '有' : '无' }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="访问" width="100" align="center">
          <template #default="{ row }">
            {{ row.viewCount ?? 0 }}{{ row.maxViews != null ? ` / ${row.maxViews}` : '' }}
          </template>
        </ElTableColumn>
        <ElTableColumn prop="expireTime" label="过期时间" width="168">
          <template #default="{ row }">{{ row.expireTime || '永不过期' }}</template>
        </ElTableColumn>
        <ElTableColumn prop="createTime" label="创建时间" width="168" />
        <ElTableColumn label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <ElButton link type="danger" @click="handleRevoke(row)">取消分享</ElButton>
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

  defineOptions({ name: 'FileShares' })

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
      handleMutationError(e, '加载分享列表失败')
    } finally {
      loading.value = false
    }
  }

  async function copyLink(row: SysFileShare) {
    try {
      await navigator.clipboard.writeText(displayUrl(row))
      ElMessage.success('已复制链接')
    } catch {
      ElMessage.error('复制失败')
    }
  }

  async function handleRevoke(row: SysFileShare) {
    if (!row.shareId) return
    try {
      await ElMessageBox.confirm('确认取消该分享外链？', '提示', { type: 'warning' })
      await revokeFileShare(row.shareId)
      ElMessage.success('已取消分享')
      await loadData()
    } catch (e) {
      if (e !== 'cancel') handleMutationError(e, '取消失败')
    }
  }

  onMounted(loadData)
</script>

<style lang="scss" scoped>
  .file-shares-page {
    display: flex;
    flex-direction: column;
  }

  .page-title {
    font-size: 15px;
    font-weight: 600;
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
</style>
