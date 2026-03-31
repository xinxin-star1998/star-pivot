package com.star.pivot.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 跨域相关配置
 */
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    /**
     * 允许的域名，多个用逗号分隔；配置为 * 表示允许所有域名
     */
    private String allowedOrigins = "*";

    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
}
