package com.star.pivot.controller;

import com.star.pivot.common.domain.Result;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sys/menu")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;

    @RequestMapping("/menuTree")
    public Result<List<SysMenu>> menuTree() {
        // 从数据库查询菜单树
        List<SysMenu> menuTree = sysMenuService.menuTree();
        return Result.success(menuTree);
    }
}
