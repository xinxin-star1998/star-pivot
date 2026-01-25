import request from '@/utils/http'
import type { AlertRule, AlertRecord } from '@/types/api/monitor'

/**
 * 获取告警规则列表
 */
export function fetchGetAlertRuleList() {
  return request.get<AlertRule[]>({
    url: '/api/monitor/alert/rules'
  })
}

/**
 * 创建告警规则
 */
export function fetchCreateAlertRule(rule: AlertRule) {
  return request.post({
    url: '/api/monitor/alert/rules',
    data: rule
  })
}

/**
 * 更新告警规则
 */
export function fetchUpdateAlertRule(rule: AlertRule) {
  return request.put({
    url: '/api/monitor/alert/rules',
    data: rule
  })
}

/**
 * 删除告警规则
 */
export function fetchDeleteAlertRule(ruleId: number) {
  return request.del({
    url: `/api/monitor/alert/rules/${ruleId}`
  })
}

/**
 * 获取告警记录列表
 */
export function fetchGetAlertRecordList(status?: string, limit?: number) {
  return request.get<AlertRecord[]>({
    url: '/api/monitor/alert/records',
    params: { status, limit }
  })
}

/**
 * 处理告警记录
 */
export function fetchHandleAlert(
  recordId: number,
  handleBy: string,
  handleRemark?: string
) {
  return request.put({
    url: `/api/monitor/alert/records/${recordId}/handle`,
    params: { handleBy, handleRemark }
  })
}
