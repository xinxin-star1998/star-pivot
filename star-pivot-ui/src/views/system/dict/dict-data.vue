<!-- 字典数据管理页面 -->
<template>
  <div class="dict-data-page art-full-height">
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
          <ElButton @click="goBack" v-ripple plain> 返回 </ElButton>
          <ElButton
            @click="handleAdd"
            v-ripple
            v-auth="'system:data:add'"
            :disabled="!selectedDictType"
          >
            新增字典数据
          </ElButton>
        </template>
      </ArtTableHeader>

      <ArtTable
        ref="tableRef"
        rowKey="dictCode"
        :loading="loading"
        :columns="columns"
        :data="tableData"
        :stripe="false"
        :pagination="pagination"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      />

      <!-- 字典数据弹窗 -->
      <DictDataDialog
        v-model:visible="dialogVisible"
        :editData="editData"
        :dictType="selectedDictType"
        @submit="handleSubmit"
      />
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { safeError } from '@/utils'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import { useTableColumns } from '@/hooks/core/useTableColumns'
  import DictDataDialog from './modules/dict-data-dialog.vue'
  import {
    fetchGetDictDataList,
    fetchAddDictData,
    fetchUpdateDictData,
    fetchDeleteDictData,
    type SysDictData,
    type DictDataFormData
  } from '@/api/dict/data'
  import { fetchGetDictTypeList, type SysDictType } from '@/api/dict/type'
  import { ElMessage, ElMessageBox, ElTag } from 'element-plus'
  import ArtSearchBar from '@/components/core/forms/art-search-bar/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import { useRoute, useRouter } from 'vue-router'
  import { useAuth } from '@/hooks/core/useAuth'

  defineOptions({ name: 'DictData' })

  // 权限控制
  const { hasAuth } = useAuth()

  // 路由相关
  const route = useRoute()
  const router = useRouter()

  // 状态管理
  const loading = ref(false)
  const tableRef = ref()

  // 弹窗相关
  const dialogVisible = ref(false)
  const editData = ref<DictDataFormData | null>(null)

  // 分页相关
  const pagination = reactive({
    current: 1,
    size: 10,
    total: 0
  })

  // 字典类型列表
  const dictTypeOptions = ref<Array<{ label: string; value: string }>>([])
  const selectedDictType = ref<string>('')

  // 搜索相关
  const searchFilters = reactive({
    dictLabel: '',
    dictType: '',
    status: ''
  })

  const formItems = computed(() => [
    {
      label: '字典类型',
      key: 'dictType',
      type: 'select',
      props: {
        clearable: true,
        placeholder: '请选择字典类型',
        filterable: true
      },
      options: dictTypeOptions.value
    },
    {
      label: '字典标签',
      key: 'dictLabel',
      type: 'input',
      props: { clearable: true, placeholder: '请输入字典标签' }
    },
    {
      label: '状态',
      key: 'status',
      type: 'select',
      props: {
        clearable: true,
        placeholder: '请选择状态'
      },
      options: [
        { label: '正常', value: '0' },
        { label: '停用', value: '1' }
      ]
    }
  ])

  // 状态配置
  const STATUS_CONFIG = {
    '0': { text: '正常', type: 'success' as const },
    '1': { text: '停用', type: 'danger' as const }
  }

  // 表格列配置
  const { columnChecks, columns } = useTableColumns(() => [
    { type: 'index', width: 60, label: '序号' },
    {
      prop: 'dictCode',
      label: '字典编码',
      width: 80
    },
    {
      prop: 'dictLabel',
      label: '字典标签',
      minWidth: 120
    },
    {
      prop: 'dictValue',
      label: '字典键值',
      minWidth: 120
    },
    {
      prop: 'dictType',
      label: '字典类型',
      minWidth: 150
    },
    {
      prop: 'dictSort',
      label: '字典排序',
      width: 100,
      formatter: (row: SysDictData) => {
        return row.dictSort || 0
      }
    },
    {
      prop: 'status',
      label: '状态',
      width: 100,
      formatter: (row: SysDictData) => {
        const status = (row.status || '0') as keyof typeof STATUS_CONFIG
        const statusInfo = STATUS_CONFIG[status] || STATUS_CONFIG['0']
        return h(ElTag, { type: statusInfo.type }, () => statusInfo.text)
      }
    },
    {
      prop: 'remark',
      label: '备注',
      minWidth: 150,
      formatter: (row: SysDictData) => {
        return row.remark || h('span', { style: 'color: #999' }, '无')
      }
    },
    {
      prop: 'createTime',
      label: '创建时间',
      width: 180,
      formatter: (row: SysDictData) => {
        return row.createTime || h('span', { style: 'color: #999' }, '暂无')
      }
    },
    {
      prop: 'operation',
      label: '操作',
      width: 180,
      align: 'right',
      formatter: (row: SysDictData) => {
        const actions: any[] = []

        // 编辑字典数据按钮权限：system:data:edit
        if (hasAuth('system:data:edit')) {
          actions.push(
            h(ArtButtonTable, {
              type: 'edit',
              onClick: () => handleEdit(row)
            })
          )
        }

        // 删除字典数据按钮权限：system:data:delete
        if (hasAuth('system:data:delete')) {
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
  const tableData = ref<SysDictData[]>([])

  onMounted(() => {
    // 从路由路径参数获取字典类型
    const routeDictType = route.params.dictType as string
    if (routeDictType) {
      searchFilters.dictType = routeDictType
      selectedDictType.value = routeDictType
    }

    loadDictTypes()
    getDictDataList()
  })

  /**
   * 加载字典类型列表
   */
  const loadDictTypes = async (): Promise<void> => {
    try {
      // 使用分页接口，设置较大的pageSize以获取所有字典类型
      const result = await fetchGetDictTypeList({
        pageNum: 1,
        pageSize: 1000
      })
      // 后端返回 PageResponse 结构，从 rows 字段获取数据列表
      const list = result?.rows || []
      dictTypeOptions.value = list.map((item: SysDictType) => ({
        label: `${item.dictName}(${item.dictType})`,
        value: item.dictType
      }))
    } catch (error) {
      safeError('加载字典类型列表失败:', error)
      dictTypeOptions.value = []
    }
  }

  /**
   * 获取字典数据列表
   */
  const getDictDataList = async (): Promise<void> => {
    loading.value = true
    try {
      const params: any = {
        pageNum: pagination.current,
        pageSize: pagination.size
      }
      if (searchFilters.dictLabel) params.dictLabel = searchFilters.dictLabel
      if (searchFilters.dictType) {
        params.dictType = searchFilters.dictType
        selectedDictType.value = searchFilters.dictType
      }
      if (searchFilters.status) params.status = searchFilters.status

      const result = await fetchGetDictDataList(params)
      // 后端返回 PageResponse 结构，从 rows 字段获取数据列表
      tableData.value = result?.rows || []
      // 更新分页信息
      if (result) {
        pagination.total = result.total || 0
      }
    } catch (error) {
      safeError('获取字典数据列表失败:', error)
      ElMessage.error('获取字典数据列表失败')
    } finally {
      loading.value = false
    }
  }

  /**
   * 重置搜索条件
   */
  const handleReset = (): void => {
    Object.assign(searchFilters, {
      dictLabel: '',
      dictType: '',
      status: ''
    })
    selectedDictType.value = ''
    pagination.current = 1
    getDictDataList()
  }

  /**
   * 执行搜索
   */
  const handleSearch = (): void => {
    if (searchFilters.dictType) {
      selectedDictType.value = searchFilters.dictType
    }
    pagination.current = 1
    getDictDataList()
  }

  /**
   * 分页大小改变
   */
  const handleSizeChange = (size: number): void => {
    pagination.size = size
    pagination.current = 1
    getDictDataList()
  }

  /**
   * 当前页改变
   */
  const handleCurrentChange = (current: number): void => {
    pagination.current = current
    getDictDataList()
  }

  /**
   * 刷新字典数据列表
   */
  const handleRefresh = (): void => {
    getDictDataList()
  }

  /**
   * 新增字典数据
   */
  const handleAdd = (): void => {
    if (!selectedDictType.value) {
      ElMessage.warning('请先选择字典类型')
      return
    }
    editData.value = null
    dialogVisible.value = true
  }

  /**
   * 编辑字典数据
   */
  const handleEdit = (row: SysDictData): void => {
    editData.value = {
      dictCode: row.dictCode,
      dictSort: row.dictSort || 0,
      dictLabel: row.dictLabel,
      dictValue: row.dictValue,
      dictType: row.dictType,
      cssClass: row.cssClass || '',
      listClass: row.listClass || '',
      isDefault: row.isDefault || 'N',
      status: row.status || '0',
      remark: row.remark || ''
    }
    selectedDictType.value = row.dictType
    dialogVisible.value = true
  }

  /**
   * 删除字典数据
   */
  const handleDelete = async (row: SysDictData): Promise<void> => {
    if (!row.dictCode) {
      ElMessage.warning('字典数据ID不存在，无法删除')
      return
    }

    try {
      await ElMessageBox.confirm(`确定要删除字典数据"${row.dictLabel}"吗？删除后无法恢复`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })

      await fetchDeleteDictData([row.dictCode])
      ElMessage.success('删除成功')
      // 如果当前页没有数据了，且不是第一页，则跳转到上一页
      if (tableData.value.length === 1 && pagination.current > 1) {
        pagination.current--
      }
      await getDictDataList()
    } catch (error) {
      if (error !== 'cancel') {
        safeError('删除字典数据失败:', error)
        ElMessage.error('删除失败')
      }
    }
  }

  /**
   * 提交表单数据
   */
  const handleSubmit = async (formData: DictDataFormData): Promise<void> => {
    try {
      const isEdit = !!formData.dictCode
      if (isEdit) {
        await fetchUpdateDictData(formData)
        ElMessage.success('修改字典数据成功')
      } else {
        await fetchAddDictData(formData)
        ElMessage.success('新增字典数据成功')
      }
      dialogVisible.value = false
      await getDictDataList()
    } catch (error) {
      safeError('保存字典数据失败:', error)
      ElMessage.error(formData.dictCode ? '修改字典数据失败' : '新增字典数据失败')
    }
  }

  /**
   * 返回字典类型页面
   */
  const goBack = () => {
    // 直接跳转到字典管理页面，更可靠
    router.push({ path: '/system/dict' })
  }
</script>

<style scoped lang="scss">
  .dict-data-page {
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
