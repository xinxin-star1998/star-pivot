package com.star.pivot.config.web;

import com.star.pivot.framework.annotation.NoResponseWrapper;
import com.star.pivot.framework.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一响应处理：包装为 Result + 将 HTTP 状态码与 Result.code 对齐
 * <p>合并原 ResponseWrapperAdvice 与 ResultStatusAdvice，减少重复扫描与配置
 */
@Slf4j
@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> containingClass = returnType.getContainingClass();
        if (containingClass != null) {
            String pkg = containingClass.getPackageName();
            if (pkg.startsWith("org.springdoc") || pkg.startsWith("org.springframework.boot.actuate")) {
                return false;
            }
        }
        return !returnType.hasMethodAnnotation(NoResponseWrapper.class)
                && (containingClass == null || !containingClass.isAnnotationPresent(NoResponseWrapper.class));
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        Object resultBody = body instanceof Result ? body : Result.success(body);
        if (resultBody instanceof Result<?> result) {
            Integer code = result.getCode();
            if (code != null) {
                HttpStatus status = HttpStatus.resolve(code);
                if (status != null) {
                    response.setStatusCode(status);
                }
            }
        }
        return resultBody;
    }
}
