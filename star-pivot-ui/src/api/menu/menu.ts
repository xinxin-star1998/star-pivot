import request from '@/utils/http'
import type { MenuFormData } from '@/views/system/menu/types'

// 后端菜单实体类型
export interface SysMenu {
  menuId?: number
  menuName: string
  parentId?: number
  orderNum?: number
  path: string
  component?: string
  query?: string
  routeName?: string
  isFrame?: number // 0是外链/iframe, 1否
  isCache?: number // 0缓存, 1不缓存
  menuType?: string // M目录 C菜单 F按钮
  visible?: string // 0显示 1隐藏
  status?: string // 0正常 1停用
  perms?: string
  icon?: string
  createBy?: string
  createTime?: string // 创建时间
  updateBy?: string
  updateTime?: string // 更新时间
  remark?: string
  label:string
  value:number
  children?: SysMenu[]
}

/**
 * 获取菜单列表（从后端获取）
 */
export function fetchGetMenuList() {
  return request.get<SysMenu[]>({
    url: '/api/sys/menu/userMenuTree'
  })
}

/**
 * 获取所有菜单树（管理员使用）
 */
export function fetchGetMenuTree() {
  return request.get<SysMenu[]>({
    url: '/api/sys/menu/menuTree'
  })
}

/**
 * 获取上级菜单树
 */
export function fetchGetParentMenu() {
  return request.get<SysMenu[]>({
    url: '/api/sys/menu/getParent'
  })
}

/**
 * 新增菜单
 */
export function fetchAddMenu(data: MenuFormData) {
  return request.post({
    url: '/api/sys/menu/add',
    data
  })
}

/**
 * 修改菜单
 */
export function fetchUpdateMenu(data: MenuFormData) {
  return request.put({
    url: '/api/sys/menu',
    data
  })
}

/**
 * 删除菜单
 */
export function fetchDeleteMenu(menuId: number) {
  return request.del({
    url: `/api/sys/menu/${menuId}`
  })
}
/**
 * 根据ID获取菜单接口
 */
export function fetchGetMenuById(menuId: number) {
  return request.get<SysMenu>({
    url: `/api/sys/menu/getById/${menuId}`
  })
}

/**
 * 根据角色ID获取已分配的菜单列表
 */
export function fetchGetRoleMenus(roleId: number) {
  return request.get<SysMenu[]>({
    url: `/api/sys/menu/getMenuByRoleId/${roleId}`
  })
}