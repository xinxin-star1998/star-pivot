package com.star.pivot.config.exception.handler;

import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.UtilException;
import com.star.pivot.framework.exception.handler.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UtilExceptionHandler implements ExceptionHandler<UtilException> {
    
    @Override
    public boolean supports(Exception exception) {
        return exception instanceof UtilException;
    }
    
    @Override
    public Result handle(UtilException exception) {
        log.error("工具类异常：{}", exception.getMessage(), exception);
        return Result.error(ErrorCode.INTERNAL_ERROR.getCode(), "系统处理异常");
    }
    
    @Override
    public int getOrder() {
        return 10;
    }
}
