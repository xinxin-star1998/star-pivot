<template>
  <div class="collapse-container">
    <el-icon @click="toggleCollapse" class="collapse-icon">
      <Fold v-if="!isCollapse" />
      <Expand v-else />
    </el-icon>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { Fold, Expand } from '@element-plus/icons-vue';
import { useMenuStore } from '@/store/menu/index.ts';

const menuStore = useMenuStore();

// 根据折叠状态显示不同的图标
const isCollapse = computed(() => {
  return menuStore.collapse;
});

// 切换菜单折叠状态
const toggleCollapse = () => {
  menuStore.toggleCollapse();
};
</script>

<style scoped>
.collapse-container {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 10px;
  height: 100%;
  transition: all 0.3s;
}

.collapse-container:hover {
  background-color: rgba(64, 158, 255, 0.1);
}

.collapse-icon {
  font-size: 20px;
  color: #606266;
  transition: color 0.3s;
}

.collapse-icon:hover {
  color: #409eff;
}
</style>