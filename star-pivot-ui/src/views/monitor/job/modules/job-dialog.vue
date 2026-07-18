<template>
  <ElDialog
    v-model="dialogVisible"
    :title="dialogType === 'add' ? t('monitor.job.addTitle') : t('monitor.job.editTitle')"
    :width="dialogWidth || '560px'"
    align-center
  >
    <ElForm
      ref="formRef"
      :model="formData"
      :rules="rules"
      :label-width="labelWidth"
      :label-position="labelPosition"
      aria-label="job form"
    >
      <ElFormItem :label="t('monitor.job.jobName')" prop="jobName">
        <ElInput v-model="formData.jobName" :placeholder="t('monitor.job.jobNamePlaceholder')" />
      </ElFormItem>
      <ElFormItem :label="t('monitor.job.jobGroup')" prop="jobGroup">
        <ElInput v-model="formData.jobGroup" placeholder="DEFAULT" />
      </ElFormItem>
      <ElFormItem :label="t('monitor.job.invokeTarget')" prop="invokeTarget">
        <ElInput
          v-model="formData.invokeTarget"
          type="textarea"
          :rows="2"
          :placeholder="t('monitor.job.invokeTargetPlaceholder')"
        />
      </ElFormItem>
      <ElFormItem :label="t('monitor.job.cronExpression')" prop="cronExpression">
        <ElInput v-model="formData.cronExpression" :placeholder="t('monitor.job.cronPlaceholder')">
          <template #append>
            <ElButton type="primary" @click="handleTestCronExpression">{{
              t('monitor.job.cronEditor')
            }}</ElButton>
          </template>
        </ElInput>
      </ElFormItem>
      <ElFormItem :label="t('monitor.job.misfirePolicy')" prop="misfirePolicy">
        <ElSelect v-model="formData.misfirePolicy" :placeholder="t('common.pleaseSelect')">
          <ElOption :label="t('monitor.job.misfireFire')" value="1" />
          <ElOption :label="t('monitor.job.misfireDefault')" value="2" />
          <ElOption :label="t('monitor.job.misfireIgnore')" value="3" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem :label="t('monitor.job.concurrent')" prop="concurrent">
        <ElRadioGroup v-model="formData.concurrent">
          <ElRadio value="0">{{ t('monitor.job.concurrentAllow') }}</ElRadio>
          <ElRadio value="1">{{ t('monitor.job.concurrentForbid') }}</ElRadio>
        </ElRadioGroup>
      </ElFormItem>
      <ElFormItem :label="t('monitor.job.status')" prop="status">
        <ElRadioGroup v-model="formData.status">
          <ElRadio value="0">{{ t('common.normal') }}</ElRadio>
          <ElRadio value="1">{{ t('monitor.job.pause') }}</ElRadio>
        </ElRadioGroup>
      </ElFormItem>
      <ElFormItem :label="t('common.remark')" prop="remark">
        <ElInput
          v-model="formData.remark"
          type="textarea"
          :rows="2"
          :placeholder="t('common.pleaseInput')"
        />
      </ElFormItem>
    </ElForm>

    <CronEditorDialog
      v-model="cronDialogVisible"
      :value="formData.cronExpression"
      @confirm="applyCron"
    />
    <template #footer>
      <ElButton @click="dialogVisible = false">{{ t('common.cancel') }}</ElButton>
      <ElButton type="primary" @click="handleSubmit">{{ t('common.confirm') }}</ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import type { FormInstance, FormRules } from 'element-plus'
  import { ElMessage } from 'element-plus'
  import { useI18n } from 'vue-i18n'
  import { useMobileFormLayout } from '@/hooks/core/useMobileFormLayout'
  import { fetchAddJob, fetchJobById, fetchUpdateJob, type SysJob } from '@/api/monitor/job'
  import type { DialogType } from '@/types'
  import CronEditorDialog from './cron-editor-dialog.vue'

  interface Props {
    visible: boolean
    type: DialogType
    jobData?: Partial<SysJob>
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
    (e: 'submit'): void
  }

  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()
  const { t } = useI18n()
  const { labelPosition, labelWidth, dialogWidth } = useMobileFormLayout({
    desktopWidth: '120px'
  })

  const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
  })

  const dialogType = computed(() => props.type)

  const formRef = ref<FormInstance>()
  const formData = reactive<Partial<SysJob>>({
    jobName: '',
    jobGroup: 'DEFAULT',
    invokeTarget: '',
    cronExpression: '',
    misfirePolicy: '3',
    concurrent: '1',
    status: '0',
    remark: ''
  })

  const rules = computed<FormRules>(() => ({
    jobName: [{ required: true, message: t('monitor.job.jobNamePlaceholder'), trigger: 'blur' }],
    invokeTarget: [
      { required: true, message: t('monitor.job.invokeTargetPlaceholder'), trigger: 'blur' }
    ],
    cronExpression: [{ required: true, message: t('monitor.job.cronPlaceholder'), trigger: 'blur' }]
  }))

  const cronDialogVisible = ref(false)

  const applyCron = (expression: string) => {
    formData.cronExpression = expression
    ElMessage.success(t('common.updateSuccess'))
  }

  const initFormData = async () => {
    const isEdit = props.type === 'edit' && props.jobData?.jobId
    Object.assign(formData, {
      jobName: '',
      jobGroup: 'DEFAULT',
      invokeTarget: '',
      cronExpression: '',
      misfirePolicy: '3',
      concurrent: '1',
      status: '0',
      remark: ''
    })
    if (isEdit && props.jobData?.jobId) {
      const res = await fetchJobById(props.jobData.jobId)
      Object.assign(formData, res)
    } else if (props.jobData) {
      Object.assign(formData, props.jobData)
    }
  }

  watch(
    () => props.visible,
    (v) => {
      if (v) {
        initFormData()
      }
    }
  )

  const handleSubmit = async () => {
    await formRef.value?.validate()
    try {
      if (props.type === 'add') {
        await fetchAddJob(formData as SysJob)
        ElMessage.success(t('common.addSuccess'))
      } else {
        await fetchUpdateJob(formData as SysJob)
        ElMessage.success(t('common.updateSuccess'))
      }
      emit('submit')
      dialogVisible.value = false
    } catch (e) {
      console.error(e)
      ElMessage.error(props.type === 'add' ? t('common.addFail') : t('common.updateFail'))
    }
  }

  const handleTestCronExpression = () => {
    cronDialogVisible.value = true
  }
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
</style>
