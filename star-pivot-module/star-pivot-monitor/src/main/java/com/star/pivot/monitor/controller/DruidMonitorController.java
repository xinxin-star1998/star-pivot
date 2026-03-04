package com.star.pivot.monitor.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.monitor.domain.vo.DruidMonitorVO;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Druid 数据源监控控制器
 *
 * @author xinxin
 * @since 2026-03-04
 */
@RestController
@RequestMapping("/monitor/druid")
@Tag(name = "Druid监控", description = "Druid数据源监控和慢SQL分析接口")
@RequiredArgsConstructor
public class DruidMonitorController {

    private final MonitorService monitorService;

    /**
     * 获取 Druid 监控信息
     * <p>
     * 支持通过参数控制是否返回慢SQL列表，实现慢SQL分析与Druid监控的合并
     * </p>
     *
     * @param includeSlowSqlList 是否包含慢SQL列表（可选，默认false）
     * @param slowSqlThreshold   慢SQL阈值（毫秒，可选，默认5000，仅在 includeSlowSqlList=true 时有效）
     * @return Druid 监控信息（包含慢SQL列表，如果 includeSlowSqlList=true）
     */
    @Log(title = "Druid监控")
    @Operation(summary = "获取Druid监控信息", description = "获取数据源连接池、SQL执行统计等信息，支持慢SQL分析")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = DruidMonitorVO.class)))
    })
    @PreAuthorize("hasAuthority('monitor:druid:query')")
    @GetMapping
    public Result<DruidMonitorVO> getDruidMonitorInfo(
            @Parameter(description = "是否包含慢SQL列表") @RequestParam(required = false, defaultValue = "false") Boolean includeSlowSqlList,
            @Parameter(description = "慢SQL阈值（毫秒）") @RequestParam(required = false) Long slowSqlThreshold) {
        DruidMonitorVO druidInfo;
        if (includeSlowSqlList != null && includeSlowSqlList) {
            druidInfo = monitorService.getDruidMonitorInfoWithSlowSql(slowSqlThreshold);
        } else {
            druidInfo = monitorService.getDruidMonitorInfo();
        }
        return Result.success(druidInfo);
    }
}
