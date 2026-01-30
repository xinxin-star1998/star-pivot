package com.star.pivot.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * StarPivot 应用健康检查指示器
 *
 * <p>为 Actuator /actuator/health 提供自定义 "starPivot" 健康组件。
 * 用于标识应用自身状态，便于监控与 K8s 探针区分。
 *
 * <p>说明：
 * <ul>
 *   <li>数据库、Redis 等由 Spring Boot 自带的 DataSource、Redis 健康指示器提供</li>
 *   <li>本组件仅作为应用级补充，当前恒为 UP</li>
 * </ul>
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Component
public class StarPivotHealthIndicator implements HealthIndicator {

    private static final String APP_NAME = "StarPivot";

    @Override
    public Health health() {
        return Health.up()
                .withDetail("application", APP_NAME)
                .withDetail("status", "running")
                .build();
    }
}
