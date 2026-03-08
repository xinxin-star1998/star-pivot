package com.star.pivot.config.exception.handler;

import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BaseException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.UtilException;
import com.star.pivot.framework.exception.handler.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 核心异常处理器
 * 
 * <p>处理框架核心异常：
 * <ul>
 *   <li>{@link BaseException} - 业务异常和服务异常的基类</li>
 *   <li>{@link UtilException} - 工具类异常</li>
 * </ul>
 * 
 * <p>注意：此类实现了 {@link ExceptionHandler} 接口，由 {@code GlobalExceptionHandler}（@RestControllerAdvice）
 * 通过 {@code ExceptionHandlerRegistry} 统一调度，不要与全局异常处理入口混淆。
 */
@Slf4j
@Component
public class CoreExceptionHandler implements ExceptionHandler<Exception> {

    @Override
    public boolean supports(Exception exception) {
        return exception instanceof BaseException || exception instanceof UtilException;
    }

    @Override
    public Result<Void> handle(Exception exception) {
        if (exception instanceof BaseException baseException) {
            return handleBaseException(baseException);
        }
        return handleUtilException((UtilException) exception);
    }

    private Result<Void> handleBaseException(BaseException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        int code = errorCode.getCode();
        String message = exception.getDisplayMessage();

        if (errorCode == ErrorCode.INTERNAL_ERROR) {
            log.error("服务异常：code={}, message={}", code, message, exception);
            return Result.error(ErrorCode.SERVICE_UNAVAILABLE.getCode(), "服务暂时不可用，请稍后重试");
        }

        log.warn("业务异常：code={}, message={}", code, message);
        return Result.error(code, message);
    }

    private Result<Void> handleUtilException(UtilException exception) {
        log.error("工具类异常：{}", exception.getMessage(), exception);
        return Result.error(ErrorCode.INTERNAL_ERROR.getCode(), "系统处理异常");
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
