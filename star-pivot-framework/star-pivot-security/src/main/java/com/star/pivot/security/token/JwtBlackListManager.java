package com.star.pivot.security.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * JWT令牌黑名单管理器
 * 用于管理已登出的JWT令牌，确保令牌在登出后立即失效
 *
 * <p>性能优化：使用 SHA-256 哈希值作为 Redis key，而不是完整的 token 字符串
 * <ul>
 *   <li>减少 Redis 存储空间占用（JWT token 通常很长，200+ 字符）</li>
 *   <li>SHA-256 哈希值固定长度（64 字符），比 MD5（32字符）更安全</li>
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
     * 使用 SHA-256 计算字符串的哈希值
     *
     * @param input 输入字符串
     * @return SHA-256 哈希值（64字符十六进制字符串）
     */
    private String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 算法不可用", e);
        }
    }

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
            String tokenHash = sha256Hex(token);
            String key = BLACKLIST_PREFIX + tokenHash;

            String tokenPrefix = token.length() > 16 ? token.substring(0, 16) : token;
            String verificationHash = sha256Hex(tokenPrefix);

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
            String tokenHash = sha256Hex(token);
            String key = BLACKLIST_PREFIX + tokenHash;

            Object storedValue = redisTemplate.opsForValue().get(key);
            if (storedValue == null) {
                return false;
            }

            String tokenPrefix = token.length() > 16 ? token.substring(0, 16) : token;
            String expectedVerificationHash = sha256Hex(tokenPrefix);

            return expectedVerificationHash.equals(storedValue.toString());
        } catch (Exception e) {
            log.error("检查token黑名单失败", e);
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
            String tokenHash = sha256Hex(token);
            String key = BLACKLIST_PREFIX + tokenHash;
            redisTemplate.delete(key);
            log.debug("Token已从黑名单移除，hash: {}...", tokenHash.substring(0, Math.min(8, tokenHash.length())));
        } catch (Exception e) {
            log.error("从黑名单移除token失败", e);
        }
    }
}
