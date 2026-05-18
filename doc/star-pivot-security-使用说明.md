# star-pivot-security 使用说明

本文档说明 `star-pivot-security` 模块的能力、配置项与扩展方式，便于业务模块在**不修改** `SecurityConfig` 的前提下定制放行路径、过滤器与授权规则。

相关文档：[`权限编码与数据权限规范.md`](权限编码与数据权限规范.md)（`@PreAuthorize` 权限码约定）。

---

## 一、模块定位

| 项 | 说明 |
|----|------|
| Maven 坐标 | `com.star.pivot:star-pivot-security` |
| 所在目录 | `star-pivot-framework/star-pivot-security` |
| 引入方式 | 应用层依赖 `star-pivot-framework-boot-starter` 即可（已聚合 security） |
| 技术栈 | Spring Security 6、JWT（jjwt）、Redis（黑名单 / 刷新令牌） |

### 核心能力

- **无状态 JWT 鉴权**：`SessionCreationPolicy.STATELESS`，请求头 `Authorization: Bearer <token>`
- **令牌黑名单**：登出后将 Access Token 写入 Redis，立即失效
- **刷新令牌**：Refresh Token 存 Redis，支持 `/auth/refresh` 续期
- **密钥轮换**：`jwt.previous-secret` 过渡期内兼容旧 Token 校验
- **方法级权限**：`@EnableMethodSecurity` + `@PreAuthorize("hasAuthority('...')")`
- **可扩展放行**：配置文件、`PermitAllPathProvider`、`HttpSecurityCustomizer`

### 包结构（节选）

```
com.star.pivot.security
├── config          SecurityConfig、StarPivotSecurityProperties、CorsProperties
├── filter          JwtAuthenticationFilter、JwtSecurityExceptionHandler
├── token           JwtUtil、JwtKeyManager、JwtBlackListManager、RefreshTokenManager
├── context         SecurityUtils、SecurityContextUtils
└── extension       PermitAllPathProvider、HttpSecurityCustomizer
```

业务侧认证入口在 `star-pivot-system`（`AuthService`、`TokenService`、`CustomerUserDetailService`），security 模块提供过滤器与 Token 基础设施。

---

## 二、请求鉴权流程

```
客户端请求
    │
    ▼
JwtAuthenticationFilter
    ├─ 无 Token → 继续过滤链（后续由 authorizeHttpRequests 判定是否 401）
    ├─ Token 在黑名单 → 401 JSON
    ├─ Token 校验失败/过期 → 不设置 Authentication，继续过滤链
    └─ Token 有效 → loadUserByUsername → 写入 SecurityContext
    │
    ▼
authorizeHttpRequests
    ├─ 命中 permitAll 路径 → 放行
    └─ 其余路径 → 需已认证（authenticated）
    │
    ▼
Controller
    └─ @PreAuthorize → 方法级权限（403）
```

**未认证 / 无权限响应**（`JwtSecurityExceptionHandler`）：

| 场景 | HTTP | 响应体 |
|------|------|--------|
| 未登录访问受保护接口 | 401 | `Result`，`ErrorCode.UNAUTHORIZED` |
| 已登录但权限不足 | 403 | `Result`，`ErrorCode.FORBIDDEN` |
| Token 在黑名单 | 401 | `{"code":401,"msg":"Token已失效，请重新登录"}`（过滤器直接写回） |

### 认证相关接口（默认匿名）

实际 URL 需加上 `server.servlet.context-path`（默认为 `/api`），例如登录为 `POST /api/auth/login`。

| 路径 | 说明 |
|------|------|
| `POST /auth/login` | 用户名密码登录，返回 accessToken、refreshToken |
| `POST /auth/refresh` | 使用 refreshToken 换取新 accessToken |
| `GET/POST /auth/captcha`、`/auth/captcha/verify` | 验证码 |
| `POST /auth/register` | 注册（由 `GlobalPermitAllPathProvider` 追加） |
| `POST /auth/logout` | 登出（需携带 Token，将 Access Token 加入黑名单） |

登录、刷新、发 Token 等逻辑见 `AuthServiceImpl`、`TokenServiceImpl`；security 模块不负责业务校验（验证码、限流、账户锁定在 system 模块）。

---

## 三、配置项

### 3.1 JWT（`jwt.*`）

在 `application.yml` 或环境配置（如 `application-local.yml`）中设置：

```yaml
jwt:
  secret: ${JWT_SECRET}              # 必填，HMAC 密钥，至少 32 字节
  previous-secret: ${JWT_PREVIOUS_SECRET:}  # 可选，密钥轮换时的旧密钥
  expiration: ${JWT_EXPIRATION:86400000}     # Access Token 有效期（毫秒），默认 24h
  refresh-expiration: 604800000              # Refresh Token 有效期（毫秒），默认 7 天
```

