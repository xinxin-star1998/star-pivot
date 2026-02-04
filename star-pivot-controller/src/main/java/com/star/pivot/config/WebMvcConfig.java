package com.star.pivot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 *
 * @author xinxin
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 注册自定义转换器
     * 1. 支持 @PathVariable Long[] 解析逗号分隔的 "1,2,3" 形式
     * 2. 支持 @RequestParam LocalDateTime 解析 "yyyy-MM-dd HH:mm:ss" 格式
     * 3. 支持 @RequestParam LocalDate 解析 "yyyy-MM-dd" 格式
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Converters.StringToLongArrayConverter());
        registry.addConverter(new Converters.StringToLocalDateTimeConverter());
        registry.addConverter(new Converters.StringToLocalDateConverter());
    }

    /**
     * 配置静态资源处理器
     * 确保 API 路径不会被当作静态资源处理
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 只处理明确的静态资源路径，避免拦截 API 请求
        // 这里不配置任何静态资源路径，让 Spring Boot 使用默认配置
        // API 路径（如 /api/**）应该由 Controller 处理，不会被静态资源处理器拦截
    }
}

