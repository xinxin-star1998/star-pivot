package com.star.pivot.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.pivot.framework.demo.DemoModeUtils;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.security.LoginUserInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 演示模式过滤器：演示账号仅允许只读请求，写操作统一拒绝。
 */
@Slf4j
@Component
public class DemoModeFilter extends OncePerRequestFilter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (isDemoUser() && isWriteRequest(request) && !DemoModeUtils.isReadOnlyHttpRequest(
                request.getMethod(), request.getRequestURI(), request.getContextPath())) {
            log.info("演示模式拒绝写操作: {} {}", request.getMethod(), request.getRequestURI());
            writeDenied(response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isDemoUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Object principal = authentication.getPrincipal();
        return principal instanceof LoginUserInfo loginUser && loginUser.isDemoMode();
    }

    private boolean isWriteRequest(HttpServletRequest request) {
        String method = request.getMethod();
        if (method == null) {
            return true;
        }
        return switch (method.toUpperCase()) {
            case "POST", "PUT", "DELETE", "PATCH" -> true;
            default -> false;
        };
    }

    private void writeDenied(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        Result<Void> result = Result.error(
                ErrorCode.DEMO_MODE_DENIED.getCode(),
                ErrorCode.DEMO_MODE_DENIED.getMessage()
        );
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(result));
    }
}
