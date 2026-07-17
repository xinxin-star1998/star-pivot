<template>
  <ElDialog
    :model-value="visible"
    :title="form.configId ? t('ai.config.editConfig') : t('ai.config.addConfig')"
    width="760px"
    destroy-on-close
    @close="emit('update:visible', false)"
  >
    <ElForm ref="formRef" :model="form" :rules="rules" label-width="110px">
      <ElDivider content-position="left">{{ t('ai.config.basicInfo') }}</ElDivider>
      <ElRow :gutter="16">
        <ElCol :span="12">
          <ElFormItem :label="t('ai.config.name')" prop="configName">
            <ElInput
              v-model="form.configName"
              :placeholder="t('ai.config.configNamePlaceholder')"
            />
          </ElFormItem>
        </ElCol>
        <ElCol :span="12">
          <ElFormItem :label="t('ai.config.botName')" prop="botName">
            <ElInput v-model="form.botName" :placeholder="t('ai.config.botNameDefault')" />
          </ElFormItem>
        </ElCol>
      </ElRow>
      <ElFormItem :label="t('ai.config.botAvatar')">
        <ElInput v-model="form.botAvatar" :placeholder="t('ai.config.botAvatarPlaceholder')" />
      </ElFormItem>
      <ElRow :gutter="16">
        <ElCol :span="12">
          <ElFormItem :label="t('common.status')">
            <ElRadioGroup v-model="form.status">
              <ElRadio value="0">{{ t('common.normal') }}</ElRadio>
              <ElRadio value="1">{{ t('common.disabled') }}</ElRadio>
            </ElRadioGroup>
          </ElFormItem>
        </ElCol>
        <ElCol :span="12">
          <ElFormItem :label="t('ai.config.setDefault')">
            <ElSwitch
              v-model="isDefaultSwitch"
              :active-text="t('common.yes')"
              :inactive-text="t('common.no')"
            />
          </ElFormItem>
        </ElCol>
      </ElRow>

      <ElDivider content-position="left">{{ t('ai.config.chatSettings') }}</ElDivider>
      <ElFormItem :label="t('ai.config.welcomeMessage')">
        <ElInput
          v-model="form.welcomeMessage"
          type="textarea"
          :rows="2"
          :placeholder="t('ai.config.welcomeMessagePlaceholder')"
        />
      </ElFormItem>
      <ElFormItem :label="t('ai.config.systemPrompt')" prop="systemPrompt">
        <ElInput
          v-model="form.systemPrompt"
          :rows="6"
          :placeholder="t('ai.config.systemPromptPlaceholder')"
          type="textarea"
        />
      </ElFormItem>
      <ElRow :gutter="16">
        <ElCol :span="8">
          <ElFormItem :label="t('ai.config.defaultModel')" prop="defaultModel">
            <ElInput v-model="form.defaultModel" placeholder="deepseek-chat" />
          </ElFormItem>
        </ElCol>
        <ElCol :span="8">
          <ElFormItem :label="t('ai.config.defaultTemperature')">
            <ElInputNumber
              v-model="form.defaultTemperature"
              :min="0"
              :max="2"
              :step="0.1"
              :precision="2"
              class="!w-full"
            />
          </ElFormItem>
        </ElCol>
        <ElCol :span="8">
          <ElFormItem :label="t('ai.config.maxMemoryMessages')">
            <ElInputNumber v-model="form.maxMemoryMessages" :min="1" :max="200" class="!w-full" />
          </ElFormItem>
        </ElCol>
      </ElRow>

      <ElDivider content-position="left">{{ t('ai.config.ragSection') }}</ElDivider>
      <ElRow :gutter="16">
        <ElCol :span="12">
          <ElFormItem :label="t('ai.config.ragEnabled')">
            <ElSwitch
              v-model="ragEnabledSwitch"
              :active-text="t('ai.config.enabled')"
              :inactive-text="t('ai.config.disabled')"
            />
          </ElFormItem>
        </ElCol>
        <ElCol :span="12">
          <ElFormItem :label="t('ai.knowledge.topK')">
            <ElInputNumber v-model="form.ragTopK" :min="1" :max="20" class="!w-full" />
          </ElFormItem>
        </ElCol>
      </ElRow>

      <ElDivider content-position="left">{{ t('ai.config.optionalModels') }}</ElDivider>
      <div class="mb-2 flex justify-end">
        <ElButton type="primary" link @click="addModelRow">{{ t('ai.config.addModel') }}</ElButton>
      </div>
      <div v-if="form.models.length" class="space-y-2">
        <div v-for="(model, index) in form.models" :key="index" class="flex items-center gap-2">
          <ElInput v-model="model.id" :placeholder="t('ai.config.modelId')" class="flex-1" />
          <ElInput v-model="model.label" :placeholder="t('ai.config.modelLabel')" class="flex-1" />
          <ElButton type="danger" link @click="removeModelRow(index)">
            {{ t('common.delete') }}
          </ElButton>
        </div>
      </div>
      <div v-else class="py-2 text-xs text-g-500">{{ t('ai.config.noOptionalModels') }}</div>

      <ElFormItem :label="t('common.remark')" class="mt-4">
        <ElInput v-model="form.remark" type="textarea" :rows="2" />
      </ElFormItem>
    </ElForm>

    <template #footer>
      <ElButton @click="emit('update:visible', false)">{{ t('common.cancel') }}</ElButton>
      <ElButton type="primary" :loading="saving" @click="handleSubmit">{{
        t('ai.common.save')
      }}</ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import type { FormInstance, FormRules } from 'element-plus'
  import { useI18n } from 'vue-i18n'
  import type { AiConfigItem, AiConfigSavePayload } from '@/api/ai/config'

  const props = defineProps<{
    visible: boolean
    saving: boolean
    configData?: AiConfigItem | null
  }>()

  const emit = defineEmits<{
    'update:visible': [visible: boolean]
    submit: [payload: AiConfigSavePayload]
  }>()

  const { t } = useI18n()

  const formRef = ref<FormInstance>()

  const createEmptyForm = (): AiConfigSavePayload => ({
    configName: '',
    botName: t('ai.config.botNameDefault'),
    botAvatar: '',
    welcomeMessage: '',
    systemPrompt: '',
    defaultModel: 'deepseek-chat',
    defaultTemperature: 0.7,
    maxMemoryMessages: 30,
    models: [],
    ragEnabled: '1',
    ragTopK: 5,
    isDefault: '1',
    status: '0',
    remark: ''
  })

  const form = ref<AiConfigSavePayload>(createEmptyForm())

  const isDefaultSwitch = computed({
    get: () => form.value.isDefault === '0',
    set: (value: boolean) => {
      form.value.isDefault = value ? '0' : '1'
    }
  })

  const ragEnabledSwitch = computed({
    get: () => form.value.ragEnabled === '0',
    set: (value: boolean) => {
      form.value.ragEnabled = value ? '0' : '1'
    }
  })

  const rules = computed<FormRules>(() => ({
    configName: [{ required: true, message: t('ai.config.nameRequired'), trigger: 'blur' }],
    botName: [{ required: true, message: t('ai.config.botNameRequired'), trigger: 'blur' }],
    systemPrompt: [
      { required: true, message: t('ai.config.systemPromptRequired'), trigger: 'blur' }
    ],
    defaultModel: [
      { required: true, message: t('ai.config.defaultModelRequired'), trigger: 'blur' }
    ]
  }))

  watch(
    () => props.visible,
    (visible) => {
      if (!visible) return
      if (props.configData?.configId) {
        form.value = {
          configId: props.configData.configId,
          configName: props.configData.configName || '',
          botName: props.configData.botName || t('ai.config.botNameDefault'),
          botAvatar: props.configData.botAvatar || '',
          welcomeMessage: props.configData.welcomeMessage || '',
          systemPrompt: props.configData.systemPrompt || '',
          defaultModel: props.configData.defaultModel || 'deepseek-chat',
          defaultTemperature: props.configData.defaultTemperature ?? 0.7,
          maxMemoryMessages: props.configData.maxMemoryMessages ?? 30,
          models: (props.configData.models || []).map((item) => ({ ...item })),
          ragEnabled: props.configData.ragEnabled || '1',
          ragTopK: props.configData.ragTopK ?? 5,
          isDefault: props.configData.isDefault || '1',
          status: props.configData.status || '0',
          remark: props.configData.remark || ''
        }
      } else {
        form.value = createEmptyForm()
      }
      nextTick(() => formRef.value?.clearValidate())
    }
  )

  function addModelRow(): void {
    form.value.models = form.value.models || []
    form.value.models.push({ id: '', label: '' })
  }

  function removeModelRow(index: number): void {
    form.value.models?.splice(index, 1)
  }

  async function handleSubmit(): Promise<void> {
    const valid = await formRef.value?.validate().catch(() => false)
    if (!valid) return
    emit('submit', {
      ...form.value,
      models: (form.value.models || []).filter((item) => item.id?.trim())
    })
  }
</script>
