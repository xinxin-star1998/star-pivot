

# StarPivot

## 简介

StarPivot 是一个基于 **Vue 3 + Spring Boot** 的现代化全栈前后端分离后台管理系统模板。该项目提供了一个完整的企业级应用开发框架，包含了用户管理、角色管理、菜单权限管理、部门管理、岗位管理、字典管理等核心功能模块。

系统采用了主流的技术栈构建，具备良好的扩展性和安全性，适用于快速搭建各类后台管理系统。

## 技术栈

### 前端 (star-pivot-ui)
- **核心框架**: Vue 3 (Composition API)
- **构建工具**: Vite
- **语言**: TypeScript
- **状态管理**: Pinia
- **UI 组件库**: Element Plus (基于 `art-*` 组件库封装)
- **图表库**: ECharts
- **路由**: Vue Router
- **样式方案**: Tailwind CSS + SCSS
- **国际化**: 集成多语言支持

### 后端 (star-pivot-*)
- **核心框架**: Spring Boot 3
- **安全框架**: Spring Security + JWT
- **ORM 框架**: MyBatis-Plus
- **缓存**: Redis
- **数据库**: MySQL (默认支持)
- **工具库**: Lombok, Hutool

## 核心功能

- **用户与认证**: 支持用户登录、登出、Token 刷新、密码加密 (BCrypt) 和 JWT 黑名单机制。
- **权限控制 (RBAC)**: 
  - 实现了基于角色的访问控制 (Role-Based Access Control)。
  - 支持按钮级别的权限控制 (`@PreAuthorize` 注解)。
  - 动态路由：前端路由根据后端返回的菜单权限动态生成。
- **系统管理**:
  - **用户管理**: 用户的增删改查，分配角色、分配岗位，重置密码。
  - **角色管理**: 角色的增删改查，分配菜单权限、数据权限。
  - **菜单管理**: 配置系统左侧导航栏菜单，支持按钮权限标识。
  - **部门管理**: 支持树形结构的部门数据。
  - **岗位管理**: 管理用户岗位信息。
  - **字典管理**: 维护系统中常用的下拉选项数据。
- **系统特性**:
  - **多主题支持**: 支持明暗色模式切换，自定义主题色。
  - **数据可视化**: 集成 ECharts，提供折线图、柱状图、饼图等常用图表组件。
  - **数据表格**: 支持分页查询、列配置、搜索过滤、Excel 导入导出。

## 项目结构

```
StarPivot
├── star-pivot-ui/                # 前端项目 (Vue 3 + Vite)
│   ├── src/
│   │   ├── api/                  # 接口定义
│   │   ├── assets/               # 静态资源
│   │   ├── components/           # 公共组件 (包含 art-* 系列组件)
│   │   ├── config/               # 系统配置
│   │   ├── directives/           # 自定义指令
│   │   ├── hooks/                # 组合式函数
│   │   ├── locales/              # 国际化语言包
│   │   ├── router/               # 路由配置
│   │   ├── store/                # Pinia 状态管理
│   │   ├── types/                # TypeScript 类型定义
│   │   ├── utils/                # 工具函数
│   │   └── views/                # 页面视图
│   └── scripts/                  # 菜单数据生成脚本
│
├── star-pivot-controller/        # 启动模块与 Web 配置
│   └── src/main/java/.../
│       ├── config/               # 全局配置类 (CORS, Redis, Security, MybatisPlus)
│       ├── controller/           # REST API 控制器
│       └── security/             # JWT 安全处理器
│
├── star-pivot-system/            # 业务逻辑模块
│   └── src/main/java/.../
│       ├── domain/               # 实体类 (Entity), 传输对象 (DTO/VO)
│       ├── mapper/               # MyBatis Mapper 接口
│       ├── service/              # 服务层接口与实现
│       └── utils/                # 业务工具类 (JWT, LoginUser)
│
└── star-pivot-common/            # 通用模块
    └── src/main/java/.../
        ├── annotation/           # 自定义注解 (@Log, @PreAuthorize)
        ├── aspect/               # AOP 切面
        ├── constants/            # 常量定义
        ├── domain/               # 基础实体
        ├── exception/            # 异常定义
        └── utils/                # 通用工具类
```

## 快速开始

### 1. 环境准备

- **JDK**: 17 或更高版本
- **Node.js**: 18.x 或更高版本
- **Redis**: 5.x 或更高版本
- **MySQL**: 8.0 或更高版本

### 2. 后端部署

1.  创建数据库，执行数据库脚本（通常在项目根目录或 `doc` 目录下）。
2.  修改 `star-pivot-controller/src/main/resources/application.yml` 中的数据库连接配置和 Redis 配置。
3.  使用 Maven 编译打包：
    ```bash
    mvn clean install -DskipTests
    ```
4.  运行主启动类 `com.star.pivot.StarPivotApplication`。

### 3. 前端部署

1.  进入前端目录：
    ```bash
    cd star-pivot-ui
    ```
2.  安装依赖（推荐使用 pnpm）：
    ```bash
    pnpm install
    ```
3.  配置环境变量：
    - 复制 `.env.example` 为 `.env.development` (开发环境) 或 `.env.production` (生产环境)。
    - 修改 `VITE_API_URL` 为后端服务的地址。
4.  启动开发服务器：
    ```bash
    pnpm dev
    ```
5.  构建生产版本：
    ```bash
    pnpm build
    ```

## 接口文档概览

系统提供了以下主要接口模块：

- **认证接口** (`/auth`): 登录、登出、获取用户信息。
- **用户管理** (`/sys/user`): 用户分页查询、增删改查、重置密码。
- **角色管理** (`/sys/role`): 角色分页、分配权限 (菜单/数据)。
- **菜单管理** (`/sys/menu`): 获取菜单树、增删改菜单。
- **部门管理** (`/sys/dept`): 获取部门树。
- **字典管理** (`/sys/dict/*`): 字典类型与字典数据管理。

## 贡献

1.  Fork 本仓库。
2.  新建 Feat_xxx 分支。
3.  提交代码。
4.  新建 Pull Request。

## 许可证

本项目遵循开源协议，具体许可证信息请查看项目根目录 LICENSE 文件。