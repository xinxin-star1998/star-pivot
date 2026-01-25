package com.star.pivot.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 监控告警记录实体类
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Data
@TableName("sys_monitor_alert_record")
public class SysMonitorAlertRecord {

    /**
     * 记录ID
     */
    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;

    /**
     * 规则ID
     */
    private Long ruleId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 指标类型
     */
    private String metricType;

    /**
     * 指标名称
     */
    private String metricName;

    /**
     * 当前值
     */
    private BigDecimal currentValue;

    /**
     * 阈值
     */
    private BigDecimal thresholdValue;

    /**
     * 告警级别
     */
    private String alertLevel;

    /**
     * 告警状态（0未处理 1已处理 2已忽略）
     */
    private String alertStatus;

    /**
     * 告警渠道
     */
    private String alertChannels;

    /**
     * 告警内容
     */
    private String alertContent;

    /**
     * 告警时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime alertTime;

    /**
     * 处理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime handleTime;

    /**
     * 处理人
     */
    private String handleBy;

    /**
     * 处理备注
     */
    private String handleRemark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
