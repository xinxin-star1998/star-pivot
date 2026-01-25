package com.star.pivot.controller;

import com.star.pivot.common.domain.DeleteRequest;
import com.star.pivot.common.domain.Result;
import com.star.pivot.system.domain.bo.DeptVO;
import com.star.pivot.system.domain.dto.DeptDTO;
import com.star.pivot.system.service.SysDeptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 *
 * @author stardust
 */
@Slf4j
@RestController
@RequestMapping("/sys/dept")
@RequiredArgsConstructor
public class SysDeptController {

    private final SysDeptService deptService;

    /**
     * 查询部门树列表接口
     * 
     * @return 部门树列表，包含所有部门及其层级关系
     */
    @PreAuthorize("hasAuthority('system:dept:query')")
    @GetMapping("/tree")
    public Result<List<DeptVO>> tree() {
        List<DeptVO> deptTree = deptService.selectDeptTree();
        return Result.success(deptTree);
    }

    /**
     * 根据部门ID查询部门详情接口
     * 
     * @param deptId 部门ID
     * @return 指定ID的部门详细信息
     */
    @PreAuthorize("hasAuthority('system:dept:query')")
    @GetMapping("/{deptId}")
    public Result<DeptVO> getInfo(@PathVariable("deptId") Long deptId) {
        DeptVO deptVO = deptService.selectDeptById(deptId);
        return Result.success(deptVO);
    }

    /**
     * 新增部门接口
     * 
     * @param deptDTO 部门数据传输对象
     * @return 操作结果，成功或失败的响应
     */
    @PreAuthorize("hasAuthority('system:dept:add')")
    @PostMapping
    public Result<?> add(@Valid @RequestBody DeptDTO deptDTO) {
        boolean success = deptService.insertDept(deptDTO);
        return success ? Result.success("新增部门成功") : Result.error("新增部门失败");
    }

    /**
     * 修改部门接口
     * 
     * @param deptDTO 部门数据传输对象
     * @return 操作结果，成功或失败的响应
     */
    @PreAuthorize("hasAuthority('system:dept:edit')")
    @PutMapping
    public Result<?> edit(@Valid @RequestBody DeptDTO deptDTO) {
        boolean success = deptService.updateDept(deptDTO);
        return success ? Result.success("修改部门成功") : Result.error("修改部门失败");
    }

    /**
     * 删除部门接口（支持单删和批量删除）
     * 
     * @param deleteRequest 删除请求，包含 ids 数组
     * @return 操作结果，成功或失败的响应
     */
    @PreAuthorize("hasAuthority('system:dept:delete')")
    @DeleteMapping("/delete")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> deptIds = deleteRequest.getIds();
        if (deptIds == null || deptIds.isEmpty()) {
            return Result.error("删除ID不能为空");
        }
        boolean success = deptService.deleteDeptByIds(deptIds);
        return success ? Result.success("删除部门成功") : Result.error("删除部门失败");
    }
}

