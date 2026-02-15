# 基于 Flowable 的工作流审批 — 企业级开发文档

> 本文档描述 StarPivot 项目中基于 **Flowable BPMN** 的工作流审批模块的企业级架构、数据模型、接口设计、扩展方式及运维规范，供架构评审与开发实施使用。

---

## 1. 概述与目标

### 1.1 业务目标

- **统一审批入口**：待办、我发起的、审批记录、流程实例与业务数据关联可查。
- **双模式支持**：
  - **简单线性审批**：基于自建业务表（`wf_definition` / `wf_node` / `wf_instance` / `wf_approval_record`），适合固定顺序、审批人由配置驱动的场景。
  - **BPMN 流程**：基于 Flowable 引擎，流程定义从 `classpath*:/processes/**/*.bpmn` 自动部署，适合会签、条件分支、子流程等复杂场景。
- **业务摘要与审计**：无论哪种模式，均可通过业务表维护「业务类型 + 业务主键」与流程实例的对应关系，便于列表展示、统计与审计。

### 1.2 技术选型

| 组件           | 选型                    | 说明 |
|----------------|-------------------------|------|
| 流程引擎       | Flowable 7.0.1         | BPMN 2.0、与 Spring Boot 3 兼容，与主数据源共用数据库 |
| 流程定义存储   | Flowable ACT_* 表       | 由引擎自动建表、版本管理 |
| 业务摘要表     | 自建 wf_* 表           | 可选，与 ACT_* 并存，用于列表、统计、与业务绑定 |
| 前端           | Vue 3 + Element Plus    | 审批管理页、流程图设计器（动态节点） |

---

## 2. 架构设计

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           前端 (star-pivot-ui)                            │
│  审批管理(待我审批/我发起的) │ 流程图设计器(流程定义/节点配置) │ 业务表单发起  │
└─────────────────────────────────────────────────────────────────────────┘
                                        │
                                        ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    后端 API (star-pivot-controller)                       │
│  /api/workflow/approval  │  /api/workflow/definition  │  业务模块接口     │
└─────────────────────────────────────────────────────────────────────────┘
                                        │
          ┌─────────────────────────────┼─────────────────────────────┐
          ▼                             ▼                             ▼
┌──────────────────┐        ┌──────────────────┐        ┌──────────────────┐
│ 简单审批模式      │        │ Flowable 引擎     │        │ 业务模块         │
│ (wf_* 业务表)    │        │ RuntimeService   │        │ (如请假、报销)    │
│ 流程定义/节点/   │        │ TaskService      │        │ 业务表 + 发起/   │
│ 实例/审批记录    │        │ RepositoryService│        │ 回调              │
└──────────────────┘        └──────────────────┘        └──────────────────┘
          │                             │                             │
          └─────────────────────────────┼─────────────────────────────┘
                                        ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  MySQL：业务库 + Flowable ACT_* 表（同库，flowable.database-schema-update） │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2.2 双轨策略（业务表 + Flowable）

- **业务表（wf_*）**：承担「业务视角」的流程摘要与审计。
  - `wf_instance` 与 Flowable 关联字段：`process_instance_id`（执行 `workflow_flowable_migration.sql` 后增加）。
  - 发起流程时：若走 BPMN，则先 `RuntimeService.startProcessInstanceByKey(...)`，再将返回的 `processInstanceId` 写入 `wf_instance.process_instance_id`。
- **Flowable ACT_* 表**：由引擎管理流程定义、运行时实例、任务、历史等，不做业务列表查询的主数据源，仅通过 `process_instance_id` 与 `wf_instance` 关联。
- **审批记录**：简单模式用 `wf_approval_record`；BPMN 模式可从 `ACT_HI_TASKINST` 等历史表构建或双写，`wf_approval_record.node_id` 在 BPMN 下可为空（见迁移脚本）。

---

## 3. 数据模型

### 3.1 自建业务表（与 Flowable 并存）

建表脚本：`sql/workflow_approval.sql`。Flowable 集成扩展脚本：`sql/workflow_flowable_migration.sql`（需先执行建表再执行迁移）。

| 表名                 | 说明 |
|----------------------|------|
| `wf_definition`      | 流程定义（流程 key、名称、描述、版本、状态） |
| `wf_node`            | 流程节点（节点 key、名称、顺序、审批人类型/取值、是否允许驳回） |
| `wf_instance`        | 流程实例（关联定义、业务类型/业务ID、提交人、当前节点、状态、**process_instance_id**） |
| `wf_approval_record` | 审批记录（实例、节点 id 可空、审批人、结果、意见、时间） |

