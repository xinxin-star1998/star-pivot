基于对项目的分析，我发现 StarPivot 是一个基于 Spring Boot + Vue 3 的现代化企业级系统。

## ✅ 项目分析结论
基于仓库结构分析，StarPivot 是一个采用前后端分离架构的企业级权限管理和数据分析系统。

## ✅ 依据
- 技术栈：Spring Boot 3.2.10 + Vue 3.5.21 + Element Plus
- 架构模式：Maven 多模块项目，前后端分离
- 核心模块：`star-pivot-common`（公共模块）、`star-pivot-system`（系统模块）、`star-pivot-controller`（控制层）、`star-pivot-ui`（前端界面）

---

## 一、整体架构与模块划分

### 1. 模块职责

- **star-pivot-common**
  - 位置：`star-pivot-common/`
  - 职责：通用工具与基础能力
  - 主要内容：
    - 通用工具类（字符串、时间、集合等）
    - 公共异常与统一返回体封装
    - 通用常量、枚举
    - 与具体业务无关的基础配置

- **star-pivot-system**
  - 位置：`star-pivot-system/`
  - 职责：系统领域与业务能力
  - 主要内容：
    - 领域实体（如 `SysUser`、`SysRole`、`SysMenu` 等）
    - Mapper 接口与 XML（数据访问层）
    - 业务 Service / ServiceImpl
    - JWT 工具类、令牌黑名单管理（不依赖具体 Web 容器）

- **star-pivot-controller**
  - 位置：`star-pivot-controller/`
  - 职责：应用启动与接口层
  - 主要内容：
    - 应用启动类 `StarPivotApplication`
    - Web 配置（CORS、全局异常、拦截器、MyBatis-Plus、Redis 等）
    - Spring Security 配置与 JWT 认证过滤器
    - 各业务 Controller（用户、角色、菜单、字典等）

- **star-pivot-ui**
  - 位置：`star-pivot-ui/`
  - 职责：前端界面与交互
  - 主要内容：
    - Vue 3 + TypeScript + Vite 前端工程
    - 动态菜单与路由渲染
    - 基于 Element Plus 的后台管理界面
    - 图表、表格、表单等业务组件

### 2. 模块依赖关系

- `star-pivot-common`：不依赖其它业务模块
- `star-pivot-system`：依赖 `star-pivot-common`
- `star-pivot-controller`：依赖 `star-pivot-common` 和 `star-pivot-system`
- `star-pivot-ui`：前端独立工程，通过 HTTP 调用后端接口

逻辑依赖方向：

- `controller -> system -> common`
- `ui -> controller`（通过 `/api/**` HTTP 接口）

---

## 二、后端依赖与配置约定

### 1. Maven 多模块与 BOM 管理

- 顶层 `pom.xml` 使用 **Spring Boot 官方 BOM**：
  - 依赖：`org.springframework.boot:spring-boot-dependencies:${spring-boot.version}`
  - 由 Spring Boot 统一管理 Spring Framework / Spring Security / Spring Data 等版本
- 已移除多余的 Spring Framework / Spring Security BOM，降低版本冲突风险
- 顶层 `dependencyManagement` 仅用于管理三方库版本，不再声明项目内部模块依赖

### 2. 应用入口与 Mapper 扫描

- 启动类：`star-pivot-controller/src/main/java/com/star/pivot/StarPivotApplication.java`
- 关键注解：
  - `@SpringBootApplication(scanBasePackages = "com.star.pivot")`
  - `@MapperScan("com.star.pivot.*.mapper")`
  - `@EnableCaching` 开启 Spring Cache（用于菜单树、字典数据等缓存）

### 3. 安全与认证（Spring Security + JWT）

- 核心配置类：`star-pivot-controller/src/main/java/com/star/pivot/security/SecurityConfig.java`
- 关键点：
  - 禁用 CSRF，使用 `SessionCreationPolicy.STATELESS`（无状态会话）
  - 添加 JWT 过滤器 `JwtAuthenticationFilter`：
    - 位置：`star-pivot-controller/src/main/java/com/star/pivot/security/JwtAuthenticationFilter.java`
    - 职责：
      - 从 `Authorization` 头中解析 `Bearer` Token
      - 调用 `JwtUtil` 验证、解析用户名
      - 检查 `JwtBlackListManager` 中是否在黑名单
      - 将认证信息写入 `SecurityContext`
  - 请求授权规则：
    - `/auth/login`：匿名访问（注意：外部访问路径为 `/api/auth/login`，由 `server.servlet.context-path=/api` 决定，Spring Security 内部匹配时不包含 context-path）
    - Swagger / Knife4j 文档路径（`/swagger-ui/**`、`/v3/api-docs/**`、`/doc.html` 等）可通过配置 `security.swagger-permit-all` 控制是否放开
    - 其他所有请求：`authenticated()`，必须登录后访问

### 4. JWT 与黑名单机制

- JWT 工具类：`star-pivot-system/src/main/java/com/star/pivot/system/utils/JwtUtil.java`
  - 负责生成 Token、解析 Claims、校验签名与过期时间
  - 通过配置项 `jwt.secret` 和 `jwt.expiration` 控制密钥与有效期
- 令牌黑名单管理：`star-pivot-system/src/main/java/com/star/pivot/system/utils/JwtBlackListManager.java`
  - 将已登出的 Token 写入 Redis：key 形如 `jwt:logout:{token}`，并设置 TTL 为 Token 剩余有效期
  - 登录状态校验时，优先检查是否在黑名单中
