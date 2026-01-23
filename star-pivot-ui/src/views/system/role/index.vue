<!-- 角色管理页面 -->
<template>
  <div class="art-full-height">
    <RoleSearch
      v-show="showSearchBar"
      v-model="searchForm"
      @search="handleSearch"
      @reset="resetSearchParams"
    ></RoleSearch>

    <ElCard
      class="art-table-card"
      shadow="never"
      :style="{ 'margin-top': showSearchBar ? '12px' : '0' }"
    >
      <ArtTableHeader
        v-model:columns="columnChecks"
        v-model:showSearchBar="showSearchBar"
        :loading="loading"
        @refresh="refreshData"
      >
        <template #left>
          <ElSpace wrap>
            <ElButton @click="showDialog('add')" v-ripple v-auth="'system:role:add'"
              >新增角色</ElButton
            >
          </ElSpace>
        </template>
      </ArtTableHeader>

      <!-- 表格 -->
      <ArtTable
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
      </ArtTable>
    </ElCard>

    <!-- 角色编辑弹窗 -->
    <RoleEditDialog
      v-model="dialogVisible"
      :dialog-type="dialogType"
      :role-data="currentRoleData"
      @success="refreshData"
    />
    <!-- 菜单权限弹窗 -->
    <AssignDataScopeDialog
      v-model="assignDataScopeDialog"
      :role-data="currentRoleData"
      @success="refreshData"
    />
  </div>
</template>

<script setup lang="ts">
  import { useTable } from '@/hooks/core/useTable'
  import { fetchDeleteRole, fetchGetRoleList } from '@/api/role/role'
  import ArtButtonMore, {
    type ButtonMoreItem
  } from '@/components/core/forms/art-button-more/index.vue'
  import RoleSearch from './modules/role-search.vue'
  import RoleEditDialog from './modules/role-edit-dialog.vue'
  import AssignDataScopeDialog from './modules/assign-dataScope-dialog.vue'
  import { ElTag, ElMessageBox } from 'element-plus'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import { useRouter } from 'vue-router'

  defineOptions({ name: 'Role' })

  const router = useRouter()

  type RoleListItem = Api.SystemManage.RoleListItem

  // 搜索表单
  const searchForm = ref({
    roleName: undefined,
    roleKey: undefined,
    remark: undefined,
    status: undefined,
    daterange: undefined
  })

  const showSearchBar = ref(false)

  const dialogVisible = ref(false)
  const assignDataScopeDialog = ref(false)
  const currentRoleData = ref<RoleListItem | undefined>(undefined)

  const dialogType = ref<'add' | 'edit'>('add')

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
      apiFn: fetchGetRoleList,
      apiParams: {
        pageNum: 1,
        pageSize: 10
      },
      // 排除 apiParams 中的属性
      excludeParams: ['date range'],
      columnsFactory: () => [
        {
          type: 'index',
          label: '序号',
          width: 100,
          index: (index: number) => {
            return (pagination.current - 1) * pagination.size + index + 1
          }
        },
        {
          prop: 'roleName',
          label: '角色名称',
          minWidth: 120
        },
        {
          prop: 'roleKey',
          label: '角色编码',
          minWidth: 120
        },
        {
          prop: 'remark',
          label: '角色描述',
          minWidth: 50,
          showOverflowTooltip: true
        },
        {
          prop: 'status',
          label: '角色状态',
          width: 100,
          formatter: (row) => {
            const status = Number(row.status)
            const isEnabled = status === 0
            const tagType = isEnabled ? 'success' : 'danger'
            const text = isEnabled ? '启用' : '禁用'
            return h(ElTag, { type: tagType }, () => text)
          }
        },
        {
          prop: 'createTime',
          label: '创建日期',
          width: 180,
          sortable: true
        },
        {
          prop: 'operation',
          label: '操作',
          width: 200,
          fixed: 'right',
          formatter: (row) => {
            const menuList: ButtonMoreItem[] = [
              {
                key: 'assignDataScope',
                label: '数据权限',
                icon: 'ri:shield-keyhole-line',
                color: 'var(--el-color-primary)',
                auth: 'system:role:assignDataScope'
              },
              {
                key: 'edit',
                label: '编辑角色',
                icon: 'ri:pencil-line',
                color: 'var(--el-color-info)',
                auth: 'system:role:edit'
              },
              {
                key: 'delete',
                label: '删除角色',
                icon: 'ri:delete-bin-5-line',
                color: 'var(--el-color-danger)',
                auth: 'system:role:delete'
              },
              {
                key: 'assignUser',
                label: '分配用户',
                icon: 'ri:user-line',
                color: 'var(--el-color-primary)',
                auth: 'system:role:allocatedList'
              }
            ]

            return h(ArtButtonMore, {
              list: menuList,
              onClick: (item: ButtonMoreItem) => {
                switch (item.key) {
                  case 'assignDataScope':
                    showAssignDataScopeDialog(row)
                    break
                  case 'edit':
                    showDialog('edit', row)
                    break
                  case 'delete':
                    deleteRole(row)
                    break
                  case 'assignUser':
                    assignUser(row)
                    break
                }
              }
            })
          }
        }
      ]
    }
  })

  const showDialog = (type: 'add' | 'edit', row?: RoleListItem) => {
    dialogVisible.value = true
    dialogType.value = type
    currentRoleData.value = row
  }

  /**
   * 搜索处理
   * @param params 搜索参数
   */
  const handleSearch = (params: Record<string, any>) => {
    // 处理日期区间参数，把 daterange 转换为 startTime 和 endTime
    const { daterange, ...filtersParams } = params
    const [startTime, endTime] = Array.isArray(daterange) ? daterange : [null, null]

    // 搜索参数赋值
    Object.assign(searchParams, { ...filtersParams, startTime, endTime })
    getData()
  }

  const showAssignDataScopeDialog = (row: RoleListItem) => {
    assignDataScopeDialog.value = true
    currentRoleData.value = row
  }

  const deleteRole = (row: RoleListItem) => {
    ElMessageBox.confirm(`确定删除角色"${row.roleName}"吗？此操作不可恢复！`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        await fetchDeleteRole([row.roleId])
        refreshData()
      })
      .catch((error: any) => {
        // 用户取消操作时不显示错误（Element Plus 会 reject，但这是正常的用户行为）
        // API 调用失败的错误已在 HTTP 拦截器中统一处理并显示错误消息
        if (import.meta.env.DEV && error !== 'cancel') {
          console.error('删除角色失败:', error)
        }
      })
  }

  /**
   * 分配用户
   * @param row 角色数据
   */
  const assignUser = (row: RoleListItem) => {
    // 跳转到分配用户页面，使用 name 方式跳转以支持 params
    router.push({
      name: 'AssignUser',
      params: {
        roleId: row.roleId
      }
    })
  }
</script>
