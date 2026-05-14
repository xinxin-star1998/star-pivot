<template>
  <ElDialog
    v-model="dialogVisible"
    :title="type === 'add' ? '新增商品' : '编辑商品'"
    width="720px"
    align-center
    destroy-on-close
  >
    <ElForm
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="110px"
      aria-label="商品信息表单"
    >
      <ElRow :gutter="16">
        <ElCol :span="12">
          <ElFormItem label="分类ID" prop="categoryId">
            <ElInputNumber
              v-model="formData.categoryId"
              :min="1"
              :precision="0"
              placeholder="必填"
              style="width: 100%"
              controls-position="right"
            />
          </ElFormItem>
        </ElCol>
        <ElCol :span="12">
          <ElFormItem label="品牌ID" prop="brandId">
            <ElInputNumber
              v-model="formData.brandId"
              :min="0"
              :precision="0"
              placeholder="选填"
              style="width: 100%"
              controls-position="right"
              clearable
            />
          </ElFormItem>
        </ElCol>
      </ElRow>
      <ElFormItem label="商品名称" prop="name">
        <ElInput v-model="formData.name" placeholder="名称" maxlength="200" show-word-limit />
      </ElFormItem>
      <ElFormItem label="副标题" prop="subtitle">
        <ElInput v-model="formData.subtitle" placeholder="副标题" maxlength="500" show-word-limit />
      </ElFormItem>
      <ElFormItem label="主图 URL" prop="mainImage">
        <ElInput
          v-model="formData.mainImage"
          placeholder="主图地址"
          maxlength="512"
          show-word-limit
        />
      </ElFormItem>
      <ElFormItem label="图集" prop="images">
        <ElInput
          v-model="formData.images"
          type="textarea"
          :rows="2"
          placeholder="多图 URL，JSON 或逗号分隔，按后端约定填写"
        />
      </ElFormItem>
      <ElFormItem label="详情" prop="detail">
        <ElInput
          v-model="formData.detail"
          type="textarea"
          :rows="4"
          placeholder="商品详情（富文本/HTML 等）"
        />
      </ElFormItem>
      <ElRow :gutter="16">
        <ElCol :span="8">
          <ElFormItem label="价格" prop="price">
            <ElInputNumber
              v-model="formData.price"
              :min="0"
              :precision="2"
              :step="0.01"
              style="width: 100%"
              controls-position="right"
            />
          </ElFormItem>
        </ElCol>
        <ElCol :span="8">
          <ElFormItem label="库存" prop="stock">
            <ElInputNumber
              v-model="formData.stock"
              :min="0"
              :precision="0"
              style="width: 100%"
              controls-position="right"
            />
          </ElFormItem>
        </ElCol>
        <ElCol :span="8">
          <ElFormItem label="状态" prop="status">
            <ElRadioGroup v-model="formData.status">
              <ElRadio :value="0">上架</ElRadio>
              <ElRadio :value="1">下架</ElRadio>
            </ElRadioGroup>
          </ElFormItem>
        </ElCol>
      </ElRow>
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
    fetchMallProductAdd,
    fetchMallProductById,
    fetchMallProductUpdate,
    type MallProductSavePayload,
    type MallProductVo
  } from '@/api/mall/product'
  import type { DialogType } from '@/types'

  interface Props {
    visible: boolean
    type: DialogType
    productData?: Partial<MallProductVo>
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
    categoryId: undefined as number | undefined,
    brandId: undefined as number | undefined,
    name: '',
    subtitle: '',
    mainImage: '',
    images: '',
    detail: '',
    price: 0,
    stock: 0,
    status: 0
  })

  const rules: FormRules = {
    categoryId: [{ required: true, message: '请输入分类ID', trigger: 'blur' }],
    name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
    price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
    stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }]
  }

  const toNum = (v: unknown): number | undefined => {
    if (v === null || v === undefined || v === '') return undefined
    const n = Number(v)
    return Number.isFinite(n) ? n : undefined
  }

  const initFormData = async () => {
    const isEdit = props.type === 'edit' && props.productData?.id

    if (isEdit && props.productData?.id) {
      try {
        const detail = await fetchMallProductById(props.productData.id)
        Object.assign(formData, {
          id: detail.id,
          categoryId: detail.categoryId ?? undefined,
          brandId: detail.brandId ?? undefined,
          name: detail.name ?? '',
          subtitle: detail.subtitle ?? '',
          mainImage: detail.mainImage ?? '',
          images: detail.images ?? '',
          detail: detail.detail ?? '',
          price: detail.price != null ? Number(detail.price) : 0,
          stock: detail.stock ?? 0,
          status: detail.status ?? 0
        })
      } catch {
        const row = props.productData
        Object.assign(formData, {
          id: row.id,
          categoryId: row.categoryId ?? undefined,
          brandId: row.brandId ?? undefined,
          name: row.name ?? '',
          subtitle: row.subtitle ?? '',
          mainImage: row.mainImage ?? '',
          images: row.images ?? '',
          detail: row.detail ?? '',
          price: row.price != null ? Number(row.price) : 0,
          stock: row.stock ?? 0,
          status: row.status ?? 0
        })
      }
    } else {
      Object.assign(formData, {
        id: undefined,
        categoryId: undefined,
        brandId: undefined,
        name: '',
        subtitle: '',
        mainImage: '',
        images: '',
        detail: '',
        price: 0,
        stock: 0,
        status: 0
      })
    }
  }

  watch(
    () => [props.visible, props.type, props.productData],
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
    const cid = toNum(formData.categoryId)
    if (cid == null) {
      return
    }
    const payload: MallProductSavePayload = {
      categoryId: cid,
      name: formData.name,
      subtitle: formData.subtitle || undefined,
      mainImage: formData.mainImage || undefined,
      images: formData.images || undefined,
      detail: formData.detail || undefined,
      price: Number(formData.price),
      stock: Number(formData.stock),
      status: formData.status
    }
    const bid = toNum(formData.brandId)
    if (bid != null) {
      payload.brandId = bid
    } else {
      payload.brandId = null
    }
    try {
      if (props.type === 'add') {
        await fetchMallProductAdd(payload)
      } else {
        payload.id = formData.id
        await fetchMallProductUpdate(payload)
      }
      dialogVisible.value = false
      emit('submit')
    } catch {
      // http 拦截器提示
    }
  }
</script>
