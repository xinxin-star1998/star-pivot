/**
 * useDemoMode - 演示模式状态与写操作校验
 *
 * 演示账号可浏览、可打开编辑弹窗，但提交等写操作会被拦截。
 */
import { computed } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { HttpError, showError } from '@/utils/http/error'
import { DEMO_MODE_DENIED_MESSAGE, isDemoWriteRequest } from '@/utils/demo-mode'
import { $t } from '@/locales'

export const useDemoMode = () => {
  const userStore = useUserStore()

  const isDemoMode = computed(() => userStore.isDemoMode)

  /** 写操作前调用，返回 false 表示已拦截 */
  const assertWritable = (showMessage = true): boolean => {
    if (!isDemoMode.value) {
      return true
    }
    if (showMessage) {
      showError(new HttpError($t('httpMsg.demoModeDenied'), 403))
    }
    return false
  }

  /** HTTP 层拦截演示写请求 */
  const blockDemoWriteRequest = (method?: string, url?: string, showMessage = true): boolean => {
    if (!isDemoMode.value || !isDemoWriteRequest(method, url)) {
      return false
    }
    if (showMessage) {
      showError(new HttpError($t('httpMsg.demoModeDenied'), 403))
    }
    return true
  }

  return {
    isDemoMode,
    assertWritable,
    blockDemoWriteRequest,
    demoDeniedMessage: DEMO_MODE_DENIED_MESSAGE
  }
}
