/*
 Navicat Premium Data Transfer

 Source Server         : 101.201.181.191
 Source Server Type    : MySQL
 Source Server Version : 80045
 Source Host           : 101.201.181.191:3306
 Source Schema         : star_pivot_dev

 Target Server Type    : MySQL
 Target Server Version : 80045
 File Encoding         : 65001

 Date: 29/06/2026 16:02:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 清空旧数据
TRUNCATE TABLE sys_menu;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
                             `menu_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
                             `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
                             `parent_id` bigint(0) NULL DEFAULT 0 COMMENT '父菜单ID',
                             `order_num` int(0) NULL DEFAULT 0 COMMENT '显示顺序',
                             `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由地址',
                             `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件路径',
                             `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由参数',
                             `route_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由名称',
                             `is_frame` int(0) NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
                             `is_cache` int(0) NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
                             `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
                             `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
                             `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
                             `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限标识',
                             `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '#' COMMENT '菜单图标',
                             `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
                             `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                             `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
                             `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                             `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注',
                             PRIMARY KEY (`menu_id`) USING BTREE,
                             INDEX `idx_parent_id`(`parent_id`) USING BTREE,
                             INDEX `idx_status_visible`(`status`, `visible`) USING BTREE,
                             INDEX `idx_order_num`(`order_num`) USING BTREE,
                             INDEX `idx_parent_sort`(`parent_id`, `order_num`) USING BTREE,
                             INDEX `idx_visible_status`(`visible`, `status`) USING BTREE,
                             INDEX `idx_menu_type_status`(`menu_type`, `status`) USING BTREE,
                             INDEX `idx_menu_parent`(`parent_id`) USING BTREE,
                             INDEX `idx_menu_visible_status`(`visible`, `status`) USING BTREE,
                             INDEX `idx_menu_order`(`order_num`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 116 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu（ID连续重排，parent_id已自动修正）
-- ----------------------------
-- 一级目录 M  parent_id=0
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 1, '/system', '', NULL, 'System', 1, 1, 'M', '0', '0', '', 'ep:setting', 'system', '2025-12-31 16:34:16', 'admin', '2026-06-29 15:47:56', '系统管理模块111');
INSERT INTO `sys_menu` VALUES (2, '系统工具', 0, 2, '/tools', '', NULL, 'SystemTools', 1, 1, 'M', '0', '0', '', 'clarity:tools-line', 'admin', '2026-01-20 13:08:43', 'admin', '2026-04-19 19:09:23', '系统工具');
INSERT INTO `sys_menu` VALUES (3, '系统监控', 0, 3, '/monitor', '', NULL, 'Monitor', 1, 1, 'M', '0', '0', '', 'material-symbols:monitor-outline', 'admin', '2026-01-25 18:00:43', 'admin', '2026-05-18 15:42:45', '系统监控模块');
INSERT INTO `sys_menu` VALUES (4, '星枢项目', 0, 99, 'https://gitee.com/xin1998/StarPivot', '', NULL, '', 0, 1, 'M', '0', '0', '', 'ri:gitee-fill', 'admin', '2026-04-21 12:48:19', 'admin', '2026-05-18 22:44:22', '星枢项目');
INSERT INTO `sys_menu` VALUES (5, 'art-design-pro', 0, 100, 'https://gitee.com/lingchen163/art-design-pro', '', NULL, '', 0, 1, 'M', '0', '0', '', 'ri:guide-fill', 'admin', '2026-04-19 19:07:54', 'admin', '2026-04-23 20:26:22', '');
INSERT INTO `sys_menu` VALUES (6, '文件中心', 0, 4, '/file', '', NULL, 'FileCenter', 1, 1, 'M', '0', '0', '', 'ep:folder-opened', 'admin', '2026-06-27 15:56:30', '', NULL, '文件中心模块');

-- 二级页面 C（系统管理下 parent_id=1）
INSERT INTO `sys_menu` VALUES (7, '菜单管理', 1, 1, 'menu', '/system/menu', NULL, 'SystemMenu', 1, 1, 'C', '0', '0', 'system:menu:list', 'ep:menu', 'system', '2025-12-31 16:34:16', '', '2026-01-02 21:12:33', '菜单管理模块');
INSERT INTO `sys_menu` VALUES (8, '角色管理', 1, 2, 'role', '/system/role', NULL, 'SystemRole', 1, 1, 'C', '0', '0', 'system:role:list', 'oui:app-users-roles', 'system', '2025-12-31 16:34:16', 'admin', '2026-06-08 16:46:55', '角色管理模块');
INSERT INTO `sys_menu` VALUES (9, '用户管理', 1, 3, 'user', '/system/user', NULL, 'SystemUser', 1, 1, 'C', '0', '0', 'system:user:list', 'mdi:user', 'system', '2025-12-31 16:34:16', '', '2026-01-02 21:31:51', '用户管理模块');
INSERT INTO `sys_menu` VALUES (10, '部门管理', 1, 4, 'dept', '/system/dept', NULL, 'SystemDept', 1, 1, 'C', '0', '0', 'system:dept:list', 'ri:organization-chart', '', '2026-01-02 21:04:34', '', '2026-01-02 21:36:43', '部门管理模块');
INSERT INTO `sys_menu` VALUES (11, '岗位管理', 1, 5, 'post', '/system/post/index', NULL, 'PostManage', 1, 1, 'C', '0', '0', 'system:post:list', 'picon:post', 'xinxin', '2026-01-04 19:23:51', 'xinxin', '2026-01-04 19:25:02', '岗位管理模块');
INSERT INTO `sys_menu` VALUES (12, '字典管理', 1, 6, 'dict', '/system/dict/index', NULL, 'DictManage', 1, 1, 'C', '0', '0', 'system:type:list', 'arcticons:zdict', 'admin', '2026-01-05 12:28:54', 'admin', '2026-01-19 21:37:20', '字典管理模块。有：字典数据   字典类型');
INSERT INTO `sys_menu` VALUES (13, '日志管理', 1, 7, 'log', '', NULL, 'LogManager', 1, 1, 'M', '0', '0', '', 'mdi:math-log', 'admin', '2026-01-23 13:37:05', 'admin', '2026-05-15 09:09:47', '');
INSERT INTO `sys_menu` VALUES (14, '通知公告', 1, 8, 'notice', '/system/notice/index', NULL, 'NoticeManage', 1, 0, 'C', '0', '0', 'system:notice:list', 'fe:notice-active', 'admin', '2026-02-05 17:38:35', 'admin', '2026-03-31 22:03:49', '通知公告菜单');
INSERT INTO `sys_menu` VALUES (15, '参数配置', 1, 9, 'config', '/system/config/index', NULL, 'ConfigManage', 1, 1, 'C', '0', '0', 'system:config:list', 'mynaui:config', 'admin', '2026-03-31 22:03:28', 'admin', '2026-03-31 22:05:20', '参数配置菜单');

-- 日志管理子页面
INSERT INTO `sys_menu` VALUES (16, '操作日志', 13, 1, 'oper', '/system/log/oper/index', NULL, 'OperLog', 1, 1, 'C', '0', '0', 'system:log:list', 'icon-park-solid:log', 'admin', '2026-01-23 13:40:41', '', NULL, '操作日志');
INSERT INTO `sys_menu` VALUES (17, '登录日志', 13, 2, 'login', '/system/log/login/index', NULL, 'LoginInfoLog', 1, 1, 'C', '0', '0', 'system:login:list', 'icon-park-solid:log', 'admin', '2026-01-23 13:51:37', '', NULL, '登录日志');

-- 系统工具子页面（parent_id=2）
INSERT INTO `sys_menu` VALUES (18, '代码生成', 2, 1, 'generator', '/tools/generator/index', NULL, 'GenerateTools', 1, 1, 'C', '0', '0', 'tools:generator:list', 'mdi:generator-mobile', 'admin', '2026-01-20 13:15:59', 'admin', '2026-01-20 13:25:42', '代码生成');
INSERT INTO `sys_menu` VALUES (19, '外部库代码生成', 2, 2, 'external', '/tools/generator-external/index', NULL, 'GenExternal', 1, 1, 'C', '0', '0', '', 'ri:database-2-line', 'admin', '2026-06-29 15:43:12', 'admin', '2026-06-29 15:43:12', '外部数据库连接代码生成');

-- 系统监控子页面（parent_id=3）
INSERT INTO `sys_menu` VALUES (20, '服务器监控', 3, 1, 'server', '/monitor/server/index', NULL, 'ServerMonitor', 1, 0, 'C', '0', '0', 'monitor:server:query', 'ri:server-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:33:45', '服务器信息监控');
INSERT INTO `sys_menu` VALUES (21, 'Druid监控', 3, 2, 'druid', '/monitor/druid/index', NULL, 'DruidMonitor', 1, 0, 'C', '0', '0', 'monitor:druid:query', 'ri:database-2-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:32:03', 'Druid数据库连接池监控');
INSERT INTO `sys_menu` VALUES (22, 'Redis缓存', 3, 3, 'redis', '/monitor/redis/index', NULL, 'RedisMonitor', 1, 0, 'C', '0', '0', 'monitor:redis:query', 'ri:database-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-03-01 13:37:51', 'Redis缓存监控');
INSERT INTO `sys_menu` VALUES (23, '在线用户', 3, 4, 'online', '/monitor/online/index', NULL, 'OnlineUser', 1, 0, 'C', '0', '0', 'monitor:online:query', 'ri:user-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:32:45', '在线用户监控');
INSERT INTO `sys_menu` VALUES (24, '定时任务', 3, 5, 'job', '/monitor/job/index', NULL, 'MonitorJob', 1, 1, 'C', '0', '0', 'monitor:job:query', 'ri:time-line', 'admin', '2026-02-06 19:58:43', 'admin', '2026-02-06 20:34:11', '定时任务调度');

-- 文件中心子页面（parent_id=6）
INSERT INTO `sys_menu` VALUES (25, '文件管理', 6, 1, 'index', '/file/index', NULL, 'FileManage', 1, 1, 'C', '0', '0', 'file:resource:list', 'ep:document', 'admin', '2026-06-27 15:56:30', '', NULL, '文件管理页面');

-- ====================== 按钮权限 F 开始 ======================
-- 菜单管理 7
INSERT INTO `sys_menu` VALUES (26, '菜单查询', 7, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2026-01-12 17:39:26', '', NULL, '菜单查询');
INSERT INTO `sys_menu` VALUES (27, '菜单添加', 7, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2026-01-12 17:39:50', '', NULL, '菜单添加');
INSERT INTO `sys_menu` VALUES (28, '菜单修改', 7, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2026-01-12 17:40:05', '', NULL, '菜单修改');
INSERT INTO `sys_menu` VALUES (29, '菜单删除', 7, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:menu:delete', '#', 'admin', '2026-01-12 17:40:27', '', NULL, '菜单删除');

-- 角色管理 8
INSERT INTO `sys_menu` VALUES (30, '新增角色', 8, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:add', '#', 'admin', '2026-01-12 17:32:13', '', NULL, '新增角色按钮');
INSERT INTO `sys_menu` VALUES (31, '修改角色', 8, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2026-01-12 17:32:45', '', NULL, '修改角色按钮');
INSERT INTO `sys_menu` VALUES (32, '删除角色', 8, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:delete', '#', 'admin', '2026-01-12 17:33:14', '', NULL, '删除角色按钮');
INSERT INTO `sys_menu` VALUES (33, '角色查询', 8, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:query', '#', 'admin', '2026-01-12 17:34:57', '', NULL, '角色查询 按钮');
INSERT INTO `sys_menu` VALUES (34, '分配数据权限', 8, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:assignDataScope', '#', 'admin', '2026-01-16 19:04:22', 'admin', '2026-01-23 20:57:15', '分配角色');
INSERT INTO `sys_menu` VALUES (35, '已分配用户', 8, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:allocatedList', '#', 'admin', '2026-01-23 20:55:10', 'admin', '2026-01-23 22:41:41', '已分配用户');
INSERT INTO `sys_menu` VALUES (36, '未分配用户', 8, 7, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:unallocatedList', '#', 'admin', '2026-01-23 22:41:08', '', NULL, '未分配用户');
INSERT INTO `sys_menu` VALUES (37, '分配用户', 8, 7, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:assignUser', '#', 'admin', '2026-01-23 23:18:49', '', NULL, '分配用户：添加用户角色关联表');
INSERT INTO `sys_menu` VALUES (38, '取消授权', 8, 9, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:cancelUser', '#', 'admin', '2026-01-23 23:26:23', '', NULL, '取消授权');

-- 用户管理 9
INSERT INTO `sys_menu` VALUES (39, '新增用户', 9, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:add', '#', 'admin', '2026-01-12 16:42:30', 'admin', '2026-01-16 20:42:35', '新增用户按钮');
INSERT INTO `sys_menu` VALUES (40, '修改状态', 9, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:changeStatus', '#', 'admin', '2026-01-16 18:11:22', 'admin', '2026-01-16 20:42:31', '修改状态');
INSERT INTO `sys_menu` VALUES (41, '修改用户', 9, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2026-01-12 17:30:37', 'admin', '2026-01-16 20:42:26', '修改用户  按钮');
INSERT INTO `sys_menu` VALUES (42, '删除用户', 9, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:delete', '#', 'admin', '2026-01-12 17:31:11', 'admin', '2026-01-16 20:42:39', '删除用户按钮');
INSERT INTO `sys_menu` VALUES (43, '用户查询', 9, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:query', '#', 'admin', '2026-01-12 17:35:27', 'admin', '2026-01-16 20:42:43', '用户查询按钮');
INSERT INTO `sys_menu` VALUES (44, '修改密码', 9, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2026-01-16 18:10:55', 'admin', '2026-01-16 20:42:48', '修改密码');
INSERT INTO `sys_menu` VALUES (45, '解除锁定', 9, 7, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:unLock', '#', 'admin', '2026-01-23 18:49:56', '', NULL, '解除锁定');
INSERT INTO `sys_menu` VALUES (46, '用户导入', 9, 8, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:import', '#', 'admin', '2026-01-24 21:03:18', '', NULL, '用户导入');
INSERT INTO `sys_menu` VALUES (47, '用户导出', 9, 9, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:export', '#', 'admin', '2026-01-24 21:03:52', '', NULL, '用户导出');

-- 部门管理 10
INSERT INTO `sys_menu` VALUES (48, '部门查询', 10, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:dept:query', '#', 'admin', '2026-01-12 17:35:58', '', NULL, '部门查询按钮');
INSERT INTO `sys_menu` VALUES (49, '部门新增', 10, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:dept:add', '#', 'admin', '2026-01-12 17:36:28', '', NULL, '部门新增');
INSERT INTO `sys_menu` VALUES (50, '部门修改', 10, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2026-01-12 17:36:49', '', NULL, '部门修改');
INSERT INTO `sys_menu` VALUES (51, '部门删除', 10, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:dept:delete', '#', 'admin', '2026-01-12 17:37:21', '', NULL, '部门删除');

-- 岗位管理 11
INSERT INTO `sys_menu` VALUES (52, '岗位查询', 11, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:post:query', '#', 'admin', '2026-01-12 17:37:47', '', NULL, '岗位查询');
INSERT INTO `sys_menu` VALUES (53, '岗位新增', 11, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:post:add', '#', 'admin', '2026-01-12 17:38:09', '', NULL, '岗位新增');
INSERT INTO `sys_menu` VALUES (54, '岗位修改', 11, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:post:edit', '#', 'admin', '2026-01-12 17:38:46', '', NULL, '岗位修改');
INSERT INTO `sys_menu` VALUES (55, '岗位删除', 11, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:post:delete', '#', 'admin', '2026-01-12 17:39:04', '', NULL, '岗位删除');

-- 字典管理 12
INSERT INTO `sys_menu` VALUES (56, '字典类型添加', 12, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:type:add', '#', 'admin', '2026-01-16 19:08:40', 'admin', '2026-01-16 19:33:35', '字典类型添加');
INSERT INTO `sys_menu` VALUES (57, '字典类型修改', 12, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:type:edit', '#', 'admin', '2026-01-16 19:09:04', 'admin', '2026-01-16 19:33:43', '字典类型修改');
INSERT INTO `sys_menu` VALUES (58, '字典类型删除', 12, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:type:delete', '#', 'admin', '2026-01-16 19:09:27', 'admin', '2026-01-16 19:33:48', '字典类型删除');
INSERT INTO `sys_menu` VALUES (59, '字典类型查询', 12, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:type:query', '#', 'admin', '2026-01-19 21:33:21', '', NULL, '字典类型查询');
INSERT INTO `sys_menu` VALUES (60, '字典数据添加', 12, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:data:add', '#', 'admin', '2026-01-16 19:31:42', '', NULL, '字典数据添加');
INSERT INTO `sys_menu` VALUES (61, '字典数据修改', 12, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:data:edit', '#', 'admin', '2026-01-16 19:32:19', '', NULL, '字典数据修改');
INSERT INTO `sys_menu` VALUES (62, '字典数据删除', 12, 7, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:data:delete', '#', 'admin', '2026-01-16 19:32:51', '', NULL, '字典数据删除');
INSERT INTO `sys_menu` VALUES (63, '字典数据查询', 12, 8, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:data:query', '#', 'admin', '2026-01-19 21:33:59', '', NULL, '字典数据查询');

-- 操作日志 16
INSERT INTO `sys_menu` VALUES (64, '日志查询', 16, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:operlog:query', '#', 'admin', '2026-01-23 13:58:02', '', NULL, '日志查询');
INSERT INTO `sys_menu` VALUES (65, '清空日志', 16, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:operlog:delete', '#', 'admin', '2026-01-23 13:57:43', '', NULL, '清空日志');

-- 登录日志 17
INSERT INTO `sys_menu` VALUES (66, '日志查询', 17, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:logininfor:query', '#', 'admin', '2026-01-23 14:24:26', '', NULL, '日志查询');
INSERT INTO `sys_menu` VALUES (67, '日志删除', 17, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:logininfor:delete', '#', 'admin', '2026-01-23 14:24:41', '', NULL, '日志删除');

-- 通知公告 14
INSERT INTO `sys_menu` VALUES (68, '通知公告查询', 14, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (69, '通知公告新增', 14, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (70, '通知公告修改', 14, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (71, '通知公告删除', 14, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:delete', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (72, '通知公告导出', 14, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:export', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');

-- 参数配置 15
INSERT INTO `sys_menu` VALUES (73, '参数配置查询', 15, 1, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:query', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:23', '');
INSERT INTO `sys_menu` VALUES (74, '参数配置新增', 15, 2, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:add', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:27', '');
INSERT INTO `sys_menu` VALUES (75, '参数配置修改', 15, 3, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:edit', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:31', '');
INSERT INTO `sys_menu` VALUES (76, '参数配置删除', 15, 4, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:delete', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:36', '');
INSERT INTO `sys_menu` VALUES (77, '参数配置导出', 15, 5, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:export', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:39', '');

-- 代码生成 18
INSERT INTO `sys_menu` VALUES (78, '添加', 18, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:add', '#', 'admin', '2026-01-23 15:56:17', 'admin', '2026-01-23 19:01:31', '添加');
INSERT INTO `sys_menu` VALUES (79, '列表查询', 18, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2026-01-20 14:56:43', '', NULL, '列表查询');
INSERT INTO `sys_menu` VALUES (80, '预览', 18, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2026-01-23 15:53:25', 'admin', '2026-01-23 19:01:40', '预览');
INSERT INTO `sys_menu` VALUES (81, '编辑', 18, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2026-01-23 15:53:47', 'admin', '2026-01-23 19:01:44', '');
INSERT INTO `sys_menu` VALUES (82, '删除', 18, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:delete', '#', 'admin', '2026-01-23 15:54:05', 'admin', '2026-01-23 19:01:48', '');
INSERT INTO `sys_menu` VALUES (83, '同步数据库', 18, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:sync', '#', 'admin', '2026-01-23 15:55:18', 'admin', '2026-01-23 19:01:52', '同步数据库');
INSERT INTO `sys_menu` VALUES (84, '生成代码', 18, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:create', '#', 'admin', '2026-01-23 15:55:59', 'admin', '2026-01-23 19:01:56', '生成代码');
INSERT INTO `sys_menu` VALUES (85, '导入', 18, 8, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:import', '#', 'xinxin', '2026-01-24 00:08:41', '', NULL, '导入');

-- 外部库代码生成 19
INSERT INTO `sys_menu` VALUES (86, '查询', 19, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:external:query', '#', 'admin', '2026-06-29 15:43:12', '', NULL, '连接、查表、路径配置');
INSERT INTO `sys_menu` VALUES (87, '预览', 19, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:external:preview', '#', 'admin', '2026-06-29 15:43:13', '', NULL, '预览代码');
INSERT INTO `sys_menu` VALUES (88, '生成', 19, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:external:create', '#', 'admin', '2026-06-29 15:43:13', '', NULL, '下载 ZIP');

-- Redis缓存 22
INSERT INTO `sys_menu` VALUES (89, '删除缓存', 22, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:cache:remove', '#', 'admin', '2026-01-25 22:19:38', '', NULL, 'monitor:cache:remove');
INSERT INTO `sys_menu` VALUES (90, '清空缓存', 22, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:cache:clear', '#', 'admin', '2026-01-25 22:20:00', '', NULL, '清空缓存');
INSERT INTO `sys_menu` VALUES (91, '缓存列表', 22, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:cache:query', '#', 'admin', '2026-01-27 18:21:55', '', NULL, '缓存列表');

-- 在线用户 23
INSERT INTO `sys_menu` VALUES (92, '强制下线', 23, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:online:force-logout', '', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:30:55', '强制用户下线');

-- 定时任务 24
INSERT INTO `sys_menu` VALUES (93, '任务查询', 24, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2026-02-06 20:01:06', '', NULL, '');
INSERT INTO `sys_menu` VALUES (94, '任务新增', 24, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2026-02-06 20:01:06', '', NULL, '');
INSERT INTO `sys_menu` VALUES (95, '任务修改', 24, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2026-02-06 20:01:06', '', NULL, '');
INSERT INTO `sys_menu` VALUES (96, '任务删除', 24, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:job:delete', '#', 'admin', '2026-02-06 20:01:06', '', NULL, '');

-- 文件管理 25
INSERT INTO `sys_menu` VALUES (97, '文件查询', 25, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'file:resource:query', '#', 'admin', '2026-06-27 15:56:30', '', NULL, '文件列表/详情/预览');
INSERT INTO `sys_menu` VALUES (98, '文件上传', 25, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'file:resource:add', '#', 'admin', '2026-06-27 15:56:30', '', NULL, '上传文件');
INSERT INTO `sys_menu` VALUES (99, '文件删除', 25, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'file:resource:delete', '#', 'admin', '2026-06-27 15:56:30', '', NULL, '逻辑删除');
INSERT INTO `sys_menu` VALUES (100, '文件恢复', 25, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'file:resource:restore', '#', 'admin', '2026-06-27 15:56:30', '', NULL, '回收站恢复');
INSERT INTO `sys_menu` VALUES (101, '文件夹查询', 25, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'file:folder:query', '#', 'admin', '2026-06-27 15:56:30', '', NULL, '文件夹树');
INSERT INTO `sys_menu` VALUES (102, '文件夹新增', 25, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'file:folder:add', '#', 'admin', '2026-06-27 15:56:30', '', NULL, '新建文件夹');
INSERT INTO `sys_menu` VALUES (103, '文件夹编辑', 25, 7, '', '', NULL, '', 1, 1, 'F', '0', '0', 'file:folder:edit', '#', 'admin', '2026-06-27 15:56:30', '', NULL, '重命名/排序');
INSERT INTO `sys_menu` VALUES (104, '文件夹删除', 25, 8, '', '', NULL, '', 1, 1, 'F', '0', '0', 'file:folder:delete', '#', 'admin', '2026-06-27 15:56:30', '', NULL, '删除文件夹');
INSERT INTO `sys_menu` VALUES (105, '文件迁移', 25, 9, '', '', NULL, '', 1, 1, 'F', '0', '0', 'file:resource:move', '#', 'admin', '2026-06-27 15:56:30', '', NULL, '迁移到其他文件夹');
INSERT INTO `sys_menu` VALUES (106, '文件重命名', 25, 10, '', '', NULL, '', 1, 1, 'F', '0', '0', 'file:resource:edit', '#', 'admin', '2026-06-27 15:56:30', '', NULL, '修改文件展示名称');

SET FOREIGN_KEY_CHECKS = 1;