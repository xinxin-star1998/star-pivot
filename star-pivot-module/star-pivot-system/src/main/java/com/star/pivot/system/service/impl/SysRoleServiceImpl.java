  package com.star.pivot.system.service.impl;

  import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
  import com.baomidou.mybatisplus.core.metadata.IPage;
  import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
  import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
  import com.star.pivot.framework.domain.AppConstants;
  import com.star.pivot.framework.domain.PageResponse;
  import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.utils.validation.AssertUtils;
  import com.star.pivot.security.context.SecurityContextUtils;
  import com.star.pivot.system.domain.dto.RoleDTO;
  import com.star.pivot.system.domain.dto.RolePermissionAssignDTO;
  import com.star.pivot.system.domain.dto.RoleQueryDTO;
  import com.star.pivot.system.domain.dto.UserRoleDTO;
  import com.star.pivot.system.domain.entity.RoleMenu;
  import com.star.pivot.system.domain.entity.SysRole;
  import com.star.pivot.system.domain.entity.UserRole;
  import com.star.pivot.system.mapper.*;
  import com.star.pivot.system.service.interfaces.SysRoleService;
  import com.star.pivot.system.service.interfaces.UserPermissionCacheService;
  import org.springframework.beans.BeanUtils;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.cache.annotation.CacheEvict;
  import org.springframework.cache.annotation.Cacheable;
  import org.springframework.stereotype.Service;
  import org.springframework.transaction.annotation.Transactional;
  import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
    @Autowired
    private SysMenuMapper sysMenuMapper;
    @Autowired
    private UserPermissionCacheService userPermissionCacheService;
    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public SysRole selectRoleById(Long roleId) {
        return sysRoleMapper.selectById(roleId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "roleList", key = "'all'")
    public List<SysRole> selectAllRoles() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getDelFlag, AppConstants.DelFlag.NORMAL)
                .eq(SysRole::getStatus, AppConstants.Status.NORMAL)
                .orderByAsc(SysRole::getRoleSort);
        return this.list(wrapper);
    }

    @Override
    @CacheEvict(cacheNames = "roleList", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public boolean insertRole(RoleDTO roleDTO) {
        // 检查角色权限字符串是否已存在
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleKey, roleDTO.getRoleKey())
                .eq(SysRole::getDelFlag, "0");
        if (this.count(wrapper) > 0) {
            throw new BizException(ErrorCode.ROLE_KEY_EXISTS);
        }

        // 创建角色
        SysRole role = new SysRole();
        BeanUtils.copyProperties(roleDTO, role);
        role.setStatus(StringUtils.hasText(roleDTO.getStatus()) ? roleDTO.getStatus() : "0");
        role.setDelFlag(AppConstants.DelFlag.NORMAL);
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
        return success;
    }

    @Override
    @CacheEvict(cacheNames = "roleList", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(RoleDTO roleDTO) {
        SysRole role = this.getById(roleDTO.getRoleId());
        AssertUtils.notNull(role, ErrorCode.ROLE_NOT_FOUND);
        if ("2".equals(role.getDelFlag())) {
            throw new BizException(ErrorCode.ROLE_NOT_FOUND);
        }

        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleKey, roleDTO.getRoleKey())
                .eq(SysRole::getDelFlag, "0")
                .ne(SysRole::getRoleId, roleDTO.getRoleId());
        if (this.count(wrapper) > 0) {
            throw new BizException(ErrorCode.ROLE_KEY_USED);
        }

        if (AppConstants.ADMIN_ROLE_KEY.equals(role.getRoleKey()) && !AppConstants.ADMIN_ROLE_KEY.equals(roleDTO.getRoleKey())) {
            throw new BizException(ErrorCode.ROLE_ADMIN_PROTECTED);
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
        }
        return success;
    }

    @Override
    @CacheEvict(cacheNames = "roleList", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRoleByIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return false;
        }

        // 批量查询角色信息
        List<SysRole> roles = this.listByIds(roleIds);
        if (roles.isEmpty()) {
            return false;
        }

        String currentUser = SecurityContextUtils.getUsername();
        LocalDateTime now = LocalDateTime.now();
        List<SysRole> toUpdate = new ArrayList<>();

        for (SysRole role : roles) {
            if ("2".equals(role.getDelFlag())) {
                continue;
            }
            if (AppConstants.ADMIN_ROLE_KEY.equals(role.getRoleKey())) {
                throw new BizException(ErrorCode.ROLE_ADMIN_PROTECTED);
            }

            // 批量检查角色是否被使用
            LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserRole::getRoleId, role.getRoleId());
            long count = userRoleMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BizException(ErrorCode.ROLE_USED, "角色[" + role.getRoleName() + "]已被使用，不能删除");
            }

            role.setDelFlag(AppConstants.DelFlag.DELETE);
            role.setUpdateBy(currentUser);
            role.setUpdateTime(now);
            toUpdate.add(role);
        }

        // 批量更新
        if (!toUpdate.isEmpty()) {
            this.updateBatchById(toUpdate);
        }
        return true;
    }

    @Override
    @CacheEvict(cacheNames = "roleList", allEntries = true)
    public boolean changeRoleStatus(Long roleId, String status) {
        SysRole role = this.getById(roleId);
        AssertUtils.notNull(role, ErrorCode.ROLE_NOT_FOUND);
        if ("2".equals(role.getDelFlag())) {
            throw new BizException(ErrorCode.ROLE_NOT_FOUND);
        }

        if (AppConstants.ADMIN_ROLE_KEY.equals(role.getRoleKey()) && "1".equals(status)) {
            throw new BizException(ErrorCode.ROLE_ADMIN_PROTECTED);
        }

        role.setStatus(status);
        String currentUser = SecurityContextUtils.getUsername();
        role.setUpdateBy(currentUser);
        role.setUpdateTime(LocalDateTime.now());

        return this.updateById(role);
    }

    /**
     * 根据角色id获取部门id列表
     * @param roleId 角色id
     * @return deptIds 部门id列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<Long> selectDeptIdsByRoleId(Long roleId) {
        SysRole sysRole = sysRoleMapper.selectById(roleId);
        AssertUtils.notNull(sysRole, ErrorCode.ROLE_NOT_FOUND);
        if ("2".equals(sysRole.getDelFlag())){
            throw new BizException(ErrorCode.ROLE_DELETED);
        }
        if("1".equals(sysRole.getStatus())){
            throw new BizException(ErrorCode.ROLE_DISABLED);
        }
        List<Long> deptIds;
        if(sysRole.getRoleKey().equals(AppConstants.ADMIN_ROLE_KEY)){
            deptIds = sysDeptMapper.selectAllDeptIds();
        }else{
            deptIds = roleDeptMapper.selectDeptIdsByRoleId(roleId);
        }
        return deptIds;
    }

    /**
     * 根据角色id获取菜单id列表
     * @param roleId 角色id
     * @return menuIds 菜单id列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        SysRole sysRole = sysRoleMapper.selectById(roleId);
        if(sysRole == null){
            throw new BizException("角色不存在");
        }
        if ("2".equals(sysRole.getDelFlag())){
            throw new BizException("角色已删除");
        }
        if("1".equals(sysRole.getStatus())){
            throw new BizException("角色已禁用，请联系管理员");
        }
        List<Long> menuIds;
        if (AppConstants.ADMIN_ROLE_KEY.equals(sysRole.getRoleKey())) {
            menuIds = sysMenuMapper.selectMenuIds(null);
        } else {
            menuIds = roleMenuMapper.selectMenuIdsByRoleId(roleId);
            if (menuIds == null) {
                menuIds = Collections.emptyList();
            }
        }
        return menuIds;
    }

    @Override
    public boolean assignUser(UserRoleDTO userRoleDTO) {
        if (userRoleDTO.getUserIds() == null || userRoleDTO.getUserIds().isEmpty()) {
            return true;
        }
        // 批量插入用户角色关联，提升性能
        List<UserRole> userRoles = new ArrayList<>(userRoleDTO.getUserIds().size());
        for (Long userId : userRoleDTO.getUserIds()) {
            UserRole userRole = new UserRole();
            userRole.setRoleId(userRoleDTO.getRoleId());
            userRole.setUserId(userId);
            userRoles.add(userRole);
        }
        userRoleMapper.insertBatchUserRoles(userRoles);
        return true;
    }

    @Override
    public boolean cancelUser(UserRole userRole) {
        return userRoleMapper.deleteByRoleIdAndUserId(userRole.getRoleId(), userRole.getUserId());
    }

    /**
     * 执行角色  分配部门
     * &#064;Transactional：事务控制，保证操作原子性
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermission(RolePermissionAssignDTO rolePermissionAssignDTO) {
        // 1. 基础参数校验
        Long roleId = rolePermissionAssignDTO.getRoleId();
        List<Long> deptIds = rolePermissionAssignDTO.getDeptIds() == null ? Collections.emptyList() : rolePermissionAssignDTO.getDeptIds();
        //2.查询角色是否存在
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null || AppConstants.DelFlag.DELETE.equals(role.getDelFlag())) {
            throw new BizException("角色不存在");
        }
        // 2.1 若传入数据范围，更新角色的 data_scope 字段
        if (StringUtils.hasText(rolePermissionAssignDTO.getDataScope())) {
            role.setDataScope(rolePermissionAssignDTO.getDataScope());
            sysRoleMapper.updateById(role);
        }
        // 3.处理旧部门权限，添加新部门权限
        roleDeptMapper.deleteByRoleId(roleId);
        if(!deptIds.isEmpty()){
            roleDeptMapper.batchSave(roleId, deptIds);
        }
        // 4.清除所有用户权限缓存（角色权限变更可能影响多个用户）
        // 注意：这里清除所有用户缓存是为了简化实现，实际可以只清除拥有该角色的用户缓存
        userPermissionCacheService.clearAllUserPermissionCache();
        return true;
    }

    /**
     * 插入角色菜单关联（批量）
     */
    private void insertRoleMenus(Long roleId, List<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }
        roleMenuMapper.batchSave(roleId, menuIds);
    }
}

