import request, {type BlobFullResponse} from '@/utils/http'
import {downloadBlob, getFilenameFromContentDisposition} from '@/utils/common/file'

/**
 * 参数配置实体类型
 */
export interface Config {
    configId?: number
    configName?: string
    configKey?: string
    configValue?: string
    configType?: string
    remark?: string
}

/**
 * 参数配置搜索参数
 */
export interface ConfigSearchParams {
    pageNum?: number
    pageSize?: number
    configName?: string
    configKey?: string
    configValue?: string
    configType?: string
}

function getContentDisposition(headers: Record<string, string>): string | null {
    const key = Object.keys(headers).find((k) => k.toLowerCase() === 'content-disposition')
    return key ? headers[key] : null
}

/**
 * 获取参数配置列表（分页）
 */
export function fetchGetConfigList(params: ConfigSearchParams) {
    return request.post<Api.Common.PaginatedResponse<Config>>({
        url: '/api/system/config/list',
        data: params
    })
}

/**
 * 根据ID获取参数配置详情
 */
export function fetchGetConfigById(configId: number) {
    return request.get<Config>({
        url: `/api/system/config/${configId}`
    })
}

/**
 * 新增参数配置
 */
export function fetchAddConfig(data: Config) {
    return request.post({
        url: '/api/system/config',
        data
    })
}

/**
 * 修改参数配置
 */
export function fetchUpdateConfig(data: Config) {
    return request.put({
        url: '/api/system/config',
        data
    })
}

/**
 * 删除参数配置（支持单删和批量删除，请求体 ids）
 */
export function fetchDeleteConfig(configIds: number[]) {
    return request.del({
        url: '/api/system/config/delete',
        data: {ids: configIds}
    })
}

/**
 * 导出参数配置
 */
export async function fetchExportConfig(params: ConfigSearchParams) {
    const response = await request.post<BlobFullResponse>({
        url: '/api/system/config/export',
        data: params as Record<string, unknown>,
        responseType: 'blob',
        returnFullResponse: true
    })

    const contentDisposition = getContentDisposition(response.headers)
    const filename =
        getFilenameFromContentDisposition(contentDisposition) || `sys_config_export_${Date.now()}.xlsx`
    downloadBlob(response.data, filename)
}
