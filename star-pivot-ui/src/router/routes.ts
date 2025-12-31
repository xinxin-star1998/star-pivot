import type { RouteRecordRaw } from 'vue-router';

/**
 * 基础静态路由配置
 * 后端为主：前端只配置基础路由，权限由后端控制
 */
export const staticRoutes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { 
      title: '登录', 
      hideLayout: true 
    }
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
          icon: 'House'
        }
      },
      // 系统管理模块
      {
        path: '/system',
        component: () => import('@/layout/index.vue'),
        name: 'system',
        meta: {
          title: '系统管理',
          icon: 'Setting'
        },
        children: [
          {
            path: '/system/user',
            component: () => import('@/views/system/user/index.vue'),
            name: 'systemUser',
            meta: {
              title: '用户管理',
              icon: 'UserFilled'
            }
          },
          {
            path: '/system/role',
            component: () => import('@/views/system/role/index.vue'),
            name: 'systemRole',
            meta: {
              title: '角色管理',
              icon: 'User'
            }
          },
          {
            path: '/system/menu',
            component: () => import('@/views/system/menu/index.vue'),
            name: 'systemMenu',
            meta: {
              title: '菜单管理',
              icon: 'Menu'
            }
          }
        ]
      },
      // 错误页面
      {
        path: '/403',
        name: '403',
        component: () => import('@/views/error/403/index.vue'),
        meta: { 
          title: '403', 
          hideLayout: true 
        }
      },
      {
        path: '/404',
        name: '404',
        component: () => import('@/views/error/404/index.vue'),
        meta: { 
          title: '404', 
          hideLayout: true 
        }
      },
      {
        path: '/500',
        name: '500',
        component: () => import('@/views/error/500/index.vue'),
        meta: { 
          title: '500', 
          hideLayout: true 
        }
      }
    ]
  }
];

// 通配符路由，始终指向404
export const notFoundRoute: RouteRecordRaw = {
  path: '/:pathMatch(.*)*',
  redirect: '/404'
};