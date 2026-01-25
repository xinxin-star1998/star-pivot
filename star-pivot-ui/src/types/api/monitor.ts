/**
 * 监控相关类型定义
 */

/**
 * 服务器信息
 */
export interface ServerInfo {
  cpu: CpuInfo
  memory: MemoryInfo
  jvm: JvmInfo
  system: SystemInfo
  disk: DiskInfo
}

/**
 * CPU 信息
 */
export interface CpuInfo {
  /** CPU 核心数 */
  cpuNum: number
  /** CPU 总使用率 */
  total: number
  /** CPU 系统使用率 */
  sys: number
  /** CPU 用户使用率 */
  used: number
  /** CPU 当前等待率 */
  wait: number
  /** CPU 当前空闲率 */
  free: number
}

/**
 * 内存信息
 */
export interface MemoryInfo {
  /** 内存总量（MB） */
  total: number
  /** 已用内存（MB） */
  used: number
  /** 剩余内存（MB） */
  free: number
  /** 使用率 */
  usage: number
}

/**
 * JVM 信息
 */
export interface JvmInfo {
  /** JVM 名称 */
  name: string
  /** JVM 版本 */
  version: string
  /** JVM 启动时间（毫秒） */
  startTime: number
  /** JVM 运行时长（毫秒） */
  runTime: number
  /** JVM 最大可用内存（MB） */
  max: number
  /** JVM 已分配内存（MB） */
  total: number
  /** JVM 已使用内存（MB） */
  used: number
  /** JVM 剩余内存（MB） */
  free: number
  /** JVM 内存使用率 */
  usage: number
}

/**
 * 系统信息
 */
export interface SystemInfo {
  /** 服务器名称 */
  computerName: string
  /** 操作系统 */
  osName: string
  /** 系统架构 */
  osArch: string
  /** 服务器IP */
  computerIp: string
}

/**
 * 磁盘信息
 */
export interface DiskInfo {
  /** 磁盘总容量（GB） */
  total: number
  /** 磁盘已用容量（GB） */
  used: number
  /** 磁盘剩余容量（GB） */
  free: number
  /** 磁盘使用率 */
  usage: number
}

/**
 * Druid 监控信息
 */
export interface DruidMonitorInfo {
  /** 是否可用：true 表示数据源为 Druid 可展示监控；false 表示非 Druid 或未配置，仅展示提示 */
  available?: boolean
  /** 不可用时的提示文案 */
  message?: string
  /** 数据源名称 */
  name?: string
  /** 数据库类型 */
  dbType?: string
  /** 驱动类名 */
  driverClassName?: string
  /** 连接池信息 */
  connectionPool?: ConnectionPoolInfo
  /** SQL 统计信息 */
  sqlStat?: SqlStatInfo
}

/**
 * 连接池信息
 */
export interface ConnectionPoolInfo {
  /** 初始连接数 */
  initialSize: number
  /** 最小空闲连接数 */
  minIdle: number
  /** 最大活跃连接数 */
  maxActive: number
  /** 当前连接数 */
  activeCount: number
  /** 活跃连接数 */
  activePeak: number
  /** 连接池使用率 */
  usage: number
}

/**
 * SQL 统计信息
 */
export interface SqlStatInfo {
  /** SQL 执行总数 */
  executeCount: number
  /** SQL 执行总耗时（毫秒） */
  executeMillisTotal: number
  /** 平均执行时间（毫秒） */
  executeMillisAvg: number
  /** 慢 SQL 数量 */
  slowSqlCount: number
  /** 错误 SQL 数量 */
  errorSqlCount: number
}

/**
 * Redis 监控信息
 */
export interface RedisMonitorInfo {
  /** 是否可用：true 表示 Redis 已配置且连接正常；false 表示 Redis 未配置或连接失败 */
  available?: boolean
  /** 不可用时的提示文案 */
  message?: string
  /** Redis 版本 */
  version?: string
  /** Redis 运行模式 */
  mode?: string
  /** Redis 端口 */
  port?: number
  /** 连接状态 */
  connected?: boolean
  /** 内存信息 */
  memory?: RedisMemoryInfo
  /** 键值统计 */
  keys?: RedisKeyInfo
  /** 命令统计 */
  commands?: RedisCommandInfo
  /** 客户端信息 */
  clients?: RedisClientInfo
}

