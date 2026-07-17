<!-- 部门管理页面 -->
<template>
  <div class="dept-page art-full-height">
    <DeptSearch v-model="searchForm" @search="handleSearch" @reset="resetSearchParams"></DeptSearch>

    <ElCard class="art-table-card" shadow="never">
      <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
        <template #left>
          <ElSpace wrap>
            <ElButton @click="showDialog('add')" v-ripple v-auth="'system:dept:add'">
              {{ t('system.dept.addDept') }}
            </ElButton>
            <ElButton @click="toggleExpand" v-ripple>
              {{ isExpanded ? t('common.collapse') : t('common.expand') }}
            </ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <ArtTable
        ref="tableRef"
        rowKey="deptId"
        :loading="loading"
        :data="filteredTableData"
        :columns="columns"
        :stripe="false"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="false"
      >
      </ArtTable>

      <DeptDialog
        v-model:visible="dialogVisible"
        :type="dialogType"
        :dept-data="currentDeptData"
        @submit="handleDialogSubmit"
      />
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import { useTableColumns } from '@/hooks/core/useTableColumns'
  import { useAuth } from '@/hooks/core/useAuth'
  import { useI18n } from 'vue-i18n'
  import { handleMutationError } from '@/utils/http/mutation'
  import DeptSearch from './modules/dept-search.vue'
  import DeptDialog from './modules/dept-dialog.vue'
  import { ElMessage, ElMessageBox, ElTag } from 'element-plus'
  import { DialogType } from '@/types'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import { fetchDeleteDept, fetchGetDeptTree, type SysDept } from '@/api/dept/dept'

  defineOptions({ name: 'Dept' })

  const { hasAuth } = useAuth()
  const { t } = useI18n()

  const loading = ref(false)
  const isExpanded = ref(false)
  const tableRef = ref()

  const dialogType = ref<DialogType>('add')
  const dialogVisible = ref(false)
  const currentDeptData = ref<Partial<SysDept>>({})

  const searchForm = ref({
    deptName: '',
    leader: '',
    status: ''
  })

  const tableData = ref<SysDept[]>([])

  const getDeptList = async (): Promise<void> => {
    loading.value = true
    try {
      const res = await fetchGetDeptTree()
      tableData.value = res || []
    } catch (error) {
      console.error('获取部门列表失败:', error)
      handleMutationError(error, t('system.dept.loadFail'))
      tableData.value = []
    } finally {
      loading.value = false
    }
  }

  const deepClone = <T,>(obj: T): T => {
    if (obj === null || typeof obj !== 'object') return obj
    if (obj instanceof Date) return new Date(obj) as T
    if (Array.isArray(obj)) return obj.map((item) => deepClone(item)) as T

    const cloned = {} as T
    for (const key in obj) {
      if (Object.prototype.hasOwnProperty.call(obj, key)) {
        cloned[key] = deepClone(obj[key])
      }
    }
    return cloned
  }

  const searchDept = (items: SysDept[]): SysDept[] => {
    const results: SysDept[] = []

    for (const item of items) {
      const searchName = searchForm.value.deptName.toLowerCase().trim()
      const searchLeader = searchForm.value.leader.toLowerCase().trim()
      const searchStatus = searchForm.value.status.trim()

      const deptName = (item.deptName || '').toLowerCase()
      const leader = (item.leader || '').toLowerCase()

      const statusMatch = !searchStatus || item.status === searchStatus
      const nameMatch = !searchName || deptName.includes(searchName)
      const leaderMatch = !searchLeader || leader.includes(searchLeader)
      const allMatch = nameMatch && leaderMatch && statusMatch

      if (item.children && item.children.length > 0) {
        const matchedChildren = searchDept(item.children)
        if (matchedChildren.length > 0) {
          const clonedItem = deepClone(item)
          clonedItem.children = matchedChildren
          results.push(clonedItem)
          continue
        }
        if (allMatch) {
          const clonedItem = deepClone(item)
          clonedItem.children = searchDept(item.children)
          results.push(clonedItem)
          continue
        }
      }

      if (allMatch) {
        results.push(deepClone(item))
      }
    }

    return results
  }

  const filteredTableData = computed(() => {
    return searchDept(tableData.value)
  })

  const { columnChecks, columns } = useTableColumns(() => [
    {
      prop: 'deptName',
      label: t('system.dept.deptName'),
      minWidth: 200
    },
    {
      prop: 'orderNum',
      label: t('common.orderNum'),
      width: 100,
      sortable: true
    },
    {
      prop: 'leader',
      label: t('system.dept.leader'),
      width: 120,
      formatter: (row: SysDept) => {
        return row.leader || t('common.empty')
      }
    },
    {
      prop: 'phone',
      label: t('system.dept.phone'),
      width: 150,
      formatter: (row: SysDept) => {
        return row.phone || t('common.empty')
      }
    },
    {
      prop: 'email',
      label: t('system.dept.email'),
      minWidth: 180,
      formatter: (row: SysDept) => {
        return row.email || t('common.empty')
      }
    },
    {
      prop: 'status',
      label: t('common.status'),
      width: 100,
      formatter: (row: SysDept) => {
        const status = row.status || '0'
        const statusMap = {
          '0': { type: 'success' as const, text: t('common.normal') },
          '1': { type: 'danger' as const, text: t('common.disabled') }
        }
        const statusInfo = statusMap[status as keyof typeof statusMap] || statusMap['0']
        return h(ElTag, { type: statusInfo.type }, () => statusInfo.text)
      }
    },
    {
      prop: 'createTime',
      label: t('common.createTime'),
      width: 180,
      formatter: (row: SysDept) => {
        return row.createTime || t('common.empty')
      }
    },
    {
      prop: 'operation',
      label: t('common.operation'),
      width: 180,
      align: 'right',
      fixed: 'right',
      formatter: (row: SysDept) => {
        const buttonStyle = { style: 'text-align: right' }
        const actions: any[] = []

        if (hasAuth('system:dept:add')) {
          actions.push(
            h(ArtButtonTable, {
              type: 'add',
              onClick: () => showDialog('add', row),
              title: t('system.dept.addChild')
            })
          )
        }

        if (hasAuth('system:dept:edit')) {
          actions.push(
            h(ArtButtonTable, {
              type: 'edit',
              onClick: () => showDialog('edit', row)
            })
          )
        }

        if (hasAuth('system:dept:delete')) {
          actions.push(
            h(ArtButtonTable, {
              type: 'delete',
              onClick: () => deleteDept(row)
            })
          )
        }

        if (actions.length === 0) {
          return h('span', { style: 'color: var(--art-gray-500)' }, '')
        }

        return h('div', buttonStyle, actions)
      }
    }
  ])

  onMounted(() => {
    getDeptList()
  })

  const handleSearch = (params: Record<string, any>) => {
    Object.assign(searchForm.value, params)
  }

  const resetSearchParams = () => {
    searchForm.value = {
      deptName: '',
      leader: '',
      status: ''
    }
  }

  const refreshData = () => {
    getDeptList()
  }

  const showDialog = (type: DialogType, row?: SysDept): void => {
    dialogType.value = type
    if (type === 'add' && row) {
      currentDeptData.value = { parentId: row.deptId }
    } else if (type === 'edit' && row) {
      currentDeptData.value = { ...row }
    } else {
      currentDeptData.value = {}
    }
    nextTick(() => {
      dialogVisible.value = true
    })
  }

  const deleteDept = (row: SysDept): void => {
    if (!row.deptId) {
      ElMessage.warning(t('system.dept.idMissing'))
      return
    }

    const hasChildren = row.children && row.children.length > 0
    const confirmMessage = hasChildren
      ? t('system.dept.deleteConfirmWithChildren')
      : t('system.dept.deleteConfirm')

    ElMessageBox.confirm(confirmMessage, t('common.tips'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })
      .then(async () => {
        try {
          await fetchDeleteDept(row.deptId!)
          ElMessage.success(t('common.deleteSuccess'))
          await getDeptList()
        } catch (error) {
          console.error('删除部门失败:', error)
          handleMutationError(error, t('system.dept.deleteFail'))
        }
      })
      .catch(() => {
        // 用户取消删除
      })
  }

  const handleDialogSubmit = async () => {
    try {
      dialogVisible.value = false
      currentDeptData.value = {}
      await getDeptList()
    } catch (error) {
      console.error('提交失败:', error)
    }
  }

  const toggleExpand = (): void => {
    isExpanded.value = !isExpanded.value
    nextTick(() => {
      if (tableRef.value?.elTableRef && filteredTableData.value) {
        const processRows = (rows: SysDept[]) => {
          rows.forEach((row) => {
            if (row.children && row.children.length > 0) {
              tableRef.value.elTableRef.toggleRowExpansion(row, isExpanded.value)
              processRows(row.children)
            }
          })
        }
        processRows(filteredTableData.value)
      }
    })
  }
</script>

<style scoped lang="scss">
  .dept-page {
    padding: var(--art-page-padding);
    background-color: var(--default-bg-color);
  }

  :deep(.art-table-card) {
    border: 1px solid var(--art-card-border);
    border-radius: 12px;
    box-shadow: var(--art-shadow-card);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: var(--art-shadow-card-hover);
    }
  }

  :deep(.el-table) {
    border-radius: 8px;

    .el-table__header-wrapper {
      th {
        font-weight: 600;
        color: var(--art-gray-800);
        background-color: var(--art-gray-100) !important;
      }
    }

    .el-table__body-wrapper {
      tr {
        transition: all 0.2s ease;

        &:hover > td {
          background-color: var(--art-gray-50) !important;
        }
      }
    }
  }

  :deep(.el-button) {
    font-weight: 500;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-1px);
    }
  }

  :deep(.el-tag) {
    font-weight: 500;
    border-radius: 6px;
  }
</style>
