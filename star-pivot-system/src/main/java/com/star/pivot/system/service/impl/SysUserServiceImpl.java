package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.common.domain.Constants;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.exception.BusinessException;
import com.star.pivot.common.utils.SecurityUtils;
import com.star.pivot.system.domain.bo.UserReqBo;
import com.star.pivot.system.domain.bo.UserVO;
import com.star.pivot.system.domain.dto.UserDTO;
import com.star.pivot.system.domain.entity.*;
import com.star.pivot.system.mapper.*;
import com.star.pivot.system.service.SysUserService;
import com.star.pivot.system.service.UserPermissionCacheService;
import com.star.pivot.security.utils.SecurityContextUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户信息表(SysUser)表服务实现类
 *
 * @author makejava
 * @since 2025-12-28 17:28:24
 */
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private UserPostMapper userPostMapper;
    @Autowired
    private SysDeptMapper deptMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private UserPermissionCacheService userPermissionCacheService;
    /**
     * 用户分页查询
     *
     * @param userReqBo 查询参数
     * @return 分页结果
     */
    @Override
    public PageResponse<UserVO> pageList(UserReqBo userReqBo) {
        PageResponse<UserVO> pageResponse = new PageResponse<>();
        // 分页查询
        Page<SysUser> page = new Page<>(userReqBo.getPageNum(), userReqBo.getPageSize());
        IPage<SysUser> pageList = sysUserMapper.selectPageList(page, userReqBo);
        
        List<SysUser> userList = pageList.getRecords();
        List<UserVO> voList = convertToVOList(userList);
        
        // 转换为分页结果
        pageResponse.setTotal(pageList.getTotal());
        pageResponse.setRows(voList);
        pageResponse.setPageNum(pageList.getCurrent());
        pageResponse.setPageSize(pageList.getSize());
        pageResponse.setPageCount(pageList.getPages());
        return pageResponse;
    }

    @Override
    public SysUser getUserByUsername(String username) {
        return sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, username));
    }

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        return sysUserMapper.getRolesByUserId(userId);
    }

    @Override
    public List<SysMenu> getMenuByUserId(Long userId) {
        return sysUserMapper.getMenuByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addUser(UserDTO userDTO) {
        // 检查用户名是否已存在
        if (getUserByUsername(userDTO.getUserName()) != null) {
            throw new BusinessException("用户名已存在");
        }
        //创建用户
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userDTO, sysUser);
        sysUser.setUserType("00");
        sysUser.setStatus(StringUtils.hasText(userDTO.getStatus()) ? userDTO.getStatus() : Constants.Status.NORMAL);
        sysUser.setDelFlag(Constants.DelFlag.NORMAL);

        // 密码加密  如前端传密码，则进行加密处理，没有传，则使用默认密码123456
        if (StringUtils.hasText(userDTO.getPassword())) {
            sysUser.setPassword(SecurityUtils.encryptPassword(userDTO.getPassword()));
        } else {
            // 默认密码
            sysUser.setPassword(SecurityUtils.encryptPassword("123456"));
        }
        //创建人 当前登录人
        String currentUser = SecurityContextUtils.getUsername();
        sysUser.setCreateBy(currentUser);
        sysUser.setCreateTime(LocalDateTime.now());

        boolean success = this.save(sysUser);
        //用户添加成功后，添加角色关联信息、岗位关联信息
        if (success && userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            // 分配角色
            insertUserRoles(sysUser.getUserId(), userDTO.getRoleIds());
        }
        if (success && userDTO.getPostIds() != null && !userDTO.getPostIds().isEmpty()) {
            // 分配岗位
            insertUserPosts(sysUser.getUserId(), userDTO.getPostIds());
        }
        return success;
    }

    @Override
    public UserVO selectByUserId(Long userId) {
        UserVO vo;
        //查询用户信息
        SysUser user = this.getById(userId);
        vo = convertToVO(user);
        return vo;
    }
    /*
    * 修改用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(UserDTO userDTO) {
        SysUser user = this.getById(userDTO.getUserId());
        if (user == null || "2".equals(user.getDelFlag())) {
            throw new BusinessException("用户不存在");
        }

        // 检查用户名是否已被其他用户使用
        SysUser existUser = getUserByUsername(userDTO.getUserName());
        if (existUser != null && !existUser.getUserId().equals(userDTO.getUserId())) {
            throw new BusinessException("用户名已被使用");
        }

        // 更新用户信息
        BeanUtils.copyProperties(userDTO, user, "password", "userId");
        String currentUser = SecurityContextUtils.getUsername();
        user.setUpdateBy(currentUser);
        user.setUpdateTime(LocalDateTime.now());

        boolean success = this.updateById(user);

        if (success) {
            // 更新角色关联
            if (userDTO.getRoleIds() != null) {
                // 删除旧的角色关联
                LambdaQueryWrapper<UserRole> roleWrapper = new LambdaQueryWrapper<>();
                roleWrapper.eq(UserRole::getUserId, userDTO.getUserId());
                userRoleMapper.delete(roleWrapper);

                // 添加新的角色关联
                if (!userDTO.getRoleIds().isEmpty()) {
                    insertUserRoles(userDTO.getUserId(), userDTO.getRoleIds());
                }
                
                // 清除用户权限缓存（角色变更会影响权限）
                userPermissionCacheService.clearUserPermissionCache(userDTO.getUserName());
            }

            // 更新岗位关联
            if (userDTO.getPostIds() != null) {
                // 删除旧的岗位关联
                LambdaQueryWrapper<UserPost> postWrapper = new LambdaQueryWrapper<>();
                postWrapper.eq(UserPost::getUserId, userDTO.getUserId());
                userPostMapper.delete(postWrapper);

                // 添加新的岗位关联
                if (!userDTO.getPostIds().isEmpty()) {
                    insertUserPosts(userDTO.getUserId(), userDTO.getPostIds());
                }
            }
        }

        return success;
    }

    @Override
    public boolean changeUserStatus(Long userId, String status) {
        SysUser user = this.getById(userId);
        if (user == null || "2".equals(user.getDelFlag())) {
            throw new BusinessException("用户不存在");
        }

        user.setStatus(status);
        String currentUser = SecurityContextUtils.getUsername();
        user.setUpdateBy(currentUser);
        user.setUpdateTime(LocalDateTime.now());

        return this.updateById(user);
    }

    @Override
    public boolean resetUserPassword(Long userId, String password) {
        SysUser user = this.getById(userId);
        if (user == null || "2".equals(user.getDelFlag())) {
            throw new BusinessException("用户不存在");
        }

        user.setPassword(SecurityUtils.encryptPassword(password));
        user.setPwdUpdateDate(LocalDateTime.now());
        String currentUser = SecurityContextUtils.getUsername();
        user.setUpdateBy(currentUser);
        user.setUpdateTime(LocalDateTime.now());

        return this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUserByIds(Long[] userIds) {
        if (userIds == null || userIds.length == 0) {
            return false;
        }
        
        // 批量查询用户信息
        List<SysUser> userList = this.listByIds(List.of(userIds));
        if (userList.isEmpty()) {
            return false;
        }
        
        String currentUser = SecurityContextUtils.getUsername();
        LocalDateTime now = LocalDateTime.now();
        
        // 更新用户状态
        userList.forEach(user -> {
            if (!"2".equals(user.getDelFlag())) {
                user.setDelFlag("2");
                user.setUpdateBy(currentUser);
                user.setUpdateTime(now);
            }
        });
        
        // 批量更新
        this.updateBatchById(userList);
        return true;
    }

    /**
     * 插入用户角色关联（批量）
     */
    private void insertUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }

        // 构建用户角色关联集合，使用批量插入提升性能
        List<UserRole> userRoles = new ArrayList<>(roleIds.size());
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoles.add(userRole);
        }
        userRoleMapper.insertBatchUserRoles(userRoles);
    }

    /**
     * 插入用户岗位关联（批量）
     */
    private void insertUserPosts(Long userId, List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return;
        }

        // 构建用户岗位关联集合，使用批量插入提升性能
        List<UserPost> userPosts = new ArrayList<>(postIds.size());
        for (Long postId : postIds) {
            UserPost userPost = new UserPost();
            userPost.setUserId(userId);
            userPost.setPostId(postId);
            userPosts.add(userPost);
        }
        userPostMapper.insertBatchUserPosts(userPosts);
    }
    /**
     * 批量转换为VO，解决N+1查询问题
     */
    private List<UserVO> convertToVOList(List<SysUser> userList) {
        if (userList == null || userList.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 1. 收集所有用户ID
        List<Long> userIds = userList.stream()
                .map(SysUser::getUserId)
                .collect(Collectors.toList());
        
        // 2. 收集所有部门ID
        List<Long> deptIds = userList.stream()
                .map(SysUser::getDeptId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        
        // 3. 批量查询部门信息
        Map<Long, String> deptNameMap = new HashMap<>();
        if (!deptIds.isEmpty()) {
            List<SysDept> deptList = deptMapper.selectList(new LambdaQueryWrapper<SysDept>().in(SysDept::getDeptId, deptIds));
            for (SysDept dept : deptList) {
                deptNameMap.put(dept.getDeptId(), dept.getDeptName());
            }
        }
        
        // 4. 批量查询角色信息
        Map<Long, List<SysRole>> userRolesMap = new HashMap<>();
        LambdaQueryWrapper<UserRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(UserRole::getUserId, userIds);
        List<UserRole> userRoles = userRoleMapper.selectList(roleWrapper);
        
        // 收集角色ID
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询角色详情
        Map<Long, SysRole> roleMap = new HashMap<>();
        if (!roleIds.isEmpty()) {
            List<SysRole> roleList = sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getRoleId, roleIds));
            for (SysRole role : roleList) {
                roleMap.put(role.getRoleId(), role);
            }
        }
        
        // 构建用户角色映射
        for (UserRole userRole : userRoles) {
            userRolesMap.computeIfAbsent(userRole.getUserId(), k -> new ArrayList<>())
                    .add(roleMap.get(userRole.getRoleId()));
        }
        
        // 5. 批量查询岗位信息
        Map<Long, List<Long>> userPostIdsMap = new HashMap<>();
        LambdaQueryWrapper<UserPost> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.in(UserPost::getUserId, userIds);
        List<UserPost> userPosts = userPostMapper.selectList(postWrapper);
        
        // 收集岗位ID
        List<Long> postIds = userPosts.stream()
                .map(UserPost::getPostId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询岗位详情
        Map<Long, String> postNameMap = new HashMap<>();
        if (!postIds.isEmpty()) {
            List<SysPost> postList = postMapper.selectList(new LambdaQueryWrapper<SysPost>().in(SysPost::getPostId, postIds));
            for (SysPost post : postList) {
                postNameMap.put(post.getPostId(), post.getPostName());
            }
        }
        
        // 构建用户岗位映射
        for (UserPost userPost : userPosts) {
            userPostIdsMap.computeIfAbsent(userPost.getUserId(), k -> new ArrayList<>())
                    .add(userPost.getPostId());
        }
        
        // 6. 转换为VO列表
        List<UserVO> voList = new ArrayList<>();
        for (SysUser user : userList) {
            UserVO vo = convertToVO(user, deptNameMap, userRolesMap, userPostIdsMap, postNameMap);
            voList.add(vo);
        }
        
        return voList;
    }
    
    /**
     * 转换为VO（使用预加载的Map信息，避免N+1查询）
     */
    private UserVO convertToVO(SysUser user, Map<Long, String> deptNameMap, Map<Long, List<SysRole>> userRolesMap, 
                            Map<Long, List<Long>> userPostIdsMap, Map<Long, String> postNameMap) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        // 设置部门名称
        if (user.getDeptId() != null) {
            vo.setDeptName(deptNameMap.get(user.getDeptId()));
        }

        // 设置角色信息
        List<SysRole> roles = userRolesMap.getOrDefault(user.getUserId(), new ArrayList<>());
        if (!roles.isEmpty()) {
            vo.setRoleIds(roles.stream().map(SysRole::getRoleId).collect(Collectors.toList()));
            vo.setRoleNames(roles.stream().map(SysRole::getRoleName).collect(Collectors.toList()));
        }

        // 设置岗位信息
        List<Long> postIds = userPostIdsMap.getOrDefault(user.getUserId(), new ArrayList<>());
        if (!postIds.isEmpty()) {
            vo.setPostIds(postIds);
            
            List<String> postNames = postIds.stream()
                    .map(postNameMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            vo.setPostNames(postNames);
        }

        return vo;
    }
    
    /**
     * 转换为VO（单用户查询使用）
     */
    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        // 查询部门名称
        if (user.getDeptId() != null) {
            SysDept dept = deptMapper.selectById(user.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getDeptName());
            }
        }

        // 查询角色信息
        List<SysRole> roles = sysRoleMapper.selectRoleListByUserId(user.getUserId());
        if (roles != null && !roles.isEmpty()) {
            vo.setRoleIds(roles.stream().map(SysRole::getRoleId).collect(Collectors.toList()));
            vo.setRoleNames(roles.stream().map(SysRole::getRoleName).collect(Collectors.toList()));
        }

        // 查询岗位信息
        LambdaQueryWrapper<UserPost> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.eq(UserPost::getUserId, user.getUserId());
        List<UserPost> userPosts = userPostMapper.selectList(postWrapper);
        if (userPosts != null && !userPosts.isEmpty()) {
            List<Long> postIds = userPosts.stream().map(UserPost::getPostId).collect(Collectors.toList());
            vo.setPostIds(postIds);

            List<String> postNames = new ArrayList<>();
            for (Long postId : postIds) {
                SysPost post = postMapper.selectById(postId);
                if (post != null) {
                    postNames.add(post.getPostName());
                }
            }
            vo.setPostNames(postNames);
        }

        return vo;
    }
}

