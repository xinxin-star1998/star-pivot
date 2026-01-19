StarPivot 是基于 Spring Boot + Vue 3 的前后端分离 RBAC 管理与数据分析系统。下列内容聚焦架构、关键配置与快速上手。

---

## 1. 项目概览
- 技术栈：Spring Boot 3.2.10、Vue 3.5.21、Element Plus、Vite、TypeScript。
- 结构：Maven 多模块后端 + 独立前端；依赖方向 `controller -> system -> common`，前端通过 HTTP 调用后端。
- 主要能力：用户/角色/菜单/权限管理，JWT 无状态认证，动态菜单与按钮级权限，缓存优化。

## 2. 模块说明
- `star-pivot-common`：通用工具、异常与统一返回体、常量枚举、基础配置。
- `star-pivot-system`：领域模型（`SysUser`/`SysRole`/`SysMenu` 等）、Mapper 与 Service、JWT 工具、令牌黑名单。
- `star-pivot-controller`：启动入口、Web 配置（CORS/异常/拦截器/MyBatis-Plus/Redis）、Spring Security + JWT 过滤器、业务 Controller。
- `star-pivot-ui`：Vue 3 + Vite 前端，动态路由与菜单渲染，基于 Element Plus 的后台界面。

## 3. 技术与依赖管理
- 顶层 `pom.xml` 使用 Spring Boot 官方 BOM（`spring-boot-dependencies`）统一三方版本。
- 内部模块依赖通过 Maven 模块引用，不在 `dependencyManagement` 冗余声明。

## 4. 快速开始
### 后端
1) 环境：JDK 17+，Maven，Redis。  
2) 关键配置：`star-pivot-controller/src/main/resources/application.yml`（数据源、Redis、JWT、context-path）。  
3) 启动：
```bash
mvn -pl star-pivot-controller -am clean install
mvn -pl star-pivot-controller spring-boot:run
```
应用默认 context-path 为 `/api`。

### 前端
1) 环境：Node 18+。  
2) 安装与运行（根据团队习惯选择 npm/pnpm/yarn）：
```bash
cd star-pivot-ui
npm install
npm run dev    # 开发
npm run build  # 构建
```

## 5. 认证与授权（Spring Security + JWT）
- 核心配置：`star-pivot-controller/src/main/java/com/star/pivot/security/SecurityConfig.java`。
- 过滤器：`JwtAuthenticationFilter` 从 `Authorization: Bearer <token>` 解析用户，校验签名与过期，检查黑名单。
- 黑名单：`JwtBlackListManager`（Redis，key 形如 `jwt:logout:{token}`，TTL 为剩余有效期）。
- 登录：`POST /api/auth/login` -> `AuthController.login` -> 生成 JWT 返回。  
- 登出：`POST /api/auth/logout` -> 将 token 写入黑名单并清空上下文。
- 放行：`/api/auth/login` 及可选的 Swagger/Knife4j 文档路径；其余均需认证。

## 6. 缓存策略
- 开关：`@EnableCaching` + Redis。  
- 菜单树：`SysMenuServiceImpl.menuTree()` 使用 `@Cacheable(cacheNames = "menuTree", key = "'all'")`；菜单变更使用 `@CacheEvict(allEntries = true)`。  
- 字典数据：`DictDataServiceImpl.selectDictDataByType` 使用 `@Cacheable(cacheNames = "dictData", key = "#dictType")`；变更时 `@CacheEvict(allEntries = true)`。

## 7. 菜单 / 路由 / 权限映射
- 后端模型：`SysMenu`（表 `sys_menu`），字段 `path` 对应前端路由，`component` 对应前端组件路径（如 `system/user/index`），`perms` 对应按钮/操作权限。
- 角色授权：`SysRole` 通过 `sys_role_menu`、`sys_user_role` 绑定菜单与权限点。
- 登录后接口 `AuthController.getCurrentUser` 返回用户、角色、菜单/权限列表（admin 返回全量），前端据此构建动态路由和侧边菜单，控制按钮可见性。

## 8. 路由与前端约定
- 路由入口：`star-pivot-ui/src/router/index.ts`，`createWebHistory()` 模式，部署需代理兜底（如 Nginx `try_files $uri /index.html;`）。
- 静态路由：登录/注册/异常页等在 `staticRoutes`；首页常量 `HOME_PAGE_PATH = '/dashboard/console'`，应与后台菜单的主工作台一致。

## 9. 配置要点
- 接口前缀：`server.servlet.context-path: /api`，外部调用统一 `/api/**`，Spring Security 内部匹配不含 context-path（如 `/auth/login`）。
- Swagger/Knife4j：可通过 `security.swagger-permit-all` 控制是否放行文档访问。
- Redis：用于缓存与 JWT 黑名单，需确保连接信息正确。

## 10. 部署与运维建议
- 后端：保持使用 Spring Boot BOM，避免手动指定 Spring 相关版本；HTTP 与安全过滤器收敛在 `controller`，领域逻辑放 `system`。  
- 前端：确保生产环境有路由兜底；根据需要配置环境变量的后端接口基址。  
- 权限：在团队内明确菜单字段与前端路由、`perms` 的对应关系，避免新增页面/按钮时遗漏授权。  
- 性能：读多写少的菜单/字典接口开启缓存，变更时依赖注解自动驱逐。

## 11. 常用路径速览
- 启动类：`star-pivot-controller/src/main/java/com/star/pivot/StarPivotApplication.java`
- 安全配置：`star-pivot-controller/src/main/java/com/star/pivot/security/SecurityConfig.java`
- JWT 工具：`star-pivot-system/src/main/java/com/star/pivot/system/utils/JwtUtil.java`
- 黑名单管理：`star-pivot-system/src/main/java/com/star/pivot/system/utils/JwtBlackListManager.java`
- 菜单服务：`star-pivot-system/src/main/java/com/star/pivot/system/service/impl/SysMenuServiceImpl.java`
- 路由入口：`star-pivot-ui/src/router/index.ts`