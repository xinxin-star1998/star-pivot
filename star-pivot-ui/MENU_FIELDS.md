# 菜单字段说明文档

本文档详细说明菜单相关的字段定义和映射关系。

## 一、后端菜单字段 (SysMenu)

后端菜单实体类定义在 `star-pivot-system/src/main/java/com/star/pivot/system/domain/entity/SysMenu.java`

### 基础字段

| 字段名 | 类型 | 说明 | 示例 |
|--------|------|------|------|
| `menuId` | Long | 菜单ID（主键，自增） | 1 |
| `menuName` | String | 菜单名称 | "系统管理" |
| `parentId` | Long | 父菜单ID（0表示顶级菜单） | 0 |
| `orderNum` | Integer | 显示顺序（数字越小越靠前） | 1 |
| `path` | String | 路由地址 | "/system" |
| `component` | String | 组件路径（相对于 views 目录） | "/system/user" |
| `query` | String | 路由参数 | "?id=1" |
| `routeName` | String | 路由名称（Vue Router 的 name） | "System" |

### 状态字段

| 字段名 | 类型 | 说明 | 取值 |
|--------|------|------|------|
| `isFrame` | Integer | 是否为外链/iframe | 0=是外链/iframe, 1=否 |
| `isCache` | Integer | 是否缓存页面 | 0=缓存, 1=不缓存 |
| `menuType` | String | 菜单类型 | M=目录, C=菜单, F=按钮 |
| `visible` | String | 是否在菜单中显示 | 0=显示, 1=隐藏 |
| `status` | String | 菜单状态 | 0=正常, 1=停用 |

### 其他字段

| 字段名 | 类型 | 说明 | 示例 |
|--------|------|------|------|
| `perms` | String | 权限标识 | "system:user:list" |
| `icon` | String | 菜单图标（Iconify 图标名） | "ri:user-3-line" |
| `createBy` | String | 创建者 | "admin" |
| `createTime` | Date | 创建时间 | 2025-01-01 10:00:00 |
| `updateBy` | String | 更新者 | "admin" |
| `updateTime` | Date | 更新时间 | 2025-01-01 10:00:00 |
| `remark` | String | 备注 | "系统管理模块" |
| `children` | List<SysMenu> | 子菜单列表（树形结构） | [] |

## 二、前端菜单字段 (AppRouteRecord)

前端路由记录定义在 `art-design-pro/src/types/router/index.ts`

### 基础字段

| 字段名 | 类型 | 说明 | 示例 |
|--------|------|------|------|
| `id` | number | 菜单ID（对应后端的 menuId） | 1 |
| `name` | string | 路由名称（Vue Router 的 name） | "System" |
| `path` | string | 路由路径 | "/system" |
| `component` | string \| Function | 组件路径或异步加载函数 | "/system/user" |
| `children` | AppRouteRecord[] | 子路由列表 | [] |

### Meta 元数据字段 (RouteMeta)

| 字段名 | 类型 | 说明 | 示例 |
|--------|------|------|------|
| `title` | string | 路由标题（必填） | "系统管理" |
| `icon` | string | 路由图标 | "ri:user-3-line" |
| `showBadge` | boolean | 是否显示徽章 | true |
| `showTextBadge` | string | 文本徽章内容 | "NEW" |
| `isHide` | boolean | 是否在菜单中隐藏 | false |
| `isHideTab` | boolean | 是否在标签页中隐藏 | false |
| `link` | string | 外部链接 | "https://example.com" |
| `isIframe` | boolean | 是否为 iframe | false |
| `keepAlive` | boolean | 是否缓存页面 | true |
| `authList` | Array | 操作权限列表 | [{title: "新增", authMark: "add"}] |
| `isFirstLevel` | boolean | 是否为一级菜单 | false |
| `roles` | string[] | 角色权限 | ["R_SUPER", "R_ADMIN"] |
| `fixedTab` | boolean | 是否固定标签页 | false |
| `activePath` | string | 激活菜单路径 | "/system/user" |
| `isFullPage` | boolean | 是否为全屏页面 | false |
| `isAuthButton` | boolean | 是否为权限按钮行 | false |
| `authMark` | string | 权限标识 | "add" |
| `parentPath` | string | 父级路径 | "/system" |

