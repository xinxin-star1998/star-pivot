<template>
  <MenuLogo></MenuLogo>
  <el-menu
      :default-active="activeMenu"
      class="el-menu-vertical-demo"
      :collapse="isCollapse"
      @open="handleOpen"
      @close="handleClose"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409eff"
      unique-opened
      router
  >
    <MenuItem :menuData="menuData"></MenuItem>
  </el-menu>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useMenuStore } from '@/store/menu/index.ts'
import MenuItem from '@/layout/menu/MenuItem.vue';
import MenuLogo from "@/layout/menu/MenuLogo.vue";

const route = useRoute();
const menuStore = useMenuStore();

// 从store获取菜单数据
const menuData = computed(() => {
  // 如果菜单数据为空，返回默认首页菜单（作为fallback）
  if (!menuStore.menuData || menuStore.menuData.length === 0) {
    return [{
      path: '/dashboard',
      name: 'dashboard',
      meta: { title: '首页', icon: 'House' },
    }];
  }
  return menuStore.menuData;
});

// 组件挂载时加载菜单
onMounted(async () => {
  // 如果菜单数据为空，则加载菜单
  if (!menuStore.menuData || menuStore.menuData.length === 0) {
    await menuStore.loadMenus();
  }
});

// 监听路由变化，设置当前激活的菜单项
const activeMenu = computed(() => {
  const { path } = route;
  return path;
});

// 获取折叠状态
const isCollapse = computed(() => {
  return menuStore.collapse;
});
const handleOpen = (key: string, keyPath: string[]) => {
  // 菜单展开事件处理（可根据需要添加逻辑）
  if (import.meta.env.DEV) {
    console.log('菜单展开:', key, keyPath);
  }
}
const handleClose = (key: string, keyPath: string[]) => {
  // 菜单收起事件处理（可根据需要添加逻辑）
  if (import.meta.env.DEV) {
    console.log('菜单收起:', key, keyPath);
  }
}
</script>

<style scoped>
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 250px;
  min-height: calc(100vh - 60px);
}

.el-menu {
  border-right: none;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

.el-menu--collapse {
  width: 64px;
}

/* 滚动条样式 */
.el-menu::-webkit-scrollbar {
  width: 6px;
}

.el-menu::-webkit-scrollbar-thumb {
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

.el-menu::-webkit-scrollbar-thumb:hover {
  background-color: rgba(255, 255, 255, 0.3);
}

:deep(.el-sub-menu .el-sub-menu__title) {
  color: #bfcbd9 !important;
}

:deep(.el-sub-menu .el-sub-menu__title:hover) {
  background-color: #001528 !important;
}

:deep(.el-menu .el-menu-item) {
  color: #bfcbd9;
}

/* 菜单点中文字的颜色 */
:deep(.el-menu-item.is-active) {
  color: #409eff !important;
  background-color: #001528 !important;
}

/* 当前打开菜单的所有子菜单颜色 */
:deep(.is-opened .el-sub-menu__title) {
  background-color: #1f2d3d !important;
}

:deep(.is-opened .el-menu-item) {
  background-color: #1f2d3d !important;
}

/* 鼠标移动菜单的颜色 */
:deep(.el-menu-item:hover) {
  background-color: #001528 !important;
}

/* 折叠状态下的样式 */
:deep(.el-menu--collapse .el-sub-menu__title) {
  padding: 0 20px !important;
}

:deep(.el-menu--collapse .el-menu-item) {
  padding: 0 20px !important;
}
</style>