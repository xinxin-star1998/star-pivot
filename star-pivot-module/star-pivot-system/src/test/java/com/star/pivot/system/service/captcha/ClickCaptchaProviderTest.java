package com.star.pivot.system.service.captcha;

import com.star.pivot.framework.cache.RedisCache;
import com.star.pivot.system.constants.CaptchaTypes;
import com.star.pivot.system.domain.CaptchaState;
import com.star.pivot.system.domain.bo.CaptchaClickPoint;
import com.star.pivot.system.domain.bo.CaptchaVerifyRequest;
import com.star.pivot.system.service.interfaces.ISysConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ClickCaptchaProviderTest {

    @Mock
    private RedisCache redisCache;
    @Mock
    private ISysConfigService sysConfigService;

    private ClickCaptchaProvider clickProvider;

    @BeforeEach
    void setUp() {
        when(sysConfigService.getCaptchaExpireSeconds()).thenReturn(180);
        clickProvider = new ClickCaptchaProvider(redisCache, sysConfigService);
    }

    @Test
    void click_generateAndMatch() {
        var response = clickProvider.generate("login");
        assertEquals(CaptchaTypes.CLICK, response.getCaptchaType());
        assertNotNull(response.getBackgroundImage());
        assertNotNull(response.getClickTip());
        assertEquals(3, response.getClickWords().size());

        CaptchaState state = new CaptchaState();
        state.setClickTargets("100,80;200,90;260,100");

        CaptchaVerifyRequest ok = new CaptchaVerifyRequest();
        ok.setClickPoints(List.of(
                new CaptchaClickPoint(105, 82),
                new CaptchaClickPoint(195, 95),
                new CaptchaClickPoint(255, 98)
        ));
        assertTrue(clickProvider.matches(ok, state));

        CaptchaVerifyRequest bad = new CaptchaVerifyRequest();
        bad.setClickPoints(List.of(
                new CaptchaClickPoint(10, 10),
                new CaptchaClickPoint(20, 20),
                new CaptchaClickPoint(30, 30)
        ));
        assertFalse(clickProvider.matches(bad, state));
    }
}
