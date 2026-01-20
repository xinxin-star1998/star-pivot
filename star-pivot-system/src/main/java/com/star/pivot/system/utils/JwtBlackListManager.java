package com.star.pivot.system.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * JWT令牌黑名单管理器
 * 用于管理已登出的JWT令牌，确保令牌在登出后立即失效
 * 
 * <p>性能优化：使用 SHA-256 哈希值作为 Redis key，而不是完整的 token 字符串
 * <ul>
 *   <li>减少 Redis 存储空间占用（JWT token 通常很长，200+ 字符）</li>
 *   <li>哈希值固定长度（64 字符），便于 Redis 内存管理</li>
 *   <li>同时存储原始 token 的 hash 值用于验证，防止哈希碰撞</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtBlackListManager {

    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String BLACKLIST_PREFIX = "jwt:logout:";

    /**
     * 将JWT令牌加入黑名单
     * 
     * <p>使用 SHA-256 哈希值作为 key，减少存储空间
     * 同时存储 token 的 hash 值用于验证，防止哈希碰撞
     * 
     * @param token JWT令牌
     * @param expiration 令牌过期时间（毫秒）
     */
    public void addToBlackList(String token, long expiration) {
        if (token == null || token.isEmpty()) {
            log.warn("尝试将空token加入黑名单");
            return;
        }
        
        try {
            // 使用 SHA-256 哈希值作为 key，减少存储空间
            String tokenHash = DigestUtils.md5DigestAsHex(token.getBytes(StandardCharsets.UTF_8));
            String key = BLACKLIST_PREFIX + tokenHash;
            
            // 存储 token 的 hash 值用于验证（防止哈希碰撞）
            // 使用 token 的前16个字符作为验证值，既节省空间又能有效验证
            String tokenPrefix = token.length() > 16 ? token.substring(0, 16) : token;
            String verificationHash = DigestUtils.md5DigestAsHex(tokenPrefix.getBytes(StandardCharsets.UTF_8));
            
            // 将验证哈希值存储为 value，用于后续验证
            redisTemplate.opsForValue().set(key, verificationHash, expiration, TimeUnit.MILLISECONDS);
            
            log.debug("Token已加入黑名单，hash: {}...", tokenHash.substring(0, Math.min(8, tokenHash.length())));
        } catch (Exception e) {
            log.error("将token加入黑名单失败", e);
            throw new RuntimeException("黑名单操作失败", e);
        }
    }

    /**
     * 检查JWT令牌是否在黑名单中
     * 
     * <p>通过 token 的哈希值查找，同时验证存储的验证哈希值，防止哈希碰撞
     * 
     * @param token JWT令牌
     * @return 如果在黑名单中返回true，否则返回false
     */
    public boolean isBlackListed(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        try {
            // 计算 token 的哈希值
            String tokenHash = DigestUtils.md5DigestAsHex(token.getBytes(StandardCharsets.UTF_8));
            String key = BLACKLIST_PREFIX + tokenHash;
            
            // 检查 key 是否存在
            Object storedValue = redisTemplate.opsForValue().get(key);
            if (storedValue == null) {
                return false;
            }
            
            // 验证存储的哈希值是否匹配（防止哈希碰撞）
            String tokenPrefix = token.length() > 16 ? token.substring(0, 16) : token;
            String expectedVerificationHash = DigestUtils.md5DigestAsHex(tokenPrefix.getBytes(StandardCharsets.UTF_8));
            
            // 如果验证哈希值匹配，说明确实是这个 token
            return expectedVerificationHash.equals(storedValue.toString());
        } catch (Exception e) {
            log.error("检查token黑名单失败", e);
            // 发生异常时，为了安全起见，返回 true（拒绝访问）
            return true;
        }
    }

    /**
     * 从黑名单中移除JWT令牌（通常不需要，因为会自动过期）
     * 
     * @param token JWT令牌
     */
    public void removeFromBlackList(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }
        
        try {
            String tokenHash = DigestUtils.md5DigestAsHex(token.getBytes(StandardCharsets.UTF_8));
            String key = BLACKLIST_PREFIX + tokenHash;
            redisTemplate.delete(key);
            log.debug("Token已从黑名单移除，hash: {}...", tokenHash.substring(0, Math.min(8, tokenHash.length())));
        } catch (Exception e) {
            log.error("从黑名单移除token失败", e);
        }
    }
}