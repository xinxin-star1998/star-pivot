

# StarPivot (星枢)

StarPivot 是一套**企业级权限管理系统**，前端采用 Vue3 + TypeScript + Vite + Pinia，后端采用 Spring Boot 3 + Spring Security + MyBatis-Plus。系统具有完善的权限认证机制、动态路由菜单、系统管理等核心功能，适用于构建中大型企业应用。

## ✨ 特性

*   **完善的用户权限体系**：支持用户、角色、部门、岗位的精细化管理。
*   **基于 RBAC 的权限控制**：支持菜单权限、按钮权限、数据权限控制。
*   **动态路由**：前端路由动态生成，无需手动配置，支持多级菜单。
*   **前后端分离架构**：高效的开发和部署体验。
*   **安全加固**：
    *   支持 JWT Token 认证。
    *   Token 黑名单机制（Redis 实现），支持强制登出。
    *   密码加密存储（BCrypt）。
    *   全局统一异常处理。
*   **丰富的系统功能**：字典管理、操作日志（切面实现）、数据字典等。

## 🛠️ 技术栈

### 后端 (Backend)
*   **核心框架**: Spring Boot 3
*   **安全框架**: Spring Security 6
*   **认证授权**: JWT (JJWT) + Redis
*   **ORM 框架**: MyBatis-Plus
*   **数据库**: MySQL 8.0+
*   **缓存**: Redis
*   **工具库**: Lombok, Hutool

### 前端 (Frontend)
*   **核心框架**: Vue 3
*   **语言**: TypeScript
*   **构建工具**: Vite
*   **状态管理**: Pinia
*   **UI 组件库**: Element Plus (深度定制)
*   **样式方案**: Tailwind CSS + SCSS

## 📂 项目结构

```text
StarPivot/
├── star-pivot-common/        # 公共模块
│   ├── annotation/           # 自定义注解 (@Log, @PreAuthorize)
│   ├── aspect/               # AOP 切面 (日志切面, 权限切面)
│   ├── constants/            # 常量定义 (状态码, 业务类型)
│   ├── domain/               # 基础实体 (BaseEntity, Result)
│   ├── exception/            # 异常定义 (BusinessException, ServiceException)
│   └── utils/                # 工具类 (DateUtils, SecurityUtils, StringUtils)
│
├── star-pivot-system/        # 业务核心模块
│   ├── domain/               # 实体类 (SysUser, SysRole, SysDept, SysMenu...)
│   ├── mapper/               # MyBatis Mapper 接口
│   └── service/              # 业务逻辑层 (ServiceImpl)
│
├── star-pivot-controller/    # 启动与 Web 层
│   ├── config/               # 配置类 (SecurityConfig, CorsConfig, RedisConfig)
│   ├── controller/           # REST API 控制器
│   └── security/             # 安全相关组件 (JWT 过滤器, 异常处理器)
│
└── star-pivot-ui/            # Vue3 前端项目
    ├── src/
    │   ├── api/              # 接口封装
    │   ├── components/       # 业务组件
    │   ├── router/           # 路由配置
    │   ├── store/            # Pinia 状态管理
    │   ├── utils/            # 工具函数
    │   └── views/            # 页面视图
    └── scripts/              # 菜单生成脚本 (generate-menu-data.js)
```

## 🚀 快速开始

### 1. 环境准备
*   JDK 17+
*   Node.js 18+
*   MySQL 8.0+
*   Redis 6.0+

### 2. 后端部署

**A. 导入数据库**
创建数据库 `star_pivot`，并导入项目根目录或 SQL 文件夹下的 `star-pivot.sql` 文件。

**B. 配置数据库与 Redis**
修改 `star-pivot-controller/src/main/resources/application.yml` 中的数据源配置：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/star_pivot
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
```

**C. 编译运行**
在项目根目录下执行：
```bash
mvn clean package -DskipTests
java -jar star-pivot-controller/target/star-pivot-controller.jar
```

### 3. 前端部署

**A. 安装依赖**
进入 `star-pivot-ui` 目录：
```bash
pnpm install
```

**B. 配置环境变量**
复制 `.env.example` 为 `.env.development`，并配置 API 地址：
```env
VITE_API_URL=http://localhost:8080/your-api-context
```

**C. 启动开发服务器**
```bash
pnpm run dev
```

**D. 构建生产版本**
```bash
pnpm run build
```

## 📖 系统功能

*   **登录认证**：支持账号密码登录，登录成功后返回 JWT Token。
*   **用户管理**：支持用户的增删改查，分配角色，重置密码。
*   **角色管理**：支持角色的增删改查，分配菜单权限和数据权限。
*   **菜单管理**：支持配置前端路由、组件路径、权限字符。支持树形结构展示。
*   **部门管理**：支持多级部门架构。
*   **字典管理**：维护系统中常用的下拉选项（如：性别、状态等）。

## 🔧 进阶功能

### 动态菜单生成 (UI Utility)
前端 `scripts/generate-menu-data.js` 脚本可以扫描 `src/views` 目录，**自动生成 Vue 文件对应的后端菜单 SQL**。这极大地减少了手动配置菜单的工作量。

运行方式：
```bash
cd star-pivot-ui/scripts
node generate-menu-data.js
```

## 📝 接口文档概览

基础路径：`/api`

| 模块 | 路由 | 说明 |
| :--- | :--- | :--- |
| **Auth** | `/auth/login` | 用户登录 |
| | `/auth/logout` | 退出登录 |
| | `/auth/userinfo` | 获取当前用户信息 |
| **User** | `/sys/user/pageList` | 分页查询用户 |
| | `/sys/user/add` | 新增用户 |
| | `/sys/user/update` | 修改用户 |
| **Role** | `/sys/role/list` | 角色列表 |
| | `/sys/role/addRole` | 新增角色 |
| **Menu** | `/sys/menu/menuTree` | 获取完整菜单树 |
| | `/sys/menu/userMenuTree` | 获取当前用户菜单 |

## 🤝 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request

## 📄 许可证

本项目基于 [MIT](LICENSE) 协议开源。

---

**StarPivot - 助力企业数字化转型**