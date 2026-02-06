<template>
  <ElDialog
    v-model="dialogVisible"
    :title="dialogType === 'add' ? '新增定时任务' : '编辑定时任务'"
    width="560px"
    align-center
  >
    <ElForm ref="formRef" :model="formData" :rules="rules" label-width="120px">
      <ElFormItem label="任务名称" prop="jobName">
        <ElInput v-model="formData.jobName" placeholder="请输入任务名称" />
      </ElFormItem>
      <ElFormItem label="任务组名" prop="jobGroup">
        <ElInput v-model="formData.jobGroup" placeholder="DEFAULT" />
      </ElFormItem>
      <ElFormItem label="调用目标" prop="invokeTarget">
        <ElInput
          v-model="formData.invokeTarget"
          type="textarea"
          :rows="2"
          placeholder="如：com.star.pivot.quartz.task.SampleTask.hello()"
        />
        <div class="form-tip">格式：包名.类名.方法名()，仅允许白名单包：com.star.pivot.quartz.task</div>
      </ElFormItem>
      <ElFormItem label="Cron 表达式" prop="cronExpression">
        <ElInput v-model="formData.cronExpression" placeholder="如：0 0/5 * * * ? 表示每5分钟" />
      </ElFormItem>
      <ElFormItem label="执行策略" prop="misfirePolicy">
        <ElSelect v-model="formData.misfirePolicy" placeholder="请选择">
          <ElOption label="立即执行" value="1" />
          <ElOption label="执行一次" value="2" />
          <ElOption label="放弃执行" value="3" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="是否并发" prop="concurrent">
        <ElRadioGroup v-model="formData.concurrent">
          <ElRadio value="0">允许</ElRadio>
          <ElRadio value="1">禁止</ElRadio>
        </ElRadioGroup>
      </ElFormItem>
      <ElFormItem label="状态" prop="status">
        <ElRadioGroup v-model="formData.status">
          <ElRadio value="0">正常</ElRadio>
          <ElRadio value="1">暂停</ElRadio>
        </ElRadioGroup>
      </ElFormItem>
      <ElFormItem label="备注" prop="remark">
        <ElInput v-model="formData.remark" type="textarea" :rows="2" placeholder="选填" />
      </ElFormItem>
    </ElForm>
    <template #footer>
      <ElButton @click="dialogVisible = false">取消</ElButton>
      <ElButton type="primary" @click="handleSubmit">确定</ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import { ElMessage } from 'element-plus'
  import type { FormInstance, FormRules } from 'element-plus'
  import {
    fetchAddJob,
    fetchUpdateJob,
    fetchJobById,
    type SysJob
  } from '@/api/monitor/job'
  import type { DialogType } from '@/types'

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

  const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
  })

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

  const rules: FormRules = {
    jobName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
    invokeTarget: [{ required: true, message: '请输入调用目标', trigger: 'blur' }],
    cronExpression: [{ required: true, message: '请输入 Cron 表达式', trigger: 'blur' }]
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
        ElMessage.success('新增成功')
      } else {
        await fetchUpdateJob(formData as SysJob)
        ElMessage.success('修改成功')
      }
      emit('submit')
      dialogVisible.value = false
    } catch (e) {
      console.error(e)
    }
  }
</script>

<style scoped lang="scss">
  .form-tip {
    font-size: 12px;
    color: var(--el-text-color-secondary);
    margin-top: 4px;
  }
</style>
