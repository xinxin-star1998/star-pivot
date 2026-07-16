package com.star.pivot.system.service.impl;

import com.star.pivot.security.context.SecurityUtils;
import com.star.pivot.system.constants.PasswordModifyReason;
import com.star.pivot.system.domain.bo.PasswordModifyHint;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.service.interfaces.ISysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 密码策略校验（读取 sys_config）
 */
@Service
@RequiredArgsConstructor
public class PasswordPolicyService {

    private final ISysConfigService sysConfigService;

    /**
     * 评估登录用户是否需要修改密码
     *
     * @param user        已认证用户
     * @param rawPassword 本次登录使用的明文密码
     */
    public PasswordModifyHint evaluate(SysUser user) {
        if (user == null) {
            return PasswordModifyHint.none();
        }

        PasswordModifyHint initPasswordHint = evaluateInitPasswordModify(user);
        if (initPasswordHint.isRequired()) {
            return initPasswordHint;
        }

        return evaluatePasswordExpire(user);
    }

    private PasswordModifyHint evaluateInitPasswordModify(SysUser user) {
        if (sysConfigService.getInitPasswordModifyPolicy() != 1) {
            return PasswordModifyHint.none();
        }
        if (!StringUtils.hasText(user.getPassword())) {
            return PasswordModifyHint.none();
        }
        String initPassword = sysConfigService.getInitPassword();
        if (SecurityUtils.matchesPassword(initPassword, user.getPassword())) {
            return PasswordModifyHint.required(PasswordModifyReason.INIT_PASSWORD);
        }
        return PasswordModifyHint.none();
    }

    private PasswordModifyHint evaluatePasswordExpire(SysUser user) {
        int validateDays = sysConfigService.getPasswordValidateDays();
        if (validateDays <= 0 || user.getPwdUpdateDate() == null) {
            return PasswordModifyHint.none();
        }
        LocalDateTime expireAt = user.getPwdUpdateDate().plusDays(validateDays);
        if (expireAt.isBefore(LocalDateTime.now())) {
            return PasswordModifyHint.required(PasswordModifyReason.PASSWORD_EXPIRED);
        }
        return PasswordModifyHint.none();
    }
}
