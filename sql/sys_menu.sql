-- 一级目录 parent_id = 0
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 1, '/system', '', NULL, 'System', 1, 1, 'M', '0', '0', '', 'ep:setting', 'system', '2025-12-31 16:34:16', 'admin', '2026-04-23 17:00:25', '系统管理模块');
INSERT INTO `sys_menu` VALUES (2, '系统工具', 0, 2, '/tools', '', NULL, 'SystemTools', 1, 1, 'M', '0', '0', '', 'clarity:tools-line', 'admin', '2026-01-20 13:08:43', 'admin', '2026-04-19 19:09:23', '系统工具');
INSERT INTO `sys_menu` VALUES (3, '系统监控', 0, 3, '/monitor', '', NULL, 'Monitor', 1, 1, 'M', '0', '0', '', 'material-symbols:monitor-outline', 'admin', '2026-01-25 18:00:43', 'admin', '2026-05-18 15:42:45', '系统监控模块');
INSERT INTO `sys_menu` VALUES (4, '商城系统', 0, 5, '/mall', '', NULL, 'MallSystem', 1, 1, 'M', '0', '0', '', 'ep:goods', 'admin', '2026-04-23 20:23:16', 'admin', '2026-04-23 20:26:06', '商城系统');
INSERT INTO `sys_menu` VALUES (5, '星枢项目', 0, 99, 'https://gitee.com/xin1998/StarPivot', '', NULL, '', 0, 1, 'M', '0', '0', '', 'ri:gitee-fill', 'admin', '2026-04-21 12:48:19', 'admin', '2026-04-23 20:26:13', '');
INSERT INTO `sys_menu` VALUES (6, 'art-design-pro', 0, 100, 'https://gitee.com/lingchen163/art-design-pro', '', NULL, '', 0, 1, 'M', '0', '0', '', 'ri:guide-fill', 'admin', '2026-04-19 19:07:54', 'admin', '2026-04-23 20:26:22', '');

-- 系统管理(1) 子菜单
INSERT INTO `sys_menu` VALUES (7, '菜单管理', 1, 1, 'menu', '/system/menu', NULL, 'SystemMenu', 1, 1, 'C', '0', '0', 'system:menu:list', 'ep:menu', 'system', '2025-12-31 16:34:16', '', '2026-01-02 21:12:33', '菜单管理模块');
INSERT INTO `sys_menu` VALUES (8, '角色管理', 1, 2, 'role', '/system/role', NULL, 'SystemRole', 1, 1, 'C', '0', '0', 'system:role:list', 'oui:app-users-roles', 'system', '2025-12-31 16:34:16', '', '2026-01-02 21:30:56', '角色管理模块');
INSERT INTO `sys_menu` VALUES (9, '用户管理', 1, 3, 'user', '/system/user', NULL, 'SystemUser', 1, 1, 'C', '0', '0', 'system:user:list', 'mdi:user', 'system', '2025-12-31 16:34:16', '', '2026-01-02 21:31:51', '用户管理模块');
INSERT INTO `sys_menu` VALUES (10, '部门管理', 1, 4, 'dept', '/system/dept', NULL, 'SystemDept', 1, 1, 'C', '0', '0', 'system:dept:list', 'ri:organization-chart', '', '2026-01-02 21:04:34', '', '2026-01-02 21:36:43', '部门管理模块');
INSERT INTO `sys_menu` VALUES (11, '岗位管理', 1, 5, 'post', '/system/post/index', NULL, 'PostManage', 1, 1, 'C', '0', '0', 'system:post:list', 'picon:post', 'xinxin', '2026-01-04 19:23:51', 'xinxin', '2026-01-04 19:25:02', '岗位管理模块');
INSERT INTO `sys_menu` VALUES (12, '字典管理', 1, 6, 'dict', '/system/dict/index', NULL, 'DictManage', 1, 1, 'C', '0', '0', 'system:type:list', 'arcticons:zdict', 'admin', '2026-01-05 12:28:54', 'admin', '2026-01-19 21:37:20', '字典管理模块。有：字典数据   字典类型');
INSERT INTO `sys_menu` VALUES (13, '日志管理', 1, 7, 'log', '', NULL, 'LogManager', 1, 1, 'M', '0', '0', '', 'mdi:math-log', 'admin', '2026-01-23 13:37:05', 'admin', '2026-05-15 09:09:47', '');
INSERT INTO `sys_menu` VALUES (14, '通知公告', 1, 8, 'notice', '/system/notice/index', NULL, 'NoticeManage', 1, 0, 'C', '0', '0', 'system:notice:list', 'fe:notice-active', 'admin', '2026-02-05 17:38:35', 'admin', '2026-03-31 22:03:49', '通知公告菜单');
INSERT INTO `sys_menu` VALUES (15, '参数配置', 1, 9, 'config', '/system/config/index', NULL, 'ConfigManage', 1, 1, 'C', '0', '0', 'system:config:list', 'mynaui:config', 'admin', '2026-03-31 22:03:28', 'admin', '2026-03-31 22:05:20', '参数配置菜单');

