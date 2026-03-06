package com.star.pivot.monitor.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.monitor.config.ApiPerformanceAspect;
import com.star.pivot.monitor.domain.bo.ApiPerformanceReqBo;
import com.star.pivot.monitor.domain.entity.SysMonitorApiPerformance;
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

import java.time.LocalDate;
import java.util.List;

/**
 * API 性能监控控制器
 *
 * @author xinxin
 * @since 2026-03-04
 */
@RestController
@RequestMapping("/monitor/api")
@Tag(name = "API性能监控", description = "API性能数据查询、统计和分析接口")
@RequiredArgsConstructor
public class ApiPerformanceController {

    private final MonitorService monitorService;
    private final ApiPerformanceAspect apiPerformanceAspect;

    /**
     * 获取最慢的API接口（Top N）
     *
     * @param limit     查询数量限制
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return API性能数据列表
     */
    @Log(title = "API性能监控")
    @Operation(summary = "获取最慢的API接口", description = "查询响应时间最慢的Top N个API接口")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = SysMonitorApiPerformance.class)))
    })
    @PreAuthorize("hasAuthority('monitor:api:query')")
    @GetMapping("/slowest")
    public Result<List<SysMonitorApiPerformance>> getSlowestApis(
            @Parameter(description = "查询数量限制，默认20，最大100") @RequestParam(defaultValue = "20") Integer limit,
            @Parameter(description = "开始日期") @RequestParam LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam LocalDate endDate) {
        // 参数验证
        if (startDate == null || endDate == null) {
            return Result.error("开始日期和结束日期不能为空");
        }
        if (startDate.isAfter(endDate)) {
            return Result.error("开始日期不能晚于结束日期");
        }
        if (limit != null && (limit < 1 || limit > 100)) {
            return Result.error("limit 参数必须在 1-100 之间");
        }

        List<SysMonitorApiPerformance> apis = monitorService.getSlowestApis(limit, startDate, endDate);
        return Result.success(apis);
    }

    /**
     * 获取错误率最高的API接口（Top N）
     *
     * @param limit     查询数量限制
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return API性能数据列表
     */
    @Log(title = "API性能监控")
    @Operation(summary = "获取错误率最高的API接口", description = "查询错误率最高的Top N个API接口")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = SysMonitorApiPerformance.class)))
    })
    @PreAuthorize("hasAuthority('monitor:api:query')")
    @GetMapping("/error-rate")
    public Result<List<SysMonitorApiPerformance>> getHighestErrorRateApis(
            @Parameter(description = "查询数量限制，默认20，最大100") @RequestParam(defaultValue = "20") Integer limit,
            @Parameter(description = "开始日期") @RequestParam LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam LocalDate endDate) {
        // 参数验证
        if (startDate == null || endDate == null) {
            return Result.error("开始日期和结束日期不能为空");
        }
        if (startDate.isAfter(endDate)) {
            return Result.error("开始日期不能晚于结束日期");
        }
        if (limit != null && (limit < 1 || limit > 100)) {
            return Result.error("limit 参数必须在 1-100 之间");
        }

        List<SysMonitorApiPerformance> apis = monitorService.getHighestErrorRateApis(limit, startDate, endDate);
        return Result.success(apis);
    }

    /**
     * 分页查询API性能监控数据
     *
     * @param reqBo 查询参数
     * @return 分页结果
     */
    @Log(title = "API性能监控")
    @Operation(summary = "分页查询API性能数据", description = "根据条件分页查询API性能监控数据")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = PageResponse.class)))
    })
    @PreAuthorize("hasAuthority('monitor:api:query')")
    @PostMapping("/pageList")
    public Result<PageResponse<SysMonitorApiPerformance>> getApiPerformancePageList(@RequestBody ApiPerformanceReqBo reqBo) {
        PageResponse<SysMonitorApiPerformance> pageResponse = monitorService.getApiPerformancePageList(reqBo);
        return Result.success(pageResponse);
    }

    /**
     * 获取API性能监控统计信息
     *
     * @return 统计信息（总请求数、慢接口数、采样数、队列大小、配置参数等）
     */
    @Log(title = "API性能监控")
    @Operation(summary = "获取API性能统计信息", description = "获取API性能监控的实时统计数据")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = ApiPerformanceAspect.ApiPerformanceStats.class)))
    })
    @PreAuthorize("hasAuthority('monitor:api:query')")
    @GetMapping("/stats")
    public Result<ApiPerformanceAspect.ApiPerformanceStats> getApiPerformanceStats() {
        if (apiPerformanceAspect == null) {
            return Result.error("API性能监控未启用");
        }
        ApiPerformanceAspect.ApiPerformanceStats stats = apiPerformanceAspect.getStats();
        return Result.success(stats);
    }
}
