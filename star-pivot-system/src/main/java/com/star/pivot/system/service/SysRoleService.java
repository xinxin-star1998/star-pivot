package com.star.pivot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.system.domain.dto.RoleDTO;
import com.star.pivot.system.domain.dto.RoleQueryDTO;
import com.star.pivot.system.domain.entity.SysRole;

/**
 * 角色信息表(SysRole)表服务接口
 *
 * @author makejava
 * @since 2025-12-28 19:32:10
 */
public interface SysRoleService extends IService<SysRole> {

    PageResponse<SysRole> selectRoleList(RoleQueryDTO roleQueryDTO);

    SysRole selectRoleById(Long roleId);

    boolean insertRole(RoleDTO roleDTO);

    boolean updateRole(RoleDTO roleDTO);

    boolean deleteRoleByIds(Long[] roleIds);

    boolean changeRoleStatus(Long roleId, String status);
}

