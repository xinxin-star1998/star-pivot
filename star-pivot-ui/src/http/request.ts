import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type InternalAxiosRequestConfig } from "axios";
import { ElMessage } from 'element-plus';

// axios请求配置
const config: AxiosRequestConfig = {
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000
}

// 定义返回值类型
export interface Result<T = any> {
  code: number;
  msg: string;
  data: T;
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

  // 构造函数里面初始化
  constructor(config: AxiosRequestConfig) {
    this.instance = axios.create(config);
    // 定义拦截器
    this.interceptors();
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
        return config;
      },
      (error: any) => {
        error.data = {};
        error.data.msg = '服务器异常，请联系管理员!';
        return Promise.reject(error);
      }
    );

    // axios请求返回之后的处理
    this.instance.interceptors.response.use(
      (res: AxiosResponse) => {
        if (res.data.code === 200) {
          return res.data;
        } else {
          const msg = res.data.msg || '服务器出错!';
          ElMessage.error(msg);
          return Promise.reject(new Error(msg));
        }
      },
      (error) => {
        console.error('请求错误:', error);
        error.data = {};

        if (error && error.response) {
          const status = error.response.status;
          const errorMsg = ERROR_MESSAGES[status] || `连接错误${status}`;
          error.data.msg = errorMsg;
          ElMessage.error(errorMsg);
        } else {
          error.data.msg = '连接到服务器失败';
          ElMessage.error('连接到服务器失败');
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