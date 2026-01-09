package com.star.pivot.controller;

import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.domain.Result;
import com.star.pivot.system.domain.dto.RoleDTO;
import com.star.pivot.system.domain.dto.RoleQueryDTO;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.service.SysRoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/role")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 角色分页列表接口
     * 
     * @param roleQueryDTO 角色查询参数对象
     * @return 分页的角色列表结果
     */
    @PostMapping("/list")
    public Result<PageResponse<SysRole>> list(@RequestBody RoleQueryDTO roleQueryDTO){
        PageResponse<SysRole> result = sysRoleService.selectRoleList(roleQueryDTO);
        return Result.success(result);
    }
    /**
     * 查询角色下拉列表接口
     * 
     * @return 所有角色列表，用于下拉选择
     */
    @GetMapping("/select")
    public Result<List<SysRole>> select(){
        return Result.success(sysRoleService.list());
    }
    /**
     * 根据角色ID查询角色详情接口
     * 
     * @param roleId 角色ID
     * @return 指定ID的角色信息
     */
    @GetMapping("/{roleId}")
    public Result<SysRole> getInfo(@PathVariable("roleId") Long roleId) {
        SysRole roleVO = sysRoleService.selectRoleById(roleId);
        return Result.success(roleVO);
    }

    /**
     * 新增角色接口
     * 
     * @param roleDTO 角色数据传输对象
     * @return 操作结果，成功或失败的响应
     */
    @PostMapping("/addRole")
    public Result<?> add(@Valid @RequestBody RoleDTO roleDTO) {
        boolean success = sysRoleService.insertRole(roleDTO);
        return success ? Result.success("新增角色成功") : Result.error("新增角色失败");
    }

    /**
     * 修改角色接口
     * 
     * @param roleDTO 角色数据传输对象
     * @return 操作结果，成功或失败的响应
     */
    @PutMapping("updateRole")
    public Result<?> edit(@Valid @RequestBody RoleDTO roleDTO) {
        boolean success = sysRoleService.updateRole(roleDTO);
        return success ? Result.success("修改角色成功") : Result.error("修改角色失败");
    }

    /**
     * 删除角色接口
     * 
     * @param roleIds 角色ID数组
     * @return 操作结果，成功或失败的响应
     */
    @DeleteMapping("/{roleIds}")
    public Result<?> remove(@PathVariable("roleIds") Long[] roleIds) {
        boolean success = sysRoleService.deleteRoleByIds(roleIds);
        return success ? Result.success("删除角色成功") : Result.error("删除角色失败");
    }

    /**
     * 修改角色状态接口
     * 
     * @param roleDTO 角色数据传输对象，包含角色ID和状态
     * @return 操作结果，成功或失败的响应
     */
    @PutMapping("/changeStatus")
    public Result<?> changeStatus(@RequestBody RoleDTO roleDTO) {
        boolean success = sysRoleService.changeRoleStatus(roleDTO.getRoleId(), roleDTO.getStatus());
        return success ? Result.success("修改状态成功") : Result.error("修改状态失败");
    }
    /**
     * 根据角色ID查询部门ID列表接口
     * 
     * @param roleId 角色ID
     * @return 该角色关联的部门ID列表
     */
    @GetMapping("/{roleId}/deptIds")
    public Result<List<Long>> getDeptIds(@PathVariable("roleId") Long roleId) {
        List<Long> deptIds = sysRoleService.selectDeptIdsByRoleId(roleId);
        return Result.success(deptIds);
    }
}
