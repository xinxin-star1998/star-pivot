import request from '@/utils/http'
import type {
  MonitorHistoryDataPoint,
  MultiMetricHistoryData,
  MetricStatistics
} from '@/types/api/monitor'

/**
 * 查询历史监控数据（单指标）
 */
export function fetchGetHistoryData(
  metricType: string,
  startTime: string,
  endTime: string
) {
  return request.get<MonitorHistoryDataPoint[]>({
    url: '/api/monitor/history',
    params: { metricType, startTime, endTime }
  })
}

/**
 * 查询多指标历史数据（对比）
 */
export function fetchGetMultiMetricHistoryData(
  metricTypes: string,
  startTime: string,
  endTime: string
) {
  return request.get<MultiMetricHistoryData[]>({
    url: '/api/monitor/history/multi',
    params: { metricTypes, startTime, endTime }
  })
}

/**
 * 查询指标统计信息
 */
export function fetchGetMetricStatistics(
  metricType: string,
  startTime: string,
  endTime: string
) {
  return request.get<MetricStatistics>({
    url: '/api/monitor/history/statistics',
    params: { metricType, startTime, endTime }
  })
}
