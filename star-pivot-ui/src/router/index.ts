import { createRouter, createWebHistory } from 'vue-router'
import '@/types/router' // 导入路由类型扩展
import { staticRoutes, notFoundRoute } from './routes'
import { useUserStore } from '@/store/user'
import { useMenuStore } from '@/store/menu'

const router = createRouter({
    history: createWebHistory(),
    routes: [...staticRoutes, notFoundRoute]
})

/**
 * 路由守卫
 * 后端为主：只验证token有效性，菜单权限由后端控制
 */
router.beforeEach(async (to, _from, next) => {
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
  
  // 后端为主：不再进行前端权限检查，只验证token有效性
  // 后端已根据用户权限返回了菜单数据，前端只需显示
  next();
});

export default router