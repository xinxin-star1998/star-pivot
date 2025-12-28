<template>
  <template v-for="item in menuData" :key="item.name || item.path">
    <!-- 有子菜单的菜单项 -->
    <el-sub-menu v-if="item.children && item.children.length > 0" :index="item.path">
      <template #title>
        <el-icon v-if="item.meta?.icon">
          <component :is="getIconComponent(item.meta.icon)" />
        </el-icon>
        <span>{{ item.meta?.title }}</span>
      </template>
      <MenuItem :menu-data="item.children" />
    </el-sub-menu>
    <!-- 没有子菜单的菜单项 -->
    <el-menu-item v-else :index="item.path" @click="handleMenuClick(item)">
      <el-icon v-if="item.meta?.icon">
        <component :is="getIconComponent(item.meta.icon)" />
      </el-icon>
      <template #title>
        {{ item.meta?.title }}
      </template>
    </el-menu-item>
  </template>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';

interface MenuItem {
  path: string;
  name?: string;
  meta?: {
    title?: string;
    icon?: string;
  };
  children?: MenuItem[];
}

interface Props {
  menuData: MenuItem[];
}

defineProps<Props>();

const router = useRouter();

/**
 * 获取图标组件
 * 支持Element Plus图标名称或组件引用
 */
const getIconComponent = (iconName?: string) => {
  if (!iconName) return null;
  
  // 如果是Element Plus图标名称，直接返回
  if (iconName in ElementPlusIconsVue) {
    return ElementPlusIconsVue[iconName as keyof typeof ElementPlusIconsVue];
  }
  
  // 默认返回Menu图标
  return ElementPlusIconsVue.Menu;
};

const handleMenuClick = (item: MenuItem) => {
  if (item.path) {
    router.push(item.path);
  }
};
</script>