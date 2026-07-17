<template>
  <ElConfigProvider size="default" :locale="elementLocale" :z-index="3000">
    <RouterView></RouterView>
  </ElConfigProvider>
</template>

<script setup lang="ts">
  import { computed, onBeforeMount, onMounted } from 'vue'
  import { storeToRefs } from 'pinia'
  import { useUserStore } from './store/modules/user'
  import { resolveElementLocale } from './locales'
  import { systemUpgrade } from './utils/sys'
  import { toggleTransition } from './utils/ui/animation'
  import { checkStorageCompatibility } from './utils/storage'
  import { initializeTheme } from './hooks/core/useTheme'

  const userStore = useUserStore()
  const { language } = storeToRefs(userStore)

  const elementLocale = computed(() => resolveElementLocale(language.value))

  onBeforeMount(() => {
    toggleTransition(true)
    initializeTheme()
  })

  onMounted(() => {
    checkStorageCompatibility()
    toggleTransition(false)
    systemUpgrade()
  })
</script>
