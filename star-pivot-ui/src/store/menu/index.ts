import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useMenuStore = defineStore('menuStore', () => {
  // 菜单折叠状态
  const collapse = ref(false)

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
   * 重置菜单状态（清除持久化数据）
   */
  function resetMenu(): void {
    collapse.value = false
    localStorage.removeItem('menu-store')
  }

  return {
    collapse,
    getCollapse,
    setCollapse,
    toggleCollapse,
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