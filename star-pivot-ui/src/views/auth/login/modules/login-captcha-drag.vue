<script setup lang="ts">
  import { fetchCaptcha, fetchVerifyCaptcha } from '@/api/auth'

  defineOptions({ name: 'LoginCaptchaDrag' })

  const emit = defineEmits<{
    verified: [proof: string]
    failed: []
    refreshed: []
  }>()

  /** 与后端 DragCaptchaProvider.TRACK_MAX_X 一致 */
  const TRACK_MAX_X = 280
  const HANDLE_WIDTH = 40

  const trackRef = ref<HTMLElement>()
  const loading = ref(false)
  const verifying = ref(false)
  const success = ref(false)
  const captchaToken = ref('')
  const moveX = ref(0)
  const dragging = ref(false)
  const startClientX = ref(0)
  const startMoveX = ref(0)

  const tipText = computed(() => {
    if (success.value) return '验证通过'
    if (verifying.value) return '校验中...'
    if (dragging.value) return '松开完成验证'
    return '按住滑块拖动'
  })

  const maxMove = computed(() => {
    const width = trackRef.value?.clientWidth ?? TRACK_MAX_X + HANDLE_WIDTH
    return Math.max(0, width - HANDLE_WIDTH)
  })

  /** 映射到服务端约定的 TRACK_MAX_X 坐标系 */
  const mappedX = computed(() => {
    const max = maxMove.value
    if (max <= 0) return 0
    return Math.round((moveX.value / max) * TRACK_MAX_X)
  })

  const resetDrag = () => {
    moveX.value = 0
    verifying.value = false
    dragging.value = false
  }

  const resetAll = () => {
    resetDrag()
    success.value = false
    captchaToken.value = ''
  }

  const loadCaptcha = async () => {
    loading.value = true
    resetDrag()
    success.value = false
    try {
      const res = await fetchCaptcha('login')
      captchaToken.value = res.captchaToken
      emit('refreshed')
    } catch (error) {
      console.error('获取拖动验证码失败:', error)
    } finally {
      loading.value = false
    }
  }

  const onPointerDown = (event: PointerEvent) => {
    if (loading.value || verifying.value || success.value) return
    event.preventDefault()
    dragging.value = true
    startClientX.value = event.clientX
    startMoveX.value = moveX.value
    ;(event.currentTarget as HTMLElement).setPointerCapture?.(event.pointerId)
  }

  const onPointerMove = (event: PointerEvent) => {
    if (!dragging.value) return
    const delta = event.clientX - startClientX.value
    moveX.value = Math.max(0, Math.min(maxMove.value, startMoveX.value + delta))
  }

  const onPointerUp = async () => {
    if (!dragging.value) return
    dragging.value = false
    if (mappedX.value < TRACK_MAX_X - 20) {
      moveX.value = 0
      return
    }
    // 吸附到尽头再校验
    moveX.value = maxMove.value
    await verify()
  }

  const verify = async () => {
    if (!captchaToken.value) return
    verifying.value = true
    try {
      const res = await fetchVerifyCaptcha({
        captchaToken: captchaToken.value,
        sliderX: TRACK_MAX_X,
        scene: 'login'
      })
      success.value = true
      emit('verified', res.captchaProof)
    } catch {
      emit('failed')
      resetDrag()
      await loadCaptcha()
    } finally {
      verifying.value = false
    }
  }

  const refresh = () => {
    resetAll()
    return loadCaptcha()
  }

  defineExpose({ refresh })

  onMounted(() => {
    loadCaptcha()
  })
</script>

<template>
  <div class="drag-captcha" :class="{ success, dragging, verifying, loading }">
    <div ref="trackRef" class="drag-track">
      <div class="drag-progress" :style="{ width: `${moveX + HANDLE_WIDTH / 2}px` }"></div>
      <div class="drag-tip">{{ tipText }}</div>
      <div
        class="drag-handle"
        :style="{ transform: `translateX(${moveX}px)` }"
        @pointerdown="onPointerDown"
        @pointermove="onPointerMove"
        @pointerup="onPointerUp"
        @pointercancel="onPointerUp"
      >
        <span v-if="success" class="handle-icon success-icon">✓</span>
        <span v-else class="handle-icon">»</span>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
  .drag-captcha {
    width: 100%;
    user-select: none;
    touch-action: none;
  }

  .drag-track {
    position: relative;
    display: flex;
    align-items: center;
    width: 100%;
    height: 42px;
    overflow: hidden;
    background: #f5f6f8;
    border: 1px solid #e5e7eb;
    border-radius: 4px;
  }

  .drag-progress {
    position: absolute;
    top: 0;
    left: 0;
    z-index: 0;
    height: 100%;
    pointer-events: none;
    background: rgb(82 196 26 / 18%);
    transition: background 0.2s ease;
  }

  .drag-tip {
    position: absolute;
    inset: 0;
    z-index: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    color: #909399;
    pointer-events: none;
    letter-spacing: 0.5px;
  }

  .drag-handle {
    position: absolute;
    top: 0;
    left: 0;
    z-index: 2;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 100%;
    cursor: grab;
    background: #fff;
    border-right: 1px solid #e5e7eb;
    box-shadow: 1px 0 2px rgb(0 0 0 / 6%);
    transition:
      background 0.2s ease,
      color 0.2s ease;
  }

  .drag-handle:active {
    cursor: grabbing;
  }

  .handle-icon {
    font-size: 16px;
    font-weight: 600;
    color: #606266;
    line-height: 1;
  }

  .success-icon {
    color: #52c41a;
  }

  .drag-captcha.dragging .drag-progress {
    background: rgb(64 158 255 / 22%);
  }

  .drag-captcha.success .drag-track {
    background: #f6ffed;
    border-color: #b7eb8f;
  }

  .drag-captcha.success .drag-progress {
    width: 100% !important;
    background: rgb(82 196 26 / 28%);
  }

  .drag-captcha.success .drag-tip {
    color: #52c41a;
  }

  .drag-captcha.success .drag-handle {
    color: #52c41a;
    border-right-color: #b7eb8f;
  }

  .drag-captcha.loading,
  .drag-captcha.verifying {
    opacity: 0.85;
  }

  .drag-captcha.success .drag-handle {
    cursor: default;
  }

  .dark .drag-track {
    background: #1f2937;
    border-color: #374151;
  }

  .dark .drag-tip {
    color: #9ca3af;
  }

  .dark .drag-handle {
    background: #111827;
    border-right-color: #374151;
  }

  .dark .handle-icon {
    color: #d1d5db;
  }

  .dark .drag-captcha.success .drag-track {
    background: rgb(6 78 59 / 35%);
    border-color: rgb(52 211 153 / 35%);
  }

  .dark .drag-captcha.success .drag-tip,
  .dark .success-icon {
    color: #34d399;
  }
</style>
