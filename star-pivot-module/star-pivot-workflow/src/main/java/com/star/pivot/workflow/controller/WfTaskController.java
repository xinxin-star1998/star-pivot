package com.star.pivot.workflow.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.workflow.domain.dto.StartWorkflowDTO;
import com.star.pivot.workflow.domain.dto.TaskActionDTO;
import com.star.pivot.workflow.domain.dto.TaskQueryDTO;
import com.star.pivot.workflow.domain.vo.InstanceProgressVO;
import com.star.pivot.workflow.domain.vo.TaskVO;
import com.star.pivot.workflow.service.WfTaskQueryService;
import com.star.pivot.workflow.service.WorkflowEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workflow/task")
@RequiredArgsConstructor
@Tag(name = "工作流-任务", description = "流程发起与审批")
public class WfTaskController {

    private final WorkflowEngineService workflowEngineService;
    private final WfTaskQueryService taskQueryService;

    @Log(title = "发起流程", businessType = AppConstants.BusinessType.INSERT)
    @Operation(summary = "发起流程")
    @PreAuthorize("hasAuthority('workflow:instance:start')")
    @PostMapping("/start")
    public Result<Long> start(@Valid @RequestBody StartWorkflowDTO dto) {
        return Result.success(workflowEngineService.start(dto));
    }

    @Log(title = "审批通过", businessType = AppConstants.BusinessType.UPDATE)
    @Operation(summary = "审批通过")
    @PreAuthorize("hasAuthority('workflow:task:approve')")
    @PostMapping("/approve")
    public Result<?> approve(@Valid @RequestBody TaskActionDTO dto) {
        workflowEngineService.approve(dto);
        return Result.success("审批成功");
    }

    @Log(title = "审批驳回", businessType = AppConstants.BusinessType.UPDATE)
    @Operation(summary = "审批驳回")
    @PreAuthorize("hasAuthority('workflow:task:approve')")
    @PostMapping("/reject")
    public Result<?> reject(@Valid @RequestBody TaskActionDTO dto) {
        workflowEngineService.reject(dto);
        return Result.success("已驳回");
    }

    @Log(title = "撤销流程", businessType = AppConstants.BusinessType.UPDATE)
    @Operation(summary = "撤销流程")
    @PreAuthorize("hasAuthority('workflow:instance:cancel')")
    @PostMapping("/cancel/{instanceId}")
    public Result<?> cancel(@PathVariable Long instanceId) {
        workflowEngineService.cancel(instanceId);
        return Result.success("撤销成功");
    }

    @Operation(summary = "待办列表")
    @PreAuthorize("hasAuthority('workflow:todo:query')")
    @PostMapping("/todo/list")
    public Result<PageResponse<TaskVO>> todoList(@RequestBody TaskQueryDTO query) {
        return Result.success(taskQueryService.todoPage(query));
    }

    @Operation(summary = "已办列表")
    @PreAuthorize("hasAuthority('workflow:done:query')")
    @PostMapping("/done/list")
    public Result<PageResponse<TaskVO>> doneList(@RequestBody TaskQueryDTO query) {
        return Result.success(taskQueryService.donePage(query));
    }

    @Operation(summary = "我发起的流程")
    @PreAuthorize("hasAuthority('workflow:mine:query')")
    @PostMapping("/mine/list")
    public Result<PageResponse<TaskVO>> mineList(@RequestBody TaskQueryDTO query) {
        return Result.success(taskQueryService.startedPage(query));
    }

    @Operation(summary = "流程实例进度")
    @PreAuthorize("hasAnyAuthority('workflow:instance:progress', 'workflow:mine:query', 'workflow:todo:query', 'workflow:done:query')")
    @GetMapping("/instance/{instanceId}/progress")
    public Result<InstanceProgressVO> instanceProgress(@PathVariable Long instanceId) {
        return Result.success(taskQueryService.getInstanceProgress(instanceId));
    }
}
