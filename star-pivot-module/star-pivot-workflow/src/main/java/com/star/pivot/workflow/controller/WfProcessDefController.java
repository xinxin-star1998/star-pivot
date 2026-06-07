package com.star.pivot.workflow.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.workflow.domain.dto.ProcessDefQueryDTO;
import com.star.pivot.workflow.domain.dto.ProcessDefSaveDTO;
import com.star.pivot.workflow.domain.vo.ProcessDefVO;
import com.star.pivot.workflow.service.WfProcessDefService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workflow/def")
@RequiredArgsConstructor
@Tag(name = "工作流-流程定义", description = "流程定义管理")
public class WfProcessDefController {

    private final WfProcessDefService processDefService;

    @Operation(summary = "分页查询流程定义")
    @PreAuthorize("hasAuthority('workflow:def:query')")
    @PostMapping("/list")
    public Result<PageResponse<ProcessDefVO>> list(@RequestBody ProcessDefQueryDTO query) {
        return Result.success(processDefService.page(query));
    }

    @Operation(summary = "流程定义详情")
    @PreAuthorize("hasAuthority('workflow:def:query')")
    @GetMapping("/{defId}")
    public Result<ProcessDefVO> detail(@PathVariable Long defId) {
        return Result.success(processDefService.getById(defId));
    }

    @Log(title = "保存流程定义", businessType = AppConstants.BusinessType.INSERT)
    @Operation(summary = "保存草稿")
    @PreAuthorize("hasAuthority('workflow:def:edit')")
    @PostMapping("/save")
    public Result<Long> save(@Valid @RequestBody ProcessDefSaveDTO dto) {
        return Result.success(processDefService.saveDraft(dto));
    }

    @Log(title = "发布流程定义", businessType = AppConstants.BusinessType.UPDATE)
    @Operation(summary = "发布流程")
    @PreAuthorize("hasAuthority('workflow:def:publish')")
    @PostMapping("/{defId}/publish")
    public Result<?> publish(@PathVariable Long defId) {
        processDefService.publish(defId);
        return Result.success("发布成功");
    }

    @Log(title = "停用流程定义", businessType = AppConstants.BusinessType.UPDATE)
    @Operation(summary = "停用流程")
    @PreAuthorize("hasAuthority('workflow:def:edit')")
    @PostMapping("/{defId}/disable")
    public Result<?> disable(@PathVariable Long defId) {
        processDefService.disable(defId);
        return Result.success("停用成功");
    }

    @Log(title = "删除流程定义", businessType = AppConstants.BusinessType.DELETE)
    @Operation(summary = "删除流程定义")
    @PreAuthorize("hasAuthority('workflow:def:delete')")
    @DeleteMapping("/remove")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        processDefService.removeByIds(deleteRequest.getIds());
        return Result.success("删除成功");
    }
}
