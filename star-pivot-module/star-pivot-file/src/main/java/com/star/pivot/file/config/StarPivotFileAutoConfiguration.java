package com.star.pivot.file.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 文件中心模块自动配置：注册 Service、Mapper 等 Bean。
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.star.pivot.file")
@MapperScan(basePackages = "com.star.pivot.file.mapper")
public class StarPivotFileAutoConfiguration {
}
