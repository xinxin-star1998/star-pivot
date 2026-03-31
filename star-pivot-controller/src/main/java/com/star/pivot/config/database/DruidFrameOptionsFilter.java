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

    private static final String DRUID_PATH = "druid";
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
        String requestPath = request.getRequestURI();
        if (requestPath != null && containsDruid(requestPath)) {
            return true;
        }

        String contextPath = request.getContextPath();
        String fullPath = buildFullPath(contextPath, requestPath);
        if (fullPath != null && containsDruid(fullPath)) {
            return true;
        }

        String referer = request.getHeader("Referer");
        return referer != null && containsDruid(referer);
    }

    private String buildFullPath(String contextPath, String requestPath) {
        if (contextPath == null || contextPath.isEmpty()) {
            return requestPath;
        }
        return contextPath + requestPath;
    }

    private boolean containsDruid(String str) {
        return str != null && str.toLowerCase().contains(DRUID_PATH);
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
