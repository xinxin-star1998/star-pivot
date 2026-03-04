package com.star.pivot.system.service.impl;

import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.ServiceException;
import com.star.pivot.framework.utils.LogUtils;
import com.star.pivot.security.JwtBlackListManager;
import com.star.pivot.security.JwtUtil;
import com.star.pivot.security.RefreshTokenManager;
import com.star.pivot.security.RefreshTokenManager.RefreshTokenValue;
import com.star.pivot.system.domain.bo.LoginResponse;
import com.star.pivot.system.domain.bo.OnlineUserVO;
import com.star.pivot.system.domain.bo.TokenPair;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.service.OnlineUserService;
import com.star.pivot.system.service.SysDeptService;
import com.star.pivot.system.service.SysUserService;
import com.star.pivot.system.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Token / 会话 统一门面服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenManager refreshTokenManager;
    private final JwtBlackListManager jwtBlackListManager;
    private final SysUserService sysUserService;
    private final SysDeptService sysDeptService;
    private final OnlineUserService onlineUserService;

    @Override
    public TokenPair createTokenPair(SysUser user,
                                     String username,
                                     String ipaddr,
                                     String browser,
                                     String os,
                                     String loginLocation) {
        if (user == null || user.getUserId() == null) {
            throw new ServiceException(ErrorCode.USER_NOT_FOUND, "用户不存在，无法生成令牌");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("userId", user.getUserId());
        String accessToken = jwtUtil.generateToken(username, claims);

        String refreshToken = refreshTokenManager.generateAndStoreRefreshToken(
            user.getUserId(),
            ipaddr,
            browser,
            os,
            loginLocation
        );

        return new TokenPair(accessToken, refreshToken);
    }

    @Override
    public LoginResponse refreshToken(String username, String refreshToken) {
        if (username == null || username.trim().isEmpty()) {
            throw new ServiceException(ErrorCode.PARAM_NOT_NULL, "用户名不能为空");
        }
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new ServiceException(ErrorCode.PARAM_NOT_NULL, "刷新令牌不能为空");
        }

        SysUser user = sysUserService.getUserByUsername(username);
        if (user == null) {
            throw new ServiceException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        Long userId = user.getUserId();
        if (!refreshTokenManager.validateRefreshToken(userId, refreshToken)) {
            throw new ServiceException(ErrorCode.UNAUTHORIZED, "刷新令牌无效或已过期，请重新登录");
        }

        RefreshTokenValue oldTokenValue = refreshTokenManager.getRefreshTokenValue(userId);
        refreshTokenManager.revokeRefreshToken(userId);

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("userId", userId);
        String newAccessToken = jwtUtil.generateToken(username, claims);

        String newRefreshToken;
        if (oldTokenValue != null) {
            newRefreshToken = refreshTokenManager.generateAndStoreRefreshToken(
                userId,
                oldTokenValue.getIpaddr(),
                oldTokenValue.getBrowser(),
                oldTokenValue.getOs(),
                oldTokenValue.getLoginLocation()
            );
        } else {
            newRefreshToken = refreshTokenManager.generateAndStoreRefreshToken(userId);
        }

        LoginResponse response = new LoginResponse();
        response.setToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setUsername(username);
        response.setNickname(user.getNickName());

        return response;
    }

    @Override
    public void logout(String token, String logoutType) {
        if (token == null || token.trim().isEmpty()) {
            return;
        }

        if (!jwtUtil.validateToken(token)) {
            log.debug("登出请求的token无效或已过期，跳过黑名单及历史记录处理");
            return;
        }

        try {
            long expirationTime = jwtUtil.getClaimsFromToken(token).getExpiration().getTime() - System.currentTimeMillis();
            if (expirationTime > 0) {
                jwtBlackListManager.addToBlackList(token, expirationTime);

                String username = null;
                Long userId = null;
                try {
                    username = jwtUtil.getUsernameFromToken(token);
                    userId = jwtUtil.getUserIdFromToken(token);
                } catch (Exception e) {
                    log.debug("获取用户名失败: {}", LogUtils.truncateString(e.getMessage(), 200));
                }

                if (username != null) {
                    log.info("用户 {} 的令牌已加入黑名单", username);
                } else {
                    log.info("令牌已加入黑名单");
                }

                if (userId != null) {
                    saveOnlineUserHistory(userId, logoutType);
                    refreshTokenManager.revokeRefreshToken(userId);
                }
            } else {
                log.debug("Token已过期，无需加入黑名单");
            }
        } catch (Exception e) {
            log.warn("处理token黑名单时发生异常，但登出流程继续: {}", LogUtils.truncateString(e.getMessage(), 200));
        }
    }

    @Override
    public void forceLogout(Long userId, String logoutType) {
        if (userId == null) {
            return;
        }
        // 仅依赖 userId，即可完成刷新令牌吊销与在线历史记录，适用于监控强制下线等无访问令牌场景
        saveOnlineUserHistory(userId, logoutType);
        refreshTokenManager.revokeRefreshToken(userId);
    }

    /**
     * 保存在线用户历史记录
     */
    private void saveOnlineUserHistory(Long userId, String logoutType) {
        try {
            RefreshTokenValue tokenValue = refreshTokenManager.getRefreshTokenValue(userId);
            if (tokenValue == null) {
                log.debug("用户 {} 的刷新令牌不存在，跳过保存历史记录", userId);
                return;
            }

            SysUser user = sysUserService.getById(userId);
            if (user == null) {
                log.warn("用户 {} 不存在，跳过保存历史记录", userId);
                return;
            }

            OnlineUserVO onlineUser = new OnlineUserVO();
            onlineUser.setSessionId("jwt:refresh:user:" + userId);
            onlineUser.setUserId(userId);
            onlineUser.setUserName(user.getUserName());
            onlineUser.setNickName(user.getNickName());
            onlineUser.setIpaddr(tokenValue.getIpaddr());
            onlineUser.setBrowser(tokenValue.getBrowser());
            onlineUser.setOs(tokenValue.getOs());
            onlineUser.setLoginLocation(tokenValue.getLoginLocation());

            if (tokenValue.getIssuedAt() != null) {
                onlineUser.setLoginTime(LocalDateTime.ofInstant(
                    tokenValue.getIssuedAt().toInstant(),
                    java.time.ZoneId.systemDefault()
                ));
            }
            if (tokenValue.getLastAccessTime() != null) {
                onlineUser.setLastAccessTime(LocalDateTime.ofInstant(
                    tokenValue.getLastAccessTime().toInstant(),
                    java.time.ZoneId.systemDefault()
                ));
            }

            if (user.getDeptId() != null && sysDeptService != null) {
                try {
                    com.star.pivot.system.domain.entity.SysDept dept = sysDeptService.getById(user.getDeptId());
                    if (dept != null) {
                        onlineUser.setDeptName(dept.getDeptName());
                    }
                } catch (Exception e) {
                    log.debug("获取部门信息失败，deptId: {}", user.getDeptId());
                }
            }

            onlineUserService.saveOnlineUserHistory(onlineUser, logoutType);
        } catch (Exception e) {
            log.warn("保存在线用户历史记录失败，userId: {}", userId, e);
        }
    }
}

