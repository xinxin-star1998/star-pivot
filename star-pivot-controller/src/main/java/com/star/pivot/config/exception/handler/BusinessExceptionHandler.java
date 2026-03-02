package com.star.pivot.config.exception.handler;

import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BusinessException;
import com.star.pivot.framework.exception.handler.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BusinessExceptionHandler implements ExceptionHandler<BusinessException> {
    
    @Override
    public boolean supports(Exception exception) {
        return exception instanceof BusinessException;
    }
    
    @Override
    public Result handle(BusinessException exception) {
        log.warn("业务异常：code={}, message={}", exception.getCode(), exception.getDisplayMessage());
        return Result.error(exception.getErrorCode().getCode(), exception.getDisplayMessage());
    }
    
    @Override
    public int getOrder() {
        return 10;
    }
}
