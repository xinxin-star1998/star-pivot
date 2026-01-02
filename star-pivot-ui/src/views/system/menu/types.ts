/**
 * 菜单管理相关的类型定义
 */

/**
 * 菜单表单数据类型
 */
export interface MenuFormData {
  menuType: 'menu' | 'button'
  id: number
  name: string
  path: string
  label: string
  component: string
  icon: string
  isEnable: boolean
  sort: number
  isMenu: boolean
  keepAlive: boolean
  isHide: boolean
  isHideTab: boolean
  link: string
  isIframe: boolean
  showBadge: boolean
  showTextBadge: string
  fixedTab: boolean
  activePath: string
  roles: string[]
  isFullPage: boolean
  authName: string
  authLabel: string
  authIcon: string
  authSort: number
}

