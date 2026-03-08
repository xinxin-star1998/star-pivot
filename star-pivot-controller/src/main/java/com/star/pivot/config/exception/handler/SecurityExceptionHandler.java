package com.star.pivot.config.exception.handler;

import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.handler.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SecurityExceptionHandler implements ExceptionHandler<Exception> {
    
    @Override
    public boolean supports(Exception exception) {
        return exception instanceof AuthenticationException 
            || exception instanceof AccessDeniedException;
    }
    
    @Override
    public Result<Void> handle(Exception exception) {
        if (exception instanceof AuthenticationException) {
            log.warn("认证异常：{}", exception.getMessage());
            return Result.error(ErrorCode.UNAUTHORIZED.getCode(), "认证失败，用户名或密码错误");
        } else if (exception instanceof AccessDeniedException) {
            log.warn("访问拒绝：{}", exception.getMessage());
            return Result.error(ErrorCode.FORBIDDEN.getCode(), ErrorCode.FORBIDDEN.getMessage());
        }
        return Result.error(ErrorCode.INTERNAL_ERROR.getCode(), "安全异常");
    }
    
    @Override
    public int getOrder() {
        return 20;
    }
}
