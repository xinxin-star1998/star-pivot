package com.star.pivot.config.exception.handler;

import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.handler.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultExceptionHandler implements ExceptionHandler<Exception> {
    
    @Override
    public boolean supports(Exception exception) {
        return true;
    }
    
    @Override
    public Result handle(Exception exception) {
        if (exception instanceof DataAccessException) {
            log.error("数据库访问异常：", exception);
            return Result.error(ErrorCode.DATABASE_ERROR.getCode(), ErrorCode.DATABASE_ERROR.getMessage());
        } else if (exception instanceof IllegalArgumentException) {
            log.warn("参数非法：{}", exception.getMessage());
            return Result.error(ErrorCode.CLIENT_ERROR.getCode(), "请求参数不合法");
        } else {
            log.error("系统异常：", exception);
            return Result.error(ErrorCode.INTERNAL_ERROR.getCode(), ErrorCode.INTERNAL_ERROR.getMessage());
        }
    }
    
    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
