import request from '@/utils/http'

export interface SysLang {
  langId?: number
  langCode: string
  langName: string
  isDefault?: string
  status?: string
  orderNum?: number
}

export interface SysLangForm {
  langId?: number
  langCode: string
  langName: string
  isDefault?: string
  status?: string
  orderNum?: number
}

export interface I18nCoverageMissingItem {
  resourceKey: string
  baseContent: string
}

export interface I18nCoverage {
  namespace: string
  baseLang: string
  targetLang: string
  fieldName: string
  total: number
  translated: number
  missing: number
  coverageRate: number
  missingItems: I18nCoverageMissingItem[]
}

export interface I18nImportPayload {
  namespace: string
  fieldName?: string
  overwrite?: boolean
  lang?: string
  bundle?: Record<string, string>
  items?: Array<{ resourceKey: string; lang: string; content?: string }>
}

/** 启用语言列表 */
export function fetchI18nLangList() {
  return request.get<SysLang[]>({
    url: '/api/system/i18n/lang/list'
  })
}

/** 全部语言（含停用） */
export function fetchI18nLangAll() {
  return request.get<SysLang[]>({
    url: '/api/system/i18n/lang/all'
  })
}

/** 新增语言 */
export function fetchAddI18nLang(data: SysLangForm) {
  return request.post({
    url: '/api/system/i18n/lang',
    data
  })
}

/** 修改语言 */
export function fetchUpdateI18nLang(data: SysLangForm) {
  return request.put({
    url: '/api/system/i18n/lang',
    data
  })
}

/** 启停语言 */
export function fetchUpdateI18nLangStatus(langId: number, status: string) {
  return request.put({
    url: `/api/system/i18n/lang/${langId}/status?status=${encodeURIComponent(status)}`
  })
}

/** 查询资源多语言 */
export function fetchI18nResource(params: {
  namespace: string
  resourceKey: string
  fieldName?: string
}) {
  return request.get<Record<string, string>>({
    url: '/api/system/i18n/resource',
    params: {
      fieldName: 'menu_name',
      ...params
    }
  })
}

/** 保存资源多语言 */
export function fetchSaveI18nResource(data: {
  namespace: string
  resourceKey: string
  fieldName?: string
  translations: Record<string, string>
}) {
  return request.put({
    url: '/api/system/i18n/resource',
    data: {
      fieldName: '_',
      ...data
    }
  })
}

/** 公开拉取 UI 语言包（登录前可用） */
export function fetchUiI18nBundle(lang: string) {
  return request.get<Record<string, string>>({
    url: '/api/system/i18n/bundle/ui',
    params: { lang }
  })
}

/** 按命名空间拉取语言包（需登录） */
export function fetchI18nBundle(namespace: string, lang: string) {
  return request.get<Record<string, string>>({
    url: '/api/system/i18n/bundle',
    params: { namespace, lang }
  })
}

/** 翻译覆盖率 */
export function fetchI18nCoverage(params: { namespace: string; lang: string; fieldName?: string }) {
  return request.get<I18nCoverage>({
    url: '/api/system/i18n/coverage',
    params
  })
}

/** 导入语言包 */
export function fetchImportI18n(data: I18nImportPayload) {
  return request.post({
    url: '/api/system/i18n/import',
    data
  })
}

/** 导出语言包（带鉴权下载） */
export async function downloadI18nExport(namespace: string, lang: string) {
  const blob = await request.get<Blob>({
    url: '/api/system/i18n/export',
    params: { namespace, lang },
    responseType: 'blob'
  })
  const fileName = `i18n-${namespace}-${lang}.json`
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  link.click()
  window.URL.revokeObjectURL(url)
}
