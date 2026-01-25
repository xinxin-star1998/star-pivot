package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.system.domain.entity.SysMonitorApiPerformance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * API接口性能监控 Mapper 接口
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Mapper
public interface SysMonitorApiPerformanceMapper extends BaseMapper<SysMonitorApiPerformance> {

    /**
     * 查询指定日期范围内的接口性能数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 接口性能数据列表
     */
    List<SysMonitorApiPerformance> selectByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 查询响应时间最慢的接口（Top N）
     *
     * @param limit 查询数量
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 接口性能数据列表
     */
    List<SysMonitorApiPerformance> selectSlowestApis(
            @Param("limit") Integer limit,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 查询错误率最高的接口（Top N）
     *
     * @param limit 查询数量
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 接口性能数据列表
     */
    List<SysMonitorApiPerformance> selectHighestErrorRateApis(
            @Param("limit") Integer limit,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
