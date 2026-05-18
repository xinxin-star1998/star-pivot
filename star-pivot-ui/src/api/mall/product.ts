import request from '@/utils/http'

/** SPU VO（pms_spu_info） */
export interface MallProductVo {
  id?: number
  spuName?: string
  spuDescription?: string
  catalogId?: number
  brandId?: number
  weight?: number | string
  publishStatus?: number /** 0-下架；1-上架 */
  createTime?: string
  updateTime?: string
}

export interface MallProductListParams extends Api.Common.CommonSearchParams {
  spuName?: string
  catalogId?: number
  brandId?: number
  publishStatus?: number /** 0-下架；1-上架 */
}

export interface MallProductSavePayload {
  id?: number
  spuName: string
  spuDescription?: string
  catalogId: number
  brandId?: number | null
  weight: number
  publishStatus: number
}

export function fetchMallProductList(params: MallProductListParams) {
  return request.post<Api.Common.PaginatedResponse<MallProductVo>>({
    url: '/api/mall/product/list',
    data: params
  })
}

export function fetchMallProductById(id: number) {
  return request.get<MallProductVo>({
    url: `/api/mall/product/${id}`
  })
}

export function fetchMallProductAdd(data: MallProductSavePayload) {
  return request.post<void>({
    url: '/api/mall/product',
    data,
    showSuccessMessage: true
  })
}

export function fetchMallProductUpdate(data: MallProductSavePayload) {
  return request.put<void>({
    url: '/api/mall/product',
    data,
    showSuccessMessage: true
  })
}

export function fetchMallProductRemove(ids: number[]) {
  return request.del<void>({
    url: '/api/mall/product/remove',
    data: { ids },
    showSuccessMessage: true
  })
}
