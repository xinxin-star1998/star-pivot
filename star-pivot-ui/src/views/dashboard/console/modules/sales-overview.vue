<template>
  <div class="art-card h-105 p-5 mb-5 max-sm:mb-4">
    <div class="art-card-header">
      <div class="title">
        <h4>访问量</h4>
        <p>今年增长<span class="text-success">+15%</span></p>
      </div>
    </div>
    <ArtLineChart
      height="calc(100% - 56px)"
      :data="data"
      :xAxisData="xAxisData"
      :showAreaColor="true"
      :showAxisLine="false"
    />
  </div>
</template>

<script setup lang="ts">
  import type { DashboardTrendData } from '@/types/api/dashboard'

  const props = withDefaults(
    defineProps<{
      trendData: DashboardTrendData
    }>(),
    {
      trendData: () => ({ xAxisData: [], data: [] })
    }
  )

  const data = computed(() => props.trendData.data)
  const xAxisData = computed(() => props.trendData.xAxisData)
</script>
