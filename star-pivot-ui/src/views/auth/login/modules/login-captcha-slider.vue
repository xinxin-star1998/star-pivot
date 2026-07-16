<script setup lang="ts">
  import { fetchCaptcha, fetchVerifyCaptcha } from '@/api/auth'

  defineOptions({ name: 'LoginCaptchaSlider' })

  const emit = defineEmits<{
    verified: [proof: string]
    failed: []
    refreshed: []
  }>()

  const PANEL_WIDTH = 360
  const BLOCK_WIDTH = 60
  const maxMove = PANEL_WIDTH - BLOCK_WIDTH

  const dialogVisible = ref(false)
  const loading = ref(false)
  const verifying = ref(false)
  const success = ref(false)
  const captchaToken = ref('')
  const backgroundImage = ref('')
  const sliderImage = ref('')
  const sliderY = ref(0)
  const moveX = ref(0)
  const dragging = ref(false)
  const startClientX = ref(0)
  const startMoveX = ref(0)

  const tipText = computed(() => {
    if (success.value) return '验证通过'
    if (verifying.value) return '校验中...'
    if (dragging.value) return '松开完成验证'
    return '向右拖动滑块完成验证'
  })

  const triggerText = computed(() => {
    if (success.value) return '验证通过'
    return '点击按钮进行验证'
  })

  const resetPuzzle = () => {
    moveX.value = 0
    verifying.value = false
    dragging.value = false
  }

  const resetAll = () => {
    resetPuzzle()
    success.value = false
    captchaToken.value = ''
    backgroundImage.value = ''
    sliderImage.value = ''
  }

  const loadCaptcha = async () => {
    loading.value = true
    resetPuzzle()
    success.value = false
    try {
      const res = await fetchCaptcha('login')
      captchaToken.value = res.captchaToken
      backgroundImage.value = res.backgroundImage || ''
      sliderImage.value = res.sliderImage || ''
      sliderY.value = res.sliderY ?? 0
      emit('refreshed')
    } catch (error) {
      console.error('获取滑块验证码失败:', error)
    } finally {
      loading.value = false
    }
  }

  const openDialog = async () => {
    if (success.value) return
    dialogVisible.value = true
    await loadCaptcha()
  }

  const closeDialog = () => {
    dialogVisible.value = false
    if (!success.value) {
      resetPuzzle()
    }
  }

  onBeforeUnmount(() => {
    dialogVisible.value = false
  })

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
    moveX.value = Math.max(0, Math.min(maxMove, startMoveX.value + delta))
  }

  const onPointerUp = async () => {
    if (!dragging.value) return
    dragging.value = false
    if (moveX.value < 8) {
      moveX.value = 0
      return
    }
    await verify()
  }

  const verify = async () => {
    if (!captchaToken.value) return
    verifying.value = true
    try {
      const res = await fetchVerifyCaptcha({
        captchaToken: captchaToken.value,
        sliderX: Math.round(moveX.value),
        scene: 'login'
      })
      success.value = true
      emit('verified', res.captchaProof)
      window.setTimeout(() => {
        dialogVisible.value = false
      }, 420)
    } catch {
      emit('failed')
      await loadCaptcha()
    } finally {
      verifying.value = false
    }
  }

  const refresh = async () => {
    success.value = false
    emit('refreshed')
    if (dialogVisible.value) {
      await loadCaptcha()
    } else {
      resetAll()
    }
  }

  defineExpose({ refresh, reset: resetAll, open: openDialog })
</script>

