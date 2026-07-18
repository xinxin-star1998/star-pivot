<!-- 缓存管理页面 -->
<template>
  <div class="redis-page art-full-height">
    <ElCard class="art-table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>{{ t('monitor.redis.info') }}</span>
          <ElButton type="primary" :icon="Refresh" @click="handleRefresh" :loading="loading">
            {{ t('monitor.redis.refresh') }}
          </ElButton>
        </div>
      </template>

      <div class="cache-container">
        <div class="cache-list-panel">
          <ElCard shadow="hover" class="panel-card">
            <template #header>
              <div class="panel-header">
                <span>{{ t('monitor.redis.info') }}</span>
                <ElButton
                  :icon="Refresh"
                  circle
                  size="small"
                  @click="refreshCacheList"
                  :loading="loadingCacheList"
                />
              </div>
            </template>
            <ElTable
              ref="cacheTableRef"
              :data="cacheList"
              row-key="cacheName"
              highlight-current-row
              @current-change="handleCacheSelect"
              v-loading="loadingCacheList"
            >
              <ElTableColumn type="index" :label="t('table.column.index')" width="60" />
              <ElTableColumn prop="cacheName" :label="t('monitor.redis.info')" />
              <ElTableColumn prop="remark" :label="t('common.remark')" />
              <ElTableColumn :label="t('common.operation')" width="80">
                <template #default="{ row }">
                  <ElButton
                    type="danger"
                    :icon="Delete"
                    circle
                    size="small"
                    @click="handleDeleteCache(row)"
                    :loading="deletingCache === row.cacheName"
                  />
                </template>
              </ElTableColumn>
            </ElTable>
          </ElCard>
        </div>

        <div class="key-list-panel">
          <ElCard shadow="hover" class="panel-card">
            <template #header>
              <div class="panel-header">
                <span>{{ t('monitor.redis.dbSize') }}</span>
                <ElButton
                  :icon="Refresh"
                  circle
                  size="small"
                  @click="refreshKeys"
                  :loading="loadingKeys"
                />
              </div>
            </template>
            <div v-loading="loadingKeys" class="key-list-wrapper">
              <div class="key-list-header">
                <span class="col-index">{{ t('table.column.index') }}</span>
                <span class="col-key">{{ t('monitor.redis.info') }}</span>
                <span class="col-action">{{ t('common.operation') }}</span>
              </div>
              <ArtVirtualList
                ref="keyListRef"
                :data="keyList"
                :item-height="48"
                :height="'100%'"
                item-key="key"
                class="key-list-virtual"
              >
                <template #default="{ item, index }">
                  <div
                    class="key-list-row"
                    :class="{ 'is-active': selectedKey?.key === resolveCacheKeyInfo(item).key }"
                    @click="handleKeySelect(resolveCacheKeyInfo(item))"
                  >
                    <span class="col-index">{{ index + 1 }}</span>
                    <ElTooltip :content="resolveCacheKeyInfo(item).key" placement="top">
                      <span class="col-key text-ellipsis">{{ resolveCacheKeyInfo(item).key }}</span>
                    </ElTooltip>
                    <span class="col-action">
                      <ElButton
                        type="danger"
                        :icon="Delete"
                        circle
                        size="small"
                        @click.stop="handleDeleteKey(resolveCacheKeyInfo(item))"
                        :loading="deletingKey === resolveCacheKeyInfo(item).key"
                      />
                    </span>
                  </div>
                </template>
              </ArtVirtualList>
            </div>
          </ElCard>
        </div>

        <div class="cache-content-panel">
          <ElCard shadow="hover" class="panel-card">
            <template #header>
              <div class="panel-header">
                <span>{{ t('monitor.redis.memory') }}</span>
                <div>
                  <ElButton
                    type="danger"
                    size="small"
                    @click="handleClearAll"
                    :loading="clearingAll"
                  >
                    {{ t('system.operLog.clear') }}
                  </ElButton>
                  <ElButton
                    :icon="Refresh"
                    circle
                    size="small"
                    @click="refreshContent"
                    :loading="loadingContent"
                  />
                </div>
              </div>
            </template>
            <ElForm :model="cacheContent" label-width="100px">
              <ElFormItem :label="t('monitor.redis.info')">
                <ElInput v-model="cacheContent.cacheName" disabled />
              </ElFormItem>
              <ElFormItem :label="t('monitor.redis.dbSize')">
                <ElInput v-model="cacheContent.key" disabled />
              </ElFormItem>
              <ElFormItem :label="t('monitor.redis.memory')">
                <ElInput v-model="cacheContent.content" type="textarea" :rows="15" disabled />
              </ElFormItem>
            </ElForm>
          </ElCard>
        </div>
      </div>
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { Delete, Refresh } from '@element-plus/icons-vue'
  import {
    fetchClearAllCache,
    fetchDeleteCache,
    fetchDeleteCacheKey,
    fetchGetCacheContent,
    fetchGetCacheKeys,
    fetchGetCacheList
  } from '@/api/monitor/cache'
  import type { CacheContentInfo, CacheKeyInfo, RedisCacheInfo } from '@/types/api/monitor'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { useI18n } from 'vue-i18n'
  import { handleMutationError } from '@/utils/http/mutation'
  import ArtVirtualList from '@/components/core/lists/art-virtual-list/index.vue'

  defineOptions({ name: 'CacheManage' })

  const { t } = useI18n()

  const loading = ref(false)
  const keyListRef = ref<InstanceType<typeof ArtVirtualList> | null>(null)
  const cacheTableRef = ref<{
    setCurrentRow: (row?: RedisCacheInfo) => void
  } | null>(null)

  const loadingCacheList = ref(false)
  const loadingKeys = ref(false)
  const loadingContent = ref(false)
  const deletingCache = ref<string | null>(null)
  const deletingKey = ref<string | null>(null)
  const clearingAll = ref(false)

  const cacheList = ref<RedisCacheInfo[]>([])
  const selectedCache = ref<RedisCacheInfo | null>(null)
  const keyList = ref<CacheKeyInfo[]>([])
  const selectedKey = ref<CacheKeyInfo | null>(null)

  const resolveCacheKeyInfo = (item: Record<string, unknown>): CacheKeyInfo =>
    item as unknown as CacheKeyInfo
  const cacheContent = ref<CacheContentInfo>({
    cacheName: '',
    key: '',
    content: '',
    type: '',
    ttl: -2
  })

  const syncSelectionWithCacheList = () => {
    const name = selectedCache.value?.cacheName
    if (!name) {
      return
    }
    const exists = cacheList.value.some((c) => c.cacheName === name)
    if (!exists) {
      cacheTableRef.value?.setCurrentRow(undefined)
      selectedCache.value = null
      selectedKey.value = null
      keyList.value = []
      cacheContent.value = {
        cacheName: '',
        key: '',
        content: '',
        type: '',
        ttl: -2
      }
    }
  }

  const getCacheList = async () => {
    loadingCacheList.value = true
    try {
      const data = await fetchGetCacheList()
      cacheList.value = data ?? []
      syncSelectionWithCacheList()
    } catch (error) {
      console.error('get cache list failed:', error)
      handleMutationError(error, t('monitor.redis.loadFail'))
    } finally {
      loadingCacheList.value = false
    }
  }

  const refreshCacheList = () => {
    getCacheList()
  }

  const handleCacheSelect = async (cache: RedisCacheInfo | null) => {
    selectedCache.value = cache
    selectedKey.value = null
    if (cache) {
      await getCacheKeys(cache.cacheName)
      cacheContent.value = {
        cacheName: cache.cacheName,
        key: '',
        content: '',
        type: '',
        ttl: -2
      }
    } else {
      keyList.value = []
      cacheContent.value = {
        cacheName: '',
        key: '',
        content: '',
        type: '',
        ttl: -2
      }
    }
  }

  const getCacheKeys = async (cacheName: string) => {
    loadingKeys.value = true
    try {
      const data = await fetchGetCacheKeys(cacheName)
      keyList.value = data
      nextTick(() => keyListRef.value?.scrollToTop())
    } catch (error) {
      console.error('get cache keys failed:', error)
      handleMutationError(error, t('monitor.redis.loadFail'))
    } finally {
      loadingKeys.value = false
    }
  }

  const refreshKeys = () => {
    if (selectedCache.value) {
      getCacheKeys(selectedCache.value.cacheName)
    }
  }

  const handleKeySelect = async (key: CacheKeyInfo | null) => {
    selectedKey.value = key
    if (key && selectedCache.value) {
      await getCacheContent(selectedCache.value.cacheName, key.key)
    } else {
      cacheContent.value = {
        cacheName: selectedCache.value?.cacheName || '',
        key: '',
        content: '',
        type: '',
        ttl: -2
      }
    }
  }

  const getCacheContent = async (cacheName: string, key: string) => {
    loadingContent.value = true
    try {
      const data = await fetchGetCacheContent(cacheName, key)
      cacheContent.value = data
    } catch (error) {
      console.error('get cache content failed:', error)
      handleMutationError(error, t('monitor.redis.loadFail'))
    } finally {
      loadingContent.value = false
    }
  }

  const refreshContent = () => {
    if (selectedCache.value && selectedKey.value) {
      getCacheContent(selectedCache.value.cacheName, selectedKey.value.key)
    }
  }

  const handleDeleteCache = async (cache: RedisCacheInfo) => {
    try {
      await ElMessageBox.confirm(t('common.deleteConfirm'), t('common.tips'), {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      })

      deletingCache.value = cache.cacheName
      try {
        const deletedCount = await fetchDeleteCache(cache.cacheName)
        if (deletedCount > 0) {
          ElMessage.success(t('common.deleteSuccess'))
        } else {
          ElMessage.warning(t('common.empty'))
        }
        await getCacheList()
        if (selectedCache.value?.cacheName === cache.cacheName) {
          await getCacheKeys(cache.cacheName)
        }
      } catch (error) {
        console.error('delete cache failed:', error)
        handleMutationError(error, t('common.deleteFail'))
      } finally {
        deletingCache.value = null
      }
    } catch {
      // cancelled
    }
  }

  const handleDeleteKey = async (key: CacheKeyInfo) => {
    if (!selectedCache.value) {
      return
    }

    try {
      await ElMessageBox.confirm(t('common.deleteConfirm'), t('common.tips'), {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      })
      deletingKey.value = key.key
      try {
        await fetchDeleteCacheKey(selectedCache.value.cacheName, key.key)
        ElMessage.success(t('common.deleteSuccess'))
        await getCacheKeys(selectedCache.value.cacheName)
        if (keyList.value.length === 0) {
          await getCacheList()
        }
        cacheContent.value = {
          cacheName: selectedCache.value?.cacheName || '',
          key: '',
          content: '',
          type: '',
          ttl: -2
        }
      } catch (error) {
        console.error('delete key failed:', error)
        handleMutationError(error, t('common.deleteFail'))
      } finally {
        deletingKey.value = null
      }
    } catch {
      // cancelled
    }
  }

  const handleClearAll = async () => {
    try {
      await ElMessageBox.confirm(t('system.operLog.clearConfirm'), t('common.tips'), {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      })
      clearingAll.value = true
      try {
        await fetchClearAllCache()
        ElMessage.success(t('common.deleteSuccess'))
        cacheTableRef.value?.setCurrentRow(undefined)
        selectedCache.value = null
        selectedKey.value = null
        await getCacheList()
        keyList.value = []
        cacheContent.value = {
          cacheName: '',
          key: '',
          content: '',
          type: '',
          ttl: -2
        }
      } catch (error) {
        console.error('clear cache failed:', error)
        handleMutationError(error, t('common.deleteFail'))
      } finally {
        clearingAll.value = false
      }
    } catch {
      // cancelled
    }
  }

  const handleRefresh = async () => {
    await getCacheList()
    if (selectedCache.value) {
      await getCacheKeys(selectedCache.value.cacheName)
    }
    if (selectedCache.value && selectedKey.value) {
      await getCacheContent(selectedCache.value.cacheName, selectedKey.value.key)
    }
  }

  onMounted(() => {
    getCacheList()
  })