## 三、字段映射关系

后端字段到前端字段的转换逻辑在 `art-design-pro/src/router/core/MenuProcessor.ts`

### 直接映射

| 后端字段 | 前端字段 | 说明 |
|----------|----------|------|
| `menuId` | `id` | 菜单ID |
| `routeName` | `name` | 路由名称 |
| `path` | `path` | 路由路径（会清理 # 符号） |
| `component` | `component` | 组件路径（会清理 # 符号） |
| `menuName` | `meta.title` | 菜单名称 → 路由标题 |
| `icon` | `meta.icon` | 菜单图标 |

### 转换映射

| 后端字段 | 前端字段 | 转换规则 |
|----------|----------|----------|
| `isFrame` | `meta.isIframe` | 0 → true, 1 → false（且不是外链） |
| `isFrame` | `meta.link` | 0 且 path 是 http/https → 设置 link |
| `isCache` | `meta.keepAlive` | 0 → true, 1 → false |
| `visible` | `meta.isHide` | 0 → false, 1 → true |
| `menuType` | `component` | M 类型且 level=0 → 使用 Layout |
| `children` | `children` | 递归转换 |

### 特殊处理

1. **路径清理**：自动清理路径和 component 中的 `#` 符号
2. **路径规范化**：自动处理路径重复和拼接问题
3. **Layout 组件**：一级目录菜单自动使用 Layout 组件
4. **外链识别**：自动识别 http/https 开头的路径为外链

## 四、字段使用示例

### 后端返回示例

```json
{
  "menuId": 1,
  "menuName": "系统管理",
  "parentId": 0,
  "orderNum": 1,
  "path": "/system",
  "component": "/index/index",
  "routeName": "System",
  "isFrame": 1,
  "isCache": 0,
  "menuType": "M",
  "visible": "0",
  "status": "0",
  "icon": "ri:user-3-line",
  "children": [
    {
      "menuId": 2,
      "menuName": "用户管理",
      "parentId": 1,
      "orderNum": 1,
      "path": "user",
      "component": "/system/user",
      "routeName": "User",
      "isFrame": 1,
      "isCache": 0,
      "menuType": "C",
      "visible": "0",
      "status": "0",
      "icon": "ri:user-line"
    }
  ]
}
```

### 前端转换后示例

```typescript
{
  id: 1,
  name: "System",
  path: "/system",
  component: "/index/index",
  meta: {
    title: "系统管理",
    icon: "ri:user-3-line",
    keepAlive: true,
    isHide: false,
    isIframe: false
  },
  children: [
    {
      id: 2,
      name: "User",
      path: "/system/user",
      component: "/system/user",
      meta: {
        title: "用户管理",
        icon: "ri:user-line",
        keepAlive: true,
        isHide: false,
        isIframe: false
      }
    }
  ]
}
```

## 五、重要字段说明

### menuType（菜单类型）

- **M（目录）**：目录类型，用于分组，通常没有实际页面
- **C（菜单）**：菜单类型，有实际页面，需要配置 component
- **F（按钮）**：按钮类型，用于权限控制，不在菜单中显示

### isFrame（外链/iframe）

- **0**：外链或 iframe
  - 如果 path 以 http/https 开头 → 外链（设置 `meta.link`）
  - 否则 → iframe（设置 `meta.isIframe = true`）
- **1**：普通路由

### visible（显示状态）

- **0**：在菜单中显示（`meta.isHide = false`）
- **1**：在菜单中隐藏（`meta.isHide = true`）

### isCache（缓存状态）

- **0**：缓存页面（`meta.keepAlive = true`）
- **1**：不缓存（`meta.keepAlive = false`）

## 六、相关文件

- 后端实体：`star-pivot-system/src/main/java/com/star/pivot/system/domain/entity/SysMenu.java`
- 前端类型：`art-design-pro/src/types/router/index.ts`
- 前端接口类型：`art-design-pro/src/api/system-manage.ts`
- 数据转换：`art-design-pro/src/router/core/MenuProcessor.ts`

