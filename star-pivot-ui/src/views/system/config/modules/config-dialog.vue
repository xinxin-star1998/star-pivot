<template>
  <ElDialog
    v-model="dialogVisible"
    :title="dialogType === 'add' ? t('system.config.addTitle') : t('system.config.editTitle')"
    align-center
    :width="dialogWidth || '480px'"
  >
    <ElForm
      ref="formRef"
      :model="formData"
      :rules="rules"
      :label-width="labelWidth"
      :label-position="labelPosition"
    >
      <ElFormItem :label="t('system.config.configName')" prop="configName">
        <ElInput v-model="formData.configName" :placeholder="t('system.config.namePlaceholder')" />
      </ElFormItem>
      <ElFormItem :label="t('system.config.configKey')" prop="configKey">
        <ElInput v-model="formData.configKey" :placeholder="t('system.config.keyPlaceholder')" />
      </ElFormItem>
      <ElFormItem :label="t('system.config.configValue')" prop="configValue">
        <ElInput
          v-model="formData.configValue"
          :rows="4"
          :placeholder="t('system.config.valuePlaceholder')"
          type="textarea"
        />
      </ElFormItem>
      <ElFormItem :label="t('system.config.builtIn')" prop="configType">
        <ElRadioGroup v-model="formData.configType">
          <ElRadio value="Y">{{ t('system.config.yes') }}</ElRadio>
          <ElRadio value="N">{{ t('system.config.no') }}</ElRadio>
        </ElRadioGroup>
      </ElFormItem>
      <ElFormItem :label="t('common.remark')" prop="remark">
        <ElInput
          v-model="formData.remark"
          :rows="4"
          :placeholder="t('system.config.remarkPlaceholder')"
          type="textarea"
        />
      </ElFormItem>
    </ElForm>
    <template #footer>
      <div class="dialog-footer">
        <ElButton @click="dialogVisible = false">{{ t('common.cancel') }}</ElButton>
        <ElButton type="primary" @click="handleSubmit">{{ t('common.submit') }}</ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<script lang="ts" setup>
  import type { FormInstance, FormRules } from 'element-plus'
  import { ElMessage } from 'element-plus'
  import { useI18n } from 'vue-i18n'
  import { useMobileFormLayout } from '@/hooks/core/useMobileFormLayout'
  import {
    type Config,
    fetchAddConfig,
    fetchGetConfigById,
    fetchUpdateConfig
  } from '@/api/system/config/config'
  import { handleMutationError } from '@/utils/http/mutation'

  interface Props {
    visible: boolean
    type: string
    configData?: Partial<Config>
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void

    (e: 'submit'): void
  }

  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()
  const { t } = useI18n()
  const { labelPosition, labelWidth, dialogWidth } = useMobileFormLayout({
    desktopWidth: '100px'
  })

  const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
  })

  const dialogType = computed(() => props.type)

  const formRef = ref<FormInstance>()

  const formData = reactive({
    configId: undefined as number | undefined,
    configName: '',
    configKey: '',
    configValue: '',
    configType: 'N',
    remark: ''
  })

  const rules = computed<FormRules>(() => ({
    configName: [{ required: true, message: t('system.config.nameRequired'), trigger: 'blur' }],
    configKey: [{ required: true, message: t('system.config.keyRequired'), trigger: 'blur' }],
    configValue: [{ required: true, message: t('system.config.valueRequired'), trigger: 'blur' }],
    configType: [{ required: true, message: t('common.pleaseSelect'), trigger: 'change' }]
  }))

  const initFormData = async () => {
    const isEdit = props.type === 'edit' && props.configData

    if (isEdit && props.configData?.configId) {
      try {
        const detail = await fetchGetConfigById(props.configData.configId)
        console.log('参数配置详情数据:', detail)
        if (detail) {
          Object.assign(formData, {
            configId: detail.configId,
            configName: detail.configName || '',
            configKey: detail.configKey || '',
            configValue: detail.configValue || '',
            configType: detail.configType || 'N',
            remark: detail.remark || ''
          })
        }
      } catch (error) {
        console.error('获取参数配置详情失败:', error)
        handleMutationError(error, t('system.config.loadFail'))
        const row = props.configData
        Object.assign(formData, {
          configId: row.configId,
          configName: row.configName || '',
          configKey: row.configKey || '',
          configValue: row.configValue || '',
          configType: row.configType || 'N',
          remark: row.remark || ''
        })
      }
    } else {
      Object.assign(formData, {
        configId: undefined,
        configName: '',
        configKey: '',
        configValue: '',
        configType: 'N',
        remark: ''
      })
    }
  }

  watch(
    () => [props.visible, props.type, props.configData],
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

    await formRef.value.validate(async (valid) => {
      if (valid) {
        try {
          const submitData: any = {
            configId: formData.configId,
            configName: formData.configName,
            configKey: formData.configKey,
            configValue: formData.configValue,
            configType: formData.configType,
            remark: formData.remark
          }

          if (dialogType.value === 'add') {
            await fetchAddConfig(submitData)
          } else {
            submitData.configId = props.configData?.configId || formData.configId
            await fetchUpdateConfig(submitData)
          }
          ElMessage.success(
            dialogType.value === 'add' ? t('common.addSuccess') : t('common.updateSuccess')
          )
          dialogVisible.value = false
          emit('submit')
        } catch (error) {
          console.error('提交失败:', error)
          handleMutationError(
            error,
            dialogType.value === 'add' ? t('common.addFail') : t('common.updateFail')
          )
        }
      }
    })
  }
</script>

<style lang="scss" scoped>
  .dialog-footer {
    display: flex;
    gap: 10px;
    justify-content: flex-end;
  }
</style>
