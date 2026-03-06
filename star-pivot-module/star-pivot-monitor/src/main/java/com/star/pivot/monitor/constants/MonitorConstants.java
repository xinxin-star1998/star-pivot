package com.star.pivot.monitor.constants;

/**
 * 监控模块常量类
 *
 * @author xinxin
 * @since 2026-03-04
 */
public class MonitorConstants {

    /**
     * 监控模块前缀
     */
    public static final String MONITOR_PREFIX = "monitor";

    /**
     * API性能监控路径
     */
    public static final String API_PERFORMANCE_PATH = "/monitor/api";

    /**
     * 服务器监控路径
     */
    public static final String SERVER_MONITOR_PATH = "/monitor/server";

    /**
     * 缓存管理路径
     */
    public static final String CACHE_MANAGEMENT_PATH = "/monitor/cache";

    /**
     * Druid监控路径
     */
    public static final String DRUID_MONITOR_PATH = "/monitor/druid";

    /**
     * 健康检查路径
     */
    public static final String HEALTH_CHECK_PATH = "/monitor/health";

    /**
     * 在线用户管理路径
     */
    public static final String ONLINE_USER_PATH = "/monitor/online";

    /**
     * 默认慢接口阈值（毫秒）
     */
    public static final long DEFAULT_SLOW_API_THRESHOLD = 1000L;

    /**
     * 默认采样率
     */
    public static final double DEFAULT_SAMPLE_RATE = 0.1;

    /**
     * 默认批量写入队列大小
     */
    public static final int DEFAULT_QUEUE_SIZE = 1000;

    /**
     * 默认批量写入批次大小
     */
    public static final int DEFAULT_BATCH_SIZE = 100;

    /**
     * 默认批量写入间隔（秒）
     */
    public static final int DEFAULT_BATCH_INTERVAL = 5;

    /**
     * 权限前缀
     */
    public static final String PERMISSION_PREFIX = "monitor:";

    private MonitorConstants() {
        // 私有构造函数，防止实例化
    }
}
