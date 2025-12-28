<script setup lang="ts">
import { computed, onErrorCaptured } from 'vue'
import { useRoute } from 'vue-router'
import ErrorBoundary from '@/components/ErrorBoundary.vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const hideLayout = computed(() => route.meta?.hideLayout)

// 全局错误处理
onErrorCaptured((err: Error, instance, info) => {
  if (import.meta.env.DEV) {
    console.error('全局错误捕获:', err, info)
  }
  ElMessage.error('应用出现错误，请刷新页面')
  return false
})
</script>

<template>
  <ErrorBoundary>
    <div class="app-container">
      <div v-if="hideLayout" class="full-view">
        <router-view />
      </div>
      <div v-else class="layout-view">
        <router-view />
      </div>
    </div>
  </ErrorBoundary>
</template>

<style scoped>
.app-container {
  width: 100%;
  height: 100%;
}

.full-view {
  width: 100vw;
  height: 100vh;
}

.layout-view {
  width: 100%;
  height: 100%;
}
</style>