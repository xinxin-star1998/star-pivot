package com.star.pivot.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.star.pivot.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 角色信息表(SysRole)表实体类
 *
 * @author makejava
 * @since 2025-12-28 19:26:30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_role")
public class SysRole extends BaseEntity implements Serializable {
    /**
     * 角色ID
     */
    @TableId(type = IdType.AUTO, value = "role_id")
    private Long roleId;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 角色权限字符串
     */
    private String roleKey;
    /**
     * 显示顺序
     */
    private Integer roleSort;
    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
     */
    private String dataScope;
    /**
     * 菜单树选择项是否关联显示
     */
    private Integer menuCheckStrictly;
    /**
     * 部门树选择项是否关联显示
     */
    private Integer deptCheckStrictly;
    /**
     * 角色状态（0正常 1停用）
     */
    private String status;
}

