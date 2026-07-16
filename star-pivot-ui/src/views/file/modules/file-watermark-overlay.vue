<!-- 文件预览区域水印 -->
<template>
  <ElWatermark
    v-if="enabled && content"
    class="file-watermark"
    :content="content"
    :font="{ fontSize, color: fontColor }"
    :rotate="rotate"
    :gap="[gapX, gapY]"
  >
    <div class="file-watermark__slot">
      <slot />
    </div>
  </ElWatermark>
  <div v-else class="file-watermark__slot">
    <slot />
  </div>
</template>

<script lang="ts" setup>
  import type { SysFileWatermark } from '@/api/file/types'
  import { computed } from 'vue'

  defineOptions({ name: 'FileWatermarkOverlay' })

  const props = defineProps<{
    config?: SysFileWatermark | null
  }>()

  const enabled = computed(() => !!props.config?.enabled)
  const content = computed(() => props.config?.content || '')
  const fontSize = computed(() => props.config?.fontSize ?? 14)
  const fontColor = computed(() => props.config?.fontColor || 'rgba(0, 0, 0, 0.12)')
  const rotate = computed(() => props.config?.rotate ?? -22)
  const gapX = computed(() => props.config?.gapX ?? 120)
  const gapY = computed(() => props.config?.gapY ?? 120)
</script>

<style lang="scss" scoped>
  .file-watermark {
    width: 100%;
    height: 100%;
  }

  .file-watermark__slot {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    min-height: inherit;
  }
</style>
