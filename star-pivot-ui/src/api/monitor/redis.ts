import request from '@/utils/http'
import type { RedisMonitorInfo } from '@/types/api/monitor'

/**
 * 获取 Redis 监控信息
 */
export function fetchGetRedisMonitorInfo() {
  return request.get<RedisMonitorInfo>({
    url: '/api/monitor/redis'
  })
}
