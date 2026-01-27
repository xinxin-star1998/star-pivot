package com.star.pivot.config;

import com.star.pivot.security.HttpSecurityCustomizer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
                                // 对于 Druid 路径，完全移除 X-Frame-Options，允许在 iframe 中加载
                                // 注意：不设置 X-Frame-Options 等同于允许在 iframe 中加载
                                // 由于前端通过 Vite 代理访问，浏览器认为它们是同源的
                                // 这里不设置响应头，让过滤器来处理
                            } else {
                                // 其他路径保持 DENY，不允许在 iframe 中加载（安全考虑）
                                response.setHeader("X-Frame-Options", "DENY");
                            }
                        }
                    })
            );
            
            // 添加过滤器，确保在所有响应（包括重定向）中设置正确的 X-Frame-Options
            // 使用 HttpServletResponseWrapper 包装响应，拦截所有响应头设置
            http.addFilterBefore(new OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                                FilterChain filterChain) throws ServletException, IOException {
                    String requestPath = request.getRequestURI();
                    String contextPath = request.getContextPath();
                    String referer = request.getHeader("Referer");
                    
                    // 构建完整路径
                    String fullPath = (contextPath != null && !contextPath.isEmpty()) 
                        ? contextPath + requestPath 
                        : requestPath;
                    
                    // 检查是否为 Druid 监控路径（更宽松的匹配，确保能匹配所有情况）
                    // 1. 检查请求路径
                    boolean isDruidPath = false;
                    if (requestPath != null) {
                        String lowerPath = requestPath.toLowerCase();
                        isDruidPath = lowerPath.contains("druid");
                    }
                    // 2. 检查完整路径
                    if (!isDruidPath && fullPath != null) {
                        String lowerFullPath = fullPath.toLowerCase();
                        isDruidPath = lowerFullPath.contains("druid");
                    }
                    // 3. 检查 Referer 头（处理重定向情况）
                    if (!isDruidPath && referer != null) {
                        String lowerReferer = referer.toLowerCase();
                        isDruidPath = lowerReferer.contains("druid");
                    }
                    
                    // 如果是 Druid 路径，包装响应以确保响应头正确设置
                    if (isDruidPath) {
                        HttpServletResponse wrappedResponse = new jakarta.servlet.http.HttpServletResponseWrapper(response) {
                            @Override
                            public void setHeader(String name, String value) {
                                // 如果设置 X-Frame-Options，完全移除它（允许在 iframe 中加载）
                                if ("X-Frame-Options".equalsIgnoreCase(name)) {
                                    // 不设置 X-Frame-Options，允许在 iframe 中加载
                                    return;
                                } else {
                                    super.setHeader(name, value);
                                }
                            }
                            
                            @Override
                            public void addHeader(String name, String value) {
                                // 如果添加 X-Frame-Options，完全移除它（允许在 iframe 中加载）
                                if ("X-Frame-Options".equalsIgnoreCase(name)) {
                                    // 不添加 X-Frame-Options，允许在 iframe 中加载
                                    return;
                                } else {
                                    super.addHeader(name, value);
                                }
                            }
                            
                            @Override
                            public boolean containsHeader(String name) {
                                // 如果查询 X-Frame-Options，返回 false（表示不存在）
                                if ("X-Frame-Options".equalsIgnoreCase(name)) {
                                    return false;
                                }
                                return super.containsHeader(name);
                            }
                        };
                        
                        // 继续过滤器链，使用包装后的响应
                        filterChain.doFilter(request, wrappedResponse);
                    } else {
                        // 非 Druid 路径，正常处理
                        filterChain.doFilter(request, response);
                    }
                }
            }, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        };
    }
}