<template>
  <div class="slider-captcha-entry">
    <button
      type="button"
      class="verify-trigger custom-height"
      :class="{ success }"
      @click="openDialog"
    >
      <span class="trigger-icon">
        <ArtSvgIcon :icon="success ? 'ri:shield-check-fill' : 'ri:shield-keyhole-line'" />
      </span>
      <span class="trigger-text">{{ triggerText }}</span>
      <ArtSvgIcon v-if="!success" icon="ri:arrow-right-s-line" class="trigger-arrow" />
      <ArtSvgIcon v-else icon="ri:check-line" class="trigger-arrow success" />
    </button>

    <ElDialog
      v-model="dialogVisible"
      title="安全验证"
      width="420px"
      align-center
      append-to-body
      :close-on-click-modal="false"
      destroy-on-close
      class="slider-captcha-dialog"
      @closed="closeDialog"
    >
      <div class="slider-captcha">
        <div class="slider-panel">
          <img
            v-if="backgroundImage"
            :src="backgroundImage"
            alt="验证码背景"
            class="bg-img"
            draggable="false"
          />
          <img
            v-if="sliderImage"
            :src="sliderImage"
            alt="滑块"
            class="block-img"
            draggable="false"
            :style="{ transform: `translate3d(${moveX}px, ${sliderY}px, 0)` }"
          />

          <div v-if="loading" class="panel-mask">
            <span class="spinner" />
            <span>加载中</span>
          </div>
          <div v-else-if="success" class="panel-mask success">
            <ArtSvgIcon icon="ri:checkbox-circle-fill" class="success-icon" />
            <span>验证通过</span>
          </div>

          <button
            type="button"
            class="captcha-refresh-btn"
            title="换一张"
            :disabled="loading || verifying || success"
            @click="loadCaptcha"
          >
            <ArtSvgIcon icon="ri:refresh-line" :class="{ spinning: loading }" />
          </button>
        </div>

        <div class="slider-track" :class="{ dragging, success, verifying }">
          <div class="track-fill" :style="{ width: `${moveX + 44}px` }" />
          <div class="track-text" :class="{ dim: moveX > 40 && !success }">{{ tipText }}</div>
          <div
            class="slider-btn"
            :style="{ transform: `translate3d(${moveX}px, 0, 0)` }"
            @pointerdown="onPointerDown"
            @pointermove="onPointerMove"
            @pointerup="onPointerUp"
            @pointercancel="onPointerUp"
          >
            <ArtSvgIcon :icon="success ? 'ri:check-line' : 'ri:arrow-right-double-line'" />
          </div>
        </div>
      </div>
    </ElDialog>
  </div>
</template>

