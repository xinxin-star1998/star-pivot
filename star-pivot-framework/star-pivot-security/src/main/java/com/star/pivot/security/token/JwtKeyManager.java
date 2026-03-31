package com.star.pivot.security.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 密钥管理器（支持密钥轮换）
 *
 * <p>功能说明：
 * <ul>
 *   <li>支持多密钥管理：当前密钥和上一个密钥</li>
 *   <li>密钥轮换过渡期：在密钥轮换期间，同时支持新旧密钥验证</li>
 *   <li>新Token生成：始终使用当前密钥生成新Token</li>
 * </ul>
 */
@Slf4j
@Component
public class JwtKeyManager {

    @Value("${jwt.secret}")
    private String currentSecret;

    @Value("${jwt.previous-secret:}")
    private String previousSecret;

    public SecretKey getCurrentSigningKey() {
        return getSigningKey(currentSecret, "当前密钥");
    }

    public SecretKey getPreviousSigningKey() {
        if (previousSecret == null || previousSecret.trim().isEmpty()) {
            return null;
        }
        return getSigningKey(previousSecret, "上一个密钥");
    }

    private SecretKey getSigningKey(String secret, String keyName) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException(keyName + "不能为空");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(keyName + "长度不足，至少需要32字节（256位）");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            Claims claims = validateTokenWithKey(token, getCurrentSigningKey());
            if (claims != null && !claims.getExpiration().before(new Date())) {
                return true;
            }
        } catch (Exception e) {
            log.debug("使用当前密钥验证Token失败: {}", e.getMessage());
        }

        SecretKey previousKey = getPreviousSigningKey();
        if (previousKey != null) {
            try {
                Claims claims = validateTokenWithKey(token, previousKey);
                if (claims != null && !claims.getExpiration().before(new Date())) {
                    log.debug("使用上一个密钥验证Token成功（密钥轮换过渡期）");
                    return true;
                }
            } catch (Exception e) {
                log.debug("使用上一个密钥验证Token失败: {}", e.getMessage());
            }
        }

        return false;
    }

    private Claims validateTokenWithKey(String token, SecretKey signingKey) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.debug("Token已过期：{}", e.getMessage());
            return null;
        } catch (MalformedJwtException e) {
            log.debug("Token格式错误：{}", e.getMessage());
            return null;
        } catch (SecurityException e) {
            log.debug("Token签名验证失败：{}", e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            log.debug("Token为空或格式不正确：{}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("Token验证异常：{}", e.getMessage());
            return null;
        }
    }

    public Claims getClaimsFromToken(String token) {
        Claims claims = validateTokenWithKey(token, getCurrentSigningKey());
        if (claims != null) {
            return claims;
        }

        SecretKey previousKey = getPreviousSigningKey();
        if (previousKey != null) {
            claims = validateTokenWithKey(token, previousKey);
            if (claims != null) {
                log.debug("使用上一个密钥解析Token成功（密钥轮换过渡期）");
                return claims;
            }
        }

        throw new RuntimeException("Token验证失败：所有密钥都无法验证该Token");
    }
}
