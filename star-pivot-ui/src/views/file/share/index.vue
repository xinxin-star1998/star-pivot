<template>
  <div class="share-page">
    <div class="share-card">
      <div class="share-brand">{{ t('file.shareBrand') }}</div>

      <template v-if="meta">
        <h1 class="share-title">{{ meta.fileName }}</h1>
        <div class="share-meta">
          <span>{{ meta.mediaTypeLabel || meta.mediaType }}</span>
          <span>·</span>
          <span>{{ formatFileSize(meta.fileSize) }}</span>
        </div>

        <ElAlert
          v-if="meta.expired"
          :closable="false"
          show-icon
          :title="t('file.shareExpired')"
          type="warning"
        />

        <template v-else-if="!unlocked">
          <ElForm
            v-if="meta.hasPassword"
            class="share-form"
            autocomplete="off"
            @submit.prevent="handleUnlock"
          >
            <ElFormItem :label="t('file.sharePassword')">
              <div class="access-code-field">
                <ElInput
                  v-model="password"
                  name="share_unlock_code"
                  autocomplete="off"
                  type="text"
                  :readonly="accessCodeReadonly"
                  :class="{ 'is-masked': !showAccessCode }"
                  :placeholder="t('common.pleaseInput')"
                  @focus="accessCodeReadonly = false"
                  @keyup.enter="handleUnlock"
                />
                <ElButton link type="primary" @click="showAccessCode = !showAccessCode">
                  {{ showAccessCode ? t('file.hide') : t('file.show') }}
                </ElButton>
              </div>
            </ElFormItem>
            <ElButton :loading="loading" type="primary" @click="handleUnlock">
              {{ t('file.viewFile') }}
            </ElButton>
          </ElForm>
          <ElButton v-else :loading="loading" type="primary" @click="handleUnlock">
            {{ t('file.openFile') }}
          </ElButton>
        </template>

        <template v-else>
          <div v-if="previewSrc" class="share-preview">
            <FileWatermarkOverlay :config="meta?.watermark">
              <img
                v-if="mode === 'image'"
                :src="previewSrc"
                alt="preview"
                class="share-preview__img"
              />
              <video
                v-else-if="mode === 'video'"
                :src="previewSrc"
                class="share-preview__media"
                controls
              />
              <audio
                v-else-if="mode === 'audio'"
                :src="previewSrc"
                class="share-preview__audio"
                controls
              />
              <iframe
                v-else-if="mode === 'pdf' || mode === 'office'"
                :src="previewSrc"
                class="share-preview__frame"
                title="preview"
              />
              <div v-else class="share-preview__fallback">{{ t('file.previewFallback') }}</div>
            </FileWatermarkOverlay>
          </div>
          <ElButton
            v-if="meta.allowDownload && unlockedUrl"
            type="primary"
            @click="openFileUrl(unlockedUrl, meta.fileName)"
          >
            {{ t('file.downloadFile') }}
          </ElButton>
        </template>
      </template>

      <ElEmpty v-else-if="!loading" :description="t('file.shareNotFound')" />
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { fetchShareMeta, unlockShare } from '@/api/file/share'
  import type { SysFileSharePublic } from '@/api/file/types'
  import {
    buildOfficeViewerUrl,
    formatFileSize,
    getPreviewMode,
    openFileUrl
  } from '@/utils/file/file-center'
  import FileWatermarkOverlay from '../modules/file-watermark-overlay.vue'
  import { ElMessage } from 'element-plus'
  import { computed, onMounted, ref } from 'vue'
  import { useRoute } from 'vue-router'
  import { useI18n } from 'vue-i18n'

  const route = useRoute()
  const { t } = useI18n()
  const loading = ref(false)
  const password = ref('')
  const showAccessCode = ref(false)
  const accessCodeReadonly = ref(true)
  const meta = ref<SysFileSharePublic | null>(null)
  const unlockedUrl = ref('')
  const viewerUrl = ref('')

  const shareCode = computed(() => String(route.params.code || ''))

  const unlocked = computed(() => !!unlockedUrl.value || !!meta.value?.unlocked)

  const mode = computed(() => getPreviewMode(meta.value?.mediaType, meta.value?.fileExt))

  const previewSrc = computed(() => {
    if (mode.value === 'office') {
      return viewerUrl.value || (unlockedUrl.value ? buildOfficeViewerUrl(unlockedUrl.value) : '')
    }
    if (
      mode.value === 'pdf' ||
      mode.value === 'image' ||
      mode.value === 'video' ||
      mode.value === 'audio'
    ) {
      return viewerUrl.value || unlockedUrl.value
    }
    return ''
  })

  onMounted(async () => {
    if (!shareCode.value) return
    loading.value = true
    try {
      meta.value = await fetchShareMeta(shareCode.value)
      if (meta.value && !meta.value.hasPassword && !meta.value.expired) {
        await handleUnlock()
      }
    } catch {
      meta.value = null
    } finally {
      loading.value = false
    }
  })

  async function handleUnlock() {
    if (!shareCode.value) return
    loading.value = true
    try {
      const res = await unlockShare(shareCode.value, password.value || undefined)
      meta.value = res
      unlockedUrl.value = res.url || ''
      viewerUrl.value = res.viewerUrl || ''
    } catch {
      ElMessage.error(t('file.loadFail'))
    } finally {
      loading.value = false
    }
  }
</script>

<style lang="scss" scoped>
  .share-page {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 100vh;
    padding: 24px;
    background:
      radial-gradient(circle at 20% 20%, rgb(64 158 255 / 12%), transparent 40%),
      radial-gradient(circle at 80% 0%, rgb(103 194 58 / 10%), transparent 35%), #f5f7fa;
  }

  .share-card {
    width: min(720px, 100%);
    padding: 28px;
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 12px 40px rgb(0 0 0 / 8%);
  }

  .share-brand {
    margin-bottom: 12px;
    font-size: 13px;
    font-weight: 600;
    color: var(--el-color-primary);
    letter-spacing: 0.04em;
  }

  .share-title {
    margin: 0 0 8px;
    font-size: 22px;
    font-weight: 700;
    word-break: break-all;
  }

  .share-meta {
    display: flex;
    gap: 8px;
    margin-bottom: 20px;
    font-size: 13px;
    color: var(--el-text-color-secondary);
  }

  .share-form {
    margin-top: 12px;
  }

  .access-code-field {
    display: flex;
    gap: 8px;
    align-items: center;
    width: 100%;

    :deep(.el-input) {
      flex: 1;
    }

    :deep(.is-masked input) {
      -webkit-text-security: disc;
    }
  }

  .share-preview {
    margin: 16px 0 20px;
    overflow: hidden;
    background: #f7f8fa;
    border-radius: 12px;
    min-height: 120px;

    &__img {
      display: block;
      max-width: 100%;
      max-height: 420px;
      margin: 0 auto;
    }

    &__media {
      width: 100%;
      max-height: 420px;
      background: #000;
    }

    &__audio {
      width: 100%;
      padding: 24px;
    }

    &__frame {
      width: 100%;
      height: 480px;
      border: none;
      background: #fff;
    }

    &__fallback {
      padding: 48px 16px;
      color: var(--el-text-color-secondary);
      text-align: center;
    }
  }
</style>
