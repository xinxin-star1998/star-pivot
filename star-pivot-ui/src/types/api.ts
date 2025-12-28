// API相关类型定义

// 登录请求参数类型
export interface LoginRequest {
  username: string;
  password: string;
}

// 登录响应数据类型
export interface LoginResponse {
  token: string;
  username?: string;
  nickname?: string;
}

// 用户信息类型
export interface UserInfo {
  user: {
    userId: number;
    userName: string;
    nickName: string;
    email?: string;
    phonenumber?: string;
    sex?: string;
    avatar?: string;
    status: string;
    loginIp?: string;
    loginDate?: string;
    createTime?: string;
    updateTime?: string;
    createBy?: string;
    updateBy?: string;
    remark?: string;
    deptId?: number;
    userType?: string;
    delFlag?: number;
    pwdUpdateDate?: string;
  };
  roles: Array<{
    roleId: number;
    roleName: string;
    roleKey?: string;
    roleSort?: number;
    dataScope?: string;
    status?: string;
    delFlag?: string;
    createBy?: string;
    createTime?: string;
    updateBy?: string;
    updateTime?: string;
    remark?: string;
  }>;
  permissions: Array<{
    menuId: number;
    menuName: string;
    perms: string;
    menuType?: string;
    visible?: string;
    status?: string;
    parentId?: number;
    orderNum?: number;
    path?: string;
    component?: string;
    query?: string;
    routeName?: string;
    isFrame?: number;
    isCache?: number;
    icon?: string;
    delFlag?: string;
    createBy?: string;
    createTime?: string;
    updateBy?: string;
    updateTime?: string;
    remark?: string;
  }>;
}

