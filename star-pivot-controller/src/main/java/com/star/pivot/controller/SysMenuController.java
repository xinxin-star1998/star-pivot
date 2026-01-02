package com.star.pivot.controller;

import com.star.pivot.common.domain.Result;
import com.star.pivot.system.domain.dto.MenuDTO;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.service.SysMenuService;
import com.star.pivot.system.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单控制器
 */
@RestController
@RequestMapping("/sys/menu")
@RequiredArgsConstructor
public class SysMenuController {
    
    private final SysMenuService sysMenuService;
    private final SysUserService sysUserService;

    /**
     * 查询所有菜单树（管理员使用）
     */
    @GetMapping("/menuTree")
    public Result<List<SysMenu>> menuTree() {
        // 从数据库查询所有菜单树
        List<SysMenu> menuTree = sysMenuService.menuTree();
        return Result.success(menuTree);
    }

    /**
     * 获取当前用户的菜单树（根据用户权限）
     * 后端为主：根据用户权限返回菜单数据
     */
    @GetMapping("/userMenuTree")
    public Result<List<SysMenu>> getUserMenuTree(Authentication authentication) {
        // 从Authentication中获取当前用户信息
        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.error(401, "用户未认证");
        }

        String username = authentication.getName();
        
        // 根据用户名查询用户信息
        SysUser user = sysUserService.getUserByUsername(username);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 查询用户有权限的菜单树
        List<SysMenu> menuTree = sysMenuService.getUserMenuTree(user.getUserId());
        return Result.success(menuTree);
    }

    /**
     * 新增菜单
     */
    @PostMapping
    public Result<?> add(@Valid @RequestBody MenuDTO menuDTO) {
        boolean success = sysMenuService.insertMenu(menuDTO);
        return success ? Result.success("新增菜单成功") : Result.error("新增菜单失败");
    }
    /**
     * 修改菜单
     */
    @PutMapping
    public Result<?> edit(@Valid @RequestBody MenuDTO menuDTO) {
        boolean success = sysMenuService.updateMenu(menuDTO);
        return success ? Result.success("修改菜单成功") : Result.error("修改菜单失败");
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{menuId}")
    public Result<?> remove(@PathVariable Long menuId) {
        boolean success = sysMenuService.deleteMenu(menuId);
        return success ? Result.success("删除菜单成功") : Result.error("删除菜单失败");
    }
}
