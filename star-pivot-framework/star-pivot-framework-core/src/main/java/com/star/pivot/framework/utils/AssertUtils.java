package com.star.pivot.framework.utils;

import com.star.pivot.framework.exception.BusinessException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.ServiceException;

import java.util.Collection;
import java.util.Objects;

public final class AssertUtils {

    private AssertUtils() {
    }

    public static void isTrue(boolean expression, ErrorCode errorCode) {
        if (!expression) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isTrue(boolean expression, ErrorCode errorCode, String message) {
        if (!expression) {
            throw new BusinessException(errorCode, message);
        }
    }

    public static void isFalse(boolean expression, ErrorCode errorCode) {
        if (expression) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isFalse(boolean expression, ErrorCode errorCode, String message) {
        if (expression) {
            throw new BusinessException(errorCode, message);
        }
    }

    public static void notNull(Object object, ErrorCode errorCode) {
        if (Objects.isNull(object)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notNull(Object object, ErrorCode errorCode, String message) {
        if (Objects.isNull(object)) {
            throw new BusinessException(errorCode, message);
        }
    }

    public static void isNull(Object object, ErrorCode errorCode) {
        if (Objects.nonNull(object)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isNull(Object object, ErrorCode errorCode, String message) {
        if (Objects.nonNull(object)) {
            throw new BusinessException(errorCode, message);
        }
    }

    public static void notEmpty(String str, ErrorCode errorCode) {
        if (str == null || str.isEmpty()) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notEmpty(String str, ErrorCode errorCode, String message) {
        if (str == null || str.isEmpty()) {
            throw new BusinessException(errorCode, message);
        }
    }

    public static void notEmpty(Collection<?> collection, ErrorCode errorCode) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notEmpty(Collection<?> collection, ErrorCode errorCode, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(errorCode, message);
        }
    }

    public static void equals(Object obj1, Object obj2, ErrorCode errorCode) {
        if (!Objects.equals(obj1, obj2)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void equals(Object obj1, Object obj2, ErrorCode errorCode, String message) {
        if (!Objects.equals(obj1, obj2)) {
            throw new BusinessException(errorCode, message);
        }
    }

    public static void notEquals(Object obj1, Object obj2, ErrorCode errorCode) {
        if (Objects.equals(obj1, obj2)) {
            throw new BusinessException(errorCode);
        }
    }

    public static void notEquals(Object obj1, Object obj2, ErrorCode errorCode, String message) {
        if (Objects.equals(obj1, obj2)) {
            throw new BusinessException(errorCode, message);
        }
    }

    public static void throwBusiness(ErrorCode errorCode) {
        throw new BusinessException(errorCode);
    }

    public static void throwBusiness(ErrorCode errorCode, String message) {
        throw new BusinessException(errorCode, message);
    }

    public static void throwService(ErrorCode errorCode) {
        throw new ServiceException(errorCode);
    }

    public static void throwService(ErrorCode errorCode, String message) {
        throw new ServiceException(errorCode, message);
    }
}
