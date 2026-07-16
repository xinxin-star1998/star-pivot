<template>
  <div class="file-grid">
    <div
      v-for="row in data"
      :key="row.fileId"
      :class="{ 'is-selected': isSelected(row) }"
      class="file-grid__item"
      @click="emit('preview', row)"
    >
      <div class="file-grid__check" @click.stop>
        <ElCheckbox
          :model-value="isSelected(row)"
          @change="(val: boolean | string | number) => toggleSelect(row, !!val)"
        />
      </div>
      <div class="file-grid__cover">
        <ElImage
          v-if="row.mediaType === 'IMAGE' && resolveFileDisplayUrl(row)"
          :key="`grid-${row.fileId}-${resolveFileDisplayUrl(row)}`"
          :src="resolveFileDisplayUrl(row)"
          fit="cover"
          class="file-grid__img"
        />
        <ArtSvgIcon v-else :icon="getMediaTypeIcon(row.mediaType)" class="file-grid__icon" />
      </div>
      <div :title="row.fileName" class="file-grid__name">{{ row.fileName }}</div>
      <div class="file-grid__meta">{{ formatFileSize(row.fileSize) }}</div>
    </div>
    <ElEmpty v-if="!data.length && !loading" :image-size="80" description="暂无文件" />
  </div>
</template>

<script lang="ts" setup>
  import type { SysFile } from '@/api/file/types'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { formatFileSize, resolveFileDisplayUrl } from '@/utils/file/file-center'
  import { getMediaTypeIcon } from '../constants'

  const props = defineProps<{
    data: SysFile[]
    selectedRows: SysFile[]
    loading?: boolean
  }>()

  const emit = defineEmits<{
    preview: [file: SysFile]
    'update:selectedRows': [rows: SysFile[]]
  }>()

  function isSelected(row: SysFile) {
    return props.selectedRows.some((r) => r.fileId === row.fileId)
  }

  function toggleSelect(row: SysFile, checked: boolean) {
    if (checked) {
      if (!isSelected(row)) {
        emit('update:selectedRows', [...props.selectedRows, row])
      }
    } else {
      emit(
        'update:selectedRows',
        props.selectedRows.filter((r) => r.fileId !== row.fileId)
      )
    }
  }
</script>

<style lang="scss" scoped>
  .file-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 12px;
    padding: 4px 0 16px;
    min-height: 160px;
  }

  .file-grid__item {
    position: relative;
    padding: 10px;
    cursor: pointer;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 10px;
    transition: border-color 0.15s, box-shadow 0.15s;

    &:hover,
    &.is-selected {
      border-color: var(--el-color-primary-light-5);
      box-shadow: 0 2px 8px rgb(0 0 0 / 6%);
    }
  }

  .file-grid__check {
    position: absolute;
    top: 6px;
    left: 8px;
    z-index: 1;
  }

  .file-grid__cover {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 96px;
    margin-bottom: 8px;
    overflow: hidden;
    background: var(--el-fill-color-lighter);
    border-radius: 8px;
  }

  .file-grid__img {
    width: 100%;
    height: 100%;
  }

  .file-grid__icon {
    font-size: 36px;
    color: var(--el-color-primary);
  }

  .file-grid__name {
    overflow: hidden;
    font-size: 13px;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .file-grid__meta {
    margin-top: 4px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }
</style>
