package com.star.pivot.config;

import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BaseException;
import com.star.pivot.framework.exception.BizException;
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

import java.util.stream.Collectors;

/**
 * 统一全局异常处理器
 * <p>
 * 处理所有类型的异常，包括：
 * <ul>
 *   <li>业务异常 ({@link BizException})</li>
 *   <li>安全异常 ({@link AuthenticationException}, {@link AccessDeniedException})</li>
 *   <li>参数校验异常 (参数绑定、约束校验等)</li>
 *   <li>系统异常 (数据库、其他运行时异常)</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== 业务异常 ====================

    @ExceptionHandler(BizException.class)
    public ResponseEntity<Result<Void>> handleBizException(BizException e) {
        ErrorCode errorCode = e.getErrorCode();
        int code = errorCode.getCode();
        String message = e.getDisplayMessage();

        // 系统内部错误记录详细日志
        if (errorCode == ErrorCode.INTERNAL_ERROR) {
            log.error("系统异常：code={}, message={}", code, message, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(ErrorCode.SERVICE_UNAVAILABLE.getCode(), "服务暂时不可用，请稍后重试"));
        }

        log.warn("业务异常：code={}, message={}", code, message);
        return ResponseEntity.status(mapToHttpStatus(code)).body(Result.error(code, message));
    }

    // ==================== 安全异常 ====================

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Result<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证异常：{}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Result.error(ErrorCode.UNAUTHORIZED.getCode(), ErrorCode.UNAUTHORIZED.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("访问拒绝：{}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(Result.error(ErrorCode.FORBIDDEN.getCode(), ErrorCode.FORBIDDEN.getMessage()));
    }

    // ==================== 参数校验异常 ====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        log.warn("参数校验异常：{}", message);
        return ResponseEntity.badRequest()
            .body(Result.error(ErrorCode.VALIDATE_ERROR.getCode(), message));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBindException(BindException e) {
        String message = e.getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        log.warn("参数绑定异常：{}", message);
        return ResponseEntity.badRequest()
            .body(Result.error(ErrorCode.VALIDATE_ERROR.getCode(), message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));
        log.warn("约束校验异常：{}", message);
        return ResponseEntity.badRequest()
            .body(Result.error(ErrorCode.VALIDATE_ERROR.getCode(), message));
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

    // ==================== 请求相关异常 ====================

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

    // ==================== 系统异常 ====================

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Result<Void>> handleDataAccessException(DataAccessException e) {
        log.error("数据库访问异常：", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Result.error(ErrorCode.DATABASE_ERROR.getCode(), ErrorCode.DATABASE_ERROR.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("参数非法：{}", e.getMessage());
        return ResponseEntity.badRequest()
            .body(Result.error(ErrorCode.CLIENT_ERROR.getCode(), "请求参数不合法"));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Result<Void>> handleBaseException(BaseException e) {
        // 兼容其他 BaseException 子类（非 BizException）
        ErrorCode errorCode = e.getErrorCode();
        int code = errorCode.getCode();
        String message = e.getDisplayMessage();
        log.warn("基础异常：code={}, message={}", code, message);
        return ResponseEntity.status(mapToHttpStatus(code)).body(Result.error(code, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("未处理的异常：", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Result.error(ErrorCode.INTERNAL_ERROR.getCode(), "系统内部错误"));
    }

    // ==================== 工具方法 ====================

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
