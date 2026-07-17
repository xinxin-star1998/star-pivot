<template>
  <ArtSearchBar
    ref="searchBarRef"
    v-model="formData"
    :items="formItems"
    :rules="rules"
    :show-reset="true"
    :show-search="true"
    @reset="handleReset"
    @search="handleSearch"
  >
  </ArtSearchBar>
</template>

<script setup lang="ts">
  import ArtSearchBar from '@/components/core/forms/art-search-bar/index.vue'
  import { useI18n } from 'vue-i18n'
  import { OPER_BUSINESS_TYPE_OPTIONS } from '../constants'

  interface Props {
    modelValue: Record<string, any>
  }
  interface Emits {
    (e: 'update:modelValue', value: Record<string, any>): void
    (e: 'search', params: Record<string, any>): void
    (e: 'reset'): void
  }
  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()
  const { t } = useI18n()

  const searchBarRef = ref()
  const formData = computed({
    get: () => props.modelValue,
    set: (val: Record<string, any>) => emit('update:modelValue', val)
  })

  const rules = {}

  const statusOptions = computed(() => [
    { label: t('system.operLog.statusSuccess'), value: 0 },
    { label: t('system.operLog.statusFail'), value: 1 }
  ])

  const formItems = computed(() => [
    {
      label: t('system.operLog.title'),
      key: 'title',
      type: 'input',
      placeholder: t('system.operLog.searchTitle'),
      clearable: true
    },
    {
      label: t('system.operLog.businessType'),
      key: 'businessType',
      type: 'select',
      props: {
        placeholder: t('common.pleaseSelect'),
        clearable: true,
        options: OPER_BUSINESS_TYPE_OPTIONS
      }
    },
    {
      label: t('system.operLog.operName'),
      key: 'operName',
      type: 'input',
      placeholder: t('system.operLog.searchOperName'),
      clearable: true
    },
    {
      label: t('system.operLog.status'),
      key: 'status',
      type: 'select',
      props: {
        placeholder: t('common.pleaseSelect'),
        clearable: true,
        options: statusOptions.value
      }
    },
    {
      label: t('system.operLog.operTime'),
      key: 'dateRange',
      type: 'datetimerange',
      span: 8,
      props: {
        type: 'datetimerange',
        startPlaceholder: t('common.pleaseInput'),
        endPlaceholder: t('common.pleaseInput'),
        format: 'YYYY-MM-DD HH:mm:ss',
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        style: 'width: 100%',
        clearable: true
      }
    }
  ])

  function handleReset() {
    emit('reset')
  }

  async function handleSearch() {
    await searchBarRef.value.validate()
    const searchParams = { ...formData.value }
    if (
      searchParams.dateRange &&
      Array.isArray(searchParams.dateRange) &&
      searchParams.dateRange.length === 2
    ) {
      searchParams.startTime = searchParams.dateRange[0]
      searchParams.endTime = searchParams.dateRange[1]
    } else {
      searchParams.startTime = undefined
      searchParams.endTime = undefined
    }
    delete searchParams.dateRange
    emit('search', searchParams)
  }
</script>
