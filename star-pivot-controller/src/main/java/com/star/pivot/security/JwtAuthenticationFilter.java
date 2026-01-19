package com.star.pivot.security;

import com.star.pivot.system.utils.JwtBlackListManager;
import com.star.pivot.system.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 基于 JWT 的认证过滤器
 * <p>
 * 注意：该类位于 controller 模块中，只负责 HTTP 层的认证拦截，
 * 具体的 JWT 解析、黑名单管理等能力由 system 模块提供。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final JwtBlackListManager jwtBlackListManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.debug("处理请求: {} {}", request.getMethod(), requestURI);

        // 获取 Authorization 请求头
        String authHeader = request.getHeader("Authorization");
        log.debug("Authorization Header: {}", authHeader != null ? "Bearer ***" : "未提供");

        String token = getTokenFromRequest(request);

        if (token != null) {
            log.debug("成功提取Token: {}...", token.substring(0, Math.min(20, token.length())));

            // 检查令牌是否在黑名单中
            if (jwtBlackListManager.isBlackListed(token)) {
                log.info("Token在黑名单中，拒绝访问: {}", token.substring(0, Math.min(20, token.length())));
                // 继续执行请求，但不设置认证信息
                filterChain.doFilter(request, response);
                return;
            }

            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                log.info("Token验证成功，用户: {}", username);

                // 仅在上下文中尚未存在认证信息时才进行设置，避免覆盖其他认证
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("用户 {} 认证成功，权限: {}", username, userDetails.getAuthorities());
                } else {
                    log.debug("SecurityContext 已包含认证信息，跳过重复认证");
                }
            } else {
                log.warn("Token验证失败或已过期");
            }
        } else {
            log.debug("请求未携带Token");
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

