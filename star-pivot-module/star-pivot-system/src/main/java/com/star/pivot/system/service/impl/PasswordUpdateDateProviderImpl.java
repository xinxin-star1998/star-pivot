package com.star.pivot.system.service.impl;

import com.star.pivot.security.service.PasswordUpdateDateProvider;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.service.interfaces.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 密码更新日期提供者实现
 * 基于 SysUserService 查询用户的密码最后更新时间
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordUpdateDateProviderImpl implements PasswordUpdateDateProvider {

    private final SysUserService sysUserService;

    @Override
    public LocalDateTime getPasswordUpdateDate(String username) {
        SysUser user = sysUserService.getUserByUsername(username);
        if (user == null) {
            log.warn("用户不存在: {}", username);
            return null;
        }
        return user.getPwdUpdateDate();
    }
}
