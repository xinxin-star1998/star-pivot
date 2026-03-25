package com.star.pivot.controller.system;

import com.star.pivot.framework.domain.Result;
import com.star.pivot.system.domain.bo.MetaVo;
import com.star.pivot.system.domain.bo.RouterVo;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.service.SysMenuService;
import com.star.pivot.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 路由与当前用户菜单控制器
 * <p>
 * 提供动态路由、当前用户菜单树等与登录用户强相关的接口，已登录即可访问。
 * </p>
 */
@RestController
@RequestMapping("/router")
@RequiredArgsConstructor
@Tag(name = "路由管理", description = "动态路由、用户菜单树等接口")
public class RouterController {

    private final SysMenuService sysMenuService;
    private final SysUserService sysUserService;

    /**
     * 获取当前用户的菜单树（根据用户权限）
     * <p>
     * 从 Authentication 解析用户，再查询其有权限的菜单树。与 dynamic-routes 共用同一套解析逻辑。
     * </p>
     *
     * @param authentication Spring Security 认证对象
     * @return 当前用户有权限的菜单树，未认证或用户不存在时返回 401/404
     */
    @Operation(summary = "获取用户菜单树", description = "获取当前登录用户有权限的菜单树")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "用户未认证"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/userMenuTree")
    public ResponseEntity<Result<List<SysMenu>>> getUserMenuTree(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.error(401, "用户未认证"));
        }
        List<SysMenu> menuTree = getCurrentUserMenuTree(authentication);
        if (menuTree == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Result.error(404, "用户不存在"));
        }
        return ResponseEntity.ok(Result.success(menuTree));
    }

    /**
     * 获取动态路由
     * <p>
     * 根据当前用户权限获取菜单树并转为路由列表（仅本人路由，已登录即可访问）。
     * </p>
     *
     * @param authentication Spring Security 认证对象
     * @return 动态路由列表，无法解析用户时返回空列表
     */
    @Operation(summary = "获取动态路由", description = "根据当前用户权限获取动态路由列表，用于前端路由配置")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/dynamic-routes")
    public List<RouterVo> getDynamicRoutes(Authentication authentication) {
        List<SysMenu> menuTree = getCurrentUserMenuTree(authentication);
        if (menuTree == null || menuTree.isEmpty()) {
            return Collections.emptyList();
        }
        return buildRouterTree(menuTree);
    }

    /**
     * 根据当前认证信息解析用户并获取其菜单树（共用逻辑）
     *
     * @param authentication 认证信息，可为 null
     * @return 当前用户的菜单树；未认证或用户不存在时返回 null
     */
    private List<SysMenu> getCurrentUserMenuTree(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        SysUser user = sysUserService.getUserByUsername(username);
        if (user == null) {
            return null;
        }
        return sysMenuService.getUserMenuTree(user.getUserId());
    }
    
    /**
     * 构建路由树
     * @param menuTree 菜单树
     * @return 路由树
     */
    private List<RouterVo> buildRouterTree(List<SysMenu> menuTree) {
        List<RouterVo> routerTree = new ArrayList<>();
        
        for (SysMenu menu : menuTree) {
            RouterVo router = convertMenuToRouter(menu);
            
            // 递归构建子路由
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                List<RouterVo> children = buildRouterTree(menu.getChildren());
                router.setChildren(children);
            }
            
            routerTree.add(router);
        }
        
        return routerTree;
    }
    
    /**
     * 将SysMenu转换为RouterVo
     * @param menu 菜单
     * @return 路由
     */
    private RouterVo convertMenuToRouter(SysMenu menu) {
        RouterVo router = new RouterVo();
        router.setName(menu.getRouteName());
        router.setPath(menu.getPath());
        router.setHidden("1".equals(menu.getVisible()));
        router.setComponent(menu.getComponent());
        router.setQuery(menu.getQuery());
        
        // 构建Meta信息
        MetaVo meta = new MetaVo();
        meta.setTitle(menu.getMenuName());
        meta.setIcon(menu.getIcon());
        meta.setNoCache(menu.getIsCache() != null && menu.getIsCache() == 1);
        
        // 设置link（如果是外链）
        if (menu.getIsFrame() != null && menu.getIsFrame() == 0 && menu.getPath() != null && (menu.getPath().startsWith("http://") || menu.getPath().startsWith("https://"))) {
            meta.setLink(menu.getPath());
        }
        
        router.setMeta(meta);
        
       return router;
    }
}
