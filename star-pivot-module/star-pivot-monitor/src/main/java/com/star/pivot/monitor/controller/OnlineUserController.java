package com.star.pivot.monitor.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.monitor.domain.vo.OnlineUserVO;
import com.star.pivot.monitor.service.MonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在线用户管理控制器
 *
 * @author xinxin
 * @since 2026-03-04
 */
@RestController
@RequestMapping("/monitor/online")
@Tag(name = "在线用户管理", description = "在线用户查询和强制下线接口")
@RequiredArgsConstructor
public class OnlineUserController {

    private final MonitorService monitorService;

    /**
     * 获取在线用户列表
     * <p>
     * 说明：当前为简化实现，基于 JWT 无状态认证。
     * 通过 Redis 中存储的刷新令牌（jwt:refresh:user:{userId}）来判断用户是否在线。
     * 后续可按需优化为更完善的会话管理方案（如：引入 Session 机制、记录登录 IP/设备信息等）。
     * </p>
     *
     * @param userName 用户名（可选）
     * @param ipaddr   IP地址（可选，当前实现暂不支持）
     * @return 在线用户列表
     */
    @Log(title = "在线用户")
    @Operation(summary = "获取在线用户列表", description = "查询当前在线的用户列表，支持按用户名筛选")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = OnlineUserVO.class)))
    })
    @PreAuthorize("hasAuthority('monitor:online:query')")
    @GetMapping
    public Result<List<OnlineUserVO>> getOnlineUserList(
            @Parameter(description = "用户名") @RequestParam(required = false) String userName,
            @Parameter(description = "IP地址") @RequestParam(required = false) String ipaddr) {
        List<OnlineUserVO> onlineUserList = monitorService.getOnlineUserList(userName, ipaddr);
        return Result.success(onlineUserList);
    }

    /**
     * 强制用户下线
     * <p>
     * 说明：通过删除 Redis 中的刷新令牌来实现强制下线。
     * 删除刷新令牌后，用户无法刷新 JWT Token，从而间接实现下线效果。
     * </p>
     *
     * @param sessionId 会话ID（实际为 Redis 中的刷新令牌 key，格式：jwt:refresh:user:{userId}）
     * @return 操作结果
     */
    @Log(title = "强退在线用户", businessType = AppConstants.BusinessType.FORCE)
    @Operation(summary = "强制用户下线", description = "强制指定用户下线，删除其刷新令牌")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "强制下线成功"),
            @ApiResponse(responseCode = "400", description = "强制下线失败")
    })
    @PreAuthorize("hasAuthority('monitor:online:force-logout')")
    @DeleteMapping("/{sessionId}")
    public Result<?> forceLogout(@Parameter(description = "会话ID") @PathVariable("sessionId") String sessionId) {
        boolean success = monitorService.forceLogout(sessionId);
        return success ? Result.success("强制下线成功") : Result.error("强制下线失败");
    }
}
