import type { FileCategoryNode, SysFileFolder } from '@/api/file/types'

export interface FolderCascaderOption {
  value: string | number
  label: string
  children?: FolderCascaderOption[]
  disabled?: boolean
}

/** 递归映射文件夹树为 Cascader options */
export function mapFolderCascaderOptions(
  folders: SysFileFolder[] = [],
  excludeFolderId?: number
): FolderCascaderOption[] {
  return folders
    .filter((f) => f.folderId != null && f.folderId !== excludeFolderId)
    .map((f) => {
      const children = mapFolderCascaderOptions(f.children || [], excludeFolderId)
      return {
        value: f.folderId!,
        label: f.folderName || '',
        children: children.length ? children : undefined
      }
    })
}

/** 分类 + 文件夹树 → Cascader（第一级为分类） */
export function mapCategoryCascaderOptions(
  categories: FileCategoryNode[],
  excludeFolderId?: number
): FolderCascaderOption[] {
  return categories.map((cat) => ({
    value: cat.category,
    label: cat.categoryLabel,
    children: mapFolderCascaderOptions(cat.children || [], excludeFolderId)
  }))
}

/** Cascader path 最后一段为 folderId */
export function resolveFolderIdFromPath(path?: Array<string | number>): number | undefined {
  if (!path?.length) return undefined
  const last = path[path.length - 1]
  const id = typeof last === 'number' ? last : Number(last)
  return Number.isFinite(id) ? id : undefined
}

/** DFS 查找文件夹 */
export function findFolderInTree(
  categories: FileCategoryNode[],
  folderId: number
): { category: string; folder: SysFileFolder; pathNames: string[] } | null {
  for (const cat of categories) {
    const found = findFolderRecursive(cat.children || [], folderId, [])
    if (found) {
      return { category: cat.category, folder: found.folder, pathNames: [cat.categoryLabel, ...found.pathNames] }
    }
  }
  return null
}

function findFolderRecursive(
  folders: SysFileFolder[],
  folderId: number,
  pathNames: string[]
): { folder: SysFileFolder; pathNames: string[] } | null {
  for (const folder of folders) {
    const nextPath = [...pathNames, folder.folderName || '']
    if (folder.folderId === folderId) {
      return { folder, pathNames: nextPath }
    }
    const child = findFolderRecursive(folder.children || [], folderId, nextPath)
    if (child) return child
  }
  return null
}

/** 汇总文件夹树文件数 */
export function sumFolderFileCount(folders: SysFileFolder[] = []): number {
  return folders.reduce(
    (sum, f) => sum + (f.fileCount || 0) + sumFolderFileCount(f.children || []),
    0
  )
}

/** 递归映射为树节点 */
export function mapFoldersToTreeNodes(
  folders: SysFileFolder[],
  category: string
): Array<{
  nodeKey: string
  label: string
  folderId?: number
  folderName?: string
  category: string
  fileCount?: number
  children?: ReturnType<typeof mapFoldersToTreeNodes>
}> {
  return folders.map((folder) => ({
    nodeKey: `folder-${folder.folderId}`,
    label: folder.folderName || '',
    folderName: folder.folderName,
    folderId: folder.folderId,
    category,
    fileCount: folder.fileCount,
    children: folder.children?.length ? mapFoldersToTreeNodes(folder.children, category) : undefined
  }))
}
