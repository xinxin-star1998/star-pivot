package com.star.pivot.controller;

import com.star.pivot.common.annotation.Log;
import com.star.pivot.common.domain.DeleteRequest;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.domain.Result;
import com.star.pivot.system.domain.bo.LogininforReqBo;
import com.star.pivot.system.domain.bo.LogininforVO;
import com.star.pivot.system.service.SysLogininforService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 登录日志控制器
 *
 * @author xinxin
 * @since 2026-01-23
 */
@RestController
@RequestMapping("/sys/logininfor")
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
    @PreAuthorize("hasAuthority('system:logininfor:query')")
    @GetMapping("/{infoId}")
    public Result<LogininforVO> getLogininforById(@PathVariable("infoId") Long infoId) {
        com.star.pivot.system.domain.entity.SysLogininfor logininfor = sysLogininforService.getById(infoId);
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
    @PreAuthorize("hasAuthority('system:logininfor:delete')")
    @DeleteMapping("/delete")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        java.util.List<Long> infoIds = deleteRequest.getIds();
        if (infoIds == null || infoIds.isEmpty()) {
            return Result.error("删除ID不能为空");
        }
        boolean success = sysLogininforService.removeByIds(infoIds);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 清空登录日志
     *
     * @return 操作结果
     */
    @Log(title = "登录日志", businessType = 3)
    @PreAuthorize("hasAuthority('system:logininfor:delete')")
    @DeleteMapping("/clean")
    public Result<?> clean() {
        boolean success = sysLogininforService.remove(null);
        return success ? Result.success("清空成功") : Result.error("清空失败");
    }

    /**
     * 转换为VO
     */
    private LogininforVO convertToVO(com.star.pivot.system.domain.entity.SysLogininfor logininfor) {
        LogininforVO vo = new LogininforVO();
        org.springframework.beans.BeanUtils.copyProperties(logininfor, vo);
        return vo;
    }
}
