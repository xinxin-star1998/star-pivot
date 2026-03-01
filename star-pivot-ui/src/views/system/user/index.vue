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
              @node-click="(data: SysDept) => handleDeptSelect(data.deptId)"
            >
              <template #default="{ node }">
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
                <ElButton @click="showDialog('add')" v-ripple v-auth="'system:user:add'"
                  >新增用户</ElButton
                >
                <ElButton
                  type="danger"
                  :disabled="selectedRows.length === 0"
                  @click="handleBatchDelete"
                  v-ripple
                  v-auth="'system:user:delete'"
                >
                  批量删除
                </ElButton>
                <ElButton
                  type="primary"
                  plain
                  v-ripple
                  v-auth="'system:user:import'"
                  @click="importDialogVisible = true"
                >
                  导入用户
                </ElButton>
                <ElButton
                  type="primary"
                  plain
                  v-ripple
                  v-auth="'system:user:export'"
                  @click="handleExportUsers"
                >
                  导出用户
                </ElButton>
              </ElSpace>
            </template>
          </ArtTableHeader>

          <!-- 通用导入弹窗 -->
          <ArtImportDialog
            v-model="importDialogVisible"
            business-type="user"
            title="用户导入"
            :show-overwrite="true"
            overwrite-label="是否更新已经存在的用户数据"
            @success="refreshData"
          />

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
  import ArtImportDialog from '@/components/core/forms/art-import-dialog/index.vue'
  import { useTable } from '@/hooks/core/useTable'
  import {
    fetchDeleteUser,
    fetchGetUserList,
    fetchUpdateUserStatus,
    fetchUnlockUser
  } from '@/api/user/user'
  import { fetchExportData } from '@/api/common/import-export'
  import { fetchGetDeptTree, SysDept } from '@/api/dept/dept'
  import UserSearch from './modules/user-search.vue'
  import UserDialog from './modules/user-dialog.vue'
  import {
    ElMessageBox,
    ElSwitch,
    ElMessage,
    ElTree,
    ElInput,
    ElButton,
    ElIcon
  } from 'element-plus'
  import { Search } from '@element-plus/icons-vue'
  import { DialogType } from '@/types'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import ArtAvatarDisplay from '@/components/core/media/art-avatar-display/index.vue'
  import { useAuth } from '@/hooks/core/useAuth'

  defineOptions({ name: 'User' })

  const { hasAuth } = useAuth()

  type UserListItem = Api.SystemManage.UserListItem

  // 弹窗相关
  const dialogType = ref<DialogType>('add')
  const dialogVisible = ref(false)
  const currentUserData = ref<Partial<UserListItem>>({})
  // 导入弹窗显隐
  const importDialogVisible = ref(false)

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
          label: '用户信息',
          width: 300,
          formatter: (row) => {
            const avatarUrl = row.avatar || ''
            const hasAvatar = !!avatarUrl && avatarUrl !== ''

            return h('div', { class: 'user-info flex-c items-center' }, [
              // 有头像时用 ArtAvatarDisplay 展示（支持 OSS 私有桶 presigned，列表页可正常显示）
              hasAvatar &&
                h('div', { class: 'avatar-wrapper' }, [
                  h(ArtAvatarDisplay, {
                    avatarUrl,
                    size: 40,
                    avatarClass: 'size-10'
                  })
                ]),

              // 用户信息容器，根据是否有头像调整间距
              h(
                'div',
                {
                  class: `flex-1 min-w-0 ${hasAvatar ? 'ml-3' : ''}`
                },
                [
                  h(
                    'div',
                    {
                      class: 'flex items-center gap-2',
                      style: { whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }
                    },
                    [
                      h(
                        'span',
                        {
                          class: 'user-name font-medium',
                          style: {
                            whiteSpace: 'nowrap',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            color: 'var(--art-gray-900)'
                          }
                        },
                        row.userName || '未知用户'
                      ),
                      h('span', {
                        class: 'status-indicator',
                        style: {
                          display: 'inline-block',
                          width: '8px',
                          height: '8px',
                          borderRadius: '50%',
                          backgroundColor: row.status === '0' ? '#67C23A' : '#909399'
                        }
                      })
                    ]
                  ),
                  h(
                    'p',
                    {
                      class: 'email text-sm',
                      style: {
                        whiteSpace: 'nowrap',
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                        color: 'var(--art-gray-500)'
                      }
                    },
                    row.email || '无邮箱'
                  )
                ]
              )
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
          width: 180,
          fixed: 'right', // 固定列
          formatter: (row) => {
            const actions: any[] = []

            // 编辑用户按钮权限：system:user:edit
            if (hasAuth('system:user:edit')) {
              actions.push(
                h(ArtButtonTable, {
                  type: 'edit',
                  onClick: () => showDialog('edit', row)
                })
              )

              // 解锁账户按钮（管理员操作）- 只在账户被锁定时显示
              if (row.isLocked === true) {
                actions.push(
                  h(ArtButtonTable, {
                    icon: 'ri:lock-unlock-line',
                    iconClass: 'bg-warning/12 text-warning',
                    onClick: () => unlockUser(row)
                  })
                )
              }
            }

            // 删除用户按钮权限：system:user:delete
            if (hasAuth('system:user:delete')) {
              actions.push(
                h(ArtButtonTable, {
                  type: 'delete',
                  onClick: () => deleteUser(row)
                })
              )
            }

            if (actions.length === 0) {
              // 无任何操作权限时返回空占位
              return h('span', { style: 'color: var(--art-gray-500)' }, '')
            }

            return h('div', actions)
          }
        }
      ]
    },
    // 数据处理
    transform: {
      // 数据转换器
      dataTransformer: (records) => {
        // 类型守卫检查
        if (!Array.isArray(records)) {
          return []
        }

        return records
      }
    }
  })

  /**
   * 搜索处理
   * @param params 参数
   */
  const handleSearch = (params: Record<string, any>) => {
    // 搜索参数赋值
    Object.assign(searchParams, params)
    getData()
  }

  /**
   * 显示用户弹窗
   */
  const showDialog = (type: DialogType, row?: UserListItem): void => {
    dialogType.value = type
    currentUserData.value = row || {}
    nextTick(() => {
      dialogVisible.value = true
    })
  }

  /**
   * 删除用户
   */
  const deleteUser = async (row: UserListItem): Promise<void> => {
    try {
      await ElMessageBox.confirm(`确定要注销该用户吗？`, '注销用户', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      })
      await fetchDeleteUser([row.userId])
      refreshData()
      ElMessage.success('注销成功')
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除用户失败:', error)
        ElMessage.error('注销失败')
      }
    }
  }

  /**
   * 批量删除用户
   */
  const handleBatchDelete = async (): Promise<void> => {
    if (selectedRows.value.length === 0) {
      ElMessage.warning('请选择要删除的用户')
      return
    }
    try {
      const userNames = selectedRows.value.map((row) => row.userName || '未知用户').join('、')
      await ElMessageBox.confirm(
        `确定要注销以下 ${selectedRows.value.length} 个用户吗？\n${userNames}`,
        '批量注销用户',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'error'
        }
      )
      const userIds = selectedRows.value
        .map((row) => row.userId)
        .filter((id): id is number => id !== undefined && id !== null)
      if (userIds.length === 0) {
        ElMessage.warning('所选用户中没有有效的用户ID')
        return
      }
      await fetchDeleteUser(userIds)
      selectedRows.value = []
      refreshData()
      ElMessage.success('注销成功')
    } catch (error) {
      if (error !== 'cancel') {
        console.error('批量删除用户失败:', error)
        ElMessage.error('注销失败')
      }
    }
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

  /**
   * 解锁账户（管理员操作）
   */
  const unlockUser = async (row: UserListItem): Promise<void> => {
    try {
      await ElMessageBox.confirm(`确定要解锁用户 "${row.userName}" 的账户吗？`, '解锁账户', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await fetchUnlockUser(row.userId)
      ElMessage.success('账户解锁成功')
      refreshData()
    } catch (error: any) {
      if (error !== 'cancel') {
        console.error('解锁账户失败:', error)
        // 后端返回的消息会包含在 error 中，直接显示
        const errorMsg = error?.message || error?.response?.data?.msg || '解锁失败'
        ElMessage.error(errorMsg)
      }
    }
  }

  /**
   * 导出用户数据
   */
  const handleExportUsers = async () => {
    try {
      // 使用当前搜索条件作为导出参数
      const exportParams = {
        ...searchForm.value
      }
      await fetchExportData('user', exportParams)
    } catch (error) {
      console.error('导出用户失败:', error)
    }
  }
</script>

<style lang="scss" scoped>
  .user-page {
    padding: 16px;
    background-color: var(--default-bg-color);
  }

  .user-layout {
    display: flex;
    gap: 16px;
    height: 100%;
    overflow: hidden;
  }

  .left-panel {
    display: flex;
    flex-direction: column;
    width: 280px;
    overflow: hidden;
    background-color: var(--default-box-color);
    border-radius: 8px;
    border: 1px solid var(--art-card-border);
    box-shadow: 0 2px 12px 0 rgb(0 0 0 / 10%);
  }

  .dark .left-panel {
    box-shadow: none;
  }

  .right-panel {
    display: flex;
    flex: 1;
    flex-direction: column;
    overflow: hidden;
    background-color: var(--default-box-color);
    border-radius: 8px;
    border: 1px solid var(--art-card-border);
    box-shadow: 0 2px 12px 0 rgb(0 0 0 / 10%);
  }

  .dark .right-panel {
    box-shadow: none;
  }

  .department-tree-card {
    display: flex;
    flex-direction: column;
    height: 100%;
    border: none;
    box-shadow: none;
  }

  .card-header {
    padding: 12px 16px;
    font-size: 16px;
    font-weight: 600;
    color: var(--art-gray-900);
    border-bottom: 1px solid var(--default-border);
  }

  .department-tree-header {
    display: flex;
    flex-direction: column;
    gap: 12px;
    align-items: center;
    justify-content: space-between;
    padding: 0 16px 12px;
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
    background-color: var(--default-box-color);
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
    display: flex;
    flex: 1;
    flex-direction: column;
    border: none;
    box-shadow: none;
  }
</style>
