<template>
  <ElDialog
    :title="dialogTitle"
    :model-value="visible"
    @update:model-value="handleCancel"
    width="700px"
    align-center
    class="dict-data-dialog"
    @closed="handleClosed"
  >
    <ArtForm
      ref="formRef"
      v-model="form"
      :items="formItems"
      :rules="rules"
      :span="width > 640 ? 12 : 24"
      :gutter="20"
      label-width="auto"
      :show-reset="false"
      :show-submit="false"
    >
      <template #i18nNames>
        <div class="dict-i18n-fields">
          <div v-for="lang in nonDefaultLangs" :key="lang.langCode" class="dict-i18n-row">
            <span class="dict-i18n-label">{{ lang.langName }}</span>
            <ElInput
              v-model="form.translations![lang.langCode]"
              :placeholder="t('system.dict.labelPlaceholder')"
              clearable
              maxlength="100"
            />
          </div>
        </div>
      </template>
    </ArtForm>

    <template #footer>
      <span class="dialog-footer">
        <ElButton @click="handleCancel">{{ t('common.cancel') }}</ElButton>
        <ElButton type="primary" @click="handleSubmit">{{ t('common.confirm') }}</ElButton>
      </span>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import type { FormRules } from 'element-plus'
  import { ElMessage } from 'element-plus'
  import type { FormItem } from '@/components/core/forms/art-form/index.vue'
  import ArtForm from '@/components/core/forms/art-form/index.vue'
  import { useWindowSize } from '@vueuse/core'
  import type { DictDataFormData } from '@/api/dict/data'
  import { fetchGetDictDataById } from '@/api/dict/data'
  import { fetchI18nLangList, type SysLang } from '@/api/system/i18n'
  import { useI18n } from 'vue-i18n'

  const { width } = useWindowSize()
  const { t } = useI18n()

  interface Props {
    visible: boolean
    editData?: DictDataFormData | null
    dictType?: string
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
    (e: 'submit', data: DictDataFormData): void
  }

  const props = withDefaults(defineProps<Props>(), {
    visible: false,
    editData: null,
    dictType: ''
  })

  const emit = defineEmits<Emits>()

  const formRef = ref()
  const isEdit = ref(false)
  const langList = ref<SysLang[]>([])

  const form = reactive<DictDataFormData>({
    dictSort: 0,
    dictLabel: '',
    dictValue: '',
    dictType: '',
    cssClass: '',
    listClass: '',
    isDefault: 'N',
    status: '0',
    remark: '',
    translations: {}
  })

  const defaultLangCode = computed(() => {
    const def = langList.value.find((l) => l.isDefault === '1')
    return def?.langCode || 'zh'
  })

  const defaultLangName = computed(() => {
    const def = langList.value.find((l) => l.langCode === defaultLangCode.value)
    return def?.langName || '简体中文'
  })

  const nonDefaultLangs = computed(() =>
    langList.value.filter((l) => l.langCode !== defaultLangCode.value && l.status === '0')
  )

  const dialogTitle = computed(() => {
    return isEdit.value ? t('system.dict.editData') : t('system.dict.addData')
  })

  const rules = computed<FormRules>(() => ({
    dictLabel: [
      { required: true, message: t('system.dict.labelRequired'), trigger: 'blur' },
      { max: 100, message: t('common.pleaseInput'), trigger: 'blur' }
    ],
    dictValue: [
      { required: true, message: t('system.dict.valueRequired'), trigger: 'blur' },
      { max: 100, message: t('common.pleaseInput'), trigger: 'blur' }
    ],
    dictType: [{ required: true, message: t('common.pleaseSelect'), trigger: 'change' }],
    dictSort: [
      { required: true, message: t('common.pleaseInput'), trigger: 'blur' },
      { type: 'number', min: 0, message: t('common.pleaseInput'), trigger: 'blur' }
    ],
    status: [{ required: true, message: t('common.pleaseSelect'), trigger: 'change' }]
  }))

  const formItems = computed<FormItem[]>(() => {
    const switchSpan = width.value < 768 ? 24 : 12
    return [
      {
        label: t('system.dict.dictType'),
        key: 'dictType',
        type: 'input',
        props: {
          placeholder: t('system.dict.typePlaceholder'),
          disabled: !!props.dictType || isEdit.value
        }
      },
      {
        label: `${t('system.dict.dictLabel')}（${defaultLangName.value}）`,
        key: 'dictLabel',
        type: 'input',
        props: { placeholder: t('system.dict.labelPlaceholder') }
      },
      ...(nonDefaultLangs.value.length
        ? ([
            {
              label: t('system.dict.translations'),
              key: 'i18nNames',
              span: 24
            }
          ] as FormItem[])
        : []),
      {
        label: t('system.dict.dictValue'),
        key: 'dictValue',
        type: 'input',
        props: { placeholder: t('system.dict.valuePlaceholder') }
      },
      {
        label: t('system.dict.dictSort'),
        key: 'dictSort',
        type: 'number',
        props: {
          min: 0,
          controlsPosition: 'right',
          style: { width: '100%' }
        }
      },
      {
        label: t('system.dict.cssClass'),
        key: 'cssClass',
        type: 'select',
        props: {
          placeholder: t('common.pleaseSelect'),
          options: [
            { label: 'primary', value: 'primary' },
            { label: 'success', value: 'success' },
            { label: 'warning', value: 'warning' },
            { label: 'danger', value: 'danger' }
          ]
        }
      },
      {
        label: t('system.dict.listClass'),
        key: 'listClass',
        type: 'select',
        props: {
          placeholder: t('common.pleaseSelect'),
          options: [
            { label: 'default', value: 'default' },
            { label: 'primary', value: 'primary' },
            { label: 'success', value: 'success' },
            { label: 'info', value: 'info' },
            { label: 'warning', value: 'warning' },
            { label: 'danger', value: 'danger' }
          ]
        }
      },
      {
        label: t('system.dict.isDefault'),
        key: 'isDefault',
        type: 'radiogroup',
        span: switchSpan,
        props: {
          options: [
            { label: t('common.yes'), value: 'Y' },
            { label: t('common.no'), value: 'N' }
          ],
          size: 'default',
          direction: 'horizontal'
        }
      },
      {
        label: t('common.status'),
        key: 'status',
        type: 'radiogroup',
        span: switchSpan,
        props: {
          options: [
            { label: t('common.normal'), value: '0' },
            { label: t('common.disabled'), value: '1' }
          ],
          size: 'default',
          direction: 'horizontal'
        }
      },
      {
        label: t('common.remark'),
        key: 'remark',
        type: 'input',
        span: 24,
        props: { type: 'textarea', rows: 3, placeholder: t('common.pleaseInput') }
      }
    ]
  })

  const loadLangs = async (): Promise<void> => {
    try {
      langList.value = (await fetchI18nLangList()) || []
    } catch {
      langList.value = []
    }
  }

  const loadFormData = async (): Promise<void> => {
    if (!props.editData) return

    isEdit.value = true
    const emptyTranslations: Record<string, string> = {}
    nonDefaultLangs.value.forEach((lang) => {
      emptyTranslations[lang.langCode] = ''
    })

    let translations = { ...emptyTranslations }
    let detail = props.editData
    if (props.editData.dictCode) {
      try {
        const res = await fetchGetDictDataById(props.editData.dictCode)
        if (res) {
          detail = res
          if (res.translations) {
            Object.assign(translations, res.translations)
          }
        }
      } catch {
        // 回退使用列表行数据
      }
    }
    delete translations[defaultLangCode.value]

    Object.assign(form, {
      dictCode: detail.dictCode,
      dictSort: detail.dictSort || 0,
      dictLabel: detail.dictLabel,
      dictValue: detail.dictValue,
      dictType: detail.dictType,
      cssClass: detail.cssClass || '',
      listClass: detail.listClass || '',
      isDefault: detail.isDefault || 'N',
      status: detail.status || '0',
      remark: detail.remark || '',
      translations
    })
  }

  const resetForm = (): void => {
    const emptyTranslations: Record<string, string> = {}
    nonDefaultLangs.value.forEach((lang) => {
      emptyTranslations[lang.langCode] = ''
    })
    Object.assign(form, {
      dictCode: undefined,
      dictSort: 0,
      dictLabel: '',
      dictValue: '',
      dictType: props.dictType || '',
      cssClass: '',
      listClass: '',
      isDefault: 'N',
      status: '0',
      remark: '',
      translations: emptyTranslations
    })
    isEdit.value = false
    // 勿用 resetFields：会把已写入的 dictType 清回初始空值
    nextTick(() => {
      formRef.value?.ref?.clearValidate()
    })
  }

  const handleSubmit = async (): Promise<void> => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()

      const translations: Record<string, string> = {}
      nonDefaultLangs.value.forEach((lang) => {
        const value = form.translations?.[lang.langCode]
        if (value != null && String(value).trim()) {
          translations[lang.langCode] = String(value).trim()
        } else {
          translations[lang.langCode] = ''
        }
      })

      const submitData: DictDataFormData = {
        dictSort: form.dictSort || 0,
        dictLabel: form.dictLabel,
        dictValue: form.dictValue,
        dictType: form.dictType || props.dictType || '',
        cssClass: form.cssClass || '',
        listClass: form.listClass || '',
        isDefault: form.isDefault || 'N',
        status: form.status || '0',
        remark: form.remark || '',
        translations
      }

      if (isEdit.value && form.dictCode) {
        submitData.dictCode = form.dictCode
      }

      emit('submit', submitData)
      handleCancel()
    } catch {
      ElMessage.error(t('common.pleaseInput'))
    }
  }

  const handleCancel = (): void => {
    emit('update:visible', false)
  }

  const handleClosed = (): void => {
    resetForm()
  }

  watch(
    () => props.visible,
    async (newVal) => {
      if (newVal) {
        await loadLangs()
        await nextTick()
        if (props.editData) {
          await loadFormData()
        } else {
          resetForm()
        }
      }
    }
  )

  watch(
    () => props.dictType,
    (newVal) => {
      if (newVal && !isEdit.value) {
        form.dictType = newVal
      }
    }
  )
