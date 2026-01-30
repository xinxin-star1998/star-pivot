package com.star.pivot.security;

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
 *
 * <p>使用场景：
 * <ul>
 *   <li>定期更换JWT密钥，提升安全性</li>
 *   <li>密钥泄露后的紧急轮换</li>
 *   <li>平滑过渡，避免所有用户立即失效</li>
 * </ul>
 *
 * <p>配置说明：
 * <ul>
 *   <li>jwt.secret: 当前密钥（必须）</li>
 *   <li>jwt.previous-secret: 上一个密钥（可选，密钥轮换时配置）</li>
 * </ul>
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Slf4j
@Component
public class JwtKeyManager {

    /**
     * 当前密钥（用于生成新Token和优先验证）
     */
    @Value("${jwt.secret}")
    private String currentSecret;

    /**
     * 上一个密钥（用于密钥轮换过渡期验证旧Token）
     * 如果未配置，则只使用当前密钥
     */
    @Value("${jwt.previous-secret:}")
    private String previousSecret;

    /**
     * 获取当前密钥的SecretKey对象
     *
     * @return 当前密钥
     */
    public SecretKey getCurrentSigningKey() {
        return getSigningKey(currentSecret, "当前密钥");
    }

    /**
     * 获取上一个密钥的SecretKey对象（如果配置了）
     *
     * @return 上一个密钥，如果未配置则返回null
     */
    public SecretKey getPreviousSigningKey() {
        if (previousSecret == null || previousSecret.trim().isEmpty()) {
            return null;
        }
        return getSigningKey(previousSecret, "上一个密钥");
    }

    /**
     * 生成SecretKey对象
     *
     * @param secret 密钥字符串
     * @param keyName 密钥名称（用于日志）
     * @return SecretKey对象
     */
    private SecretKey getSigningKey(String secret, String keyName) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException(keyName + "不能为空");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // HMAC SHA密钥需要至少256位（32字节）
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(keyName + "长度不足，至少需要32字节（256位）");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 验证Token（支持密钥轮换）
     *
     * <p>验证逻辑：
     * <ol>
     *   <li>优先使用当前密钥验证</li>
     *   <li>如果失败且配置了上一个密钥，尝试使用上一个密钥验证（支持密钥轮换过渡期）</li>
     *   <li>如果都失败，返回false</li>
     * </ol>
     *
     * @param token JWT Token
     * @return 如果验证成功返回true，否则返回false
     */
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        // 1. 优先使用当前密钥验证
        try {
            Claims claims = validateTokenWithKey(token, getCurrentSigningKey());
            if (claims != null && !claims.getExpiration().before(new Date())) {
                return true;
            }
        } catch (Exception e) {
            log.debug("使用当前密钥验证Token失败: {}", e.getMessage());
        }

        // 2. 如果当前密钥验证失败，尝试使用上一个密钥（密钥轮换过渡期）
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

    /**
     * 使用指定密钥验证Token
     *
     * @param token JWT Token
     * @param signingKey 签名密钥
     * @return Claims对象，如果验证失败返回null
     */
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

    /**
     * 从Token中获取Claims（支持密钥轮换）
     *
     * <p>先尝试使用当前密钥解析，失败则尝试上一个密钥
     *
     * @param token JWT Token
     * @return Claims对象
     * @throws RuntimeException 如果所有密钥都验证失败
     */
    public Claims getClaimsFromToken(String token) {
        // 优先使用当前密钥
        Claims claims = validateTokenWithKey(token, getCurrentSigningKey());
        if (claims != null) {
            return claims;
        }

        // 尝试使用上一个密钥
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
