package com.star.pivot.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 安全服务工具类
 *
 * @author stardust
 * @since 2024-01-01
 */
public class SecurityUtils {

    /**
     * BCrypt密码加密器
     */
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 生成BCrypt密码
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * 验证密码
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否相同
     */
    public static boolean isPasswordSame(String rawPassword, String encodedPassword) {
        return matchesPassword(rawPassword, encodedPassword);
    }
}

