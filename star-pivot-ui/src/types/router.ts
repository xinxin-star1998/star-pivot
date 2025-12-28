import 'vue-router'

/**
 * 扩展路由 meta 类型
 */
declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    icon?: string
    hideLayout?: boolean
    roles?: string[]
  }
}

