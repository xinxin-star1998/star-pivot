package com.star.pivot.system.utils;

import com.star.pivot.common.domain.Constants;
import com.star.pivot.system.domain.entity.SysDept;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.mapper.RoleDeptMapper;
import com.star.pivot.system.mapper.SysDeptMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据权限工具类
 * 用于解析当前用户的数据权限范围，并提供可访问的部门ID列表
 *
 * @author stardust
 * @since 2026-01-24
 */
public class DataScopeHelper {

    /**
     * 数据权限范围结果
     */
    public static class DataScopeResult {
        /**
         * 数据权限类型（1：全部 2：自定 3：本部门 4：本部门及以下 5：仅本人）
         */
        private String dataScope;

        /**
         * 可访问的部门ID列表（当 dataScope 为 2、3、4 时使用）
         */
        private List<Long> deptIds;

        /**
         * 当前用户ID（当 dataScope 为 5 时使用）
         */
        private Long userId;

        /**
         * 是否需要数据权限过滤（false 表示全部数据权限，不需要过滤）
         */
        private boolean needFilter;

        public DataScopeResult(String dataScope, List<Long> deptIds, Long userId, boolean needFilter) {
            this.dataScope = dataScope;
            this.deptIds = deptIds;
            this.userId = userId;
            this.needFilter = needFilter;
        }

        public String getDataScope() {
            return dataScope;
        }

        public List<Long> getDeptIds() {
            return deptIds;
        }

        public Long getUserId() {
            return userId;
        }

        public boolean isNeedFilter() {
            return needFilter;
        }
    }

    /**
     * 获取当前用户的数据权限范围
     *
     * @param currentUser 当前用户信息
     * @param roles 当前用户的角色列表
     * @param roleDeptMapper 角色部门关联Mapper
     * @param sysDeptMapper 部门Mapper
     * @return 数据权限范围结果
     */
    public static DataScopeResult getDataScope(SysUser currentUser, List<SysRole> roles,
                                               RoleDeptMapper roleDeptMapper, SysDeptMapper sysDeptMapper) {
        if (currentUser == null) {
            // 未登录用户，返回空结果（需要过滤，但无权限）
            return new DataScopeResult(null, new ArrayList<>(), null, true);
        }

        Long currentUserId = currentUser.getUserId();

        if (roles == null || roles.isEmpty()) {
            // 无角色，返回空结果（需要过滤，但无权限）
            return new DataScopeResult(null, new ArrayList<>(), currentUserId, true);
        }

        // 检查是否有超级管理员角色（admin 角色拥有全部数据权限）
        boolean hasAdminRole = roles.stream()
                .anyMatch(role -> Constants.ADMIN_ROLE_KEY.equals(role.getRoleKey()));
        if (hasAdminRole) {
            // 超级管理员拥有全部数据权限，不需要过滤
            return new DataScopeResult(Constants.DataScope.ALL, null, currentUserId, false);
        }

        // 获取所有角色的数据权限范围，取最宽松的权限（优先级：全部 > 自定/本部门及以下 > 本部门 > 仅本人）
        Set<Long> allDeptIds = new HashSet<>();
        boolean hasAllScope = false;
        boolean hasDeptAndChildScope = false;
        boolean hasDeptScope = false;
        boolean hasSelfScope = false;

        for (SysRole role : roles) {
            String dataScope = role.getDataScope();
            if (dataScope == null) {
                continue;
            }

            switch (dataScope) {
                case Constants.DataScope.ALL:
                    // 全部数据权限，直接返回
                    hasAllScope = true;
                    break;
                case Constants.DataScope.CUSTOM:
                    // 自定数据权限，查询角色关联的部门
                    if (roleDeptMapper != null) {
                        List<Long> customDeptIds = roleDeptMapper.selectDeptIdsByRoleId(role.getRoleId());
                        if (customDeptIds != null && !customDeptIds.isEmpty()) {
                            allDeptIds.addAll(customDeptIds);
                        }
                    }
                    break;
                case Constants.DataScope.DEPT:
                    // 本部门数据权限
                    hasDeptScope = true;
                    if (currentUser.getDeptId() != null) {
                        allDeptIds.add(currentUser.getDeptId());
                    }
                    break;
                case Constants.DataScope.DEPT_AND_CHILD:
                    // 本部门及以下数据权限
                    hasDeptAndChildScope = true;
                    if (currentUser.getDeptId() != null && sysDeptMapper != null) {
                        // 查询本部门及所有子部门
                        List<Long> deptAndChildIds = getDeptAndChildIds(currentUser.getDeptId(), sysDeptMapper);
                        allDeptIds.addAll(deptAndChildIds);
                    }
                    break;
                case Constants.DataScope.SELF:
                    // 仅本人数据权限
                    hasSelfScope = true;
                    break;
            }
        }

        // 确定最终的数据权限范围
        if (hasAllScope) {
            // 有全部数据权限，不需要过滤
            return new DataScopeResult(Constants.DataScope.ALL, null, currentUserId, false);
        }
        
        // 合并所有部门ID
        List<Long> finalDeptIds = new ArrayList<>(allDeptIds);
        if (hasDeptAndChildScope && currentUser.getDeptId() != null && sysDeptMapper != null) {
            // 确保包含本部门及子部门
            List<Long> deptAndChildIds = getDeptAndChildIds(currentUser.getDeptId(), sysDeptMapper);
            finalDeptIds.addAll(deptAndChildIds);
        }
        // 去重
        finalDeptIds = finalDeptIds.stream().distinct().collect(Collectors.toList());
        
        // 如果同时有部门权限和仅本人权限，取并集（可以看到自己部门的用户，也可以看到自己）
        if (hasSelfScope && !finalDeptIds.isEmpty()) {
            // 同时有仅本人和部门权限，返回特殊标记，SQL中会用 OR 条件
            return new DataScopeResult("6", finalDeptIds, currentUserId, true); // "6" 表示仅本人+部门权限
        } else if (hasSelfScope) {
            // 只有仅本人权限
            return new DataScopeResult(Constants.DataScope.SELF, null, currentUserId, true);
        } else if (!finalDeptIds.isEmpty()) {
            // 只有部门权限
            return new DataScopeResult(
                    hasDeptAndChildScope ? Constants.DataScope.DEPT_AND_CHILD : 
                    (hasDeptScope ? Constants.DataScope.DEPT : Constants.DataScope.CUSTOM),
                    finalDeptIds,
                    currentUserId,
                    true
            );
        } else {
            // 无任何数据权限，返回空结果
            return new DataScopeResult(null, new ArrayList<>(), currentUserId, true);
        }
    }

