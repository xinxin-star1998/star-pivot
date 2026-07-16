package com.star.pivot.system.service.impl;

import com.star.pivot.security.context.SecurityUtils;
import com.star.pivot.system.constants.PasswordModifyReason;
import com.star.pivot.system.domain.bo.PasswordModifyHint;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.service.interfaces.ISysConfigService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordPolicyServiceTest {

    @Mock
    private ISysConfigService sysConfigService;

    @InjectMocks
    private PasswordPolicyService passwordPolicyService;

    @Test
    void shouldRequireChangeWhenUsingInitPasswordAndPolicyEnabled() {
        when(sysConfigService.getInitPasswordModifyPolicy()).thenReturn(1);
        when(sysConfigService.getInitPassword()).thenReturn("123456");

        SysUser user = new SysUser();
        user.setPassword(SecurityUtils.encryptPassword("123456"));

        PasswordModifyHint hint = passwordPolicyService.evaluate(user);

        assertTrue(hint.isRequired());
        assertEquals(PasswordModifyReason.INIT_PASSWORD, hint.getReason());
    }

    @Test
    void shouldRequireChangeWhenPasswordExpired() {
        when(sysConfigService.getInitPasswordModifyPolicy()).thenReturn(0);
        when(sysConfigService.getPasswordValidateDays()).thenReturn(30);

        SysUser user = new SysUser();
        user.setPassword(SecurityUtils.encryptPassword("MySecretPwd1"));
        user.setPwdUpdateDate(LocalDateTime.now().minusDays(31));

        PasswordModifyHint hint = passwordPolicyService.evaluate(user);

        assertTrue(hint.isRequired());
        assertEquals(PasswordModifyReason.PASSWORD_EXPIRED, hint.getReason());
    }

    @Test
    void shouldNotRequireChangeWhenPoliciesDisabled() {
        when(sysConfigService.getInitPasswordModifyPolicy()).thenReturn(0);
        when(sysConfigService.getPasswordValidateDays()).thenReturn(0);

        SysUser user = new SysUser();
        user.setPassword(SecurityUtils.encryptPassword("123456"));

        PasswordModifyHint hint = passwordPolicyService.evaluate(user);

        assertFalse(hint.isRequired());
    }
}
