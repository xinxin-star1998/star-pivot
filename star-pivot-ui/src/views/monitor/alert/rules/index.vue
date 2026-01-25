<!-- 告警规则管理页面 -->
<template>
  <div class="alert-rules-page art-full-height">
    <ElCard class="art-table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>告警规则管理</span>
          <ElButton type="primary" :icon="Plus" @click="handleAdd" v-auth="'monitor:alert:add'">
            新增规则
          </ElButton>
        </div>
      </template>

      <ElTable v-loading="loading" :data="ruleList" border>
        <ElTableColumn prop="ruleName" label="规则名称" width="150" />
        <ElTableColumn prop="metricType" label="指标类型" width="120" />
        <ElTableColumn prop="metricName" label="指标名称" width="150" />
        <ElTableColumn label="阈值条件" width="150">
          <template #default="{ row }">
            <span>{{ getThresholdText(row) }}</span>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="thresholdValue" label="阈值" width="100" />
        <ElTableColumn label="告警级别" width="100">
          <template #default="{ row }">
            <ElTag :type="getAlertLevelType(row.alertLevel)">
              {{ getAlertLevelText(row.alertLevel) }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="alertChannels" label="告警渠道" width="150" />
        <ElTableColumn label="状态" width="80">
          <template #default="{ row }">
            <ElTag :type="row.enabled === '1' ? 'success' : 'info'">
              {{ row.enabled === '1' ? '启用' : '禁用' }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <ElButton link type="primary" @click="handleEdit(row)" v-auth="'monitor:alert:edit'">
              编辑
            </ElButton>
            <ElButton link type="danger" @click="handleDelete(row)" v-auth="'monitor:alert:delete'">
              删除
            </ElButton>
          </template>
        </ElTableColumn>
      </ElTable>
    </ElCard>

    <!-- 新增/编辑对话框 -->
    <ElDialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <ElForm ref="formRef" :model="formData" :rules="formRules" label-width="120px">
        <ElFormItem label="规则名称" prop="ruleName">
          <ElInput v-model="formData.ruleName" placeholder="请输入规则名称" />
        </ElFormItem>
        <ElFormItem label="指标类型" prop="metricType">
          <ElSelect v-model="formData.metricType" placeholder="请选择指标类型" style="width: 100%">
            <ElOption label="CPU使用率" value="server_cpu" />
            <ElOption label="内存使用率" value="server_memory" />
            <ElOption label="磁盘使用率" value="server_disk" />
            <ElOption label="JVM堆内存使用率" value="jvm_heap" />
            <ElOption label="Druid连接池使用率" value="druid_pool_usage" />
            <ElOption label="Redis内存使用率" value="redis_memory" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="指标名称" prop="metricName">
          <ElInput v-model="formData.metricName" placeholder="请输入指标名称" />
        </ElFormItem>
        <ElFormItem label="阈值类型" prop="thresholdType">
          <ElSelect v-model="formData.thresholdType" placeholder="请选择阈值类型" style="width: 100%">
            <ElOption label="大于" value="0" />
            <ElOption label="小于" value="1" />
            <ElOption label="等于" value="2" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="阈值" prop="thresholdValue">
          <ElInputNumber v-model="formData.thresholdValue" :precision="2" style="width: 100%" />
        </ElFormItem>
        <ElFormItem label="告警级别" prop="alertLevel">
          <ElSelect v-model="formData.alertLevel" placeholder="请选择告警级别" style="width: 100%">
            <ElOption label="低" value="0" />
            <ElOption label="中" value="1" />
            <ElOption label="高" value="2" />
            <ElOption label="紧急" value="3" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="告警渠道" prop="alertChannels">
          <ElCheckboxGroup v-model="alertChannels">
            <ElCheckbox label="in_app">站内消息</ElCheckbox>
            <ElCheckbox label="email">邮件</ElCheckbox>
            <ElCheckbox label="sms">短信</ElCheckbox>
            <ElCheckbox label="webhook">Webhook</ElCheckbox>
          </ElCheckboxGroup>
        </ElFormItem>
        <ElFormItem label="Webhook地址" prop="webhookUrl" v-if="alertChannels.includes('webhook')">
          <ElInput v-model="formData.webhookUrl" placeholder="请输入Webhook地址" />
        </ElFormItem>
        <ElFormItem label="是否启用" prop="enabled">
          <ElRadioGroup v-model="formData.enabled">
            <ElRadio label="1">启用</ElRadio>
            <ElRadio label="0">禁用</ElRadio>
          </ElRadioGroup>
        </ElFormItem>
        <ElFormItem label="备注" prop="remark">
          <ElInput v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit" :loading="submitting">确定</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  import { Plus } from '@element-plus/icons-vue'
  import { fetchGetAlertRuleList, fetchCreateAlertRule, fetchUpdateAlertRule, fetchDeleteAlertRule } from '@/api/monitor/alert'
  import type { AlertRule } from '@/types/api/monitor'
  import { ElMessage, ElMessageBox } from 'element-plus'

  defineOptions({ name: 'AlertRules' })

  const loading = ref(false)
  const submitting = ref(false)
  const ruleList = ref<AlertRule[]>([])
  const dialogVisible = ref(false)
  const dialogTitle = ref('新增告警规则')
  const formRef = ref()
  const alertChannels = ref<string[]>([])

  const formData = ref<AlertRule>({
    ruleName: '',
    metricType: '',
    metricName: '',
    thresholdType: '0',
    thresholdValue: 0,
    alertLevel: '1',
    alertChannels: '',
    webhookUrl: '',
    enabled: '1',
    remark: ''
  })

  const formRules = {
    ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
    metricType: [{ required: true, message: '请选择指标类型', trigger: 'change' }],
    metricName: [{ required: true, message: '请输入指标名称', trigger: 'blur' }],
    thresholdType: [{ required: true, message: '请选择阈值类型', trigger: 'change' }],
    thresholdValue: [{ required: true, message: '请输入阈值', trigger: 'blur' }],
    alertLevel: [{ required: true, message: '请选择告警级别', trigger: 'change' }]
  }

  // 获取阈值文本
  const getThresholdText = (row: AlertRule) => {
    const map: Record<string, string> = { '0': '大于', '1': '小于', '2': '等于' }
    return map[row.thresholdType] || '-'
  }

  // 获取告警级别文本
  const getAlertLevelText = (level: string) => {
    const map: Record<string, string> = { '0': '低', '1': '中', '2': '高', '3': '紧急' }
    return map[level] || '-'
  }

  // 获取告警级别类型
  const getAlertLevelType = (level: string) => {
    const map: Record<string, 'info' | 'success' | 'warning' | 'danger'> = {
      '0': 'info',
      '1': 'success',
      '2': 'warning',
      '3': 'danger'
    }
    return map[level] || 'info'
  }

  // 获取规则列表
  const getRuleList = async () => {
    loading.value = true
    try {
      const data = await fetchGetAlertRuleList()
      ruleList.value = data
    } catch (error) {
      if (import.meta.env.DEV) {
        console.error('获取告警规则列表失败:', error)
      }
    } finally {
      loading.value = false
    }
  }

  // 新增
  const handleAdd = () => {
    dialogTitle.value = '新增告警规则'
    resetForm()
    dialogVisible.value = true
  }

  // 编辑
  const handleEdit = (row: AlertRule) => {
    dialogTitle.value = '编辑告警规则'
    formData.value = { ...row }
    alertChannels.value = row.alertChannels ? row.alertChannels.split(',') : []
    dialogVisible.value = true
  }

  // 删除
  const handleDelete = async (row: AlertRule) => {
    try {
      await ElMessageBox.confirm('确定要删除该告警规则吗？', '提示', {
        type: 'warning'
      })
      await fetchDeleteAlertRule(row.ruleId!)
      ElMessage.success('删除成功')
      getRuleList()
    } catch (error) {
      if (error !== 'cancel') {
        if (import.meta.env.DEV) {
          console.error('删除告警规则失败:', error)
        }
      }
    }
  }

  // 提交
  const handleSubmit = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid: boolean) => {
      if (valid) {
        submitting.value = true
        try {
          formData.value.alertChannels = alertChannels.value.join(',')
          if (formData.value.ruleId) {
            await fetchUpdateAlertRule(formData.value)
            ElMessage.success('更新成功')
          } else {
            await fetchCreateAlertRule(formData.value)
            ElMessage.success('创建成功')
          }
          dialogVisible.value = false
          getRuleList()
        } catch (error) {
          if (import.meta.env.DEV) {
            console.error('保存告警规则失败:', error)
          }
        } finally {
          submitting.value = false
        }
      }
    })
  }

  // 重置表单
  const resetForm = () => {
    formData.value = {
      ruleName: '',
      metricType: '',
      metricName: '',
      thresholdType: '0',
      thresholdValue: 0,
      alertLevel: '1',
      alertChannels: '',
      webhookUrl: '',
      enabled: '1',
      remark: ''
    }
    alertChannels.value = []
    formRef.value?.resetFields()
  }

  onMounted(() => {
    getRuleList()
  })
</script>

<style scoped lang="scss">
  .alert-rules-page {
    padding: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
</style>
