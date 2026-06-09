package com.star.pivot.controller.system;

import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.system.domain.bo.OnlineUserVO;
import com.star.pivot.system.service.interfaces.OnlineUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在线用户管理控制器
 * 提供查看和管理在线用户会话的功能
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sys/online")
@Tag(name = "在线用户管理", description = "在线用户会话的查询、踢下线等功能")
public class SysOnlineUserController {

    private final OnlineUserService onlineUserService;

    /**
     * 分页查询在线用户列表
     */
    @Operation(summary = "查询在线用户", description = "分页查询当前在线的用户会话")
    @PreAuthorize("hasAuthority('monitor:online:query')")
    @GetMapping("/pageList")
    public Result<PageResponse<OnlineUserVO>> pageList(
            @RequestParam(value = "pageNum", defaultValue = "1") Long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Long pageSize,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "ipaddr", required = false) String ipaddr) {

        try {
            log.debug("查询在线用户列表: pageNum={}, pageSize={}, userName={}, ipaddr={}",
                     pageNum, pageSize, userName, ipaddr);

            PageResponse<OnlineUserVO> pageResponse = onlineUserService.selectOnlineUserList(
                pageNum, pageSize, userName, ipaddr);

            log.debug("查询在线用户列表成功: total={}", pageResponse.getTotal());
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询在线用户列表失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 强制用户下线
     */
    @Operation(summary = "强制用户下线", description = "强制指定的用户会话下线")
    @PreAuthorize("hasAuthority('monitor:online:forceLogout')")
    @DeleteMapping("/forceLogout/{sessionId}")
    public Result<?> forceLogout(@PathVariable("sessionId") String sessionId) {
        try {
            log.info("强制用户下线: sessionId={}", sessionId);

            boolean success = onlineUserService.forceLogout(sessionId);
            if (success) {
                log.info("强制用户下线成功: sessionId={}", sessionId);
                return Result.success("强制下线成功");
            } else {
                log.warn("强制用户下线失败: sessionId={}", sessionId);
                return Result.error("强制下线失败");
            }
        } catch (Exception e) {
            log.error("强制用户下线失败: sessionId={}", sessionId, e);
            return Result.error("强制下线失败：" + e.getMessage());
        }
    }

    /**
     * 批量强制用户下线
     */
    @Operation(summary = "批量强制用户下线", description = "批量强制指定的用户会话下线")
    @PreAuthorize("hasAuthority('monitor:online:forceLogout')")
    @DeleteMapping("/batchForceLogout")
    public Result<?> batchForceLogout(@RequestBody List<String> sessionIds) {
        try {
            log.info("批量强制用户下线: sessionIds={}", sessionIds);

            boolean success = onlineUserService.batchForceLogout(sessionIds);
            if (success) {
                log.info("批量强制用户下线成功: count={}", sessionIds.size());
                return Result.success("批量强制下线成功");
            } else {
                log.warn("批量强制用户下线失败: count={}", sessionIds.size());
                return Result.error("批量强制下线失败");
            }
        } catch (Exception e) {
            log.error("批量强制用户下线失败: sessionIds={}", sessionIds, e);
            return Result.error("批量强制下线失败：" + e.getMessage());
        }
    }

    /**
     * 根据用户ID强制下线
     */
    @Operation(summary = "根据用户ID强制下线", description = "强制指定用户的所有会话下线")
    @PreAuthorize("hasAuthority('monitor:online:forceLogout')")
    @DeleteMapping("/forceLogoutByUserId/{userId}")
    public Result<?> forceLogoutByUserId(@PathVariable("userId") Long userId) {
        try {
            log.info("强制用户ID下线: userId={}", userId);

            onlineUserService.forceLogoutByUserId(userId);
            log.info("强制用户ID下线成功: userId={}", userId);
            return Result.success("强制下线成功");
        } catch (Exception e) {
            log.error("强制用户ID下线失败: userId={}", userId, e);
            return Result.error("强制下线失败：" + e.getMessage());
        }
    }
}