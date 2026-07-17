package com.star.pivot.framework.utils;

import com.star.pivot.framework.exception.ErrorCode;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import java.util.Locale;

/**
 * 国际化消息工具（依赖 MessageSource，启动时由 autoconfigure 注入）
 */
public final class MessageUtils {

    private static MessageSource messageSource;

    private MessageUtils() {
    }

    public static void setMessageSource(MessageSource source) {
        messageSource = source;
    }

    public static String message(String code, Object... args) {
        return message(code, null, args);
    }

    public static String message(String code, String defaultMessage, Object... args) {
        if (!StringUtils.hasText(code) || messageSource == null) {
            return defaultMessage != null ? defaultMessage : code;
        }
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(code, args, defaultMessage != null ? defaultMessage : code, locale);
        } catch (Exception e) {
            return defaultMessage != null ? defaultMessage : code;
        }
    }

    /**
     * 按 ErrorCode 解析文案；无 MessageSource 时回退枚举默认中文
     */
    public static String error(ErrorCode errorCode) {
        if (errorCode == null) {
            return message("error.INTERNAL_ERROR", "系统内部错误");
        }
        return message("error." + errorCode.name(), errorCode.getMessage());
    }

    /**
     * 有自定义 detail 时优先 detail，否则按错误码国际化
     */
    public static String display(ErrorCode errorCode, String detailMessage) {
        if (StringUtils.hasText(detailMessage)) {
            return detailMessage;
        }
        return error(errorCode);
    }
}
