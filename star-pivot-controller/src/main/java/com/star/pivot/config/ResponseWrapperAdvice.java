package com.star.pivot.config;

import com.star.pivot.common.annotation.NoResponseWrapper;
import com.star.pivot.common.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一响应包装器
 * 自动将Controller返回的数据包装成Result格式
 *
 * @author xinxin
 */
@Slf4j
@RestControllerAdvice
public class ResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 统一跳过 SpringDoc / Actuator 等框架自身的 Controller，避免破坏其返回结构
        // 说明：SpringDoc 的 OpenAPI 文档接口如果被包装成 Result，会导致类型转换异常（例如期望 byte[] 却拿到 Result）
        Class<?> containingClass = returnType.getContainingClass();
        if (containingClass != null) {
            String packageName = containingClass.getPackageName();
            if (packageName.startsWith("org.springdoc")
                    || packageName.startsWith("org.springframework.boot.actuate")) {
                return false;
            }
        }

        // 如果返回类型已经是Result，不需要再次包装
        if (Result.class.isAssignableFrom(returnType.getParameterType())) {
            return false;
        }
        // 如果方法或类上有@NoResponseWrapper注解，不包装
        return !returnType.hasMethodAnnotation(NoResponseWrapper.class)
                && !returnType.getContainingClass().isAnnotationPresent(NoResponseWrapper.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                   Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                   ServerHttpRequest request, ServerHttpResponse response) {
        // 如果body已经是Result类型，直接返回
        if (body instanceof Result) {
            return body;
        }
        // 包装成Result格式
        return Result.success(body);
    }
}

