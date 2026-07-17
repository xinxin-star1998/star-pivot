<template>
  <ElDialog
    v-model="visible"
    :title="t('monitor.job.cronEditor')"
    width="980px"
    append-to-body
    destroy-on-close
    class="cron-editor-dialog"
  >
    <div class="cron-layout">
      <div class="cron-main">
        <div class="cron-header">
          <div class="cron-title">
            <div class="cron-title__name">{{ t('monitor.job.cronEditor') }}</div>
            <div class="cron-title__desc">{{ t('monitor.job.cronPlaceholder') }}</div>
          </div>
          <ElRadioGroup v-model="mode" class="mode-toggle">
            <ElRadioButton label="visual">{{ t('common.expand') }}</ElRadioButton>
            <ElRadioButton label="advanced">{{ t('monitor.job.cronEditor') }}</ElRadioButton>
          </ElRadioGroup>
        </div>

        <div class="cron-section">
          <div class="cron-section__title">{{ t('monitor.job.cronExpression') }}</div>
          <ElRow :gutter="12">
            <ElCol v-for="p in presets" :key="p.label" :span="8">
              <ElCard
                shadow="never"
                class="preset-card"
                :class="{ 'preset-card--active': draft === p.expression }"
                @click="applyPreset(p.expression)"
              >
                <div class="preset-card__name">{{ p.label }}</div>
                <div class="preset-card__desc">{{ p.desc }}</div>
              </ElCard>
            </ElCol>
          </ElRow>
        </div>

        <div class="cron-section">
          <div class="cron-section__title">{{ t('monitor.job.cronEditor') }}</div>
          <ElTabs v-model="visualTab" type="card" class="visual-tabs" :disabled="mode !== 'visual'">
            <ElTabPane :label="t('monitor.job.cronExpression')" name="minute">
              <div class="form-grid">
                <div class="form-row">
                  <div class="form-row__label">{{ t('monitor.job.cronExpression') }}</div>
                  <ElInputNumber
                    v-model="minute.every"
                    :min="1"
                    :max="59"
                    controls-position="right"
                  />
                  <div class="form-row__label">{{ t('monitor.job.cronPlaceholder') }}</div>
                </div>
              </div>
            </ElTabPane>
            <ElTabPane :label="t('monitor.server.cpu')" name="hour">
              <div class="form-grid">
                <div class="form-row">
                  <div class="form-row__label">{{ t('monitor.server.cpu') }}</div>
                  <ElInputNumber
                    v-model="hour.every"
                    :min="1"
                    :max="23"
                    controls-position="right"
                  />
                  <div class="form-row__label">{{ t('monitor.job.cronExpression') }}</div>
                  <ElInputNumber
                    v-model="hour.minute"
                    :min="0"
                    :max="59"
                    controls-position="right"
                  />
                  <div class="form-row__label">{{ t('monitor.job.cronPlaceholder') }}</div>
                </div>
              </div>
            </ElTabPane>
            <ElTabPane :label="t('common.createTime')" name="day">
              <div class="form-grid">
                <div class="form-row">
                  <div class="form-row__label">{{ t('common.createTime') }}</div>
                  <ElInputNumber v-model="day.hour" :min="0" :max="23" controls-position="right" />
                  <div class="form-row__label">{{ t('monitor.server.cpu') }}</div>
                  <ElInputNumber
                    v-model="day.minute"
                    :min="0"
                    :max="59"
                    controls-position="right"
                  />
                  <div class="form-row__label">{{ t('monitor.job.cronPlaceholder') }}</div>
                </div>
              </div>
            </ElTabPane>
            <ElTabPane :label="t('monitor.job.jobGroup')" name="week">
              <div class="form-grid">
                <div class="form-row">
                  <div class="form-row__label">{{ t('monitor.job.jobGroup') }}</div>
                  <ElSelect v-model="week.dow" style="width: 140px">
                    <ElOption
                      v-for="d in dowOptions"
                      :key="d.value"
                      :label="d.label"
                      :value="d.value"
                    />
                  </ElSelect>
                  <div class="form-row__label">{{ t('monitor.server.cpu') }}</div>
                  <ElInputNumber v-model="week.hour" :min="0" :max="23" controls-position="right" />
                  <div class="form-row__label">{{ t('monitor.server.mem') }}</div>
                  <ElInputNumber
                    v-model="week.minute"
                    :min="0"
                    :max="59"
                    controls-position="right"
                  />
                  <div class="form-row__label">{{ t('monitor.job.cronPlaceholder') }}</div>
                </div>
              </div>
            </ElTabPane>
            <ElTabPane :label="t('monitor.server.mem')" name="month">
              <div class="form-grid">
                <div class="form-row">
                  <div class="form-row__label">{{ t('monitor.server.mem') }}</div>
                  <ElInputNumber v-model="month.dom" :min="1" :max="31" controls-position="right" />
                  <div class="form-row__label">{{ t('monitor.server.cpu') }}</div>
                  <ElInputNumber
                    v-model="month.hour"
                    :min="0"
                    :max="23"
                    controls-position="right"
                  />
                  <div class="form-row__label">{{ t('monitor.server.mem') }}</div>
                  <ElInputNumber
                    v-model="month.minute"
                    :min="0"
                    :max="59"
                    controls-position="right"
                  />
                  <div class="form-row__label">{{ t('monitor.job.cronPlaceholder') }}</div>
                </div>
              </div>
            </ElTabPane>
          </ElTabs>

          <div v-if="mode === 'advanced'" class="advanced-section">
            <div class="advanced-header">
              <div class="advanced-title">{{ t('monitor.job.cronEditor') }}</div>
              <div class="advanced-tip">{{ t('monitor.job.cronPlaceholder') }}</div>
            </div>
            <ElInput
              v-model="draft"
              type="textarea"
              :rows="4"
              :placeholder="t('monitor.job.cronPlaceholder')"
              maxlength="100"
              show-word-limit
            />
            <div class="advanced-hint">
              {{ t('monitor.job.cronPlaceholder') }}
              <span class="mono">S M H D Mo W</span>
            </div>
          </div>
        </div>

        <div class="cron-section">
          <div class="cron-section__title">{{ t('monitor.job.cronExpression') }}</div>
          <ElInput v-model="draft" readonly />
        </div>
      </div>

      <div class="cron-side">
        <ElCard shadow="never" class="side-card side-card--summary">
          <div class="side-title">{{ t('monitor.job.cronExpression') }}</div>
          <div class="side-cron">{{ draft || '-' }}</div>
          <div class="side-tz">Asia/Shanghai</div>
          <div class="chips">
            <div class="chip" v-for="c in chips" :key="c.k">
              <div class="chip__k">{{ c.k }}</div>
              <div class="chip__v">{{ c.v }}</div>
            </div>
          </div>
        </ElCard>

        <ElCard shadow="never" class="side-card">
          <div class="side-row">
            <div class="side-title">{{ t('monitor.job.status') }}</div>
            <ElTag :type="validation.ok ? 'success' : 'danger'" effect="light">
              {{
                validation.ok ? t('system.loginLog.statusSuccess') : t('system.loginLog.statusFail')
              }}
            </ElTag>
          </div>
          <div class="side-desc">
            <template v-if="validation.ok">
              {{ scheduleText }}
            </template>
            <template v-else>
              {{ validation.message }}
            </template>
          </div>
        </ElCard>

        <ElCard shadow="never" class="side-card">
          <div class="side-row">
            <div class="side-title">{{ t('monitor.job.runOnce') }}</div>
            <div class="side-tz">Asia/Shanghai</div>
          </div>
          <div class="next-list">
            <div v-for="(t, i) in nextRuns" :key="t + i" class="next-item">
              <div class="next-item__main">{{ t }}</div>
            </div>
            <div v-if="!nextRuns.length" class="next-empty">-</div>
          </div>
        </ElCard>
      </div>
    </div>

    <template #footer>
      <ElButton @click="visible = false">{{ t('common.cancel') }}</ElButton>
      <ElButton type="primary" :disabled="!validation.ok" @click="confirmUse">{{
        t('common.confirm')
      }}</ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import dayjs from 'dayjs'
  import { CronExpressionParser } from 'cron-parser'
  import { useI18n } from 'vue-i18n'

  interface Props {
    modelValue: boolean
    value?: string
  }

  interface Emits {
    (e: 'update:modelValue', v: boolean): void
    (e: 'confirm', expression: string): void
  }

  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()
  const { t } = useI18n()

  const visible = computed({
    get: () => props.modelValue,
    set: (v) => emit('update:modelValue', v)
  })

  type Mode = 'visual' | 'advanced'
  const mode = ref<Mode>('visual')

  const presets = computed(() => [
    {
      label: t('monitor.job.cronExpression'),
      expression: '0 0/5 * * * ?',
      desc: t('common.remark')
    },
    {
      label: t('monitor.job.cronExpression'),
      expression: '0 0/15 * * * ?',
      desc: t('common.remark')
    },
    { label: t('monitor.server.cpu'), expression: '0 0 0/1 * * ?', desc: t('common.remark') },
    { label: t('common.createTime'), expression: '0 0 3 * * ?', desc: t('common.remark') },
    { label: t('monitor.job.jobName'), expression: '0 0 9 ? * MON-FRI', desc: t('common.remark') },
    { label: t('monitor.job.jobGroup'), expression: '0 0 2 ? * MON', desc: t('common.remark') }
  ])

  const draft = ref('')

  const visualTab = ref<'minute' | 'hour' | 'day' | 'week' | 'month'>('day')
  const minute = reactive({ every: 5 })
  const hour = reactive({ every: 1, minute: 0 })
  const day = reactive({ hour: 3, minute: 0 })
  const week = reactive({ dow: 'MON', hour: 3, minute: 0 })
  const month = reactive({ dom: 1, hour: 9, minute: 0 })

  const dowOptions = computed(
    () =>
      [
        { label: t('monitor.job.jobGroup'), value: 'MON' },
        { label: t('monitor.job.jobGroup'), value: 'TUE' },
        { label: t('monitor.job.jobGroup'), value: 'WED' },
        { label: t('monitor.job.jobGroup'), value: 'THU' },
        { label: t('monitor.job.jobGroup'), value: 'FRI' },
        { label: t('monitor.job.jobGroup'), value: 'SAT' },
        { label: t('monitor.job.jobGroup'), value: 'SUN' }
      ] as const
  )

  const buildVisualCron = () => {
    // Quartz 6 段：秒 分 时 日 月 周
    switch (visualTab.value) {
      case 'minute':
        return `0 0/${minute.every} * * * ?`
      case 'hour':
        return `0 ${hour.minute} 0/${hour.every} * * ?`
      case 'day':
        return `0 ${day.minute} ${day.hour} * * ?`
      case 'week':
        return `0 ${week.minute} ${week.hour} ? * ${week.dow}`
      case 'month':
        return `0 ${month.minute} ${month.hour} ${month.dom} * ?`
      default:
        return draft.value
    }
  }

  const applyPreset = (expression: string) => {
    draft.value = expression
  }

  watch(
    [
      mode,
      visualTab,
      () => minute.every,
      () => hour.every,
      () => hour.minute,
      () => day.hour,
      () => day.minute,
      () => week.dow,
      () => week.hour,
      () => week.minute,
      () => month.dom,
      () => month.hour,
      () => month.minute
    ],
    () => {
      if (mode.value === 'visual') draft.value = buildVisualCron()
    },
    { immediate: true }
  )

  watch(
    () => props.value,
    (v) => {
      if (v && !visible.value) draft.value = v
    },
    { immediate: true }
  )

  watch(
    () => visible.value,
    (v) => {
      if (v) {
        draft.value = props.value || buildVisualCron()
      }
    }
  )

  const validation = computed(() => {
    const exp = (draft.value || '').trim()
    if (!exp) return { ok: false, message: t('monitor.job.cronPlaceholder') }
    try {
      CronExpressionParser.parse(exp)
      return { ok: true, message: t('system.loginLog.statusSuccess') }
    } catch (e: any) {
      return { ok: false, message: e?.message || t('system.loginLog.statusFail') }
    }
  })

  const nextRuns = computed(() => {
    if (!validation.value.ok) return []
    try {
      const it = CronExpressionParser.parse(draft.value.trim(), { currentDate: new Date() })
      const arr: string[] = []
      for (let i = 0; i < 6; i++) {
        const d = it.next().toDate()
        arr.push(dayjs(d).format('YYYY/MM/DD HH:mm:ss'))
      }
      return arr
    } catch {
      return []
    }
  })

  const chips = computed(() => {
    const parts = (draft.value || '').trim().split(/\s+/)
    const fill = (i: number) => parts[i] ?? '-'
    return [
      { k: 'S', v: fill(0) },
      { k: 'M', v: fill(1) },
      { k: 'H', v: fill(2) },
      { k: 'D', v: fill(3) },
      { k: 'Mo', v: fill(4) },
      { k: 'W', v: fill(5) }
    ]
  })

  const scheduleText = computed(() => {
    if (!validation.value.ok) return ''
    const exp = draft.value.trim()
    if (!exp) return ''
    return `${t('monitor.job.cronExpression')}: ${exp}`
  })

  const confirmUse = () => {
    if (!validation.value.ok) return
    const exp = draft.value.trim()
    emit('confirm', exp)
    visible.value = false
  }
