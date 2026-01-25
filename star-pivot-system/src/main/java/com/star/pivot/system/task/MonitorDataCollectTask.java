package com.star.pivot.system.task;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.star.pivot.system.domain.entity.SysMonitorHistory;
import com.star.pivot.system.mapper.SysMonitorHistoryMapper;
import com.star.pivot.system.service.MonitorService;
import com.star.pivot.system.service.MonitorAlertService;
import com.star.pivot.system.domain.bo.DruidMonitorVO;
import com.star.pivot.system.domain.bo.RedisMonitorVO;
import com.star.pivot.system.domain.bo.ServerInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 监控数据采集定时任务
 * <p>
 * 每5分钟采集一次监控数据并保存到数据库
 * </p>
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MonitorDataCollectTask {

    private final MonitorService monitorService;
    private final MonitorAlertService monitorAlertService;
    private final SysMonitorHistoryMapper monitorHistoryMapper;
    private final DataSource dataSource;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 采集监控数据
     * 每5分钟执行一次（cron表达式：0 * /5 * * * ?）
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void collectMonitorData() {
        try {
            log.debug("开始采集监控数据...");
            LocalDateTime collectTime = LocalDateTime.now();

            // 采集服务器信息
            collectServerInfo(collectTime);

            // 采集Druid监控信息
            collectDruidInfo(collectTime);

            // 采集Redis监控信息
            collectRedisInfo(collectTime);

            log.debug("监控数据采集完成");
        } catch (Exception e) {
            log.error("采集监控数据失败", e);
        }
    }

    /**
     * 检查告警规则并触发告警
     * 每5分钟执行一次（在数据采集之后）
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void checkAlerts() {
        try {
            log.debug("开始检查告警规则...");
            monitorAlertService.checkAndTriggerAlerts();
            log.debug("告警规则检查完成");
        } catch (Exception e) {
            log.error("检查告警规则失败", e);
        }
    }

    /**
     * 采集服务器信息
     */
    private void collectServerInfo(LocalDateTime collectTime) {
        try {
            ServerInfoVO serverInfo = monitorService.getServerInfo();

            // CPU使用率
            if (serverInfo.getCpu() != null) {
                saveMetric("server_cpu", "CPU使用率", serverInfo.getCpu().getUsed(), "%", collectTime);
            }

            // 内存使用率
            if (serverInfo.getMemory() != null) {
                saveMetric("server_memory", "内存使用率", serverInfo.getMemory().getUsage(), "%", collectTime);
                saveMetric("server_memory_used", "内存已用", (double) serverInfo.getMemory().getUsed(), "MB", collectTime);
                saveMetric("server_memory_total", "内存总量", (double) serverInfo.getMemory().getTotal(), "MB", collectTime);
            }

            // 磁盘使用率
            if (serverInfo.getDisk() != null) {
                saveMetric("server_disk", "磁盘使用率", serverInfo.getDisk().getUsage(), "%", collectTime);
                saveMetric("server_disk_used", "磁盘已用", Double.valueOf(serverInfo.getDisk().getUsed()), "GB", collectTime);
                saveMetric("server_disk_total", "磁盘总量", Double.valueOf(serverInfo.getDisk().getTotal()), "GB", collectTime);
            }

            // JVM堆内存使用率
            if (serverInfo.getJvm() != null) {
                saveMetric("jvm_heap", "JVM堆内存使用率", serverInfo.getJvm().getUsage(), "%", collectTime);
                saveMetric("jvm_heap_used", "JVM堆内存已用", (double) serverInfo.getJvm().getUsed(), "MB", collectTime);
                saveMetric("jvm_heap_max", "JVM堆内存最大", (double) serverInfo.getJvm().getMax(), "MB", collectTime);
            }
        } catch (Exception e) {
            log.warn("采集服务器信息失败", e);
        }
    }

    /**
     * 采集Druid监控信息
     */
    private void collectDruidInfo(LocalDateTime collectTime) {
        try {
            DruidMonitorVO druidInfo = monitorService.getDruidMonitorInfo();
            if (!druidInfo.getAvailable()) {
                return;
            }

            // 连接池使用率
            if (druidInfo.getConnectionPool() != null) {
                saveMetric("druid_active", "连接池活跃连接数", 
                        (double) druidInfo.getConnectionPool().getActiveCount(), "个", collectTime);
                saveMetric("druid_pool_usage", "连接池使用率", 
                        druidInfo.getConnectionPool().getUsage(), "%", collectTime);
            }

            // SQL统计
            if (druidInfo.getSqlStat() != null) {
                saveMetric("druid_sql_count", "SQL执行总数", 
                        (double) druidInfo.getSqlStat().getExecuteCount(), "次", collectTime);
                saveMetric("druid_sql_avg_time", "SQL平均执行时间", 
                        druidInfo.getSqlStat().getExecuteMillisAvg(), "ms", collectTime);
                saveMetric("druid_slow_sql_count", "慢SQL数量", 
                        (double) druidInfo.getSqlStat().getSlowSqlCount(), "个", collectTime);
            }
        } catch (Exception e) {
            log.warn("采集Druid监控信息失败", e);
        }
    }

    /**
     * 采集Redis监控信息
     */
    private void collectRedisInfo(LocalDateTime collectTime) {
        try {
            RedisMonitorVO redisInfo = monitorService.getRedisMonitorInfo();
            if (!redisInfo.getAvailable()) {
                return;
            }

            // 内存使用率
            if (redisInfo.getMemory() != null) {
                saveMetric("redis_memory", "Redis内存使用率", 
                        redisInfo.getMemory().getUsage(), "%", collectTime);
                saveMetric("redis_memory_used", "Redis内存已用", 
                        (double) redisInfo.getMemory().getUsedMemory(), "MB", collectTime);
            }

            // 键值统计
            if (redisInfo.getKeys() != null) {
                saveMetric("redis_keys", "Redis键总数", 
                        (double) redisInfo.getKeys().getTotalKeys(), "个", collectTime);
            }

            // 命令统计
            if (redisInfo.getCommands() != null) {
                saveMetric("redis_commands_per_sec", "Redis每秒命令数", 
                        redisInfo.getCommands().getCommandsPerSecond(), "次/秒", collectTime);
            }
        } catch (Exception e) {
            log.warn("采集Redis监控信息失败", e);
        }
    }

    /**
     * 保存指标数据
     */
    private void saveMetric(String metricType, String metricName, Double value, String unit, LocalDateTime collectTime) {
        if (value == null || value.isNaN() || value.isInfinite()) {
            return;
        }

        try {
            SysMonitorHistory history = new SysMonitorHistory();
            history.setMetricType(metricType);
            history.setMetricName(metricName);
            history.setMetricValue(BigDecimal.valueOf(value));
            history.setMetricUnit(unit);
            history.setCollectTime(collectTime);
            monitorHistoryMapper.insert(history);
        } catch (Exception e) {
            log.warn("保存监控指标失败: {}={}", metricName, value, e);
        }
    }
}
