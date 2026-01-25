import request from '@/utils/http'
import { downloadBlob, getFilenameFromContentDisposition } from '@/utils/common/file'
import { ElMessage } from 'element-plus'

/**
 * 通用导入导出 API
 * 提供统一的导入导出接口，支持不同业务模块的导入导出功能
 */

/**
 * 导入数据
 * @param businessType 业务类型标识（如：user、dept、role 等）
 * @param data Excel 解析后的行数据列表
 * @param options 导入选项（如：是否覆盖已存在数据等）
 */
export function fetchImportData(
  businessType: string,
  data: Array<Record<string, unknown>>,
  options?: Record<string, unknown>
) {
  return request.post<{
    successCount: number
    failCount: number
    errorMessages?: string[]
  }>({
    url: `/api/common/import-export/import/${businessType}`,
    data: {
      rowList: data,
      options: options || {}
    }
  })
}

/**
 * 导出数据并自动下载
 * @param businessType 业务类型标识
 * @param queryParams 查询参数
 * @param filename 可选的文件名（如果不提供，将从响应头获取）
 */
export async function fetchExportData(
  businessType: string,
  queryParams?: Record<string, unknown>,
  filename?: string
) {
  try {
    const response = await request.post<Blob>({
      url: `/api/common/import-export/export/${businessType}`,
      data: queryParams || {},
      responseType: 'blob'
    })

    // 从响应头获取文件名
    const contentDisposition = (response as any).headers?.['content-disposition']
    const finalFilename = filename || getFilenameFromContentDisposition(contentDisposition)

    // 下载文件
    downloadBlob(response as Blob, finalFilename)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败：' + (error as Error).message)
    throw error
  }
}

/**
 * 下载导入模板并自动下载
 * @param businessType 业务类型标识
 * @param filename 可选的文件名（如果不提供，将从响应头获取）
 */
export async function fetchDownloadTemplate(businessType: string, filename?: string) {
  try {
    const response = await request.get<Blob>({
      url: `/api/common/import-export/template/${businessType}`,
      responseType: 'blob'
    })

    // 从响应头获取文件名
    const contentDisposition = (response as any).headers?.['content-disposition']
    const finalFilename = filename || getFilenameFromContentDisposition(contentDisposition)

    // 下载文件
    downloadBlob(response as Blob, finalFilename)
    ElMessage.success('模板下载成功')
  } catch (error) {
    ElMessage.error('模板下载失败：' + (error as Error).message)
    throw error
  }
}
