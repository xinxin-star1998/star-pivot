package com.star.pivot.system.exception.handler;

import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BusinessException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.handler.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SystemExceptionHandler implements ExceptionHandler<BusinessException> {
    
    @Override
    public boolean supports(Exception exception) {
        if (!(exception instanceof BusinessException)) {
            return false;
        }
        BusinessException ex = (BusinessException) exception;
        ErrorCode errorCode = ex.getErrorCode();
        return errorCode == ErrorCode.USER_NOT_FOUND 
            || errorCode == ErrorCode.USER_USERNAME_EXISTS
            || errorCode == ErrorCode.ACCOUNT_DISABLED
            || errorCode == ErrorCode.ROLE_NOT_FOUND
            || errorCode == ErrorCode.DEPT_NOT_FOUND;
    }
    
    @Override
    public Result handle(BusinessException exception) {
        log.warn("系统业务异常：code={}, message={}", 
            exception.getCode(), exception.getDisplayMessage());
        
        ErrorCode errorCode = exception.getErrorCode();
        String message = exception.getDisplayMessage();
        
        return Result.error(errorCode.getCode(), message);
    }
    
    @Override
    public int getOrder() {
        return 5;
    }
}
