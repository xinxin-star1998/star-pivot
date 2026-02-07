package com.star.pivot.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * API接口性能监控实体类
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Data
@TableName("sys_monitor_api_performance")
public class SysMonitorApiPerformance {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 接口路径
     */
    private String apiPath;

    /**
     * 请求方法（GET, POST等）
     */
    private String apiMethod;

    /**
     * 请求次数
     */
    private Long requestCount;

    /**
     * 成功次数
     */
    private Long successCount;

    /**
     * 错误次数
     */
    private Long errorCount;

    /**
     * 总响应时间（毫秒）
     */
    private Long responseTimeTotal;

    /**
     * 最大响应时间（毫秒）
     */
    private Long responseTimeMax;

    /**
     * 最小响应时间（毫秒）
     */
    private Long responseTimeMin;

    /**
     * 平均响应时间（毫秒）
     */
    private BigDecimal responseTimeAvg;

    /**
     * 统计日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate statDate;

    /**
     * 统计小时（0-23，用于按小时统计）
     */
    private Integer statHour;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
