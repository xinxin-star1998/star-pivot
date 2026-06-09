# StarPivot 数据库迁移指南

> 本文档描述 StarPivot 权限管理系统的数据库结构、初始化流程、版本迁移策略及数据备份恢复方案。

---

## 目录

- [数据库概览](#数据库概览)
- [SQL 脚本说明](#sql-脚本说明)
- [首次初始化](#首次初始化)
- [Docker 环境自动初始化](#docker-环境自动初始化)
- [手动初始化（非 Docker）](#手动初始化非-docker)
- [版本迁移与补丁](#版本迁移与补丁)
- [数据库备份与恢复](#数据库备份与恢复)
- [多环境数据库配置](#多环境数据库配置)
- [表结构说明](#表结构说明)
- [常见问题](#常见问题)

---

## 数据库概览

| 项目       | 值                          |
| ---------- | --------------------------- |
| 数据库引擎 | MySQL 8.0                   |
| 字符集     | utf8mb4 / utf8mb4_unicode_ci |
| 时区       | GMT+8 (Asia/Shanghai)       |
| 默认库名   | `star-pivot`                |
| ORM        | MyBatis-Plus                |
| 连接池     | Druid                       |
| 迁移工具   | Flyway                      |

---

## SQL 脚本说明

所有 SQL 文件位于 `sql/` 目录：

| 文件                          | 用途                    | 导入顺序 | 说明                           |
| ----------------------------- | ----------------------- | -------- | ------------------------------ |
| `star-pivot.sql`              | 核心库结构 + 初始数据   | 1        | 包含系统表、商城表、Quartz 表 |
| `workflow.sql`                | 工作流模块表结构        | 2        | wf_* 系列表                  |
| `mall_pms.sql`                | 商城商品模块独立结构    | 3        | 与 star-pivot.sql 中 pms_* 重复时可选 |
| `patch_workflow_menu.sql`     | 工作流菜单数据补丁      | 4        | 在 star-pivot.sql 之后执行    |
| `patch_external_gen_menu.sql` | 外部代码生成菜单补丁    | 5        | 在 star-pivot.sql 之后执行    |
| `star_pivot_dev.sql`          | 开发环境完整数据        | -        | 包含大量测试数据，**仅开发环境** |

> **注意**：`star-pivot.sql` 已包含 `pms_*` 表结构，`mall_pms.sql` 为独立备份，通常无需重复导入。

---

## 首次初始化

### 前提条件

- MySQL 8.0+ 已安装并运行
- 拥有创建数据库和用户权限的账号（通常为 root）

### 步骤

```bash
# 1. 登录 MySQL
mysql -u root -p

# 2. 创建数据库
CREATE DATABASE `star-pivot` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 3. 创建应用用户（可选但推荐）
CREATE USER 'starpivot'@'%' IDENTIFIED BY '<强密码>';
GRANT ALL PRIVILEGES ON `star-pivot`.* TO 'starpivot'@'%';
FLUSH PRIVILEGES;

# 4. 退出并导入 SQL
exit

mysql -u root -p star-pivot < sql/star-pivot.sql
mysql -u root -p star-pivot < sql/workflow.sql
mysql -u root -p star-pivot < sql/patch_workflow_menu.sql
mysql -u root -p star-pivot < sql/patch_external_gen_menu.sql
```

### Windows (PowerShell)

```powershell
# 使用 mysql 客户端
mysql -u root -p star-pivot < sql/star-pivot.sql
mysql -u root -p star-pivot < sql/workflow.sql
mysql -u root -p star-pivot < sql/patch_workflow_menu.sql
mysql -u root -p star-pivot < sql/patch_external_gen_menu.sql
```

---

## Docker 环境自动初始化

Docker 部署时，数据库初始化由 Flyway 自动管理。应用启动时，Flyway 会检测并执行 `db/migration/` 下的迁移脚本。

### 启动

```bash
docker compose up -d
# 1. MySQL 容器启动（空数据库）
# 2. 应用容器启动 → Flyway 自动执行 V1~Vn 迁移
```

### 环境变量

| 变量             | 默认值 | 说明                            |
| ---------------- | ------ | ------------------------------- |
| `FLYWAY_ENABLED` | true   | 设为 false 可跳过迁移（调试用）|

> **注意**：如需完全重建数据库，先删除数据卷：
> ```bash
> docker compose down -v   # ⚠️ 会删除所有数据
> docker compose up -d     # Flyway 自动重建全部表和数据
> ```

---

## 手动初始化（非 Docker）

适用于已在外部部署 MySQL 的场景（如阿里云 RDS）。

### 1. 连接数据库

```bash
mysql -h <RDS_HOST> -P 3306 -u <USERNAME> -p
```

### 2. 按顺序导入

```sql
USE `star-pivot`;
SOURCE /path/to/sql/star-pivot.sql;
SOURCE /path/to/sql/workflow.sql;
SOURCE /path/to/sql/patch_workflow_menu.sql;
SOURCE /path/to/sql/patch_external_gen_menu.sql;
```

### 3. 配置应用连接

编辑 `application-prod.yml` 或通过环境变量设置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://<RDS_HOST>:3306/star-pivot?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: <USERNAME>
    password: <PASSWORD>
```

或通过 Docker 环境变量：

```ini
# .env
DB_URL=jdbc:mysql://<RDS_HOST>:3306/star-pivot?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
DB_USERNAME=<USERNAME>
DB_PASSWORD=<PASSWORD>
```

---

## Flyway 数据库迁移

### 概述

StarPivot 使用 [Flyway](https://flywaydb.org/) 管理数据库版本迁移。迁移脚本位于：

```
star-pivot-controller/src/main/resources/db/migration/
├── V1__init_schema.sql       ← 全量表结构（49 张表 DDL）
├── V2__init_data.sql         ← 系统初始数据（用户、角色、菜单、字典等）
├── V3__workflow_tables.sql   ← 工作流模块表（4 张 wf_* 表）
├── V4__patch_menus.sql       ← 菜单补丁（外部代码生成 + 工作流菜单）
└── V{n}__*.sql               ← 后续增量迁移
```

### 命名规范

迁移文件遵循 Flyway 标准命名：

```
V{version}__{description}.sql
```

- `V` 前缀表示版本化迁移（不可修改）
- `{version}` 递增版本号（V5, V6, ...）
- `{description}` 使用下划线描述变更内容

### 配置说明

Flyway 配置位于 `application.yml`：

```yaml
spring:
  flyway:
    enabled: ${FLYWAY_ENABLED:true}
    locations: classpath:db/migration
    baseline-on-migrate: true
    baseline-version: 4
    validate-on-migrate: true
```

| 配置项               | 默认值 | 说明                                           |
| -------------------- | ------ | ---------------------------------------------- |
| `enabled`            | true   | 是否启用 Flyway                                |
| `baseline-on-migrate`| true   | 已有数据库首次运行时自动 baseline              |
| `baseline-version`   | 4      | baseline 版本号，<= 此版本的迁移会被跳过       |
| `validate-on-migrate`| true   | 迁移前校验已执行脚本的 checksum                |

### 各环境策略

| 环境 | Flyway 状态 | 说明                                                  |
| ---- | ----------- | ----------------------------------------------------- |
| dev  | 默认关闭    | 远程共享库避免冲突；本地可设 `FLYWAY_ENABLED=true`    |
| prod | 默认开启    | Docker 部署自动执行迁移，含 connect-retries 重试      |

### 新增迁移步骤

1. 在 `db/migration/` 下创建新文件，如 `V5__add_audit_log.sql`
2. 编写 DDL / DML（使用 `CREATE TABLE IF NOT EXISTS`、`INSERT IGNORE` 等幂等操作）
3. 启动应用，Flyway 自动执行未应用的迁移
4. 查看 `flyway_schema_history` 表确认迁移状态

```sql
SELECT version, description, success, installed_on FROM flyway_schema_history;
```

### 重新初始化（Docker）

如需从零开始重建数据库：

```bash
docker compose down -v   # ⚠️ 删除数据卷（含所有数据）
docker compose up -d     # Flyway 自动执行全部迁移
```

---

## 数据库备份与恢复

### 自动备份（推荐）

创建备份脚本 `backup-db.sh`：

```bash
#!/bin/bash
BACKUP_DIR="/data/backups/star-pivot"
DATE=$(date +%Y%m%d_%H%M%S)
KEEP_DAYS=30

mkdir -p $BACKUP_DIR

# Docker 环境
docker compose exec -T mysql mysqldump -u root -p${MYSQL_ROOT_PASSWORD} \
  --single-transaction \
  --routines \
  --triggers \
  star-pivot | gzip > "$BACKUP_DIR/star-pivot_$DATE.sql.gz"

# 清理旧备份
find $BACKUP_DIR -name "*.sql.gz" -mtime +$KEEP_DAYS -delete

echo "Backup completed: star-pivot_$DATE.sql.gz"
```

设置 cron 定时任务：

```bash
# 每天凌晨 2:30 执行备份
30 2 * * * /path/to/backup-db.sh >> /var/log/star-pivot-backup.log 2>&1
```

### 手动备份

```bash
# Docker 环境
docker compose exec -T mysql mysqldump -u root -p<密码> \
  --single-transaction star-pivot > backup.sql

# 非 Docker 环境
mysqldump -h <HOST> -u root -p --single-transaction star-pivot > backup.sql
```

### 恢复

```bash
# Docker 环境
docker compose exec -T mysql mysql -u root -p<密码> star-pivot < backup.sql

# 非 Docker 环境
mysql -h <HOST> -u root -p star-pivot < backup.sql
```

### 仅恢复表结构

```bash
mysqldump -u root -p --no-data star-pivot > schema_only.sql
mysql -u root -p star-pivot_new < schema_only.sql
```

---

## 多环境数据库配置

### 环境对比

| 配置项            | 本地 (local)            | 开发 (dev)              | 生产 (prod)              |
| ----------------- | ----------------------- | ----------------------- | ------------------------ |
| Profile           | `application-local.yml` | `application-dev.yml`   | `application-prod.yml`   |
| 数据库地址        | `localhost:3306`        | 远程开发库              | 生产 RDS / Docker MySQL  |
| Druid 控制台      | 开启                    | 开启                    | **关闭**                 |
| 连接池大小        | 5-20                    | 1-20                    | 5-50                     |
| 慢 SQL 阈值       | 默认                    | 5000ms                  | 3000ms                   |
| 测试数据          | 可导入 star_pivot_dev   | 可导入 star_pivot_dev   | **禁止导入**             |

### 开发环境（使用完整测试数据）

```bash
# 导入开发数据（包含大量测试用户、商品等）
mysql -u root -p star_pivot_dev < sql/star_pivot_dev.sql
```

> `star_pivot_dev.sql` 约 8MB，包含完整测试数据集，仅用于开发/演示环境。

---

## 表结构说明

### 系统核心表 (sys_*)

| 表名                          | 说明             |
| ----------------------------- | ---------------- |
| `sys_user`                    | 用户信息         |
| `sys_role`                    | 角色信息         |
| `sys_menu`                    | 菜单权限         |
| `sys_dept`                    | 部门（树形结构） |
| `sys_post`                    | 岗位信息         |
| `sys_role_menu`               | 角色-菜单关联    |
| `sys_role_dept`               | 角色-部门关联    |
| `sys_user_role`               | 用户-角色关联    |
| `sys_user_post`               | 用户-岗位关联    |
| `sys_config`                  | 参数配置         |
| `sys_dict_type`               | 字典类型         |
| `sys_dict_data`               | 字典数据         |
| `sys_notice`                  | 通知公告         |
| `sys_oper_log`                | 操作日志         |
| `sys_logininfor`              | 登录日志         |
| `sys_online_user`             | 在线用户记录     |
| `sys_job`                     | 定时任务         |
| `sys_job_log`                 | 任务执行日志     |
| `sys_monitor_api_performance` | API 性能监控     |
| `sys_monitor_slow_sql`        | 慢 SQL 记录      |

### 工作流表 (wf_*)

| 表名                | 说明           |
| ------------------- | -------------- |
| `wf_process_def`    | 流程定义       |
| `wf_instance`       | 流程实例       |
| `wf_task`           | 待办任务       |
| `wf_task_history`   | 审批历史       |

### 商城商品表 (pms_*)

| 表名                          | 说明             |
| ----------------------------- | ---------------- |
| `pms_attr`                    | 商品属性         |
| `pms_attr_group`              | 属性分组         |
| `pms_attr_attrgroup_relation` | 属性-分组关联    |
| `pms_brand`                   | 品牌             |
| `pms_category`                | 商品分类（三级） |
| `pms_category_brand_relation` | 分类-品牌关联    |
| `pms_sku_info`                | SKU 信息         |
| `pms_sku_images`              | SKU 图片         |
| `pms_sku_sale_attr_value`     | SKU 销售属性值   |
| `pms_spu_info`                | SPU 信息         |
| `pms_spu_info_desc`           | SPU 详情描述     |
| `pms_spu_images`              | SPU 图片         |
| `pms_spu_comment`             | 商品评价         |
| `pms_comment_replay`          | 评价回复         |
| `pms_product_attr_value`      | SPU 属性值       |

### 代码生成表 (gen_*)

| 表名              | 说明           |
| ----------------- | -------------- |
| `gen_table`       | 代码生成业务表 |
| `gen_table_column`| 业务表字段信息 |

### Quartz 调度表 (qrtz_*)

| 表名                      | 说明               |
| ------------------------- | ------------------ |
| `qrtz_job_details`        | 任务详细信息       |
| `qrtz_triggers`           | 触发器信息         |
| `qrtz_simple_triggers`    | 简单触发器         |
| `qrtz_simprop_triggers`   | 同步属性触发器     |
| `qrtz_cron_triggers`      | Cron 触发器        |
| `qrtz_blob_triggers`      | Blob 触发器        |
| `qrtz_calendars`          | 日历               |
| `qrtz_paused_trigger_grps`| 暂停的触发器组     |
| `qrtz_fired_triggers`     | 已触发的触发器     |
| `qrtz_scheduler_state`    | 调度器状态         |
| `qrtz_locks`              | 悲观锁             |

---

## 常见问题

### Q: Docker 首次启动后数据库为空？

MySQL Docker 镜像仅在**数据卷首次创建**时执行 `docker-entrypoint-initdb.d` 中的脚本。如果数据卷已存在（即使表为空），不会重新执行。

**解决**：
```bash
docker compose down -v   # 删除数据卷
docker compose up -d     # 重新创建并初始化
```

### Q: 导入 SQL 报错 `Unknown character set`？

确保 MySQL 版本 >= 8.0，且服务端支持 `utf8mb4`：
```sql
SHOW VARIABLES LIKE 'character_set%';
-- 确认 character_set_server = utf8mb4
```

### Q: 导入 star-pivot.sql 报 `Table already exists`？

脚本使用了 `DROP TABLE IF EXISTS`，正常情况下不会报错。如出现此错误，检查：
1. 是否有外键约束导致无法 DROP（先禁用：`SET FOREIGN_KEY_CHECKS = 0;`）
2. 是否有其他进程锁定表

### Q: 如何在已有数据库上添加新模块表？

仅导入对应模块的 SQL 文件即可，例如添加工作流模块：
```bash
mysql -u root -p star-pivot < sql/workflow.sql
mysql -u root -p star-pivot < sql/patch_workflow_menu.sql
```

### Q: MyBatis-Plus 逻辑删除如何影响迁移？

系统使用 `deleted` 字段进行逻辑删除（`0` 未删除，`1` 已删除）。迁移数据时：
- 确保 `deleted` 字段一并导出
- 如只需活跃数据，可添加 `WHERE deleted = 0` 过滤

### Q: 数据库字符集不一致导致乱码？

统一设置为 `utf8mb4`：
```sql
ALTER DATABASE `star-pivot` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- 对每张表执行：
ALTER TABLE `table_name` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
