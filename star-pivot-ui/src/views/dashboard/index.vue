<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '@/store/user'
import { useMenuStore } from '@/store/menu'
import { 
  User, 
  Setting, 
  Document, 
  DataAnalysis,
  Clock,
  Location,
  Message
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const menuStore = useMenuStore()

// 获取用户名
const username = computed(() => {
  return userStore.userInfo?.nickName || userStore.userInfo?.userName || '用户'
})

// 获取当前时间
const currentTime = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
})

// 获取星期
const weekDay = computed(() => {
  const days = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  return days[new Date().getDay()]
})

// 统计卡片数据
const statsCards = [
  {
    title: '总用户数',
    value: '1,234',
    icon: User,
    color: '#409eff',
    bgColor: 'rgba(64, 158, 255, 0.1)'
  },
  {
    title: '系统菜单',
    value: '12',
    icon: Setting,
    color: '#67c23a',
    bgColor: 'rgba(103, 194, 58, 0.1)'
  },
  {
    title: '文档数量',
    value: '568',
    icon: Document,
    color: '#e6a23c',
    bgColor: 'rgba(230, 162, 60, 0.1)'
  },
  {
    title: '数据统计',
    value: '8,901',
    icon: DataAnalysis,
    color: '#f56c6c',
    bgColor: 'rgba(245, 108, 108, 0.1)'
  }
]
</script>