    /**
     * 获取部门及其所有子部门的ID列表
     *
     * @param deptId 部门ID
     * @param sysDeptMapper 部门Mapper
     * @return 部门ID列表（包含自身）
     */
    private static List<Long> getDeptAndChildIds(Long deptId, SysDeptMapper sysDeptMapper) {
        List<Long> result = new ArrayList<>();
        if (deptId == null || sysDeptMapper == null) {
            if (deptId != null) {
                result.add(deptId);
            }
            return result;
        }

        // 添加自身
        result.add(deptId);

        // 查询所有部门
        List<SysDept> allDepts = sysDeptMapper.selectList(null);
        
        // 查找所有子部门（通过 ancestors 字段）
        // ancestors 格式说明：
        // - 部门100: parent_id=0, ancestors='0'
        // - 部门101: parent_id=100, ancestors='0,100'（父部门的ancestors + ',' + 父部门的deptId）
        // - 部门102: parent_id=101, ancestors='0,100,101'
        // 要查找部门100的所有子部门，需要查找 ancestors 包含 "0,100" 的部门
        // 具体：ancestors 等于 "0,100" 或 ancestors 以 "0,100," 开头
        
        // 先获取当前部门的 ancestors
        SysDept currentDept = sysDeptMapper.selectById(deptId);
        if (currentDept == null) {
            return result;
        }
        
        String currentAncestors = currentDept.getAncestors();
        String targetAncestors = (currentAncestors == null || currentAncestors.isEmpty()) 
                ? deptId.toString() 
                : currentAncestors + "," + deptId;
        
        for (SysDept dept : allDepts) {
            if (dept.getAncestors() != null && dept.getDeptId() != null && !dept.getDeptId().equals(deptId)) {
                String ancestors = dept.getAncestors();
                // 检查 ancestors 是否等于 targetAncestors（直接子部门）或以 targetAncestors + "," 开头（间接子部门）
                if (ancestors.equals(targetAncestors) || ancestors.startsWith(targetAncestors + ",")) {
                    result.add(dept.getDeptId());
                }
            }
        }

        return result;
    }
}
