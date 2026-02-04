package com.star.pivot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.star.pivot.common.domain.AppConstants;
import com.star.pivot.common.domain.DeleteRequest;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.domain.Result;
import com.star.pivot.system.domain.dto.*;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.domain.entity.UserRole;
import com.star.pivot.system.service.SysRoleService;
import com.star.pivot.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/role")
@Tag(name = "角色管理", description = "角色的增删改查、权限分配、用户分配等接口")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysUserService sysUserService;
    /**
     * 角色分页列表接口
     * 
     * @param roleQueryDTO 角色查询参数对象
     * @return 分页的角色列表结果
     */
    @Operation(summary = "分页查询角色", description = "根据条件分页查询角色列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = PageResponse.class)))
    })
    @PreAuthorize("hasAuthority('system:role:query')")
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
    @Operation(summary = "查询角色下拉列表", description = "获取所有角色列表，用于下拉选择框")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @PreAuthorize("hasAuthority('system:role:query')")
    @GetMapping("/select")
    public Result<List<SysRole>> select(){
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getDelFlag, AppConstants.DelFlag.NORMAL);
        return Result.success(sysRoleService.list());
    }
    /**
     * 根据角色ID查询角色详情接口
     * 
     * @param roleId 角色ID
     * @return 指定ID的角色信息
     */
    @Operation(summary = "获取角色详情", description = "根据角色ID获取角色的详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = SysRole.class))),
            @ApiResponse(responseCode = "404", description = "角色不存在")
    })
    @PreAuthorize("hasAuthority('system:role:query')")
    @GetMapping("/{roleId}")
    public Result<SysRole> getInfo(@Parameter(description = "角色ID") @PathVariable("roleId") Long roleId) {
        SysRole roleVO = sysRoleService.selectRoleById(roleId);
        return Result.success(roleVO);
    }

    /**
     * 新增角色接口
     * 
     * @param roleDTO 角色数据传输对象
     * @return 操作结果，成功或失败的响应
     */
    @Operation(summary = "新增角色", description = "创建新角色，需要提供角色名称、角色键值、权限等信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "新增成功"),
            @ApiResponse(responseCode = "400", description = "参数错误或角色键值已存在")
    })
    @PreAuthorize("hasAuthority('system:role:add')")
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
    @Operation(summary = "修改角色", description = "更新角色信息，包括角色名称、权限等")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "404", description = "角色不存在")
    })
    @PreAuthorize("hasAuthority('system:role:edit')")
    @PutMapping("updateRole")
    public Result<?> edit(@Valid @RequestBody RoleDTO roleDTO) {
        boolean success = sysRoleService.updateRole(roleDTO);
        return success ? Result.success("修改角色成功") : Result.error("修改角色失败");
    }

    /**
     * 删除角色接口（支持单删和批量删除）
     * 
     * @param deleteRequest 删除请求，包含 ids 数组
     * @return 操作结果，成功或失败的响应
     */
    @Operation(summary = "删除角色", description = "删除角色（支持批量删除）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "删除ID为空")
    })
    @PreAuthorize("hasAuthority('system:role:delete')")
    @DeleteMapping("/delete")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> roleIds = deleteRequest.getIds();
        if (roleIds == null || roleIds.isEmpty()) {
            return Result.error("删除ID不能为空");
        }
        boolean success = sysRoleService.deleteRoleByIds(roleIds);
        return success ? Result.success("删除角色成功") : Result.error("删除角色失败");
    }

    /**
     * 修改角色状态接口
     * 
     * @param roleDTO 角色数据传输对象，包含角色ID和状态
     * @return 操作结果，成功或失败的响应
     */
    @Operation(summary = "修改角色状态", description = "启用或禁用角色")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "404", description = "角色不存在")
    })
    @PreAuthorize("hasAuthority('system:role:edit')")
    @PutMapping("/changeStatus")
    public Result<?> changeStatus(@RequestBody RoleDTO roleDTO) {
        boolean success = sysRoleService.changeRoleStatus(roleDTO.getRoleId(), roleDTO.getStatus());
        return success ? Result.success("修改状态成功") : Result.error("修改状态失败");
    }
    /**
     * 分配角色权限接口 部门权限
     */
    @Operation(summary = "分配角色数据权限", description = "为角色分配数据权限（部门权限范围）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "分配成功"),
            @ApiResponse(responseCode = "404", description = "角色不存在")
    })
    @PreAuthorize("hasAuthority('system:role:assignDataScope')")
    @PostMapping("/assignPermission")
    public Result<?> assignPermission(@RequestBody RolePermissionAssignDTO rolePermissionAssignDTO) {
        boolean success = sysRoleService.assignPermission(rolePermissionAssignDTO);
        return success ? Result.success("分配权限成功") : Result.error("分配权限失败");
    }
    /**
     * 根据角色ID查询部门ID列表接口
     *
     * @param roleId 角色ID
     * @return 该角色关联的部门ID列表
     */
    @Operation(summary = "获取角色的部门ID列表", description = "根据角色ID获取该角色关联的部门ID列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @PreAuthorize("hasAuthority('system:role:query')")
    @GetMapping("/{roleId}/deptIds")
    public Result<List<Long>> getDeptIds(@Parameter(description = "角色ID") @PathVariable("roleId") Long roleId) {
        List<Long> deptIds = sysRoleService.selectDeptIdsByRoleId(roleId);
        return Result.success(deptIds);
    }
    /**
     * 根据角色ID获取菜单接口
     *
     * @param roleId 角色ID
     * @return 指定角色拥有的菜单列表
     */
    @Operation(summary = "获取角色的菜单ID列表", description = "根据角色ID获取该角色拥有的菜单ID列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @PreAuthorize("hasAuthority('system:role:query')")
    @GetMapping("/getMenuIdsByRoleId/{roleId}")
    public Result<List<Long>> getMenuIdsByRoleId(@Parameter(description = "角色ID") @PathVariable("roleId") Long roleId){
        List<Long> menuIds = sysRoleService.getMenuIdsByRoleId(roleId);
        return Result.success("查询成功", menuIds);
    }
    /**
      * 根据角色id获取已分配的用户列表 分页  allocatedList
     * @param assignUserReqBo 用户请求参数对象
     * @return 指定角色拥有的用户列表
     */
    @Operation(summary = "查询已分配用户", description = "根据角色ID分页查询已分配该角色的用户列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = PageResponse.class)))
    })
    @PreAuthorize("hasAuthority('system:role:allocatedList')")
    @PostMapping("/allocatedList")
    public Result<PageResponse<SysUser>> getUserListByRoleId(@RequestBody AssignUserReqBo assignUserReqBo){
        PageResponse<SysUser> userPage = sysUserService.getUserListByRoleId(assignUserReqBo);
        return Result.success("查询成功", userPage);
    }

    /**
     * 根据角色id获取 不属于该角色的用户列表 分页
     * @param assignUserReqBo 用户请求参数对象
     * @return 指定角色拥有的用户列表
     */
    @Operation(summary = "查询未分配用户", description = "根据角色ID分页查询未分配该角色的用户列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = PageResponse.class)))
    })
    @PreAuthorize("hasAuthority('system:role:unallocatedList')")
    @PostMapping("/unallocatedList")
    public Result<PageResponse<SysUser>> getUserListNotInByRoleId(@RequestBody AssignUserReqBo assignUserReqBo){
        PageResponse<SysUser> userPage = sysUserService.unallocatedList(assignUserReqBo);
        return Result.success("查询成功", userPage);
    }
    /**
     * 添加userId 和 roleId  到sys_user_role
     *
     */
    @Operation(summary = "分配用户", description = "为角色分配用户，建立用户与角色的关联关系")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "分配成功")
    })
    @PreAuthorize("hasAuthority('system:role:assignUser')")
    @PostMapping("/assignUser")
    public Result<?> assignUser(@RequestBody UserRoleDTO userRoleDTO) {
        boolean success = sysRoleService.assignUser(userRoleDTO);
        return success ? Result.success("分配用户成功") : Result.error("分配用户失败");
    }
    /*
     * 根据角色id 用户id 取消授权的用户
     */
    @Operation(summary = "取消用户授权", description = "取消用户与角色的关联关系")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "取消成功")
    })
    @PreAuthorize("hasAuthority('system:role:cancelUser')")
    @DeleteMapping("/cancelUser")
    public Result<?> cancelUser(@RequestBody UserRole userRole) {
        boolean success = sysRoleService.cancelUser(userRole);
        return success ? Result.success("取消授权成功") : Result.error("取消授权失败");
    }
}
