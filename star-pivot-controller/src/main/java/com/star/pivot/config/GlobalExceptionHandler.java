package com.star.pivot.config;

import com.star.pivot.common.domain.Result;
import com.star.pivot.common.exception.BusinessException;
import com.star.pivot.common.exception.ServiceException;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author xinxin
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理（BusinessException）
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        // 业务异常通常是可预期的，使用 warn 级别记录日志，避免干扰系统级错误告警
        log.warn("业务异常：{}", e.getMessage(), e);
        Integer code = e.getCode();
        if (code == null) {
            code = 500;
        }
        return ResponseEntity.status(toHttpStatus(code)).body(Result.error(code, e.getMessage()));
    }

    /**
     * 业务异常处理（ServiceException）
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Result<Void>> handleServiceException(ServiceException e) {
        // ServiceException 一般封装的是内部服务错误，这里只在日志中记录详细信息，对外返回通用提示，避免暴露内部实现或敏感信息
        log.error("服务异常：{}", e.getMessage(), e);
        Integer code = e.getCode();
        if (code == null) {
            code = 500;
        }
        return ResponseEntity.status(toHttpStatus(code))
                .body(Result.error(code, "服务暂时不可用，请稍后重试或联系管理员"));
    }

    /**
     * 认证异常处理
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Result<Void>> handleAuthenticationException(AuthenticationException e) {
        // 认证异常详细原因仅记录在日志中，避免对外暴露具体失败原因（例如账号是否存在）
        log.warn("认证异常：{}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Result.error(401, "认证失败，用户名或密码错误"));
    }

    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 参数校验异常只返回用户可理解的提示语，不直接透出底层异常信息
        log.warn("参数校验异常：{}", e.getMessage());
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.error(400, "参数校验失败：" + message));
    }

    /**
     * 参数绑定异常处理
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBindException(BindException e) {
        log.warn("参数绑定异常：{}", e.getMessage());
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.error(400, "参数绑定失败：" + message));
    }

    /**
     * 访问拒绝异常处理
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("访问拒绝：{}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Result.error(403, "没有权限访问该资源"));
    }

    /**
     * 数据库访问异常处理
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Result<Void>> handleDataAccessException(DataAccessException e) {
        log.error("数据库访问异常：", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(500, "数据库操作失败，请联系管理员"));
    }

    /**
     * HTTP方法不支持异常处理
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        log.warn("HTTP方法不支持：{}", e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Result.error(405, "请求方法不支持：" + e.getMethod()));
    }

    /**
     * IllegalArgumentException 参数非法异常处理
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        // 非法参数异常不直接把内部校验规则对外暴露，只返回统一提示
        log.warn("参数非法：{}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.error(400, "请求参数不合法，请检查后重试"));
    }

    /**
     * 通用异常处理（放在最后，作为兜底）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("系统异常：", e);
        // 对外统一返回通用文案，不随日志级别变化，避免在生产环境误暴露内部错误信息
        String message = "系统异常，请联系管理员";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(500, message));
    }

    private static HttpStatus toHttpStatus(int code) {
        try {
            return HttpStatus.valueOf(code);
        } catch (Exception ignore) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}

