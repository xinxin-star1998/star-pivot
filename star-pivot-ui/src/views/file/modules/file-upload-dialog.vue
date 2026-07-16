<template>
  <ElDialog
    v-model="visible"
    destroy-on-close
    title="上传文件"
    width="540px"
    @closed="onDialogClosed"
  >
    <ElForm class="upload-form" label-width="88px">
      <ElFormItem label="目标文件夹" required>
        <ElCascader
          v-model="selectedPath"
          :options="cascaderOptions"
          :props="cascaderProps"
          clearable
          filterable
          placeholder="请选择业务分类与文件夹"
          style="width: 100%"
        />
      </ElFormItem>
      <ElFormItem label="备注">
        <ElInput v-model="remark" :rows="2" placeholder="可选" type="textarea" />
      </ElFormItem>
    </ElForm>

    <ElUpload
      ref="uploadRef"
      v-model:file-list="fileList"
      :auto-upload="false"
      :disabled="uploading"
      :limit="20"
      :on-change="handleChange"
      :on-exceed="handleExceed"
      :on-remove="handleRemove"
      drag
      multiple
    >
      <ArtSvgIcon class="upload-icon" icon="ri:upload-cloud-2-line" />
      <div class="el-upload__text">拖拽文件到此处，或 <em>点击选择</em></div>
      <template #tip>
        <div class="upload-tip">
          单次最多 20 个文件；大文件自动分片上传，支持秒传与断点续传
        </div>
      </template>
    </ElUpload>

    <ElProgress
      v-if="uploading && uploadTotal > 0"
      :percentage="uploadProgress"
      :stroke-width="8"
      class="upload-progress"
    />
    <div v-if="uploading && currentFileName" class="upload-status">
      正在处理：{{ currentFileName }}
    </div>

    <template #footer>
      <ElButton :disabled="uploading" @click="visible = false">取消</ElButton>
      <ElButton :disabled="!targetFolderId" :loading="uploading" type="primary" @click="submit">
        {{ uploading ? `上传中 ${uploadDone}/${uploadTotal}` : '开始上传' }}
      </ElButton>
    </template>
  </ElDialog>
</template>

