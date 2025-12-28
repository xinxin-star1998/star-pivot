package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.mapper.SysMenuMapper;
import com.star.pivot.system.service.SysMenuService;
import com.star.pivot.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        List<SysMenu> userMenus = sysUserService.getMenuByUserId(userId);
        
        if (userMenus == null || userMenus.isEmpty()) {
            return new ArrayList<>();
        }
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

