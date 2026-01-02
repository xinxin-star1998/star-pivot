import request from '@/utils/http'
// 获取用户列表
export function fetchGetUserList(params: Api.SystemManage.UserSearchParams) {
  return request.post<Api.SystemManage.UserList>({
    url: '/api/sys/user/pageList',
    params
  })
}