/**
 * HTTP 请求封装模块
 * 基于 Axios 封装的 HTTP 请求工具，提供统一的请求/响应处理
 *
 * ## 主要功能
 *
 * - 请求/响应拦截器（自动添加 Token、统一错误处理）
 * - 401 未授权自动登出（带防抖机制）
 * - 请求失败自动重试（可配置）
 * - 统一的成功/错误消息提示
 * - 支持 GET/POST/PUT/DELETE 等常用方法
 *
 * @module utils/http
 * @author Art Design Pro Team
 */

import axios, { AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { useUserStore } from '@/store/modules/user'
import { ApiStatus } from './status'
import { HttpError, handleError, showError, showSuccess } from './error'
import { $t } from '@/locales'
import { BaseResponse } from '@/types'

/** 请求配置常量 */
const REQUEST_TIMEOUT = 15000
const LOGOUT_DELAY = 500
const MAX_RETRIES = 3 // 最大重试次数，0表示不重试
const RETRY_DELAY = 1000 // 重试延迟（毫秒）
const UNAUTHORIZED_DEBOUNCE_TIME = 3000

/** 401防抖状态 */
let isUnauthorizedErrorShown = false
let unauthorizedTimer: ReturnType<typeof setTimeout> | null = null

/** 扩展 AxiosRequestConfig */
interface ExtendedAxiosRequestConfig extends AxiosRequestConfig {
  url: string
  params?: any
  data?: any
  showErrorMessage?: boolean
  showSuccessMessage?: boolean
}

const { VITE_API_URL, VITE_WITH_CREDENTIALS } = import.meta.env

/** Axios实例 */
const axiosInstance = axios.create({
  timeout: REQUEST_TIMEOUT,
  baseURL: VITE_API_URL,
  withCredentials: VITE_WITH_CREDENTIALS === 'true',
  validateStatus: (status) => status >= 200 && status < 300,
  transformResponse: [
    (data, headers) => {
      const contentType = headers['content-type']
      if (contentType?.includes('application/json')) {
        try {
          return JSON.parse(data)
        } catch {
          return data
        }
      }
      return data
    }
  ]
})

/**
 * 规范化请求 URL，避免出现 /api/api 重复前缀
 * - 当 baseURL 已包含 /api（或使用 Vite 代理 /api）时，业务层仍然写 /api/xxx 会导致重复
 */
function normalizeRequestUrl(url: string): string {
  if (!url) return url
  // 只处理以 /api/ 开头的相对路径
  if (!url.startsWith('/api/')) return url

  const base = (VITE_API_URL || '').trim()
  const baseHasApi = base === '/api' || base.endsWith('/api') || base.includes('/api/')

  return baseHasApi ? url.replace(/^\/api/, '') : url
}

/** 请求拦截器 */
axiosInstance.interceptors.request.use(
  (request: InternalAxiosRequestConfig) => {
    if (typeof request.url === 'string') {
      request.url = normalizeRequestUrl(request.url)
    }
    const { accessToken } = useUserStore()
    if (accessToken)
      request.headers.set(
        'Authorization',
        accessToken.startsWith('Bearer ') ? accessToken : `Bearer ${accessToken}`
      )

    // 设置 Content-Type（仅当未设置且数据不是 FormData 时）
    // 注意：axios 会自动将 JavaScript 对象序列化为 JSON，无需手动 stringify
    if (request.data && !(request.data instanceof FormData) && !request.headers['Content-Type']) {
      // 如果 data 已经是字符串，直接使用；否则让 axios 自动处理
      if (typeof request.data === 'string') {
        request.headers.set('Content-Type', 'application/json')
      } else {
        // axios 会自动处理对象类型的数据，设置 Content-Type
        request.headers.set('Content-Type', 'application/json')
      }
    }

    return request
  },
  (error) => {
    showError(createHttpError($t('httpMsg.requestConfigError'), ApiStatus.error))
    return Promise.reject(error)
  }
)

/** 响应拦截器 */
axiosInstance.interceptors.response.use(
  (response: AxiosResponse<BaseResponse | Blob>) => {
    // 如果是 blob 响应类型，直接返回响应，不进行 JSON 解析
    if (response.config.responseType === 'blob' || response.data instanceof Blob) {
      return response
    }
    
    // JSON 响应类型，进行常规处理
    const data = response.data as BaseResponse
    const { code, msg, message } = data
    const messageText = msg || message || $t('httpMsg.requestFailed')
    if (code === ApiStatus.success) return response
    if (code === ApiStatus.unauthorized) handleUnauthorizedError(messageText)
    throw createHttpError(messageText, code)
  },
  (error) => {
    if (error.response?.status === ApiStatus.unauthorized) handleUnauthorizedError()
    return Promise.reject(handleError(error))
  }
)

/** 统一创建HttpError */
function createHttpError(message: string, code: number) {
  return new HttpError(message, code)
}

/** 处理401错误（带防抖） */
function handleUnauthorizedError(message?: string): never {
  const error = createHttpError(message || $t('httpMsg.unauthorized'), ApiStatus.unauthorized)

  if (!isUnauthorizedErrorShown) {
    isUnauthorizedErrorShown = true
    logOut()

    unauthorizedTimer = setTimeout(resetUnauthorizedError, UNAUTHORIZED_DEBOUNCE_TIME)

    showError(error, true)
    throw error
  }

  throw error
}

/** 重置401防抖状态 */
function resetUnauthorizedError() {
  isUnauthorizedErrorShown = false
  if (unauthorizedTimer) clearTimeout(unauthorizedTimer)
  unauthorizedTimer = null
}

/** 退出登录函数 */
function logOut() {
  setTimeout(() => {
    useUserStore().logOut()
  }, LOGOUT_DELAY)
}

/** 是否需要重试 */
function shouldRetry(statusCode: number) {
  return [
    ApiStatus.requestTimeout,
    ApiStatus.internalServerError,
    ApiStatus.badGateway,
    ApiStatus.serviceUnavailable,
    ApiStatus.gatewayTimeout
  ].includes(statusCode)
}

/** 请求重试逻辑（使用指数退避策略） */
async function retryRequest<T>(
  config: ExtendedAxiosRequestConfig,
  retries: number = MAX_RETRIES,
  baseDelay: number = RETRY_DELAY
): Promise<T> {
  try {
    return await request<T>(config)
  } catch (error) {
    if (retries > 0 && error instanceof HttpError && shouldRetry(error.code)) {
      // 指数退避：延迟时间 = 基础延迟 * 2^(总重试次数 - 当前剩余重试次数)
      const delayTime = baseDelay * Math.pow(2, MAX_RETRIES - retries)
      await delay(delayTime)
      return retryRequest<T>(config, retries - 1, baseDelay)
    }
    throw error
  }
}

/** 延迟函数 */
function delay(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

/** 请求函数 */
async function request<T = any>(config: ExtendedAxiosRequestConfig): Promise<T> {
  // POST | PUT 参数自动填充
  if (
    ['POST', 'PUT'].includes(config.method?.toUpperCase() || '') &&
    config.params &&
    !config.data
  ) {
    config.data = config.params
    config.params = undefined
  }

  try {
    const res = await axiosInstance.request<BaseResponse<T> | Blob>(config)

    // 如果是 blob 响应类型，直接返回 blob 对象
    if (config.responseType === 'blob' || res.data instanceof Blob) {
      return res.data as T
    }

    // JSON 响应类型，进行常规处理
    const jsonData = res.data as BaseResponse<T>
    
    // 显示成功消息
    if (config.showSuccessMessage) {
      const successMsg = jsonData.msg || jsonData.message
      if (successMsg) {
        showSuccess(successMsg)
      }
    }

    return jsonData.data as T
  } catch (error) {
    if (error instanceof HttpError && error.code !== ApiStatus.unauthorized) {
      const showMsg = config.showErrorMessage !== false
      showError(error, showMsg)
    }
    return Promise.reject(error)
  }
}

/** API方法集合 */
const api = {
  get<T>(config: ExtendedAxiosRequestConfig) {
    return retryRequest<T>({ ...config, method: 'GET' })
  },
  post<T>(config: ExtendedAxiosRequestConfig) {
    return retryRequest<T>({ ...config, method: 'POST' })
  },
  put<T>(config: ExtendedAxiosRequestConfig) {
    return retryRequest<T>({ ...config, method: 'PUT' })
  },
  del<T>(config: ExtendedAxiosRequestConfig) {
    return retryRequest<T>({ ...config, method: 'DELETE' })
  },
  request<T>(config: ExtendedAxiosRequestConfig) {
    return retryRequest<T>(config)
  }
}

export default api