/**
 * Redis 内存信息
 */
export interface RedisMemoryInfo {
  /** 已使用内存（MB） */
  usedMemory: number
  /** 最大内存（MB） */
  maxMemory: number
  /** 内存使用率 */
  usage: number
}

/**
 * Redis 键值统计
 */
export interface RedisKeyInfo {
  /** 键总数 */
  totalKeys: number
  /** 过期键数 */
  expiredKeys: number
}

/**
 * Redis 命令统计
 */
export interface RedisCommandInfo {
  /** 总命令数 */
  totalCommands: number
  /** 每秒命令数 */
  commandsPerSecond: number
}

/**
 * Redis 客户端信息
 */
export interface RedisClientInfo {
  /** 已连接客户端数 */
  connectedClients: number
  /** 阻塞客户端数 */
  blockedClients: number
}

/**
 * Redis 缓存信息
 */
export interface RedisCacheInfo {
  /** 缓存名称 */
  cacheName: string
  /** 备注 */
  remark: string
  /** 键名列表 */
  keys?: CacheKeyInfo[]
}

/**
 * 缓存键信息
 */
export interface CacheKeyInfo {
  /** 键名 */
  key: string
  /** 键类型（string, hash, list, set, zset） */
  type: string
  /** 过期时间（秒），-1表示永不过期，-2表示键不存在 */
  ttl: number
  /** 值大小（字节） */
  size: number
}

/**
 * 缓存内容信息
 */
export interface CacheContentInfo {
  /** 缓存名称 */
  cacheName: string
  /** 缓存键名 */
  key: string
  /** 缓存内容（JSON 格式） */
  content: string
  /** 键类型 */
  type: string
  /** 过期时间（秒） */
  ttl: number
}

/**
 * 在线用户
 */
export interface OnlineUser {
  /** 会话ID */
  sessionId: string
  /** 用户ID */
  userId: number
  /** 用户名 */
  userName: string
  /** 用户昵称 */
  nickName?: string
  /** 部门名称 */
  deptName?: string
  /** IP地址 */
  ipaddr?: string
  /** 登录地点 */
  loginLocation?: string
  /** 浏览器 */
  browser?: string
  /** 操作系统 */
  os?: string
  /** 登录时间 */
  loginTime: string
  /** 最后访问时间 */
  lastAccessTime: string
}

/**
 * 在线用户查询参数
 */
export interface OnlineUserQueryParams {
  /** 用户名（可选） */
  userName?: string
  /** IP地址（可选） */
  ipaddr?: string
}

/**
 * 监控历史数据点
 */
export interface MonitorHistoryDataPoint {
  /** 时间 */
  time: string
  /** 指标值 */
  value: number
  /** 单位 */
  unit?: string
}

/**
 * 多指标历史数据
 */
export interface MultiMetricHistoryData {
  /** 指标类型 */
  metricType: string
  /** 数据点列表 */
  data: MonitorHistoryDataPoint[]
}

/**
 * 指标统计信息
 */
export interface MetricStatistics {
  /** 平均值 */
  avgValue: number
  /** 最大值 */
  maxValue: number
  /** 最小值 */
  minValue: number
  /** 数据点数量 */
  count: number
}

/**
 * 告警规则
 */
export interface AlertRule {
  /** 规则ID */
  ruleId?: number
  /** 规则名称 */
  ruleName: string
  /** 指标类型 */
  metricType: string
  /** 指标名称 */
  metricName: string
  /** 阈值类型（0大于 1小于 2等于） */
  thresholdType: string
  /** 阈值 */
  thresholdValue: number
  /** 告警级别（0低 1中 2高 3紧急） */
  alertLevel: string
  /** 告警渠道（逗号分隔） */
  alertChannels?: string
  /** Webhook地址 */
  webhookUrl?: string
  /** 是否启用（0否 1是） */
  enabled: string
  /** 备注 */
  remark?: string
  /** 创建者 */
  createBy?: string
  /** 创建时间 */
  createTime?: string
  /** 更新者 */
  updateBy?: string
  /** 更新时间 */
  updateTime?: string
}

