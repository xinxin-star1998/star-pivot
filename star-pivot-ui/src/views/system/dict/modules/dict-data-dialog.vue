<template>
  <ElDialog
    :title="dialogTitle"
    :model-value="visible"
    @update:model-value="handleCancel"
    width="700px"
    align-center
    class="dict-data-dialog"
    @closed="handleClosed"
  >
    <ArtForm
      ref="formRef"
      v-model="form"
      :items="formItems"
      :rules="rules"
      :span="width > 640 ? 12 : 24"
      :gutter="20"
      label-width="100px"
      :show-reset="false"
      :show-submit="false"
    />

    <template #footer>
      <span class="dialog-footer">
        <ElButton @click="handleCancel">取 消</ElButton>
        <ElButton type="primary" @click="handleSubmit">确 定</ElButton>
      </span>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import type { FormRules } from 'element-plus'
  import { ElMessage } from 'element-plus'
  import type { FormItem } from '@/components/core/forms/art-form/index.vue'
  import ArtForm from '@/components/core/forms/art-form/index.vue'
  import { useWindowSize } from '@vueuse/core'
  import type { DictDataFormData } from '@/api/dict/data'

  const { width } = useWindowSize()

  interface Props {
    visible: boolean
    editData?: DictDataFormData | null
    dictType?: string
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
    (e: 'submit', data: DictDataFormData): void
  }

  const props = withDefaults(defineProps<Props>(), {
    visible: false,
    editData: null,
    dictType: ''
  })

  const emit = defineEmits<Emits>()

  const formRef = ref()
  const isEdit = ref(false)

  const form = reactive<DictDataFormData>({
    dictSort: 0,
    dictLabel: '',
    dictValue: '',
    dictType: '',
    cssClass: '',
    listClass: '',
    isDefault: 'N',
    status: '0',
    remark: ''
  })

  const dialogTitle = computed(() => {
    return isEdit.value ? '编辑字典数据' : '新增字典数据'
  })

  const rules = reactive<FormRules>({
    dictLabel: [
      { required: true, message: '请输入字典标签', trigger: 'blur' },
      { max: 100, message: '字典标签长度不能超过100个字符', trigger: 'blur' }
    ],
    dictValue: [
      { required: true, message: '请输入字典键值', trigger: 'blur' },
      { max: 100, message: '字典键值长度不能超过100个字符', trigger: 'blur' }
    ],
    dictType: [{ required: true, message: '请选择字典类型', trigger: 'change' }],
    dictSort: [
      { required: true, message: '请输入字典排序', trigger: 'blur' },
      { type: 'number', min: 0, message: '字典排序必须大于等于0', trigger: 'blur' }
    ],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }]
  })

  const formItems = computed<FormItem[]>(() => {
    const switchSpan = width.value < 640 ? 12 : 6
    return [
      {
        label: '字典类型',
        key: 'dictType',
        type: 'input',
        props: {
          placeholder: '请输入字典类型',
          disabled: !!props.dictType || isEdit.value
        }
      },
      {
        label: '字典标签',
        key: 'dictLabel',
        type: 'input',
        props: { placeholder: '请输入字典标签' }
      },
      {
        label: '字典键值',
        key: 'dictValue',
        type: 'input',
        props: { placeholder: '请输入字典键值' }
      },
      {
        label: '字典排序',
        key: 'dictSort',
        type: 'number',
        props: {
          min: 0,
          controlsPosition: 'right',
          style: { width: '100%' }
        }
      },
      {
        label: '样式属性',
        key: 'cssClass',
        type: 'input',
        props: { placeholder: '如：primary、success、warning、danger' }
      },
      {
        label: '回显样式',
        key: 'listClass',
        type: 'input',
        props: { placeholder: '如：default、primary、success、info、warning、danger' }
      },
      {
        label: '是否默认',
        key: 'isDefault',
        type: 'radio',
        span: switchSpan,
        props: {
          options: [
            { label: '是', value: 'Y' },
            { label: '否', value: 'N' }
          ]
        }
      },
      {
        label: '状态',
        key: 'status',
        type: 'radio',
        span: switchSpan,
        props: {
          options: [
            { label: '正常', value: '0' },
            { label: '停用', value: '1' }
          ]
        }
      },
      {
        label: '备注',
        key: 'remark',
        type: 'input',
        span: 24,
        props: { type: 'textarea', rows: 3, placeholder: '请输入备注' }
      }
    ]
  })

  /**
   * 加载表单数据（编辑模式）
   */
  const loadFormData = (): void => {
    if (!props.editData) return

    isEdit.value = true
    Object.assign(form, {
      dictCode: props.editData.dictCode,
      dictSort: props.editData.dictSort || 0,
      dictLabel: props.editData.dictLabel,
      dictValue: props.editData.dictValue,
      dictType: props.editData.dictType,
      cssClass: props.editData.cssClass || '',
      listClass: props.editData.listClass || '',
      isDefault: props.editData.isDefault || 'N',
      status: props.editData.status || '0',
      remark: props.editData.remark || ''
    })
  }

  /**
   * 重置表单数据
   */
  const resetForm = (): void => {
    Object.assign(form, {
      dictCode: undefined,
      dictSort: 0,
      dictLabel: '',
      dictValue: '',
      dictType: props.dictType || '',
      cssClass: '',
      listClass: '',
      isDefault: 'N',
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

  /**
   * 提交表单
   */
  const handleSubmit = async (): Promise<void> => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()

      const submitData: DictDataFormData = {
        dictSort: form.dictSort || 0,
        dictLabel: form.dictLabel,
        dictValue: form.dictValue,
        dictType: form.dictType || props.dictType || '',
        cssClass: form.cssClass || '',
        listClass: form.listClass || '',
        isDefault: form.isDefault || 'N',
        status: form.status || '0',
        remark: form.remark || ''
      }

      // 如果是编辑模式，添加dictCode
      if (isEdit.value && form.dictCode) {
        submitData.dictCode = form.dictCode
      }

      emit('submit', submitData)
      handleCancel()
    } catch {
      ElMessage.error('表单校验失败，请检查输入')
    }
  }

  /**
   * 取消操作
   */
  const handleCancel = (): void => {
    emit('update:visible', false)
  }

  /**
   * 对话框关闭后的回调
   */
  const handleClosed = (): void => {
    resetForm()
  }

  /**
   * 监听对话框显示状态
   */
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

  /**
   * 监听字典类型变化
   */
  watch(
    () => props.dictType,
    (newVal) => {
      if (newVal && !isEdit.value) {
        form.dictType = newVal
      }
    }
  )
</script>

<style scoped lang="scss"></style>

