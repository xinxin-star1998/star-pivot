package com.star.pivot.config.web;

import com.star.pivot.framework.utils.StructuredLogUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

/**
 * MDC过滤器
 * 为每个请求生成唯一追踪ID，便于日志追踪
 */
@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "mdcFilter")
public class MDCFilter implements Filter {

    private static final String TRACE_ID = "traceId";
    private static final String REQUEST_ID = "requestId";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化过滤器
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 生成追踪ID
        String traceId = UUID.randomUUID().toString().replace("-", "");
        String requestId = generateRequestId(((HttpServletRequest) request).getRequestURI(),
                                           ((HttpServletRequest) request).getMethod());

        // 将ID放入MDC上下文
        MDC.put(TRACE_ID, traceId);
        MDC.put(REQUEST_ID, requestId);

        try {
            // 执行后续过滤器和请求处理
            chain.doFilter(request, response);
        } finally {
            // 清理MDC上下文，防止内存泄漏
            MDC.clear();
        }
    }

    @Override
    public void destroy() {
        // 销毁过滤器
    }

    /**
     * 生成请求ID
     * @param uri 请求URI
     * @param method 请求方法
     * @return 请求ID
     */
    private String generateRequestId(String uri, String method) {
        return method + "_" + uri.replaceAll("[^a-zA-Z0-9]", "_") + "_" +
               System.currentTimeMillis();
    }
}