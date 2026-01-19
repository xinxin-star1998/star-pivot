package com.star.pivot.config;

import com.star.pivot.common.domain.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一将 HTTP 状态码与 Result.code 对齐，减少前端分支判断。
 * 对所有返回 Result 的响应生效：
 * - Result.code 对应的标准 HTTP 状态码，则设置为该状态码
 * - 未映射到标准状态码时，保持默认状态码（通常为 200）
 */
@RestControllerAdvice
public class ResultStatusAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 只处理返回体是 Result 的情况
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (body instanceof Result<?> result) {
            Integer code = result.getCode();
            if (code != null) {
                HttpStatus status = HttpStatus.resolve(code);
                if (status != null) {
                    response.setStatusCode(status);
                }
            }
        }
        return body;
    }
}
