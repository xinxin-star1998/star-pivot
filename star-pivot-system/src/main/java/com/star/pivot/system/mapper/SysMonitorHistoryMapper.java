package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.system.domain.entity.SysMonitorHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 监控历史数据 Mapper 接口
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Mapper
public interface SysMonitorHistoryMapper extends BaseMapper<SysMonitorHistory> {

    /**
     * 查询指定时间范围内的指标数据（用于趋势图表）
     *
     * @param metricType 指标类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 指标数据列表
     */
    List<SysMonitorHistory> selectByMetricTypeAndTimeRange(
            @Param("metricType") String metricType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 查询多个指标的数据（用于多指标对比）
     *
     * @param metricTypes 指标类型列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 指标数据列表
     */
    List<SysMonitorHistory> selectByMetricTypesAndTimeRange(
            @Param("metricTypes") List<String> metricTypes,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 查询指定时间范围内的指标统计信息（平均值、最大值、最小值）
     *
     * @param metricType 指标类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    Map<String, Object> selectStatisticsByMetricType(
            @Param("metricType") String metricType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
