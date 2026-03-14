<template>
  <img
    v-if="displayUrl"
    :src="displayUrl"
    :class="avatarClass"
    :style="{ width: size + 'px', height: size + 'px' }"
    class="rounded-full object-cover"
    alt="头像"
  />
</template>

<script setup lang="ts">
  import { fetchGetAvatarPresignedUrl } from '@/api/user/user'

  const props = withDefaults(
    defineProps<{
      /** 头像地址（可能是 OSS 私有桶永久地址，组件内部会请求临时 URL 用于展示） */
      avatarUrl?: string
      /** 尺寸（像素） */
      size?: number
      /** 额外 class */
      avatarClass?: string
    }>(),
    {
      avatarUrl: '',
      size: 40,
      avatarClass: ''
    }
  )

  const displayUrl = ref('')

  function extractFilePathFromUrl(url: string): string | null {
    try {
      if (!url.startsWith('http://') && !url.startsWith('https://')) {
        return url
      }
      const urlObj = new URL(url)
      let pathname = urlObj.pathname
      if (pathname.startsWith('/')) {
        pathname = pathname.slice(1)
      }
      const hostname = urlObj.hostname
      const parts = pathname.split('/')
      if (parts.length === 0 || (parts.length === 1 && parts[0] === '')) {
        return null
      }
      if (hostname.includes('.oss-') || hostname.includes('.aliyuncs.com')) {
        return pathname
      }
      if (hostname.includes('localhost') || hostname.includes('127.0.0.1')) {
        if (parts.length > 1) {
          return parts.slice(1).join('/')
        }
        return pathname
      }
      if (parts.length > 1) {
        return parts.slice(1).join('/')
      }
      return pathname
    } catch {
      return url.includes('/') ? url : null
    }
  }

  async function resolveDisplayUrl(url: string) {
    if (!url || url === '') {
      displayUrl.value = ''
      return
    }
    const isOssPermanent = url.includes('aliyuncs.com') || url.includes('.oss-')
    const isMinioPermanent = url.includes('localhost') || url.includes('127.0.0.1')
    const filePath = extractFilePathFromUrl(url)
    if ((isOssPermanent || isMinioPermanent) && filePath) {
      try {
        const response = (await fetchGetAvatarPresignedUrl(filePath)) as any
        const presigned = response?.presignedUrl ?? response?.data?.presignedUrl
        displayUrl.value = presigned || ''
      } catch (e) {
        if (import.meta.env.DEV) console.error('获取头像临时链接失败：', e)
        displayUrl.value = ''
      }
    } else {
      displayUrl.value = url
    }
  }

  watch(
    () => props.avatarUrl,
    (val) => resolveDisplayUrl(val ?? ''),
    { immediate: true }
  )
</script>
