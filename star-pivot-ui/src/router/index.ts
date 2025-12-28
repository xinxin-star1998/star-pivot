import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import '@/types/router' // 导入路由类型扩展
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const routes: Array<RouteRecordRaw> = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue'),
        meta: { title: '登录', hideLayout: true }
    },
    {
        path: '/',
        component: () => import('@/layout/index.vue'),
        name: 'Layout',
        redirect: '/dashboard',
        children: [
            {
                path: '/dashboard',
                component: () => import('@/views/dashboard/index.vue'),
                name: 'dashboard',
                meta: {
                    title: '首页',
                    icon: '#icondashboard'
                }
            }
        ]
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/dashboard'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();
  
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
  
  // 检查路由权限
  if (to.meta?.roles && to.meta.roles.length > 0) {
    const hasPermission = to.meta.roles.some(role => userStore.hasPermission(role));
    if (!hasPermission) {
      ElMessage.warning('您没有权限访问该页面');
      next({ path: from.path || '/dashboard' });
      return;
    }
  }
  
  // 如果用户信息未加载，先加载用户信息
  if (!userStore.userInfo && token) {
    try {
      await userStore.getUserInfo();
    } catch (error) {
      // 加载失败，清除token并跳转登录
      localStorage.removeItem('token');
      sessionStorage.removeItem('token');
      next({ path: '/login' });
      return;
    }
  }
  
  next();
});

export default router