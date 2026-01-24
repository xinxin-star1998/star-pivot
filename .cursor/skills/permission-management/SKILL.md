---
name: permission-management
description: 统一指导在 StarPivot 项目中处理菜单权限、路由权限、按钮/接口权限以及数据权限相关需求的技能。当前端或后端涉及权限设计、调整、排查或联调时使用。
---

# StarPivot 权限管理 Skill

> 本 Skill 专门用于 StarPivot 项目内的权限相关需求，所有说明与约定均以本仓库现有实现为基准，优先保持一致，不做大规模拍脑袋重构。

## 使用场景（何时自动应用）

在以下任一场景下，优先启用本 Skill：

- 用户提到：**权限、菜单、路由、角色、按钮权限、接口鉴权、数据权限、数据范围、DataScope、SysMenu、RouterController、SysUser** 等关键词。
- 需要：**新增/修改 菜单或路由，并带权限控制**。
- 需要：**为后端接口增加/调整权限校验或数据范围限制**。
- 需要：**排查某个菜单/接口“看不到/403/无权限” 类问题**。
- 需要：**前后端联调权限相关逻辑**。

若同时有其他通用 Skill（如代码 review、API 设计）也匹配，请在执行高层检查后，具体到权限逻辑时优先遵循本 Skill。

---

## 总体原则

1. **保持现有风格与设计**  
   - 不擅自引入全新权限框架或大改数据结构。  
   - 优先阅读当前相关模块（如 `SysMenuController`、`RouterController`、`SysUserServiceImpl`、前端 `menu` 相关视图与路由）。

2. **前后端契约优先**  
   - 修改权限点、菜单结构或接口返回字段时，**必须同步考虑前端调用/显示逻辑**。  
   - 尽量给出前后端同时修改的建议与检查点。

3. **中文注释解释“为什么”**  
   - 关键权限判断、数据范围拼接、复杂 SQL 条件，必须补充简短中文注释，说明设计原因与注意事项。

4. **最小可用改动**  
   - 在不影响整体架构的前提下，优先做**局部、小范围、可回滚的**变更。

---

## 权限相关模块快速索引（基于当前项目）

> 注意：路径可能随时间演进，如发现不一致，应优先阅读最近修改记录与同类实现。

- 后端控制层（与菜单/路由/权限相关）
  - `star-pivot-controller/src/main/java/com/star/pivot/controller/RouterController.java`
  - `star-pivot-controller/src/main/java/com/star/pivot/controller/SysMenuController.java`
- 后端用户与数据权限
  - `star-pivot-system/src/main/java/com/star/pivot/system/mapper/SysUserMapper.java`
  - `star-pivot-system/src/main/java/com/star/pivot/system/service/impl/SysUserServiceImpl.java`
  - `star-pivot-system/src/main/java/com/star/pivot/system/utils/DataScopeService.java`
  - `star-pivot-common/src/main/java/com/star/pivot/common/domain/DataScope.java`
  - `star-pivot-system/src/main/resources/mapper/SysUserMapper.xml`
- 前端菜单与路由
  - `star-pivot-ui/src/api/menu/menu.ts`
  - `star-pivot-ui/src/router/core/DynamicRouteAppender.ts`
  - `star-pivot-ui/src/views/system/menu/index.vue`
  - `star-pivot-ui/src/views/system/menu/modules/menu-dialog.vue`
  - `star-pivot-ui/src/components/core/base/art-icon-picker/index.vue`（菜单图标选择相关）

在处理权限需求前，应优先打开上述对应文件，查看现有实现与命名约定。

---

## 常见任务与操作步骤

### 1. 新增一个带权限控制的菜单/路由

1. **后端菜单数据结构与接口**
   - 在 `SysMenuController`、`SysMenu` 相关实体与 Mapper 中，确认是否需要新增字段（如权限标识、组件路径、可见性等）。  
   - 保持与现有菜单数据结构一致，如已有 `perms`、`component`、`visible` 等字段，优先复用这些约定。
   - 如需新增字段，添加时写明中文注释，说明字段含义与使用场景。

2. **前端菜单表单与展示**
   - 在 `menu-dialog.vue` 中为新字段增加表单项，注意：  
     - label、placeholder、校验规则统一使用中文说明。  
     - 表单值类型与后端接收类型必须一致（如字符串/数字/布尔）。
   - 在 `menu/index.vue` 中调整列表展示列，确保能看到关键权限信息（如权限编码、是否外链、是否缓存等，如适用）。

3. **前端路由与动态菜单加载**
   - 在 `DynamicRouteAppender.ts` 中，查看后端菜单数据是如何转换为前端路由对象的。  
   - 若新增了影响路由行为的字段（如 `hidden`、`redirect`、`meta.roles` 等），在该文件中补充转换逻辑，并保持与已有字段风格一致。

4. **权限点（按钮/接口）绑定**
   - 若菜单下还包含按钮级权限（如“新增”“编辑”“删除”按钮）：  
     - 确认后端是否有对应的权限标识/注解。  
     - 前端通过指令/判断（如 `v-hasPerm`、`usePermission` 等模式，如项目中存在）控制按钮显隐。  
   - 修改或新增时，使用中文注释说明该权限点的具体业务含义。

5. **验收检查**
   - 新增菜单在菜单管理页面可正确创建/编辑/删除。  
   - 绑定角色后，登录相应角色账号能看到/看不到对应菜单。  
   - 若配置了按钮权限，验证有权限与无权限账号的按钮显隐差异。

### 2. 为后端接口增加权限/角色校验

