package com.star.pivot.config.exception.handler;

import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.handler.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SpringFrameworkExceptionHandler implements ExceptionHandler<Exception> {
    
    @Override
    public boolean supports(Exception exception) {
        return exception instanceof MethodArgumentNotValidException
            || exception instanceof BindException
            || exception instanceof ConstraintViolationException
            || exception instanceof MissingServletRequestParameterException
            || exception instanceof MethodArgumentTypeMismatchException
            || exception instanceof HttpRequestMethodNotSupportedException
            || exception instanceof NoHandlerFoundException;
    }
    
    @Override
    public Result<Void> handle(Exception exception) {
        if (exception instanceof MethodArgumentNotValidException e) {
            String message = e.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            log.warn("参数校验异常：{}", message);
            return Result.error(ErrorCode.VALIDATE_ERROR.getCode(), message);
        } else if (exception instanceof BindException e) {
            String message = e.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            log.warn("参数绑定异常：{}", message);
            return Result.error(ErrorCode.VALIDATE_ERROR.getCode(), message);
        } else if (exception instanceof ConstraintViolationException e) {
            String message = e.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            log.warn("约束校验异常：{}", message);
            return Result.error(ErrorCode.VALIDATE_ERROR.getCode(), message);
        } else if (exception instanceof MissingServletRequestParameterException e) {
            log.warn("缺少请求参数：{}", e.getParameterName());
            return Result.error(ErrorCode.PARAM_NOT_NULL.getCode(), "缺少必填参数：" + e.getParameterName());
        } else if (exception instanceof MethodArgumentTypeMismatchException e) {
            log.warn("参数类型不匹配：{}={}", e.getName(), e.getValue());
            return Result.error(ErrorCode.PARAM_INVALID.getCode(), "参数类型不正确：" + e.getName());
        } else if (exception instanceof HttpRequestMethodNotSupportedException e) {
            log.warn("HTTP方法不支持：{}", e.getMethod());
            return Result.error(ErrorCode.METHOD_NOT_ALLOWED.getCode(), "不支持 " + e.getMethod() + " 请求方法");
        } else if (exception instanceof NoHandlerFoundException e) {
            log.warn("资源不存在：{}", e.getRequestURL());
            return Result.error(ErrorCode.NOT_FOUND.getCode(), "请求的资源不存在");
        }
        return Result.error(ErrorCode.INTERNAL_ERROR.getCode(), "框架异常");
    }
    
    @Override
    public int getOrder() {
        return 30;
    }
}
