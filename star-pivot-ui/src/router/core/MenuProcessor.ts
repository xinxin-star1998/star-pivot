/**
 * 菜单处理器
 *
 * 负责菜单数据的获取、过滤和处理
 *
 * @module router/core/MenuProcessor
 * @author Art Design Pro Team
 */

import type { AppRouteRecord } from '@/types/router'
import { fetchGetMenuList, type SysMenu } from '@/api/system-manage'
import { RoutesAlias } from '../routesAlias'
import { formatMenuTitle } from '@/utils'

export class MenuProcessor {
  /**
   * 获取菜单数据（从后端获取）
   */
  async getMenuList(): Promise<AppRouteRecord[]> {
    try {
      // 从后端获取菜单数据
      const sysMenuList = await fetchGetMenuList()

      // 检查返回数据
      if (!sysMenuList || !Array.isArray(sysMenuList)) {
        console.error('[MenuProcessor] 后端返回的菜单数据格式错误:', sysMenuList)
        throw new Error('后端返回的菜单数据格式错误，期望数组类型')
      }

      console.log('[MenuProcessor] 获取到菜单数据:', sysMenuList)

      // 将后端 SysMenu 转换为前端 AppRouteRecord
      const convertedList = this.convertSysMenuToRouteRecord(sysMenuList)

      // 过滤空菜单项
      const menuList = this.filterEmptyMenus(convertedList)

      // 在规范化路径之前，验证原始路径配置
      this.validateMenuPaths(menuList)

      // 规范化路径（将相对路径转换为完整路径）
      return this.normalizeMenuPaths(menuList)
    } catch (error) {
      console.error('[MenuProcessor] 获取菜单数据失败:', error)
      throw error
    }
  }

