package com.star.pivot.system.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class RolePermissionAssignDTO {
    /**
     * 角色ID（必填）
     */
    @NotNull(message = "角色ID不能为空")
    private Long roleId;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 角色编码
     */
    private String roleKey;
    /**
     * 数据范围
     */
    private String dataScope;
//    /**
//     * 菜单ID列表（可为空，空则表示清空该角色所有菜单权限）
//     */
//    private List<Long> menuIds;

    /**
     * 部门ID列表（可为空，空则表示清空该角色所有部门权限）
     */
    private List<Long> deptIds;
}