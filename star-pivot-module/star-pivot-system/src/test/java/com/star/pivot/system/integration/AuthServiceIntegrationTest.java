package com.star.pivot.system.integration;

import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.system.domain.bo.RegisterRequest;
import com.star.pivot.system.domain.bo.RegisterResponse;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.domain.entity.UserRole;
import com.star.pivot.system.mapper.UserRoleMapper;
import com.star.pivot.system.service.impl.AuthServiceImpl;
import com.star.pivot.system.service.interfaces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 认证服务集成测试（注册流程与配置化默认角色）
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceIntegrationTest {

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

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "defaultRegisterRoleId", 10L);
    }

    @Test
    void register_shouldRejectWhenRegisterDisabled() {
        when(sysConfigService.isRegisterUserEnabled()).thenReturn(false);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");

        BizException ex = assertThrows(BizException.class, () -> authService.register(request));
        assertEquals(ErrorCode.FORBIDDEN.getCode(), ex.getErrorCode().getCode());
        verify(rateLimitService, never()).checkRegisterIpRateLimit(anyString());
    }

    @Test
    void register_shouldApplyRateLimitAndConfiguredDefaultRole() {
        when(sysConfigService.isRegisterUserEnabled()).thenReturn(true);
        when(userService.getUserByUsername("newuser")).thenReturn(null);
        doAnswer(invocation -> {
            SysUser user = invocation.getArgument(0);
            user.setUserId(100L);
            return true;
        }).when(userService).save(any(SysUser.class));

        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");

        RegisterResponse response = authService.register(request);

        assertEquals(100L, response.getUserId());
        assertEquals("newuser", response.getUsername());
        verify(rateLimitService).checkRegisterIpRateLimit(anyString());
        verify(rateLimitService).checkRegisterIpUsernameRateLimit(anyString(), anyString());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UserRole>> roleCaptor = ArgumentCaptor.forClass(List.class);
        verify(userRoleMapper).insertBatchUserRoles(roleCaptor.capture());
        assertEquals(10L, roleCaptor.getValue().get(0).getRoleId());
        assertEquals(100L, roleCaptor.getValue().get(0).getUserId());
    }

    @Test
    void register_shouldRejectDuplicateUsername() {
        when(sysConfigService.isRegisterUserEnabled()).thenReturn(true);
        SysUser exists = new SysUser();
        exists.setUserId(1L);
        when(userService.getUserByUsername("exists")).thenReturn(exists);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("exists");
        request.setPassword("password123");

        BizException ex = assertThrows(BizException.class, () -> authService.register(request));
        assertEquals(ErrorCode.USER_USERNAME_EXISTS.getCode(), ex.getErrorCode().getCode());
        verify(userRoleMapper, never()).insertBatchUserRoles(anyList());
    }
}
