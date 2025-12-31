<template>
  <div class="user-management">
    <el-card class="user-card">
      <template #header>
        <div class="card-header">
          <span class="title">用户管理</span>
          <el-button type="primary" @click="handleAddUser">新增用户</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" inline>
          <el-form-item label="用户名">
            <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="searchForm.email" placeholder="请输入邮箱" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 用户列表 -->
      <el-table :data="userList" style="width: 100%" v-loading="loading">
        <el-table-column prop="userId" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickName" label="昵称" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '1' ? 'success' : 'danger'">
              {{ row.status === '1' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination"
      />
    </el-card>
    
    <!-- 用户编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="!!userForm.userId" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickName">
          <el-input v-model="userForm.nickName" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio label="1">正常</el-radio>
            <el-radio label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';

interface User {
  userId?: number;
  username: string;
  nickName: string;
  email: string;
  phone: string;
  status: string;
  createTime?: string;
}

const loading = ref(false);
const dialogVisible = ref(false);
const dialogTitle = ref('');

// 搜索表单
const searchForm = reactive({
  username: '',
  email: ''
});

// 分页信息
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
});

// 用户列表
const userList = ref<User[]>([]);

// 用户表单
const userForm = ref<User>({
  username: '',
  nickName: '',
  email: '',
  phone: '',
  status: '1'
});

// 表单验证规则
const userRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符', trigger: 'blur' }
  ],
  nickName: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
};

const userFormRef = ref<FormInstance>();

// 模拟加载用户列表
const loadUserList = async () => {
  loading.value = true;
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 模拟数据
    userList.value = [
      { userId: 1, username: 'admin', nickName: '管理员', email: 'admin@example.com', phone: '13800138000', status: '1', createTime: '2024-01-01 10:00:00' },
      { userId: 2, username: 'user1', nickName: '用户1', email: 'user1@example.com', phone: '13800138001', status: '1', createTime: '2024-01-02 10:00:00' },
      { userId: 3, username: 'user2', nickName: '用户2', email: 'user2@example.com', phone: '13800138002', status: '0', createTime: '2024-01-03 10:00:00' },
      { userId: 4, username: 'user3', nickName: '用户3', email: 'user3@example.com', phone: '13800138003', status: '1', createTime: '2024-01-04 10:00:00' }
    ];
    
    pagination.total = userList.value.length;
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  loadUserList();
};

// 重置
const handleReset = () => {
  searchForm.username = '';
  searchForm.email = '';
  loadUserList();
};

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size;
  loadUserList();
};

// 当前页改变
const handleCurrentChange = (page: number) => {
  pagination.page = page;
  loadUserList();
};

// 新增用户
const handleAddUser = () => {
  dialogTitle.value = '新增用户';
  userForm.value = {
    username: '',
    nickName: '',
    email: '',
    phone: '',
    status: '1'
  };
  dialogVisible.value = true;
};

// 编辑用户
const handleEdit = (row: User) => {
  dialogTitle.value = '编辑用户';
  userForm.value = { ...row };
  dialogVisible.value = true;
};

// 删除用户
const handleDelete = async (row: User) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    // 模拟删除操作
    userList.value = userList.value.filter(user => user.userId !== row.userId);
    ElMessage.success('删除成功');
  } catch (error) {
    // 用户取消删除
  }
};

// 保存用户
const handleSave = async () => {
  if (!userFormRef.value) return;
  
  await userFormRef.value.validate((valid) => {
    if (valid) {
      // 模拟保存操作
      if (userForm.value.userId) {
        // 编辑
        const index = userList.value.findIndex(user => user.userId === userForm.value.userId);
        if (index !== -1) {
          userList.value[index] = { ...userForm.value };
        }
        ElMessage.success('用户更新成功');
      } else {
        // 新增
        const newUserId = Math.max(...userList.value.map(u => u.userId || 0)) + 1;
        userList.value.push({ ...userForm.value, userId: newUserId });
        ElMessage.success('用户添加成功');
      }
      
      dialogVisible.value = false;
    }
  });
};

onMounted(() => {
  loadUserList();
});
</script>

<style scoped>
.user-management {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
}

.user-card {
  min-height: 600px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.search-area {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>