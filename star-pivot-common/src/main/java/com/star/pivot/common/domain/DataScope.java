package com.star.pivot.common.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
// 数据权限DTO（封装SQL片段和参数）
@Data
@NoArgsConstructor
public class DataScope {
    /** 数据权限过滤SQL，如：t.dept_id IN (1,2,3) */
    private String sqlFilter;
    /** 用于预编译的参数（防SQL注入） */
    private List<Long> deptIds;
    /** 当前用户ID（仅本人权限用） */
    private Long userId;
    /** 当前用户所属部门ID（本部门权限用） */
    private Long userDeptId;

    /**
     * 构造函数（兼容旧代码）
     */
    public DataScope(String sqlFilter, List<Long> deptIds, Long userId) {
        this.sqlFilter = sqlFilter;
        this.deptIds = deptIds;
        this.userId = userId;
    }

    /**
     * 完整构造函数
     */
    public DataScope(String sqlFilter, List<Long> deptIds, Long userId, Long userDeptId) {
        this.sqlFilter = sqlFilter;
        this.deptIds = deptIds;
        this.userId = userId;
        this.userDeptId = userDeptId;
    }
}