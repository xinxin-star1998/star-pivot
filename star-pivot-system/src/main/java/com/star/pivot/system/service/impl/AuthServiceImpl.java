package com.star.pivot.system.service.impl;

import com.star.pivot.common.exception.ServiceException;
import com.star.pivot.system.domain.bo.LoginRequest;
import com.star.pivot.system.domain.bo.LoginResponse;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.service.AuthService;
import com.star.pivot.system.service.CaptchaService;
import com.star.pivot.system.service.SysUserService;
import com.star.pivot.system.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final SysUserService userService;
    private final CaptchaService captchaService;

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            // 1. 验证验证码
            if (!captchaService.validateCaptcha(request.getCaptchaId(), request.getCaptcha())) {
                throw new ServiceException("验证码错误", 401);
            }
            
            // 2. 使用 AuthenticationManager 进行认证
            UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            authenticationManager.authenticate(authenticationToken);
            
            // 3. 认证成功后查询用户信息（认证通过后用户一定存在）
            SysUser user = userService.getUserByUsername(request.getUsername());
            if (user == null) {
                log.error("用户不存在: {}", request.getUsername());
                throw new ServiceException("用户不存在", 404);
            }
            
            // 4. 生成 JWT Token
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", request.getUsername());
            claims.put("userId", user.getUserId());
            String token = jwtUtil.generateToken(request.getUsername(), claims);
            
            // 5. 返回登录响应
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUsername(request.getUsername());
            response.setNickname(user.getNickName());

            log.info("用户登录成功: {}", request.getUsername());
            return response;
        } catch (AuthenticationException e) {
            log.error("认证失败: {}", e.getMessage());
            throw new ServiceException("用户名或密码错误", 401);
        }
    }
}
