import { createRouter, createWebHistory } from 'vue-router'
import '@/types/router' // 导入路由类型扩展
import { staticRoutes } from './routes'
import { useUserStore } from '@/store/user'
import { useMenuStore } from '@/store/menu'
import { ElMessage } from 'element-plus'

const router = createRouter({
    history: createWebHistory(),
    routes: staticRoutes
})

/**
 * 路由守卫
 * 前端为辅：前端配置完整的路由表，并使用路由守卫检查权限
 */
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();
  const menuStore = useMenuStore();
  
  // 如果用户访问登录页，且已有token，则跳转到首页
  if (to.path === '/login') {
    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    if (token) {
      next({ path: '/' });
    } else {
      next();
    }
    return;
  }
  
  // 如果访问非登录页，检查是否有token
  const token = localStorage.getItem('token') || sessionStorage.getItem('token');
  if (!token) {
    // 没有token，跳转到登录页
    next({ path: '/login', query: { redirect: to.fullPath } });
    return;
  }
  
  // 如果用户信息未加载，先加载用户信息和菜单
  if (!userStore.userInfo && token) {
    try {
      await userStore.getUserInfo();
      // 加载菜单数据（后端为主：后端根据用户权限返回菜单）
      await menuStore.loadMenus();
    } catch (error) {
      // 加载失败，清除token并跳转登录
      localStorage.removeItem('token');
      sessionStorage.removeItem('token');
      next({ path: '/login' });
      return;
    }
  }
  
  // 前端为辅：前端路由守卫检查权限
  // 1. 检查路由meta中配置的权限
  if (to.meta?.roles && to.meta.roles.length > 0) {
    const hasPermission = to.meta.roles.some(role => userStore.hasPermission(role));
    if (!hasPermission) {
      ElMessage.warning('您没有权限访问该页面');
      next({ path: from.path || '/dashboard' });
      return;
    }
  }
  
  // 2. 检查菜单权限：如果路由不在用户菜单中，也拒绝访问
  // 这样可以确保即使前端路由存在，但用户没有菜单权限时也无法访问
  // 后端为主：后端根据用户权限返回菜单，前端检查路由是否在菜单中
  if (to.path !== '/dashboard' && to.path !== '/404' && to.path !== '/login') {
    const menuPaths = getAllMenuPaths(menuStore.menuData);
    // 如果路由不在菜单中，说明用户没有该菜单的权限
    if (menuPaths.length > 0 && !menuPaths.includes(to.path)) {
      // 如果路由配置了权限，再检查一次权限
      if (to.meta?.roles && to.meta.roles.length > 0) {
        const hasPermission = to.meta.roles.some(role => userStore.hasPermission(role));
        if (!hasPermission) {
          ElMessage.warning('您没有权限访问该页面');
          next({ path: '/dashboard' });
          return;
        }
      } else {
        // 如果路由不在菜单中且没有配置权限，拒绝访问
        ElMessage.warning('您没有权限访问该页面');
        next({ path: '/dashboard' });
        return;
      }
    }
  }
  
  next();
});

/**
 * 获取所有菜单路径（递归）
 */
function getAllMenuPaths(menus: any[]): string[] {
  const paths: string[] = [];
  const traverse = (menuList: any[]) => {
    menuList.forEach(menu => {
      if (menu.path) {
        paths.push(menu.path);
      }
      if (menu.children && menu.children.length > 0) {
        traverse(menu.children);
      }
    });
  };
  traverse(menus);
  return paths;
}

export default router