package com.star.pivot.system.utils;

import org.springframework.util.StringUtils;

/**
 * 登录 IP 黑名单匹配（与 sys.login.blackIPList 配置约定一致）
 */
public final class LoginIpBlacklistMatcher {

    private LoginIpBlacklistMatcher() {
    }

    /**
     * 判断 IP 是否命中黑名单
     *
     * @param ip              客户端 IP
     * @param blacklistConfig 黑名单配置，多个匹配项以 ; 分隔
     */
    public static boolean isBlocked(String ip, String blacklistConfig) {
        if (!StringUtils.hasText(ip) || !StringUtils.hasText(blacklistConfig)) {
            return false;
        }
        String[] patterns = blacklistConfig.split(";");
        for (String rawPattern : patterns) {
            String pattern = rawPattern.trim();
            if (matches(ip, pattern)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matches(String ip, String pattern) {
        if (!StringUtils.hasText(pattern)) {
            return false;
        }
        if ("*".equals(pattern)) {
            return true;
        }
        if (pattern.contains("*")) {
            String regex = pattern.replace(".", "\\.").replace("*", ".*");
            return ip.matches(regex);
        }
        if (pattern.endsWith(".")) {
            return ip.startsWith(pattern);
        }
        return ip.equals(pattern);
    }
}
