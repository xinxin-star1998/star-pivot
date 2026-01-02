package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.common.exception.BusinessException;
import com.star.pivot.system.domain.dto.MenuDTO;
import com.star.pivot.system.domain.entity.RoleMenu;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.mapper.RoleMenuMapper;
import com.star.pivot.system.mapper.SysMenuMapper;
import com.star.pivot.system.service.SysMenuService;
import com.star.pivot.system.service.SysUserService;
import com.star.pivot.system.utils.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单权限表(SysMenu)表服务实现类
 *
 * @author makejava
 * @since 2025-12-28 21:57:29
 */
@Service("sysMenuService")
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysUserService sysUserService;
    private final RoleMenuMapper roleMenuMapper;
    @Override
    public List<SysMenu> menuTree() {
        // 查询所有权限
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(SysMenu::getOrderNum);
        List<SysMenu> allMenu = this.list(queryWrapper);

        // 构建权限树
        return buildMenuTree(allMenu, 0L);
    }

    @Override
    public List<SysMenu> getUserMenuTree(Long userId) {
        //查询用户角色
        List<SysRole> roles = sysUserService.getRolesByUserId(userId);
        // 查询用户有权限的菜单（平铺列表）
//        List<SysMenu> userMenus = sysUserService.getMenuByUserId(userId);
//
//        if (userMenus == null || userMenus.isEmpty()) {
//            return new ArrayList<>();
//        }
        List<SysMenu> allMenu = null;
        //获取用户角色对应的菜单 roleKey == admin 时，查询全部菜单
        if (roles.stream().anyMatch(role -> "admin".equals(role.getRoleKey()))) {
            // 查询所有菜单（用于构建树结构）
            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByAsc(SysMenu::getOrderNum);
            allMenu = this.list(queryWrapper);
        }else{
            // 获取用户角色对应的菜单
            allMenu = sysUserService.getMenuByUserId(userId);
        }
        return buildMenuTree(allMenu, 0L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertMenu(MenuDTO menuDTO) {
        // 检查菜单名称是否唯一
        if (!checkMenuNameUnique(menuDTO.getMenuName(), menuDTO.getParentId(), null)) {
            throw new BusinessException("菜单名称已存在");
        }

        // 创建菜单
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(menuDTO, menu);
        menu.setParentId(menuDTO.getParentId() != null ? menuDTO.getParentId() : 0L);
        menu.setOrderNum(menuDTO.getOrderNum() != null ? menuDTO.getOrderNum() : 0);
        menu.setIsFrame(menuDTO.getIsFrame() != null ? menuDTO.getIsFrame() : 1);
        menu.setIsCache(menuDTO.getIsCache() != null ? menuDTO.getIsCache() : 0);
        menu.setVisible(StringUtils.hasText(menuDTO.getVisible()) ? menuDTO.getVisible() : "0");
        menu.setStatus(StringUtils.hasText(menuDTO.getStatus()) ? menuDTO.getStatus() : "0");

        String currentUser = SecurityContextUtils.getUsername();
        menu.setCreateBy(currentUser);
        menu.setCreateTime(LocalDateTime.now());

        return this.save(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMenu(MenuDTO menuDTO) {
        SysMenu menu = this.getById(menuDTO.getMenuId());
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }

        // 不能将父菜单设置为自己的子菜单
        if (menuDTO.getParentId() != null && menuDTO.getParentId().equals(menuDTO.getMenuId())) {
            throw new BusinessException("不能将父菜单设置为自己的子菜单");
        }

        // 检查菜单名称是否唯一
        if (!checkMenuNameUnique(menuDTO.getMenuName(), menuDTO.getParentId(), menuDTO.getMenuId())) {
            throw new BusinessException("菜单名称已存在");
        }

        // 更新菜单信息
        BeanUtils.copyProperties(menuDTO, menu, "menuId");
        String currentUser = SecurityContextUtils.getUsername();
        menu.setUpdateBy(currentUser);
        menu.setUpdateTime(LocalDateTime.now());

        return this.updateById(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMenu(Long menuId) {
        // 检查是否有子菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, menuId);
        long count = this.count(wrapper);
        if (count > 0) {
            throw new BusinessException("存在子菜单，不允许删除");
        }

        // 检查是否有角色使用该菜单
        LambdaQueryWrapper<RoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
        roleMenuWrapper.eq(RoleMenu::getMenuId, menuId);
        long roleMenuCount = roleMenuMapper.selectCount(roleMenuWrapper);
        if (roleMenuCount > 0) {
            throw new BusinessException("菜单已被角色使用，不允许删除");
        }

        return this.removeById(menuId);
    }

    public boolean checkMenuNameUnique(String menuName, Long parentId, Long menuId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getMenuName, menuName)
                .eq(SysMenu::getParentId, parentId != null ? parentId : 0L);
        if (menuId != null) {
            wrapper.ne(SysMenu::getMenuId, menuId);
        }
        return this.count(wrapper) == 0;
    }
    /**
     * 构建菜单树
     * @param allMenu 所有菜单列表
     * @param parentId 父菜单ID
     * @return 菜单树
     */
    private List<SysMenu> buildMenuTree(List<SysMenu> allMenu, long parentId) {
        List<SysMenu> tree = new ArrayList<>();

        // 筛选出指定父级ID的菜单
        List<SysMenu> children = allMenu.stream()
                .filter(menu -> menu.getParentId() != null && menu.getParentId().equals(parentId))
                .toList();

        // 递归构建子树
        for (SysMenu menu : children) {
            List<SysMenu> childTree = buildMenuTree(allMenu, menu.getMenuId());
            menu.setChildren(childTree);
            tree.add(menu);
        }

        return tree;
    }
}

