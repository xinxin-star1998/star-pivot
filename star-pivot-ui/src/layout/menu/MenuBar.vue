<template>
  <el-scrollbar class="scrollbar-container">
    <el-menu
      :default-active="activeMenu"
      :collapse="isCollapse"
      :unique-opened="true"
      :collapse-transition="false"
      class="menu-container"
      router
    >
      <MenuItem :menu-data="menuData" />
    </el-menu>
  </el-scrollbar>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useUserStore } from '@/store/user';
import MenuItem from './MenuItem.vue';

// 菜单数据 - 这里可以替换为从后端获取的动态菜单
const menuData = ref([
  {
    path: '/dashboard',
    name: 'dashboard',
    meta: { title: '首页', icon: 'House' },
  },
  {
    path: '/user',
    name: 'user',
    meta: { title: '用户管理', icon: 'User' },
    children: [
      {
        path: '/user/list',
        name: 'userList',
        meta: { title: '用户列表', icon: 'List' },
      }
    ]
  }
]);

const route = useRoute();
const userStore = useUserStore();

// 监听路由变化，设置当前激活的菜单项
const activeMenu = computed(() => {
  const { path } = route;
  return path;
});

// 获取折叠状态
const isCollapse = computed(() => {
  return userStore.isMenuCollapse;
});
</script>

<style scoped>
.scrollbar-container {
  height: calc(100vh - 60px);
}

.menu-container {
  border: none;
  height: 100%;
}

:deep(.el-menu-item.is-active) {
  background-color: #4a5056;
  color: #fff;
}

:deep(.el-sub-menu__title:hover) {
  background-color: #4a5056 !important;
  color: #fff !important;
}

:deep(.el-menu-item:hover) {
  background-color: #4a5056 !important;
  color: #fff !important;
}
</style>