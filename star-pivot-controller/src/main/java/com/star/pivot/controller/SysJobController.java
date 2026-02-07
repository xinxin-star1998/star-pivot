package com.star.pivot.controller;

import com.star.pivot.common.domain.DeleteRequest;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.domain.Result;
import com.star.pivot.quartz.domain.bo.SysJobLogVO;
import com.star.pivot.quartz.domain.bo.SysJobVO;
import com.star.pivot.quartz.domain.dto.SysJobCommonDto;
import com.star.pivot.quartz.domain.dto.SysJobDTO;
import com.star.pivot.quartz.domain.dto.SysJobLogQueryDTO;
import com.star.pivot.quartz.domain.dto.SysJobQueryDTO;
import com.star.pivot.quartz.service.ISysJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 定时任务控制器
 *
 * @author StarPivot
 */
@Tag(name = "定时任务")
@RestController
@RequestMapping("/monitor/job")
@RequiredArgsConstructor
public class SysJobController {

    private final ISysJobService sysJobService;

    @Operation(summary = "分页查询定时任务列表")
    @PreAuthorize("hasAuthority('monitor:job:query')")
    @PostMapping("/list")
    public Result<PageResponse<SysJobVO>> list(@RequestBody SysJobQueryDTO query) {
        return Result.success(sysJobService.selectJobPage(query));
    }

    @Operation(summary = "根据ID查询定时任务")
    @PreAuthorize("hasAuthority('monitor:job:query')")
    @GetMapping("/{jobId}")
    public Result<SysJobVO> getInfo(@PathVariable Long jobId) {
        return Result.success(sysJobService.getJobById(jobId));
    }

    @Operation(summary = "新增定时任务")
    @PreAuthorize("hasAuthority('monitor:job:add')")
    @PostMapping
    public Result<?> add(@Valid @RequestBody SysJobDTO dto) {
        sysJobService.insertJob(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "修改定时任务")
    @PreAuthorize("hasAuthority('monitor:job:edit')")
    @PutMapping
    public Result<?> edit(@Valid @RequestBody SysJobDTO dto) {
        sysJobService.updateJob(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "删除定时任务")
    @PreAuthorize("hasAuthority('monitor:job:remove')")
    @DeleteMapping
    public Result<?> remove(@RequestBody DeleteRequest request) {
        List<Long> jobIds = request.getIds();
        if (jobIds == null || jobIds.isEmpty()) {
            return Result.error("删除ID不能为空");
        }
        sysJobService.deleteJobByIds(jobIds);
        return Result.success("删除成功");
    }

    @Operation(summary = "修改任务状态（暂停/恢复）")
    @PreAuthorize("hasAuthority('monitor:job:edit')")
    @PostMapping("/changeStatus")
    public Result<?> changeStatus(@RequestBody SysJobCommonDto commonDto) {
        sysJobService.changeStatus(commonDto.getJobId(), commonDto.getStatus());
        return Result.success("操作成功");
    }

    @Operation(summary = "立即执行一次")
    @PreAuthorize("hasAuthority('monitor:job:edit')")
    @PutMapping("/run/{jobId}")
    public Result<?> runOnce(@PathVariable Long jobId) {
        sysJobService.runOnce(jobId);
        return Result.success("已触发执行");
    }

    @Operation(summary = "分页查询任务执行日志")
    @PreAuthorize("hasAuthority('monitor:job:query')")
    @PostMapping("/log/list")
    public Result<PageResponse<SysJobLogVO>> logList(@RequestBody SysJobLogQueryDTO query) {
        return Result.success(sysJobService.selectJobLogPage(query));
    }

    @Operation(summary = "清空任务日志")
    @PreAuthorize("hasAuthority('monitor:job:remove')")
    @DeleteMapping("/log/clear")
    public Result<?> clearLog() {
        sysJobService.clearJobLog();
        return Result.success("清空成功");
    }
}
