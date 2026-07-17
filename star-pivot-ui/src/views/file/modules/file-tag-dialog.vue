<template>
  <ElDialog
    v-model="visible"
    :title="mode === 'manage' ? t('file.tagTitle') : t('file.tagBind')"
    width="480px"
    destroy-on-close
    @closed="emit('closed')"
  >
    <template v-if="mode === 'bind'">
      <div class="tag-hint">{{ t('file.tagBindHint', { count: fileIds.length }) }}</div>
      <ElCheckboxGroup v-model="checkedTagIds" class="tag-check-group">
        <ElCheckbox v-for="tag in tags" :key="tag.tagId" :value="tag.tagId!">
          <ElTag :color="tag.tagColor || '#409EFF'" effect="dark" size="small">
            {{ tag.tagName }}
          </ElTag>
        </ElCheckbox>
      </ElCheckboxGroup>
      <ElEmpty v-if="!tags.length" :image-size="64" :description="t('file.tagEmpty')" />
    </template>

    <template v-else>
      <div class="tag-create">
        <ElInput
          v-model="newName"
          maxlength="50"
          :placeholder="t('file.tagNamePlaceholder')"
          clearable
        />
        <ElColorPicker v-model="newColor" size="small" />
        <ElButton type="primary" :disabled="!newName.trim()" @click="handleCreate">
          {{ t('file.addTag') }}
        </ElButton>
      </div>
      <ElTable :data="tags" size="small" max-height="320">
        <ElTableColumn :label="t('file.tagName')" min-width="140">
          <template #default="{ row }">
            <ElTag :color="row.tagColor || '#409EFF'" effect="dark" size="small">
              {{ row.tagName }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn :label="t('common.operation')" width="100" align="center">
          <template #default="{ row }">
            <ElButton link type="danger" @click="handleDelete(row)">{{
              t('common.delete')
            }}</ElButton>
          </template>
        </ElTableColumn>
      </ElTable>
    </template>

    <template #footer>
      <ElButton @click="visible = false">{{ t('common.cancel') }}</ElButton>
      <ElButton v-if="mode === 'bind'" type="primary" :loading="saving" @click="handleBind">
        {{ t('common.confirm') }}
      </ElButton>
    </template>
  </ElDialog>
</template>

<script lang="ts" setup>
  import type { SysFileTag } from '@/api/file/types'
  import { bindFileTags, createFileTag, deleteFileTag, fetchFileTagList } from '@/api/file/file'
  import { handleMutationError } from '@/utils/http/mutation'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { computed, ref, watch } from 'vue'
  import { useI18n } from 'vue-i18n'

  const props = withDefaults(
    defineProps<{
      modelValue: boolean
      mode: 'manage' | 'bind'
      fileIds?: number[]
      initialTagIds?: number[]
    }>(),
    {
      fileIds: () => [],
      initialTagIds: () => []
    }
  )

  const emit = defineEmits<{
    'update:modelValue': [value: boolean]
    success: []
    closed: []
  }>()

  const { t } = useI18n()

  const visible = computed({
    get: () => props.modelValue,
    set: (v: boolean) => emit('update:modelValue', v)
  })

  const tags = ref<SysFileTag[]>([])
  const checkedTagIds = ref<number[]>([])
  const newName = ref('')
  const newColor = ref('#409EFF')
  const saving = ref(false)
  const fileIds = computed(() => props.fileIds || [])

  watch(
    () => props.modelValue,
    async (open) => {
      if (open) {
        await loadTags()
        checkedTagIds.value = [...(props.initialTagIds || [])]
        newName.value = ''
        newColor.value = '#409EFF'
      }
    }
  )

  async function loadTags() {
    tags.value = (await fetchFileTagList()) || []
  }

  async function handleCreate() {
    const name = newName.value.trim()
    if (!name) return
    try {
      await createFileTag({ tagName: name, tagColor: newColor.value })
      ElMessage.success(t('file.tagCreateSuccess'))
      newName.value = ''
      await loadTags()
      emit('success')
    } catch (e) {
      handleMutationError(e, t('file.tagCreateFail'))
    }
  }

  async function handleDelete(row: SysFileTag) {
    if (!row.tagId) return
    try {
      await ElMessageBox.confirm(
        t('file.tagDeleteConfirm', { name: row.tagName }),
        t('common.tips'),
        { type: 'warning' }
      )
      await deleteFileTag(row.tagId)
      ElMessage.success(t('common.deleteSuccess'))
      await loadTags()
      emit('success')
    } catch (e) {
      if (e !== 'cancel') handleMutationError(e, t('file.tagDeleteFail'))
    }
  }

  async function handleBind() {
    if (!fileIds.value.length) {
      ElMessage.warning(t('file.selectFilesFirst'))
      return
    }
    if (!checkedTagIds.value.length) {
      ElMessage.warning(t('file.tagRequired'))
      return
    }
    saving.value = true
    try {
      await bindFileTags(fileIds.value, checkedTagIds.value)
      ElMessage.success(t('file.tagBindSuccess'))
      visible.value = false
      emit('success')
    } catch (e) {
      handleMutationError(e, t('file.tagBindFail'))
    } finally {
      saving.value = false
    }
  }
</script>

<style lang="scss" scoped>
  .tag-hint {
    margin-bottom: 12px;
    font-size: 13px;
    color: var(--el-text-color-secondary);
  }

  .tag-check-group {
    display: flex;
    flex-direction: column;
    gap: 8px;
    max-height: 320px;
    overflow: auto;
  }

  .tag-create {
    display: flex;
    gap: 8px;
    align-items: center;
    margin-bottom: 12px;
  }
</style>
