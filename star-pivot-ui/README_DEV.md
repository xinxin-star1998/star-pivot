# 开发环境配置说明

## 启动步骤

### 1. 启动后端服务

```bash
# 进入项目根目录
cd star-pivot-controller

# 启动Spring Boot应用
mvn spring-boot:run
# 或使用IDE直接运行 StarPivotApplication.java
```

后端服务启动后，访问地址：`http://localhost:8080`

### 2. 启动前端服务

```bash
# 进入前端目录
cd star-pivot-ui

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev
```

前端服务启动后，访问地址：`http://localhost:3000`

## 代理配置

前端已配置Vite代理，开发环境会自动将 `/api` 请求代理到 `http://localhost:8080/api`。

### 代理配置说明

- **开发环境**：使用相对路径 `/api`，Vite自动代理
- **生产环境**：使用完整URL，通过环境变量 `VITE_API_BASE_URL` 配置

### 环境变量配置

创建 `.env.development` 文件（已创建）：
```bash
VITE_API_BASE_URL=/api
```

创建 `.env.production` 文件：
```bash
VITE_API_BASE_URL=https://api.yourdomain.com/api
```

## 常见问题

### 1. ERR_CONNECTION_REFUSED 错误

**原因**：后端服务未启动或端口不匹配

**解决方法**：
1. 检查后端服务是否已启动
2. 确认后端端口是否为8080
3. 检查 `application.yml` 中的端口配置

### 2. CORS 跨域错误

**原因**：开发环境未使用代理，直接请求后端

**解决方法**：
1. 确保使用相对路径 `/api`（开发环境）
2. 检查 `vite.config.ts` 中的代理配置
3. 重启前端开发服务器

### 3. 401 未授权错误

**原因**：Token过期或未登录

**解决方法**：
1. 重新登录获取Token
2. 检查Token是否有效
3. 检查后端JWT配置

## 调试技巧

### 查看请求URL

在浏览器开发者工具的Network面板中，可以看到：
- **开发环境**：请求URL为 `/api/xxx`（相对路径）
- **生产环境**：请求URL为完整URL

### 查看后端日志

后端日志会显示：
- 请求路径
- 请求参数
- 响应数据
- 错误信息

### 检查网络连接

```bash
# 检查后端服务是否运行
curl http://localhost:8080/api/auth/login

# 或使用浏览器访问
http://localhost:8080/api/auth/login
```

