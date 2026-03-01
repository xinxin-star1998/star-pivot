package com.star.pivot.config;

import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BusinessException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.ServiceException;
import com.star.pivot.framework.exception.UtilException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        log.warn("业务异常：code={}, message={}", e.getCode(), e.getDisplayMessage());
        return buildErrorResponse(e.getErrorCode(), e.getDisplayMessage());
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Result<Void>> handleServiceException(ServiceException e) {
        log.error("服务异常：code={}, message={}", e.getCode(), e.getDisplayMessage(), e);
        ErrorCode errorCode = e.getErrorCode();
        if (errorCode == ErrorCode.INTERNAL_ERROR) {
            return buildErrorResponse(ErrorCode.SERVICE_UNAVAILABLE, "服务暂时不可用，请稍后重试");
        }
        return buildErrorResponse(errorCode, e.getDisplayMessage());
    }

    @ExceptionHandler(UtilException.class)
    public ResponseEntity<Result<Void>> handleUtilException(UtilException e) {
        log.error("工具类异常：{}", e.getMessage(), e);
        return buildErrorResponse(ErrorCode.INTERNAL_ERROR, "系统处理异常");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Result<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证异常：{}", e.getMessage());
        return buildErrorResponse(ErrorCode.UNAUTHORIZED, "认证失败，用户名或密码错误");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("访问拒绝：{}", e.getMessage());
        return buildErrorResponse(ErrorCode.FORBIDDEN, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验异常：{}", message);
        return buildErrorResponse(ErrorCode.VALIDATE_ERROR, message);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBindException(BindException e) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定异常：{}", message);
        return buildErrorResponse(ErrorCode.VALIDATE_ERROR, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("约束校验异常：{}", message);
        return buildErrorResponse(ErrorCode.VALIDATE_ERROR, message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        log.warn("缺少请求参数：{}", e.getParameterName());
        return buildErrorResponse(ErrorCode.PARAM_NOT_NULL, "缺少必填参数：" + e.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        log.warn("参数类型不匹配：{}={}", e.getName(), e.getValue());
        return buildErrorResponse(ErrorCode.PARAM_INVALID, "参数类型不正确：" + e.getName());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        log.warn("HTTP方法不支持：{}", e.getMethod());
        return buildErrorResponse(ErrorCode.METHOD_NOT_ALLOWED, "不支持 " + e.getMethod() + " 请求方法");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Result<Void>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("资源不存在：{}", e.getRequestURL());
        return buildErrorResponse(ErrorCode.NOT_FOUND, "请求的资源不存在");
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Result<Void>> handleDataAccessException(DataAccessException e) {
        log.error("数据库访问异常：", e);
        return buildErrorResponse(ErrorCode.DATABASE_ERROR, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("参数非法：{}", e.getMessage());
        return buildErrorResponse(ErrorCode.CLIENT_ERROR, "请求参数不合法");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("系统异常：", e);
        return buildErrorResponse(ErrorCode.INTERNAL_ERROR, null);
    }

    private ResponseEntity<Result<Void>> buildErrorResponse(ErrorCode errorCode, String detailMessage) {
        String message = detailMessage != null ? detailMessage : errorCode.getMessage();
        HttpStatus httpStatus = mapToHttpStatus(errorCode.getCode());
        return ResponseEntity.status(httpStatus)
                .body(Result.error(errorCode.getCode(), message));
    }

    private HttpStatus mapToHttpStatus(int code) {
        if (code >= 400 && code < 600) {
            try {
                return HttpStatus.valueOf(code);
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (code >= 1000) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
