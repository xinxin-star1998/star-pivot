<!--
  在线用户页面
-->
<template>
  <div class="online-user-page art-full-height">
    <ElCollapseTransition>
      <div v-show="showSearchBar">
        <ElCard shadow="never" style="margin-bottom: 12px">
          <ElForm :model="searchForm" :inline="true">
            <ElFormItem :label="t('monitor.online.userName')">
              <ElInput
                v-model="searchForm.userName"
                :placeholder="t('common.pleaseInput')"
                clearable
                style="width: 200px"
              />
            </ElFormItem>
            <ElFormItem :label="t('monitor.online.ipaddr')">
              <ElInput
                v-model="searchForm.ipaddr"
                :placeholder="t('common.pleaseInput')"
                clearable
                style="width: 200px"
              />
            </ElFormItem>
            <ElFormItem>
              <ElButton type="primary" :icon="Search" @click="handleSearch">{{
                t('table.searchBar.search')
              }}</ElButton>
              <ElButton :icon="Refresh" @click="resetSearchParams">{{
                t('common.reset')
              }}</ElButton>
            </ElFormItem>
          </ElForm>
        </ElCard>
      </div>
    </ElCollapseTransition>

    <ElCard class="art-table-card" shadow="never" :style="cardStyle">
      <ArtTableHeader
        v-model:columns="columnChecks"
        v-model:showSearchBar="showSearchBar"
        :loading="loading"
        @refresh="refreshData"
      >
        <template #left>
          <ElSpace wrap>
            <ElButton
              type="danger"
              :disabled="selectedRows.length === 0"
              @click="handleBatchForceLogout"
              v-ripple
              v-auth="'monitor:online:force-logout'"
            >
              {{ t('common.batchDelete') }} {{ t('monitor.online.forceLogout') }}
            </ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <ArtTable
        :loading="loading"
        :data="data"
        :columns="columns"
        @selection-change="handleSelectionChange"
      >
        <template #operation="{ row }">
          <ElButton
            type="danger"
            link
            size="small"
            @click="handleForceLogout(row)"
            v-auth="'monitor:online:force-logout'"
          >
            {{ t('monitor.online.forceLogout') }}
          </ElButton>
        </template>
      </ArtTable>
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { Refresh, Search } from '@element-plus/icons-vue'
  import { fetchForceLogout, fetchGetOnlineUserList } from '@/api/monitor/online'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { useI18n } from 'vue-i18n'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import { usePageVisibility } from '@/hooks/core/usePageVisibility'
  import type { OnlineUser, OnlineUserQueryParams } from '@/types/api/monitor'
  import type { ColumnOption } from '@/types'

  defineOptions({ name: 'OnlineUser' })

  const { t } = useI18n()
  const { onPause, onResume } = usePageVisibility()
  onPause(() => stopAutoRefresh())
  onResume(() => startAutoRefresh())

  const showSearchBar = ref(true)

  const cardStyle = computed(() => ({
    'margin-top': showSearchBar.value ? '12px' : '0'
  }))

  const searchForm = ref<OnlineUserQueryParams>({
    userName: undefined,
    ipaddr: undefined
  })

  const loading = ref(false)
  const data = ref<OnlineUser[]>([])
  const selectedRows = ref<OnlineUser[]>([])

  const columns = computed<ColumnOption[]>(() => [
    { type: 'selection' },
    { type: 'index', width: 60, label: t('table.column.index') },
    { prop: 'userName', label: t('monitor.online.userName'), width: 120 },
    { prop: 'nickName', label: t('monitor.online.userName'), width: 120 },
    { prop: 'deptName', label: t('monitor.online.deptName'), width: 120 },
    { prop: 'ipaddr', label: t('monitor.online.ipaddr'), width: 150 },
    { prop: 'loginLocation', label: t('monitor.online.loginLocation'), width: 150 },
    { prop: 'browser', label: t('monitor.online.browser'), width: 120 },
    { prop: 'os', label: t('monitor.online.os'), width: 120 },
    { prop: 'loginTime', label: t('monitor.online.loginTime'), width: 180 },
    { prop: 'lastAccessTime', label: t('common.createTime'), width: 180 }
  ])

  const columnChecks = ref<ColumnOption[]>([])

  watch(
    columns,
    (value) => {
      columnChecks.value = [...value]
    },
    { immediate: true }
  )

  const getData = async () => {
    loading.value = true
    try {
      const result = await fetchGetOnlineUserList(searchForm.value)
      data.value = result || []
    } catch (error) {
      if (import.meta.env.DEV) {
        console.error('get online users failed:', error)
      }
      data.value = []
    } finally {
      loading.value = false
    }
  }

  const refreshData = () => {
    getData()
  }

  const handleSearch = () => {
    getData()
  }

  const resetSearchParams = () => {
    searchForm.value = {
      userName: undefined,
      ipaddr: undefined
    }
    getData()
  }

  const handleSelectionChange = (rows: OnlineUser[]) => {
    selectedRows.value = rows
  }

  const handleForceLogout = async (row: OnlineUser) => {
    try {
      await ElMessageBox.confirm(t('monitor.online.forceLogoutConfirm'), t('common.tips'), {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      })

      await fetchForceLogout(row.sessionId)
      ElMessage.success(t('monitor.online.forceLogout'))
      getData()
    } catch (error) {
      if (error !== 'cancel' && import.meta.env.DEV) {
        console.error('force logout failed:', error)
      }
    }
  }

  const handleBatchForceLogout = async () => {
    if (selectedRows.value.length === 0) {
      ElMessage.warning(t('common.pleaseSelect'))
      return
    }

    try {
      await ElMessageBox.confirm(t('monitor.online.forceLogoutConfirm'), t('common.tips'), {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      })

      const promises = selectedRows.value.map((row) => fetchForceLogout(row.sessionId))
      await Promise.all(promises)

      ElMessage.success(t('monitor.online.forceLogout'))
      selectedRows.value = []
      getData()
    } catch (error) {
      if (error !== 'cancel' && import.meta.env.DEV) {
        console.error('batch force logout failed:', error)
      }
    }
  }

  let refreshTimer: number | null = null
  const startAutoRefresh = () => {
    if (refreshTimer) {
      clearInterval(refreshTimer)
    }
    refreshTimer = window.setInterval(() => {
      getData()
    }, 10000)
  }

  const stopAutoRefresh = () => {
    if (refreshTimer) {
      clearInterval(refreshTimer)
      refreshTimer = null
    }
  }

  onMounted(() => {
    getData()
    startAutoRefresh()
  })

  onBeforeUnmount(() => {
    stopAutoRefresh()
  })
</script>

<style scoped lang="scss">
  .online-user-page {
    padding: 20px;
    background-color: var(--default-bg-color);
  }

  :deep(.el-card) {
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

  :deep(.el-form-item__label) {
    font-weight: 500;
    color: var(--art-gray-700);
  }

  :deep(.el-input__wrapper) {
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 2px 8px 0 rgb(0 0 0 / 8%);
    }
  }
</style>
