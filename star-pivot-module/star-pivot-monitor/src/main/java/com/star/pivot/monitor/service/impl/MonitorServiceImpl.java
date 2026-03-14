package com.star.pivot.monitor.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.monitor.domain.bo.ApiPerformanceReqBo;
import com.star.pivot.monitor.domain.vo.DruidMonitorVO;

import com.star.pivot.monitor.domain.vo.OnlineUserVO;
import com.star.pivot.monitor.domain.vo.RedisCacheVO;
import com.star.pivot.monitor.domain.vo.ServerInfoVO;
import com.star.pivot.monitor.domain.entity.SysMonitorApiPerformance;

import com.star.pivot.monitor.mapper.SysMonitorApiPerformanceMapper;
import com.star.pivot.monitor.service.MonitorService;
import com.star.pivot.security.RefreshTokenManager;
import com.star.pivot.security.RefreshTokenManager.RefreshTokenValue;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.service.OnlineUserService;
import com.star.pivot.system.service.SysDeptService;
import com.star.pivot.system.service.SysUserService;
import com.star.pivot.system.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.star.pivot.framework.utils.LogUtils.truncateString;

/**
 * 监控服务实现类
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Slf4j
@Service
public class MonitorServiceImpl implements MonitorService {

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private SysUserService sysUserService;

    @Autowired(required = false)
    private SysDeptService sysDeptService;

    @Autowired(required = false)
    private RefreshTokenManager refreshTokenManager;

    @Autowired(required = false)
    private OnlineUserService onlineUserService;

    @Autowired(required = false)
    private TokenService tokenService;

    @Autowired(required = false)
    private SysMonitorApiPerformanceMapper apiPerformanceMapper;

    private static final SystemInfo SYSTEM_INFO = new SystemInfo();
    private static final HardwareAbstractionLayer HAL = SYSTEM_INFO.getHardware();
    private static final OperatingSystem OS = SYSTEM_INFO.getOperatingSystem();

    // 常量定义
    private static final int BYTES_TO_KB = 1024;
    private static final int KB_TO_MB = 1024;
    private static final int MB_TO_GB = 1024;
    private static final int CPU_SAMPLE_INTERVAL_MS = 1000;
    private static final double PERCENTAGE_BASE = 100.0;
    private static final int SCAN_COUNT = 100; // SCAN 每次扫描的 key 数量
    private static final int CPU_CACHE_SECONDS = 2; // CPU 信息缓存时间（秒）

    // CPU 信息缓存
    private static final Map<String, Object> cpuInfoCache = new ConcurrentHashMap<>();
    private static final String CACHE_KEY_CPU_INFO = "cpu_info";
    private static final String CACHE_KEY_CPU_TIMESTAMP = "cpu_timestamp";

    // Redis 键前缀常量
    private static final String REDIS_KEY_PREFIX_JWT_REFRESH_USER = "jwt:refresh:user";
    private static final String REDIS_KEY_PREFIX_JWT_LOGOUT = "jwt:logout";
    private static final String REDIS_KEY_PREFIX_ONLINE_USER = "online:user";
    private static final String REDIS_KEY_PREFIX_SYS_CONFIG = "sys_config";
    private static final String REDIS_KEY_PREFIX_SYS_DICT = "sys_dict";
    private static final String REDIS_KEY_PREFIX_CAPTCHA = "captcha";
    private static final String REDIS_KEY_PREFIX_REPEAT_SUBMIT = "repeat_submit";
    private static final String REDIS_KEY_PREFIX_RATE_LIMIT = "rate_limit";
    private static final String REDIS_KEY_PREFIX_PWD_ERR_CNT = "pwd_err_cnt";

    // Redis 缓存名称和键前缀映射（用于备注显示）
    private static final Map<String, String> CACHE_REMARK_MAP = new HashMap<>();

    static {
        // 初始化缓存备注映射（用于显示友好的备注信息）
        // 注意：这些键前缀应与 Redis 中实际使用的键保持一致
        CACHE_REMARK_MAP.put(REDIS_KEY_PREFIX_JWT_REFRESH_USER, "用户信息");
        CACHE_REMARK_MAP.put(REDIS_KEY_PREFIX_JWT_LOGOUT, "登出黑名单");
        CACHE_REMARK_MAP.put(REDIS_KEY_PREFIX_SYS_CONFIG, "配置信息");
        CACHE_REMARK_MAP.put(REDIS_KEY_PREFIX_SYS_DICT, "数据字典");
        CACHE_REMARK_MAP.put(REDIS_KEY_PREFIX_CAPTCHA, "验证码");
        CACHE_REMARK_MAP.put(REDIS_KEY_PREFIX_REPEAT_SUBMIT, "防重提交");
        CACHE_REMARK_MAP.put(REDIS_KEY_PREFIX_RATE_LIMIT, "限流处理");
        CACHE_REMARK_MAP.put(REDIS_KEY_PREFIX_PWD_ERR_CNT, "密码错误次数");
    }

    @Override
    public ServerInfoVO getServerInfo() {
        ServerInfoVO serverInfo = new ServerInfoVO();

        try {
            // CPU 信息
            ServerInfoVO.CpuInfo cpuInfo = getCpuInfo();
            serverInfo.setCpu(cpuInfo);

            // 内存信息
            ServerInfoVO.MemoryInfo memoryInfo = getMemoryInfo();
            serverInfo.setMemory(memoryInfo);

            // JVM 信息
            ServerInfoVO.JvmInfo jvmInfo = getJvmInfo();
            serverInfo.setJvm(jvmInfo);

            // 系统信息
            ServerInfoVO.SystemInfo systemInfo = getSystemInfo();
            serverInfo.setSystem(systemInfo);

            // 磁盘信息
            ServerInfoVO.DiskInfo diskInfo = getDiskInfo();
            serverInfo.setDisk(diskInfo);
        } catch (Exception e) {
            log.error("获取服务器信息失败", e);
            throw new BizException(ErrorCode.INTERNAL_ERROR, "获取服务器信息失败: " + e.getMessage());
        }

        return serverInfo;
    }

    /**
     * 获取 CPU 信息
     * 使用缓存机制避免频繁阻塞线程
     */
    private ServerInfoVO.CpuInfo getCpuInfo() {
        // 检查缓存
        Long cacheTimestamp = (Long) cpuInfoCache.get(CACHE_KEY_CPU_TIMESTAMP);
        if (cacheTimestamp != null && System.currentTimeMillis() - cacheTimestamp < CPU_CACHE_SECONDS * 1000) {
            ServerInfoVO.CpuInfo cachedInfo = (ServerInfoVO.CpuInfo) cpuInfoCache.get(CACHE_KEY_CPU_INFO);
            if (cachedInfo != null) {
                return cachedInfo;
            }
        }

        // 缓存失效或不存在，重新计算
        CentralProcessor processor = HAL.getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        try {
            Thread.sleep(CPU_SAMPLE_INTERVAL_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long[] ticks = processor.getSystemCpuLoadTicks();

        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;

        ServerInfoVO.CpuInfo cpuInfo = new ServerInfoVO.CpuInfo();
        cpuInfo.setCpuNum(processor.getLogicalProcessorCount());
        cpuInfo.setTotal(PERCENTAGE_BASE);
        cpuInfo.setSys(PERCENTAGE_BASE * cSys / totalCpu);
        cpuInfo.setUsed(PERCENTAGE_BASE * user / totalCpu);
        cpuInfo.setWait(PERCENTAGE_BASE * iowait / totalCpu);
        cpuInfo.setFree(PERCENTAGE_BASE * idle / totalCpu);

        // 更新缓存
        cpuInfoCache.put(CACHE_KEY_CPU_INFO, cpuInfo);
        cpuInfoCache.put(CACHE_KEY_CPU_TIMESTAMP, System.currentTimeMillis());

        return cpuInfo;
    }

    /**
     * 获取内存信息
     */
    private ServerInfoVO.MemoryInfo getMemoryInfo() {
        GlobalMemory memory = HAL.getMemory();
        long total = memory.getTotal();
        long available = memory.getAvailable();
        long used = total - available;

        ServerInfoVO.MemoryInfo memoryInfo = new ServerInfoVO.MemoryInfo();
        memoryInfo.setTotal(total / BYTES_TO_KB / KB_TO_MB); // 转换为 MB
        memoryInfo.setUsed(used / BYTES_TO_KB / KB_TO_MB);
        memoryInfo.setFree(available / BYTES_TO_KB / KB_TO_MB);
        memoryInfo.setUsage(PERCENTAGE_BASE * used / total);

        return memoryInfo;
    }

    /**
     * 获取 JVM 信息
     */
    private ServerInfoVO.JvmInfo getJvmInfo() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

        long totalMemory = memoryMXBean.getHeapMemoryUsage().getMax();
        long usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
        long freeMemory = totalMemory - usedMemory;

        ServerInfoVO.JvmInfo jvmInfo = new ServerInfoVO.JvmInfo();
        jvmInfo.setName(runtimeMXBean.getVmName());
        jvmInfo.setVersion(runtimeMXBean.getVmVersion());
        jvmInfo.setStartTime(runtimeMXBean.getStartTime());
        jvmInfo.setRunTime(runtimeMXBean.getUptime());
        jvmInfo.setMax(totalMemory / BYTES_TO_KB / KB_TO_MB); // 转换为 MB
        jvmInfo.setTotal(memoryMXBean.getHeapMemoryUsage().getCommitted() / BYTES_TO_KB / KB_TO_MB);
        jvmInfo.setUsed(usedMemory / BYTES_TO_KB / KB_TO_MB);
        jvmInfo.setFree(freeMemory / BYTES_TO_KB / KB_TO_MB);
        jvmInfo.setUsage(PERCENTAGE_BASE * usedMemory / totalMemory);

        return jvmInfo;
    }

    /**
     * 获取系统信息
     */
    private ServerInfoVO.SystemInfo getSystemInfo() {
        try {
            ServerInfoVO.SystemInfo systemInfo = new ServerInfoVO.SystemInfo();
            systemInfo.setComputerName(InetAddress.getLocalHost().getHostName());
            systemInfo.setOsName(System.getProperty("os.name"));
            systemInfo.setOsArch(System.getProperty("os.arch"));
            systemInfo.setComputerIp(InetAddress.getLocalHost().getHostAddress());
            return systemInfo;
        } catch (Exception e) {
            log.error("获取系统信息失败", e);
            ServerInfoVO.SystemInfo systemInfo = new ServerInfoVO.SystemInfo();
            systemInfo.setComputerName("未知");
            systemInfo.setOsName(System.getProperty("os.name", "未知"));
            systemInfo.setOsArch(System.getProperty("os.arch", "未知"));
            systemInfo.setComputerIp("未知");
            return systemInfo;
        }
    }

    /**
     * 获取磁盘信息
     */
    private ServerInfoVO.DiskInfo getDiskInfo() {
        FileSystem fileSystem = OS.getFileSystem();
        List<OSFileStore> fileStores = fileSystem.getFileStores();

        long total = 0;
        long free = 0;

        for (OSFileStore store : fileStores) {
            total += store.getTotalSpace();
            free += store.getFreeSpace();
        }

        long used = total - free;

        ServerInfoVO.DiskInfo diskInfo = new ServerInfoVO.DiskInfo();
        diskInfo.setTotal(total / BYTES_TO_KB / KB_TO_MB / MB_TO_GB); // 转换为 GB
        diskInfo.setUsed(used / BYTES_TO_KB / KB_TO_MB / MB_TO_GB);
        diskInfo.setFree(free / BYTES_TO_KB / KB_TO_MB / MB_TO_GB);
        diskInfo.setUsage(total > 0 ? PERCENTAGE_BASE * used / total : 0.0);

        return diskInfo;
    }

    @Override
    public DruidMonitorVO getDruidMonitorInfo() {
        return getDruidMonitorInfo(false, null);
    }

    @Override
    public DruidMonitorVO getDruidMonitorInfoWithSlowSql(Long slowSqlThreshold) {
        return getDruidMonitorInfo(true, slowSqlThreshold);
    }

    /**
     * 获取 Druid 监控信息（支持包含慢SQL列表）
     *
     * @param includeSlowSqlList 是否包含慢SQL列表
     * @param slowSqlThreshold 慢SQL阈值（毫秒），当 includeSlowSqlList 为 true 时有效，null 则使用默认值 5000
     * @return Druid 监控信息
     */
    public DruidMonitorVO getDruidMonitorInfo(boolean includeSlowSqlList, Long slowSqlThreshold) {
        if (dataSource == null || !(dataSource instanceof DruidDataSource)) {
            DruidMonitorVO vo = new DruidMonitorVO();
            vo.setAvailable(false);
            vo.setMessage("数据源不是 Druid 数据源或数据源未配置");
            return vo;
        }

        try {
            DruidDataSource druidDataSource = (DruidDataSource) dataSource;
            DruidMonitorVO monitorVO = new DruidMonitorVO();
            monitorVO.setName(druidDataSource.getName());
            monitorVO.setDbType(druidDataSource.getDbType());
            monitorVO.setDriverClassName(druidDataSource.getDriverClassName());

            // 连接池信息
            DruidMonitorVO.ConnectionPoolInfo poolInfo = new DruidMonitorVO.ConnectionPoolInfo();
            poolInfo.setInitialSize(druidDataSource.getInitialSize());
            poolInfo.setMinIdle(druidDataSource.getMinIdle());
            poolInfo.setMaxActive(druidDataSource.getMaxActive());
            poolInfo.setActiveCount(druidDataSource.getActiveCount());
            poolInfo.setActivePeak(druidDataSource.getActivePeak());
            if (druidDataSource.getMaxActive() > 0) {
                poolInfo.setUsage(PERCENTAGE_BASE * druidDataSource.getActiveCount() / druidDataSource.getMaxActive());
            } else {
                poolInfo.setUsage(0.0);
            }
            monitorVO.setConnectionPool(poolInfo);

            // SQL 统计信息
            DruidStatManagerFacade statManagerFacade = DruidStatManagerFacade.getInstance();
            // 获取 SQL 统计列表，传入 null 表示获取所有 SQL 统计
            List<Map<String, Object>> sqlList = statManagerFacade.getSqlStatDataList(null);

            DruidMonitorVO.SqlStatInfo sqlStatInfo = new DruidMonitorVO.SqlStatInfo();
            long executeCount = 0;
            long executeMillisTotal = 0;
            long slowSqlCount = 0;
            long errorSqlCount = 0;

            // 慢SQL列表（如果需要）
            List<DruidMonitorVO.SlowSqlInfo> slowSqlList = null;
            if (includeSlowSqlList) {
                slowSqlList = new ArrayList<>();
                // 默认慢SQL阈值为 5000 毫秒
                long threshold = (slowSqlThreshold != null && slowSqlThreshold > 0) ? slowSqlThreshold : 5000L;

                if (sqlList != null) {
                    for (Map<String, Object> sql : sqlList) {
                        try {
                            String sqlId = getStringValue(sql, "ID");
                            String sqlText = getStringValue(sql, "SQL");
                            Long count = getLongValue(sql, "ExecuteCount");
                            Long millis = getLongValue(sql, "ExecuteMillisTotal");
                            Long maxMillis = getLongValue(sql, "ExecuteMillisMax");
                            Long slowCount = getLongValue(sql, "SlowCount");
                            Long errorCount = getLongValue(sql, "ErrorCount");
                            Long lastExecuteTime = getLongValue(sql, "LastExecuteTime");

                            // 累计统计信息
                            if (count != null) executeCount += count;
                            if (millis != null) executeMillisTotal += millis;
                            if (slowCount != null) slowSqlCount += slowCount;
                            if (errorCount != null) errorSqlCount += errorCount;

                            // 判断是否为慢SQL并添加到列表
                            if (count != null && count > 0 && millis != null) {
                                double avgTime = (double) millis / count;
                                if (avgTime >= threshold || (slowCount != null && slowCount > 0)) {
                                    DruidMonitorVO.SlowSqlInfo slowSqlInfo = new DruidMonitorVO.SlowSqlInfo();
                                    slowSqlInfo.setSqlId(sqlId);
                                    // SQL文本截断到5000字符，避免过长
                                    slowSqlInfo.setSqlText(sqlText != null ? truncateString(sqlText, 5000) : "");
                                    slowSqlInfo.setExecuteCount(count);
                                    slowSqlInfo.setExecuteTimeTotal(millis);
                                    slowSqlInfo.setExecuteTimeMax(maxMillis);
                                    slowSqlInfo.setExecuteTimeAvg(avgTime);
                                    slowSqlInfo.setSlowCount(slowCount != null ? slowCount : 0L);
                                    slowSqlInfo.setErrorCount(errorCount != null ? errorCount : 0L);
                                    slowSqlInfo.setLastExecuteTime(lastExecuteTime);
                                    slowSqlList.add(slowSqlInfo);
                                }
                            }
                        } catch (Exception e) {
                            log.warn("处理慢SQL信息失败", e);
                        }
                    }
                }
            } else {
                // 不包含慢SQL列表时，只统计总数
                if (sqlList != null) {
                    for (Map<String, Object> sql : sqlList) {
                        Long count = getLongValue(sql, "ExecuteCount");
                        Long millis = getLongValue(sql, "ExecuteMillisTotal");
                        Long slowCount = getLongValue(sql, "SlowCount");
                        Long errorCount = getLongValue(sql, "ErrorCount");

                        if (count != null) executeCount += count;
                        if (millis != null) executeMillisTotal += millis;
                        if (slowCount != null) slowSqlCount += slowCount;
                        if (errorCount != null) errorSqlCount += errorCount;
                    }
                }
            }

            sqlStatInfo.setExecuteCount(executeCount);
            sqlStatInfo.setExecuteMillisTotal(executeMillisTotal);
            sqlStatInfo.setExecuteMillisAvg(executeCount > 0 ? (double) executeMillisTotal / executeCount : 0.0);
            sqlStatInfo.setSlowSqlCount(slowSqlCount);
            sqlStatInfo.setErrorSqlCount(errorSqlCount);
            monitorVO.setSqlStat(sqlStatInfo);

            // 设置慢SQL列表
            monitorVO.setSlowSqlList(slowSqlList);

            monitorVO.setAvailable(true);
            return monitorVO;
        } catch (Exception e) {
            log.error("获取 Druid 监控信息失败", e);
            DruidMonitorVO vo = new DruidMonitorVO();
            vo.setAvailable(false);
            vo.setMessage("获取 Druid 监控信息失败: " + e.getMessage());
            return vo;
        }
    }

    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取字符串值
     */
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 使用 SCAN 命令扫描 Redis key，避免阻塞 Redis
     *
     * @param pattern key 匹配模式
     * @return 匹配的 key 集合
     */
    private Set<String> scanKeys(String pattern) {
        Set<String> keys = new HashSet<>();
        if (redisTemplate == null) {
            return keys;
        }

        try {
            ScanOptions options = ScanOptions.scanOptions()
                    .match(pattern)
                    .count(SCAN_COUNT)
                    .build();

            try (Cursor<String> cursor = redisTemplate.scan(options)) {
                while (cursor.hasNext()) {
                    keys.add(cursor.next());
                }
            }
        } catch (Exception e) {
            log.error("使用 SCAN 命令扫描 key 失败，pattern: {}", pattern, e);
        }

        return keys;
    }

    /**
     * 获取在线用户列表
     * <p>
     * 说明：基于 JWT 无状态认证，通过 Redis 中存储的刷新令牌（jwt:refresh:user:{userId}）来判断用户是否在线。
     * 从刷新令牌的存储结构中读取完整的登录信息（IP、浏览器、操作系统、登录地点、登录时间等）。
     * </p>
     *
     * @param userName 用户名（可选，用于过滤）
     * @param ipaddr   IP地址（可选，用于过滤）
     * @return 在线用户列表
     */
    @Override
    public List<OnlineUserVO> getOnlineUserList(String userName, String ipaddr) {
        List<OnlineUserVO> onlineUserList = new ArrayList<>();

        if (redisTemplate == null || sysUserService == null || refreshTokenManager == null) {
            log.warn("Redis、SysUserService 或 RefreshTokenManager 未配置，无法获取在线用户列表");
            return onlineUserList;
        }

        try {
            // 参数规范化处理
            String normalizedUserName = (userName != null) ? userName.trim() : null;
            String normalizedIpaddr = (ipaddr != null) ? ipaddr.trim() : null;

            // 使用 SCAN 命令替代 keys()，避免阻塞 Redis
            // 刷新令牌的 key 格式为 "{REDIS_KEY_PREFIX_JWT_REFRESH_USER}:{userId}"
            Set<String> keys = scanKeys(REDIS_KEY_PREFIX_JWT_REFRESH_USER + ":*");

            if (!keys.isEmpty()) {
                // 批量查询优化：先收集所有 userId
                List<Long> userIds = new ArrayList<>();
                Map<String, Long> keyToUserIdMap = new HashMap<>();

                for (String key : keys) {
                    try {
                        String userIdStr = key.substring((REDIS_KEY_PREFIX_JWT_REFRESH_USER + ":").length());
                        Long userId = Long.parseLong(userIdStr);
                        userIds.add(userId);
                        keyToUserIdMap.put(key, userId);
                    } catch (NumberFormatException e) {
                        log.warn("解析用户ID失败，key: {}", key, e);
                    }
                }

                // 批量查询用户信息
                Map<Long, SysUser> userMap = new HashMap<>();
                if (!userIds.isEmpty() && sysUserService != null) {
                    try {
                        List<SysUser> users = sysUserService.listByIds(userIds);
                        if (users != null) {
                            userMap = users.stream()
                                    .collect(Collectors.toMap(SysUser::getUserId, user -> user));
                        }
                    } catch (Exception e) {
                        log.error("批量查询用户信息失败", e);
                    }
                }

                // 批量查询部门信息
                Map<Long, String> deptNameMap = new HashMap<>();
                if (sysDeptService != null) {
                    try {
                        Set<Long> deptIds = userMap.values().stream()
                                .map(SysUser::getDeptId)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet());

                        if (!deptIds.isEmpty()) {
                            List<com.star.pivot.system.domain.entity.SysDept> depts =
                                    sysDeptService.listByIds(new ArrayList<>(deptIds));
                            if (depts != null) {
                                deptNameMap = depts.stream()
                                        .collect(Collectors.toMap(
                                                com.star.pivot.system.domain.entity.SysDept::getDeptId,
                                                com.star.pivot.system.domain.entity.SysDept::getDeptName));
                            }
                        }
                    } catch (Exception e) {
                        log.warn("批量查询部门信息失败", e);
                    }
                }

                // 构建在线用户列表
                for (Map.Entry<String, Long> entry : keyToUserIdMap.entrySet()) {
                    try {
                        String key = entry.getKey();
                        Long userId = entry.getValue();

                        // 从 RefreshTokenManager 获取完整的登录信息
                        RefreshTokenValue tokenValue = refreshTokenManager.getRefreshTokenValue(userId);
                        if (tokenValue == null) {
                            // 刷新令牌不存在或已过期，跳过
                            continue;
                        }

                        // 从批量查询结果中获取用户信息
                        SysUser user = userMap.get(userId);
                        if (user == null) {
                            // 用户不存在，跳过
                            continue;
                        }

                        // 构建在线用户信息（使用公共方法）
                        OnlineUserVO onlineUser = buildOnlineUserVO(key, userId, user, tokenValue, deptNameMap);

                        // 过滤条件：用户名
                        if (normalizedUserName != null && !normalizedUserName.isEmpty()) {
                            String userDisplayName = user.getUserName() != null ? user.getUserName() : "";
                            String userNickName = user.getNickName() != null ? user.getNickName() : "";
                            if (!userDisplayName.contains(normalizedUserName) && !userNickName.contains(normalizedUserName)) {
                                continue;
                            }
                        }

                        // 过滤条件：IP地址
                        if (normalizedIpaddr != null && !normalizedIpaddr.isEmpty()) {
                            String userIp = onlineUser.getIpaddr();
                            if (userIp == null || !userIp.contains(normalizedIpaddr)) {
                                continue;
                            }
                        }

                        onlineUserList.add(onlineUser);
                    } catch (Exception e) {
                        log.warn("构建在线用户信息失败，userId: {}, error: {}",
                            entry.getValue(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取在线用户列表失败", e);
        }

        return onlineUserList;
    }

    /**
     * 将 Date 转换为 LocalDateTime
     */
    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), java.time.ZoneId.systemDefault());
    }

    /**
     * 构建在线用户 VO（公共方法，消除代码重复）
     *
     * @param sessionId   会话ID
     * @param userId      用户ID
     * @param user        用户实体
     * @param tokenValue  刷新令牌值
     * @param deptNameMap 部门名称映射（可为空）
     * @return 在线用户 VO
     */
    private OnlineUserVO buildOnlineUserVO(String sessionId, Long userId, SysUser user,
                                            RefreshTokenValue tokenValue,
                                            Map<Long, String> deptNameMap) {
        OnlineUserVO onlineUser = new OnlineUserVO();
        onlineUser.setSessionId(sessionId);
        onlineUser.setUserId(userId);
        onlineUser.setUserName(user.getUserName());
        onlineUser.setNickName(user.getNickName());

        // 获取部门名称（优先使用传入的映射，避免重复查询）
        if (deptNameMap != null && user.getDeptId() != null) {
            String deptName = deptNameMap.get(user.getDeptId());
            if (deptName != null) {
                onlineUser.setDeptName(deptName);
            }
        } else if (user.getDeptId() != null && sysDeptService != null) {
            // 降级方案：如果映射为空，则单独查询
            try {
                com.star.pivot.system.domain.entity.SysDept dept = sysDeptService.getById(user.getDeptId());
                if (dept != null) {
                    onlineUser.setDeptName(dept.getDeptName());
                }
            } catch (Exception e) {
                log.debug("获取部门信息失败，deptId: {}", user.getDeptId());
            }
        }

        // 从 RefreshTokenValue 中读取登录信息
        // 注意：如果用户是在实现此功能之前登录的，这些字段可能为 null（旧数据）
        onlineUser.setIpaddr(tokenValue.getIpaddr() != null ? tokenValue.getIpaddr() : "");
        onlineUser.setBrowser(tokenValue.getBrowser() != null ? tokenValue.getBrowser() : "");
        onlineUser.setOs(tokenValue.getOs() != null ? tokenValue.getOs() : "");
        onlineUser.setLoginLocation(tokenValue.getLoginLocation() != null ? tokenValue.getLoginLocation() : "");

        // 转换登录时间和最后访问时间
        if (tokenValue.getIssuedAt() != null) {
            onlineUser.setLoginTime(convertToLocalDateTime(tokenValue.getIssuedAt()));
        } else {
            onlineUser.setLoginTime(LocalDateTime.now());
        }

        if (tokenValue.getLastAccessTime() != null) {
            onlineUser.setLastAccessTime(convertToLocalDateTime(tokenValue.getLastAccessTime()));
        } else {
            // 如果没有最后访问时间，使用登录时间
            onlineUser.setLastAccessTime(onlineUser.getLoginTime());
        }

        return onlineUser;
    }

    /**
     * 强制用户下线
     * <p>
     * 说明：通过删除 Redis 中的刷新令牌来实现强制下线。
     * 由于 JWT 是无状态的，无法直接使已签发的 Token 失效，
     * 因此通过删除刷新令牌，使用户无法刷新 Token，从而间接实现下线效果。
     * </p>
     * <p>
     * 注意：已签发的 Access Token 在过期前仍然有效，这是 JWT 无状态特性的限制。
     * 如需立即失效，可考虑引入 Token 黑名单机制。
     * </p>
     *
     * @param sessionId 会话ID（实际为 Redis 中的刷新令牌 key，格式：jwt:refresh:user:{userId}）
     * @return 是否成功
     */
    @Override
    public boolean forceLogout(String sessionId) {
        // 参数校验
        if (sessionId == null || sessionId.trim().isEmpty()) {
            log.warn("强制下线失败：sessionId 为空");
            return false;
        }

        try {
            // sessionId 格式为 "{REDIS_KEY_PREFIX_JWT_REFRESH_USER}:{userId}"
            if (sessionId.startsWith(REDIS_KEY_PREFIX_JWT_REFRESH_USER + ":")) {
                String userIdStr = sessionId.substring((REDIS_KEY_PREFIX_JWT_REFRESH_USER + ":").length());
                Long userId = Long.parseLong(userIdStr);

                // 统一通过 TokenService 处理刷新令牌吊销与在线用户历史记录（1 表示强制下线）
                if (tokenService != null) {
                    tokenService.forceLogout(userId, "1");
                    log.info("强制用户下线成功，sessionId: {}", sessionId);
                    return true;
                }

                // 兼容降级逻辑：直接删除刷新令牌
                if (refreshTokenManager != null) {
                    refreshTokenManager.revokeRefreshToken(userId);
                }

                // 降级逻辑需要手动删除 Redis key
                redisTemplate.delete(sessionId);
                String userKey = REDIS_KEY_PREFIX_ONLINE_USER + ":" + userIdStr;
                redisTemplate.delete(userKey);
                log.info("强制用户下线成功（降级模式），sessionId: {}", sessionId);
                return true;
            } else {
                // 如果不是标准格式，直接删除
                redisTemplate.delete(sessionId);
                log.info("强制用户下线成功（非标准格式），sessionId: {}", sessionId);
                return true;
            }
        } catch (Exception e) {
            log.error("强制用户下线失败: {}", sessionId, e);
            return false;
        }
    }





    @Override
    public List<RedisCacheVO> getCacheList() {
        if (redisTemplate == null) {
            throw new BizException(ErrorCode.REDIS_ERROR, "Redis 未配置或未启用");
        }

        try {
            // 扫描所有键，动态发现缓存组
            Set<String> allKeys = scanKeys("*");

            // 按前缀分组
            Map<String, Set<String>> cacheGroups = new HashMap<>();
            for (String key : allKeys) {
                // 提取键的前缀（第一个冒号之前的部分，或者整个键如果没有冒号）
                String prefix = extractCachePrefix(key);
                if (prefix != null && !prefix.isEmpty()) {
                    cacheGroups.computeIfAbsent(prefix, k -> new HashSet<>()).add(key);
                }
            }

            // 构建缓存列表
            List<RedisCacheVO> cacheList = new ArrayList<>();
            for (Map.Entry<String, Set<String>> entry : cacheGroups.entrySet()) {
                RedisCacheVO cacheVO = new RedisCacheVO();
                cacheVO.setCacheName(entry.getKey());
                // 使用备注映射，如果没有则使用前缀作为备注
                String remark = CACHE_REMARK_MAP.getOrDefault(entry.getKey(), entry.getKey());
                cacheVO.setRemark(remark);
                cacheList.add(cacheVO);
            }

            // 按缓存名称排序
            cacheList.sort(Comparator.comparing(RedisCacheVO::getCacheName));

            return cacheList;
        } catch (Exception e) {
            log.error("获取缓存列表失败", e);
            throw new BizException(ErrorCode.REDIS_ERROR, "获取缓存列表失败: " + e.getMessage());
        }
    }

    /**
     * 提取缓存前缀
     * 规则：取第一个冒号之前的部分作为前缀
     * 例如：jwt:refresh:user:1 -> jwt
     *      sys_config:key -> sys_config
     *      captcha:token -> captcha
     */
    private String extractCachePrefix(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }

        int colonIndex = key.indexOf(':');
        if (colonIndex > 0) {
            return key.substring(0, colonIndex);
        }

        // 如果没有冒号，返回整个键作为前缀
        return key;
    }

    @Override
    public List<RedisCacheVO.CacheKeyInfo> getCacheKeys(String cacheName) {
        if (redisTemplate == null) {
            throw new BizException(ErrorCode.REDIS_ERROR, "Redis 未配置或未启用");
        }

        if (cacheName == null || cacheName.trim().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "缓存名称不能为空");
        }

        try {
            // 使用缓存名称作为前缀，扫描所有匹配的键
            // 例如：cacheName = "jwt"，则扫描 "jwt:*"
            String keyPattern = cacheName + ":*";
            Set<String> keys = scanKeys(keyPattern);
            List<RedisCacheVO.CacheKeyInfo> keyInfoList = new ArrayList<>();

            for (String key : keys) {
                RedisCacheVO.CacheKeyInfo keyInfo = new RedisCacheVO.CacheKeyInfo();
                keyInfo.setKey(key);

                // 获取键类型
                String type = redisTemplate.execute((RedisCallback<String>) connection -> {
                    DataType dataType = connection.keyCommands().type(key.getBytes());
                    return dataType != null ? dataType.code() : "unknown";
                });
                keyInfo.setType(type != null ? type : "unknown");

                // 获取过期时间
                Long ttl = redisTemplate.getExpire(key);
                keyInfo.setTtl(ttl != null ? ttl : -2L);

                // 获取值大小（估算）
                try {
                    Object value = redisTemplate.opsForValue().get(key);
                    if (value != null) {
                        // 简单估算：将对象序列化为字符串的长度
                        long size = value.toString().getBytes().length;
                        keyInfo.setSize(size);
                    } else {
                        keyInfo.setSize(0L);
                    }
                } catch (Exception e) {
                    log.debug("获取键大小失败，key: {}", key, e);
                    keyInfo.setSize(0L);
                }

                keyInfoList.add(keyInfo);
            }

            return keyInfoList;
        } catch (Exception e) {
            log.error("获取缓存键列表失败，cacheName: {}", cacheName, e);
            throw new BizException(ErrorCode.REDIS_ERROR, "获取缓存键列表失败: " + e.getMessage());
        }
    }

    @Override
    public RedisCacheVO.CacheContentInfo getCacheContent(String cacheName, String key) {
        if (redisTemplate == null) {
            throw new BizException(ErrorCode.REDIS_ERROR, "Redis 未配置或未启用");
        }

        if (key == null || key.trim().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "缓存键名不能为空");
        }

        try {
            RedisCacheVO.CacheContentInfo contentInfo = new RedisCacheVO.CacheContentInfo();
            contentInfo.setCacheName(cacheName);
            contentInfo.setKey(key);

            // 检查键是否存在
            Boolean exists = redisTemplate.hasKey(key);
            if (!exists) {
                contentInfo.setType("none");
                contentInfo.setTtl(-2L);
                contentInfo.setContent("(键不存在)");
                return contentInfo;
            }

            // 获取键类型
            String type = redisTemplate.execute((RedisCallback<String>) connection -> {
                try {
                    DataType dataType = connection.keyCommands().type(key.getBytes());
                    return dataType != null ? dataType.code() : "unknown";
                } catch (Exception e) {
                    log.warn("获取键类型失败，key: {}", key, e);
                    return "unknown";
                }
            });
            contentInfo.setType(type != null ? type : "unknown");

            // 获取过期时间
            Long ttl = redisTemplate.getExpire(key);
            contentInfo.setTtl(ttl != null ? ttl : -2L);

            // 根据类型获取缓存值
            String content = getCacheValueByType(key, type);
            contentInfo.setContent(content);

            return contentInfo;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取缓存内容失败，cacheName: {}, key: {}", cacheName, key, e);
            throw new BizException(ErrorCode.REDIS_ERROR, "获取缓存内容失败: " + e.getMessage());
        }
    }

    /**
     * 根据 Redis 数据类型获取缓存值
     *
     * @param key  键名
     * @param type 数据类型（string, hash, list, set, zset）
     * @return 格式化后的内容字符串
     */
    private String getCacheValueByType(String key, String type) {
        if (type == null || "none".equals(type) || "unknown".equals(type)) {
            return "(无法获取内容)";
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // 配置 ObjectMapper，允许未知属性，忽略反序列化错误
            objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);

            switch (type.toLowerCase()) {
                case "string":
                    // String 类型 - 先尝试反序列化，失败则使用原始字节
                    try {
                        Object stringValue = redisTemplate.opsForValue().get(key);
                        if (stringValue != null) {
                            return formatObjectValue(stringValue, objectMapper);
                        }
                    } catch (Exception e) {
                        // 反序列化失败，尝试获取原始字节
                        log.debug("反序列化失败，尝试获取原始字节，key: {}, error: {}", key, e.getMessage());
                    }
                    // 如果反序列化失败，尝试获取原始字节
                    try {
                        byte[] rawBytes = redisTemplate.execute((RedisCallback<byte[]>) connection -> connection.get(key.getBytes()));
                        if (rawBytes != null) {
                            String rawString = new String(rawBytes, "UTF-8");
                            // 尝试格式化 JSON
                            try {
                                Object jsonValue = objectMapper.readValue(rawString, Object.class);
                                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonValue);
                            } catch (Exception e) {
                                // 不是有效的 JSON，直接返回原始字符串
                                return rawString;
                            }
                        }
                    } catch (Exception e) {
                        log.debug("获取原始字节失败，key: {}", key, e);
                    }
                    return "(空值)";

                case "hash":
                    // Hash 类型
                    Map<Object, Object> hashValue = redisTemplate.opsForHash().entries(key);
                    if (!hashValue.isEmpty()) {
                        return formatObjectValue(hashValue, objectMapper);
                    }
                    return "(空哈希)";

                case "list":
                    // List 类型
                    List<Object> listValue = redisTemplate.opsForList().range(key, 0, -1);
                    if (listValue != null && !listValue.isEmpty()) {
                        return formatObjectValue(listValue, objectMapper);
                    }
                    return "(空列表)";

                case "set":
                    // Set 类型
                    Set<Object> setValue = redisTemplate.opsForSet().members(key);
                    if (setValue != null && !setValue.isEmpty()) {
                        return formatObjectValue(setValue, objectMapper);
                    }
                    return "(空集合)";

                case "zset":
                    // Sorted Set 类型
                    Set<Object> zsetValue = redisTemplate.opsForZSet().range(key, 0, -1);
                    if (zsetValue != null && !zsetValue.isEmpty()) {
                        // 获取带分数的有序集合
                        Map<String, Double> zsetWithScore = new LinkedHashMap<>();
                        for (Object member : zsetValue) {
                            Double score = redisTemplate.opsForZSet().score(key, member);
                            zsetWithScore.put(member.toString(), score != null ? score : 0.0);
                        }
                        return formatObjectValue(zsetWithScore, objectMapper);
                    }
                    return "(空有序集合)";

                default:
                    // 未知类型，尝试作为 String 获取
                    try {
                        Object value = redisTemplate.opsForValue().get(key);
                        if (value != null) {
                            return formatObjectValue(value, objectMapper);
                        }
                    } catch (Exception e) {
                        log.debug("尝试获取未知类型键值失败，key: {}, type: {}", key, type, e);
                    }
                    return "(不支持的类型: " + type + ")";
            }
        } catch (Exception e) {
            log.error("获取缓存值失败，key: {}, type: {}", key, type, e);
            return "(获取内容失败: " + e.getMessage() + ")";
        }
    }

    /**
     * 格式化对象值为字符串
     * 对于无法序列化的对象，提供友好的降级处理
     *
     * @param value 对象值
     * @param objectMapper Jackson ObjectMapper
     * @return 格式化后的字符串
     */
    private String formatObjectValue(Object value, ObjectMapper objectMapper) {
        if (value == null) {
            return "(null)";
        }

        // 如果是基本类型或字符串，直接返回
        if (value instanceof String || value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }

        // 尝试使用 Jackson 序列化
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
            // Jackson 无法序列化（通常是缺少默认构造函数或复杂的嵌套对象）
            log.debug("Jackson 序列化失败，尝试使用反射获取对象信息: {}", e.getMessage());
            return formatObjectWithReflection(value);
        } catch (Exception e) {
            // 其他序列化错误，使用反射方式
            log.debug("序列化失败，使用反射方式: {}", e.getMessage());
            return formatObjectWithReflection(value);
        }
    }

    /**
     * 使用反射方式格式化对象
     * 对于无法序列化的对象，尝试提取关键信息
     *
     * @param value 对象值
     * @return 格式化后的字符串
     */
    private String formatObjectWithReflection(Object value) {
        if (value == null) {
            return "(null)";
        }

        try {
            Class<?> clazz = value.getClass();
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append("  \"@class\": \"").append(clazz.getName()).append("\",\n");

            // 尝试使用反射获取字段值
            java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
            boolean hasFields = false;
            for (java.lang.reflect.Field field : fields) {
                // 跳过静态字段和序列化相关字段
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) ||
                    "serialVersionUID".equals(field.getName())) {
                    continue;
                }

                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(value);
                    if (hasFields) {
                        sb.append(",\n");
                    }
                    sb.append("  \"").append(field.getName()).append("\": ");

                    if (fieldValue == null) {
                        sb.append("null");
                    } else if (fieldValue instanceof String || fieldValue instanceof Number || fieldValue instanceof Boolean) {
                        // 基本类型，直接输出
                        if (fieldValue instanceof String) {
                            sb.append("\"").append(escapeJsonString(fieldValue.toString())).append("\"");
                        } else {
                            sb.append(fieldValue);
                        }
                    } else {
                        // 复杂对象，显示类型和 toString()
                        sb.append("\"").append(escapeJsonString(fieldValue.getClass().getSimpleName() + ": " + fieldValue)).append("\"");
                    }
                    hasFields = true;
                } catch (Exception e) {
                    // 忽略无法访问的字段
                    log.debug("无法访问字段 {}: {}", field.getName(), e.getMessage());
                }
            }

            if (!hasFields) {
                // 如果没有可访问的字段，使用 toString()
                sb.append("  \"toString\": \"").append(escapeJsonString(value.toString())).append("\"");
            }

            sb.append("\n}");
            return sb.toString();
        } catch (Exception e) {
            log.debug("反射格式化失败，使用 toString(): {}", e.getMessage());
            // 最后的降级方案：使用 toString()
            return "{\n  \"@class\": \"" + value.getClass().getName() + "\",\n" +
                   "  \"toString\": \"" + escapeJsonString(value.toString()) + "\"\n}";
        }
    }

    /**
     * 转义 JSON 字符串中的特殊字符
     *
     * @param str 原始字符串
     * @return 转义后的字符串
     */
    private String escapeJsonString(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    @Override
    public long deleteCache(String cacheName) {
        if (redisTemplate == null) {
            throw new BizException(ErrorCode.REDIS_ERROR, "Redis 未配置或未启用");
        }

        if (cacheName == null || cacheName.trim().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "缓存名称不能为空");
        }

        try {
            String keyPattern = cacheName + ":*";
            Set<String> keys = scanKeys(keyPattern);
            if (keys.isEmpty()) {
                return 0L;
            }

            Long deletedCount = redisTemplate.delete(keys);
            return deletedCount != null ? deletedCount : 0L;
        } catch (Exception e) {
            log.error("删除缓存失败，cacheName: {}", cacheName, e);
            throw new BizException(ErrorCode.REDIS_ERROR, "删除缓存失败: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteCacheKey(String cacheName, String key) {
        if (redisTemplate == null) {
            throw new BizException(ErrorCode.REDIS_ERROR, "Redis 未配置或未启用");
        }

        if (key == null || key.trim().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "缓存键名不能为空");
        }

        try {
            Boolean deleted = redisTemplate.delete(key);
            return Boolean.TRUE.equals(deleted);
        } catch (Exception e) {
            log.error("删除缓存键失败，cacheName: {}, key: {}", cacheName, key, e);
            throw new BizException(ErrorCode.REDIS_ERROR, "删除缓存键失败: " + e.getMessage());
        }
    }

    @Override
    public boolean clearAllCache() {
        if (redisTemplate == null) {
            throw new BizException(ErrorCode.REDIS_ERROR, "Redis 未配置或未启用");
        }

        try {
            redisTemplate.execute((RedisCallback<Object>) connection -> {
                RedisServerCommands serverCommands = connection.serverCommands();
                serverCommands.flushDb(RedisServerCommands.FlushOption.ASYNC);
                return null;
            });
            log.info("Redis 当前数据库已成功清空（FLUSHDB）");
            return true;
        } catch (Exception e) {
            log.error("清空所有缓存失败", e);
            throw new BizException(ErrorCode.REDIS_ERROR, "清空所有缓存失败: " + e.getMessage());
        }
    }

    @Override
    public PageResponse<SysMonitorApiPerformance> getApiPerformancePageList(ApiPerformanceReqBo reqBo) {
        PageResponse<SysMonitorApiPerformance> pageResponse = new PageResponse<>();

        if (apiPerformanceMapper == null) {
            log.warn("ApiPerformanceMapper 未配置，无法查询API性能数据");
            pageResponse.setTotal(0L);
            pageResponse.setRows(new ArrayList<>());
            pageResponse.setPageNum(1L);
            pageResponse.setPageSize(10L);
            pageResponse.setPageCount(0L);
            return pageResponse;
        }

        // 构建查询条件
        LambdaQueryWrapper<SysMonitorApiPerformance> queryWrapper = new LambdaQueryWrapper<>();

        // 接口路径（模糊查询）
        if (StringUtils.hasText(reqBo.getApiPath())) {
            queryWrapper.like(SysMonitorApiPerformance::getApiPath, reqBo.getApiPath());
        }

        // 请求方法
        if (StringUtils.hasText(reqBo.getApiMethod())) {
            queryWrapper.eq(SysMonitorApiPerformance::getApiMethod, reqBo.getApiMethod());
        }

        // 日期范围
        if (reqBo.getStartDate() != null) {
            queryWrapper.ge(SysMonitorApiPerformance::getStatDate, reqBo.getStartDate());
        }
        if (reqBo.getEndDate() != null) {
            queryWrapper.le(SysMonitorApiPerformance::getStatDate, reqBo.getEndDate());
        }

        // 排序
        if (StringUtils.hasText(reqBo.getOrderBy())) {
            String orderBy = reqBo.getOrderBy();
            boolean isAsc = "asc".equalsIgnoreCase(reqBo.getOrderDirection());

            switch (orderBy.toLowerCase()) {
                case "responsetimeavg":
                    if (isAsc) {
                        queryWrapper.orderByAsc(SysMonitorApiPerformance::getResponseTimeAvg);
                    } else {
                        queryWrapper.orderByDesc(SysMonitorApiPerformance::getResponseTimeAvg);
                    }
                    break;
                case "errorcount":
                    if (isAsc) {
                        queryWrapper.orderByAsc(SysMonitorApiPerformance::getErrorCount);
                    } else {
                        queryWrapper.orderByDesc(SysMonitorApiPerformance::getErrorCount);
                    }
                    break;
                case "requestcount":
                    if (isAsc) {
                        queryWrapper.orderByAsc(SysMonitorApiPerformance::getRequestCount);
                    } else {
                        queryWrapper.orderByDesc(SysMonitorApiPerformance::getRequestCount);
                    }
                    break;
                default:
                    // 默认按平均响应时间倒序
                    queryWrapper.orderByDesc(SysMonitorApiPerformance::getResponseTimeAvg);
                    break;
            }
        } else {
            // 默认按平均响应时间倒序
            queryWrapper.orderByDesc(SysMonitorApiPerformance::getResponseTimeAvg);
        }

        // 分页查询
        Page<SysMonitorApiPerformance> page = new Page<>(reqBo.getPageNum(), reqBo.getPageSize());
        IPage<SysMonitorApiPerformance> pageList = apiPerformanceMapper.selectPage(page, queryWrapper);

        // 设置分页结果
        pageResponse.setTotal(pageList.getTotal());
        pageResponse.setRows(pageList.getRecords());
        pageResponse.setPageNum(pageList.getCurrent());
        pageResponse.setPageSize(pageList.getSize());
        pageResponse.setPageCount(pageList.getPages());

        return pageResponse;
    }

    @Override
    public List<SysMonitorApiPerformance> getSlowestApis(Integer limit, LocalDate startDate, LocalDate endDate) {
        if (apiPerformanceMapper == null) {
            log.warn("ApiPerformanceMapper 未配置，无法查询最慢API");
            return new ArrayList<>();
        }

        return apiPerformanceMapper.selectSlowestApis(limit, startDate, endDate);
    }

    @Override
    public List<SysMonitorApiPerformance> getHighestErrorRateApis(Integer limit, LocalDate startDate, LocalDate endDate) {
        if (apiPerformanceMapper == null) {
            log.warn("ApiPerformanceMapper 未配置，无法查询错误率最高的API");
            return new ArrayList<>();
        }

        return apiPerformanceMapper.selectHighestErrorRateApis(limit, startDate, endDate);
    }
}
