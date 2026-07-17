<template>
  <ArtSearchBar
    ref="searchBarRef"
    v-model="formData"
    :items="formItems"
    :rules="rules"
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
    set: (val) => emit('update:modelValue', val)
  })

  const rules = {}

  const statusOptions = computed(() => [
    { label: t('system.role.statusEnabled'), value: 0 },
    { label: t('system.role.statusDisabled'), value: 1 }
  ])

  const formItems = computed(() => [
    {
      label: t('system.role.roleName'),
      key: 'roleName',
      type: 'input',
      placeholder: t('system.role.roleNamePlaceholder'),
      clearable: true
    },
    {
      label: t('system.role.roleKey'),
      key: 'roleKey',
      type: 'input',
      placeholder: t('system.role.roleKeyPlaceholder'),
      clearable: true
    },
    {
      label: t('system.role.roleDesc'),
      key: 'remark',
      type: 'input',
      placeholder: t('system.role.roleDescPlaceholder'),
      clearable: true
    },
    {
      label: t('system.role.roleStatus'),
      key: 'status',
      type: 'select',
      props: {
        placeholder: t('common.pleaseSelect'),
        options: statusOptions.value,
        clearable: true
      }
    },
    {
      label: t('system.role.createDate'),
      key: 'daterange',
      type: 'datetime',
      props: {
        style: { width: '100%' },
        placeholder: t('system.role.dateRangePlaceholder'),
        type: 'daterange',
        rangeSeparator: t('system.role.rangeSeparator'),
        startPlaceholder: t('system.role.startDate'),
        endPlaceholder: t('system.role.endDate'),
        valueFormat: 'YYYY-MM-DD',
        shortcuts: [
          { text: t('system.role.today'), value: [new Date(), new Date()] },
          { text: t('system.role.lastWeek'), value: [new Date(Date.now() - 604800000), new Date()] },
          {
            text: t('system.role.lastMonth'),
            value: [new Date(Date.now() - 2592000000), new Date()]
          }
        ]
      }
    }
  ])

  const handleReset = () => {
    emit('reset')
  }

  const handleSearch = async () => {
    await searchBarRef.value.validate()
    emit('search', formData.value)
  }
</script>
