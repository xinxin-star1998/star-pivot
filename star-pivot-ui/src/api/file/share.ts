import request from '@/utils/http'
import type { SysFileShare, SysFileShareCreateParams, SysFileSharePublic } from './types'

export function createFileShare(data: SysFileShareCreateParams) {
  return request.post<SysFileShare>({
    url: '/api/file/share',
    data
  })
}

export function fetchFileShares(fileId: number) {
  return request.get<SysFileShare[]>({
    url: `/api/file/share/file/${fileId}`
  })
}

export function fetchMyShares() {
  return request.get<SysFileShare[]>({
    url: '/api/file/share/mine'
  })
}

export function revokeFileShare(shareId: number) {
  return request.del({
    url: `/api/file/share/${shareId}`
  })
}

export function fetchShareMeta(shareCode: string) {
  return request.get<SysFileSharePublic>({
    url: `/api/file/share/public/${shareCode}`,
    // 公开接口，不强制带 token 提示
    showErrorMessage: true
  })
}

export function unlockShare(shareCode: string, password?: string) {
  return request.post<SysFileSharePublic>({
    url: `/api/file/share/public/${shareCode}/unlock`,
    data: { password }
  })
}
