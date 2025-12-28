import { defineStore } from 'pinia';
import { ref } from 'vue';

/**
 * 全局加载状态管理
 */
export const useLoadingStore = defineStore('loading', () => {
  // 全局加载状态
  const globalLoading = ref(false);
  // 加载任务计数
  const loadingCount = ref(0);

  /**
   * 开始加载
   */
  function startLoading() {
    loadingCount.value++;
    globalLoading.value = true;
  }

  /**
   * 结束加载
   */
  function stopLoading() {
    loadingCount.value = Math.max(0, loadingCount.value - 1);
    if (loadingCount.value === 0) {
      globalLoading.value = false;
    }
  }

  /**
   * 重置加载状态
   */
  function resetLoading() {
    loadingCount.value = 0;
    globalLoading.value = false;
  }

  return {
    globalLoading,
    loadingCount,
    startLoading,
    stopLoading,
    resetLoading
  };
});

