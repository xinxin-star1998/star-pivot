<template>
  <ElDialog
    v-model="visible"
    :title="t('file.versionTitle', { name: file?.fileName || '' })"
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
        <ElButton type="primary" plain>{{ t('file.versionSelectFile') }}</ElButton>
      </ElUpload>
      <ElInput
        v-model="remark"
        class="version-remark"
        clearable
        :placeholder="t('file.versionRemarkPlaceholder')"
      />
      <ElButton type="primary" :loading="uploading" :disabled="!uploadFile" @click="handleUpload">
        {{ t('file.versionUpload') }}
      </ElButton>
    </div>

    <ElTable v-loading="loading" :data="versions" size="small" max-height="400">
      <ElTableColumn :label="t('file.version')" width="100">
        <template #default="{ row }">
          <ElTag v-if="row.current" type="success" size="small">
            {{ t('file.currentVersion', { no: row.versionNo }) }}
          </ElTag>
          <span v-else>v{{ row.versionNo }}</span>
        </template>
      </ElTableColumn>
      <ElTableColumn
        prop="fileName"
        :label="t('file.fileName')"
        min-width="160"
        show-overflow-tooltip
      />
      <ElTableColumn :label="t('file.size')" width="100">
        <template #default="{ row }">{{ formatFileSize(row.fileSize) }}</template>
      </ElTableColumn>
      <ElTableColumn prop="createBy" :label="t('file.operBy')" width="100" />
      <ElTableColumn prop="createTime" :label="t('file.time')" width="168" />
      <ElTableColumn
        prop="remark"
        :label="t('common.remark')"
        min-width="100"
        show-overflow-tooltip
      />
      <ElTableColumn :label="t('common.operation')" width="140" fixed="right">
        <template #default="{ row }">
          <ElButton
            v-if="!row.current && row.versionId"
            link
            type="primary"
            @click="handleRestore(row)"
          >
            {{ t('file.restoreVersion') }}
          </ElButton>
          <ElButton
            v-if="!row.current && row.versionId"
            link
            type="danger"
            @click="handleDelete(row)"
          >
            {{ t('common.delete') }}
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
  import { useI18n } from 'vue-i18n'

  const props = defineProps<{
    modelValue: boolean
    file: SysFile | null
  }>()

  const emit = defineEmits<{
    'update:modelValue': [value: boolean]
    success: []
  }>()

  const { t } = useI18n()

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
      handleMutationError(e, t('file.versionLoadFail'))
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
      ElMessage.success(t('file.versionUploadSuccess'))
      uploadFile.value = null
      remark.value = ''
      await loadVersions()
      emit('success')
    } catch (e) {
      handleMutationError(e, t('file.versionUploadFail'))
    } finally {
      uploading.value = false
    }
  }

  async function handleRestore(row: SysFileVersion) {
    if (!props.file?.fileId || !row.versionId) return
    try {
      await ElMessageBox.confirm(
        t('file.restoreVersionConfirm', { no: row.versionNo }),
        t('file.restoreVersion'),
        { type: 'warning' }
      )
      await restoreFileVersion(props.file.fileId, row.versionId)
      ElMessage.success(t('file.restoreVersionSuccess'))
      await loadVersions()
      emit('success')
    } catch (e) {
      if (e !== 'cancel') handleMutationError(e, t('file.restoreVersionFail'))
    }
  }

  async function handleDelete(row: SysFileVersion) {
    if (!props.file?.fileId || !row.versionId) return
    try {
      await ElMessageBox.confirm(
        t('file.versionDeleteConfirm', { no: row.versionNo }),
        t('common.tips'),
        { type: 'warning' }
      )
      await deleteFileVersion(props.file.fileId, row.versionId)
      ElMessage.success(t('common.deleteSuccess'))
      await loadVersions()
    } catch (e) {
      if (e !== 'cancel') handleMutationError(e, t('common.deleteFail'))
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
