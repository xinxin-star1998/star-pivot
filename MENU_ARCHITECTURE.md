# 菜单系统架构说明

## 架构模式：后端为主，前端为辅

本系统采用"后端为主，前端为辅"的混合模式菜单系统。

## 架构设计

### 1. 后端为主：提供菜单数据

#### 1.1 接口设计
- **接口路径**：`GET /api/sys/menu/userMenuTree`
- **接口说明**：根据当前登录用户的权限返回菜单树
- **权限控制**：需要认证，Spring Security自动获取当前用户信息

#### 1.2 实现逻辑
```java
// SysMenuController.java
@GetMapping("/userMenuTree")
public Result<List<SysMenu>> getUserMenuTree(Authentication authentication) {
    // 1. 从Authentication获取当前用户
    // 2. 查询用户有权限的菜单（平铺列表）
    // 3. 构建菜单树结构
    // 4. 返回树形菜单数据
}
```

#### 1.3 数据过滤
- 只返回用户有权限的菜单（通过角色关联）
- 过滤掉按钮类型菜单（F类型）
- 过滤掉隐藏菜单（visible='1'）
- 过滤掉停用菜单（status='1'）
- 按orderNum排序

### 2. 前端为辅：渲染菜单和路由守卫

#### 2.1 菜单渲染
- **位置**：`MenuBar.vue`、`MenuItem.vue`
- **方式**：使用递归组件渲染菜单树
- **数据来源**：调用 `/api/sys/menu/userMenuTree` 接口

#### 2.2 路由配置
- **位置**：`router/routes.ts`
- **方式**：前端配置完整的静态路由表
- **说明**：所有可能的路由都在前端配置，包括权限配置

#### 2.3 路由守卫
- **位置**：`router/index.ts`
- **功能**：
  1. 检查Token是否存在
  2. 加载用户信息和菜单数据
  3. 检查路由meta中的权限配置
  4. 检查路由是否在用户菜单中
  5. 双重验证确保安全性

## 工作流程

### 登录流程
1. 用户登录 → 获取Token
2. 前端存储Token
3. 跳转到首页

### 菜单加载流程
1. 用户访问需要认证的页面
2. 路由守卫检查Token
3. 调用 `/api/sys/menu/userMenuTree` 获取菜单
4. 后端根据用户权限返回菜单树
5. 前端转换菜单格式并存储
6. 递归组件渲染菜单

### 路由访问流程
1. 用户点击菜单或直接访问路由
2. 路由守卫拦截
3. 检查路由meta中的权限配置
4. 检查路由是否在用户菜单中
5. 双重验证通过后允许访问

## 优势

### 后端为主的优势
1. **权限集中管理**：所有权限判断在后端完成
2. **安全性高**：前端无法绕过权限检查
3. **动态性强**：菜单可以根据用户权限动态变化
4. **易于维护**：权限变更只需修改后端

### 前端为辅的优势
1. **用户体验好**：前端路由配置完整，支持直接访问
2. **SEO友好**：路由完整，有利于SEO
3. **开发效率高**：前端可以预先配置所有路由
4. **双重保障**：前端路由守卫提供额外安全层

## 文件结构

### 后端文件
```
star-pivot-controller/
  └── src/main/java/com/star/pivot/controller/
      └── SysMenuController.java          # 菜单控制器
star-pivot-system/
  └── src/main/java/com/star/pivot/system/
      ├── service/
      │   ├── SysMenuService.java         # 菜单服务接口
      │   └── impl/
      │       └── SysMenuServiceImpl.java # 菜单服务实现
      └── mapper/
          └── SysMenuMapper.java          # 菜单Mapper
```

### 前端文件
```
star-pivot-ui/src/
  ├── http/api/menu/
  │   └── menu.ts                         # 菜单API
  ├── router/
  │   ├── index.ts                        # 路由配置和守卫
  │   └── routes.ts                       # 静态路由配置
  ├── store/menu/
  │   └── index.ts                        # 菜单状态管理
  └── layout/menu/
      ├── MenuBar.vue                     # 菜单栏组件
      └── MenuItem.vue                    # 菜单项组件（递归）
```

## 使用示例

### 后端添加新菜单
1. 在数据库中添加菜单记录
2. 配置菜单的权限标识（perms）
3. 分配菜单给相应角色
4. 前端无需修改，菜单自动显示

### 前端添加新路由
1. 在 `router/routes.ts` 中添加路由配置
2. 配置路由的meta.roles权限
3. 创建对应的Vue组件
4. 后端配置对应的菜单和权限

## 注意事项

1. **权限标识一致性**：前端路由的meta.roles必须与后端菜单的perms一致
2. **路径一致性**：前端路由的path必须与后端菜单的path一致
3. **菜单加载时机**：菜单在路由守卫中加载，确保每次访问都能获取最新菜单
4. **双重验证**：前端路由守卫和菜单检查双重验证，确保安全性

