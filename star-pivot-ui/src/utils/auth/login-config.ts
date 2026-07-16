import { fetchLoginConfig } from '@/api/auth'

let loginConfigCache: Api.Auth.LoginConfigResponse | null = null
let pendingRequest: Promise<Api.Auth.LoginConfigResponse> | null = null

const defaultLoginConfig: Api.Auth.LoginConfigResponse = {
  captchaEnabled: true,
  registerEnabled: false,
  captchaType: 'image'
}

/**
 * 读取登录页配置（带内存缓存）
 */
export async function getLoginConfig(force = false): Promise<Api.Auth.LoginConfigResponse> {
  if (!force && loginConfigCache) {
    return loginConfigCache
  }

  if (!force && pendingRequest) {
    return pendingRequest
  }

  pendingRequest = fetchLoginConfig()
    .then((config) => {
      loginConfigCache = config
      return config
    })
    .catch(() => defaultLoginConfig)
    .finally(() => {
      pendingRequest = null
    })

  return pendingRequest
}

/** 参数设置保存后可在管理端调用，强制下次重新拉取 */
export function clearLoginConfigCache(): void {
  loginConfigCache = null
}
