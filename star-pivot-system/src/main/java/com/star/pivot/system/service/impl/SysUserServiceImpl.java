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
import com.star.pivot.system.domain.dto.UserDTO;
import com.star.pivot.system.domain.entity.*;
import com.star.pivot.system.mapper.SysUserMapper;
import com.star.pivot.system.mapper.UserPostMapper;
import com.star.pivot.system.mapper.UserRoleMapper;
import com.star.pivot.system.service.SysUserService;
import com.star.pivot.system.utils.SecurityContextUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

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
    /**
     * 用户分页查询
     *
     * @param userReqBo 查询参数
     * @return 分页结果
     */
    @Override
    public PageResponse<SysUser> pageList(UserReqBo userReqBo) {
        PageResponse<SysUser> pageResponse = new PageResponse<>();
        // 分页查询
        Page<SysUser> page = new Page<>(userReqBo.getPageNum(), userReqBo.getPageSize());
        IPage<SysUser> pageList = sysUserMapper.selectPageList(page,userReqBo);
        // 转换为分页结果
        pageResponse.setTotal(pageList.getTotal());
        pageResponse.setRows(pageList.getRecords());
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
}

