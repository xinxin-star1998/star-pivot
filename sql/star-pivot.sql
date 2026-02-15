/*
 Navicat Premium Data Transfer

 Source Server         : xinxin
 Source Server Type    : MySQL
 Source Server Version : 50743
 Source Host           : localhost:3306
 Source Schema         : star-pivot

 Target Server Type    : MySQL
 Target Server Version : 50743
 File Encoding         : 65001

 Date: 15/02/2026 19:54:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`  (
  `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代码生成业务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table
-- ----------------------------
INSERT INTO `gen_table` VALUES (40, 'sys_config', '参数配置表', NULL, NULL, 'SysConfig', 'crud', 'element-ui', 'com.star.pivot.system', 'system', 'config', '参数配置', 'admin', '0', '/', '{\"treeCode\":\"\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuId\":1}', 'admin', '2026-02-05 17:10:22', '', '2026-02-06 11:26:41', '');
INSERT INTO `gen_table` VALUES (41, 'sys_dept', '部门表', NULL, NULL, 'SysDept', 'crud', 'art-design-pro', 'com.star.pivot.system', 'system', 'dept', '部门', 'admin', '0', '/', '{\"treeCode\":\"\",\"treeName\":\"\",\"treeParentCode\":\"\"}', 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:05:36', '');
INSERT INTO `gen_table` VALUES (42, 'sys_menu', '菜单权限表', NULL, NULL, 'SysMenu', 'crud', 'element-ui', 'com.star.pivot.system', 'system', 'menu', '菜单权限', 'admin', '0', '/', '{\"treeCode\":\"\",\"treeName\":\"\",\"treeParentCode\":\"\"}', 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24', '');
INSERT INTO `gen_table` VALUES (43, 'sys_notice', '通知公告表', NULL, NULL, 'SysNotice', 'crud', 'element-plus', 'com.star.pivot.system', 'system', 'notice', '通知公告', 'admin', '0', '/', '{\"treeCode\":\"\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuId\":1}', 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:21:12', '');
INSERT INTO `gen_table` VALUES (44, 'sys_post', '岗位信息表', NULL, NULL, 'SysPost', 'crud', 'element-ui', 'com.star.pivot.system', 'system', 'post', '岗位信息', 'admin', '0', '/', '{\"treeCode\":\"\",\"treeName\":\"\",\"treeParentCode\":\"\"}', 'admin', '2026-02-05 17:10:22', '', '2026-02-06 13:14:15', '');
INSERT INTO `gen_table` VALUES (45, 'sys_role', '角色信息表', NULL, NULL, 'SysRole', 'crud', '', 'com.star.pivot.system', 'system', 'role', '角色信息', 'admin', '0', '/', NULL, 'admin', '2026-02-05 17:10:22', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (46, 'sys_role_dept', '角色与部门关联表', NULL, NULL, 'SysRoleDept', 'crud', '', 'com.star.pivot.system', 'system', 'dept', '角色与部门关联', 'admin', '0', '/', NULL, 'admin', '2026-02-05 17:10:22', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (47, 'sys_role_menu', '角色与菜单关联表', NULL, NULL, 'SysRoleMenu', 'crud', '', 'com.star.pivot.system', 'system', 'menu', '角色与菜单关联', 'admin', '0', '/', NULL, 'admin', '2026-02-05 17:10:22', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (48, 'sys_user', '用户信息表', NULL, NULL, 'SysUser', 'crud', '', 'com.star.pivot.system', 'system', 'user', '用户信息', 'admin', '0', '/', NULL, 'admin', '2026-02-05 17:10:22', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (49, 'sys_user_post', '用户与岗位关联表', NULL, NULL, 'SysUserPost', 'crud', '', 'com.star.pivot.system', 'system', 'post', '用户与岗位关联', 'admin', '0', '/', NULL, 'admin', '2026-02-05 17:10:22', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (50, 'sys_user_role', '用户与角色关联表', NULL, NULL, 'SysUserRole', 'crud', '', 'com.star.pivot.system', 'system', 'role', '用户与角色关联', 'admin', '0', '/', NULL, 'admin', '2026-02-05 17:10:22', '', NULL, NULL);

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`  (
  `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典类型',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 494 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------
INSERT INTO `gen_table_column` VALUES (384, 40, 'config_id', '参数主键', 'int(5)', 'Long', 'configId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 21:13:59');
INSERT INTO `gen_table_column` VALUES (385, 40, 'config_name', '参数名称', 'varchar(100)', 'String', 'configName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 2, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 21:13:59');
INSERT INTO `gen_table_column` VALUES (386, 40, 'config_key', '参数键名', 'varchar(100)', 'String', 'configKey', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 21:13:59');
INSERT INTO `gen_table_column` VALUES (387, 40, 'config_value', '参数键值', 'varchar(500)', 'String', 'configValue', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'textarea', '', 4, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 21:13:59');
INSERT INTO `gen_table_column` VALUES (388, 40, 'config_type', '系统内置（Y是 N否）', 'char(1)', 'String', 'configType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', '', 5, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 21:13:59');
INSERT INTO `gen_table_column` VALUES (389, 40, 'create_by', '创建者', 'varchar(64)', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 6, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 21:13:59');
INSERT INTO `gen_table_column` VALUES (390, 40, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 7, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 21:13:59');
INSERT INTO `gen_table_column` VALUES (391, 40, 'update_by', '更新者', 'varchar(64)', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'input', '', 8, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 21:13:59');
INSERT INTO `gen_table_column` VALUES (392, 40, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'datetime', '', 9, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 21:13:59');
INSERT INTO `gen_table_column` VALUES (393, 40, 'remark', '备注', 'varchar(500)', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', 'textarea', '', 10, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 21:13:59');
INSERT INTO `gen_table_column` VALUES (394, 41, 'dept_id', '部门id', 'bigint(20)', 'Long', 'deptId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:05:36');
INSERT INTO `gen_table_column` VALUES (395, 41, 'parent_id', '父部门id', 'bigint(20)', 'Long', 'parentId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:05:36');
INSERT INTO `gen_table_column` VALUES (396, 41, 'ancestors', '祖级列表', 'varchar(50)', 'String', 'ancestors', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:05:36');
INSERT INTO `gen_table_column` VALUES (397, 41, 'dept_name', '部门名称', 'varchar(30)', 'String', 'deptName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:05:36');
INSERT INTO `gen_table_column` VALUES (398, 41, 'order_num', '显示顺序', 'int(4)', 'Integer', 'orderNum', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:05:36');
INSERT INTO `gen_table_column` VALUES (399, 41, 'leader', '负责人', 'varchar(20)', 'String', 'leader', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:05:36');
INSERT INTO `gen_table_column` VALUES (400, 41, 'phone', '联系电话', 'varchar(11)', 'String', 'phone', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 7, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:05:36');
INSERT INTO `gen_table_column` VALUES (401, 41, 'email', '邮箱', 'varchar(50)', 'String', 'email', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 8, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:05:36');
INSERT INTO `gen_table_column` VALUES (402, 41, 'status', '部门状态（0正常 1停用）', 'char(1)', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', '', 9, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:05:36');
INSERT INTO `gen_table_column` VALUES (403, 41, 'del_flag', '删除标志（0代表存在 2代表删除）', 'char(1)', 'String', 'delFlag', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 10, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:05:36');
INSERT INTO `gen_table_column` VALUES (404, 41, 'create_by', '创建者', 'varchar(64)', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 11, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:05:36');
INSERT INTO `gen_table_column` VALUES (405, 41, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 12, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:05:36');
INSERT INTO `gen_table_column` VALUES (406, 41, 'update_by', '更新者', 'varchar(64)', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'input', '', 13, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:05:36');
INSERT INTO `gen_table_column` VALUES (407, 41, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'datetime', '', 14, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:05:36');
INSERT INTO `gen_table_column` VALUES (408, 42, 'menu_id', '菜单ID', 'bigint(20)', 'Long', 'menuId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (409, 42, 'menu_name', '菜单名称', 'varchar(50)', 'String', 'menuName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', 'input', '', 2, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (410, 42, 'parent_id', '父菜单ID', 'bigint(20)', 'Long', 'parentId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (411, 42, 'order_num', '显示顺序', 'int(4)', 'Integer', 'orderNum', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (412, 42, 'path', '路由地址', 'varchar(200)', 'String', 'path', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (413, 42, 'component', '组件路径', 'varchar(255)', 'String', 'component', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (414, 42, 'query', '路由参数', 'varchar(255)', 'String', 'query', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 7, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (415, 42, 'route_name', '路由名称', 'varchar(50)', 'String', 'routeName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 8, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (416, 42, 'is_frame', '是否为外链（0是 1否）', 'int(1)', 'Integer', 'isFrame', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 9, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (417, 42, 'is_cache', '是否缓存（0缓存 1不缓存）', 'int(1)', 'Integer', 'isCache', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 10, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (418, 42, 'menu_type', '菜单类型（M目录 C菜单 F按钮）', 'char(1)', 'String', 'menuType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', '', 11, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (419, 42, 'visible', '菜单状态（0显示 1隐藏）', 'char(1)', 'String', 'visible', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 12, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (420, 42, 'status', '菜单状态（0正常 1停用）', 'char(1)', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', '', 13, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (421, 42, 'perms', '权限标识', 'varchar(100)', 'String', 'perms', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 14, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (422, 42, 'icon', '菜单图标', 'varchar(100)', 'String', 'icon', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 15, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (423, 42, 'create_by', '创建者', 'varchar(64)', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 16, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (424, 42, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 17, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (425, 42, 'update_by', '更新者', 'varchar(64)', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'input', '', 18, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (426, 42, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'datetime', '', 19, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (427, 42, 'remark', '备注', 'varchar(500)', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', 'textarea', '', 20, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:10:24');
INSERT INTO `gen_table_column` VALUES (428, 43, 'notice_id', '公告ID', 'int(4)', 'Integer', 'noticeId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:21:12');
INSERT INTO `gen_table_column` VALUES (429, 43, 'notice_title', '公告标题', 'varchar(50)', 'String', 'noticeTitle', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:21:12');
INSERT INTO `gen_table_column` VALUES (430, 43, 'notice_type', '公告类型（1通知 2公告）', 'char(1)', 'String', 'noticeType', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', 'sys_notice_type', 3, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:21:12');
INSERT INTO `gen_table_column` VALUES (431, 43, 'notice_content', '公告内容', 'longblob', 'String', 'noticeContent', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'editor', '', 4, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:21:12');
INSERT INTO `gen_table_column` VALUES (432, 43, 'status', '公告状态（0正常 1关闭）', 'char(1)', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', 'sys_notice_status', 5, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:21:12');
INSERT INTO `gen_table_column` VALUES (433, 43, 'create_by', '创建者', 'varchar(64)', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 6, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:21:12');
INSERT INTO `gen_table_column` VALUES (434, 43, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 7, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:21:12');
INSERT INTO `gen_table_column` VALUES (435, 43, 'update_by', '更新者', 'varchar(64)', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'input', '', 8, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:21:12');
INSERT INTO `gen_table_column` VALUES (436, 43, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'datetime', '', 9, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:21:12');
INSERT INTO `gen_table_column` VALUES (437, 43, 'remark', '备注', 'varchar(255)', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', 'input', '', 10, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 12:21:12');
INSERT INTO `gen_table_column` VALUES (438, 44, 'post_id', '岗位ID', 'bigint(20)', 'Long', 'postId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 13:14:15');
INSERT INTO `gen_table_column` VALUES (439, 44, 'post_code', '岗位编码', 'varchar(64)', 'String', 'postCode', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 13:14:15');
INSERT INTO `gen_table_column` VALUES (440, 44, 'post_name', '岗位名称', 'varchar(50)', 'String', 'postName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', 'input', '', 3, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 13:14:15');
INSERT INTO `gen_table_column` VALUES (441, 44, 'post_sort', '显示顺序', 'int(4)', 'Integer', 'postSort', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 13:14:15');
INSERT INTO `gen_table_column` VALUES (442, 44, 'status', '状态（0正常 1停用）', 'char(1)', 'String', 'status', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'radio', '', 5, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 13:14:15');
INSERT INTO `gen_table_column` VALUES (443, 44, 'create_by', '创建者', 'varchar(64)', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 6, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 13:14:15');
INSERT INTO `gen_table_column` VALUES (444, 44, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 7, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 13:14:15');
INSERT INTO `gen_table_column` VALUES (445, 44, 'update_by', '更新者', 'varchar(64)', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'input', '', 8, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 13:14:15');
INSERT INTO `gen_table_column` VALUES (446, 44, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'datetime', '', 9, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 13:14:15');
INSERT INTO `gen_table_column` VALUES (447, 44, 'remark', '备注', 'varchar(500)', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', 'textarea', '', 10, 'admin', '2026-02-05 17:10:22', '', '2026-02-06 13:14:15');
INSERT INTO `gen_table_column` VALUES (448, 45, 'role_id', '角色ID', 'bigint(20)', 'Long', 'roleId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (449, 45, 'role_name', '角色名称', 'varchar(30)', 'String', 'roleName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', 'input', '', 2, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (450, 45, 'role_key', '角色权限字符串', 'varchar(100)', 'String', 'roleKey', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (451, 45, 'role_sort', '显示顺序', 'int(4)', 'Integer', 'roleSort', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (452, 45, 'data_scope', '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）', 'char(1)', 'String', 'dataScope', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (453, 45, 'menu_check_strictly', '菜单树选择项是否关联显示', 'tinyint(1)', 'Integer', 'menuCheckStrictly', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (454, 45, 'dept_check_strictly', '部门树选择项是否关联显示', 'tinyint(1)', 'Integer', 'deptCheckStrictly', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 7, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (455, 45, 'status', '角色状态（0正常 1停用）', 'char(1)', 'String', 'status', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'radio', '', 8, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (456, 45, 'del_flag', '删除标志（0代表存在 2代表删除）', 'char(1)', 'String', 'delFlag', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 9, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (457, 45, 'create_by', '创建者', 'varchar(64)', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 10, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (458, 45, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 11, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (459, 45, 'update_by', '更新者', 'varchar(64)', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'input', '', 12, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (460, 45, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'datetime', '', 13, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (461, 45, 'remark', '备注', 'varchar(500)', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', 'textarea', '', 14, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (462, 46, 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (463, 46, 'role_id', '角色ID', 'bigint(20)', 'Long', 'roleId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (464, 46, 'dept_id', '部门ID', 'bigint(20)', 'Long', 'deptId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (465, 47, 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (466, 47, 'role_id', '角色ID', 'bigint(20)', 'Long', 'roleId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (467, 47, 'menu_id', '菜单ID', 'bigint(20)', 'Long', 'menuId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (468, 48, 'user_id', '用户ID', 'bigint(20)', 'Long', 'userId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (469, 48, 'dept_id', '部门ID', 'bigint(20)', 'Long', 'deptId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (470, 48, 'user_name', '用户账号', 'varchar(30)', 'String', 'userName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', 'input', '', 3, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (471, 48, 'nick_name', '用户昵称', 'varchar(30)', 'String', 'nickName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (472, 48, 'user_type', '用户类型（00系统用户）', 'varchar(2)', 'String', 'userType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', '', 5, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (473, 48, 'email', '用户邮箱', 'varchar(50)', 'String', 'email', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (474, 48, 'phonenumber', '手机号码', 'varchar(11)', 'String', 'phonenumber', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 7, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (475, 48, 'sex', '用户性别（0男 1女 2未知）', 'char(1)', 'String', 'sex', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', '', 8, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (476, 48, 'avatar', '头像地址', 'varchar(500)', 'String', 'avatar', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'textarea', '', 9, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (477, 48, 'password', '密码', 'varchar(100)', 'String', 'password', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 10, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (478, 48, 'status', '账号状态（0正常 1停用）', 'char(1)', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', '', 11, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (479, 48, 'del_flag', '删除标志（0代表存在 2代表删除）', 'char(1)', 'String', 'delFlag', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 12, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (480, 48, 'login_ip', '最后登录IP', 'varchar(128)', 'String', 'loginIp', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 13, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (481, 48, 'login_date', '最后登录时间', 'datetime', 'Date', 'loginDate', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'datetime', '', 14, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (482, 48, 'pwd_update_date', '密码最后更新时间', 'datetime', 'Date', 'pwdUpdateDate', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'datetime', '', 15, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (483, 48, 'create_by', '创建者', 'varchar(64)', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 16, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (484, 48, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 17, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (485, 48, 'update_by', '更新者', 'varchar(64)', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'input', '', 18, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (486, 48, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'datetime', '', 19, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (487, 48, 'remark', '备注', 'varchar(500)', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', 'textarea', '', 20, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (488, 49, 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (489, 49, 'user_id', '用户ID', 'bigint(20)', 'Long', 'userId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (490, 49, 'post_id', '岗位ID', 'bigint(20)', 'Long', 'postId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (491, 50, 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (492, 50, 'user_id', '用户ID', 'bigint(20)', 'Long', 'userId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (493, 50, 'role_id', '角色ID', 'bigint(20)', 'Long', 'roleId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-02-05 17:10:22', '', NULL);

-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob NULL COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Blob类型的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_blob_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`, `calendar_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '日历信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_calendars
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Cron类型的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_cron_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint(20) NOT NULL COMMENT '触发的时间',
  `sched_time` bigint(20) NOT NULL COMMENT '定时器制定的时间',
  `priority` int(11) NOT NULL COMMENT '优先级',
  `state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`, `entry_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '已触发的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_fired_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务组名',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob NULL COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '任务详细信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_job_details
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`, `lock_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '存储的悲观锁信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_locks
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`, `trigger_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '暂停的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_paused_trigger_grps
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint(20) NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint(20) NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`, `instance_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '调度器状态表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_scheduler_state
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint(20) NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint(20) NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint(20) NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '简单触发器的信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_simple_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int(11) NULL DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int(11) NULL DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint(20) NULL DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint(20) NULL DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13, 4) NULL DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13, 4) NULL DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '同步机制的行锁表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_simprop_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint(20) NULL DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint(20) NULL DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int(11) NULL DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发器的类型',
  `start_time` bigint(20) NOT NULL COMMENT '开始时间',
  `end_time` bigint(20) NULL DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint(6) NULL DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob NULL COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  INDEX `sched_name`(`sched_name`, `job_name`, `job_group`) USING BTREE,
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '触发器详细信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2026-01-25 17:41:36', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2026-01-25 17:41:36', '', NULL, '初始化密码 123456');
INSERT INTO `sys_config` VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2026-01-25 17:41:36', '', NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO `sys_config` VALUES (4, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'true', 'Y', 'admin', '2026-01-25 17:41:36', '', NULL, '是否开启验证码功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', '2026-01-25 17:41:36', '', NULL, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (6, '用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', 'admin', '2026-01-25 17:41:36', '', NULL, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');
INSERT INTO `sys_config` VALUES (7, '用户管理-初始密码修改策略', 'sys.account.initPasswordModify', '1', 'Y', 'admin', '2026-01-25 17:41:36', '', NULL, '0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框');
INSERT INTO `sys_config` VALUES (8, '用户管理-账号密码更新周期', 'sys.account.passwordValidateDays', '0', 'Y', 'admin', '2026-01-25 17:41:36', '', NULL, '密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父部门id',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int(11) NULL DEFAULT 0 COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 112 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (100, 0, '0', '星枢科技', 0, '星枢', '18834581124', '	\r\n18834581124@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', 'admin', '2025-12-29 20:57:13');
INSERT INTO `sys_dept` VALUES (101, 100, '0,100', '深圳总公司', 1, '星枢', '18834581124', '	\r\n18834581124@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', '', NULL);
INSERT INTO `sys_dept` VALUES (102, 100, '0,100', '长沙分公司', 2, '星枢', '18834581124', '	\r\n	\r\n18834581124@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', '', NULL);
INSERT INTO `sys_dept` VALUES (103, 101, '0,100,101', '研发部门', 1, '星枢', '18834581124', '	\r\n18834581124@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', '', NULL);
INSERT INTO `sys_dept` VALUES (104, 101, '0,100,101', '市场部门', 2, '星枢', '18834581124', '	\r\n18834581124@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', '', NULL);
INSERT INTO `sys_dept` VALUES (105, 101, '0,100,101', '测试部门', 3, '星枢', '18834581124', '	\r\n18834581124@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', '', NULL);
INSERT INTO `sys_dept` VALUES (106, 101, '0,100,101', '财务部门', 4, '星枢', '18834581124', '	\r\n18834581124@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', '', NULL);
INSERT INTO `sys_dept` VALUES (107, 101, '0,100,101', '运维部门', 5, '星枢', '18834581124', '	\r\n18834581124@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', '', NULL);
INSERT INTO `sys_dept` VALUES (108, 102, '0,100,102', '市场部门', 1, '星枢', '18834581124', '18834581124@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', '', NULL);
INSERT INTO `sys_dept` VALUES (109, 102, '0,100,102', '财务部门', 2, '星枢', '18834581124', '	\r\n18834581124@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', '', NULL);
INSERT INTO `sys_dept` VALUES (110, 100, '0,100', '山西分公司', 3, '星枢', '18834581124', '18834581124@163.com', '0', '0', 'admin', '2026-01-20 12:52:02', 'admin', '2026-01-20 12:53:11');
INSERT INTO `sys_dept` VALUES (111, 110, '0,100,110', '人事部', 1, '星枢', '18834581124', '18834581124@163.com', '0', '0', 'admin', '2026-01-20 12:52:49', 'admin', '2026-01-20 12:53:15');

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int(11) NULL DEFAULT 0 COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 102 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '性别男');
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '性别女');
INSERT INTO `sys_dict_data` VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '性别未知');
INSERT INTO `sys_dict_data` VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '显示菜单');
INSERT INTO `sys_dict_data` VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '隐藏菜单');
INSERT INTO `sys_dict_data` VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '默认分组');
INSERT INTO `sys_dict_data` VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '系统分组');
INSERT INTO `sys_dict_data` VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '系统默认是');
INSERT INTO `sys_dict_data` VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '系统默认否');
INSERT INTO `sys_dict_data` VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '通知');
INSERT INTO `sys_dict_data` VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '公告');
INSERT INTO `sys_dict_data` VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '关闭状态');
INSERT INTO `sys_dict_data` VALUES (18, 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '其他操作');
INSERT INTO `sys_dict_data` VALUES (19, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '新增操作');
INSERT INTO `sys_dict_data` VALUES (20, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '修改操作');
INSERT INTO `sys_dict_data` VALUES (21, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '删除操作');
INSERT INTO `sys_dict_data` VALUES (22, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '授权操作');
INSERT INTO `sys_dict_data` VALUES (23, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '导出操作');
INSERT INTO `sys_dict_data` VALUES (24, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '导入操作');
INSERT INTO `sys_dict_data` VALUES (25, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '强退操作');
INSERT INTO `sys_dict_data` VALUES (26, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '生成操作');
INSERT INTO `sys_dict_data` VALUES (27, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '清空操作');
INSERT INTO `sys_dict_data` VALUES (28, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (29, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (100, 1, '存在', '0', 'is_delete_status', 'success', 'success', 'N', '0', 'admin', '2026-01-12 15:59:08', '', NULL, '存在');
INSERT INTO `sys_dict_data` VALUES (101, 2, '删除', '2', 'is_delete_status', 'primary', 'default', 'N', '0', 'admin', '2026-01-12 16:02:57', '', NULL, '删除');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '系统开关列表');
INSERT INTO `sys_dict_type` VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '任务状态列表');
INSERT INTO `sys_dict_type` VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '任务分组列表');
INSERT INTO `sys_dict_type` VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '系统是否列表');
INSERT INTO `sys_dict_type` VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '通知类型列表');
INSERT INTO `sys_dict_type` VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '通知状态列表');
INSERT INTO `sys_dict_type` VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '操作类型列表');
INSERT INTO `sys_dict_type` VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2025-12-28 13:46:34', '', NULL, '登录状态列表');
INSERT INTO `sys_dict_type` VALUES (100, '删除标识', 'is_delete_status', '0', 'admin', '2026-01-12 15:57:49', '', NULL, '删除标识：0未删除 2 删除');

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`  (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务调度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job` VALUES (1, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '0 0 2 * * ?', '3', '1', '0', 'admin', '2026-02-06 20:41:50', 'admin', '2026-02-07 14:02:58', '每天凌晨2点清空操作日志表 sys_oper_log');

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`  (
  `job_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日志信息',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '异常信息',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务调度日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------
INSERT INTO `sys_job_log` VALUES (3, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '执行成功', '0', '', '2026-02-07 02:00:00');
INSERT INTO `sys_job_log` VALUES (4, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '执行成功', '0', '', '2026-02-07 14:03:19');
INSERT INTO `sys_job_log` VALUES (5, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '执行成功', '0', '', '2026-02-08 02:00:00');
INSERT INTO `sys_job_log` VALUES (6, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '执行成功', '0', '', '2026-02-09 02:00:00');
INSERT INTO `sys_job_log` VALUES (7, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '执行成功', '0', '', '2026-02-10 02:00:00');
INSERT INTO `sys_job_log` VALUES (8, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '执行成功', '0', '', '2026-02-11 02:00:00');
INSERT INTO `sys_job_log` VALUES (9, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '执行成功', '0', '', '2026-02-12 02:00:00');

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
  `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作系统',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '提示消息',
  `login_time` datetime(0) NULL DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE,
  INDEX `idx_sys_logininfor_s`(`status`) USING BTREE,
  INDEX `idx_sys_logininfor_lt`(`login_time`) USING BTREE,
  INDEX `idx_login_time`(`login_time`) USING BTREE,
  INDEX `idx_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 178 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES (176, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10', '0', '登录成功', '2026-02-15 16:47:59');
INSERT INTO `sys_logininfor` VALUES (177, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10', '0', '登录成功', '2026-02-15 17:48:01');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int(11) NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由名称',
  `is_frame` int(11) NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int(11) NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
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
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 132 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 1, '/system', '/index/index', NULL, 'System', 1, 1, 'M', '0', '0', NULL, 'uil:setting', 'system', '2025-12-31 16:34:16', '', '2026-01-02 21:36:28', '系统管理模块');
INSERT INTO `sys_menu` VALUES (4, '菜单管理', 1, 1, 'menu', '/system/menu', NULL, 'SystemMenu', 1, 1, 'C', '0', '0', 'system:menu:list', 'ep:menu', 'system', '2025-12-31 16:34:16', '', '2026-01-02 21:12:33', '菜单管理模块');
INSERT INTO `sys_menu` VALUES (5, '角色管理', 1, 2, 'role', '/system/role', NULL, 'SystemRole', 1, 1, 'C', '0', '0', 'system:role:list', 'oui:app-users-roles', 'system', '2025-12-31 16:34:16', '', '2026-01-02 21:30:56', '角色管理模块');
INSERT INTO `sys_menu` VALUES (6, '用户管理', 1, 3, 'user', '/system/user', NULL, 'SystemUser', 1, 1, 'C', '0', '0', 'system:user:list', 'mdi:user', 'system', '2025-12-31 16:34:16', '', '2026-01-02 21:31:51', '用户管理模块');
INSERT INTO `sys_menu` VALUES (8, '部门管理', 1, 4, 'dept', '/system/dept', NULL, 'SystemDept', 1, 1, 'C', '0', '0', 'system:dept:list', 'ri:organization-chart', '', '2026-01-02 21:04:34', '', '2026-01-02 21:36:43', '部门管理模块');
INSERT INTO `sys_menu` VALUES (9, '岗位管理', 1, 5, 'post', '/system/post/index', NULL, 'PostManage', 1, 1, 'C', '0', '0', 'system:post:list', 'picon:post', 'xinxin', '2026-01-04 19:23:51', 'xinxin', '2026-01-04 19:25:02', '岗位管理模块');
INSERT INTO `sys_menu` VALUES (12, '字典管理', 1, 6, 'dict', '/system/dict/index', NULL, 'DictManage', 1, 1, 'C', '0', '0', 'system:type:list', 'arcticons:zdict', 'admin', '2026-01-05 12:28:54', 'admin', '2026-01-19 21:37:20', '字典管理模块。有：字典数据   字典类型');
INSERT INTO `sys_menu` VALUES (39, '新增用户', 6, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:add', '#', 'admin', '2026-01-12 16:42:30', 'admin', '2026-01-16 20:42:35', '新增用户按钮');
INSERT INTO `sys_menu` VALUES (40, '修改用户', 6, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2026-01-12 17:30:37', 'admin', '2026-01-16 20:42:26', '修改用户  按钮');
INSERT INTO `sys_menu` VALUES (41, '删除用户', 6, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:delete', '#', 'admin', '2026-01-12 17:31:11', 'admin', '2026-01-16 20:42:39', '删除用户按钮');
INSERT INTO `sys_menu` VALUES (42, '新增角色', 5, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:add', '#', 'admin', '2026-01-12 17:32:13', '', NULL, '新增角色按钮');
INSERT INTO `sys_menu` VALUES (43, '修改角色', 5, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2026-01-12 17:32:45', '', NULL, '修改角色按钮');
INSERT INTO `sys_menu` VALUES (44, '删除角色', 5, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:delete', '#', 'admin', '2026-01-12 17:33:14', '', NULL, '删除角色按钮');
INSERT INTO `sys_menu` VALUES (45, '角色查询', 5, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:query', '#', 'admin', '2026-01-12 17:34:57', '', NULL, '角色查询 按钮');
INSERT INTO `sys_menu` VALUES (46, '用户查询', 6, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:query', '#', 'admin', '2026-01-12 17:35:27', 'admin', '2026-01-16 20:42:43', '用户查询按钮');
INSERT INTO `sys_menu` VALUES (47, '部门查询', 8, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:dept:query', '#', 'admin', '2026-01-12 17:35:58', '', NULL, '部门查询按钮');
INSERT INTO `sys_menu` VALUES (48, '部门新增', 8, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:dept:add', '#', 'admin', '2026-01-12 17:36:28', '', NULL, '部门新增');
INSERT INTO `sys_menu` VALUES (49, '部门修改', 8, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2026-01-12 17:36:49', '', NULL, '部门修改');
INSERT INTO `sys_menu` VALUES (50, '部门删除', 8, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:dept:delete', '#', 'admin', '2026-01-12 17:37:21', '', NULL, '部门删除');
INSERT INTO `sys_menu` VALUES (51, '岗位查询', 9, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:post:query', '#', 'admin', '2026-01-12 17:37:47', '', NULL, '岗位查询');
INSERT INTO `sys_menu` VALUES (52, '岗位新增', 9, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:post:add', '#', 'admin', '2026-01-12 17:38:09', '', NULL, '岗位新增');
INSERT INTO `sys_menu` VALUES (53, '岗位修改', 9, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:post:edit', '#', 'admin', '2026-01-12 17:38:46', '', NULL, '岗位修改');
INSERT INTO `sys_menu` VALUES (54, '岗位删除', 9, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:post:delete', '#', 'admin', '2026-01-12 17:39:04', '', NULL, '岗位删除');
INSERT INTO `sys_menu` VALUES (55, '菜单查询', 4, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2026-01-12 17:39:26', '', NULL, '菜单查询');
INSERT INTO `sys_menu` VALUES (56, '菜单添加', 4, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2026-01-12 17:39:50', '', NULL, '菜单添加');
INSERT INTO `sys_menu` VALUES (57, '菜单修改', 4, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2026-01-12 17:40:05', '', NULL, '菜单修改');
INSERT INTO `sys_menu` VALUES (58, '菜单删除', 4, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:menu:delete', '#', 'admin', '2026-01-12 17:40:27', '', NULL, '菜单删除');
INSERT INTO `sys_menu` VALUES (59, '修改密码', 6, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2026-01-16 18:10:55', 'admin', '2026-01-16 20:42:48', '修改密码');
INSERT INTO `sys_menu` VALUES (60, '修改状态', 6, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:changeStatus', '#', 'admin', '2026-01-16 18:11:22', 'admin', '2026-01-16 20:42:31', '修改状态');
INSERT INTO `sys_menu` VALUES (61, '分配数据权限', 5, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:assignDataScope', '#', 'admin', '2026-01-16 19:04:22', 'admin', '2026-01-23 20:57:15', '分配角色');
INSERT INTO `sys_menu` VALUES (62, '字典类型添加', 12, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:type:add', '#', 'admin', '2026-01-16 19:08:40', 'admin', '2026-01-16 19:33:35', '字典类型添加');
INSERT INTO `sys_menu` VALUES (63, '字典类型修改', 12, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:type:edit', '#', 'admin', '2026-01-16 19:09:04', 'admin', '2026-01-16 19:33:43', '字典类型修改');
INSERT INTO `sys_menu` VALUES (64, '字典类型删除', 12, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:type:delete', '#', 'admin', '2026-01-16 19:09:27', 'admin', '2026-01-16 19:33:48', '字典类型删除');
INSERT INTO `sys_menu` VALUES (66, '字典数据添加', 12, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:data:add', '#', 'admin', '2026-01-16 19:31:42', '', NULL, '字典数据添加');
INSERT INTO `sys_menu` VALUES (67, '字典数据修改', 12, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:data:edit', '#', 'admin', '2026-01-16 19:32:19', '', NULL, '字典数据修改');
INSERT INTO `sys_menu` VALUES (68, '字典数据删除', 12, 7, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:data:delete', '#', 'admin', '2026-01-16 19:32:51', '', NULL, '字典数据删除');
INSERT INTO `sys_menu` VALUES (69, '字典类型查询', 12, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:type:query', '#', 'admin', '2026-01-19 21:33:21', '', NULL, '字典类型查询');
INSERT INTO `sys_menu` VALUES (70, '字典数据查询', 12, 8, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:data:query', '#', 'admin', '2026-01-19 21:33:59', '', NULL, '字典数据查询');
INSERT INTO `sys_menu` VALUES (71, '系统工具', 0, 2, '/tools', '', NULL, 'SystemTools', 1, 1, 'M', '0', '0', '', 'clarity:tools-line', 'admin', '2026-01-20 13:08:43', '', NULL, '系统工具');
INSERT INTO `sys_menu` VALUES (72, '代码生成', 71, 1, 'generator', '/tools/generator/index', NULL, 'GenerateTools', 1, 1, 'C', '0', '0', 'tools:generator:list', 'mdi:generator-mobile', 'admin', '2026-01-20 13:15:59', 'admin', '2026-01-20 13:25:42', '代码生成');
INSERT INTO `sys_menu` VALUES (73, '列表查询', 72, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2026-01-20 14:56:43', '', NULL, '列表查询');
INSERT INTO `sys_menu` VALUES (74, '日志管理', 1, 8, 'log', '', NULL, 'LogManager', 1, 1, 'M', '0', '0', '', 'mdi:math-log', 'admin', '2026-01-23 13:37:05', 'admin', '2026-01-23 13:47:13', '');
INSERT INTO `sys_menu` VALUES (75, '操作日志', 74, 1, 'oper', '/system/log/oper/index', NULL, 'OperLog', 1, 1, 'C', '0', '0', 'system:log:list', 'icon-park-solid:log', 'admin', '2026-01-23 13:40:41', '', NULL, '操作日志');
INSERT INTO `sys_menu` VALUES (76, '登录日志', 74, 2, 'login', '/system/log/login/index', NULL, 'LoginInfoLog', 1, 1, 'C', '0', '0', 'system:login:list', 'icon-park-solid:log', 'admin', '2026-01-23 13:51:37', '', NULL, '登录日志');
INSERT INTO `sys_menu` VALUES (77, '清空日志', 75, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:operlog:delete', '#', 'admin', '2026-01-23 13:57:43', '', NULL, '清空日志');
INSERT INTO `sys_menu` VALUES (78, '日志查询', 75, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:operlog:query', '#', 'admin', '2026-01-23 13:58:02', '', NULL, '日志查询');
INSERT INTO `sys_menu` VALUES (79, '日志查询', 76, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:logininfor:query', '#', 'admin', '2026-01-23 14:24:26', '', NULL, '日志查询');
INSERT INTO `sys_menu` VALUES (80, '日志删除', 76, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:logininfor:delete', '#', 'admin', '2026-01-23 14:24:41', '', NULL, '日志删除');
INSERT INTO `sys_menu` VALUES (81, '预览', 72, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2026-01-23 15:53:25', 'admin', '2026-01-23 19:01:40', '预览');
INSERT INTO `sys_menu` VALUES (82, '编辑', 72, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2026-01-23 15:53:47', 'admin', '2026-01-23 19:01:44', '');
INSERT INTO `sys_menu` VALUES (83, '删除', 72, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:delete', '#', 'admin', '2026-01-23 15:54:05', 'admin', '2026-01-23 19:01:48', '');
INSERT INTO `sys_menu` VALUES (84, '同步数据库', 72, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:sync', '#', 'admin', '2026-01-23 15:55:18', 'admin', '2026-01-23 19:01:52', '同步数据库');
INSERT INTO `sys_menu` VALUES (85, '生成代码', 72, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:create', '#', 'admin', '2026-01-23 15:55:59', 'admin', '2026-01-23 19:01:56', '生成代码\n');
INSERT INTO `sys_menu` VALUES (86, '添加', 72, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:add', '#', 'admin', '2026-01-23 15:56:17', 'admin', '2026-01-23 19:01:31', '添加');
INSERT INTO `sys_menu` VALUES (87, '解除锁定', 6, 7, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:unLock', '#', 'admin', '2026-01-23 18:49:56', '', NULL, '解除锁定');
INSERT INTO `sys_menu` VALUES (88, '已分配用户', 5, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:allocatedList', '#', 'admin', '2026-01-23 20:55:10', 'admin', '2026-01-23 22:41:41', '已分配用户');
INSERT INTO `sys_menu` VALUES (89, '未分配用户', 5, 7, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:unallocatedList', '#', 'admin', '2026-01-23 22:41:08', '', NULL, '未分配用户');
INSERT INTO `sys_menu` VALUES (90, '分配用户', 5, 7, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:assignUser', '#', 'admin', '2026-01-23 23:18:49', '', NULL, '分配用户：添加用户角色关联表');
INSERT INTO `sys_menu` VALUES (91, '取消授权', 5, 9, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:cancelUser', '#', 'admin', '2026-01-23 23:26:23', '', NULL, '取消授权');
INSERT INTO `sys_menu` VALUES (92, '导入', 72, 8, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:import', '#', 'xinxin', '2026-01-24 00:08:41', '', NULL, '导入');
INSERT INTO `sys_menu` VALUES (93, '用户导入', 6, 8, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:import', '#', 'admin', '2026-01-24 21:03:18', '', NULL, '用户导入');
INSERT INTO `sys_menu` VALUES (94, '用户导出', 6, 9, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:export', '#', 'admin', '2026-01-24 21:03:52', '', NULL, '用户导出');
INSERT INTO `sys_menu` VALUES (95, '系统监控', 0, 3, '/monitor', '', NULL, 'Monitor', 1, 0, 'M', '0', '0', '', 'material-symbols:monitor-outline', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 19:11:56', '系统监控模块');
INSERT INTO `sys_menu` VALUES (96, '服务器监控', 95, 1, 'server', '/monitor/server/index', NULL, 'ServerMonitor', 1, 0, 'C', '0', '0', 'monitor:server:query', 'ri:server-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:33:45', '服务器信息监控');
INSERT INTO `sys_menu` VALUES (97, 'Druid监控', 95, 2, 'druid', '/monitor/druid/index', NULL, 'DruidMonitor', 1, 0, 'C', '0', '0', 'monitor:druid:query', 'ri:database-2-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:32:03', 'Druid数据库连接池监控');
INSERT INTO `sys_menu` VALUES (98, 'Redis监控', 95, 3, 'redis', '/monitor/redis/index', NULL, 'RedisMonitor', 1, 0, 'C', '0', '0', 'monitor:redis:query', 'ri:database-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:32:23', 'Redis缓存监控');
INSERT INTO `sys_menu` VALUES (99, '在线用户', 95, 4, 'online', '/monitor/online/index', NULL, 'OnlineUser', 1, 0, 'C', '0', '0', 'monitor:online:query', 'ri:user-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:32:45', '在线用户监控');
INSERT INTO `sys_menu` VALUES (100, '强制下线', 99, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:online:force-logout', '', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:30:55', '强制用户下线');
INSERT INTO `sys_menu` VALUES (107, 'API性能监控', 95, 7, 'api', '/monitor/api', NULL, 'ApiPerformance', 1, 1, 'C', '0', '0', 'monitor:api:query', 'tabler:api', 'admin', '2026-01-25 20:54:00', 'admin', '2026-02-06 20:06:52', '');
INSERT INTO `sys_menu` VALUES (114, '删除缓存', 98, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:cache:remove', '#', 'admin', '2026-01-25 22:19:38', '', NULL, 'monitor:cache:remove');
INSERT INTO `sys_menu` VALUES (115, '清空缓存', 98, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:cache:clear', '#', 'admin', '2026-01-25 22:20:00', '', NULL, '清空缓存');
INSERT INTO `sys_menu` VALUES (116, '健康检查', 96, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:health:query', '#', 'admin', '2026-01-25 22:29:27', '', NULL, '健康检查');
INSERT INTO `sys_menu` VALUES (117, '缓存列表', 98, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:cache:query', '#', 'admin', '2026-01-27 18:21:55', '', NULL, '缓存列表');
INSERT INTO `sys_menu` VALUES (118, '通知公告', 1, 8, 'notice', '/system/notice/index', NULL, 'NoticeManage', 1, 0, 'C', '0', '0', 'system:notice:list', 'fe:notice-active', 'admin', '2026-02-05 17:38:35', 'admin', '2026-02-05 17:56:17', '通知公告菜单');
INSERT INTO `sys_menu` VALUES (119, '通知公告查询', 118, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (120, '通知公告新增', 118, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (121, '通知公告修改', 118, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (122, '通知公告删除', 118, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:delete', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (123, '通知公告导出', 118, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:export', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (124, '定时任务', 95, 5, 'job', '/monitor/job/index', NULL, 'MonitorJob', 1, 1, 'C', '0', '0', 'monitor:job:query', 'ri:time-line', 'admin', '2026-02-06 19:58:43', 'admin', '2026-02-06 20:34:11', '定时任务调度');
INSERT INTO `sys_menu` VALUES (125, '任务查询', 124, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2026-02-06 20:01:06', '', NULL, '');
INSERT INTO `sys_menu` VALUES (126, '任务新增', 124, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2026-02-06 20:01:06', '', NULL, '');
INSERT INTO `sys_menu` VALUES (127, '任务修改', 124, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2026-02-06 20:01:06', '', NULL, '');
INSERT INTO `sys_menu` VALUES (128, '任务删除', 124, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:job:delete', '#', 'admin', '2026-02-06 20:01:06', '', NULL, '');
INSERT INTO `sys_menu` VALUES (129, '流程管理', 0, 4, '/workflow', '', NULL, 'WorkflowManager', 1, 1, 'M', '0', '0', '', 'ri:settings-line', 'admin', '2026-02-15 16:49:59', 'admin', '2026-02-15 16:55:35', '');
INSERT INTO `sys_menu` VALUES (130, '审批管理', 129, 1, 'approval', '/workflow/approval/index', NULL, 'ApprovalManager', 1, 1, 'C', '0', '0', '', '#', 'admin', '2026-02-15 16:54:33', '', NULL, '');
INSERT INTO `sys_menu` VALUES (131, '流程图', 129, 2, 'flowchart', '/workflow/flowchart/index', NULL, 'Flowchart', 1, 1, 'C', '0', '0', '', 'ri:file-line', 'admin', '2026-02-15 18:55:36', '', NULL, '');

-- ----------------------------
-- Table structure for sys_monitor_api_performance
-- ----------------------------
DROP TABLE IF EXISTS `sys_monitor_api_performance`;
CREATE TABLE `sys_monitor_api_performance`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `api_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '接口路径',
  `api_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求方法（GET, POST等）',
  `request_count` bigint(20) NULL DEFAULT 0 COMMENT '请求次数',
  `success_count` bigint(20) NULL DEFAULT 0 COMMENT '成功次数',
  `error_count` bigint(20) NULL DEFAULT 0 COMMENT '错误次数',
  `response_time_total` bigint(20) NULL DEFAULT 0 COMMENT '总响应时间（毫秒）',
  `response_time_max` bigint(20) NULL DEFAULT 0 COMMENT '最大响应时间（毫秒）',
  `response_time_min` bigint(20) NULL DEFAULT 0 COMMENT '最小响应时间（毫秒）',
  `response_time_avg` decimal(20, 2) NULL DEFAULT NULL COMMENT '平均响应时间（毫秒）',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `stat_hour` tinyint(4) NULL DEFAULT NULL COMMENT '统计小时（0-23，用于按小时统计）',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_api_stat`(`api_path`, `api_method`, `stat_date`, `stat_hour`) USING BTREE,
  INDEX `idx_stat_date`(`stat_date`) USING BTREE,
  INDEX `idx_response_time_avg`(`response_time_avg`) USING BTREE,
  INDEX `idx_error_count`(`error_count`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'API接口性能监控表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_monitor_api_performance
-- ----------------------------
INSERT INTO `sys_monitor_api_performance` VALUES (1, '/api/auth/userinfo', 'GET', 5, 5, 0, 121, 66, 8, 24.20, '2026-01-25', 19, '2026-01-25 19:51:56', '2026-01-25 19:57:17');
INSERT INTO `sys_monitor_api_performance` VALUES (2, '/api/router/userMenuTree', 'GET', 5, 5, 0, 70, 21, 9, 14.00, '2026-01-25', 19, '2026-01-25 19:51:56', '2026-01-25 19:57:17');
INSERT INTO `sys_monitor_api_performance` VALUES (3, '/api/auth/userinfo', 'GET', 92, 92, 0, 5143, 129, 23, 55.90, '2026-01-25', 20, '2026-01-25 20:00:38', '2026-01-25 20:58:26');
INSERT INTO `sys_monitor_api_performance` VALUES (4, '/api/router/userMenuTree', 'GET', 74, 74, 0, 651, 38, 5, 8.80, '2026-01-25', 20, '2026-01-25 20:00:39', '2026-01-25 20:58:26');
INSERT INTO `sys_monitor_api_performance` VALUES (5, '/api/sys/menu/getParent', 'GET', 68, 68, 0, 276, 17, 2, 4.06, '2026-01-25', 20, '2026-01-25 20:02:55', '2026-01-25 20:59:55');
INSERT INTO `sys_monitor_api_performance` VALUES (6, '/api/sys/menu/menuTree', 'GET', 61, 61, 0, 3690, 103, 33, 60.49, '2026-01-25', 20, '2026-01-25 20:02:55', '2026-01-25 20:59:51');
INSERT INTO `sys_monitor_api_performance` VALUES (7, '/api/sys/menu/add', 'POST', 8, 8, 0, 885, 122, 102, 110.63, '2026-01-25', 20, '2026-01-25 20:04:19', '2026-01-25 20:59:51');
INSERT INTO `sys_monitor_api_performance` VALUES (8, '/api/sys/menu', 'PUT', 14, 14, 0, 1684, 168, 99, 120.29, '2026-01-25', 20, '2026-01-25 20:04:26', '2026-01-25 20:59:21');
INSERT INTO `sys_monitor_api_performance` VALUES (9, '/api/auth/logout', 'POST', 1, 1, 0, 74, 74, 74, 74.00, '2026-01-25', 20, '2026-01-25 20:06:12', NULL);
INSERT INTO `sys_monitor_api_performance` VALUES (10, '/api/auth/captcha', 'GET', 21, 21, 0, 1232, 192, 45, 58.67, '2026-01-25', 20, '2026-01-25 20:06:13', '2026-01-25 20:15:05');
INSERT INTO `sys_monitor_api_performance` VALUES (11, '/api/auth/captcha/verify', 'POST', 3, 2, 1, 199, 79, 49, 66.33, '2026-01-25', 20, '2026-01-25 20:06:22', '2026-01-25 20:15:11');
INSERT INTO `sys_monitor_api_performance` VALUES (12, '/api/auth/login', 'POST', 2, 2, 0, 3348, 2188, 1160, 1674.00, '2026-01-25', 20, '2026-01-25 20:06:23', '2026-01-25 20:15:13');
INSERT INTO `sys_monitor_api_performance` VALUES (13, '/api/sys/menu/add', 'POST', 5, 5, 0, 529, 111, 103, 105.80, '2026-01-25', 21, '2026-01-25 21:00:13', '2026-01-25 21:02:05');
INSERT INTO `sys_monitor_api_performance` VALUES (14, '/api/sys/menu/menuTree', 'GET', 8, 8, 0, 510, 74, 58, 63.75, '2026-01-25', 21, '2026-01-25 21:00:13', '2026-01-25 21:02:23');
INSERT INTO `sys_monitor_api_performance` VALUES (15, '/api/sys/menu/getParent', 'GET', 7, 7, 0, 21, 5, 2, 3.00, '2026-01-25', 21, '2026-01-25 21:00:16', '2026-01-25 21:02:23');
INSERT INTO `sys_monitor_api_performance` VALUES (16, '/api/sys/menu', 'PUT', 2, 2, 0, 260, 149, 111, 130.00, '2026-01-25', 21, '2026-01-25 21:01:23', '2026-01-25 21:02:21');
INSERT INTO `sys_monitor_api_performance` VALUES (17, '/api/auth/userinfo', 'GET', 25, 25, 0, 1641, 107, 50, 65.64, '2026-01-25', 21, '2026-01-25 21:02:22', '2026-01-25 21:37:31');
INSERT INTO `sys_monitor_api_performance` VALUES (18, '/api/router/userMenuTree', 'GET', 25, 25, 0, 233, 21, 5, 9.32, '2026-01-25', 21, '2026-01-25 21:02:22', '2026-01-25 21:37:31');
INSERT INTO `sys_monitor_api_performance` VALUES (19, '/api/auth/captcha', 'GET', 1, 1, 0, 2011, 2011, 2011, 2011.00, '2026-01-27', 14, '2026-01-27 14:56:49', NULL);
INSERT INTO `sys_monitor_api_performance` VALUES (20, '/api/auth/captcha', 'GET', 1, 1, 0, 2010, 2010, 2010, 2010.00, '2026-01-27', 17, '2026-01-27 17:16:24', NULL);
INSERT INTO `sys_monitor_api_performance` VALUES (21, '/api/sys/user/pageList', 'POST', 1, 1, 0, 1392, 1392, 1392, 1392.00, '2026-01-27', 18, '2026-01-27 18:18:44', NULL);
INSERT INTO `sys_monitor_api_performance` VALUES (22, '/api/auth/login', 'POST', 1, 1, 0, 2087, 2087, 2087, 2087.00, '2026-01-27', 19, '2026-01-27 19:15:58', NULL);
INSERT INTO `sys_monitor_api_performance` VALUES (23, '/api/auth/captcha', 'GET', 1, 1, 0, 2029, 2029, 2029, 2029.00, '2026-02-04', 19, '2026-02-04 19:24:20', NULL);
INSERT INTO `sys_monitor_api_performance` VALUES (24, '/api/auth/captcha', 'GET', 2, 2, 0, 4036, 2018, 2018, 2018.00, '2026-02-04', 21, '2026-02-04 21:58:19', '2026-02-04 21:58:45');
INSERT INTO `sys_monitor_api_performance` VALUES (25, '/api/auth/captcha', 'GET', 1, 1, 0, 2039, 2039, 2039, 2039.00, '2026-02-05', 17, '2026-02-05 17:36:14', NULL);
INSERT INTO `sys_monitor_api_performance` VALUES (26, '/api/auth/captcha', 'GET', 1, 1, 0, 2010, 2010, 2010, 2010.00, '2026-02-05', 18, '2026-02-05 18:35:45', NULL);
INSERT INTO `sys_monitor_api_performance` VALUES (27, '/api/auth/captcha', 'GET', 1, 1, 0, 2003, 2003, 2003, 2003.00, '2026-02-05', 19, '2026-02-05 19:50:34', NULL);
INSERT INTO `sys_monitor_api_performance` VALUES (28, '/api/auth/login', 'POST', 1, 1, 0, 1885, 1885, 1885, 1885.00, '2026-02-06', 12, '2026-02-06 12:23:54', NULL);
INSERT INTO `sys_monitor_api_performance` VALUES (29, '/api/sys/operlog/pageList', 'POST', 1, 1, 0, 5067, 5067, 5067, 5067.00, '2026-02-07', 14, '2026-02-07 14:03:13', NULL);
INSERT INTO `sys_monitor_api_performance` VALUES (30, '/api/sys/post/simpleList', 'GET', 1, 1, 0, 10044, 10044, 10044, 10044.00, '2026-02-09', 12, '2026-02-09 12:26:37', NULL);
INSERT INTO `sys_monitor_api_performance` VALUES (31, '/api/sys/role/updateRole', 'PUT', 1, 1, 0, 1579, 1579, 1579, 1579.00, '2026-02-09', 12, '2026-02-09 12:33:31', NULL);
INSERT INTO `sys_monitor_api_performance` VALUES (32, '/api/sys/user/pageList', 'POST', 1, 1, 0, 10246, 10246, 10246, 10246.00, '2026-02-09', 13, '2026-02-09 13:20:45', NULL);
INSERT INTO `sys_monitor_api_performance` VALUES (33, '/api/auth/login', 'POST', 1, 1, 0, 28901, 28901, 28901, 28901.00, '2026-02-09', 14, '2026-02-09 14:12:37', NULL);
INSERT INTO `sys_monitor_api_performance` VALUES (34, '/api/auth/login', 'POST', 1, 1, 0, 1353, 1353, 1353, 1353.00, '2026-02-12', 16, '2026-02-12 16:55:01', NULL);
INSERT INTO `sys_monitor_api_performance` VALUES (35, '/api/auth/captcha', 'GET', 1, 1, 0, 1503, 1503, 1503, 1503.00, '2026-02-12', 17, '2026-02-12 17:09:52', NULL);

-- ----------------------------
-- Table structure for sys_monitor_slow_sql
-- ----------------------------
DROP TABLE IF EXISTS `sys_monitor_slow_sql`;
CREATE TABLE `sys_monitor_slow_sql`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sql_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'SQL ID（Druid生成的SQL标识）',
  `sql_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'SQL语句',
  `execute_count` bigint(20) NULL DEFAULT 0 COMMENT '执行次数',
  `execute_time_total` bigint(20) NULL DEFAULT 0 COMMENT '总执行时间（毫秒）',
  `execute_time_max` bigint(20) NULL DEFAULT 0 COMMENT '最大执行时间（毫秒）',
  `execute_time_avg` decimal(20, 2) NULL COMMENT '平均执行时间（毫秒）',
  `slow_count` bigint(20) NULL DEFAULT 0 COMMENT '慢SQL次数',
  `error_count` bigint(20) NULL DEFAULT 0 COMMENT '错误次数',
  `last_execute_time` datetime(0) NULL DEFAULT NULL COMMENT '最后执行时间',
  `optimization_suggestion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '优化建议',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0待优化 1已优化 2已忽略）',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sql_id`(`sql_id`) USING BTREE,
  INDEX `idx_execute_time_avg`(`execute_time_avg`) USING BTREE,
  INDEX `idx_slow_count`(`slow_count`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '慢SQL记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_monitor_slow_sql
-- ----------------------------

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob NULL COMMENT '公告内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES (3, '测试新增：2026-2-5 ', '1', 0x3C703EE6B58BE8AF95E696B0E5A29E3C2F703E, '0', 'admin', '2026-02-05 19:32:02', '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_online_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_online_user`;
CREATE TABLE `sys_online_user`  (
  `session_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话ID（Redis key）',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录账号',
  `nick_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户昵称',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
  `ipaddr` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作系统',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '状态（0在线 1离线）',
  `start_timestamp` datetime(0) NULL DEFAULT NULL COMMENT '会话开始时间（登录时间）',
  `last_access_time` datetime(0) NULL DEFAULT NULL COMMENT '最后访问时间',
  `end_timestamp` datetime(0) NULL DEFAULT NULL COMMENT '会话结束时间（登出/强制下线时间）',
  `expire_time` int(11) NULL DEFAULT NULL COMMENT '会话超时时间（秒）',
  `token_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '令牌标识',
  `logout_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '下线类型（0正常登出 1强制下线 2过期下线）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间（记录入库时间）',
  PRIMARY KEY (`session_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_start_timestamp`(`start_timestamp`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '在线用户记录表（历史记录）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_online_user
-- ----------------------------
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:1', 1, 'admin', '超级管理员', '星枢科技', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10', '1', '2026-02-09 13:32:57', '2026-02-09 13:32:57', '2026-02-09 13:37:36', NULL, '', '0', '2026-02-09 13:37:36');
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:113', 113, 'wangwu', 'wangwu', '人事部', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '1', '2026-01-27 19:31:06', '2026-01-27 19:31:06', '2026-01-27 19:36:21', NULL, '', '0', '2026-01-27 19:36:21');
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:114', 114, 'starPivot', 'starPivot演示用户', '山西分公司', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10', '1', '2026-02-09 13:31:46', '2026-02-09 13:31:46', '2026-02-09 13:32:38', NULL, '', '0', '2026-02-09 13:32:38');
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:115', 115, 'xiaoming', 'xiaoming', '', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10', '1', '2026-02-12 16:49:35', '2026-02-12 16:49:35', '2026-02-12 16:54:27', NULL, '', '0', '2026-02-12 16:54:27');
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:2', 2, 'user', '用户管理员', '测试部门', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10', '1', '2026-02-09 12:31:16', '2026-02-09 12:31:16', '2026-02-09 12:31:52', NULL, '', '0', '2026-02-09 12:31:52');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int(11) NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求方式',
  `operator_type` int(11) NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '返回参数',
  `status` int(11) NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime(0) NULL DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint(20) NULL DEFAULT 0 COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`) USING BTREE,
  INDEX `idx_sys_oper_log_bt`(`business_type`) USING BTREE,
  INDEX `idx_sys_oper_log_s`(`status`) USING BTREE,
  INDEX `idx_sys_oper_log_ot`(`oper_time`) USING BTREE,
  INDEX `idx_oper_time`(`oper_time`) USING BTREE,
  INDEX `idx_oper_name`(`oper_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5730 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES (5540, '操作日志', 3, 'com.star.pivot.controller.SysOperLogController.clean()', 'DELETE', 1, 'admin', '星枢科技', '/api/sys/operlog/clean', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"清空成功\",\"data\":null,\"timestamp\":1770889642680}', 0, '', '2026-02-12 17:47:23', 139);
INSERT INTO `sys_oper_log` VALUES (5541, '操作日志', 0, 'com.star.pivot.controller.SysOperLogController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/operlog/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"title\":null,\"businessType\":null,\"operName\":null,\"status\":null,\"startTime\":null,\"endTime\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=1, rows=[OperLogVO(operId=5540, title=操作日志, businessType=3, method=com.star.pivot.controller.SysOperLogController.clean(), requestMethod=DELETE, operatorType=1, operName=admin, deptName=星枢科技, operUrl=/api/sys/operlog/clean, operIp=0:0:0:0:0:0:0:1, operLocation=, operParam=, jsonResult={\"code\":200,\"message\":\"清空成功\",\"data\":null,\"timestamp\":1770889642680}, status=0, errorMsg=, operTime=2026-02-12T17:47:23, costTime=139)], pageNum=1, pageSize=20, pageCount=1), timestamp=1770889642862)', 0, '', '2026-02-12 17:47:23', 120);
INSERT INTO `sys_oper_log` VALUES (5542, '登录日志', 0, 'com.star.pivot.controller.SysLogininforController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/logininfor/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"ipaddr\":null,\"status\":null,\"startTime\":null,\"endTime\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=69, rows=[LogininforVO(infoId=177, userName=admin, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, status=0, msg=登录成功, loginTime=2026-02-12T17:36:35), LogininforVO(infoId=176, userName=admin, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, status=0, msg=登录成功, loginTime=2026-02-12T17:16:03), LogininforVO(infoId=175, userName=admin, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, status=0, msg=登录成功, loginTime=2026-02-12T16:55), LogininforVO(infoId=174, userName=xiaoming, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, status=0, msg=登录成功, loginTime=2026-02-12T16:49:35), LogininforVO(infoId=173, userName=xiaoming, ipaddr=60.220.119.64, loginLocation=, browser=WeChat 7.0.20.1781, os=Windows 10/11, status=0, msg=登录成功, loginTime=2026-02-09T18:19:27), LogininforVO(infoId=172, userName=starPivot, ipaddr=39.144.98.133, loginLocation=, browser=Chrome 132, os=Windows 10/11, status=0, msg=登录成功, loginTime=2026-02-09T18:10:46), LogininforVO(infoId=171, userName=starPivot, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, status=0, msg=登录成功, loginTime=2026-02-09T14:57:37), LogininforVO(infoId=170, userName=admin, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, status=0, msg=登录成功, loginTime=2026-02-09T14:49:51), LogininforVO(infoId=169, userName=starPivot, ipaddr=60.220.119.64, loginLocation=, browser=Chrome 144, os=Windows 10/11, status=0, msg=登录成功, loginTime=2026-02-09T14:18:40), LogininforVO(infoId=167, userName=starPivot, ipaddr=60.220.119.64, loginLocation=, browser=Chrome 144, os=Windows 10/11, status=0, msg=登录成功, loginTime=2026-02-09T14:12:31), LogininforVO(infoId=168, userName=starPivot, ipaddr=60.220.119.64, loginLocati...', 0, '', '2026-02-12 17:47:24', 130);
INSERT INTO `sys_oper_log` VALUES (5543, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=9.751384152567445, used=395, free=3664}, duration=41, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.3119368343137, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:47:24.609147500}, timestamp=1770889644650)', 0, '', '2026-02-12 17:47:25', 91);
INSERT INTO `sys_oper_log` VALUES (5544, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":1.7608492038713706,\"used\":0.4870433968154855,\"wait\":0.0,\"free\":97.55853886980955},\"memory\":{\"total\":16236,\"used\":6272,\"free\":9963,\"usage\":38.630762786631124},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":729816,\"max\":4060,\"total\":850,\"used\":399,\"free\":3660,\"usage\":9.84990632005513},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.3119368343137}},\"timestamp\":1770889645656}', 0, '', '2026-02-12 17:47:26', 1097);
INSERT INTO `sys_oper_log` VALUES (5545, '登录日志', 3, 'com.star.pivot.controller.SysLogininforController.clean()', 'DELETE', 1, 'admin', '星枢科技', '/api/sys/logininfor/clean', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"清空成功\",\"data\":null,\"timestamp\":1770889646876}', 0, '', '2026-02-12 17:47:27', 128);
INSERT INTO `sys_oper_log` VALUES (5546, '登录日志', 0, 'com.star.pivot.controller.SysLogininforController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/logininfor/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"ipaddr\":null,\"status\":null,\"startTime\":null,\"endTime\":null}]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"total\":0,\"rows\":[],\"pageNum\":1,\"pageSize\":20,\"pageCount\":0},\"timestamp\":1770889647039}', 0, '', '2026-02-12 17:47:27', 98);
INSERT INTO `sys_oper_log` VALUES (5547, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=10.342517157493553, used=419, free=3640}, duration=47, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311937901528067, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:47:34.560049800}, timestamp=1770889654607)', 0, '', '2026-02-12 17:47:35', 104);
INSERT INTO `sys_oper_log` VALUES (5548, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.3804614629356897,\"used\":0.28841433480608736,\"wait\":0.0,\"free\":99.14089347079037},\"memory\":{\"total\":16236,\"used\":6295,\"free\":9940,\"usage\":38.7769699533085},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":739782,\"max\":4060,\"total\":850,\"used\":421,\"free\":3638,\"usage\":10.391778241237397},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311938168331658}},\"timestamp\":1770889655623}', 0, '', '2026-02-12 17:47:36', 1120);
INSERT INTO `sys_oper_log` VALUES (5549, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=10.539561492468923, used=427, free=3632}, duration=46, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311934166277783, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:47:44.551756}, timestamp=1770889664597)', 0, '', '2026-02-12 17:47:45', 99);
INSERT INTO `sys_oper_log` VALUES (5550, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.29232491603433264,\"used\":0.0,\"wait\":0.0,\"free\":99.61437989799727},\"memory\":{\"total\":16236,\"used\":6264,\"free\":9971,\"usage\":38.58163409906239},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":749772,\"max\":4060,\"total\":850,\"used\":431,\"free\":3628,\"usage\":10.638083659956608},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311934166277783}},\"timestamp\":1770889665607}', 0, '', '2026-02-12 17:47:46', 1109);
INSERT INTO `sys_oper_log` VALUES (5551, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=10.785866911188135, used=437, free=3622}, duration=43, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311939769153208, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:47:54.567257300}, timestamp=1770889674610)', 0, '', '2026-02-12 17:47:55', 106);
INSERT INTO `sys_oper_log` VALUES (5552, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.2841611069928342,\"used\":0.48183839881393625,\"wait\":0.0,\"free\":99.04250061774154},\"memory\":{\"total\":16236,\"used\":6174,\"free\":10061,\"usage\":38.03217231321776},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":759778,\"max\":4060,\"total\":850,\"used\":439,\"free\":3620,\"usage\":10.835127994931977},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311939769153208}},\"timestamp\":1770889675614}', 0, '', '2026-02-12 17:47:56', 1100);
INSERT INTO `sys_oper_log` VALUES (5553, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=10.982911246163505, used=445, free=3614}, duration=42, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311941369974758, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:48:04.552547300}, timestamp=1770889684594)', 0, '', '2026-02-12 17:48:05', 95);
INSERT INTO `sys_oper_log` VALUES (5554, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":1.1495666072416548,\"used\":0.28278109055142314,\"wait\":0.0,\"free\":98.46929366201512},\"memory\":{\"total\":16236,\"used\":6210,\"free\":10025,\"usage\":38.25416565317697},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":769799,\"max\":4060,\"total\":850,\"used\":449,\"free\":3610,\"usage\":11.081433413651189},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.31194163677835}},\"timestamp\":1770889685640}', 0, '', '2026-02-12 17:48:06', 1141);
INSERT INTO `sys_oper_log` VALUES (5555, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=11.229216664882717, used=455, free=3604}, duration=47, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.31188480761332, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:48:14.555375600}, timestamp=1770889694602)', 0, '', '2026-02-12 17:48:15', 98);
INSERT INTO `sys_oper_log` VALUES (5556, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":1.36875,\"used\":0.78125,\"wait\":0.0,\"free\":97.16875},\"memory\":{\"total\":16236,\"used\":6235,\"free\":10000,\"usage\":38.40381327153031},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":779767,\"max\":4060,\"total\":850,\"used\":457,\"free\":3602,\"usage\":11.278477748626559},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311886141631279}},\"timestamp\":1770889695609}', 0, '', '2026-02-12 17:48:16', 1105);
INSERT INTO `sys_oper_log` VALUES (5557, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=11.475522083601929, used=465, free=3594}, duration=42, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311924027741298, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:48:24.554196700}, timestamp=1770889704596)', 0, '', '2026-02-12 17:48:25', 84);
INSERT INTO `sys_oper_log` VALUES (5558, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.5729423361261705,\"used\":0.28339083292262196,\"wait\":0.0,\"free\":99.1436668309512},\"memory\":{\"total\":16236,\"used\":6236,\"free\":9999,\"usage\":38.408841623979804},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":789779,\"max\":4060,\"total\":850,\"used\":467,\"free\":3592,\"usage\":11.52478316734577},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311924027741298}},\"timestamp\":1770889705620}', 0, '', '2026-02-12 17:48:26', 1108);
INSERT INTO `sys_oper_log` VALUES (5559, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=11.672566418577297, used=473, free=3586}, duration=48, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311924828152073, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:48:34.675884600}, timestamp=1770889714723)', 0, '', '2026-02-12 17:48:35', 101);
INSERT INTO `sys_oper_log` VALUES (5560, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.7692307692307693,\"used\":0.19692307692307692,\"wait\":0.0,\"free\":98.93538461538462},\"memory\":{\"total\":16236,\"used\":6251,\"free\":9985,\"usage\":38.500939868844206},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":799903,\"max\":4060,\"total\":850,\"used\":477,\"free\":3582,\"usage\":11.771088586064982},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311924828152073}},\"timestamp\":1770889715744}', 0, '', '2026-02-12 17:48:36', 1122);
INSERT INTO `sys_oper_log` VALUES (5561, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=11.869610753552667, used=481, free=3578}, duration=37, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311925094955665, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:48:44.559674600}, timestamp=1770889724596)', 0, '', '2026-02-12 17:48:45', 95);
INSERT INTO `sys_oper_log` VALUES (5562, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.5846873461349089,\"used\":0.09847365829640571,\"wait\":0.0,\"free\":99.03372722796652},\"memory\":{\"total\":16236,\"used\":6260,\"free\":9975,\"usage\":38.55776746925427},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":809783,\"max\":4060,\"total\":850,\"used\":485,\"free\":3574,\"usage\":11.968132921040352},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311925094955665}},\"timestamp\":1770889725623}', 0, '', '2026-02-12 17:48:46', 1122);
INSERT INTO `sys_oper_log` VALUES (5563, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=12.115916172271879, used=491, free=3568}, duration=38, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311926428973624, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:48:54.574043500}, timestamp=1770889734612)', 0, '', '2026-02-12 17:48:55', 89);
INSERT INTO `sys_oper_log` VALUES (5564, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.5857427716849452,\"used\":0.18693918245264207,\"wait\":0.0,\"free\":98.93444666001994},\"memory\":{\"total\":16236,\"used\":6266,\"free\":9969,\"usage\":38.596021442434626},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":819784,\"max\":4060,\"total\":850,\"used\":493,\"free\":3566,\"usage\":12.16517725601572},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311926428973624}},\"timestamp\":1770889735625}', 0, '', '2026-02-12 17:48:56', 1102);
INSERT INTO `sys_oper_log` VALUES (5565, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=12.312960507247249, used=499, free=3560}, duration=43, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311926962580808, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:49:04.553839200}, timestamp=1770889744596)', 0, '', '2026-02-12 17:49:05', 96);
INSERT INTO `sys_oper_log` VALUES (5566, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":1.174926567089557,\"used\":0.29373164177238925,\"wait\":0.0,\"free\":98.53134179113805},\"memory\":{\"total\":16236,\"used\":6270,\"free\":9965,\"usage\":38.621596268529416},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":829770,\"max\":4060,\"total\":850,\"used\":503,\"free\":3556,\"usage\":12.411482674734932},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311926962580808}},\"timestamp\":1770889745612}', 0, '', '2026-02-12 17:49:06', 1112);
INSERT INTO `sys_oper_log` VALUES (5567, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=12.608527009710302, used=511, free=3548}, duration=51, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311931765045458, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:49:14.591429}, timestamp=1770889754642)', 0, '', '2026-02-12 17:49:15', 136);
INSERT INTO `sys_oper_log` VALUES (5568, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.7692307692307693,\"used\":0.09846153846153846,\"wait\":0.0,\"free\":98.75076923076924},\"memory\":{\"total\":16236,\"used\":6300,\"free\":9935,\"usage\":38.80355526171372},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":839780,\"max\":4060,\"total\":850,\"used\":513,\"free\":3546,\"usage\":12.657788093454144},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311931765045458}},\"timestamp\":1770889755622}', 0, '', '2026-02-12 17:49:16', 1116);
INSERT INTO `sys_oper_log` VALUES (5569, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=12.805571344685672, used=519, free=3540}, duration=45, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311932031849048, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:49:24.639748}, timestamp=1770889764684)', 0, '', '2026-02-12 17:49:25', 183);
INSERT INTO `sys_oper_log` VALUES (5570, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.19356852950359038,\"used\":0.18732438339057134,\"wait\":0.0,\"free\":98.93849516078676},\"memory\":{\"total\":16236,\"used\":6282,\"free\":9953,\"usage\":38.69622760488506},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":849767,\"max\":4060,\"total\":850,\"used\":521,\"free\":3538,\"usage\":12.854832428429514},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311932031849048}},\"timestamp\":1770889765610}', 0, '', '2026-02-12 17:49:26', 1109);
INSERT INTO `sys_oper_log` VALUES (5571, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=13.051876763404884, used=529, free=3530}, duration=45, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311932298652641, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:49:34.565863200}, timestamp=1770889774610)', 0, '', '2026-02-12 17:49:35', 97);
INSERT INTO `sys_oper_log` VALUES (5572, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.6782587248735973,\"used\":0.4809470958194599,\"wait\":0.0,\"free\":98.26735725736836},\"memory\":{\"total\":16236,\"used\":6288,\"free\":9947,\"usage\":38.73217190421301},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":859777,\"max\":4060,\"total\":850,\"used\":529,\"free\":3530,\"usage\":13.051876763404884},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311936567510108}},\"timestamp\":1770889775617}', 0, '', '2026-02-12 17:49:36', 1104);
INSERT INTO `sys_oper_log` VALUES (5573, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=13.248921098380253, used=537, free=3522}, duration=43, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.3119368343137, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:49:44.554771400}, timestamp=1770889784597)', 0, '', '2026-02-12 17:49:45', 90);
INSERT INTO `sys_oper_log` VALUES (5574, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.4974505658500187,\"used\":0.5907225469468972,\"wait\":0.0,\"free\":98.71906479293621},\"memory\":{\"total\":16236,\"used\":6287,\"free\":9948,\"usage\":38.72391963201121},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":869767,\"max\":4060,\"total\":850,\"used\":539,\"free\":3520,\"usage\":13.298182182124096},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.3119368343137}},\"timestamp\":1770889785608}', 0, '', '2026-02-12 17:49:46', 1101);
INSERT INTO `sys_oper_log` VALUES (5575, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=13.445965433355623, used=545, free=3514}, duration=35, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311937901528067, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:49:54.557199900}, timestamp=1770889794592)', 0, '', '2026-02-12 17:49:55', 85);
INSERT INTO `sys_oper_log` VALUES (5576, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.4804138950480414,\"used\":0.29563932002956395,\"wait\":0.0,\"free\":99.13155949741315},\"memory\":{\"total\":16236,\"used\":6290,\"free\":9945,\"usage\":38.7435518597566},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":879779,\"max\":4060,\"total\":850,\"used\":549,\"free\":3510,\"usage\":13.544487600843308},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311937901528067}},\"timestamp\":1770889795621}', 0, '', '2026-02-12 17:49:56', 1114);
INSERT INTO `sys_oper_log` VALUES (5577, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=13.692270852074834, used=555, free=3504}, duration=44, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311942437189126, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:50:04.560323600}, timestamp=1770889804604)', 0, '', '2026-02-12 17:50:05', 92);
INSERT INTO `sys_oper_log` VALUES (5578, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.4874390701162355,\"used\":0.09998750156230471,\"wait\":0.0,\"free\":99.21259842519684},\"memory\":{\"total\":16236,\"used\":6293,\"free\":9942,\"usage\":38.76231795980735},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":889776,\"max\":4060,\"total\":850,\"used\":557,\"free\":3502,\"usage\":13.741531935818676},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311942437189126}},\"timestamp\":1770889805618}', 0, '', '2026-02-12 17:50:06', 1106);
INSERT INTO `sys_oper_log` VALUES (5579, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=13.889315187050205, used=563, free=3496}, duration=48, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.31194297079631, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:50:14.560810400}, timestamp=1770889814608)', 0, '', '2026-02-12 17:50:15', 97);
INSERT INTO `sys_oper_log` VALUES (5580, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.8677991137370753,\"used\":0.4862136878385032,\"wait\":0.0,\"free\":98.54751354012802},\"memory\":{\"total\":16236,\"used\":6297,\"free\":9938,\"usage\":38.787074776412744},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":899784,\"max\":4060,\"total\":850,\"used\":567,\"free\":3492,\"usage\":13.98783735453789},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.31194297079631}},\"timestamp\":1770889815627}', 0, '', '2026-02-12 17:50:16', 1116);
INSERT INTO `sys_oper_log` VALUES (5581, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=14.135620605769416, used=573, free=3486}, duration=48, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311961647047728, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:50:24.566546600}, timestamp=1770889824614)', 0, '', '2026-02-12 17:50:25', 97);
INSERT INTO `sys_oper_log` VALUES (5582, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.5936386927451103,\"used\":0.09998125351496594,\"wait\":0.0,\"free\":99.10641754670999},\"memory\":{\"total\":16236,\"used\":6312,\"free\":9923,\"usage\":38.87852342550616},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":909804,\"max\":4060,\"total\":850,\"used\":577,\"free\":3482,\"usage\":14.234142773257101},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311964848690828}},\"timestamp\":1770889825644}', 0, '', '2026-02-12 17:50:26', 1127);
INSERT INTO `sys_oper_log` VALUES (5583, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=14.332664940744785, used=581, free=3478}, duration=43, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.31184238584224, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:50:34.550789900}, timestamp=1770889834593)', 0, '', '2026-02-12 17:50:35', 85);
INSERT INTO `sys_oper_log` VALUES (5584, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.676923076923077,\"used\":0.38153846153846155,\"wait\":0.0,\"free\":98.74461538461539},\"memory\":{\"total\":16236,\"used\":6321,\"free\":9914,\"usage\":38.936746453868714},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":919782,\"max\":4060,\"total\":850,\"used\":585,\"free\":3474,\"usage\":14.43118710823247},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.31184238584224}},\"timestamp\":1770889835617}', 0, '', '2026-02-12 17:50:36', 1120);
INSERT INTO `sys_oper_log` VALUES (5585, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=14.578970359463996, used=591, free=3468}, duration=45, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.3118469215033, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:50:44.545684300}, timestamp=1770889844590)', 0, '', '2026-02-12 17:50:45', 96);
INSERT INTO `sys_oper_log` VALUES (5586, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.4775900073475386,\"used\":0.29390154298310067,\"wait\":0.0,\"free\":98.65295126132746},\"memory\":{\"total\":16236,\"used\":6324,\"free\":9911,\"usage\":38.954044948659075},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":929781,\"max\":4060,\"total\":850,\"used\":593,\"free\":3466,\"usage\":14.62823144320784},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.3118469215033}},\"timestamp\":1770889845622}', 0, '', '2026-02-12 17:50:46', 1120);
INSERT INTO `sys_oper_log` VALUES (5587, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=14.825275778183208, used=601, free=3458}, duration=43, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311848255521257, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:50:54.550931500}, timestamp=1770889854593)', 0, '', '2026-02-12 17:50:55', 85);
INSERT INTO `sys_oper_log` VALUES (5588, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.9476923076923077,\"used\":0.18461538461538463,\"wait\":0.0,\"free\":98.86769230769231},\"memory\":{\"total\":16236,\"used\":6328,\"free\":9907,\"usage\":38.9773341600041},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":939779,\"max\":4060,\"total\":850,\"used\":603,\"free\":3456,\"usage\":14.874536861927051},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311848255521257}},\"timestamp\":1770889855617}', 0, '', '2026-02-12 17:50:56', 1109);
INSERT INTO `sys_oper_log` VALUES (5589, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=15.022320113158578, used=609, free=3450}, duration=43, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.31184852232485, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:51:04.567412500}, timestamp=1770889864610)', 0, '', '2026-02-12 17:51:05', 95);
INSERT INTO `sys_oper_log` VALUES (5590, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.5874632835447785,\"used\":0.09999375039060059,\"wait\":0.0,\"free\":99.11880507468283},\"memory\":{\"total\":16236,\"used\":6336,\"free\":9899,\"usage\":39.02607790193077},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":949786,\"max\":4060,\"total\":850,\"used\":613,\"free\":3446,\"usage\":15.120842280646263},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.31184852232485}},\"timestamp\":1770889865652}', 0, '', '2026-02-12 17:51:06', 1138);
INSERT INTO `sys_oper_log` VALUES (5591, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=15.367147699365475, used=623, free=3436}, duration=38, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.31184878912844, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:51:14.556890600}, timestamp=1770889874594)', 0, '', '2026-02-12 17:51:15', 87);
INSERT INTO `sys_oper_log` VALUES (5592, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.7754323342974953,\"used\":0.0923133731306542,\"wait\":0.0,\"free\":98.94147332143517},\"memory\":{\"total\":16236,\"used\":6327,\"free\":9908,\"usage\":38.973773412814985},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":959777,\"max\":4060,\"total\":850,\"used\":625,\"free\":3434,\"usage\":15.416408783109317},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.31184878912844}},\"timestamp\":1770889875618}', 0, '', '2026-02-12 17:51:16', 1110);
INSERT INTO `sys_oper_log` VALUES (5593, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=15.564192034340845, used=631, free=3428}, duration=37, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.31185492561105, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:51:24.552272700}, timestamp=1770889884589)', 0, '', '2026-02-12 17:51:25', 96);
INSERT INTO `sys_oper_log` VALUES (5594, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.7691361063253753,\"used\":0.09844942160964805,\"wait\":0.0,\"free\":98.93551562884568},\"memory\":{\"total\":16236,\"used\":6364,\"free\":9871,\"usage\":39.198653845089694},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":969780,\"max\":4060,\"total\":850,\"used\":635,\"free\":3424,\"usage\":15.662714201828528},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.31185492561105}},\"timestamp\":1770889885669}', 0, '', '2026-02-12 17:51:26', 1166);
INSERT INTO `sys_oper_log` VALUES (5595, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=6.6320968026598095, used=269, free=3790}, duration=45, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311855459218233, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:51:34.551870}, timestamp=1770889894596)', 0, '', '2026-02-12 17:51:35', 87);
INSERT INTO `sys_oper_log` VALUES (5596, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.4795868175110674,\"used\":0.5841121495327103,\"wait\":0.0,\"free\":98.74569601574028},\"memory\":{\"total\":16236,\"used\":6378,\"free\":9858,\"usage\":39.28329376813906},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":979783,\"max\":4060,\"total\":850,\"used\":271,\"free\":3788,\"usage\":6.681357886403652},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311855459218233}},\"timestamp\":1770889895624}', 0, '', '2026-02-12 17:51:36', 1127);
INSERT INTO `sys_oper_log` VALUES (5597, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=6.927663305122864, used=281, free=3778}, duration=44, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311855192414642, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:51:44.557593}, timestamp=1770889904601)', 0, '', '2026-02-12 17:51:45', 96);
INSERT INTO `sys_oper_log` VALUES (5598, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.9874382851071808,\"used\":0.3874757827635773,\"wait\":0.0,\"free\":98.42509843134805},\"memory\":{\"total\":16236,\"used\":6404,\"free\":9831,\"usage\":39.44489876049909},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":989770,\"max\":4060,\"total\":850,\"used\":283,\"free\":3776,\"usage\":6.976924388866706},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311855192414642}},\"timestamp\":1770889905608}', 0, '', '2026-02-12 17:51:46', 1103);
INSERT INTO `sys_oper_log` VALUES (5599, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=7.124707640098234, used=289, free=3770}, duration=43, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.3118565264326, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:51:54.563170}, timestamp=1770889914606)', 0, '', '2026-02-12 17:51:55', 97);
INSERT INTO `sys_oper_log` VALUES (5600, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.4738169958771768,\"used\":0.38766845117223553,\"wait\":0.0,\"free\":98.65854408959449},\"memory\":{\"total\":16236,\"used\":6423,\"free\":9812,\"usage\":39.56223500402148},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":999773,\"max\":4060,\"total\":850,\"used\":291,\"free\":3768,\"usage\":7.1739687238420755},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.3118565264326}},\"timestamp\":1770889915615}', 0, '', '2026-02-12 17:51:56', 1106);
INSERT INTO `sys_oper_log` VALUES (5601, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=7.321751975073603, used=297, free=3762}, duration=43, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311863463325984, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:52:04.558233}, timestamp=1770889924601)', 0, '', '2026-02-12 17:52:05', 97);
INSERT INTO `sys_oper_log` VALUES (5602, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.7692307692307693,\"used\":0.09846153846153846,\"wait\":0.0,\"free\":98.94153846153846},\"memory\":{\"total\":16236,\"used\":6442,\"free\":9793,\"usage\":39.680485493443776},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1009781,\"max\":4060,\"total\":850,\"used\":301,\"free\":3758,\"usage\":7.420274142561288},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311863463325984}},\"timestamp\":1770889925621}', 0, '', '2026-02-12 17:52:06', 1117);
INSERT INTO `sys_oper_log` VALUES (5603, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=7.568057393792815, used=307, free=3752}, duration=45, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311877870719936, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:52:14.552807100}, timestamp=1770889934597)', 0, '', '2026-02-12 17:52:15', 92);
INSERT INTO `sys_oper_log` VALUES (5604, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.7694201649636834,\"used\":0.19697156223070295,\"wait\":0.0,\"free\":98.75046165209898},\"memory\":{\"total\":16236,\"used\":6586,\"free\":9649,\"usage\":40.56906033084634},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1019781,\"max\":4060,\"total\":850,\"used\":311,\"free\":3748,\"usage\":7.6665795612805},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311877870719936}},\"timestamp\":1770889935623}', 0, '', '2026-02-12 17:52:16', 1118);
INSERT INTO `sys_oper_log` VALUES (5605, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=7.814362812512027, used=317, free=3742}, duration=42, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311878137523527, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:52:24.563783400}, timestamp=1770889944605)', 0, '', '2026-02-12 17:52:25', 100);
INSERT INTO `sys_oper_log` VALUES (5606, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.4800295402794018,\"used\":0.28309434426733954,\"wait\":0.0,\"free\":99.04609514431657},\"memory\":{\"total\":16236,\"used\":6593,\"free\":9642,\"usage\":40.612583247502485},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1029787,\"max\":4060,\"total\":850,\"used\":321,\"free\":3738,\"usage\":7.912884979999712},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311882406380994}},\"timestamp\":1770889945627}', 0, '', '2026-02-12 17:52:26', 1122);
INSERT INTO `sys_oper_log` VALUES (5607, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=8.060668231231238, used=327, free=3732}, duration=42, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.31187840432712, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:52:34.596058}, timestamp=1770889954638)', 0, '', '2026-02-12 17:52:35', 125);
INSERT INTO `sys_oper_log` VALUES (5608, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.7567960283344433,\"used\":0.09686989162680874,\"wait\":0.0,\"free\":98.95259429678514},\"memory\":{\"total\":16236,\"used\":6594,\"free\":9641,\"usage\":40.6159755809732},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1039822,\"max\":4060,\"total\":850,\"used\":329,\"free\":3730,\"usage\":8.10992931497508},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.31187840432712}},\"timestamp\":1770889955663}', 0, '', '2026-02-12 17:52:36', 1150);
INSERT INTO `sys_oper_log` VALUES (5609, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=8.257712566206608, used=335, free=3724}, duration=53, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311895746560579, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:52:44.623126700}, timestamp=1770889964676)', 0, '', '2026-02-12 17:52:45', 160);
INSERT INTO `sys_oper_log` VALUES (5610, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":1.636923076923077,\"used\":0.19076923076923077,\"wait\":0.0,\"free\":97.98153846153846},\"memory\":{\"total\":16236,\"used\":6583,\"free\":9652,\"usage\":40.55065511733504},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1049778,\"max\":4060,\"total\":850,\"used\":337,\"free\":3722,\"usage\":8.30697364995045},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311895746560579}},\"timestamp\":1770889965620}', 0, '', '2026-02-12 17:52:46', 1104);
INSERT INTO `sys_oper_log` VALUES (5611, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":903,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770889969439}', 0, '', '2026-02-12 17:52:49', 59);
INSERT INTO `sys_oper_log` VALUES (5612, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3346,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770889971951}', 0, '', '2026-02-12 17:52:52', 127);
INSERT INTO `sys_oper_log` VALUES (5613, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770889973316)', 0, '', '2026-02-12 17:52:53', 211);
INSERT INTO `sys_oper_log` VALUES (5614, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=8.602540152413505, used=349, free=3710}, duration=38, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311904551079104, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:52:54.718344400}, timestamp=1770889974756)', 0, '', '2026-02-12 17:52:55', 248);
INSERT INTO `sys_oper_log` VALUES (5615, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.6708105114160872,\"used\":1.24930764970152,\"wait\":0.0,\"free\":97.88910086774571},\"memory\":{\"total\":16236,\"used\":6688,\"free\":9547,\"usage\":41.19300909843084},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1059853,\"max\":4060,\"total\":850,\"used\":351,\"free\":3708,\"usage\":8.651801236157347},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311904551079104}},\"timestamp\":1770889975695}', 0, '', '2026-02-12 17:52:56', 1187);
INSERT INTO `sys_oper_log` VALUES (5616, 'Redis缓存管理', 0, 'com.star.pivot.controller.MonitorController.getCacheList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/cache/list', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":[{\"cacheName\":\"backup1\",\"remark\":\"backup1\",\"keys\":null},{\"cacheName\":\"backup2\",\"remark\":\"backup2\",\"keys\":null},{\"cacheName\":\"backup3\",\"remark\":\"backup3\",\"keys\":null},{\"cacheName\":\"backup4\",\"remark\":\"backup4\",\"keys\":null},{\"cacheName\":\"cache\",\"remark\":\"cache\",\"keys\":null},{\"cacheName\":\"jwt\",\"remark\":\"jwt\",\"keys\":null}],\"timestamp\":1770889978394}', 0, '', '2026-02-12 17:52:58', 75);
INSERT INTO `sys_oper_log` VALUES (5617, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":1,\"activePeak\":2,\"usage\":5.0},\"sqlStat\":{\"executeCount\":931,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770889979435}', 0, '', '2026-02-12 17:52:59', 50);
INSERT INTO `sys_oper_log` VALUES (5618, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3365,\"commandsPerSecond\":3.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770889979536}', 0, '', '2026-02-12 17:53:00', 116);
INSERT INTO `sys_oper_log` VALUES (5619, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3372,\"commandsPerSecond\":3.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770889981946}', 0, '', '2026-02-12 17:53:02', 113);
INSERT INTO `sys_oper_log` VALUES (5620, 'API性能监控', 0, 'com.star.pivot.controller.MonitorController.getApiPerformancePageList()', 'POST', 1, 'admin', '星枢科技', '/api/monitor/api/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"apiPath\":null,\"apiMethod\":null,\"startDate\":\"2026-02-05\",\"endDate\":\"2026-02-12\",\"orderBy\":\"responseTimeAvg\",\"orderDirection\":\"desc\"}]', 'Result(code=200, message=操作成功, data=PageResponse(total=11, rows=[SysMonitorApiPerformance(id=33, apiPath=/api/auth/login, apiMethod=POST, requestCount=1, successCount=1, errorCount=0, responseTimeTotal=28901, responseTimeMax=28901, responseTimeMin=28901, responseTimeAvg=28901.00, statDate=2026-02-09, statHour=14, createTime=2026-02-09T14:12:37, updateTime=null), SysMonitorApiPerformance(id=32, apiPath=/api/sys/user/pageList, apiMethod=POST, requestCount=1, successCount=1, errorCount=0, responseTimeTotal=10246, responseTimeMax=10246, responseTimeMin=10246, responseTimeAvg=10246.00, statDate=2026-02-09, statHour=13, createTime=2026-02-09T13:20:45, updateTime=null), SysMonitorApiPerformance(id=30, apiPath=/api/sys/post/simpleList, apiMethod=GET, requestCount=1, successCount=1, errorCount=0, responseTimeTotal=10044, responseTimeMax=10044, responseTimeMin=10044, responseTimeAvg=10044.00, statDate=2026-02-09, statHour=12, createTime=2026-02-09T12:26:37, updateTime=null), SysMonitorApiPerformance(id=29, apiPath=/api/sys/operlog/pageList, apiMethod=POST, requestCount=1, successCount=1, errorCount=0, responseTimeTotal=5067, responseTimeMax=5067, responseTimeMin=5067, responseTimeAvg=5067.00, statDate=2026-02-07, statHour=14, createTime=2026-02-07T14:03:13, updateTime=null), SysMonitorApiPerformance(id=25, apiPath=/api/auth/captcha, apiMethod=GET, requestCount=1, successCount=1, errorCount=0, responseTimeTotal=2039, responseTimeMax=2039, responseTimeMin=2039, responseTimeAvg=2039.00, statDate=2026-02-05, statHour=17, createTime=2026-02-05T17:36:14, updateTime=null), SysMonitorApiPerformance(id=26, apiPath=/api/auth/captcha, apiMethod=GET, requestCount=1, successCount=1, errorCount=0, responseTimeTotal=2010, responseTimeMax=2010, responseTimeMin=2010, responseTimeAvg=2010.00, statDate=2026-02-05, statHour=18, createTime=2026-02-05T18:35:45, updateTime=null), SysMonitorApiPerformance(id=27, apiPath=/api/auth/captcha, apiMethod=GET, requestCount=1, successCount...', 0, '', '2026-02-12 17:53:03', 115);
INSERT INTO `sys_oper_log` VALUES (5621, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770889983386)', 0, '', '2026-02-12 17:53:03', 195);
INSERT INTO `sys_oper_log` VALUES (5622, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=8.996628822364244, used=365, free=3694}, duration=39, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311906685507838, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:53:04.552984800}, timestamp=1770889984591)', 0, '', '2026-02-12 17:53:05', 85);
INSERT INTO `sys_oper_log` VALUES (5623, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.9602363658746769,\"used\":0.473962821617629,\"wait\":0.0,\"free\":97.99335220977471},\"memory\":{\"total\":16236,\"used\":6732,\"free\":9503,\"usage\":41.46444389429296},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1069782,\"max\":4060,\"total\":850,\"used\":367,\"free\":3692,\"usage\":9.045889906108085},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311906685507838}},\"timestamp\":1770889985622}', 0, '', '2026-02-12 17:53:06', 1116);
INSERT INTO `sys_oper_log` VALUES (5624, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":969,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770889989434}', 0, '', '2026-02-12 17:53:09', 54);
INSERT INTO `sys_oper_log` VALUES (5625, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3390,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770889992039}', 0, '', '2026-02-12 17:53:12', 198);
INSERT INTO `sys_oper_log` VALUES (5626, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770889993320)', 0, '', '2026-02-12 17:53:13', 216);
INSERT INTO `sys_oper_log` VALUES (5627, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=9.242934241083455, used=375, free=3684}, duration=47, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311901616239597, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:53:14.560491800}, timestamp=1770889994607)', 0, '', '2026-02-12 17:53:15', 96);
INSERT INTO `sys_oper_log` VALUES (5628, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":1.2553073657005722,\"used\":0.2892129715094456,\"wait\":0.0,\"free\":97.78475170758722},\"memory\":{\"total\":16236,\"used\":6663,\"free\":9572,\"usage\":41.04290435712755},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1079780,\"max\":4060,\"total\":850,\"used\":377,\"free\":3682,\"usage\":9.292195324827297},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311905885097064}},\"timestamp\":1770889995623}', 0, '', '2026-02-12 17:53:16', 1112);
INSERT INTO `sys_oper_log` VALUES (5629, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":993,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770889999445}', 0, '', '2026-02-12 17:53:19', 45);
INSERT INTO `sys_oper_log` VALUES (5630, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3406,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890001939}', 0, '', '2026-02-12 17:53:22', 121);
INSERT INTO `sys_oper_log` VALUES (5631, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890003322)', 0, '', '2026-02-12 17:53:23', 214);
INSERT INTO `sys_oper_log` VALUES (5632, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=9.538500743546509, used=387, free=3672}, duration=44, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311910420758123, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:53:24.554262200}, timestamp=1770890004598)', 0, '', '2026-02-12 17:53:25', 92);
INSERT INTO `sys_oper_log` VALUES (5633, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.8615384615384616,\"used\":0.48615384615384616,\"wait\":0.0,\"free\":98.55384615384615},\"memory\":{\"total\":16236,\"used\":6665,\"free\":9570,\"usage\":41.0545008445948},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1089778,\"max\":4060,\"total\":850,\"used\":389,\"free\":3670,\"usage\":9.587761827290352},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311910420758123}},\"timestamp\":1770890005620}', 0, '', '2026-02-12 17:53:26', 1114);
INSERT INTO `sys_oper_log` VALUES (5634, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":1017,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890009434}', 0, '', '2026-02-12 17:53:29', 50);
INSERT INTO `sys_oper_log` VALUES (5635, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3422,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890011975}', 0, '', '2026-02-12 17:53:32', 162);
INSERT INTO `sys_oper_log` VALUES (5636, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890013305)', 0, '', '2026-02-12 17:53:33', 202);
INSERT INTO `sys_oper_log` VALUES (5637, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=9.834067246009564, used=399, free=3660}, duration=38, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311910687561713, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:53:34.559835700}, timestamp=1770890014597)', 0, '', '2026-02-12 17:53:35', 96);
INSERT INTO `sys_oper_log` VALUES (5638, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.4800295402794018,\"used\":0.09846759800603114,\"wait\":0.0,\"free\":98.75069235029848},\"memory\":{\"total\":16236,\"used\":6668,\"free\":9567,\"usage\":41.06968213835379},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1099780,\"max\":4060,\"total\":850,\"used\":401,\"free\":3658,\"usage\":9.883328329753406},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311910687561713}},\"timestamp\":1770890015627}', 0, '', '2026-02-12 17:53:36', 1126);
INSERT INTO `sys_oper_log` VALUES (5639, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":1041,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890019429}', 0, '', '2026-02-12 17:53:39', 53);
INSERT INTO `sys_oper_log` VALUES (5640, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3438,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890021943}', 0, '', '2026-02-12 17:53:42', 124);
INSERT INTO `sys_oper_log` VALUES (5641, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890023301)', 0, '', '2026-02-12 17:53:43', 202);
INSERT INTO `sys_oper_log` VALUES (5642, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=10.129633748472617, used=411, free=3648}, duration=38, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311915223222773, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:53:44.554406400}, timestamp=1770890024592)', 0, '', '2026-02-12 17:53:45', 85);
INSERT INTO `sys_oper_log` VALUES (5643, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.881305081567598,\"used\":0.7875492218263641,\"wait\":0.0,\"free\":98.2373898368648},\"memory\":{\"total\":16236,\"used\":6665,\"free\":9570,\"usage\":41.054284312671136},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1109767,\"max\":4060,\"total\":850,\"used\":413,\"free\":3646,\"usage\":10.17889483221646},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311915223222773}},\"timestamp\":1770890025605}', 0, '', '2026-02-12 17:53:46', 1098);
INSERT INTO `sys_oper_log` VALUES (5644, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":1065,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890029435}', 0, '', '2026-02-12 17:53:49', 54);
INSERT INTO `sys_oper_log` VALUES (5645, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3454,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890031933}', 0, '', '2026-02-12 17:53:52', 120);
INSERT INTO `sys_oper_log` VALUES (5646, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890033302)', 0, '', '2026-02-12 17:53:53', 208);
INSERT INTO `sys_oper_log` VALUES (5647, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=10.425200250935672, used=423, free=3636}, duration=37, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.31191655724073, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:53:54.561462800}, timestamp=1770890034598)', 0, '', '2026-02-12 17:53:55', 97);
INSERT INTO `sys_oper_log` VALUES (5648, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.9538461538461539,\"used\":0.28923076923076924,\"wait\":0.0,\"free\":98.56},\"memory\":{\"total\":16236,\"used\":6671,\"free\":9564,\"usage\":41.09068573494905},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1119774,\"max\":4060,\"total\":850,\"used\":425,\"free\":3634,\"usage\":10.474461334679514},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.31191655724073}},\"timestamp\":1770890035614}', 0, '', '2026-02-12 17:53:56', 1113);
INSERT INTO `sys_oper_log` VALUES (5649, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":1089,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890039429}', 0, '', '2026-02-12 17:53:59', 53);
INSERT INTO `sys_oper_log` VALUES (5650, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3470,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890041936}', 0, '', '2026-02-12 17:54:02', 117);
INSERT INTO `sys_oper_log` VALUES (5651, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890043283)', 0, '', '2026-02-12 17:54:03', 179);
INSERT INTO `sys_oper_log` VALUES (5652, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=10.720766753398726, used=435, free=3624}, duration=39, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311922160116156, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:54:04.554253500}, timestamp=1770890044593)', 0, '', '2026-02-12 17:54:05', 86);
INSERT INTO `sys_oper_log` VALUES (5653, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":1.2370753323485968,\"used\":0.5723781388478582,\"wait\":0.0,\"free\":97.99975381585426},\"memory\":{\"total\":16236,\"used\":6673,\"free\":9562,\"usage\":41.105554260373864},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1129782,\"max\":4060,\"total\":850,\"used\":437,\"free\":3622,\"usage\":10.77002783714257},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311922426919748}},\"timestamp\":1770890045622}', 0, '', '2026-02-12 17:54:06', 1115);
INSERT INTO `sys_oper_log` VALUES (5654, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":1113,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890049434}', 0, '', '2026-02-12 17:54:09', 53);
INSERT INTO `sys_oper_log` VALUES (5655, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3486,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890051943}', 0, '', '2026-02-12 17:54:12', 129);
INSERT INTO `sys_oper_log` VALUES (5656, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890053332)', 0, '', '2026-02-12 17:54:13', 199);
INSERT INTO `sys_oper_log` VALUES (5657, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=10.967072172117938, used=445, free=3614}, duration=50, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311926962580808, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:54:14.548367700}, timestamp=1770890054598)', 0, '', '2026-02-12 17:54:15', 92);
INSERT INTO `sys_oper_log` VALUES (5658, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":1.1568518860377823,\"used\":0.19691095932557998,\"wait\":0.0,\"free\":98.36317765060612},\"memory\":{\"total\":16236,\"used\":6673,\"free\":9563,\"usage\":41.10006878497442},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1139783,\"max\":4060,\"total\":850,\"used\":447,\"free\":3612,\"usage\":11.016333255861781},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311926962580808}},\"timestamp\":1770890055622}', 0, '', '2026-02-12 17:54:16', 1116);
INSERT INTO `sys_oper_log` VALUES (5659, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":1137,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890059442}', 0, '', '2026-02-12 17:54:19', 65);
INSERT INTO `sys_oper_log` VALUES (5660, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3502,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890061958}', 0, '', '2026-02-12 17:54:22', 138);
INSERT INTO `sys_oper_log` VALUES (5661, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890063305)', 0, '', '2026-02-12 17:54:23', 206);
INSERT INTO `sys_oper_log` VALUES (5662, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=11.262638674580993, used=457, free=3602}, duration=42, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311927496187991, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:54:24.561860600}, timestamp=1770890064603)', 0, '', '2026-02-12 17:54:25', 95);
INSERT INTO `sys_oper_log` VALUES (5663, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":1.2675616609428662,\"used\":0.4932875429285045,\"wait\":0.0,\"free\":98.23915079612863},\"memory\":{\"total\":16236,\"used\":6670,\"free\":9565,\"usage\":41.086908455836266},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1149771,\"max\":4060,\"total\":850,\"used\":459,\"free\":3600,\"usage\":11.311899758324834},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311927496187991}},\"timestamp\":1770890065609}', 0, '', '2026-02-12 17:54:26', 1101);
INSERT INTO `sys_oper_log` VALUES (5664, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":1164,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890069435}', 0, '', '2026-02-12 17:54:29', 51);
INSERT INTO `sys_oper_log` VALUES (5665, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3527,\"commandsPerSecond\":4.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890071939}', 0, '', '2026-02-12 17:54:32', 118);
INSERT INTO `sys_oper_log` VALUES (5666, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890073330)', 0, '', '2026-02-12 17:54:33', 232);
INSERT INTO `sys_oper_log` VALUES (5667, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=9, rows=[UserVO(userId=115, deptId=111, deptName=人事部, userName=xiaoming, nickName=xiaoming, userType=00, email=xiaoming@163.com, phonenumber=15588888888, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/115.webp?Expires=1771493004&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=FwGw6RsMwvDlOUUPjGbde%2B%2BMbsg%3D, status=0, loginIp=, loginDate=null, createTime=2026-02-09T18:19:02, remark=, roleIds=[5], roleNames=[注册用户], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=114, deptId=110, deptName=山西分公司, userName=starPivot, nickName=starPivot演示用户, userType=00, email=starPivot@163.com, phonenumber=13388888888, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/114.webp?Expires=1771219638&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=jS6ZhM4CsHr5DThRHmRdQew%2FlIk%3D, status=0, loginIp=, loginDate=null, createTime=2026-02-09T12:22:01, remark=111, roleIds=null, roleNames=null, postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=113, deptId=111, deptName=人事部, userName=wangwu, nickName=wangwu, userType=00, email=wangwu@163.com, phonenumber=13326541578, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/113.webp?Expires=1771492946&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=5DCWthMgcQ8Zl8QqJDJ2upy%2FYzM%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-27T18:35:41, remark=111, roleIds=null, roleNames=null, postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=112, deptId=100, deptName=星枢科技, userName=lisi, nickName=李四, userType=00, email=lisi@163.com, phonenumber=18855555555, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1771492951&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=GFErrGwlGIJMYzT5mSpYQfa4k5k%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-24T21:56:29, remark=, roleIds=n...', 0, '', '2026-02-12 17:54:34', 652);
INSERT INTO `sys_oper_log` VALUES (5668, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=11.804510595763258, used=479, free=3580}, duration=43, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311941903581943, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:54:34.590897500}, timestamp=1770890074633)', 0, '', '2026-02-12 17:54:35', 127);
INSERT INTO `sys_oper_log` VALUES (5669, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":1.6367216342603987,\"used\":0.9660349495446714,\"wait\":0.0,\"free\":97.39724341619493},\"memory\":{\"total\":16236,\"used\":6781,\"free\":9454,\"usage\":41.768166005883415},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1159777,\"max\":4060,\"total\":850,\"used\":481,\"free\":3578,\"usage\":11.853771679507101},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311942170385533}},\"timestamp\":1770890075615}', 0, '', '2026-02-12 17:54:36', 1109);
INSERT INTO `sys_oper_log` VALUES (5670, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=9, rows=[UserVO(userId=115, deptId=111, deptName=人事部, userName=xiaoming, nickName=xiaoming, userType=00, email=xiaoming@163.com, phonenumber=15588888888, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/115.webp?Expires=1771493004&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=FwGw6RsMwvDlOUUPjGbde%2B%2BMbsg%3D, status=0, loginIp=, loginDate=null, createTime=2026-02-09T18:19:02, remark=, roleIds=[5], roleNames=[注册用户], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=114, deptId=110, deptName=山西分公司, userName=starPivot, nickName=starPivot演示用户, userType=00, email=starPivot@163.com, phonenumber=13388888888, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/114.webp?Expires=1771219638&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=jS6ZhM4CsHr5DThRHmRdQew%2FlIk%3D, status=0, loginIp=, loginDate=null, createTime=2026-02-09T12:22:01, remark=111, roleIds=null, roleNames=null, postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=113, deptId=111, deptName=人事部, userName=wangwu, nickName=wangwu, userType=00, email=wangwu@163.com, phonenumber=13326541578, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/113.webp?Expires=1771492946&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=5DCWthMgcQ8Zl8QqJDJ2upy%2FYzM%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-27T18:35:41, remark=111, roleIds=null, roleNames=null, postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=112, deptId=100, deptName=星枢科技, userName=lisi, nickName=李四, userType=00, email=lisi@163.com, phonenumber=18855555555, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1771492951&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=GFErrGwlGIJMYzT5mSpYQfa4k5k%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-24T21:56:29, remark=, roleIds=n...', 0, '', '2026-02-12 17:54:37', 610);
INSERT INTO `sys_oper_log` VALUES (5671, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":1230,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890079431}', 0, '', '2026-02-12 17:54:39', 52);
INSERT INTO `sys_oper_log` VALUES (5672, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3578,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890081934}', 0, '', '2026-02-12 17:54:42', 125);
INSERT INTO `sys_oper_log` VALUES (5673, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890083302)', 0, '', '2026-02-12 17:54:43', 202);
INSERT INTO `sys_oper_log` VALUES (5674, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=12.198599265713996, used=495, free=3564}, duration=41, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311938968742433, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:54:44.556471400}, timestamp=1770890084597)', 0, '', '2026-02-12 17:54:45', 89);
INSERT INTO `sys_oper_log` VALUES (5675, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":3.7526914795447555,\"used\":0.0,\"wait\":0.0,\"free\":96.14887726853276},\"memory\":{\"total\":16236,\"used\":6790,\"free\":9445,\"usage\":41.82193810025957},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1169762,\"max\":4060,\"total\":850,\"used\":497,\"free\":3562,\"usage\":12.24786034945784},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311938968742433}},\"timestamp\":1770890085603}', 0, '', '2026-02-12 17:54:46', 1095);
INSERT INTO `sys_oper_log` VALUES (5676, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":1303,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890089426}', 0, '', '2026-02-12 17:54:49', 43);
INSERT INTO `sys_oper_log` VALUES (5677, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3603,\"commandsPerSecond\":4.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890091934}', 0, '', '2026-02-12 17:54:52', 119);
INSERT INTO `sys_oper_log` VALUES (5678, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890093280)', 0, '', '2026-02-12 17:54:53', 185);
INSERT INTO `sys_oper_log` VALUES (5679, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=12.789732270640105, used=519, free=3540}, duration=40, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311952042118426, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:54:54.559048800}, timestamp=1770890094599)', 0, '', '2026-02-12 17:54:55', 96);
INSERT INTO `sys_oper_log` VALUES (5680, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":2.3063640848544646,\"used\":0.1973359644795264,\"wait\":0.0,\"free\":97.49629995066601},\"memory\":{\"total\":16236,\"used\":6712,\"free\":9523,\"usage\":41.34061169306071},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1179762,\"max\":4060,\"total\":850,\"used\":521,\"free\":3538,\"usage\":12.838993354383948},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311956310975892}},\"timestamp\":1770890095606}', 0, '', '2026-02-12 17:54:56', 1103);
INSERT INTO `sys_oper_log` VALUES (5681, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":1331,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890099454}', 0, '', '2026-02-12 17:54:59', 63);
INSERT INTO `sys_oper_log` VALUES (5682, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3619,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890101948}', 0, '', '2026-02-12 17:55:02', 126);
INSERT INTO `sys_oper_log` VALUES (5683, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890103294)', 0, '', '2026-02-12 17:55:03', 193);
INSERT INTO `sys_oper_log` VALUES (5684, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=13.08529877310316, used=531, free=3528}, duration=43, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311956577779485, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:55:04.542668800}, timestamp=1770890104585)', 0, '', '2026-02-12 17:55:05', 81);
INSERT INTO `sys_oper_log` VALUES (5685, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":3.8942367795974495,\"used\":0.19377422177772222,\"wait\":0.0,\"free\":95.81822727840981},\"memory\":{\"total\":16236,\"used\":6713,\"free\":9522,\"usage\":41.35102928449913},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1189769,\"max\":4060,\"total\":850,\"used\":533,\"free\":3526,\"usage\":13.134559856847002},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311956577779485}},\"timestamp\":1770890105609}', 0, '', '2026-02-12 17:55:06', 1100);
INSERT INTO `sys_oper_log` VALUES (5686, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":1,\"activePeak\":2,\"usage\":5.0},\"sqlStat\":{\"executeCount\":1402,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890109437}', 0, '', '2026-02-12 17:55:09', 37);
INSERT INTO `sys_oper_log` VALUES (5687, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3639,\"commandsPerSecond\":2.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890111954}', 0, '', '2026-02-12 17:55:12', 129);
INSERT INTO `sys_oper_log` VALUES (5688, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890113281)', 0, '', '2026-02-12 17:55:13', 185);
INSERT INTO `sys_oper_log` VALUES (5689, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=13.479387443053898, used=547, free=3512}, duration=54, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311965915905194, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:55:14.546642300}, timestamp=1770890114600)', 0, '', '2026-02-12 17:55:15', 94);
INSERT INTO `sys_oper_log` VALUES (5690, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":1.9262723859929842,\"used\":0.19078097113668532,\"wait\":0.0,\"free\":97.59369807372761},\"memory\":{\"total\":16236,\"used\":6744,\"free\":9491,\"usage\":41.54285250976138},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1199820,\"max\":4060,\"total\":850,\"used\":549,\"free\":3510,\"usage\":13.52864852679774},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311965915905194}},\"timestamp\":1770890115659}', 0, '', '2026-02-12 17:55:16', 1153);
INSERT INTO `sys_oper_log` VALUES (5691, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":1475,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890119431}', 0, '', '2026-02-12 17:55:19', 45);
INSERT INTO `sys_oper_log` VALUES (5692, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3660,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890121936}', 0, '', '2026-02-12 17:55:22', 115);
INSERT INTO `sys_oper_log` VALUES (5693, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890123281)', 0, '', '2026-02-12 17:55:23', 185);
INSERT INTO `sys_oper_log` VALUES (5694, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=13.774953945516954, used=559, free=3500}, duration=47, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.311966182708785, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:55:24.562905300}, timestamp=1770890124609)', 0, '', '2026-02-12 17:55:25', 89);
INSERT INTO `sys_oper_log` VALUES (5695, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":2.694204685573366,\"used\":0.2959309494451295,\"wait\":0.0,\"free\":97.00986436498151},\"memory\":{\"total\":16236,\"used\":6721,\"free\":9514,\"usage\":41.401312808994064},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1209774,\"max\":4060,\"total\":850,\"used\":561,\"free\":3498,\"usage\":13.824215029260795},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311966449512378}},\"timestamp\":1770890125617}', 0, '', '2026-02-12 17:55:26', 1097);
INSERT INTO `sys_oper_log` VALUES (5696, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":1499,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890129427}', 0, '', '2026-02-12 17:55:29', 48);
INSERT INTO `sys_oper_log` VALUES (5697, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3676,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890131938}', 0, '', '2026-02-12 17:55:32', 120);
INSERT INTO `sys_oper_log` VALUES (5698, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890133286)', 0, '', '2026-02-12 17:55:33', 185);
INSERT INTO `sys_oper_log` VALUES (5699, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=14.070520447980007, used=571, free=3488}, duration=56, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.3119368343137, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:55:34.555014600}, timestamp=1770890134611)', 0, '', '2026-02-12 17:55:35', 105);
INSERT INTO `sys_oper_log` VALUES (5700, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":3.951255539143279,\"used\":2.7695716395864105,\"wait\":0.0,\"free\":93.27917282127031},\"memory\":{\"total\":16236,\"used\":6930,\"free\":9305,\"usage\":42.684577225027915},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1219763,\"max\":4060,\"total\":850,\"used\":573,\"free\":3486,\"usage\":14.119781531723849},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.311925361759256}},\"timestamp\":1770890135616}', 0, '', '2026-02-12 17:55:36', 1110);
INSERT INTO `sys_oper_log` VALUES (5701, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":1523,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890139427}', 0, '', '2026-02-12 17:55:39', 46);
INSERT INTO `sys_oper_log` VALUES (5702, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3692,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890141954}', 0, '', '2026-02-12 17:55:42', 133);
INSERT INTO `sys_oper_log` VALUES (5703, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890143285)', 0, '', '2026-02-12 17:55:43', 183);
INSERT INTO `sys_oper_log` VALUES (5704, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=14.36608695044306, used=583, free=3476}, duration=50, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.312055028304819, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:55:44.554756200}, timestamp=1770890144604)', 0, '', '2026-02-12 17:55:45', 94);
INSERT INTO `sys_oper_log` VALUES (5705, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":3.5635155096011815,\"used\":0.19079271294928607,\"wait\":0.0,\"free\":96.14721811915312},\"memory\":{\"total\":16236,\"used\":6906,\"free\":9329,\"usage\":42.53733551693749},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1229763,\"max\":4060,\"total\":850,\"used\":585,\"free\":3474,\"usage\":14.415348034186904},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.312055295108411}},\"timestamp\":1770890145603}', 0, '', '2026-02-12 17:55:46', 1091);
INSERT INTO `sys_oper_log` VALUES (5706, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":1547,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890149427}', 0, '', '2026-02-12 17:55:49', 47);
INSERT INTO `sys_oper_log` VALUES (5707, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3708,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890151944}', 0, '', '2026-02-12 17:55:52', 128);
INSERT INTO `sys_oper_log` VALUES (5708, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890153278)', 0, '', '2026-02-12 17:55:53', 182);
INSERT INTO `sys_oper_log` VALUES (5709, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=14.612392369162272, used=593, free=3466}, duration=42, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.312056629126369, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:55:54.555526600}, timestamp=1770890154597)', 0, '', '2026-02-12 17:55:55', 89);
INSERT INTO `sys_oper_log` VALUES (5710, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":4.22821270310192,\"used\":0.19079271294928607,\"wait\":0.0,\"free\":95.3902018709995},\"memory\":{\"total\":16236,\"used\":6913,\"free\":9322,\"usage\":42.5819892114172},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1239762,\"max\":4060,\"total\":850,\"used\":597,\"free\":3462,\"usage\":14.710914536649957},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.312056629126369}},\"timestamp\":1770890155605}', 0, '', '2026-02-12 17:55:56', 1097);
INSERT INTO `sys_oper_log` VALUES (5711, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":1,\"activePeak\":2,\"usage\":5.0},\"sqlStat\":{\"executeCount\":1580,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890159422}', 0, '', '2026-02-12 17:55:59', 37);
INSERT INTO `sys_oper_log` VALUES (5712, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3728,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890161934}', 0, '', '2026-02-12 17:56:02', 113);
INSERT INTO `sys_oper_log` VALUES (5713, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890163287)', 0, '', '2026-02-12 17:56:03', 187);
INSERT INTO `sys_oper_log` VALUES (5714, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=14.957219955369169, used=607, free=3452}, duration=43, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.312061164787428, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:56:04.542373}, timestamp=1770890164585)', 0, '', '2026-02-12 17:56:05', 76);
INSERT INTO `sys_oper_log` VALUES (5715, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":2.0183373330871945,\"used\":0.0,\"wait\":0.0,\"free\":97.58784074826164},\"memory\":{\"total\":16236,\"used\":6906,\"free\":9329,\"usage\":42.53870688578735},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1249778,\"max\":4060,\"total\":850,\"used\":611,\"free\":3448,\"usage\":15.055742122856854},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.312061164787428}},\"timestamp\":1770890165620}', 0, '', '2026-02-12 17:56:06', 1111);
INSERT INTO `sys_oper_log` VALUES (5716, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":1623,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890169430}', 0, '', '2026-02-12 17:56:09', 47);
INSERT INTO `sys_oper_log` VALUES (5717, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3749,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890171936}', 0, '', '2026-02-12 17:56:12', 119);
INSERT INTO `sys_oper_log` VALUES (5718, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890173290)', 0, '', '2026-02-12 17:56:13', 193);
INSERT INTO `sys_oper_log` VALUES (5719, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=15.252786457832224, used=619, free=3440}, duration=43, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.31206596725208, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:56:14.548831100}, timestamp=1770890174591)', 0, '', '2026-02-12 17:56:15', 87);
INSERT INTO `sys_oper_log` VALUES (5720, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":3.5562665354088474,\"used\":0.0,\"wait\":0.0,\"free\":96.34529010028918},\"memory\":{\"total\":16236,\"used\":6898,\"free\":9337,\"usage\":42.48820682936876},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1259762,\"max\":4060,\"total\":850,\"used\":623,\"free\":3436,\"usage\":15.35130862531991},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.31206596725208}},\"timestamp\":1770890175604}', 0, '', '2026-02-12 17:56:16', 1100);
INSERT INTO `sys_oper_log` VALUES (5721, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":1647,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890179425}', 0, '', '2026-02-12 17:56:19', 46);
INSERT INTO `sys_oper_log` VALUES (5722, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3765,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890181939}', 0, '', '2026-02-12 17:56:22', 115);
INSERT INTO `sys_oper_log` VALUES (5723, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10, loginTime=2026-02-12T17:36:35.917, lastAccessTime=2026-02-12T17:36:35.917)], timestamp=1770890183281)', 0, '', '2026-02-12 17:56:23', 187);
INSERT INTO `sys_oper_log` VALUES (5724, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=15.499091876551436, used=629, free=3430}, duration=42, database={activeCount=1, healthy=true, usage=5.0, maxActive=20}, disk={total=1429, healthy=true, usage=15.312070502913137, used=218, free=1210}, overall=healthy, redis={healthy=true}, timestamp=2026-02-12T17:56:24.549042700}, timestamp=1770890184591)', 0, '', '2026-02-12 17:56:25', 85);
INSERT INTO `sys_oper_log` VALUES (5725, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":3.6604121808674255,\"used\":0.676714856967087,\"wait\":0.0,\"free\":95.46601045832051},\"memory\":{\"total\":16236,\"used\":6900,\"free\":9335,\"usage\":42.50362871415404},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770888915837,\"runTime\":1269781,\"max\":4060,\"total\":850,\"used\":633,\"free\":3426,\"usage\":15.59761404403912},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":218,\"free\":1210,\"usage\":15.312070502913137}},\"timestamp\":1770890185621}', 0, '', '2026-02-12 17:56:26', 1115);
INSERT INTO `sys_oper_log` VALUES (5726, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-521097739\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":0,\"minIdle\":2,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":1695,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770890189421}', 0, '', '2026-02-12 17:56:29', 42);
INSERT INTO `sys_oper_log` VALUES (5727, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.4\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":8,\"expiredKeys\":null},\"commands\":{\"totalCommands\":3785,\"commandsPerSecond\":1.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770890191945}', 0, '', '2026-02-12 17:56:32', 123);
INSERT INTO `sys_oper_log` VALUES (5728, '用户登录', 0, 'com.star.pivot.controller.AuthController.login()', 'POST', 0, 'anonymousUser', '', '/api/auth/login', '0:0:0:0:0:0:0:1', '', '[{\"username\":\"admin\",\"password\":\"******\",\"captchaProof\":\"h4ZD1fstYtPSG8bJ6-OEAo72QdgXG4Ux4H_5qYTF25E\"}]', '{\"code\":200,\"message\":\"登录成功\",\"data\":{\"token\":\"eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc3MTE0NTI3OCwiZXhwIjoxNzcxMjMxNjc4fQ.5H9NvbKtpne-r1ML2Akz4X22Tef2VnRTUtNI_dUJIzw\",\"username\":\"admin\",\"nickname\":\"超级管理员\",\"refreshToken\":\"35eb0f17a0614396a09f0916592454ac\"},\"timestamp\":1771145279085}', 0, '', '2026-02-15 16:47:59', 2114);
INSERT INTO `sys_oper_log` VALUES (5729, '用户登录', 0, 'com.star.pivot.controller.AuthController.login()', 'POST', 0, 'anonymousUser', '', '/api/auth/login', '0:0:0:0:0:0:0:1', '', '[{\"username\":\"admin\",\"password\":\"******\",\"captchaProof\":\"JLJgS3UKni2er73yYR36DgW6hfi9AVttithCrGBJzwM\"}]', '{\"code\":200,\"message\":\"登录成功\",\"data\":{\"token\":\"eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc3MTE0ODg4MSwiZXhwIjoxNzcxMjM1MjgxfQ.JQ6FHedyjQ9wh0pdE0XKM8e4CRz4T-0VTaa8ZFfV9oA\",\"username\":\"admin\",\"nickname\":\"超级管理员\",\"refreshToken\":\"fe93f97b1a264e5aac1f6051b098ed91\"},\"timestamp\":1771148881536}', 0, '', '2026-02-15 17:48:02', 2061);

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int(11) NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '岗位信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES (1, 'ceo', '董事长', 1, '0', 'admin', '2025-12-28 13:46:34', 'admin', '2026-01-19 21:39:13', '');
INSERT INTO `sys_post` VALUES (2, 'se', '项目经理', 2, '0', 'admin', '2025-12-28 13:46:34', '', NULL, '');
INSERT INTO `sys_post` VALUES (3, 'hr', '人力资源', 3, '0', 'admin', '2025-12-28 13:46:34', '', NULL, '');
INSERT INTO `sys_post` VALUES (4, 'user', '普通员工', 4, '0', 'admin', '2025-12-28 13:46:34', '', NULL, '');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(11) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 103 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2025-12-28 13:46:34', 'admin', '2026-01-23 19:59:34', '超级管理员：拥有所有菜单权限');
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', '2025-12-28 13:46:34', 'admin', '2026-02-12 17:54:47', '普通角色');
INSERT INTO `sys_role` VALUES (3, '测试专属', 'test', 3, '2', 1, 1, '0', '0', '', '2026-01-03 17:06:10', 'admin', '2026-02-12 17:55:07', '专属于测试的角色');
INSERT INTO `sys_role` VALUES (4, '演示专属', 'yanshi', 4, '4', 0, 1, '0', '0', 'admin', '2026-02-09 12:25:59', 'admin', '2026-02-12 17:55:59', '演示专属角色');
INSERT INTO `sys_role` VALUES (5, '注册用户', 'register_role', 5, '5', 0, 1, '0', '0', 'admin', '2026-02-12 16:57:43', 'admin', '2026-02-12 17:56:28', '注册时，新用户初始化角色');

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_role_dept`(`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 178 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色与部门关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------
INSERT INTO `sys_role_dept` VALUES (167, 2, 100);
INSERT INTO `sys_role_dept` VALUES (168, 2, 101);
INSERT INTO `sys_role_dept` VALUES (169, 2, 104);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_role_menu`(`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 741 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色与菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (397, 1, 1);
INSERT INTO `sys_role_menu` VALUES (399, 1, 2);
INSERT INTO `sys_role_menu` VALUES (398, 1, 3);
INSERT INTO `sys_role_menu` VALUES (400, 1, 4);
INSERT INTO `sys_role_menu` VALUES (405, 1, 5);
INSERT INTO `sys_role_menu` VALUES (411, 1, 6);
INSERT INTO `sys_role_menu` VALUES (419, 1, 8);
INSERT INTO `sys_role_menu` VALUES (424, 1, 9);
INSERT INTO `sys_role_menu` VALUES (429, 1, 12);
INSERT INTO `sys_role_menu` VALUES (412, 1, 39);
INSERT INTO `sys_role_menu` VALUES (414, 1, 40);
INSERT INTO `sys_role_menu` VALUES (415, 1, 41);
INSERT INTO `sys_role_menu` VALUES (406, 1, 42);
INSERT INTO `sys_role_menu` VALUES (407, 1, 43);
INSERT INTO `sys_role_menu` VALUES (408, 1, 44);
INSERT INTO `sys_role_menu` VALUES (409, 1, 45);
INSERT INTO `sys_role_menu` VALUES (416, 1, 46);
INSERT INTO `sys_role_menu` VALUES (420, 1, 47);
INSERT INTO `sys_role_menu` VALUES (421, 1, 48);
INSERT INTO `sys_role_menu` VALUES (422, 1, 49);
INSERT INTO `sys_role_menu` VALUES (423, 1, 50);
INSERT INTO `sys_role_menu` VALUES (425, 1, 51);
INSERT INTO `sys_role_menu` VALUES (426, 1, 52);
INSERT INTO `sys_role_menu` VALUES (427, 1, 53);
INSERT INTO `sys_role_menu` VALUES (428, 1, 54);
INSERT INTO `sys_role_menu` VALUES (401, 1, 55);
INSERT INTO `sys_role_menu` VALUES (402, 1, 56);
INSERT INTO `sys_role_menu` VALUES (403, 1, 57);
INSERT INTO `sys_role_menu` VALUES (404, 1, 58);
INSERT INTO `sys_role_menu` VALUES (417, 1, 59);
INSERT INTO `sys_role_menu` VALUES (413, 1, 60);
INSERT INTO `sys_role_menu` VALUES (410, 1, 61);
INSERT INTO `sys_role_menu` VALUES (430, 1, 62);
INSERT INTO `sys_role_menu` VALUES (431, 1, 63);
INSERT INTO `sys_role_menu` VALUES (432, 1, 64);
INSERT INTO `sys_role_menu` VALUES (434, 1, 66);
INSERT INTO `sys_role_menu` VALUES (435, 1, 67);
INSERT INTO `sys_role_menu` VALUES (436, 1, 68);
INSERT INTO `sys_role_menu` VALUES (433, 1, 69);
INSERT INTO `sys_role_menu` VALUES (437, 1, 70);
INSERT INTO `sys_role_menu` VALUES (445, 1, 71);
INSERT INTO `sys_role_menu` VALUES (446, 1, 72);
INSERT INTO `sys_role_menu` VALUES (447, 1, 73);
INSERT INTO `sys_role_menu` VALUES (438, 1, 74);
INSERT INTO `sys_role_menu` VALUES (439, 1, 75);
INSERT INTO `sys_role_menu` VALUES (442, 1, 76);
INSERT INTO `sys_role_menu` VALUES (441, 1, 77);
INSERT INTO `sys_role_menu` VALUES (440, 1, 78);
INSERT INTO `sys_role_menu` VALUES (443, 1, 79);
INSERT INTO `sys_role_menu` VALUES (444, 1, 80);
INSERT INTO `sys_role_menu` VALUES (449, 1, 81);
INSERT INTO `sys_role_menu` VALUES (450, 1, 82);
INSERT INTO `sys_role_menu` VALUES (451, 1, 83);
INSERT INTO `sys_role_menu` VALUES (452, 1, 84);
INSERT INTO `sys_role_menu` VALUES (453, 1, 85);
INSERT INTO `sys_role_menu` VALUES (448, 1, 86);
INSERT INTO `sys_role_menu` VALUES (418, 1, 87);
INSERT INTO `sys_role_menu` VALUES (589, 2, 1);
INSERT INTO `sys_role_menu` VALUES (590, 2, 4);
INSERT INTO `sys_role_menu` VALUES (595, 2, 5);
INSERT INTO `sys_role_menu` VALUES (601, 2, 6);
INSERT INTO `sys_role_menu` VALUES (607, 2, 8);
INSERT INTO `sys_role_menu` VALUES (611, 2, 9);
INSERT INTO `sys_role_menu` VALUES (615, 2, 12);
INSERT INTO `sys_role_menu` VALUES (602, 2, 39);
INSERT INTO `sys_role_menu` VALUES (604, 2, 40);
INSERT INTO `sys_role_menu` VALUES (596, 2, 42);
INSERT INTO `sys_role_menu` VALUES (597, 2, 43);
INSERT INTO `sys_role_menu` VALUES (598, 2, 44);
INSERT INTO `sys_role_menu` VALUES (599, 2, 45);
INSERT INTO `sys_role_menu` VALUES (605, 2, 46);
INSERT INTO `sys_role_menu` VALUES (608, 2, 47);
INSERT INTO `sys_role_menu` VALUES (609, 2, 48);
INSERT INTO `sys_role_menu` VALUES (610, 2, 49);
INSERT INTO `sys_role_menu` VALUES (612, 2, 51);
INSERT INTO `sys_role_menu` VALUES (613, 2, 52);
INSERT INTO `sys_role_menu` VALUES (614, 2, 53);
INSERT INTO `sys_role_menu` VALUES (591, 2, 55);
INSERT INTO `sys_role_menu` VALUES (592, 2, 56);
INSERT INTO `sys_role_menu` VALUES (593, 2, 57);
INSERT INTO `sys_role_menu` VALUES (594, 2, 58);
INSERT INTO `sys_role_menu` VALUES (606, 2, 59);
INSERT INTO `sys_role_menu` VALUES (603, 2, 60);
INSERT INTO `sys_role_menu` VALUES (600, 2, 61);
INSERT INTO `sys_role_menu` VALUES (616, 2, 62);
INSERT INTO `sys_role_menu` VALUES (617, 2, 63);
INSERT INTO `sys_role_menu` VALUES (618, 2, 64);
INSERT INTO `sys_role_menu` VALUES (619, 2, 69);
INSERT INTO `sys_role_menu` VALUES (620, 2, 70);
INSERT INTO `sys_role_menu` VALUES (621, 2, 71);
INSERT INTO `sys_role_menu` VALUES (622, 2, 72);
INSERT INTO `sys_role_menu` VALUES (623, 2, 73);
INSERT INTO `sys_role_menu` VALUES (624, 2, 81);
INSERT INTO `sys_role_menu` VALUES (625, 3, 1);
INSERT INTO `sys_role_menu` VALUES (626, 3, 4);
INSERT INTO `sys_role_menu` VALUES (631, 3, 5);
INSERT INTO `sys_role_menu` VALUES (641, 3, 6);
INSERT INTO `sys_role_menu` VALUES (651, 3, 8);
INSERT INTO `sys_role_menu` VALUES (656, 3, 9);
INSERT INTO `sys_role_menu` VALUES (661, 3, 12);
INSERT INTO `sys_role_menu` VALUES (642, 3, 39);
INSERT INTO `sys_role_menu` VALUES (644, 3, 40);
INSERT INTO `sys_role_menu` VALUES (645, 3, 41);
INSERT INTO `sys_role_menu` VALUES (632, 3, 42);
INSERT INTO `sys_role_menu` VALUES (633, 3, 43);
INSERT INTO `sys_role_menu` VALUES (634, 3, 44);
INSERT INTO `sys_role_menu` VALUES (635, 3, 45);
INSERT INTO `sys_role_menu` VALUES (646, 3, 46);
INSERT INTO `sys_role_menu` VALUES (652, 3, 47);
INSERT INTO `sys_role_menu` VALUES (653, 3, 48);
INSERT INTO `sys_role_menu` VALUES (654, 3, 49);
INSERT INTO `sys_role_menu` VALUES (655, 3, 50);
INSERT INTO `sys_role_menu` VALUES (657, 3, 51);
INSERT INTO `sys_role_menu` VALUES (658, 3, 52);
INSERT INTO `sys_role_menu` VALUES (659, 3, 53);
INSERT INTO `sys_role_menu` VALUES (660, 3, 54);
INSERT INTO `sys_role_menu` VALUES (627, 3, 55);
INSERT INTO `sys_role_menu` VALUES (628, 3, 56);
INSERT INTO `sys_role_menu` VALUES (629, 3, 57);
INSERT INTO `sys_role_menu` VALUES (630, 3, 58);
INSERT INTO `sys_role_menu` VALUES (647, 3, 59);
INSERT INTO `sys_role_menu` VALUES (643, 3, 60);
INSERT INTO `sys_role_menu` VALUES (636, 3, 61);
INSERT INTO `sys_role_menu` VALUES (662, 3, 62);
INSERT INTO `sys_role_menu` VALUES (663, 3, 63);
INSERT INTO `sys_role_menu` VALUES (664, 3, 64);
INSERT INTO `sys_role_menu` VALUES (666, 3, 66);
INSERT INTO `sys_role_menu` VALUES (667, 3, 67);
INSERT INTO `sys_role_menu` VALUES (668, 3, 68);
INSERT INTO `sys_role_menu` VALUES (665, 3, 69);
INSERT INTO `sys_role_menu` VALUES (669, 3, 70);
INSERT INTO `sys_role_menu` VALUES (683, 3, 71);
INSERT INTO `sys_role_menu` VALUES (684, 3, 72);
INSERT INTO `sys_role_menu` VALUES (686, 3, 73);
INSERT INTO `sys_role_menu` VALUES (670, 3, 74);
INSERT INTO `sys_role_menu` VALUES (671, 3, 75);
INSERT INTO `sys_role_menu` VALUES (674, 3, 76);
INSERT INTO `sys_role_menu` VALUES (673, 3, 77);
INSERT INTO `sys_role_menu` VALUES (672, 3, 78);
INSERT INTO `sys_role_menu` VALUES (675, 3, 79);
INSERT INTO `sys_role_menu` VALUES (676, 3, 80);
INSERT INTO `sys_role_menu` VALUES (687, 3, 81);
INSERT INTO `sys_role_menu` VALUES (688, 3, 82);
INSERT INTO `sys_role_menu` VALUES (689, 3, 83);
INSERT INTO `sys_role_menu` VALUES (690, 3, 84);
INSERT INTO `sys_role_menu` VALUES (691, 3, 85);
INSERT INTO `sys_role_menu` VALUES (685, 3, 86);
INSERT INTO `sys_role_menu` VALUES (648, 3, 87);
INSERT INTO `sys_role_menu` VALUES (637, 3, 88);
INSERT INTO `sys_role_menu` VALUES (639, 3, 89);
INSERT INTO `sys_role_menu` VALUES (638, 3, 90);
INSERT INTO `sys_role_menu` VALUES (640, 3, 91);
INSERT INTO `sys_role_menu` VALUES (692, 3, 92);
INSERT INTO `sys_role_menu` VALUES (649, 3, 93);
INSERT INTO `sys_role_menu` VALUES (650, 3, 94);
INSERT INTO `sys_role_menu` VALUES (693, 3, 95);
INSERT INTO `sys_role_menu` VALUES (694, 3, 96);
INSERT INTO `sys_role_menu` VALUES (696, 3, 97);
INSERT INTO `sys_role_menu` VALUES (697, 3, 98);
INSERT INTO `sys_role_menu` VALUES (701, 3, 99);
INSERT INTO `sys_role_menu` VALUES (702, 3, 100);
INSERT INTO `sys_role_menu` VALUES (708, 3, 107);
INSERT INTO `sys_role_menu` VALUES (698, 3, 114);
INSERT INTO `sys_role_menu` VALUES (699, 3, 115);
INSERT INTO `sys_role_menu` VALUES (695, 3, 116);
INSERT INTO `sys_role_menu` VALUES (700, 3, 117);
INSERT INTO `sys_role_menu` VALUES (677, 3, 118);
INSERT INTO `sys_role_menu` VALUES (678, 3, 119);
INSERT INTO `sys_role_menu` VALUES (679, 3, 120);
INSERT INTO `sys_role_menu` VALUES (680, 3, 121);
INSERT INTO `sys_role_menu` VALUES (681, 3, 122);
INSERT INTO `sys_role_menu` VALUES (682, 3, 123);
INSERT INTO `sys_role_menu` VALUES (703, 3, 124);
INSERT INTO `sys_role_menu` VALUES (704, 3, 125);
INSERT INTO `sys_role_menu` VALUES (705, 3, 126);
INSERT INTO `sys_role_menu` VALUES (706, 3, 127);
INSERT INTO `sys_role_menu` VALUES (707, 3, 128);
INSERT INTO `sys_role_menu` VALUES (717, 4, 1);
INSERT INTO `sys_role_menu` VALUES (718, 4, 4);
INSERT INTO `sys_role_menu` VALUES (719, 4, 5);
INSERT INTO `sys_role_menu` VALUES (720, 4, 6);
INSERT INTO `sys_role_menu` VALUES (721, 4, 8);
INSERT INTO `sys_role_menu` VALUES (722, 4, 9);
INSERT INTO `sys_role_menu` VALUES (723, 4, 12);
INSERT INTO `sys_role_menu` VALUES (710, 4, 45);
INSERT INTO `sys_role_menu` VALUES (711, 4, 46);
INSERT INTO `sys_role_menu` VALUES (712, 4, 47);
INSERT INTO `sys_role_menu` VALUES (713, 4, 51);
INSERT INTO `sys_role_menu` VALUES (709, 4, 55);
INSERT INTO `sys_role_menu` VALUES (714, 4, 69);
INSERT INTO `sys_role_menu` VALUES (715, 4, 70);
INSERT INTO `sys_role_menu` VALUES (724, 4, 118);
INSERT INTO `sys_role_menu` VALUES (716, 4, 119);
INSERT INTO `sys_role_menu` VALUES (733, 5, 1);
INSERT INTO `sys_role_menu` VALUES (734, 5, 4);
INSERT INTO `sys_role_menu` VALUES (735, 5, 5);
INSERT INTO `sys_role_menu` VALUES (736, 5, 6);
INSERT INTO `sys_role_menu` VALUES (737, 5, 8);
INSERT INTO `sys_role_menu` VALUES (738, 5, 9);
INSERT INTO `sys_role_menu` VALUES (739, 5, 12);
INSERT INTO `sys_role_menu` VALUES (726, 5, 45);
INSERT INTO `sys_role_menu` VALUES (727, 5, 46);
INSERT INTO `sys_role_menu` VALUES (728, 5, 47);
INSERT INTO `sys_role_menu` VALUES (729, 5, 51);
INSERT INTO `sys_role_menu` VALUES (725, 5, 55);
INSERT INTO `sys_role_menu` VALUES (730, 5, 69);
INSERT INTO `sys_role_menu` VALUES (731, 5, 70);
INSERT INTO `sys_role_menu` VALUES (740, 5, 118);
INSERT INTO `sys_role_menu` VALUES (732, 5, 119);
INSERT INTO `sys_role_menu` VALUES (549, 100, 1);
INSERT INTO `sys_role_menu` VALUES (550, 100, 4);
INSERT INTO `sys_role_menu` VALUES (551, 100, 5);
INSERT INTO `sys_role_menu` VALUES (552, 100, 6);
INSERT INTO `sys_role_menu` VALUES (553, 100, 8);
INSERT INTO `sys_role_menu` VALUES (554, 100, 9);
INSERT INTO `sys_role_menu` VALUES (555, 100, 12);
INSERT INTO `sys_role_menu` VALUES (538, 100, 45);
INSERT INTO `sys_role_menu` VALUES (539, 100, 46);
INSERT INTO `sys_role_menu` VALUES (540, 100, 47);
INSERT INTO `sys_role_menu` VALUES (541, 100, 51);
INSERT INTO `sys_role_menu` VALUES (537, 100, 55);
INSERT INTO `sys_role_menu` VALUES (542, 100, 69);
INSERT INTO `sys_role_menu` VALUES (543, 100, 70);
INSERT INTO `sys_role_menu` VALUES (560, 100, 71);
INSERT INTO `sys_role_menu` VALUES (561, 100, 72);
INSERT INTO `sys_role_menu` VALUES (547, 100, 73);
INSERT INTO `sys_role_menu` VALUES (557, 100, 74);
INSERT INTO `sys_role_menu` VALUES (558, 100, 75);
INSERT INTO `sys_role_menu` VALUES (559, 100, 76);
INSERT INTO `sys_role_menu` VALUES (545, 100, 78);
INSERT INTO `sys_role_menu` VALUES (546, 100, 79);
INSERT INTO `sys_role_menu` VALUES (548, 100, 81);
INSERT INTO `sys_role_menu` VALUES (556, 100, 118);
INSERT INTO `sys_role_menu` VALUES (544, 100, 119);
INSERT INTO `sys_role_menu` VALUES (510, 101, 1);
INSERT INTO `sys_role_menu` VALUES (511, 101, 4);
INSERT INTO `sys_role_menu` VALUES (513, 101, 5);
INSERT INTO `sys_role_menu` VALUES (515, 101, 6);
INSERT INTO `sys_role_menu` VALUES (518, 101, 8);
INSERT INTO `sys_role_menu` VALUES (520, 101, 9);
INSERT INTO `sys_role_menu` VALUES (522, 101, 12);
INSERT INTO `sys_role_menu` VALUES (514, 101, 45);
INSERT INTO `sys_role_menu` VALUES (516, 101, 46);
INSERT INTO `sys_role_menu` VALUES (519, 101, 47);
INSERT INTO `sys_role_menu` VALUES (521, 101, 51);
INSERT INTO `sys_role_menu` VALUES (512, 101, 55);
INSERT INTO `sys_role_menu` VALUES (523, 101, 69);
INSERT INTO `sys_role_menu` VALUES (524, 101, 70);
INSERT INTO `sys_role_menu` VALUES (532, 101, 71);
INSERT INTO `sys_role_menu` VALUES (533, 101, 72);
INSERT INTO `sys_role_menu` VALUES (534, 101, 73);
INSERT INTO `sys_role_menu` VALUES (527, 101, 74);
INSERT INTO `sys_role_menu` VALUES (528, 101, 75);
INSERT INTO `sys_role_menu` VALUES (530, 101, 76);
INSERT INTO `sys_role_menu` VALUES (529, 101, 78);
INSERT INTO `sys_role_menu` VALUES (531, 101, 79);
INSERT INTO `sys_role_menu` VALUES (535, 101, 81);
INSERT INTO `sys_role_menu` VALUES (536, 101, 85);
INSERT INTO `sys_role_menu` VALUES (517, 101, 94);
INSERT INTO `sys_role_menu` VALUES (525, 101, 118);
INSERT INTO `sys_role_menu` VALUES (526, 101, 119);
INSERT INTO `sys_role_menu` VALUES (587, 102, 1);
INSERT INTO `sys_role_menu` VALUES (577, 102, 4);
INSERT INTO `sys_role_menu` VALUES (588, 102, 5);
INSERT INTO `sys_role_menu` VALUES (580, 102, 6);
INSERT INTO `sys_role_menu` VALUES (583, 102, 8);
INSERT INTO `sys_role_menu` VALUES (585, 102, 9);
INSERT INTO `sys_role_menu` VALUES (579, 102, 45);
INSERT INTO `sys_role_menu` VALUES (581, 102, 46);
INSERT INTO `sys_role_menu` VALUES (584, 102, 47);
INSERT INTO `sys_role_menu` VALUES (586, 102, 51);
INSERT INTO `sys_role_menu` VALUES (578, 102, 55);
INSERT INTO `sys_role_menu` VALUES (582, 102, 94);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime(0) NULL DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE,
  INDEX `idx_username`(`user_name`) USING BTREE,
  INDEX `idx_phone`(`phonenumber`) USING BTREE,
  INDEX `idx_email`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 116 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 100, 'admin', '超级管理员', '00', 'admin@163.com', '18518712884', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/1.jpeg?Expires=1771492967&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=%2FOlFm1%2BjEWE9ct6178m5Ylc%2FeLU%3D', '$2a$10$FTKzzUPrLDbNfKoWn9e1y.B6D0MX0HeHBPHFRfcx3R2Ane73gMbf6', '0', '0', '0:0:0:0:0:0:0:1', '2025-12-29 22:16:38', '2026-01-04 18:13:47', 'admin', '2025-12-28 13:46:34', 'admin', '2026-02-12 17:22:49', '超级管理员');
INSERT INTO `sys_user` VALUES (2, 105, 'user', '用户管理员', '00', 'user@qq.com', '15666666666', '1', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/2.webp?Expires=1771492975&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=crgqBsWiZLm6O%2BvLufm%2BJF4wYd4%3D', '$2a$12$eYocwR0zs3iWKl7gsvHVQ.KLDTWvXqecm.29aXPi3IAF.mmkARVR.', '0', '0', '0:0:0:0:0:0:0:1', '2025-12-29 22:08:54', '2025-12-28 13:46:34', 'admin', '2025-12-28 13:46:34', 'admin', '2026-02-12 17:22:56', '测试员--用户管理员');
INSERT INTO `sys_user` VALUES (100, 100, 'xinxin', '超级管理员', '00', '123@qq.com', '18834581458', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/100.webp?Expires=1771492963&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=E%2BgvtJ2%2BeQVCUTfqe17%2B%2Ba%2BDoaw%3D', '$2a$12$eYocwR0zs3iWKl7gsvHVQ.KLDTWvXqecm.29aXPi3IAF.mmkARVR.', '0', '0', '', NULL, NULL, 'admin', '2026-01-04 15:34:36', 'admin', '2026-02-12 17:22:45', '作者，超级管理员');
INSERT INTO `sys_user` VALUES (101, 105, 'test', '测试用户', '00', '123@qq.com', '18825454547', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/101.webp?Expires=1771492959&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=EIhrjRWOnchJpD38pJ1gVvqsyI8%3D', '$2a$10$YdqAWweActfkWOaWEjz9p.bBWqNWVdT9EQ2OHcUODgFg.f3ma13Va', '0', '0', '', NULL, NULL, 'admin', '2026-01-04 16:50:12', 'admin', '2026-02-12 17:22:41', '测试用户专属');
INSERT INTO `sys_user` VALUES (102, 107, 'zhangsan', '张三', '00', 'zhangsan@qq.com', '18812345678', '1', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/102.webp?Expires=1771492955&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=tLF0DA2reNMiUuFD%2F29gkVsiZrs%3D', '$2a$10$cmueflWIca3U8waJO3gSv.HAEhTFvygR7lGdMto630Nbs2GcXXTpy', '0', '0', '', NULL, NULL, 'admin', '2026-01-14 18:04:51', 'admin', '2026-02-12 17:22:37', '张三-普通员工');
INSERT INTO `sys_user` VALUES (112, 100, 'lisi', '李四', '00', 'lisi@163.com', '18855555555', '1', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1771492951&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=GFErrGwlGIJMYzT5mSpYQfa4k5k%3D', '$2a$10$2eglfjL.MCwsuprXJfWIRezp.4/nV3hpuOOgtRHfG9d8kyCfVDsB.', '0', '0', '', NULL, NULL, 'admin', '2026-01-24 21:56:29', 'admin', '2026-02-12 17:22:33', '');
INSERT INTO `sys_user` VALUES (113, 111, 'wangwu', 'wangwu', '00', 'wangwu@163.com', '13326541578', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/113.webp?Expires=1771492946&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=5DCWthMgcQ8Zl8QqJDJ2upy%2FYzM%3D', '$2a$10$XOXffZph7qNkosKfc/gPHOhh1NsQsVB9lzD4VNf6Rz5cYn.6ksHvG', '0', '0', '', NULL, NULL, 'wangwu', '2026-01-27 18:35:41', 'admin', '2026-02-12 17:22:28', '111');
INSERT INTO `sys_user` VALUES (114, 110, 'starPivot', 'starPivot演示用户', '00', 'starPivot@163.com', '13388888888', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/114.webp?Expires=1771219638&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=jS6ZhM4CsHr5DThRHmRdQew%2FlIk%3D', '$2a$10$7v0PWRiJVzWOFSDuFkCaXeiAF3xK0qgKxwPzOfSd47JC0LH4juJ4W', '0', '0', '', NULL, NULL, 'admin', '2026-02-09 12:22:01', 'admin', '2026-02-09 13:27:22', '111');
INSERT INTO `sys_user` VALUES (115, 111, 'xiaoming', 'xiaoming', '00', 'xiaoming@163.com', '15588888888', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/115.webp?Expires=1771493004&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=FwGw6RsMwvDlOUUPjGbde%2B%2BMbsg%3D', '$2a$10$WJeRMCrDzSJbVYA.dD0eOOqLpB0yLUqIjNqFRWehtkVEmaszZ3jPK', '0', '0', '', NULL, NULL, 'xiaoming', '2026-02-09 18:19:02', 'admin', '2026-02-12 17:23:46', '');

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_post`(`user_id`, `post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 197 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES (194, 1, 1);
INSERT INTO `sys_user_post` VALUES (195, 2, 2);
INSERT INTO `sys_user_post` VALUES (193, 100, 1);
INSERT INTO `sys_user_post` VALUES (192, 113, 4);
INSERT INTO `sys_user_post` VALUES (190, 114, 4);
INSERT INTO `sys_user_post` VALUES (196, 115, 4);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_role`(`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 216 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户与角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (210, 1, 1);
INSERT INTO `sys_user_role` VALUES (211, 2, 2);
INSERT INTO `sys_user_role` VALUES (214, 100, 3);
INSERT INTO `sys_user_role` VALUES (215, 114, 4);
INSERT INTO `sys_user_role` VALUES (213, 115, 3);
INSERT INTO `sys_user_role` VALUES (212, 115, 5);

-- ----------------------------
-- Table structure for wf_approval_record
-- ----------------------------
DROP TABLE IF EXISTS `wf_approval_record`;
CREATE TABLE `wf_approval_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `instance_id` bigint(20) NOT NULL COMMENT '流程实例 id',
  `node_id` bigint(20) NULL DEFAULT NULL COMMENT '节点 id（BPMN 模式下可为空）',
  `approver_id` bigint(20) NOT NULL COMMENT '审批人用户 ID',
  `result` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '审批结果：approved/rejected',
  `comment` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '审批意见',
  `approve_time` datetime(0) NULL DEFAULT NULL COMMENT '审批时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_instance_id`(`instance_id`) USING BTREE,
  INDEX `idx_node_id`(`node_id`) USING BTREE,
  INDEX `idx_approver_id`(`approver_id`) USING BTREE,
  INDEX `idx_approve_time`(`approve_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '审批记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wf_approval_record
-- ----------------------------

-- ----------------------------
-- Table structure for wf_definition
-- ----------------------------
DROP TABLE IF EXISTS `wf_definition`;
CREATE TABLE `wf_definition`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `definition_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流程唯一标识，如 user_register、leave_request',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '流程名称',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '描述',
  `version` int(11) NULL DEFAULT 1 COMMENT '版本号',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0启用 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_definition_key`(`definition_key`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程定义表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wf_definition
-- ----------------------------
INSERT INTO `wf_definition` VALUES (2, 'registe_request', '注册审批', '注册审批', 1, '0', 'admin', '2026-02-15 19:08:09', 'admin', '2026-02-15 19:08:09');

-- ----------------------------
-- Table structure for wf_instance
-- ----------------------------
DROP TABLE IF EXISTS `wf_instance`;
CREATE TABLE `wf_instance`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Flowable 流程实例 ID',
  `definition_id` bigint(20) NOT NULL COMMENT '流程定义 id',
  `business_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务类型，如 user_register、leave',
  `business_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务主键，如用户 ID、请假单 ID',
  `submitter_id` bigint(20) NOT NULL COMMENT '提交人用户 ID',
  `current_node_id` bigint(20) NULL DEFAULT NULL COMMENT '当前待审批节点 id（wf_node.id），为空表示已结束',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'running' COMMENT '状态：running/approved/rejected',
  `submit_time` datetime(0) NULL DEFAULT NULL COMMENT '提交时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间（通过/驳回）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_process_instance_id`(`process_instance_id`) USING BTREE,
  INDEX `idx_definition_id`(`definition_id`) USING BTREE,
  INDEX `idx_business`(`business_type`, `business_id`) USING BTREE,
  INDEX `idx_submitter_id`(`submitter_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_submit_time`(`submit_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程实例表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wf_instance
-- ----------------------------

-- ----------------------------
-- Table structure for wf_node
-- ----------------------------
DROP TABLE IF EXISTS `wf_node`;
CREATE TABLE `wf_node`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `definition_id` bigint(20) NOT NULL COMMENT '所属流程定义 id',
  `node_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '节点唯一标识，如 dept_leader、hr、admin',
  `node_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '节点名称',
  `sort_order` int(11) NULL DEFAULT 0 COMMENT '顺序，从小到大表示审批顺序',
  `approver_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '审批人类型：role/user/dept_leader 等',
  `approver_value` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '审批人取值，如角色 key、用户 ID、部门 ID',
  `allow_reject` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'Y' COMMENT '是否允许驳回（Y/N）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_definition_id`(`definition_id`) USING BTREE,
  INDEX `idx_sort_order`(`definition_id`, `sort_order`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程节点定义表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wf_node
-- ----------------------------
INSERT INTO `wf_node` VALUES (2, 2, 'node_1', '节点1', 0, 'user', '1', 'Y', '2026-02-15 19:08:38', '2026-02-15 19:08:38');
INSERT INTO `wf_node` VALUES (3, 2, 'node_2', '节点2', 1, 'user', '1', 'Y', '2026-02-15 19:08:38', '2026-02-15 19:08:38');

SET FOREIGN_KEY_CHECKS = 1;
