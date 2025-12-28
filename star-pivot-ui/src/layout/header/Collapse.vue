<script setup lang="ts">
import { computed } from 'vue'
import { Fold, Expand } from "@element-plus/icons-vue"
import { useMenuStore } from '@/store/menu'

const store = useMenuStore()

// 根据折叠状态显示不同的图标
const status = computed(() => {
  return !store.getCollapse
})

// 切换菜单折叠状态
const toggleCollapse = () => {
  store.toggleCollapse()
}
</script>

<template>
  <el-icon @click="toggleCollapse" style="cursor: pointer;" color="#FFF" size="30">
    <component :is="status ? Fold : Expand"></component>
  </el-icon>
</template>

<style scoped>

</style>