-- 日志管理(13) 子菜单
INSERT INTO `sys_menu` VALUES (16, '操作日志', 13, 1, 'oper', '/system/log/oper/index', NULL, 'OperLog', 1, 1, 'C', '0', '0', 'system:log:list', 'icon-park-solid:log', 'admin', '2026-01-23 13:40:41', '', NULL, '操作日志');
INSERT INTO `sys_menu` VALUES (17, '登录日志', 13, 2, 'login', '/system/log/login/index', NULL, 'LoginInfoLog', 1, 1, 'C', '0', '0', 'system:login:list', 'icon-park-solid:log', 'admin', '2026-01-23 13:51:37', '', NULL, '登录日志');

-- 系统工具(2) 子菜单
INSERT INTO `sys_menu` VALUES (18, '代码生成', 2, 1, 'generator', '/tools/generator/index', NULL, 'GenerateTools', 1, 1, 'C', '0', '0', 'tools:generator:list', 'mdi:generator-mobile', 'admin', '2026-01-20 13:15:59', 'admin', '2026-01-20 13:25:42', '代码生成');

-- 系统监控(3) 子菜单
INSERT INTO `sys_menu` VALUES (19, '服务器监控', 3, 1, 'server', '/monitor/server/index', NULL, 'ServerMonitor', 1, 0, 'C', '0', '0', 'monitor:server:query', 'ri:server-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:33:45', '服务器信息监控');
INSERT INTO `sys_menu` VALUES (20, 'Druid监控', 3, 2, 'druid', '/monitor/druid/index', NULL, 'DruidMonitor', 1, 0, 'C', '0', '0', 'monitor:druid:query', 'ri:database-2-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:32:03', 'Druid数据库连接池监控');
INSERT INTO `sys_menu` VALUES (21, 'Redis缓存', 3, 3, 'redis', '/monitor/redis/index', NULL, 'RedisMonitor', 1, 0, 'C', '0', '0', 'monitor:redis:query', 'ri:database-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-03-01 13:37:51', 'Redis缓存监控');
INSERT INTO `sys_menu` VALUES (22, '在线用户', 3, 4, 'online', '/monitor/online/index', NULL, 'OnlineUser', 1, 0, 'C', '0', '0', 'monitor:online:query', 'ri:user-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:32:45', '在线用户监控');
INSERT INTO `sys_menu` VALUES (23, '定时任务', 3, 5, 'job', '/monitor/job/index', NULL, 'MonitorJob', 1, 1, 'C', '0', '0', 'monitor:job:query', 'ri:time-line', 'admin', '2026-02-06 19:58:43', 'admin', '2026-02-06 20:34:11', '定时任务调度');

-- 商城系统(4) 子菜单
INSERT INTO `sys_menu` VALUES (24, '商品管理', 4, 1, 'good', '', NULL, 'GoodManager', 1, 1, 'M', '0', '0', '', 'ep:goods-filled', 'admin', '2026-05-15 10:38:29', '', NULL, '');

-- 商品管理(24) 子菜单
INSERT INTO `sys_menu` VALUES (25, '分类维护', 24, 1, 'category', '/mall/category/index', NULL, 'CategoryManager', 1, 1, 'C', '0', '0', 'mall:category:list', 'ri:node-tree', 'admin', '2026-05-15 10:43:03', 'admin', '2026-05-18 14:48:34', '');
INSERT INTO `sys_menu` VALUES (26, '品牌列表', 24, 2, 'brand', '/mall/brand/index', NULL, 'BrandManager', 1, 1, 'C', '0', '0', 'mall:brand:list', 'ep:shop', 'admin', '2026-04-23 20:25:38', 'admin', '2026-05-18 14:44:07', '品牌管理');
INSERT INTO `sys_menu` VALUES (27, '平台属性', 24, 3, 'platform-attributes', '', NULL, 'PlatformAttributes', 1, 1, 'M', '0', '0', '', 'mdi:tools', 'admin', '2026-05-18 14:43:45', 'admin', '2026-05-18 15:04:12', '');
INSERT INTO `sys_menu` VALUES (28, '商品维护', 24, 4, 'product', '/mall/product/index', NULL, 'ProductManager', 1, 1, 'C', '0', '0', 'mall:product:list', 'ep:goods-filled', 'admin', '2026-04-23 20:27:38', 'admin', '2026-05-18 14:48:55', '商品管理');

