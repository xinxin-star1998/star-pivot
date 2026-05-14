<template>
  <ArtSearchBar
    ref="searchBarRef"
    v-model="formData"
    :items="formItems"
    :rules="rules"
    @reset="handleReset"
    @search="handleSearch"
  />
</template>

<script setup lang="ts">
  interface Props {
    modelValue: Record<string, unknown>
  }
  interface Emits {
    (e: 'update:modelValue', value: Record<string, unknown>): void
    (e: 'search', params: Record<string, unknown>): void
    (e: 'reset'): void
  }
  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()

  const searchBarRef = ref()
  const formData = computed({
    get: () => props.modelValue,
    set: (val) => emit('update:modelValue', val)
  })

  const rules = {}

  const statusOptions = [
    { label: '上架', value: 0 },
    { label: '下架', value: 1 }
  ]

  const formItems = computed(() => [
    {
      label: '商品名称',
      key: 'name',
      type: 'input',
      placeholder: '模糊查询',
      clearable: true
    },
    {
      label: '分类ID',
      key: 'categoryId',
      type: 'number',
      props: { min: 0, precision: 0, placeholder: '精确匹配', controlsPosition: 'right' }
    },
    {
      label: '品牌ID',
      key: 'brandId',
      type: 'number',
      props: { min: 0, precision: 0, placeholder: '精确匹配', controlsPosition: 'right' }
    },
    {
      label: '状态',
      key: 'status',
      type: 'select',
      props: {
        placeholder: '请选择',
        options: statusOptions,
        clearable: true
      }
    }
  ])

  function handleReset() {
    emit('reset')
  }

  async function handleSearch() {
    await searchBarRef.value.validate()
    emit('search', formData.value)
  }
</script>
