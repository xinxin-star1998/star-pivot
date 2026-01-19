package com.star.pivot.controller;

import com.star.pivot.common.domain.Result;
import com.star.pivot.system.domain.dto.MenuDTO;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.service.SysMenuService;
import com.star.pivot.system.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * 查询所有菜单树接口（管理员使用）
     * 
     * @return 菜单树列表，包含所有菜单项及其层级关系
     */
    @PreAuthorize("hasAuthority('system:menu:query')")
    @GetMapping("/menuTree")
    public Result<List<SysMenu>> menuTree() {
        // 从数据库查询所有菜单树
        List<SysMenu> menuTree = sysMenuService.menuTree();
        return Result.success(menuTree);
    }

    /**
     * 获取当前用户的菜单树接口（根据用户权限）
     * 
     * @param authentication Spring Security认证对象
     * @return 当前用户有权限的菜单树列表
     */
    @PreAuthorize("hasAuthority('system:menu:query')")
    @GetMapping("/userMenuTree")
    public ResponseEntity<Result<List<SysMenu>>> getUserMenuTree(Authentication authentication) {
        // 从Authentication中获取当前用户信息
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.error(401, "用户未认证"));
        }

        String username = authentication.getName();
        
        // 根据用户名查询用户信息
        SysUser user = sysUserService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Result.error(404, "用户不存在"));
        }

        // 查询用户有权限的菜单树
        List<SysMenu> menuTree = sysMenuService.getUserMenuTree(user.getUserId());
        return ResponseEntity.ok(Result.success(menuTree));
    }

    /**
     * 新增菜单接口
     * 
     * @param menuDTO 菜单数据传输对象，包含菜单的详细信息
     * @return 操作结果，成功或失败的响应
     */
    @PreAuthorize("hasAuthority('system:menu:add')")
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody MenuDTO menuDTO) {
        boolean success = sysMenuService.insertMenu(menuDTO);
        return success ? Result.success("新增菜单成功") : Result.error("新增菜单失败");
    }
    /**
     * 修改菜单接口
     * 
     * @param menuDTO 菜单数据传输对象，包含要更新的菜单信息
     * @return 操作结果，成功或失败的响应
     */
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @PutMapping
    public Result<?> edit(@Valid @RequestBody MenuDTO menuDTO) {
        boolean success = sysMenuService.updateMenu(menuDTO);
        return success ? Result.success("修改菜单成功") : Result.error("修改菜单失败");
    }

    /**
     * 删除菜单接口
     * 
     * @param menuId 菜单ID
     * @return 操作结果，成功或失败的响应
     */
    @PreAuthorize("hasAuthority('system:menu:delete')")
    @DeleteMapping("/{menuId}")
    public Result<?> remove(@PathVariable("menuId") Long menuId) {
        boolean success = sysMenuService.deleteMenu(menuId);
        return success ? Result.success("删除菜单成功") : Result.error("删除菜单失败");
    }
    /**
     * 获取上级菜单树接口
     * 
     * @return 上级菜单列表，用于菜单选择或层级展示
     */
    @PreAuthorize("hasAuthority('system:menu:query')")
    @GetMapping("/getParent")
    public Result<List<SysMenu>> getParent(){
        List<SysMenu> list = sysMenuService.getParent();
        return Result.success("查询成功",list);
    }
    /**
     * 根据ID获取菜单接口
     * 
     * @param menuId 菜单ID
     * @return 指定ID的菜单信息
     */
    @PreAuthorize("hasAuthority('system:menu:query')")
    @GetMapping("/getById/{menuId}")
    public Result<SysMenu> getById(@PathVariable("menuId") Long menuId){
        SysMenu menu = sysMenuService.getById(menuId);
        return Result.success("查询成功",menu);
    }
}
