package com.star.pivot.system.service.impl;

import com.star.pivot.framework.cache.RedisCache;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.system.constants.CaptchaTypes;
import com.star.pivot.system.domain.CaptchaState;
import com.star.pivot.system.domain.bo.CaptchaIssueResponse;
import com.star.pivot.system.domain.bo.CaptchaVerifyRequest;
import com.star.pivot.system.service.captcha.CaptchaProvider;
import com.star.pivot.system.service.captcha.ImageCaptchaProvider;
import com.star.pivot.system.service.interfaces.ISysConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CaptchaServiceImplTest {

    @Mock
    private RedisCache redisCache;
    @Mock
    private ISysConfigService sysConfigService;

    private CaptchaServiceImpl captchaService;
    private ImageCaptchaProvider imageCaptchaProvider;

    @BeforeEach
    void setUp() {
        when(sysConfigService.getCaptchaLength()).thenReturn(4);
        when(sysConfigService.getCaptchaExpireSeconds()).thenReturn(180);
        when(sysConfigService.getCaptchaType()).thenReturn(CaptchaTypes.IMAGE);
        imageCaptchaProvider = new ImageCaptchaProvider(redisCache, sysConfigService);
        captchaService = new CaptchaServiceImpl(redisCache, sysConfigService, List.of(imageCaptchaProvider));
    }

    @Test
    void testGenerateCaptcha_ReturnsValidResponse() {
        CaptchaIssueResponse response = captchaService.generateCaptcha("login");

        assertNotNull(response);
        assertEquals(CaptchaTypes.IMAGE, response.getCaptchaType());
        assertNotNull(response.getCaptchaToken());
        assertNotNull(response.getCaptchaImage());
        assertTrue(response.getCaptchaImage().startsWith("data:image/jpeg;base64,"));

        verify(redisCache, times(1)).setCacheObject(
                anyString(),
                any(CaptchaState.class),
                eq(180L),
                eq(TimeUnit.SECONDS)
        );
    }

    @Test
    void testGenerateCaptcha_GeneratesUniqueTokens() {
        CaptchaIssueResponse response1 = captchaService.generateCaptcha("login");
        CaptchaIssueResponse response2 = captchaService.generateCaptcha("login");
        assertNotEquals(response1.getCaptchaToken(), response2.getCaptchaToken());
    }

    @Test
    void testVerifyCaptcha_Success_WhenCodeMatches() {
        CaptchaState state = createImageState("abcd", "login");
        when(redisCache.getCacheObject(anyString())).thenReturn(state);

        CaptchaVerifyRequest request = new CaptchaVerifyRequest();
        request.setCaptchaToken("token");
        request.setCode("ABCD");
        request.setScene("login");

        var response = assertDoesNotThrow(() -> captchaService.verifyCaptcha(request));
        assertNotNull(response.getCaptchaProof());
        verify(redisCache).deleteObject(startsWith("captcha:token:"));
        verify(redisCache).setCacheObject(startsWith("captcha:proof:"), eq("login"), anyLong(), eq(TimeUnit.SECONDS));
    }

    @Test
    void testVerifyCaptcha_Fails_WhenCodeMismatch() {
        CaptchaState state = createImageState("abcd", "login");
        when(redisCache.getCacheObject(anyString())).thenReturn(state);

        CaptchaVerifyRequest request = new CaptchaVerifyRequest();
        request.setCaptchaToken("token");
        request.setCode("xxxx");

        BizException ex = assertThrows(BizException.class, () -> captchaService.verifyCaptcha(request));
        assertEquals(ErrorCode.CAPTCHA_ERROR.getCode(), ex.getCode());
    }

    @Test
    void testVerifyCaptcha_Fails_WhenExpired() {
        when(redisCache.getCacheObject(anyString())).thenReturn(null);
        CaptchaVerifyRequest request = new CaptchaVerifyRequest();
        request.setCaptchaToken("expired-token");
        request.setCode("abcd");

        BizException ex = assertThrows(BizException.class, () -> captchaService.verifyCaptcha(request));
        assertEquals(ErrorCode.CAPTCHA_EXPIRED.getCode(), ex.getCode());
    }

    @Test
    void testVerifyCaptcha_Fails_WhenTooManyAttempts() {
        CaptchaState state = createImageState("abcd", "login");
        state.setAttempts(5);
        when(redisCache.getCacheObject(anyString())).thenReturn(state);

        CaptchaVerifyRequest request = new CaptchaVerifyRequest();
        request.setCaptchaToken("token");
        request.setCode("abcd");

        BizException ex = assertThrows(BizException.class, () -> captchaService.verifyCaptcha(request));
        assertEquals(ErrorCode.CAPTCHA_TOO_MANY_ATTEMPTS.getCode(), ex.getCode());
    }

    @Test
    void testValidateAndConsumeCaptchaProof_Success() {
        String proof = "proof-1";
        when(redisCache.getCacheObject("captcha:proof:" + proof)).thenReturn("login");
        assertTrue(captchaService.validateAndConsumeCaptchaProof(proof, "login"));
        verify(redisCache).deleteObject("captcha:proof:" + proof);
    }

    @Test
    void testValidateAndConsumeCaptchaProof_Fails_WhenSceneMismatch() {
        String proof = "proof-2";
        when(redisCache.getCacheObject("captcha:proof:" + proof)).thenReturn("register");
        assertFalse(captchaService.validateAndConsumeCaptchaProof(proof, "login"));
    }

    @Test
    void testValidateAndConsumeCaptchaProof_Fails_WhenNullProof() {
        assertFalse(captchaService.validateAndConsumeCaptchaProof(null, "login"));
    }

    @Test
    void testValidateAndConsumeCaptchaProof_Fails_WhenInvalidProof() {
        when(redisCache.getCacheObject(anyString())).thenReturn(null);
        assertFalse(captchaService.validateAndConsumeCaptchaProof("invalid-proof", "login"));
    }

    @Test
    void testSliderProvider_MatchesWithinTolerance() {
        CaptchaProvider slider = mock(CaptchaProvider.class);
        when(slider.getType()).thenReturn(CaptchaTypes.SLIDER);
        when(sysConfigService.getCaptchaType()).thenReturn(CaptchaTypes.SLIDER);

        CaptchaIssueResponse issued = new CaptchaIssueResponse();
        issued.setCaptchaType(CaptchaTypes.SLIDER);
        issued.setCaptchaToken("slider-token");
        when(slider.generate("login")).thenReturn(issued);

        CaptchaServiceImpl service = new CaptchaServiceImpl(
                redisCache, sysConfigService, List.of(imageCaptchaProvider, slider));
        CaptchaIssueResponse response = service.generateCaptcha("login");
        assertEquals(CaptchaTypes.SLIDER, response.getCaptchaType());
        verify(slider).generate("login");
    }

    private CaptchaState createImageState(String code, String scene) {
        CaptchaState state = new CaptchaState();
        String salt = "salt";
        state.setSalt(salt);
        state.setCodeHash(com.star.pivot.system.service.captcha.CaptchaSupport.hashWithSalt(code.toLowerCase(), salt));
        state.setAttempts(0);
        state.setMaxAttempts(5);
        state.setScene(scene);
        state.setCaptchaType(CaptchaTypes.IMAGE);
        return state;
    }
}
