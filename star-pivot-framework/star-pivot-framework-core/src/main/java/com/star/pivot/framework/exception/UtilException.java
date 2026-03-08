package com.star.pivot.framework.exception;

import java.io.Serial;

/**
 * 工具类异常（已废弃，请使用 {@link BizException}）
 * <p>
 * 此类保留仅为向后兼容，实际是 {@link BizException} 的别名。
 * 
 * @deprecated 请使用 {@link BizException} 替代
 */
@Deprecated(since = "1.0", forRemoval = true)
public class UtilException extends BizException {

    @Serial
    private static final long serialVersionUID = 8247610319171014183L;

    public UtilException(Throwable e) {
        super(e.getMessage(), e);
    }

    public UtilException(String message) {
        super(message);
    }

    public UtilException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
