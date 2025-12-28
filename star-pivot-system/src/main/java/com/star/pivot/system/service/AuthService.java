package com.star.pivot.system.service;

import com.star.pivot.system.domain.bo.LoginRequest;
import com.star.pivot.system.domain.bo.LoginResponse;

public interface AuthService {
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);
}
