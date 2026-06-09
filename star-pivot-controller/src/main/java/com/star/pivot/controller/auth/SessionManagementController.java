package com.star.pivot.controller.auth;

import com.star.pivot.framework.domain.Result;
import com.star.pivot.system.domain.bo.DeviceSessionVO;
import com.star.pivot.system.service.interfaces.SessionManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会话管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth/sessions")
@RequiredArgsConstructor
@Tag(name = "会话管理", description = "用户会话管理相关接口")
public class SessionManagementController {
    
    private final SessionManagementService sessionManagementService;
    
    @Operation(summary = "获取用户活跃会话列表", description = "获取指定用户的活跃会话列表，仅本人或管理员可访问")
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public Result<List<DeviceSessionVO>> getUserSessions(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        List<DeviceSessionVO> sessions = sessionManagementService.getUserActiveSessions(userId);
        return Result.success(sessions);
    }
    
    @Operation(summary = "强制下线指定会话", description = "强制下线指定的设备会话，仅本人或管理员可操作")
    @DeleteMapping("/{userId}/{deviceSessionId}")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> forceLogoutSession(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "设备会话ID") @PathVariable String deviceSessionId) {
        sessionManagementService.forceLogoutSession(userId, deviceSessionId);
        return Result.success("会话已强制下线", null);
    }
    
    @Operation(summary = "强制下线用户所有会话", description = "强制下线指定用户的所有会话，仅本人或管理员可操作")
    @DeleteMapping("/all/{userId}")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> forceLogoutAllSessions(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        sessionManagementService.forceLogoutAllSessions(userId);
        return Result.success("所有会话已强制下线", null);
    }
}
