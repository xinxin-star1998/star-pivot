<template>
  <div class="role-management">
    <el-card class="role-card">
      <template #header>
        <div class="card-header">
          <span class="title">角色管理</span>
          <el-button type="primary" @click="handleAddRole">新增角色</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" inline>
          <el-form-item label="角色名称">
            <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable />
          </el-form-item>
          <el-form-item label="角色标识">
            <el-input v-model="searchForm.roleKey" placeholder="请输入角色标识" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 角色列表 -->
      <el-table :data="roleList" style="width: 100%" v-loading="loading">
        <el-table-column prop="roleId" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" width="120" />
        <el-table-column prop="roleKey" label="角色标识" width="120" />
        <el-table-column prop="roleSort" label="排序" width="80" />
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
    
    <!-- 角色编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="roleForm" :rules="roleRules" ref="roleFormRef" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" :disabled="!!roleForm.roleId" />
        </el-form-item>
        <el-form-item label="角色标识" prop="roleKey">
          <el-input v-model="roleForm.roleKey" />
        </el-form-item>
        <el-form-item label="排序" prop="roleSort">
          <el-input-number v-model="roleForm.roleSort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="roleForm.status">
            <el-radio label="1">正常</el-radio>
            <el-radio label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="roleForm.remark" type="textarea" />
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

interface Role {
  roleId?: number;
  roleName: string;
  roleKey: string;
  roleSort: number;
  status: string;
  remark?: string;
  createTime?: string;
}

const loading = ref(false);
const dialogVisible = ref(false);
const dialogTitle = ref('');

// 搜索表单
const searchForm = reactive({
  roleName: '',
  roleKey: ''
});

// 分页信息
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
});

// 角色列表
const roleList = ref<Role[]>([]);

// 角色表单
const roleForm = ref<Role>({
  roleName: '',
  roleKey: '',
  roleSort: 1,
  status: '1',
  remark: ''
});

// 表单验证规则
const roleRules: FormRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  roleKey: [
    { required: true, message: '请输入角色标识', trigger: 'blur' }
  ]
};

const roleFormRef = ref<FormInstance>();

// 模拟加载角色列表
const loadRoleList = async () => {
  loading.value = true;
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 模拟数据
    roleList.value = [
      { roleId: 1, roleName: '管理员', roleKey: 'admin', roleSort: 1, status: '1', remark: '系统管理员', createTime: '2024-01-01 10:00:00' },
      { roleId: 2, roleName: '普通用户', roleKey: 'user', roleSort: 2, status: '1', remark: '普通用户角色', createTime: '2024-01-02 10:00:00' },
      { roleId: 3, roleName: '测试角色', roleKey: 'test', roleSort: 3, status: '0', remark: '测试角色', createTime: '2024-01-03 10:00:00' }
    ];
    
    pagination.total = roleList.value.length;
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  loadRoleList();
};

// 重置
const handleReset = () => {
  searchForm.roleName = '';
  searchForm.roleKey = '';
  loadRoleList();
};

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size;
  loadRoleList();
};

// 当前页改变
const handleCurrentChange = (page: number) => {
  pagination.page = page;
  loadRoleList();
};

// 新增角色
const handleAddRole = () => {
  dialogTitle.value = '新增角色';
  roleForm.value = {
    roleName: '',
    roleKey: '',
    roleSort: 1,
    status: '1',
    remark: ''
  };
  dialogVisible.value = true;
};

// 编辑角色
const handleEdit = (row: Role) => {
  dialogTitle.value = '编辑角色';
  roleForm.value = { ...row };
  dialogVisible.value = true;
};

// 删除角色
const handleDelete = async (row: Role) => {
  try {
    await ElMessageBox.confirm(`确定要删除角色 "${row.roleName}" 吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    // 模拟删除操作
    roleList.value = roleList.value.filter(role => role.roleId !== row.roleId);
    ElMessage.success('删除成功');
  } catch (error) {
    // 用户取消删除
  }
};

// 保存角色
const handleSave = async () => {
  if (!roleFormRef.value) return;
  
  await roleFormRef.value.validate((valid) => {
    if (valid) {
      // 模拟保存操作
      if (roleForm.value.roleId) {
        // 编辑
        const index = roleList.value.findIndex(role => role.roleId === roleForm.value.roleId);
        if (index !== -1) {
          roleList.value[index] = { ...roleForm.value };
        }
        ElMessage.success('角色更新成功');
      } else {
        // 新增
        const newRoleId = Math.max(...roleList.value.map(r => r.roleId || 0)) + 1;
        roleList.value.push({ ...roleForm.value, roleId: newRoleId });
        ElMessage.success('角色添加成功');
      }
      
      dialogVisible.value = false;
    }
  });
};

onMounted(() => {
  loadRoleList();
});
</script>

<style scoped>
.role-management {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
}

.role-card {
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