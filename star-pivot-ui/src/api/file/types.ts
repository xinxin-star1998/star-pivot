/** 文件中心类型定义 */

export interface SysFileFolder {
  folderId?: number
  category?: string
  folderName?: string
  parentId?: number
  orderNum?: number
  status?: string
  fileCount?: number
  children?: SysFileFolder[]
}

export interface FileCategoryNode {
  category: string
  categoryLabel: string
  defaultFolderId?: number
  children: SysFileFolder[]
}

export interface SysFileTag {
  tagId?: number
  tagName?: string
  tagColor?: string
  fileId?: number
  remark?: string
  createTime?: string
}

export interface SysFile {
  fileId?: number
  folderId?: number
  category?: string
  categoryLabel?: string
  mediaType?: string
  mediaTypeLabel?: string
  fileName?: string
  fileExt?: string
  contentType?: string
  fileSize?: number
  objectName?: string
  storageProvider?: string
  bizType?: string
  bizId?: string
  displayUrl?: string
  previewMode?: 'image' | 'video' | 'audio' | 'pdf' | 'office' | 'download'
  createBy?: string
  createTime?: string
  remark?: string
  folderName?: string
  updateBy?: string
  updateTime?: string
  deleteBy?: string
  deleteTime?: string
  favorited?: boolean
  tags?: SysFileTag[]
}

export interface SysFileQueryParams {
  pageNum?: number
  pageSize?: number
  folderId?: number
  category?: string
  mediaType?: string
  fileName?: string
  /** 增强检索：文件名/备注/标签 */
  keyword?: string
  createBy?: string
  beginTime?: string
  endTime?: string
  /** all | favorite | recent */
  listScope?: string
  tagId?: number
}

export interface SysFileAudit {
  auditId?: number
  fileId?: number
  fileName?: string
  action?: string
  actionLabel?: string
  detail?: string
  operBy?: string
  operByUserId?: number
  operIp?: string
  operTime?: string
}

export interface SysFileAuditQueryParams {
  pageNum?: number
  pageSize?: number
  action?: string
  fileName?: string
  operBy?: string
  fileId?: number
  beginTime?: string
  endTime?: string
}

export interface SysFileRecycleQueryParams {
  pageNum?: number
  pageSize?: number
  category?: string
  fileName?: string
  deleteBy?: string
  beginTime?: string
  endTime?: string
}

export interface SysFileFolderForm {
  folderId?: number
  category?: string
  folderName?: string
  parentId?: number
  orderNum?: number
  status?: string
  remark?: string
}

export interface SysFileShareCreateParams {
  fileId: number
  password?: string
  expireTime?: string
  maxViews?: number
  allowDownload?: boolean
  remark?: string
}

export interface SysFileShare {
  shareId?: number
  fileId?: number
  shareCode?: string
  shareUrl?: string
  hasPassword?: boolean
  expireTime?: string
  maxViews?: number
  viewCount?: number
  allowDownload?: boolean
  status?: string
  fileName?: string
  mediaType?: string
  createBy?: string
  createTime?: string
}

export interface SysFileSharePublic {
  shareCode?: string
  fileName?: string
  mediaType?: string
  mediaTypeLabel?: string
  fileSize?: number
  fileExt?: string
  hasPassword?: boolean
  expired?: boolean
  allowDownload?: boolean
  unlocked?: boolean
  url?: string
  previewMode?: string
  viewerUrl?: string
  watermark?: SysFileWatermark
}

export interface SysFileWatermark {
  enabled?: boolean
  content?: string
  fontSize?: number
  fontColor?: string
  rotate?: number
  gapX?: number
  gapY?: number
  downloadEnabled?: boolean
}

export interface SysFileRenameParams {
  fileId: number
  fileName: string
}

export interface SysFileHashCheckParams {
  fileHash: string
  fileSize: number
  folderId: number
  fileName?: string
  bizType?: string
  bizId?: string
  remark?: string
}

export interface SysFileHashCheckResult {
  instant: boolean
  file?: SysFile
}

export interface SysFileMultipartInitParams {
  folderId: number
  fileName: string
  fileSize: number
  contentType?: string
  fileHash?: string
  bizType?: string
  bizId?: string
  remark?: string
}

export interface SysFileMultipartInitResult {
  uploadId: string
  objectName: string
  partSize: number
  uploadedParts?: number[]
  uploadedPartDetails?: Array<{ partNumber: number; etag: string }>
}

export interface SysFileMultipartCompleteParams {
  uploadId: string
  objectName: string
  folderId: number
  fileName: string
  fileSize: number
  contentType?: string
  fileHash?: string
  mediaType?: string
  bizType?: string
  bizId?: string
  remark?: string
  parts: Array<{ partNumber: number; etag: string }>
}

export interface SysFileVersion {
  versionId?: number
  fileId?: number
  versionNo?: number
  fileName?: string
  fileSize?: number
  fileHash?: string
  contentType?: string
  current?: boolean
  createBy?: string
  createTime?: string
  remark?: string
  displayUrl?: string
}

export interface SysFileUsageStatItem {
  groupId?: number
  groupName?: string
  fileCount?: number
  totalBytes?: number
  uniqueObjects?: number
}

export interface SysFileUsageSummary {
  fileCount?: number
  totalBytes?: number
  uniqueObjects?: number
  items?: SysFileUsageStatItem[]
}

