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
class DragCaptchaProviderTest {

    @Mock
    private RedisCache redisCache;
    @Mock
    private ISysConfigService sysConfigService;

    private DragCaptchaProvider provider;

    @BeforeEach
    void setUp() {
        when(sysConfigService.getCaptchaExpireSeconds()).thenReturn(180);
        provider = new DragCaptchaProvider(redisCache, sysConfigService);
    }

    @Test
    void generate_shouldStoreTrackEndTarget() {
        CaptchaIssueResponse response = provider.generate("login");

        assertEquals(CaptchaTypes.DRAG, response.getCaptchaType());
        assertNotNull(response.getCaptchaToken());
        assertNull(response.getCaptchaImage());
        assertNull(response.getBackgroundImage());

        ArgumentCaptor<CaptchaState> stateCaptor = ArgumentCaptor.forClass(CaptchaState.class);
        verify(redisCache).setCacheObject(anyString(), stateCaptor.capture(), eq(180L), eq(TimeUnit.SECONDS));
        CaptchaState state = stateCaptor.getValue();
        assertEquals(CaptchaTypes.DRAG, state.getCaptchaType());
        assertEquals(DragCaptchaProvider.TRACK_MAX_X, state.getTargetX());
    }

    @Test
    void matches_shouldRequireNearTrackEnd() {
        CaptchaState state = new CaptchaState();
        state.setTargetX(DragCaptchaProvider.TRACK_MAX_X);

        CaptchaVerifyRequest ok = new CaptchaVerifyRequest();
        ok.setSliderX(DragCaptchaProvider.TRACK_MAX_X);
        assertTrue(provider.matches(ok, state));

        CaptchaVerifyRequest near = new CaptchaVerifyRequest();
        near.setSliderX(DragCaptchaProvider.TRACK_MAX_X - 8);
        assertTrue(provider.matches(near, state));

        CaptchaVerifyRequest bad = new CaptchaVerifyRequest();
        bad.setSliderX(100);
        assertFalse(provider.matches(bad, state));
    }
}
