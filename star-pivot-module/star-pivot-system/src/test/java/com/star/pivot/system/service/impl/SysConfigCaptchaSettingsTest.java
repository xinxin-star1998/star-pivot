package com.star.pivot.system.service.impl;

import com.star.pivot.framework.cache.RedisCache;
import com.star.pivot.system.constants.SysConfigKeys;
import com.star.pivot.system.domain.entity.SysConfig;
import com.star.pivot.system.mapper.SysConfigMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SysConfigCaptchaSettingsTest {

    @Mock
    private SysConfigMapper baseMapper;
    @Mock
    private RedisCache redisCache;

    @InjectMocks
    private SysConfigServiceImpl sysConfigService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sysConfigService, "baseMapper", baseMapper);
    }

    @Test
    void getCaptchaLength_shouldUseDefaultWhenMissing() {
        when(baseMapper.selectByConfigKey(SysConfigKeys.ACCOUNT_CAPTCHA_LENGTH)).thenReturn(null);
        assertEquals(4, sysConfigService.getCaptchaLength());
    }

    @Test
    void getCaptchaLength_shouldClampHighValue() {
        when(baseMapper.selectByConfigKey(SysConfigKeys.ACCOUNT_CAPTCHA_LENGTH)).thenReturn(config("10"));
        assertEquals(6, sysConfigService.getCaptchaLength());
    }

    @Test
    void getCaptchaExpireSeconds_shouldClampLowValue() {
        when(baseMapper.selectByConfigKey(SysConfigKeys.ACCOUNT_CAPTCHA_EXPIRE_SECONDS)).thenReturn(config("30"));
        assertEquals(60, sysConfigService.getCaptchaExpireSeconds());
    }

    @Test
    void getCaptchaExpireSeconds_shouldUseConfiguredValue() {
        when(baseMapper.selectByConfigKey(SysConfigKeys.ACCOUNT_CAPTCHA_EXPIRE_SECONDS)).thenReturn(config("300"));
        assertEquals(300, sysConfigService.getCaptchaExpireSeconds());
    }

    private static SysConfig config(String value) {
        SysConfig config = new SysConfig();
        config.setConfigValue(value);
        return config;
    }
}
