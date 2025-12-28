<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const hideLayout = computed(() => route.meta?.hideLayout)
</script>

<template>
  <div class="app-container">
    <div v-if="hideLayout" class="full-view">
      <router-view />
    </div>
    <div v-else class="layout-view">
      <router-view />
    </div>
  </div>
</template>

<style scoped>
.app-container {
  width: 100%;
  height: 100%;
}

.full-view {
  width: 100vw;
  height: 100vh;
}

.layout-view {
  width: 100%;
  height: 100%;
}
</style>