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

import java.util.concurrent.TimeUnit;

/**
 * 拖动条验证码（按住滑块拖到尽头）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DragCaptchaProvider implements CaptchaProvider {

    /** 与前端 login-captcha-drag 最大可拖动距离一致（像素） */
    public static final int TRACK_MAX_X = 280;

    private static final int MAX_ATTEMPTS = 5;
    private static final int TOLERANCE = 10;

    private final RedisCache redisCache;
    private final ISysConfigService sysConfigService;

    @Override
    public String getType() {
        return CaptchaTypes.DRAG;
    }

    @Override
    public CaptchaIssueResponse generate(String scene) {
        String captchaToken = CaptchaSupport.randomToken();
        CaptchaState state = new CaptchaState();
        state.setAttempts(0);
        state.setMaxAttempts(MAX_ATTEMPTS);
        state.setScene(scene);
        state.setCaptchaType(CaptchaTypes.DRAG);
        state.setTargetX(TRACK_MAX_X);

        String key = CaptchaSupport.tokenKey(captchaToken);
        int expireSeconds = sysConfigService.getCaptchaExpireSeconds();
        try {
            redisCache.setCacheObject(key, state, expireSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("存储拖动验证码失败，key={}", key, e);
            throw new BizException(ErrorCode.CAPTCHA_GENERATE_ERROR, "验证码生成失败，请检查Redis连接: " + e.getMessage());
        }

        CaptchaIssueResponse response = new CaptchaIssueResponse();
        response.setCaptchaType(CaptchaTypes.DRAG);
        response.setCaptchaToken(captchaToken);
        return response;
    }

    @Override
    public boolean matches(CaptchaVerifyRequest request, CaptchaState state) {
        if (request == null || request.getSliderX() == null || state.getTargetX() == null) {
            return false;
        }
        int x = request.getSliderX();
        // 必须接近轨道尽头，且不允许明显不足
        return x >= state.getTargetX() - TOLERANCE && x <= state.getTargetX() + TOLERANCE;
    }
}
