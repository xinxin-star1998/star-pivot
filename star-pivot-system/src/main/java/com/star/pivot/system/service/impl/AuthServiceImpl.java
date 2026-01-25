package com.star.pivot.system.service.impl;

import com.star.pivot.common.exception.ServiceException;
import com.star.pivot.common.utils.LogUtils;
import com.star.pivot.system.domain.bo.LoginRequest;
import com.star.pivot.system.domain.bo.LoginResponse;
import com.star.pivot.system.domain.entity.SysLogininfor;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.service.AccountLockService;
import com.star.pivot.system.service.AuthService;
import com.star.pivot.system.service.CaptchaService;
import com.star.pivot.system.service.LoginRateLimitService;
import com.star.pivot.system.service.SysLogininforService;
import com.star.pivot.system.service.SysUserService;
import com.star.pivot.system.utils.LoginUser;
import com.star.pivot.security.JwtUtil;
import com.star.pivot.security.RefreshTokenManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private final RefreshTokenManager refreshTokenManager;
    private final AuthenticationManager authenticationManager;
    private final SysUserService userService;
    private final CaptchaService captchaService;
    private final SysLogininforService sysLogininforService;
    private final LoginRateLimitService rateLimitService;
    private final AccountLockService accountLockService;

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
            // 1. 检查账户是否被锁定（在验证码之前检查，避免浪费验证码）
            accountLockService.checkAccountLocked(request.getUsername());
            
            // 2. 检查IP维度限流
            rateLimitService.checkIpRateLimit(ipaddr);
            
            // 3. 检查IP+用户名维度限流
            rateLimitService.checkIpUsernameRateLimit(ipaddr, request.getUsername());
            
            // 4. 验证验证码 proof（一次性）
            if (!captchaService.validateAndConsumeCaptchaProof(request.getCaptchaProof(), "login")) {
                logininfor.setStatus("1");
                logininfor.setMsg("验证码错误或已失效");
                sysLogininforService.saveLogininfor(logininfor);
                // 验证码错误也记录失败次数
                accountLockService.recordLoginFailure(request.getUsername());
                throw new ServiceException("验证码错误或已失效", 401);
            }
            
            // 5. 使用 AuthenticationManager 进行认证
            UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            
            // 6. 从认证结果中获取用户信息（避免重复查询数据库）
            // 认证过程中 CustomerUserDetailService.loadUserByUsername() 已经查询了用户信息
            // 并封装在 LoginUser 对象中，这里直接从 Authentication 中获取即可
            SysUser user = null;
            if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
                LoginUser loginUser = (LoginUser) authentication.getPrincipal();
                user = loginUser.getUser();
            }
            
            // 如果无法从认证对象中获取用户信息，则降级查询数据库（理论上不应该发生）
            if (user == null) {
                log.warn("无法从认证对象中获取用户信息，降级查询数据库: {}", request.getUsername());
                user = userService.getUserByUsername(request.getUsername());
                if (user == null) {
                    log.error("用户不存在: {}", request.getUsername());
                    logininfor.setStatus("1");
                    logininfor.setMsg("用户不存在");
                    sysLogininforService.saveLogininfor(logininfor);
                    throw new ServiceException("用户不存在", 404);
                }
            }
            
            // 7. 生成访问令牌（Access Token）
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", request.getUsername());
            claims.put("userId", user.getUserId());
            String token = jwtUtil.generateToken(request.getUsername(), claims);

            // 8. 生成刷新令牌（Refresh Token），并存储完整的登录信息到 Redis
            String refreshToken = refreshTokenManager.generateAndStoreRefreshToken(
                user.getUserId(),
                ipaddr,
                browser,
                os,
                loginLocation
            );

            // 9. 返回登录响应
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setRefreshToken(refreshToken);
            response.setUsername(request.getUsername());
            response.setNickname(user.getNickName());

            // 10. 登录成功，清除失败记录和限流计数
            accountLockService.clearLoginFailures(request.getUsername());
            rateLimitService.clearIpRateLimit(ipaddr);
            rateLimitService.clearIpUsernameRateLimit(ipaddr, request.getUsername());
            
            // 11. 记录登录成功日志
            logininfor.setStatus("0");
            logininfor.setMsg("登录成功");
            sysLogininforService.saveLogininfor(logininfor);

            log.info("用户登录成功: {}", request.getUsername());
            return response;
        } catch (AuthenticationException e) {
            log.error("认证失败: {}", e.getMessage());
            // 记录登录失败
            accountLockService.recordLoginFailure(request.getUsername());
            // 记录登录失败日志
            logininfor.setStatus("1");
            logininfor.setMsg("用户名或密码错误");
            sysLogininforService.saveLogininfor(logininfor);
            throw new ServiceException("用户名或密码错误", 401);
        } catch (ServiceException e) {
            // ServiceException已经在上面处理了，这里不需要重复记录
            // 但如果是账户锁定或限流异常，不需要再记录失败次数（已经在对应服务中处理）
            if (e.getCode() != 423 && e.getCode() != 429) {
                // 其他业务异常（如验证码错误）已在上面记录失败次数
            }
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
