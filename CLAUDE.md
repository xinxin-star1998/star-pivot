# CLAUDE.md

本文档为 Claude Code (claude.ai/code) 在处理此仓库代码时提供指导。

## 项目概述

StarPivot 是一个全栈企业管理系统，使用 Spring Boot 3（后端）和 Vue 3（前端）构建。它具有完整的 RBAC 权限管理系统，支持 JWT 认证、动态路由和按钮级权限。

## 架构

项目采用多模块 Maven 结构：
- `star-pivot-dependencies`: BOM 依赖管理
- `star-pivot-framework`: 核心功能（安全、日志、文件处理、通用工具）
- `star-pivot-module`: 业务模块（系统、字典、生成器、定时任务、监控、商城）
- `star-pivot-controller`: 应用程序入口点和控制器
- `star-pivot-ui`: 前端 Vue 3 应用

## 后端结构

后端使用 Spring Boot 3，包含：
- Java 17+
- Spring Security 6 用于身份验证和授权
- MyBatis-Plus 用于数据库操作
- MySQL 作为主数据库
- Redis 用于缓存和 JWT 黑名单
- Maven 用于依赖管理

## 前端结构

前端构建于：
- Vue 3 + TypeScript
- Vite 作为构建工具
- Element Plus UI 库
- Pinia 用于状态管理
- Vue Router 用于导航
- ECharts 用于数据可视化

## 开发命令

### 后端
- 构建项目: `mvn clean install`
- 运行后端: `mvn -pl star-pivot-controller spring-boot:run`
- 打包部署: `mvn clean package -DskipTests`

### 前端
- 安装依赖: `cd star-pivot-ui && pnpm install`
- 启动开发服务器: `pnpm dev`
- 构建生产版本: `pnpm build`

### 数据库设置
- 创建数据库: `CREATE DATABASE star-pivot DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
- 导入表结构: `mysql -u root -p star-pivot < sql/star-pivot.sql`

### 环境要求
- JDK 17+
- Maven 3.6+
- Node.js 20.19.0+
- pnpm 8.8.0+
- MySQL 5.7+/8.0+
- Redis 5.0+

## 默认登录凭据
- 用户名: admin
- 密码: admin123

## 主要功能
- 完整的 RBAC（基于角色的访问控制）系统
- JWT 无状态认证和 Redis 黑名单
- 动态菜单和路由
- 按钮级权限控制
- 部门管理（层级结构）
- 文件上传支持（集成阿里云 OSS）
- Excel 导入/导出功能
- 系统监控功能
- 通过 SpringDoc OpenAPI 提供 API 文档

## 项目导航
- 文档: `doc/` 目录包含架构图、规范和部署指南
- SQL 脚本: `sql/` 目录包含数据库初始化脚本
- 配置: 各种 .env 文件和应用程序属性文件
- 前端源码: `star-pivot-ui/src/` 目录