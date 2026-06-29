import type { AppRouteRecord } from '@/types/router'

/** 仅菜单展示、点击新开页，不参与 Vue Router 动态注册 */
export function isLinkOnlyMenu(route: AppRouteRecord): boolean {
  return !!(
    route.meta?.link &&
    !route.meta?.isIframe &&
    !route.component &&
    (!route.children || route.children.length === 0)
  )
}