- 登录流程：
  - 接口：`POST /api/auth/login`
  - 控制器：`AuthController.login`
  - 服务：`AuthServiceImpl.login`
  - 步骤：用户名密码认证 -> 查询用户信息 -> 生成 JWT -> 返回 `LoginResponse`（包含 `token`、`username` 等）
- 登出流程：
  - 接口：`POST /api/auth/logout`
  - 控制器：`AuthController.logout`
  - 步骤：从请求头提取 Token -> 校验有效性 -> 写入黑名单 -> 清空 `SecurityContext`

### 5. 缓存策略（菜单树与字典数据）

- 缓存开启：`@EnableCaching`（见启动类） + Redis 配置（`RedisConfig`）
- 菜单树缓存：
  - 实现类：`SysMenuServiceImpl`
  - 方法与注解：
    - `@Cacheable(cacheNames = "menuTree", key = "'all'") List<SysMenu> menuTree()`：缓存完整菜单树
    - 在菜单新增/修改/删除时，通过 `@CacheEvict(cacheNames = "menuTree", allEntries = true)` 自动清理缓存
- 字典数据缓存：
  - 实现类：`DictDataServiceImpl`
  - 方法与注解：
    - `@Cacheable(cacheNames = "dictData", key = "#dictType") List<DictDataVO> selectDictDataByType(String dictType)`：按字典类型缓存数据
    - 新增/修改/删除字典数据时，通过 `@CacheEvict(cacheNames = "dictData", allEntries = true)` 清理缓存

---

## 三、前后端路由与菜单/权限映射

### 1. 后端菜单模型（SysMenu）

- 表与实体：`sys_menu` / `SysMenu`
- 关键字段：
  - `menuType`：菜单类型（目录、菜单、按钮）
  - `path`：路由路径（对应前端 `vue-router` 的 `path`）
  - `component`：前端组件路径（如 `system/user/index`）
  - `perms`：权限标识（如 `system:user:list`），可用于按钮级权限控制
  - `visible` / `status`：显示状态与启用状态
- 角色菜单关系：通过 `sys_role_menu`、`sys_user_role` 等表完成角色到菜单的授权

### 2. 前端路由与静态路由

- 路由入口：`star-pivot-ui/src/router/index.ts`
  - 使用 `createWebHistory()`，需要服务端配置路由兜底（例如 Nginx 中 `try_files $uri /index.html;`）
  - 静态路由：`staticRoutes`（登录、注册、异常页等，无需权限）
  - 主页路径常量：
    - `export const HOME_PAGE_PATH = '/dashboard/console'`
    - 建议保持与后台菜单中主工作台路由一致
- 静态路由定义示例：`star-pivot-ui/src/router/routes/staticRoutes.ts`
  - 登录页：`/auth/login`
  - 注册页：`/auth/register`
  - 忘记密码：`/auth/forget-password`
  - 异常页：`/403`、`/500`、`/:pathMatch(.*)*`（404）

### 3. 菜单-路由-权限三者关系

- **菜单（后端）**：`SysMenu`
  - `path` 与前端路由 `path` 对应
  - `component` 用于前端通过动态导入加载对应 `.vue` 组件
  - `perms` 与前端按钮级权限（如指令/组件的 `v-permission`）对应
- **路由（前端）**：`vue-router` 路由记录
  - `path`：与菜单 `path` 对应
  - `name`：通常与 `routeName` 或组件名称对应
  - `meta`：可包含 `roles`、`perms`、`title` 等信息，用于前端权限和显示控制
- **权限（角色/权限点）**：
  - 通过 `SysRole` 与 `SysMenu` 的关联，将角色与菜单（及权限点）绑定
  - 登录成功后，后端 `AuthController.getCurrentUser` 会返回：
    - 用户基本信息 `user`
    - 角色列表 `roles`
    - 菜单/权限列表 `permissions`（admin 角色返回完整菜单树，否则返回授权菜单）
  - 前端根据返回的菜单/权限数据构建动态路由和侧边菜单，并控制页面与按钮的可见性

---

## 四、前后端接口约定

- 统一前缀：
  - 后端通过 `application.yml` 配置：`server.servlet.context-path: /api`
  - 所有后端接口统一以 `/api` 为前缀对外暴露，如：
    - 登录：`POST /api/auth/login`
    - 获取当前用户信息：`GET /api/auth/userinfo`
    - 菜单管理：`/api/system/menu/**`
    - 用户管理：`/api/system/user/**`
- 安全拦截：
  - Spring Security 内部仅匹配去掉 context-path 的路径（例如 `/auth/login`），外部调用统一使用 `/api/...`。

---

## 五、开发与部署建议

1. **后端依赖管理**：统一通过 Spring Boot BOM 管理版本，避免手动指定过多 Spring 相关版本。
2. **模块职责**：
   - 领域逻辑尽量收敛在 `star-pivot-system` 中
   - 所有与 HTTP / 安全过滤器相关的内容放在 `star-pivot-controller` 中
3. **前端路由模式**：
   - 默认使用 `createWebHistory`，部署时确保反向代理配置了前端路由兜底
4. **缓存**：
   - 对菜单树和字典数据等读多写少的接口启用 Redis 缓存，提高性能
5. **文档**：
   - 在团队内说明菜单字段与前端路由、权限标识之间的对应关系，便于统一扩展与排查问题