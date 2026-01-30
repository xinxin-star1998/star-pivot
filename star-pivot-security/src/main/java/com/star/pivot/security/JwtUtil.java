package com.star.pivot.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类（支持密钥轮换）
 *
 * <p>说明：
 * <ul>
 *   <li>生成Token时使用当前密钥</li>
 *   <li>验证Token时支持密钥轮换（优先使用当前密钥，失败则尝试上一个密钥）</li>
 *   <li>如需启用密钥轮换，请配置 jwt.previous-secret</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtKeyManager jwtKeyManager;

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
     * 生成密钥（使用当前密钥）
     */
    private SecretKey getSigningKey() {
        // 使用JwtKeyManager获取当前密钥（支持密钥轮换）
        return jwtKeyManager.getCurrentSigningKey();
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
     * 从 Token 中获取 Claims（支持密钥轮换）
     */
    public Claims getClaimsFromToken(String token) {
        // 使用JwtKeyManager解析Token（支持密钥轮换）
        return jwtKeyManager.getClaimsFromToken(token);
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
     * 验证 Token 是否有效（支持密钥轮换）
     */
    public boolean validateToken(String token) {
        // 使用JwtKeyManager验证Token（支持密钥轮换）
        return jwtKeyManager.validateToken(token);
    }

    /**
     * 判断 Token 是否过期（支持密钥轮换）
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.debug("判断Token是否过期时发生异常：{}", e.getMessage());
            return true;
        }
    }
}

