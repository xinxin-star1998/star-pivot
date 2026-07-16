import request from '@/utils/http'
import type { BlobFullResponse } from '@/utils/http'
import {
  downloadBlob,
  getContentDisposition,
  getFilenameFromContentDisposition
} from '@/utils/common/file'
import type {
  SysFile,
  SysFileHashCheckParams,
  SysFileHashCheckResult,
  SysFileMultipartCompleteParams,
  SysFileMultipartInitParams,
  SysFileMultipartInitResult,
  SysFileQueryParams,
  SysFileRecycleQueryParams,
  SysFileRenameParams,
  SysFileTag,
  SysFileAudit,
  SysFileAuditQueryParams,
  SysFileUsageSummary,
  SysFileVersion,
  SysFileWatermark
} from './types'

export function fetchFileList(params: SysFileQueryParams) {
  return request.post<Api.Common.PageResponse<SysFile>>({
    url: '/api/file/list',
    data: params
  })
}

export function fetchFileDetail(fileId: number) {
  return request.get<SysFile>({
    url: `/api/file/${fileId}`
  })
}

export function uploadFile(formData: FormData) {
  return request.post<SysFile>({
    url: '/api/file/upload',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function checkFileHash(data: SysFileHashCheckParams) {
  return request.post<SysFileHashCheckResult>({
    url: '/api/file/check-hash',
    data
  })
}

export function initMultipartUpload(data: SysFileMultipartInitParams) {
  return request.post<SysFileMultipartInitResult>({
    url: '/api/file/multipart/init',
    data
  })
}

export function uploadMultipartPart(formData: FormData) {
  return request.post<{ etag: string; partNumber: string }>({
    url: '/api/file/multipart/part',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 120000
  })
}

export function completeMultipartUpload(data: SysFileMultipartCompleteParams) {
  return request.post<SysFile>({
    url: '/api/file/multipart/complete',
    data
  })
}

export function abortMultipartUpload(uploadId: string, objectName: string) {
  return request.del({
    url: '/api/file/multipart/abort',
    params: { uploadId, objectName }
  })
}

export function fetchMultipartStatus(uploadId: string, objectName: string) {
  return request.get<SysFileMultipartInitResult>({
    url: '/api/file/multipart/status',
    params: { uploadId, objectName }
  })
}

export function fetchFilePreviewUrl(fileId: number) {
  return request.get<{ url: string; objectName: string; mode?: string; viewerUrl?: string }>({
    url: `/api/file/preview-url/${fileId}`
  })
}

export function deleteFiles(ids: number[]) {
  return request.del({
    url: '/api/file/remove',
    data: { ids }
  })
}

export function purgeFiles(ids: number[]) {
  return request.del({
    url: '/api/file/purge',
    data: { ids }
  })
}

export function clearRecycleBin() {
  return request.del<number>({
    url: '/api/file/recycle/clear'
  })
}

export function restoreFiles(ids: number[]) {
  return request.put({
    url: '/api/file/restore',
    data: { ids }
  })
}

export function fetchRecycleList(params: SysFileRecycleQueryParams) {
  return request.post<Api.Common.PageResponse<SysFile>>({
    url: '/api/file/recycle/list',
    data: params
  })
}

export function moveFiles(ids: number[], targetFolderId: number) {
  return request.put({
    url: '/api/file/move',
    data: { ids, targetFolderId }
  })
}

export function renameFile(data: SysFileRenameParams) {
  return request.put({
    url: '/api/file/rename',
    data
  })
}

export function toggleFileFavorite(fileId: number) {
  return request.put<{ favorited: boolean }>({
    url: `/api/file/favorite/${fileId}`
  })
}

export function touchFileRecent(fileId: number) {
  return request.put({
    url: `/api/file/recent/${fileId}`
  })
}

export function fetchFileTagList() {
  return request.get<SysFileTag[]>({
    url: '/api/file/tag/list'
  })
}

export function createFileTag(data: { tagName: string; tagColor?: string; remark?: string }) {
  return request.post<SysFileTag>({
    url: '/api/file/tag',
    data
  })
}

export function updateFileTag(data: {
  tagId: number
  tagName: string
  tagColor?: string
  remark?: string
}) {
  return request.put<SysFileTag>({
    url: '/api/file/tag',
    data
  })
}

export function deleteFileTag(tagId: number) {
  return request.del({
    url: `/api/file/tag/${tagId}`
  })
}

export function bindFileTags(fileIds: number[], tagIds: number[]) {
  return request.put({
    url: '/api/file/tag/bind',
    data: { fileIds, tagIds }
  })
}

export function unbindFileTags(fileIds: number[], tagIds: number[]) {
  return request.put({
    url: '/api/file/tag/unbind',
    data: { fileIds, tagIds }
  })
}

export async function downloadFilesZip(ids: number[]) {
  const response = await request.post<BlobFullResponse>({
    url: '/api/file/download/zip',
    data: { ids },
    responseType: 'blob',
    returnFullResponse: true,
    timeout: 300000
  })
  const disposition = getContentDisposition(response.headers)
  const filename =
    getFilenameFromContentDisposition(disposition) || `files-${Date.now()}.zip`
  downloadBlob(response.data, filename)
}

export function fetchFileVersions(fileId: number) {
  return request.get<SysFileVersion[]>({
    url: `/api/file/${fileId}/versions`
  })
}

export function uploadFileVersion(fileId: number, formData: FormData) {
  return request.post<SysFile>({
    url: `/api/file/${fileId}/version`,
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 120000
  })
}

export function restoreFileVersion(fileId: number, versionId: number) {
  return request.post<SysFile>({
    url: `/api/file/${fileId}/versions/${versionId}/restore`
  })
}

export function deleteFileVersion(fileId: number, versionId: number) {
  return request.del({
    url: `/api/file/${fileId}/versions/${versionId}`
  })
}

export function fetchFileUsageStats(groupBy: 'user' | 'dept' = 'user') {
  return request.get<SysFileUsageSummary>({
    url: '/api/file/stats/usage',
    params: { groupBy }
  })
}

export function fetchFileAuditList(params: SysFileAuditQueryParams) {
  return request.post<Api.Common.PageResponse<SysFileAudit>>({
    url: '/api/file/audit/list',
    data: params
  })
}

export function fetchFileWatermarkConfig() {
  return request.get<SysFileWatermark>({
    url: '/api/file/watermark/config'
  })
}

export async function downloadFileWatermarked(fileId: number, fileName?: string) {
  const response = await request.get<BlobFullResponse>({
    url: `/api/file/download/watermarked/${fileId}`,
    responseType: 'blob',
    returnFullResponse: true,
    timeout: 120000
  })
  const disposition = getContentDisposition(response.headers)
  const filename =
    getFilenameFromContentDisposition(disposition) || fileName || `file-${fileId}`
  downloadBlob(response.data, filename)
}
