# 菜单数据生成脚本使用说明

## 功能说明

`generate-menu-data.js` 脚本可以根据 `src/views` 目录下的文件结构自动生成菜单表数据。

## 使用方法

```bash
# 方法1: 使用 npm 脚本
npm run generate:menu

# 方法2: 直接运行脚本
node scripts/generate-menu-data.js
```

## 生成的文件

脚本会在项目根目录生成两个文件：

1. **menu-data.sql** - SQL INSERT 语句，可以直接导入数据库
2. **menu-data.json** - JSON 格式的菜单数据，便于查看和编辑

## 菜单结构规则

### 目录结构映射

- **一级目录** → 一级菜单（M类型，使用 Layout 组件）
- **二级目录** → 二级菜单（C类型，指向具体组件）
- **index.vue** → 页面组件

### 示例

```
views/
  dashboard/          → 一级菜单：仪表盘
    console/
      index.vue       → 二级菜单：控制台
  system/             → 一级菜单：系统管理
    user/
      index.vue       → 二级菜单：用户管理
    role/
      index.vue       → 二级菜单：角色管理
```

### 排除的目录

以下目录不会生成菜单：

- `auth` - 认证相关页面
- `exception` - 异常页面
- `result` - 结果页面
- `outside` - 外部页面
- `index` - 布局页面
- `modules` - 模块组件（不作为菜单）

## 字段说明

### 自动生成的字段

- `menuName` - 根据目录/文件名自动生成中文名称
- `path` - 根据文件路径自动生成路由路径
- `component` - 根据文件路径自动生成组件路径
- `routeName` - 根据路径自动生成路由名称（PascalCase）
- `menuType` - 自动判断（M=目录，C=菜单）
- `icon` - 根据目录自动分配图标

### 可配置的字段

可以在脚本中修改以下配置：

```javascript
// 目录对应的图标
const DIR_ICONS = {
  dashboard: 'ri:pie-chart-line',
  system: 'ri:user-3-line'
  // 添加更多...
}

// 目录对应的中文名称
const DIR_NAMES = {
  dashboard: '仪表盘',
  system: '系统管理'
  // 添加更多...
}

// 页面对应的中文名称
const PAGE_NAMES = {
  console: '控制台',
  user: '用户管理'
  // 添加更多...
}
```

## SQL 使用说明

生成的 SQL 文件包含：

1. **DELETE 语句** - 清空现有菜单数据
2. **INSERT 语句** - 插入新菜单数据

### 注意事项

- `menu_id` 字段由数据库自增，不需要手动指定
- `parent_id` 会根据插入顺序自动计算
- 如果菜单层级复杂，可能需要手动调整 `parent_id`

### 导入数据库

```sql
-- 方法1: 直接执行 SQL 文件
source menu-data.sql;

-- 方法2: 复制 SQL 内容到数据库客户端执行
```

## 自定义配置

如果需要自定义菜单名称、图标等，可以：

1. 修改脚本中的配置对象（`DIR_ICONS`、`DIR_NAMES`、`PAGE_NAMES`）
2. 重新运行脚本生成新的数据
3. 或者直接编辑生成的 JSON 文件，然后手动导入数据库

## 示例输出

```
菜单数据预览:
- [M] 仪表盘 (/dashboard)
  - [C] 控制台 (/dashboard/console)
- [M] 系统管理 (/system)
  - [C] 菜单管理 (/system/menu)
  - [C] 角色管理 (/system/role)
  - [C] 用户管理 (/system/user)
  - [C] 个人中心 (/system/user-center)
```
