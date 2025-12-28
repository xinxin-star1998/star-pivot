<template>
  <div class="login-out">
    <el-dropdown>
      <span class="el-dropdown-link">
        <el-avatar :size="30" :src="userAvatar" />
        <span style="margin-left: 8px;">{{ userInfo?.nickName || userInfo?.userName || '用户' }}</span>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/store/user';
import { useRouter } from 'vue-router';
import { computed } from 'vue';
import { SwitchButton } from '@element-plus/icons-vue';

const userStore = useUserStore();
const router = useRouter();

// 获取用户信息
const userInfo = computed(() => userStore.userInfo);

// 获取用户头像
const userAvatar = computed(() => {
  if (userStore.userInfo?.avatar) {
    return userStore.userInfo.avatar;
  }
  return 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'; // 默认头像
});

// 处理登出
const handleLogout = async () => {
  try {
    await userStore.logout();
    // 跳转到登录页
    router.push('/login');
  } catch (error) {
    if (import.meta.env.DEV) {
      console.error('登出失败:', error);
    }
  }
};
</script>

<style scoped>
.login-out {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
}
</style>