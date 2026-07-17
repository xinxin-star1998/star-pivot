<template>
  <ElDialog
    :title="dialogTitle"
    :model-value="visible"
    @update:model-value="handleCancel"
    width="600px"
    align-center
    class="dict-type-dialog"
    @closed="handleClosed"
  >
    <ArtForm
      ref="formRef"
      v-model="form"
      :items="formItems"
      :rules="rules"
      :span="24"
      :gutter="20"
      label-width="auto"
      :show-reset="false"
      :show-submit="false"
    />

    <template #footer>
      <span class="dialog-footer">
        <ElButton @click="handleCancel">{{ t('common.cancel') }}</ElButton>
        <ElButton type="primary" @click="handleSubmit">{{ t('common.confirm') }}</ElButton>
      </span>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import type { FormRules } from 'element-plus'
  import { ElMessage } from 'element-plus'
  import type { FormItem } from '@/components/core/forms/art-form/index.vue'
  import ArtForm from '@/components/core/forms/art-form/index.vue'
  import type { DictTypeFormData } from '@/api/dict/type'
  import { useI18n } from 'vue-i18n'

  interface Props {
    visible: boolean
    editData?: DictTypeFormData | null
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
    (e: 'submit', data: DictTypeFormData): void
  }

  const props = withDefaults(defineProps<Props>(), {
    visible: false,
    editData: null
  })

  const emit = defineEmits<Emits>()
  const { t } = useI18n()

  const formRef = ref()
  const isEdit = ref(false)

  const form = reactive<DictTypeFormData>({
    dictName: '',
    dictType: '',
    status: '0',
    remark: ''
  })

  const dialogTitle = computed(() => {
    return isEdit.value ? t('system.dict.editType') : t('system.dict.addType')
  })

  const validateDictType = (rule: any, value: string, callback: any) => {
    if (!value) {
      callback(new Error(t('system.dict.typeRequired')))
      return
    }
    if (!/^[a-zA-Z0-9_-]+$/.test(value)) {
      callback(new Error(t('system.dict.typeRequired')))
      return
    }
    callback()
  }

  const rules = computed<FormRules>(() => ({
    dictName: [
      { required: true, message: t('system.dict.nameRequired'), trigger: 'blur' },
      { max: 100, message: t('common.pleaseInput'), trigger: 'blur' }
    ],
    dictType: [{ validator: validateDictType, trigger: 'blur' }],
    status: [{ required: true, message: t('common.pleaseSelect'), trigger: 'change' }]
  }))

  const formItems = computed<FormItem[]>(() => [
    {
      label: t('system.dict.dictName'),
      key: 'dictName',
      type: 'input',
      props: { placeholder: t('system.dict.namePlaceholder') }
    },
    {
      label: t('system.dict.dictType'),
      key: 'dictType',
      type: 'input',
      props: {
        placeholder: t('system.dict.typePlaceholder'),
        disabled: isEdit.value
      }
    },
    {
      label: t('common.status'),
      key: 'status',
      type: 'radiogroup',
      props: {
        options: [
          { label: t('common.normal'), value: '0' },
          { label: t('common.disabled'), value: '1' }
        ]
      }
    },
    {
      label: t('common.remark'),
      key: 'remark',
      type: 'input',
      span: 24,
      props: { type: 'textarea', rows: 3, placeholder: t('common.pleaseInput') }
    }
  ])

  const loadFormData = (): void => {
    if (!props.editData) return

    isEdit.value = true
    Object.assign(form, {
      dictId: props.editData.dictId,
      dictName: props.editData.dictName,
      dictType: props.editData.dictType,
      status: props.editData.status || '0',
      remark: props.editData.remark || ''
    })
  }

  const resetForm = (): void => {
    Object.assign(form, {
      dictId: undefined,
      dictName: '',
      dictType: '',
      status: '0',
      remark: ''
    })
    nextTick(() => {
      if (formRef.value?.ref) {
        formRef.value.ref.resetFields()
      }
    })
    isEdit.value = false
  }

  const handleSubmit = async (): Promise<void> => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()

      const submitData: DictTypeFormData = {
        dictName: form.dictName,
        dictType: form.dictType,
        status: form.status || '0',
        remark: form.remark || ''
      }

      if (isEdit.value && form.dictId) {
        submitData.dictId = form.dictId
      }

      emit('submit', submitData)
      handleCancel()
    } catch {
      ElMessage.error(t('common.pleaseInput'))
    }
  }

  const handleCancel = (): void => {
    emit('update:visible', false)
  }

  const handleClosed = (): void => {
    resetForm()
  }

  watch(
    () => props.visible,
    async (newVal) => {
      if (newVal) {
        await nextTick()
        if (props.editData) {
          loadFormData()
        } else {
          resetForm()
        }
      }
    }
  )
</script>

<style scoped lang="scss">
  :deep(.el-dialog) {
    overflow: hidden;
    border-radius: 16px;

    .el-dialog__header {
      padding: 20px 24px;
      margin: 0;
      background: linear-gradient(
        135deg,
        var(--el-color-primary-light-9) 0%,
        var(--el-color-primary-light-8) 100%
      );
      border-bottom: 1px solid var(--art-card-border);

      .el-dialog__title {
        font-size: 18px;
        font-weight: 600;
        color: var(--art-gray-900);
      }
    }

    .el-dialog__body {
      padding: 24px;
    }

    .el-dialog__footer {
      padding: 16px 24px;
      background-color: var(--art-gray-50);
      border-top: 1px solid var(--art-card-border);
    }
  }

  :deep(.el-form-item__label) {
    font-weight: 500;
    color: var(--art-gray-700);
  }

  :deep(.el-input__wrapper),
  :deep(.el-textarea__inner) {
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 2px 8px 0 rgb(0 0 0 / 8%);
    }
  }

  :deep(.el-radio-group) {
    .el-radio {
      margin-right: 20px;
    }
  }

  :deep(.el-button) {
    padding: 10px 24px;
    font-weight: 500;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-1px);
    }
  }

  .dialog-footer {
    display: flex;
    gap: 12px;
    justify-content: flex-end;
  }
</style>
