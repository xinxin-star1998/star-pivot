# 贡献指南（Gitee / GitHub 通用）

感谢你为 StarPivot 做出贡献。本文档用于统一 Gitee 与 GitHub 的代码提交、分支管理与 PR 规范，降低协作成本。

## 一、基础原则

- 一个分支只做一件事，避免“大杂烩”提交。
- 小步提交，提交信息清晰表达改动意图。
- 先本地自测，再发起 PR。
- 不提交敏感信息（密码、密钥、令牌、生产配置）。

## 二、分支命名规范

推荐格式：`<type>/<short-description>`

- `feature/*`：新功能
- `fix/*`：缺陷修复
- `refactor/*`：重构（不改变业务行为）
- `docs/*`：文档变更
- `chore/*`：构建、脚本、依赖、配置等杂项
- `test/*`：测试相关

示例：

- `feature/user-import`
- `fix/login-timeout`
- `docs/update-deploy-guide`

## 三、Commit Message 规范

采用 Conventional Commits：

`<type>(<scope>): <subject>`

常用 `type`：

- `feat`：新增功能
- `fix`：修复问题
- `refactor`：重构
- `docs`：文档
- `style`：格式调整（不影响逻辑）
- `test`：测试
- `chore`：工程化/依赖/配置
- `perf`：性能优化
- `build`：构建相关
- `ci`：CI/CD 相关

示例：

- `feat(auth): add account lock after failed attempts`
- `fix(config): prevent empty key from being saved`
- `docs(readme): update startup instructions`

约束建议：

- 标题使用英文，简洁明确，建议不超过 72 字符。
- `subject` 使用祈使句，不以句号结尾。
- 如有破坏性变更，在正文标注 `BREAKING CHANGE:`。

### Commit 自动校验（Husky + Commitlint）

项目已配置 `husky` 与 `commitlint`，会在 `git commit` 时自动校验提交信息格式。

首次拉取后请在前端目录执行一次依赖安装：

```bash
cd star-pivot-ui
pnpm install
```

可手动校验最近一次提交信息：

```bash
cd star-pivot-ui
pnpm commitlint
```

说明：`commitlint` 配置文件位于 `star-pivot-ui/commitlint.config.cjs`。

## 四、代码规范与提交范围

- 仅提交与当前需求相关的文件，不捎带无关改动。
- 避免提交编译产物与日志（如 `target/`、`logs/`）。
- Java 代码遵循现有分层与命名约定；Vue/TS 代码遵循项目已有风格。
- 新增配置项时，补充默认值说明和使用文档。

## 五、提交流程（建议）

1. 从最新主分支创建功能分支。
2. 完成开发并自测（接口、页面、权限、回归）。
3. 按规范提交 Commit（可多个小提交）。
4. 推送分支并发起 PR（Gitee/GitHub 均适用）。
5. 按评审意见继续迭代，直到合并。

## 六、Pull Request 规范

PR 标题建议格式：

- `feat: xxx`
- `fix: xxx`
- `refactor: xxx`

PR 描述至少包含：

- 背景与目标（为什么要改）
- 主要改动点（改了什么）
- 影响范围（模块、接口、数据库、配置）
- 测试说明（如何验证）
- 风险与回滚方案（如有）

## 七、合并要求（建议）

- 至少 1 名维护者通过评审（按项目实际执行）。
- 关键问题全部解决，无阻塞评论。
- CI 或本地构建校验通过（如已配置）。
- 不接受包含敏感信息或无关文件的 PR。

## 八、Issue 规范（可选但推荐）

提 Issue 时请尽量提供：

- 复现步骤
- 期望结果与实际结果
- 相关日志/截图
- 运行环境（JDK、Node、数据库、操作系统）

---

如果你是首次参与贡献，建议先从 `docs`、`fix` 类问题开始，方便熟悉项目结构与提交流程。
