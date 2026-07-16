package com.star.pivot.system.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录页公开配置（匿名可读）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginConfigResponse {

    /** 是否开启登录验证码 */
    private boolean captchaEnabled;

    /** 是否开放用户自助注册 */
    private boolean registerEnabled;

    /** 验证码类型：image / slider */
    private String captchaType;
}
