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

