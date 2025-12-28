package com.star.pivot.system.utils;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * JWT秘钥生成工具类
 * 用于生成符合HMAC-SHA256算法要求的256位(32字节)安全随机秘钥
 */
public class JwtSecretGenerator {

    public static void main(String[] args) {
        // JWT HMAC-SHA256算法要求秘钥至少256位(32字节)
        int keyLength = 32; // 单位：字节
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[keyLength];
        secureRandom.nextBytes(keyBytes);
        
        // 使用Base64编码便于存储和配置
        String secretKey = Base64.getEncoder().encodeToString(keyBytes);
        
        System.out.println("生成的JWT安全秘钥(请复制到application.properties中配置jwt.secret):");
        System.out.println("jwt.secret=" + secretKey);
    }
}