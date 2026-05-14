import request from '@/utils/http'

/** 品牌 VO（与后端 BrandVo 字段对齐） */
export interface MallBrandVo {
  id?: number
  brandName?: string
  brandLogo?: string
  brandDesc?: string
  sort?: number
  status?: number
  createTime?: string
  updateTime?: string
}

/** 列表查询：分页 + 模糊名称 + 状态 */
export interface MallBrandListParams extends Api.Common.CommonSearchParams {
  brandName?: string
  status?: number
}

/** 新增/修改提交体（与后端 BrandSaveBo 对齐） */
export interface MallBrandSavePayload {
  id?: number
  brandName: string
  brandLogo?: string
  brandDesc?: string
  sort?: number
  status: number
}

/** 品牌分页列表 */
export function fetchMallBrandList(params: MallBrandListParams) {
  return request.post<Api.Common.PaginatedResponse<MallBrandVo>>({
    url: '/api/mall/brand/list',
    data: params
  })
}

/** 品牌详情 */
export function fetchMallBrandById(id: number) {
  return request.get<MallBrandVo>({
    url: `/api/mall/brand/${id}`
  })
}

/** 新增品牌 */
export function fetchMallBrandAdd(data: MallBrandSavePayload) {
  return request.post<void>({
    url: '/api/mall/brand',
    data,
    showSuccessMessage: true
  })
}

/** 修改品牌 */
export function fetchMallBrandUpdate(data: MallBrandSavePayload) {
  return request.put<void>({
    url: '/api/mall/brand',
    data,
    showSuccessMessage: true
  })
}

/** 删除品牌（请求体 ids） */
export function fetchMallBrandRemove(ids: number[]) {
  return request.del<void>({
    url: '/api/mall/brand/remove',
    data: { ids },
    showSuccessMessage: true
  })
}
