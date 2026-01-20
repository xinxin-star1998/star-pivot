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
    <RolePermissionDialog
      v-model="permissionDialog"
      :role-data="currentRoleData"
      @success="refreshData"
    />
  </div>
</template>

<script setup lang="ts">
  import { ButtonMoreItem } from '@/components/core/forms/art-button-more/index.vue'
  import { useTable } from '@/hooks/core/useTable'
  import { fetchDeleteRole, fetchGetRoleList } from '@/api/role/role'
  import ArtButtonMore from '@/components/core/forms/art-button-more/index.vue'
  import RoleSearch from './modules/role-search.vue'
  import RoleEditDialog from './modules/role-edit-dialog.vue'
  import RolePermissionDialog from './modules/role-permission-dialog.vue'
  import { ElTag, ElMessageBox } from 'element-plus'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import { useAuth } from '@/hooks/core/useAuth'

  defineOptions({ name: 'Role' })

  const { hasAuth } = useAuth()

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
  const permissionDialog = ref(false)
  const currentRoleData = ref<RoleListItem | undefined>(undefined)

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
          minWidth: 150,
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
          width: 80,
          fixed: 'right',
          formatter: (row) =>
            h('div', [
              h(ArtButtonMore, {
                list: [
                  {
                    key: 'permission',
                    label: '分配菜单',
                    icon: 'ri:user-3-line',
                    // 分配菜单视为角色编辑权限的一部分
                    auth: 'system:role:edit'
                  },
                  {
                    key: 'edit',
                    label: '编辑角色',
                    icon: 'ri:edit-2-line',
                    auth: 'system:role:edit'
                  },
                  {
                    key: 'delete',
                    label: '删除角色',
                    icon: 'ri:delete-bin-4-line',
                    color: '#f56c6c',
                    auth: 'system:role:delete'
                  }
                ],
                onClick: (item: ButtonMoreItem) => buttonMoreClick(item, row)
              })
            ])
        }
      ]
    }
  })

  const dialogType = ref<'add' | 'edit'>('add')

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

  const buttonMoreClick = (item: ButtonMoreItem, row: RoleListItem) => {
    switch (item.key) {
      case 'permission':
        showPermissionDialog(row)
        break
      case 'edit':
        showDialog('edit', row)
        break
      case 'delete':
        deleteRole(row)
        break
    }
  }

  const showPermissionDialog = (roleId: RoleListItem) => {
    permissionDialog.value = true
    currentRoleData.value = roleId
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
      .catch((error) => {
        // 用户取消操作时不显示错误（Element Plus 会 reject，但这是正常的用户行为）
        // API 调用失败的错误已在 HTTP 拦截器中统一处理并显示错误消息
        if (import.meta.env.DEV && error !== 'cancel') {
          console.error('删除角色失败:', error)
        }
      })
  }
</script>
