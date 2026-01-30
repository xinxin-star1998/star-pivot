package com.star.pivot.system.service;

import com.star.pivot.system.domain.entity.SysMonitorSlowSql;

import java.util.List;

/**
 * 慢SQL服务接口
 *
 * @author xinxin
 * @since 2026-01-25
 */
public interface SlowSqlService {

    /**
     * 从Druid统计中提取慢SQL列表
     * <p>
     * 说明：从Druid的SQL统计中提取慢SQL（执行时间超过阈值的SQL），
     * 保存到数据库并提供优化建议。
     * </p>
     *
     * @param slowSqlThreshold 慢SQL阈值（毫秒），默认5000
     * @return 慢SQL列表
     */
    List<SysMonitorSlowSql> extractSlowSqlList(Long slowSqlThreshold);

    /**
     * 获取慢SQL详情
     *
     * @param sqlId SQL ID
     * @return 慢SQL详情
     */
    SysMonitorSlowSql getSlowSqlDetail(String sqlId);

    /**
     * 获取慢SQL列表
     *
     * @param limit 查询数量限制
     * @return 慢SQL列表
     */
    List<SysMonitorSlowSql> getSlowSqlList(Integer limit);

    /**
     * 生成SQL优化建议
     * <p>
     * 说明：根据SQL语句和执行统计信息，生成优化建议。
     * </p>
     *
     * @param sqlText SQL语句
     * @param executeTimeAvg 平均执行时间
     * @param executeCount 执行次数
     * @return 优化建议
     */
    String generateOptimizationSuggestion(String sqlText, Double executeTimeAvg, Long executeCount);

    /**
     * 更新慢SQL状态
     *
     * @param id 慢SQL ID
     * @param status 状态（0待优化 1已优化 2已忽略）
     * @return 是否成功
     */
    boolean updateSlowSqlStatus(Long id, String status);
}