1. **定位 Controller 方法**
   - 打开对应 `*Controller` 类，找到需要增加权限控制的方法。

2. **参考现有权限注解或校验方式**
   - 若项目已有统一的权限注解/拦截器（如 `@PreAuthorize`、自定义 `@Permission` 等），应**复用同样方式与命名风格**。  
   - 权限编码命名应：  
     - 体现模块：如 `system:menu:add`、`system:menu:edit`。  
     - 与菜单或按钮上的权限编码保持一致。

3. **增加中文注释**
   - 在方法上方加入简短中文注释，说明：  
     - 该接口的业务用途。  
     - 该权限点的目标角色/使用场景（如“仅系统管理员可用”“仅所在部门管理员可用”）。

4. **前端调用联动**
   - 确认前端对应 API 封装（如 `menu.ts` 或其他模块 API 文件）是否需要：  
     - 新增接口方法。  
     - 调整入参与返回类型。  
   - 前端若需要根据权限控制按钮显隐，应使用与后端同一权限编码。

### 3. 数据权限 / 数据范围（DataScope）

1. **先理解现有 DataScope 设计**
   - 打开 `DataScope.java`、`DataScopeService.java`、`SysUserMapper.xml`。  
   - 理解当前项目将数据范围与用户、角色、组织的关联方式（如本部门、本部门及下级、仅本人、全部数据等）。

2. **新增或调整数据范围类型时**
   - 优先在 `DataScope` 中新增枚举/常量，**使用有业务含义的命名**，并写明中文注释。  
   - 在 `DataScopeService` 中增加新的数据范围拼接逻辑，注意：  
     - 保持 SQL 片段风格与已有实现一致。  
     - 对复杂 JOIN 或子查询添加中文注释，说明为什么要这么拼接。

3. **改动 Mapper XML**
   - 在 `SysUserMapper.xml` 或其他受影响的 Mapper 中，  
     - 将数据范围条件插入到统一位置（通常在 WHERE 条件中统一追加）。  
     - 避免写死组织 ID，统一从 DataScope 参数中取值。

4. **联调与回归**
   - 至少验证以下场景：  
     - 全量数据角色是否能看到全部。  
     - 限制到某部门/本人数据时，是否符合预期过滤。  
     - 边界条件，如无组织、逻辑删除的数据不应意外暴露。

### 4. 排查“无权限/菜单不显示”问题

排查顺序建议：

1. **确认菜单/权限配置**
   - 在菜单管理中检查该菜单是否：  
     - 被标记为隐藏/禁用。  
     - 未分配到当前角色。  
     - 权限编码是否与前端/后端一致。

2. **确认角色绑定**
   - 检查用户所挂角色，角色是否已绑定对应菜单和按钮权限。

3. **前端路由加载**
   - 查看 `DynamicRouteAppender.ts` 以及前端获取菜单数据的 API：  
     - 是否过滤掉了某些类型的菜单（例如外链、按钮类型）。  
     - `meta` 或路由字段是否错配，导致前端忽略该菜单。

4. **后端数据过滤/数据权限**
   - 检查相应接口是否在查询时应用了 DataScope，  
     - 该用户的 DataScope 是否导致数据被完全过滤。  
     - 是否出现 SQL 条件冲突。

5. **日志与报错信息**
   - 若接口返回 403 或业务错误码，结合项目统一异常/权限处理逻辑查看日志，  
     - 分析是“未登录”“权限不足”还是“数据权限过滤为空”等原因。

---

## 输出与沟通约定

1. **语言与注释**
   - 所有解释、实现方案、注释与文档，统一使用中文，必要时中英文混排专有名词。  
   - 对关键权限逻辑，必须说明“为什么这样设计”，而不仅是“做了什么”。

2. **回答结构建议**
   - 简要概括需求与整体思路。  
   - 列出前后端分别需要改动的点（若同时涉及）。  
   - 用步骤列出具体修改位置和注意事项。  
   - 如存在多种可选方案，先做简短对比，再推荐一个更贴近现有项目风格的方案。

3. **避免的行为**
   - 不在用户未要求时，擅自重构整个权限体系或菜单结构。  
   - 不随意引入新权限框架/库。  
   - 不用与现有实现冲突的命名方式或权限编码风格。

---

## 简短示例

### 示例 1：为菜单新增“仅管理员可见”的权限点

回答时可采用类似结构：

- **整体思路**：  
  - 后端新增一个权限编码 `system:menu:adminOnly`，在 `SysMenuController` 对应接口上增加权限注解。  
  - 前端在菜单管理页面为该菜单配置该权限编码，并通过角色管理将该权限只分配给管理员角色。

- **后端改动**：  
  - 在 `SysMenuController` 的某接口上增加权限注解，并写中文注释说明仅管理员可用。  
  - 如有统一权限常量类，可在其中新增常量。

- **前端改动**：  
  - 在 `menu-dialog.vue` 中增加一个“仅管理员可见”开关，对应后端权限编码。  
  - 在角色分配页面，将该权限只勾选给管理员角色。

### 示例 2：为查询接口增加“仅本人数据”数据范围

- **整体思路**：  
  - 在 `DataScope` 中新增表示“仅本人”的数据范围类型。  
  - 在 `DataScopeService` 中根据该类型拼接 `AND user_id = 当前用户ID` 的 SQL 条件。  
  - 在对应 Service 查询方法中注入/使用该 DataScope。

- **实现要点**：  
  - SQL 条件注释中说明：该数据范围用于限制到当前登录用户本人数据。  
  - 如有缓存/分页场景，注意数据范围变化后的查询是否仍然正确。

