/** 可选值分隔符（与谷粒商城库表约定一致） */
export const VALUE_SELECT_SEPARATOR = ';'

/**
 * 库表字符串 → 多选标签数组。
 * 优先按分号拆分；若无分号则兼容英文/中文逗号的历史数据。
 */
export function parseValueSelect(raw?: string | null): string[] {
  const text = raw?.trim()
  if (!text) return []
  const parts = text.includes(VALUE_SELECT_SEPARATOR)
    ? text.split(VALUE_SELECT_SEPARATOR)
    : text.split(/[,，]/)
  return parts.map((s) => s.trim()).filter(Boolean)
}

/** 有可选值串但 value_type=0 时，回显按多值处理（兼容谷粒历史数据） */
export function normalizeAttrValueFields(
  valueType: number | undefined | null,
  valueSelectRaw?: string | null
): { valueType: number; valueSelect: string } {
  const valueSelect = valueSelectRaw?.trim() ?? ''
  const tags = parseValueSelect(valueSelect)
  let vt = valueType != null && !Number.isNaN(Number(valueType)) ? Number(valueType) : 0
  if (tags.length > 0 && vt !== 1) vt = 1
  return { valueType: vt, valueSelect: vt === 1 ? valueSelect : '' }
}

/** 多选标签数组 → 库表字符串（分号拼接） */
export function joinValueSelect(tags: string[]): string | undefined {
  const list = tags.map((s) => s.trim()).filter(Boolean)
  if (!list.length) return undefined
  return list.join(VALUE_SELECT_SEPARATOR)
}
