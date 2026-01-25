package com.star.pivot.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 刷新令牌管理器
 *
 * <p>职责：
 * <ul>
 *   <li>生成刷新令牌（不直接复用访问令牌）</li>
 *   <li>将刷新令牌与用户信息、过期时间存储到 Redis</li>
 *   <li>校验刷新令牌是否有效（存在、未过期、未被吊销）</li>
 *   <li>在刷新或登出时吊销对应刷新令牌</li>
 * </ul>
 *
 * <p>实现要点：
 * <ul>
 *   <li>使用随机 UUID 作为原始刷新令牌，避免直接暴露用户敏感信息</li>
 *   <li>Redis 中仅保存哈希后的 token，减小存储体积并避免泄露原始 token</li>
 *   <li>以用户 ID 作为索引前缀，方便按用户维度批量吊销</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenManager {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 刷新令牌在 Redis 中的 key 前缀
     * <p>完整 key 形如：jwt:refresh:user:{userId}
     */
    private static final String REFRESH_TOKEN_PREFIX = "jwt:refresh:user:";

    /**
     * 刷新令牌默认有效期（毫秒），可通过配置覆盖
     * <p>默认 7 天：7 * 24 * 60 * 60 * 1000
     */
    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshTokenExpiration;

    /**
     * 生成刷新令牌并存储
     *
     * @param userId 用户 ID
     * @return 刷新令牌原文（返回给前端）
     */
    public String generateAndStoreRefreshToken(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("生成刷新令牌时 userId 不能为空");
        }

        // 生成随机刷新令牌原文
        String rawToken = UUID.randomUUID().toString().replace("-", "");
        // 对原文做哈希后再存入 Redis，避免直接存储原始值
        String tokenHash = DigestUtils.md5DigestAsHex(rawToken.getBytes(StandardCharsets.UTF_8));

        String key = buildKey(userId);

        try {
            // 记录生成时间，方便排查问题
            RefreshTokenValue value = new RefreshTokenValue();
            value.setTokenHash(tokenHash);
            value.setIssuedAt(new Date());

            redisTemplate.opsForValue().set(key, value, refreshTokenExpiration, TimeUnit.MILLISECONDS);

            log.debug("已为用户 {} 生成刷新令牌，key={}", userId, key);
        } catch (Exception e) {
            log.error("生成刷新令牌失败，userId={}", userId, e);
            throw new RuntimeException("生成刷新令牌失败", e);
        }

        // 仅将原始刷新令牌返回给调用方
        return rawToken;
    }

    /**
     * 校验刷新令牌是否有效
     *
     * @param userId   用户 ID
     * @param rawToken 刷新令牌原文
     * @return 如果有效返回 true，否则返回 false
     */
    public boolean validateRefreshToken(Long userId, String rawToken) {
        if (userId == null || rawToken == null || rawToken.isEmpty()) {
            return false;
        }

        String key = buildKey(userId);
        Object stored = redisTemplate.opsForValue().get(key);
        if (stored == null) {
            return false;
        }

        if (!(stored instanceof RefreshTokenValue)) {
            // 兼容极端情况：Redis 中数据被人为修改
            log.warn("刷新令牌存储格式异常，key={}", key);
            return false;
        }

        RefreshTokenValue value = (RefreshTokenValue) stored;
        String rawHash = DigestUtils.md5DigestAsHex(rawToken.getBytes(StandardCharsets.UTF_8));
        return rawHash.equals(value.getTokenHash());
    }

    /**
     * 吊销指定用户的刷新令牌
     *
     * @param userId 用户 ID
     */
    public void revokeRefreshToken(Long userId) {
        if (userId == null) {
            return;
        }
        String key = buildKey(userId);
        try {
            redisTemplate.delete(key);
            log.debug("已吊销用户 {} 的刷新令牌", userId);
        } catch (Exception e) {
            log.error("吊销刷新令牌失败，userId={}", userId, e);
        }
    }

    /**
     * 计算刷新令牌剩余有效期
     *
     * @param userId 用户 ID
     * @return 剩余时间（Duration），如果不存在或已过期则返回 Duration.ZERO
     */
    public Duration getRemainingTTL(Long userId) {
        if (userId == null) {
            return Duration.ZERO;
        }
        String key = buildKey(userId);
        Long expire = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
        if (expire == null || expire <= 0) {
            return Duration.ZERO;
        }
        return Duration.ofMillis(expire);
    }

    /**
     * 构建 Redis key
     */
    private String buildKey(Long userId) {
        return REFRESH_TOKEN_PREFIX + userId;
    }

    /**
     * 刷新令牌在 Redis 中的存储结构
     *
     * <p>为了减少 Redis 中的字段数量，这里仅存储：
     * <ul>
     *   <li>tokenHash：原始刷新令牌的哈希值</li>
     *   <li>issuedAt：签发时间</li>
     * </ul>
     */
    public static class RefreshTokenValue implements java.io.Serializable {
        /**
         * 刷新令牌原文的哈希值
         */
        private String tokenHash;

        /**
         * 刷新令牌签发时间
         */
        private Date issuedAt;

        public String getTokenHash() {
            return tokenHash;
        }

        public void setTokenHash(String tokenHash) {
            this.tokenHash = tokenHash;
        }

        public Date getIssuedAt() {
            return issuedAt;
        }

        public void setIssuedAt(Date issuedAt) {
            this.issuedAt = issuedAt;
        }
    }
}

