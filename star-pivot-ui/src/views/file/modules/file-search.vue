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

<script lang="ts" setup>
  import ArtSearchBar from '@/components/core/forms/art-search-bar/index.vue'
  import { FILE_CATEGORIES } from '../constants'
  import { useI18n } from 'vue-i18n'

  const props = defineProps<{
    modelValue: Record<string, unknown>
    recycle?: boolean
  }>()

  const emit = defineEmits<{
    'update:modelValue': [value: Record<string, unknown>]
    search: []
    reset: []
  }>()

  const { t } = useI18n()

  const searchBarRef = ref()
  const formData = computed({
    get: () => props.modelValue,
    set: (val) => emit('update:modelValue', val)
  })

  const rules = {}

  const categoryOptions = computed(() =>
    FILE_CATEGORIES.map((item) => ({
      label: t(`file.cat.${item.code}`),
      value: item.code
    }))
  )

  const formItems = computed(() => {
    const items = [
      {
        label: props.recycle ? t('file.fileName') : t('file.searchName'),
        key: 'fileName',
        type: 'input',
        placeholder: props.recycle
          ? t('file.searchFileNamePlaceholder')
          : t('file.searchKeywordPlaceholder'),
        clearable: true
      },
      {
        label: props.recycle ? t('file.deleter') : t('file.uploader'),
        key: props.recycle ? 'deleteBy' : 'createBy',
        type: 'input',
        placeholder: props.recycle
          ? t('file.searchDeleterPlaceholder')
          : t('file.searchUploaderPlaceholder'),
        clearable: true
      },
      {
        label: props.recycle ? t('file.deleteTime') : t('file.uploadTime'),
        key: 'timeRange',
        type: 'datetimerange',
        props: {
          type: 'datetimerange',
          valueFormat: 'YYYY-MM-DD HH:mm:ss',
          startPlaceholder: t('file.startTime'),
          endPlaceholder: t('file.endTime'),
          clearable: true
        }
      }
    ]
    if (props.recycle) {
      items.splice(1, 0, {
        label: t('file.category'),
        key: 'category',
        type: 'select',
        props: {
          placeholder: t('file.categoryAll'),
          clearable: true,
          options: categoryOptions.value
        }
      })
    }
    return items
  })

  function handleReset() {
    emit('reset')
  }

  async function handleSearch() {
    await searchBarRef.value?.validate()
    emit('search')
  }
</script>
