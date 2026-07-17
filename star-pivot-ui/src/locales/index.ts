/**
 * 国际化配置
 *
 * 基于 vue-i18n 实现的多语言国际化解决方案。
 * 语言列表从后端动态拉取（失败时回退本地中/英）；文案优先合并库表 UI 包。
 *
 * @module locales
 */

import type { I18n, I18nOptions } from 'vue-i18n'
import { createI18n } from 'vue-i18n'
import { computed, type ComputedRef, ref } from 'vue'
import type { Language } from 'element-plus/es/locale'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import en from 'element-plus/es/locale/lang/en'
import ja from 'element-plus/es/locale/lang/ja'
import ko from 'element-plus/es/locale/lang/ko'
import zhTw from 'element-plus/es/locale/lang/zh-tw'
import { LanguageEnum } from '@/enums/appEnum'
import { getSystemStorage } from '@/utils/storage'
import { StorageKeyManager } from '@/utils/storage/storage-key-manager'
import { unflattenMessages } from './unflatten'

// 同步导入语言文件（本地兜底；库表 UI 包可覆盖）
import enMessages from './langs/en.json'
import zhMessages from './langs/zh.json'

/**
 * 存储键管理器实例
 */
const storageKeyManager = new StorageKeyManager()

/**
 * 语言消息对象
 */
const messages = {
  [LanguageEnum.EN]: enMessages,
  [LanguageEnum.ZH]: zhMessages
}

/** Element Plus 语言包映射（第三语言回退英文） */
const ELEMENT_LOCALES: Record<string, Language> = {
  zh: zhCn,
  'zh-cn': zhCn,
  'zh-hans': zhCn,
  'zh-tw': zhTw,
  'zh-hant': zhTw,
  'zh-hk': zhTw,
  en: en,
  'en-us': en,
  'en-gb': en,
  ja: ja,
  'ja-jp': ja,
  ko: ko,
  'ko-kr': ko
}

/**
 * 解析 Element Plus locale（未知语言回退 en）
 */
export function resolveElementLocale(lang?: string): Language {
  if (!lang) return zhCn
  const normalized = lang.trim().toLowerCase().replace('_', '-')
  if (ELEMENT_LOCALES[normalized]) {
    return ELEMENT_LOCALES[normalized]
  }
  const primary = normalized.split('-')[0]
  return ELEMENT_LOCALES[primary] || en
}

export interface LanguageOption {
  value: string
  label: string
}

/** 本地兜底语言选项（接口失败或未返回时使用） */
const FALLBACK_LANGUAGE_OPTIONS: LanguageOption[] = [
  { value: LanguageEnum.ZH, label: '简体中文' },
  { value: LanguageEnum.EN, label: 'English' }
]

/**
 * 语言选项列表（动态，来自后端启用语言；失败回退中/英）
 */
export const languageOptions = ref<LanguageOption[]>([...FALLBACK_LANGUAGE_OPTIONS])

/**
 * 从后端拉取启用语言列表
 */
export async function loadLanguageOptions(): Promise<void> {
  try {
    const { fetchI18nLangList } = await import('@/api/system/i18n')
    const list = await fetchI18nLangList()
    if (!list?.length) {
      return
    }
    languageOptions.value = list.map((item) => ({
      value: normalizeAppLang(item.langCode),
      label: item.langName
    }))
    // 去重（zh-CN / zh 归一后可能重复）
    const seen = new Set<string>()
    languageOptions.value = languageOptions.value.filter((item) => {
      if (seen.has(item.value)) return false
      seen.add(item.value)
      return true
    })
  } catch (error) {
    console.warn('[i18n] 加载语言列表失败，使用本地兜底:', error)
  }
}

/**
 * 规范化语言码：en-US / en_US → en，zh-CN → zh
 */
