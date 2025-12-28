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
import { useUserStore } from '@/store/user';

const userStore = useUserStore();

// 根据折叠状态显示不同的图标
const isCollapse = computed(() => {
  return userStore.isMenuCollapse;
});

// 切换菜单折叠状态
const toggleCollapse = () => {
  userStore.toggleMenuCollapse();
};
</script>

<style scoped>
.collapse-container {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.collapse-icon {
  font-size: 18px;
  color: #606266;
}

.collapse-icon:hover {
  color: #409eff;
}
</style>