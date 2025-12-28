import type { RouteRecordRaw } from 'vue-router';
import type { FrontendMenu } from '@/http/api/menu/menu';
import router from '@/router';

/**
 * 动态加载组件
 */
function loadComponent(componentPath: string): () => Promise<any> {
  // 如果componentPath以/开头，去掉第一个/
  let path = componentPath.startsWith('/') ? componentPath.slice(1) : componentPath;
  
  // 支持多种路径格式
  if (path.startsWith('@/')) {
    // 使用@别名
    path = path.replace('@/', '');
    return () => import(/* @vite-ignore */ `@/${path}`);
  } else if (path.startsWith('views/')) {
    // views目录下的组件
    path = path.replace('views/', '');
    return () => import(/* @vite-ignore */ `@/views/${path}`);
  } else {
    // 直接路径，默认在views目录下
    return () => import(/* @vite-ignore */ `@/views/${path}`);
  }
}

/**
 * 将菜单转换为路由配置
 */
export function menuToRoute(menu: FrontendMenu, parentPath = ''): RouteRecordRaw {
  const route: RouteRecordRaw = {
    path: menu.path.startsWith('/') ? menu.path : `${parentPath}/${menu.path}`,
    name: menu.name || menu.path,
    meta: {
      title: menu.meta.title,
      icon: menu.meta.icon,
      roles: menu.meta.roles,
      hideLayout: menu.meta.hideLayout
    }
  };

  // 如果有component，动态加载组件
  if (menu.component) {
    if (menu.component === 'Layout') {
      // Layout组件使用固定的layout组件
      route.component = () => import('@/layout/index.vue');
    } else {
      // 其他组件动态加载
      route.component = typeof menu.component === 'function' 
        ? menu.component 
        : loadComponent(menu.component);
    }
  }

  // 处理子路由
  if (menu.children && menu.children.length > 0) {
    route.children = menu.children.map(child => menuToRoute(child, route.path));
  }

  return route;
}

/**
 * 动态注册路由
 */
export function registerRoutes(menus: FrontendMenu[]): void {
  // 找到Layout路由（通常是根路由）
  const layoutRoute = router.getRoutes().find(route => route.path === '/');
  
  if (layoutRoute && layoutRoute.children) {
    // 将菜单转换为路由并添加到Layout的children中
    menus.forEach(menu => {
      const route = menuToRoute(menu);
      // 检查路由是否已存在
      const existingRoute = layoutRoute.children?.find(r => r.path === route.path);
      if (!existingRoute) {
        router.addRoute('Layout', route);
      }
    });
  }
}

/**
 * 移除动态路由（登出时使用）
 */
export function removeDynamicRoutes(): void {
  // 获取所有路由
  const routes = router.getRoutes();
  
  // 移除动态添加的路由（保留登录页和Layout基础路由）
  routes.forEach(route => {
    if (route.name && route.name !== 'Login' && route.name !== 'Layout' && route.name !== 'dashboard') {
      router.removeRoute(route.name);
    }
  });
}

