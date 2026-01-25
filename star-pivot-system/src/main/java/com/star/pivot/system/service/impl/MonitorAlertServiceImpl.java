package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.star.pivot.system.domain.entity.SysMonitorAlertRecord;
import com.star.pivot.system.domain.entity.SysMonitorAlertRule;
import com.star.pivot.system.mapper.SysMonitorAlertRecordMapper;
import com.star.pivot.system.mapper.SysMonitorAlertRuleMapper;
import com.star.pivot.system.mapper.SysMonitorHistoryMapper;
import com.star.pivot.system.service.MonitorAlertService;
import com.star.pivot.system.service.MonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 监控告警服务实现类
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorAlertServiceImpl implements MonitorAlertService {

    private final SysMonitorAlertRuleMapper alertRuleMapper;
    private final SysMonitorAlertRecordMapper alertRecordMapper;
    private final SysMonitorHistoryMapper monitorHistoryMapper;
    private final MonitorService monitorService;

    @Override
    public void checkAndTriggerAlerts() {
        try {
            // 查询所有启用的告警规则
            List<SysMonitorAlertRule> rules = alertRuleMapper.selectList(
                    new LambdaQueryWrapper<SysMonitorAlertRule>()
                            .eq(SysMonitorAlertRule::getEnabled, "1")
            );

            for (SysMonitorAlertRule rule : rules) {
                try {
                    checkRuleAndTrigger(rule);
                } catch (Exception e) {
                    log.warn("检查告警规则失败，ruleId: {}", rule.getRuleId(), e);
                }
            }
        } catch (Exception e) {
            log.error("检查告警规则失败", e);
        }
    }

    /**
     * 检查单个规则并触发告警
     */
    private void checkRuleAndTrigger(SysMonitorAlertRule rule) {
        // 获取最新的指标值（从最近一次采集的数据中获取）
        BigDecimal currentValue = getLatestMetricValue(rule.getMetricType(), rule.getMetricName());
        if (currentValue == null) {
            return;
        }

        // 判断是否触发告警
        boolean shouldAlert = false;
        String thresholdType = rule.getThresholdType();
        BigDecimal thresholdValue = rule.getThresholdValue();

        switch (thresholdType) {
            case "0": // 大于
                shouldAlert = currentValue.compareTo(thresholdValue) > 0;
                break;
            case "1": // 小于
                shouldAlert = currentValue.compareTo(thresholdValue) < 0;
                break;
            case "2": // 等于
                shouldAlert = currentValue.compareTo(thresholdValue) == 0;
                break;
        }

        if (shouldAlert) {
            // 检查是否已有未处理的相同告警（避免重复告警）
            long unhandledCount = alertRecordMapper.selectCount(
                    new LambdaQueryWrapper<SysMonitorAlertRecord>()
                            .eq(SysMonitorAlertRecord::getRuleId, rule.getRuleId())
                            .eq(SysMonitorAlertRecord::getAlertStatus, "0")
                            .ge(SysMonitorAlertRecord::getAlertTime, LocalDateTime.now().minusHours(1))
            );

            // 如果1小时内已有未处理的告警，则不再创建新告警
            if (unhandledCount > 0) {
                return;
            }

            // 创建告警记录
            createAlertRecord(rule, currentValue);
        }
    }

    /**
     * 获取最新的指标值
     */
    private BigDecimal getLatestMetricValue(String metricType, String metricName) {
        try {
            List<com.star.pivot.system.domain.entity.SysMonitorHistory> historyList = 
                    monitorHistoryMapper.selectList(
                            new LambdaQueryWrapper<com.star.pivot.system.domain.entity.SysMonitorHistory>()
                                    .eq(com.star.pivot.system.domain.entity.SysMonitorHistory::getMetricType, metricType)
                                    .eq(com.star.pivot.system.domain.entity.SysMonitorHistory::getMetricName, metricName)
                                    .orderByDesc(com.star.pivot.system.domain.entity.SysMonitorHistory::getCollectTime)
                                    .last("LIMIT 1")
                    );

            if (!historyList.isEmpty()) {
                return historyList.get(0).getMetricValue();
            }
        } catch (Exception e) {
            log.warn("获取最新指标值失败: {} {}", metricType, metricName, e);
        }
        return null;
    }

    /**
     * 创建告警记录并发送告警通知
     */
    @Transactional(rollbackFor = Exception.class)
    public void createAlertRecord(SysMonitorAlertRule rule, BigDecimal currentValue) {
        SysMonitorAlertRecord record = new SysMonitorAlertRecord();
        record.setRuleId(rule.getRuleId());
        record.setRuleName(rule.getRuleName());
        record.setMetricType(rule.getMetricType());
        record.setMetricName(rule.getMetricName());
        record.setCurrentValue(currentValue);
        record.setThresholdValue(rule.getThresholdValue());
        record.setAlertLevel(rule.getAlertLevel());
        record.setAlertStatus("0"); // 未处理
        record.setAlertChannels(rule.getAlertChannels());
        record.setAlertTime(LocalDateTime.now());

        // 构建告警内容
        String alertContent = String.format("告警：%s 当前值 %s，超过阈值 %s",
                rule.getMetricName(), currentValue, rule.getThresholdValue());
        record.setAlertContent(alertContent);

        alertRecordMapper.insert(record);

        // 发送告警通知
        sendAlertNotification(record, rule);
    }

    /**
     * 发送告警通知
     */
    private void sendAlertNotification(SysMonitorAlertRecord record, SysMonitorAlertRule rule) {
        String channels = rule.getAlertChannels();
        if (channels == null || channels.isEmpty()) {
            return;
        }

        String[] channelArray = channels.split(",");
        for (String channel : channelArray) {
            try {
                switch (channel.trim()) {
                    case "in_app":
                        // 站内消息（可以集成到消息中心）
                        log.info("发送站内消息告警: {}", record.getAlertContent());
                        break;
                    case "email":
                        // 邮件告警（需要配置邮件服务）
                        log.info("发送邮件告警: {}", record.getAlertContent());
                        // TODO: 实现邮件发送
                        break;
                    case "sms":
                        // 短信告警（需要配置短信服务）
                        log.info("发送短信告警: {}", record.getAlertContent());
                        // TODO: 实现短信发送
                        break;
                    case "webhook":
                        // Webhook告警
                        if (rule.getWebhookUrl() != null && !rule.getWebhookUrl().isEmpty()) {
                            sendWebhookAlert(rule.getWebhookUrl(), record);
                        }
                        break;
                }
            } catch (Exception e) {
                log.warn("发送告警通知失败，渠道: {}", channel, e);
            }
        }
    }

    /**
     * 发送Webhook告警
     */
    private void sendWebhookAlert(String webhookUrl, SysMonitorAlertRecord record) {
        try {
            // 使用HTTP客户端发送Webhook请求
            // TODO: 实现Webhook发送（可以使用RestTemplate或HttpClient）
            log.info("发送Webhook告警到: {}, 内容: {}", webhookUrl, record.getAlertContent());
        } catch (Exception e) {
            log.warn("发送Webhook告警失败", e);
        }
    }

    @Override
    public boolean createAlertRule(SysMonitorAlertRule rule) {
        try {
            rule.setCreateTime(LocalDateTime.now());
            return alertRuleMapper.insert(rule) > 0;
        } catch (Exception e) {
            log.error("创建告警规则失败", e);
            return false;
        }
    }

    @Override
    public boolean updateAlertRule(SysMonitorAlertRule rule) {
        try {
            rule.setUpdateTime(LocalDateTime.now());
            return alertRuleMapper.updateById(rule) > 0;
        } catch (Exception e) {
            log.error("更新告警规则失败", e);
            return false;
        }
    }

    @Override
    public boolean deleteAlertRule(Long ruleId) {
        try {
            return alertRuleMapper.deleteById(ruleId) > 0;
        } catch (Exception e) {
            log.error("删除告警规则失败", e);
            return false;
        }
    }

    @Override
    public List<SysMonitorAlertRule> getAlertRuleList() {
        return alertRuleMapper.selectList(null);
    }

    @Override
    public List<SysMonitorAlertRecord> getAlertRecordList(String status, Integer limit) {
        LambdaQueryWrapper<SysMonitorAlertRecord> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(SysMonitorAlertRecord::getAlertStatus, status);
        }
        wrapper.orderByDesc(SysMonitorAlertRecord::getAlertTime);
        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        return alertRecordMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAlert(Long recordId, String handleBy, String handleRemark) {
        try {
            SysMonitorAlertRecord record = alertRecordMapper.selectById(recordId);
            if (record == null) {
                return false;
            }
            record.setAlertStatus("1"); // 已处理
            record.setHandleTime(LocalDateTime.now());
            record.setHandleBy(handleBy);
            record.setHandleRemark(handleRemark);
            return alertRecordMapper.updateById(record) > 0;
        } catch (Exception e) {
            log.error("处理告警记录失败", e);
            return false;
        }
    }
}
