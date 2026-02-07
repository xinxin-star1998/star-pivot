package com.star.pivot.system.domain.bo;

import lombok.Data;

/**
 * 用户注册请求参数
 *
 * 用于接收前端注册表单数据，只包含账号和密码，
 * 确认密码等逻辑在前端完成校验。
 */
@Data
public class RegisterRequest {

    /**
     * 用户名（登录账号）
     */
    private String username;

    /**
     * 登录密码
     */
    private String password;
}

