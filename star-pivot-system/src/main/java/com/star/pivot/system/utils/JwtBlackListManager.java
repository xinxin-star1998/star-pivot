package com.star.pivot.system.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * JWT令牌黑名单管理器
 * 用于管理已登出的JWT令牌，确保令牌在登出后立即失效
 */
@Component
@RequiredArgsConstructor
public class JwtBlackListManager {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 将JWT令牌加入黑名单
     * 
     * @param token JWT令牌
     * @param expiration 令牌过期时间（毫秒）
     */
    public void addToBlackList(String token, long expiration) {
        // 使用固定前缀存储已登出的令牌
        String key = "jwt:logout:" + token;
        // 设置值为1，表示该令牌已登出
        redisTemplate.opsForValue().set(key, 1, expiration, TimeUnit.MILLISECONDS);
    }

    /**
     * 检查JWT令牌是否在黑名单中
     * 
     * @param token JWT令牌
     * @return 如果在黑名单中返回true，否则返回false
     */
    public boolean isBlackListed(String token) {
        String key = "jwt:logout:" + token;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null;
    }

    /**
     * 从黑名单中移除JWT令牌（通常不需要，因为会自动过期）
     * 
     * @param token JWT令牌
     */
    public void removeFromBlackList(String token) {
        String key = "jwt:logout:" + token;
        redisTemplate.delete(key);
    }
}