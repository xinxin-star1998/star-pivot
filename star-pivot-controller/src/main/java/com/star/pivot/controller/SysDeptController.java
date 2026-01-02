package com.star.pivot.controller;

import com.star.pivot.common.domain.Result;
import com.star.pivot.system.domain.bo.DeptVO;
import com.star.pivot.system.domain.dto.DeptDTO;
import com.star.pivot.system.service.SysDeptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 *
 * @author stardust
 * @date 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class SysDeptController {

    private final SysDeptService deptService;

    /**
     * 查询部门树列表
     */
    @GetMapping("/tree")
    public Result<List<DeptVO>> tree() {
        List<DeptVO> deptTree = deptService.selectDeptTree();
        return Result.success(deptTree);
    }

    /**
     * 根据部门ID查询部门详情
     */
    @GetMapping("/{deptId}")
    public Result<DeptVO> getInfo(@PathVariable Long deptId) {
        DeptVO deptVO = deptService.selectDeptById(deptId);
        return Result.success(deptVO);
    }

    /**
     * 新增部门
     */
    @PostMapping
    public Result<?> add(@Valid @RequestBody DeptDTO deptDTO) {
        boolean success = deptService.insertDept(deptDTO);
        return success ? Result.success("新增部门成功") : Result.error("新增部门失败");
    }

    /**
     * 修改部门
     */
    @PutMapping
    public Result<?> edit(@Valid @RequestBody DeptDTO deptDTO) {
        boolean success = deptService.updateDept(deptDTO);
        return success ? Result.success("修改部门成功") : Result.error("修改部门失败");
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{deptId}")
    public Result<?> remove(@PathVariable Long deptId) {
        boolean success = deptService.deleteDept(deptId);
        return success ? Result.success("删除部门成功") : Result.error("删除部门失败");
    }
}

