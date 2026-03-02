package com.star.pivot.framework.exception;

import java.io.Serial;

/**
 * 服务异常
 * 用于服务层异常场景（不允许被继承）
 */
public final class ServiceException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ServiceException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ServiceException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }

    public ServiceException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ServiceException(ErrorCode errorCode, String detailMessage, Throwable cause) {
        super(errorCode, detailMessage, cause);
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
