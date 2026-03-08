package com.star.pivot.system.service.impl;

import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.utils.AssertUtils;
import com.star.pivot.framework.utils.LogUtils;
import com.star.pivot.security.utils.SecurityUtils;
import com.star.pivot.system.domain.bo.LoginRequest;
import com.star.pivot.system.domain.bo.LoginResponse;
import com.star.pivot.system.domain.bo.RegisterRequest;
import com.star.pivot.system.domain.bo.RegisterResponse;
import com.star.pivot.system.domain.entity.SysLogininfor;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.domain.entity.UserRole;
import com.star.pivot.system.mapper.UserRoleMapper;
import com.star.pivot.system.service.AccountLockService;
import com.star.pivot.system.service.AuthService;
import com.star.pivot.system.service.CaptchaService;
import com.star.pivot.system.service.LoginRateLimitService;
import com.star.pivot.system.service.SysLogininforService;
import com.star.pivot.system.service.SysUserService;
import com.star.pivot.system.service.TokenService;
import com.star.pivot.system.utils.LoginUser;
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
import java.util.Collections;

/**
 * 认证服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    /**
     * 注册用户默认角色 ID
     *
     * 注意：此 ID 必须与数据库表 sys_role 中普通用户的 role_id 保持一致
     * 默认为 5，对应 "common" 或 "user" 角色
     *
     * 建议后续改造：通过配置文件（application.yml）读取，如：
     * @Value("${auth.register.default-role-id:5}")
     */
    private static final Long DEFAULT_REGISTER_ROLE_ID = 5L;

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final SysUserService userService;
    private final CaptchaService captchaService;
    private final SysLogininforService sysLogininforService;
    private final LoginRateLimitService rateLimitService;
    private final AccountLockService accountLockService;
    private final UserRoleMapper userRoleMapper;

    @Override
    public LoginResponse login(LoginRequest request) {
        HttpServletRequest httpRequest = getRequest();
        String ipaddr = LogUtils.getClientIp(httpRequest);
        String browser = LogUtils.getBrowser(httpRequest);
        String os = LogUtils.getOs(httpRequest);
        String loginLocation = LogUtils.getLoginLocation(ipaddr);

        SysLogininfor logininfor = buildLoginInfo(request.getUsername(), ipaddr, browser, os, loginLocation);
        
        try {
            preCheckBeforeAuthentication(request, ipaddr, logininfor);
            
            // 5. 使用 AuthenticationManager 进行认证
            UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            
            // 6. 从认证结果中获取用户信息（避免重复查询数据库）
            // 认证过程中 CustomerUserDetailService.loadUserByUsername() 已经查询了用户信息
            // 并封装在 LoginUser 对象中，这里直接从 Authentication 中获取即可
            SysUser user = null;
            if (authentication != null && authentication.getPrincipal() instanceof LoginUser loginUser) {
                user = loginUser.getUser();
            }
            
            // 如果无法从认证对象中获取用户信息，则降级查询数据库（理论上不应该发生）
            if (user == null) {
                log.warn("无法从认证对象中获取用户信息，降级查询数据库: {}", request.getUsername());
                user = userService.getUserByUsername(request.getUsername());
                if (user == null) {
                    log.error("用户不存在: {}", request.getUsername());
                    recordLoginFailure(logininfor, "用户不存在");
                    throw new BizException(ErrorCode.USER_NOT_FOUND);
                }
            }
            
            // 7. 生成访问令牌与刷新令牌
            com.star.pivot.system.domain.bo.TokenPair tokenPair =
                tokenService.createTokenPair(user, request.getUsername(), ipaddr, browser, os, loginLocation);

            // 8. 返回登录响应
            LoginResponse response = new LoginResponse();
            response.setToken(tokenPair.getAccessToken());
            response.setRefreshToken(tokenPair.getRefreshToken());
            response.setUsername(request.getUsername());
            response.setNickname(user.getNickName());

            // 9. 登录成功，清除失败记录和限流计数
            accountLockService.clearLoginFailures(request.getUsername());
            rateLimitService.clearIpRateLimit(ipaddr);
            rateLimitService.clearIpUsernameRateLimit(ipaddr, request.getUsername());

            // 10. 记录登录成功日志
            recordLoginSuccess(logininfor);
            log.info("用户登录成功: {}", request.getUsername());
            return response;
        } catch (AuthenticationException e) {
            log.error("认证失败: {}", e.getMessage());
            accountLockService.recordLoginFailure(request.getUsername());
            recordLoginFailure(logininfor, "用户名或密码错误");
            throw new BizException(ErrorCode.LOGIN_FAILED);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("登录异常: {}", e.getMessage(), e);
            recordLoginFailure(logininfor, "登录异常: " + (e.getMessage() != null ? LogUtils.truncateString(e.getMessage(), 255) : "未知错误"));
            throw new BizException(ErrorCode.INTERNAL_ERROR, "登录失败");
        }
    }

    /**
     * 用户注册
     * 说明：
     * - 前端已完成账号、密码长度等基础校验，这里主要做幂等与安全校验
     * - 注册成功后仅返回基础用户信息，不自动登录
     */
    @Override
    public RegisterResponse register(RegisterRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        AssertUtils.notEmpty(username, ErrorCode.PARAM_NOT_NULL, "用户名不能为空");
        AssertUtils.notEmpty(password, ErrorCode.PARAM_NOT_NULL, "密码不能为空");

        SysUser exists = userService.getUserByUsername(username.trim());
        AssertUtils.isNull(exists, ErrorCode.USER_USERNAME_EXISTS);

        SysUser user = new SysUser();
        user.setUserName(username.trim());
        user.setNickName(username.trim());
        user.setUserType("00");
        user.setStatus(AppConstants.Status.NORMAL);
        user.setPassword(SecurityUtils.encryptPassword(password));
        user.setDelFlag(AppConstants.DelFlag.NORMAL);
        user.setCreateBy(username.trim());
        user.setCreateTime(java.time.LocalDateTime.now());

        boolean success = userService.save(user);
        if (!success || user.getUserId() == null) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "注册失败，请稍后重试");
        }

        // 为新用户分配默认角色（普通角色），使登录后可获取菜单权限
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getUserId());
        userRole.setRoleId(DEFAULT_REGISTER_ROLE_ID);
        userRoleMapper.insertBatchUserRoles(Collections.singletonList(userRole));

        RegisterResponse response = new RegisterResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUserName());
        response.setNickName(user.getNickName());
        return response;
    }

    /** 记录登录成功日志（状态 0） */
    private void recordLoginSuccess(SysLogininfor logininfor) {
        logininfor.setStatus("0");
        logininfor.setMsg("登录成功");
        sysLogininforService.saveLogininfor(logininfor);
    }

    /** 记录登录失败日志（状态 1，消息由调用方传入） */
    private void recordLoginFailure(SysLogininfor logininfor, String message) {
        logininfor.setStatus("1");
        logininfor.setMsg(message);
        sysLogininforService.saveLogininfor(logininfor);
    }

    /**
     * 构建登录信息实体
     */
    private SysLogininfor buildLoginInfo(String username,
                                         String ipaddr,
                                         String browser,
                                         String os,
                                         String loginLocation) {
        SysLogininfor logininfor = new SysLogininfor();
        logininfor.setUserName(username);
        logininfor.setIpaddr(ipaddr);
        logininfor.setBrowser(browser);
        logininfor.setOs(os);
        logininfor.setLoginLocation(loginLocation);
        logininfor.setLoginTime(LocalDateTime.now());
        return logininfor;
    }

    /**
     * 登录前安全校验：账号锁定、限流、验证码
     */
    private void preCheckBeforeAuthentication(LoginRequest request,
                                              String ipaddr,
                                              SysLogininfor logininfor) {
        // 1. 检查账户是否被锁定（在验证码之前检查，避免浪费验证码）
        accountLockService.checkAccountLocked(request.getUsername());

        // 2. 检查IP维度限流
        rateLimitService.checkIpRateLimit(ipaddr);

        // 3. 检查IP+用户名维度限流
        rateLimitService.checkIpUsernameRateLimit(ipaddr, request.getUsername());

        // 4. 验证验证码 proof（一次性）
        if (!captchaService.validateAndConsumeCaptchaProof(request.getCaptchaProof(), "login")) {
            recordLoginFailure(logininfor, "验证码错误或已失效");
            accountLockService.recordLoginFailure(request.getUsername());
            throw new BizException(ErrorCode.CAPTCHA_ERROR, "验证码错误或已失效");
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
