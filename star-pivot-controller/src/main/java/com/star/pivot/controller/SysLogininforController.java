package com.star.pivot.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.system.domain.bo.LogininforReqBo;
import com.star.pivot.system.domain.bo.LogininforVO;
import com.star.pivot.system.domain.entity.SysLogininfor;
import com.star.pivot.system.service.SysLogininforService;
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
 * 登录日志控制器
 *
 * @author xinxin
 * @since 2026-01-23
 */
@RestController
@RequestMapping("/sys/logininfor")
@Tag(name = "登录日志管理", description = "登录日志的查询、删除等接口")
public class SysLogininforController {

    private final SysLogininforService sysLogininforService;

    public SysLogininforController(SysLogininforService sysLogininforService) {
        this.sysLogininforService = sysLogininforService;
    }

    /**
     * 分页查询登录日志
     *
     * @param logininforReqBo 查询参数
     * @return 分页结果
     */
    @Log(title = "登录日志")
    @Operation(summary = "分页查询登录日志", description = "根据条件分页查询登录日志列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = PageResponse.class)))
    })
    @PreAuthorize("hasAuthority('system:logininfor:query')")
    @PostMapping("/pageList")
    public Result<PageResponse<LogininforVO>> pageList(@RequestBody LogininforReqBo logininforReqBo) {
        PageResponse<LogininforVO> pageResponse = sysLogininforService.pageList(logininforReqBo);
        return Result.success(pageResponse);
    }

    /**
     * 根据ID获取登录日志详情
     *
     * @param infoId 日志ID
     * @return 登录日志详情
     */
    @Log(title = "登录日志")
    @Operation(summary = "获取登录日志详情", description = "根据日志ID获取登录日志的详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = LogininforVO.class))),
            @ApiResponse(responseCode = "404", description = "登录日志不存在")
    })
    @PreAuthorize("hasAuthority('system:logininfor:query')")
    @GetMapping("/{infoId}")
    public Result<LogininforVO> getLogininforById(@Parameter(description = "日志ID") @PathVariable Long infoId) {
        SysLogininfor logininfor = sysLogininforService.getById(infoId);
        if (logininfor == null) {
            return Result.error("登录日志不存在");
        }
        LogininforVO logininforVO = convertToVO(logininfor);
        return Result.success(logininforVO);
    }

    /**
     * 删除登录日志（支持单删和批量删除）
     *
     * @param deleteRequest 删除请求，包含 ids 数组
     * @return 操作结果
     */
    @Log(title = "登录日志", businessType = 3)
    @Operation(summary = "删除登录日志", description = "删除登录日志（支持批量删除）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "删除ID为空")
    })
    @PreAuthorize("hasAuthority('system:logininfor:delete')")
    @DeleteMapping("/delete")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> infoIds = validateIds(deleteRequest.getIds());
        boolean success = sysLogininforService.removeByIds(infoIds);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 清空登录日志
     *
     * @return 操作结果
     */
    @Log(title = "登录日志", businessType = 3)
    @Operation(summary = "清空登录日志", description = "清空所有登录日志")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "清空成功")
    })
    @PreAuthorize("hasAuthority('system:logininfor:delete')")
    @DeleteMapping("/clean")
    public Result<?> clean() {
        boolean success = sysLogininforService.remove(null);
        return success ? Result.success("清空成功") : Result.error("清空失败");
    }

    /**
     * 转换为VO
     */
    private LogininforVO convertToVO(SysLogininfor logininfor) {
        LogininforVO vo = new LogininforVO();
        BeanUtils.copyProperties(logininfor, vo);
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
