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
     * 菜单ID列表（可为空，空则表示清空该角色所有菜单权限）
     */
    private List<Long> menuIds;

    /**
     * 部门ID列表（可为空，空则表示清空该角色所有部门权限）
     */
    private List<Long> deptIds;

    /*
     * 操作人ID（用于审计日志）
     */
//    @NotNull(message = "操作人ID不能为空")
//    private Long operatorId;
}