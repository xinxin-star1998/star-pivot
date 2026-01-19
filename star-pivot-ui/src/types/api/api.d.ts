/**
 * API 接口类型定义模块
 *
 * 提供所有后端接口的类型定义
 *
 * ## 主要功能
 *
 * - 通用类型（分页参数、响应结构等）
 * - 认证类型（登录、用户信息等）
 * - 系统管理类型（用户、角色等）
 * - 全局命名空间声明
 *
 * ## 使用场景
 *
 * - API 请求参数类型约束
 * - API 响应数据类型定义
 * - 接口文档类型同步
 *
 * ## 注意事项
 *
 * - 在 .vue 文件使用需要在 eslint.config.mjs 中配置 globals: { Api: 'readonly' }
 * - 使用全局命名空间，无需导入即可使用
 *
 * ## 使用方式
 *
 * ```typescript
 * const params: Api.Auth.LoginParams = { userName: 'admin', password: '123456' }
 * const response: Api.Auth.UserInfo = await fetchUserInfo()
 * ```
 *
 * @module types/api/api
 * @author Art Design Pro Team
 */

declare namespace Api {
  /** 通用类型 */
  namespace Common {
    /** 分页参数（前端内部使用，Element Plus 标准字段） */
    interface PaginationParams {
      /** 当前页码 */
      current: number
      /** 每页条数 */
      size: number
      /** 总条数 */
      total: number
    }
    /** 通用搜索参数（发送给后端的参数，使用 pageNum 和 pageSize） */
    interface CommonSearchParams {
      /** 当前页码，默认值为 1 */
      pageNum?: number
      /** 每页条数，默认值为 10 */
      pageSize?: number
    }
    /** 分页响应基础结构（后端返回的数据结构） */
    interface PaginatedResponse<T = any> {
      records: T[]
      pageNum?: number
      pageSize?: number
      total: number
    }
    /** 启用状态 */
    type EnableStatus = '1' | '2'
  }

  /** 认证类型 */
  namespace Auth {
    /** 登录请求参数 */
    interface LoginParams {
      username: string
      password: string
      rememberPassword: boolean
      /** 验证码通过凭证（一次性 proof） */
      captchaProof: string
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
      /** 服务端生成的验证码 token */
      captchaToken: string
      captchaImage: string
    }

    /** 验证码校验响应 */
    interface CaptchaVerifyResponse {
      captchaProof: string
    }
  }

  /** 系统管理类型 */
  namespace SystemManage {
    /** 用户列表 */
    type UserList = Api.Common.PaginatedResponse<UserListItem>

    /** 用户列表项 */
    interface UserListItem {
      userId: number
      deptId?: number
      avatar?: string
      status: string
      userName: string
      nickName?: string
      userType?: string
      sex: string
      phonenumber?: string
      email?: string
      password?: string
      loginIp?: string
      loginDate?: string
      pwdUpdateDate?: string
      userRoles?: string[]
      roleName?: string
      createBy?: string
      createTime?: string
      updateBy?: string
      updateTime?: string
      delFlag?: number
      remark?: string
    }
    interface UserBo {
      userId: number
      deptId: number
      userName: string
      nickName: string
      email: string
      avatar: string
      password: string
      phonenumber: string
      status: string
      sex: string
      remark: string
      roleIds: string[]
      postIds: string[]
    }

    /** 用户搜索参数 */
    type UserSearchParams = Partial<
      Pick<
        UserListItem,
        'userId' | 'userName' | 'nickName' | 'sex' | 'phonenumber' | 'email' | 'status' | 'deptId'
      >
    > &
      Api.Common.CommonSearchParams

    /** 角色列表 */
    type RoleList = Api.Common.PaginatedResponse<RoleListItem>

    /** 角色列表项 */
    interface RoleListItem {
      roleId: number
      roleName: string
      roleKey: string
      roleSort: number
      dataScope: string
      menuCheckStrictly: number
      deptCheckStrictly: number
      remark: string
      status: number
      createTime: string
      delFlag: number
      createBy: string
      updateTime: string
      updateBy: string
    }
    interface RolePermissionAssignDTO {
      roleId: number
      menuIds: number[]
      deptIds: number[]
    }

    /** 角色搜索参数 */
    type RoleSearchParams = Partial<
      Pick<RoleListItem, 'roleId' | 'roleName' | 'roleKey' | 'remark' | 'status'>
    > &
      Api.Common.CommonSearchParams
  }

  namespace Post {
    /** 岗位列表 */
    type PostList = Api.Common.PaginatedResponse<PostListItem>

    interface PostListItem {
      postId: number
      postName: string
      postCode: string
      postSort: number
      status: number
      remark: string
      createTime: string
      updateTime: string
      createBy: string
      updateBy: string
    }
    type PostSearchParams = Partial<
      Pick<PostListItem, 'postId' | 'postName' | 'postCode' | 'status'>
    > &
      Api.Common.CommonSearchParams

    interface PostBo {
      postId: number
      postCode: string
      postName: string
    }
  }
}