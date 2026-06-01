package com.star.pivot.framework.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public Result(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功");
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    public static <T> Result<T> success(String message) {
        return new Result<>(200, message);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败");
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }

    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未授权，请先登录");
    }

    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(401, message);
    }

    public static <T> Result<T> forbidden() {
        return new Result<>(403, "无权限访问");
    }

    public static <T> Result<T> forbidden(String message) {
        return new Result<>(403, message);
    }

    public static <T> Result<T> badRequest() {
        return new Result<>(400, "请求参数错误");
    }

    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message);
    }

    public static <T> Result<T> notFound() {
        return new Result<>(404, "请求的资源不存在");
    }

    public static <T> Result<T> notFound(String message) {
        return new Result<>(404, message);
    }

    public static <T> Result<T> serviceUnavailable() {
        return new Result<>(503, "服务暂时不可用");
    }

    public static <T> Result<T> serviceUnavailable(String message) {
        return new Result<>(503, message);
    }

    public static <T> Result<T> conflict() {
        return new Result<>(409, "资源冲突");
    }

    public static <T> Result<T> conflict(String message) {
        return new Result<>(409, message);
    }

    public static <T> Result<T> created(T data) {
        return new Result<>(201, "创建成功", data);
    }

    public static <T> Result<T> noContent() {
        return new Result<>(204, "操作成功");
    }
}
