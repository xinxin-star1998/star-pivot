package com.star.pivot.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Druid 监控页面 Frame Options 过滤器
 *
 * <p>允许 Druid 监控页面在 iframe 中加载，解决 X-Frame-Options 限制问题
 *
 * <p>职责：
 * <ul>
 *   <li>检查请求是否为 Druid 监控路径</li>
 *   <li>对于 Druid 路径，移除 X-Frame-Options 响应头</li>
 *   <li>对于其他路径，保持 X-Frame-Options: DENY</li>
 * </ul>
 *
 * @author xinxin
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DruidFrameOptionsFilter extends OncePerRequestFilter {

    /**
     * Druid 监控路径标识
     */
    private static final String DRUID_PATH = "druid";

    /**
     * X-Frame-Options 响应头名称
     */
    private static final String X_FRAME_OPTIONS = "X-Frame-Options";

    /**
     * 禁用 iframe 加载的值
     */
    private static final String DENY = "DENY";

    /**
     * 过滤器处理逻辑
     *
     * @param request HTTP 请求
     * @param response HTTP 响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException IO 异常
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (isDruidPath(request)) {
            // Druid 路径：包装响应，移除 X-Frame-Options 头
            DruidFrameOptionsResponseWrapper wrappedResponse =
                    new DruidFrameOptionsResponseWrapper(response);
            filterChain.doFilter(request, wrappedResponse);
        } else {
            // 其他路径：设置 X-Frame-Options: DENY，禁止在 iframe 中加载
            response.setHeader(X_FRAME_OPTIONS, DENY);
            filterChain.doFilter(request, response);
        }
    }

    /**
     * 判断请求是否为 Druid 监控路径
     *
     * <p>检查多个维度：
     * <ul>
     *   <li>请求路径（requestPath）</li>
     *   <li>完整路径（包含 context-path）</li>
     *   <li>Referer 头（处理重定向情况）</li>
     * </ul>
     *
     * @param request HTTP 请求
     * @return 是否为 Druid 路径
     */
    private boolean isDruidPath(HttpServletRequest request) {
        // 1. 检查请求路径
        String requestPath = request.getRequestURI();
        if (requestPath != null && containsDruid(requestPath)) {
            return true;
        }

        // 2. 检查完整路径（包含 context-path）
        String contextPath = request.getContextPath();
        String fullPath = buildFullPath(contextPath, requestPath);
        if (fullPath != null && containsDruid(fullPath)) {
            return true;
        }

        // 3. 检查 Referer 头（处理重定向情况）
        String referer = request.getHeader("Referer");
        if (referer != null && containsDruid(referer)) {
            return true;
        }

        return false;
    }

    /**
     * 构建完整路径
     *
     * @param contextPath 上下文路径
     * @param requestPath 请求路径
     * @return 完整路径
     */
    private String buildFullPath(String contextPath, String requestPath) {
        if (contextPath == null || contextPath.isEmpty()) {
            return requestPath;
        }
        return contextPath + requestPath;
    }

    /**
     * 检查字符串是否包含 Druid 路径标识（不区分大小写）
     *
     * @param str 要检查的字符串
     * @return 是否包含 Druid 路径标识
     */
    private boolean containsDruid(String str) {
        if (str == null) {
            return false;
        }
        return str.toLowerCase().contains(DRUID_PATH);
    }

    /**
     * Druid Frame Options 响应包装器
     *
     * <p>重写响应头设置方法，移除 X-Frame-Options 头
     */
    private static class DruidFrameOptionsResponseWrapper extends HttpServletResponseWrapper {

        /**
         * 构造方法
         *
         * @param response 原始响应
         */
        public DruidFrameOptionsResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        /**
         * 重写 setHeader 方法，拦截 X-Frame-Options 设置
         *
         * @param name 响应头名称
         * @param value 响应头值
         */
        @Override
        public void setHeader(String name, String value) {
            // 如果设置 X-Frame-Options，不设置（允许在 iframe 中加载）
            if (X_FRAME_OPTIONS.equalsIgnoreCase(name)) {
                return;
            }
            super.setHeader(name, value);
        }

        /**
         * 重写 addHeader 方法，拦截 X-Frame-Options 添加
         *
         * @param name 响应头名称
         * @param value 响应头值
         */
        @Override
        public void addHeader(String name, String value) {
            // 如果添加 X-Frame-Options，不添加（允许在 iframe 中加载）
            if (X_FRAME_OPTIONS.equalsIgnoreCase(name)) {
                return;
            }
            super.addHeader(name, value);
        }

        /**
         * 重写 containsHeader 方法，返回 X-Frame-Options 不存在
         *
         * @param name 响应头名称
         * @return 是否包含响应头
         */
        @Override
        public boolean containsHeader(String name) {
            // 如果查询 X-Frame-Options，返回 false（表示不存在）
            if (X_FRAME_OPTIONS.equalsIgnoreCase(name)) {
                return false;
            }
            return super.containsHeader(name);
        }
    }
}