<template>
  <div class="dashboard-container">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <h1 class="welcome-title">
          <span class="greeting">欢迎回来，</span>
          <span class="username">{{ username }}</span>
        </h1>
        <p class="welcome-desc">今天是 {{ currentTime }} {{ weekDay }}</p>
      </div>
      <div class="welcome-avatar">
        <el-avatar :size="80" :src="userStore.userInfo?.avatar">
          <el-icon :size="40"><User /></el-icon>
        </el-avatar>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col 
        v-for="(card, index) in statsCards" 
        :key="index"
        :xs="12" 
        :sm="12" 
        :md="6" 
        :lg="6" 
        :xl="6"
      >
        <el-card class="stats-card" shadow="hover">
          <div class="stats-content">
            <div class="stats-icon" :style="{ backgroundColor: card.bgColor }">
              <el-icon :size="32" :style="{ color: card.color }">
                <component :is="card.icon" />
              </el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ card.value }}</div>
              <div class="stats-title">{{ card.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 信息卡片区域 -->
    <el-row :gutter="20" class="info-row">
      <!-- 用户信息卡片 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><User /></el-icon>
              <span>用户信息</span>
            </div>
          </template>
          <div class="user-info-list">
            <div class="info-item">
              <span class="info-label">用户名：</span>
              <span class="info-value">{{ userStore.userInfo?.userName || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">昵称：</span>
              <span class="info-value">{{ userStore.userInfo?.nickName || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">邮箱：</span>
              <span class="info-value">{{ userStore.userInfo?.email || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">手机号：</span>
              <span class="info-value">{{ userStore.userInfo?.phonenumber || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">状态：</span>
              <el-tag :type="userStore.userInfo?.status === '0' ? 'success' : 'danger'">
                {{ userStore.userInfo?.status === '0' ? '正常' : '停用' }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 系统信息卡片 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Setting /></el-icon>
              <span>系统信息</span>
            </div>
          </template>
          <div class="system-info-list">
            <div class="info-item">
              <el-icon><Clock /></el-icon>
              <span class="info-label">当前时间：</span>
              <span class="info-value">{{ currentTime }}</span>
            </div>
            <div class="info-item">
              <el-icon><Setting /></el-icon>
              <span class="info-label">菜单状态：</span>
              <el-tag :type="menuStore.collapse ? 'info' : 'success'">
                {{ menuStore.collapse ? '已折叠' : '展开' }}
              </el-tag>
            </div>
            <div class="info-item">
              <el-icon><User /></el-icon>
              <span class="info-label">角色数量：</span>
              <span class="info-value">{{ userStore.roles.length }} 个</span>
            </div>
            <div class="info-item">
              <el-icon><Document /></el-icon>
              <span class="info-label">权限数量：</span>
              <span class="info-value">{{ userStore.permissions.length }} 个</span>
            </div>
            <div class="info-item">
              <el-icon><Location /></el-icon>
              <span class="info-label">登录IP：</span>
              <span class="info-value">{{ userStore.userInfo?.loginIp || '-' }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 角色信息卡片 -->
    <el-card v-if="userStore.roles.length > 0" class="roles-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon><User /></el-icon>
          <span>角色信息</span>
        </div>
      </template>
      <div class="roles-list">
        <el-tag 
          v-for="role in userStore.roles" 
          :key="role.roleId"
          type="primary"
          class="role-tag"
        >
          {{ role.roleName }}
        </el-tag>
      </div>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.dashboard-container {
  padding: 20px;
  min-height: calc(100vh - 100px);
}

// 欢迎区域
.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  margin-bottom: 20px;
  color: #fff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);

  .welcome-content {
    flex: 1;

    .welcome-title {
      margin: 0 0 10px 0;
      font-size: 32px;
      font-weight: 600;

      .greeting {
        opacity: 0.9;
      }

      .username {
        margin-left: 8px;
        font-weight: 700;
      }
    }

    .welcome-desc {
      margin: 0;
      font-size: 16px;
      opacity: 0.9;
    }
  }

  .welcome-avatar {
    :deep(.el-avatar) {
      border: 3px solid rgba(255, 255, 255, 0.3);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
  }
}

// 统计卡片
.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  border-radius: 12px;
  transition: all 0.3s;
  border: none;

  &:hover {
    transform: translateY(-4px);
  }

  :deep(.el-card__body) {
    padding: 20px;
  }

  .stats-content {
    display: flex;
    align-items: center;
    gap: 16px;

    .stats-icon {
      width: 64px;
      height: 64px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .stats-info {
      flex: 1;

      .stats-value {
        font-size: 28px;
        font-weight: 700;
        color: #303133;
        margin-bottom: 4px;
        line-height: 1;
      }

      .stats-title {
        font-size: 14px;
        color: #909399;
        margin-top: 4px;
      }
    }
  }
}

// 信息卡片区域
.info-row {
  margin-bottom: 20px;
}

.info-card {
  border-radius: 12px;
  border: none;
  height: 100%;

  :deep(.el-card__header) {
    padding: 18px 20px;
    border-bottom: 1px solid #f0f0f0;
  }

  :deep(.el-card__body) {
    padding: 20px;
  }

  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
    color: #303133;

    .el-icon {
      font-size: 18px;
      color: #409eff;
    }
  }

  .user-info-list,
  .system-info-list {
    .info-item {
      display: flex;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #f5f5f5;
      gap: 8px;

      &:last-child {
        border-bottom: none;
      }

      .el-icon {
        color: #909399;
        font-size: 16px;
      }

      .info-label {
        color: #606266;
        font-size: 14px;
        min-width: 80px;
      }

      .info-value {
        color: #303133;
        font-size: 14px;
        font-weight: 500;
        flex: 1;
      }
    }
  }
}

// 角色卡片
.roles-card {
  border-radius: 12px;
  border: none;

  :deep(.el-card__header) {
    padding: 18px 20px;
    border-bottom: 1px solid #f0f0f0;
  }

  :deep(.el-card__body) {
    padding: 20px;
  }

  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
    color: #303133;

    .el-icon {
      font-size: 18px;
      color: #409eff;
    }
  }

  .roles-list {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;

    .role-tag {
      padding: 8px 16px;
      font-size: 14px;
      border-radius: 6px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .welcome-section {
    flex-direction: column;
    text-align: center;
    padding: 20px;

    .welcome-title {
      font-size: 24px;
    }

    .welcome-avatar {
      margin-top: 20px;
    }
  }

  .stats-card .stats-content {
    flex-direction: column;
    text-align: center;

    .stats-info {
      text-align: center;
    }
  }
}
</style>