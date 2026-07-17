<template>
  <ElDialog
    v-model="visible"
    destroy-on-close
    :title="t('file.moveTitle')"
    width="480px"
    @closed="reset"
  >
    <p class="move-tip">
      {{ t('file.moveTip', { count: fileIds.length }) }}
    </p>
    <ElForm label-width="88px">
      <ElFormItem :label="t('file.moveTarget')" required>
        <ElCascader
          v-model="selectedPath"
          :options="cascaderOptions"
          :props="cascaderProps"
          clearable
          filterable
          :placeholder="t('file.folderPlaceholder')"
          style="width: 100%"
        />
      </ElFormItem>
    </ElForm>
    <template #footer>
      <ElButton @click="visible = false">{{ t('common.cancel') }}</ElButton>
      <ElButton :disabled="!targetFolderId" :loading="submitting" type="primary" @click="submit">
        {{ t('file.confirmMove') }}
      </ElButton>
    </template>
  </ElDialog>
</template>

<script lang="ts" setup>
  import { moveFiles } from '@/api/file/file'
  import type { FileCategoryNode } from '@/api/file/types'
  import { mapCategoryCascaderOptions, resolveFolderIdFromPath } from '@/utils/file/folder-tree'
  import { ElMessage } from 'element-plus'
  import { computed, ref, watch } from 'vue'
  import { useI18n } from 'vue-i18n'

  const visible = defineModel<boolean>('visible', { default: false })

  const props = defineProps<{
    categories: FileCategoryNode[]
    fileIds: number[]
    excludeFolderId?: number
  }>()

  const emit = defineEmits<{
    success: [targetFolderId: number]
  }>()

  const { t } = useI18n()

  const submitting = ref(false)
  const selectedPath = ref<Array<string | number> | undefined>()

  const cascaderProps = {
    expandTrigger: 'hover' as const,
    emitPath: true,
    checkStrictly: true
  }

  const cascaderOptions = computed(() =>
    mapCategoryCascaderOptions(props.categories, props.excludeFolderId)
  )

  const targetFolderId = computed(() => resolveFolderIdFromPath(selectedPath.value))

  watch(
    () => visible.value,
    (open) => {
      if (open) {
        selectedPath.value = undefined
      }
    }
  )

  function reset() {
    selectedPath.value = undefined
  }

  async function submit() {
    const folderId = targetFolderId.value
    if (!folderId) {
      ElMessage.warning(t('file.selectTargetFolder'))
      return
    }
    if (props.fileIds.length === 0) {
      ElMessage.warning(t('file.selectMoveFiles'))
      return
    }
    submitting.value = true
    try {
      await moveFiles(props.fileIds, folderId)
      ElMessage.success(t('file.moveSuccess'))
      visible.value = false
      emit('success', folderId)
    } finally {
      submitting.value = false
    }
  }
</script>

<style scoped>
  .move-tip {
    margin: 0 0 16px;
    font-size: 13px;
    line-height: 1.6;
    color: var(--el-text-color-secondary);
  }
</style>