/**
 * 告警记录
 */
export interface AlertRecord {
  /** 记录ID */
  recordId?: number
  /** 规则ID */
  ruleId: number
  /** 规则名称 */
  ruleName: string
  /** 指标类型 */
  metricType: string
  /** 指标名称 */
  metricName: string
  /** 当前值 */
  currentValue: number
  /** 阈值 */
  thresholdValue: number
  /** 告警级别 */
  alertLevel: string
  /** 告警状态（0未处理 1已处理 2已忽略） */
  alertStatus: string
  /** 告警渠道 */
  alertChannels?: string
  /** 告警内容 */
  alertContent?: string
  /** 告警时间 */
  alertTime: string
  /** 处理时间 */
  handleTime?: string
  /** 处理人 */
  handleBy?: string
  /** 处理备注 */
  handleRemark?: string
  /** 创建时间 */
  createTime?: string
}

/**
 * 慢SQL记录
 */
export interface SlowSql {
  /** 主键ID */
  id?: number
  /** SQL ID */
  sqlId: string
  /** SQL语句 */
  sqlText: string
  /** 执行次数 */
  executeCount: number
  /** 总执行时间（毫秒） */
  executeTimeTotal: number
  /** 最大执行时间（毫秒） */
  executeTimeMax: number
  /** 平均执行时间（毫秒） */
  executeTimeAvg: number
  /** 慢SQL次数 */
  slowCount: number
  /** 错误次数 */
  errorCount: number
  /** 最后执行时间 */
  lastExecuteTime?: string
  /** 优化建议 */
  optimizationSuggestion?: string
  /** 状态（0待优化 1已优化 2已忽略） */
  status: string
  /** 创建时间 */
  createTime?: string
  /** 更新时间 */
  updateTime?: string
}

/**
 * API性能监控数据
 */
export interface ApiPerformance {
  /** 主键ID */
  id?: number
  /** 接口路径 */
  apiPath: string
  /** 请求方法 */
  apiMethod: string
  /** 请求次数 */
  requestCount: number
  /** 成功次数 */
  successCount: number
  /** 错误次数 */
  errorCount: number
  /** 总响应时间（毫秒） */
  responseTimeTotal: number
  /** 最大响应时间（毫秒） */
  responseTimeMax: number
  /** 最小响应时间（毫秒） */
  responseTimeMin: number
  /** 平均响应时间（毫秒） */
  responseTimeAvg: number
  /** 统计日期 */
  statDate: string
  /** 统计小时 */
  statHour?: number
  /** 创建时间 */
  createTime?: string
  /** 更新时间 */
  updateTime?: string
}

/**
 * 系统健康检查报告
 */
export interface HealthCheckReport {
  /** 时间戳 */
  timestamp: string
  /** 整体健康状态 */
  overall: string
  /** 数据库健康状态 */
  database?: {
    healthy: boolean
    activeCount?: number
    maxActive?: number
    usage?: number
    message?: string
    error?: string
  }
  /** Redis健康状态 */
  redis?: {
    healthy: boolean
    message?: string
    error?: string
  }
  /** 磁盘健康状态 */
  disk?: {
    healthy: boolean
    usage?: number
    total?: number
    used?: number
    free?: number
    error?: string
  }
  /** JVM健康状态 */
  jvm?: {
    healthy: boolean
    usage?: number
    max?: number
    used?: number
    free?: number
    error?: string
  }
  /** 错误信息 */
  error?: string
}

/**
 * API性能监控查询参数
 */
export interface ApiPerformanceReqBo {
  /** 当前页码 */
  pageNum?: number
  /** 每页显示条数 */
  pageSize?: number
  /** 接口路径（模糊查询） */
  apiPath?: string
  /** 请求方法 */
  apiMethod?: string
  /** 开始日期 */
  startDate?: string
  /** 结束日期 */
  endDate?: string
  /** 排序字段（responseTimeAvg, errorCount, requestCount等） */
  orderBy?: string
  /** 排序方式（asc, desc） */
  orderDirection?: string
}
