/**
 * 计算文件 SHA-256（十六进制）。超大文件失败时返回空字符串。
 */
export async function computeFileSha256(file: Blob): Promise<string> {
  try {
    const buffer = await file.arrayBuffer()
    const digest = await crypto.subtle.digest('SHA-256', buffer)
    return Array.from(new Uint8Array(digest))
      .map((b) => b.toString(16).padStart(2, '0'))
      .join('')
  } catch {
    return ''
  }
}

/** 默认分片阈值 5MB */
export const DEFAULT_MULTIPART_THRESHOLD = 5 * 1024 * 1024
