<template>
  <div class="art-avatar-upload">
    <div class="avatar-preview" :style="{ width: size + 'px', height: size + 'px' }">
      <img v-if="imageUrl && imageUrl !== ''" :src="imageUrl" alt="头像预览" class="avatar-img" />
      <div v-else class="avatar-placeholder">
        <i class="iconfont icon-camera"></i>
        <span>{{ placeholder }}</span>
      </div>
    </div>
    <div class="avatar-actions">
      <input
        ref="fileInput"
        type="file"
        accept="image/*"
        class="file-input"
        @change="handleFileChange"
      />
      <button type="button" class="upload-btn" @click="triggerFileInput">
        <i class="iconfont icon-upload"></i>
        {{ imageUrl ? '更换头像' : '上传头像' }}
      </button>
      <button v-if="imageUrl" type="button" class="delete-btn" @click="handleDelete">
        <i class="iconfont icon-delete"></i>
        删除头像
      </button>
    </div>
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
  </div>
</template>

<script setup lang="ts">
  import { ref, watch, onMounted } from 'vue'
  import { fetchUploadAvatar, fetchDeleteAvatar, fetchGetAvatarPresignedUrl } from '@/api/user/user'

  // Props
  const props = defineProps({
    modelValue: {
      type: String,
      default: ''
    },
    userId: {
      type: [String, Number],
      default: ''
    },
    size: {
      type: Number,
      default: 120
    },
    placeholder: {
      type: String,
      default: '点击上传头像'
    },
    // 是否自动上传到服务器，新增用户时设为false，编辑用户时设为true
    autoUpload: {
      type: Boolean,
      default: true
    },
    // 是否使用临时访问链接
    usePresignedUrl: {
      type: Boolean,
      default: false
    }
  })

  // 新增：保存当前选择的文件，用于延迟上传
  const currentFile = ref<File | null>(null)

  // Emits
  const emit = defineEmits(['update:modelValue', 'success', 'error'])

  // Refs
  const fileInput = ref<HTMLInputElement | null>(null)
  const imageUrl = ref(props.modelValue)
  const errorMessage = ref('')

  // Watch for modelValue changes
  watch(
    () => props.modelValue,
    (newVal) => {
      imageUrl.value = newVal
      // 如果设置了使用临时链接且有文件路径，自动获取临时链接
      if (props.usePresignedUrl && newVal && newVal.includes('/')) {
        // 从完整URL中提取文件路径
        const filePath = extractFilePathFromUrl(newVal)
        if (filePath) {
          getPresignedUrlForExistingAvatar(filePath)
        }
      }
    }
  )

  // Watch for usePresignedUrl changes
  watch(
    () => props.usePresignedUrl,
    (newVal) => {
      if (newVal && imageUrl.value && imageUrl.value.includes('/')) {
        // 当切换为使用临时链接时，自动获取现有头像的临时链接
        const filePath = extractFilePathFromUrl(imageUrl.value)
        if (filePath) {
          getPresignedUrlForExistingAvatar(filePath)
        }
      }
    }
  )

  // 从完整URL中提取文件路径
  const extractFilePathFromUrl = (url: string): string | null => {
    try {
      // 解析URL，提取路径部分
      const urlObj = new URL(url)
      let pathname = urlObj.pathname
      // 移除开头的斜杠
      if (pathname.startsWith('/')) {
        pathname = pathname.slice(1)
      }
      // 移除存储桶名称（假设URL格式为：http://domain/bucket/path/to/file）
      const parts = pathname.split('/')
      if (parts.length > 1) {
        // 移除第一个部分（存储桶名称），返回剩余部分
        return parts.slice(1).join('/')
      }
      return null
    } catch (e) {
      console.error('URL解析失败：', e)
      return null
    }
  }

  // 获取现有头像的临时访问链接
  const getPresignedUrlForExistingAvatar = async (filePath: string) => {
    try {
      const response = (await fetchGetAvatarPresignedUrl(filePath)) as any
      if (response && response.presignedUrl) {
        imageUrl.value = response.presignedUrl
        emit('update:modelValue', response.presignedUrl)
      }
    } catch (error: any) {
      console.error('获取临时访问链接失败：', error)
      // 获取失败时保持原有URL
    }
  }

  // 组件挂载时初始化
  onMounted(() => {
    // 如果设置了使用临时链接且有初始头像URL，自动获取临时链接
    if (props.usePresignedUrl && props.modelValue && props.modelValue.includes('/')) {
      const filePath = extractFilePathFromUrl(props.modelValue)
      if (filePath) {
        getPresignedUrlForExistingAvatar(filePath)
      }
    }
  })

  // Methods
  const triggerFileInput = () => {
    fileInput.value?.click()
  }

  // 手动上传方法，用于新增用户后调用
  const uploadImageToServer = async () => {
    if (!currentFile.value || !props.userId) {
      return Promise.resolve()
    }

    try {
      errorMessage.value = ''
      const formData = new FormData()
      formData.append('file', currentFile.value)
      formData.append('userId', String(props.userId))
      formData.append('usePresignedUrl', String(props.usePresignedUrl))

      const response = (await fetchUploadAvatar(formData)) as any
      // 使用服务器返回的URL替换本地预览URL
      imageUrl.value = response.avatarUrl
      emit('update:modelValue', response.avatarUrl)
      emit('success', response.avatarUrl)
      // 上传成功后清空临时文件
      currentFile.value = null
    } catch (uploadError: any) {
      // 上传失败时，保持本地预览，不恢复原来的头像
      errorMessage.value = uploadError.message || '上传失败，请重试'
      emit('error', uploadError)
      throw uploadError
    }
  }

  // 暴露手动上传方法给父组件
  defineExpose({
    uploadImageToServer
  })

  const handleFileChange = async (event: Event) => {
    const target = event.target as HTMLInputElement
    const file = target.files?.[0]
    if (!file) return

    try {
      errorMessage.value = ''

      // 先显示本地预览
      const reader = new FileReader()
      reader.onload = async (e) => {
        // 显示本地预览
        const localPreviewUrl = e.target?.result as string
        imageUrl.value = localPreviewUrl

        // 保存当前文件
        currentFile.value = file

        // 根据autoUpload属性决定是否立即上传到服务器
        if (props.autoUpload) {
          try {
            await uploadImageToServer()
          } catch {
            // 错误已在uploadImageToServer中处理
          }
        } else {
          // 非自动上传模式下（新增用户时），只显示本地预览，不触发服务器上传
          // 不更新modelValue，避免将临时的data URL保存到表单中
          console.log('非自动上传模式：只显示本地预览，不上传服务器')
        }
      }
      reader.readAsDataURL(file)
    } catch (error: any) {
      errorMessage.value = error.message || '预览失败，请重试'
      emit('error', error)
    } finally {
      // Reset file input
      if (target) {
        target.value = ''
      }
    }
  }

  const handleDelete = async () => {
    if (!props.userId) {
      errorMessage.value = '请先输入用户ID'
      return
    }

    try {
      errorMessage.value = ''
      await fetchDeleteAvatar(String(props.userId))
      imageUrl.value = ''
      emit('update:modelValue', '')
      emit('success', '')
    } catch (error: any) {
      errorMessage.value = error.message || '删除失败，请重试'
      emit('error', error)
    }
  }
</script>

<style scoped lang="scss">
  .art-avatar-upload {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 16px;

    .avatar-preview {
      position: relative;
      border-radius: 50%;
      overflow: hidden;
      border: 2px dashed #d9d9d9;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        border-color: #1890ff;
      }

      .avatar-img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .avatar-placeholder {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 100%;
        background-color: #f5f5f5;
        color: #999;

        i {
          font-size: 24px;
          margin-bottom: 8px;
        }

        span {
          font-size: 14px;
        }
      }
    }

    .avatar-actions {
      display: flex;
      gap: 12px;

      .file-input {
        display: none;
      }

      .upload-btn,
      .delete-btn {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 8px 16px;
        border: 1px solid #d9d9d9;
        border-radius: 4px;
        background-color: #fff;
        color: #666;
        cursor: pointer;
        font-size: 14px;
        transition: all 0.3s ease;

        &:hover {
          color: #1890ff;
          border-color: #1890ff;
        }

        i {
          font-size: 14px;
        }
      }

      .delete-btn {
        &:hover {
          color: #ff4d4f;
          border-color: #ff4d4f;
        }
      }
    }

    .error-message {
      color: #ff4d4f;
      font-size: 12px;
      margin-top: 8px;
    }
  }
</style>
