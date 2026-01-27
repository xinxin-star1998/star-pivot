package com.star.pivot.config;

import com.star.pivot.security.HttpSecurityCustomizer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.header.HeaderWriter;

/**
 * Druid 监控页面 Frame Options 配置
 * <p>
 * 允许 Druid 监控页面在 iframe 中加载，解决 X-Frame-Options 限制问题
 */
@Configuration
public class DruidFrameOptionsConfig {

    /**
     * 配置 Druid 监控页面的 Frame Options
     * <p>
     * 允许 /druid/* 路径在 iframe 中加载
     * <p>
     * 注意：由于前端通过 Vite 代理访问后端，浏览器认为它们是同源的（都是 localhost:3000），
     * 所以使用 SAMEORIGIN 即可。如果前后端部署在不同域名，可能需要使用 ALLOW-FROM 或完全禁用。
     */
    @Bean
    public HttpSecurityCustomizer druidFrameOptionsCustomizer() {
        return http -> {
            http.headers(headers -> headers
                    // 禁用默认的 X-Frame-Options（默认是 DENY）
                    .frameOptions(frameOptions -> frameOptions.disable())
                    // 添加自定义 HeaderWriter，根据路径设置不同的 X-Frame-Options
                    .addHeaderWriter(new HeaderWriter() {
                        @Override
                        public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
                            String requestPath = request.getRequestURI();
                            String contextPath = request.getContextPath();
                            
                            // 构建完整路径（包含 context-path）
                            // context-path 为 /api，所以完整路径是 /api/druid/*
                            String fullPath = (contextPath != null && !contextPath.isEmpty()) 
                                ? contextPath + requestPath 
                                : requestPath;
                            
                            // 检查是否为 Druid 监控路径
                            // 需要检查多种情况：
                            // 1. requestPath 包含 /druid（Spring Security 内部路径，可能不包含 context-path）
                            // 2. fullPath 包含 /api/druid（完整路径）
                            // 3. 处理重定向情况（Druid 可能会重定向到其他路径）
                            boolean isDruidPath = false;
                            if (requestPath != null) {
                                // 检查 requestPath 是否包含 druid
                                isDruidPath = requestPath.contains("/druid") 
                                    || requestPath.startsWith("/druid")
                                    || requestPath.equals("/druid");
                            }
                            if (!isDruidPath && fullPath != null) {
                                // 检查完整路径是否包含 druid
                                isDruidPath = fullPath.contains("/druid") 
                                    || fullPath.contains("/api/druid");
                            }
                            
                            if (isDruidPath) {
                                // 为 Druid 路径设置 SAMEORIGIN，允许在相同源的 iframe 中加载
                                // 由于前端通过 Vite 代理访问，浏览器认为它们是同源的
                                response.setHeader("X-Frame-Options", "SAMEORIGIN");
                            } else {
                                // 其他路径保持 DENY，不允许在 iframe 中加载（安全考虑）
                                response.setHeader("X-Frame-Options", "DENY");
                            }
                        }
                    })
            );
        };
    }
}
