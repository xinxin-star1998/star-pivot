import request from '@/utils/http'
// 获取角色列表
export function fetchGetRoleList(params: Api.SystemManage.RoleSearchParams) {
  return request.post<Api.SystemManage.RoleList>({
    url: '/api/sys/role/list',
    params
  })
}
//新增角色
export function fetchAddRole(data: Api.SystemManage.RoleListItem) {
  return request.post<Api.SystemManage.RoleListItem>({
    url: '/api/sys/role/add',
    data
  })
}