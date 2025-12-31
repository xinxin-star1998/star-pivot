# 项目优化说明

本文档记录了项目优化过程中修复的问题和改进点。

## 已完成的优化

### 1. 前端优化

#### 1.1 API路径配置修复
- **问题**：`request.ts` 中 baseURL 和 `auth.ts` 中 API 路径重复，导致请求路径错误
- **修复**：
  - `request.ts`: baseURL 设置为 `http://localhost:8080/api`
  - `auth.ts`: API 路径去掉 `/api` 前缀，改为 `/auth/login`、`/auth/logout`、`/auth/userinfo`

#### 1.2 Console日志优化
- **问题**：生产环境不应输出 console.log 和 console.error
- **修复**：所有 console 调用都添加了环境判断 `import.meta.env.DEV`，只在开发环境输出

#### 1.3 登录页面默认账号移除
- **问题**：登录页面硬编码了默认用户名和密码
- **修复**：移除了默认值，改为空字符串

#### 1.4 Vite配置注释修正
- **问题**：注释说端口5173，实际是3000
- **修复**：更新注释为正确的说明

#### 1.5 类型定义提取
- **问题**：`auth.ts` 和 `store/user/index.ts` 中重复定义了相同的类型
- **修复**：创建 `src/types/api.ts` 统一管理 API 相关类型定义

### 2. 后端优化

#### 2.1 异常处理优化
- **问题**：`RuntimeException` 处理过于宽泛，可能掩盖具体异常
- **修复**：
  - 添加了更具体的异常处理：`AccessDeniedException`、`DataAccessException`、`HttpRequestMethodNotSupportedException`、`IllegalArgumentException`
  - 优化了通用异常处理，生产环境不暴露详细错误信息

#### 2.2 JWT工具类异常处理优化
- **问题**：异常被吞掉，只返回 false，不利于调试
- **修复**：
  - 添加了详细的异常类型判断：`ExpiredJwtException`、`MalformedJwtException`、`SecurityException`
  - 添加了日志记录，便于问题排查

#### 2.3 JWT密钥环境变量配置
- **问题**：JWT密钥硬编码在配置文件中
- **修复**：
  - 使用环境变量 `JWT_SECRET` 配置密钥
  - 提供了默认值，便于开发环境使用
  - 添加了配置说明注释

#### 2.4 CORS配置优化
- **问题**：CORS配置允许所有域名，生产环境不安全
- **修复**：
  - 支持通过环境变量 `CORS_ALLOWED_ORIGINS` 配置允许的域名
  - 开发环境可以使用 `*` 允许所有域名
  - 生产环境建议配置具体的域名列表

#### 2.5 数据库连接配置优化
- **问题**：数据库连接未启用SSL，生产环境不安全
- **修复**：添加了配置说明，提示生产环境启用SSL

## 环境变量配置

### 后端环境变量

创建 `.env` 文件（参考 `.env.example`）：

```bash
# 数据库配置
DB_URL=jdbc:mysql://localhost:3306/star-pivot?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&requireSSL=true&serverTimezone=GMT%2B8
DB_USERNAME=root
DB_PASSWORD=your_password_here

# JWT配置
JWT_SECRET=your_jwt_secret_key_here_at_least_32_bytes
JWT_EXPIRATION=86400000

# CORS配置
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com

# 日志级别
LOG_LEVEL=info
SECURITY_LOG_LEVEL=warn
SQL_LOG_LEVEL=OFF
MYBATIS_LOG_IMPL=org.apache.ibatis.logging.slf4j.Slf4jImpl
```

### 前端环境变量

创建 `.env.development` 或 `.env.production` 文件：

```bash
# 开发环境
VITE_API_BASE_URL=http://localhost:8080/api

# 生产环境
VITE_API_BASE_URL=https://api.yourdomain.com/api
```

## 第二轮优化（已完成）

### 1. HTTP请求重试机制 ✅
- **位置**：`request.ts`
- **实现**：
  - 添加了自动重试机制，最大重试3次
  - 支持网络错误和特定状态码（408, 500, 502, 503, 504）的重试
  - 使用指数退避策略，每次重试延迟递增

### 2. 401自动跳转登录 ✅
- **位置**：`request.ts` 响应拦截器
- **实现**：
  - 检测到401未授权时，自动清除token
  - 自动跳转到登录页
  - 避免重复跳转逻辑

