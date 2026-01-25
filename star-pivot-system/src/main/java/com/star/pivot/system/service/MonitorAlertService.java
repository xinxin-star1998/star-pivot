package com.star.pivot.system.service;

import com.star.pivot.system.domain.entity.SysMonitorAlertRecord;
import com.star.pivot.system.domain.entity.SysMonitorAlertRule;

import java.util.List;

/**
 * 监控告警服务接口
 *
 * @author xinxin
 * @since 2026-01-25
 */
public interface MonitorAlertService {

    /**
     * 检查告警规则并触发告警
     * <p>
     * 说明：定时任务调用此方法，检查所有启用的告警规则，
     * 如果指标超过阈值，则创建告警记录并发送告警通知。
     * </p>
     */
    void checkAndTriggerAlerts();

    /**
     * 创建告警规则
     *
     * @param rule 告警规则
     * @return 是否成功
     */
    boolean createAlertRule(SysMonitorAlertRule rule);

    /**
     * 更新告警规则
     *
     * @param rule 告警规则
     * @return 是否成功
     */
    boolean updateAlertRule(SysMonitorAlertRule rule);

    /**
     * 删除告警规则
     *
     * @param ruleId 规则ID
     * @return 是否成功
     */
    boolean deleteAlertRule(Long ruleId);

    /**
     * 查询告警规则列表
     *
     * @return 告警规则列表
     */
    List<SysMonitorAlertRule> getAlertRuleList();

    /**
     * 查询告警记录列表
     *
     * @param status 告警状态（可选）
     * @param limit 查询数量限制
     * @return 告警记录列表
     */
    List<SysMonitorAlertRecord> getAlertRecordList(String status, Integer limit);

    /**
     * 处理告警记录
     *
     * @param recordId 记录ID
     * @param handleBy 处理人
     * @param handleRemark 处理备注
     * @return 是否成功
     */
    boolean handleAlert(Long recordId, String handleBy, String handleRemark);
}
