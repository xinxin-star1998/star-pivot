package com.star.pivot.framework.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public final class ServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;
    private final String detailMessage;

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detailMessage = null;
    }

    public ServiceException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage != null ? detailMessage : errorCode.getMessage());
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }

    public ServiceException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.detailMessage = null;
    }

    public ServiceException(ErrorCode errorCode, String detailMessage, Throwable cause) {
        super(detailMessage != null ? detailMessage : errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }

    public ServiceException(String message) {
        super(message);
        this.errorCode = ErrorCode.INTERNAL_ERROR;
        this.detailMessage = message;
    }

    public ServiceException(String message, Throwable cause) {
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