-- 平台属性(27) 子菜单
INSERT INTO `sys_menu` VALUES (29, '属性分组', 27, 1, 'group', '/mall/group/index', NULL, 'AttrgroupManager', 1, 1, 'C', '0', '0', '', 'ep:histogram', 'admin', '2026-05-18 14:59:27', 'admin', '2026-05-18 15:27:29', '');
INSERT INTO `sys_menu` VALUES (30, '规格参数', 27, 2, 'base', '/mall/attr/base/index', NULL, 'BaseParam', 1, 1, 'C', '0', '0', '', 'ep:document', 'admin', '2026-05-18 15:06:50', 'admin', '2026-05-18 16:39:24', '');
INSERT INTO `sys_menu` VALUES (31, '销售属性', 27, 3, 'sale', '/mall/attr/sale/index', NULL, 'SalesAttributes', 1, 1, 'C', '0', '0', '', 'ep:present', 'admin', '2026-05-18 15:12:11', 'admin', '2026-05-18 16:39:34', '');

-- ==================== 按钮权限（F类型）====================
-- 菜单管理(7) 按钮
INSERT INTO `sys_menu` VALUES (32, '菜单查询', 7, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2026-01-12 17:39:26', '', NULL, '菜单查询');
INSERT INTO `sys_menu` VALUES (33, '菜单添加', 7, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2026-01-12 17:39:50', '', NULL, '菜单添加');
INSERT INTO `sys_menu` VALUES (34, '菜单修改', 7, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2026-01-12 17:40:05', '', NULL, '菜单修改');
INSERT INTO `sys_menu` VALUES (35, '菜单删除', 7, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:menu:delete', '#', 'admin', '2026-01-12 17:40:27', '', NULL, '菜单删除');

-- 角色管理(8) 按钮
INSERT INTO `sys_menu` VALUES (36, '新增角色', 8, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:add', '#', 'admin', '2026-01-12 17:32:13', '', NULL, '新增角色按钮');
INSERT INTO `sys_menu` VALUES (37, '修改角色', 8, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2026-01-12 17:32:45', '', NULL, '修改角色按钮');
INSERT INTO `sys_menu` VALUES (38, '删除角色', 8, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:delete', '#', 'admin', '2026-01-12 17:33:14', '', NULL, '删除角色按钮');
INSERT INTO `sys_menu` VALUES (39, '角色查询', 8, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:query', '#', 'admin', '2026-01-12 17:34:57', '', NULL, '角色查询 按钮');
INSERT INTO `sys_menu` VALUES (40, '分配数据权限', 8, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:assignDataScope', '#', 'admin', '2026-01-16 19:04:22', 'admin', '2026-01-23 20:57:15', '分配角色');
INSERT INTO `sys_menu` VALUES (41, '已分配用户', 8, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:allocatedList', '#', 'admin', '2026-01-23 20:55:10', 'admin', '2026-01-23 22:41:41', '已分配用户');
INSERT INTO `sys_menu` VALUES (42, '未分配用户', 8, 7, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:unallocatedList', '#', 'admin', '2026-01-23 22:41:08', '', NULL, '未分配用户');
INSERT INTO `sys_menu` VALUES (43, '分配用户', 8, 7, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:assignUser', '#', 'admin', '2026-01-23 23:18:49', '', NULL, '分配用户：添加用户角色关联表');
INSERT INTO `sys_menu` VALUES (44, '取消授权', 8, 9, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:cancelUser', '#', 'admin', '2026-01-23 23:26:23', '', NULL, '取消授权');

-- 用户管理(9) 按钮
INSERT INTO `sys_menu` VALUES (45, '新增用户', 9, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:add', '#', 'admin', '2026-01-12 16:42:30', 'admin', '2026-01-16 20:42:35', '新增用户按钮');
INSERT INTO `sys_menu` VALUES (46, '修改状态', 9, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:changeStatus', '#', 'admin', '2026-01-16 18:11:22', 'admin', '2026-01-16 20:42:31', '修改状态');
INSERT INTO `sys_menu` VALUES (47, '修改用户', 9, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2026-01-12 17:30:37', 'admin', '2026-01-16 20:42:26', '修改用户  按钮');
INSERT INTO `sys_menu` VALUES (48, '删除用户', 9, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:delete', '#', 'admin', '2026-01-12 17:31:11', 'admin', '2026-01-16 20:42:39', '删除用户按钮');
INSERT INTO `sys_menu` VALUES (49, '用户查询', 9, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:query', '#', 'admin', '2026-01-12 17:35:27', 'admin', '2026-01-16 20:42:43', '用户查询按钮');
INSERT INTO `sys_menu` VALUES (50, '修改密码', 9, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2026-01-16 18:10:55', 'admin', '2026-01-16 20:42:48', '修改密码');
INSERT INTO `sys_menu` VALUES (51, '解除锁定', 9, 7, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:unLock', '#', 'admin', '2026-01-23 18:49:56', '', NULL, '解除锁定');
INSERT INTO `sys_menu` VALUES (52, '用户导入', 9, 8, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:import', '#', 'admin', '2026-01-24 21:03:18', '', NULL, '用户导入');
INSERT INTO `sys_menu` VALUES (53, '用户导出', 9, 9, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:export', '#', 'admin', '2026-01-24 21:03:52', '', NULL, '用户导出');

-- 部门管理(10) 按钮
INSERT INTO `sys_menu` VALUES (54, '部门查询', 10, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:dept:query', '#', 'admin', '2026-01-12 17:35:58', '', NULL, '部门查询按钮');
INSERT INTO `sys_menu` VALUES (55, '部门新增', 10, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:dept:add', '#', 'admin', '2026-01-12 17:36:28', '', NULL, '部门新增');
INSERT INTO `sys_menu` VALUES (56, '部门修改', 10, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2026-01-12 17:36:49', '', NULL, '部门修改');
INSERT INTO `sys_menu` VALUES (57, '部门删除', 10, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:dept:delete', '#', 'admin', '2026-01-12 17:37:21', '', NULL, '部门删除');

-- 岗位管理(11) 按钮
INSERT INTO `sys_menu` VALUES (58, '岗位查询', 11, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:post:query', '#', 'admin', '2026-01-12 17:37:47', '', NULL, '岗位查询');
INSERT INTO `sys_menu` VALUES (59, '岗位新增', 11, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:post:add', '#', 'admin', '2026-01-12 17:38:09', '', NULL, '岗位新增');
INSERT INTO `sys_menu` VALUES (60, '岗位修改', 11, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:post:edit', '#', 'admin', '2026-01-12 17:38:46', '', NULL, '岗位修改');
INSERT INTO `sys_menu` VALUES (61, '岗位删除', 11, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:post:delete', '#', 'admin', '2026-01-12 17:39:04', '', NULL, '岗位删除');

-- 字典管理(12) 按钮
INSERT INTO `sys_menu` VALUES (62, '字典类型添加', 12, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:type:add', '#', 'admin', '2026-01-16 19:08:40', 'admin', '2026-01-16 19:33:35', '字典类型添加');
INSERT INTO `sys_menu` VALUES (63, '字典类型修改', 12, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:type:edit', '#', 'admin', '2026-01-16 19:09:04', 'admin', '2026-01-16 19:33:43', '字典类型修改');
INSERT INTO `sys_menu` VALUES (64, '字典类型删除', 12, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:type:delete', '#', 'admin', '2026-01-16 19:09:27', 'admin', '2026-01-16 19:33:48', '字典类型删除');
INSERT INTO `sys_menu` VALUES (65, '字典类型查询', 12, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:type:query', '#', 'admin', '2026-01-19 21:33:21', '', NULL, '字典类型查询');
INSERT INTO `sys_menu` VALUES (66, '字典数据添加', 12, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:data:add', '#', 'admin', '2026-01-16 19:31:42', '', NULL, '字典数据添加');
INSERT INTO `sys_menu` VALUES (67, '字典数据修改', 12, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:data:edit', '#', 'admin', '2026-01-16 19:32:19', '', NULL, '字典数据修改');
INSERT INTO `sys_menu` VALUES (68, '字典数据删除', 12, 7, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:data:delete', '#', 'admin', '2026-01-16 19:32:51', '', NULL, '字典数据删除');
INSERT INTO `sys_menu` VALUES (69, '字典数据查询', 12, 8, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:data:query', '#', 'admin', '2026-01-19 21:33:59', '', NULL, '字典数据查询');

-- 操作日志(16) 按钮
INSERT INTO `sys_menu` VALUES (70, '日志查询', 16, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:operlog:query', '#', 'admin', '2026-01-23 13:58:02', '', NULL, '日志查询');
INSERT INTO `sys_menu` VALUES (71, '清空日志', 16, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:operlog:delete', '#', 'admin', '2026-01-23 13:57:43', '', NULL, '清空日志');

-- 登录日志(17) 按钮
INSERT INTO `sys_menu` VALUES (72, '日志查询', 17, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:logininfor:query', '#', 'admin', '2026-01-23 14:24:26', '', NULL, '日志查询');
INSERT INTO `sys_menu` VALUES (73, '日志删除', 17, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:logininfor:delete', '#', 'admin', '2026-01-23 14:24:41', '', NULL, '日志删除');

-- 通知公告(14) 按钮
INSERT INTO `sys_menu` VALUES (74, '通知公告查询', 14, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (75, '通知公告新增', 14, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (76, '通知公告修改', 14, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (77, '通知公告删除', 14, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:delete', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (78, '通知公告导出', 14, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:export', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');

-- 参数配置(15) 按钮
INSERT INTO `sys_menu` VALUES (79, '参数配置查询', 15, 1, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:query', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:23', '');
INSERT INTO `sys_menu` VALUES (80, '参数配置新增', 15, 2, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:add', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:27', '');
INSERT INTO `sys_menu` VALUES (81, '参数配置修改', 15, 3, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:edit', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:31', '');
INSERT INTO `sys_menu` VALUES (82, '参数配置删除', 15, 4, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:delete', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:36', '');
INSERT INTO `sys_menu` VALUES (83, '参数配置导出', 15, 5, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:export', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:39', '');

-- 代码生成(18) 按钮
INSERT INTO `sys_menu` VALUES (84, '添加', 18, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:add', '#', 'admin', '2026-01-23 15:56:17', 'admin', '2026-01-23 19:01:31', '添加');
INSERT INTO `sys_menu` VALUES (85, '列表查询', 18, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2026-01-20 14:56:43', '', NULL, '列表查询');
INSERT INTO `sys_menu` VALUES (86, '预览', 18, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2026-01-23 15:53:25', 'admin', '2026-01-23 19:01:40', '预览');
INSERT INTO `sys_menu` VALUES (87, '编辑', 18, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2026-01-23 15:53:47', 'admin', '2026-01-23 19:01:44', '');
INSERT INTO `sys_menu` VALUES (88, '删除', 18, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:delete', '#', 'admin', '2026-01-23 15:54:05', 'admin', '2026-01-23 19:01:48', '');
INSERT INTO `sys_menu` VALUES (89, '同步数据库', 18, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:sync', '#', 'admin', '2026-01-23 15:55:18', 'admin', '2026-01-23 19:01:52', '同步数据库');
INSERT INTO `sys_menu` VALUES (90, '生成代码', 18, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:create', '#', 'admin', '2026-01-23 15:55:59', 'admin', '2026-01-23 19:01:56', '生成代码');
INSERT INTO `sys_menu` VALUES (91, '导入', 18, 8, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:import', '#', 'xinxin', '2026-01-24 00:08:41', '', NULL, '导入');

-- 在线用户(22) 按钮
INSERT INTO `sys_menu` VALUES (92, '强制下线', 22, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:online:force-logout', '', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:30:55', '强制用户下线');

-- Redis缓存(21) 按钮
INSERT INTO `sys_menu` VALUES (93, '删除缓存', 21, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:cache:remove', '#', 'admin', '2026-01-25 22:19:38', '', NULL, 'monitor:cache:remove');
INSERT INTO `sys_menu` VALUES (94, '清空缓存', 21, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:cache:clear', '#', 'admin', '2026-01-25 22:20:00', '', NULL, '清空缓存');
INSERT INTO `sys_menu` VALUES (95, '缓存列表', 21, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:cache:query', '#', 'admin', '2026-01-27 18:21:55', '', NULL, '缓存列表');

-- 定时任务(23) 按钮
INSERT INTO `sys_menu` VALUES (96, '任务查询', 23, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2026-02-06 20:01:06', '', NULL, '');
INSERT INTO `sys_menu` VALUES (97, '任务新增', 23, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2026-02-06 20:01:06', '', NULL, '');
INSERT INTO `sys_menu` VALUES (98, '任务修改', 23, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2026-02-06 20:01:06', '', NULL, '');
INSERT INTO `sys_menu` VALUES (99, '任务删除', 23, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:job:delete', '#', 'admin', '2026-02-06 20:01:06', '', NULL, '');

-- 属性分组(29) 按钮
INSERT INTO `sys_menu` VALUES (100, '属性分组查询', 29, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:group:query', '#', 'admin', '2026-05-18 15:42:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (101, '属性分组新增', 29, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:group:add', '#', 'admin', '2026-05-18 15:42:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (102, '属性分组修改', 29, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:group:edit', '#', 'admin', '2026-05-18 15:42:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (103, '属性分组删除', 29, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:group:delete', '#', 'admin', '2026-05-18 15:42:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (104, '属性分组导出', 29, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:group:export', '#', 'admin', '2026-05-18 15:42:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (105, '属性分组导入', 29, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:group:import', '#', 'admin', '2026-05-18 19:46:08', '', NULL, '属性分组导入');

-- 规格参数(30) 按钮
INSERT INTO `sys_menu` VALUES (106, '基本属性查询', 30, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:base:query', '#', 'admin', '2026-05-18 16:29:55', 'admin', '2026-05-18 16:45:18', '基本属性查询');
INSERT INTO `sys_menu` VALUES (107, '基础属性添加', 30, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:base:add', '#', 'admin', '2026-05-18 16:45:44', '', NULL, '基础属性添加');
INSERT INTO `sys_menu` VALUES (108, '基础属性删除', 30, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:base:delete', '#', 'admin', '2026-05-18 16:46:07', '', NULL, '基础属性删除');
INSERT INTO `sys_menu` VALUES (109, '基础属性修改', 30, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:base:edit', '#', 'admin', '2026-05-18 16:46:27', 'admin', '2026-05-18 16:55:39', '基础属性修改');
INSERT INTO `sys_menu` VALUES (110, '基本规格导入', 30, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:base:import', '#', 'admin', '2026-05-18 19:47:10', '', NULL, '基本规格导入');
INSERT INTO `sys_menu` VALUES (111, '基本规格导出', 30, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:base:export', '#', 'admin', '2026-05-18 19:47:35', '', NULL, '基本规格导出');

-- 销售属性(31) 按钮
INSERT INTO `sys_menu` VALUES (112, '基本属性查询', 31, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:sale:query', '#', 'admin', '2026-05-18 16:30:37', 'admin', '2026-05-18 16:46:53', '基本属性查询');
INSERT INTO `sys_menu` VALUES (113, '基本属性添加', 31, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:sale:add', '#', 'admin', '2026-05-18 16:47:23', '', NULL, '基本属性添加');
INSERT INTO `sys_menu` VALUES (114, '基本属性删除', 31, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:sale:delete', '#', 'admin', '2026-05-18 16:47:47', '', NULL, '基本属性删除');
INSERT INTO `sys_menu` VALUES (115, '基本属性修改', 31, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:sale:edit', '#', 'admin', '2026-05-18 16:48:12', 'admin', '2026-05-18 16:55:45', '基本属性修改');
INSERT INTO `sys_menu` VALUES (116, '销售属性导入', 31, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:sale:import', '#', 'admin', '2026-05-18 19:48:01', '', NULL, '销售属性导入');
INSERT INTO `sys_menu` VALUES (117, '销售属性导出', 31, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:sale:export', '#', 'admin', '2026-05-18 19:48:22', '', NULL, '销售属性导出');

-- 品牌列表(26) 按钮
INSERT INTO `sys_menu` VALUES (118, '品牌查询', 26, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:brand:query', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (119, '品牌新增', 26, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:brand:add', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (120, '品牌修改', 26, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:brand:edit', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (121, '品牌删除', 26, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:brand:delete', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');

-- 分类维护(25) 按钮
INSERT INTO `sys_menu` VALUES (122, '分类查询', 25, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:category:query', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (123, '分类新增', 25, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:category:add', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (124, '分类修改', 25, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:category:edit', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (125, '分类删除', 25, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:category:delete', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');

-- 商品维护(28) 按钮
INSERT INTO `sys_menu` VALUES (126, '商品查询', 28, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:product:query', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (127, '商品新增', 28, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:product:add', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (128, '商品修改', 28, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:product:edit', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (129, '商品删除', 28, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:product:delete', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');