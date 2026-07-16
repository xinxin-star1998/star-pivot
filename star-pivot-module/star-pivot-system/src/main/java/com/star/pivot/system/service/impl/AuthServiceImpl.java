package com.star.pivot.system.service.impl;

import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.utils.LogUtils;
import com.star.pivot.framework.utils.validation.AssertUtils;
import com.star.pivot.security.context.SecurityUtils;
import com.star.pivot.system.domain.bo.*;
import com.star.pivot.system.domain.entity.SysLogininfor;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.domain.entity.UserRole;
import com.star.pivot.system.mapper.UserRoleMapper;
import com.star.pivot.system.service.interfaces.*;
import com.star.pivot.system.utils.LoginIpBlacklistMatcher;
import com.star.pivot.system.utils.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
     * 注册用户默认角色 ID，须与 sys_role 中普通用户 role_id 一致
     */
    @Value("${auth.register.default-role-id:5}")
    private Long defaultRegisterRoleId;

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final SysUserService userService;
    private final CaptchaService captchaService;
    private final SysLogininforService sysLogininforService;
    private final LoginRateLimitService rateLimitService;
    private final AccountLockService accountLockService;
    private final UserRoleMapper userRoleMapper;
    private final ISysConfigService sysConfigService;
    private final PasswordPolicyService passwordPolicyService;

    @Override
    public LoginResponse login(LoginRequest request) {
        validateLoginRequest(request);
        
        HttpServletRequest httpRequest = getRequest();
        if (httpRequest == null) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "无法获取请求上下文");
        }
        
        String ipaddr = LogUtils.getClientIp(httpRequest);
        if (ipaddr == null || ipaddr.isEmpty()) {
            ipaddr = "unknown";
        }
        String browser = LogUtils.getBrowser(httpRequest);
        String os = LogUtils.getOs(httpRequest);
        String loginLocation = LogUtils.getLoginLocation(ipaddr);

        SysLogininfor logininfor = buildLoginInfo(request.getUsername(), ipaddr, browser, os, loginLocation);
        
        try {
            checkLoginIpBlacklisted(ipaddr, logininfor);
            preCheckBeforeAuthentication(request, ipaddr, logininfor);
            
            UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            
            SysUser user = extractUserFromAuthentication(authentication, request.getUsername());
            
            com.star.pivot.system.domain.bo.TokenPair tokenPair =
                tokenService.createTokenPair(user, request.getUsername(), ipaddr, browser, os, loginLocation);

            LoginResponse response = new LoginResponse();
            response.setToken(tokenPair.getAccessToken());
            response.setRefreshToken(tokenPair.getRefreshToken());
            response.setUsername(request.getUsername());
            response.setNickname(user.getNickName());

            PasswordModifyHint passwordModifyHint = passwordPolicyService.evaluate(user);
            if (passwordModifyHint.isRequired()) {
                response.setNeedChangePassword(true);
                response.setPasswordModifyReason(passwordModifyHint.getReason());
            }

            accountLockService.clearLoginFailures(request.getUsername());
            rateLimitService.clearIpRateLimit(ipaddr);
            rateLimitService.clearIpUsernameRateLimit(ipaddr, request.getUsername());

            recordLoginSuccess(logininfor);
            log.info("用户登录成功: {}, IP: {}", request.getUsername(), ipaddr);
            return response;
        } catch (AuthenticationException e) {
            log.error("认证失败: {}, IP: {}", e.getMessage(), ipaddr);
            accountLockService.recordLoginFailure(request.getUsername());
            recordLoginFailure(logininfor, "用户名或密码错误");
            throw new BizException(ErrorCode.LOGIN_FAILED);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("登录异常: {}, IP: {}", e.getMessage(), ipaddr, e);
            recordLoginFailure(logininfor, "登录异常: " + (e.getMessage() != null ? LogUtils.truncateString(e.getMessage(), 255) : "未知错误"));
            throw new BizException(ErrorCode.INTERNAL_ERROR, "登录失败");
        }
    }

    private void validateLoginRequest(LoginRequest request) {
        AssertUtils.notNull(request, ErrorCode.PARAM_NOT_NULL, "登录请求不能为空");
        AssertUtils.notEmpty(request.getUsername(), ErrorCode.PARAM_NOT_NULL, "用户名不能为空");
        AssertUtils.notEmpty(request.getPassword(), ErrorCode.PARAM_NOT_NULL, "密码不能为空");
        if (sysConfigService.isCaptchaEnabled()) {
            AssertUtils.notEmpty(request.getCaptchaProof(), ErrorCode.PARAM_NOT_NULL, "验证码凭证不能为空");
        }
        
        String username = request.getUsername().trim();
        if (username.length() > 100) {
            throw new BizException(ErrorCode.PARAM_INVALID, "用户名长度不能超过100个字符");
        }
        
        String password = request.getPassword();
        if (password.length() < 6 || password.length() > 100) {
            throw new BizException(ErrorCode.PARAM_INVALID, "密码长度必须在6-100个字符之间");
        }
    }

    private SysUser extractUserFromAuthentication(Authentication authentication, String username) {
        if (authentication == null || authentication.getPrincipal() == null) {
            log.error("认证对象或principal为空，用户: {}", username);
            throw new BizException(ErrorCode.LOGIN_FAILED, "认证失败");
        }
        
        if (!(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            log.error("认证principal类型错误，期望LoginUser，实际: {}, 用户: {}", 
                authentication.getPrincipal().getClass().getName(), username);
            throw new BizException(ErrorCode.LOGIN_FAILED, "认证数据格式错误");
        }
        
        SysUser user = loginUser.getUser();
        if (user == null) {
            log.error("LoginUser中的用户信息为空，用户: {}", username);
            throw new BizException(ErrorCode.USER_NOT_FOUND, "用户信息不存在");
        }
        
        return user;
    }

    /**
     * 用户注册
     * 说明：
     * - 前端已完成账号、密码长度等基础校验，这里主要做幂等与安全校验
     * - 注册成功后仅返回基础用户信息，不自动登录
     */
    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (!sysConfigService.isRegisterUserEnabled()) {
            throw new BizException(ErrorCode.FORBIDDEN, "当前未开放用户注册");
        }

        HttpServletRequest httpRequest = getRequest();
        String ipaddr = httpRequest != null ? LogUtils.getClientIp(httpRequest) : "unknown";

        String username = request.getUsername();
        String password = request.getPassword();

        AssertUtils.notEmpty(username, ErrorCode.PARAM_NOT_NULL, "用户名不能为空");
        AssertUtils.notEmpty(password, ErrorCode.PARAM_NOT_NULL, "密码不能为空");

        rateLimitService.checkRegisterIpRateLimit(ipaddr);
        rateLimitService.checkRegisterIpUsernameRateLimit(ipaddr, username.trim());

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
        userRole.setRoleId(defaultRegisterRoleId);
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

        // 4. 验证验证码 proof（一次性，可通过 sys.account.captchaEnabled 关闭）
        if (sysConfigService.isCaptchaEnabled()) {
            if (!captchaService.validateAndConsumeCaptchaProof(request.getCaptchaProof(), "login")) {
                recordLoginFailure(logininfor, "验证码错误或已失效");
                accountLockService.recordLoginFailure(request.getUsername());
                throw new BizException(ErrorCode.CAPTCHA_ERROR, "验证码错误或已失效");
            }
        }
    }

    /**
     * 检查登录 IP 是否命中黑名单（sys.login.blackIPList）
     */
    private void checkLoginIpBlacklisted(String ipaddr, SysLogininfor logininfor) {
        if (LoginIpBlacklistMatcher.isBlocked(ipaddr, sysConfigService.getLoginBlackIpList())) {
            recordLoginFailure(logininfor, "登录IP已被禁止访问");
            throw new BizException(ErrorCode.FORBIDDEN, "当前IP禁止登录");
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
