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
import java.util.Map;

/**
 * JWT 工具类
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration; // 默认24小时，单位：毫秒

    /**
     * 可选的刷新令牌有效期配置
     *
     * <p>说明：
     * <ul>
     *   <li>Access Token 仍然使用 {@link #expiration} 作为过期时间</li>
     *   <li>刷新接口在生成新的 Access Token 时，可以根据业务需要决定是否参考该值</li>
     * </ul>
     */
    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshExpiration;

    /**
     * 生成密钥
     */
    private SecretKey getSigningKey() {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT密钥不能为空");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // HMAC SHA密钥需要至少256位（32字节）
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT密钥长度不足，至少需要32字节（256位）");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 Token
     */
    public String generateToken(String username, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从 Token 中获取 Claims
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 从 Token 中获取用户ID
     *
     * <p>说明：生成 Token 时需在 claims 中加入 {@code userId} 字段，
     * 否则该方法会返回 {@code null}。
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object userId = claims.get("userId");
        if (userId == null) {
            return null;
        }
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        if (userId instanceof Long) {
            return (Long) userId;
        }
        try {
            return Long.valueOf(userId.toString());
        } catch (NumberFormatException e) {
            log.warn("从Token中解析userId失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.debug("Token已过期：{}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("Token格式错误：{}", e.getMessage());
            return false;
        } catch (SecurityException e) {
            log.warn("Token签名验证失败：{}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("Token为空或格式不正确：{}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Token验证异常：", e);
            return false;
        }
    }

    /**
     * 判断 Token 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.debug("Token已过期：{}", e.getMessage());
            return true;
        } catch (Exception e) {
            log.warn("判断Token是否过期时发生异常：{}", e.getMessage());
            return true;
        }
    }
}

