import request from '@/utils/http'
import type { SlowSql } from '@/types/api/monitor'

/**
 * 获取慢SQL列表
 */
export function fetchGetSlowSqlList(limit?: number) {
  return request.get<SlowSql[]>({
    url: '/api/monitor/sql/slow',
    params: { limit }
  })
}

/**
 * 获取慢SQL详情
 */
export function fetchGetSlowSqlDetail(sqlId: string) {
  return request.get<SlowSql>({
    url: `/api/monitor/sql/slow/${sqlId}`
  })
}

/**
 * 从Druid提取慢SQL列表
 */
export function fetchExtractSlowSqlList(threshold?: number) {
  return request.post<SlowSql[]>({
    url: '/api/monitor/sql/slow/extract',
    params: { threshold }
  })
}

/**
 * 更新慢SQL状态
 */
export function fetchUpdateSlowSqlStatus(id: number, status: string) {
  return request.put({
    url: `/api/monitor/sql/slow/${id}/status`,
    params: { status }
  })
}
