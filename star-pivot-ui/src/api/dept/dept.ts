import request from '@/utils/http'

/**
 * 部门实体类型
 */
export interface SysDept {
  deptId?: number
  deptName: string
  parentId?: number
  orderNum?: number
  leader?: string
  phone?: string
  email?: string
  status?: string // 0正常 1停用
  createTime?: string
  updateTime?: string
  remark?: string
  label?: string
  value?: number
  children?: SysDept[]
}

/**
 * 获取部门树
 */
export function fetchGetDeptTree() {
  return request.get<SysDept[]>({
    url: '/api/sys/dept/tree'
  })
}

/**
 * 根据ID获取部门详情
 */
export function fetchGetDeptById(deptId: number) {
  return request.get<SysDept>({
    url: `/api/sys/dept/${deptId}`
  })
}

/**
 * 新增部门
 */
export function fetchAddDept(data: SysDept) {
  return request.post({
    url: '/api/sys/dept',
    data
  })
}

/**
 * 修改部门
 */
export function fetchUpdateDept(data: SysDept) {
  return request.put({
    url: '/api/sys/dept',
    data
  })
}

/**
 * 删除部门
 */
export function fetchDeleteDept(deptId: number) {
  return request.del({
    url: `/api/sys/dept/${deptId}`
  })
}
