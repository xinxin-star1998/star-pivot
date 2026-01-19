package com.star.pivot.system.service.impl;

import com.star.pivot.system.service.CaptchaService;
import com.star.pivot.system.utils.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
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
    private static final int EXPIRE_TIME = 300;

    @Override
    public BufferedImage generateCaptcha(String captchaId) {
        // 生成随机验证码
        String code = generateRandomCode();
        
        // 将验证码存储到Redis
        redisCache.setCacheObject("captcha:" + captchaId, code, EXPIRE_TIME, TimeUnit.SECONDS);
        
        // 生成验证码图片
        return createImage(code);
    }

    @Override
    public boolean validateCaptcha(String captchaId, String code) {
        // 从Redis获取验证码
        String storedCode = redisCache.getCacheObject("captcha:" + captchaId);
        
        // 验证验证码
        if (storedCode == null) {
            return false;
        }
        
        boolean isValid = storedCode.equalsIgnoreCase(code);
        
        // 验证后删除验证码
        redisCache.deleteObject("captcha:" + captchaId);
        
        return isValid;
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
}