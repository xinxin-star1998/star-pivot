<template>
  <el-breadcrumb separator="/" class="breadcrumb">
    <el-breadcrumb-item 
      v-for="(item, index) in breadcrumbs" 
      :key="index"
    >
      {{ item.meta?.title || item.name }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const breadcrumbs = ref<any[]>([]);

// 生成面包屑
const getBreadcrumbs = () => {
  const matched = route.matched.filter(item => item.meta && item.meta.title);
  breadcrumbs.value = matched;
};

// 监听路由变化
watch(
  () => route.path,
  () => {
    getBreadcrumbs();
  },
  { immediate: true }
);

// 初始化面包屑
getBreadcrumbs();
</script>

<style scoped>
.breadcrumb {
  margin-left: 20px;
}
</style>