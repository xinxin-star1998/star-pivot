<template>
  <ElDrawer
    v-model="visible"
    :close-on-click-modal="true"
    class="file-preview-drawer"
    destroy-on-close
    direction="rtl"
    size="560px"
    @closed="reset"
  >
    <template #header>
      <div class="preview-header">
        <div class="preview-header__main">
          <ArtSvgIcon :icon="fileIcon" class="preview-header__type-icon" />
          <span :title="detail?.fileName" class="preview-header__title">{{ shortFileName }}</span>
          <ElTag v-if="detail?.mediaTypeLabel" :type="mediaTagType" effect="light" size="small">
            {{ detail.mediaTypeLabel }}
          </ElTag>
        </div>
        <div v-if="detail?.fileName" :title="detail.fileName" class="preview-header__sub">
          {{ detail.fileName }}
        </div>
      </div>
    </template>

    <div v-loading="loading" class="drawer-body">
      <div class="preview-body">
        <FileWatermarkOverlay :config="watermark">
          <template v-if="previewUrl">
            <img
              v-if="mode === 'image'"
              :src="previewUrl"
              alt="preview"
              class="preview-image"
              @load="onImageLoad"
            />
            <video v-else-if="mode === 'video'" :src="previewUrl" class="preview-media" controls />
            <audio v-else-if="mode === 'audio'" :src="previewUrl" class="preview-audio" controls />
            <iframe
              v-else-if="mode === 'pdf' || mode === 'office'"
              :src="officeSrc"
              class="preview-pdf"
              title="preview"
            />
            <div v-else class="preview-fallback">
              <ArtSvgIcon :icon="fileIcon" class="preview-fallback__icon" />
              <p>{{ t('file.previewUnsupported') }}</p>
            </div>
          </template>
        </FileWatermarkOverlay>
      </div>

      <div v-if="detail" class="info-grid">
        <div class="info-card">
          <span class="info-card__label">STORAGE</span>
          <span class="info-card__value">{{ storageLabel }}</span>
        </div>
        <div class="info-card">
          <span class="info-card__label">SIZE</span>
          <span class="info-card__value">{{ formatFileSize(detail.fileSize) }}</span>
        </div>
        <div class="info-card">
          <span class="info-card__label">KIND</span>
          <span class="info-card__value">{{ detail.mediaType || '-' }}</span>
        </div>
        <div class="info-card">
          <span class="info-card__label">FOLDER</span>
          <span :title="folderDisplay" class="info-card__value">{{ folderDisplay }}</span>
        </div>
      </div>

      <div v-if="detail" class="meta-section">
        <div class="meta-section__title">{{ t('file.metaTitle') }}</div>
        <dl class="meta-list">
          <div v-if="detail.fileId" class="meta-row">
            <dt>{{ t('file.metaFileId') }}</dt>
            <dd>{{ detail.fileId }}</dd>
          </div>
          <div v-if="detail.contentType" class="meta-row">
            <dt>MIME</dt>
            <dd>{{ detail.contentType }}</dd>
          </div>
          <div v-if="detail.fileExt" class="meta-row">
            <dt>{{ t('file.metaExt') }}</dt>
            <dd>.{{ detail.fileExt }}</dd>
          </div>
          <div v-if="imageSize" class="meta-row">
            <dt>{{ t('file.metaDimension') }}</dt>
            <dd>{{ imageSize }}</dd>
          </div>
          <div v-if="detail.categoryLabel" class="meta-row">
            <dt>{{ t('file.category') }}</dt>
            <dd>{{ detail.categoryLabel }}</dd>
          </div>
          <div v-if="detail.objectName" class="meta-row">
            <dt>{{ t('file.metaObjectKey') }}</dt>
            <dd class="meta-row__mono">
              <span :title="detail.objectName">{{ detail.objectName }}</span>
              <ElButton
                link
                type="primary"
                @click="copyText(detail.objectName, t('file.metaObjectKey'))"
              >
                {{ t('file.copy') }}
              </ElButton>
            </dd>
          </div>
          <div v-if="detail.bizType || detail.bizId" class="meta-row">
            <dt>{{ t('file.metaBizRef') }}</dt>
            <dd>{{ bizRef }}</dd>
          </div>
          <div v-if="detail.createBy" class="meta-row">
            <dt>{{ t('file.uploader') }}</dt>
            <dd>{{ detail.createBy }}</dd>
          </div>
          <div v-if="detail.createTime" class="meta-row">
            <dt>{{ t('file.uploadTime') }}</dt>
            <dd>{{ formatDateTime(detail.createTime) }}</dd>
          </div>
          <div v-if="detail.updateTime" class="meta-row">
            <dt>{{ t('file.updateTime') }}</dt>
            <dd>{{ formatDateTime(detail.updateTime) }}</dd>
          </div>
          <div v-if="detail.remark" class="meta-row">
            <dt>{{ t('common.remark') }}</dt>
            <dd>{{ detail.remark }}</dd>
          </div>
        </dl>
      </div>
    </div>

    <template #footer>
      <div class="preview-footer">
        <ElButton v-if="showShare" @click="shareVisible = true">{{ t('file.share') }}</ElButton>
        <ElButton v-if="showVersion" @click="versionVisible = true">{{
          t('file.version')
        }}</ElButton>
        <ElButton v-if="showRename" @click="handleRename">{{ t('file.rename') }}</ElButton>
        <ElButton v-if="showMove" @click="handleMove">{{ t('file.move') }}</ElButton>
        <ElButton :disabled="!previewUrl" type="primary" @click="download">{{
          t('file.download')
        }}</ElButton>
        <ElButton :disabled="!previewUrl" @click="copyPreviewLink">{{
          t('file.copyLink')
        }}</ElButton>
        <ElButton v-if="showDelete" plain type="danger" @click="handleDelete">{{
          t('common.delete')
        }}</ElButton>
      </div>
    </template>
  </ElDrawer>

  <FileShareDialog v-model:visible="shareVisible" :file="detail" />
  <FileVersionDialog v-model="versionVisible" :file="detail" @success="emit('renamed')" />