<style scoped>
  .slider-captcha-entry {
    width: 100%;
  }

  .verify-trigger {
    display: flex;
    gap: 10px;
    align-items: center;
    width: 100%;
    height: 52px;
    padding: 0 16px;
    font-size: 14px;
    color: #64748b;
    cursor: pointer;
    background: rgb(255 255 255 / 72%);
    border: 1px solid rgb(226 232 240 / 90%);
    border-radius: 10px;
    box-shadow: 0 1px 2px rgb(15 23 42 / 3%);
    transition:
      border-color 0.2s ease,
      box-shadow 0.2s ease,
      color 0.2s ease,
      background 0.2s ease;
  }

  .verify-trigger:hover {
    color: #3b82f6;
    border-color: rgb(147 197 253 / 80%);
    box-shadow: 0 4px 14px rgb(59 130 246 / 12%);
  }

  .verify-trigger.success {
    color: #059669;
    background: rgb(236 253 245 / 85%);
    border-color: rgb(167 243 208 / 90%);
  }

  .trigger-icon {
    display: inline-flex;
    font-size: 18px;
  }

  .trigger-text {
    flex: 1;
    text-align: left;
  }

  .trigger-arrow {
    font-size: 18px;
    opacity: 0.7;
  }

  .trigger-arrow.success {
    color: #10b981;
    opacity: 1;
  }

  .slider-captcha {
    width: 100%;
    user-select: none;
    touch-action: none;
  }

  .slider-panel {
    position: relative;
    width: 360px;
    max-width: 100%;
    height: 180px;
    margin: 0 auto;
    overflow: hidden;
    background: linear-gradient(145deg, #e8f4f5, #d7eef1);
    border: 1px solid rgb(255 255 255 / 70%);
    border-radius: 14px;
    box-shadow:
      0 8px 24px rgb(15 118 130 / 10%),
      inset 0 1px 0 rgb(255 255 255 / 60%);
  }

  .bg-img {
    display: block;
    width: 360px;
    height: 180px;
    object-fit: fill;
  }

  .block-img {
    position: absolute;
    top: 0;
    left: 0;
    width: 64px;
    height: 54px;
    pointer-events: none;
    will-change: transform;
    filter: drop-shadow(0 4px 10px rgb(0 0 0 / 28%));
  }

  .panel-mask {
    position: absolute;
    inset: 0;
    z-index: 2;
    display: flex;
    gap: 8px;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    font-weight: 500;
    color: #fff;
    letter-spacing: 0.02em;
    background: rgb(15 23 42 / 28%);
    backdrop-filter: blur(2px);
  }

  .panel-mask.success {
    background: rgb(16 185 129 / 42%);
  }

  .success-icon {
    font-size: 18px;
  }

  .spinner {
    width: 16px;
    height: 16px;
    border: 2px solid rgb(255 255 255 / 35%);
    border-top-color: #fff;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }

  .captcha-refresh-btn {
    position: absolute;
    top: 10px;
    right: 10px;
    z-index: 3;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 30px;
    height: 30px;
    color: #fff;
    cursor: pointer;
    background: rgb(15 23 42 / 28%);
    border: 1px solid rgb(255 255 255 / 25%);
    border-radius: 10px;
    backdrop-filter: blur(8px);
    transition:
      background 0.2s ease,
      transform 0.2s ease;
  }

  .captcha-refresh-btn:hover:not(:disabled) {
    background: rgb(15 23 42 / 42%);
    transform: rotate(-20deg);
  }

  .captcha-refresh-btn:disabled {
    cursor: not-allowed;
    opacity: 0.6;
  }

  .spinning {
    animation: spin 0.8s linear infinite;
  }

  .slider-track {
    position: relative;
    width: 360px;
    max-width: 100%;
    height: 44px;
    margin: 14px auto 0;
    overflow: hidden;
    background: linear-gradient(180deg, #f8fafc, #eef2f7);
    border: 1px solid #e5e7eb;
    border-radius: 22px;
    box-shadow: inset 0 1px 2px rgb(15 23 42 / 4%);
    transition:
      border-color 0.2s ease,
      box-shadow 0.2s ease;
  }

  .slider-track.dragging {
    border-color: rgb(59 130 246 / 45%);
    box-shadow:
      inset 0 1px 2px rgb(15 23 42 / 4%),
      0 0 0 3px rgb(59 130 246 / 12%);
  }

  .slider-track.success {
    border-color: rgb(16 185 129 / 45%);
    box-shadow:
      inset 0 1px 2px rgb(15 23 42 / 4%),
      0 0 0 3px rgb(16 185 129 / 12%);
  }

  .track-fill {
    position: absolute;
    top: 0;
    left: 0;
    height: 100%;
    background: linear-gradient(90deg, rgb(59 130 246 / 12%), rgb(59 130 246 / 28%));
  }

  .slider-track.success .track-fill {
    background: linear-gradient(90deg, rgb(16 185 129 / 14%), rgb(16 185 129 / 32%));
  }

  .track-text {
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 13px;
    color: #94a3b8;
    pointer-events: none;
    transition: opacity 0.2s ease;
  }

  .track-text.dim {
    opacity: 0.25;
  }

  .slider-btn {
    position: absolute;
    top: 2px;
    left: 2px;
    z-index: 2;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 38px;
    color: #3b82f6;
    cursor: grab;
    background: linear-gradient(180deg, #fff, #f8fafc);
    border: 1px solid #e2e8f0;
    border-radius: 19px;
    box-shadow:
      0 4px 12px rgb(15 23 42 / 10%),
      0 1px 0 rgb(255 255 255 / 80%) inset;
    will-change: transform;
  }

  .slider-btn:active {
    cursor: grabbing;
  }

  .slider-track.dragging .slider-btn {
    color: #2563eb;
    border-color: #bfdbfe;
    box-shadow:
      0 6px 16px rgb(37 99 235 / 22%),
      0 1px 0 rgb(255 255 255 / 80%) inset;
  }

  .slider-track.success .slider-btn {
    color: #059669;
    border-color: #a7f3d0;
    cursor: default;
  }

  :deep(.dark) .verify-trigger,
  .dark .verify-trigger {
    color: #94a3b8;
    background: rgb(26 26 30 / 80%);
    border-color: #374151;
  }

  .dark .verify-trigger.success {
    color: #34d399;
    background: rgb(6 78 59 / 35%);
    border-color: rgb(52 211 153 / 35%);
  }

  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }
</style>

<style>
  .slider-captcha-dialog .el-dialog__header {
    margin-right: 0;
    padding-bottom: 8px;
  }

  .slider-captcha-dialog .el-dialog__body {
    padding-top: 8px;
    padding-bottom: 20px;
  }

  .slider-captcha-dialog .el-dialog__title {
    font-size: 16px;
    font-weight: 600;
  }
</style>
