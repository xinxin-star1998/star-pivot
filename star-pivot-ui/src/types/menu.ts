/**
 * 菜单项元数据接口
 */
export interface MenuMeta {
  title: string
  icon: string
  roles?: string[]
  hideLayout?: boolean
}

/**
 * 菜单项接口
 */
export interface MenuItem {
  path: string
  component?: string | (() => Promise<any>)
  name?: string
  meta: MenuMeta
  children?: MenuItem[]
}

