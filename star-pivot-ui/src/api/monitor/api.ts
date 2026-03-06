import request from '@/utils/http'
import type { ApiPerformance, ApiPerformanceReqBo } from '@/types/api/monitor'

/**
 * 获取API性能监控数据（按日期范围查询）
 * 注意：此接口暂未实现，可使用最慢接口和错误率最高接口替代
 */
export function fetchGetApiPerformanceList(startDate: string, endDate: string) {
  return request.get<ApiPerformance[]>({
    url: '/api/monitor/api/performance',
    params: { startDate, endDate }
  })
}

/**
 * 获取最慢的API接口
 */
export function fetchGetSlowestApis(limit: number, startDate: string, endDate: string) {
  return request.get<ApiPerformance[]>({
    url: '/api/monitor/api/slowest',
    params: { limit, startDate, endDate }
  })
}

/**
 * 获取错误率最高的API接口
 */
export function fetchGetHighestErrorRateApis(limit: number, startDate: string, endDate: string) {
  return request.get<ApiPerformance[]>({
    url: '/api/monitor/api/error-rate',
    params: { limit, startDate, endDate }
  })
}



/**
 * 分页查询API性能监控数据
 */
export function fetchGetApiPerformancePageList(params: ApiPerformanceReqBo) {
  return request.post<Api.Common.PaginatedResponse<ApiPerformance>>({
    url: '/api/monitor/api/pageList',
    data: params
  })
}
