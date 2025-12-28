<script setup lang="ts">
import { useUserStore } from '@/store/user'
import { useMenuStore } from '@/store/menu'

const userStore = useUserStore()
const menuStore = useMenuStore()
</script>

<template>
  <div class="dashboard-container">
    <h1>欢迎，{{ userStore.getUsername }}！</h1>
    <p>这是您的仪表盘页面</p>
    <div class="info-card">
      <h3>系统信息</h3>
      <p>菜单折叠状态: {{ menuStore.getCollapse ? '已折叠' : '未折叠' }}</p>
    </div>
  </div>
</template>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.info-card {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 4px;
}
</style>