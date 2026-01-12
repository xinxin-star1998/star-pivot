<!-- 用户管理页面 -->
<!-- art-full-height 自动计算出页面剩余高度 -->
<!-- art-table-card 一个符合系统样式的 class，同时自动撑满剩余高度 -->
<!-- 更多 useTable 使用示例请移步至 功能示例 下面的高级表格示例或者查看官方文档 -->
<!-- useTable 文档：https://www.artd.pro/docs/zh/guide/hooks/use-table.html -->
<template>
  <div class="user-page art-full-height">
    <div class="user-layout">
      <!-- 左侧部门树 -->
      <div class="left-panel">
        <ElCard shadow="never" class="department-tree-card">
          <div class="department-tree-header">
            <div class="dept-search-box">
              <ElInput
                v-model="deptSearchText"
                placeholder="搜索部门名称"
                size="small"
                clearable
                @input="handleDeptSearch"
              >
                <template #prefix>
                  <el-icon class="el-input__icon"><Search /></el-icon>
                </template>
              </ElInput>
            </div>
            <ElButton
              type="primary"
              size="small"
              :plain="selectedDeptId !== undefined"
              @click="showAllUsers"
              v-ripple
            >
              显示全部
            </ElButton>
          </div>
          <div class="department-tree-wrapper">
            <ElTree
              :data="deptTree"
              :props="deptTreeProps"
              :default-expand-all="false"
              :default-checked-keys="selectedDeptId ? [selectedDeptId] : []"
              :filter-node-method="filterDeptNode"
              ref="deptTreeRef"
              @node-click="(data) => handleDeptSelect(data.deptId)"
            >
              <template #default="{ node, data }">
                <span class="custom-tree-node">
                  <span>{{ node.label }}</span>
                </span>
              </template>
            </ElTree>
          </div>
        </ElCard>
      </div>

      <!-- 右侧用户列表 -->
      <div class="right-panel">
        <!-- 搜索栏 -->
        <UserSearch
          v-model="searchForm"
          @search="handleSearch"
          @reset="resetSearchParams"
        ></UserSearch>

        <ElCard class="art-table-card" shadow="never">
          <!-- 表格头部 -->
          <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
            <template #left>
              <ElSpace wrap>
                <ElButton @click="showDialog('add')" v-ripple>新增用户</ElButton>
              </ElSpace>
            </template>
          </ArtTableHeader>

          <!-- 表格 -->
          <ArtTable
            :loading="loading"
            :data="data"
            :columns="columns"
            :pagination="pagination"
            @selection-change="handleSelectionChange"
            @pagination:size-change="handleSizeChange"
            @pagination:current-change="handleCurrentChange"
          >
          </ArtTable>

          <!-- 用户弹窗 -->
          <UserDialog
            v-model:visible="dialogVisible"
            :type="dialogType"
            :user-data="currentUserData"
            @submit="handleDialogSubmit"
          />
        </ElCard>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import { ACCOUNT_TABLE_DATA } from '@/mock/temp/formData'
  import { useTable } from '@/hooks/core/useTable'
  import { fetchDeleteUser, fetchGetUserList, fetchUpdateUserStatus } from '@/api/user/user'
  import { fetchGetDeptTree, SysDept } from '@/api/dept/dept'
  import UserSearch from './modules/user-search.vue'
  import UserDialog from './modules/user-dialog.vue'
  import { ElMessageBox, ElImage, ElSwitch, ElMessage, ElTree, ElInput } from 'element-plus'
  import { Search } from '@element-plus/icons-vue'
  import { DialogType } from '@/types'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'

  defineOptions({ name: 'User' })

  type UserListItem = Api.SystemManage.UserListItem

  // 弹窗相关
  const dialogType = ref<DialogType>('add')
  const dialogVisible = ref(false)
  const currentUserData = ref<Partial<UserListItem>>({})

  // 选中行
  const selectedRows = ref<UserListItem[]>([])

  // 搜索表单
  const searchForm = ref<{
    userName: string | undefined
    sex: string | undefined
    phonenumber: string | undefined
    email: string | undefined
    status: string
    deptId: number | undefined
  }>({
    userName: undefined,
    sex: undefined,
    phonenumber: undefined,
    email: undefined,
    status: '0',
    deptId: undefined
  })

  // 部门树相关
  const deptTree = ref<SysDept[]>([])
  const deptLoading = ref(false)
  const selectedDeptId = ref<number | undefined>(undefined)
  const deptTreeRef = ref()

  // 部门树配置
  const deptTreeProps = {
    label: 'deptName',
    children: 'children',
    value: 'deptId'
  }

  // 部门搜索文本
  const deptSearchText = ref('')

  // 部门树节点过滤方法
  const filterDeptNode = (value: string, data: any) => {
    if (!value) return true
    return data.deptName?.includes(value)
  }

  // 处理部门搜索
  const handleDeptSearch = () => {
    if (deptTreeRef.value) {
      deptTreeRef.value.filter(deptSearchText.value)
    }
  }

  // 获取部门树数据
  const getDeptTree = async () => {
    try {
      deptLoading.value = true
      const data = await fetchGetDeptTree()
      deptTree.value = data
    } catch (error) {
      console.error('获取部门树失败:', error)
    } finally {
      deptLoading.value = false
    }
  }

  // 处理部门选择
  const handleDeptSelect = (deptId: number | undefined) => {
    selectedDeptId.value = deptId
    searchForm.value.deptId = deptId
    handleSearch(searchForm.value)
  }

  // 显示全部用户
  const showAllUsers = () => {
    selectedDeptId.value = undefined
    searchForm.value.deptId = undefined
    handleSearch(searchForm.value)
  }

  // 组件初始化
  onMounted(() => {
    getDeptTree()
  })

  const {
    columns,
    columnChecks,
    data,
    loading,
    pagination,
    getData,
    searchParams,
    resetSearchParams,
    handleSizeChange,
    handleCurrentChange,
    refreshData
  } = useTable({
    // 核心配置
    core: {
      apiFn: fetchGetUserList,
      apiParams: {
        pageNum: 1,
        pageSize: 20,
        ...searchForm.value
      },
      columnsFactory: () => [
        { type: 'selection' }, // 勾选列
        { type: 'index', width: 60, label: '序号' }, // 序号
        {
          prop: 'userInfo',
          label: '用户名',
          width: 280,
          // visible: false, // 默认是否显示列
          formatter: (row) => {
            const avatarUrl = row.avatar || ''
            return h('div', { class: 'user flex-c' }, [
              h(ElImage, {
                class: 'size-9.5 rounded-md',
                src: avatarUrl,
                previewSrcList: avatarUrl ? [avatarUrl] : [],
                // 图片预览是否插入至 body 元素上，用于解决表格内部图片预览样式异常
                previewTeleported: true
              }),
              h('div', { class: 'ml-2' }, [
                h('p', { class: 'user-name' }, row.userName || ''),
                h('p', { class: 'email' }, row.email || '')
              ])
            ])
          }
        },
        {
          prop: 'sex',
          label: '性别',
          sortable: true,
          formatter: (row) => {
            // 性别映射：0男 1女 2未知
            const sexMap: Record<string, string> = {
              '0': '男',
              '1': '女',
              '2': '未知'
            }
            return sexMap[row.sex] || row.sex || '未知'
          }
        },
        {
          prop: 'phonenumber',
          label: '手机号',
          formatter: (row) => {
            return row.phonenumber || '未知'
          }
        },
        {
          prop: 'status',
          label: '状态',
          formatter: (row) => {
            return h(ElSwitch, {
              modelValue: row.status === '0',
              activeValue: true,
              inactiveValue: false,
              onChange: (value: string | number | boolean) => {
                handleStatusChange(row, value === true)
              }
            })
          }
        },
        {
          prop: 'createTime',
          label: '创建日期',
          sortable: true
        },
        {
          prop: 'operation',
          label: '操作',
          width: 120,
          fixed: 'right', // 固定列
          formatter: (row) =>
            h('div', [
              h(ArtButtonTable, {
                type: 'edit',
                onClick: () => showDialog('edit', row)
              }),
              h(ArtButtonTable, {
                type: 'delete',
                onClick: () => deleteUser(row)
              })
            ])
        }
      ]
    },
    // 数据处理
    transform: {
      // 数据转换器 - 替换头像
      dataTransformer: (records) => {
        // 类型守卫检查
        if (!Array.isArray(records)) {
          console.warn('数据转换器: 期望数组类型，实际收到:', typeof records)
          return []
        }

        // 调试：打印第一条数据查看字段结构
        if (records.length > 0) {
          console.log('用户列表原始数据示例:', records[0])
          console.log('用户列表所有字段:', Object.keys(records[0]))
        }

        // 使用本地头像替换接口返回的头像
        return records.map((item, index: number) => {
          return {
            ...item,
            avatar: ACCOUNT_TABLE_DATA[index % ACCOUNT_TABLE_DATA.length].avatar
          }
        })
      }
    },
    // 生命周期钩子
    hooks: {
      onSuccess: (data) => {
        console.log('用户列表加载成功，数据:', data)
        if (data.length > 0) {
          console.log('第一条用户数据:', data[0])
        }
      }
    }
  })

  /**
   * 搜索处理
   * @param params 参数
   */
  const handleSearch = (params: Record<string, any>) => {
    console.log(params)
    // 搜索参数赋值
    Object.assign(searchParams, params)
    getData()
  }

  /**
   * 显示用户弹窗
   */
  const showDialog = (type: DialogType, row?: UserListItem): void => {
    console.log('打开弹窗:', { type, row })
    dialogType.value = type
    currentUserData.value = row || {}
    nextTick(() => {
      dialogVisible.value = true
    })
  }

  /**
   * 删除用户
   */
  const deleteUser = (row: UserListItem): void => {
    console.log('删除用户:', row)
    ElMessageBox.confirm(`确定要注销该用户吗？`, '注销用户', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }).then(() => {
      fetchDeleteUser([row.userId])
      refreshData()
      ElMessage.success('注销成功')
    })
  }

  /**
   * 处理弹窗提交事件
   */
  const handleDialogSubmit = async () => {
    try {
      dialogVisible.value = false
      currentUserData.value = {}
      // 刷新列表数据
      refreshData()
    } catch (error) {
      console.error('提交失败:', error)
    }
  }

  /**
   * 处理表格行选择变化
   */
  const handleSelectionChange = (selection: UserListItem[]): void => {
    selectedRows.value = selection
    console.log('选中行数据:', selectedRows.value)
  }

  /**
   * 处理用户状态切换
   */
  const handleStatusChange = async (row: UserListItem, value: boolean) => {
    try {
      const newStatus = value ? '0' : '1'
      await fetchUpdateUserStatus(row.userId, Number(newStatus))
      ElMessage.success(value ? '启用成功' : '禁用成功')
      // 更新本地数据
      row.status = newStatus
      // 刷新列表
      refreshData()
    } catch (error) {
      console.error('更新状态失败:', error)
      ElMessage.error('更新状态失败')
      // 刷新列表以恢复原状态
      refreshData()
    }
  }
