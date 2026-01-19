import request from '@/utils/http'

/**
 * 获取角色列表（分页）
 */
export function fetchGetRoleList(params: Api.SystemManage.RoleSearchParams) {
  return request.post<Api.SystemManage.RoleList>({
    url: '/api/sys/role/list',
    params
  })
}

/**
 * 新增角色
 */
export function fetchAddRole(data: Api.SystemManage.RoleListItem) {
  return request.post<Api.SystemManage.RoleListItem>({
    url: '/api/sys/role/addRole',
    data,
    showSuccessMessage: true
  })
}

/**
 * 修改角色
 */
export function fetchUpdateRole(data: Api.SystemManage.RoleListItem) {
  return request.put<Api.SystemManage.RoleListItem>({
    url: '/api/sys/role/updateRole',
    data,
    showSuccessMessage: true
  })
}

/**
 * 删除角色
 */
export function fetchDeleteRole(roleIds: number[]) {
  // 将数组转换为逗号分隔的字符串，Spring 框架会自动将其转换为数组
  const roleIdsStr = roleIds.join(',')
  return request.del({
    url: `/api/sys/role/${roleIdsStr}`,
    showSuccessMessage: true
  })
}

/**
 * 修改角色状态
 */
export function fetchChangeRoleStatus(data: Api.SystemManage.RoleListItem) {
  return request.put({
    url: '/api/sys/role/changeStatus',
    data,
    showSuccessMessage: true
  })
}

/**
 * 获取角色详情
 */
export function fetchGetRoleById(roleId: number) {
  return request.get<Api.SystemManage.RoleListItem>({
    url: `/api/sys/role/${roleId}`
  })
}

/**
 * 获取角色下拉列表
 */
export function fetchGetRoleSelect() {
  return request.get<Api.SystemManage.RoleListItem[]>({
    url: '/api/sys/role/select'
  })
}

/**
 * 分配菜单权限 和 部门权限
 */
export function fetchAssignPermission(data: Api.SystemManage.RolePermissionAssignDTO) {
  return request.post({
    url: '/api/sys/role/assignPermission',
    data,
    showSuccessMessage: true
  })
}
/**
 * 根据角色ID获取已分配的菜单列表
 */
export function fetchGetRoleMenus(roleId: number) {
  return request.get<number[]>({
    url: `/api/sys/role/getMenuIdsByRoleId/${roleId}`
  })
}
/**
 * 根据角色ID获取已分配的部门ID列表
 */
export function fetchGetRoleDeptIds(roleId: number) {
  return request.get<number[]>({
    url: `/api/sys/role/${roleId}/deptIds`
  })
}
