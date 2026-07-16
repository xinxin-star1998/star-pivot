package com.star.pivot.framework.demo;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 演示模式工具：识别演示角色，并判断 HTTP 请求是否为只读（允许查询类 POST）。
 */
public final class DemoModeUtils {

    public static final String DEMO_ROLE_KEY = "demo";
    public static final String LEGACY_DEMO_ROLE_KEY = "yanshi";
    public static final String DEMO_DENIED_MESSAGE = "演示模式，不允许操作";

    private static final Set<String> DEMO_ROLE_KEYS = Set.of(DEMO_ROLE_KEY, LEGACY_DEMO_ROLE_KEY);

    private static final Pattern READ_ONLY_POST_PATH = Pattern.compile(
            ".*/(list|pageList|allocatedList|unallocatedList|recycle/list|log/list)(/.*)?$",
            Pattern.CASE_INSENSITIVE
    );

    private DemoModeUtils() {
    }

    public static boolean isDemoRoleKey(String roleKey) {
        return roleKey != null && DEMO_ROLE_KEYS.contains(roleKey);
    }

    /**
     * 演示账号允许的 HTTP 请求（其余写操作一律拒绝）。
     */
    public static boolean isReadOnlyHttpRequest(String method, String requestUri, String contextPath) {
        if (method == null || requestUri == null) {
            return false;
        }
        String normalizedMethod = method.toUpperCase(Locale.ROOT);
        if ("GET".equals(normalizedMethod) || "HEAD".equals(normalizedMethod) || "OPTIONS".equals(normalizedMethod)) {
            return true;
        }

        String path = stripContextPath(requestUri, contextPath);
        if (path.startsWith("/auth/refresh") || path.endsWith("/logout")) {
            return true;
        }

        if (isAiChatDemoAllowed(normalizedMethod, path)) {
            return true;
        }

        if ("POST".equals(normalizedMethod)) {
            if (READ_ONLY_POST_PATH.matcher(path).matches()) {
                return true;
            }
            // 预览/读取类接口
            return path.contains("/presigned-urls")
                    || path.contains("/captcha/verify");
        }

        return false;
    }

    /**
     * 演示账号允许使用 AI 对话（发消息、会话 CRUD），便于展示核心能力。
     */
    private static boolean isAiChatDemoAllowed(String method, String path) {
        if (!path.startsWith("/ai/chat/")) {
            return false;
        }
        return switch (method) {
            case "POST" -> path.equals("/ai/chat/send")
                    || path.equals("/ai/chat/stream")
                    || path.equals("/ai/chat/sessions");
            case "PUT" -> path.equals("/ai/chat/sessions/rename");
            case "DELETE" -> path.equals("/ai/chat/sessions")
                    || path.equals("/ai/chat/history");
            default -> false;
        };
    }

    private static String stripContextPath(String requestUri, String contextPath) {
        if (contextPath != null && !contextPath.isEmpty() && requestUri.startsWith(contextPath)) {
            String relative = requestUri.substring(contextPath.length());
            return relative.isEmpty() ? "/" : relative;
        }
        return requestUri;
    }
}
