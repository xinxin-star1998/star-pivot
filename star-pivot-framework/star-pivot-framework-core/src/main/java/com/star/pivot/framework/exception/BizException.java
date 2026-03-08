package com.star.pivot.framework.exception;

import java.io.Serial;

/**
 * 统一业务异常
 * <p>
 * 合并原 BusinessException、ServiceException、UtilException，简化异常处理架构。
 * 所有业务相关异常统一使用此类。
 * 
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 使用错误码
 * throw new BizException(ErrorCode.USER_NOT_FOUND);
 * 
 * // 使用错误码 + 自定义消息
 * throw new BizException(ErrorCode.VALIDATE_ERROR, "用户名不能为空");
 * 
 * // 直接使用消息（自动使用 INTERNAL_ERROR）
 * throw new BizException("操作失败");
 * }</pre>
 */
public class BizException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BizException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BizException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }

    public BizException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public BizException(ErrorCode errorCode, String detailMessage, Throwable cause) {
        super(errorCode, detailMessage, cause);
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }
}
