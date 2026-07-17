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

  const loginStatusOptions = computed(() => [
    { label: t('system.loginLog.statusSuccess'), value: '0' },
    { label: t('system.loginLog.statusFail'), value: '1' }
  ])

  const formItems = computed(() => [
    {
      label: t('system.loginLog.userName'),
      key: 'userName',
      type: 'input',
      placeholder: t('system.loginLog.searchUserName'),
      clearable: true
    },
    {
      label: t('system.loginLog.ipaddr'),
      key: 'ipaddr',
      type: 'input',
      placeholder: t('system.loginLog.searchIp'),
      clearable: true
    },
    {
      label: t('system.loginLog.status'),
      key: 'status',
      type: 'select',
      props: {
        placeholder: t('common.pleaseSelect'),
        clearable: true,
        options: loginStatusOptions.value
      }
    },
    {
      label: t('system.loginLog.loginTime'),
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