### 3. 动态菜单加载功能 ✅
- **位置**：`MenuBar.vue`、`store/menu/index.ts`、`http/api/menu/menu.ts`
- **实现**：
  - 创建菜单API接口，从后端获取菜单数据
  - 实现菜单数据转换函数，将后端格式转换为前端格式
  - 在store中管理菜单状态
  - 组件挂载时自动加载菜单
  - 支持菜单树结构、排序、权限过滤

### 4. 生产环境配置文件 ✅
- **位置**：`application-prod.yml`
- **实现**：
  - 创建生产环境专用配置
  - 强制使用环境变量配置敏感信息
  - 优化日志配置，使用文件日志
  - 禁用SQL日志输出到控制台

### 5. 加载状态管理 ✅
- **位置**：`store/loading/index.ts`
- **实现**：
  - 创建全局加载状态管理store
  - 支持多个加载任务计数
  - 提供开始/结束/重置加载状态的方法
  - 提供开始/结束/重置加载状态的方法

## 第三轮优化（已完成）

### 1. 路由动态注册功能 ✅
- **位置**：`utils/route.ts`、`store/menu/index.ts`
- **实现**：
  - 创建路由工具函数，将菜单数据转换为路由配置
  - 支持动态加载组件
  - 菜单加载时自动注册路由
  - 登出时自动移除动态路由

### 2. 请求取消功能 ✅
- **位置**：`request.ts`
- **实现**：
  - 使用AbortController实现请求取消
  - 每个请求自动创建取消控制器
  - 提供取消单个请求和取消所有请求的方法
  - 请求完成或失败后自动清理控制器

### 3. Vue全局错误边界处理 ✅
- **位置**：`components/ErrorBoundary.vue`、`App.vue`
- **实现**：
  - 创建错误边界组件，捕获子组件错误
  - 在App.vue中包装应用，提供全局错误处理
  - 错误时显示友好的错误页面
  - 支持重新加载和返回首页

### 4. 菜单图标优化 ✅
- **位置**：`layout/menu/MenuItem.vue`
- **实现**：
  - 支持Element Plus图标名称
  - 自动识别图标组件
  - 提供默认图标fallback

### 5. 路由守卫权限检查 ✅
- **位置**：`router/index.ts`
- **实现**：
  - 添加路由权限检查
  - 根据用户权限控制路由访问
  - 无权限时显示提示并跳转
  - 自动加载用户信息

## 第四轮优化（已完成）

### 1. 移除前端路由权限认证 ✅
- **位置**：`router/index.ts`, `routes.ts`, `http/api/menu/menu.ts`, `store/user/index.ts`, `utils/route.ts`
- **实现**：
  - 移除路由守卫中的权限检查逻辑
  - 移除静态路由中的roles配置
  - 移除菜单转换中的权限相关字段
  - 简化用户store中的权限检查方法
  - 采用"后端为主，前端只显示"的架构模式
  - 后端根据用户权限返回菜单，前端只负责显示

## 待优化项（建议）

### 1. Token黑名单机制
- **位置**：`AuthController.java` 第59行
- **说明**：登出时未将Token加入黑名单，Token仍可使用
- **建议**：实现Redis Token黑名单机制

### 2. 数据库查询优化
- **位置**：`SysUserMapper.xml`
- **说明**：部分查询可能缺少索引
- **建议**：添加数据库索引，优化查询性能

### 3. 请求防抖/节流
- **位置**：`request.ts`
- **说明**：可以添加请求防抖/节流功能，避免频繁请求
- **建议**：对特定接口添加防抖/节流处理

### 4. 性能优化
- **说明**：可以添加虚拟滚动、懒加载等性能优化
- **建议**：对大数据列表使用虚拟滚动

## 安全建议

1. **生产环境必须配置**：
   - 使用强密码的JWT密钥（至少32字节）
   - 配置具体的CORS允许域名
   - 启用数据库SSL连接
   - 使用环境变量管理敏感信息

2. **不要提交到版本控制**：
   - `.env` 文件
   - 包含真实密码的配置文件
   - JWT密钥

3. **定期更新依赖**：
   - 定期检查并更新依赖包版本
   - 关注安全漏洞公告

## 测试建议

优化后建议进行以下测试：

1. ✅ API路径是否正确
2. ✅ 登录功能是否正常
3. ✅ Token验证是否正常
4. ✅ 异常处理是否正确
5. ✅ CORS配置是否生效
6. ✅ 环境变量配置是否生效

