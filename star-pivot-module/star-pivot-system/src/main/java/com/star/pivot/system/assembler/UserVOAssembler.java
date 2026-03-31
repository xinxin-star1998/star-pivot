package com.star.pivot.system.assembler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.star.pivot.system.domain.bo.UserVO;
import com.star.pivot.system.domain.entity.*;
import com.star.pivot.system.mapper.*;
import com.star.pivot.system.service.interfaces.AccountLockService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户 VO 组装器：将 SysUser 转为 UserVO，负责部门/角色/岗位批量查询与映射，便于单测和复用。
 */
@Component
public class UserVOAssembler {

    @Autowired
    private SysDeptMapper deptMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private UserPostMapper userPostMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired(required = false)
    private AccountLockService accountLockService;

    /**
     * 批量转换为 VO，解决 N+1 查询问题
     */
    public List<UserVO> convertToVOList(List<SysUser> userList) {
        if (userList == null || userList.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> userIds = userList.stream().map(SysUser::getUserId).collect(Collectors.toList());
        List<Long> deptIds = userList.stream()
                .map(SysUser::getDeptId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> deptNameMap = new HashMap<>();
        if (!deptIds.isEmpty()) {
            List<SysDept> deptList = deptMapper.selectList(new LambdaQueryWrapper<SysDept>().in(SysDept::getDeptId, deptIds));
            for (SysDept dept : deptList) {
                deptNameMap.put(dept.getDeptId(), dept.getDeptName());
            }
        }

        Map<Long, List<SysRole>> userRolesMap = buildUserRolesMap(userIds);
        Map<Long, List<Long>> userPostIdsMap = new HashMap<>();
        Map<Long, String> postNameMap = new HashMap<>();
        buildUserPostMaps(userIds, userPostIdsMap, postNameMap);

        List<UserVO> voList = new ArrayList<>();
        for (SysUser user : userList) {
            UserVO vo = convertToVO(user, deptNameMap, userRolesMap, userPostIdsMap, postNameMap);
            voList.add(vo);
        }

        if (accountLockService != null) {
            for (UserVO vo : voList) {
                vo.setIsLocked(vo.getUserName() != null && accountLockService.isAccountLocked(vo.getUserName()));
            }
        }
        return voList;
    }

    /**
     * 单用户转换为 VO（自查部门/角色/岗位）
     */
    public UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        if (user.getDeptId() != null) {
            SysDept dept = deptMapper.selectById(user.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getDeptName());
            }
        }

        List<SysRole> roles = sysRoleMapper.selectRoleListByUserId(user.getUserId());
        if (roles != null && !roles.isEmpty()) {
            vo.setRoleIds(roles.stream().map(SysRole::getRoleId).collect(Collectors.toList()));
            vo.setRoleNames(roles.stream().map(SysRole::getRoleName).collect(Collectors.toList()));
        }

        LambdaQueryWrapper<UserPost> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.eq(UserPost::getUserId, user.getUserId());
        List<UserPost> userPosts = userPostMapper.selectList(postWrapper);
        if (userPosts != null && !userPosts.isEmpty()) {
            List<Long> postIds = userPosts.stream().map(UserPost::getPostId).collect(Collectors.toList());
            vo.setPostIds(postIds);
            List<SysPost> postList = postMapper.selectList(new LambdaQueryWrapper<SysPost>().in(SysPost::getPostId, postIds));
            Map<Long, String> postNameMap = postList.stream()
                    .collect(Collectors.toMap(SysPost::getPostId, SysPost::getPostName, (k1, k2) -> k1));
            List<String> postNames = postIds.stream()
                    .map(postNameMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            vo.setPostNames(postNames);
        }

        vo.setIsLocked(accountLockService != null && user.getUserName() != null && accountLockService.isAccountLocked(user.getUserName()));
        return vo;
    }

    private Map<Long, List<SysRole>> buildUserRolesMap(List<Long> userIds) {
        Map<Long, List<SysRole>> userRolesMap = new HashMap<>();
        LambdaQueryWrapper<UserRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(UserRole::getUserId, userIds);
        List<UserRole> userRoles = userRoleMapper.selectList(roleWrapper);
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).distinct().collect(Collectors.toList());

        Map<Long, SysRole> roleMap = new HashMap<>();
        if (!roleIds.isEmpty()) {
            List<SysRole> roleList = sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getRoleId, roleIds));
            for (SysRole role : roleList) {
                roleMap.put(role.getRoleId(), role);
            }
        }
        for (UserRole ur : userRoles) {
            userRolesMap.computeIfAbsent(ur.getUserId(), k -> new ArrayList<>()).add(roleMap.get(ur.getRoleId()));
        }
        return userRolesMap;
    }

    private void buildUserPostMaps(List<Long> userIds, Map<Long, List<Long>> userPostIdsMap, Map<Long, String> postNameMap) {
        LambdaQueryWrapper<UserPost> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.in(UserPost::getUserId, userIds);
        List<UserPost> userPosts = userPostMapper.selectList(postWrapper);
        List<Long> postIds = userPosts.stream().map(UserPost::getPostId).distinct().collect(Collectors.toList());
        if (!postIds.isEmpty()) {
            List<SysPost> postList = postMapper.selectList(new LambdaQueryWrapper<SysPost>().in(SysPost::getPostId, postIds));
            for (SysPost post : postList) {
                postNameMap.put(post.getPostId(), post.getPostName());
            }
        }
        for (UserPost up : userPosts) {
            userPostIdsMap.computeIfAbsent(up.getUserId(), k -> new ArrayList<>()).add(up.getPostId());
        }
    }

    UserVO convertToVO(SysUser user, Map<Long, String> deptNameMap, Map<Long, List<SysRole>> userRolesMap,
                       Map<Long, List<Long>> userPostIdsMap, Map<Long, String> postNameMap) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        if (user.getDeptId() != null) {
            vo.setDeptName(deptNameMap.get(user.getDeptId()));
        }
        List<SysRole> roles = userRolesMap.getOrDefault(user.getUserId(), new ArrayList<>());
        if (!roles.isEmpty()) {
            vo.setRoleIds(roles.stream().map(SysRole::getRoleId).collect(Collectors.toList()));
            vo.setRoleNames(roles.stream().map(SysRole::getRoleName).collect(Collectors.toList()));
        }
        List<Long> postIds = userPostIdsMap.getOrDefault(user.getUserId(), new ArrayList<>());
        if (!postIds.isEmpty()) {
            vo.setPostIds(postIds);
            vo.setPostNames(postIds.stream().map(postNameMap::get).filter(Objects::nonNull).collect(Collectors.toList()));
        }
        return vo;
    }
}
