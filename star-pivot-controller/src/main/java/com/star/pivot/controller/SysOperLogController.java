package com.star.pivot.controller;

import com.star.pivot.common.annotation.Log;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.domain.Result;
import com.star.pivot.system.domain.bo.OperLogReqBo;
import com.star.pivot.system.domain.bo.OperLogVO;
import com.star.pivot.system.service.SysOperLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制器
 *
 * @author xinxin
 * @since 2026-01-23
 */
@RestController
@RequestMapping("/sys/operlog")
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
    @Log(title = "操作日志", businessType = 0)
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
    @Log(title = "操作日志", businessType = 0)
    @PreAuthorize("hasAuthority('system:operlog:query')")
    @GetMapping("/{operId}")
    public Result<OperLogVO> getOperLogById(@PathVariable("operId") Long operId) {
        com.star.pivot.system.domain.entity.SysOperLog operLog = sysOperLogService.getById(operId);
        if (operLog == null) {
            return Result.error("操作日志不存在");
        }
        OperLogVO operLogVO = convertToVO(operLog);
        return Result.success(operLogVO);
    }

    /**
     * 删除操作日志
     *
     * @param operIds 日志ID数组
     * @return 操作结果
     */
    @Log(title = "操作日志", businessType = 3)
    @PreAuthorize("hasAuthority('system:operlog:delete')")
    @DeleteMapping("/{operIds}")
    public Result<?> remove(@PathVariable Long[] operIds) {
        boolean success = sysOperLogService.removeByIds(java.util.Arrays.asList(operIds));
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 清空操作日志
     *
     * @return 操作结果
     */
    @Log(title = "操作日志", businessType = 3)
    @PreAuthorize("hasAuthority('system:operlog:delete')")
    @DeleteMapping("/clean")
    public Result<?> clean() {
        boolean success = sysOperLogService.remove(null);
        return success ? Result.success("清空成功") : Result.error("清空失败");
    }

    /**
     * 转换为VO
     */
    private OperLogVO convertToVO(com.star.pivot.system.domain.entity.SysOperLog operLog) {
        OperLogVO vo = new OperLogVO();
        org.springframework.beans.BeanUtils.copyProperties(operLog, vo);
        return vo;
    }
}
