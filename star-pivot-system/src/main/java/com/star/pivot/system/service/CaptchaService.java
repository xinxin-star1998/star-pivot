package com.star.pivot.system.service;

import java.awt.image.BufferedImage;

/**
 * 验证码服务接口
 */
public interface CaptchaService {

    /**
     * 生成验证码图片
     * @param captchaId 验证码ID
     * @return 验证码图片
     */
    BufferedImage generateCaptcha(String captchaId);

    /**
     * 验证验证码
     * @param captchaId 验证码ID
     * @param code 用户输入的验证码
     * @return 是否验证通过
     */
    boolean validateCaptcha(String captchaId, String code);
}