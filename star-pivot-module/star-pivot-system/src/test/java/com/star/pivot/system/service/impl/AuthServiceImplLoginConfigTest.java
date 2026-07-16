package com.star.pivot.system.service.impl;

import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.system.domain.bo.LoginRequest;
import com.star.pivot.system.mapper.UserRoleMapper;
import com.star.pivot.system.service.interfaces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplLoginConfigTest {

    @Mock
    private TokenService tokenService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private SysUserService userService;
    @Mock
    private CaptchaService captchaService;
    @Mock
    private SysLogininforService sysLogininforService;
    @Mock
    private LoginRateLimitService rateLimitService;
    @Mock
    private AccountLockService accountLockService;
    @Mock
    private UserRoleMapper userRoleMapper;
    @Mock
    private ISysConfigService sysConfigService;
    @Mock
    private PasswordPolicyService passwordPolicyService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.100");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void login_shouldRejectBlockedIp() {
        when(sysConfigService.getLoginBlackIpList()).thenReturn("192.168.1.100");

        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        BizException ex = assertThrows(BizException.class, () -> authService.login(request));
        assertEquals(ErrorCode.FORBIDDEN.getCode(), ex.getErrorCode().getCode());
        verify(accountLockService, never()).checkAccountLocked(anyString());
    }

    @Test
    void login_shouldSkipCaptchaWhenDisabled() {
        when(sysConfigService.getLoginBlackIpList()).thenReturn("");
        when(sysConfigService.isCaptchaEnabled()).thenReturn(false);

        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        assertThrows(Exception.class, () -> authService.login(request));

        verify(captchaService, never()).validateAndConsumeCaptchaProof(anyString(), anyString());
        verify(accountLockService).checkAccountLocked("admin");
    }
}
