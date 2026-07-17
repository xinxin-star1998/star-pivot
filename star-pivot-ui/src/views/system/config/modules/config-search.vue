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

<script lang="ts" setup>
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

  const configTypeOptions = computed(() => [
    { label: t('system.config.yes'), value: 'Y' },
    { label: t('system.config.no'), value: 'N' }
  ])

  const formItems = computed(() => [
    {
      label: t('system.config.configName'),
      key: 'configName',
      type: 'input',
      placeholder: t('system.config.searchName'),
      clearable: true
    },
    {
      label: t('system.config.configKey'),
      key: 'configKey',
      type: 'input',
      placeholder: t('system.config.searchKey'),
      clearable: true
    },
    {
      label: t('system.config.configValue'),
      key: 'configValue',
      type: 'input',
      placeholder: t('system.config.valuePlaceholder'),
      clearable: true
    },
    {
      label: t('system.config.builtIn'),
      key: 'configType',
      type: 'select',
      props: {
        placeholder: t('common.pleaseSelect'),
        options: configTypeOptions.value,
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
