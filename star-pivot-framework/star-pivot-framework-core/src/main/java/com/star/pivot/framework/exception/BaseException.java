package com.star.pivot.framework.exception;

import lombok.Getter;

import java.io.Serial;

/**
 * 基础异常类
 * 为业务异常和服务异常提供统一的实现
 */
@Getter
public abstract class BaseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;
    private final String detailMessage;

    protected BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detailMessage = null;
    }

    protected BaseException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage != null ? detailMessage : errorCode.getMessage());
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }

    protected BaseException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.detailMessage = null;
    }

    protected BaseException(ErrorCode errorCode, String detailMessage, Throwable cause) {
        super(detailMessage != null ? detailMessage : errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }

    protected BaseException(String message) {
        super(message);
        this.errorCode = ErrorCode.INTERNAL_ERROR;
        this.detailMessage = message;
    }

    protected BaseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.INTERNAL_ERROR;
        this.detailMessage = message;
    }

    public int getCode() {
        return errorCode.getCode();
    }

    public String getDisplayMessage() {
        if (detailMessage != null) {
            return detailMessage;
        }
        return errorCode.getMessage();
    }
}
