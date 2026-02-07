package com.star.pivot.common.utils;

import cn.hutool.core.util.EscapeUtil;

public final class XssUtils {

    private XssUtils() {}

    public static String escapeHtml(String input) {
        return input == null ? null : EscapeUtil.escapeHtml4(input);
    }

    public static String unescapeHtml(String input) {
        return input == null ? null : EscapeUtil.unescapeHtml4(input);
    }

    public static String stripHtml(String html) {
        return html == null ? null : html.replaceAll("<[^>]*>", "");
    }

    public static boolean containsXss(String input) {
        if (input == null || input.isEmpty()) return false;
        String lowerInput = input.toLowerCase();
        return lowerInput.contains("<script") || lowerInput.contains("javascript:")
                || lowerInput.contains("onerror=") || lowerInput.contains("onclick=")
                || lowerInput.contains("onload=") || lowerInput.contains("<iframe")
                || lowerInput.contains("<embed") || lowerInput.contains("<object");
    }

    public static String sanitizeInput(String input) {
        if (input == null) return null;
        return escapeHtml(stripHtml(input));
    }
}
