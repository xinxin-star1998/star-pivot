package com.star.pivot.system.service.captcha;

import com.star.pivot.framework.cache.RedisCache;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.system.constants.CaptchaTypes;
import com.star.pivot.system.domain.CaptchaState;
import com.star.pivot.system.domain.bo.CaptchaIssueResponse;
import com.star.pivot.system.domain.bo.CaptchaVerifyRequest;
import com.star.pivot.system.service.interfaces.ISysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 图形字符验证码
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImageCaptchaProvider implements CaptchaProvider {

    private static final String CODE_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int IMAGE_WIDTH = 120;
    private static final int IMAGE_HEIGHT = 40;
    private static final int CHAR_SLOT_WIDTH = 22;
    private static final int IMAGE_PADDING = 10;
    private static final int MAX_ATTEMPTS = 5;
    private static final Font CAPTCHA_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 20);

    private final RedisCache redisCache;
    private final ISysConfigService sysConfigService;

    @Override
    public String getType() {
        return CaptchaTypes.IMAGE;
    }

    @Override
    public CaptchaIssueResponse generate(String scene) {
        String code = generateRandomCode();
        String normalizedCode = code.toLowerCase();
        String captchaToken = CaptchaSupport.randomToken();
        String salt = CaptchaSupport.randomSalt();

        CaptchaState state = new CaptchaState();
        state.setCodeHash(CaptchaSupport.hashWithSalt(normalizedCode, salt));
        state.setSalt(salt);
        state.setAttempts(0);
        state.setMaxAttempts(MAX_ATTEMPTS);
        state.setScene(scene);
        state.setCaptchaType(CaptchaTypes.IMAGE);

        String key = CaptchaSupport.tokenKey(captchaToken);
        int expireSeconds = sysConfigService.getCaptchaExpireSeconds();
        try {
            redisCache.setCacheObject(key, state, expireSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("存储图形验证码失败，key={}", key, e);
            throw new BizException(ErrorCode.CAPTCHA_GENERATE_ERROR, "验证码生成失败，请检查Redis连接: " + e.getMessage());
        }

        CaptchaIssueResponse response = new CaptchaIssueResponse();
        response.setCaptchaType(CaptchaTypes.IMAGE);
        response.setCaptchaToken(captchaToken);
        response.setCaptchaImage(CaptchaSupport.toJpegDataUrl(createImage(code)));
        return response;
    }

    @Override
    public boolean matches(CaptchaVerifyRequest request, CaptchaState state) {
        if (request == null || !StringUtils.hasText(request.getCode())) {
            return false;
        }
        String inputHash = CaptchaSupport.hashWithSalt(request.getCode().toLowerCase(), state.getSalt());
        return inputHash.equals(state.getCodeHash());
    }

    private String generateRandomCode() {
        int codeLength = sysConfigService.getCaptchaLength();
        Random random = new Random();
        StringBuilder sb = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++) {
            sb.append(CODE_CHARSET.charAt(random.nextInt(CODE_CHARSET.length())));
        }
        return sb.toString();
    }

    private BufferedImage createImage(String code) {
        int imageWidth = Math.max(IMAGE_WIDTH, IMAGE_PADDING * 2 + code.length() * CHAR_SLOT_WIDTH);
        BufferedImage image = new BufferedImage(imageWidth, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, imageWidth, IMAGE_HEIGHT);

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
                g.drawString(String.valueOf(code.charAt(i)), IMAGE_PADDING + i * CHAR_SLOT_WIDTH, 28);
            }
        } catch (Throwable t) {
            g.dispose();
            String hint = t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName();
            if (hint.contains("Font") || hint.contains("fontconfig") || hint.contains("Fontconfig")) {
                throw new BizException(ErrorCode.CAPTCHA_GENERATE_ERROR,
                        "验证码生成失败：服务器未安装字体。请在 ECS 上安装 fontconfig 与字体包");
            }
            throw new BizException(ErrorCode.CAPTCHA_GENERATE_ERROR, "验证码生成失败: " + hint);
        }

        for (int i = 0; i < 50; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.fillRect(random.nextInt(imageWidth), random.nextInt(IMAGE_HEIGHT), 1, 1);
        }
        g.dispose();
        return image;
    }
}
