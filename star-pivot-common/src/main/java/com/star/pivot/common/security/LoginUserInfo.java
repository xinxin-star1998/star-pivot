package com.star.pivot.common.security;

/**
 * 登录用户信息最小接口（避免模块间循环依赖）。
 *
 * <p>说明：
 * <ul>
 *   <li>security 模块只依赖 common，因此只能面向 common 中的接口编程</li>
 *   <li>system 模块中的 LoginUser 实现该接口，以便在 SecurityContext 中可被识别</li>
 * </ul>
 *
 * @author stardust
 * @since 2026-01-21
 */
public interface LoginUserInfo {

    /**
     * 获取当前用户ID。
     *
     * @return 用户ID
     */
    Long getUserId();

    /**
     * 获取当前用户名。
     *
     * @return 用户名
     */
    String getUsername();
}