  /**
   * 将后端 SysMenu 转换为前端 AppRouteRecord
   * @param sysMenus 后端菜单列表
   * @param level 当前层级（从0开始，0表示一级菜单）
   */
  private convertSysMenuToRouteRecord(sysMenus: SysMenu[], level = 0): AppRouteRecord[] {
    if (!Array.isArray(sysMenus)) {
      console.error('[MenuProcessor] convertSysMenuToRouteRecord 接收到的不是数组:', sysMenus)
      return []
    }

    return sysMenus.map((menu, index) => {
      try {
        if (!menu) {
          console.warn(`[MenuProcessor] 菜单项 ${index} 为空，跳过`)
          return null
        }

        // 清理路径中的 # 符号
        let path = (menu.path || '').replace(/#/g, '').trim()
        const isExternalLink = path.startsWith('http://') || path.startsWith('https://')
        // isFrame: 0是外链/iframe, 1否
        const isIframe = menu.isFrame === 0 && !isExternalLink
        const isDirectory = menu.menuType === 'M' // M目录 C菜单 F按钮

        // 处理 component
        let component = menu.component
        if (component) {
          // 清理 component 中的 # 符号
          component = component.replace(/#/g, '').trim()
        }

        // 只有一级菜单（level === 0）且是目录类型时，如果没有 component，才使用 Layout
        // 二级及以下菜单如果是目录类型，不设置 component（留空）
        if (isDirectory && (!component || component === '')) {
          if (level === 0) {
            // 一级菜单使用 Layout
            component = RoutesAlias.Layout
          } else {
            // 二级及以下菜单不设置 component
            component = undefined
          }
        }

        const routeRecord: AppRouteRecord = {
          id: menu.menuId,
          name: menu.routeName || undefined,
          path: path,
          component: component || undefined,
          meta: {
            title: menu.menuName || '',
            icon: menu.icon,
            // isFrame: 0是外链/iframe, 1否
            isIframe: isIframe,
            // isCache: 0缓存, 1不缓存 -> keepAlive: true缓存, false不缓存
            keepAlive: menu.isCache === 0,
            // visible: 0显示, 1隐藏 -> isHide: true隐藏, false显示
            isHide: menu.visible === '1',
            // 如果是外链，设置 link
            link: isExternalLink ? path : undefined
          },
          children: menu.children && menu.children.length > 0
            ? this.convertSysMenuToRouteRecord(menu.children, level + 1)
            : undefined
        }

        return routeRecord
      } catch (error) {
        console.error(`[MenuProcessor] 转换菜单项 ${index} 失败:`, menu, error)
        return null
      }
    }).filter((item): item is AppRouteRecord => item !== null)
  }

  /**
   * 递归过滤空菜单项
   */
  private filterEmptyMenus(menuList: AppRouteRecord[]): AppRouteRecord[] {
    return menuList
      .map((item) => {
        // 如果有子菜单，先递归过滤子菜单
        if (item.children && item.children.length > 0) {
          const filteredChildren = this.filterEmptyMenus(item.children)
          // 如果过滤后还有子菜单，保留该菜单项
          if (filteredChildren.length > 0) {
            return {
              ...item,
              children: filteredChildren
            }
          }
          // 如果过滤后没有子菜单了，返回 null 标记为删除
          return null
        }
        return item
      })
      .filter((item): item is AppRouteRecord => {
        if (!item) return false

        // 如果有子菜单（即使过滤后为空数组），说明这是一个目录菜单，应该保留
        if ('children' in item && Array.isArray(item.children)) {
          // 如果有子菜单数组，保留（即使为空数组，因为可能是目录菜单）
          return true
        }

        // 如果有外链或 iframe，保留
        if (item.meta?.isIframe === true || item.meta?.link) {
          return true
        }

        // 如果有有效的 component，保留
        if (item.component && item.component !== '' && item.component !== RoutesAlias.Layout) {
          return true
        }

        // 如果是一级菜单且使用 Layout，保留
        if (item.component === RoutesAlias.Layout) {
          return true
        }

        // 其他情况过滤掉
        return false
      })
  }

  /**
   * 验证菜单列表是否有效
   */
  validateMenuList(menuList: AppRouteRecord[]): boolean {
    if (!Array.isArray(menuList)) {
      console.error('[MenuProcessor] 菜单列表不是数组类型:', menuList)
      return false
    }
    if (menuList.length === 0) {
      console.warn('[MenuProcessor] 菜单列表为空，用户可能没有菜单权限')
      return false
    }
    return true
  }

  /**
   * 规范化菜单路径
   * 将相对路径转换为完整路径，确保菜单跳转正确
   */
  private normalizeMenuPaths(menuList: AppRouteRecord[], parentPath = ''): AppRouteRecord[] {
    return menuList.map((item) => {
      // 清理父路径中的 # 符号
      const cleanParentPath = parentPath.replace(/#/g, '').trim()

      // 构建完整路径
      const fullPath = this.buildFullPath(item.path || '', cleanParentPath)

      // 递归处理子菜单
      const children = item.children?.length
        ? this.normalizeMenuPaths(item.children, fullPath)
        : item.children

      return {
        ...item,
        path: fullPath,
        children
      }
    })
  }

  /**
   * 验证菜单路径配置
   * 检测非一级菜单是否错误使用了 / 开头的路径
   */
  /**
   * 验证菜单路径配置
   * 检测非一级菜单是否错误使用了 / 开头的路径
   */
  private validateMenuPaths(menuList: AppRouteRecord[], level = 1): void {
    menuList.forEach((route) => {
      if (!route.children?.length) return

      const parentName = String(route.name || route.path || '未知路由')

      route.children.forEach((child) => {
        const childPath = child.path || ''

        // 跳过合法的绝对路径：外部链接和 iframe 路由
        if (this.isValidAbsolutePath(childPath)) return

        // 检测非法的绝对路径
        if (childPath.startsWith('/')) {
          this.logPathError(child, childPath, parentName, level)
        }
      })

      // 递归检查更深层级的子路由
      this.validateMenuPaths(route.children, level + 1)
    })
  }

  /**
   * 判断是否为合法的绝对路径
   */
  private isValidAbsolutePath(path: string): boolean {
    return (
      path.startsWith('http://') ||
      path.startsWith('https://') ||
      path.startsWith('/outside/iframe/')
    )
  }

  /**
   * 输出路径配置错误日志
   */
  private logPathError(
    route: AppRouteRecord,
    path: string,
    parentName: string,
    level: number
  ): void {
    const routeName = String(route.name || path || '未知路由')
    const menuTitle = route.meta?.title || routeName
    const suggestedPath = path.split('/').pop() || path.slice(1)

    console.error(
      `[路由配置错误] 菜单 "${formatMenuTitle(menuTitle)}" (name: ${routeName}, path: ${path}) 配置错误\n` +
        `  位置: ${parentName} > ${routeName}\n` +
        `  问题: ${level + 1}级菜单的 path 不能以 / 开头\n` +
        `  当前配置: path: '${path}'\n` +
        `  应该改为: path: '${suggestedPath}'`
    )
  }

  /**
   * 构建完整路径
   */
  private buildFullPath(path: string, parentPath: string): string {
    if (!path) return ''

    // 清理路径中的 # 符号和多余的空格
    path = path.replace(/#/g, '').trim()

    // 外部链接直接返回
    if (path.startsWith('http://') || path.startsWith('https://')) {
      return path
    }

    // 如果已经是绝对路径，直接返回（清理后）
    if (path.startsWith('/')) {
      // 清理路径中的重复斜杠
      return path.replace(/\/+/g, '/')
    }

    // 拼接父路径和当前路径
    if (parentPath) {
      // 清理父路径中的 # 符号和重复斜杠
      const cleanParent = parentPath.replace(/#/g, '').replace(/\/+/g, '/').replace(/\/$/, '')
      const cleanChild = path.replace(/^\//, '')
      
      // 检查子路径是否已经包含父路径的一部分，避免重复拼接
      // 例如：父路径是 /monitor，子路径是 monitor/druid，应该拼接成 /monitor/druid
      // 但如果子路径已经包含完整的父路径，则直接使用子路径
      const parentSegments = cleanParent.split('/').filter(Boolean)
      const childSegments = cleanChild.split('/').filter(Boolean)
      
      // 如果子路径的第一个段与父路径的最后一个段相同，跳过第一个段
      if (parentSegments.length > 0 && childSegments.length > 0) {
        const parentLastSegment = parentSegments[parentSegments.length - 1]
        const childFirstSegment = childSegments[0]
        
        if (parentLastSegment === childFirstSegment) {
          // 跳过重复的段
          childSegments.shift()
          const remainingPath = childSegments.join('/')
          const fullPath = `${cleanParent}/${remainingPath}`
          return fullPath.replace(/\/+/g, '/')
        }
      }
      
      const fullPath = `${cleanParent}/${cleanChild}`
      // 清理最终路径中的重复斜杠
      return fullPath.replace(/\/+/g, '/')
    }

    // 没有父路径，添加前导斜杠
    return `/${path}`
  }
}
