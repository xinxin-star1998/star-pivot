<!-- 商城-商品管理 -->
<template>
  <div class="mall-product-page art-full-height">
    <ProductSearch v-model="searchForm" @search="handleSearch" @reset="resetSearchParams" />

    <ElCard class="art-table-card" shadow="never" style="margin-top: 12px">
      <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
        <template #left>
          <ElSpace wrap>
            <ElButton type="primary" @click="showDialog('add')" v-ripple>新增商品</ElButton>
            <ElButton
              type="danger"
              :disabled="selectedRows.length === 0"
              @click="handleBatchDelete"
              v-ripple
            >
              批量删除
            </ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <ArtTable
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        @selection-change="handleSelectionChange"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      />
    </ElCard>

    <ProductDialog
      v-model:visible="dialogVisible"
      :type="dialogType"
      :product-data="currentProduct"
      @submit="handleDialogSubmit"
    />
  </div>
</template>

<script setup lang="ts">
  import { h } from 'vue'
  import { useTable } from '@/hooks/core/useTable'
  import {
    fetchMallProductList,
    fetchMallProductRemove,
    type MallProductVo
  } from '@/api/mall/product'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ProductSearch from './modules/product-search.vue'
  import ProductDialog from './modules/product-dialog.vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { ElTag } from 'element-plus'
  import type { DialogType } from '@/types'

  defineOptions({ name: 'MallProduct' })

  const searchForm = ref({
    name: undefined as string | undefined,
    categoryId: undefined as number | undefined,
    brandId: undefined as number | undefined,
    status: undefined as number | undefined
  })

  const dialogType = ref<DialogType>('add')
  const dialogVisible = ref(false)
  const currentProduct = ref<Partial<MallProductVo>>({})
  const selectedRows = ref<MallProductVo[]>([])

  const formatPrice = (row: MallProductVo) => {
    if (row.price === null || row.price === undefined || row.price === '') return '-'
    const n = Number(row.price)
    return Number.isFinite(n) ? n.toFixed(2) : String(row.price)
  }

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
    core: {
      apiFn: fetchMallProductList,
      apiParams: {
        pageNum: 1,
        pageSize: 10,
        ...searchForm.value
      },
      columnsFactory: () => [
        { type: 'selection' },
        {
          type: 'index',
          label: '序号',
          width: 70,
          index: (index: number) => (pagination.current - 1) * pagination.size + index + 1
        },
        // { prop: 'id', label: 'ID', width: 80 },
        { prop: 'name', label: '商品名称', minWidth: 160, showOverflowTooltip: true },
        { prop: 'categoryId', label: '分类ID', width: 90 },
        { prop: 'brandId', label: '品牌ID', width: 90 },
        {
          prop: 'price',
          label: '价格',
          width: 100,
          formatter: (row: MallProductVo) => h('span', {}, () => formatPrice(row))
        },
        { prop: 'stock', label: '库存', width: 90 },
        {
          prop: 'status',
          label: '状态',
          width: 90,
          formatter: (row: MallProductVo) => {
            const s = row.status
            if (s === undefined || s === null) {
              return h('span', {}, () => '-')
            }
            const on = s === 0
            return h(ElTag, { type: on ? 'success' : 'info' }, () => (on ? '上架' : '下架'))
          }
        },
        { prop: 'createTime', label: '创建时间', width: 170 },
        {
          prop: 'operation',
          label: '操作',
          width: 160,
          fixed: 'right',
          formatter: (row: MallProductVo) => {
            return h('div', [
              h(ArtButtonTable, {
                type: 'edit',
                onClick: () => showDialog('edit', row)
              }),
              h(ArtButtonTable, {
                type: 'delete',
                onClick: () => deleteOne(row)
              })
            ])
          }
        }
      ]
    }
  })

  const handleSearch = (params: Record<string, unknown>) => {
    Object.assign(searchParams, params)
    getData()
  }

  const showDialog = (type: DialogType, row?: MallProductVo) => {
    dialogType.value = type
    currentProduct.value = row ? { ...row } : {}
    nextTick(() => {
      dialogVisible.value = true
    })
  }

  const handleDialogSubmit = () => {
    currentProduct.value = {}
    refreshData()
  }

  const handleSelectionChange = (selection: MallProductVo[]) => {
    selectedRows.value = selection
  }

  const deleteOne = (row: MallProductVo) => {
    if (!row.id) return
    ElMessageBox.confirm(`确定删除商品「${row.name || row.id}」吗？`, '删除商品', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        await fetchMallProductRemove([row.id!])
        refreshData()
      })
      .catch(() => {})
  }

  const handleBatchDelete = () => {
    if (selectedRows.value.length === 0) {
      ElMessage.warning('请选择要删除的商品')
      return
    }
    const names = selectedRows.value.map((r) => r.name || r.id).join('、')
    ElMessageBox.confirm(`确定删除以下商品吗？\n${names}`, '批量删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        const ids = selectedRows.value.map((r) => r.id!).filter(Boolean)
        await fetchMallProductRemove(ids)
        selectedRows.value = []
        refreshData()
      })
      .catch(() => {})
  }
</script>

<style scoped lang="scss">
  .mall-product-page {
    padding: var(--art-page-padding);
    background-color: var(--default-bg-color);
  }

  :deep(.art-table-card) {
    border: 1px solid var(--art-card-border);
    border-radius: 12px;
    box-shadow: var(--art-shadow-card);
  }
</style>
