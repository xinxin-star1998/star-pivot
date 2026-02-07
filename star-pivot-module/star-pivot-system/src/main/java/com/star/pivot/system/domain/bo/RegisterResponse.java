package com.star.pivot.system.domain.bo;

import lombok.Data;

/**
 * 用户注册响应结果
 *
 * 返回基础的用户标识信息，前端当前只用作提示，
 * 不直接完成自动登录。
 */
@Data
public class RegisterResponse {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名（登录账号）
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickName;
}

