package com.star.pivot.common.utils;

import cn.hutool.core.util.EscapeUtil;

/**
 * XSS 防护工具类
 *
 * <p>提供输入验证与转义功能，防止跨站脚本攻击（XSS）
 *
 * <p>使用场景：
 * <ul>
 *   <li>用户输入数据存储前进行转义</li>
 *   <li>富文本编辑器内容输出前进行清理</li>
 *   <li>URL参数、表单数据等用户可控输入的处理</li>
 * </ul>
 *
 * <p>注意事项：
 * <ul>
 *   <li>HTML转义会转义所有HTML特殊字符，适用于纯文本场景</li>
 *   <li>富文本内容需要更复杂的处理（如使用OWASP Java HTML Sanitizer）</li>
 *   <li>前端使用 v-html 时，后端应确保数据已转义或使用白名单过滤</li>
 * </ul>
 *
 * @author xinxin
 * @since 2026-01-25
 */
public final class XssUtils {

    private XssUtils() {
        // 工具类，禁止实例化
    }

    /**
     * HTML转义：将HTML特殊字符转义为实体字符
     *
     * <p>转义规则：
     * <ul>
     *   <li>&lt; → &amp;lt;</li>
     *   <li>&gt; → &amp;gt;</li>
     *   <li>&amp; → &amp;amp;</li>
     *   <li>&quot; → &amp;quot;</li>
     *   <li>&#39; → &amp;#39;</li>
     * </ul>
     *
     * <p>使用场景：用户输入的纯文本内容（如用户名、备注、描述等）
     *
     * @param input 待转义的字符串
     * @return 转义后的字符串，如果输入为null则返回null
     */
    public static String escapeHtml(String input) {
        if (input == null) {
            return null;
        }
        return EscapeUtil.escapeHtml4(input);
    }

    /**
     * HTML反转义：将HTML实体字符还原为原始字符
     *
     * <p>使用场景：从数据库读取已转义的数据，需要还原显示时
     *
     * @param input 待反转义的字符串
     * @return 反转义后的字符串，如果输入为null则返回null
     */
    public static String unescapeHtml(String input) {
        if (input == null) {
            return null;
        }
        return EscapeUtil.unescapeHtml4(input);
    }

    /**
     * 清理HTML标签：移除所有HTML标签，只保留纯文本内容
     *
     * <p>使用场景：从富文本内容中提取纯文本（如摘要、预览等）
     *
     * @param html HTML内容
     * @return 纯文本内容，如果输入为null则返回null
     */
    public static String stripHtml(String html) {
        if (html == null) {
            return null;
        }
        // 移除所有HTML标签
        return html.replaceAll("<[^>]*>", "");
    }

    /**
     * 验证输入是否包含潜在的XSS攻击代码
     *
     * <p>检测常见的XSS攻击模式：
     * <ul>
     *   <li>script标签</li>
     *   <li>javascript:协议</li>
     *   <li>onerror、onclick等事件处理器</li>
     *   <li>iframe、embed等危险标签</li>
     * </ul>
     *
     * @param input 待验证的字符串
     * @return 如果包含潜在XSS代码返回true，否则返回false
     */
    public static boolean containsXss(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        String lowerInput = input.toLowerCase();
        // 检测常见的XSS攻击模式
        return lowerInput.contains("<script") ||
                lowerInput.contains("javascript:") ||
                lowerInput.contains("onerror=") ||
                lowerInput.contains("onclick=") ||
                lowerInput.contains("onload=") ||
                lowerInput.contains("<iframe") ||
                lowerInput.contains("<embed") ||
                lowerInput.contains("<object");
    }

    /**
     * 清理并转义输入：先清理HTML标签，再进行HTML转义
     *
     * <p>使用场景：用户输入的文本内容，需要完全去除HTML标签并转义特殊字符
     *
     * @param input 待处理的字符串
     * @return 清理并转义后的字符串，如果输入为null则返回null
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // 先移除HTML标签，再转义特殊字符
        String stripped = stripHtml(input);
        return escapeHtml(stripped);
    }
}
