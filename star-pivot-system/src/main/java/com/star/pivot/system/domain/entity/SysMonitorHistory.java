package com.star.pivot.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 监控指标历史数据实体类
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Data
@TableName("sys_monitor_history")
public class SysMonitorHistory {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 指标类型（server_cpu, server_memory, server_disk, jvm_heap, druid_active, redis_memory等）
     */
    private String metricType;

    /**
     * 指标名称
     */
    private String metricName;

    /**
     * 指标值
     */
    private BigDecimal metricValue;

    /**
     * 指标单位（%, MB, GB, ms等）
     */
    private String metricUnit;

    /**
     * 采集时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime collectTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
