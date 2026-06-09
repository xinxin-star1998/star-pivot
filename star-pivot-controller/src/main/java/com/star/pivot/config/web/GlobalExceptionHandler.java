package com.star.pivot.config.web;

import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BaseException;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.EnhancedExceptionProcessor;
import com.star.pivot.framework.exception.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
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

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<Result<Void>> handleBizException(BizException e) {
        ErrorCode errorCode = e.getErrorCode();
        int code = errorCode.getCode();
        String message = e.getDisplayMessage();

        // 记录异常日志
        if (errorCode == ErrorCode.INTERNAL_ERROR) {
            log.error("系统内部异常：code={}, message={}", code, message, e);
        } else {
            log.warn("业务异常：code={}, message={}", code, message);
        }

        return ResponseEntity.status(mapToHttpStatus(code)).body(Result.error(code, message));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Result<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证异常：{}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Result.error(ErrorCode.UNAUTHORIZED.getCode(), "登录凭证无效或已过期，请重新登录"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("访问拒绝：{}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(Result.error(ErrorCode.FORBIDDEN.getCode(), "权限不足，无法执行此操作"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> fieldErrors = e.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage
            ));

        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

        log.warn("参数校验失败：{}", message);

        // 使用增强的异常处理器
        return ResponseEntity.badRequest()
            .body(EnhancedExceptionProcessor.processValidationException(fieldErrors, "handleMethodArgumentNotValidException", "GlobalExceptionHandler"));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBindException(BindException e) {
        Map<String, String> fieldErrors = e.getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage
            ));

        String message = e.getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));

        log.warn("参数绑定异常：{}", message);

        // 使用增强的异常处理器
        return ResponseEntity.badRequest()
            .body(EnhancedExceptionProcessor.processValidationException(fieldErrors, "handleBindException", "GlobalExceptionHandler"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        Map<String, String> violations = e.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                v -> v.getPropertyPath().toString(),
                ConstraintViolation::getMessage
            ));

        String message = e.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));

        log.warn("约束校验异常：{}", message);

        // 构造字段错误信息
        Map<String, String> fieldErrors = new HashMap<>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            if (propertyPath.isEmpty()) {
                propertyPath = "parameter";
            }
            fieldErrors.put(propertyPath, violation.getMessage());
        }

        return ResponseEntity.badRequest()
            .body(EnhancedExceptionProcessor.processValidationException(fieldErrors, "handleConstraintViolationException", "GlobalExceptionHandler"));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("缺少请求参数：{}", e.getParameterName());
        return ResponseEntity.badRequest()
            .body(Result.error(ErrorCode.PARAM_NOT_NULL.getCode(), "缺少必填参数：" + e.getParameterName()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("参数类型不匹配：{}={}", e.getName(), e.getValue());
        return ResponseEntity.badRequest()
            .body(Result.error(ErrorCode.PARAM_INVALID.getCode(), "参数类型不正确：" + e.getName()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("HTTP方法不支持：{}", e.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(Result.error(ErrorCode.METHOD_NOT_ALLOWED.getCode(), "不支持 " + e.getMethod() + " 请求方法"));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Result<Void>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("资源不存在：{}", e.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Result.error(ErrorCode.NOT_FOUND.getCode(), "请求的资源不存在"));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Result<Void>> handleDataAccessException(DataAccessException e) {
        log.error("数据库访问异常：{}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Result.error(ErrorCode.DATABASE_ERROR.getCode(), "数据库操作失败，请稍后重试"));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Result<Void>> handleBaseException(BaseException e) {
        ErrorCode errorCode = e.getErrorCode();
        int code = errorCode.getCode();
        String message = e.getDisplayMessage();
        log.warn("基础异常：code={}, message={}", code, message);
        return ResponseEntity.status(mapToHttpStatus(code)).body(Result.error(code, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        // 使用增强的异常处理器
        Result<Void> result = EnhancedExceptionProcessor.processSystemException(e, "handleException", "GlobalExceptionHandler");

        // 获取HTTP状态码
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (e instanceof org.springframework.web.HttpRequestMethodNotSupportedException) {
            status = HttpStatus.METHOD_NOT_ALLOWED;
        } else if (e instanceof org.springframework.web.servlet.NoHandlerFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (e instanceof IllegalArgumentException || e instanceof org.springframework.web.bind.MissingServletRequestParameterException
                || e instanceof org.springframework.web.method.annotation.MethodArgumentTypeMismatchException) {
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(result);
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
