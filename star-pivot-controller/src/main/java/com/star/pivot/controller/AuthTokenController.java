package com.star.pivot.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.system.domain.bo.LoginResponse;
import com.star.pivot.system.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 认证 - Token / 会话 管理
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理 - Token", description = "访问令牌刷新、登出等接口")
public class AuthTokenController {

    private final TokenService tokenService;

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

        String username = body.get("username");
        if (username == null || username.trim().isEmpty()) {
            return Result.error(400, "用户名不能为空");
        }

        try {
            LoginResponse response = tokenService.refreshToken(username, refreshToken);
            return Result.success("令牌刷新成功", response);
        } catch (BizException e) {
            if (e.getErrorCode() == ErrorCode.UNAUTHORIZED) {
                return Result.error(401, e.getMessage());
            }
            throw e;
        }
    }

    @Log(title = "用户登出", businessType = 0)
    @Operation(summary = "用户登出", description = "登出当前用户，将访问令牌加入黑名单，并吊销刷新令牌")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登出成功")
    })
    @PostMapping("/logout")
    public Result<Void> logout(@Parameter(description = "Authorization请求头，格式：Bearer {token}", required = false)
                               @RequestHeader(value = "Authorization", required = false) String authHeader) {
        // 场景1：未提供令牌，视为未登录状态，直接清除本地会话
        if (authHeader == null || authHeader.trim().isEmpty()) {
            SecurityContextHolder.clearContext();
            log.debug("登出请求未提供令牌，已清除本地会话");
            return Result.success("已清除会话");
        }

        // 场景2：令牌格式错误，清除本地会话
        String token = null;
        if (authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim();
        }

        if (token == null || token.isEmpty()) {
            SecurityContextHolder.clearContext();
            log.debug("登出请求的令牌格式不正确，已清除本地会话");
            return Result.success("已清除会话");
        }

        // 场景3：正常登出，调用 TokenService 处理令牌吊销
        tokenService.logout(token, "0");
        SecurityContextHolder.clearContext();
        log.info("用户登出成功");
        return Result.success("登出成功");
    }
}