export function normalizeAppLang(lang?: string): string {
  if (!lang || !String(lang).trim()) {
    return LanguageEnum.ZH
  }
  const value = String(lang).trim().toLowerCase().replace('_', '-')
  const primary = value.split('-')[0]
  return primary || LanguageEnum.ZH
}

/**
 * 从存储中获取语言设置
 * @returns 语言编码，获取失败则返回默认语言
 */
const getDefaultLanguage = (): string => {
  try {
    const storageKey = storageKeyManager.getStorageKey('user')
    const userStore = localStorage.getItem(storageKey)

    if (userStore) {
      const { language } = JSON.parse(userStore)
      if (typeof language === 'string' && language) {
        return normalizeAppLang(language)
      }
    }
  } catch (error) {
    console.warn('[i18n] 从版本化存储获取语言设置失败:', error)
  }

  try {
    const sys = getSystemStorage()
    if (sys) {
      const { user } = JSON.parse(sys)
      if (typeof user?.language === 'string' && user.language) {
        return normalizeAppLang(user.language)
      }
    }
  } catch (error) {
    console.warn('[i18n] 从系统存储获取语言设置失败:', error)
  }

  console.debug('[i18n] 使用默认语言:', LanguageEnum.ZH)
  return LanguageEnum.ZH
}

/**
 * i18n 配置选项
 */
const i18nOptions: I18nOptions = {
  locale: getDefaultLanguage(),
  legacy: false,
  globalInjection: true,
  fallbackLocale: LanguageEnum.ZH,
  messages
}

/**
 * i18n 实例
 */
const i18n: I18n = createI18n(i18nOptions)

/**
 * 翻译函数类型
 */
interface Translation {
  (key: string): string
}

/**
 * 全局翻译函数
 * 可在任何地方使用，无需导入 useI18n
 */
export const $t = i18n.global.t as Translation

/**
 * 当前语言（响应式）
 */
export const currentLocale: ComputedRef<string> = computed(
  () => (i18n.global.locale as { value: string }).value
)

/**
 * 本地 JSON 底稿（深拷贝，避免 merge 污染原始 messages）
 */
function getLocalMessageBase(lang: string): Record<string, unknown> {
  const normalized = normalizeAppLang(lang)
  const base =
    normalized === LanguageEnum.ZH
      ? zhMessages
      : enMessages
  return JSON.parse(JSON.stringify(base)) as Record<string, unknown>
}

/**
 * 确保 vue-i18n 已注册该 locale；新语言码先拷贝本地底稿，再 merge 远程包
 */
function ensureLocaleRegistered(locale: string): void {
  const available = i18n.global.availableLocales as string[]
  if (!available.includes(locale)) {
    i18n.global.setLocaleMessage(locale, getLocalMessageBase(locale))
  }
}

/**
 * 从后端拉取 UI 语言包并合并到 vue-i18n（失败时静默保留本地 JSON）
 */
export async function loadRemoteUiMessages(lang?: string): Promise<void> {
  const locale = normalizeAppLang(
    lang || (i18n.global.locale as { value: string }).value || LanguageEnum.ZH
  )
  ensureLocaleRegistered(locale)
  try {
    const { fetchUiI18nBundle } = await import('@/api/system/i18n')
    const flat = await fetchUiI18nBundle(locale)
    if (!flat || Object.keys(flat).length === 0) {
      return
    }
    const nested = unflattenMessages(flat)
    i18n.global.mergeLocaleMessage(locale, nested)
  } catch (error) {
    console.warn('[i18n] 加载远程 UI 语言包失败，使用本地兜底:', error)
  }
}

/**
 * 切换当前语言并拉取对应 UI 语言包（无整页刷新）
 */
export async function switchAppLanguage(lang: string): Promise<void> {
  const normalized = normalizeAppLang(lang)
  if (!normalized) return
  ensureLocaleRegistered(normalized)
  ;(i18n.global.locale as { value: string }).value = normalized
  document.documentElement.setAttribute('lang', normalized)
  await loadRemoteUiMessages(normalized)
}

export default i18n
