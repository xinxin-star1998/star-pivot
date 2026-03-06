package com.star.pivot.controller;

import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.ServiceException;
import com.star.pivot.system.domain.bo.DeptVO;
import com.star.pivot.system.domain.dto.DeptDTO;
import com.star.pivot.system.service.SysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "部门管理", description = "部门的增删改查、部门树查询等接口")
public class SysDeptController {

    private final SysDeptService deptService;

    /**
     * 查询部门树列表接口
     * 
     * @return 部门树列表，包含所有部门及其层级关系
     */
    @Operation(summary = "查询部门树", description = "获取所有部门的树形结构")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
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
    @Operation(summary = "获取部门详情", description = "根据部门ID获取部门的详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = DeptVO.class))),
            @ApiResponse(responseCode = "404", description = "部门不存在")
    })
    @PreAuthorize("hasAuthority('system:dept:query')")
    @GetMapping("/{deptId}")
    public Result<DeptVO> getInfo(@Parameter(description = "部门ID") @PathVariable("deptId") Long deptId) {
        DeptVO deptVO = deptService.selectDeptById(deptId);
        return Result.success(deptVO);
    }

    /**
     * 新增部门接口
     * 
     * @param deptDTO 部门数据传输对象
     * @return 操作结果，成功或失败的响应
     */
    @Operation(summary = "新增部门", description = "创建新部门，需要提供部门名称、上级部门等信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "新增成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
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
    @Operation(summary = "修改部门", description = "更新部门信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "404", description = "部门不存在")
    })
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
    @Operation(summary = "删除部门", description = "删除部门（支持批量删除），如果部门有子部门或用户则不能删除")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "删除ID为空或存在子部门/用户")
    })
    @PreAuthorize("hasAuthority('system:dept:delete')")
    @DeleteMapping("/delete")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> deptIds = validateIds(deleteRequest.getIds());
        boolean success = deptService.deleteDeptByIds(deptIds);
        return success ? Result.success("删除部门成功") : Result.error("删除部门失败");
    }

    /**
     * 验证ID列表非空
     */
    private List<Long> validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException(ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        return ids;
    }
}

