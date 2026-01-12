package com.star.pivot.controller;

import com.star.pivot.system.domain.bo.MetaVo;
import com.star.pivot.system.domain.bo.RouterVo;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.service.SysMenuService;
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
     * @return 动态路由列表
     */
    @GetMapping("/dynamic-routes")
    public List<RouterVo> getDynamicRoutes() {
        // 获取菜单树
        List<SysMenu> menuTree = sysMenuService.menuTree();
        
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
        meta.setNoCache("1".equals(menu.getIsCache()));
        
        // 设置link（如果是外链）
        if ("0".equals(menu.getIsFrame()) && menu.getPath() != null && (menu.getPath().startsWith("http://") || menu.getPath().startsWith("https://"))) {
            meta.setLink(menu.getPath());
        }
        
        router.setMeta(meta);
        
        return router;
    }
}
