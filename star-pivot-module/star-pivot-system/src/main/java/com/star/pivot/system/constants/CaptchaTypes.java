package com.star.pivot.system.constants;

/**
 * 验证码类型（与 sys.account.captchaType 对应）
 */
public final class CaptchaTypes {

    private CaptchaTypes() {
    }

    /** 图形字符验证码 */
    public static final String IMAGE = "image";

    /** 滑块拼图验证码 */
    public static final String SLIDER = "slider";

    /** 拖动条验证码（按住滑块拖到尽头） */
    public static final String DRAG = "drag";

    /** 文字点选验证码 */
    public static final String CLICK = "click";

    public static boolean isSupported(String type) {
        if (type == null) {
            return false;
        }
        String normalized = type.trim().toLowerCase();
        return IMAGE.equals(normalized)
                || SLIDER.equals(normalized)
                || DRAG.equals(normalized)
                || CLICK.equals(normalized);
    }

    public static String normalize(String type) {
        if (type == null || type.isBlank()) {
            return IMAGE;
        }
        String normalized = type.trim().toLowerCase();
        return isSupported(normalized) ? normalized : IMAGE;
    }
}
