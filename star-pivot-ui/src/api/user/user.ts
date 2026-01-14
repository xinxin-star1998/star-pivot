import request from '@/utils/http'

/**
 * 获取用户列表（分页）
 */
export function fetchGetUserList(params: Api.SystemManage.UserSearchParams) {
  return request.post<Api.SystemManage.UserList>({
    url: '/api/sys/user/pageList',
    params
  })
}

/**
 * 根据ID获取用户详情
 */
export function fetchGetUserById(userId: number) {
  return request.get<Api.SystemManage.UserListItem>({
    url: `/api/sys/user/${userId}`
  })
}

/**
 * 新增用户
 */
export function fetchAddUser(data: Api.SystemManage.UserBo) {
  return request.post({
    url: '/api/sys/user/add',
    data
  })
}

/**
 * 修改用户
 */
export function fetchUpdateUser(data: Api.SystemManage.UserListItem) {
  return request.post({
    url: '/api/sys/user/update',
    data
  })
}

/**
 * 删除用户
 */
export function fetchDeleteUser(userIds: number[]) {
  return request.del({
    url: `/api/sys/user/${userIds}`
  })
}

/**
 * 修改用户状态
 */
export function fetchUpdateUserStatus(userId: number, status: number) {
  return request.post({
    url: '/api/sys/user/changeStatus',
    data: {
      userId,
      status
    }
  })
}
/**
 * 重置用户密码
 */
export function fetchResetUserPassword(userId: number, password: string) {
  return request.post({
    url: '/api/sys/user/resetPwd',
    data: {
      userId,
      password
    }
  })
}

/**
 * 上传头像
 */
export function fetchUploadAvatar(data: FormData) {
  return request.post({
    url: '/api/avatar/upload',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除头像
 */
export function fetchDeleteAvatar(userId: string) {
  return request.del({
    url: '/api/avatar/delete',
    params: {
      userId
    }
  })
}

/**
 * 获取头像临时访问链接
 */
export function fetchGetAvatarPresignedUrl(filePath: string) {
  return request.get({
    url: '/api/avatar/presigned-url',
    params: {
      filePath
    }
  })
}
