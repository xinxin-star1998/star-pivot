# 更新日志

本文件遵循 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/) 格式，版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [Unreleased]

## [1.1.0] - 2026-06-27

### Added

- 开源治理文件：`CODE_OF_CONDUCT.md`、`SECURITY.md`、Issue 模板
- OSS-Compass 评估徽章与开源健康度差距分析文档

### Fixed

- 删除数据库迁移依赖
- 修复验证码相关问题（仅显示大写字母）
- 修复修改密码后旧密码在缓存期限内仍可登录的问题
- 代码生成器与前端格式化优化

### Changed

- 升级 Spring Boot 至 3.5.x，接口文档切换为 Knife4j

## [1.0.0] - 2026-04-07

首个稳定版本发布。

### Added

- 完整的 RBAC 权限管理系统（用户 / 角色 / 菜单 / 按钮级权限）
- JWT 无状态认证 + Redis Token 黑名单
- 动态菜单与动态路由
- 部门（树形）、岗位、字典管理
- 阿里云 OSS 文件存储（统一替换 MinIO）
- EasyExcel 模块化导入导出
- 可配置用户注册（系统参数开关 + 限流 + 默认角色）
- 商城模块（商品图片 OSS 上传，存 `objectName`，展示走预签名 URL）
- MyBatis-Plus 代码生成器（ReqBo 模板迁移）
- 定时任务（Quartz）、系统监控模块
- Vue 3 + TypeScript + Element Plus 前端
- SpringDoc / Knife4j API 文档
- GitHub Actions CI（后端 / 前端 / Docker / SonarQube）

### Changed

- 多模块 Maven 架构重构（dependencies → framework → module → controller）
- 生产环境配置校验与安全加固

[Unreleased]: https://github.com/xinxin-star1998/star-pivot/compare/v1.1.0...HEAD
[1.1.0]: https://github.com/xinxin-star1998/star-pivot/releases/tag/v1.1.0
[1.0.0]: https://github.com/xinxin-star1998/star-pivot/releases/tag/v1.0.0
