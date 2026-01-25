package com.star.pivot.system.service.impl;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.star.pivot.system.domain.entity.SysMonitorSlowSql;
import com.star.pivot.system.mapper.SysMonitorSlowSqlMapper;
import com.star.pivot.system.service.SlowSqlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 慢SQL服务实现类
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SlowSqlServiceImpl implements SlowSqlService {

    private final SysMonitorSlowSqlMapper slowSqlMapper;
    private static final long DEFAULT_SLOW_SQL_THRESHOLD = 5000L; // 默认慢SQL阈值：5秒

    @Override
    public List<SysMonitorSlowSql> extractSlowSqlList(Long slowSqlThreshold) {
        List<SysMonitorSlowSql> slowSqlList = new ArrayList<>();
        
        if (slowSqlThreshold == null || slowSqlThreshold <= 0) {
            slowSqlThreshold = DEFAULT_SLOW_SQL_THRESHOLD;
        }

        try {
            DruidStatManagerFacade statManagerFacade = DruidStatManagerFacade.getInstance();
            List<Map<String, Object>> sqlList = statManagerFacade.getSqlStatDataList(null);

            if (sqlList != null) {
                for (Map<String, Object> sql : sqlList) {
                    try {
                        String sqlId = getStringValue(sql, "ID");
                        String sqlText = getStringValue(sql, "SQL");
                        Long executeCount = getLongValue(sql, "ExecuteCount");
                        Long executeMillisTotal = getLongValue(sql, "ExecuteMillisTotal");
                        Long executeMillisMax = getLongValue(sql, "ExecuteMillisMax");
                        Long slowCount = getLongValue(sql, "SlowCount");
                        Long errorCount = getLongValue(sql, "ErrorCount");
                        Long lastExecuteTime = getLongValue(sql, "LastExecuteTime");

                        // 计算平均执行时间
                        double executeTimeAvg = executeCount > 0 ? (double) executeMillisTotal / executeCount : 0;

                        // 判断是否为慢SQL
                        if (executeTimeAvg >= slowSqlThreshold || slowCount > 0) {
                            SysMonitorSlowSql slowSql = new SysMonitorSlowSql();
                            slowSql.setSqlId(sqlId);
                            slowSql.setSqlText(sqlText != null ? truncateString(sqlText, 5000) : "");
                            slowSql.setExecuteCount(executeCount != null ? executeCount : 0L);
                            slowSql.setExecuteTimeTotal(executeMillisTotal != null ? executeMillisTotal : 0L);
                            slowSql.setExecuteTimeMax(executeMillisMax != null ? executeMillisMax : 0L);
                            slowSql.setExecuteTimeAvg(BigDecimal.valueOf(executeTimeAvg));
                            slowSql.setSlowCount(slowCount != null ? slowCount : 0L);
                            slowSql.setErrorCount(errorCount != null ? errorCount : 0L);
                            
                            if (lastExecuteTime != null && lastExecuteTime > 0) {
                                slowSql.setLastExecuteTime(
                                        LocalDateTime.ofEpochSecond(lastExecuteTime / 1000, 0, 
                                                java.time.ZoneOffset.of("+8"))
                                );
                            }

                            // 生成优化建议
                            String suggestion = generateOptimizationSuggestion(
                                    sqlText, executeTimeAvg, executeCount);
                            slowSql.setOptimizationSuggestion(suggestion);
                            slowSql.setStatus("0"); // 待优化

                            // 保存或更新慢SQL记录
                            saveOrUpdateSlowSql(slowSql);
                            slowSqlList.add(slowSql);
                        }
                    } catch (Exception e) {
                        log.warn("提取慢SQL失败", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("从Druid提取慢SQL列表失败", e);
        }

        return slowSqlList;
    }

    /**
     * 保存或更新慢SQL记录
     */
    @Transactional(rollbackFor = Exception.class)
    private void saveOrUpdateSlowSql(SysMonitorSlowSql slowSql) {
        SysMonitorSlowSql existing = slowSqlMapper.selectOne(
                new LambdaQueryWrapper<SysMonitorSlowSql>()
                        .eq(SysMonitorSlowSql::getSqlId, slowSql.getSqlId())
        );

        if (existing != null) {
            // 更新现有记录
            slowSql.setId(existing.getId());
            slowSql.setCreateTime(existing.getCreateTime());
            slowSql.setUpdateTime(LocalDateTime.now());
            // 如果状态不是"已优化"或"已忽略"，则更新数据
            if (!"1".equals(existing.getStatus()) && !"2".equals(existing.getStatus())) {
                slowSqlMapper.updateById(slowSql);
            }
        } else {
            // 插入新记录
            slowSql.setCreateTime(LocalDateTime.now());
            slowSqlMapper.insert(slowSql);
        }
    }

    @Override
    public SysMonitorSlowSql getSlowSqlDetail(String sqlId) {
        return slowSqlMapper.selectOne(
                new LambdaQueryWrapper<SysMonitorSlowSql>()
                        .eq(SysMonitorSlowSql::getSqlId, sqlId)
        );
    }

    @Override
    public List<SysMonitorSlowSql> getSlowSqlList(Integer limit) {
        LambdaQueryWrapper<SysMonitorSlowSql> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysMonitorSlowSql::getExecuteTimeAvg);
        wrapper.orderByDesc(SysMonitorSlowSql::getSlowCount);
        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        return slowSqlMapper.selectList(wrapper);
    }

    @Override
    public String generateOptimizationSuggestion(String sqlText, Double executeTimeAvg, Long executeCount) {
        if (sqlText == null || sqlText.isEmpty()) {
            return "无法生成优化建议：SQL语句为空";
        }

        StringBuilder suggestion = new StringBuilder();

        // 检查是否有索引提示
        if (!sqlText.toUpperCase().contains("INDEX") && !sqlText.toUpperCase().contains("USE INDEX")) {
            suggestion.append("建议：检查是否缺少必要的索引。");
        }

        // 检查是否有SELECT *
        if (sqlText.toUpperCase().contains("SELECT *")) {
            suggestion.append("建议：避免使用SELECT *，只查询需要的字段。");
        }

        // 检查是否有子查询
        if (sqlText.toUpperCase().contains("SELECT") && 
            sqlText.toUpperCase().split("SELECT").length > 2) {
            suggestion.append("建议：考虑将子查询优化为JOIN操作。");
        }

        // 检查是否有LIKE '%xxx%'
        if (sqlText.toUpperCase().contains("LIKE '%") || sqlText.toUpperCase().contains("LIKE \"%")) {
            suggestion.append("建议：避免使用前导通配符的LIKE查询，考虑使用全文索引。");
        }

        // 根据执行时间给出建议
        if (executeTimeAvg != null && executeTimeAvg > 10000) {
            suggestion.append("建议：执行时间较长，考虑优化SQL逻辑或添加索引。");
        }

        // 根据执行次数给出建议
        if (executeCount != null && executeCount > 10000) {
            suggestion.append("建议：执行次数较多，考虑使用缓存或优化查询逻辑。");
        }

        if (suggestion.length() == 0) {
            suggestion.append("SQL执行正常，暂无优化建议。");
        }

        return suggestion.toString();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSlowSqlStatus(Long id, String status) {
        try {
            LambdaUpdateWrapper<SysMonitorSlowSql> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(SysMonitorSlowSql::getId, id);
            wrapper.set(SysMonitorSlowSql::getStatus, status);
            wrapper.set(SysMonitorSlowSql::getUpdateTime, LocalDateTime.now());
            return slowSqlMapper.update(null, wrapper) > 0;
        } catch (Exception e) {
            log.error("更新慢SQL状态失败", e);
            return false;
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
     * 获取Long值
     */
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
     * 截断字符串
     */
    private String truncateString(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }
}
