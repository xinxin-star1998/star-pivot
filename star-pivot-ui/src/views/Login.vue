<template>
  <div class="login-container">
    <div class="login-left">
      <div class="login-left-content">
        <img src="@/assets/image/logo2.png" alt="星枢系统Logo" class="logo-image"/>
        <h1>欢迎使用星枢后台权限系统</h1>
        <p>高效、安全、便捷的后台管理系统</p>
      </div>
    </div>
    <div class="login-right">
      <div class="login-form">
        <h2>用户登录</h2>
        <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" label-width="0px">
          <el-form-item prop="username">
            <el-input 
              v-model="loginForm.username" 
              placeholder="请输入用户名" 
              prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input 
              v-model="loginForm.password" 
              type="password" 
              placeholder="请输入密码" 
              prefix-icon="Lock"
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-checkbox v-model="rememberMe">记住密码</el-checkbox>
          </el-form-item>
          <el-form-item>
            <el-button 
              type="primary" 
              @click="handleLogin" 
              :loading="loading" 
              class="login-button"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
const store = useUserStore()
interface LoginForm {
  username: string
  password: string
}

const router = useRouter()
const loading = ref(false)
const rememberMe = ref(false)
const loginFormRef = ref()

const loginForm = reactive<LoginForm>({
  username: 'admin',
  password: '123456'
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  try {
    await loginFormRef.value?.validate()
    loading.value = true
    
    // 模拟登录API调用
    try {
      // 这里应该替换为真实的登录API调用
      // const response = await loginApi(loginForm)
      
      // 模拟API响应
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      // 假设登录成功，获取token和用户信息
      const token = 'mock-token-' + Date.now()
      
      // 存储token到localStorage或sessionStorage
      if (rememberMe.value) {
        localStorage.setItem('token', token)
      } else {
        sessionStorage.setItem('token', token)
      }
      
      // 更新用户信息
      store.setUsername(loginForm.username)
      
      ElMessage.success('登录成功')
      
      // 登录成功后跳转到首页
      router.push('/dashboard')
    } catch (error) {
      ElMessage.error('登录失败，请检查用户名和密码')
      console.error('登录错误:', error)
    } finally {
      loading.value = false
    }
  } catch (error) {
    console.log('验证失败', error)
    ElMessage.error('请检查输入信息')
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  height: 100vh;
  width: 100%;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  text-align: center;
}

.login-left-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-image {
  width: 80px;
  height: 80px;
  margin-bottom: 20px;
}

.login-left-content h1 {
  font-size: 28px;
  margin-bottom: 15px;
  font-weight: 600;
}

.login-left-content p {
  font-size: 16px;
  opacity: 0.9;
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  padding: 0 40px;
}

.login-form {
  width: 100%;
  max-width: 400px;
}

.login-form h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
}

.login-button {
  width: 100%;
  height: 45px;
  font-size: 16px;
}

:deep(.el-input__wrapper) {
  padding: 0 15px;
}
</style>