package com.star.pivot.system.service.impl;

import com.star.pivot.framework.cache.RedisCache;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.system.domain.CaptchaState;
import com.star.pivot.system.domain.bo.CaptchaIssueResponse;
import com.star.pivot.system.domain.bo.CaptchaVerifyRequest;
import com.star.pivot.system.domain.bo.CaptchaVerifyResponse;
import com.star.pivot.system.service.interfaces.CaptchaService;
import com.star.pivot.system.service.interfaces.ISysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private final ISysConfigService sysConfigService;

    // 验证码字符集（只包含大写字母和数字）
    private static final String CODE_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    // 验证码图片宽度（最小值，随位数自动扩展）
    private static final int IMAGE_WIDTH = 120;
    // 验证码图片高度
    private static final int IMAGE_HEIGHT = 40;
    /** 单个字符占位宽度 */
    private static final int CHAR_SLOT_WIDTH = 22;
    /** 图片左右留白 */
    private static final int IMAGE_PADDING = 10;
    // 验证码最大尝试次数
    private static final int MAX_ATTEMPTS = 5;
    // proof 过期时间（秒）
    private static final int PROOF_EXPIRE_SECONDS = 300;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /** 缓存字体，避免每次生成验证码时重复解析字体（首请求后加速） */
    private static final Font CAPTCHA_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 20);

    private static String buildCaptchaTokenKey(String token) {
        return "captcha:token:" + token;
    }

    private static String buildCaptchaProofKey(String proof) {
        return "captcha:proof:" + proof;
    }

    @Override
    public CaptchaIssueResponse generateCaptcha(String scene) {
        // 生成随机验证码和 token
        String code = generateRandomCode();
        // 为了让验证码校验对大小写不敏感，这里统一将用于校验的验证码转换为小写再参与哈希计算
        // 图片中仍然绘制原始 code（包含大小写），用户输入时可以任意大小写组合
        String normalizedCode = code.toLowerCase();
        String captchaToken = generateRandomToken();
        String salt = generateRandomSalt();
        String hash = hashCode(normalizedCode, salt);

        // 记录状态（不存明文）
        CaptchaState state = new CaptchaState();
        state.setCodeHash(hash);
        state.setSalt(salt);
        state.setAttempts(0);
        state.setMaxAttempts(MAX_ATTEMPTS);
        state.setScene(scene);

        String key = buildCaptchaTokenKey(captchaToken);
        int expireSeconds = sysConfigService.getCaptchaExpireSeconds();
        try {
            redisCache.setCacheObject(key, state, expireSeconds, TimeUnit.SECONDS);
            log.debug("验证码已存储到Redis，key: {}, scene: {}", key, scene);
        } catch (Exception e) {
            log.error("存储验证码到Redis失败，key: {}, error: {}", key, e.getMessage(), e);
            throw new BizException(ErrorCode.CAPTCHA_GENERATE_ERROR, "验证码生成失败，请检查Redis连接: " + e.getMessage());
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
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "验证码参数不完整");
        }

        String key = buildCaptchaTokenKey(request.getCaptchaToken());
        log.debug("验证验证码，key: {}, code: {}", key, request.getCode());
        
        CaptchaState state;
        try {
            state = redisCache.getCacheObject(key);
        } catch (Exception e) {
            log.error("从Redis获取验证码状态失败，key: {}, error: {}", key, e.getMessage(), e);
            throw new BizException(ErrorCode.REDIS_ERROR, "验证码校验失败，请检查Redis连接: " + e.getMessage());
        }
        
        if (state == null) {
            log.warn("验证码已失效，key: {}, token: {}", key, request.getCaptchaToken());
            throw new BizException(ErrorCode.CAPTCHA_EXPIRED);
        }

        if (state.getAttempts() >= state.getMaxAttempts()) {
            redisCache.deleteObject(key);
            throw new BizException(ErrorCode.CAPTCHA_TOO_MANY_ATTEMPTS);
        }

        String normalizedInputCode = request.getCode().toLowerCase();
        String inputHash = hashCode(normalizedInputCode, state.getSalt());
        boolean match = inputHash.equals(state.getCodeHash());

        if (!match) {
            int newAttempts = state.getAttempts() + 1;
            state.setAttempts(newAttempts);
            if (newAttempts >= state.getMaxAttempts()) {
                redisCache.deleteObject(key);
            } else {
                redisCache.setCacheObject(key, state, sysConfigService.getCaptchaExpireSeconds(), TimeUnit.SECONDS);
            }
            throw new BizException(ErrorCode.CAPTCHA_ERROR);
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

    /** 从 throwable 及其 cause 链中取第一条非空消息 */
    private static String getThrowableMessage(Throwable t) {
        for (Throwable x = t; x != null; x = x.getCause()) {
            if (x.getMessage() != null && !x.getMessage().isEmpty()) {
                return x.getMessage();
            }
        }
        return t.getClass().getSimpleName();
    }

    /**
     * 生成随机验证码
     */
    private String generateRandomCode() {
        int codeLength = sysConfigService.getCaptchaLength();
        Random random = new Random();
        StringBuilder sb = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++) {
            sb.append(CODE_CHARSET.charAt(random.nextInt(CODE_CHARSET.length())));
        }
        return sb.toString();
    }

    /**
     * 创建验证码图片。
     * Linux 无头环境（如 ECS）需安装 fontconfig 与字体包，否则会抛出 ServiceException。
     */
    private BufferedImage createImage(String code) {
        int imageWidth = Math.max(IMAGE_WIDTH, IMAGE_PADDING * 2 + code.length() * CHAR_SLOT_WIDTH);
        BufferedImage image = new BufferedImage(imageWidth, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 设置背景色
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, imageWidth, IMAGE_HEIGHT);

        // 绘制干扰线
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.drawLine(random.nextInt(imageWidth), random.nextInt(IMAGE_HEIGHT),
                    random.nextInt(imageWidth), random.nextInt(IMAGE_HEIGHT));
        }

        try {
            g.setFont(CAPTCHA_FONT);
            for (int i = 0; i < code.length(); i++) {
                g.setColor(new Color(random.nextInt(80), random.nextInt(80), random.nextInt(80)));
                int x = IMAGE_PADDING + i * CHAR_SLOT_WIDTH;
                g.drawString(String.valueOf(code.charAt(i)), x, 28);
            }
        } catch (Throwable t) {
            g.dispose();
            String hint = getThrowableMessage(t);
            if (hint.contains("Font") || hint.contains("fontconfig") || hint.contains("Fontconfig")) {
                throw new BizException(ErrorCode.CAPTCHA_GENERATE_ERROR, "验证码生成失败：服务器未安装字体。请在 ECS 上安装 fontconfig 与字体包，例如：yum install -y fontconfig dejavu-sans-fonts 或 apt-get install -y fontconfig fonts-dejavu-core");
            }
            throw new BizException(ErrorCode.CAPTCHA_GENERATE_ERROR, "验证码生成失败: " + hint);
        }

        // 绘制干扰点
        for (int i = 0; i < 50; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.fillRect(random.nextInt(imageWidth), random.nextInt(IMAGE_HEIGHT), 1, 1);
        }

        g.dispose();
        return image;
    }

    /**
     * 将图片转为 Base64 DataURL（使用 JPEG 编码，比 PNG 更快、体积更小）
     */
    private String toBase64DataUrl(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            return "data:image/jpeg;base64," + base64;
        } catch (IOException e) {
            log.error("验证码图片编码失败", e);
            throw new BizException(ErrorCode.CAPTCHA_GENERATE_ERROR);
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
            throw new BizException(ErrorCode.CAPTCHA_GENERATE_ERROR, "验证码服务异常");
        }
    }
}