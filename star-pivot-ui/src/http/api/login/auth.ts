import request from '@/http/request.ts';
import type { LoginRequest, LoginResponse, UserInfo } from '@/types/api';

// 重新导出类型，保持向后兼容
export type { LoginRequest, LoginResponse, UserInfo };

// 认证相关API
const authApi = {
  /**
   * 用户登录
   */
  login: (data: LoginRequest) => {
    return request.post('/auth/login', data);
  },

  /**
   * 用户登出
   */
  logout: () => {
    return request.post('/auth/logout');
  },

  /**
   * 获取当前用户信息
   */
  getUserInfo: () => {
    return request.get('/auth/userinfo');
  }
};

export default authApi;