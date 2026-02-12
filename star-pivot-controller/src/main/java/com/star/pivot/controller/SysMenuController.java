package com.star.pivot.controller;

import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.system.domain.dto.MenuDTO;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "菜单管理", description = "菜单的增删改查、菜单树查询等接口")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    /**
     * 查询所有菜单树接口（管理员使用）
     *
     * @return 菜单树列表，包含所有菜单项及其层级关系
     */
    @Operation(summary = "查询菜单树", description = "获取所有菜单的树形结构（管理员使用）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
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
    @Operation(summary = "新增菜单", description = "创建新菜单，需要提供菜单名称、路由、权限标识等信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "新增成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
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
    @Operation(summary = "修改菜单", description = "更新菜单信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "404", description = "菜单不存在")
    })
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @PutMapping
    public Result<?> edit(@Valid @RequestBody MenuDTO menuDTO) {
        boolean success = sysMenuService.updateMenu(menuDTO);
        return success ? Result.success("修改菜单成功") : Result.error("修改菜单失败");
    }

    /**
     * 删除菜单接口（支持单删和批量删除）
     * 
     * @param deleteRequest 删除请求，包含 ids 数组
     * @return 操作结果，成功或失败的响应
     */
    @Operation(summary = "删除菜单", description = "删除菜单（支持批量删除），如果菜单有子菜单则不能删除")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "删除ID为空或存在子菜单")
    })
    @PreAuthorize("hasAuthority('system:menu:delete')")
    @DeleteMapping("/delete")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> menuIds = deleteRequest.getIds();
        if (menuIds == null || menuIds.isEmpty()) {
            return Result.error("删除ID不能为空");
        }
        boolean success = sysMenuService.deleteMenuByIds(menuIds);
        return success ? Result.success("删除菜单成功") : Result.error("删除菜单失败");
    }
    /**
     * 获取上级菜单树接口
     * 
     * @return 上级菜单列表，用于菜单选择或层级展示
     */
    @Operation(summary = "获取上级菜单树", description = "获取所有可作为上级菜单的菜单列表，用于菜单选择")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
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
    @Operation(summary = "获取菜单详情", description = "根据菜单ID获取菜单的详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = SysMenu.class))),
            @ApiResponse(responseCode = "404", description = "菜单不存在")
    })
    @PreAuthorize("hasAuthority('system:menu:query')")
    @GetMapping("/getById/{menuId}")
    public Result<SysMenu> getById(@Parameter(description = "菜单ID") @PathVariable("menuId") Long menuId){
        SysMenu menu = sysMenuService.getById(menuId);
        return Result.success("查询成功",menu);
    }
}
