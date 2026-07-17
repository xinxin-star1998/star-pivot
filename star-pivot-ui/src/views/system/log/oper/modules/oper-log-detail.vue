<!-- 操作日志详情抽屉 -->
<template>
  <ElDrawer
    v-model="dialogVisible"
    :title="t('system.operLog.detail')"
    direction="rtl"
    :close-on-click-modal="false"
    :close-on-press-escape="true"
    :size="drawerSize"
    class="oper-log-detail-drawer"
    :loading="isLoading"
    @update:model-value="handleDrawerClose"
    @open="handleDrawerOpen"
    @opened="handleDrawerOpened"
    @close="() => handleDrawerClose(false)"
    @closed="handleDrawerClosed"
  >
    <div v-if="isLoading" class="loading-container">
      <div class="loading-spinner"></div>
    </div>

    <div v-else-if="loadError" class="error-container">
      <ElResult status="error" :title="t('system.operLog.loadFail')" :subTitle="loadErrorMsg">
        <template #extra>
          <ElButton type="primary" @click="handleRetry">{{ t('monitor.server.refresh') }}</ElButton>
        </template>
      </ElResult>
    </div>

    <div v-else-if="!operLog" class="empty-container">
      <ElEmpty :description="t('common.empty')" />
    </div>

    <div v-else class="log-detail-container">
      <OperLogDetailFields :oper-log="operLog" />
    </div>
  </ElDrawer>
</template>

<script setup lang="ts">
  import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
  import { ElButton, ElDrawer, ElEmpty, ElResult } from 'element-plus'
  import { useI18n } from 'vue-i18n'
  import type { OperLogListItem } from '@/types/api/operlog'
  import OperLogDetailFields from './oper-log-detail-fields.vue'

  interface Props {
    visible: boolean
    operLog: OperLogListItem | null
    loading?: boolean
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
    (e: 'open'): void
    (e: 'close'): void
    (e: 'retry'): void
  }

  const props = withDefaults(defineProps<Props>(), {
    loading: false
  })
  const emit = defineEmits<Emits>()
  const { t } = useI18n()

  const loadError = ref(false)
  const loadErrorMsg = ref('')

  let timeoutTimer: ReturnType<typeof setTimeout> | null = null
  const LOAD_TIMEOUT = 30000

  const isLoading = computed(() => props.loading || (loadError.value === false && !props.operLog))

  const drawerSize = computed(() => {
    if (typeof window !== 'undefined') {
      const width = window.innerWidth
      if (width < 768) return '90%'
      if (width < 1200) return '70%'
    }
    return '50%'
  })

  const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => {
      if (!value) emit('update:visible', false)
    }
  })

  const handleDrawerOpen = () => {
    loadError.value = false
    loadErrorMsg.value = ''
    emit('open')
    document.body.style.overflow = 'hidden'

    timeoutTimer = setTimeout(() => {
      loadError.value = true
      loadErrorMsg.value = t('system.operLog.loadFail')
    }, LOAD_TIMEOUT)
  }

  const handleDrawerOpened = () => {
    const drawerContent = document.querySelector('.oper-log-detail-drawer .el-drawer__body')
    if (drawerContent) {
      drawerContent.setAttribute('tabindex', '-1')
      ;(drawerContent as HTMLElement).focus()
    }
  }

  const handleDrawerClose = (value: boolean) => {
    if (!value) {
      if (timeoutTimer) clearTimeout(timeoutTimer)
      emit('update:visible', false)
      emit('close')
    }
  }

  const handleDrawerClosed = () => {
    document.body.style.overflow = ''
  }

  const handleRetry = () => {
    loadError.value = false
    loadErrorMsg.value = ''
    emit('retry')
  }

  const handleResize = () => void drawerSize.value

  watch(
    () => props.operLog,
    (newVal) => {
      if (newVal && timeoutTimer) {
        clearTimeout(timeoutTimer)
        timeoutTimer = null
      }
    }
  )

  onMounted(() => {
    window.addEventListener('resize', handleResize)
  })

  onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    document.body.style.overflow = ''
    if (timeoutTimer) clearTimeout(timeoutTimer)
  })
</script>

<style scoped lang="scss">
  :deep(.oper-log-detail-drawer) {
    .el-drawer {
      max-width: 900px;
      border-radius: 12px 0 0 12px;
      box-shadow: -20px 0 60px rgb(0 0 0 / 15%);
    }

    .el-drawer__header {
      position: sticky;
      top: 0;
      z-index: 10;
      padding: 16px 24px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

      .el-drawer__title {
        font-size: 18px;
        font-weight: 600;
        color: #fff;
      }

      .el-drawer__close {
        font-size: 20px;
        color: rgb(255 255 255 / 80%);

        &:hover {
          color: #fff;
        }
      }
    }

    .el-drawer__body {
      max-height: calc(100vh - 80px);
      padding: 24px;
      overflow-y: auto;
      background-color: #fafbfc;
    }
  }

  .loading-container {
    display: flex;
    flex-direction: column;
    gap: 16px;
    align-items: center;
    justify-content: center;
    min-height: 300px;
    padding: 60px 20px;
  }

  .loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #667eea;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }

  @keyframes spin {
    0% {
      transform: rotate(0deg);
    }

    100% {
      transform: rotate(360deg);
    }
  }

  .loading-text {
    font-size: 14px;
    color: #999;
  }

  .error-container {
    padding: 40px 20px;
  }

  .empty-container {
    padding: 60px 20px;
  }

  @media (width <= 768px) {
    :deep(.oper-log-detail-drawer) .el-drawer {
      max-width: 100%;
    }
  }

  ::-webkit-scrollbar {
    width: 8px;
    height: 8px;
  }

  ::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 4px;
  }

  ::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 4px;
  }
</style>