</template>

<script lang="ts" setup>
  import {
    downloadFileWatermarked,
    fetchFileDetail,
    fetchFilePreviewUrl,
    fetchFileWatermarkConfig,
    renameFile
  } from '@/api/file/file'
  import type { SysFile, SysFileWatermark } from '@/api/file/types'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { useAuth } from '@/hooks/core/useAuth'
  import {
    buildOfficeViewerUrl,
    formatFileSize,
    getPreviewMode,
    openFileUrl,
    type PreviewMode
  } from '@/utils/file/file-center'
  import { getMediaTypeIcon, MEDIA_TYPE_TAG } from '../constants'
  import FileShareDialog from './file-share-dialog.vue'
  import FileVersionDialog from './file-version-dialog.vue'
  import FileWatermarkOverlay from './file-watermark-overlay.vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { computed, ref, watch } from 'vue'
  import { useI18n } from 'vue-i18n'

  const visible = defineModel<boolean>('visible', { default: false })

  const props = defineProps<{
    file?: SysFile | null
  }>()

  const emit = defineEmits<{
    delete: [fileId: number]
    move: [fileIds: number[]]
    renamed: []
  }>()

  const { hasAuth } = useAuth()
  const { t } = useI18n()

  const loading = ref(false)
  const previewUrl = ref('')
  const viewerUrl = ref('')
  const detail = ref<SysFile | null>(null)
  const imageSize = ref('')
  const shareVisible = ref(false)
  const versionVisible = ref(false)
  const watermark = ref<SysFileWatermark | null>(null)

  const mode = computed<PreviewMode>(() =>
    getPreviewMode(detail.value?.mediaType, detail.value?.fileExt)
  )

  const officeSrc = computed(() => {
    if (mode.value === 'pdf') return previewUrl.value
    if (mode.value === 'office') {
      return viewerUrl.value || (previewUrl.value ? buildOfficeViewerUrl(previewUrl.value) : '')
    }
    return ''
  })

  const fileIcon = computed(() => getMediaTypeIcon(detail.value?.mediaType))

  const mediaTagType = computed(
    () =>
      (MEDIA_TYPE_TAG[detail.value?.mediaType || ''] || 'info') as
        | ''
        | 'success'
        | 'warning'
        | 'info'
        | 'primary'
        | 'danger'
  )

  const shortFileName = computed(() => {
    const name = detail.value?.fileName || t('file.previewTitle')
    return name.length > 22 ? `${name.slice(0, 20)}...` : name
  })

  const storageLabel = computed(() => {
    const p = detail.value?.storageProvider?.toUpperCase()
    if (p === 'OSS') return t('file.storageOss')
    if (p === 'LOCAL') return t('file.storageLocal')
    return detail.value?.storageProvider || '-'
  })

  const folderDisplay = computed(() => {
    const cat = detail.value?.categoryLabel
    const folder = detail.value?.folderName
    if (cat && folder) return `${cat} / ${folder}`
    return folder || cat || '-'
  })

  const bizRef = computed(() => {
    const type = detail.value?.bizType
    const id = detail.value?.bizId
    if (type && id) return `${type} #${id}`
    return type || id || '-'
  })

  const showDelete = computed(() => hasAuth('file:resource:delete') && !!detail.value?.fileId)

  const showMove = computed(() => hasAuth('file:resource:move') && !!detail.value?.fileId)

  const showRename = computed(() => hasAuth('file:resource:edit') && !!detail.value?.fileId)

  const showShare = computed(() => hasAuth('file:resource:share') && !!detail.value?.fileId)

  const showVersion = computed(() => hasAuth('file:resource:version') && !!detail.value?.fileId)

  watch(
    () => [visible.value, props.file?.fileId] as const,
    async ([open, fileId]) => {
      if (!open || !fileId) return
      loading.value = true
      previewUrl.value = ''
      viewerUrl.value = ''
      detail.value = props.file ? { ...props.file } : null
      imageSize.value = ''
      try {
        const [fileDetail, urlRes, wm] = await Promise.all([
          fetchFileDetail(fileId),
          fetchFilePreviewUrl(fileId),
          fetchFileWatermarkConfig().catch(() => null)
        ])
        detail.value = fileDetail
        previewUrl.value = urlRes.url
        viewerUrl.value = (urlRes as { viewerUrl?: string }).viewerUrl || ''
        watermark.value = wm
      } catch {
        previewUrl.value = props.file?.displayUrl || ''
      } finally {
        loading.value = false
      }
    }
  )

  function onImageLoad(e: Event) {
    const img = e.target as HTMLImageElement
    if (img.naturalWidth && img.naturalHeight) {
      imageSize.value = `${img.naturalWidth} × ${img.naturalHeight}`
    }
  }

  function formatDateTime(value: string) {
    const d = new Date(value.replace(/-/g, '/'))
    if (Number.isNaN(d.getTime())) return value
    return d.toLocaleString('zh-CN', { hour12: false })
  }

  async function copyText(text: string, label: string) {
    try {
      await navigator.clipboard.writeText(text)
      ElMessage.success(t('file.copySuccess'))
    } catch {
      ElMessage.error(t('file.copyFail'))
    }
  }

  function copyPreviewLink() {
    if (previewUrl.value) {
      copyText(previewUrl.value, t('file.accessLink'))
    }
  }

  function download() {
    const fileId = detail.value?.fileId
    if (watermark.value?.downloadEnabled && mode.value === 'image' && fileId) {
      downloadFileWatermarked(fileId, detail.value?.fileName).catch(() => {
        ElMessage.error(t('file.loadFail'))
      })
      return
    }
    if (previewUrl.value) {
      openFileUrl(previewUrl.value, detail.value?.fileName)
    }
  }

  async function handleDelete() {
    const fileId = detail.value?.fileId
    if (!fileId) return
    await ElMessageBox.confirm(t('file.deleteFileConfirm'), t('common.tips'), { type: 'warning' })
    emit('delete', fileId)
    visible.value = false
  }

  function handleMove() {
    const fileId = detail.value?.fileId
    if (!fileId) return
    emit('move', [fileId])
    visible.value = false
  }

  async function handleRename() {
    const fileId = detail.value?.fileId
    if (!fileId) return
    const { value } = await ElMessageBox.prompt(t('file.renamePrompt'), t('file.rename'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      inputValue: detail.value?.fileName || '',
      inputValidator: (val) => {
        const name = val?.trim()
        if (!name) return t('file.nameEmpty')
        if (name.includes('/') || name.includes('\\')) return t('file.nameInvalid')
        return true
      }
    })
    await renameFile({ fileId, fileName: value.trim() })
    ElMessage.success(t('file.renameSuccess'))
    detail.value = await fetchFileDetail(fileId)
    emit('renamed')
  }

  function reset() {
    previewUrl.value = ''
    detail.value = null
    imageSize.value = ''
    watermark.value = null
  }
