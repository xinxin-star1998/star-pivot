package com.star.pivot.controller.system;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.system.domain.bo.OperLogReqBo;
import com.star.pivot.system.domain.bo.OperLogVO;
import com.star.pivot.system.domain.entity.SysOperLog;
import com.star.pivot.system.service.interfaces.SysOperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志控制器
 *
 * @author xinxin
 * @since 2026-01-23
 */
@RestController
@RequestMapping("/sys/operlog")
@Tag(name = "操作日志管理", description = "操作日志的查询、删除等接口")
public class SysOperLogController {

    private final SysOperLogService sysOperLogService;

    public SysOperLogController(SysOperLogService sysOperLogService) {
        this.sysOperLogService = sysOperLogService;
    }

    /**
     * 分页查询操作日志
     *
     * @param operLogReqBo 查询参数
     * @return 分页结果
     */
    @Log(title = "操作日志")
    @PreAuthorize("hasAuthority('system:operlog:query')")
    @PostMapping("/pageList")
    public Result<PageResponse<OperLogVO>> pageList(@RequestBody OperLogReqBo operLogReqBo) {
        PageResponse<OperLogVO> pageResponse = sysOperLogService.pageList(operLogReqBo);
        return Result.success(pageResponse);
    }

    /**
     * 根据ID获取操作日志详情
     *
     * @param operId 日志ID
     * @return 操作日志详情
     */
    @Log(title = "操作日志")
    @Operation(summary = "获取操作日志详情", description = "根据日志ID获取操作日志的详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = OperLogVO.class))),
            @ApiResponse(responseCode = "404", description = "操作日志不存在")
    })
    @PreAuthorize("hasAuthority('system:operlog:query')")
    @GetMapping("/{operId}")
    public Result<OperLogVO> getOperLogById(@Parameter(description = "日志ID") @PathVariable Long operId) {
        SysOperLog operLog = sysOperLogService.getById(operId);
        if (operLog == null) {
            return Result.error("操作日志不存在");
        }
        OperLogVO operLogVO = convertToVO(operLog);
        return Result.success(operLogVO);
    }

    /**
     * 删除操作日志（支持单删和批量删除）
     *
     * @param deleteRequest 删除请求，包含 ids 数组
     * @return 操作结果
     */
    @Log(title = "操作日志", businessType = 3)
    @Operation(summary = "删除操作日志", description = "删除操作日志（支持批量删除）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "删除ID为空")
    })
    @PreAuthorize("hasAuthority('system:operlog:delete')")
    @DeleteMapping("/delete")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> operIds = validateIds(deleteRequest.getIds());
        boolean success = sysOperLogService.removeByIds(operIds);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 清空操作日志
     *
     * @return 操作结果
     */
    @Log(title = "操作日志", businessType = 3)
    @Operation(summary = "清空操作日志", description = "清空所有操作日志")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "清空成功")
    })
    @PreAuthorize("hasAuthority('system:operlog:delete')")
    @DeleteMapping("/clean")
    public Result<?> clean() {
        boolean success = sysOperLogService.remove(null);
        return success ? Result.success("清空成功") : Result.error("清空失败");
    }

    /**
     * 转换为VO
     */
    private OperLogVO convertToVO(SysOperLog operLog) {
        OperLogVO vo = new OperLogVO();
        BeanUtils.copyProperties(operLog, vo);
       return vo;
    }

    /**
     * 验证ID列表非空
     */
    private List<Long> validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BizException(
                ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        return ids;
    }
}