<script lang="ts" setup>
  import {
    checkFileHash,
    completeMultipartUpload,
    fetchMultipartStatus,
    initMultipartUpload,
    uploadFile,
    uploadMultipartPart
  } from '@/api/file/file'
  import type { FileCategoryNode } from '@/api/file/types'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { computeFileSha256, DEFAULT_MULTIPART_THRESHOLD } from '@/utils/file/file-hash'
  import {
    findFolderInTree,
    mapCategoryCascaderOptions,
    resolveFolderIdFromPath
  } from '@/utils/file/folder-tree'
  import type { UploadFile, UploadInstance } from 'element-plus'
  import { ElMessage } from 'element-plus'
  import { computed, ref, watch } from 'vue'

  const visible = defineModel<boolean>('visible', { default: false })

  const props = defineProps<{
    categories: FileCategoryNode[]
    defaultFolderId?: number
    /** 外部拖入预填的本地文件 */
    seedFiles?: File[]
  }>()

  const emit = defineEmits<{
    success: [folderId: number]
    closed: []
  }>()

  const uploadRef = ref<UploadInstance>()
  const fileList = ref<UploadFile[]>([])
  const remark = ref('')
  const uploading = ref(false)
  const uploadDone = ref(0)
  const uploadTotal = ref(0)
  const byteDone = ref(0)
  const byteTotal = ref(0)
  const currentFileName = ref('')
  const selectedPath = ref<Array<string | number> | undefined>()

  const cascaderProps = {
    expandTrigger: 'hover' as const,
    emitPath: true,
    checkStrictly: true
  }

  const cascaderOptions = computed(() => mapCategoryCascaderOptions(props.categories))

  const targetFolderId = computed(() => resolveFolderIdFromPath(selectedPath.value))

  const uploadProgress = computed(() => {
    if (byteTotal.value > 0) {
      return Math.min(100, Math.round((byteDone.value / byteTotal.value) * 100))
    }
    return uploadTotal.value ? Math.round((uploadDone.value / uploadTotal.value) * 100) : 0
  })

  function resolvePathByFolderId(folderId?: number): Array<string | number> | undefined {
    if (!folderId) return undefined
    const found = findFolderInTree(props.categories, folderId)
    if (!found) return undefined
    // Cascader 需要完整 value path：分类 + 各级 folderId
    const path: Array<string | number> = [found.category]
    const walk = (
      folders: typeof props.categories[0]['children'],
      target: number,
      acc: number[]
    ): boolean => {
      for (const f of folders || []) {
        const next = [...acc, f.folderId!]
        if (f.folderId === target) {
          path.push(...next)
          return true
        }
        if (walk(f.children, target, next)) return true
      }
      return false
    }
    walk(props.categories.find((c) => c.category === found.category)?.children, folderId, [])
    return path.length > 1 ? path : undefined
  }

  watch(
    () => visible.value,
    (open) => {
      if (open) {
        selectedPath.value = resolvePathByFolderId(props.defaultFolderId)
        applySeedFiles(props.seedFiles)
      }
    }
  )

  watch(
    () => props.seedFiles,
    (files) => {
      if (visible.value) applySeedFiles(files)
    }
  )

  function applySeedFiles(files?: File[]) {
    if (!files?.length) return
    fileList.value = files.map((raw, idx) => ({
      name: raw.name,
      size: raw.size,
      status: 'ready',
      uid: Date.now() + idx,
      raw: raw as UploadFile['raw']
    }))
  }

  function onDialogClosed() {
    reset()
    emit('closed')
  }

  function handleChange(_file: UploadFile, files: UploadFile[]) {
    fileList.value = files
  }

  function handleRemove(_file: UploadFile, files: UploadFile[]) {
    fileList.value = files
  }

  function handleExceed() {
    ElMessage.warning('单次最多上传 20 个文件')
  }

  function reset() {
    fileList.value = []
    remark.value = ''
    selectedPath.value = undefined
    uploadDone.value = 0
    uploadTotal.value = 0
    byteDone.value = 0
    byteTotal.value = 0
    currentFileName.value = ''
    uploadRef.value?.clearFiles()
  }

  async function uploadSingleFile(file: File, folderId: number) {
    currentFileName.value = file.name
    const fileHash = await computeFileSha256(file)

    if (fileHash) {
      const check = await checkFileHash({
        fileHash,
        fileSize: file.size,
        folderId,
        fileName: file.name,
        remark: remark.value || undefined
      })
      if (check.instant) {
        byteDone.value += file.size
        return
      }
    }

    if (file.size >= DEFAULT_MULTIPART_THRESHOLD) {
      await uploadByMultipart(file, folderId, fileHash)
    } else {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('folderId', String(folderId))
      if (remark.value) formData.append('remark', remark.value)
      if (fileHash) formData.append('fileHash', fileHash)
      await uploadFile(formData)
      byteDone.value += file.size
    }
  }

  async function uploadByMultipart(file: File, folderId: number, fileHash: string) {
    const resumeKey = `file-multipart:${folderId}:${fileHash || file.name}:${file.size}`
    let init = null as Awaited<ReturnType<typeof initMultipartUpload>> | null

    const cached = fileHash ? localStorage.getItem(resumeKey) : null
    if (cached) {
      try {
        const parsed = JSON.parse(cached) as {
          uploadId: string
          objectName: string
          partSize?: number
        }
        if (parsed.uploadId && parsed.objectName) {
          try {
            init = await fetchMultipartStatus(parsed.uploadId, parsed.objectName)
            init.partSize = init.partSize || parsed.partSize || DEFAULT_MULTIPART_THRESHOLD
          } catch {
            localStorage.removeItem(resumeKey)
          }
        }
      } catch {
        localStorage.removeItem(resumeKey)
      }
    }

    if (!init?.uploadId || !init?.objectName) {
      init = await initMultipartUpload({
        folderId,
        fileName: file.name,
        fileSize: file.size,
        contentType: file.type || undefined,
        fileHash: fileHash || undefined,
        remark: remark.value || undefined
      })
      if (fileHash) {
        localStorage.setItem(
          resumeKey,
          JSON.stringify({
            uploadId: init.uploadId,
            objectName: init.objectName,
            partSize: init.partSize
          })
        )
      }
    }

    const partSize = init.partSize || DEFAULT_MULTIPART_THRESHOLD
    const partMap = new Map<number, string>()
    ;(init.uploadedPartDetails || []).forEach((p) => {
      if (p.partNumber && p.etag) partMap.set(p.partNumber, p.etag)
    })

    const totalParts = Math.ceil(file.size / partSize)
    for (let i = 0; i < totalParts; i++) {
      const partNumber = i + 1
      const start = i * partSize
      const end = Math.min(file.size, start + partSize)
      const chunk = file.slice(start, end)

      if (partMap.has(partNumber)) {
        byteDone.value += chunk.size
        continue
      }

      const formData = new FormData()
      formData.append('uploadId', init.uploadId)
      formData.append('objectName', init.objectName)
      formData.append('partNumber', String(partNumber))
      formData.append('file', chunk, `${file.name}.part${partNumber}`)
      const res = await uploadMultipartPart(formData)
      partMap.set(partNumber, res.etag)
      byteDone.value += chunk.size
    }

    const parts = Array.from(partMap.entries())
      .map(([partNumber, etag]) => ({ partNumber, etag }))
      .sort((a, b) => a.partNumber - b.partNumber)

    await completeMultipartUpload({
      uploadId: init.uploadId,
      objectName: init.objectName,
      folderId,
      fileName: file.name,
      fileSize: file.size,
      contentType: file.type || undefined,
      fileHash: fileHash || undefined,
      remark: remark.value || undefined,
      parts
    })
    localStorage.removeItem(resumeKey)
  }

  async function submit() {
    const folderId = targetFolderId.value
    if (!folderId) {
      ElMessage.warning('请选择目标文件夹')
      return
    }
    const items = fileList.value.filter((f) => f.raw)
    if (items.length === 0) {
      ElMessage.warning('请选择文件')
      return
    }

    uploading.value = true
    uploadDone.value = 0
    uploadTotal.value = items.length
    byteDone.value = 0
    byteTotal.value = items.reduce((sum, item) => sum + (item.raw?.size || 0), 0)
    let failed = 0
    let instantCount = 0

    try {
      for (const item of items) {
        if (!item.raw) continue
        const before = byteDone.value
        try {
          await uploadSingleFile(item.raw, folderId)
          if (byteDone.value - before >= item.raw.size && item.raw.size > 0) {
            // 秒传也会增加 byteDone
          }
        } catch {
          failed++
        } finally {
          uploadDone.value++
        }
      }

      if (failed === 0) {
        ElMessage.success(
          instantCount > 0
            ? `成功处理 ${items.length} 个文件（含秒传）`
            : `成功上传 ${items.length} 个文件`
        )
        visible.value = false
        emit('success', folderId)
      } else if (failed < items.length) {
        ElMessage.warning(`部分上传失败：成功 ${items.length - failed}，失败 ${failed}`)
        emit('success', folderId)
      } else {
        ElMessage.error('上传失败，请重试')
      }
    } finally {
      uploading.value = false
      currentFileName.value = ''
    }
  }
</script>

<style lang="scss" scoped>
  .upload-form {
    margin-bottom: 8px;
  }

  .upload-icon {
    margin-bottom: 8px;
    font-size: 48px;
    color: var(--el-color-primary);
  }

  .upload-tip {
    font-size: 12px;
    line-height: 1.5;
    color: var(--el-text-color-secondary);
  }

  .upload-progress {
    margin-top: 16px;
  }

  .upload-status {
    margin-top: 8px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }
</style>
