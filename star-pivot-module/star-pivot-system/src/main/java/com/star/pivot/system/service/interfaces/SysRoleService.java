package com.star.pivot.system.service.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.system.domain.dto.RoleDTO;
import com.star.pivot.system.domain.dto.RolePermissionAssignDTO;
import com.star.pivot.system.domain.dto.RoleQueryDTO;
import com.star.pivot.system.domain.dto.UserRoleDTO;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.domain.entity.UserRole;

import java.util.List;

/**
 * 角色信息表(SysRole)表服务接口
 *
 * @author makejava
 * @since 2025-12-28 19:32:10
 */
public interface SysRoleService extends IService<SysRole> {

    PageResponse<SysRole> selectRoleList(RoleQueryDTO roleQueryDTO);

    SysRole selectRoleById(Long roleId);

    /**
     * 获取所有有效角色列表（用于下拉选择等场景）
     * @return 角色列表
     */
    List<SysRole> selectAllRoles();

    boolean insertRole(RoleDTO roleDTO);

    boolean updateRole(RoleDTO roleDTO);

    /**
     * 删除角色（支持单删和批量删除）
     *
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean deleteRoleByIds(List<Long> roleIds);

    boolean changeRoleStatus(Long roleId, String status);

    List<Long> selectDeptIdsByRoleId(Long roleId);

    boolean assignPermission(RolePermissionAssignDTO rolePermissionAssignDTO);

    List<Long> getMenuIdsByRoleId(Long roleId);

    boolean assignUser(UserRoleDTO userRoleDTO);

    boolean cancelUser(UserRole userRole);
}

