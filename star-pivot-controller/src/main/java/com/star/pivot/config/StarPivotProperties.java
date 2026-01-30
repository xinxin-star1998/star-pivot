package com.star.pivot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * StarPivot 统一配置属性
 *
 * <p>使用 @ConfigurationProperties 集中管理 JWT、缓存、监控等配置，
 * 便于维护和通过 IDE 补全。各环境具体值仍在 application-*.yml 中配置。
 *
 * <p>说明：
 * <ul>
 *   <li>jwt.* 等现有配置保留，本类可作为统一读取入口（可选迁移）</li>
 *   <li>cache、monitor 等新增配置项可在此定义默认值</li>
 * </ul>
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Data
@Component
@ConfigurationProperties(prefix = "star-pivot")
public class StarPivotProperties {

    /**
     * JWT 相关配置
     */
    private Jwt jwt = new Jwt();

    /**
     * 缓存相关配置
     */
    private Cache cache = new Cache();

    /**
     * 监控相关配置
     */
    private Monitor monitor = new Monitor();

    @Data
    public static class Jwt {
        /** 密钥（生产环境必须通过环境变量配置） */
        private String secret;
        /** 访问令牌过期时间（毫秒），默认 24 小时 */
        private Long expiration = 86400000L;
        /** 刷新令牌过期时间（毫秒），默认 7 天 */
        private Long refreshExpiration = 604800000L;
        /** 上一个密钥（密钥轮换过渡期使用，可选） */
        private String previousSecret;
    }

    @Data
    public static class Cache {
        /** 菜单树缓存过期时间（秒），默认 1 小时 */
        private Long menuTreeTtl = 3600L;
        /** 字典数据缓存过期时间（秒），默认 1 小时 */
        private Long dictDataTtl = 3600L;
        /** 用户权限缓存过期时间（秒），默认 30 分钟 */
        private Long userPermissionsTtl = 1800L;
    }

    @Data
    public static class Monitor {
        /** API 性能监控是否启用 */
        private Boolean apiPerformanceEnabled = true;
        /** 慢接口阈值（毫秒），超过此值才记录 */
        private Long slowApiThresholdMs = 1000L;
        /** 采样率（0~1），如 0.1 表示记录 10% 的慢接口请求 */
        private Double sampleRate = 0.1;
    }
}
