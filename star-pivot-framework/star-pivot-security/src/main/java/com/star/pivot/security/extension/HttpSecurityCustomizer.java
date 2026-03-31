package com.star.pivot.security.extension;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * HttpSecurity 自定义扩展点
 * <p>
 * 其他模块如需追加过滤器、授权规则、会话策略等，可实现该接口并注册为 Spring Bean。
 */
public interface HttpSecurityCustomizer {

    /**
     * 自定义 HttpSecurity
     */
    void customize(HttpSecurity http) throws Exception;
}
