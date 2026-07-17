<template>
  <ElDialog
    v-model="visible"
    :title="isEdit ? t('file.editFolder') : t('file.newFolder')"
    destroy-on-close
    width="480px"
    @closed="reset"
  >
    <ElForm ref="formRef" :model="form" :rules="rules" label-width="90px">
      <ElFormItem v-if="!isEdit" :label="t('file.category')" prop="category">
        <ElSelect
          v-model="form.category"
          :placeholder="t('file.selectCategory')"
          style="width: 100%"
        >
          <ElOption
            v-for="item in categoryOptions"
            :key="item.code"
            :label="item.label"
            :value="item.code"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem v-if="!isEdit && parentLabel" :label="t('file.parentFolder')">
        <ElInput :model-value="parentLabel" disabled />
      </ElFormItem>
      <ElFormItem :label="t('file.folderName')" prop="folderName">
        <ElInput v-model="form.folderName" :placeholder="t('file.nameRequired')" />
      </ElFormItem>
      <ElFormItem :label="t('common.orderNum')">
        <ElInputNumber v-model="form.orderNum" :min="0" />
      </ElFormItem>
      <ElFormItem :label="t('common.remark')">
        <ElInput v-model="form.remark" :rows="2" type="textarea" />
      </ElFormItem>
    </ElForm>
    <template #footer>
      <ElButton @click="visible = false">{{ t('common.cancel') }}</ElButton>
      <ElButton :loading="submitting" type="primary" @click="submit">{{
        t('common.confirm')
      }}</ElButton>
    </template>
  </ElDialog>
</template>

<script lang="ts" setup>
  import { createFolder, updateFolder } from '@/api/file/folder'
  import type { SysFileFolderForm } from '@/api/file/types'
  import { FILE_CATEGORIES } from '../constants'
  import type { FormInstance, FormRules } from 'element-plus'
  import { ElMessage } from 'element-plus'
  import { computed, reactive, ref, watch } from 'vue'
  import { useI18n } from 'vue-i18n'

  const visible = defineModel<boolean>('visible', { default: false })

  const props = defineProps<{
    type: 'add' | 'edit'
    data?: SysFileFolderForm
    defaultCategory?: string
    parentId?: number
    parentLabel?: string
  }>()

  const emit = defineEmits<{
    success: []
  }>()

  const { t } = useI18n()

  const formRef = ref<FormInstance>()
  const submitting = ref(false)

  const form = reactive<SysFileFolderForm>({
    category: '',
    folderName: '',
    parentId: 0,
    orderNum: 0,
    remark: ''
  })

  const isEdit = computed(() => props.type === 'edit')

  const categoryOptions = computed(() =>
    FILE_CATEGORIES.map((item) => ({
      code: item.code,
      label: t(`file.cat.${item.code}`)
    }))
  )

  const rules = computed<FormRules>(() => ({
    category: [{ required: true, message: t('file.categoryRequired'), trigger: 'change' }],
    folderName: [{ required: true, message: t('file.nameRequired'), trigger: 'blur' }]
  }))

  watch(
    () => visible.value,
    (open) => {
      if (!open) return
      if (props.type === 'edit' && props.data) {
        Object.assign(form, props.data)
      } else {
        reset()
        form.category = props.defaultCategory || ''
        form.parentId = props.parentId || 0
      }
    }
  )

  function reset() {
    form.folderId = undefined
    form.category = props.defaultCategory || ''
    form.folderName = ''
    form.parentId = props.parentId || 0
    form.orderNum = 0
    form.remark = ''
  }

  async function submit() {
    await formRef.value?.validate()
    submitting.value = true
    try {
      if (isEdit.value) {
        await updateFolder({ ...form })
        ElMessage.success(t('common.updateSuccess'))
      } else {
        await createFolder({ ...form, parentId: form.parentId || 0 })
        ElMessage.success(t('file.createSuccess'))
      }
      visible.value = false
      emit('success')
    } finally {
      submitting.value = false
    }
  }
</script>
