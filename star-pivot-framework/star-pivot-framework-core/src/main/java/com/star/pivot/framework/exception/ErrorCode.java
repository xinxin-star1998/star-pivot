package com.star.pivot.framework.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(200, "操作成功"),

    CLIENT_ERROR(400, "请求参数错误"),
    PARAM_NOT_NULL(400, "参数不能为空"),
    PARAM_INVALID(400, "参数格式不正确"),
    VALIDATE_ERROR(400, "数据校验失败"),

    UNAUTHORIZED(401, "未授权，请先登录"),
    TOKEN_EXPIRED(401, "登录已过期，请重新登录"),
    TOKEN_INVALID(401, "无效的认证信息"),
    LOGIN_FAILED(401, "用户名或密码错误"),
    CAPTCHA_ERROR(401, "验证码错误"),
    CAPTCHA_EXPIRED(401, "验证码已失效"),
    CAPTCHA_TOO_MANY_ATTEMPTS(401, "验证码尝试次数过多"),

    FORBIDDEN(403, "无权限访问该资源"),
    ACCESS_DENIED(403, "权限不足"),

    NOT_FOUND(404, "资源不存在"),
    USER_NOT_FOUND(404, "用户不存在"),
    ROLE_NOT_FOUND(404, "角色不存在"),
    MENU_NOT_FOUND(404, "菜单不存在"),
    DEPT_NOT_FOUND(404, "部门不存在"),
    POST_NOT_FOUND(404, "岗位不存在"),
    NOTICE_NOT_FOUND(404, "通知公告不存在"),
    DICT_NOT_FOUND(404, "字典数据不存在"),

    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    LOCKED(423, "资源已被锁定"),
    ACCOUNT_LOCKED(423, "账号已被锁定"),
    ACCOUNT_DISABLED(423, "账号已被禁用"),

    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后重试"),
    RATE_LIMIT_EXCEEDED(429, "接口访问频率超限"),

    INTERNAL_ERROR(500, "系统内部错误"),
    SERVICE_UNAVAILABLE(500, "服务暂时不可用"),
    DATABASE_ERROR(500, "数据库操作失败"),
    REDIS_ERROR(500, "缓存操作失败"),
    FILE_UPLOAD_ERROR(500, "文件上传失败"),
    FILE_DELETE_ERROR(500, "文件删除失败"),
    IMPORT_ERROR(500, "数据导入失败"),
    EXPORT_ERROR(500, "数据导出失败"),
    CAPTCHA_GENERATE_ERROR(500, "验证码生成失败"),

    USER_USERNAME_EXISTS(1001, "用户名已存在"),
    USER_USERNAME_USED(1002, "用户名已被使用"),
    USER_PASSWORD_ERROR(1003, "密码错误"),
    USER_IMPORT_EMPTY(1004, "导入数据不能为空"),
    USER_IMPORT_ROW_EMPTY(1005, "行数据为空"),
    USER_IMPORT_USERNAME_EMPTY(1006, "用户账号不能为空"),
    USER_IMPORT_NICKNAME_EMPTY(1007, "用户昵称不能为空"),
    USER_IMPORT_DEPT_FORMAT_ERROR(1008, "部门ID格式不正确"),

    ROLE_KEY_EXISTS(2001, "角色权限字符串已存在"),
    ROLE_KEY_USED(2002, "角色权限字符串已被使用"),
    ROLE_USED(2003, "角色已被使用，不能删除"),
    ROLE_DELETED(2004, "角色已删除"),
    ROLE_DISABLED(2005, "角色已禁用"),
    ROLE_ADMIN_PROTECTED(2006, "不能修改或删除超级管理员角色"),

    MENU_NAME_EXISTS(3001, "菜单名称已存在"),
    MENU_HAS_CHILDREN(3002, "存在子菜单，不允许删除"),
    MENU_USED_BY_ROLE(3003, "菜单已被角色使用，不允许删除"),
    MENU_PARENT_ERROR(3004, "不能将父菜单设置为自己的子菜单"),

    DEPT_NAME_EXISTS(4001, "部门名称已存在"),
    DEPT_HAS_CHILDREN(4002, "存在子部门，不允许删除"),
    DEPT_HAS_USERS(4003, "部门存在用户，不允许删除"),
    DEPT_PARENT_ERROR(4004, "不能将父部门设置为自己的子部门"),

    POST_CODE_EXISTS(5001, "岗位编码已存在"),
    POST_CODE_USED(5002, "岗位编码已被使用"),
    POST_USED(5003, "岗位已被使用，不能删除"),

    DICT_TYPE_NOT_FOUND(6001, "字典类型不存在"),

    // 验证相关错误
    PARAM_OUT_OF_RANGE(6002, "参数超出允许范围"),
    PARAM_TOO_LONG(6003, "参数长度超出限制"),
    PARAM_TOO_SHORT(6004, "参数长度不足"),
    INVALID_FORMAT(6005, "参数格式不正确"),

    // 文件相关错误
    FILE_SIZE_EXCEEDED(7001, "文件大小超出限制"),
    FILE_TYPE_NOT_ALLOWED(7002, "文件类型不被允许"),
    FILE_NAME_INVALID(7003, "文件名包含非法字符"),
    FILE_NOT_READABLE(7004, "文件无法读取"),

    // 网络相关错误
    NETWORK_TIMEOUT(8001, "网络请求超时"),
    NETWORK_CONNECTION_FAILED(8002, "网络连接失败"),
    API_CALL_FAILED(8003, "API调用失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ErrorCode fromCode(int code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return INTERNAL_ERROR;
    }
}
