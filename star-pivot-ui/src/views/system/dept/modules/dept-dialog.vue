<template>
  <ElDialog
    v-model="dialogVisible"
    :title="dialogType === 'add' ? t('system.dept.addTitle') : t('system.dept.editTitle')"
    width="40%"
    align-center
  >
    <ElForm ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <ElFormItem :label="t('system.dept.parentDept')" prop="parentId">
        <ElTreeSelect
          v-model="formData.parentId"
          :data="deptTreeData"
          :props="deptTreeProps"
          :placeholder="t('system.dept.parentPlaceholder')"
          clearable
          check-strictly
          :render-after-expand="false"
          :disabled="dialogType === 'edit' && formData.deptId === formData.parentId"
        />
      </ElFormItem>
      <ElFormItem :label="t('system.dept.deptName')" prop="deptName">
        <ElInput v-model="formData.deptName" :placeholder="t('system.dept.deptNamePlaceholder')" />
      </ElFormItem>
      <ElFormItem :label="t('common.displayOrder')" prop="orderNum">
        <ElInputNumber
          v-model="formData.orderNum"
          :min="0"
          :placeholder="t('system.dept.orderPlaceholder')"
        />
      </ElFormItem>
      <ElFormItem :label="t('system.dept.leader')" prop="leader">
        <ElInput v-model="formData.leader" :placeholder="t('system.dept.leaderPlaceholder')" />
      </ElFormItem>
      <ElFormItem :label="t('system.dept.phone')" prop="phone">
        <ElInput v-model="formData.phone" :placeholder="t('system.dept.phonePlaceholder')" />
      </ElFormItem>
      <ElFormItem :label="t('system.dept.email')" prop="email">
        <ElInput v-model="formData.email" :placeholder="t('system.dept.emailPlaceholder')" />
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
          :placeholder="t('system.dept.remarkPlaceholder')"
          :rows="3"
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
  import { ElMessage, ElTreeSelect } from 'element-plus'
  import { useI18n } from 'vue-i18n'
  import {
    fetchAddDept,
    fetchGetDeptById,
    fetchGetDeptTree,
    fetchUpdateDept,
    type SysDept
  } from '@/api/dept/dept'
  import { handleMutationError } from '@/utils/http/mutation'
  import { DialogType } from '@/types'

  interface Props {
    visible: boolean
    type: DialogType
    deptData?: Partial<SysDept>
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
    (e: 'submit'): void
  }

  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()
  const { t } = useI18n()

  const deptTreeData = ref<SysDept[]>([])
  const deptTreeProps = {
    value: 'deptId',
    label: 'deptName',
    children: 'children'
  }
  const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
  })

  const dialogType = computed(() => props.type)
  const formRef = ref<FormInstance>()

  const formData = reactive({
    deptId: undefined as number | undefined,
    parentId: 0,
    deptName: '',
    orderNum: 0,
    leader: '',
    phone: '',
    email: '',
    status: '0',
    remark: ''
  })

  const rules = computed<FormRules>(() => ({
    deptName: [
      { required: true, message: t('system.dept.nameRequired'), trigger: 'blur' },
      { min: 2, max: 50, message: t('system.dept.nameLength'), trigger: 'blur' }
    ],
    phone: [
      { pattern: /^1[3-9]\d{9}$|^$/, message: t('system.dept.phoneInvalid'), trigger: 'blur' }
    ],
    email: [{ type: 'email', message: t('system.dept.emailInvalid'), trigger: 'blur' }]
  }))

  const isChildOf = (dept: SysDept, excludeId: number, allDepts: SysDept[]): boolean => {
    if (dept.deptId === excludeId) return true
    if (dept.parentId === excludeId) return true
    if (dept.parentId) {
      const parent = findDeptById(dept.parentId, allDepts)
      if (parent) {
        return isChildOf(parent, excludeId, allDepts)
      }
    }
    return false
  }

  const findDeptById = (deptId: number, depts: SysDept[]): SysDept | null => {
    for (const dept of depts) {
      if (dept.deptId === deptId) return dept
      if (dept.children && dept.children.length > 0) {
        const found = findDeptById(deptId, dept.children)
        if (found) return found
      }
    }
    return null
  }

  const filterDeptTree = (tree: SysDept[], excludeId?: number, allDepts?: SysDept[]): SysDept[] => {
    if (!excludeId) return tree
    const allDeptsList = allDepts || tree
    return tree
      .filter((dept) => {
        if (dept.deptId === excludeId) return false
        return !isChildOf(dept, excludeId, allDeptsList)
      })
      .map((dept) => {
        const cloned = { ...dept }
        if (dept.children && dept.children.length > 0) {
          cloned.children = filterDeptTree(dept.children, excludeId, allDeptsList)
        }
        return cloned
      })
  }

  const initFormData = async () => {
    const isEdit = props.type === 'edit' && props.deptData

    if (isEdit && props.deptData?.deptId) {
      try {
        const deptDetail = await fetchGetDeptById(props.deptData.deptId)
        if (deptDetail) {
          Object.assign(formData, {
            deptId: deptDetail.deptId,
            parentId: deptDetail.parentId || 0,
            deptName: deptDetail.deptName || '',
            orderNum: deptDetail.orderNum || 0,
            leader: deptDetail.leader || '',
            phone: deptDetail.phone || '',
            email: deptDetail.email || '',
            status: deptDetail.status || '0',
            remark: deptDetail.remark || ''
          })
        }
      } catch (error) {
        console.error('获取部门详情失败:', error)
        handleMutationError(error, t('system.dept.loadDetailFail'))
        Object.assign(formData, {
          deptId: props.deptData.deptId,
          parentId: props.deptData.parentId || 0,
          deptName: props.deptData.deptName || '',
          orderNum: props.deptData.orderNum || 0,
          leader: props.deptData.leader || '',
          phone: props.deptData.phone || '',
          email: props.deptData.email || '',
          status: props.deptData.status || '0',
          remark: props.deptData.remark || ''
        })
      }
    } else {
      Object.assign(formData, {
        deptId: undefined,
        parentId: props.deptData?.parentId || 0,
        deptName: '',
        orderNum: 0,
        leader: '',
        phone: '',
        email: '',
        status: '0',
        remark: ''
      })
    }
  }

  const getDeptTree = async () => {
    try {
      const res = await fetchGetDeptTree()
      if (Array.isArray(res) && res.length > 0) {
        if (props.type === 'edit' && formData.deptId) {
          deptTreeData.value = filterDeptTree(res, formData.deptId, res)
        } else {
          deptTreeData.value = res
        }
      } else {
        deptTreeData.value = []
      }
    } catch (error) {
      console.error('获取部门树失败:', error)
      deptTreeData.value = []
      handleMutationError(error, t('system.dept.loadTreeFail'))
    }
  }

  watch(
    () => [props.visible, props.type, props.deptData],
    async ([visible]) => {
      if (visible) {
        await initFormData()
        await getDeptTree()
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
          const submitData = {
            ...formData,
            parentId: formData.parentId || 0
          }

          if (dialogType.value === 'add') {
            await fetchAddDept(submitData)
            ElMessage.success(t('common.addSuccess'))
          } else {
            await fetchUpdateDept(submitData)
            ElMessage.success(t('common.updateSuccess'))
          }
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

  :deep(.el-tree-select) {
    width: 100%;
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
