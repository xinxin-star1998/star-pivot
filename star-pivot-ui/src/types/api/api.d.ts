declare namespace Api.Auth {
  /** 登录请求参数 */
  interface LoginParams {
    username: string
    password: string
    rememberPassword: boolean
    captchaId: string
    captcha: string
  }
  /** 登录响应 */
  interface LoginResponse {
    token: string
    username: string
    nickname: string
  }
  /** 用户信息 */
  interface UserInfo {
    user: {
      userId: number
      username: string
      nickName: string
      avatar: string
      email: string
      phoneNumber: string
      sex: number
      status: string
      createTime: string
    }
    roles: Array<{
      roleId: number
      roleName: string
      roleKey: string
      roleSort: number
      status: string
      createTime: string
    }>
    permissions: Array<{
      menuId: number
      menuName: string
      parentId: number
      orderNum: number
      path: string
      component: string
      query: string
      isFrame: number
      isCache: number
      menuType: string
      visible: string
      status: string
      perms: string
      icon: string
      createTime: string
      children?: Array<any>
    }>
  }
  /** 验证码响应 */
  interface CaptchaResponse {
    captchaId: string
    captchaImage: string
  }
}