# CI/CD 集成配置

本目录包含 StarPivot 项目的 CI/CD 集成配置文件。

## 文件说明

### GitHub Actions
- `.github/workflows/ci-cd.yml` - GitHub Actions CI/CD 工作流配置

### SonarQube
- `sonar-project.properties` - 后端 SonarQube 配置
- `star-pivot-ui/sonar-project.properties` - 前端 SonarQube 配置

### Git Hooks
- `.husky/pre-commit` - Pre-commit hook 配置
- `.husky/pre-push` - Pre-push hook 配置
- `install-husky.sh` - Husky 安装脚本

### 文档
- `doc/CI-CD集成指南.md` - CI/CD 集成完整指南
- `doc/代码审查流程规范.md` - 代码审查流程规范
- `doc/代码质量门禁配置.md` - 代码质量门禁配置

## 快速开始

### 1. 配置 GitHub Secrets

在 GitHub 仓库设置中添加以下 Secrets：

- `SONAR_TOKEN` - SonarQube 认证令牌
- `SONAR_HOST_URL` - SonarQube 服务器地址
- `SNYK_TOKEN` - Snyk 安全扫描令牌（可选）

### 2. 安装 Git Hooks

```bash
bash install-husky.sh
```

### 3. 配置分支保护

在 GitHub 仓库设置中配置分支保护规则，要求：
- 所有 CI/CD 检查通过
- 至少 1 名审查者批准

## 使用说明

### 提交代码

```bash
# Pre-commit hook 会自动运行以下检查：
# - 后端：mvn spotless:check
# - 前端：npm run lint, npm run lint:prettier, npm run lint:stylelint

git add .
git commit -m "feat: add new feature"
```

### 推送代码

```bash
# Pre-push hook 会自动运行以下检查：
# - 提交信息格式检查
# - 后端测试：mvn test
# - 前端测试：npm run test
# - 覆盖率检查

git push origin feature/xxx
```

### 查看 CI/CD 状态

1. 进入 GitHub 仓库
2. 点击 **Actions** 标签
3. 查看工作流运行状态

### 查看 SonarQube 报告

1. 访问 SonarQube 服务器
2. 选择 StarPivot 项目
3. 查看质量门禁状态和代码质量报告

## 质量标准

### 后端
- 代码覆盖率 ≥ 50%
- 新增代码覆盖率 ≥ 70%
- 重复率 ≤ 3%
- 圈复杂度 ≤ 10

### 前端
- 代码覆盖率 ≥ 50%
- 新增代码覆盖率 ≥ 70%
- ESLint 错误 = 0
- TypeScript 错误 = 0

## 详细文档

- [CI/CD 集成指南](./doc/CI-CD集成指南.md) - 完整的 CI/CD 集成指南
- [代码审查流程规范](./doc/代码审查流程规范.md) - 代码审查流程和规范
- [代码质量门禁配置](./doc/代码质量门禁配置.md) - 质量门禁配置和标准

## 故障排查

### CI/CD 失败

查看 GitHub Actions 日志，确定失败原因并修复。

### SonarQube 扫描失败

1. 检查 `SONAR_TOKEN` 和 `SONAR_HOST_URL` 配置
2. 查看 SonarQube 扫描日志
3. 修复相关问题后重新扫描

### Git Hooks 失败

```bash
# 手动运行检查
mvn spotless:check -pl star-pivot-controller -am
cd star-pivot-ui && npm run lint

# 修复后重新提交
git add .
git commit -m "fix: resolve code quality issues"
```

## 支持

如有问题，请参考详细文档或联系技术负责人。
