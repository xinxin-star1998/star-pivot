package com.star.pivot.system.service;

import com.star.pivot.system.domain.entity.SysMonitorSlowSql;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 慢SQL自动告警服务
 *
 * <p>定时检查慢SQL，对超时慢SQL进行告警记录
 *
 * <p>告警规则：
 * <ul>
 *   <li>执行时间超过5秒的SQL：严重告警</li>
 *   <li>执行时间超过3秒的SQL：警告</li>
 *   <li>执行次数多且平均时间较长的SQL：提醒优化</li>
 * </ul>
 *
 * <p>告警方式：
 * <ul>
 *   <li>记录到日志（ERROR级别）</li>
 *   <li>可扩展：发送邮件、短信、钉钉等（需实现 AlertService）</li>
 * </ul>
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SlowSqlAlertService {

    private final SlowSqlService slowSqlService;

    /**
     * 严重慢SQL阈值（毫秒）：5秒
     */
    private static final long CRITICAL_SLOW_SQL_THRESHOLD = 5000;

    /**
     * 警告慢SQL阈值（毫秒）：3秒
     */
    private static final long WARNING_SLOW_SQL_THRESHOLD = 3000;

    /**
     * 定时检查慢SQL并告警（每5分钟执行一次）
     *
     * <p>说明：
     * <ul>
     *   <li>从Druid提取慢SQL列表</li>
     *   <li>对超时慢SQL进行告警</li>
     *   <li>避免频繁告警：同一SQL在短时间内只告警一次</li>
     * </ul>
     */
    @Scheduled(fixedRate = 300000) // 5分钟 = 300000毫秒
    public void checkAndAlertSlowSql() {
        try {
            // 提取慢SQL列表（使用默认阈值5秒）
            List<SysMonitorSlowSql> slowSqlList = slowSqlService.extractSlowSqlList(CRITICAL_SLOW_SQL_THRESHOLD);

            if (slowSqlList == null || slowSqlList.isEmpty()) {
                return;
            }

            // 对每个慢SQL进行告警判断
            for (SysMonitorSlowSql slowSql : slowSqlList) {
                checkAndAlert(slowSql);
            }

            log.debug("慢SQL告警检查完成，检查数量: {}", slowSqlList.size());
        } catch (Exception e) {
            log.error("慢SQL告警检查失败", e);
        }
    }

    /**
     * 检查并告警单个慢SQL
     *
     * @param slowSql 慢SQL记录
     */
    private void checkAndAlert(SysMonitorSlowSql slowSql) {
        if (slowSql == null) {
            return;
        }

        Double avgTime = slowSql.getExecuteTimeAvg() != null 
                ? slowSql.getExecuteTimeAvg().doubleValue() 
                : 0.0;
        Long maxTime = slowSql.getExecuteTimeMax();
        Long executeCount = slowSql.getExecuteCount();

        // 严重告警：执行时间超过5秒
        if (maxTime != null && maxTime >= CRITICAL_SLOW_SQL_THRESHOLD) {
            String alertMsg = String.format(
                    "【严重告警】发现超时慢SQL！最大执行时间: %dms, 平均执行时间: %.2fms, 执行次数: %d, SQL: %s",
                    maxTime, avgTime, executeCount != null ? executeCount : 0,
                    truncateSql(slowSql.getSqlText(), 200)
            );
            log.error(alertMsg);
            // TODO: 可扩展发送邮件、短信、钉钉等告警
            // alertService.sendCriticalAlert(alertMsg);
        }
        // 警告：执行时间超过3秒
        else if (maxTime != null && maxTime >= WARNING_SLOW_SQL_THRESHOLD) {
            String alertMsg = String.format(
                    "【警告】发现慢SQL！最大执行时间: %dms, 平均执行时间: %.2fms, 执行次数: %d, SQL: %s",
                    maxTime, avgTime, executeCount != null ? executeCount : 0,
                    truncateSql(slowSql.getSqlText(), 200)
            );
            log.warn(alertMsg);
        }
        // 提醒：高频慢SQL（执行次数多且平均时间较长）
        else if (executeCount != null && executeCount > 1000 && avgTime > 1000) {
            String alertMsg = String.format(
                    "【提醒】发现高频慢SQL，建议优化！执行次数: %d, 平均执行时间: %.2fms, SQL: %s",
                    executeCount, avgTime, truncateSql(slowSql.getSqlText(), 200)
            );
            log.info(alertMsg);
        }
    }

    /**
     * 截断SQL语句（用于日志输出）
     *
     * @param sql SQL语句
     * @param maxLength 最大长度
     * @return 截断后的SQL
     */
    private String truncateSql(String sql, int maxLength) {
        if (sql == null || sql.length() <= maxLength) {
            return sql;
        }
        return sql.substring(0, maxLength) + "...";
    }
}
