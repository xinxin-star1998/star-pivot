/**
 * 演示模式：与后端 DemoModeUtils 保持一致的只读请求判定。
 */

const READ_ONLY_POST_PATH =
  /\/(list|pageList|allocatedList|unallocatedList|recycle\/list|log\/list)(\/|$)/i

const AUTH_ALLOW_POST = ['/auth/refresh', '/auth/logout', '/auth/captcha/verify']

function normalizePath(url?: string): string {
  if (!url) return ''
  const withoutQuery = url.split('?')[0]
  return withoutQuery.replace(/^\/api(\/v1)?/, '') || withoutQuery
}

/** 演示账号允许的 AI 对话写接口（与后端 DemoModeUtils 保持一致） */
function isAiChatDemoAllowed(method: string, path: string): boolean {
  if (!path.startsWith('/ai/chat/')) {
    return false
  }
  if (method === 'POST') {
    return (
      path === '/ai/chat/send' || path === '/ai/chat/stream' || path === '/ai/chat/sessions'
    )
  }
  if (method === 'PUT') {
    return path === '/ai/chat/sessions/rename'
  }
  if (method === 'DELETE') {
    return path === '/ai/chat/sessions' || path === '/ai/chat/history'
  }
  return false
}

/** 演示账号下是否允许发出的 HTTP 请求 */
export function isDemoReadOnlyRequest(method?: string, url?: string): boolean {
  const normalizedMethod = (method || 'GET').toUpperCase()
  if (normalizedMethod === 'GET' || normalizedMethod === 'HEAD' || normalizedMethod === 'OPTIONS') {
    return true
  }

  const path = normalizePath(url)
  if (AUTH_ALLOW_POST.some((item) => path.includes(item))) {
    return true
  }

  if (isAiChatDemoAllowed(normalizedMethod, path)) {
    return true
  }

  if (normalizedMethod === 'POST') {
    if (READ_ONLY_POST_PATH.test(path)) {
      return true
    }
    return path.includes('/presigned-urls') || path.includes('/captcha/verify')
  }

  return false
}

export const DEMO_MODE_DENIED_MESSAGE = '演示模式，不允许操作'

export function isDemoWriteRequest(method?: string, url?: string): boolean {
  const normalizedMethod = (method || 'GET').toUpperCase()
  if (!['POST', 'PUT', 'DELETE', 'PATCH'].includes(normalizedMethod)) {
    return false
  }
  return !isDemoReadOnlyRequest(normalizedMethod, url)
}
