package com.star.pivot.system.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.DataScope;
import com.star.pivot.system.domain.entity.SysDept;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.mapper.RoleDeptMapper;
import com.star.pivot.system.mapper.SysDeptMapper;
import com.star.pivot.system.mapper.SysRoleMapper;
import com.star.pivot.system.mapper.SysUserMapper;
import com.star.pivot.security.utils.SecurityContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 数据权限服务。
 * <p>
 * 根据当前登录用户的角色数据范围（全部 / 自定义部门 / 本部门及以下 / 本部门 / 仅本人），
 * 计算并返回用于 SQL 过滤的 {@link DataScope}，供 MyBatis 拦截器或业务层拼接数据权限条件。
 * </p>
 *
 * @author stardust
 * @since 2024-01-01
 */
@Slf4j
@Service
public class DataScopeService {

    /** SQL 无数据：使用空 IN 条件（避免 Druid 将 1=0 判为注入而拦截） */
    private static final String SQL_NONE = "EMPTY_IN";
    /** SQL 全部数据：恒真条件，用于超级管理员或全部数据权限 */
    private static final String SQL_ALL = "1=1";
    /** MyBatis 用户部门 ID 占位符，表别名使用 u */
    private static final String PLACEHOLDER_USER_DEPT_ID = "u.dept_id = #{param.userDeptId}";
    /** MyBatis 创建人占位符（仅本人权限），表别名使用 u */
    private static final String PLACEHOLDER_USER_ID = "u.create_by = #{param.userId}";

    /** 数据权限优先级：1(全部) > 2(自定义) > 3(本部门) > 4(本部门及以下) > 5(仅本人) */
    private static final Map<String, Integer> SCOPE_PRIORITY = Map.of(
            AppConstants.DataScope.ALL, 1,
            AppConstants.DataScope.CUSTOM, 2,
            AppConstants.DataScope.DEPT, 3,
            AppConstants.DataScope.DEPT_AND_CHILD, 4,
            AppConstants.DataScope.SELF, 5
    );

    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysDeptMapper deptMapper;
    @Autowired
    private RoleDeptMapper roleDeptMapper;

    /**
     * 获取当前登录用户的数据权限。
     * <p>
     * 流程：未登录 → 无数据；超级管理员（userId=1）→ 全部数据；
     * 否则按角色数据范围取最大权限（1 &gt; 2 &gt; 3 &gt; 4 &gt; 5），并汇总涉及的部门 ID。
     * </p>
     *
     * @return 数据权限对象，含 SQL 片段、部门 ID 列表、当前用户 ID、用户部门 ID
     */
    public DataScope getCurrentUserDataScope() {
        Long userId = SecurityContextUtils.getUserId();
        if (userId == null) {
            log.debug("数据权限：未登录，返回无数据");
            return createNoDataScope();
        }

        // 统一在方法开始处查询用户信息，避免在多个分支中重复查询
        SysUser user = userMapper.selectById(userId);
        Long userDeptId = user != null ? user.getDeptId() : null;

        // 检查超级管理员（合并角色查询，避免重复）
        List<SysRole> roleList = roleMapper.selectRolesByUserId(userId);
        if (isSuperAdmin(userId, roleList)) {
            log.debug("数据权限：超级管理员 userId={}，返回全部数据", userId);
            return new DataScope(SQL_ALL, Collections.emptyList(), userId, userDeptId);
        }

        if (CollectionUtils.isEmpty(roleList)) {
            log.debug("数据权限：用户 userId={} 无角色，返回无数据", userId);
            return new DataScope(SQL_NONE, Collections.emptyList(), userId, userDeptId);
        }

        // 计算最终数据权限（取最高优先级）
        ScopeResult result = calculateDataScope(roleList, userDeptId);
        // 构建 SQL 过滤条件
        String sqlFilter = buildSqlFilter(result.scopeType, userId, result.deptIds, userDeptId);

        log.debug("数据权限：userId={}, scopeType={}, deptIds={}, userDeptId={}", userId, result.scopeType, result.deptIds, userDeptId);
        // 仅本人时只传 userId，不传 userDeptId，避免 mapper 先匹配 dept 条件导致未按 create_by 过滤
        Long scopeUserDeptId = AppConstants.DataScope.SELF.equals(result.scopeType) ? null : userDeptId;
        return new DataScope(sqlFilter, new ArrayList<>(result.deptIds), userId, scopeUserDeptId);
    }

    /**
     * 计算数据权限范围（取最高优先级）
     */
    private ScopeResult calculateDataScope(List<SysRole> roleList, Long userDeptId) {
        String highestScope = AppConstants.DataScope.SELF;
        Set<Long> deptIdSet = new HashSet<>();

        for (SysRole role : roleList) {
            String scope = Optional.ofNullable(role.getDataScope()).map(String::trim).orElse(AppConstants.DataScope.SELF);
            
            // 如果遇到全部权限，直接返回
            if (AppConstants.DataScope.ALL.equals(scope)) {
                return new ScopeResult(AppConstants.DataScope.ALL, Collections.emptySet());
            }

            // 比较优先级，只处理更高优先级的权限
            if (isHigherPriority(scope, highestScope)) {
                highestScope = scope;
                deptIdSet.clear();
                processScope(scope, role.getRoleId(), userDeptId, deptIdSet);
            } else if (scope.equals(highestScope)) {
                // 相同优先级的权限处理
                if (AppConstants.DataScope.CUSTOM.equals(scope)) {
                    // 自定义权限：累加多个角色的部门ID
                    addCustomDeptIds(role.getRoleId(), deptIdSet);
                }
                // 其他类型（DEPT、DEPT_AND_CHILD、SELF）基于用户部门或用户ID计算，不需要累加
            }
        }

        return new ScopeResult(highestScope, deptIdSet);
    }

