package com.star.pivot.framework.security;

import java.io.Serializable;

/**
 * 登录用户信息最小接口（避免模块间循环依赖）。
 * security 模块面向此接口编程；system 模块中的 LoginUser 实现该接口。
 */
public interface LoginUserInfo extends Serializable {

    Long getUserId();

    String getUsername();

    /**
     * 是否为演示账号（只读浏览，写操作由演示模式拦截）。
     */
    default boolean isDemoMode() {
        return false;
    }
}
