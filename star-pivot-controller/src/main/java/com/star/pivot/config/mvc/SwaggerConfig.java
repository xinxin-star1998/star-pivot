package com.star.pivot.config.mvc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("StarPivot 权限管理系统 API 文档")
                        .version("1.0.0")
                        .description("基于 Spring Boot 3.x 的权限管理系统，提供用户管理、角色管理、菜单管理、部门管理等核心功能。")
                        .contact(new Contact().name("StarPivot 开发团队").email("support@starpivot.com"))
                        .license(new License().name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:8080/api").description("开发环境"),
                        new Server().url("https://api.starpivot.com").description("生产环境")))
                .components(new Components()
                        .addSecuritySchemes("Bearer Token", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("请输入 JWT Token，格式：Bearer {token}")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Token"));
    }
}
