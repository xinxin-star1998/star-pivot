package com.star.pivot.monitor.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 监控模块配置属性
 *
 * @author xinxin
 * @since 2026-03-04
 */
@Data
@Component
@ConfigurationProperties(prefix = "star-pivot.monitor")
public class MonitorProperties {

    /**
     * 是否启用API性能监控
     */
    private Boolean apiPerformanceEnabled = true;

    /**
     * 采样率：只记录指定比例的请求（高频接口）
     * 范围：0.0 - 1.0，默认0.1（10%）
     */
    private Double sampleRate = 0.1;

    /**
     * 慢接口阈值：只记录响应时间超过此值的接口（毫秒）
     * 默认1000ms（1秒）
     */
    private Long slowApiThresholdMs = 1000L;

    /**
     * 批量写入队列大小
     * 默认1000
     */
    private Integer queueSize = 1000;

    /**
     * 批量写入批次大小
     * 默认100
     */
    private Integer batchSize = 100;

    /**
     * 批量写入间隔（秒）
     * 默认5秒
     */
    private Integer batchInterval = 5;
}
