<template>
  <div v-if="hasError" class="error-boundary">
    <el-result
      icon="error"
      title="页面出现错误"
      sub-title="抱歉，页面加载时出现了问题，请刷新页面重试"
    >
      <template #extra>
        <el-button type="primary" @click="handleReset">重新加载</el-button>
        <el-button @click="handleGoHome">返回首页</el-button>
      </template>
    </el-result>
  </div>
  <slot v-else />
</template>

<script setup lang="ts">
import { ref, onErrorCaptured } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const router = useRouter();
const hasError = ref(false);
const errorInfo = ref<Error | null>(null);

// 捕获子组件错误
onErrorCaptured((err: Error, instance, info) => {
  hasError.value = true;
  errorInfo.value = err;
  
  if (import.meta.env.DEV) {
    console.error('组件错误:', err);
    console.error('错误信息:', info);
  } else {
    // 生产环境可以上报错误到监控系统
    // reportError(err, info);
  }
  
  ElMessage.error('页面加载失败，请刷新重试');
  
  // 返回false阻止错误继续传播
  return false;
});

const handleReset = () => {
  hasError.value = false;
  errorInfo.value = null;
  window.location.reload();
};

const handleGoHome = () => {
  router.push('/dashboard');
  hasError.value = false;
  errorInfo.value = null;
};
</script>

<style scoped>
.error-boundary {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}
</style>

