package com.star.pivot.controller;

import com.star.pivot.common.domain.Result;
import com.star.pivot.system.domain.dto.MenuDTO;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.service.SysMenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 * <p>
 * 提供菜单的增删改查等管理接口，需 system:menu:* 权限。当前用户菜单树已迁移至 {@link RouterController#getUserMenuTree}。
 * </p>
 */
@RestController
@RequestMapping("/sys/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;

    /**
     * 查询所有菜单树接口（管理员使用）
     *
     * @return 菜单树列表，包含所有菜单项及其层级关系
     */
    @PreAuthorize("hasAuthority('system:menu:query')")
    @GetMapping("/menuTree")
    public Result<List<SysMenu>> menuTree() {
        List<SysMenu> menuTree = sysMenuService.menuTree();
        return Result.success(menuTree);
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
