package com.star.pivot.controller;

import com.star.pivot.common.constants.Constants;
import com.star.pivot.common.domain.Result;
import com.star.pivot.system.domain.bo.CaptchaIssueResponse;
import com.star.pivot.system.domain.bo.CaptchaVerifyRequest;
import com.star.pivot.system.domain.bo.CaptchaVerifyResponse;
import com.star.pivot.system.domain.bo.LoginRequest;
import com.star.pivot.system.domain.bo.LoginResponse;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.service.AuthService;
import com.star.pivot.system.service.CaptchaService;
import com.star.pivot.system.service.SysUserService;
import com.star.pivot.system.service.SysMenuService;
import com.star.pivot.system.utils.JwtBlackListManager;
import com.star.pivot.system.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SysUserService sysUserService;
    private final SysMenuService sysMenuService;
    private final JwtBlackListManager jwtBlackListManager;
    private final JwtUtil jwtUtil;
    private final CaptchaService captchaService;

    /**
     * 用户登录接口
     * 
     * @param request 登录请求参数，包含用户名和密码
     * @return 登录响应结果，包含用户信息和认证令牌
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success("登录成功", response);
    }

    /**
     * 用户登出接口
     * 
     * <p>该接口采用幂等性设计，无论调用多少次，结果都是一致的：
     * <ul>
     *   <li>如果没有提供token，直接返回成功</li>
     *   <li>如果token无效或已过期，直接返回成功（避免恶意请求刷日志）</li>
     *   <li>如果token有效，将其加入黑名单后返回成功</li>
     * </ul>
     * 
     * @param authHeader 可选的Authorization请求头，格式：Bearer {token}
     * @return 登出结果响应
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // 如果没有提供Authorization头，直接返回成功（幂等性设计）
        if (authHeader == null || authHeader.trim().isEmpty()) {
            SecurityContextHolder.clearContext();
            return Result.success("登出成功");
        }

        // 从请求头中提取JWT令牌
        String token = null;
        if (authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim();
        }

        // 如果没有提取到有效的token格式，直接返回成功（幂等性设计）
        if (token == null || token.isEmpty()) {
            SecurityContextHolder.clearContext();
            log.debug("登出请求未提供有效的token格式，已清除SecurityContext");
            return Result.success("登出成功");
        }

        // 验证token是否有效
        if (!jwtUtil.validateToken(token)) {
            // Token无效或已过期，直接返回成功（幂等性设计）
            // 使用debug级别日志，避免恶意请求刷日志
            SecurityContextHolder.clearContext();
            log.debug("登出请求的token无效或已过期，已清除SecurityContext");
            return Result.success("登出成功");
        }

        // Token有效，尝试将其加入黑名单
        try {
            // 获取令牌的剩余过期时间
            long expirationTime = jwtUtil.getClaimsFromToken(token).getExpiration().getTime() - System.currentTimeMillis();
            if (expirationTime > 0) {
                jwtBlackListManager.addToBlackList(token, expirationTime);
                
                // 获取用户名用于日志记录
                String username = null;
                try {
                    username = jwtUtil.getUsernameFromToken(token);
                } catch (Exception e) {
                    log.debug("获取用户名失败: {}", e.getMessage());
                }
                
                if (username != null) {
                    log.info("用户 {} 的令牌已加入黑名单", username);
                } else {
                    log.info("令牌已加入黑名单");
                }
            } else {
                log.debug("Token已过期，无需加入黑名单");
            }
        } catch (Exception e) {
            // 如果获取token信息失败（如解析异常），记录日志但不影响登出流程
            // 确保接口的幂等性和稳定性
            log.warn("处理token黑名单时发生异常，但登出流程继续: {}", e.getMessage());
        }

        // 清除SecurityContext
        SecurityContextHolder.clearContext();

        return Result.success("登出成功");
    }

    /**
     * 获取验证码接口（服务端生成 captchaToken）
     *
     * @param scene 业务场景，可选，默认 login
     * @return 包含 captchaToken 和 Base64 图片
     */
    @GetMapping("/captcha")
    public Result<CaptchaIssueResponse> getCaptcha(@RequestParam(value = "scene", required = false) String scene) {
        try {
            CaptchaIssueResponse response = captchaService.generateCaptcha(scene != null ? scene : "login");
            return Result.success(response);
        } catch (Exception e) {
            log.error("生成验证码失败", e);
            return Result.error(500, "生成验证码失败");
        }
    }

    /**
     * 校验验证码，一次性，返回 proof
     */
    @PostMapping("/captcha/verify")
    public Result<CaptchaVerifyResponse> verifyCaptcha(@RequestBody CaptchaVerifyRequest request) {
        CaptchaVerifyResponse response = captchaService.verifyCaptcha(request);
        return Result.success(response);
    }

    /**
     * 获取当前用户信息接口
     * 
     * @param authentication Spring Security认证对象
     * @return 当前用户信息，包含用户基本信息、角色列表和权限菜单
     */
    @GetMapping("/userinfo")
    public ResponseEntity<Result<Map<String, Object>>> getCurrentUser(Authentication authentication) {
        // 从Authentication中获取当前用户信息
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.error(401, "用户未认证"));
        }

        String username = authentication.getName();

        // 根据用户名查询用户信息
        SysUser user = sysUserService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Result.error(404, "用户不存在"));
        }

        // 查询用户的角色和权限
        List<SysRole> roles = sysUserService.getRolesByUserId(user.getUserId());
        
        List<SysMenu> permissions;
        // 检查用户是否拥有admin角色，如果有则查询所有菜单树
        if (roles.stream().anyMatch(role -> Constants.ADMIN_ROLE_KEY.equals(role.getRoleKey()))) {
            // 如果用户角色包含admin，则查询所有菜单树
            permissions = sysMenuService.menuTree();
        } else {
            // 否则只查询用户有权限的菜单
            permissions = sysUserService.getMenuByUserId(user.getUserId());
        }

        // 组装返回数据
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user", user);
        userInfo.put("roles", roles);
        userInfo.put("permissions", permissions);

        return ResponseEntity.ok(Result.success(userInfo));
    }
}