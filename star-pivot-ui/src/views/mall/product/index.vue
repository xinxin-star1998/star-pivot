<!-- 商城-SPU 管理（pms_spu_info） -->
<template>
  <div class="mall-product-page art-full-height">
    <ProductSearch v-model="searchForm" @search="handleSearch" @reset="resetSearchParams" />

    <ElCard class="art-table-card" shadow="never" style="margin-top: 12px">
      <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
        <template #left>
          <ElSpace wrap>
            <ElButton v-auth="'mall:product:add'" type="primary" @click="showDialog('add')" v-ripple>
              新增 SPU
            </ElButton>
            <ElButton
              v-auth="'mall:product:delete'"
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
  import { useAuth } from '@/hooks/core/useAuth'

  defineOptions({ name: 'MallProduct' })

  const { hasAuth } = useAuth()

  const searchForm = ref({
    spuName: undefined as string | undefined,
    catalogId: undefined as number | undefined,
    brandId: undefined as number | undefined,
    publishStatus: undefined as number | undefined
  })

  const dialogType = ref<DialogType>('add')
  const dialogVisible = ref(false)
  const currentProduct = ref<Partial<MallProductVo>>({})
  const selectedRows = ref<MallProductVo[]>([])

  const formatWeight = (row: MallProductVo) => {
    const w = row.weight
    if (w === null || w === undefined || w === '') return '-'
    const n = Number(w)
    return Number.isFinite(n) ? String(n) : String(w)
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
        { prop: 'spuName', label: 'SPU 名称', minWidth: 160, showOverflowTooltip: true },
        {
          prop: 'spuDescription',
          label: '描述',
          minWidth: 180,
          showOverflowTooltip: true
        },
        { prop: 'catalogId', label: '分类 ID', width: 100 },
        { prop: 'brandId', label: '品牌 ID', width: 100 },
        {
          prop: 'weight',
          label: '重量',
          width: 100,
          formatter: (row: MallProductVo) => h('span', {}, () => formatWeight(row))
        },
        {
          prop: 'publishStatus',
          label: '上架状态',
          width: 110,
          formatter: (row: MallProductVo) => {
            const s = row.publishStatus
            if (s === undefined || s === null) {
              return h('span', {}, () => '-')
            }
            const on = s === 1
            return h(ElTag, { type: on ? 'success' : 'info' }, () => (on ? '上架' : '下架'))
          }
        },
        { prop: 'createTime', label: '创建时间', width: 170 },
        { prop: 'updateTime', label: '更新时间', width: 170 },
        {
          prop: 'operation',
          label: '操作',
          width: 160,
          fixed: 'right',
          formatter: (row: MallProductVo) => {
            const actions: ReturnType<typeof h>[] = []
            if (hasAuth('mall:product:edit')) {
              actions.push(
                h(ArtButtonTable, {
                  label: '编辑',
                  type: 'edit',
                  onClick: () => showDialog('edit', row)
                })
              )
            }
            if (hasAuth('mall:product:delete')) {
              actions.push(
                h(ArtButtonTable, {
                  label: '删除',
                  type: 'delete',
                  onClick: () => deleteOne(row)
                })
              )
            }
            if (actions.length === 0) {
              return h('span', { style: 'color: #999' }, '')
            }
            return h('div', actions)
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
    ElMessageBox.confirm(`确定删除 SPU「${row.spuName || row.id}」吗？`, '删除 SPU', {
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
      ElMessage.warning('请选择要删除的 SPU')
      return
    }
    const names = selectedRows.value.map((r) => r.spuName || r.id).join('、')
    ElMessageBox.confirm(`确定删除以下 SPU 吗？\n${names}`, '批量删除', {
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
