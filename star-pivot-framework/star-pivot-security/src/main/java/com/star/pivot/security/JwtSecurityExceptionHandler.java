package com.star.pivot.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.pivot.framework.domain.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT 安全异常统一处理：未认证与权限不足均返回 JSON 格式 Result
 * <p>同时实现 AuthenticationEntryPoint（未登录）与 AccessDeniedHandler（无权限），减少重复代码
 */
@Slf4j
@Component
public class JwtSecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException{
        log.error("未认证访问: {}", authException.getMessage());
        writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, Result.error(401, "未认证，请先登录"));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.error("权限不足: {}", accessDeniedException.getMessage());
        writeJson(response, HttpServletResponse.SC_FORBIDDEN, Result.error(403, "权限不足，拒绝访问"));
    }

    private void writeJson(HttpServletResponse response, int status, Result<Void> result) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status);
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(result));
    }
}
