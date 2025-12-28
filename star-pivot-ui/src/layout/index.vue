<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="asideWidth" class="aside-container" :class="{ 'is-collapse': isCollapse }">
      <MenuBar />
    </el-aside>

    <el-container>
      <!-- 头部 -->
      <el-header class="header-container">
        <Header></Header>
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
import { onMounted, computed } from 'vue';
import { useUserStore } from '@/store/user';
import { useMenuStore } from '@/store/menu';
import Header from "@/layout/header/Header.vue";

const userStore = useUserStore();
const menuStore = useMenuStore();

// 根据折叠状态计算侧边栏宽度
const isCollapse = computed(() => menuStore.collapse);
const asideWidth = computed(() => isCollapse.value ? '64px' : '250px');

// 组件挂载时获取用户信息和菜单
onMounted(async () => {
  try {
    // 如果有token，获取用户信息和菜单
    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    if (token) {
      await userStore.getUserInfo();
      // 加载菜单数据
      await menuStore.loadMenus();
    }
  } catch (error) {
    if (import.meta.env.DEV) {
      console.error('获取用户信息失败:', error);
    }
    // 如果获取用户信息失败，可能是token过期，跳转到登录页
    localStorage.removeItem('token');
    sessionStorage.removeItem('token');
  }
});
</script>

<style scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;
}

.aside-container {
  background-color: #304156;
  color: #fff;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  transition: width 0.3s ease;
  overflow: hidden;
  position: relative;
}

.aside-container.is-collapse {
  width: 64px !important;
}

.header-container {
  padding: 0;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  height: 60px;
  display: flex;
  align-items: center;
}

.main-container {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
  height: calc(100vh - 60px);
}
</style>