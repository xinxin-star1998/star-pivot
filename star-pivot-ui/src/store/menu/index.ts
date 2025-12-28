import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import menuApi, { transformMenu, type FrontendMenu, type BackendMenu } from '@/http/api/menu/menu'
import { registerRoutes } from '@/utils/route'

export const useMenuStore = defineStore('menuStore', () => {
  // 菜单折叠状态
  const collapse = ref(false)
  // 菜单数据
  const menuData = ref<FrontendMenu[]>([])
  // 菜单加载状态
  const loading = ref(false)

  // 获取菜单折叠状态
  const getCollapse = computed(() => collapse.value)

  /**
   * 设置菜单折叠状态
   * @param newValue 新的折叠状态
   */
  function setCollapse(newValue: boolean): void {
    collapse.value = newValue
  }

  /**
   * 切换菜单折叠状态
   */
  function toggleCollapse(): void {
    collapse.value = !collapse.value
  }

  /**
   * 加载菜单数据
   */
  async function loadMenus(): Promise<void> {
    try {
      loading.value = true
      const backendMenus = await menuApi.getUserMenus()
      const transformedMenus = transformMenu(backendMenus)
      menuData.value = transformedMenus
      
      // 动态注册路由
      registerRoutes(transformedMenus)
    } catch (error) {
      if (import.meta.env.DEV) {
        console.error('加载菜单失败:', error)
      }
      // 加载失败时使用空数组，避免页面崩溃
      menuData.value = []
    } finally {
      loading.value = false
    }
  }

  /**
   * 重置菜单状态（清除持久化数据）
   */
  function resetMenu(): void {
    collapse.value = false
    menuData.value = []
    localStorage.removeItem('menu-store')
    // 移除动态路由
    import('@/utils/route').then(({ removeDynamicRoutes }) => {
      removeDynamicRoutes()
    })
  }

  return {
    collapse,
    menuData,
    loading,
    getCollapse,
    setCollapse,
    toggleCollapse,
    loadMenus,
    resetMenu
  }
}, {
  persist: {
    key: 'menu-store',
    storage: localStorage,
    // 在 setup store 中，只持久化 collapse 字段
    // 注意：paths 对应的是 return 对象中的属性名，不是 ref 的变量名
    // @ts-ignore - pinia-plugin-persistedstate v4 类型定义问题
    paths: ['collapse']
  }
})