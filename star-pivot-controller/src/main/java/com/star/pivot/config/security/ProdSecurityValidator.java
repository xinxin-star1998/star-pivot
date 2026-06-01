package com.star.pivot.config.security;

import com.star.pivot.security.config.CorsProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * 生产环境安全项启动校验，防止 CORS 误配为通配或未配置。
 */
@Component
@Profile("prod")
@RequiredArgsConstructor
public class ProdSecurityValidator {

    private final CorsProperties corsProperties;

    @PostConstruct
    public void validate() {
        String allowedOrigins = corsProperties.getAllowedOrigins();
        if (!StringUtils.hasText(allowedOrigins)) {
            throw new IllegalStateException(
                    "生产环境必须配置 CORS_ALLOWED_ORIGINS（cors.allowed-origins），指定前端访问域名");
        }
        for (String origin : allowedOrigins.split(",")) {
            String trimmed = origin.trim();
            if ("*".equals(trimmed)) {
                throw new IllegalStateException(
                        "生产环境 cors.allowed-origins 不可使用 *，请配置明确的前端域名");
            }
            if (!StringUtils.hasText(trimmed)) {
                throw new IllegalStateException(
                        "生产环境 cors.allowed-origins 包含空项，请检查 CORS_ALLOWED_ORIGINS");
            }
        }
    }
}
