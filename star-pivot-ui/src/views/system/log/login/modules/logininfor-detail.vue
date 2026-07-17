<!-- 登录日志详情对话框 -->
<template>
  <ElDialog
    v-model="dialogVisible"
    :title="t('system.operLog.detail')"
    width="800px"
    :close-on-click-modal="false"
    @update:model-value="handleDialogChange"
  >
    <ElDescriptions :column="2" border v-if="logininfor">
      <ElDescriptionsItem label="ID">{{ logininfor.infoId }}</ElDescriptionsItem>
      <ElDescriptionsItem :label="t('system.loginLog.userName')">{{
        logininfor.userName
      }}</ElDescriptionsItem>
      <ElDescriptionsItem :label="t('system.loginLog.status')">
        <ElTag :type="logininfor.status === '0' ? 'success' : 'danger'">
          {{
            logininfor.status === '0'
              ? t('system.loginLog.statusSuccess')
              : t('system.loginLog.statusFail')
          }}
        </ElTag>
      </ElDescriptionsItem>
      <ElDescriptionsItem :label="t('system.loginLog.loginTime')">{{
        logininfor.loginTime
      }}</ElDescriptionsItem>
      <ElDescriptionsItem :label="t('system.loginLog.ipaddr')">{{
        logininfor.ipaddr
      }}</ElDescriptionsItem>
      <ElDescriptionsItem :label="t('system.loginLog.loginLocation')">
        {{ logininfor.loginLocation || t('common.empty') }}
      </ElDescriptionsItem>
      <ElDescriptionsItem :label="t('system.loginLog.browser')">
        {{ logininfor.browser || t('common.empty') }}
      </ElDescriptionsItem>
      <ElDescriptionsItem :label="t('system.loginLog.os')">
        {{ logininfor.os || t('common.empty') }}
      </ElDescriptionsItem>
      <ElDescriptionsItem :label="t('system.loginLog.msg')" :span="2">
        <ElText :type="logininfor.status === '0' ? 'success' : 'danger'">
          {{ logininfor.msg || t('common.empty') }}
        </ElText>
      </ElDescriptionsItem>
    </ElDescriptions>
  </ElDialog>
</template>

<script setup lang="ts">
  import { ElTag, ElText, ElDescriptions, ElDescriptionsItem } from 'element-plus'
  import { useI18n } from 'vue-i18n'
  import type { LogininforListItem } from '@/types/api/logininfor'

  interface Props {
    visible: boolean
    logininfor: LogininforListItem | null
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
  }

  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()
  const { t } = useI18n()

  const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
  })

  const handleDialogChange = (value: boolean) => {
    emit('update:visible', value)
  }
</script>

<style scoped lang="scss"></style>