| 配置项 | 说明 |
|--------|------|
| `jwt.secret` | 签发与校验主密钥；轮换时改为新值，旧值写入 `previous-secret` |
| `jwt.previous-secret` | 过渡期内仍可用旧密钥验签已发出的 Token |
| `jwt.expiration` | Access Token 过期时间 |
| `jwt.refresh-expiration` | Refresh Token 在 Redis 中的 TTL |

生产环境请通过环境变量注入 `JWT_SECRET`，勿将真实密钥提交仓库。

### 3.2 安全扩展（`security.*`）

```yaml
security:
  swagger-permit-all: true   # 是否匿名放行 Swagger/OpenAPI 路径，生产建议 false
  permit-all-paths:          # 额外匿名路径（Ant 风格）
    - /public/**
    - /actuator/health
```

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `security.swagger-permit-all` | `true` | 放行 `/swagger-ui/**`、`/v3/api-docs/**` 等 |
| `security.permit-all-paths` | `[]` | 与内置登录路径、各 `PermitAllPathProvider` 合并去重 |

### 3.3 跨域（`cors.*`）

```yaml
cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:*}
```

| 值 | 行为 |
|----|------|
| `*` | 允许任意来源，`Allow-Credentials=false` |
| 逗号分隔域名 | 仅允许列表内来源，`Allow-Credentials=true` |

### 3.4 内置匿名路径（代码写死）

`SecurityConfig` 已内置（含带 `/api` 前缀的副本，兼容网关或前端代理）：

- `/auth/login`、`/auth/refresh`、`/auth/captcha`、`/auth/captcha/verify`
- 以及对应的 `/api/auth/...`

此外会合并：`security.permit-all-paths`、所有 `PermitAllPathProvider` 实现。

---

## 四、扩展方式

### 方式 1：配置文件追加放行路径

在 `application.yml`（或 `application-{profile}.yml`）中增加：

```yaml
security:
  permit-all-paths:
    - /public/**
    - /actuator/health

cors:
  allowed-origins: "https://admin.example.com,https://www.example.com"
```

适用于运维可调、无需改代码的静态路径。

### 方式 2：实现 `PermitAllPathProvider`（推荐）

在任意被 Spring 扫描的模块中注册 `@Component`：

```java
package com.star.pivot.xxx;

import com.star.pivot.security.extension.PermitAllPathProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DemoPermitAllPathProvider implements PermitAllPathProvider {
    @Override
    public List<String> permitAllPaths() {
        return List.of(
                "/public/**",
                "/webhook/payment/callback"
        );
    }
}
```

**项目内参考：**

| 类 | 作用 |
|----|------|
| `GlobalPermitAllPathProvider` | 全局放行，如 `/auth/register` |
| `DevToolsPermitAllPathProvider` | 仅 `local`/`dev`/`test` 放行 Druid、Swagger 等 |

生产环境勿将 Druid、Swagger 加入永久放行；开发环境可依赖 `DevToolsPermitAllPathProvider` 或关闭 `security.swagger-permit-all` 后自行控制。

### 方式 3：实现 `HttpSecurityCustomizer`

用于追加过滤器、授权规则等（**慎用**覆盖默认认证逻辑）：

```java
package com.star.pivot.xxx;

import com.star.pivot.security.extension.HttpSecurityCustomizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class DemoHttpSecurityCustomizer implements HttpSecurityCustomizer {
    @Override
    public void customize(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/demo/open/**").permitAll()
        );
        // http.addFilterBefore(new YourFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
```

说明：

- 在 `SecurityConfig` 完成默认 `authorizeHttpRequests` 与 `JwtAuthenticationFilter` 注册**之后**执行。
- 仅需匿名访问的路径，优先用 **方式 1 / 2**，避免与 `anyRequest().authenticated()` 顺序冲突。
- 追加 `permitAll` 规则时，应使用 `requestMatchers(...).permitAll()` 明确路径。

---

## 五、业务代码常用 API

### 5.1 获取当前用户

```java
import com.star.pivot.security.context.SecurityContextUtils;
import com.star.pivot.framework.security.LoginUserInfo;

LoginUserInfo user = SecurityContextUtils.getLoginUser();
Long userId = SecurityContextUtils.getUserId();
String username = SecurityContextUtils.getUsername();
```

`Principal` 为 system 模块的 `LoginUser`（实现 `LoginUserInfo`、`UserDetails`）时，`getLoginUser()` 可拿到完整业务字段。

### 5.2 密码加解密

```java
import com.star.pivot.security.context.SecurityUtils;

String encoded = SecurityUtils.encryptPassword(rawPassword);
boolean ok = SecurityUtils.matchesPassword(rawPassword, encoded);
```

