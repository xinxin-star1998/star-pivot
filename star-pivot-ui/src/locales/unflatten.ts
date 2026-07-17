/**
 * 将扁平 key（如 login.title / setting.list.0）还原为嵌套对象/数组
 */
export function unflattenMessages(flat: Record<string, string>): Record<string, unknown> {
  const root: Record<string, unknown> = {}

  Object.entries(flat).forEach(([path, value]) => {
    if (!path) {
      return
    }
    const parts = path.split('.')
    let cursor: Record<string, unknown> | unknown[] = root

    parts.forEach((part, index) => {
      const isLast = index === parts.length - 1
      const nextIsIndex = !isLast && /^\d+$/.test(parts[index + 1] || '')
      const asIndex = /^\d+$/.test(part)

      if (isLast) {
        if (Array.isArray(cursor) && asIndex) {
          cursor[Number(part)] = value
        } else if (!Array.isArray(cursor)) {
          cursor[part] = value
        }
        return
      }

      if (Array.isArray(cursor) && asIndex) {
        const idx = Number(part)
        if (cursor[idx] == null) {
          cursor[idx] = nextIsIndex ? [] : {}
        }
        cursor = cursor[idx] as Record<string, unknown> | unknown[]
        return
      }

      if (!Array.isArray(cursor)) {
        if (cursor[part] == null) {
          cursor[part] = nextIsIndex ? [] : {}
        }
        cursor = cursor[part] as Record<string, unknown> | unknown[]
      }
    })
  })

  return root
}
