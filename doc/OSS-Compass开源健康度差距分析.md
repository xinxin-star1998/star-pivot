# StarPivot OSS-Compass 开源健康度差距分析

> 评估平台：[OSS Compass](https://compass.gitee.com/analyze/sn4fke5b)  
> 仓库：[Gitee xin1998/StarPivot](https://gitee.com/xin1998/StarPivot) · [GitHub star-pivot](https://github.com/xinxin-star1998/star-pivot)  
> 数据快照：最近 6 个月（以 OSS-Compass 页面为准，指标会随社区活动动态变化）

## 一、评估体系概览

OSS-Compass 基于 CHAOSS 指标体系，从 **协作、贡献者、软件** 三个维度，结合 **生产力、稳健性、创新力** 三类品质，对开源项目进行生态健康评估。Gitee 已逐步用该体系替换原「Gitee 指数」。

```
开源生态
├── 协作（Collaboration）     → 生产力 / 稳健性 / 创新力
├── 贡献者（Contributors）
└── 软件（Software）
```

## 二、当前得分快照

| 评估模型 | 得分 | 品质维度 | 状态 |
|----------|------|----------|------|
| Collab. Dev. Index（协作开发指数） | **68** | 生产力 | 中等偏上 |
| Community Support（社区支持） | **62** | 稳健性 | 中等 |
| Activity（活跃度） | **60** | 创新力 | 中等 |
| Organizations Activity（组织活跃度） | **--** | — | 暂无数据 |

**综合判断**：项目软件工程基础较好（文档、CI、架构），但社区协作与外部贡献活跃度仍有明显提升空间。

## 三、分维度差距分析

### 3.1 协作（Collaboration）— Collab. Dev. Index 68

**已具备**

| 项 | 说明 |
|----|------|
| CONTRIBUTING.md | 分支、Commit、PR 规范完整 |
| PR 模板 | `.github/PULL_REQUEST_TEMPLATE.md` |
| Issue 模板 | Bug / Feature 结构化模板（本次补齐） |
| 双平台托管 | GitHub + Gitee 同步 |
| Conventional Commits | 前端 commitlint 校验 |

**差距与建议**

| 差距 | 影响 | 优先级 | 行动项 |
|------|------|--------|--------|
| 外部 PR 数量少 | 协作指数难提升 | 高 | 在 README / 文档标注「Good First Issue」标签，降低贡献门槛 |
| Issue 响应周期未制度化 | 社区支持感知弱 | 中 | 维护者承诺 3 个工作日内首次回复（写入 CONTRIBUTING） |
| 缺少 Issue/PR 标签体系 | 贡献者难以筛选任务 | 中 | 使用 `bug` / `enhancement` / `good first issue` / `help wanted` 标签 |
| Gitee 与 GitHub Issue 未打通 | 协作数据分散 | 低 | 指定主平台（建议 GitHub），Gitee 做镜像并注明 |

**目标**：Collab. Dev. Index → **75+**（6 个月内）

---

### 3.2 贡献者（Contributors）— Community Support 62

**已具备**

| 项 | 说明 |
|----|------|
| 开源许可证 | MIT |
| 行为准则 | CODE_OF_CONDUCT.md（本次补齐） |
| 贡献流程文档 | CONTRIBUTING.md |
| 在线演示 | starpivot.org.cn 降低试用成本 |

**差距与建议**

| 差距 | 影响 | 优先级 | 行动项 |
|------|------|--------|--------|
| 贡献者以维护者为主 | 贡献者多样性低 | 高 | 发布「贡献者指南」短视频或 doc/ 图文，降低上手成本 |
| 缺少 CODEOWNERS | 评审责任不清晰 | 中 | 添加 `.github/CODEOWNERS` 指定模块负责人 |
| 无公开 Roadmap | 外部贡献方向不明 | 中 | 在 README 或 doc/ 增加版本规划（v1.1 / v1.2） |
| 社区渠道单一 | 社区支持指数受限 | 低 | 考虑 Discussions / 交流群（按需） |

**目标**：Community Support → **70+**

---

### 3.3 软件（Software）— Activity 60

**已具备**

| 项 | 说明 |
|----|------|
| README | 结构完整，含快速开始、架构、文档索引 |
| doc/ 文档体系 | 架构、部署、权限、业务模块文档 |
| CI/CD | 后端 / 前端 / Docker / SonarQube 四套 workflow |
| API 文档 | Knife4j / OpenAPI |
| 版本标签 | v1.0.0 |
| 安全策略 | SECURITY.md（本次补齐） |
| 更新日志 | CHANGELOG.md（本次补齐） |

**差距与建议**

| 差距 | 影响 | 优先级 | 行动项 |
|------|------|--------|--------|
| Release 与 tag 不同步 | 生命周期指标偏弱 | 高 | 每次发版在 GitHub/Gitee 创建 Release，关联 CHANGELOG |
| 单元测试覆盖率未公开 | 稳健性背书不足 | 中 | CI 已生成 JaCoCo，可在 README 展示覆盖率徽章 |
| 依赖安全扫描未文档化 | 供应链透明度 | 中 | 启用 Dependabot 或定期 `mvn dependency-check` |
| 页面预览图为占位 | 软件呈现不完整 | 低 | 补充 doc/images/ 实际截图 |
| v1.0.0 后无新 tag | 活跃度信号弱 | 高 | 积累改动后发布 v1.1.0 |

**目标**：Activity → **70+**

---

### 3.4 组织活跃度 — Organizations Activity（暂无）

该指标通常与 **企业 / 组织账号下的协作活动** 相关。个人仓库常见为 `--`。

**可选行动**

- 若未来有企业或团队组织账号，将仓库迁移或建立 mirror 组织
- 在 OSS-Compass 提交「社区分析」而非仅单仓库分析（多仓库联动时更有数据）

## 四、仓库治理清单（本次补齐情况）

| 文件 | 状态 | OSS-Compass 关联维度 |
|------|------|----------------------|
| README.md | ✅ 已有，已加 OSS-Compass 徽章与说明 | 软件 |
| LICENSE | ✅ MIT | 软件 / 合规 |
| CONTRIBUTING.md | ✅ 已有 | 协作 |
| CODE_OF_CONDUCT.md | ✅ **新增** | 贡献者 / 协作 |
| SECURITY.md | ✅ **新增** | 软件 / 稳健性 |
| CHANGELOG.md | ✅ **新增** | 软件 / 生命周期 |
| Issue 模板 | ✅ **新增** | 协作 |
| PR 模板 | ✅ 已有 | 协作 |
| CI/CD | ✅ 已有 | 软件 / 稳健性 |
| OSS-Compass Badge | ✅ 已有 | 可见性 |

## 五、OSS-Compass 提交与维护

### 5.1 当前状态：✅ 已纳入评估

StarPivot **已完成** OSS-Compass 单仓库分析，无需重复提交：

- 分析报告：[https://compass.gitee.com/analyze/sn4fke5b](https://compass.gitee.com/analyze/sn4fke5b)
- 徽章 ID：`sn4fke5b`
- Gitee 仓库页可查看各模型得分并跳转报告

### 5.2 日常维护

1. **保持仓库活跃**：定期 commit、Issue 互动、Release 发布
2. **双平台同步**：GitHub 与 Gitee 保持 tag / Release 一致，便于数据采集
3. **刷新报告**：OSS-Compass 按周期更新数据，重大版本发布后约 1–2 周查看指标变化
4. **更新徽章**：若 OSS-Compass 重新生成项目 ID，在 README 中替换 badge URL

### 5.3 新仓库或 fork 如何提交（参考）

若将来有新仓库需纳入评估：

1. 访问 [https://compass.gitee.com](https://compass.gitee.com)
2. 点击右上角 **提交项目**
3. 使用 Gitee / GitHub 授权登录
4. 选择 **单仓库分析**，填入仓库 URL
5. 在 [compass-projects-information](https://gitee.com/oss-compass/compass-projects-information) 跟踪 PR 进度
6. 报告生成后，在报告页 **Compass 徽章** 弹窗复制 Markdown 嵌入 README

## 六、90 天改进路线图

| 阶段 | 时间 | 行动 | 预期影响 |
|------|------|------|----------|
| P0 | 立即 | 发布 v1.1.0 Release + CHANGELOG | Activity ↑ |
| P0 | 立即 | 推送治理文件至 GitHub / Gitee | Community Support ↑ |
| P1 | 2 周内 | 标注 3–5 个 Good First Issue | Collab. Dev. Index ↑ |
| P1 | 1 月内 | README 覆盖率徽章、补充预览截图 | Activity / 稳健性 ↑ |
| P2 | 3 月内 | 外部贡献者 PR ≥ 2、Issue 定期 triage | 全维度 ↑ |

## 七、参考链接

- [OSS Compass 官网](https://compass.gitee.com)
- [StarPivot 分析报告](https://compass.gitee.com/analyze/sn4fke5b)
- [OSS Compass 快速入门](https://compass.gitee.com/docs/quick-start/)
- [CHAOSS 社区](https://chaoss.community/)
- [Gitee 指数替换说明](https://compass.gitee.com/zh/blog/2024/01/05/compass-collaboration-update/)

---

*本文档随项目治理进展更新，建议在每次发版后回顾并修订「当前得分快照」一节。*
