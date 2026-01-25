<!-- 操作日志详情对话框 -->
<template>
  <ElDialog
    v-model="dialogVisible"
    title="操作日志详情"
    width="800px"
    :close-on-click-modal="false"
    @update:model-value="handleDialogChange"
  >
    <ElDescriptions :column="2" border v-if="operLog">
      <ElDescriptionsItem label="日志ID">{{ operLog.operId }}</ElDescriptionsItem>
      <ElDescriptionsItem label="模块标题">{{ operLog.title }}</ElDescriptionsItem>
      <ElDescriptionsItem label="业务类型">
        {{ getBusinessTypeText(operLog.businessType) }}
      </ElDescriptionsItem>
      <ElDescriptionsItem label="操作状态">
        <ElTag :type="operLog.status === 0 ? 'success' : 'danger'">
          {{ operLog.status === 0 ? '正常' : '异常' }}
        </ElTag>
      </ElDescriptionsItem>
      <ElDescriptionsItem label="操作人员">{{ operLog.operName }}</ElDescriptionsItem>
      <ElDescriptionsItem label="部门名称">
        {{ operLog.deptName || '未知' }}
      </ElDescriptionsItem>
      <ElDescriptionsItem label="请求方式">
        {{ operLog.requestMethod }}
      </ElDescriptionsItem>
      <ElDescriptionsItem label="请求URL" :span="2">
        {{ operLog.operUrl }}
      </ElDescriptionsItem>
      <ElDescriptionsItem label="操作IP">{{ operLog.operIp }}</ElDescriptionsItem>
      <ElDescriptionsItem label="操作时间">{{ operLog.operTime }}</ElDescriptionsItem>
      <ElDescriptionsItem label="耗时">{{ operLog.costTime }}ms</ElDescriptionsItem>
      <ElDescriptionsItem label="方法名称" :span="2">
        <ElText type="info" size="small">{{ operLog.method }}</ElText>
      </ElDescriptionsItem>
      <ElDescriptionsItem label="请求参数" :span="2">
        <pre class="param-display">{{ formatParam(operLog.operParam) }}</pre>
      </ElDescriptionsItem>
      <ElDescriptionsItem label="返回结果" :span="2" v-if="operLog.jsonResult">
        <pre class="param-display">{{ formatParam(operLog.jsonResult) }}</pre>
      </ElDescriptionsItem>
      <ElDescriptionsItem label="错误信息" :span="2" v-if="operLog.errorMsg">
        <ElText type="danger">{{ operLog.errorMsg }}</ElText>
      </ElDescriptionsItem>
    </ElDescriptions>
  </ElDialog>
</template>

<script setup lang="ts">
  import { ElTag, ElText, ElDescriptions, ElDescriptionsItem } from 'element-plus'
  import type { OperLogListItem } from '@/types/api/operlog'

  interface Props {
    visible: boolean
    operLog: OperLogListItem | null
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
  }

  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()

  // 对话框显示状态
  const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
  })

  /**
   * 对话框状态变化处理
   */
  const handleDialogChange = (value: boolean) => {
    emit('update:visible', value)
  }

  /**
   * 获取业务类型文本
   */
  const getBusinessTypeText = (businessType?: number) => {
    const map: Record<number, string> = {
      0: '其它',
      1: '新增',
      2: '修改',
      3: '删除'
    }
    return map[businessType ?? 0] || '未知'
  }

  /**
   * 格式化参数显示
   */
  const formatParam = (param?: string) => {
    if (!param) return '无'
    try {
      const obj = JSON.parse(param)
      return JSON.stringify(obj, null, 2)
    } catch {
      return param
    }
  }
</script>

<style scoped lang="scss">
  .param-display {
    max-height: 200px;
    overflow-y: auto;
    padding: 10px;
    background: #f5f7fa;
    border-radius: 4px;
    font-size: 12px;
    line-height: 1.5;
    white-space: pre-wrap;
    word-break: break-all;
  }
</style>