### 3.2 关键字段说明

- **wf_instance**
  - `business_type`、`business_id`：与业务主数据绑定，如 `leave` + 请假单 ID。
  - `process_instance_id`：Flowable 流程实例 ID，与 `ACT_RU_EXECUTION` / `ACT_HI_PROCINST` 对应。
  - `status`：`running` / `approved` / `rejected`，可由引擎事件或定时同步更新。
- **wf_approval_record**
  - `node_id`：简单模式必填；BPMN 模式下可为空，审批记录可从 Flowable 历史构建。

### 3.3 Flowable 引擎表（自动维护）

- 流程定义：`ACT_RE_PROCDEF`、`ACT_GE_BYTEARRAY` 等。
- 运行时：`ACT_RU_EXECUTION`、`ACT_RU_TASK`、`ACT_RU_VARIABLE` 等。
- 历史：`ACT_HI_PROCINST`、`ACT_HI_TASKINST`、`ACT_HI_VARINST` 等。

业务查询以 **wf_* 表** 为主，按需关联 Flowable 表（如需要当前任务名、候选人等）。

---

## 4. 流程定义与部署

### 4.1 简单模式（动态节点）

- 流程定义与节点保存在 `wf_definition`、`wf_node`，通过「流程图设计器」页面维护（新建/编辑流程、拖拽节点、配置审批人类型与取值）。
- 引擎不参与执行，由业务代码按节点顺序推进并写入 `wf_approval_record`。

### 4.2 BPMN 模式（Flowable）

- **放置位置**：`classpath*:/processes/`，后缀 `**.bpmn20.xml,**.bpmn`（与 `application.yml` 中 `flowable.process-definition-location-*` 一致）。
- **建议目录**：在 `star-pivot-controller` 或独立模块的 `src/main/resources/processes/` 下放置 BPMN 文件。
- **部署**：应用启动时 Flowable 自动扫描并部署，无需额外接口；版本由引擎管理。
- **示例**（单用户任务）：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
             xmlns:flowable="http://flowable.org/bpmn" targetNamespace="http://star.pivot/workflow">
    <process id="leaveRequest" name="请假审批">
        <startEvent id="start" />
        <sequenceFlow sourceRef="start" targetRef="deptLeaderTask" />
        <userTask id="deptLeaderTask" name="部门领导审批" flowable:assignee="${deptLeader}" />
        <sequenceFlow sourceRef="deptLeaderTask" targetRef="end" />
        <endEvent id="end" />
    </process>
</definitions>
```

- 启动实例时传入变量（如 `deptLeader`、`businessKey`），并将返回的 `processInstance.getId()` 写入 `wf_instance.process_instance_id`。

---

## 5. 应用配置

### 5.1 依赖（star-pivot-dependencies）

```xml
<flowable.version>7.0.1</flowable.version>
<!-- ... -->
<dependency>
    <groupId>org.flowable</groupId>
    <artifactId>flowable-spring-boot-starter</artifactId>
    <version>${flowable.version}</version>
</dependency>
```

### 5.2 配置项（application.yml）

```yaml
flowable:
  database-schema-update: true   # 与主数据源共用，启动时更新引擎表结构
  async-executor-activate: false # 按需开启异步执行
  process-definition-location-prefix: classpath*:/processes/
  process-definition-location-suffixes: "**.bpmn20.xml,**.bpmn"
  check-process-definitions: true
