/**
 * 前端动态路由追加模块
 *
 * 提供前端动态追加隐藏路由的功能
 *
 * ## 主要功能
 *
 * - 追加个人中心路由（数据库不存菜单）
 * - 追加字典数据明细路由（数据库不存菜单）
 * - 支持扩展其他前端动态路由
 *
 * ## 使用场景
 *
 * - 路由守卫中动态添加前端路由
 * - 不需要在数据库中配置的隐藏路由
 * - 明细页面路由（如字典数据）
 *
 * @module router/core/DynamicRouteAppender
 * @author Art Design Pro Team
 */

import type { AppRouteRecord } from '@/types/router'

/**
 * 前端动态路由追加器
 */
export class DynamicRouteAppender {
  /**
   * 追加所有前端动态路由到菜单列表
   * @param menuList 菜单列表
   * @returns 更新后的菜单列表
   */
  static appendDynamicRoutes(menuList: AppRouteRecord[]): AppRouteRecord[] {
    // 追加个人中心路由
    this.appendUserCenterRoute(menuList)

    // 追加字典数据明细路由
    this.appendDictDataRoute(menuList)

    // 追加分配用户路由
    this.appendAssignUserRoute(menuList)

    // 可以在这里继续追加其他前端动态路由

    return menuList
  }

  /**
   * 追加「个人中心」路由（数据库不存菜单）
   * @param menuList 菜单列表
   */
  static appendUserCenterRoute(menuList: AppRouteRecord[]): void {
    const existsUserCenter = menuList.some(
      (route: AppRouteRecord) => route.name === 'UserCenter' || route.path === '/system/user-center'
    )

    if (!existsUserCenter) {
      const userCenterRoute: AppRouteRecord = {
        path: '/system/user-center',
        name: 'UserCenter',
        // 注意：这里以 `/` 开头，避免出现 `viewssystem` 拼接问题
        component: '/system/user-center/index',
        meta: {
          title: '个人中心',
          icon: 'ri:user-3-line',
          isHide: true,
          keepAlive: true
        },
        menuType: 'C',
        status: '0',
        orderNum: 999
      }

      menuList.push(userCenterRoute)
      if (import.meta.env.DEV) {
        console.log('[DynamicRouteAppender] 已动态追加个人中心路由')
      }
    }
  }

  /**
   * 追加「字典数据」明细路由（数据库不存菜单）
   * @param menuList 菜单列表
   */
  static appendDictDataRoute(menuList: AppRouteRecord[]): void {
    const existsDictData = menuList.some(
      (route: AppRouteRecord) =>
        route.name === 'DictData' || route.path?.includes('/system/dict/data')
    )

    if (!existsDictData) {
      const dictDataRoute: AppRouteRecord = {
        // 明细页路径，带上动态参数 dictType
        path: '/system/dict/data/:dictType',
        name: 'DictData',
        // 注意：这里以 `/` 开头，对应视图文件 `src/views/system/dict/dict-data.vue`
        component: '/system/dict/dict-data',
        meta: {
          title: '字典数据',
          // 不在菜单树中显示，只通过点击"字典类型"进入
          isHide: true,
          // 指定父级菜单路径，用于面包屑、高亮等
          parentPath: '/system/dict',
          keepAlive: true,
          // 手动配置权限列表，因为该路由不在数据库中，无法从菜单获取 perms
          // authList: [
          //   { title: '查询字典数据', authMark: 'system:data:query' },
          //   { title: '新增字典数据', authMark: 'system:data:add' },
          //   { title: '编辑字典数据', authMark: 'system:data:edit' },
          //   { title: '删除字典数据', authMark: 'system:data:delete' }
          // ]
        },
        menuType: 'C',
        status: '0',
        orderNum: 1000
      }

      menuList.push(dictDataRoute)
      if (import.meta.env.DEV) {
        console.log('[DynamicRouteAppender] 已动态追加字典数据明细路由')
      }
    }
  }

  /**
   * 追加「分配用户」路由（数据库不存菜单）
   * @param menuList 菜单列表
   */
  static appendAssignUserRoute(menuList: AppRouteRecord[]): void {
    const existsAssignUser = menuList.some(
      (route: AppRouteRecord) =>
        route.name === 'AssignUser' || route.path?.includes('/system/role/assign-user')
    )

    if (!existsAssignUser) {
      const assignUserRoute: AppRouteRecord = {
        // 分配用户页面路径，带上动态参数 roleId
        path: '/system/role/assign-user/:roleId',
        name: 'AssignUser',
        // 注意：这里以 `/` 开头，对应视图文件 `src/views/system/role/assign-user.vue`
        component: '/system/role/assign-user',
        meta: {
          title: '分配用户',
          // 不在菜单树中显示，只通过点击"分配用户"按钮进入
          isHide: true,
          // 指定父级菜单路径，用于面包屑、高亮等
          parentPath: '/system/role',
          keepAlive: true,
          isHideTab: true
        },
        menuType: 'C',
        status: '0',
        orderNum: 1001
      }

      menuList.push(assignUserRoute)
      if (import.meta.env.DEV) {
        console.log('[DynamicRouteAppender] 已动态追加分配用户路由')
      }
    }
  }
}
