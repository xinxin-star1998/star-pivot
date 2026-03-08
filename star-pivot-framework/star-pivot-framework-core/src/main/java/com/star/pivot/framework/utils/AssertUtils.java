package com.star.pivot.framework.utils;

import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;

import java.util.Collection;
import java.util.Objects;

/**
 * 断言工具类
 * <p>
 * 提供常用的参数校验方法，校验失败时抛出 {@link BizException}
 */
public final class AssertUtils {

    private AssertUtils() {
    }

    public static void isTrue(boolean expression, ErrorCode errorCode) {
        if (!expression) {
            throw new BizException(errorCode);
        }
    }

    public static void isTrue(boolean expression, ErrorCode errorCode, String message) {
        if (!expression) {
            throw new BizException(errorCode, message);
        }
    }

    public static void isFalse(boolean expression, ErrorCode errorCode) {
        if (expression) {
            throw new BizException(errorCode);
        }
    }

    public static void isFalse(boolean expression, ErrorCode errorCode, String message) {
        if (expression) {
            throw new BizException(errorCode, message);
        }
    }

    public static void notNull(Object object, ErrorCode errorCode) {
        if (Objects.isNull(object)) {
            throw new BizException(errorCode);
        }
    }

    public static void notNull(Object object, ErrorCode errorCode, String message) {
        if (Objects.isNull(object)) {
            throw new BizException(errorCode, message);
        }
    }

    public static void isNull(Object object, ErrorCode errorCode) {
        if (Objects.nonNull(object)) {
            throw new BizException(errorCode);
        }
    }

    public static void isNull(Object object, ErrorCode errorCode, String message) {
        if (Objects.nonNull(object)) {
            throw new BizException(errorCode, message);
        }
    }

    public static void notEmpty(String str, ErrorCode errorCode) {
        if (str == null || str.isEmpty()) {
            throw new BizException(errorCode);
        }
    }

    public static void notEmpty(String str, ErrorCode errorCode, String message) {
        if (str == null || str.isEmpty()) {
            throw new BizException(errorCode, message);
        }
    }

    public static void notEmpty(Collection<?> collection, ErrorCode errorCode) {
        if (collection == null || collection.isEmpty()) {
            throw new BizException(errorCode);
        }
    }

    public static void notEmpty(Collection<?> collection, ErrorCode errorCode, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new BizException(errorCode, message);
        }
    }

    public static void equals(Object obj1, Object obj2, ErrorCode errorCode) {
        if (!Objects.equals(obj1, obj2)) {
            throw new BizException(errorCode);
        }
    }

    public static void equals(Object obj1, Object obj2, ErrorCode errorCode, String message) {
        if (!Objects.equals(obj1, obj2)) {
            throw new BizException(errorCode, message);
        }
    }

    public static void notEquals(Object obj1, Object obj2, ErrorCode errorCode) {
        if (Objects.equals(obj1, obj2)) {
            throw new BizException(errorCode);
        }
    }

    public static void notEquals(Object obj1, Object obj2, ErrorCode errorCode, String message) {
        if (Objects.equals(obj1, obj2)) {
            throw new BizException(errorCode, message);
        }
    }

    /**
     * 直接抛出业务异常
     */
    public static void throwBiz(ErrorCode errorCode) {
        throw new BizException(errorCode);
    }

    /**
     * 直接抛出业务异常（带自定义消息）
     */
    public static void throwBiz(ErrorCode errorCode, String message) {
        throw new BizException(errorCode, message);
    }

    // ==================== 废弃方法（向后兼容） ====================

    /**
     * @deprecated 请使用 {@link #throwBiz(ErrorCode)} 替代
     */
    @Deprecated(since = "1.0", forRemoval = true)
    public static void throwBusiness(ErrorCode errorCode) {
        throw new BizException(errorCode);
    }

    /**
     * @deprecated 请使用 {@link #throwBiz(ErrorCode, String)} 替代
     */
    @Deprecated(since = "1.0", forRemoval = true)
    public static void throwBusiness(ErrorCode errorCode, String message) {
        throw new BizException(errorCode, message);
    }

    /**
     * @deprecated 请使用 {@link #throwBiz(ErrorCode)} 替代
     */
    @Deprecated(since = "1.0", forRemoval = true)
    public static void throwService(ErrorCode errorCode) {
        throw new BizException(errorCode);
    }

    /**
     * @deprecated 请使用 {@link #throwBiz(ErrorCode, String)} 替代
     */
    @Deprecated(since = "1.0", forRemoval = true)
    public static void throwService(ErrorCode errorCode, String message) {
        throw new BizException(errorCode, message);
    }
}
