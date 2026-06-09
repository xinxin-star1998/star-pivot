package com.star.pivot.security.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenManager {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtBlackListManager jwtBlackListManager;
    private static final String REFRESH_TOKEN_PREFIX = "jwt:refresh:user:";
    private static final String DEVICE_SESSION_PREFIX = "jwt:device:session:";
    private static final String ACCESS_TOKEN_KEY_PREFIX = "jwt:access:token:";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Value("${jwt.expiration:86400000}")
    private long accessTokenExpiration;
    
    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshTokenExpiration;
    
    @Value("${jwt.max-concurrent-sessions:5}")
    private int maxConcurrentSessions;
    
    @Value("${jwt.refresh-token-rotation:true}")
    private boolean refreshTokenRotation;

    public String generateAndStoreRefreshToken(Long userId) {
        return generateAndStoreRefreshToken(userId, null, null, null, null, null);
    }

    public String generateAndStoreRefreshToken(Long userId,
                                               String ipaddr,
                                               String browser,
                                               String os,
                                               String loginLocation,
                                               String accessToken) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        String rawToken = UUID.randomUUID().toString().replace("-", "");
        String tokenHash = sha256Hex(rawToken);
        String key = buildKey(userId);
        
        // 生成设备指纹（基于设备信息，仅用于展示）
        String deviceFingerprint = generateDeviceFingerprint(ipaddr, browser, os);
        // deviceSessionId 使用 UUID 随机生成，避免确定性导致会话覆盖
        String deviceSessionId = UUID.randomUUID().toString().replace("-", "");

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
            value.setDeviceFingerprint(deviceFingerprint);
            value.setDeviceSessionId(deviceSessionId);

            enforceMaxSessions(userId, deviceSessionId);
            
            redisTemplate.opsForValue().set(key, value, refreshTokenExpiration, TimeUnit.MILLISECONDS);
            
            // 单独存储 Access Token 明文（用于后续加入黑名单），设置与刷新令牌相同的过期时间
            storeAccessTokenForRevocation(userId, accessToken);
            
            storeDeviceSession(userId, deviceSessionId, rawToken, ipaddr, browser, os);
            
            log.info("已为用户 {} 生成刷新令牌并存储登录信息，key={}, ip={}, deviceSession={}", 
                userId, key, ipaddr, deviceSessionId);
        } catch (Exception e) {
            log.error("生成刷新令牌失败，userId={}", userId, e);
            throw new RuntimeException("生成刷新令牌失败", e);
        }

        return rawToken;
    }
    
    /**
     * 生成设备指纹（仅用于展示目的）
     */
    private String generateDeviceFingerprint(String ipaddr, String browser, String os) {
        String rawInput = (ipaddr != null ? ipaddr : "") + 
                         ":" + (browser != null ? browser : "") + 
                         ":" + (os != null ? os : "");
        return sha256Hex(rawInput);
    }

    /**
     * 计算 SHA-256 哈希值并转换为十六进制字符串
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
     * 存储 Access Token 明文到 Redis，以便后续需要时加入黑名单
     */
    private void storeAccessTokenForRevocation(Long userId, String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            return;
        }
        String key = ACCESS_TOKEN_KEY_PREFIX + userId;
        try {
            redisTemplate.opsForValue().set(key, accessToken, refreshTokenExpiration, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.warn("存储 Access Token 失败，userId={}", userId, e);
        }
    }
    
    /**
     * 获取存储的 Access Token 明文
     */
    private String getStoredAccessToken(Long userId) {
        String key = ACCESS_TOKEN_KEY_PREFIX + userId;
        try {
            Object stored = redisTemplate.opsForValue().get(key);
            return stored != null ? stored.toString() : null;
        } catch (Exception e) {
            log.warn("获取 Access Token 失败，userId={}", userId, e);
            return null;
        }
    }
    
    private void enforceMaxSessions(Long userId, String newDeviceSessionId) {
        String sessionSetKey = DEVICE_SESSION_PREFIX + userId;
        
        try {
            Long currentCount = redisTemplate.opsForSet().size(sessionSetKey);
            if (currentCount != null && currentCount >= maxConcurrentSessions) {
                Object oldestSession = redisTemplate.opsForSet().pop(sessionSetKey);
                if (oldestSession != null) {
                    String oldestSessionStr = oldestSession.toString();
                    if (!oldestSessionStr.equals(newDeviceSessionId)) {
                        log.info("用户 {} 达到最大会话数 {}，移除最旧会话: {}", 
                            userId, maxConcurrentSessions, oldestSessionStr);
                        removeSessionByDeviceSessionId(userId, oldestSessionStr);
                    } else {
                        redisTemplate.opsForSet().add(sessionSetKey, oldestSession);
                    }
                }
            }
            
            redisTemplate.opsForSet().add(sessionSetKey, newDeviceSessionId);
            redisTemplate.expire(sessionSetKey, refreshTokenExpiration, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.warn("管理会话数量失败，userId={}", userId, e);
        }
    }
    
    private void storeDeviceSession(Long userId, String deviceSessionId, String rawToken, 
                                   String ipaddr, String browser, String os) {
        String sessionKey = DEVICE_SESSION_PREFIX + userId + ":" + deviceSessionId;
        try {
            DeviceSessionInfo sessionInfo = new DeviceSessionInfo();
            sessionInfo.setDeviceSessionId(deviceSessionId);
            sessionInfo.setRawTokenHash(sha256Hex(rawToken));
            sessionInfo.setIpaddr(ipaddr);
            sessionInfo.setBrowser(browser);
            sessionInfo.setOs(os);
            sessionInfo.setCreatedAt(new Date());
            sessionInfo.setLastAccessTime(new Date());
            
            redisTemplate.opsForValue().set(sessionKey, sessionInfo, refreshTokenExpiration, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.warn("存储设备会话信息失败，userId={}, deviceSessionId={}", userId, deviceSessionId, e);
        }
    }
    
    private void removeSessionByDeviceSessionId(Long userId, String deviceSessionId) {
        String sessionKey = DEVICE_SESSION_PREFIX + userId + ":" + deviceSessionId;
        try {
            redisTemplate.delete(sessionKey);
        } catch (Exception e) {
            log.warn("删除设备会话失败，userId={}, deviceSessionId={}", userId, deviceSessionId, e);
        }
    }
    
    /**
     * 删除指定的设备会话
     * 
     * @param userId 用户ID
     * @param deviceSessionId 设备会话ID
     */
    public void removeDeviceSession(Long userId, String deviceSessionId) {
        if (userId == null || deviceSessionId == null) {
            return;
        }
        String sessionKey = DEVICE_SESSION_PREFIX + userId + ":" + deviceSessionId;
        String sessionSetKey = DEVICE_SESSION_PREFIX + userId;
        try {
            // 删除设备会话信息
            redisTemplate.delete(sessionKey);
            // 从会话集合中移除
            redisTemplate.opsForSet().remove(sessionSetKey, deviceSessionId);
            log.info("已删除设备会话，userId={}, deviceSessionId={}", userId, deviceSessionId);
        } catch (Exception e) {
            log.warn("删除设备会话失败，userId={}, deviceSessionId={}", userId, deviceSessionId, e);
        }
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
                
                if (value.getDeviceSessionId() != null) {
                    updateDeviceSessionAccessTime(userId, value.getDeviceSessionId());
                }
                
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
    
    private void updateDeviceSessionAccessTime(Long userId, String deviceSessionId) {
        String sessionKey = DEVICE_SESSION_PREFIX + userId + ":" + deviceSessionId;
        try {
            DeviceSessionInfo sessionInfo = (DeviceSessionInfo) redisTemplate.opsForValue().get(sessionKey);
            if (sessionInfo != null) {
                sessionInfo.setLastAccessTime(new Date());
                Long expire = redisTemplate.getExpire(sessionKey, TimeUnit.MILLISECONDS);
                if (expire != null && expire > 0) {
                    redisTemplate.opsForValue().set(sessionKey, sessionInfo, expire, TimeUnit.MILLISECONDS);
                }
            }
        } catch (Exception e) {
            log.debug("更新设备会话访问时间失败，userId={}, deviceSessionId={}", userId, deviceSessionId, e);
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

        String rawHash = sha256Hex(rawToken);
        boolean isValid = rawHash.equals(value.getTokenHash());
        
        if (isValid && refreshTokenRotation) {
            updateLastAccessTime(userId);
        }
        
        return isValid;
    }

    public void revokeRefreshToken(Long userId) {
        if (userId == null) {
            return;
        }
        String key = buildKey(userId);
        try {
            RefreshTokenValue value = readRefreshTokenValueCompat(key);
            if (value != null && value.getDeviceSessionId() != null) {
                removeSessionByDeviceSessionId(userId, value.getDeviceSessionId());
                
                String sessionSetKey = DEVICE_SESSION_PREFIX + userId;
                redisTemplate.opsForSet().remove(sessionSetKey, value.getDeviceSessionId());
            }
            
            // 将 Access Token 加入黑名单
            revokeAccessTokenFromBlacklist(userId);
            
            redisTemplate.delete(key);
            log.debug("已吊销用户 {} 的刷新令牌", userId);
        } catch (Exception e) {
            log.error("吊销刷新令牌失败，userId={}", userId, e);
        }
    }
    
    public void revokeAllUserSessions(Long userId) {
        if (userId == null) {
            return;
        }
        
        String key = buildKey(userId);
        String sessionSetKey = DEVICE_SESSION_PREFIX + userId;
        
        try {
            // 将 Access Token 加入黑名单（通过 JwtBlackListManager）
            revokeAccessTokenFromBlacklist(userId);
            
            // 删除刷新令牌
            redisTemplate.delete(key);
            
            // 获取所有会话ID，然后用 Pipeline 批量删除
            Set<Object> sessions = redisTemplate.opsForSet().members(sessionSetKey);
            
            // 使用 Pipeline 批量删除所有设备会话，减少 Redis 往返次数
            if (sessions != null && !sessions.isEmpty()) {
                redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                    for (Object session : sessions) {
                        String sessionKey = DEVICE_SESSION_PREFIX + userId + ":" + session;
                        connection.keyCommands().del(sessionKey.getBytes(StandardCharsets.UTF_8));
                    }
                    return null;
                });
            }
            
            // 删除会话集合和 Access Token 存储
            redisTemplate.delete(sessionSetKey);
            redisTemplate.delete(ACCESS_TOKEN_KEY_PREFIX + userId);
            
            log.info("已吊销用户 {} 的所有会话", userId);
        } catch (Exception e) {
            log.error("吊销用户所有会话失败，userId={}", userId, e);
        }
    }
    
    /**
     * 将用户的 Access Token 加入黑名单（通过 JwtBlackListManager）
     */
    private void revokeAccessTokenFromBlacklist(Long userId) {
        try {
            String accessToken = getStoredAccessToken(userId);
            if (accessToken != null && !accessToken.isEmpty()) {
                // Access Token 黑名单过期时间应与 Access Token 自身的有效期一致，而非 Refresh Token 的有效期
                jwtBlackListManager.addToBlackList(accessToken, accessTokenExpiration);
                log.debug("已将用户 {} 的 Access Token 加入黑名单，TTL={}ms", userId, accessTokenExpiration);
            }
        } catch (Exception e) {
            log.warn("将 Access Token 加入黑名单失败，userId={}", userId, e);
        }
    }

    public List<DeviceSessionInfo> getUserActiveSessions(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        
        String sessionSetKey = DEVICE_SESSION_PREFIX + userId;
        List<DeviceSessionInfo> sessions = new ArrayList<>();
        
        try {
            Set<Object> sessionIds = redisTemplate.opsForSet().members(sessionSetKey);
            if (sessionIds != null && !sessionIds.isEmpty()) {
                // 使用 Pipeline 批量获取，减少 Redis 往返次数
                List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                    for (Object sessionId : sessionIds) {
                        String sessionKey = DEVICE_SESSION_PREFIX + userId + ":" + sessionId;
                        connection.get(sessionKey.getBytes(StandardCharsets.UTF_8));
                    }
                    return null;
                });
                
                for (Object result : results) {
                    if (result instanceof DeviceSessionInfo) {
                        sessions.add((DeviceSessionInfo) result);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("获取用户活跃会话失败，userId={}", userId, e);
        }
        
        return sessions;
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
        private static final long serialVersionUID = 2L;
        private String tokenHash;
        private Date issuedAt;
        private String ipaddr;
        private String browser;
        private String os;
        private String loginLocation;
        private Date lastAccessTime;
        private String deviceFingerprint;
        private String deviceSessionId;

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
        
        public String getDeviceFingerprint() {
            return deviceFingerprint;
        }
        
        public void setDeviceFingerprint(String deviceFingerprint) {
            this.deviceFingerprint = deviceFingerprint;
        }
        
        public String getDeviceSessionId() {
            return deviceSessionId;
        }
        
        public void setDeviceSessionId(String deviceSessionId) {
            this.deviceSessionId = deviceSessionId;
        }
    }
    
    public static class DeviceSessionInfo implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private String deviceSessionId;
        private String rawTokenHash;
        private String ipaddr;
        private String browser;
        private String os;
        private Date createdAt;
        private Date lastAccessTime;
        
        public String getDeviceSessionId() {
            return deviceSessionId;
        }
        
        public void setDeviceSessionId(String deviceSessionId) {
            this.deviceSessionId = deviceSessionId;
        }
        
        public String getRawTokenHash() {
            return rawTokenHash;
        }
        
        public void setRawTokenHash(String rawTokenHash) {
            this.rawTokenHash = rawTokenHash;
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
        
        public Date getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }
        
        public Date getLastAccessTime() {
            return lastAccessTime;
        }
        
        public void setLastAccessTime(Date lastAccessTime) {
            this.lastAccessTime = lastAccessTime;
        }
    }
}