```

- 与主库共用数据源，无需单独配置 `flowable.datasource`。
- 生产环境建议将 `database-schema-update` 设为 `false`，由 DBA 或迁移脚本管理表结构。

---

## 6. 接口设计（REST API）

### 6.1 审批相关 `/api/workflow/approval`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET  | `/pending/list` | 待我审批列表（含流程名称、业务类型、业务ID、提交时间） |
| GET  | `/my/list` | 我发起的列表，可选 `status`（running/approved/rejected） |
| GET  | `/instance/{id}` | 按实例 ID 查流程实例 |
| GET  | `/instance?businessType=&businessId=` | 按业务类型+业务主键查实例 |
| GET  | `/instance/{id}/records` | 审批记录列表 |
| POST | `/submit` | 提交审批（body: definitionKey, businessType, businessId, submitterId） |
| POST | `/approve` | 审批通过/驳回（body: instanceId, approved, comment） |

### 6.2 流程定义相关 `/api/workflow/definition`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET  | `/list` | 流程定义列表（下拉/设计器用） |
| GET  | `/{id}` | 流程定义详情（含节点，流程图设计器用） |
| POST | `/` | 新增流程定义 |
| PUT  | `/{id}` | 修改流程定义 |
| DELETE| `/{id}` | 删除流程定义 |
| PUT  | `/{definitionId}/nodes` | 保存节点（全量替换） |

### 6.3 与 Flowable 的对接要点

- **提交**：若流程为 BPMN，先根据 `definitionKey` 解析为流程 key，调用 `runtimeService.startProcessInstanceByKey(key, businessKey, variables)`，再插入/更新 `wf_instance`（含 `process_instance_id`）。
- **待办**：简单模式查 `wf_instance` + `wf_node`；BPMN 模式可查 `TaskService.createTaskQuery().taskAssignee(userId).list()`，再与 `wf_instance` 通过 `process_instance_id` 关联。
- **审批**：BPMN 模式调用 `taskService.complete(taskId, variables)`，并可选写入 `wf_approval_record`（node_id 为空）或从历史表同步。

---

## 7. 前端模块

### 7.1 页面与路由

- **审批管理**：`src/views/workflow/approval/index.vue`
  -  Tab「待我审批」「我发起的」；列表 + 筛选（我发起的按状态）；详情抽屉（流程信息、审批记录、审批操作）。
- **流程图设计器**：`src/views/workflow/flowchart/index.vue`
  - 流程定义选择/新建/编辑/删除；节点拖拽排序；节点编辑（nodeKey、nodeName、审批人类型/取值）；保存节点。

### 7.2 接口封装

- `src/api/workflow/approval.ts`：待办、我发起的、实例、审批记录、提交、审批。
- `src/api/workflow/definition.ts`：流程定义列表、详情、增删改、节点保存。
- 类型：`src/types/api/api.d.ts` 中 `Api.Workflow.*`（WfInstance、WfDefinition、WfNode、WfApprovalRecord、各类 DTO/VO）。

---

## 8. 权限与安全

- **菜单/路由**：按现有 RBAC 配置审批管理、流程图设计器菜单及路由。
- **权限点建议**（与 `doc/权限编码与数据权限规范.md` 一致）：
  - `workflow:approval:query`：查看待办、我发起的、实例、审批记录。
  - `workflow:approval:approve`：执行审批（通过/驳回）。
  - `workflow:definition:query`：流程定义列表与详情。
  - `workflow:definition:add` / `edit` / `delete`：流程定义与节点维护。
- **数据权限**：待办/我发起的仅展示与当前用户相关数据；列表接口需按 `submitterId` 或任务候选人/办理人过滤。

---

## 9. 运维与监控

- **数据库**：Flowable 表与业务表同库，备份与恢复一并考虑；大表（如 `ACT_HI_*`）可定期归档或清理历史数据。
- **日志**：关键操作（发起、审批、驳回）建议记录操作日志（与现有 framework-log 集成）。
- **健康与版本**：应用依赖 Flowable 7.0.1，升级 Flowable 时需评估 ACT_* 表结构变更与兼容性，并在测试环境验证。

---

## 10. 扩展与最佳实践

### 10.1 业务接入方式

1. **仅用简单模式**：只使用 wf_* 表与现有审批/定义接口，不部署 BPMN，不依赖 Flowable 运行时。
2. **仅用 BPMN**：流程全部用 BPMN 定义，wf_* 仅做摘要（含 `process_instance_id`），待办与审批记录可从引擎 API 或历史表获取。
3. **混合**：部分流程用简单模式、部分用 BPMN，通过 `wf_definition.definition_key` 或扩展字段区分，提交与审批入口根据类型分支调用不同逻辑。

### 10.2 可扩展点

- **审批人解析**：`wf_node.approver_type` / `approver_value` 可扩展为角色、部门负责人、指定用户、表达式等，由统一解析服务返回 userId 列表。
- **流程实例与业务回调**：流程结束（通过/驳回）时发送事件或调用业务模块回调，更新业务表状态、通知等。
- **会签/或签**：BPMN 中使用多实例（multi-instance），简单模式需在业务层实现多审批人聚合逻辑。

### 10.3 文档与脚本索引

| 资源 | 路径 |
|------|------|
| 业务表建表 | `sql/workflow_approval.sql` |
| Flowable 集成迁移 | `sql/workflow_flowable_migration.sql` |
| 实体类 | `star-pivot-system` 下 `WfDefinition`、`WfNode`、`WfInstance`、`WfApprovalRecord` |
| 权限规范 | `doc/权限编码与数据权限规范.md` |
| 架构图 | `doc/架构图与流程图.md` |

---

*文档版本：1.0 | 基于 Flowable 7.0.1 与当前 StarPivot 代码与表结构整理*
