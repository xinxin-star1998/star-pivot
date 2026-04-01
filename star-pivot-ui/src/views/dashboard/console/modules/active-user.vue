<template>
  <div class="art-card h-105 p-4 box-border mb-5 max-sm:mb-4">
    <ArtBarChart
      class="box-border p-2"
      barWidth="50%"
      height="13.7rem"
      :showAxisLine="false"
      :data="chartData"
      :xAxisData="xAxisLabels"
    />
    <div class="ml-1">
      <h3 class="mt-5 text-lg font-medium">用户概述</h3>
      <p class="mt-1 text-sm">近 12 个月新增用户趋势</p>
      <p class="mt-1 text-sm">我们为您创建了多个选项，可将它们组合在一起并定制为像素完美的页面</p>
    </div>
    <div class="flex-b mt-2">
      <div class="flex-1" v-for="(item, index) in list" :key="index">
        <p class="text-2xl text-g-900">{{ item.num }}</p>
        <p class="text-xs text-g-500">{{ item.name }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type {DashboardTrendData} from '@/types/api/dashboard'

  interface UserStatItem {
    name: string
    num: string
  }

const props = withDefaults(
    defineProps<{
      trendData: DashboardTrendData
    }>(),
    {
      trendData: () => ({xAxisData: [], data: []})
    }
)

const xAxisLabels = computed(() => props.trendData.xAxisData)
const chartData = computed(() => props.trendData.data)

  /**
   * 用户统计数据列表
   * 包含总用户量、总访问量、日访问量和周同比等关键指标
   */
  const list: UserStatItem[] = [
    {name: '统计维度', num: '新增用户'},
    {name: '周期', num: '近12个月'},
    {name: '数据来源', num: 'sys_user'},
    {name: '状态', num: '实时'}
  ]
</script>