</script>

<style scoped lang="scss">
  .cron-layout {
    display: grid;
    grid-template-columns: 1fr 320px;
    gap: 16px;
  }

  .cron-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 16px;
    background: linear-gradient(
      135deg,
      var(--el-color-primary-light-9) 0%,
      var(--el-color-primary-light-8) 100%
    );
    border: 1px solid var(--art-card-border);
    border-radius: 12px;
  }

  .mode-toggle :deep(.el-radio-button__inner) {
    padding: 8px 14px;
    font-weight: 600;
    border-radius: 10px;
  }

  .mode-toggle :deep(.el-radio-button:first-child .el-radio-button__inner) {
    border-top-left-radius: 10px;
    border-bottom-left-radius: 10px;
  }

  .mode-toggle :deep(.el-radio-button:last-child .el-radio-button__inner) {
    border-top-right-radius: 10px;
    border-bottom-right-radius: 10px;
  }

  .cron-title__name {
    font-weight: 600;
    line-height: 1.2;
    color: var(--art-gray-900);
  }

  .cron-title__desc {
    margin-top: 4px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }

  .cron-section {
    margin-top: 14px;
  }

  .cron-section__title {
    margin-bottom: 10px;
    font-weight: 600;
    color: var(--art-gray-800);
  }

  .preset-card {
    cursor: pointer;
    border: 1px solid var(--art-card-border);
    border-radius: 12px;
    transition: all 0.2s ease;
  }

  .preset-card:hover {
    box-shadow: 0 6px 18px rgb(0 0 0 / 8%);
    transform: translateY(-1px);
  }

  .preset-card--active {
    background: var(--el-color-primary-light-9);
    border-color: var(--el-color-primary);
  }

  .preset-card__name {
    font-weight: 600;
    color: var(--art-gray-900);
  }

  .preset-card__desc {
    margin-top: 6px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }

  .visual-tabs :deep(.el-tabs__content) {
    padding: 12px;
    border: 1px solid var(--art-card-border);
    border-top: 0;
    border-radius: 0 0 12px 12px;
  }

  .form-grid {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .form-row {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    align-items: center;
  }

  .form-row__label {
    color: var(--art-gray-700);
  }

  .advanced-section {
    padding-top: 10px;
    margin-top: 14px;
    border-top: 1px solid var(--art-card-border);
  }

  .advanced-header {
    display: flex;
    gap: 10px;
    align-items: baseline;
    justify-content: space-between;
    margin-bottom: 10px;
  }

  .advanced-title {
    font-weight: 600;
    color: var(--art-gray-800);
  }

  .advanced-tip {
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }

  .advanced-hint {
    margin-top: 10px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }

  .mono {
    font-family:
      ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New',
      monospace;
  }

  .cron-side {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .side-card {
    border: 1px solid var(--art-card-border);
    border-radius: 12px;
  }

  .side-card--summary {
    color: #fff;
    background: linear-gradient(135deg, #2d3a57 0%, #1f2b44 100%);
    border: 0;
  }

  .side-title {
    font-weight: 600;
  }

  .side-row {
    display: flex;
    gap: 10px;
    align-items: center;
    justify-content: space-between;
  }

  .side-cron {
    margin-top: 10px;
    font-weight: 700;
    letter-spacing: 1px;
  }

  .side-tz {
    margin-top: 8px;
    font-size: 12px;
    opacity: 0.8;
  }

  .side-desc {
    margin-top: 10px;
    font-size: 13px;
    color: var(--el-text-color-regular);
  }

  .side-desc--ok {
    color: var(--el-color-success);
  }

  .chips {
    display: grid;
    grid-template-columns: repeat(6, 1fr);
    gap: 8px;
    margin-top: 12px;
  }

  .chip {
    padding: 8px 10px;
    text-align: center;
    background: rgb(255 255 255 / 10%);
    border-radius: 12px;
  }

  .chip__k {
    font-size: 12px;
    opacity: 0.9;
  }

  .chip__v {
    margin-top: 6px;
    font-weight: 700;
  }

  .next-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin-top: 10px;
  }

  .next-item {
    padding: 10px 12px;
    border: 1px solid var(--art-card-border);
    border-radius: 12px;
  }

  .next-item__main {
    font-weight: 600;
  }

  .next-empty {
    color: var(--el-text-color-secondary);
  }
</style>
