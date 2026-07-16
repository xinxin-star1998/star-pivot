<template>
  <ElDialog v-model="visible" destroy-on-close title="分享文件" width="520px" @closed="reset">
    <div v-if="file" class="share-file-name">{{ file.fileName }}</div>

    <ElForm label-width="96px" autocomplete="off" @submit.prevent>
      <ElFormItem label="访问密码">
        <div class="access-code-field">
          <ElInput
            v-model="password"
            clearable
            name="share_access_code"
            autocomplete="off"
            type="text"
            :readonly="accessCodeReadonly"
            :class="{ 'is-masked': !showAccessCode }"
            placeholder="可选，留空则无需密码"
            @focus="accessCodeReadonly = false"
          />
          <ElButton class="access-code-toggle" link type="primary" @click="showAccessCode = !showAccessCode">
            {{ showAccessCode ? '隐藏' : '显示' }}
          </ElButton>
        </div>
      </ElFormItem>
      <ElFormItem label="有效期">
        <ElSelect v-model="expirePreset" style="width: 100%">
          <ElOption label="1 天" value="1d" />
          <ElOption label="7 天" value="7d" />
          <ElOption label="30 天" value="30d" />
          <ElOption label="永久" value="never" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="访问次数">
        <ElInputNumber v-model="maxViews" :min="0" controls-position="right" placeholder="0=不限" />
        <span class="form-hint">0 表示不限制</span>
      </ElFormItem>
      <ElFormItem label="允许下载">
        <ElSwitch v-model="allowDownload" />
      </ElFormItem>
    </ElForm>

    <div v-if="createdShare" class="share-result">
      <div class="share-result__label">分享链接</div>
      <div class="share-result__row">
        <ElInput :model-value="displayShareUrl" readonly />
        <ElButton type="primary" @click="copyLink">复制</ElButton>
      </div>
    </div>

    <div v-if="existingShares.length" class="share-list">
      <div class="share-list__title">已有分享</div>
      <div v-for="item in existingShares" :key="item.shareId" class="share-list__item">
        <div class="share-list__meta">
          <span>{{ item.shareUrl || `/s/${item.shareCode}` }}</span>
          <span class="share-list__sub">
            {{ item.hasPassword ? '有密码' : '无密码' }}
            · 访问 {{ item.viewCount || 0 }}{{ item.maxViews ? `/${item.maxViews}` : '' }}
            · {{ item.expireTime || '永久' }}
          </span>
        </div>
        <ElButton link type="danger" @click="handleRevoke(item.shareId!)">取消</ElButton>
      </div>
    </div>

    <template #footer>
      <ElButton @click="visible = false">关闭</ElButton>
      <ElButton :loading="creating" type="primary" @click="handleCreate">创建分享</ElButton>
    </template>
  </ElDialog>
</template>

<script lang="ts" setup>
  import { createFileShare, fetchFileShares, revokeFileShare } from '@/api/file/share'
  import type { SysFile, SysFileShare } from '@/api/file/types'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { computed, ref, watch } from 'vue'

  const visible = defineModel<boolean>('visible', { default: false })

  const props = defineProps<{
    file?: SysFile | null
  }>()

  const password = ref('')
  const showAccessCode = ref(false)
  const accessCodeReadonly = ref(true)
  const expirePreset = ref('7d')
  const maxViews = ref(0)
  const allowDownload = ref(true)
  const creating = ref(false)
  const createdShare = ref<SysFileShare | null>(null)
  const existingShares = ref<SysFileShare[]>([])

  const displayShareUrl = computed(() => {
    if (!createdShare.value) return ''
    if (createdShare.value.shareUrl?.startsWith('http')) {
      return createdShare.value.shareUrl
    }
    return `${window.location.origin}/s/${createdShare.value.shareCode}`
  })

  watch(
    () => visible.value,
    async (open) => {
      if (!open || !props.file?.fileId) return
      resetForm()
      try {
        existingShares.value = (await fetchFileShares(props.file.fileId)) || []
      } catch {
        existingShares.value = []
      }
    }
  )

  function resetForm() {
    password.value = ''
    showAccessCode.value = false
    accessCodeReadonly.value = true
    expirePreset.value = '7d'
    maxViews.value = 0
    allowDownload.value = true
    createdShare.value = null
  }

  function reset() {
    resetForm()
    existingShares.value = []
  }

  function resolveExpireTime(): string | undefined {
    if (expirePreset.value === 'never') return undefined
    const days = expirePreset.value === '1d' ? 1 : expirePreset.value === '30d' ? 30 : 7
    const d = new Date(Date.now() + days * 24 * 60 * 60 * 1000)
    const pad = (n: number) => String(n).padStart(2, '0')
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
  }

  async function handleCreate() {
    if (!props.file?.fileId) return
    creating.value = true
    try {
      createdShare.value = await createFileShare({
        fileId: props.file.fileId,
        password: password.value || undefined,
        expireTime: resolveExpireTime(),
        maxViews: maxViews.value > 0 ? maxViews.value : undefined,
        allowDownload: allowDownload.value
      })
      ElMessage.success('分享已创建')
      existingShares.value = (await fetchFileShares(props.file.fileId)) || []
    } finally {
      creating.value = false
    }
  }

  async function copyLink() {
    try {
      await navigator.clipboard.writeText(displayShareUrl.value)
      ElMessage.success('链接已复制')
    } catch {
      ElMessage.error('复制失败')
    }
  }

  async function handleRevoke(shareId: number) {
    await ElMessageBox.confirm('确认取消该分享链接？', '提示', { type: 'warning' })
    await revokeFileShare(shareId)
    ElMessage.success('已取消')
    if (props.file?.fileId) {
      existingShares.value = (await fetchFileShares(props.file.fileId)) || []
    }
    if (createdShare.value?.shareId === shareId) {
      createdShare.value = null
    }
  }
</script>

<style lang="scss" scoped>
  .access-code-field {
    display: flex;
    gap: 8px;
    align-items: center;
    width: 100%;

    :deep(.el-input) {
      flex: 1;
    }

    :deep(.is-masked input) {
      -webkit-text-security: disc;
    }
  }

  .access-code-toggle {
    flex-shrink: 0;
  }

  .share-file-name {
    margin-bottom: 16px;
    overflow: hidden;
    font-size: 14px;
    font-weight: 600;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .form-hint {
    margin-left: 8px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }

  .share-result {
    padding: 12px;
    margin-top: 8px;
    background: var(--el-fill-color-light);
    border-radius: 8px;

    &__label {
      margin-bottom: 8px;
      font-size: 13px;
      font-weight: 600;
    }

    &__row {
      display: flex;
      gap: 8px;
    }
  }

  .share-list {
    margin-top: 16px;

    &__title {
      margin-bottom: 8px;
      font-size: 13px;
      font-weight: 600;
    }

    &__item {
      display: flex;
      gap: 8px;
      align-items: center;
      justify-content: space-between;
      padding: 8px 0;
      border-bottom: 1px solid var(--el-border-color-lighter);
    }

    &__meta {
      display: flex;
      flex: 1;
      flex-direction: column;
      gap: 4px;
      min-width: 0;
      font-size: 12px;
    }

    &__sub {
      color: var(--el-text-color-secondary);
    }
  }
</style>
