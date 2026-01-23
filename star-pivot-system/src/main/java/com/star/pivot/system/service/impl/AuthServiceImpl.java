package com.star.pivot.system.service.impl;

import com.star.pivot.common.exception.ServiceException;
import com.star.pivot.common.utils.LogUtils;
import com.star.pivot.system.domain.bo.LoginRequest;
import com.star.pivot.system.domain.bo.LoginResponse;
import com.star.pivot.system.domain.entity.SysLogininfor;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.service.AuthService;
import com.star.pivot.system.service.CaptchaService;
import com.star.pivot.system.service.SysLogininforService;
import com.star.pivot.system.service.SysUserService;
import com.star.pivot.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
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
    private final SysLogininforService sysLogininforService;

    @Override
    public LoginResponse login(LoginRequest request) {
        HttpServletRequest httpRequest = getRequest();
        String ipaddr = LogUtils.getClientIp(httpRequest);
        String browser = LogUtils.getBrowser(httpRequest);
        String os = LogUtils.getOs(httpRequest);
        String loginLocation = LogUtils.getLoginLocation(ipaddr);
        
        SysLogininfor logininfor = new SysLogininfor();
        logininfor.setUserName(request.getUsername());
        logininfor.setIpaddr(ipaddr);
        logininfor.setBrowser(browser);
        logininfor.setOs(os);
        logininfor.setLoginLocation(loginLocation);
        logininfor.setLoginTime(LocalDateTime.now());
        
        try {
            // 1. 验证验证码 proof（一次性）
            if (!captchaService.validateAndConsumeCaptchaProof(request.getCaptchaProof(), "login")) {
                logininfor.setStatus("1");
                logininfor.setMsg("验证码错误或已失效");
                sysLogininforService.saveLogininfor(logininfor);
                throw new ServiceException("验证码错误或已失效", 401);
            }
            
            // 2. 使用 AuthenticationManager 进行认证
            UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            authenticationManager.authenticate(authenticationToken);
            
            // 3. 认证成功后查询用户信息（认证通过后用户一定存在）
            SysUser user = userService.getUserByUsername(request.getUsername());
            if (user == null) {
                log.error("用户不存在: {}", request.getUsername());
                logininfor.setStatus("1");
                logininfor.setMsg("用户不存在");
                sysLogininforService.saveLogininfor(logininfor);
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

            // 6. 记录登录成功日志
            logininfor.setStatus("0");
            logininfor.setMsg("登录成功");
            sysLogininforService.saveLogininfor(logininfor);

            log.info("用户登录成功: {}", request.getUsername());
            return response;
        } catch (AuthenticationException e) {
            log.error("认证失败: {}", e.getMessage());
            // 记录登录失败日志
            logininfor.setStatus("1");
            logininfor.setMsg("用户名或密码错误");
            sysLogininforService.saveLogininfor(logininfor);
            throw new ServiceException("用户名或密码错误", 401);
        } catch (ServiceException e) {
            // ServiceException已经在上面处理了，这里不需要重复记录
            throw e;
        } catch (Exception e) {
            log.error("登录异常: {}", e.getMessage(), e);
            // 记录其他异常
            logininfor.setStatus("1");
            logininfor.setMsg("登录异常: " + (e.getMessage() != null ? LogUtils.truncateString(e.getMessage(), 255) : "未知错误"));
            sysLogininforService.saveLogininfor(logininfor);
            throw new ServiceException("登录失败", 500);
        }
    }
    
    /**
     * 获取当前请求
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
