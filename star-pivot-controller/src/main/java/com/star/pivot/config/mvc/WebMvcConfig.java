package com.star.pivot.config.mvc;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Converters.StringToLongArrayConverter());
        registry.addConverter(new Converters.StringToLocalDateTimeConverter());
        registry.addConverter(new Converters.StringToLocalDateConverter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // use Spring Boot default static resource handling
    }
}
