<template>
  <ElDialog
    v-model="dialogVisible"
    :title="dialogType === 'add' ? t('system.notice.addTitle') : t('system.notice.editTitle')"
    width="60%"
    align-center
  >
    <ElForm
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
      aria-label="通知公告表单"
    >
      <ElFormItem :label="t('system.notice.noticeTitle')" prop="noticeTitle">
        <ElInput
          v-model="formData.noticeTitle"
          :placeholder="t('system.notice.titlePlaceholder')"
        />
      </ElFormItem>
      <ElFormItem :label="t('system.notice.noticeType')" prop="noticeType">
        <ElSelect v-model="formData.noticeType" :placeholder="t('common.pleaseSelect')">
          <ElOption :label="t('system.notice.typeNotice')" value="1" />
          <ElOption :label="t('system.notice.typeAnnounce')" value="2" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem :label="t('system.notice.noticeStatus')" prop="status">
        <ElRadioGroup v-model="formData.status">
          <ElRadio value="0">{{ t('common.normal') }}</ElRadio>
          <ElRadio value="1">{{ t('common.disabled') }}</ElRadio>
        </ElRadioGroup>
      </ElFormItem>
      <ElFormItem :label="t('system.notice.noticeContent')" prop="noticeContent">
        <art-wang-editor v-model="formData.noticeContent" />
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

<script setup lang="ts">
  import { ElMessage } from 'element-plus'
  import type { FormInstance, FormRules } from 'element-plus'
  import { useI18n } from 'vue-i18n'
  import {
    fetchAddNotice,
    fetchUpdateNotice,
    fetchGetNoticeById,
    type Notice
  } from '@/api/system/notice/notice'
  import { handleMutationError } from '@/utils/http/mutation'
  import { DialogType } from '@/types'

  interface Props {
    visible: boolean
    type: DialogType
    noticeData?: Partial<Notice>
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
    (e: 'submit'): void
  }

  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()
  const { t } = useI18n()

  const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
  })

  const dialogType = computed(() => props.type)

  const formRef = ref<FormInstance>()

  const formData = reactive({
    noticeTitle: '',
    noticeType: '',
    noticeContent: '',
    status: 0
  })

  const rules = computed<FormRules>(() => ({
    noticeTitle: [{ required: true, message: t('system.notice.titleRequired'), trigger: 'blur' }],
    noticeType: [{ required: true, message: t('system.notice.typeRequired'), trigger: 'change' }]
  }))

  const initFormData = async () => {
    const isEdit = props.type === 'edit' && props.noticeData

    if (isEdit && props.noticeData?.noticeId) {
      try {
        const detail = await fetchGetNoticeById(props.noticeData.noticeId)
        console.log('通知公告详情数据:', detail)
        if (detail) {
          Object.assign(formData, {
            noticeTitle: detail.noticeTitle || '',
            noticeType: detail.noticeType || '',
            noticeContent: detail.noticeContent || '',
            status: detail.status || '0'
          })
        }
      } catch (error) {
        console.error('获取通知公告详情失败:', error)
        handleMutationError(error, t('system.notice.loadFail'))
        const row = props.noticeData
        Object.assign(formData, {
          noticeTitle: row.noticeTitle || '',
          noticeType: row.noticeType || '',
          noticeContent: row.noticeContent || '',
          status: row.status || '0'
        })
      }
    } else {
      Object.assign(formData, {
        noticeTitle: '',
        noticeType: '',
        noticeContent: '',
        status: '0'
      })
    }
  }

  watch(
    () => [props.visible, props.type, props.noticeData],
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
          const submitData: Notice = {
            noticeTitle: formData.noticeTitle,
            noticeType: formData.noticeType,
            noticeContent: formData.noticeContent,
            status: formData.status.toString()
          }

          if (dialogType.value === 'add') {
            await fetchAddNotice(submitData)
          } else {
            submitData.noticeId = props.noticeData?.noticeId
            await fetchUpdateNotice(submitData)
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
      max-height: 60vh;
      padding: 24px;
      overflow-y: auto;
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
      box-shadow: var(--art-shadow-sm);
    }
  }

  :deep(.el-select) {
    width: 100%;

    .el-select__wrapper {
      border-radius: 8px;
      transition: all 0.3s ease;

      &:hover {
        box-shadow: var(--art-shadow-sm);
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
</style>
