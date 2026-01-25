<!-- 慢SQL分析页面 -->
<template>
  <div class="slow-sql-page art-full-height">
    <ElCard class="art-table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>慢SQL分析</span>
          <div>
            <ElButton type="primary" :icon="Refresh" @click="handleExtract" :loading="extracting">
              从Druid提取
            </ElButton>
            <ElButton :icon="Refresh" @click="getSlowSqlList" :loading="loading">
              刷新
            </ElButton>
          </div>
        </div>
      </template>

      <ElTable v-loading="loading" :data="slowSqlList" border>
        <ElTableColumn prop="sqlId" label="SQL ID" width="100" />
        <ElTableColumn prop="sqlText" label="SQL语句" min-width="300" show-overflow-tooltip>
          <template #default="{ row }">
            <ElTooltip :content="row.sqlText" placement="top" :show-after="300">
              <div class="sql-text">{{ row.sqlText }}</div>
            </ElTooltip>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="executeCount" label="执行次数" width="100" />
        <ElTableColumn prop="executeTimeAvg" label="平均执行时间(ms)" width="150" sortable>
          <template #default="{ row }">
            <span :class="getTimeClass(row.executeTimeAvg)">
              {{ formatNumber(row.executeTimeAvg) }}
            </span>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="executeTimeMax" label="最大执行时间(ms)" width="150" />
        <ElTableColumn prop="slowCount" label="慢SQL次数" width="120">
          <template #default="{ row }">
            <ElTag type="warning">{{ row.slowCount }}</ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="errorCount" label="错误次数" width="100">
          <template #default="{ row }">
            <ElTag v-if="row.errorCount > 0" type="danger">{{ row.errorCount }}</ElTag>
            <span v-else>0</span>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="lastExecuteTime" label="最后执行时间" width="180" />
        <ElTableColumn label="状态" width="100">
          <template #default="{ row }">
            <ElTag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <ElButton link type="primary" @click="handleViewDetail(row)">
              查看详情
            </ElButton>
            <ElDropdown @command="(cmd) => handleStatusChange(row, cmd)" v-auth="'monitor:sql:edit'">
              <ElButton link type="primary">
                状态 <ElIcon><ArrowDown /></ElIcon>
              </ElButton>
              <template #dropdown>
                <ElDropdownMenu>
                  <ElDropdownItem command="0">待优化</ElDropdownItem>
                  <ElDropdownItem command="1">已优化</ElDropdownItem>
                  <ElDropdownItem command="2">已忽略</ElDropdownItem>
                </ElDropdownMenu>
              </template>
            </ElDropdown>
          </template>
        </ElTableColumn>
      </ElTable>
    </ElCard>

    <!-- 详情对话框 -->
    <ElDialog v-model="detailVisible" title="慢SQL详情" width="800px">
      <ElDescriptions :column="2" border v-if="currentSql">
        <ElDescriptionsItem label="SQL ID">{{ currentSql.sqlId }}</ElDescriptionsItem>
        <ElDescriptionsItem label="执行次数">{{ currentSql.executeCount }}</ElDescriptionsItem>
        <ElDescriptionsItem label="平均执行时间">{{ formatNumber(currentSql.executeTimeAvg) }} ms</ElDescriptionsItem>
        <ElDescriptionsItem label="最大执行时间">{{ currentSql.executeTimeMax }} ms</ElDescriptionsItem>
        <ElDescriptionsItem label="慢SQL次数">
          <ElTag type="warning">{{ currentSql.slowCount }}</ElTag>
        </ElDescriptionsItem>
        <ElDescriptionsItem label="错误次数">
          <ElTag v-if="currentSql.errorCount > 0" type="danger">{{ currentSql.errorCount }}</ElTag>
          <span v-else>0</span>
        </ElDescriptionsItem>
        <ElDescriptionsItem label="最后执行时间" :span="2">
          {{ currentSql.lastExecuteTime || '-' }}
        </ElDescriptionsItem>
        <ElDescriptionsItem label="SQL语句" :span="2">
          <ElInput
            v-model="currentSql.sqlText"
            type="textarea"
            :rows="5"
            readonly
            style="font-family: monospace"
          />
        </ElDescriptionsItem>
        <ElDescriptionsItem label="优化建议" :span="2">
          <ElInput
            v-model="currentSql.optimizationSuggestion"
            type="textarea"
            :rows="4"
            readonly
          />
        </ElDescriptionsItem>
      </ElDescriptions>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  import { Refresh, ArrowDown } from '@element-plus/icons-vue'
  import { fetchGetSlowSqlList, fetchExtractSlowSqlList, fetchUpdateSlowSqlStatus } from '@/api/monitor/sql'
  import type { SlowSql } from '@/types/api/monitor'
  import { ElMessage, ElMessageBox } from 'element-plus'

  defineOptions({ name: 'SlowSql' })

  const loading = ref(false)
  const extracting = ref(false)
  const slowSqlList = ref<SlowSql[]>([])
  const detailVisible = ref(false)
  const currentSql = ref<SlowSql | null>(null)

  // 格式化数字
  const formatNumber = (value: number) => {
    if (value == null || isNaN(value)) return '-'
    return value.toFixed(2)
  }

  // 获取时间样式类
  const getTimeClass = (time: number) => {
    if (time >= 10000) return 'text-danger'
    if (time >= 5000) return 'text-warning'
    return ''
  }

  // 获取状态文本
  const getStatusText = (status: string) => {
    const map: Record<string, string> = { '0': '待优化', '1': '已优化', '2': '已忽略' }
    return map[status] || '-'
  }

  // 获取状态类型
  const getStatusType = (status: string) => {
    const map: Record<string, 'info' | 'success' | 'warning'> = {
      '0': 'warning',
      '1': 'success',
      '2': 'info'
    }
    return map[status] || 'info'
  }

  // 获取慢SQL列表
  const getSlowSqlList = async () => {
    loading.value = true
    try {
      const data = await fetchGetSlowSqlList(100)
      slowSqlList.value = data
    } catch (error) {
      if (import.meta.env.DEV) {
        console.error('获取慢SQL列表失败:', error)
      }
    } finally {
      loading.value = false
    }
  }

  // 从Druid提取
  const handleExtract = async () => {
    try {
      await ElMessageBox.confirm('确定要从Druid提取慢SQL吗？', '提示', {
        type: 'info'
      })
      extracting.value = true
      try {
        const data = await fetchExtractSlowSqlList(5000)
        ElMessage.success(`成功提取 ${data.length} 条慢SQL`)
        getSlowSqlList()
      } catch (error) {
        if (import.meta.env.DEV) {
          console.error('提取慢SQL失败:', error)
        }
      } finally {
        extracting.value = false
      }
    } catch (error) {
      // 用户取消
    }
  }

  // 查看详情
  const handleViewDetail = (row: SlowSql) => {
    currentSql.value = { ...row }
    detailVisible.value = true
  }

  // 状态变更
  const handleStatusChange = async (row: SlowSql, status: string) => {
    try {
      await fetchUpdateSlowSqlStatus(row.id!, status)
      ElMessage.success('状态更新成功')
      getSlowSqlList()
    } catch (error) {
      if (import.meta.env.DEV) {
        console.error('更新慢SQL状态失败:', error)
      }
    }
  }

  onMounted(() => {
    getSlowSqlList()
  })
</script>

<style scoped lang="scss">
  .slow-sql-page {
    padding: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .sql-text {
    font-family: monospace;
    font-size: 12px;
    max-width: 300px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .text-warning {
    color: var(--el-color-warning);
  }

  .text-danger {
    color: var(--el-color-danger);
  }
</style>
