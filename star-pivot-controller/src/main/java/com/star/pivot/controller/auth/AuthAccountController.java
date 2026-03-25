package com.star.pivot.controller.auth;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.system.domain.bo.LoginRequest;
import com.star.pivot.system.domain.bo.LoginResponse;
import com.star.pivot.system.domain.bo.RegisterRequest;
import com.star.pivot.system.domain.bo.RegisterResponse;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.service.AuthService;
import com.star.pivot.system.service.SysMenuService;
import com.star.pivot.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证 - 登录 & 注册 & 当前用户信息
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理 - 账号", description = "用户登录、注册、当前用户信息等接口")
public class AuthAccountController {

    private final AuthService authService;
    private final SysUserService sysUserService;
    private final SysMenuService sysMenuService;

    @Log(title = "用户登录")
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

    @Log(title = "用户注册", businessType = 1)
    @Operation(summary = "用户注册", description = "通过用户名和密码注册新用户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "注册成功", content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误或用户名已存在")
    })
    @PostMapping("/register")
    public Result<RegisterResponse> register(@RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return Result.success(AppConstants.REGISTER_SUCCESS, response);
    }

    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息，包括用户基本信息、角色列表和权限菜单树")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "用户未认证"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/userinfo")
    public ResponseEntity<Result<Map<String, Object>>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.error(401, "用户未认证"));
        }

        String username = authentication.getName();

        SysUser user = sysUserService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Result.error(404, "用户不存在"));
        }

        SysUser userWithRoles = sysUserService.getUserWithRoles(user.getUserId());
        List<SysRole> roles = userWithRoles != null && userWithRoles.getRoles() != null 
            ? userWithRoles.getRoles() 
            : new ArrayList<>();

        // 判断用户权限：是否为管理员或拥有全部数据权限
        boolean hasAdminPrivilege = checkAdminPrivilege(roles);

        List<SysMenu> permissions;
        if (hasAdminPrivilege) {
            permissions = sysMenuService.menuTree();
        } else {
            permissions = sysUserService.getMenuByUserId(user.getUserId());
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user", userWithRoles);
        userInfo.put("roles", roles);
        userInfo.put("permissions", permissions);

        return ResponseEntity.ok(Result.success(userInfo));
    }

    /**
     * 检查用户是否拥有管理员权限
     * 管理员权限定义：角色为管理员 或 拥有全部数据权限
     *
     * @param roles 用户角色列表
     * @return 是否拥有管理员权限
     */
    private boolean checkAdminPrivilege(List<SysRole> roles) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }

        return roles.stream().anyMatch(role ->
                AppConstants.ADMIN_ROLE_KEY.equals(role.getRoleKey()) ||
                AppConstants.DataScope.ALL.equals(role.getDataScope())
        );
    }
}

