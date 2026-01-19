package com.star.pivot.controller;

import com.star.pivot.common.domain.Result;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
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
     * @return 登出结果响应
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authHeader) {
        // 从SecurityContext中获取当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }

        // 从请求头中提取JWT令牌
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 如果提取到令牌，则将其加入黑名单
        if (token != null && jwtUtil.validateToken(token)) {
            // 获取令牌的剩余过期时间
            long expirationTime = jwtUtil.getClaimsFromToken(token).getExpiration().getTime() - System.currentTimeMillis();
            if (expirationTime > 0) {
                jwtBlackListManager.addToBlackList(token, expirationTime);
                log.info("用户 {} 的令牌已加入黑名单", username);
            }
        } else {
            log.warn("无法从请求头中提取有效的JWT令牌");
        }

        // 清除SecurityContext
        SecurityContextHolder.clearContext();

        return Result.success("登出成功");
    }

    /**
     * 获取验证码接口
     * 
     * @param captchaId 验证码ID
     * @return 验证码图片（Base64格式）
     */
    @GetMapping("/captcha")
    public Result<Map<String, Object>> getCaptcha(@RequestParam String captchaId) {
        try {
            // 生成验证码图片
            BufferedImage image = captchaService.generateCaptcha(captchaId);
            
            // 将图片转换为Base64
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            outputStream.close();
            
            // 返回验证码图片
            Map<String, Object> result = new HashMap<>();
            result.put("captchaId", captchaId);
            result.put("captchaImage", "data:image/png;base64," + base64Image);
            return Result.success(result);
        } catch (Exception e) {
            log.error("生成验证码失败", e);
            return Result.error(500, "生成验证码失败");
        }
    }

    /**
     * 获取当前用户信息接口
     * 
     * @param authentication Spring Security认证对象
     * @return 当前用户信息，包含用户基本信息、角色列表和权限菜单
     */
    @GetMapping("/userinfo")
    public Result<Map<String, Object>> getCurrentUser(Authentication authentication) {
        // 从Authentication中获取当前用户信息
        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.error(401, "用户未认证");
        }

        String username = authentication.getName();

        // 根据用户名查询用户信息
        SysUser user = sysUserService.getUserByUsername(username);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 查询用户的角色和权限
        List<SysRole> roles = sysUserService.getRolesByUserId(user.getUserId());
        
        List<SysMenu> permissions;
        // 检查用户是否拥有admin角色，如果有则查询所有菜单树
        if (roles.stream().anyMatch(role -> "admin".equals(role.getRoleKey()))) {
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

        return Result.success(userInfo);
    }
}