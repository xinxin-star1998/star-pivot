  package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.common.constants.Constants;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.exception.BusinessException;
import com.star.pivot.system.domain.dto.RoleDTO;
import com.star.pivot.system.domain.dto.RoleQueryDTO;
import com.star.pivot.system.domain.entity.RoleDept;
import com.star.pivot.system.domain.entity.RoleMenu;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.domain.entity.UserRole;
import com.star.pivot.system.mapper.*;
import com.star.pivot.system.service.SysRoleService;
import com.star.pivot.system.utils.SecurityContextUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色信息表(SysRole)表服务实现类
 *
 * @author makejava
 * @since 2025-12-28 19:32:10
 */
@Service("sysRoleService")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Autowired
    private RoleDeptMapper roleDeptMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private SysDeptMapper sysDeptMapper;
    @Override
    public PageResponse<SysRole> selectRoleList(RoleQueryDTO roleQueryDTO) {
        PageResponse<SysRole> pageResponse = new PageResponse<>();
        Page<SysRole> page = new Page<>(roleQueryDTO.getPageNum(), roleQueryDTO.getPageSize());
        IPage<SysRole> pageList = sysRoleMapper.selectPageList(page,roleQueryDTO);
        pageResponse.setTotal(pageList.getTotal());
        pageResponse.setRows(pageList.getRecords());
        pageResponse.setPageNum(Long.valueOf(roleQueryDTO.getPageNum()));
        pageResponse.setPageSize(Long.valueOf(roleQueryDTO.getPageSize()));
        return pageResponse;
    }

    @Override
    public SysRole selectRoleById(Long roleId) {
        return sysRoleMapper.selectById(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertRole(RoleDTO roleDTO) {
        // 检查角色权限字符串是否已存在
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleKey, roleDTO.getRoleKey())
                .eq(SysRole::getDelFlag, "0");
        if (this.count(wrapper) > 0) {
            throw new BusinessException("角色权限字符串已存在");
        }

        // 创建角色
        SysRole role = new SysRole();
        BeanUtils.copyProperties(roleDTO, role);
        role.setStatus(StringUtils.hasText(roleDTO.getStatus()) ? roleDTO.getStatus() : "0");
        role.setDelFlag(Constants.DelFlag.NORMAL);
        role.setMenuCheckStrictly(roleDTO.getMenuCheckStrictly() != null ? roleDTO.getMenuCheckStrictly() : 1);
        role.setDeptCheckStrictly(roleDTO.getDeptCheckStrictly() != null ? roleDTO.getDeptCheckStrictly() : 1);

        String currentUser = SecurityContextUtils.getUsername();
        role.setCreateBy(currentUser);
        role.setCreateTime(LocalDateTime.now());

        boolean success = this.save(role);

        if (success && roleDTO.getMenuIds() != null && !roleDTO.getMenuIds().isEmpty()) {
            // 分配菜单权限
            insertRoleMenus(role.getRoleId(), roleDTO.getMenuIds());
        }

        if (success && roleDTO.getDeptIds() != null && !roleDTO.getDeptIds().isEmpty()) {
            // 分配部门权限
            insertRoleDepts(role.getRoleId(), roleDTO.getDeptIds());
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(RoleDTO roleDTO) {
        SysRole role = this.getById(roleDTO.getRoleId());
        if (role == null || "2".equals(role.getDelFlag())) {
            throw new BusinessException("角色不存在");
        }

        // 检查角色权限字符串是否已被其他角色使用
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleKey, roleDTO.getRoleKey())
                .eq(SysRole::getDelFlag, "0")
                .ne(SysRole::getRoleId, roleDTO.getRoleId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("角色权限字符串已被使用");
        }

        // 不能修改超级管理员角色
        if (Constants.ADMIN_ROLE_KEY.equals(role.getRoleKey()) && !Constants.ADMIN_ROLE_KEY.equals(roleDTO.getRoleKey())) {
            throw new BusinessException("不能修改超级管理员角色");
        }

        // 更新角色信息
        BeanUtils.copyProperties(roleDTO, role, "roleId");
        String currentUser = SecurityContextUtils.getUsername();
        role.setUpdateBy(currentUser);
        role.setUpdateTime(LocalDateTime.now());

        boolean success = this.updateById(role);

        if (success) {
            // 更新菜单权限
            if (roleDTO.getMenuIds() != null) {
                // 删除旧的菜单关联
                LambdaQueryWrapper<RoleMenu> menuWrapper = new LambdaQueryWrapper<>();
                menuWrapper.eq(RoleMenu::getRoleId, roleDTO.getRoleId());
                roleMenuMapper.delete(menuWrapper);

                // 添加新的菜单关联
                if (!roleDTO.getMenuIds().isEmpty()) {
                    insertRoleMenus(roleDTO.getRoleId(), roleDTO.getMenuIds());
                }
            }

            // 更新部门权限
            if (roleDTO.getDeptIds() != null) {
                // 删除旧的部门关联
                LambdaQueryWrapper<RoleDept> deptWrapper = new LambdaQueryWrapper<>();
                deptWrapper.eq(RoleDept::getRoleId, roleDTO.getRoleId());
                roleDeptMapper.delete(deptWrapper);

                // 添加新的部门关联
                if (!roleDTO.getDeptIds().isEmpty()) {
                    insertRoleDepts(roleDTO.getRoleId(), roleDTO.getDeptIds());
                }
            }
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            SysRole role = this.getById(roleId);
            if (role != null && !"2".equals(role.getDelFlag())) {
                // 不能删除超级管理员角色
                if (Constants.ADMIN_ROLE_KEY.equals(role.getRoleKey())) {
                    throw new BusinessException("不能删除超级管理员角色");
                }

                // 检查是否有用户使用该角色
                LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(UserRole::getRoleId, roleId);
                long count = userRoleMapper.selectCount(wrapper);
                if (count > 0) {
                    throw new BusinessException("角色[" + role.getRoleName() + "]已被使用，不能删除");
                }

                role.setDelFlag(Constants.DelFlag.DELETE);
                String currentUser = SecurityContextUtils.getUsername();
                role.setUpdateBy(currentUser);
                role.setUpdateTime(LocalDateTime.now());
                this.updateById(role);
            }
        }
        return true;
    }

    @Override
    public boolean changeRoleStatus(Long roleId, String status) {
        SysRole role = this.getById(roleId);
        if (role == null || "2".equals(role.getDelFlag())) {
            throw new BusinessException("角色不存在");
        }

        // 不能停用超级管理员角色
        if (Constants.ADMIN_ROLE_KEY.equals(role.getRoleKey()) && "1".equals(status)) {
            throw new BusinessException("不能停用超级管理员角色");
        }

        role.setStatus(status);
        String currentUser = SecurityContextUtils.getUsername();
        role.setUpdateBy(currentUser);
        role.setUpdateTime(LocalDateTime.now());

        return this.updateById(role);
    }

    /**
     * 查询角色部门权限  deptIds
     * @param roleId 角色ID
     * @return 部门ID列表
     */
    @Override
    public List<Long> selectDeptIdsByRoleId(Long roleId) {
        SysRole sysRole = this.sysRoleMapper.selectById(roleId);
        List<Long> deptIds;
        if(sysRole.getRoleKey().equals(Constants.ADMIN_ROLE_KEY)){
            deptIds = sysDeptMapper.selectAllDeptIds();
        }else{
            deptIds = sysDeptMapper.selectDeptIdsByRoleId(roleId);
        }
        return deptIds;
    }

    /**
     * 插入角色菜单关联
     */
    private void insertRoleMenus(Long roleId, List<Long> menuIds) {
        for (Long menuId : menuIds) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }

    /**
     * 插入角色部门关联
     */
    private void insertRoleDepts(Long roleId, List<Long> deptIds) {
        for (Long deptId : deptIds) {
            RoleDept roleDept = new RoleDept();
            roleDept.setRoleId(roleId);
            roleDept.setDeptId(deptId);
            roleDeptMapper.insert(roleDept);
        }
    }
}

