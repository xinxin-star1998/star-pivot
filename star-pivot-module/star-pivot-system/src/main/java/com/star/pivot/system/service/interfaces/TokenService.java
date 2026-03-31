package com.star.pivot.system.service.interfaces;

import com.star.pivot.system.domain.bo.LoginResponse;
import com.star.pivot.system.domain.bo.TokenPair;
import com.star.pivot.system.domain.entity.SysUser;

/**
 * Token / 会话 统一门面服务
 */
public interface TokenService {

    /**
     * 为指定用户生成访问令牌与刷新令牌
     */
    TokenPair createTokenPair(SysUser user,
                              String username,
                              String ipaddr,
                              String browser,
                              String os,
                              String loginLocation);

    /**
     * 使用刷新令牌刷新访问令牌并轮换刷新令牌
     */
    LoginResponse refreshToken(String username, String refreshToken);

    /**
     * 处理登出逻辑：将访问令牌加入黑名单并吊销刷新令牌，同时记录在线用户历史
     *
     * @param token      访问令牌
     * @param logoutType 下线类型（0正常登出 1强制下线 2过期下线）
     */
    void logout(String token, String logoutType);

    /**
     * 强制指定用户下线（无访问令牌场景，如监控强制下线）
     *
     * @param userId     用户ID
     * @param logoutType 下线类型（建议使用 1 表示强制下线）
     */
    void forceLogout(Long userId, String logoutType);
}

