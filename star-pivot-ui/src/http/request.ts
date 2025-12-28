import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type InternalAxiosRequestConfig } from "axios";
import { ElMessage } from 'element-plus';
import router from '@/router';

// axios请求配置
// 开发环境：如果配置了Vite代理，使用相对路径；否则使用完整URL
// 生产环境：使用环境变量配置的API地址
const getBaseURL = () => {
  if (import.meta.env.PROD) {
    // 生产环境使用环境变量配置的完整URL
    return import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
  } else {
    // 开发环境：优先使用代理（相对路径），如果没有配置代理则使用完整URL
    return import.meta.env.VITE_API_BASE_URL || '/api';
  }
};

const config: AxiosRequestConfig = {
  baseURL: getBaseURL(),
  timeout: 10000
}

// 请求重试配置
const MAX_RETRY_COUNT = 3; // 最大重试次数
const RETRY_DELAY = 1000; // 重试延迟（毫秒）

// 需要重试的状态码
const RETRY_STATUS_CODES = [408, 500, 502, 503, 504];

// 定义返回值类型
export interface Result<T = any> {
  code: number;
  message: string;  // 后端返回的是message字段，不是msg
  data: T;
  timestamp?: number;
}

// 定义错误消息映射
const ERROR_MESSAGES: Record<number, string> = {
  400: '错误请求',
  401: '未授权，请重新登录',
  403: '拒绝访问',
  404: '请求错误,未找到接口',
  405: '请求方法未允许',
  408: '请求超时',
  500: '服务器端出错',
  501: '网络未实现',
  502: '网络错误',
  503: '服务不可用',
  504: '网络超时',
  505: 'http版本不支持该请求'
};

class Http {
  // axios实例
  private instance: AxiosInstance;
  // 请求取消控制器Map
  private cancelTokenMap: Map<string, AbortController> = new Map();

  // 构造函数里面初始化
  constructor(config: AxiosRequestConfig) {
    this.instance = axios.create(config);
    // 定义拦截器
    this.interceptors();
  }

  /**
   * 取消请求
   */
  cancelRequest(url: string): void {
    const controller = this.cancelTokenMap.get(url);
    if (controller) {
      controller.abort();
      this.cancelTokenMap.delete(url);
    }
  }

  /**
   * 取消所有请求
   */
  cancelAllRequests(): void {
    this.cancelTokenMap.forEach(controller => controller.abort());
    this.cancelTokenMap.clear();
  }

  /**
   * 延迟函数
   */
  private delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  /**
   * 检查是否需要重试
   */
  private shouldRetry(error: any, retryCount: number): boolean {
    if (retryCount >= MAX_RETRY_COUNT) {
      return false;
    }
    // 网络错误或特定状态码需要重试
    if (!error.response) {
      return true; // 网络错误，需要重试
    }
    const status = error.response.status;
    return RETRY_STATUS_CODES.includes(status);
  }

  // 拦截器
  private interceptors() {
    // axios发送请求之前的处理
    this.instance.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => {
        // 在请求头部携带token
        const token = localStorage.getItem('token') || sessionStorage.getItem('token');
        if (token) {
          config.headers!['Authorization'] = `Bearer ${token}`;
        }
        
        // 创建AbortController用于取消请求
        const url = config.url || '';
        const controller = new AbortController();
        config.signal = controller.signal;
        this.cancelTokenMap.set(url, controller);
        
        return config;
      },
      (error: any) => {
        error.data = {};
        error.data.message = '服务器异常，请联系管理员!';  // 使用message字段
        return Promise.reject(error);
      }
    );

    // axios请求返回之后的处理
    this.instance.interceptors.response.use(
      (res: AxiosResponse) => {
        // 请求成功后移除取消控制器
        const url = res.config.url || '';
        this.cancelTokenMap.delete(url);
        
        if (res.data.code === 200) {
          return res.data;
        } else {
          const message = res.data.message || res.data.msg || '服务器出错!';  // 优先使用message字段
          ElMessage.error(message);
          return Promise.reject(new Error(message));
        }
      },
      async (error) => {
        // 请求失败后移除取消控制器
        const url = error.config?.url || '';
        this.cancelTokenMap.delete(url);
        
        // 如果是取消请求，直接返回
        if (axios.isCancel(error)) {
          return Promise.reject(error);
        }
        
        // 开发环境输出错误日志，生产环境可通过构建工具移除
        if (import.meta.env.DEV) {
          console.error('请求错误:', error);
        }
        
        const config = error.config as InternalAxiosRequestConfig & { __retryCount?: number };
        
        // 401未授权，清除token并跳转登录页
        if (error.response?.status === 401) {
          localStorage.removeItem('token');
          sessionStorage.removeItem('token');
          // 避免重复跳转
          if (router.currentRoute.value.path !== '/login') {
            ElMessage.error('登录已过期，请重新登录');
            router.push('/login');
          }
          return Promise.reject(error);
        }

        // 初始化重试次数
        if (!config.__retryCount) {
          config.__retryCount = 0;
        }

        // 检查是否需要重试
        if (this.shouldRetry(error, config.__retryCount)) {
          config.__retryCount += 1;
          
          // 延迟后重试
          await this.delay(RETRY_DELAY * config.__retryCount);
          
          if (import.meta.env.DEV) {
            console.log(`请求重试第 ${config.__retryCount} 次:`, config.url);
          }
          
          return this.instance(config);
        }

        error.data = {};

        if (error && error.response) {
          // 有响应，说明连接成功但返回了错误状态码
          const status = error.response.status;
          const errorMsg = ERROR_MESSAGES[status] || `连接错误${status}`;
          error.data.message = errorMsg;
          ElMessage.error(errorMsg);
        } else if (error.code === 'ERR_NETWORK' || error.message === 'Network Error' || error.message?.includes('ERR_CONNECTION_REFUSED')) {
          // 网络连接错误，可能是后端服务未启动
          error.data.message = '无法连接到服务器';
          const tips = '请检查：\n1. 后端服务是否已启动（端口8080）\n2. 后端服务是否正常运行\n3. 网络连接是否正常';
          ElMessage.error('无法连接到服务器');
          if (import.meta.env.DEV) {
            console.error('后端服务连接失败:', tips);
            console.error('请确保后端服务已启动: http://localhost:8080');
            console.error('当前请求URL:', error.config?.baseURL + error.config?.url);
          }
        } else {
          // 其他错误
          error.data.message = error.message || '连接到服务器失败';
          ElMessage.error(error.data.message);
        }

        return Promise.reject(error);
      }
    );
  }

  /* GET 方法 */
  get<T = Result>(url: string, params?: object): Promise<T> {
    return this.instance.get(url, { params });
  }

  /* POST 方法 */
  post<T = Result>(url: string, data?: object): Promise<T> {
    return this.instance.post(url, data);
  }

  /* PUT 方法 */
  put<T = Result>(url: string, data?: object): Promise<T> {
    return this.instance.put(url, data);
  }

  /* DELETE 方法 */
  delete<T = Result>(url: string): Promise<T> {
    return this.instance.delete(url);
  }

  // 图片上传
  upload<T = Result>(url: string, params?: FormData): Promise<T> {
    return this.instance.post(url, params, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  }
}

export default new Http(config);