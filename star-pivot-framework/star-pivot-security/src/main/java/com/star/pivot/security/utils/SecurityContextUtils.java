package com.star.pivot.security.utils;

import com.star.pivot.framework.security.LoginUserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Security 上下文工具类。
 *
 * <p>注意：该工具类位于 security 模块中，避免 system 模块持有 Spring Security 相关的工具类实现。
 * 为了防止模块循环依赖，这里只依赖 common 中的 {@link LoginUserInfo} 接口来识别当前登录用户。
 *
 * @author stardust
 * @since 2024-01-01
 */
public class SecurityContextUtils {

    /**
     * 获取当前登录用户（基于 common 的最小接口）。
     *
     * @return 当前登录用户信息，未登录返回 null
     */
    public static LoginUserInfo getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUserInfo) {
            return (LoginUserInfo) principal;
        }
        return null;
    }

    /**
     * 获取当前用户ID。
     *
     * @return 用户ID，未登录返回 null
     */
    public static Long getUserId() {
        LoginUserInfo loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUserId() : null;
    }

    /**
     * 获取当前用户名。
     *
     * <p>兼容处理：
     * <ul>
     *   <li>优先读取 {@link LoginUserInfo}</li>
     *   <li>其次兼容 {@link UserDetails}</li>
     *   <li>最后兼容 principal 为 String 的场景</li>
     * </ul>
     *
     * @return 用户名，未登录返回 null
     */
    public static String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUserInfo) {
            return ((LoginUserInfo) principal).getUsername();
        }
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        if (principal instanceof String) {
            return (String) principal;
        }
        return null;
    }
}

