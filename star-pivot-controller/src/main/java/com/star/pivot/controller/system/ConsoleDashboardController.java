package com.star.pivot.controller.system;

import com.star.pivot.framework.domain.Result;
import com.star.pivot.system.domain.bo.ConsoleDashboardVO;
import com.star.pivot.system.service.interfaces.ConsoleDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工作台首页数据接口
 */
@RestController
@RequestMapping("/system/dashboard" )
@RequiredArgsConstructor
@Tag(name = "工作台首页" , description = "工作台首页真实业务数据接口" )
public class ConsoleDashboardController {

    private final ConsoleDashboardService consoleDashboardService;

    @Operation(summary = "获取工作台首页数据" )
    @PreAuthorize("isAuthenticated()" )
    @GetMapping("/console" )
    public Result<ConsoleDashboardVO> getConsoleData() {
        return Result.success(consoleDashboardService.getConsoleData());
    }
}
