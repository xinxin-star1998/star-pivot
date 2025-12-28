import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const username = ref('')

  const getUsername = computed(() => username.value)

  function setUsername(newUsername: string): void {
    username.value = newUsername
  }

  /**
   * 清除用户信息（包括持久化数据）
   */
  function clearUser(): void {
    username.value = ''
    // 清除持久化存储
    localStorage.removeItem('user-store')
  }

  return {
    username,
    getUsername,
    setUsername,
    clearUser
  }
}, {
  persist: {
    key: 'user-store',
    storage: localStorage,
    // @ts-ignore - pinia-plugin-persistedstate v4 类型定义问题
    paths: ['username']
  }
})
