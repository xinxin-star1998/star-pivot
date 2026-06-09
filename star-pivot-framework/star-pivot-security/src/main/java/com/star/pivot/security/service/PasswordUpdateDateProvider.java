package com.star.pivot.security.service;

import java.time.LocalDateTime;

/**
 * 密码更新日期提供者接口
 * 由业务模块（如 system）实现，避免 security 模块直接依赖业务层
 */
public interface PasswordUpdateDateProvider {

    /**
     * 根据用户名获取密码最后更新时间
     *
     * @param username 用户名
     * @return 密码最后更新时间，如果用户不存在或没有密码更新时间则返回 null
     */
    LocalDateTime getPasswordUpdateDate(String username);
}
