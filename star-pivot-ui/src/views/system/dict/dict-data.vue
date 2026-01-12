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
          <ElButton @click="handleAdd" v-ripple :disabled="!selectedDictType">
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

  defineOptions({ name: 'DictData' })

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
    {
      prop: 'dictLabel',
      label: '序号',
      width: 80,
      formatter: (_, __, index: number) => {
        return index + 1
      }
    },
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
        const buttonStyle = { style: 'text-align: right' }
        return h('div', buttonStyle, [
          h(ArtButtonTable, {
            type: 'edit',
            onClick: () => handleEdit(row)
          }),
          h(ArtButtonTable, {
            type: 'delete',
            onClick: () => handleDelete(row)
          })
        ])
      }
    }
  ])

  // 数据相关
  const tableData = ref<SysDictData[]>([])

  onMounted(() => {
    // 从路由参数获取字典类型
    const routeDictType = route.query.dictType as string
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
    router.go(-1) // 返回上一页
  }
</script>

<style scoped lang="scss">
  .dict-data-page {
    padding: 20px;
  }
</style>
