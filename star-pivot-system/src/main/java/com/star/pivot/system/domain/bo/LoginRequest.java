package com.star.pivot.system.domain.bo;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    
    private String password;
    
    private String captchaId;
    
    private String captcha;
}