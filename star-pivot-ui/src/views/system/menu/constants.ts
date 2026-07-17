/**
 * 菜单管理页面常量配置
 */
import type { ComposerTranslation } from 'vue-i18n'

/**
 * 菜单类型配置
 */
export const getMenuTypeConfig = (t: ComposerTranslation) =>
  ({
    M: { text: t('system.menu.typeDir'), color: 'info' as const },
    C: { text: t('system.menu.typeMenu'), color: 'primary' as const },
    F: { text: t('system.menu.typeButton'), color: 'danger' as const }
  }) as const

/**
 * 状态配置
 */
export const getStatusConfig = (t: ComposerTranslation) =>
  ({
    '0': { text: t('common.normal'), type: 'success' as const },
    '1': { text: t('common.disabled'), type: 'danger' as const }
  }) as const

/**
 * 初始搜索状态
 */
export const INITIAL_SEARCH_STATE = {
  menuName: '',
  route: '',
  perms: '',
  status: ''
} as const
