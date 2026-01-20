package com.star.pivot.controller;

import com.star.pivot.system.domain.bo.MetaVo;
import com.star.pivot.system.domain.bo.RouterVo;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.service.SysMenuService;
import com.star.pivot.system.utils.SecurityContextUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/router")
@RequiredArgsConstructor
public class RouterController {
    
    private final SysMenuService sysMenuService;
    
    /**
     * 获取动态路由
     * 根据当前用户权限返回对应的路由列表
     * @return 动态路由列表
     */
    @PreAuthorize("hasAuthority('system:menu:query')")
    @GetMapping("/dynamic-routes")
    public List<RouterVo> getDynamicRoutes() {
        // 根据当前用户权限获取菜单树，而不是返回所有菜单
        Long userId = SecurityContextUtils.getUserId();
        List<SysMenu> menuTree = sysMenuService.getUserMenuTree(userId);
        
        // 转换为路由树
        return buildRouterTree(menuTree);
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
