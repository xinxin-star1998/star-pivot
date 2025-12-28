import type { RouteRecordRaw } from 'vue-router';

/**
 * 完整的静态路由配置
 * 前端为辅：前端配置完整的路由表，路由守卫检查权限
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
          icon: 'Setting',
          roles: ['sys:manage']
        },
        children: [
          {
            path: '/system/user',
            component: () => import('@/views/system/user/index.vue'),
            name: 'systemUser',
            meta: {
              title: '用户管理',
              icon: 'UserFilled',
              roles: ['sys:user']
            }
          },
          {
            path: '/system/role',
            component: () => import('@/views/system/role/index.vue'),
            name: 'systemRole',
            meta: {
              title: '角色管理',
              icon: 'Wallet',
              roles: ['sys:role']
            }
          },
          {
            path: '/system/menu',
            component: () => import('@/views/system/menu/index.vue'),
            name: 'systemMenu',
            meta: {
              title: '菜单管理',
              icon: 'Menu',
              roles: ['sys:menu']
            }
          }
        ]
      },
      // 404页面
      {
        path: '/404',
        component: () => import('@/views/error/404/index.vue'),
        name: 'NotFound',
        meta: {
          title: '页面不存在',
          hideLayout: true
        }
      }
    ]
  },
  // 404重定向
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
];

