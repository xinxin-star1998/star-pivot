<!-- Redis 监控与缓存管理页面 -->
<template>
  <div class="redis-page art-full-height">
    <ElCard class="art-table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>Redis 监控与缓存管理</span>
          <ElButton type="primary" :icon="Refresh" @click="handleRefresh" :loading="loading">
            刷新
          </ElButton>
        </div>
      </template>

      <ElTabs v-model="activeTab" @tab-change="handleTabChange">
        <!-- Redis 监控标签页 -->
        <ElTabPane label="Redis 监控" name="monitor">
          <div v-loading="loadingMonitor">
            <!-- Redis 未配置或连接失败时展示空状态 -->
            <ElEmpty
              v-if="redisInfo && redisInfo.available === false"
              :description="redisInfo.message || 'Redis 未配置或未启用'"
              class="redis-empty"
            />
            <!-- Redis 正常时展示监控卡片 -->
            <ElRow v-else-if="redisInfo && redisInfo.available !== false" :gutter="20">
              <!-- Redis 基本信息 -->
              <ElCol :xs="24" :sm="12" :md="8">
                <ElCard shadow="hover">
                  <template #header>基本信息</template>
                  <ElDescriptions :column="1" border>
                    <ElDescriptionsItem label="Redis 版本">
                      {{ redisInfo.version || '-' }}
                    </ElDescriptionsItem>
                    <ElDescriptionsItem label="运行模式">
                      {{ redisInfo.mode || '-' }}
                    </ElDescriptionsItem>
                    <ElDescriptionsItem label="端口">
                      {{ redisInfo.port || '-' }}
                    </ElDescriptionsItem>
                    <ElDescriptionsItem label="连接状态">
                      <ElTag :type="redisInfo.connected ? 'success' : 'danger'">
                        {{ redisInfo.connected ? '已连接' : '未连接' }}
                      </ElTag>
                    </ElDescriptionsItem>
                  </ElDescriptions>
                </ElCard>
              </ElCol>

              <!-- 内存信息 -->
              <ElCol :xs="24" :sm="12" :md="8">
                <ElCard shadow="hover">
                  <template #header>内存信息</template>
                  <ElDescriptions :column="1" border>
                    <ElDescriptionsItem label="已使用内存">
                      {{ formatSize(redisInfo.memory?.usedMemory || 0) }} MB
                    </ElDescriptionsItem>
                    <ElDescriptionsItem label="最大内存">
                      {{ formatSize(redisInfo.memory?.maxMemory || 0) }} MB
                    </ElDescriptionsItem>
                    <ElDescriptionsItem label="内存使用率">
                      <span :class="getUsageClass(redisInfo.memory?.usage || 0)">
                        {{ formatPercent(redisInfo.memory?.usage || 0) }}
                      </span>
                    </ElDescriptionsItem>
                  </ElDescriptions>
                  <ElProgress
                    :percentage="redisInfo.memory?.usage || 0"
                    :color="getProgressColor(redisInfo.memory?.usage || 0)"
                    :stroke-width="8"
                    style="margin-top: 10px"
                  />
                </ElCard>
              </ElCol>

              <!-- 客户端信息 -->
              <ElCol :xs="24" :sm="12" :md="8">
                <ElCard shadow="hover">
                  <template #header>客户端信息</template>
                  <ElDescriptions :column="1" border>
                    <ElDescriptionsItem label="已连接客户端数">
                      {{ redisInfo.clients?.connectedClients || 0 }}
                    </ElDescriptionsItem>
                    <ElDescriptionsItem label="阻塞客户端数">
                      <ElTag type="warning">
                        {{ redisInfo.clients?.blockedClients || 0 }}
                      </ElTag>
                    </ElDescriptionsItem>
                  </ElDescriptions>
                </ElCard>
              </ElCol>
            </ElRow>

            <!-- 统计信息 -->
            <ElRow :gutter="20" style="margin-top: 20px" v-if="redisInfo">
              <ElCol :xs="24" :sm="12" :md="12">
                <ElCard shadow="hover">
                  <template #header>键值统计</template>
                  <ElDescriptions :column="1" border>
                    <ElDescriptionsItem label="键总数">
                      {{ formatNumber(redisInfo.keys?.totalKeys || 0) }}
                    </ElDescriptionsItem>
                    <ElDescriptionsItem label="过期键数">
                      {{ formatNumber(redisInfo.keys?.expiredKeys || 0) }}
                    </ElDescriptionsItem>
                  </ElDescriptions>
                </ElCard>
              </ElCol>

              <ElCol :xs="24" :sm="12" :md="12">
                <ElCard shadow="hover">
                  <template #header>命令统计</template>
                  <ElDescriptions :column="1" border>
                    <ElDescriptionsItem label="总命令数">
                      {{ formatNumber(redisInfo.commands?.totalCommands || 0) }}
                    </ElDescriptionsItem>
                    <ElDescriptionsItem label="每秒命令数">
                      {{ formatNumber(redisInfo.commands?.commandsPerSecond || 0, 2) }}
                    </ElDescriptionsItem>
                  </ElDescriptions>
                </ElCard>
              </ElCol>
            </ElRow>
          </div>
        </ElTabPane>

        <!-- 缓存管理标签页 -->
        <ElTabPane label="缓存管理" name="cache">
          <div class="cache-container">
            <!-- 左侧：缓存列表 -->
            <div class="cache-list-panel">
              <ElCard shadow="hover" class="panel-card">
                <template #header>
                  <div class="panel-header">
                    <span>缓存列表</span>
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
                  :data="cacheList"
                  highlight-current-row
                  @current-change="handleCacheSelect"
                  v-loading="loadingCacheList"
                >
                  <ElTableColumn type="index" label="序号" width="60" />
                  <ElTableColumn prop="cacheName" label="缓存名称" />
                  <ElTableColumn prop="remark" label="备注" />
                  <ElTableColumn label="操作" width="80">
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

            <!-- 中间：键名列表（虚拟滚动，适用于大量键） -->
            <div class="key-list-panel">
              <ElCard shadow="hover" class="panel-card">
                <template #header>
                  <div class="panel-header">
                    <span>键名列表</span>
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
                  <!-- 表头 -->
                  <div class="key-list-header">
                    <span class="col-index">序号</span>
                    <span class="col-key">缓存键名</span>
                    <span class="col-action">操作</span>
                  </div>
                  <!-- 虚拟滚动列表 -->
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
                        :class="{ 'is-active': selectedKey?.key === item.key }"
                        @click="handleKeySelect(item)"
                      >
                        <span class="col-index">{{ index + 1 }}</span>
                        <ElTooltip :content="item.key" placement="top">
                          <span class="col-key text-ellipsis">{{ item.key }}</span>
                        </ElTooltip>
                        <span class="col-action">
                          <ElButton
                            type="danger"
                            :icon="Delete"
                            circle
                            size="small"
                            @click.stop="handleDeleteKey(item)"
                            :loading="deletingKey === item.key"
                          />
                        </span>
                      </div>
                    </template>
                  </ArtVirtualList>
                </div>
              </ElCard>
            </div>

            <!-- 右侧：缓存内容 -->
            <div class="cache-content-panel">
              <ElCard shadow="hover" class="panel-card">
                <template #header>
                  <div class="panel-header">
                    <span>缓存内容</span>
                    <div>
                      <ElButton
                        type="danger"
                        size="small"
                        @click="handleClearAll"
                        :loading="clearingAll"
                      >
                        清理全部
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
                  <ElFormItem label="缓存名称">
                    <ElInput v-model="cacheContent.cacheName" disabled />
                  </ElFormItem>
                  <ElFormItem label="缓存键名">
                    <ElInput v-model="cacheContent.key" disabled />
                  </ElFormItem>
                  <ElFormItem label="缓存内容">
                    <ElInput v-model="cacheContent.content" type="textarea" :rows="15" disabled />
                  </ElFormItem>
                </ElForm>
              </ElCard>
            </div>
          </div>
        </ElTabPane>
      </ElTabs>
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { Refresh, Delete } from '@element-plus/icons-vue'
  import { fetchGetRedisMonitorInfo } from '@/api/monitor/redis'
  import {
    fetchGetCacheList,
    fetchGetCacheKeys,
    fetchGetCacheContent,
    fetchDeleteCache,
    fetchDeleteCacheKey,
    fetchClearAllCache
  } from '@/api/monitor/cache'
  import type {
    RedisMonitorInfo,
    RedisCacheInfo,
    CacheKeyInfo,
    CacheContentInfo
  } from '@/types/api/monitor'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import ArtVirtualList from '@/components/core/lists/art-virtual-list/index.vue'

  defineOptions({ name: 'RedisMonitor' })

  const activeTab = ref('monitor')
  const loading = ref(false)
  const loadingMonitor = ref(false)
  const keyListRef = ref<InstanceType<typeof ArtVirtualList> | null>(null)

  const loadingCacheList = ref(false)
  const loadingKeys = ref(false)
  const loadingContent = ref(false)
  const deletingCache = ref<string | null>(null)
  const deletingKey = ref<string | null>(null)
  const clearingAll = ref(false)

  const redisInfo = ref<RedisMonitorInfo | null>(null)
  const cacheList = ref<RedisCacheInfo[]>([])
  const selectedCache = ref<RedisCacheInfo | null>(null)
  const keyList = ref<CacheKeyInfo[]>([])
  const selectedKey = ref<CacheKeyInfo | null>(null)
  const cacheContent = ref<CacheContentInfo>({
    cacheName: '',
    key: '',
    content: '',
    type: '',
    ttl: -2
  })

  let refreshTimer: number | null = null

  // 格式化百分比
  const formatPercent = (value: number) => {
    return `${value.toFixed(2)}%`
  }

  // 格式化大小
  const formatSize = (value: number) => {
    return value.toLocaleString()
  }

  // 格式化数字
  const formatNumber = (value: number, decimals: number = 0) => {
    return value.toLocaleString('zh-CN', {
      minimumFractionDigits: decimals,
      maximumFractionDigits: decimals
    })
  }

  // 获取使用率样式类
  const getUsageClass = (usage: number) => {
    if (usage >= 90) return 'text-danger'
    if (usage >= 70) return 'text-warning'
    return 'text-success'
  }

  // 获取进度条颜色
  const getProgressColor = (usage: number) => {
    if (usage >= 90) return '#f56c6c'
    if (usage >= 70) return '#e6a23c'
    return '#67c23a'
  }

  /**
   * 获取 Redis 监控数据
   */
  const getRedisMonitorInfo = async () => {
    loadingMonitor.value = true
    try {
      const data = await fetchGetRedisMonitorInfo()
      redisInfo.value = data
    } catch (error) {
      if (import.meta.env.DEV) {
        console.error('获取 Redis 监控信息失败:', error)
      }
      redisInfo.value = {
        available: false,
        message: '获取 Redis 监控信息失败'
      }
    } finally {
      loadingMonitor.value = false
    }
  }

  /**
   * 获取缓存列表
   */
  const getCacheList = async () => {
    loadingCacheList.value = true
    try {
      const data = await fetchGetCacheList()
      cacheList.value = data
    } catch (error) {
      console.error('获取缓存列表失败:', error)
    } finally {
      loadingCacheList.value = false
    }
  }

  /**
   * 刷新缓存列表
   */
  const refreshCacheList = () => {
    getCacheList()
  }

  /**
   * 处理缓存选择
   */
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

  /**
   * 获取键名列表
   */
  const getCacheKeys = async (cacheName: string) => {
    loadingKeys.value = true
    try {
      const data = await fetchGetCacheKeys(cacheName)
      keyList.value = data
      nextTick(() => keyListRef.value?.scrollToTop())
    } catch (error) {
      console.error('获取键名列表失败:', error)
      ElMessage.error('获取键名列表失败')
    } finally {
      loadingKeys.value = false
    }
  }

  /**
   * 刷新键名列表
   */
  const refreshKeys = () => {
    if (selectedCache.value) {
      getCacheKeys(selectedCache.value.cacheName)
    }
  }

  /**
   * 处理键选择
   */
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

  /**
   * 获取缓存内容
   */
  const getCacheContent = async (cacheName: string, key: string) => {
    loadingContent.value = true
    try {
      const data = await fetchGetCacheContent(cacheName, key)
      cacheContent.value = data
    } catch (error) {
      console.error('获取缓存内容失败:', error)
      ElMessage.error('获取缓存内容失败')
    } finally {
      loadingContent.value = false
    }
  }

  /**
   * 刷新缓存内容
   */
  const refreshContent = () => {
    if (selectedCache.value && selectedKey.value) {
      getCacheContent(selectedCache.value.cacheName, selectedKey.value.key)
    }
  }

  /**
   * 删除缓存
   */
  const handleDeleteCache = async (cache: RedisCacheInfo) => {
    try {
      await ElMessageBox.confirm(
        `确定要删除缓存 "${cache.cacheName}" 的所有键吗？此操作不可恢复！`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )

      deletingCache.value = cache.cacheName
      try {
        const deletedCount = await fetchDeleteCache(cache.cacheName)
        ElMessage.success(`删除成功，共删除 ${deletedCount} 个键`)
        if (selectedCache.value?.cacheName === cache.cacheName) {
          await getCacheKeys(cache.cacheName)
        }
      } catch (error) {
        console.error('删除缓存失败:', error)
        ElMessage.error('删除缓存失败')
      } finally {
        deletingCache.value = null
      }
    } catch {
      // 用户取消
    }
  }

  /**
   * 删除键
   */
  const handleDeleteKey = async (key: CacheKeyInfo) => {
    if (!selectedCache.value) {
      return
    }

    try {
      await ElMessageBox.confirm(`确定要删除键 "${key.key}" 吗？此操作不可恢复！`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      deletingKey.value = key.key
      try {
        await fetchDeleteCacheKey(selectedCache.value.cacheName, key.key)
        ElMessage.success('删除成功')
        await getCacheKeys(selectedCache.value.cacheName)
        cacheContent.value = {
          cacheName: selectedCache.value.cacheName,
          key: '',
          content: '',
          type: '',
          ttl: -2
        }
      } catch (error) {
        console.error('删除键失败:', error)
        ElMessage.error('删除键失败')
      } finally {
        deletingKey.value = null
      }
    } catch {
      // 用户取消
    }
  }

  /**
   * 清空所有缓存
   */
  const handleClearAll = async () => {
    try {
      await ElMessageBox.confirm('确定要清空所有缓存吗？此操作不可恢复！', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      clearingAll.value = true
      try {
        await fetchClearAllCache()
        ElMessage.success('清空成功')
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
        console.error('清空缓存失败:', error)
        ElMessage.error('清空缓存失败')
      } finally {
        clearingAll.value = false
      }
    } catch {
      // 用户取消
    }
  }

  /**
   * 处理标签页切换
   */
  const handleTabChange = (tabName: string) => {
    if (tabName === 'monitor') {
      getRedisMonitorInfo()
    } else if (tabName === 'cache') {
      getCacheList()
    }
  }

  /**
   * 处理刷新按钮点击
   */
  const handleRefresh = () => {
    if (activeTab.value === 'monitor') {
      getRedisMonitorInfo()
    } else if (activeTab.value === 'cache') {
      getCacheList()
      if (selectedCache.value) {
        getCacheKeys(selectedCache.value.cacheName)
      }
      if (selectedCache.value && selectedKey.value) {
        getCacheContent(selectedCache.value.cacheName, selectedKey.value.key)
      }
    }
  }

  // 自动刷新（仅监控标签页，每10秒）
  const startAutoRefresh = () => {
    if (refreshTimer) {
      clearInterval(refreshTimer)
    }
    refreshTimer = window.setInterval(() => {
      if (activeTab.value === 'monitor') {
        getRedisMonitorInfo()
      }
    }, 10000)
  }

  // 停止自动刷新
  const stopAutoRefresh = () => {
    if (refreshTimer) {
      clearInterval(refreshTimer)
      refreshTimer = null
    }
  }

  onMounted(() => {
    getRedisMonitorInfo()
    startAutoRefresh()
  })

  onBeforeUnmount(() => {
    stopAutoRefresh()
  })
</script>

<style scoped lang="scss">
  .redis-page {
    padding: 20px;
  }

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .text-success {
    color: var(--el-color-success);
  }

  .text-warning {
    color: var(--el-color-warning);
  }

  .text-danger {
    color: var(--el-color-danger);
  }

  .redis-empty {
    padding: 48px 0;
  }

  .cache-container {
    display: flex;
    gap: 20px;
    height: calc(100vh - 300px);
  }

  .cache-list-panel,
  .key-list-panel,
  .cache-content-panel {
    display: flex;
    flex: 1;
    flex-direction: column;
  }

  .panel-card {
    display: flex;
    flex: 1;
    flex-direction: column;
    overflow: hidden;

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

  .el-table {
    flex: 1;
    overflow: auto;
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
    transition: background 0.2s;

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

  .el-form {
    flex: 1;
    overflow: auto;
  }
</style>