    /**
     * 判断 scope1 是否比 scope2 优先级更高
     */
    private boolean isHigherPriority(String scope1, String scope2) {
        return SCOPE_PRIORITY.getOrDefault(scope1, 99) < SCOPE_PRIORITY.getOrDefault(scope2, 99);
    }

    /**
     * 处理特定权限类型，填充部门ID集合
     */
    private void processScope(String scope, Long roleId, Long userDeptId, Set<Long> deptIdSet) {
        switch (scope) {
            case AppConstants.DataScope.CUSTOM:
                addCustomDeptIds(roleId, deptIdSet);
                break;
            case AppConstants.DataScope.DEPT_AND_CHILD:
                if (userDeptId != null) {
                    deptIdSet.addAll(selectDeptIdsByParentId(userDeptId));
                }
                break;
            case AppConstants.DataScope.DEPT:
                if (userDeptId != null) {
                    deptIdSet.add(userDeptId);
                }
                break;
            case AppConstants.DataScope.SELF:
            default:
                // 仅本人权限，不需要部门ID
                break;
        }
    }

    /**
     * 添加自定义权限的部门ID
     */
    private void addCustomDeptIds(Long roleId, Set<Long> deptIdSet) {
        List<Long> ids = roleDeptMapper.selectDeptIdsByRoleId(roleId);
        if (ids != null) {
            deptIdSet.addAll(ids);
        }
    }

    /**
     * 判断是否为超级管理员
     */
    private boolean isSuperAdmin(Long userId, List<SysRole> roleList) {
        if (!AppConstants.ADMIN_USER_ID.equals(userId)) {
            return false;
        }
        return roleList.stream().anyMatch(role ->
                AppConstants.ADMIN_ROLE_KEY.equals(role.getRoleKey()) ||
                AppConstants.DataScope.ALL.equals(role.getDataScope())
        );
    }

    /**
     * 构建数据权限 SQL 过滤条件。
     * <p>
     * 注意：对于部门ID列表，直接拼接实际值（部门ID为Long类型，来自数据库，无SQL注入风险）。
     * 对于用户ID和部门ID，使用占位符以支持参数化查询。
     * </p>
     */
    private String buildSqlFilter(String dataScopeType, Long userId, Set<Long> deptIdSet, Long userDeptId) {
        switch (dataScopeType) {
            case AppConstants.DataScope.ALL:
                return SQL_ALL;
            case AppConstants.DataScope.CUSTOM:
            case AppConstants.DataScope.DEPT_AND_CHILD:
                if (CollectionUtils.isEmpty(deptIdSet)) {
                    return SQL_NONE;
                }
                // 直接拼接部门ID列表（Long类型，无SQL注入风险）
                return "u.dept_id IN (" + String.join(",", deptIdSet.stream().map(String::valueOf).toList()) + ")";
            case AppConstants.DataScope.DEPT:
                if (userDeptId == null) {
                    return SQL_NONE;
                }
                return PLACEHOLDER_USER_DEPT_ID;
            case AppConstants.DataScope.SELF:
                return PLACEHOLDER_USER_ID;
            default:
                return SQL_NONE;
        }
    }

    /**
     * 创建无数据权限对象
     */
    private DataScope createNoDataScope() {
        return new DataScope(SQL_NONE, Collections.emptyList(), null, null);
    }

    /**
     * 递归查询指定部门及其所有子部门的 ID（含当前部门）。
     * 仅包含未删除、状态正常的部门。
     */
    private Set<Long> selectDeptIdsByParentId(Long parentId) {
        if (parentId == null) {
            return new HashSet<>();
        }
        Set<Long> deptIdSet = new HashSet<>();
        deptIdSet.add(parentId);
        selectChildrenDeptIds(parentId, deptIdSet);
        return deptIdSet;
    }

    /**
     * 递归收集子部门 ID 到给定集合。
     */
    private void selectChildrenDeptIds(Long parentId, Set<Long> deptIdSet) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, parentId)
                .eq(SysDept::getDelFlag, AppConstants.DelFlag.NORMAL)
                .eq(SysDept::getStatus, AppConstants.Status.NORMAL);
        
        List<SysDept> children = deptMapper.selectList(wrapper);
        for (SysDept child : children) {
            deptIdSet.add(child.getDeptId());
            selectChildrenDeptIds(child.getDeptId(), deptIdSet);
        }
    }

    /**
     * 权限计算结果内部类
     */
    private static class ScopeResult {
        final String scopeType;
        final Set<Long> deptIds;

        ScopeResult(String scopeType, Set<Long> deptIds) {
            this.scopeType = scopeType;
            this.deptIds = deptIds;
        }
    }
}
