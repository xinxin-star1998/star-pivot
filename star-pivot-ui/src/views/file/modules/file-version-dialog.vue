<template>
  <ElDialog
    v-model="visible"
    :title="`版本历史 - ${file?.fileName || ''}`"
    width="720px"
    destroy-on-close
    @open="loadVersions"
  >
    <div class="version-upload">
      <ElUpload
        :auto-upload="false"
        :limit="1"
        :show-file-list="true"
        :on-change="onFileChange"
        :on-remove="() => (uploadFile = null)"
      >
        <ElButton type="primary" plain>选择新版本文件</ElButton>
      </ElUpload>
      <ElInput v-model="remark" class="version-remark" clearable placeholder="备注（可选）" />
      <ElButton type="primary" :loading="uploading" :disabled="!uploadFile" @click="handleUpload">
        上传新版本
      </ElButton>
    </div>

    <ElTable v-loading="loading" :data="versions" size="small" max-height="400">
      <ElTableColumn label="版本" width="100">
        <template #default="{ row }">
          <ElTag v-if="row.current" type="success" size="small">当前 v{{ row.versionNo }}</ElTag>
          <span v-else>v{{ row.versionNo }}</span>
        </template>
      </ElTableColumn>
      <ElTableColumn prop="fileName" label="文件名" min-width="160" show-overflow-tooltip />
      <ElTableColumn label="大小" width="100">
        <template #default="{ row }">{{ formatFileSize(row.fileSize) }}</template>
      </ElTableColumn>
      <ElTableColumn prop="createBy" label="操作人" width="100" />
      <ElTableColumn prop="createTime" label="时间" width="168" />
      <ElTableColumn prop="remark" label="备注" min-width="100" show-overflow-tooltip />
      <ElTableColumn label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <ElButton
            v-if="!row.current && row.versionId"
            link
            type="primary"
            @click="handleRestore(row)"
          >
            恢复
          </ElButton>
          <ElButton
            v-if="!row.current && row.versionId"
            link
            type="danger"
            @click="handleDelete(row)"
          >
            删除
          </ElButton>
        </template>
      </ElTableColumn>
    </ElTable>
  </ElDialog>
</template>

<script lang="ts" setup>
  import type { SysFile, SysFileVersion } from '@/api/file/types'
  import {
    deleteFileVersion,
    fetchFileVersions,
    restoreFileVersion,
    uploadFileVersion
  } from '@/api/file/file'
  import { formatFileSize } from '@/utils/file/file-center'
  import { handleMutationError } from '@/utils/http/mutation'
  import { ElMessage, ElMessageBox, type UploadFile } from 'element-plus'
  import { computed, ref } from 'vue'

  const props = defineProps<{
    modelValue: boolean
    file: SysFile | null
  }>()

  const emit = defineEmits<{
    'update:modelValue': [value: boolean]
    success: []
  }>()

  const visible = computed({
    get: () => props.modelValue,
    set: (v: boolean) => emit('update:modelValue', v)
  })

  const loading = ref(false)
  const uploading = ref(false)
  const versions = ref<SysFileVersion[]>([])
  const uploadFile = ref<File | null>(null)
  const remark = ref('')

  async function loadVersions() {
    if (!props.file?.fileId) return
    loading.value = true
    try {
      versions.value = (await fetchFileVersions(props.file.fileId)) || []
    } catch (e) {
      handleMutationError(e, '加载版本失败')
    } finally {
      loading.value = false
    }
  }

  function onFileChange(file: UploadFile) {
    uploadFile.value = (file.raw as File) || null
  }

  async function handleUpload() {
    if (!props.file?.fileId || !uploadFile.value) return
    uploading.value = true
    try {
      const form = new FormData()
      form.append('file', uploadFile.value)
      if (remark.value.trim()) form.append('remark', remark.value.trim())
      await uploadFileVersion(props.file.fileId, form)
      ElMessage.success('新版本已上传')
      uploadFile.value = null
      remark.value = ''
      await loadVersions()
      emit('success')
    } catch (e) {
      handleMutationError(e, '上传新版本失败')
    } finally {
      uploading.value = false
    }
  }

  async function handleRestore(row: SysFileVersion) {
    if (!props.file?.fileId || !row.versionId) return
    try {
      await ElMessageBox.confirm(`确认恢复到 v${row.versionNo}？当前内容将自动归档。`, '恢复版本', {
        type: 'warning'
      })
      await restoreFileVersion(props.file.fileId, row.versionId)
      ElMessage.success('已恢复')
      await loadVersions()
      emit('success')
    } catch (e) {
      if (e !== 'cancel') handleMutationError(e, '恢复失败')
    }
  }

  async function handleDelete(row: SysFileVersion) {
    if (!props.file?.fileId || !row.versionId) return
    try {
      await ElMessageBox.confirm(`确认删除历史版本 v${row.versionNo}？`, '提示', { type: 'warning' })
      await deleteFileVersion(props.file.fileId, row.versionId)
      ElMessage.success('已删除')
      await loadVersions()
    } catch (e) {
      if (e !== 'cancel') handleMutationError(e, '删除失败')
    }
  }
</script>

<style lang="scss" scoped>
  .version-upload {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    align-items: flex-start;
    margin-bottom: 16px;
  }

  .version-remark {
    width: 220px;
  }
</style>
