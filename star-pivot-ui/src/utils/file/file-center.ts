import type { SysFile } from '@/api/file/types'

export type PreviewMode = 'image' | 'video' | 'audio' | 'pdf' | 'office' | 'download'

const OFFICE_EXTS = new Set(['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'csv', 'rtf'])

export function formatFileSize(bytes?: number): string {
  if (bytes == null || bytes <= 0) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let size = bytes
  let i = 0
  while (size >= 1024 && i < units.length - 1) {
    size /= 1024
    i++
  }
  return `${size.toFixed(i === 0 ? 0 : 1)} ${units[i]}`
}

export function getPreviewMode(mediaType?: string, fileExt?: string): PreviewMode {
  switch (mediaType) {
    case 'IMAGE':
      return 'image'
    case 'VIDEO':
      return 'video'
    case 'AUDIO':
      return 'audio'
    case 'DOCUMENT': {
      const ext = fileExt?.toLowerCase()
      if (ext === 'pdf') return 'pdf'
      if (ext && OFFICE_EXTS.has(ext)) return 'office'
      return 'download'
    }
    default:
      return 'download'
  }
}

export function buildOfficeViewerUrl(fileUrl: string): string {
  return `https://view.officeapps.live.com/op/embed.aspx?src=${encodeURIComponent(fileUrl)}`
}

export function resolveFileDisplayUrl(file: SysFile): string {
  return file.displayUrl || ''
}

/** 触发浏览器下载/新窗口打开 */
export function openFileUrl(url: string, fileName?: string) {
  if (!url) return
  const link = document.createElement('a')
  link.href = url
  link.target = '_blank'
  if (fileName) link.download = fileName
  link.rel = 'noopener noreferrer'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}
