package com.star.pivot.system.domain.bo;

import lombok.Data;

/**
 * 校验验证码请求（一次性）
 */
@Data
public class CaptchaVerifyRequest {
    private String captchaToken;
    private String code;

    /**
     * 业务场景（login/register/reset 等），可选
     */
    private String scene;
}

