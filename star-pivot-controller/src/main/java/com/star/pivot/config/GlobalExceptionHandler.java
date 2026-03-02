package com.star.pivot.config;

import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.handler.ExceptionHandlerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ExceptionHandlerRegistry registry;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        return registry.handle(e)
            .map(result -> ResponseEntity.status(mapToHttpStatus(result.getCode())).body(Result.<Void>error(result.getCode(), result.getMessage())))
            .orElseGet(() -> {
                log.error("未处理的异常", e);
                return ResponseEntity.internalServerError()
                    .body(Result.<Void>error(ErrorCode.INTERNAL_ERROR.getCode(), "系统内部错误"));
            });
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
