<template>
  <div class="ai-knowledge-page art-full-height">
    <ElCard shadow="never" class="search-card">
      <ElForm :inline="true" :model="searchForm">
        <ElFormItem :label="t('ai.knowledge.name')">
          <ElInput
            v-model="searchForm.kbName"
            clearable
            :placeholder="t('ai.knowledge.namePlaceholder')"
          />
        </ElFormItem>
        <ElFormItem>
          <ElButton type="primary" @click="handleSearch">{{ t('ai.common.search') }}</ElButton>
          <ElButton @click="resetSearch">{{ t('common.reset') }}</ElButton>
        </ElFormItem>
      </ElForm>
    </ElCard>

    <ElCard class="art-table-card" shadow="never">
      <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
        <template #left>
          <ElButton v-auth="'ai:knowledge:edit'" type="primary" @click="openKbEdit()">
            {{ t('ai.knowledge.addKb') }}
          </ElButton>
        </template>
      </ArtTableHeader>
      <ArtTable
        :columns="columns"
        :data="data"
        :loading="loading"
        :pagination="pagination"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      />
    </ElCard>

    <ElDialog
      v-model="kbEditVisible"
      :title="kbForm.kbId ? t('ai.knowledge.editKb') : t('ai.knowledge.addKb')"
      destroy-on-close
      width="560px"
    >
      <ElForm ref="kbFormRef" :model="kbForm" :rules="kbRules" label-width="100px">
        <ElFormItem :label="t('ai.knowledge.name')" prop="kbName">
          <ElInput v-model="kbForm.kbName" />
        </ElFormItem>
        <ElFormItem :label="t('ai.knowledge.description')">
          <ElInput v-model="kbForm.description" type="textarea" :rows="2" />
        </ElFormItem>
        <ElFormItem :label="t('ai.knowledge.topK')">
          <ElInputNumber v-model="kbForm.topK" :min="1" :max="20" />
        </ElFormItem>
        <ElFormItem :label="t('ai.knowledge.chunkSize')">
          <ElInputNumber v-model="kbForm.chunkSize" :min="200" :max="4000" :step="100" />
        </ElFormItem>
        <ElFormItem :label="t('ai.knowledge.chunkOverlap')">
          <ElInputNumber v-model="kbForm.chunkOverlap" :min="0" :max="500" :step="20" />
        </ElFormItem>
        <ElFormItem :label="t('common.status')">
          <ElRadioGroup v-model="kbForm.status">
            <ElRadio value="0">{{ t('common.normal') }}</ElRadio>
            <ElRadio value="1">{{ t('common.disabled') }}</ElRadio>
          </ElRadioGroup>
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="kbEditVisible = false">{{ t('common.cancel') }}</ElButton>
        <ElButton type="primary" :loading="kbSaving" @click="saveKb">{{
          t('ai.common.save')
        }}</ElButton>
      </template>
    </ElDialog>

    <ElDrawer
      v-model="docDrawerVisible"
      :title="t('ai.knowledge.docManage', { name: activeKb?.kbName || '' })"
      size="760px"
    >
      <div class="mb-3 flex justify-end gap-2">
        <ElUpload
          v-auth="'ai:knowledge:edit'"
          :show-file-list="false"
          :auto-upload="false"
          accept=".pdf,.docx,.md,.markdown,.txt"
          @change="handleFileSelect"
        >
          <ElButton type="primary" :loading="fileUploading">{{
            t('ai.knowledge.uploadDoc')
          }}</ElButton>
        </ElUpload>
        <ElButton v-auth="'ai:knowledge:edit'" @click="openDocEdit()">
          {{ t('ai.knowledge.pasteText') }}
        </ElButton>
      </div>
      <ElTable v-loading="docLoading" :data="docList" border>
        <ElTableColumn prop="title" :label="t('ai.knowledge.docTitle')" min-width="160" />
        <ElTableColumn prop="sourceType" :label="t('ai.knowledge.source')" width="80">
          <template #default="{ row }">
            {{
              row.sourceType === 'FILE'
                ? t('ai.knowledge.sourceFile')
                : t('ai.knowledge.sourceText')
            }}
          </template>
        </ElTableColumn>
        <ElTableColumn prop="indexStatus" :label="t('ai.knowledge.parseStatus')" width="100">
          <template #default="{ row }">
            <ElTag :type="indexStatusTag(row.indexStatus).type" size="small">
              {{ indexStatusTag(row.indexStatus).label }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="chunkCount" :label="t('ai.knowledge.chunkCount')" width="80" />
        <ElTableColumn prop="updateTime" :label="t('ai.common.updateTime')" min-width="160" />
        <ElTableColumn :label="t('common.operation')" width="220" fixed="right">
          <template #default="{ row }">
            <ElButton v-auth="'ai:knowledge:edit'" link type="primary" @click="openDocEdit(row)">
              {{ t('ai.common.edit') }}
            </ElButton>
            <ElButton v-auth="'ai:knowledge:edit'" link type="primary" @click="reindexDoc(row)">
              {{ t('ai.knowledge.reindex') }}
            </ElButton>
            <ElButton v-auth="'ai:knowledge:delete'" link type="danger" @click="removeDoc(row)">
              {{ t('common.delete') }}
            </ElButton>
          </template>
        </ElTableColumn>
      </ElTable>
    </ElDrawer>

    <ElDialog
      v-model="docEditVisible"
      :title="docForm.docId ? t('ai.knowledge.editDoc') : t('ai.knowledge.addDoc')"
      destroy-on-close
      width="720px"
    >
      <ElForm ref="docFormRef" :model="docForm" :rules="docRules" label-width="80px">
        <ElFormItem :label="t('ai.knowledge.docTitle')" prop="title">
          <ElInput v-model="docForm.title" />
        </ElFormItem>
        <ElFormItem :label="t('ai.knowledge.content')" prop="content">
          <ElInput
            v-model="docForm.content"
            :rows="14"
            :placeholder="t('ai.knowledge.contentPlaceholder')"
            type="textarea"
          />
        </ElFormItem>
        <ElFormItem :label="t('common.status')">
          <ElRadioGroup v-model="docForm.status">
            <ElRadio value="0">{{ t('common.normal') }}</ElRadio>
            <ElRadio value="1">{{ t('common.disabled') }}</ElRadio>
          </ElRadioGroup>
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="docEditVisible = false">{{ t('common.cancel') }}</ElButton>
        <ElButton type="primary" :loading="docSaving" @click="saveDoc">{{
          t('ai.common.save')
        }}</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script lang="ts" setup>
  import { h } from 'vue'
  import type { FormInstance, FormRules } from 'element-plus'
  import { ElMessage, ElMessageBox, ElTag } from 'element-plus'
  import { useI18n } from 'vue-i18n'
  import { useTable } from '@/hooks/core/useTable'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import {
    type AiKnowledgeBaseItem,
    type AiKnowledgeBaseSavePayload,
    type AiKnowledgeDocumentItem,
    type AiKnowledgeDocumentSavePayload,
    fetchAiKnowledgeBaseList,
    fetchAiKnowledgeBaseRemove,
    fetchAiKnowledgeBaseSave,
    fetchAiKnowledgeDocumentDetail,
    fetchAiKnowledgeDocumentList,
    fetchAiKnowledgeDocumentReindex,
    fetchAiKnowledgeDocumentRemove,
    fetchAiKnowledgeDocumentSave,
    fetchAiKnowledgeDocumentUpload
  } from '@/api/ai/knowledge'
  import { handleMutationError } from '@/utils/http/mutation'

  defineOptions({ name: 'AiKnowledge' })

  const { t } = useI18n()

  const searchForm = ref({ kbName: '' })
  const kbEditVisible = ref(false)
  const kbSaving = ref(false)
  const kbFormRef = ref<FormInstance>()
  const kbForm = ref<AiKnowledgeBaseSavePayload>({
    kbName: '',
    description: '',
    topK: 5,
    chunkSize: 600,
    chunkOverlap: 80,
    status: '0'
  })

  const kbRules = computed<FormRules>(() => ({
    kbName: [{ required: true, message: t('ai.knowledge.nameRequired'), trigger: 'blur' }]
  }))

  const docDrawerVisible = ref(false)
  const docLoading = ref(false)
  const docList = ref<AiKnowledgeDocumentItem[]>([])
  const activeKb = ref<AiKnowledgeBaseItem | null>(null)

  const docEditVisible = ref(false)
  const docSaving = ref(false)
  const fileUploading = ref(false)
  const docFormRef = ref<FormInstance>()
  const docForm = ref<AiKnowledgeDocumentSavePayload>({
    kbId: 0,
    title: '',
    content: '',
    status: '0'
  })

  const docRules = computed<FormRules>(() => ({
    title: [{ required: true, message: t('ai.knowledge.titleRequired'), trigger: 'blur' }],
    content: [{ required: true, message: t('ai.knowledge.contentRequired'), trigger: 'blur' }]
  }))

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
      apiFn: fetchAiKnowledgeBaseList,
      apiParams: { pageNum: 1, pageSize: 20, ...searchForm.value },
      columnsFactory: () => [
        { type: 'index', width: 60, label: t('ai.common.index') },
        { prop: 'kbName', label: t('ai.knowledge.name'), minWidth: 140 },
        { prop: 'topK', label: 'TopK', width: 70 },
        { prop: 'chunkSize', label: t('ai.knowledge.chunkSizeShort'), width: 80 },
        {
          prop: 'status',
          label: t('common.status'),
          width: 80,
          formatter: (row: AiKnowledgeBaseItem) =>
            h(ElTag, { type: row.status === '0' ? 'success' : 'info', size: 'small' }, () =>
              row.status === '0' ? t('common.normal') : t('common.disabled')
            )
        },
        { prop: 'updateTime', label: t('ai.common.updateTime'), minWidth: 160 },
        {
          prop: 'actions',
          label: t('common.operation'),
          width: 220,
          fixed: 'right',
          formatter: (row: AiKnowledgeBaseItem) =>
            h('div', { class: 'flex gap-2' }, [
              h(
                'a',
                { class: 'text-primary cursor-pointer', onClick: () => openDocs(row) },
                t('ai.knowledge.docs')
              ),
              h(
                'a',
                { class: 'text-primary cursor-pointer', onClick: () => openKbEdit(row) },
                t('ai.common.edit')
              ),
              h(
                'a',
                { class: 'text-danger cursor-pointer', onClick: () => removeKb(row) },
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
    searchForm.value = { kbName: '' }
    handleSearch()
  }

  function openKbEdit(row?: AiKnowledgeBaseItem): void {
    kbForm.value = row?.kbId
      ? {
          kbId: row.kbId,
          kbName: row.kbName || '',
          description: row.description || '',
          topK: row.topK ?? 5,
          chunkSize: row.chunkSize ?? 600,
          chunkOverlap: row.chunkOverlap ?? 80,
          status: row.status || '0'
        }
      : {
          kbName: '',
          description: '',
          topK: 5,
          chunkSize: 600,
          chunkOverlap: 80,
          status: '0'
        }
    kbEditVisible.value = true
    nextTick(() => kbFormRef.value?.clearValidate())
  }

  async function saveKb(): Promise<void> {
    const valid = await kbFormRef.value?.validate().catch(() => false)
    if (!valid) return
    kbSaving.value = true
    try {
      await fetchAiKnowledgeBaseSave(kbForm.value)
      ElMessage.success(t('ai.common.saveSuccess'))
      kbEditVisible.value = false
      await refreshData()
    } catch (error) {
      handleMutationError(error, t('ai.common.saveFail'))
    } finally {
      kbSaving.value = false
    }
  }

  async function removeKb(row: AiKnowledgeBaseItem): Promise<void> {
    if (!row.kbId) return
    try {
      await ElMessageBox.confirm(
        t('ai.knowledge.deleteKbConfirm', { name: row.kbName }),
        t('common.tips'),
        { type: 'warning' }
      )
    } catch {
      return
    }
    try {
      await fetchAiKnowledgeBaseRemove(row.kbId)
      ElMessage.success(t('ai.common.deleteSuccess'))
      await refreshData()
    } catch (error) {
      handleMutationError(error, t('ai.common.deleteFail'))
    }
  }

  async function openDocs(row: AiKnowledgeBaseItem): Promise<void> {
    activeKb.value = row
    docDrawerVisible.value = true
    await loadDocs()
  }

  async function loadDocs(): Promise<void> {
    if (!activeKb.value?.kbId) return
    docLoading.value = true
    try {
      const result = await fetchAiKnowledgeDocumentList({
        kbId: activeKb.value.kbId,
        pageNum: 1,
        pageSize: 100
      })
      docList.value = result?.rows || []
    } catch {
      docList.value = []
    } finally {
      docLoading.value = false
    }
  }

  function openDocEdit(row?: AiKnowledgeDocumentItem): void {
    if (!activeKb.value?.kbId) return
    if (row?.docId) {
      fetchAiKnowledgeDocumentDetail(row.docId)
        .then((detail) => {
          docForm.value = {
            docId: detail.docId,
            kbId: activeKb.value!.kbId!,
            title: detail.title || '',
            content: detail.content || '',
            status: detail.status || '0'
          }
          docEditVisible.value = true
        })
        .catch((error) => handleMutationError(error, t('ai.knowledge.loadDocFail')))
    } else {
      docForm.value = {
        kbId: activeKb.value.kbId,
        title: '',
        content: '',
        status: '0'
      }
      docEditVisible.value = true
    }
    nextTick(() => docFormRef.value?.clearValidate())
  }

  async function saveDoc(): Promise<void> {
    const valid = await docFormRef.value?.validate().catch(() => false)
    if (!valid) return
    docSaving.value = true
    try {
      await fetchAiKnowledgeDocumentSave(docForm.value)
      ElMessage.success(t('ai.knowledge.saveDocSuccess'))
      docEditVisible.value = false
      await loadDocs()
    } catch (error) {
      handleMutationError(error, t('ai.common.saveFail'))
    } finally {
      docSaving.value = false
    }
  }

  async function removeDoc(row: AiKnowledgeDocumentItem): Promise<void> {
    if (!row.docId) return
    try {
      await ElMessageBox.confirm(
        t('ai.knowledge.deleteDocConfirm', { name: row.title }),
        t('common.tips'),
        { type: 'warning' }
      )
    } catch {
      return
    }
    try {
      await fetchAiKnowledgeDocumentRemove(row.docId)
      ElMessage.success(t('ai.common.deleteSuccess'))
      await loadDocs()
    } catch (error) {
      handleMutationError(error, t('ai.common.deleteFail'))
    }
  }

  async function reindexDoc(row: AiKnowledgeDocumentItem): Promise<void> {
    if (!row.docId) return
    try {
      await fetchAiKnowledgeDocumentReindex(row.docId)
      ElMessage.success(t('ai.knowledge.reindexSuccess'))
      await loadDocs()
    } catch (error) {
      handleMutationError(error, t('ai.knowledge.reindexFail'))
    }
  }

  function indexStatusTag(status?: string): {
    label: string
    type: 'success' | 'warning' | 'info' | 'danger'
  } {
    switch (status) {
      case 'DONE':
        return { label: t('ai.knowledge.indexed'), type: 'success' }
      case 'PROCESSING':
        return { label: t('ai.knowledge.indexing'), type: 'warning' }
      case 'PENDING':
        return { label: t('ai.knowledge.pending'), type: 'info' }
      case 'FAILED':
        return { label: t('ai.knowledge.failed'), type: 'danger' }
      default:
        return { label: status || t('ai.common.unknown'), type: 'info' }
    }
  }

  async function handleFileSelect(uploadFile: { raw?: File }): Promise<void> {
    const file = uploadFile.raw
    if (!file || !activeKb.value?.kbId) return
    fileUploading.value = true
    try {
      await fetchAiKnowledgeDocumentUpload(activeKb.value.kbId, file)
      ElMessage.success(t('ai.knowledge.uploadSuccess'))
      await loadDocs()
    } catch (error) {
      handleMutationError(error, t('ai.knowledge.uploadFail'))
    } finally {
      fileUploading.value = false
    }
  }
</script>

<style scoped lang="scss">
  .ai-knowledge-page {
    .search-card {
      margin-bottom: 12px;
    }
  }
</style>
