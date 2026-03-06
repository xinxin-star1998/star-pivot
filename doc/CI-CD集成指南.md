# CI/CD 集成指南

本文档介绍 StarPivot 项目的 CI/CD 集成配置，包括代码质量检查、SonarQube 配置和代码审查流程。

## 目录

- [快速开始](#快速开始)
- [GitHub Actions](#github-actions)
- [SonarQube](#sonarqube)
- [代码审查流程](#代码审查流程)
- [质量门禁](#质量门禁)
- [Git Hooks](#git-hooks)
- [故障排查](#故障排查)

## 快速开始

### 1. 配置 GitHub Secrets

在 GitHub 仓库中配置以下 Secrets：

| Secret 名称 | 说明 | 示例 |
|------------|------|------|
| `SONAR_TOKEN` | SonarQube 认证令牌 | 从 SonarQube 服务器获取 |
| `SONAR_HOST_URL` | SonarQube 服务器地址 | `https://sonarqube.example.com` |
| `SNYK_TOKEN` | Snyk 安全扫描令牌 | 从 Snyk 获取 |

### 2. 配置分支保护

在 GitHub 仓库设置中：

1. 进入 **Settings** → **Branches**
2. 点击 **Add branch protection rule**
3. 配置以下规则：
   - Branch name pattern: `main`, `master`, `develop`
   - Require status checks to pass before merging: ✅
   - Require branches to be up to date before merging: ✅
   - Required status checks:
     - Backend Code Quality
     - Frontend Code Quality
     - Security Scan
     - Quality Gate
   - Require pull request reviews before merging: ✅
   - Require approval of 1 reviewer: ✅
   - Dismiss stale PR approvals when new commits are pushed: ✅

### 3. 安装 Git Hooks

```bash
# 运行安装脚本
bash install-husky.sh

# 或手动安装
npm install -D husky
npx husky install
```

## GitHub Actions

### 工作流程

StarPivot 使用 GitHub Actions 实现自动化 CI/CD 流程，包括：

1. **Backend Code Quality**: 后端代码质量检查
2. **Frontend Code Quality**: 前端代码质量检查
3. **Security Scan**: 安全漏洞扫描
4. **Quality Gate**: 质量门禁检查
5. **Build and Package**: 构建和打包

### 触发条件

CI/CD 流程在以下情况下触发：

- Push 到 `main`, `master`, `develop` 分支
- 创建或更新 Pull Request 到上述分支

### 查看工作流

1. 进入 GitHub 仓库
2. 点击 **Actions** 标签
3. 查看工作流运行状态和日志

### 本地测试工作流

```bash
# 测试后端质量检查
mvn clean compile spotless:check -pl star-pivot-controller -am
mvn test -pl star-pivot-module -am

# 测试前端质量检查
cd star-pivot-ui
npm run lint
npm run lint:prettier
npm run lint:stylelint
npx vue-tsc --noEmit
npm run build
```

## SonarQube

### 配置 SonarQube

#### 1. 安装 SonarQube 服务器

使用 Docker 快速启动：

```bash
docker run -d \
  --name sonarqube \
  -p 9000:9000 \
  -e SONAR_JDBC_URL="jdbc:postgresql://db:5432/sonar" \
  -e SONAR_JDBC_USERNAME=sonar \
  -e SONAR_JDBC_PASSWORD=sonar \
  sonarqube:lts-community
```

#### 2. 创建项目

1. 访问 `http://localhost:9000`
2. 登录（默认用户名/密码：admin/admin）
3. 点击 **Create Project**
4. 项目密钥：`StarPivot`
5. 显示名称：`StarPivot`

#### 3. 获取认证令牌

1. 进入 **My Account** → **Security**
2. 点击 **Generate Token**
3. 输入令牌名称，如 `github-actions`
4. 复制生成的令牌

#### 4. 配置 GitHub Secrets

将获取的令牌配置到 GitHub Secrets：
- `SONAR_TOKEN`: 刚才生成的令牌
- `SONAR_HOST_URL`: SonarQube 服务器地址

### 运行 SonarQube 扫描

#### 后端扫描

```bash
mvn clean sonar:sonar \
  -Dsonar.projectKey=StarPivot \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=YOUR_TOKEN
```

#### 前端扫描

```bash
cd star-pivot-ui
npm install -g sonar-scanner
sonar-scanner \
  -Dsonar.projectKey=StarPivot-frontend \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=YOUR_TOKEN
```

### 查看分析结果

1. 访问 SonarQube 服务器
2. 选择 StarPivot 项目
3. 查看以下信息：
   - 质量门禁状态
   - 代码覆盖率
   - 问题列表
   - 技术债务
   - 安全漏洞

### 质量门禁配置

StarPivot 项目已配置以下质量门禁：

- 代码覆盖率 ≥ 50%
- 新增代码覆盖率 ≥ 70%
- 重复率 ≤ 3%
- 可靠性评级 A
- 安全性评级 A
- 可维护性评级 A
- 无阻断性问题

## 代码审查流程

### 创建 Pull Request

1. 从 `develop` 分支创建功能分支
2. 完成开发和测试
3. 推送到远程仓库
4. 创建 Pull Request 到 `develop` 分支

### PR 描述模板

使用以下模板创建 PR：

```markdown
## 变更说明
简要描述本次变更的内容和目的

## 变更类型
- [ ] 新功能 (feature)
- [ ] Bug 修复 (bugfix)
- [ ] 代码重构 (refactor)
- [ ] 文档更新 (documentation)
- [ ] 性能优化 (performance)
- [ ] 测试相关 (testing)

## 影响范围
- [ ] 后端 (star-pivot-controller)
- [ ] 前端 (star-pivot-ui)
- [ ] 框架层 (star-pivot-framework)
- [ ] 业务层 (star-pivot-module)

## 测试情况
- [ ] 单元测试已通过
- [ ] 集成测试已通过
- [ ] 手动测试已完成
- [ ] 测试覆盖率达标

## 检查清单
- [ ] 代码符合项目规范
- [ ] 已添加必要的注释
- [ ] 已更新相关文档
- [ ] 无安全漏洞
- [ ] 无性能问题

## 相关 Issue
Closes #(issue number)

## 截图/演示
(如有 UI 变更，请提供截图或演示视频)
```

### 审查流程

1. **自动检查**：CI/CD 自动运行所有检查
2. **代码审查**：至少 1 名审查者审查代码
3. **修改反馈**：根据反馈修改代码
4. **批准合并**：所有检查通过后合并

### 审查标准

- 代码覆盖率 ≥ 50%
- 所有 CI/CD 检查通过
- 至少 1 名审查者批准
- 无阻断性问题
- SonarQube 质量门禁通过

详细流程请参考：[代码审查流程规范](./doc/代码审查流程规范.md)

## 质量门禁

### 后端质量标准

| 指标 | 标准 |
|------|------|
| 代码覆盖率 | ≥ 50% |
| 新增代码覆盖率 | ≥ 70% |
| 重复率 | ≤ 3% |
| 圈复杂度 | ≤ 10 |
| 可靠性评级 | A |
| 安全性评级 | A |
| 可维护性评级 | A |

### 前端质量标准

| 指标 | 标准 |
|------|------|
| 代码覆盖率 | ≥ 50% |
| 新增代码覆盖率 | ≥ 70% |
| ESLint 错误 | 0 |
| TypeScript 错误 | 0 |
| 组件复杂度 | ≤ 10 |

详细配置请参考：[代码质量门禁配置](./doc/代码质量门禁配置.md)

## Git Hooks

### Pre-commit Hooks

在提交代码前自动运行：

- Spotless 代码格式检查（后端）
- ESLint 代码检查（前端）
- Prettier 格式检查（前端）
- Stylelint 样式检查（前端）

### Pre-push Hooks

在推送代码前自动运行：

- 提交信息格式检查
- 后端单元测试
- 前端单元测试
- 代码覆盖率检查

### 禁用 Hooks

临时禁用 hooks：

```bash
# 禁用 pre-commit
git commit --no-verify

# 禁用 pre-push
git push --no-verify
```

### 配置 Hooks

```bash
# 安装 Husky
bash install-husky.sh

# 或手动安装
npm install -D husky
npx husky install
```

## 故障排查

### CI/CD 失败

#### 1. 后端编译失败

```bash
# 本地重现
mvn clean compile -pl star-pivot-controller -am

# 检查依赖
mvn dependency:tree -pl star-pivot-controller

# 清理并重新构建
mvn clean install -U
```

#### 2. 前端构建失败

```bash
# 本地重现
cd star-pivot-ui
npm run build

# 清理缓存
rm -rf node_modules package-lock.json
npm install
npm run build
```

#### 3. 测试失败

```bash
# 后端测试
mvn test -pl star-pivot-module -am

# 前端测试
cd star-pivot-ui
npm run test
```

### SonarQube 扫描失败

#### 1. 连接失败

检查 `SONAR_HOST_URL` 和 `SONAR_TOKEN` 是否正确配置。

#### 2. 质量门禁失败

1. 查看 SonarQube 报告
2. 修复相关问题
3. 重新运行扫描

### Git Hooks 失败

#### 1. Pre-commit 失败

```bash
# 查看具体错误
git commit

# 手动运行检查
mvn spotless:check -pl star-pivot-controller -am
cd star-pivot-ui && npm run lint
```

#### 2. Pre-push 失败

```bash
# 查看具体错误
git push

# 手动运行检查
mvn test -pl star-pivot-module -am
cd star-pivot-ui && npm run test
```

## 最佳实践

### 1. 提交前检查

```bash
# 运行所有检查
mvn clean compile spotless:check test -pl star-pivot-controller -am
cd star-pivot-ui && npm run lint && npm run test
```

### 2. 小步提交

- 保持 PR 规模适中（建议 < 500 行）
- 每个 PR 只解决一个问题
- 及时响应审查意见

### 3. 持续改进

- 定期查看 SonarQube 报告
- 修复代码质量问题
- 提升测试覆盖率

### 4. 团队协作

- 及时审查同事的 PR
- 提供建设性的反馈
- 分享最佳实践

## 相关文档

- [代码审查流程规范](./doc/代码审查流程规范.md)
- [代码质量门禁配置](./doc/代码质量门禁配置.md)
- [项目 README](./README.md)

## 支持

如有问题，请联系：

- 技术负责人：[email]
- DevOps 团队：[email]
- GitHub Issues：[项目地址]

---

**文档版本**: v1.0  
**最后更新**: 2026-03-06  
**维护者**: StarPivot 团队
