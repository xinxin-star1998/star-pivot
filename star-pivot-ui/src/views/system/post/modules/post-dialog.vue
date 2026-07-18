<template>
  <ElDialog
    v-model="dialogVisible"
    :title="dialogType === 'add' ? t('system.post.addTitle') : t('system.post.editTitle')"
    :width="dialogWidth || '480px'"
    align-center
  >
    <ElForm
      ref="formRef"
      :model="formData"
      :rules="rules"
      :label-width="labelWidth"
      :label-position="labelPosition"
      aria-label="岗位信息表单"
    >
      <ElFormItem :label="t('system.post.postCode')" prop="postCode">
        <ElInput v-model="formData.postCode" :placeholder="t('system.post.postCodePlaceholder')" />
      </ElFormItem>
      <ElFormItem :label="t('system.post.postName')" prop="postName">
        <ElInput v-model="formData.postName" :placeholder="t('system.post.postNamePlaceholder')" />
      </ElFormItem>
      <ElFormItem :label="t('system.post.postSort')" prop="postSort">
        <ElInputNumber
          v-model="formData.postSort"
          :min="0"
          :max="999"
          :placeholder="t('common.pleaseInput')"
          style="width: 100%"
        />
      </ElFormItem>
      <ElFormItem :label="t('common.status')" prop="status">
        <ElRadioGroup v-model="formData.status">
          <ElRadio :value="'0'">{{ t('common.normal') }}</ElRadio>
          <ElRadio :value="'1'">{{ t('common.disabled') }}</ElRadio>
        </ElRadioGroup>
      </ElFormItem>
      <ElFormItem :label="t('common.remark')" prop="remark">
        <ElInput
          type="textarea"
          v-model="formData.remark"
          :rows="4"
          :placeholder="t('common.pleaseInput')"
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

<script setup lang="ts">
  import type { FormInstance, FormRules } from 'element-plus'
  import { ElMessage } from 'element-plus'
  import { useI18n } from 'vue-i18n'
  import { useMobileFormLayout } from '@/hooks/core/useMobileFormLayout'
  import { fetchAddPost, fetchGetPostById, fetchUpdatePost, type SysPost } from '@/api/post/post'
  import { handleMutationError } from '@/utils/http/mutation'

  interface Props {
    visible: boolean
    type: string
    postData?: Partial<Api.Post.PostListItem>
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
    postId: 0,
    postCode: '',
    postName: '',
    postSort: 0,
    status: '0',
    remark: ''
  })

  const rules = computed<FormRules>(() => ({
    postCode: [
      { required: true, message: t('system.post.postCodeRequired'), trigger: 'blur' },
      { min: 2, max: 50, message: t('common.pleaseInput'), trigger: 'blur' }
    ],
    postName: [
      { required: true, message: t('system.post.postNameRequired'), trigger: 'blur' },
      { min: 2, max: 50, message: t('common.pleaseInput'), trigger: 'blur' }
    ],
    postSort: [{ required: true, message: t('common.pleaseInput'), trigger: 'blur' }],
    status: [{ required: true, message: t('common.pleaseSelect'), trigger: 'change' }]
  }))

  const initFormData = async () => {
    const isEdit = props.type === 'edit' && props.postData

    if (isEdit && props.postData?.postId) {
      try {
        const postDetail = await fetchGetPostById(props.postData.postId)
        if (postDetail) {
          Object.assign(formData, {
            postId: postDetail.postId || 0,
            postCode: postDetail.postCode || '',
            postName: postDetail.postName || '',
            postSort: postDetail.postSort || 0,
            status: postDetail.status !== undefined ? postDetail.status.toString() : '0',
            remark: postDetail.remark || ''
          })
        }
      } catch (error) {
        console.error('获取岗位详情失败:', error)
        handleMutationError(error, t('system.post.loadFail'))
        const row = props.postData
        Object.assign(formData, {
          postId: row.postId || 0,
          postCode: row.postCode || '',
          postName: row.postName || '',
          postSort: row.postSort || 0,
          status: row.status !== undefined ? row.status.toString() : '0',
          remark: row.remark || ''
        })
      }
    } else {
      Object.assign(formData, {
        postId: 0,
        postCode: '',
        postName: '',
        postSort: 0,
        status: '0',
        remark: ''
      })
    }
  }

  watch(
    () => [props.visible, props.type, props.postData],
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
          const submitData: SysPost = {
            postCode: formData.postCode,
            postName: formData.postName,
            postSort: formData.postSort,
            status: formData.status,
            remark: formData.remark
          }

          if (dialogType.value === 'add') {
            await fetchAddPost(submitData)
          } else {
            submitData.postId = formData.postId
            await fetchUpdatePost(submitData)
          }
          ElMessage.success(
            dialogType.value === 'add' ? t('common.addSuccess') : t('common.updateSuccess')
          )
          dialogVisible.value = false
          emit('submit')
        } catch (error) {
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
      box-shadow: var(--art-shadow-sm);
    }
  }

  :deep(.el-input-number) {
    width: 100%;

    .el-input__wrapper {
      border-radius: 8px;
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
