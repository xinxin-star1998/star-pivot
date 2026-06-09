package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.system.domain.entity.SysLogininfor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 登录日志Mapper接口
 *
 * @author xinxin
 * @since 2026-01-23
 */
@Mapper
public interface SysLogininforMapper extends BaseMapper<SysLogininfor> {

    /**
     * 按月份统计登录次数
     * @param start 开始时间
     * @param end 结束时间
     * @return List<Object[]> 每个元素为 [yearMonth, count]
     */
    List<Map<String, Object>> countByMonthRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 批量统计用户登录次数
     * @param userNames 用户名列表
     * @param startTime 开始时间
     * @return List<Object[]> 每个元素为 [userName, count]
     */
    List<Map<String, Object>> countByUserNames(@Param("userNames") List<String> userNames, @Param("startTime") LocalDateTime startTime);
}
