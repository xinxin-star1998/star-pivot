package com.star.pivot.framework.exception;

import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.utils.string.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;
import java.util.NoSuchElementException;

/**
 * 异常处理工具类
 */
@Slf4j
public class ExceptionHelper {

    /**
     * 执行可能抛出异常的操作
     */
    public static <T> T safeExecute(Supplier<T> supplier, String errorMessage) {
        try {
            return supplier.get();
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error(errorMessage, e);
            throw new BizException(ErrorCode.INTERNAL_ERROR, errorMessage);
        }
    }

    /**
     * 执行可能抛出异常的操作（无返回值）
     */
    public static void safeExecute(Runnable runnable, String errorMessage) {
        try {
            runnable.run();
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error(errorMessage, e);
            throw new BizException(ErrorCode.INTERNAL_ERROR, errorMessage);
        }
    }

    /**
     * 包装异常为BizException
     */
    public static <E extends Exception> BizException wrapException(E e, String message) {
        if (e instanceof BizException) {
            return (BizException) e;
        }

        String errorMsg = StringUtils.isNotBlank(message) ? message : e.getMessage();
        log.error("异常包装: {}", errorMsg, e);

        if (e instanceof IllegalArgumentException) {
            return new BizException(ErrorCode.CLIENT_ERROR, errorMsg);
        } else if (e instanceof NullPointerException) {
            return new BizException(ErrorCode.PARAM_NOT_NULL, "参数不能为空");
        } else if (e instanceof NoSuchElementException) {
            return new BizException(ErrorCode.NOT_FOUND, "请求的资源不存在");
        }

        return new BizException(ErrorCode.INTERNAL_ERROR, errorMsg);
    }

    /**
     * 创建标准异常响应
     */
    public static <T> Result<T> createErrorResponse(ErrorCode errorCode, String message) {
        String displayMessage = StringUtils.isNotBlank(message) ? message : errorCode.getMessage();
        return Result.error(errorCode.getCode(), displayMessage);
    }

    /**
     * 创建成功响应
     */
    public static <T> Result<T> createSuccessResponse(T data) {
        return Result.success(data);
    }

    /**
     * 创建成功响应（无数据）
     */
    public static <T> Result<T> createSuccessResponse() {
        return Result.success();
    }

    /**
     * 状态码映射
     */
    private static int mapHttpStatus(int errorCode) {
        if (errorCode >= 400 && errorCode < 600) {
            return errorCode;
        }
        return 500; // INTERNAL_SERVER_ERROR
    }

    /**
     * 检查前置条件
     */
    public static void check(boolean condition, ErrorCode errorCode, String message) {
        if (!condition) {
            throw new BizException(errorCode, message);
        }
    }

    /**
     * 检查参数非空
     */
    public static void notNull(Object value, String paramName) {
        check(value != null, ErrorCode.PARAM_NOT_NULL, "参数不能为空: " + paramName);
    }

    /**
     * 检查字符串非空
     */
    public static void notBlank(String value, String paramName) {
        check(StringUtils.isNotBlank(value), ErrorCode.PARAM_NOT_NULL, "参数不能为空: " + paramName);
    }
}