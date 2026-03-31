package com.star.pivot.controller.system;

import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.system.service.interfaces.ISysNoticeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import com.star.pivot.system.domain.dto.SysNoticeQueryDTO;
import com.star.pivot.system.domain.bo.SysNoticeVO;
import com.star.pivot.system.domain.dto.SysNoticeDTO;

import java.util.List;

/**
 * 通知公告控制器
 * 
 * @author admin
 * @since 2026-02-05
 */
@Slf4j
@RestController
@RequestMapping("/system/notice")
@RequiredArgsConstructor
public class SysNoticeController
{
    private final ISysNoticeService sysNoticeService;

    /**
     * 分页查询通知公告列表
     * 
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    @PreAuthorize("hasAuthority('system:notice:query')")
    @PostMapping("/list")
    public Result<PageResponse<SysNoticeVO>> noticePageList(@RequestBody SysNoticeQueryDTO queryDTO)
    {
        PageResponse<SysNoticeVO> page = sysNoticeService.selectSysNoticePage(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取通知公告详细信息
     * 
     * @param noticeId 通知公告主键
     * @return 通知公告信息
     */
    @PreAuthorize("hasAuthority('system:notice:query')")
    @GetMapping(value = "/getNoticeInfo/{noticeId}")
    public Result<SysNoticeVO> getNoticeInfo(@PathVariable("noticeId") Integer noticeId)
    {
        SysNoticeVO sysNoticeVO = sysNoticeService.selectSysNoticeByNoticeId(noticeId);
        return Result.success(sysNoticeVO);
    }

    /**
     * 新增通知公告
     * 
     * @param sysNoticeDTO 通知公告信息
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:notice:add')")
    @PostMapping
    public Result<?> addNotice(@Valid @RequestBody SysNoticeDTO sysNoticeDTO)
    {
        boolean success = sysNoticeService.insertSysNotice(sysNoticeDTO);
        return success ? Result.success("新增通知公告成功") : Result.error("新增通知公告失败");
    }

    /**
     * 修改通知公告
     * 
     * @param sysNoticeDTO 通知公告信息
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:notice:edit')")
    @PutMapping
    public Result<?> edit(@Valid @RequestBody SysNoticeDTO sysNoticeDTO)
    {
        boolean success = sysNoticeService.updateSysNotice(sysNoticeDTO);
        return success ? Result.success("修改通知公告成功") : Result.error("修改通知公告失败");
    }

    /**
     * 删除通知公告
     * 
     * @param deleteRequest 需要删除的通知公告主键数组
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:notice:delete')")
    @DeleteMapping("/removeNotice")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest)
    {
        List<Long> noticeIds = validateIds(deleteRequest.getIds());

        boolean success = sysNoticeService.deleteSysNoticeByNoticeIds(noticeIds);
       return success ? Result.success("删除通知公告成功") : Result.error("删除通知公告失败");
   }

    /**
     * 验证ID列表非空
     */
    private List<Long> validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        return ids;
    }
}
