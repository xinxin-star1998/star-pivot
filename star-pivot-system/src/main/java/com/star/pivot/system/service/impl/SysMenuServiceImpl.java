package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.mapper.SysMenuMapper;
import com.star.pivot.system.service.SysMenuService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限表(SysMenu)表服务实现类
 *
 * @author makejava
 * @since 2025-12-28 21:57:29
 */
@Service("sysMenuService")
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Override
    public List<SysMenu> menuTree() {
        // 查询所有权限
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(SysMenu::getOrderNum);
        List<SysMenu> allMenu = this.list(queryWrapper);

        // 构建权限树
        return buildMenuTree(allMenu, 0L);
    }

    private List<SysMenu> buildMenuTree(List<SysMenu> allMenu, long parentId) {
        List<SysMenu> tree = new ArrayList<>();

        // 筛选出指定父级ID的权限
        List<SysMenu> children = allMenu.stream()
                .filter(permission -> permission.getParentId() != null && permission.getParentId().equals(parentId))
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

