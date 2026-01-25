package com.star.pivot.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 监控告警规则实体类
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Data
@TableName("sys_monitor_alert_rule")
public class SysMonitorAlertRule {

    /**
     * 规则ID
     */
    @TableId(value = "rule_id", type = IdType.AUTO)
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
     * 阈值类型（0大于 1小于 2等于）
     */
    private String thresholdType;

    /**
     * 阈值
     */
    private BigDecimal thresholdValue;

    /**
     * 告警级别（0低 1中 2高 3紧急）
     */
    private String alertLevel;

    /**
     * 告警渠道（逗号分隔：in_app,email,sms,webhook）
     */
    private String alertChannels;

    /**
     * Webhook地址
     */
    private String webhookUrl;

    /**
     * 是否启用（0否 1是）
     */
    private String enabled;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
