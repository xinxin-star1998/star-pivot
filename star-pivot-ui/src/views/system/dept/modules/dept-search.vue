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

  const formItems = computed(() => [
    {
      label: t('system.dept.deptName'),
      key: 'deptName',
      type: 'input',
      placeholder: t('system.dept.deptNamePlaceholder'),
      clearable: true
    },
    {
      label: t('system.dept.leader'),
      key: 'leader',
      type: 'input',
      props: { placeholder: t('system.dept.leaderPlaceholder') }
    },
    {
      label: t('common.status'),
      key: 'status',
      type: 'select',
      props: {
        placeholder: t('common.pleaseSelect'),
        clearable: true,
        options: [
          { label: t('common.normal'), value: '0' },
          { label: t('common.disabled'), value: '1' }
        ]
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
