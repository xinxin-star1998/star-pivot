<!-- 字典类型管理页面 -->
<template>
  <div class="dict-type-page art-full-height">
    <!-- 搜索栏 -->
    <ArtSearchBar
      v-model="searchFilters"
      :items="formItems"
      :showExpand="false"
      @reset="handleReset"
      @search="handleSearch"
    />

    <ElCard class="art-table-card" shadow="never">
      <!-- 表格头部 -->
      <ArtTableHeader
        :showZebra="false"
        :loading="loading"
        v-model:columns="columnChecks"
        @refresh="handleRefresh"
      >
        <template #left>
          <ElButton @click="handleAdd" v-ripple v-auth="'system:type:add'">新增字典类型</ElButton>
        </template>
      </ArtTableHeader>

      <ArtTable
        ref="tableRef"
        rowKey="dictId"
        :loading="loading"
        :columns="columns"
        :data="tableData"
        :stripe="false"
        :pagination="pagination"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      />

      <!-- 字典类型弹窗 -->
      <DictTypeDialog v-model:visible="dialogVisible" :editData="editData" @submit="handleSubmit" />
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { safeError } from '@/utils'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import { useTableColumns } from '@/hooks/core/useTableColumns'
  import DictTypeDialog from '@views/system/dict/modules/dict-type-dialog.vue'
  import {
    fetchGetDictTypeList,
    fetchAddDictType,
    fetchUpdateDictType,
    fetchDeleteDictType,
    type SysDictType,
    type DictTypeFormData
  } from '@/api/dict/type'
  import { ElMessage, ElMessageBox, ElTag } from 'element-plus'
  import ArtSearchBar from '@/components/core/forms/art-search-bar/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import { useRouter } from 'vue-router'
  import { useAuth } from '@/hooks/core/useAuth'
  const router = useRouter()

  defineOptions({ name: 'DictType' })

  // 权限控制
  const { hasAuth } = useAuth()

  // 状态管理
  const loading = ref(false)
  const tableRef = ref()

  // 弹窗相关
  const dialogVisible = ref(false)
  const editData = ref<DictTypeFormData | null>(null)

  // 分页相关
  const pagination = reactive({
    current: 1,
    size: 10,
    total: 0
  })

  // 搜索相关
  const searchFilters = reactive({
    dictName: '',
    dictType: '',
    status: ''
  })

  const formItems = computed(() => [
    {
      label: '字典名称',
      key: 'dictName',
      type: 'input',
      props: { clearable: true, placeholder: '请输入字典名称' }
    },
    {
      label: '字典类型',
      key: 'dictType',
      type: 'input',
      props: { clearable: true, placeholder: '请输入字典类型' }
    },
    {
      label: '状态',
      key: 'status',
      type: 'select',
      props: {
        clearable: true,
        placeholder: '请选择状态',
        options: [
          { label: '正常', value: '0' },
          { label: '停用', value: '1' }
        ]
      }
    }
  ])

  // 状态配置
  const STATUS_CONFIG = {
    '0': { text: '正常', type: 'success' as const },
    '1': { text: '停用', type: 'danger' as const }
  }
  // 新增：字典类型点击事件处理函数
  const handleDictTypeClick = (row: SysDictType) => {
    // 跳转到字典数据明细页，使用动态路由参数
    router.push({
      name: 'DictData',
      params: {
        dictType: row.dictType
      }
    })
  }
  // 表格列配置
  const { columnChecks, columns } = useTableColumns(() => [
    {
      type: 'index',
      label: '序号',
      width: 100,
      index: (index: number) => {
        return (pagination.current - 1) * pagination.size + index + 1
      }
    },
    {
      prop: 'dictName',
      label: '字典名称',
      minWidth: 150
    },
    {
      prop: 'dictType',
      label: '字典类型',
      minWidth: 150,
      // 新增：自定义单元格渲染，添加点击事件和样式
      formatter: (row: SysDictType) => {
        return h(
          'span',
          {
            style: {
              color: '#409eff', // 蓝色文字，提示可点击
              cursor: 'pointer' // 鼠标手型
              // textDecoration: 'underline' // 下划线（可选）
            },
            onClick: () => handleDictTypeClick(row) // 绑定点击事件
          },
          row.dictType
        )
      }
    },
    {
      prop: 'status',
      label: '状态',
      width: 100,
      formatter: (row: SysDictType) => {
        const status = (row.status || '0') as keyof typeof STATUS_CONFIG
        const statusInfo = STATUS_CONFIG[status] || STATUS_CONFIG['0']
        return h(ElTag, { type: statusInfo.type }, () => statusInfo.text)
      }
    },
    {
      prop: 'remark',
      label: '备注',
      minWidth: 200,
      formatter: (row: SysDictType) => {
        return row.remark || h('span', { style: 'color: #999' }, '无')
      }
    },
    {
      prop: 'createTime',
      label: '创建时间',
      width: 180,
      formatter: (row: SysDictType) => {
        return row.createTime || h('span', { style: 'color: #999' }, '暂无')
      }
    },
    {
      prop: 'operation',
      label: '操作',
      width: 180,
      align: 'right',
      formatter: (row: SysDictType) => {
        const actions: any[] = []

        // 编辑字典类型按钮权限：system:type:edit
        if (hasAuth('system:type:edit')) {
          actions.push(
            h(ArtButtonTable, {
              type: 'edit',
              onClick: () => handleEdit(row)
            })
          )
        }

        // 删除字典类型按钮权限：system:type:delete
        if (hasAuth('system:type:delete')) {
          actions.push(
            h(ArtButtonTable, {
              type: 'delete',
              onClick: () => handleDelete(row)
            })
          )
        }

        if (actions.length === 0) {
          // 无任何操作权限时返回空占位
          return h('span', { style: 'color: #999' }, '')
        }

        return h('div', { style: 'text-align: right' }, actions)
      }
    }
  ])

  // 数据相关
  const tableData = ref<SysDictType[]>([])

  onMounted(() => {
    getDictTypeList()
  })

  /**
   * 获取字典类型列表
   */
  const getDictTypeList = async (): Promise<void> => {
    loading.value = true
    try {
      const params: any = {
        pageNum: pagination.current,
        pageSize: pagination.size
      }
      if (searchFilters.dictName) params.dictName = searchFilters.dictName
      if (searchFilters.dictType) params.dictType = searchFilters.dictType
      if (searchFilters.status) params.status = searchFilters.status

      const result = await fetchGetDictTypeList(params)
      // 后端返回 PageResponse 结构，从 rows 字段获取数据列表
      tableData.value = result?.rows || []
      // 更新分页信息
      if (result) {
        pagination.total = result.total || 0
      }
    } catch (error) {
      safeError('获取字典类型列表失败:', error)
      ElMessage.error('获取字典类型列表失败')
    } finally {
      loading.value = false
    }
  }

  /**
   * 重置搜索条件
   */
  const handleReset = (): void => {
    Object.assign(searchFilters, {
      dictName: '',
      dictType: '',
      status: ''
    })
    pagination.current = 1
    getDictTypeList()
  }

  /**
   * 执行搜索
   */
  const handleSearch = (): void => {
    pagination.current = 1
    getDictTypeList()
  }

  /**
   * 分页大小改变
   */
  const handleSizeChange = (size: number): void => {
    pagination.size = size
    pagination.current = 1
    getDictTypeList()
  }

  /**
   * 当前页改变
   */
  const handleCurrentChange = (current: number): void => {
    pagination.current = current
    getDictTypeList()
  }

  /**
   * 刷新字典类型列表
   */
  const handleRefresh = (): void => {
    getDictTypeList()
  }

  /**
   * 新增字典类型
   */
  const handleAdd = (): void => {
    editData.value = null
    dialogVisible.value = true
  }

  /**
   * 编辑字典类型
   */
  const handleEdit = (row: SysDictType): void => {
    editData.value = {
      dictId: row.dictId,
      dictName: row.dictName,
      dictType: row.dictType,
      status: row.status || '0',
      remark: row.remark || ''
    }
    dialogVisible.value = true
  }

  /**
   * 删除字典类型
   */
  const handleDelete = async (row: SysDictType): Promise<void> => {
    if (!row.dictId) {
      ElMessage.warning('字典类型ID不存在，无法删除')
      return
    }

    try {
      await ElMessageBox.confirm(`确定要删除字典类型"${row.dictName}"吗？删除后无法恢复`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })

      await fetchDeleteDictType([row.dictId])
      ElMessage.success('删除成功')
      // 如果当前页没有数据了，且不是第一页，则跳转到上一页
      if (tableData.value.length === 1 && pagination.current > 1) {
        pagination.current--
      }
      await getDictTypeList()
    } catch (error) {
      if (error !== 'cancel') {
        safeError('删除字典类型失败:', error)
        ElMessage.error('删除失败')
      }
    }
  }

  /**
   * 提交表单数据
   */
  const handleSubmit = async (formData: DictTypeFormData): Promise<void> => {
    try {
      const isEdit = !!formData.dictId
      if (isEdit) {
        await fetchUpdateDictType(formData)
        ElMessage.success('修改字典类型成功')
      } else {
        await fetchAddDictType(formData)
        ElMessage.success('新增字典类型成功')
      }
      dialogVisible.value = false
      await getDictTypeList()
    } catch (error) {
      safeError('保存字典类型失败:', error)
      ElMessage.error(formData.dictId ? '修改字典类型失败' : '新增字典类型失败')
    }
  }
</script>

<style scoped lang="scss">
  .dict-type-page {
    padding: 16px;
    background-color: var(--default-bg-color);
  }

  :deep(.art-table-card) {
    border: 1px solid var(--art-card-border);
    border-radius: 12px;
    box-shadow: 0 2px 12px 0 rgb(0 0 0 / 8%);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 16px 0 rgb(0 0 0 / 12%);
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
