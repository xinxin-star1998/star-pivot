package com.star.pivot.common.exception;

import lombok.Getter;

import java.io.Serial;

/**
 * 业务异常
 *
 * @author xinxin
 */
@Getter
public final class ServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 错误明细，内部调试错误
     */
    private String detailMessage;

    /**
     * 空构造方法，避免反序列化问题
     */
    public ServiceException() {
    }

    public ServiceException(String message) {
        this.message = message;
    }

    public ServiceException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 链式设置消息
     */
    public ServiceException setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * 链式设置错误码
     */
    public ServiceException setCode(Integer code) {
        this.code = code;
        return this;
    }

    /**
     * 链式设置详细消息
     */
    public ServiceException setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }
}