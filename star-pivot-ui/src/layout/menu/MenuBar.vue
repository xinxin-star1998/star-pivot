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
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import {useMenuStore} from '@/store/menu/index.ts'
import MenuItem from '@/layout/menu/MenuItem.vue';
import MenuLogo from "@/layout/menu/MenuLogo.vue";

// 菜单数据 - 这里可以替换为从后端获取的动态菜单
const menuData = ref([
  {
    path: '/dashboard',
    name: 'dashboard',
    meta: { title: '首页', icon: 'House' },
  },
  {
    path: "/system",
    component: "Layout",
    name: "system",
    meta: {
      title: "系统管理",
      icon: "Setting",
      roles: ["sys:manage"],
    },
    children: [
      {
        path: "/userList",
        component: "/system/User/UserList",
        name: "userList",
        meta: {
          title: "用户管理",
          icon: "UserFilled",
          roles: ["sys:user"],
        },
      },
      {
        path: "/roleList",
        component: "/system/Role/RoleList",
        name: "roleList",
        meta: {
          title: "角色管理",
          icon: "Wallet",
          roles: ["sys:role"],
        },
      },
      {
        path: "/menuList",
        component: "/system/Menu/MenuList",
        name: "menuList",
        meta: {
          title: "菜单管理",
          icon: "Menu",
          roles: ["sys:menu"],
        },
      },
    ],
  },
  {
    path: "/goodsRoot",
    component: "Layout",
    name: "goodsRoot",
    meta: {
      title: "商品管理",
      icon: "Setting",
      roles: ["sys:goodsRoot"],
    },
    children: [
      {
        path: "/category",
        component: "/goods/Category",
        name: "category",
        meta: {
          title: "商品类型",
          icon: "UserFilled",
          roles: ["sys:category"],
        },
      },
      {
        path: "/goodsList",
        component: "/goods/GoodsList",
        name: "goodsList",
        meta: {
          title: "商品信息",
          icon: "Wallet",
          roles: ["sys:goodsList"],
        },
      }
    ]
  }
]);

const route = useRoute();
const menuStore = useMenuStore();

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
  console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
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