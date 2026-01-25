package com.star.pivot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 *
 * @author xinxin
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 注册自定义转换器，支持 @PathVariable Long[] 解析逗号分隔的 "1,2,3" 形式
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToLongArrayConverter());
    }
}

