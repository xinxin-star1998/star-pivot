/**
 * 路由转换器
 *
 * 负责将菜单数据转换为 Vue Router 路由配置
 *
 * @module router/core/RouteTransformer
 * @author Art Design Pro Team
 */

import type { RouteRecordRaw } from 'vue-router'
import type { AppRouteRecord } from '@/types/router'
import { ComponentLoader } from './ComponentLoader'
import { IframeRouteManager } from './IframeRouteManager'
import { RoutesAlias } from '../routesAlias'

interface ConvertedRoute extends Omit<RouteRecordRaw, 'children'> {
  id?: number
  children?: ConvertedRoute[]
  component?: RouteRecordRaw['component'] | (() => Promise<any>)
}

export class RouteTransformer {
  private componentLoader: ComponentLoader
  private iframeManager: IframeRouteManager

  constructor(componentLoader: ComponentLoader) {
    this.componentLoader = componentLoader
    this.iframeManager = IframeRouteManager.getInstance()
  }

  /**
   * 转换路由配置
   */
  transform(route: AppRouteRecord, depth = 0): ConvertedRoute {
    const { component, children, ...routeConfig } = route

    // 基础路由配置，确保 meta 对象存在
    const converted: ConvertedRoute = {
      ...routeConfig,
      component: undefined,
      meta: {
        ...route.meta,
        // 确保 isLayout 标记被正确传递
        isLayout: route.meta?.isLayout || false
      }
    }

    // 对于嵌套路由（depth > 0），需要将路径转换为相对路径
    // Vue Router 要求嵌套路由的路径必须是相对于父路由的
    if (depth > 0 && route.path) {
      // 提取路径的最后一段作为相对路径
      const pathSegments = route.path.split('/').filter(Boolean)
      if (pathSegments.length > 0) {
        // 获取路径的最后一段（相对于父路由）
        const relativePath = pathSegments[pathSegments.length - 1]
        converted.path = relativePath
      }
    }

    // 处理不同类型的路由
    if (route.meta.isIframe) {
      this.handleIframeRoute(converted, route, depth)
    } else if (this.isFirstLevelRoute(route, depth)) {
      this.handleFirstLevelRoute(converted, route, component as string)
    } else {
      // 对于嵌套路由，支持多级路由
      // 目录类型（M）如果有子菜单，可以使用 Layout 来承载子路由
      // 菜单类型（C）不应该使用 Layout，必须使用自己的 component
      if (component === RoutesAlias.Layout) {
        // 检查是否是菜单类型（C），菜单类型不应该使用 Layout
        if (route.menuType === 'C') {
          console.warn(
            `[RouteTransformer] 检测到菜单类型（C）错误使用了 Layout 组件: ${route.path || route.name}`,
            '菜单类型不应该使用 Layout，必须使用具体的组件路径，将忽略该 component 设置。'
          )
          // 不设置 component，让路由为空
          this.handleNormalRoute(converted, undefined, depth)
        } else if (children && children.length > 0) {
          // 目录类型且有子菜单，允许使用 Layout（支持多级路由）
          console.log(
            `[RouteTransformer] 目录 "${route.meta?.title || route.path}" (depth: ${depth}) 有子菜单，使用 Layout`
          )
          // 标记这是 Layout 组件
          converted.meta.isLayout = true
          this.handleNormalRoute(converted, component as string, depth)
        } else {
          // 目录类型但没有子菜单，不应该使用 Layout
          console.warn(
            `[RouteTransformer] 检测到目录类型但没有子菜单的路由使用了 Layout 组件: ${route.path || route.name}`,
            '目录类型如果没有子菜单，不应该使用 Layout，将忽略该 component 设置。'
          )
          this.handleNormalRoute(converted, undefined, depth)
        }
      } else {
        // 不是 Layout，正常处理
        this.handleNormalRoute(converted, component as string, depth)
      }
    }

    // 递归处理子路由
    if (children?.length) {
      converted.children = children.map((child) => this.transform(child, depth + 1))
    }

    return converted
  }

  /**
   * 判断是否为一级路由（需要 Layout 包裹）
   */
  private isFirstLevelRoute(route: AppRouteRecord, depth: number): boolean {
    return depth === 0 && (!route.children || route.children.length === 0)
  }

  /**
   * 处理 iframe 类型路由
   */
  private handleIframeRoute(
    targetRoute: ConvertedRoute,
    sourceRoute: AppRouteRecord,
    depth: number
  ): void {
    if (depth === 0) {
      // 顶级 iframe：用 Layout 包裹
      targetRoute.component = this.componentLoader.loadLayout()
      targetRoute.path = this.extractFirstSegment(sourceRoute.path || '')
      targetRoute.name = ''

      targetRoute.children = [
        {
          ...sourceRoute,
          component: this.componentLoader.loadIframe()
        } as ConvertedRoute
      ]
    } else {
      // 非顶级（嵌套）iframe：直接使用 Iframe.vue
      targetRoute.component = this.componentLoader.loadIframe()
    }

    // 记录 iframe 路由
    this.iframeManager.add(sourceRoute)
  }

  /**
   * 处理一级菜单路由
   */
  private handleFirstLevelRoute(
    converted: ConvertedRoute,
    route: AppRouteRecord,
    component: string | undefined
  ): void {
    converted.component = this.componentLoader.loadLayout()
    converted.path = this.extractFirstSegment(route.path || '')
    converted.name = ''
    route.meta.isFirstLevel = true

    converted.children = [
      {
        ...route,
        component: component ? this.componentLoader.load(component) : undefined
      } as ConvertedRoute
    ]
  }

  /**
   * 处理普通路由
   * @param converted 转换后的路由对象
   * @param component 组件路径
   * @param depth 路由深度（0 表示一级路由）
   */
  private handleNormalRoute(converted: ConvertedRoute, component: string | undefined, depth: number): void {
    if (component) {
      // 如果 component 是 Layout，根据深度选择不同的 Layout
      if (component === RoutesAlias.Layout) {
        if (depth === 0) {
          // 一级路由使用完整的 Layout（包含侧边栏、头部等）
          converted.component = this.componentLoader.loadLayout()
        } else {
          // 嵌套路由使用简单的 Layout（只包含 RouterView）
          converted.component = this.componentLoader.loadNestedLayout()
        }
      } else {
        converted.component = this.componentLoader.load(component)
      }
    }
  }

  /**
   * 提取路径的第一段
   */
  private extractFirstSegment(path: string): string {
    const segments = path.split('/').filter(Boolean)
    return segments.length > 0 ? `/${segments[0]}` : '/'
  }
}
