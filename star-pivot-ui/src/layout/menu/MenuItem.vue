<template>
  <template v-for="item in menuData" :key="item.name">
    <!-- 有子菜单的菜单项 -->
    <el-sub-menu v-if="item.children && item.children.length > 0" :index="item.path">
      <template #title>
        <el-icon v-if="item.meta?.icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <span>{{ item.meta?.title }}</span>
      </template>
      <MenuItem :menu-data="item.children" />
    </el-sub-menu>
    <!-- 没有子菜单的菜单项 -->
    <el-menu-item v-else :index="item.path" @click="handleMenuClick(item)">
      <el-icon v-if="item.meta?.icon">
        <component :is="item.meta.icon" />
      </el-icon>
      <template #title>
        {{ item.meta?.title }}
      </template>
    </el-menu-item>
  </template>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';

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

const handleMenuClick = (item: MenuItem) => {
  if (item.path) {
    router.push(item.path);
  }
};
</script>