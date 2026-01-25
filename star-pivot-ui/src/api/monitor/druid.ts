import request from '@/utils/http'
import type { DruidMonitorInfo } from '@/types/api/monitor'

/**
 * 获取 Druid 监控信息
 */
export function fetchGetDruidMonitorInfo() {
  return request.get<DruidMonitorInfo>({
    url: '/api/monitor/druid'
  })
}
