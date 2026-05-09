package com.star.pivot.config.database;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Druid 监控页面 Frame Options 过滤器
 *
 * <p>允许 Druid 监控页面在 iframe 中加载，解决 X-Frame-Options 限制问题
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DruidFrameOptionsFilter extends OncePerRequestFilter {

    private static final String DRUID_PATH_SEGMENT = "/druid/";
    private static final String X_FRAME_OPTIONS = "X-Frame-Options";
    private static final String DENY = "DENY";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (isDruidPath(request)) {
            DruidFrameOptionsResponseWrapper wrappedResponse =
                    new DruidFrameOptionsResponseWrapper(response);
            filterChain.doFilter(request, wrappedResponse);
        } else {
            response.setHeader(X_FRAME_OPTIONS, DENY);
            filterChain.doFilter(request, response);
        }
    }

    private boolean isDruidPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri == null) {
            return false;
        }

        // 只对真正的 druid 监控页面放开 iframe，避免“包含字符串”误判
        // 兼容：/druid/** 与 /api/druid/**（context-path=/api）
        if (uri.startsWith("/druid/") || uri.startsWith("/api/druid/")) {
            return true;
        }

        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath + DRUID_PATH_SEGMENT)) {
            return true;
        }

        return false;
    }

    private static class DruidFrameOptionsResponseWrapper extends HttpServletResponseWrapper {

        public DruidFrameOptionsResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setHeader(String name, String value) {
            if (X_FRAME_OPTIONS.equalsIgnoreCase(name)) {
                return;
            }
            super.setHeader(name, value);
        }

        @Override
        public void addHeader(String name, String value) {
            if (X_FRAME_OPTIONS.equalsIgnoreCase(name)) {
                return;
            }
            super.addHeader(name, value);
        }

        @Override
        public boolean containsHeader(String name) {
            if (X_FRAME_OPTIONS.equalsIgnoreCase(name)) {
                return false;
            }
            return super.containsHeader(name);
        }
    }
}
