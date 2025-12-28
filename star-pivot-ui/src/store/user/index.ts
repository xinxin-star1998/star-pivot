import { defineStore } from 'pinia';
import { ref } from 'vue';
import authApi from '@/http/api/login/auth';

// 定义类型，避免循环导入
interface LoginRequest {
  username: string;
  password: string;
}

interface LoginResponse {
  token: string;
  username?: string;
  nickname?: string;
}

interface UserInfo {
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

export const useUserStore = defineStore('user', () => {
  // 用户状态
  const userInfo = ref<UserInfo['user'] | null>(null);
  const roles = ref<UserInfo['roles']>([]);
  const permissions = ref<UserInfo['permissions']>([]);
  const token = ref<string | null>(null);
  // 添加菜单折叠状态
  const isMenuCollapse = ref(false);

  // 登录
  const login = async (loginData: LoginRequest) => {
    try {
      const response = await authApi.login(loginData);
      if (response.code === 200 && response.data) {
        const loginResponse = response.data as LoginResponse;
        token.value = loginResponse.token;
        
        // 将token存储到localStorage
        localStorage.setItem('token', loginResponse.token);
        
        // 获取用户信息
        await getUserInfo();
        
        return response;
      } else {
        throw new Error(response.message || response.msg || '登录失败');
      }
    } catch (error: any) {
      console.error('登录失败:', error);
      throw error;
    }
  };

  // 获取用户信息
  const getUserInfo = async () => {
    try {
      const response = await authApi.getUserInfo();
      if (response.code === 200 && response.data) {
        const data = response.data as UserInfo;
        userInfo.value = data.user;
        roles.value = data.roles;
        permissions.value = data.permissions;
        return response;
      } else {
        throw new Error(response.message || response.msg || '获取用户信息失败');
      }
    } catch (error: any) {
      console.error('获取用户信息失败:', error);
      throw error;
    }
  };

  // 登出
  const logout = async () => {
    try {
      await authApi.logout();
    } catch (error) {
      console.error('登出失败:', error);
    } finally {
      // 清除本地存储和状态
      token.value = null;
      userInfo.value = null;
      roles.value = [];
      permissions.value = [];
      localStorage.removeItem('token');
      sessionStorage.removeItem('token');
    }
  };

  // 检查是否有权限
  const hasPermission = (permission: string) => {
    return permissions.value.some(perm => perm.perms === permission);
  };

  // 检查是否有角色
  const hasRole = (role: string) => {
    return roles.value.some(r => r.roleName === role);
  };

  // 切换菜单折叠状态
  const toggleMenuCollapse = () => {
    isMenuCollapse.value = !isMenuCollapse.value;
  };

  return {
    userInfo,
    roles,
    permissions,
    token,
    isMenuCollapse,
    login,
    getUserInfo,
    logout,
    hasPermission,
    hasRole,
    toggleMenuCollapse
  };
});