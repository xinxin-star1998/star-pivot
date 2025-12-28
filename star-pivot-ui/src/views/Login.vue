<template>
  <div class="login-container">
    <el-card class="login-form" :body-style="{ padding: '30px' }">
      <div class="login-header">
        <h2>用户登录</h2>
      </div>
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" label-width="0">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            prefix-icon="User"
            placeholder="请输入用户名"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            prefix-icon="Lock"
            placeholder="请输入密码"
            type="password"
            size="large"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            @click="handleLogin"
            :loading="loading"
            style="width: 100%"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';
import { ElMessage } from 'element-plus';

interface LoginFormData {
  username: string;
  password: string;
}

const router = useRouter();
const userStore = useUserStore();

const loginFormRef = ref();

const loginForm = reactive<LoginFormData>({
  username: '',
  password: ''
});

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符', trigger: 'blur' }
  ]
};

const loading = ref(false);

const handleLogin = async () => {
  try {
    await loginFormRef.value.validate();
    loading.value = true;
    
    await userStore.login(loginForm);
    ElMessage.success('登录成功');
    
    // 跳转到首页
    router.push('/dashboard');
  } catch (error: any) {
    // 错误信息已由request拦截器处理，这里只处理特殊逻辑
    if (import.meta.env.DEV) {
      console.error('登录失败:', error);
    }
    ElMessage.error(error.message || '登录失败');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-form {
  width: 400px;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 20px;
}

.login-header h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
  font-weight: 500;
}
</style>