</script>

<style scoped lang="scss">
  :deep(.el-dialog) {
    overflow: hidden;
    border-radius: 16px;

    .el-dialog__header {
      padding: 20px 24px;
      margin: 0;
      background: linear-gradient(
        135deg,
        var(--el-color-primary-light-9) 0%,
        var(--el-color-primary-light-8) 100%
      );
      border-bottom: 1px solid var(--art-card-border);

      .el-dialog__title {
        font-size: 18px;
        font-weight: 600;
        color: var(--art-gray-900);
      }
    }

    .el-dialog__body {
      padding: 24px;
    }

    .el-dialog__footer {
      padding: 16px 24px;
      background-color: var(--art-gray-50);
      border-top: 1px solid var(--art-card-border);
    }
  }

  :deep(.el-form-item__label) {
    font-weight: 500;
    color: var(--art-gray-700);
  }

  :deep(.el-input__wrapper),
  :deep(.el-textarea__inner) {
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 2px 8px 0 rgb(0 0 0 / 8%);
    }
  }

  :deep(.el-input-number) {
    width: 100%;

    .el-input__wrapper {
      border-radius: 8px;
    }
  }

  :deep(.el-select) {
    width: 100%;

    .el-select__wrapper {
      border-radius: 8px;
      transition: all 0.3s ease;

      &:hover {
        box-shadow: 0 2px 8px 0 rgb(0 0 0 / 8%);
      }
    }
  }

  :deep(.el-radio-group) {
    .el-radio {
      margin-right: 20px;
    }
  }

  :deep(.el-button) {
    padding: 10px 24px;
    font-weight: 500;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-1px);
    }
  }

  .dialog-footer {
    display: flex;
    gap: 12px;
    justify-content: flex-end;
  }

  .dict-i18n-fields {
    display: flex;
    flex-direction: column;
    gap: 10px;
    width: 100%;
  }

  .dict-i18n-row {
    display: flex;
    gap: 12px;
    align-items: center;
  }

  .dict-i18n-label {
    flex-shrink: 0;
    width: 72px;
    font-size: 13px;
    color: var(--art-gray-600);
  }
</style>
