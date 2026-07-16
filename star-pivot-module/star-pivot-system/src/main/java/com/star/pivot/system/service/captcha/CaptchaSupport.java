package com.star.pivot.system.service.captcha;

import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 验证码公共工具（token / hash / 图片编码）
 */
@Slf4j
public final class CaptchaSupport {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private CaptchaSupport() {
    }

    public static String tokenKey(String token) {
        return "captcha:token:" + token;
    }

    public static String proofKey(String proof) {
        return "captcha:proof:" + proof;
    }

    public static String randomToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String randomSalt() {
        byte[] bytes = new byte[16];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String sha256(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 算法不可用", e);
            throw new BizException(ErrorCode.CAPTCHA_GENERATE_ERROR, "验证码服务异常");
        }
    }

    public static String hashWithSalt(String value, String salt) {
        return sha256(value + ":" + salt);
    }

    public static String toJpegDataUrl(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", outputStream);
            String base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return "data:image/jpeg;base64," + base64;
        } catch (IOException e) {
            log.error("验证码图片编码失败", e);
            throw new BizException(ErrorCode.CAPTCHA_GENERATE_ERROR);
        }
    }

    public static String toPngDataUrl(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            String base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return "data:image/png;base64," + base64;
        } catch (IOException e) {
            log.error("验证码图片编码失败", e);
            throw new BizException(ErrorCode.CAPTCHA_GENERATE_ERROR);
        }
    }

    public static SecureRandom secureRandom() {
        return SECURE_RANDOM;
    }
}
