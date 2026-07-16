<script setup lang="ts">
  import { fetchCaptcha, fetchVerifyCaptcha } from '@/api/auth'

  defineOptions({ name: 'LoginCaptchaClick' })

  const emit = defineEmits<{
    verified: [proof: string]
    failed: []
    refreshed: []
  }>()

  const dialogVisible = ref(false)
  const loading = ref(false)
  const verifying = ref(false)
  const success = ref(false)
  const captchaToken = ref('')
  const backgroundImage = ref('')
  const clickTip = ref('')
  const clickWords = ref<string[]>([])
  const clickPoints = ref<Array<{ x: number; y: number }>>([])
  const panelRef = ref<HTMLElement>()

  const triggerText = computed(() => (success.value ? '验证通过' : '点击按钮进行验证'))
  const remainCount = computed(() =>
    Math.max(0, clickWords.value.length - clickPoints.value.length)
  )

  const resetPuzzle = () => {
    clickPoints.value = []
    verifying.value = false
  }

  const resetAll = () => {
    resetPuzzle()
    success.value = false
    captchaToken.value = ''
    backgroundImage.value = ''
    clickTip.value = ''
    clickWords.value = []
  }

  const loadCaptcha = async () => {
    loading.value = true
    resetPuzzle()
    success.value = false
    try {
      const res = await fetchCaptcha('login')
      captchaToken.value = res.captchaToken
      backgroundImage.value = res.backgroundImage || ''
      clickTip.value = res.clickTip || ''
      clickWords.value = res.clickWords || []
      emit('refreshed')
    } catch (error) {
      console.error('获取点选验证码失败:', error)
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
    if (!success.value) resetPuzzle()
  }

  onBeforeUnmount(() => {
    dialogVisible.value = false
  })

  const onPanelClick = async (event: MouseEvent) => {
    if (loading.value || verifying.value || success.value) return
    if (clickPoints.value.length >= clickWords.value.length) return

    const target = event.currentTarget as HTMLElement
    const rect = target.getBoundingClientRect()
    // 图片固定 360x180，映射到原始坐标
    const x = Math.round(((event.clientX - rect.left) / rect.width) * 360)
    const y = Math.round(((event.clientY - rect.top) / rect.height) * 180)
    clickPoints.value.push({ x, y })

    if (clickPoints.value.length >= clickWords.value.length) {
      await verify()
    }
  }

  const verify = async () => {
    if (!captchaToken.value) return
    verifying.value = true
    try {
      const res = await fetchVerifyCaptcha({
        captchaToken: captchaToken.value,
        clickPoints: clickPoints.value,
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

  const undoLast = () => {
    if (success.value || verifying.value) return
    clickPoints.value.pop()
  }

  const refresh = async () => {
    success.value = false
    emit('refreshed')
    if (dialogVisible.value) await loadCaptcha()
    else resetAll()
  }

  defineExpose({ refresh, reset: resetAll, open: openDialog })
</script>

<template>
  <div class="captcha-entry">
    <button type="button" class="verify-trigger" :class="{ success }" @click="openDialog">
      <span class="trigger-icon">
        <ArtSvgIcon :icon="success ? 'ri:shield-check-fill' : 'ri:cursor-fill'" />
      </span>
      <span class="trigger-text">{{ triggerText }}</span>
      <ArtSvgIcon
        :icon="success ? 'ri:check-line' : 'ri:arrow-right-s-line'"
        class="trigger-arrow"
      />
    </button>

    <ElDialog
      v-model="dialogVisible"
      title="安全验证 · 文字点选"
      width="420px"
      align-center
      append-to-body
      :close-on-click-modal="false"
      destroy-on-close
      class="behavior-captcha-dialog"
      @closed="closeDialog"
    >
      <div class="click-captcha">
        <div class="tip-bar">
          <span>{{ clickTip || '请按提示依次点击文字' }}</span>
          <button type="button" class="text-btn" :disabled="loading || success" @click="loadCaptcha"
            >换一张</button
          >
        </div>

        <div ref="panelRef" class="click-panel" @click="onPanelClick">
          <img
            v-if="backgroundImage"
            :src="backgroundImage"
            alt="点选验证码"
            class="bg-img"
            draggable="false"
          />
          <div
            v-for="(point, index) in clickPoints"
            :key="`${point.x}-${point.y}-${index}`"
            class="click-mark"
            :style="{ left: `${(point.x / 360) * 100}%`, top: `${(point.y / 180) * 100}%` }"
          >
            {{ index + 1 }}
          </div>

          <div v-if="loading" class="panel-mask">
            <span class="spinner" />
            <span>加载中</span>
          </div>
          <div v-else-if="success" class="panel-mask success">
            <ArtSvgIcon icon="ri:checkbox-circle-fill" />
            <span>验证通过</span>
          </div>
        </div>

        <div class="footer-row">
          <span class="remain">还需点击 {{ remainCount }} 处</span>
          <ElButton
            size="small"
            :disabled="!clickPoints.length || success || verifying"
            @click="undoLast"
          >
            撤销
          </ElButton>
        </div>
      </div>
    </ElDialog>
  </div>
</template>

<style src="./captcha-entry-shared.css"></style>
<style scoped>
  .click-captcha {
    user-select: none;
  }

  .tip-bar {
    display: flex;
    gap: 12px;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
    font-size: 14px;
    font-weight: 500;
    color: #334155;
  }

  .text-btn {
    color: #3b82f6;
    cursor: pointer;
    background: none;
    border: none;
  }

  .click-panel {
    position: relative;
    width: 360px;
    max-width: 100%;
    height: 180px;
    margin: 0 auto;
    overflow: hidden;
    cursor: crosshair;
    border: 1px solid rgb(255 255 255 / 70%);
    border-radius: 14px;
    box-shadow: 0 8px 24px rgb(15 118 130 / 10%);
  }

  .bg-img {
    display: block;
    width: 360px;
    height: 180px;
    object-fit: fill;
    pointer-events: none;
  }

  .click-mark {
    position: absolute;
    z-index: 2;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 24px;
    height: 24px;
    font-size: 12px;
    font-weight: 700;
    color: #fff;
    pointer-events: none;
    background: #3b82f6;
    border: 2px solid #fff;
    border-radius: 50%;
    box-shadow: 0 2px 8px rgb(37 99 235 / 35%);
    transform: translate(-50%, -50%);
  }

  .footer-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 12px;
  }

  .remain {
    font-size: 13px;
    color: #94a3b8;
  }
</style>
