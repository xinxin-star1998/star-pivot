package com.star.pivot.system.domain.bo;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    
    private String username;
    
    private String nickname;
}