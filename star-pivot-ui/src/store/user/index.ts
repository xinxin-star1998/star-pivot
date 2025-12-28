import { defineStore } from 'pinia';
import { ref } from 'vue';
import authApi from '@/http/api/login/auth';
import type { LoginRequest, LoginResponse, UserInfo } from '@/types/api';

export const useUserStore = defineStore('user', () => {
  // 用户状态
  const userInfo = ref<UserInfo['user'] | null>(null);
  const roles = ref<UserInfo['roles']>([]);
  const permissions = ref<UserInfo['permissions']>([]);
  const token = ref<string | null>(null);
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
        throw new Error(response.message || response.message || '登录失败');
      }
    } catch (error: any) {
      if (import.meta.env.DEV) {
        console.error('登录失败:', error);
      }
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
        throw new Error(response.message || response.message || '获取用户信息失败');
      }
    } catch (error: any) {
      if (import.meta.env.DEV) {
        console.error('获取用户信息失败:', error);
      }
      throw error;
    }
  };

  // 登出
  const logout = async () => {
    try {
      await authApi.logout();
    } catch (error) {
      if (import.meta.env.DEV) {
        console.error('登出失败:', error);
      }
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
  return {
    userInfo,
    roles,
    permissions,
    token,
    login,
    getUserInfo,
    logout,
    hasPermission,
    hasRole,
  };
});