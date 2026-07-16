package com.star.pivot.system.service.captcha;

import com.star.pivot.framework.cache.RedisCache;
import com.star.pivot.system.constants.CaptchaTypes;
import com.star.pivot.system.domain.CaptchaState;
import com.star.pivot.system.domain.bo.CaptchaIssueResponse;
import com.star.pivot.system.domain.bo.CaptchaVerifyRequest;
import com.star.pivot.system.service.interfaces.ISysConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SliderCaptchaProviderTest {

    @Mock
    private RedisCache redisCache;
    @Mock
    private ISysConfigService sysConfigService;

    private SliderCaptchaProvider provider;

    @BeforeEach
    void setUp() {
        when(sysConfigService.getCaptchaExpireSeconds()).thenReturn(180);
        provider = new SliderCaptchaProvider(redisCache, sysConfigService);
    }

    @Test
    void generate_shouldReturnSliderPayloadAndStoreTargetX() {
        CaptchaIssueResponse response = provider.generate("login");

        assertEquals(CaptchaTypes.SLIDER, response.getCaptchaType());
        assertNotNull(response.getCaptchaToken());
        assertNotNull(response.getBackgroundImage());
        assertNotNull(response.getSliderImage());
        assertNotNull(response.getSliderY());
        assertTrue(response.getBackgroundImage().startsWith("data:image/jpeg;base64,"));
        assertTrue(response.getSliderImage().startsWith("data:image/png;base64,"));

        ArgumentCaptor<CaptchaState> stateCaptor = ArgumentCaptor.forClass(CaptchaState.class);
        verify(redisCache).setCacheObject(anyString(), stateCaptor.capture(), eq(180L), eq(TimeUnit.SECONDS));
        CaptchaState state = stateCaptor.getValue();
        assertEquals(CaptchaTypes.SLIDER, state.getCaptchaType());
        assertNotNull(state.getTargetX());
        assertTrue(state.getTargetX() > 0);
    }

    @Test
    void matches_shouldAllowTolerance() {
        CaptchaState state = new CaptchaState();
        state.setTargetX(120);

        CaptchaVerifyRequest ok = new CaptchaVerifyRequest();
        ok.setSliderX(124);
        assertTrue(provider.matches(ok, state));

        CaptchaVerifyRequest bad = new CaptchaVerifyRequest();
        bad.setSliderX(140);
        assertFalse(provider.matches(bad, state));
    }
}
