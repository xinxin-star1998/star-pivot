package com.star.pivot.config;

import com.star.pivot.config.web.MDCFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Web过滤器配置类
 * 注册各种Web过滤器
 */
@Configuration
public class FilterConfig {

    /**
     * 注册MDC过滤器
     * 为每个请求生成唯一追踪ID，便于日志追踪
     */
    @Bean
    public FilterRegistrationBean<MDCFilter> mdcFilter() {
        FilterRegistrationBean<MDCFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new MDCFilter());
        registration.addUrlPatterns("/*");
        registration.setName("mdcFilter");
        registration.setOrder(1); // 设置优先级
        return registration;
    }
}