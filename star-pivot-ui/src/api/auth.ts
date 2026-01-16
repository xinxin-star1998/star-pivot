import request from '@/utils/http'

/**
 * 登录
 * @param data 登录参数
 * @returns 登录响应
 */
export function fetchLogin(data: Api.Auth.LoginParams) {
  return request.post<Api.Auth.LoginResponse>({
    url: '/api/auth/login',
    data
    // showSuccessMessage: true // 显示成功消息
    // showErrorMessage: false // 不显示错误消息
  })
}

/**
 * 获取用户信息
 * @returns 用户信息
 */
export function fetchGetUserInfo() {
  return request.get<Api.Auth.UserInfo>({
    url: '/api/auth/userinfo'
    // 自定义请求头
    // headers: {
    //   'X-Custom-Header': 'your-custom-value'
    // }
  })
}

/**
 * 登出
 * @returns 登出响应
 */
export function fetchLogout() {
  return request.post({
    url: '/api/auth/logout'
  })
}

/**
 * 获取验证码
 * @param captchaId 验证码ID
 * @returns 验证码响应
 */
export function fetchCaptcha(captchaId: string) {
  return request.get<Api.Auth.CaptchaResponse>({
    url: '/api/auth/captcha',
    params: { captchaId }
  })
}
