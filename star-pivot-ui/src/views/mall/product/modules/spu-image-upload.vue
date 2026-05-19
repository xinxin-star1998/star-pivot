<template>
  <div class="spu-image-upload">
    <ElUpload
      v-model:file-list="fileList"
      :action="uploadAction"
      :headers="uploadHeaders"
      list-type="picture-card"
      :multiple="multiple"
      :limit="limit"
      accept="image/*"
      :on-success="handleSuccess"
      :on-remove="handleRemove"
      :on-exceed="handleExceed"
      :before-upload="beforeUpload"
    >
      <ElIcon><Plus /></ElIcon>
    </ElUpload>
    <p v-if="hint" class="spu-image-upload__hint">{{ hint }}</p>
  </div>
</template>

<script setup lang="ts">
import {Plus} from '@element-plus/icons-vue'
import type {UploadProps, UploadUserFile} from 'element-plus'
import {ElMessage} from 'element-plus'
import {getApiBaseUrl} from '@/utils/http'
import {useUserStore} from '@/store/modules/user'

defineOptions({ name: 'SpuImageUpload' })

  const props = withDefaults(
    defineProps<{
      modelValue: string[]
      multiple?: boolean
      limit?: number
      hint?: string
    }>(),
    {
      multiple: true,
      limit: 10,
      hint: ''
    }
  )

  const emit = defineEmits<{
    (e: 'update:modelValue', value: string[]): void
  }>()

  const userStore = useUserStore()

  const uploadAction = computed(() => {
    const base = getApiBaseUrl().replace(/\/$/, '')
    const path = '/common/upload/wangeditor'
    if (!base || base === '/') return `/api${path}`
    return `${base}${path}`
  })

  const uploadHeaders = computed(() => {
    const t = userStore.accessToken
    if (!t) return {}
    return { Authorization: t.startsWith('Bearer ') ? t : `Bearer ${t}` }
  })

  const fileList = ref<UploadUserFile[]>([])

  const syncFileListFromModel = (urls: string[]) => {
    fileList.value = urls.map((url, index) => ({
      name: `image-${index}`,
      url,
      status: 'success' as const,
      uid: Date.now() + index
    }))
  }

  watch(
    () => props.modelValue,
    (urls) => {
      const next = urls ?? []
      const current = fileList.value.map((f) => f.url).filter(Boolean)
      if (next.join('|') !== current.join('|')) {
        syncFileListFromModel(next)
      }
    },
    { immediate: true }
  )

  const emitUrls = () => {
    const urls = fileList.value.map((f) => f.url).filter((u): u is string => !!u)
    emit('update:modelValue', urls)
  }

  const beforeUpload: UploadProps['beforeUpload'] = (file) => {
    if (!file.type.startsWith('image/')) {
      ElMessage.warning('只能上传图片文件')
      return false
    }
    return true
  }

  const handleSuccess: UploadProps['onSuccess'] = (
    response: { errno?: number; data?: { url?: string }; message?: string },
    uploadFile
  ) => {
    if (response?.errno === 0 && response.data?.url) {
      uploadFile.url = response.data.url
      emitUrls()
      return
    }
    ElMessage.error(response?.message || '图片上传失败')
    const idx = fileList.value.findIndex((f) => f.uid === uploadFile.uid)
    if (idx >= 0) fileList.value.splice(idx, 1)
  }

  const handleRemove = () => {
    emitUrls()
  }

  const handleExceed = () => {
    ElMessage.warning(`最多上传 ${props.limit} 张图片`)
  }
</script>

<style scoped lang="scss">
  .spu-image-upload__hint {
    margin: 8px 0 0;
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }
</style>
