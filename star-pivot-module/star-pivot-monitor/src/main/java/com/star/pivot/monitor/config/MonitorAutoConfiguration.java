package com.star.pivot.monitor.config;

import com.star.pivot.monitor.config.properties.MonitorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * 监控模块自动配置类
 *
 * @author xinxin
 * @since 2026-03-04
 */
@Slf4j
@AutoConfiguration
@ComponentScan(basePackages = "com.star.pivot.monitor")
@EnableConfigurationProperties(MonitorProperties.class)
@ConditionalOnProperty(prefix = "star-pivot.monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MonitorAutoConfiguration {

    public MonitorAutoConfiguration() {
        log.info("StarPivot 监控模块自动配置已启用");
    }
}
