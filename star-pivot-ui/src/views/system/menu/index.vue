<template>
  <div class="menu-management">
    <el-card class="menu-card">
      <template #header>
        <div class="card-header">
          <span class="title">菜单管理</span>
          <el-button type="primary" @click="handleAddMenu">新增菜单</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" inline>
          <el-form-item label="菜单名称">
            <el-input v-model="searchForm.menuName" placeholder="请输入菜单名称" clearable />
          </el-form-item>
          <el-form-item label="菜单标识">
            <el-input v-model="searchForm.perms" placeholder="请输入菜单标识" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 菜单树形列表 -->
      <el-table 
        :data="menuList" 
        style="width: 100%" 
        v-loading="loading"
        row-key="menuId"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="menuName" label="菜单名称" width="180">
          <template #default="{ row }">
            <span>{{ row.icon ? ` ${row.menuName}` : row.menuName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="menuType" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="menuTypeTag(row.menuType)">
              {{ menuTypeText(row.menuType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路径" width="150" />
        <el-table-column prop="perms" label="权限标识" width="150" />
        <el-table-column prop="orderNum" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '1' ? 'success' : 'danger'">
              {{ row.status === '1' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 菜单编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="menuForm" :rules="menuRules" ref="menuFormRef" label-width="100px">
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="menuForm.parentId"
            :data="menuTree"
            :props="{ value: 'menuId', label: 'menuName', children: 'children' }"
            placeholder="选择上级菜单"
            check-strictly
            clearable
          />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="menuForm.menuType">
            <el-radio label="M">目录</el-radio>
            <el-radio label="C">菜单</el-radio>
            <el-radio label="F">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="menuForm.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="路由路径" prop="path" v-if="menuForm.menuType !== 'F'">
          <el-input v-model="menuForm.path" placeholder="请输入路由路径" />
        </el-form-item>
        <el-form-item label="组件路径" prop="component" v-if="menuForm.menuType === 'C'">
          <el-input v-model="menuForm.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="权限标识" prop="perms" v-if="menuForm.menuType !== 'M'">
          <el-input v-model="menuForm.perms" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="图标" prop="icon" v-if="menuForm.menuType !== 'F'">
          <el-input v-model="menuForm.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="排序" prop="orderNum">
          <el-input-number v-model="menuForm.orderNum" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="menuForm.status">
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

interface Menu {
  menuId?: number;
  menuName: string;
  parentId: number | null;
  orderNum: number;
  path?: string;
  component?: string;
  menuType: string; // M目录 C菜单 F按钮
  visible?: string; // 0显示 1隐藏
  status: string; // 0正常 1停用
  perms?: string;
  icon?: string;
  children?: Menu[];
}

const loading = ref(false);
const dialogVisible = ref(false);
const dialogTitle = ref('');

// 搜索表单
const searchForm = reactive({
  menuName: '',
  perms: ''
});

// 菜单列表
const menuList = ref<Menu[]>([]);
const menuTree = ref<Menu[]>([]);

// 菜单表单
const menuForm = ref<Menu>({
  menuName: '',
  parentId: null,
  orderNum: 1,
  menuType: 'M',
  status: '1'
});

// 表单验证规则
const menuRules: FormRules = {
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ],
  menuType: [
    { required: true, message: '请选择菜单类型', trigger: 'change' }
  ]
};

const menuFormRef = ref<FormInstance>();

// 获取菜单类型标签类型
const menuTypeTag = (type: string) => {
  if (type === 'M') return 'info';
  if (type === 'C') return 'warning';
  if (type === 'F') return 'danger';
  return '';
};

// 获取菜单类型文本
const menuTypeText = (type: string) => {
  if (type === 'M') return '目录';
  if (type === 'C') return '菜单';
  if (type === 'F') return '按钮';
  return '';
};

// 模拟加载菜单列表
const loadMenuList = async () => {
  loading.value = true;
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 模拟数据
    menuList.value = [
      {
        menuId: 1,
        menuName: '系统管理',
        parentId: null,
        orderNum: 1,
        path: '/system',
        component: 'Layout',
        menuType: 'M',
        status: '1',
        icon: 'Setting',
        children: [
          {
            menuId: 2,
            menuName: '用户管理',
            parentId: 1,
            orderNum: 1,
            path: '/system/user',
            component: 'system/user/index',
            menuType: 'C',
            status: '1',
            perms: 'sys:user:list',
            icon: 'UserFilled'
          },
          {
            menuId: 3,
            menuName: '角色管理',
            parentId: 1,
            orderNum: 2,
            path: '/system/role',
            component: 'system/role/index',
            menuType: 'C',
            status: '1',
            perms: 'sys:role:list',
            icon: 'User'
          },
          {
            menuId: 4,
            menuName: '菜单管理',
            parentId: 1,
            orderNum: 3,
            path: '/system/menu',
            component: 'system/menu/index',
            menuType: 'C',
            status: '1',
            perms: 'sys:menu:list',
            icon: 'Menu'
          }
        ]
      },
      {
        menuId: 5,
        menuName: '首页',
        parentId: null,
        orderNum: 0,
        path: '/dashboard',
        component: 'dashboard/index',
        menuType: 'C',
        status: '1',
        icon: 'House'
      }
    ];
    
    // 构建菜单树结构
    menuTree.value = [
      { menuId: 0, menuName: '根目录', parentId: null, orderNum: 0, menuType: 'M', status: '1' },
      ...menuList.value
    ];
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  loadMenuList();
};

// 重置
const handleReset = () => {
  searchForm.menuName = '';
  searchForm.perms = '';
  loadMenuList();
};

// 新增菜单
const handleAddMenu = () => {
  dialogTitle.value = '新增菜单';
  menuForm.value = {
    menuName: '',
    parentId: null,
    orderNum: 1,
    menuType: 'M',
    status: '1'
  };
  dialogVisible.value = true;
};

// 编辑菜单
const handleEdit = (row: Menu) => {
  dialogTitle.value = '编辑菜单';
  menuForm.value = { ...row };
  dialogVisible.value = true;
};

// 删除菜单
const handleDelete = async (row: Menu) => {
  try {
    await ElMessageBox.confirm(`确定要删除菜单 "${row.menuName}" 吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    // 模拟删除操作
    ElMessage.success('删除成功');
  } catch (error) {
    // 用户取消删除
  }
};

// 保存菜单
const handleSave = async () => {
  if (!menuFormRef.value) return;
  
  await menuFormRef.value.validate((valid) => {
    if (valid) {
      // 模拟保存操作
      if (menuForm.value.menuId) {
        // 编辑
        ElMessage.success('菜单更新成功');
      } else {
        // 新增
        ElMessage.success('菜单添加成功');
      }
      
      dialogVisible.value = false;
      loadMenuList();
    }
  });
};

onMounted(() => {
  loadMenuList();
});
</script>

<style scoped>
.menu-management {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
}

.menu-card {
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