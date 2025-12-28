<template>
  <el-dropdown placement="bottom-start">
    <span class="el-dropdown-link">
      <img class="user" src="@/assets/image/user2.png" />
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item>修改密码</el-dropdown-item>
        <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗?',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    // 清除用户信息（包括持久化数据）
    userStore.clearUser()
    
    // 清除token
    localStorage.removeItem('token')
    sessionStorage.removeItem('token')
    
    ElMessage.success('已退出登录')
    
    // 跳转到登录页
    router.push('/login')
  } catch (error) {
    // 用户取消登出
    console.log('用户取消登出')
  }
}
</script>

<style scoped lang="scss">
.el-dropdown-link:focus{
  outline: none;
}
.user{
  height: 45px;
  width: 45px;
  border-radius: 50%;
  cursor: pointer;
  background-color: #FFF;
}
</style>
