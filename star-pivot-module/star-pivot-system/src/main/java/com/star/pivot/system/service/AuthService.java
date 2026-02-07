package com.star.pivot.system.service;

import com.star.pivot.system.domain.bo.LoginRequest;
import com.star.pivot.system.domain.bo.LoginResponse;
import com.star.pivot.system.domain.bo.RegisterRequest;
import com.star.pivot.system.domain.bo.RegisterResponse;

public interface AuthService {
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户注册
     */
    RegisterResponse register(RegisterRequest request);
}
