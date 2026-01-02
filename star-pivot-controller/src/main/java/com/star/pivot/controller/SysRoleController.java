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
     * 角色分页列表
     */
    @PostMapping("/list")
    public Result<PageResponse<SysRole>> list(@RequestBody RoleQueryDTO roleQueryDTO){
        PageResponse<SysRole> result = sysRoleService.selectRoleList(roleQueryDTO);
        return Result.success(result);
    }
    /*
      查询角色下拉列表
     */
    @GetMapping("/select")
    public Result<List<SysRole>> select(){
        return Result.success(sysRoleService.list());
    }
    /**
     * 根据角色ID查询角色详情
     */
    @GetMapping("/{roleId}")
    public Result<SysRole> getInfo(@PathVariable Long roleId) {
        SysRole roleVO = sysRoleService.selectRoleById(roleId);
        return Result.success(roleVO);
    }

    /**
     * 新增角色
     */
    @PostMapping
    public Result<?> add(@Valid @RequestBody RoleDTO roleDTO) {
        boolean success = sysRoleService.insertRole(roleDTO);
        return success ? Result.success("新增角色成功") : Result.error("新增角色失败");
    }

    /**
     * 修改角色
     */
    @PutMapping
    public Result<?> edit(@Valid @RequestBody RoleDTO roleDTO) {
        boolean success = sysRoleService.updateRole(roleDTO);
        return success ? Result.success("修改角色成功") : Result.error("修改角色失败");
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{roleIds}")
    public Result<?> remove(@PathVariable Long[] roleIds) {
        boolean success = sysRoleService.deleteRoleByIds(roleIds);
        return success ? Result.success("删除角色成功") : Result.error("删除角色失败");
    }

    /**
     * 修改角色状态
     */
    @PutMapping("/changeStatus")
    public Result<?> changeStatus(@RequestBody RoleDTO roleDTO) {
        boolean success = sysRoleService.changeRoleStatus(roleDTO.getRoleId(), roleDTO.getStatus());
        return success ? Result.success("修改状态成功") : Result.error("修改状态失败");
    }
}
