package com.star.pivot.system.service.impl;

import com.star.pivot.framework.cache.RedisCache;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.system.constants.CaptchaTypes;
import com.star.pivot.system.domain.CaptchaState;
import com.star.pivot.system.domain.bo.CaptchaIssueResponse;
import com.star.pivot.system.domain.bo.CaptchaVerifyRequest;
import com.star.pivot.system.domain.bo.CaptchaVerifyResponse;
import com.star.pivot.system.service.captcha.CaptchaProvider;
import com.star.pivot.system.service.captcha.CaptchaSupport;
import com.star.pivot.system.service.interfaces.CaptchaService;
import com.star.pivot.system.service.interfaces.ISysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 验证码 Facade：按 sys.account.captchaType 路由到具体 Provider，统一签发 captchaProof
 */
@Slf4j
@Service
public class CaptchaServiceImpl implements CaptchaService {

    private static final int PROOF_EXPIRE_SECONDS = 300;

    private final RedisCache redisCache;
    private final ISysConfigService sysConfigService;
    private final Map<String, CaptchaProvider> providers;

    public CaptchaServiceImpl(RedisCache redisCache,
                              ISysConfigService sysConfigService,
                              List<CaptchaProvider> providerList) {
        this.redisCache = redisCache;
        this.sysConfigService = sysConfigService;
        this.providers = providerList.stream()
                .collect(Collectors.toMap(CaptchaProvider::getType, Function.identity(), (a, b) -> a));
    }

    @Override
    public CaptchaIssueResponse generateCaptcha(String scene) {
        CaptchaProvider provider = resolveProvider(sysConfigService.getCaptchaType());
        return provider.generate(scene != null ? scene : "login");
    }

    @Override
    public CaptchaVerifyResponse verifyCaptcha(CaptchaVerifyRequest request) {
        if (request == null || !StringUtils.hasText(request.getCaptchaToken())) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "验证码参数不完整");
        }

        String key = CaptchaSupport.tokenKey(request.getCaptchaToken());
        CaptchaState state;
        try {
            state = redisCache.getCacheObject(key);
        } catch (Exception e) {
            log.error("从Redis获取验证码状态失败，key={}", key, e);
            throw new BizException(ErrorCode.REDIS_ERROR, "验证码校验失败，请检查Redis连接: " + e.getMessage());
        }

        if (state == null) {
            throw new BizException(ErrorCode.CAPTCHA_EXPIRED);
        }
        if (state.getAttempts() >= state.getMaxAttempts()) {
            redisCache.deleteObject(key);
            throw new BizException(ErrorCode.CAPTCHA_TOO_MANY_ATTEMPTS);
        }

        String type = CaptchaTypes.normalize(
                StringUtils.hasText(state.getCaptchaType()) ? state.getCaptchaType() : sysConfigService.getCaptchaType());
        CaptchaProvider provider = resolveProvider(type);
        boolean match = provider.matches(request, state);

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

        redisCache.deleteObject(key);

        String captchaProof = CaptchaSupport.randomToken();
        String proofKey = CaptchaSupport.proofKey(captchaProof);
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

        String key = CaptchaSupport.proofKey(captchaProof);
        String storedScene = redisCache.getCacheObject(key);
        if (storedScene == null) {
            return false;
        }

        redisCache.deleteObject(key);

        if (scene != null && !scene.isEmpty() && !scene.equals(storedScene)) {
            log.warn("验证码 proof 场景不匹配，期望: {} 实际: {}", scene, storedScene);
            return false;
        }
        return true;
    }

    private CaptchaProvider resolveProvider(String type) {
        String normalized = CaptchaTypes.normalize(type);
        CaptchaProvider provider = providers.get(normalized);
        if (provider == null) {
            provider = providers.get(CaptchaTypes.IMAGE);
        }
        if (provider == null) {
            throw new BizException(ErrorCode.CAPTCHA_GENERATE_ERROR, "未找到可用的验证码实现");
        }
        return provider;
    }
}