与 `SecurityConfig` 中的 `BCryptPasswordEncoder` 算法一致。

### 5.3 方法级权限

Controller 上使用 Spring Security 表达式，权限码规范见 [`权限编码与数据权限规范.md`](权限编码与数据权限规范.md)：

```java
@PreAuthorize("hasAuthority('system:user:query')")
@GetMapping("/list")
public Result<?> list() { ... }

@PreAuthorize("hasAuthority('system:user:unLock') and @ss.hasRole('admin')")
@PostMapping("/unlock/{userId}")
public Result<?> unlock(...) { ... }
```

`@ss` 为 system 模块的 `PermissionService` Bean，用于角色等扩展判断。

### 5.4 Token 与黑名单（业务层）

登出、强制下线由 `TokenService` 调用 security 模块：

- `JwtBlackListManager.addToBlackList(token, expiration)` — Access Token 拉黑
- `RefreshTokenManager.revokeRefreshToken(userId)` — 吊销 Refresh Token

Redis Key 前缀：`jwt:logout:`（黑名单）、`jwt:refresh:user:{userId}`（刷新令牌）。

---

## 六、前端对接要点

1. 登录成功后保存 `accessToken`，请求头携带：`Authorization: Bearer <token>`。
2. Access Token 过期时调用 `POST /api/auth/refresh`，请求体含 `username`、`refreshToken`。
3. 登出调用 `POST /api/auth/logout`，并携带当前 Token，确保服务端拉黑。
4. 收到 401 时清除本地 Token 并跳转登录；403 提示无权限即可。

---

## 七、生产环境建议

| 项 | 建议 |
|----|------|
| `security.swagger-permit-all` | 设为 `false`，或通过网关限制文档访问 |
| `DevToolsPermitAllPathProvider` | 生产 profile 不激活（已用 `@Profile({"local","dev","test"})`） |
| `cors.allowed-origins` | 配置明确前端域名，避免 `*` |
| `jwt.secret` | 环境变量注入，定期轮换并配置 `previous-secret` |
| Redis | 黑名单与刷新令牌依赖 Redis，需保证高可用 |
| 敏感路径 | 回调、健康检查等通过 `permit-all-paths` 最小化放行范围 |

示例（生产 profile 片段）：

```yaml
security:
  swagger-permit-all: false
  permit-all-paths: []

cors:
  allowed-origins: https://admin.example.com

jwt:
  secret: ${JWT_SECRET}
  previous-secret: ${JWT_PREVIOUS_SECRET:}
  expiration: 7200000   # 例如 2 小时
```

---

## 八、常见问题

**1. 接口 401，但已传 Token**

- 检查 Header 是否为 `Bearer ` 前缀（注意空格）。
- Token 是否过期、是否已登出（黑名单）。
- `jwt.secret` 是否与签发时一致；轮换密钥时是否配置了 `previous-secret`。

**2. 配置了 `permit-all-paths` 仍 401**

- 路径是否包含 `context-path`：Security 匹配一般为 Servlet 路径（如 `/auth/login`），若网关剥离了 `/api` 前缀，勿重复加前缀。
- 可同时配置带/不带 `/api` 的路径，或参考内置登录路径写法。

**3. Swagger 生产仍可访问**

- 设置 `security.swagger-permit-all: false`。
- 确认未激活 `DevToolsPermitAllPathProvider`（生产 profile）。

**4. CORS 预检失败**

- 检查 `cors.allowed-origins` 是否包含前端源。
- 使用具体域名时才能 `Allow-Credentials=true`；带 Cookie 时不应配 `*`。

**5. `@PreAuthorize` 不生效**

- 确认调用的是 Spring 代理 Bean（同类内部自调用不经过代理）。
- 权限字符串与菜单 `perms`、用户角色授权一致，见权限规范文档。

**6. 如何新增模块依赖 security**

- 模块只需依赖 `star-pivot-framework-core` 等业务包；**运行态**由 `star-pivot-controller` 引入 `star-pivot-framework-boot-starter` 即可。
- 若独立启动某模块，需显式依赖 `star-pivot-security` 或 boot-starter。

---

## 九、对接检查清单

- [ ] 敏感接口使用 `@PreAuthorize`，权限码已录入菜单
- [ ] 匿名接口通过 `permit-all-paths` 或 `PermitAllPathProvider` 登记，未误放行业务写操作
- [ ] 生产关闭 Swagger 匿名、限制 CORS、JWT 密钥走环境变量
- [ ] 登出/改密流程调用 `TokenService` 拉黑 Token
- [ ] 业务代码取当前用户使用 `SecurityContextUtils`，密码使用 `SecurityUtils`

按上述方式扩展即可在不 fork `SecurityConfig` 的前提下，与安全模块保持一致的行为与配置风格。
