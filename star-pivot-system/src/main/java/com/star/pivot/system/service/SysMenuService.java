package com.star.pivot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.system.domain.dto.MenuDTO;
import com.star.pivot.system.domain.entity.SysMenu;

import java.util.List;

/**
 * 菜单权限表(SysMenu)表服务接口
 *
 * @author makejava
 * @since 2025-12-28 21:57:29
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 查询所有菜单树
     * @return 菜单树列表
     */
    List<SysMenu> menuTree();

    /**
     * 根据用户ID查询菜单树（仅返回用户有权限的菜单）
     * @param userId 用户ID
     * @return 菜单树列表
     */
    List<SysMenu> getUserMenuTree(Long userId);

    boolean insertMenu(MenuDTO menuDTO);

    boolean updateMenu(MenuDTO menuDTO);

    boolean deleteMenu(Long menuId);

    List<SysMenu> getParent();
}

