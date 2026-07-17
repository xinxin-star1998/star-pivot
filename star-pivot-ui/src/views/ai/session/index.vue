<template>
  <div class="ai-session-page art-full-height">
    <ElCard shadow="never" class="search-card">
      <ElForm :inline="true" :model="searchForm">
        <ElFormItem :label="t('ai.session.userId')">
          <ElInputNumber
            v-model="searchForm.userId"
            :min="1"
            class="!w-36"
            controls-position="right"
          />
        </ElFormItem>
        <ElFormItem :label="t('ai.session.conversationId')">
          <ElInput
            v-model="searchForm.conversationId"
            class="!w-56"
            clearable
            placeholder="conversationId"
          />
        </ElFormItem>
        <ElFormItem :label="t('ai.session.sessionTitle')">
          <ElInput
            v-model="searchForm.title"
            clearable
            :placeholder="t('ai.session.titlePlaceholder')"
          />
        </ElFormItem>
        <ElFormItem>
          <ElButton type="primary" @click="handleSearch">{{ t('ai.common.search') }}</ElButton>
          <ElButton @click="resetSearch">{{ t('common.reset') }}</ElButton>
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

    <ElDrawer v-model="messageDrawerVisible" size="520px" :title="t('ai.session.messages')">
      <div v-if="messageLoading" class="py-10 text-center text-g-500">{{
        t('ai.common.loading')
      }}</div>
      <div v-else-if="!sessionMessages.length" class="py-10 text-center text-g-500">
        {{ t('ai.session.noMessages') }}
      </div>
      <div v-else class="space-y-4">
        <div
          v-for="(item, index) in sessionMessages"
          :key="index"
          class="rounded-md border border-g-300/60 p-3"
        >
          <div class="mb-1 flex items-center justify-between text-xs text-g-500">
            <span>{{
              item.role === 'USER' ? t('ai.session.user') : t('ai.session.assistant')
            }}</span>
            <span>{{ formatMessageTime(item.createTime) }}</span>
          </div>
          <div class="whitespace-pre-wrap break-words text-sm text-g-900">{{ item.content }}</div>
        </div>
      </div>
    </ElDrawer>
  </div>
</template>

<script lang="ts" setup>
  import { h } from 'vue'
  import { useI18n } from 'vue-i18n'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { useTable } from '@/hooks/core/useTable'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import {
    type AiChatSessionAdminItem,
    fetchAiSessionAdminList,
    fetchAiSessionAdminMessages,
    fetchAiSessionAdminRemove
  } from '@/api/ai/session'
  import type { ChatHistoryMessage } from '@/api/ai/chat'
  import { handleMutationError } from '@/utils/http/mutation'

  defineOptions({ name: 'AiSession' })

  const { t } = useI18n()

  const searchForm = ref<{ userId?: number; conversationId: string; title: string }>({
    conversationId: '',
    title: ''
  })

  const messageDrawerVisible = ref(false)
  const messageLoading = ref(false)
  const sessionMessages = ref<ChatHistoryMessage[]>([])

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
      apiFn: fetchAiSessionAdminList,
      apiParams: { pageNum: 1, pageSize: 20, ...searchForm.value },
      columnsFactory: () => [
        { type: 'index', width: 60, label: t('ai.common.index') },
        { prop: 'sessionId', label: 'ID', width: 80 },
        { prop: 'userId', label: t('ai.session.userId'), width: 90 },
        { prop: 'conversationId', label: t('ai.session.conversationId'), minWidth: 180 },
        { prop: 'title', label: t('ai.session.sessionTitle'), minWidth: 140 },
        { prop: 'messageCount', label: t('ai.session.messageCount'), width: 80 },
        { prop: 'updateTime', label: t('ai.common.updateTime'), minWidth: 160 },
        {
          prop: 'actions',
          label: t('common.operation'),
          width: 160,
          fixed: 'right',
          formatter: (row: AiChatSessionAdminItem) =>
            h('div', { class: 'flex gap-2' }, [
              h(
                'a',
                { class: 'text-primary cursor-pointer', onClick: () => openMessages(row) },
                t('ai.common.view')
              ),
              h(
                'a',
                { class: 'text-danger cursor-pointer', onClick: () => handleDelete(row) },
                t('common.delete')
              )
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
    searchForm.value = { conversationId: '', title: '' }
    handleSearch()
  }

  async function openMessages(row: AiChatSessionAdminItem): Promise<void> {
    if (!row.conversationId) return
    messageDrawerVisible.value = true
    messageLoading.value = true
    sessionMessages.value = []
    try {
      sessionMessages.value = (await fetchAiSessionAdminMessages(row.conversationId)) || []
    } catch (error) {
      handleMutationError(error, t('ai.session.loadMessagesFail'))
    } finally {
      messageLoading.value = false
    }
  }

  async function handleDelete(row: AiChatSessionAdminItem): Promise<void> {
    if (!row.sessionId) return
    try {
      await ElMessageBox.confirm(
        t('ai.session.deleteConfirm', { name: row.title || row.conversationId }),
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
      await fetchAiSessionAdminRemove(row.sessionId)
      ElMessage.success(t('ai.common.deleteSuccess'))
      await refreshData()
    } catch (error) {
      handleMutationError(error, t('ai.common.deleteFail'))
    }
  }

  function formatMessageTime(timestamp?: number): string {
    if (!timestamp) return ''
    return new Date(timestamp).toLocaleString()
  }
</script>

<style scoped lang="scss">
  .ai-session-page {
    .search-card {
      margin-bottom: 12px;
    }
  }
</style>