</script>

<style scoped lang="scss">
  .redis-page {
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

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .cache-container {
    display: flex;
    gap: 20px;
    height: calc(100vh - 300px);
  }

  .cache-list-panel,
  .cache-content-panel {
    display: flex;
    flex-direction: column;
    width: 400px;
    min-width: 0;
  }

  .key-list-panel {
    display: flex;
    flex-direction: column;
    width: 500px;
    min-width: 0;
  }

  .panel-card {
    display: flex;
    flex: 1;
    flex-direction: column;
    overflow: hidden;
    border: 1px solid var(--art-card-border);
    border-radius: 12px;
    box-shadow: 0 2px 8px 0 rgb(0 0 0 / 6%);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 12px 0 rgb(0 0 0 / 10%);
    }

    :deep(.el-card__header) {
      padding: 14px 18px;
      font-weight: 600;
      color: var(--art-gray-800);
      border-bottom: 1px solid var(--art-card-border);
    }

    :deep(.el-card__body) {
      display: flex;
      flex: 1;
      flex-direction: column;
      overflow: hidden;
    }
  }

  .panel-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  :deep(.el-table) {
    flex: 1;
    overflow: auto;
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

  .key-list-wrapper {
    display: flex;
    flex: 1;
    flex-direction: column;
    min-height: 0;
  }

  .key-list-header {
    display: flex;
    flex-shrink: 0;
    align-items: center;
    padding: 10px 12px;
    font-size: 14px;
    font-weight: 600;
    color: var(--el-text-color-regular);
    background: var(--el-fill-color-lighter);
    border-radius: 8px 8px 0 0;

    .col-index {
      flex-shrink: 0;
      width: 60px;
    }

    .col-key {
      flex: 1;
      min-width: 0;
    }

    .col-action {
      flex-shrink: 0;
      width: 80px;
      text-align: center;
    }
  }

  .key-list-virtual {
    flex: 1;
    min-height: 0;
  }

  .key-list-row {
    display: flex;
    align-items: center;
    padding: 0 12px;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      background: var(--el-fill-color-light);
    }

    &.is-active {
      background: var(--el-color-primary-light-9);
    }

    .col-index {
      flex-shrink: 0;
      width: 60px;
    }

    .col-key {
      flex: 1;
      min-width: 0;
    }

    .col-action {
      display: flex;
      flex-shrink: 0;
      align-items: center;
      justify-content: center;
      width: 80px;
    }
  }

  .text-ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  :deep(.el-form) {
    flex: 1;
    overflow: auto;
  }

  :deep(.el-form-item__label) {
    font-weight: 500;
    color: var(--art-gray-700);
  }

  :deep(.el-input__wrapper),
  :deep(.el-textarea__inner) {
    border-radius: 8px;
    transition: all 0.3s ease;
  }

  :deep(.el-button) {
    font-weight: 500;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-1px);
    }
  }

  @media (width <= 1200px) {
    .cache-container {
      flex-wrap: wrap;
      height: auto;
      min-height: 420px;
    }

    .cache-list-panel,
    .key-list-panel,
    .cache-content-panel {
      width: calc(50% - 10px);
      min-height: 360px;
    }

    .cache-content-panel {
      width: 100%;
    }
  }

  @media (width <= 768px) {
    .card-header {
      flex-wrap: wrap;
      gap: 8px;
    }

    .cache-container {
      flex-direction: column;
      gap: 12px;
      height: auto;
    }

    .cache-list-panel,
    .key-list-panel,
    .cache-content-panel {
      width: 100%;
      min-height: 280px;
    }

    :deep(.el-button:hover) {
      transform: none;
    }
  }
</style>
