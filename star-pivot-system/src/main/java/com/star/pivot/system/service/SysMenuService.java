package com.star.pivot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.system.domain.entity.SysMenu;

import java.util.List;

/**
 * 菜单权限表(SysMenu)表服务接口
 *
 * @author makejava
 * @since 2025-12-28 21:57:29
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> menuTree();
}

