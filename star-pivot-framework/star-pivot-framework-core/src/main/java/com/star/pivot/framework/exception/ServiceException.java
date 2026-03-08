package com.star.pivot.framework.exception;

import java.io.Serial;

/**
 * 服务异常（已废弃，请使用 {@link BizException}）
 * <p>
 * 此类保留仅为向后兼容，实际是 {@link BizException} 的别名。
 * 
 * @deprecated 请使用 {@link BizException} 替代
 */
@Deprecated(since = "1.0", forRemoval = true)
public final class ServiceException extends BizException {

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
