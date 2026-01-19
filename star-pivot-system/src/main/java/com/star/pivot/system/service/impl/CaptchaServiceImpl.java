package com.star.pivot.system.service.impl;

import com.star.pivot.common.exception.ServiceException;
import com.star.pivot.system.domain.bo.CaptchaIssueResponse;
import com.star.pivot.system.domain.bo.CaptchaVerifyRequest;
import com.star.pivot.system.domain.bo.CaptchaVerifyResponse;
import com.star.pivot.system.service.CaptchaService;
import com.star.pivot.system.utils.RedisCache;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 *
 * 设计要点：
 * - 服务端生成高熵 captchaToken（不可预测）
 * - Redis 中只存验证码 hash + 盐，不存明文
 * - 设置短 TTL + 限制尝试次数；成功或超限后失效
 * - 校验通过后签发一次性 captchaProof，业务接口消费 proof
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final RedisCache redisCache;

    // 验证码字符集
    private static final String CODE_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    // 验证码长度
    private static final int CODE_LENGTH = 4;
    // 验证码图片宽度
    private static final int IMAGE_WIDTH = 120;
    // 验证码图片高度
    private static final int IMAGE_HEIGHT = 40;
    // 验证码过期时间（秒）
    private static final int CAPTCHA_EXPIRE_SECONDS = 180;
    // 验证码最大尝试次数
    private static final int MAX_ATTEMPTS = 5;
    // proof 过期时间（秒）
    private static final int PROOF_EXPIRE_SECONDS = 300;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static String buildCaptchaTokenKey(String token) {
        return "captcha:token:" + token;
    }

    private static String buildCaptchaProofKey(String proof) {
        return "captcha:proof:" + proof;
    }

    @Data
    private static class CaptchaState implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 验证码 hash（SHA-256）
         */
        private String codeHash;

        /**
         * 随机盐
         */
        private String salt;

        /**
         * 已尝试次数
         */
        private int attempts;

        /**
         * 最大尝试次数
         */
        private int maxAttempts;

        /**
         * 业务场景（login/register/reset 等）
         */
        private String scene;
    }

    @Override
    public CaptchaIssueResponse generateCaptcha(String scene) {
        // 生成随机验证码和 token
        String code = generateRandomCode();
        String captchaToken = generateRandomToken();
        String salt = generateRandomSalt();
        String hash = hashCode(code, salt);

        // 记录状态（不存明文）
        CaptchaState state = new CaptchaState();
        state.setCodeHash(hash);
        state.setSalt(salt);
        state.setAttempts(0);
        state.setMaxAttempts(MAX_ATTEMPTS);
        state.setScene(scene);

        String key = buildCaptchaTokenKey(captchaToken);
        try {
            redisCache.setCacheObject(key, state, CAPTCHA_EXPIRE_SECONDS, TimeUnit.SECONDS);
            log.debug("验证码已存储到Redis，key: {}, scene: {}", key, scene);
            
            // 验证是否真的存储成功
            CaptchaState verifyState = redisCache.getCacheObject(key);
            if (verifyState == null) {
                log.error("验证码存储失败：Redis中未找到key: {}", key);
                throw new ServiceException("验证码存储失败，请检查Redis连接", 500);
            }
        } catch (Exception e) {
            log.error("存储验证码到Redis失败，key: {}, error: {}", key, e.getMessage(), e);
            throw new ServiceException("验证码生成失败，请检查Redis连接: " + e.getMessage(), 500);
        }

        // 生成验证码图片并转成 Base64 DataURL
        BufferedImage image = createImage(code);
        String base64Image = toBase64DataUrl(image);

        CaptchaIssueResponse response = new CaptchaIssueResponse();
        response.setCaptchaToken(captchaToken);
        response.setCaptchaImage(base64Image);
        return response;
    }

    @Override
    public CaptchaVerifyResponse verifyCaptcha(CaptchaVerifyRequest request) {
        if (request == null || request.getCaptchaToken() == null || request.getCode() == null) {
            throw new ServiceException("验证码参数不完整", 400);
        }

        String key = buildCaptchaTokenKey(request.getCaptchaToken());
        log.debug("验证验证码，key: {}, code: {}", key, request.getCode());
        
        CaptchaState state;
        try {
            state = redisCache.getCacheObject(key);
        } catch (Exception e) {
            log.error("从Redis获取验证码状态失败，key: {}, error: {}", key, e.getMessage(), e);
            throw new ServiceException("验证码校验失败，请检查Redis连接: " + e.getMessage(), 500);
        }
        
        if (state == null) {
            log.warn("验证码已失效，key: {}, token: {}", key, request.getCaptchaToken());
            throw new ServiceException("验证码已失效，请重新获取", 401);
        }

        if (state.getAttempts() >= state.getMaxAttempts()) {
            redisCache.deleteObject(key);
            throw new ServiceException("验证码尝试次数过多，请重新获取", 401);
        }

        String inputHash = hashCode(request.getCode(), state.getSalt());
        boolean match = inputHash.equals(state.getCodeHash());

        if (!match) {
            int newAttempts = state.getAttempts() + 1;
            state.setAttempts(newAttempts);
            if (newAttempts >= state.getMaxAttempts()) {
                redisCache.deleteObject(key);
            } else {
                // 重置存储，TTL 重新计时问题在这里可接受
                redisCache.setCacheObject(key, state, CAPTCHA_EXPIRE_SECONDS, TimeUnit.SECONDS);
            }
            throw new ServiceException("验证码错误", 401);
        }

        // 校验成功，删除验证码 token 状态（一次性）
        redisCache.deleteObject(key);

        // 生成短期 proof，并与场景绑定
        String captchaProof = generateRandomToken();
        String proofKey = buildCaptchaProofKey(captchaProof);
        String scene = request.getScene() != null ? request.getScene() : state.getScene();
        redisCache.setCacheObject(proofKey, scene, PROOF_EXPIRE_SECONDS, TimeUnit.SECONDS);

        CaptchaVerifyResponse response = new CaptchaVerifyResponse();
        response.setCaptchaProof(captchaProof);
        return response;
    }

    @Override
    public boolean validateAndConsumeCaptchaProof(String captchaProof, String scene) {
        if (captchaProof == null || captchaProof.isEmpty()) {
            return false;
        }

        String key = buildCaptchaProofKey(captchaProof);
        String storedScene = redisCache.getCacheObject(key);
        if (storedScene == null) {
            return false;
        }

        // 一次性使用，先删除
        redisCache.deleteObject(key);

        // 如果指定了场景，则需要匹配
        if (scene != null && !scene.isEmpty() && storedScene != null && !scene.equals(storedScene)) {
            log.warn("验证码 proof 场景不匹配，期望: {} 实际: {}", scene, storedScene);
            return false;
        }

        return true;
    }

    /**
     * 生成随机验证码
     */
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CODE_CHARSET.charAt(random.nextInt(CODE_CHARSET.length())));
        }
        return sb.toString();
    }

    /**
     * 创建验证码图片
     */
    private BufferedImage createImage(String code) {
        // 创建图片
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景色
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

        // 绘制干扰线
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.drawLine(random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT),
                    random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT));
        }

        // 设置字体
        g.setFont(new Font("Arial", Font.BOLD, 20));

        // 绘制验证码
        for (int i = 0; i < code.length(); i++) {
            g.setColor(new Color(random.nextInt(80), random.nextInt(80), random.nextInt(80)));
            g.drawString(String.valueOf(code.charAt(i)), 25 * i + 10, 25);
        }

        // 绘制干扰点
        for (int i = 0; i < 50; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.fillRect(random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT), 1, 1);
        }

        g.dispose();
        return image;
    }

    /**
     * 将图片转为 Base64 DataURL
     */
    private String toBase64DataUrl(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            return "data:image/png;base64," + base64;
        } catch (IOException e) {
            log.error("验证码图片编码失败", e);
            throw new ServiceException("生成验证码失败", 500);
        }
    }

    /**
     * 生成高熵随机 token（Base64URL）
     */
    private String generateRandomToken() {
        byte[] bytes = new byte[32]; // 256 bit
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * 生成随机盐
     */
    private String generateRandomSalt() {
        byte[] bytes = new byte[16];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * 对验证码 + 盐做 SHA-256 hash
     */
    private String hashCode(String code, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String raw = code + ":" + salt;
            byte[] hash = digest.digest(raw.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 算法不可用", e);
            throw new ServiceException("验证码服务异常", 500);
        }
    }
}