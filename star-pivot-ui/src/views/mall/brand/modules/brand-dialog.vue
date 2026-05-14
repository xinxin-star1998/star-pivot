<template>
  <ElDialog
    v-model="dialogVisible"
    :title="type === 'add' ? '新增品牌' : '编辑品牌'"
    width="520px"
    align-center
    destroy-on-close
  >
    <ElForm
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
      aria-label="品牌信息表单"
    >
      <ElFormItem label="品牌名称" prop="brandName">
        <ElInput
          v-model="formData.brandName"
          placeholder="请输入品牌名称"
          maxlength="128"
          show-word-limit
        />
      </ElFormItem>
      <ElFormItem label="Logo 地址" prop="brandLogo">
        <ElInput
          v-model="formData.brandLogo"
          placeholder="图片 URL"
          maxlength="512"
          show-word-limit
        />
      </ElFormItem>
      <ElFormItem label="描述" prop="brandDesc">
        <ElInput v-model="formData.brandDesc" type="textarea" :rows="3" placeholder="品牌描述" />
      </ElFormItem>
      <ElFormItem label="排序" prop="sort">
        <ElInputNumber v-model="formData.sort" :min="0" :max="99999" style="width: 100%" />
      </ElFormItem>
      <ElFormItem label="状态" prop="status">
        <ElRadioGroup v-model="formData.status">
          <ElRadio :value="0">启用</ElRadio>
          <ElRadio :value="1">停用</ElRadio>
        </ElRadioGroup>
      </ElFormItem>
    </ElForm>
    <template #footer>
      <ElButton @click="dialogVisible = false">取消</ElButton>
      <ElButton type="primary" @click="handleSubmit">提交</ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import type { FormInstance, FormRules } from 'element-plus'
  import {
    fetchMallBrandAdd,
    fetchMallBrandById,
    fetchMallBrandUpdate,
    type MallBrandSavePayload,
    type MallBrandVo
  } from '@/api/mall/brand'
  import type { DialogType } from '@/types'

  interface Props {
    visible: boolean
    type: DialogType
    brandData?: Partial<MallBrandVo>
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
    (e: 'submit'): void
  }

  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()

  const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
  })

  const formRef = ref<FormInstance>()

  const formData = reactive({
    id: undefined as number | undefined,
    brandName: '',
    brandLogo: '',
    brandDesc: '',
    sort: 0,
    status: 0
  })

  const rules: FormRules = {
    brandName: [{ required: true, message: '请输入品牌名称', trigger: 'blur' }],
    sort: [{ required: true, message: '请输入排序', trigger: 'blur' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }]
  }

  const initFormData = async () => {
    const isEdit = props.type === 'edit' && props.brandData?.id

    if (isEdit && props.brandData?.id) {
      try {
        const detail = await fetchMallBrandById(props.brandData.id)
        Object.assign(formData, {
          id: detail.id,
          brandName: detail.brandName ?? '',
          brandLogo: detail.brandLogo ?? '',
          brandDesc: detail.brandDesc ?? '',
          sort: detail.sort ?? 0,
          status: detail.status ?? 0
        })
      } catch {
        const row = props.brandData
        Object.assign(formData, {
          id: row.id,
          brandName: row.brandName ?? '',
          brandLogo: row.brandLogo ?? '',
          brandDesc: row.brandDesc ?? '',
          sort: row.sort ?? 0,
          status: row.status ?? 0
        })
      }
    } else {
      Object.assign(formData, {
        id: undefined,
        brandName: '',
        brandLogo: '',
        brandDesc: '',
        sort: 0,
        status: 0
      })
    }
  }

  watch(
    () => [props.visible, props.type, props.brandData],
    async ([visible]) => {
      if (visible) {
        await initFormData()
        nextTick(() => {
          formRef.value?.clearValidate()
        })
      }
    },
    { immediate: true }
  )

  const handleSubmit = async () => {
    if (!formRef.value) return
    try {
      await formRef.value.validate()
    } catch {
      return
    }
    const payload: MallBrandSavePayload = {
      brandName: formData.brandName,
      brandLogo: formData.brandLogo || undefined,
      brandDesc: formData.brandDesc || undefined,
      sort: formData.sort,
      status: formData.status
    }
    try {
      if (props.type === 'add') {
        await fetchMallBrandAdd(payload)
      } else {
        payload.id = formData.id
        await fetchMallBrandUpdate(payload)
      }
      dialogVisible.value = false
      emit('submit')
    } catch {
      // 错误提示由 http 拦截器处理
    }
  }
</script>
