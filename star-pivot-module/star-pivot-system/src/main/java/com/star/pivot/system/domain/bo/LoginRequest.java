package com.star.pivot.system.domain.bo;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    
    private String password;
    
    /**
     * 验证码通过凭证（一次性 proof）
     */
    private String captchaProof;
}