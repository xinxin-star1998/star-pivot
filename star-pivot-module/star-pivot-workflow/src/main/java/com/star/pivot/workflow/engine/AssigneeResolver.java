package com.star.pivot.workflow.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.system.domain.entity.SysDept;
import com.star.pivot.system.domain.entity.SysPost;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.domain.entity.UserPost;
import com.star.pivot.system.domain.entity.UserRole;
import com.star.pivot.system.mapper.SysDeptMapper;
import com.star.pivot.system.mapper.PostMapper;
import com.star.pivot.system.mapper.SysRoleMapper;
import com.star.pivot.system.mapper.SysUserMapper;
import com.star.pivot.system.mapper.UserPostMapper;
import com.star.pivot.system.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AssigneeResolver {

    private final SysUserMapper sysUserMapper;
    private final SysDeptMapper sysDeptMapper;
    private final SysRoleMapper sysRoleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PostMapper postMapper;
    private final UserPostMapper userPostMapper;

    public List<Long> resolve(Map<String, Object> assigneeRule, Long starterId) {
        if (assigneeRule == null || assigneeRule.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "审批人规则不能为空");
        }
        String type = String.valueOf(assigneeRule.get("type"));
        return switch (type) {
            case "STARTER_SELF" -> List.of(requireStarter(starterId));
            case "STARTER_DEPT_LEADER" -> resolveDeptLeader(starterId);
            case "ROLE" -> resolveByRole(String.valueOf(assigneeRule.get("value")));
            case "POST" -> resolveByPost(String.valueOf(assigneeRule.get("value")));
            case "USER" -> List.of(Long.valueOf(String.valueOf(assigneeRule.get("value"))));
            default -> throw new BizException(ErrorCode.PARAM_INVALID, "不支持的审批人规则: " + type);
        };
    }

    private Long requireStarter(Long starterId) {
        if (starterId == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "发起人不能为空");
        }
        return starterId;
    }

    private List<Long> resolveDeptLeader(Long starterId) {
        SysUser starter = sysUserMapper.selectById(starterId);
        if (starter == null || starter.getDeptId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "发起人未关联部门，无法解析部门负责人");
        }
        SysDept dept = sysDeptMapper.selectById(starter.getDeptId());
        if (dept == null || !StringUtils.hasText(dept.getLeader())) {
            throw new BizException(ErrorCode.PARAM_INVALID, "部门未配置负责人");
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDeptId, dept.getDeptId())
                .and(w -> w.eq(SysUser::getUserName, dept.getLeader())
                        .or()
                        .eq(SysUser::getNickName, dept.getLeader()))
                .eq(SysUser::getStatus, "0")
                .last("LIMIT 1");
        SysUser leader = sysUserMapper.selectOne(wrapper);
        if (leader == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "未找到部门负责人对应用户: " + dept.getLeader());
        }
        return List.of(leader.getUserId());
    }

    private List<Long> resolveByRole(String roleKey) {
        if (!StringUtils.hasText(roleKey)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "角色标识不能为空");
        }
        LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysRole::getRoleKey, roleKey).eq(SysRole::getStatus, "0");
        SysRole role = sysRoleMapper.selectOne(roleWrapper);
        if (role == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "角色不存在: " + roleKey);
        }
        LambdaQueryWrapper<UserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(UserRole::getRoleId, role.getRoleId());
        List<UserRole> userRoles = userRoleMapper.selectList(urWrapper);
        if (CollectionUtils.isEmpty(userRoles)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "角色下无用户: " + roleKey);
        }
        List<Long> allUserIds = userRoles.stream()
                .map(UserRole::getUserId)
                .distinct()
                .toList();
        List<SysUser> users = sysUserMapper.selectBatchIds(allUserIds);
        Set<Long> userIds = users.stream()
                .filter(user -> "0".equals(user.getStatus()))
                .map(SysUser::getUserId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (userIds.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "角色下无可用用户: " + roleKey);
        }
        return new ArrayList<>(userIds);
    }

    private List<Long> resolveByPost(String postCode) {
        if (!StringUtils.hasText(postCode)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "岗位编码不能为空");
        }
        LambdaQueryWrapper<SysPost> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.eq(SysPost::getPostCode, postCode).eq(SysPost::getStatus, "0");
        SysPost post = postMapper.selectOne(postWrapper);
        if (post == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "岗位不存在: " + postCode);
        }
        LambdaQueryWrapper<UserPost> upWrapper = new LambdaQueryWrapper<>();
        upWrapper.eq(UserPost::getPostId, post.getPostId());
        List<UserPost> userPosts = userPostMapper.selectList(upWrapper);
        if (CollectionUtils.isEmpty(userPosts)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "岗位下无用户: " + postCode);
        }
        List<Long> allUserIds = userPosts.stream()
                .map(UserPost::getUserId)
                .distinct()
                .toList();
        List<SysUser> users = sysUserMapper.selectBatchIds(allUserIds);
        Set<Long> userIds = users.stream()
                .filter(user -> "0".equals(user.getStatus()))
                .map(SysUser::getUserId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (userIds.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "岗位下无可用用户: " + postCode);
        }
        return new ArrayList<>(userIds);
    }
}
