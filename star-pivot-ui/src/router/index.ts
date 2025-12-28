import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import '@/types/router' // 导入路由类型扩展

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
router.beforeEach((to, from, next) => {
  // 如果用户访问登录页，且已有token，则跳转到首页
  if (to.path === '/login') {
    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    if (token) {
      next({ path: '/' });
    } else {
      next();
    }
  } else {
    // 如果访问非登录页，检查是否有token
    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    if (!token) {
      // 没有token，跳转到登录页
      next({ path: '/login' });
    } else {
      next();
    }
  }
});

export default router