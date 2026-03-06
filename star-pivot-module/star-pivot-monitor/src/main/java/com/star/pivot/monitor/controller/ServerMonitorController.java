package com.star.pivot.monitor.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.monitor.domain.vo.ServerInfoVO;
import com.star.pivot.monitor.service.MonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器监控控制器
 *
 * @author xinxin
 * @since 2026-03-04
 */
@RestController
@RequestMapping("/monitor/server")
@Tag(name = "服务器监控", description = "服务器信息查询接口")
@RequiredArgsConstructor
public class ServerMonitorController {

    private final MonitorService monitorService;

    /**
     * 获取服务器信息
     *
     * @return 服务器信息
     */
    @Log(title = "服务器监控")
    @Operation(summary = "获取服务器信息", description = "获取CPU、内存、磁盘、JVM等服务器运行状态信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = ServerInfoVO.class)))
    })
    @PreAuthorize("hasAuthority('monitor:server:query')")
    @GetMapping
    public Result<ServerInfoVO> getServerInfo() {
        ServerInfoVO serverInfo = monitorService.getServerInfo();
        return Result.success(serverInfo);
    }
}
