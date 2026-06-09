package com.star.pivot.security.service;

import com.star.pivot.security.token.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 密码更新检查服务
 * 用于验证JWT令牌是否在密码更新之前签发
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordUpdateCheckService {

    private final PasswordUpdateDateProvider passwordUpdateDateProvider;
    private final JwtUtil jwtUtil;

    /**
     * 检查JWT是否在密码更新之前签发
     *
     * @param token JWT令牌
     * @param username 用户名
     * @return 如果JWT在密码更新之后签发或没有密码更新时间，则返回true；如果JWT在密码更新之前签发，则返回false
     */
    public boolean isTokenValidAfterPasswordUpdate(String token, String username) {
        try {
            // 获取JWT的签发时间
            Date tokenIssueDate = jwtUtil.getIssueDateFromToken(token);
            if (tokenIssueDate == null) {
                log.warn("无法从Token中获取签发时间: {}", username);
                return false;
            }

            // 通过抽象接口获取密码更新时间（由业务模块实现）
            LocalDateTime pwdUpdateDate = passwordUpdateDateProvider.getPasswordUpdateDate(username);

            if (pwdUpdateDate == null) {
                // 如果没有密码更新时间（用户不存在或从未修改过密码），认为令牌有效
                return true;
            }

            // 将Date转换为LocalDateTime进行比较
            LocalDateTime tokenIssueDateTime = tokenIssueDate.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();

            // 检查JWT是否在密码更新之后签发
            boolean isValid = !tokenIssueDateTime.isBefore(pwdUpdateDate);
            if (!isValid) {
                log.info("JWT在密码更新之前签发，令牌无效: user={}, tokenIssueTime={}, pwdUpdateTime={}",
                        username, tokenIssueDateTime, pwdUpdateDate);
            }

            return isValid;
        } catch (Exception e) {
            log.error("验证令牌时发生异常: {}", username, e);
            return false;
        }
    }

    /**
     * 从JWT中获取签发时间
     *
     * @param token JWT令牌
     * @return 签发时间
     */
    public Date getIssueDateFromToken(String token) {
        return jwtUtil.getIssueDateFromToken(token);
    }
}