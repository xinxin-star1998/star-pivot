import request from '@/utils/http'

/** 商品 VO（与后端 ProductVo 对齐） */
export interface MallProductVo {
  id?: number
  categoryId?: number
  brandId?: number
  name?: string
  subtitle?: string
  mainImage?: string
  images?: string
  detail?: string
  price?: number | string
  stock?: number
  status?: number
  createTime?: string
  updateTime?: string
}

/** 列表查询 */
export interface MallProductListParams extends Api.Common.CommonSearchParams {
  name?: string
  categoryId?: number
  brandId?: number
  status?: number
}

/** 新增/修改（与后端 ProductSaveBo 对齐） */
export interface MallProductSavePayload {
  id?: number
  categoryId: number
  brandId?: number | null
  name: string
  subtitle?: string
  mainImage?: string
  images?: string
  detail?: string
  price: number
  stock: number
  status: number
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
