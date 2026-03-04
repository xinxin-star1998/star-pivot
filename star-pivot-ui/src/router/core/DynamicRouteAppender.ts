/**
 * 前端动态路由追加模块
 *
 * 提供前端动态追加隐藏路由的功能
 *
 * ## 主要功能
 *
 * - 追加首页（工作台）路由（数据库不存菜单）
 * - 追加个人中心路由（数据库不存菜单）
 * - 追加字典数据明细路由（数据库不存菜单）
 * - 追加 Druid 监控 iframe 路由（数据库不存菜单）
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
import { useMenuStore } from '@/store/modules/menu'

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
    // 追加首页（工作台）路由，置于最前
    this.appendDashboardConsoleRoute(menuList)

    // 追加个人中心路由
    this.appendUserCenterRoute(menuList)

    // 追加字典数据明细路由
    this.appendDictDataRoute(menuList)

    // 追加分配用户路由
    this.appendAssignUserRoute(menuList)

    // 追加 Druid 监控 iframe 路由
    this.appendDruidIframeRoute(menuList)

    // 可以在这里继续追加其他前端动态路由

    return menuList
  }

  /**
   * 追加「首页（工作台）」路由（数据库不存菜单）
   * 对应视图：@/views/dashboard/console/index.vue，与 HOME_PAGE_PATH 一致
   * 结构：仪表盘（父级目录）-> 工作台（子菜单）
   * @param menuList 菜单列表
   */
  static appendDashboardConsoleRoute(menuList: AppRouteRecord[]): void {
    // 检查是否已存在工作台路由（通过路径或名称）
    // 注意：需要检查所有可能的位置，包括平级路由和嵌套路由
    const existsConsole = menuList.some((route: AppRouteRecord) => {
      // 检查平级路由
      if (route.name === 'Console' || route.path === '/dashboard/console') {
        return true
      }
      // 检查是否在子路由中
      if (route.path === '/dashboard' && route.children) {
        return route.children.some((child) => child.name === 'Console' || child.path === 'console')
      }
      return false
    })

    if (existsConsole) {
      if (import.meta.env.DEV) {
        console.log('[DynamicRouteAppender] 工作台路由已存在，跳过追加')
      }
      return
    }

    // 查找是否已存在仪表盘父级目录
    // 注意：不限制 menuType，因为可能数据库中已经存在但类型不同
    let dashboardParent = menuList.find((route: AppRouteRecord) => route.path === '/dashboard')

    if (!dashboardParent) {
      // 创建仪表盘父级目录
      dashboardParent = {
        path: '/dashboard',
        name: 'Dashboard',
        component: '/index/index', // 使用 Layout
        meta: {
          title: 'menus.dashboard.title',
          icon: 'ri:dashboard-3-line',
          keepAlive: true
        },
        menuType: 'M',
        status: '0',
        orderNum: 0,
        children: []
      }
      menuList.unshift(dashboardParent)
      if (import.meta.env.DEV) {
        console.log('[DynamicRouteAppender] 已创建仪表盘父级目录')
      }
    } else {
      // 如果已存在 dashboard 路由，确保它的配置正确
      // 确保 menuType 是 'M'（目录类型），这样才能承载子路由
      if (dashboardParent.menuType !== 'M') {
        if (import.meta.env.DEV) {
          console.warn(
            `[DynamicRouteAppender] 检测到 /dashboard 路由的 menuType 不是 'M'，将更新为 'M' 以支持子路由`
          )
        }
        dashboardParent.menuType = 'M'
      }
      // 确保有 Layout 组件，这样才能正确渲染子路由
      if (!dashboardParent.component || dashboardParent.component === '') {
        dashboardParent.component = '/index/index'
        if (import.meta.env.DEV) {
          console.log('[DynamicRouteAppender] 已为仪表盘父级目录设置 Layout 组件')
        }
      }
      // 确保有 name，这样路由注册时才能正确识别
      if (!dashboardParent.name) {
        dashboardParent.name = 'Dashboard'
        if (import.meta.env.DEV) {
          console.log('[DynamicRouteAppender] 已为仪表盘父级目录设置路由名称')
        }
      }
    }

    // 确保 children 数组存在
    if (!dashboardParent.children) {
      dashboardParent.children = []
    }

    // 检查子路由中是否已存在工作台
    const existsConsoleChild = dashboardParent.children.some(
      (child) => child.name === 'Console' || child.path === 'console'
    )

    if (!existsConsoleChild) {
      // 创建工作台子路由
      const consoleRoute: AppRouteRecord = {
        path: 'console', // 相对路径
        name: 'Console',
        component: '/dashboard/console/index',
        meta: {
          title: 'menus.dashboard.console',
          icon: 'ri:dashboard-3-line',
          keepAlive: true
        },
        menuType: 'C',
        status: '0',
        orderNum: 0
      }

      dashboardParent.children.push(consoleRoute)
      if (import.meta.env.DEV) {
        console.log('[DynamicRouteAppender] 已动态追加首页（工作台）路由到仪表盘目录下')
      }
    } else {
      if (import.meta.env.DEV) {
        console.log('[DynamicRouteAppender] 工作台子路由已存在于仪表盘目录下，跳过追加')
      }
    }
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
      // 动态获取 system:data 开头的权限列表
      const menuStore = useMenuStore()
      const dataPerms = menuStore.getPermsByPrefix('system:data')

      // 将权限标识转换为 authList 格式
      const authList = dataPerms.map((perm) => {
        // 根据权限后缀生成标题
        const actionMap: Record<string, string> = {
          query: '查询字典数据',
          add: '新增字典数据',
          edit: '编辑字典数据',
          delete: '删除字典数据',
          export: '导出字典数据',
          import: '导入字典数据'
        }
        const action = perm.split(':').pop() || ''
        const title = actionMap[action] || `操作(${action})`
        return { title, authMark: perm }
      })

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
          // 动态从菜单中获取权限列表
          authList: authList.length > 0 ? authList : undefined
        },
        menuType: 'C',
        status: '0',
        orderNum: 1000
      }

      menuList.push(dictDataRoute)
      if (import.meta.env.DEV) {
        console.log('[DynamicRouteAppender] 已动态追加字典数据明细路由，权限列表:', dataPerms)
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

  /**
   * 追加「Druid 监控」iframe 路由（数据库不存菜单）
   * 将 Druid 原生监控页面通过 iframe 方式集成到动态路由中
   * <p>
   * 注意：项目中存在两种 Druid 监控页面：
   * 1. 自定义页面：/monitor/druid（Vue 组件，通过 API 展示监控数据）
   * 2. 内置页面：/monitor/druid-iframe（Druid 原生页面，通过 iframe 嵌入）
   * <p>
   * 此方法添加的是内置页面路由，提供完整的 Druid 原生监控功能
   * @param menuList 菜单列表
   */
  static appendDruidIframeRoute(menuList: AppRouteRecord[]): void {
    const existsDruidIframe = menuList.some(
      (route: AppRouteRecord) =>
        route.name === 'DruidIframe' || route.path === '/monitor/druid-iframe'
    )

    if (!existsDruidIframe) {
      // 使用相对路径，让 Vite 代理处理
      // 这样前端和后端就是同源（都是 localhost:3000），可以满足 SAMEORIGIN 的要求
      // 开发环境：通过 Vite 代理转发到后端
      // 生产环境：如果前后端部署在同一域名下，也是同源
      const druidUrl = '/api/druid/index.html'

      const druidIframeRoute: AppRouteRecord = {
        path: '/monitor/druid-iframe',
        name: 'DruidIframe',
        meta: {
          title: 'Druid 监控（内置页面）',
          icon: 'ri:database-2-line',
          // 设置为 iframe 类型，通过 link 指定外部链接
          isIframe: true,
          // Druid 监控页面地址（使用相对路径，通过 Vite 代理，确保同源）
          link: druidUrl,
          // 不在菜单树中显示，可通过编程式导航访问
          // 如果需要显示，可以设置为 false，但建议通过自定义页面入口访问
          isHide: true,
          // 指定父级菜单路径，用于面包屑、高亮等
          parentPath: '/monitor',
          keepAlive: true
        },
        menuType: 'C',
        status: '0',
        orderNum: 1002
      }

      menuList.push(druidIframeRoute)
      if (import.meta.env.DEV) {
        console.log(
          '[DynamicRouteAppender] 已动态追加 Druid 监控内置页面 iframe 路由, URL:',
          druidUrl
        )
      }
    }
  }
}
