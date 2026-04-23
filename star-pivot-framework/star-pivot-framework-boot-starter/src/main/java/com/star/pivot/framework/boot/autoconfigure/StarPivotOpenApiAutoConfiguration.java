package com.star.pivot.framework.boot.autoconfigure;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * SpringDoc OpenAPI 通用配置。
 *
 * <p>默认开启（由 springdoc.* 控制），可通过 star-pivot.openapi.enabled=false 关闭该默认 Bean。
 */
@AutoConfiguration
@ConditionalOnClass(OpenAPI.class)
@ConditionalOnProperty(prefix = "star-pivot.openapi", name = "enabled", havingValue = "true", matchIfMissing = true)
public class StarPivotOpenApiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(OpenAPI.class)
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("StarPivot 权限管理系统 API 文档")
                        .version("1.0.0")
                        .description("基于 Spring Boot 3.x 的权限管理系统，提供用户管理、角色管理、菜单管理、部门管理等核心功能。")
                        .contact(new Contact().name("StarPivot 开发团队").email("support@starpivot.com")))
                .components(new Components()
                        .addSecuritySchemes("Bearer Token", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("请输入 JWT Token，格式：Bearer {token}")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Token"));
    }
}