</script>

<style lang="scss" scoped>
  .user-page {
    padding: 16px;
    background-color: #f5f7fa;
  }

  .user-layout {
    display: flex;
    gap: 16px;
    height: 100%;
    overflow: hidden;
  }

  .left-panel {
    width: 280px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }

  .right-panel {
    flex: 1;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }

  .department-tree-card {
    height: 100%;
    display: flex;
    flex-direction: column;
    border: none;
    box-shadow: none;
  }

  .card-header {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    padding: 12px 16px;
    border-bottom: 1px solid #ebeef5;
  }

  .department-tree-header {
    padding: 0 16px 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-direction: column;
    gap: 12px;
  }

  .dept-search-box {
    width: 100%;
  }

  :deep(.el-input) {
    width: 100%;
  }

  .department-tree-wrapper {
    flex: 1;
    padding: 0 16px 16px;
    overflow-y: auto;
    background-color: #fff;
  }

  :deep(.el-tree) {
    width: 100%;
    height: 100%;
    overflow: auto;
  }

  :deep(.el-tree-node__content) {
    height: 36px;
    line-height: 36px;
  }

  :deep(.el-tree-node__expand-icon) {
    font-size: 14px;
  }

  .custom-tree-node {
    display: flex;
    align-items: center;
    width: 100%;
  }

  .art-table-card {
    flex: 1;
    display: flex;
    flex-direction: column;
    border: none;
    box-shadow: none;
  }
</style>
