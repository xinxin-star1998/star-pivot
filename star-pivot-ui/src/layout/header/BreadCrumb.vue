<template>
  <el-breadcrumb separator="/" class="breadcrumb">
    <el-breadcrumb-item 
      v-for="(item, index) in breadcrumbs" 
      :key="index"
      :to="index === breadcrumbs.length - 1 ? undefined : { path: item.path }"
    >
      {{ item.meta?.title || item.name }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

interface BreadcrumbItem {
  path: string
  name?: string
  meta?: {
    title?: string
  }
}

const route = useRoute()
const breadcrumbs = ref<BreadcrumbItem[]>([])

const getBreadcrumbs = () => {
  const matched = route.matched.filter(item => item.meta && item.meta.title)
  breadcrumbs.value = matched.map(item => ({
    path: item.path,
    name: item.name as string,
    meta: item.meta
  }))
}

// 初始化面包屑
getBreadcrumbs()

// 监听路由变化，更新面包屑
watch(() => route.path, () => {
  getBreadcrumbs()
})
</script>

<style scoped>
.breadcrumb {
  margin-left: 20px;
}
</style>