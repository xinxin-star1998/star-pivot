import request from '@/http/request.ts';
import type { Result } from '@/http/request';

// 后端菜单数据结构
export interface BackendMenu {
  menuId: number;
  menuName: string;
  parentId: number | null;
  orderNum: number;
  path: string;
  component?: string;
  query?: string;
  routeName?: string;
  isFrame?: number;
  isCache?: number;
  menuType: string; // M目录 C菜单 F按钮
  visible: string; // 0显示 1隐藏
  status: string; // 0正常 1停用
  perms?: string;
  icon?: string;
  children?: BackendMenu[];
}

// 前端菜单数据结构
export interface FrontendMenu {
  path: string;
  name?: string;
  component?: string;
  meta: {
    title: string;
    icon?: string;
    roles?: string[];
    hideLayout?: boolean;
  };
  children?: FrontendMenu[];
}

/**
 * 将后端菜单数据转换为前端菜单格式
 * 后端返回的已经是树形结构，直接递归转换即可
 * 后端已经按orderNum排序，保持原有顺序
 */
export function transformMenu(backendMenus: BackendMenu[]): FrontendMenu[] {
  if (!backendMenus || backendMenus.length === 0) {
    return [];
  }
  
  return backendMenus
    .filter(menu => {
      // 过滤掉按钮类型的菜单（F类型），只保留目录和菜单
      // 同时过滤掉隐藏的菜单（visible='1'）和停用的菜单（status='1'）
      return menu.menuType !== 'F' 
        && menu.visible !== '1' 
        && menu.status !== '1';
    })
    .map(menu => {
      const frontendMenu: FrontendMenu = {
        path: menu.path || `/menu-${menu.menuId}`,
        name: menu.routeName || menu.menuName,
        component: menu.component,
        meta: {
          title: menu.menuName,
          icon: menu.icon,
          roles: menu.perms ? [menu.perms] : undefined,
        },
        // 递归转换子菜单
        children: menu.children && menu.children.length > 0 
          ? transformMenu(menu.children) 
          : undefined
      };
      return frontendMenu;
    });
}

// 菜单相关API
const menuApi = {
  /**
   * 获取用户菜单树
   * 调用后端菜单树接口获取树形结构的菜单数据
   */
  getUserMenus: async (): Promise<BackendMenu[]> => {
    try {
      const response = await request.get<Result<BackendMenu[]>>('/sys/menu/menuTree');
      if (response.code === 200 && response.data) {
        // 后端返回的data就是菜单树数组
        return response.data as BackendMenu[];
      }
      return [];
    } catch (error) {
      if (import.meta.env.DEV) {
        console.error('获取菜单树失败:', error);
      }
      return [];
    }
  }
};

export default menuApi;

