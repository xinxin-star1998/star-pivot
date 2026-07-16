<template>
  <div class="file-timeline">
    <div v-for="group in groups" :key="group.label" class="file-timeline__group">
      <div class="file-timeline__label">{{ group.label }}</div>
      <div class="file-timeline__list">
        <div
          v-for="row in group.items"
          :key="row.fileId"
          class="file-timeline__row"
          @click="emit('preview', row)"
        >
          <ElCheckbox
            :model-value="isSelected(row)"
            class="file-timeline__check"
            @change="(val: boolean | string | number) => toggleSelect(row, !!val)"
            @click.stop
          />
          <div class="file-timeline__thumb">
            <ElImage
              v-if="row.mediaType === 'IMAGE' && resolveFileDisplayUrl(row)"
              :src="resolveFileDisplayUrl(row)"
              fit="cover"
              class="file-timeline__img"
            />
            <ArtSvgIcon v-else :icon="getMediaTypeIcon(row.mediaType)" class="file-timeline__icon" />
          </div>
          <div class="file-timeline__main">
            <div :title="row.fileName" class="file-timeline__name">{{ row.fileName }}</div>
            <div class="file-timeline__meta">
              {{ formatFileSize(row.fileSize) }}
              <span v-if="row.createBy"> · {{ row.createBy }}</span>
              <span v-if="row.createTime"> · {{ row.createTime }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <ElEmpty v-if="!groups.length && !loading" :image-size="80" description="暂无文件" />
  </div>
</template>

<script lang="ts" setup>
  import type { SysFile } from '@/api/file/types'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { formatFileSize, resolveFileDisplayUrl } from '@/utils/file/file-center'
  import { getMediaTypeIcon } from '../constants'
  import { computed } from 'vue'

  const props = defineProps<{
    data: SysFile[]
    selectedRows: SysFile[]
    loading?: boolean
    /** 回收站用删除时间 */
    timeField?: 'createTime' | 'deleteTime'
  }>()

  const emit = defineEmits<{
    preview: [file: SysFile]
    'update:selectedRows': [rows: SysFile[]]
  }>()

  const groups = computed(() => {
    const field = props.timeField || 'createTime'
    const buckets: Record<string, SysFile[]> = {
      今天: [],
      昨天: [],
      本周: [],
      更早: []
    }
    const now = new Date()
    const startOfToday = new Date(now.getFullYear(), now.getMonth(), now.getDate())
    const startOfYesterday = new Date(startOfToday.getTime() - 86400000)
    const startOfWeek = new Date(startOfToday.getTime() - startOfToday.getDay() * 86400000)

    for (const row of props.data) {
      const raw = row[field]
      const d = raw ? new Date(String(raw).replace(/-/g, '/')) : null
      if (!d || Number.isNaN(d.getTime())) {
        buckets['更早'].push(row)
        continue
      }
      if (d >= startOfToday) buckets['今天'].push(row)
      else if (d >= startOfYesterday) buckets['昨天'].push(row)
      else if (d >= startOfWeek) buckets['本周'].push(row)
      else buckets['更早'].push(row)
    }

    return Object.entries(buckets)
      .filter(([, items]) => items.length)
      .map(([label, items]) => ({ label, items }))
  })

  function isSelected(row: SysFile) {
    return props.selectedRows.some((r) => r.fileId === row.fileId)
  }

  function toggleSelect(row: SysFile, checked: boolean) {
    if (checked) {
      if (!isSelected(row)) emit('update:selectedRows', [...props.selectedRows, row])
    } else {
      emit(
        'update:selectedRows',
        props.selectedRows.filter((r) => r.fileId !== row.fileId)
      )
    }
  }
</script>

<style lang="scss" scoped>
  .file-timeline {
    display: flex;
    flex-direction: column;
    gap: 18px;
    padding: 4px 0 16px;
    min-height: 160px;
  }

  .file-timeline__label {
    margin-bottom: 8px;
    font-size: 13px;
    font-weight: 600;
    color: var(--el-text-color-regular);
  }

  .file-timeline__list {
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .file-timeline__row {
    display: flex;
    gap: 10px;
    align-items: center;
    padding: 8px 10px;
    cursor: pointer;
    border-radius: 8px;

    &:hover {
      background: var(--el-fill-color-light);
    }
  }

  .file-timeline__thumb {
    display: flex;
    flex-shrink: 0;
    align-items: center;
    justify-content: center;
    width: 44px;
    height: 44px;
    overflow: hidden;
    background: var(--el-fill-color-lighter);
    border-radius: 6px;
  }

  .file-timeline__img {
    width: 100%;
    height: 100%;
  }

  .file-timeline__icon {
    font-size: 20px;
    color: var(--el-color-primary);
  }

  .file-timeline__main {
    flex: 1;
    min-width: 0;
  }

  .file-timeline__name {
    overflow: hidden;
    font-size: 13px;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .file-timeline__meta {
    margin-top: 2px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }
</style>
