import { useWindowSize } from '@vueuse/core'
import { computed } from 'vue'

export interface MobileFormLayoutOptions {
  /** 桌面端标签位置，默认 right */
  desktopPosition?: 'left' | 'right' | 'top'
  /** 桌面端标签宽度，默认 100px */
  desktopWidth?: string | number
  /** 窄屏断点，默认 768 */
  breakpoint?: number
}

/**
 * 弹窗/表单在窄屏下自动切换为顶部标签，避免左标签挤压输入区。
 */
export function useMobileFormLayout(options: MobileFormLayoutOptions = {}) {
  const { desktopPosition = 'right', desktopWidth = '100px', breakpoint = 768 } = options

  const { width } = useWindowSize()
  const isMobile = computed(() => width.value < breakpoint)

  const labelPosition = computed(() => (isMobile.value ? 'top' : desktopPosition))
  const labelWidth = computed(() => (isMobile.value ? 'auto' : desktopWidth))
  const dialogWidth = computed(() => (isMobile.value ? '95%' : undefined))

  return {
    isMobile,
    labelPosition,
    labelWidth,
    dialogWidth
  }
}
