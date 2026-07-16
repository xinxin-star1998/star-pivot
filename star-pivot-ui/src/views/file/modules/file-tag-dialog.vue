<template>
  <ElDialog
    v-model="visible"
    :title="mode === 'manage' ? '标签管理' : '打标签'"
    width="480px"
    destroy-on-close
    @closed="emit('closed')"
  >
    <template v-if="mode === 'bind'">
      <div class="tag-hint">为选中的 {{ fileIds.length }} 个文件设置标签</div>
      <ElCheckboxGroup v-model="checkedTagIds" class="tag-check-group">
        <ElCheckbox v-for="tag in tags" :key="tag.tagId" :value="tag.tagId!">
          <ElTag :color="tag.tagColor || '#409EFF'" effect="dark" size="small">
            {{ tag.tagName }}
          </ElTag>
        </ElCheckbox>
      </ElCheckboxGroup>
      <ElEmpty v-if="!tags.length" :image-size="64" description="暂无标签，请先创建" />
    </template>

    <template v-else>
      <div class="tag-create">
        <ElInput v-model="newName" maxlength="50" placeholder="新标签名称" clearable />
        <ElColorPicker v-model="newColor" size="small" />
        <ElButton type="primary" :disabled="!newName.trim()" @click="handleCreate">创建</ElButton>
      </div>
      <ElTable :data="tags" size="small" max-height="320">
        <ElTableColumn label="标签" min-width="140">
          <template #default="{ row }">
            <ElTag :color="row.tagColor || '#409EFF'" effect="dark" size="small">
              {{ row.tagName }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="操作" width="100" align="center">
          <template #default="{ row }">
            <ElButton link type="danger" @click="handleDelete(row)">删除</ElButton>
          </template>
        </ElTableColumn>
      </ElTable>
    </template>

    <template #footer>
      <ElButton @click="visible = false">取消</ElButton>
      <ElButton v-if="mode === 'bind'" type="primary" :loading="saving" @click="handleBind">
        确定
      </ElButton>
    </template>
  </ElDialog>
</template>

<script lang="ts" setup>
  import type { SysFileTag } from '@/api/file/types'
  import {
    bindFileTags,
    createFileTag,
    deleteFileTag,
    fetchFileTagList
  } from '@/api/file/file'
  import { handleMutationError } from '@/utils/http/mutation'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { computed, ref, watch } from 'vue'

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
      ElMessage.success('标签已创建')
      newName.value = ''
      await loadTags()
      emit('success')
    } catch (e) {
      handleMutationError(e, '创建标签失败')
    }
  }

  async function handleDelete(row: SysFileTag) {
    if (!row.tagId) return
    try {
      await ElMessageBox.confirm(`确定删除标签「${row.tagName}」？`, '提示', { type: 'warning' })
      await deleteFileTag(row.tagId)
      ElMessage.success('已删除')
      await loadTags()
      emit('success')
    } catch (e) {
      if (e !== 'cancel') handleMutationError(e, '删除标签失败')
    }
  }

  async function handleBind() {
    if (!fileIds.value.length) {
      ElMessage.warning('请先选择文件')
      return
    }
    if (!checkedTagIds.value.length) {
      ElMessage.warning('请选择至少一个标签')
      return
    }
    saving.value = true
    try {
      await bindFileTags(fileIds.value, checkedTagIds.value)
      ElMessage.success('打标成功')
      visible.value = false
      emit('success')
    } catch (e) {
      handleMutationError(e, '打标失败')
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
