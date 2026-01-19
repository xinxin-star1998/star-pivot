package com.star.pivot.system.domain.bo;

import lombok.Data;

/**
 * 校验验证码响应：返回短期的已通过证明
 */
@Data
public class CaptchaVerifyResponse {
    private String captchaProof;
}

