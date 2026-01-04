package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.common.constants.Constants;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.exception.BusinessException;
import com.star.pivot.common.utils.SecurityUtils;
import com.star.pivot.system.domain.bo.UserReqBo;
import com.star.pivot.system.domain.bo.UserVO;
import com.star.pivot.system.domain.dto.UserDTO;
import com.star.pivot.system.domain.entity.*;
import com.star.pivot.system.mapper.*;
import com.star.pivot.system.service.SysUserService;
import com.star.pivot.system.utils.SecurityContextUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        IPage<SysUser> pageList = sysUserMapper.selectPageList(page,userReqBo);
        // 转换为VO
//        IPage<UserVO> voPage = new Page<>(pageList.getCurrent(), pageList.getSize(), pageList.getTotal());
        List<UserVO> voList = pageList.getRecords().stream()
                .map(this::convertToVO)
                .toList();
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
        UserVO vo = new UserVO();
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
    public boolean deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            SysUser user = this.getById(userId);
            if (user != null && !"2".equals(user.getDelFlag())) {
                user.setDelFlag("2");
                String currentUser = SecurityContextUtils.getUsername();
                user.setUpdateBy(currentUser);
                user.setUpdateTime(LocalDateTime.now());
                this.updateById(user);
            }
        }
        return true;
    }

    /**
     * 插入用户角色关联
     */
    private void insertUserRoles(Long userId, List<Long> roleIds) {
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
    }

    /**
     * 插入用户岗位关联
     */
    private void insertUserPosts(Long userId, List<Long> postIds) {
        for (Long postId : postIds) {
            UserPost userPost = new UserPost();
            userPost.setUserId(userId);
            userPost.setPostId(postId);
            userPostMapper.insert(userPost);
        }
    }
    /**
     * 转换为VO
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

