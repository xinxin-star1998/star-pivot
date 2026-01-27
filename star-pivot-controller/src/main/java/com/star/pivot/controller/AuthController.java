package com.star.pivot.controller;

import com.star.pivot.common.annotation.Log;
import com.star.pivot.common.domain.Constants;
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
import com.star.pivot.system.service.SysDeptService;
import com.star.pivot.system.service.OnlineUserService;
import com.star.pivot.system.domain.bo.OnlineUserVO;
import com.star.pivot.security.JwtBlackListManager;
import com.star.pivot.security.JwtUtil;
import com.star.pivot.security.RefreshTokenManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
@Tag(name = "认证管理", description = "用户登录、登出、令牌刷新、验证码等认证相关接口")
public class AuthController {

    private final AuthService authService;
    private final SysUserService sysUserService;
    private final SysMenuService sysMenuService;
    private final SysDeptService sysDeptService;
    private final JwtBlackListManager jwtBlackListManager;
    private final JwtUtil jwtUtil;
    private final CaptchaService captchaService;
    private final RefreshTokenManager refreshTokenManager;
    private final OnlineUserService onlineUserService;

    /**
     * 用户登录接口
     * 
     * @param request 登录请求参数，包含用户名和密码
     * @return 登录响应结果，包含用户信息和认证令牌
     */
    @Log(title = "用户登录", businessType = 0)
    @Operation(summary = "用户登录", description = "通过用户名和密码进行登录，返回访问令牌和刷新令牌")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误"),
            @ApiResponse(responseCode = "423", description = "账户已被锁定")
    })
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success("登录成功", response);
    }

    /**
     * 使用刷新令牌获取新的访问令牌
     *
     * <p>设计说明：
     * <ul>
     *   <li>刷新令牌只验证「是否为指定用户的有效刷新令牌」，不再校验密码</li>
     *   <li>接口本身无需携带旧的 Access Token，可在 Access Token 过期后单独调用</li>
     *   <li>刷新成功后会颁发新的 Access Token，并轮换新的刷新令牌（一次性使用）</li>
     * </ul>
     */
    @Operation(summary = "刷新访问令牌", description = "使用刷新令牌获取新的访问令牌，刷新令牌会轮换（一次性使用）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "刷新成功", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "刷新令牌无效或已过期")
    })
    @PostMapping("/refresh")
    public Result<LoginResponse> refreshToken(@RequestBody Map<String, String> body) {
        if (body == null) {
            return Result.error(400, "请求体不能为空");
        }

        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return Result.error(400, "刷新令牌不能为空");
        }

        // 为了简化传参，这里约定前端同时传递用户名，用于反查用户并校验 refreshToken
        String username = body.get("username");
        if (username == null || username.trim().isEmpty()) {
            return Result.error(400, "用户名不能为空");
        }

        SysUser user = sysUserService.getUserByUsername(username);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        Long userId = user.getUserId();
        // 校验刷新令牌是否有效
        if (!refreshTokenManager.validateRefreshToken(userId, refreshToken)) {
            return Result.error(401, "刷新令牌无效或已过期，请重新登录");
        }

        // 刷新成功：先获取旧的登录信息（如果有），然后吊销旧的刷新令牌
        RefreshTokenManager.RefreshTokenValue oldTokenValue = refreshTokenManager.getRefreshTokenValue(userId);
        refreshTokenManager.revokeRefreshToken(userId);

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("userId", userId);
        String newAccessToken = jwtUtil.generateToken(username, claims);

        // 生成新的刷新令牌，保留原有的登录信息（IP、浏览器、操作系统等），但更新登录时间和最后访问时间
        String newRefreshToken;
        if (oldTokenValue != null) {
            // 保留原有的登录信息，仅更新时间
            newRefreshToken = refreshTokenManager.generateAndStoreRefreshToken(
                userId,
                oldTokenValue.getIpaddr(),
                oldTokenValue.getBrowser(),
                oldTokenValue.getOs(),
                oldTokenValue.getLoginLocation()
            );
        } else {
            // 如果没有旧的登录信息，使用默认值（兼容旧数据）
            newRefreshToken = refreshTokenManager.generateAndStoreRefreshToken(userId);
        }

        LoginResponse response = new LoginResponse();
        response.setToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setUsername(username);
        response.setNickname(user.getNickName());

        return Result.success("令牌刷新成功", response);
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
    @Log(title = "用户登出", businessType = 0)
    @Operation(summary = "用户登出", description = "登出当前用户，将访问令牌加入黑名单，并吊销刷新令牌")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登出成功")
    })
    @PostMapping("/logout")
    public Result<Void> logout(@Parameter(description = "Authorization请求头，格式：Bearer {token}", required = false) 
                                @RequestHeader(value = "Authorization", required = false) String authHeader) {
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
                Long userId = null;
                try {
                    username = jwtUtil.getUsernameFromToken(token);
                    userId = jwtUtil.getUserIdFromToken(token);
                } catch (Exception e) {
                    log.debug("获取用户名失败: {}", e.getMessage());
                }
                
                if (username != null) {
                    log.info("用户 {} 的令牌已加入黑名单", username);
                } else {
                    log.info("令牌已加入黑名单");
                }

                // 同步吊销该用户的刷新令牌，避免登出后仍可刷新
                // 在吊销前，先获取在线用户信息并保存历史记录
                if (userId != null) {
                    // 获取在线用户信息并保存历史记录
                    saveOnlineUserHistory(userId, "0"); // 0表示正常登出
                    refreshTokenManager.revokeRefreshToken(userId);
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
    @Operation(summary = "获取验证码", description = "生成图形验证码，返回验证码Token和Base64编码的图片")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功", content = @Content(schema = @Schema(implementation = CaptchaIssueResponse.class))),
            @ApiResponse(responseCode = "500", description = "生成验证码失败")
    })
    @GetMapping("/captcha")
    public Result<CaptchaIssueResponse> getCaptcha(@Parameter(description = "业务场景，可选，默认login") 
                                                     @RequestParam(value = "scene", required = false) String scene) {
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
    @Operation(summary = "校验验证码", description = "校验用户输入的验证码，验证通过后返回proof凭证（一次性使用）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "校验成功", content = @Content(schema = @Schema(implementation = CaptchaVerifyResponse.class))),
            @ApiResponse(responseCode = "400", description = "验证码错误或已过期")
    })
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
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息，包括用户基本信息、角色列表和权限菜单树")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "用户未认证"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
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

        // ✅ 使用关联查询优化，一次性获取用户及其角色信息，减少数据库查询次数
        SysUser userWithRoles = sysUserService.getUserWithRoles(user.getUserId());
        List<SysRole> roles = userWithRoles.getRoles() != null ? 
            userWithRoles.getRoles() : new ArrayList<>();
        
        List<SysMenu> permissions;
        // 检查用户是否拥有admin角色或dataScope=1的角色，如果有则查询所有菜单树
        boolean isAdmin = roles.stream().anyMatch(role -> Constants.ADMIN_ROLE_KEY.equals(role.getRoleKey()));
        // 检查是否有角色的dataScope为1（全部数据权限）
        boolean hasAllDataScope = roles.stream().anyMatch(role -> Constants.DataScope.ALL.equals(role.getDataScope()));
        
        if (isAdmin || hasAllDataScope) {
            // 如果用户角色包含admin或拥有全部数据权限，则查询所有菜单树
            permissions = sysMenuService.menuTree();
        } else {
            // 否则只查询用户有权限的菜单
            permissions = sysUserService.getMenuByUserId(user.getUserId());
        }

        // 组装返回数据（使用 userWithRoles 以包含完整的用户信息）
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user", userWithRoles);
        userInfo.put("roles", roles);
        userInfo.put("permissions", permissions);

        return ResponseEntity.ok(Result.success(userInfo));
    }

    /**
     * 保存在线用户历史记录
     * <p>
     * 说明：从 Redis 中获取在线用户信息，然后保存到数据库作为历史记录。
     * </p>
     *
     * @param userId     用户ID
     * @param logoutType 下线类型（0正常登出 1强制下线 2过期下线）
     */
    private void saveOnlineUserHistory(Long userId, String logoutType) {
        try {
            // 从 RefreshTokenManager 获取在线用户信息
            RefreshTokenManager.RefreshTokenValue tokenValue = refreshTokenManager.getRefreshTokenValue(userId);
            if (tokenValue == null) {
                log.debug("用户 {} 的刷新令牌不存在，跳过保存历史记录", userId);
                return;
            }

            // 从数据库获取用户信息
            SysUser user = sysUserService.getById(userId);
            if (user == null) {
                log.warn("用户 {} 不存在，跳过保存历史记录", userId);
                return;
            }

            // 构建 OnlineUserVO
            OnlineUserVO onlineUser = new OnlineUserVO();
            onlineUser.setSessionId("jwt:refresh:user:" + userId);
            onlineUser.setUserId(userId);
            onlineUser.setUserName(user.getUserName());
            onlineUser.setNickName(user.getNickName());
            onlineUser.setIpaddr(tokenValue.getIpaddr());
            onlineUser.setBrowser(tokenValue.getBrowser());
            onlineUser.setOs(tokenValue.getOs());
            onlineUser.setLoginLocation(tokenValue.getLoginLocation());

            // 转换时间
            if (tokenValue.getIssuedAt() != null) {
                onlineUser.setLoginTime(java.time.LocalDateTime.ofInstant(
                    tokenValue.getIssuedAt().toInstant(),
                    java.time.ZoneId.systemDefault()
                ));
            }
            if (tokenValue.getLastAccessTime() != null) {
                onlineUser.setLastAccessTime(java.time.LocalDateTime.ofInstant(
                    tokenValue.getLastAccessTime().toInstant(),
                    java.time.ZoneId.systemDefault()
                ));
            }

            // 获取部门名称
            if (user.getDeptId() != null && sysDeptService != null) {
                try {
                    com.star.pivot.system.domain.entity.SysDept dept = sysDeptService.getById(user.getDeptId());
                    if (dept != null) {
                        onlineUser.setDeptName(dept.getDeptName());
                    }
                } catch (Exception e) {
                    log.debug("获取部门信息失败，deptId: {}", user.getDeptId());
                }
            }

            // 保存历史记录
            onlineUserService.saveOnlineUserHistory(onlineUser, logoutType);
        } catch (Exception e) {
            log.warn("保存在线用户历史记录失败，userId: {}", userId, e);
        }
    }
}