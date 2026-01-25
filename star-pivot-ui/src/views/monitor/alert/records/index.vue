<!-- 告警记录列表页面 -->
<template>
  <div class="alert-records-page art-full-height">
    <ElCard class="art-table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>告警记录</span>
          <ElButton :icon="Refresh" @click="getRecordList" :loading="loading">刷新</ElButton>
        </div>
      </template>

      <!-- 查询条件 -->
      <ElForm :model="queryForm" inline class="query-form">
        <ElFormItem label="告警状态">
          <ElSelect
            v-model="queryForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <ElOption label="未处理" value="0" />
            <ElOption label="已处理" value="1" />
            <ElOption label="已忽略" value="2" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem>
          <ElButton type="primary" @click="handleQuery">查询</ElButton>
          <ElButton @click="resetQuery">重置</ElButton>
        </ElFormItem>
      </ElForm>

      <ElTable v-loading="loading" :data="recordList" border>
        <ElTableColumn prop="ruleName" label="规则名称" width="150" />
        <ElTableColumn prop="metricName" label="指标名称" width="150" />
        <ElTableColumn prop="currentValue" label="当前值" width="100" />
        <ElTableColumn prop="thresholdValue" label="阈值" width="100" />
        <ElTableColumn label="告警级别" width="100">
          <template #default="{ row }">
            <ElTag :type="getAlertLevelType(row.alertLevel)">
              {{ getAlertLevelText(row.alertLevel) }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="告警状态" width="100">
          <template #default="{ row }">
            <ElTag :type="getStatusType(row.alertStatus)">
              {{ getStatusText(row.alertStatus) }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="alertChannels" label="告警渠道" width="150" />
        <ElTableColumn prop="alertContent" label="告警内容" min-width="200" show-overflow-tooltip />
        <ElTableColumn prop="alertTime" label="告警时间" width="180" />
        <ElTableColumn prop="handleBy" label="处理人" width="100" />
        <ElTableColumn prop="handleTime" label="处理时间" width="180" />
        <ElTableColumn label="操作" width="150" fixed="right" v-if="hasHandleAuth">
          <template #default="{ row }">
            <ElButton
              v-if="row.alertStatus === '0'"
              link
              type="primary"
              @click="handleAlert(row)"
              v-auth="'monitor:alert:handle'"
            >
              处理
            </ElButton>
          </template>
        </ElTableColumn>
      </ElTable>
    </ElCard>

    <!-- 处理告警对话框 -->
    <ElDialog v-model="handleVisible" title="处理告警" width="500px">
      <ElForm ref="handleFormRef" :model="handleForm" :rules="handleRules" label-width="100px">
        <ElFormItem label="告警内容">
          <ElInput :value="currentRecord?.alertContent" readonly type="textarea" :rows="3" />
        </ElFormItem>
        <ElFormItem label="处理备注" prop="handleRemark">
          <ElInput
            v-model="handleForm.handleRemark"
            type="textarea"
            :rows="4"
            placeholder="请输入处理备注"
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="handleVisible = false">取消</ElButton>
        <ElButton type="primary" @click="submitHandle" :loading="submitting">确定</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  import { Refresh } from '@element-plus/icons-vue'
  import { fetchGetAlertRecordList, fetchHandleAlert } from '@/api/monitor/alert'
  import type { AlertRecord } from '@/types/api/monitor'
  import { ElMessage } from 'element-plus'
  import { useAuth } from '@/hooks/core/useAuth'
  import { useUserStore } from '@/store/modules/user'

  defineOptions({ name: 'AlertRecords' })

  const loading = ref(false)
  const submitting = ref(false)
  const recordList = ref<AlertRecord[]>([])
  const handleVisible = ref(false)
  const currentRecord = ref<AlertRecord | null>(null)
  const handleFormRef = ref()
  const { hasAuth } = useAuth()
  const userStore = useUserStore()

  const queryForm = ref({
    status: ''
  })

  const handleForm = ref({
    handleRemark: ''
  })

  const handleRules = {
    handleRemark: [{ required: true, message: '请输入处理备注', trigger: 'blur' }]
  }

  // 检查是否有处理权限
  const hasHandleAuth = computed(() => {
    return hasAuth('monitor:alert:handle')
  })

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

  // 获取状态文本
  const getStatusText = (status: string) => {
    const map: Record<string, string> = { '0': '未处理', '1': '已处理', '2': '已忽略' }
    return map[status] || '-'
  }

  // 获取状态类型
  const getStatusType = (status: string) => {
    const map: Record<string, 'warning' | 'success' | 'info'> = {
      '0': 'warning',
      '1': 'success',
      '2': 'info'
    }
    return map[status] || 'info'
  }

  // 获取记录列表
  const getRecordList = async () => {
    loading.value = true
    try {
      const data = await fetchGetAlertRecordList(queryForm.value.status, 100)
      recordList.value = data
    } catch (error) {
      if (import.meta.env.DEV) {
        console.error('获取告警记录列表失败:', error)
      }
    } finally {
      loading.value = false
    }
  }

  // 查询
  const handleQuery = () => {
    getRecordList()
  }

  // 重置查询
  const resetQuery = () => {
    queryForm.value.status = ''
    getRecordList()
  }

  // 处理告警
  const handleAlert = (row: AlertRecord) => {
    currentRecord.value = row
    handleForm.value.handleRemark = ''
    handleVisible.value = true
  }

  // 提交处理
  const submitHandle = async () => {
    if (!handleFormRef.value) return
    await handleFormRef.value.validate(async (valid: boolean) => {
      if (valid && currentRecord.value) {
        submitting.value = true
        try {
          await fetchHandleAlert(
            currentRecord.value.recordId!,
            userStore.getUserInfo?.user?.username || 'system',
            handleForm.value.handleRemark
          )
          ElMessage.success('处理成功')
          handleVisible.value = false
          getRecordList()
        } catch (error) {
          if (import.meta.env.DEV) {
            console.error('处理告警失败:', error)
          }
        } finally {
          submitting.value = false
        }
      }
    })
  }

  onMounted(() => {
    getRecordList()
  })
</script>

<style scoped lang="scss">
  .alert-records-page {
    padding: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .query-form {
    margin-bottom: 20px;
  }
</style>
