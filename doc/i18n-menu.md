# 国际化说明（菜单 + 字典 + UI）

## 概述

| 类型 | 默认列 | `sys_i18n` | 生效方式 |
|---|---|---|---|
| 菜单 | `sys_menu.menu_name` | `namespace=menu` | 动态路由 `meta.title`；菜单树 `/sys/menu/menuTree`、上级树 `/sys/menu/getParent`；非默认语言在「国际化管理 → UI 文案 → 菜单」维护 |
| 字典 | `sys_dict_data.dict_label` | `namespace=dict_data` | `/sys/dict/data/type/{type}` |
| UI 文案 | 前端 `zh.json` / `en.json` 兜底 | `namespace=ui` | 启动时 merge 到 vue-i18n；与菜单同页「UI 文案」切换命名空间编辑 |
| 后端错误码 | `ErrorCode` 默认中文 | `classpath:i18n/messages*.properties` | `X-Lang` → `MessageSource` |

缺失译文时均回退默认语言/本地 JSON / 枚举默认文案。

## 表约定

### sys_lang

| 字段 | 说明 |
|---|---|
| lang_code | `zh` / `en` / `ja` 等，与前端语言切换对齐 |
| is_default | `1` 表示默认语言 |
| status | `0` 正常 `1` 停用 |

### sys_i18n

| namespace | resource_key | field_name | 说明 |
|---|---|---|---|
| `menu` | menuId | `menu_name` | 菜单标题 |
| `dict_data` | dictCode | `dict_label` | 字典标签 |
| `ui` | 点分 key，如 `login.title` | `_` | 前端静态文案 |

数组项用数字段：`setting.menuType.list.0`。

长文案可执行 `sql/sys_i18n_alter_content.sql` 将 `content` 扩至 2000。

## 初始化

```bash
# 推荐：一份全量（DDL + menu + dict + ui）
mysql -u root -p your_db < sql/sys_i18n.sql

# 或分文件增量/覆盖：
# mysql -u root -p your_db < sql/sys_menu.sql
# mysql -u root -p your_db < sql/sys_i18n_rebuild_menu.sql
# mysql -u root -p your_db < sql/sys_i18n_dict.sql
# mysql -u root -p your_db < sql/sys_i18n_ui.sql

# 可选：仅扩容译文字段（全量 DDL 已含 varchar(2000)）
# mysql -u root -p your_db < sql/sys_i18n_alter_content.sql
```

重新生成：

```bash
# 仅 UI 增量脚本
python sql/_gen_i18n_ui.py
# 全量 sys_i18n.sql（DDL + menu + dict + ui）
python sql/_gen_sys_i18n_full.py
```

重新生成后端错误码文案（改完 `ErrorCode.java` 后）：

```bash
python sql/_gen_error_i18n.py
```

清 Redis（可选）：

- `i18n:menu:zh` / `i18n:menu:en`
- `i18n:dict:zh` / `i18n:dict:en`
- `i18n:ui:zh` / `i18n:ui:en`

## 接口

前缀：`/system/i18n`（网关一般为 `/api/system/i18n`）

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| GET | `/lang/list` | 已登录/匿名 | 启用语言 |
| GET | `/lang/all` | i18n:query | 全部语言 |
| POST/PUT | `/lang` | i18n:edit | 增改语言 |
| PUT | `/lang/{id}/status` | i18n:edit | 启停 |
| GET | `/resource` | i18n/menu/data:query | 某资源多语言 |
| PUT | `/resource` | i18n/menu/data:edit | 批量保存 |
| GET | `/bundle?namespace=&lang=` | 已登录 | 语言包 |
| GET | `/bundle/ui?lang=` | **匿名** | UI 语言包（登录页） |
| GET | `/coverage?namespace=&lang=` | i18n:query | 覆盖率与缺失列表 |
| GET | `/export?namespace=&lang=` | i18n:query | 导出 JSON |
| POST | `/import` | i18n:edit | 导入 JSON（`bundle` 或 `items`） |

语言优先级：`X-Lang` → `Accept-Language` → 默认语言。

## 前端

1. `zh.json`/`en.json` 本地兜底；`main.ts` 调用 `loadRemoteUiMessages()` 合并库表覆盖。
2. 顶栏切语言：**无整页刷新**，热更新 UI 包 + 重拉动态路由/标签标题；Element Plus locale 随语言联动（未知语言回退 en）。
3. 第三语言：库表启用即可；无本地 JSON 时依赖远程 UI 包 + `fallbackLocale=zh`。
4. 国际化管理：
   - **语言管理**：`sys_lang`
   - **UI 文案**：命名空间可选 `ui`（页面文案）或 `menu`（菜单标题，key 为 menuId）；按语言浏览/搜索/编辑
   - **覆盖率**：对比默认语言缺失项；支持 JSON 导入/导出
5. 菜单管理弹窗只维护默认语言 `menu_name`；非默认语言标题请在「国际化管理 → UI 文案 → 菜单」中修改。
6. 已 `$t` 覆盖的前端模块（节选）：
   - 系统：用户/角色/部门/岗位/字典/菜单/通知/参数/国际化/操作日志/登录日志/用户中心
   - 监控：定时任务/在线用户/服务器/Redis
   - 文件中心：主列表、弹窗、网格/时间线、版本/标签、分享/审计/统计
   - 工具：代码生成、外部库表代码生成
   - AI：会话管理、知识库、配置、用量统计

## 后端错误码

- 资源：`classpath:i18n/messages.properties`（默认/zh）、`messages_en.properties`
- key：`error.{ErrorCode.name}`，如 `error.USER_NOT_FOUND`
- `GlobalExceptionHandler`：无自定义 detail 时按 `X-Lang` 解析；有 detail 仍返回自定义文案

## 缓存

| Redis | 说明 |
|---|---|
| `i18n:menu:{lang}` | 菜单 |
| `i18n:dict:{lang}` | 字典 |
| `i18n:ui:{lang}` | UI 文案 |
