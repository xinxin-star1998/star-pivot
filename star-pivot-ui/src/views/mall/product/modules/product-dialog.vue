<template>
  <ElDialog
    v-model="dialogVisible"
    :title="type === 'add' ? '新增 SPU' : '编辑 SPU'"
    width="640px"
    align-center
    destroy-on-close
  >
    <ElForm
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
      aria-label="SPU 信息表单"
    >
      <ElRow :gutter="16">
        <ElCol :span="12">
          <ElFormItem label="商品分类" prop="catalogPath">
            <ElCascader
              v-model="catalogPath"
              :options="categoryOptions"
              :props="cascaderProps"
              clearable
              filterable
              placeholder="请选择三级类目"
              style="width: 100%"
            />
          </ElFormItem>
        </ElCol>
        <ElCol :span="12">
          <ElFormItem label="品牌 ID" prop="brandId">
            <ElInputNumber
              v-model="formData.brandId"
              :min="0"
              :precision="0"
              placeholder="选填"
              style="width: 100%"
              controls-position="right"
            />
          </ElFormItem>
        </ElCol>
      </ElRow>
      <ElFormItem label="SPU 名称" prop="spuName">
        <ElInput v-model="formData.spuName" placeholder="名称" maxlength="200" show-word-limit />
      </ElFormItem>
      <ElFormItem label="描述" prop="spuDescription">
        <ElInput
          v-model="formData.spuDescription"
          type="textarea"
          :rows="4"
          placeholder="SPU 描述"
        />
      </ElFormItem>
      <ElRow :gutter="16">
        <ElCol :span="12">
          <ElFormItem label="重量" prop="weight">
            <ElInputNumber
              v-model="formData.weight"
              :min="0"
              :precision="3"
              :step="0.001"
              style="width: 100%"
              controls-position="right"
            />
          </ElFormItem>
        </ElCol>
        <ElCol :span="12">
          <ElFormItem label="上架状态" prop="publishStatus">
            <ElRadioGroup v-model="formData.publishStatus">
              <ElRadio :value="1">上架</ElRadio>
              <ElRadio :value="0">下架</ElRadio>
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
  import { fetchMallCategoryTree, type MallCategoryTreeNode } from '@/api/mall/category'
  import {
    filterVisibleCategoryTree,
    findCategoryPath,
    mapCategoryCascaderOptions
  } from '@/utils/mall/category-tree'
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
  const categoryOptions = ref<MallCategoryTreeNode[]>([])
  const catalogPath = ref<number[]>([])

  const cascaderProps = {
    value: 'catId',
    label: 'name',
    children: 'children',
    leaf: 'leaf',
    emitPath: true,
    expandTrigger: 'click' as const,
    checkStrictly: false
  }

  const loadCategoryOptions = async () => {
    try {
      const tree = await fetchMallCategoryTree()
      categoryOptions.value = mapCategoryCascaderOptions(filterVisibleCategoryTree(tree || []))
    } catch {
      categoryOptions.value = []
    }
  }

  const syncCatalogPathFromId = (catalogId: number | undefined) => {
    if (catalogId == null || !categoryOptions.value.length) {
      catalogPath.value = []
      return
    }
    catalogPath.value = findCategoryPath(categoryOptions.value, catalogId) ?? []
  }

  watch(catalogPath, (path) => {
    const last = path?.length ? path[path.length - 1] : undefined
    formData.catalogId = last
  })

  const formData = reactive({
    id: undefined as number | undefined,
    catalogId: undefined as number | undefined,
    brandId: undefined as number | undefined,
    spuName: '',
    spuDescription: '',
    weight: 0,
    publishStatus: 0
  })

  const rules: FormRules = {
    catalogPath: [
      {
        validator: (_rule, _value, callback) => {
          if (!catalogPath.value?.length) {
            callback(new Error('请选择三级商品分类'))
            return
          }
          const last = catalogPath.value[catalogPath.value.length - 1]
          if (last == null) {
            callback(new Error('请选择三级商品分类'))
            return
          }
          callback()
        },
        trigger: 'change'
      }
    ],
    spuName: [{ required: true, message: '请输入 SPU 名称', trigger: 'blur' }],
    weight: [{ required: true, message: '请输入重量', trigger: 'blur' }],
    publishStatus: [{ required: true, message: '请选择上架状态', trigger: 'change' }]
  }

  const toNum = (v: unknown): number | undefined => {
    if (v === null || v === undefined || v === '') return undefined
    const n = Number(v)
    return Number.isFinite(n) ? n : undefined
  }

  const toWeight = (v: unknown): number => {
    if (v === null || v === undefined || v === '') return 0
    const n = Number(v)
    return Number.isFinite(n) ? n : 0
  }

  const initFormData = async () => {
    await loadCategoryOptions()
    const isEdit = props.type === 'edit' && props.productData?.id

    if (isEdit && props.productData?.id) {
      try {
        const detail = await fetchMallProductById(props.productData.id)
        Object.assign(formData, {
          id: detail.id,
          catalogId: detail.catalogId != null ? Number(detail.catalogId) : undefined,
          brandId: detail.brandId != null ? Number(detail.brandId) : undefined,
          spuName: detail.spuName ?? '',
          spuDescription: detail.spuDescription ?? '',
          weight: toWeight(detail.weight),
          publishStatus: detail.publishStatus ?? 0
        })
        syncCatalogPathFromId(formData.catalogId)
      } catch {
        const row = props.productData
        Object.assign(formData, {
          id: row.id,
          catalogId: row.catalogId != null ? Number(row.catalogId) : undefined,
          brandId: row.brandId != null ? Number(row.brandId) : undefined,
          spuName: row.spuName ?? '',
          spuDescription: row.spuDescription ?? '',
          weight: toWeight(row.weight),
          publishStatus: row.publishStatus ?? 0
        })
        syncCatalogPathFromId(formData.catalogId)
      }
    } else {
      catalogPath.value = []
      Object.assign(formData, {
        id: undefined,
        catalogId: undefined,
        brandId: undefined,
        spuName: '',
        spuDescription: '',
        weight: 0,
        publishStatus: 0
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
    const catalogId = toNum(
      catalogPath.value?.length
        ? catalogPath.value[catalogPath.value.length - 1]
        : formData.catalogId
    )
    if (catalogId == null) {
      return
    }
    const payload: MallProductSavePayload = {
      catalogId,
      spuName: formData.spuName,
      spuDescription: formData.spuDescription || undefined,
      weight: Number(formData.weight),
      publishStatus: formData.publishStatus
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
