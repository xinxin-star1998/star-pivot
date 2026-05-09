package com.star.pivot.framework.boot.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 通用配置（converter 等），下沉到 starter，入口层无需再维护。
 */
@AutoConfiguration
public class StarPivotWebMvcAutoConfiguration implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StarPivotConverters.StringToLongArrayConverter());
        registry.addConverter(new StarPivotConverters.StringToLocalDateTimeConverter());
        registry.addConverter(new StarPivotConverters.StringToLocalDateConverter());
    }
}

