package com.star.pivot.config.exception.handler;

import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.ServiceException;
import com.star.pivot.framework.exception.handler.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServiceExceptionHandler implements ExceptionHandler<ServiceException> {
    
    @Override
    public boolean supports(Exception exception) {
        return exception instanceof ServiceException;
    }
    
    @Override
    public Result handle(ServiceException exception) {
        log.error("服务异常：code={}, message={}", exception.getCode(), exception.getDisplayMessage(), exception);
        ErrorCode errorCode = exception.getErrorCode();
        if (errorCode == ErrorCode.INTERNAL_ERROR) {
            return Result.error(ErrorCode.SERVICE_UNAVAILABLE.getCode(), "服务暂时不可用，请稍后重试");
        }
        return Result.error(errorCode.getCode(), exception.getDisplayMessage());
    }
    
    @Override
    public int getOrder() {
        return 10;
    }
}
