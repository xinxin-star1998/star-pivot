# com.star.pivot.framework 包与 Framework 子模块约定

## 一、现状说明

`com.star.pivot.framework` 并非单一 JAR 的包名，而是由 **多个 framework 子模块** 共同使用的**逻辑包前缀**：多个 artifact 中都有该包下的类，在 classpath 上合并后对外表现为统一的 `com.star.pivot.framework` 命名空间。

| 子模块 (artifact) | 提供的 common 子包 / 类 | 说明 |
|-------------------|-------------------------|------|
| **star-pivot-framework-core** | `common.domain`、`common.constants`、`common.exception`、`common.sql`、`common.annotation`（NoResponseWrapper）、`common.utils`（SecurityUtils, DateUtils, StringUtils, ServiceUtils, XssUtils, DateDeserializer） | 底座，拥有根包及核心 domain/异常/工具 |
| **star-pivot-framework-log** | `common.annotation.Log`、`common.utils.LogUtils` | 操作日志 |
| **star-pivot-framework-file** | `common.utils.OssUtil`、`MinioUtil`、`MinioProperties` | 文件存储（OSS/MinIO） |
| **star-pivot-framework-web** | `common.security.LoginUserInfo` | Web 层登录用户信息 |

因此会出现：**同一包名**（如 `com.star.pivot.framework.utils`）在不同 JAR 中都有类（core 的 StringUtils、log 的 LogUtils、file 的 OssUtil）。这是有意设计：通过依赖 framework-core / security（传递 log、file、web）在应用层统一使用 `com.star.pivot.framework.*`，无需关心类来自哪个 JAR。

---

## 二、约定与规范

### 1. 包归属约定

- **framework-core**：独占并维护以下子包，其他子模块不要在此放类：
  - `com.star.pivot.framework.domain`
  - `com.star.pivot.framework.constants`
  - `com.star.pivot.framework.exception`
  - `com.star.pivot.framework.sql`
- **framework-core** 的 `common.annotation`、`common.utils` 以 core 自身需求为主；**log / file** 在 `common.utils` 或 `common.annotation` 下新增类时，注意**不要与 core 或其他子模块同名类冲突**（当前通过不同类名区分：LogUtils、OssUtil、MinioUtil 等）。
- **framework-web**：仅使用 `com.star.pivot.framework.security` 子包（如 LoginUserInfo），与认证、登录用户信息相关。

### 2. 新增类时的建议

- **在 framework-core 中新增**：放在 `common` 下已有子包（domain/constants/exception/utils/annotation/sql）中，或与团队确认新子包名。
- **在 framework-log / file / web 中新增**：
  - 优先放在已有、语义明确的子包（如 `common.utils`、`common.security`），并保持**类名唯一、见名知意**，避免与 core 或其他子模块同名。
  - 若新能力较多、希望与 core 的 utils 区分，可考虑使用**子模块专属子包**（如 `common.file`、`common.log`），并在本约定中补充说明。当前未强制迁移，以“不重名、不冲突”为准。

### 3. 避免认知混淆

- **不要**认为存在一个名为 “star-pivot-common” 的独立模块或 JAR；通用能力由 **star-pivot-framework-core** 及 log/file/web 共同提供，应用层通过依赖 **star-pivot-framework-boot-starter**（或 core + security）获得完整 common 能力。
- 若在 IDE 中看到同一包下类来自多个 JAR，属正常现象；引用时仍使用 `com.star.pivot.framework.*` 即可。

### 4. 长期可选优化

若后续希望**从包结构上**更清晰区分来源，可考虑（非必须）：

- log 专属类迁至 `com.star.pivot.framework.log`（如 LogUtils、Log）。
- file 专属类迁至 `com.star.pivot.framework.file`（如 OssUtil、MinioUtil、MinioProperties）。
- web 已使用 `common.security`，可保持不动。

迁移会涉及包名与 import 的批量修改，可在新功能或重构时逐步进行；当前以“约定 + 不重名”为主。

---

## 三、小结

| 项目 | 约定 |
|------|------|
| **common 包来源** | 多个 framework 子模块（core / log / file / web）共同提供，无单独 common 模块 |
| **core 职责** | 独占 domain、constants、exception、sql；与各模块共享 annotation、utils 时注意类名不冲突 |
| **log / file / web** | 在 common 下已有子包中扩展，类名唯一；可选长期迁至 common.log、common.file 等 |
| **应用层使用** | 依赖 star-pivot-framework-boot-starter 或 core + security，直接使用 `com.star.pivot.framework.*` |

以上约定用于避免包名冲突与认知混淆，新代码请按此规范放置与引用。
