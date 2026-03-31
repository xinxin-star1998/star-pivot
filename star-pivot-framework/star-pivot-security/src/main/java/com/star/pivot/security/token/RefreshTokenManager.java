package com.star.pivot.security.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenManager {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "jwt:refresh:user:";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshTokenExpiration;

    public String generateAndStoreRefreshToken(Long userId) {
        return generateAndStoreRefreshToken(userId, null, null, null, null);
    }

    public String generateAndStoreRefreshToken(Long userId, String ipaddr, String browser, String os, String loginLocation) {
        if (userId == null) {
            throw new IllegalArgumentException("生成刷新令牌时 userId 不能为空");
        }

        String rawToken = UUID.randomUUID().toString().replace("-", "");
        String tokenHash = DigestUtils.md5DigestAsHex(rawToken.getBytes(StandardCharsets.UTF_8));
        String key = buildKey(userId);

        try {
            Date now = new Date();
            RefreshTokenValue value = new RefreshTokenValue();
            value.setTokenHash(tokenHash);
            value.setIssuedAt(now);
            value.setIpaddr(ipaddr);
            value.setBrowser(browser);
            value.setOs(os);
            value.setLoginLocation(loginLocation);
            value.setLastAccessTime(now);

            redisTemplate.opsForValue().set(key, value, refreshTokenExpiration, TimeUnit.MILLISECONDS);
            log.debug("已为用户 {} 生成刷新令牌并存储登录信息，key={}, ip={}", userId, key, ipaddr);
        } catch (Exception e) {
            log.error("生成刷新令牌失败，userId={}", userId, e);
            throw new RuntimeException("生成刷新令牌失败", e);
        }

        return rawToken;
    }

    public void updateLastAccessTime(Long userId) {
        if (userId == null) {
            return;
        }
        String key = buildKey(userId);
        try {
            RefreshTokenValue value = readRefreshTokenValueCompat(key);
            if (value != null) {
                value.setLastAccessTime(new Date());
                Long expire = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
                if (expire != null && expire > 0) {
                    redisTemplate.opsForValue().set(key, value, expire, TimeUnit.MILLISECONDS);
                } else {
                    redisTemplate.opsForValue().set(key, value, refreshTokenExpiration, TimeUnit.MILLISECONDS);
                }
            }
        } catch (Exception e) {
            log.warn("更新最后访问时间失败，userId={}", userId, e);
        }
    }

    public boolean validateRefreshToken(Long userId, String rawToken) {
        if (userId == null || rawToken == null || rawToken.isEmpty()) {
            return false;
        }

        String key = buildKey(userId);
        RefreshTokenValue value = readRefreshTokenValueCompat(key);
        if (value == null) {
            return false;
        }

        String rawHash = DigestUtils.md5DigestAsHex(rawToken.getBytes(StandardCharsets.UTF_8));
        return rawHash.equals(value.getTokenHash());
    }

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

    public RefreshTokenValue getRefreshTokenValue(Long userId) {
        if (userId == null) {
            return null;
        }
        String key = buildKey(userId);
        return readRefreshTokenValueCompat(key);
    }

    private RefreshTokenValue readRefreshTokenValueCompat(String key) {
        try {
            Object stored = redisTemplate.opsForValue().get(key);
            if (stored instanceof RefreshTokenValue) {
                return (RefreshTokenValue) stored;
            }
            if (stored != null) {
                log.warn("刷新令牌类型不匹配，key={}, actualType={}", key, stored.getClass().getName());
            }
        } catch (Exception e) {
            log.debug("标准反序列化失败，尝试兼容读取，key={}, error={}", key, e.getMessage());
        }

        RefreshTokenValue legacyValue = readLegacyRefreshTokenValue(key);
        if (legacyValue == null) {
            return null;
        }

        // 兼容读取成功后回写为新类型，避免下次重复走兼容路径
        try {
            Long expire = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
            if (expire != null && expire > 0) {
                redisTemplate.opsForValue().set(key, legacyValue, expire, TimeUnit.MILLISECONDS);
            } else {
                redisTemplate.opsForValue().set(key, legacyValue);
            }
        } catch (Exception e) {
            log.debug("兼容数据回写失败，key={}, error={}", key, e.getMessage());
        }
        return legacyValue;
    }

    private RefreshTokenValue readLegacyRefreshTokenValue(String key) {
        try {
            byte[] rawBytes = redisTemplate.execute(
                    (RedisCallback<byte[]>) connection -> connection.get(key.getBytes(StandardCharsets.UTF_8)));
            if (rawBytes == null || rawBytes.length == 0) {
                return null;
            }

            JsonNode root = OBJECT_MAPPER.readTree(rawBytes);
            JsonNode payload = root;
            if (root.isArray() && root.size() > 1) {
                payload = root.get(1);
            }
            if (payload == null || !payload.isObject()) {
                return null;
            }

            RefreshTokenValue value = new RefreshTokenValue();
            value.setTokenHash(getText(payload, "tokenHash"));
            value.setIpaddr(getText(payload, "ipaddr"));
            value.setBrowser(getText(payload, "browser"));
            value.setOs(getText(payload, "os"));
            value.setLoginLocation(getText(payload, "loginLocation"));
            value.setIssuedAt(parseLegacyDate(payload.get("issuedAt")));
            value.setLastAccessTime(parseLegacyDate(payload.get("lastAccessTime")));
            return value;
        } catch (Exception e) {
            log.debug("兼容读取旧刷新令牌失败，key={}, error={}", key, e.getMessage());
            return null;
        }
    }

    private String getText(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        if (field == null || field.isNull()) {
            return null;
        }
        return field.asText(null);
    }

    private Date parseLegacyDate(JsonNode dateNode) {
        if (dateNode == null || dateNode.isNull()) {
            return null;
        }
        String rawText;
        if (dateNode.isArray() && dateNode.size() > 1) {
            rawText = dateNode.get(1).asText(null);
        } else {
            rawText = dateNode.asText(null);
        }
        if (rawText == null || rawText.isEmpty()) {
            return null;
        }
        try {
            return Date.from(OffsetDateTime.parse(rawText).toInstant());
        } catch (Exception ignored) {
            return null;
        }
    }

    private String buildKey(Long userId) {
        return REFRESH_TOKEN_PREFIX + userId;
    }

    public static class RefreshTokenValue implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private String tokenHash;
        private Date issuedAt;
        private String ipaddr;
        private String browser;
        private String os;
        private String loginLocation;
        private Date lastAccessTime;

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

        public String getIpaddr() {
            return ipaddr;
        }

        public void setIpaddr(String ipaddr) {
            this.ipaddr = ipaddr;
        }

        public String getBrowser() {
            return browser;
        }

        public void setBrowser(String browser) {
            this.browser = browser;
        }

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public String getLoginLocation() {
            return loginLocation;
        }

        public void setLoginLocation(String loginLocation) {
            this.loginLocation = loginLocation;
        }

        public Date getLastAccessTime() {
            return lastAccessTime;
        }

        public void setLastAccessTime(Date lastAccessTime) {
            this.lastAccessTime = lastAccessTime;
        }
    }
}
