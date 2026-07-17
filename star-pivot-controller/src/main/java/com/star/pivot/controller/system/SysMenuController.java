package com.star.pivot.controller.system;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.system.domain.constant.I18nConstants;
import com.star.pivot.system.domain.dto.MenuDTO;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.service.interfaces.SysI18nService;
import com.star.pivot.system.service.interfaces.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private final SysI18nService sysI18nService;

    /**
     * 查询所有菜单树接口（管理员使用）
     * <p>
     * menuName 按请求语言（X-Lang / Accept-Language）从 sys_i18n 解析，缺失回退表字段。
     * </p>
     *
     * @return 菜单树列表，包含所有菜单项及其层级关系
     */
    @Operation(summary = "查询菜单树", description = "获取所有菜单的树形结构（管理员使用），menuName 按请求语言解析")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @PreAuthorize("hasAuthority('system:menu:query')")
    @GetMapping("/menuTree")
    public Result<List<SysMenu>> menuTree(HttpServletRequest request) {
        List<SysMenu> menuTree = sysMenuService.menuTree();
        applyLocalizedMenuNames(menuTree, request);
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
    @Log(title = "新增菜单", businessType = AppConstants.BusinessType.INSERT)
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
    @Log(title = "修改菜单", businessType = AppConstants.BusinessType.UPDATE)
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
    @Log(title = "删除菜单", businessType = AppConstants.BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:menu:delete')")
    @DeleteMapping("/delete")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> menuIds = validateIds(deleteRequest.getIds());
        boolean success = sysMenuService.deleteMenuByIds(menuIds);
        return success ? Result.success("删除菜单成功") : Result.error("删除菜单失败");
    }
    /**
     * 获取上级菜单树接口
     * <p>
     * menuName 按请求语言解析，与菜单列表、侧栏一致。
     * </p>
     *
     * @return 上级菜单列表，用于菜单选择或层级展示
     */
    @Operation(summary = "获取上级菜单树", description = "获取所有可作为上级菜单的菜单列表，menuName 按请求语言解析")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @PreAuthorize("hasAuthority('system:menu:query')")
    @GetMapping("/getParent")
    public Result<List<SysMenu>> getParent(HttpServletRequest request) {
        List<SysMenu> list = sysMenuService.getParent();
        applyLocalizedMenuNames(list, request);
        return Result.success("查询成功", list);
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
        if (menu != null) {
            Map<String, String> translations = sysI18nService.getResourceTranslations(
                    I18nConstants.NAMESPACE_MENU,
                    String.valueOf(menuId),
                    I18nConstants.FIELD_MENU_NAME);
            if (translations == null || translations.isEmpty()) {
                translations = new LinkedHashMap<>();
            }
            String defaultLang = sysI18nService.getDefaultLangCode();
            translations.putIfAbsent(defaultLang, menu.getMenuName());
            menu.setTranslations(translations);
        }
        return Result.success("查询成功",menu);
    }

    /**
     * 验证ID列表非空
     */
    private List<Long> validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        return ids;
    }

    /**
     * 按请求语言覆盖菜单树中的 menuName（缺失回退原值）
     */
    private void applyLocalizedMenuNames(List<SysMenu> menus, HttpServletRequest request) {
        if (menus == null || menus.isEmpty()) {
            return;
        }
        String lang = sysI18nService.resolveRequestLang(request);
        Map<Long, String> titleMap = sysI18nService.getMenuTitleMap(lang);
        if (titleMap == null || titleMap.isEmpty()) {
            return;
        }
        applyLocalizedMenuNames(menus, titleMap);
    }

    private void applyLocalizedMenuNames(List<SysMenu> menus, Map<Long, String> titleMap) {
        for (SysMenu menu : menus) {
            if (menu.getMenuId() != null) {
                String localized = titleMap.get(menu.getMenuId());
                if (StringUtils.hasText(localized)) {
                    menu.setMenuName(localized);
                }
            }
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                applyLocalizedMenuNames(menu.getChildren(), titleMap);
            }
        }
    }
}
