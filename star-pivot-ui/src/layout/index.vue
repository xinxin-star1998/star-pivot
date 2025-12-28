<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="200px" class="aside-container">
      <div class="logo">
        <h3>StarPivot</h3>
      </div>
      <MenuBar />
    </el-aside>

    <el-container>
      <!-- 头部 -->
      <el-header class="header-container">
        <div class="header-content">
          <Collapse />
          <BreadCrumb />
          <LoginOut />
        </div>
      </el-header>

      <!-- 主要内容 -->
      <el-main class="main-container">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import MenuBar from './menu/MenuBar.vue'
import Collapse from './header/Collapse.vue'
import BreadCrumb from './header/BreadCrumb.vue'
import LoginOut from './header/LoginOut.vue'
import { onMounted } from 'vue';
import { useUserStore } from '@/store/user';

const userStore = useUserStore();

// 组件挂载时获取用户信息
onMounted(async () => {
  try {
    // 如果有token，获取用户信息
    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    if (token) {
      await userStore.getUserInfo();
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
    // 如果获取用户信息失败，可能是token过期，跳转到登录页
    localStorage.removeItem('token');
    sessionStorage.removeItem('token');
  }
});
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside-container {
  background-color: #545c64;
  color: #fff;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #4a5056;
  border-bottom: 1px solid #444a51;
}

.logo h3 {
  color: #fff;
  margin: 0;
  font-weight: 600;
  font-size: 18px;
}

.header-container {
  padding: 0;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 100%;
}

.main-container {
  background-color: #f0f2f5;
  padding: 20px;
  min-height: calc(100vh - 120px);
}
</style>