</script>

<style lang="scss" scoped>
  .preview-header {
    &__main {
      display: flex;
      gap: 8px;
      align-items: center;
      min-width: 0;
    }

    &__type-icon {
      flex-shrink: 0;
      font-size: 20px;
      color: var(--el-color-primary);
    }

    &__title {
      overflow: hidden;
      font-size: 16px;
      font-weight: 600;
      color: var(--el-text-color-primary);
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    &__sub {
      padding-left: 28px;
      margin-top: 6px;
      overflow: hidden;
      font-size: 12px;
      color: var(--el-text-color-secondary);
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .drawer-body {
    display: flex;
    flex-direction: column;
    gap: 16px;
    min-height: 100%;
    padding-bottom: 8px;
  }

  .preview-body {
    display: flex;
    flex-shrink: 0;
    align-items: center;
    justify-content: center;
    min-height: 180px;
    overflow: hidden;
    background: var(--el-fill-color-lighter);
    border-radius: 10px;
  }

  .preview-image {
    display: block;
    max-width: 100%;
    max-height: min(42vh, 360px);
    object-fit: contain;
  }

  .preview-media {
    width: 100%;
    max-height: min(42vh, 360px);
    background: #000;
  }

  .preview-audio {
    width: 100%;
    padding: 24px 16px;
  }

  .preview-pdf {
    width: 100%;
    height: min(50vh, 420px);
    background: #fff;
    border: none;
  }

  .preview-fallback {
    padding: 40px 16px;
    color: var(--el-text-color-secondary);
    text-align: center;

    &__icon {
      margin-bottom: 8px;
      font-size: 48px;
      opacity: 0.5;
    }

    p {
      margin: 0;
      font-size: 13px;
    }
  }

  .info-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 10px;
  }

  .info-card {
    padding: 12px 14px;
    background: var(--el-fill-color-blank);
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 8px;

    &__label {
      display: block;
      margin-bottom: 6px;
      font-size: 11px;
      font-weight: 600;
      color: var(--el-text-color-placeholder);
      letter-spacing: 0.04em;
    }

    &__value {
      display: block;
      overflow: hidden;
      font-size: 13px;
      font-weight: 500;
      color: var(--el-text-color-primary);
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .meta-section {
    &__title {
      margin-bottom: 10px;
      font-size: 13px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }

  .meta-list {
    margin: 0;
    overflow: hidden;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 8px;
  }

  .meta-row {
    display: grid;
    grid-template-columns: 80px 1fr;
    gap: 10px;
    padding: 10px 12px;
    font-size: 13px;
    border-bottom: 1px solid var(--el-border-color-lighter);

    &:last-child {
      border-bottom: none;
    }

    dt {
      margin: 0;
      color: var(--el-text-color-secondary);
    }

    dd {
      margin: 0;
      color: var(--el-text-color-primary);
      word-break: break-all;
    }

    &__mono {
      display: flex;
      gap: 8px;
      align-items: flex-start;
      font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
      font-size: 12px;
      line-height: 1.5;

      span {
        flex: 1;
      }
    }
  }

  .preview-footer {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    justify-content: flex-end;
    width: 100%;
  }
</style>

<style lang="scss">
  .file-preview-drawer {
    .el-drawer__header {
      padding-bottom: 16px;
      margin-bottom: 0;
      border-bottom: 1px solid var(--el-border-color-lighter);
    }

    .el-drawer__body {
      padding-top: 16px;
      overflow-y: auto;
    }

    .el-drawer__footer {
      padding-top: 12px;
      border-top: 1px solid var(--el-border-color-lighter);
    }
  }
</style>
