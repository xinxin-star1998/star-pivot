package com.star.pivot.framework.exception;

import java.io.Serial;

/**
 * 业务异常（已废弃，请使用 {@link BizException}）
 * <p>
 * 此类保留仅为向后兼容，实际是 {@link BizException} 的别名。
 * 
 * @deprecated 请使用 {@link BizException} 替代
 */
@Deprecated(since = "1.0", forRemoval = true)
public class BusinessException extends BizException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BusinessException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public BusinessException(ErrorCode errorCode, String detailMessage, Throwable cause) {
        super(errorCode, detailMessage, cause);
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
