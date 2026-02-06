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

 Date: 06/02/2026 11:01:00
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
INSERT INTO `gen_table` VALUES (40, 'sys_config', '参数配置表', NULL, NULL, 'SysConfig', 'crud', '', 'com.star.pivot.system', 'system', 'config', '参数配置', 'admin', '0', '/', NULL, 'admin', '2026-02-05 17:10:22', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (41, 'sys_dept', '部门表', NULL, NULL, 'SysDept', 'crud', '', 'com.star.pivot.system', 'system', 'dept', '部门', 'admin', '0', '/', NULL, 'admin', '2026-02-05 17:10:22', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (42, 'sys_menu', '菜单权限表', NULL, NULL, 'SysMenu', 'crud', '', 'com.star.pivot.system', 'system', 'menu', '菜单权限', 'admin', '0', '/', NULL, 'admin', '2026-02-05 17:10:22', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (43, 'sys_notice', '通知公告表', NULL, NULL, 'SysNotice', 'crud', 'element-plus', 'com.star.pivot.system', 'system', 'notice', '通知公告', 'admin', '0', '/', '{\"treeCode\":\"\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuId\":1}', 'admin', '2026-02-05 17:10:22', '', '2026-02-05 17:30:23', '');
INSERT INTO `gen_table` VALUES (44, 'sys_post', '岗位信息表', NULL, NULL, 'SysPost', 'crud', '', 'com.star.pivot.system', 'system', 'post', '岗位信息', 'admin', '0', '/', NULL, 'admin', '2026-02-05 17:10:22', '', NULL, NULL);
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
INSERT INTO `gen_table_column` VALUES (384, 40, 'config_id', '参数主键', 'int(5)', 'Integer', 'configId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (385, 40, 'config_name', '参数名称', 'varchar(100)', 'String', 'configName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 2, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (386, 40, 'config_key', '参数键名', 'varchar(100)', 'String', 'configKey', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (387, 40, 'config_value', '参数键值', 'varchar(500)', 'String', 'configValue', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'textarea', '', 4, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (388, 40, 'config_type', '系统内置（Y是 N否）', 'char(1)', 'String', 'configType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', '', 5, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (389, 40, 'create_by', '创建者', 'varchar(64)', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 6, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (390, 40, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 7, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (391, 40, 'update_by', '更新者', 'varchar(64)', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'input', '', 8, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (392, 40, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'datetime', '', 9, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (393, 40, 'remark', '备注', 'varchar(500)', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', 'textarea', '', 10, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (394, 41, 'dept_id', '部门id', 'bigint(20)', 'Long', 'deptId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (395, 41, 'parent_id', '父部门id', 'bigint(20)', 'Long', 'parentId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (396, 41, 'ancestors', '祖级列表', 'varchar(50)', 'String', 'ancestors', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (397, 41, 'dept_name', '部门名称', 'varchar(30)', 'String', 'deptName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (398, 41, 'order_num', '显示顺序', 'int(4)', 'Integer', 'orderNum', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (399, 41, 'leader', '负责人', 'varchar(20)', 'String', 'leader', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (400, 41, 'phone', '联系电话', 'varchar(11)', 'String', 'phone', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 7, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (401, 41, 'email', '邮箱', 'varchar(50)', 'String', 'email', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 8, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (402, 41, 'status', '部门状态（0正常 1停用）', 'char(1)', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', '', 9, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (403, 41, 'del_flag', '删除标志（0代表存在 2代表删除）', 'char(1)', 'String', 'delFlag', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 10, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (404, 41, 'create_by', '创建者', 'varchar(64)', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 11, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (405, 41, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 12, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (406, 41, 'update_by', '更新者', 'varchar(64)', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'input', '', 13, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (407, 41, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'datetime', '', 14, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (408, 42, 'menu_id', '菜单ID', 'bigint(20)', 'Long', 'menuId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (409, 42, 'menu_name', '菜单名称', 'varchar(50)', 'String', 'menuName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', 'input', '', 2, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (410, 42, 'parent_id', '父菜单ID', 'bigint(20)', 'Long', 'parentId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (411, 42, 'order_num', '显示顺序', 'int(4)', 'Integer', 'orderNum', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (412, 42, 'path', '路由地址', 'varchar(200)', 'String', 'path', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (413, 42, 'component', '组件路径', 'varchar(255)', 'String', 'component', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (414, 42, 'query', '路由参数', 'varchar(255)', 'String', 'query', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 7, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (415, 42, 'route_name', '路由名称', 'varchar(50)', 'String', 'routeName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 8, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (416, 42, 'is_frame', '是否为外链（0是 1否）', 'int(1)', 'Integer', 'isFrame', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 9, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (417, 42, 'is_cache', '是否缓存（0缓存 1不缓存）', 'int(1)', 'Integer', 'isCache', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 10, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (418, 42, 'menu_type', '菜单类型（M目录 C菜单 F按钮）', 'char(1)', 'String', 'menuType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', '', 11, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (419, 42, 'visible', '菜单状态（0显示 1隐藏）', 'char(1)', 'String', 'visible', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 12, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (420, 42, 'status', '菜单状态（0正常 1停用）', 'char(1)', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', '', 13, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (421, 42, 'perms', '权限标识', 'varchar(100)', 'String', 'perms', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 14, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (422, 42, 'icon', '菜单图标', 'varchar(100)', 'String', 'icon', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 15, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (423, 42, 'create_by', '创建者', 'varchar(64)', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 16, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (424, 42, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 17, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (425, 42, 'update_by', '更新者', 'varchar(64)', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'input', '', 18, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (426, 42, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'datetime', '', 19, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (427, 42, 'remark', '备注', 'varchar(500)', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', 'textarea', '', 20, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (428, 43, 'notice_id', '公告ID', 'int(4)', 'Integer', 'noticeId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-02-05 17:10:22', '', '2026-02-05 17:30:23');
INSERT INTO `gen_table_column` VALUES (429, 43, 'notice_title', '公告标题', 'varchar(50)', 'String', 'noticeTitle', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-02-05 17:10:22', '', '2026-02-05 17:30:23');
INSERT INTO `gen_table_column` VALUES (430, 43, 'notice_type', '公告类型（1通知 2公告）', 'char(1)', 'String', 'noticeType', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'select', 'sys_notice_type', 3, 'admin', '2026-02-05 17:10:22', '', '2026-02-05 17:30:23');
INSERT INTO `gen_table_column` VALUES (431, 43, 'notice_content', '公告内容', 'longblob', 'String', 'noticeContent', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'editor', '', 4, 'admin', '2026-02-05 17:10:22', '', '2026-02-05 17:30:23');
INSERT INTO `gen_table_column` VALUES (432, 43, 'status', '公告状态（0正常 1关闭）', 'char(1)', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', 'sys_notice_status', 5, 'admin', '2026-02-05 17:10:22', '', '2026-02-05 17:30:23');
INSERT INTO `gen_table_column` VALUES (433, 43, 'create_by', '创建者', 'varchar(64)', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 6, 'admin', '2026-02-05 17:10:22', '', '2026-02-05 17:30:23');
INSERT INTO `gen_table_column` VALUES (434, 43, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 7, 'admin', '2026-02-05 17:10:22', '', '2026-02-05 17:30:23');
INSERT INTO `gen_table_column` VALUES (435, 43, 'update_by', '更新者', 'varchar(64)', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'input', '', 8, 'admin', '2026-02-05 17:10:22', '', '2026-02-05 17:30:23');
INSERT INTO `gen_table_column` VALUES (436, 43, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'datetime', '', 9, 'admin', '2026-02-05 17:10:22', '', '2026-02-05 17:30:23');
INSERT INTO `gen_table_column` VALUES (437, 43, 'remark', '备注', 'varchar(255)', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', 'input', '', 10, 'admin', '2026-02-05 17:10:22', '', '2026-02-05 17:30:23');
INSERT INTO `gen_table_column` VALUES (438, 44, 'post_id', '岗位ID', 'bigint(20)', 'Long', 'postId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (439, 44, 'post_code', '岗位编码', 'varchar(64)', 'String', 'postCode', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (440, 44, 'post_name', '岗位名称', 'varchar(50)', 'String', 'postName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', 'input', '', 3, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (441, 44, 'post_sort', '显示顺序', 'int(4)', 'Integer', 'postSort', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (442, 44, 'status', '状态（0正常 1停用）', 'char(1)', 'String', 'status', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'radio', '', 5, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (443, 44, 'create_by', '创建者', 'varchar(64)', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 6, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (444, 44, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 7, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (445, 44, 'update_by', '更新者', 'varchar(64)', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'input', '', 8, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (446, 44, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'datetime', '', 9, 'admin', '2026-02-05 17:10:22', '', NULL);
INSERT INTO `gen_table_column` VALUES (447, 44, 'remark', '备注', 'varchar(500)', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', 'textarea', '', 10, 'admin', '2026-02-05 17:10:22', '', NULL);
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
  `fired_time` bigint(13) NOT NULL COMMENT '触发的时间',
  `sched_time` bigint(13) NOT NULL COMMENT '定时器制定的时间',
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
  `last_checkin_time` bigint(13) NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint(13) NOT NULL COMMENT '检查间隔时间',
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
  `repeat_count` bigint(7) NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint(12) NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint(10) NOT NULL COMMENT '已经触发的次数',
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
  `next_fire_time` bigint(13) NULL DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint(13) NULL DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int(11) NULL DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发器的类型',
  `start_time` bigint(13) NOT NULL COMMENT '开始时间',
  `end_time` bigint(13) NULL DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint(2) NULL DEFAULT NULL COMMENT '补偿执行的策略',
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
  `config_id` int(5) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
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
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
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
  `dict_sort` int(4) NULL DEFAULT 0 COMMENT '字典排序',
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务调度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_job
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务调度日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 143 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES (108, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-25 12:27:57');
INSERT INTO `sys_logininfor` VALUES (109, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '1', '用户名或密码错误', '2026-01-25 12:43:41');
INSERT INTO `sys_logininfor` VALUES (110, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '1', '用户名或密码错误', '2026-01-25 12:45:47');
INSERT INTO `sys_logininfor` VALUES (111, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '1', '用户名或密码错误', '2026-01-25 12:48:55');
INSERT INTO `sys_logininfor` VALUES (112, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '1', '用户名或密码错误', '2026-01-25 12:49:12');
INSERT INTO `sys_logininfor` VALUES (114, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-25 17:16:36');
INSERT INTO `sys_logininfor` VALUES (115, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-25 17:24:18');
INSERT INTO `sys_logininfor` VALUES (116, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-25 19:25:08');
INSERT INTO `sys_logininfor` VALUES (117, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-25 19:31:47');
INSERT INTO `sys_logininfor` VALUES (118, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-25 20:06:23');
INSERT INTO `sys_logininfor` VALUES (119, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-25 20:15:13');
INSERT INTO `sys_logininfor` VALUES (120, 'xinxin', '0:0:0:0:0:0:0:1', '内网IP', 'Edge 144', 'Windows 10/11', '0', '登录成功', '2026-01-27 13:32:08');
INSERT INTO `sys_logininfor` VALUES (121, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-27 14:34:15');
INSERT INTO `sys_logininfor` VALUES (122, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-27 14:57:56');
INSERT INTO `sys_logininfor` VALUES (123, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-27 15:32:51');
INSERT INTO `sys_logininfor` VALUES (124, 'xinxin', '0:0:0:0:0:0:0:1', '内网IP', 'Edge 144', 'Windows 10/11', '0', '登录成功', '2026-01-27 16:57:05');
INSERT INTO `sys_logininfor` VALUES (125, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-27 17:29:18');
INSERT INTO `sys_logininfor` VALUES (126, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-27 18:15:17');
INSERT INTO `sys_logininfor` VALUES (127, 'wangwu', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-27 18:36:02');
INSERT INTO `sys_logininfor` VALUES (128, 'wangwu', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-27 18:36:25');
INSERT INTO `sys_logininfor` VALUES (129, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-27 19:06:10');
INSERT INTO `sys_logininfor` VALUES (130, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-27 19:15:57');
INSERT INTO `sys_logininfor` VALUES (131, 'wangwu', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-27 19:17:41');
INSERT INTO `sys_logininfor` VALUES (132, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-27 19:23:33');
INSERT INTO `sys_logininfor` VALUES (133, 'wangwu', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-27 19:31:05');
INSERT INTO `sys_logininfor` VALUES (134, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-27 19:36:29');
INSERT INTO `sys_logininfor` VALUES (135, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10/11', '0', '登录成功', '2026-02-01 17:10:20');
INSERT INTO `sys_logininfor` VALUES (136, 'ADMIN', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10/11', '0', '登录成功', '2026-02-04 16:23:47');
INSERT INTO `sys_logininfor` VALUES (137, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10/11', '0', '登录成功', '2026-02-04 16:29:50');
INSERT INTO `sys_logininfor` VALUES (138, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10/11', '0', '登录成功', '2026-02-04 19:27:44');
INSERT INTO `sys_logininfor` VALUES (139, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10/11', '0', '登录成功', '2026-02-04 19:33:41');
INSERT INTO `sys_logininfor` VALUES (140, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10/11', '0', '登录成功', '2026-02-04 22:19:29');
INSERT INTO `sys_logininfor` VALUES (141, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10/11', '0', '登录成功', '2026-02-05 17:40:21');
INSERT INTO `sys_logininfor` VALUES (142, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10/11', '0', '登录成功', '2026-02-05 18:38:13');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由名称',
  `is_frame` int(1) NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int(1) NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
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
) ENGINE = InnoDB AUTO_INCREMENT = 124 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

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
INSERT INTO `sys_menu` VALUES (106, '慢SQL分析', 95, 8, 'slow', '/monitor/sql/slow', NULL, 'SqlSlow', 1, 1, 'C', '0', '0', 'monitor:sql:query', 'tabler:sql', 'admin', '2026-01-25 20:51:46', 'admin', '2026-01-25 21:01:23', '');
INSERT INTO `sys_menu` VALUES (107, 'API性能监控', 95, 9, 'api', '/monitor/api', NULL, 'ApiPerformance', 1, 1, 'C', '0', '0', 'monitor:api:query', 'tabler:api', 'admin', '2026-01-25 20:54:00', 'admin', '2026-01-25 21:02:21', '');
INSERT INTO `sys_menu` VALUES (112, '提取慢SQL', 106, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:sql:extract', '#', 'admin', '2026-01-25 21:01:44', '', NULL, '提取慢SQL');
INSERT INTO `sys_menu` VALUES (113, '编辑慢SQL状态', 106, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:sql:edit', '#', 'admin', '2026-01-25 21:02:05', '', NULL, '编辑慢SQL状态');
INSERT INTO `sys_menu` VALUES (114, '删除缓存', 98, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:cache:remove', '#', 'admin', '2026-01-25 22:19:38', '', NULL, 'monitor:cache:remove');
INSERT INTO `sys_menu` VALUES (115, '清空缓存', 98, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:cache:clear', '#', 'admin', '2026-01-25 22:20:00', '', NULL, '清空缓存');
INSERT INTO `sys_menu` VALUES (116, '健康检查', 96, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:health:query', '#', 'admin', '2026-01-25 22:29:27', '', NULL, '健康检查');
INSERT INTO `sys_menu` VALUES (117, '缓存列表', 98, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:cache:query', '#', 'admin', '2026-01-27 18:21:55', '', NULL, '缓存列表');
INSERT INTO `sys_menu` VALUES (118, '通知公告', 1, 8, 'notice', '/system/notice/index', NULL, 'NoticeManage', 1, 0, 'C', '0', '0', 'system:notice:list', 'fe:notice-active', 'admin', '2026-02-05 17:38:35', 'admin', '2026-02-05 17:56:17', '通知公告菜单');
INSERT INTO `sys_menu` VALUES (119, '通知公告查询', 118, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (120, '通知公告新增', 118, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (121, '通知公告修改', 118, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (122, '通知公告删除', 118, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (123, '通知公告导出', 118, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:export', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');

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
  `response_time_avg` decimal(20, 2) NULL COMMENT '平均响应时间（毫秒）',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `stat_hour` tinyint(4) NULL DEFAULT NULL COMMENT '统计小时（0-23，用于按小时统计）',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_api_stat`(`api_path`, `api_method`, `stat_date`, `stat_hour`) USING BTREE,
  INDEX `idx_stat_date`(`stat_date`) USING BTREE,
  INDEX `idx_response_time_avg`(`response_time_avg`) USING BTREE,
  INDEX `idx_error_count`(`error_count`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'API接口性能监控表' ROW_FORMAT = Dynamic;

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
  `notice_id` int(4) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
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
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:1', 1, 'admin', '超级管理员', '星枢科技', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10/11', '1', '2026-02-04 19:27:45', '2026-02-04 19:27:45', '2026-02-04 19:33:28', NULL, '', '0', '2026-02-04 19:33:28');
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:113', 113, 'wangwu', 'wangwu', '人事部', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '1', '2026-01-27 19:31:06', '2026-01-27 19:31:06', '2026-01-27 19:36:21', NULL, '', '0', '2026-01-27 19:36:21');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int(2) NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求方式',
  `operator_type` int(1) NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '返回参数',
  `status` int(1) NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime(0) NULL DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint(20) NULL DEFAULT 0 COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`) USING BTREE,
  INDEX `idx_sys_oper_log_bt`(`business_type`) USING BTREE,
  INDEX `idx_sys_oper_log_s`(`status`) USING BTREE,
  INDEX `idx_sys_oper_log_ot`(`oper_time`) USING BTREE,
  INDEX `idx_oper_time`(`oper_time`) USING BTREE,
  INDEX `idx_oper_name`(`oper_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4728 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES (4661, '操作日志', 3, 'com.star.pivot.controller.SysOperLogController.clean()', 'DELETE', 1, 'admin', '星枢科技', '/api/sys/operlog/clean', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"清空成功\",\"data\":null,\"timestamp\":1770281821855}', 0, '', '2026-02-05 16:57:02', 48);
INSERT INTO `sys_oper_log` VALUES (4662, '操作日志', 0, 'com.star.pivot.controller.SysOperLogController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/operlog/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"title\":null,\"businessType\":null,\"operName\":null,\"status\":null,\"startTime\":null,\"endTime\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=1, rows=[OperLogVO(operId=4661, title=操作日志, businessType=3, method=com.star.pivot.controller.SysOperLogController.clean(), requestMethod=DELETE, operatorType=1, operName=admin, deptName=星枢科技, operUrl=/api/sys/operlog/clean, operIp=0:0:0:0:0:0:0:1, operLocation=, operParam=, jsonResult={\"code\":200,\"message\":\"清空成功\",\"data\":null,\"timestamp\":1770281821855}, status=0, errorMsg=, operTime=2026-02-05T16:57:02, costTime=48)], pageNum=1, pageSize=20, pageCount=1), timestamp=1770281821890)', 0, '', '2026-02-05 16:57:02', 9);
INSERT INTO `sys_oper_log` VALUES (4663, '操作日志', 0, 'com.star.pivot.controller.SysOperLogController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/operlog/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"title\":null,\"businessType\":null,\"operName\":null,\"status\":null,\"startTime\":null,\"endTime\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=2, rows=[OperLogVO(operId=4662, title=操作日志, businessType=0, method=com.star.pivot.controller.SysOperLogController.pageList(), requestMethod=POST, operatorType=1, operName=admin, deptName=星枢科技, operUrl=/api/sys/operlog/pageList, operIp=0:0:0:0:0:0:0:1, operLocation=, operParam=[{\"pageNum\":1,\"pageSize\":20,\"title\":null,\"businessType\":null,\"operName\":null,\"status\":null,\"startTime\":null,\"endTime\":null}], jsonResult=Result(code=200, message=操作成功, data=PageResponse(total=1, rows=[OperLogVO(operId=4661, title=操作日志, businessType=3, method=com.star.pivot.controller.SysOperLogController.clean(), requestMethod=DELETE, operatorType=1, operName=admin, deptName=星枢科技, operUrl=/api/sys/operlog/clean, operIp=0:0:0:0:0:0:0:1, operLocation=, operParam=, jsonResult={\"code\":200,\"message\":\"清空成功\",\"data\":null,\"timestamp\":1770281821855}, status=0, errorMsg=, operTime=2026-02-05T16:57:02, costTime=48)], pageNum=1, pageSize=20, pageCount=1), timestamp=1770281821890), status=0, errorMsg=, operTime=2026-02-05T16:57:02, costTime=9), OperLogVO(operId=4661, title=操作日志, businessType=3, method=com.star.pivot.controller.SysOperLogController.clean(), requestMethod=DELETE, operatorType=1, operName=admin, deptName=星枢科技, operUrl=/api/sys/operlog/clean, operIp=0:0:0:0:0:0:0:1, operLocation=, operParam=, jsonResult={\"code\":200,\"message\":\"清空成功\",\"data\":null,\"timestamp\":1770281821855}, status=0, errorMsg=, operTime=2026-02-05T16:57:02, costTime=48)], pageNum=1, pageSize=20, pageCount=1), timestamp=1770281827729)', 0, '', '2026-02-05 16:57:08', 10);
INSERT INTO `sys_oper_log` VALUES (4664, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=7, rows=[UserVO(userId=113, deptId=111, deptName=人事部, userName=wangwu, nickName=wangwu, userType=00, email=wangwu@163.com, phonenumber=13326541578, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/113.webp?Expires=1770826856&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=iVp1U%2Bl1YTwCa8Hh8bNAjCaKLcc%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-27T18:35:41, remark=111, roleIds=[2], roleNames=[普通角色], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=112, deptId=100, deptName=星枢科技, userName=lisi, nickName=李四, userType=00, email=lisi@163.com, phonenumber=18855555555, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1770826867&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=8%2FkmP7g0Zcndp10KaWzHKgTAqx8%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-24T21:56:29, remark=, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=102, deptId=107, deptName=运维部门, userName=zhangsan, nickName=张三, userType=00, email=zhangsan@qq.com, phonenumber=18812345678, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/102.webp?Expires=1770826873&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=UtsRTDHEfJjt4W5YS6afRYphvqw%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-14T18:04:51, remark=张三-普通员工, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=101, deptId=105, deptName=测试部门, userName=test, nickName=测试用户, userType=00, email=123@qq.com, phonenumber=18825454547, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/101.webp?Expires=1770826880&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=ECNEmoAjUPklrKAipbqQj1DPW5A%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-04T16:50:12, remark=测试用户专属, roleIds=null, roleNam...', 0, '', '2026-02-05 17:11:06', 1076);
INSERT INTO `sys_oper_log` VALUES (4665, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=7, rows=[UserVO(userId=113, deptId=111, deptName=人事部, userName=wangwu, nickName=wangwu, userType=00, email=wangwu@163.com, phonenumber=13326541578, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/113.webp?Expires=1770826856&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=iVp1U%2Bl1YTwCa8Hh8bNAjCaKLcc%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-27T18:35:41, remark=111, roleIds=[2], roleNames=[普通角色], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=112, deptId=100, deptName=星枢科技, userName=lisi, nickName=李四, userType=00, email=lisi@163.com, phonenumber=18855555555, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1770826867&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=8%2FkmP7g0Zcndp10KaWzHKgTAqx8%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-24T21:56:29, remark=, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=102, deptId=107, deptName=运维部门, userName=zhangsan, nickName=张三, userType=00, email=zhangsan@qq.com, phonenumber=18812345678, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/102.webp?Expires=1770826873&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=UtsRTDHEfJjt4W5YS6afRYphvqw%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-14T18:04:51, remark=张三-普通员工, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=101, deptId=105, deptName=测试部门, userName=test, nickName=测试用户, userType=00, email=123@qq.com, phonenumber=18825454547, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/101.webp?Expires=1770826880&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=ECNEmoAjUPklrKAipbqQj1DPW5A%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-04T16:50:12, remark=测试用户专属, roleIds=null, roleNam...', 0, '', '2026-02-05 17:12:55', 35);
INSERT INTO `sys_oper_log` VALUES (4666, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=7, rows=[UserVO(userId=113, deptId=111, deptName=人事部, userName=wangwu, nickName=wangwu, userType=00, email=wangwu@163.com, phonenumber=13326541578, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/113.webp?Expires=1770826856&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=iVp1U%2Bl1YTwCa8Hh8bNAjCaKLcc%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-27T18:35:41, remark=111, roleIds=[2], roleNames=[普通角色], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=112, deptId=100, deptName=星枢科技, userName=lisi, nickName=李四, userType=00, email=lisi@163.com, phonenumber=18855555555, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1770826867&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=8%2FkmP7g0Zcndp10KaWzHKgTAqx8%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-24T21:56:29, remark=, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=102, deptId=107, deptName=运维部门, userName=zhangsan, nickName=张三, userType=00, email=zhangsan@qq.com, phonenumber=18812345678, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/102.webp?Expires=1770826873&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=UtsRTDHEfJjt4W5YS6afRYphvqw%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-14T18:04:51, remark=张三-普通员工, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=101, deptId=105, deptName=测试部门, userName=test, nickName=测试用户, userType=00, email=123@qq.com, phonenumber=18825454547, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/101.webp?Expires=1770826880&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=ECNEmoAjUPklrKAipbqQj1DPW5A%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-04T16:50:12, remark=测试用户专属, roleIds=null, roleNam...', 0, '', '2026-02-05 17:12:57', 28);
INSERT INTO `sys_oper_log` VALUES (4667, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=7, rows=[UserVO(userId=113, deptId=111, deptName=人事部, userName=wangwu, nickName=wangwu, userType=00, email=wangwu@163.com, phonenumber=13326541578, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/113.webp?Expires=1770826856&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=iVp1U%2Bl1YTwCa8Hh8bNAjCaKLcc%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-27T18:35:41, remark=111, roleIds=[2], roleNames=[普通角色], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=112, deptId=100, deptName=星枢科技, userName=lisi, nickName=李四, userType=00, email=lisi@163.com, phonenumber=18855555555, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1770826867&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=8%2FkmP7g0Zcndp10KaWzHKgTAqx8%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-24T21:56:29, remark=, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=102, deptId=107, deptName=运维部门, userName=zhangsan, nickName=张三, userType=00, email=zhangsan@qq.com, phonenumber=18812345678, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/102.webp?Expires=1770826873&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=UtsRTDHEfJjt4W5YS6afRYphvqw%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-14T18:04:51, remark=张三-普通员工, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=101, deptId=105, deptName=测试部门, userName=test, nickName=测试用户, userType=00, email=123@qq.com, phonenumber=18825454547, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/101.webp?Expires=1770826880&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=ECNEmoAjUPklrKAipbqQj1DPW5A%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-04T16:50:12, remark=测试用户专属, roleIds=null, roleNam...', 0, '', '2026-02-05 17:13:07', 25);
INSERT INTO `sys_oper_log` VALUES (4668, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"1\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":null}]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"total\":0,\"rows\":[],\"pageNum\":1,\"pageSize\":20,\"pageCount\":0},\"timestamp\":1770282808943}', 0, '', '2026-02-05 17:13:29', 12);
INSERT INTO `sys_oper_log` VALUES (4669, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=7, rows=[UserVO(userId=113, deptId=111, deptName=人事部, userName=wangwu, nickName=wangwu, userType=00, email=wangwu@163.com, phonenumber=13326541578, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/113.webp?Expires=1770826856&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=iVp1U%2Bl1YTwCa8Hh8bNAjCaKLcc%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-27T18:35:41, remark=111, roleIds=[2], roleNames=[普通角色], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=112, deptId=100, deptName=星枢科技, userName=lisi, nickName=李四, userType=00, email=lisi@163.com, phonenumber=18855555555, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1770826867&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=8%2FkmP7g0Zcndp10KaWzHKgTAqx8%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-24T21:56:29, remark=, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=102, deptId=107, deptName=运维部门, userName=zhangsan, nickName=张三, userType=00, email=zhangsan@qq.com, phonenumber=18812345678, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/102.webp?Expires=1770826873&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=UtsRTDHEfJjt4W5YS6afRYphvqw%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-14T18:04:51, remark=张三-普通员工, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=101, deptId=105, deptName=测试部门, userName=test, nickName=测试用户, userType=00, email=123@qq.com, phonenumber=18825454547, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/101.webp?Expires=1770826880&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=ECNEmoAjUPklrKAipbqQj1DPW5A%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-04T16:50:12, remark=测试用户专属, roleIds=null, roleNam...', 0, '', '2026-02-05 17:13:31', 21);
INSERT INTO `sys_oper_log` VALUES (4670, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=7, rows=[UserVO(userId=113, deptId=111, deptName=人事部, userName=wangwu, nickName=wangwu, userType=00, email=wangwu@163.com, phonenumber=13326541578, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/113.webp?Expires=1770826856&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=iVp1U%2Bl1YTwCa8Hh8bNAjCaKLcc%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-27T18:35:41, remark=111, roleIds=[2], roleNames=[普通角色], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=112, deptId=100, deptName=星枢科技, userName=lisi, nickName=李四, userType=00, email=lisi@163.com, phonenumber=18855555555, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1770826867&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=8%2FkmP7g0Zcndp10KaWzHKgTAqx8%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-24T21:56:29, remark=, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=102, deptId=107, deptName=运维部门, userName=zhangsan, nickName=张三, userType=00, email=zhangsan@qq.com, phonenumber=18812345678, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/102.webp?Expires=1770826873&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=UtsRTDHEfJjt4W5YS6afRYphvqw%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-14T18:04:51, remark=张三-普通员工, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=101, deptId=105, deptName=测试部门, userName=test, nickName=测试用户, userType=00, email=123@qq.com, phonenumber=18825454547, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/101.webp?Expires=1770826880&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=ECNEmoAjUPklrKAipbqQj1DPW5A%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-04T16:50:12, remark=测试用户专属, roleIds=null, roleNam...', 0, '', '2026-02-05 17:14:06', 32);
INSERT INTO `sys_oper_log` VALUES (4671, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=7, rows=[UserVO(userId=113, deptId=111, deptName=人事部, userName=wangwu, nickName=wangwu, userType=00, email=wangwu@163.com, phonenumber=13326541578, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/113.webp?Expires=1770826856&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=iVp1U%2Bl1YTwCa8Hh8bNAjCaKLcc%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-27T18:35:41, remark=111, roleIds=[2], roleNames=[普通角色], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=112, deptId=100, deptName=星枢科技, userName=lisi, nickName=李四, userType=00, email=lisi@163.com, phonenumber=18855555555, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1770826867&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=8%2FkmP7g0Zcndp10KaWzHKgTAqx8%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-24T21:56:29, remark=, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=102, deptId=107, deptName=运维部门, userName=zhangsan, nickName=张三, userType=00, email=zhangsan@qq.com, phonenumber=18812345678, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/102.webp?Expires=1770826873&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=UtsRTDHEfJjt4W5YS6afRYphvqw%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-14T18:04:51, remark=张三-普通员工, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=101, deptId=105, deptName=测试部门, userName=test, nickName=测试用户, userType=00, email=123@qq.com, phonenumber=18825454547, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/101.webp?Expires=1770826880&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=ECNEmoAjUPklrKAipbqQj1DPW5A%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-04T16:50:12, remark=测试用户专属, roleIds=null, roleNam...', 0, '', '2026-02-05 17:14:07', 25);
INSERT INTO `sys_oper_log` VALUES (4672, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=7, rows=[UserVO(userId=113, deptId=111, deptName=人事部, userName=wangwu, nickName=wangwu, userType=00, email=wangwu@163.com, phonenumber=13326541578, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/113.webp?Expires=1770826856&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=iVp1U%2Bl1YTwCa8Hh8bNAjCaKLcc%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-27T18:35:41, remark=111, roleIds=[2], roleNames=[普通角色], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=112, deptId=100, deptName=星枢科技, userName=lisi, nickName=李四, userType=00, email=lisi@163.com, phonenumber=18855555555, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1770826867&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=8%2FkmP7g0Zcndp10KaWzHKgTAqx8%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-24T21:56:29, remark=, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=102, deptId=107, deptName=运维部门, userName=zhangsan, nickName=张三, userType=00, email=zhangsan@qq.com, phonenumber=18812345678, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/102.webp?Expires=1770826873&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=UtsRTDHEfJjt4W5YS6afRYphvqw%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-14T18:04:51, remark=张三-普通员工, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=101, deptId=105, deptName=测试部门, userName=test, nickName=测试用户, userType=00, email=123@qq.com, phonenumber=18825454547, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/101.webp?Expires=1770826880&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=ECNEmoAjUPklrKAipbqQj1DPW5A%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-04T16:50:12, remark=测试用户专属, roleIds=null, roleNam...', 0, '', '2026-02-05 17:14:12', 27);
INSERT INTO `sys_oper_log` VALUES (4673, '操作日志', 0, 'com.star.pivot.controller.SysOperLogController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/operlog/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"title\":null,\"businessType\":null,\"operName\":null,\"status\":null,\"startTime\":null,\"endTime\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=12, rows=[OperLogVO(operId=4672, title=用户管理, businessType=0, method=com.star.pivot.controller.SysUserController.pageList(), requestMethod=POST, operatorType=1, operName=admin, deptName=星枢科技, operUrl=/api/sys/user/pageList, operIp=0:0:0:0:0:0:0:1, operLocation=, operParam=[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":null}], jsonResult=Result(code=200, message=操作成功, data=PageResponse(total=7, rows=[UserVO(userId=113, deptId=111, deptName=人事部, userName=wangwu, nickName=wangwu, userType=00, email=wangwu@163.com, phonenumber=13326541578, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/113.webp?Expires=1770826856&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=iVp1U%2Bl1YTwCa8Hh8bNAjCaKLcc%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-27T18:35:41, remark=111, roleIds=[2], roleNames=[普通角色], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=112, deptId=100, deptName=星枢科技, userName=lisi, nickName=李四, userType=00, email=lisi@163.com, phonenumber=18855555555, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1770826867&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=8%2FkmP7g0Zcndp10KaWzHKgTAqx8%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-24T21:56:29, remark=, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=102, deptId=107, deptName=运维部门, userName=zhangsan, nickName=张三, userType=00, email=zhangsan@qq.com, phonenumber=18812345678, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/102.webp?Expires=1770826873&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=UtsRTDHEfJjt4W5YS6afRYphvqw%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-14T18:04:51, remark=张三-普通员工, roleIds=n...', 0, '', '2026-02-05 17:14:16', 19);
INSERT INTO `sys_oper_log` VALUES (4674, '登录日志', 0, 'com.star.pivot.controller.SysLogininforController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/logininfor/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"ipaddr\":null,\"status\":null,\"startTime\":null,\"endTime\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=32, rows=[LogininforVO(infoId=140, userName=admin, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10/11, status=0, msg=登录成功, loginTime=2026-02-04T22:19:29), LogininforVO(infoId=139, userName=admin, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10/11, status=0, msg=登录成功, loginTime=2026-02-04T19:33:41), LogininforVO(infoId=138, userName=admin, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10/11, status=0, msg=登录成功, loginTime=2026-02-04T19:27:44), LogininforVO(infoId=137, userName=admin, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10/11, status=0, msg=登录成功, loginTime=2026-02-04T16:29:50), LogininforVO(infoId=136, userName=ADMIN, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10/11, status=0, msg=登录成功, loginTime=2026-02-04T16:23:47), LogininforVO(infoId=135, userName=admin, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10/11, status=0, msg=登录成功, loginTime=2026-02-01T17:10:20), LogininforVO(infoId=134, userName=admin, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 143, os=Windows 10/11, status=0, msg=登录成功, loginTime=2026-01-27T19:36:29), LogininforVO(infoId=133, userName=wangwu, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 143, os=Windows 10/11, status=0, msg=登录成功, loginTime=2026-01-27T19:31:05), LogininforVO(infoId=132, userName=admin, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 143, os=Windows 10/11, status=0, msg=登录成功, loginTime=2026-01-27T19:23:33), LogininforVO(infoId=131, userName=wangwu, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 143, os=Windows 10/11, status=0, msg=登录成功, loginTime=2026-01-27T19:17:41), LogininforVO(infoId=130, userName=admin, ipad...', 0, '', '2026-02-05 17:14:17', 20);
INSERT INTO `sys_oper_log` VALUES (4675, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=7.139383889184209, used=289, free=3770}, duration=192, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=17.896911723880226, used=255, free=1173}, overall=healthy, redis={healthy=true}, timestamp=2026-02-05T17:14:21.266715500}, timestamp=1770282861458)', 0, '', '2026-02-05 17:14:21', 195);
INSERT INTO `sys_oper_log` VALUES (4676, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.572342913410056,\"used\":1.6431780417256447,\"wait\":0.0,\"free\":97.59369807372761},\"memory\":{\"total\":16236,\"used\":8768,\"free\":7467,\"usage\":54.00780140461853},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770281879045,\"runTime\":984045,\"max\":4060,\"total\":450,\"used\":305,\"free\":3754,\"usage\":7.533472559134949},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":255,\"free\":1173,\"usage\":17.896914925523326}},\"timestamp\":1770282863097}', 0, '', '2026-02-05 17:14:23', 1834);
INSERT INTO `sys_oper_log` VALUES (4677, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-113041420\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":5,\"minIdle\":5,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":498,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1770282866824}', 0, '', '2026-02-05 17:14:27', 3);
INSERT INTO `sys_oper_log` VALUES (4678, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[true,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-113041420\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":5,\"minIdle\":5,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":500,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":[]},\"timestamp\":1770282868911}', 0, '', '2026-02-05 17:14:29', 3);
INSERT INTO `sys_oper_log` VALUES (4679, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=7.730516894110318, used=313, free=3746}, duration=47, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=17.896952811633344, used=255, free=1173}, overall=healthy, redis={healthy=true}, timestamp=2026-02-05T17:14:31.265659500}, timestamp=1770282871312)', 0, '', '2026-02-05 17:14:31', 49);
INSERT INTO `sys_oper_log` VALUES (4680, 'Redis监控', 0, 'com.star.pivot.controller.MonitorController.getRedisMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/redis', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"version\":\"7.2.12\",\"mode\":\"standalone\",\"port\":6379,\"connected\":true,\"memory\":{\"usedMemory\":1,\"maxMemory\":0,\"usage\":0.0},\"keys\":{\"totalKeys\":4,\"expiredKeys\":null},\"commands\":{\"totalCommands\":431,\"commandsPerSecond\":3.0},\"clients\":{\"connectedClients\":2,\"blockedClients\":0}},\"timestamp\":1770282871455}', 0, '', '2026-02-05 17:14:31', 8);
INSERT INTO `sys_oper_log` VALUES (4681, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.19377422177772222,\"used\":1.2689086135766972,\"wait\":0.0,\"free\":98.24353044130517},\"memory\":{\"total\":16236,\"used\":8879,\"free\":7356,\"usage\":54.68754323120004},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770281879045,\"runTime\":993282,\"max\":4060,\"total\":450,\"used\":319,\"free\":3740,\"usage\":7.878300145341845},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":255,\"free\":1173,\"usage\":17.896952811633344}},\"timestamp\":1770282872331}', 0, '', '2026-02-05 17:14:32', 1067);
INSERT INTO `sys_oper_log` VALUES (4682, 'Redis缓存管理', 0, 'com.star.pivot.controller.MonitorController.getCacheList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/cache/list', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":[{\"cacheName\":\"cache\",\"remark\":\"cache\",\"keys\":null},{\"cacheName\":\"jwt\",\"remark\":\"jwt\",\"keys\":null}],\"timestamp\":1770282873556}', 0, '', '2026-02-05 17:14:34', 9);
INSERT INTO `sys_oper_log` VALUES (4683, 'Redis缓存管理', 0, 'com.star.pivot.controller.MonitorController.getCacheKeys()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/cache/keys', '0:0:0:0:0:0:0:1', '', '[\"cache\"]', '{\"code\":200,\"message\":\"操作成功\",\"data\":[{\"key\":\"cache:menuTree::all\",\"type\":\"string\",\"ttl\":2754,\"size\":27473},{\"key\":\"cache:userPermissions::admin\",\"type\":\"string\",\"ttl\":1253,\"size\":1957}],\"timestamp\":1770282874664}', 0, '', '2026-02-05 17:14:35', 37);
INSERT INTO `sys_oper_log` VALUES (4684, 'Redis缓存管理', 0, 'com.star.pivot.controller.MonitorController.getCacheContent()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/cache/content', '0:0:0:0:0:0:0:1', '', '[\"cache\",\"cache:menuTree::all\"]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cacheName\":\"cache\",\"key\":\"cache:menuTree::all\",\"content\":\"{\\n  \\\"@class\\\": \\\"java.util.ArrayList\\\",\\n  \\\"toString\\\": \\\"[SysMenu(menuId=1, menuName=系统管理, parentId=0, orderNum=1, path=/system, component=/index/index, query=null, routeName=System, isFrame=1, isCache=1, menuType=M, visible=0, status=0, perms=null, icon=uil:setting, createBy=system, createTime=2025-12-31T16:34:16, updateBy=, updateTime=2026-01-02T21:36:28, remark=系统管理模块, value=1, label=系统管理, children=[SysMenu(menuId=4, menuName=菜单管理, parentId=1, orderNum=1, path=menu, component=/system/menu, query=null, routeName=SystemMenu, isFrame=1, isCache=1, menuType=C, visible=0, status=0, perms=system:menu:list, icon=ep:menu, createBy=system, createTime=2025-12-31T16:34:16, updateBy=, updateTime=2026-01-02T21:12:33, remark=菜单管理模块, value=4, label=菜单管理, children=[SysMenu(menuId=55, menuName=菜单查询, parentId=4, orderNum=1, path=, component=, query=null, routeName=, isFrame=1, isCache=1, menuType=F, visible=0, status=0, perms=system:menu:query, icon=#, createBy=admin, createTime=2026-01-12T17:39:26, updateBy=, updateTime=null, remark=菜单查询, value=55, label=菜单查询, children=[]), SysMenu(menuId=56, menuName=菜单添加, parentId=4, orderNum=2, path=, component=, query=null, routeName=, isFrame=1, isCache=1, menuType=F, visible=0, status=0, perms=system:menu:add, icon=#, createBy=admin, createTime=2026-01-12T17:39:50, updateBy=, updateTime=null, remark=菜单添加, value=56, label=菜单添加, children=[]), SysMenu(menuId=57, menuName=菜单修改, parentId=4, orderNum=3, path=, component=, query=null, routeName=, isFrame=1, isCache=1, menuType=F, visible=0, status=0, perms=system:menu:edit, icon=#, createBy=admin, createTime=2026-01-12T17:40:05, updateBy=, updateTime=null, remark=菜单修改, value=57, label=菜单修改, children=[]), SysMenu(menuId=58, menuName=菜单删除, parentId=4...', 0, '', '2026-02-05 17:14:36', 14);
INSERT INTO `sys_oper_log` VALUES (4685, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[true,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-113041420\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":5,\"minIdle\":5,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":514,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":[]},\"timestamp\":1770282876821}', 0, '', '2026-02-05 17:14:37', 1);
INSERT INTO `sys_oper_log` VALUES (4686, 'Redis缓存管理', 0, 'com.star.pivot.controller.MonitorController.getCacheContent()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/cache/content', '0:0:0:0:0:0:0:1', '', '[\"cache\",\"cache:userPermissions::admin\"]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cacheName\":\"cache\",\"key\":\"cache:userPermissions::admin\",\"content\":\"{\\n  \\\"@class\\\": \\\"com.star.pivot.system.utils.LoginUser\\\",\\n  \\\"userId\\\": 1,\\n  \\\"user\\\": \\\"SysUser: SysUser(userId=1, deptId=100, userName=admin, nickName=超级管理员, userType=00, email=admin@163.com, phonenumber=18518712884, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/1.jpeg?Expires=1770826564&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=w3c6rxcK8kVssU9pPtiAg83UZxw%3D, password=$2a$10$FTKzzUPrLDbNfKoWn9e1y.B6D0MX0HeHBPHFRfcx3R2Ane73gMbf6, status=0, loginIp=0:0:0:0:0:0:0:1, loginDate=2025-12-29T22:16:38, pwdUpdateDate=2026-01-04T18:13:47, roles=null)\\\",\\n  \\\"authorities\\\": \\\"ArrayList: [system:menu:list, system:role:list, system:user:list, system:dept:list, system:post:list, system:type:list, system:user:add, system:user:edit, system:user:delete, system:role:add, system:role:edit, system:role:delete, system:role:query, system:user:query, system:dept:query, system:dept:add, system:dept:edit, system:dept:delete, system:post:query, system:post:add, system:post:edit, system:post:delete, system:menu:query, system:menu:add, system:menu:edit, system:menu:delete, system:user:resetPwd, system:user:changeStatus, system:role:assignDataScope, system:type:add, system:type:edit, system:type:delete, system:data:add, system:data:edit, system:data:delete, system:type:query, system:data:query, tools:generator:list, tool:gen:query, system:log:list, system:login:list, system:operlog:delete, system:operlog:query, system:logininfor:query, system:logininfor:delete, tool:gen:preview, tool:gen:edit, tool:gen:delete, tool:gen:sync, tool:gen:create, tool:gen:add, system:user:unLock, system:role:allocatedList, system:role:unallocatedList, system:role:assignUser, system:role:cancelUser, tool:gen:import, system:user:import, system:user:export, monitor:server:query, monitor:druid:query, monitor:redis:query, monitor:online:query, monitor:onli...', 0, '', '2026-02-05 17:14:39', 9);
INSERT INTO `sys_oper_log` VALUES (4687, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=8.321649899036426, used=337, free=3722}, duration=46, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=17.896951210811796, used=255, free=1173}, overall=healthy, redis={healthy=true}, timestamp=2026-02-05T17:14:41.264488400}, timestamp=1770282881310)', 0, '', '2026-02-05 17:14:41', 48);
INSERT INTO `sys_oper_log` VALUES (4688, 'Redis缓存管理', 0, 'com.star.pivot.controller.MonitorController.getCacheKeys()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/cache/keys', '0:0:0:0:0:0:0:1', '', '[\"jwt\"]', '{\"code\":200,\"message\":\"操作成功\",\"data\":[{\"key\":\"jwt:logout:f37131180e1d9c01323180e9a4c019b4\",\"type\":\"string\",\"ttl\":7982,\"size\":32},{\"key\":\"jwt:refresh:user:1\",\"type\":\"string\",\"ttl\":536688,\"size\":70}],\"timestamp\":1770282881783}', 0, '', '2026-02-05 17:14:42', 12);
INSERT INTO `sys_oper_log` VALUES (4689, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.48746953315417785,\"used\":0.581213674145366,\"wait\":0.0,\"free\":98.83132304230986},\"memory\":{\"total\":16236,\"used\":9036,\"free\":7199,\"usage\":55.65580181651037},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770281879045,\"runTime\":1003281,\"max\":4060,\"total\":450,\"used\":341,\"free\":3718,\"usage\":8.420172066524112},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":255,\"free\":1173,\"usage\":17.896951744418978}},\"timestamp\":1770282882330}', 0, '', '2026-02-05 17:14:42', 1067);
INSERT INTO `sys_oper_log` VALUES (4690, 'Redis缓存管理', 0, 'com.star.pivot.controller.MonitorController.getCacheContent()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/cache/content', '0:0:0:0:0:0:0:1', '', '[\"jwt\",\"jwt:logout:f37131180e1d9c01323180e9a4c019b4\"]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cacheName\":\"jwt\",\"key\":\"jwt:logout:f37131180e1d9c01323180e9a4c019b4\",\"content\":\"141e36d7b9893af75b96fd074c32c6e4\",\"type\":\"string\",\"ttl\":7980},\"timestamp\":1770282884305}', 0, '', '2026-02-05 17:14:44', 6);
INSERT INTO `sys_oper_log` VALUES (4691, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[true,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-113041420\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":5,\"minIdle\":5,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":526,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":[]},\"timestamp\":1770282886821}', 0, '', '2026-02-05 17:14:47', 3);
INSERT INTO `sys_oper_log` VALUES (4692, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=3.1234624702942195, used=126, free=3933}, duration=53, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=17.898984254180494, used=255, free=1173}, overall=healthy, redis={healthy=true}, timestamp=2026-02-05T17:14:51.281925200}, timestamp=1770282891334)', 0, '', '2026-02-05 17:14:51', 54);
INSERT INTO `sys_oper_log` VALUES (4693, 'Redis缓存管理', 0, 'com.star.pivot.controller.MonitorController.getCacheContent()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/cache/content', '0:0:0:0:0:0:0:1', '', '[\"jwt\",\"jwt:refresh:user:1\"]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cacheName\":\"jwt\",\"key\":\"jwt:refresh:user:1\",\"content\":\"{\\r\\n  \\\"tokenHash\\\" : \\\"1f79f37043518bb6c7e872943b70d2ca\\\",\\r\\n  \\\"issuedAt\\\" : 1770214769496,\\r\\n  \\\"ipaddr\\\" : \\\"0:0:0:0:0:0:0:1\\\",\\r\\n  \\\"browser\\\" : \\\"Chrome 144\\\",\\r\\n  \\\"os\\\" : \\\"Windows 10/11\\\",\\r\\n  \\\"loginLocation\\\" : \\\"内网IP\\\",\\r\\n  \\\"lastAccessTime\\\" : 1770214769496\\r\\n}\",\"type\":\"string\",\"ttl\":536678},\"timestamp\":1770282891382}', 0, '', '2026-02-05 17:14:51', 8);
INSERT INTO `sys_oper_log` VALUES (4694, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.2996067661194682,\"used\":5.580176018975095,\"wait\":0.0,\"free\":94.02034829286562},\"memory\":{\"total\":16236,\"used\":9397,\"free\":6838,\"usage\":57.88254394214652},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770281879045,\"runTime\":1013297,\"max\":4060,\"total\":450,\"used\":130,\"free\":3929,\"usage\":3.221984637781904},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":255,\"free\":1173,\"usage\":17.899879380230626}},\"timestamp\":1770282892345}', 0, '', '2026-02-05 17:14:52', 1066);
INSERT INTO `sys_oper_log` VALUES (4695, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10/11, loginTime=2026-02-04T22:19:29.496, lastAccessTime=2026-02-04T22:19:29.496)], timestamp=1770282893782)', 0, '', '2026-02-05 17:14:54', 13);
INSERT INTO `sys_oper_log` VALUES (4696, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[true,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-113041420\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":5,\"minIdle\":5,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":538,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":[]},\"timestamp\":1770282896819}', 0, '', '2026-02-05 17:14:57', 2);
INSERT INTO `sys_oper_log` VALUES (4697, '慢SQL分析', 0, 'com.star.pivot.controller.MonitorController.getSlowSqlList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/sql/slow', '0:0:0:0:0:0:0:1', '', '[100]', '{\"code\":200,\"message\":\"操作成功\",\"data\":[],\"timestamp\":1770282898679}', 0, '', '2026-02-05 17:14:59', 4);
INSERT INTO `sys_oper_log` VALUES (4698, 'API性能监控', 0, 'com.star.pivot.controller.MonitorController.getApiPerformancePageList()', 'POST', 1, 'admin', '星枢科技', '/api/monitor/api/pageList', '0:0:0:0:0:0:0:1', '', '参数解析失败', 'Result(code=200, message=操作成功, data=PageResponse(total=2, rows=[SysMonitorApiPerformance(id=23, apiPath=/api/auth/captcha, apiMethod=GET, requestCount=1, successCount=1, errorCount=0, responseTimeTotal=2029, responseTimeMax=2029, responseTimeMin=2029, responseTimeAvg=2029.00, statDate=2026-02-04, statHour=19, createTime=2026-02-04T19:24:20, updateTime=null), SysMonitorApiPerformance(id=24, apiPath=/api/auth/captcha, apiMethod=GET, requestCount=2, successCount=2, errorCount=0, responseTimeTotal=4036, responseTimeMax=2018, responseTimeMin=2018, responseTimeAvg=2018.00, statDate=2026-02-04, statHour=21, createTime=2026-02-04T21:58:19, updateTime=2026-02-04T21:58:45)], pageNum=1, pageSize=20, pageCount=1), timestamp=1770282901085)', 0, '', '2026-02-05 17:15:01', 24);
INSERT INTO `sys_oper_log` VALUES (4699, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=3.566812223988801, used=144, free=3915}, duration=51, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=17.900167794913244, used=255, free=1173}, overall=healthy, redis={healthy=true}, timestamp=2026-02-05T17:15:01.268222200}, timestamp=1770282901319)', 0, '', '2026-02-05 17:15:01', 53);
INSERT INTO `sys_oper_log` VALUES (4700, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.6714716934639315,\"used\":6.831762459188074,\"wait\":0.0,\"free\":92.496765847348},\"memory\":{\"total\":16236,\"used\":9607,\"free\":6628,\"usage\":59.17394033486902},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770281879045,\"runTime\":1023284,\"max\":4060,\"total\":450,\"used\":148,\"free\":3911,\"usage\":3.6653343914764855},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":255,\"free\":1173,\"usage\":17.900175532217403}},\"timestamp\":1770282902333}', 0, '', '2026-02-05 17:15:02', 1066);
INSERT INTO `sys_oper_log` VALUES (4701, '在线用户', 0, 'com.star.pivot.controller.MonitorController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/online', '0:0:0:0:0:0:0:1', '', '[null,null]', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 144, os=Windows 10/11, loginTime=2026-02-04T22:19:29.496, lastAccessTime=2026-02-04T22:19:29.496)], timestamp=1770282903775)', 0, '', '2026-02-05 17:15:04', 5);
INSERT INTO `sys_oper_log` VALUES (4702, 'Druid监控', 0, 'com.star.pivot.controller.MonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/druid', '0:0:0:0:0:0:0:1', '', '[true,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-113041420\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":5,\"minIdle\":5,\"maxActive\":20,\"activeCount\":0,\"activePeak\":2,\"usage\":0.0},\"sqlStat\":{\"executeCount\":555,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":[]},\"timestamp\":1770282906820}', 0, '', '2026-02-05 17:15:07', 2);
INSERT INTO `sys_oper_log` VALUES (4703, '系统健康检查', 0, 'com.star.pivot.controller.MonitorController.getHealthCheck()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/health', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data={jvm={max=4060, healthy=true, usage=3.8131176427080127, used=154, free=3905}, duration=45, database={activeCount=0, healthy=true, usage=0.0, maxActive=20}, disk={total=1429, healthy=true, usage=17.90009949319377, used=255, free=1173}, overall=healthy, redis={healthy=true}, timestamp=2026-02-05T17:15:11.266764100}, timestamp=1770282911311)', 0, '', '2026-02-05 17:15:11', 47);
INSERT INTO `sys_oper_log` VALUES (4704, '服务器监控', 0, 'com.star.pivot.controller.MonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.19359270592643477,\"used\":4.383938050334104,\"wait\":0.0,\"free\":95.42246924373946},\"memory\":{\"total\":16236,\"used\":9659,\"free\":6576,\"usage\":59.49173702149753},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1770281879045,\"runTime\":1033280,\"max\":4060,\"total\":450,\"used\":158,\"free\":3901,\"usage\":3.9116398101956973},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.1.13\"},\"disk\":{\"total\":1429,\"used\":255,\"free\":1173,\"usage\":17.900103762051238}},\"timestamp\":1770282912328}', 0, '', '2026-02-05 17:15:12', 1063);
INSERT INTO `sys_oper_log` VALUES (4705, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=7, rows=[UserVO(userId=113, deptId=111, deptName=人事部, userName=wangwu, nickName=wangwu, userType=00, email=wangwu@163.com, phonenumber=13326541578, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/113.webp?Expires=1770826856&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=iVp1U%2Bl1YTwCa8Hh8bNAjCaKLcc%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-27T18:35:41, remark=111, roleIds=[2], roleNames=[普通角色], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=112, deptId=100, deptName=星枢科技, userName=lisi, nickName=李四, userType=00, email=lisi@163.com, phonenumber=18855555555, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1770826867&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=8%2FkmP7g0Zcndp10KaWzHKgTAqx8%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-24T21:56:29, remark=, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=102, deptId=107, deptName=运维部门, userName=zhangsan, nickName=张三, userType=00, email=zhangsan@qq.com, phonenumber=18812345678, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/102.webp?Expires=1770826873&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=UtsRTDHEfJjt4W5YS6afRYphvqw%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-14T18:04:51, remark=张三-普通员工, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=101, deptId=105, deptName=测试部门, userName=test, nickName=测试用户, userType=00, email=123@qq.com, phonenumber=18825454547, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/101.webp?Expires=1770826880&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=ECNEmoAjUPklrKAipbqQj1DPW5A%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-04T16:50:12, remark=测试用户专属, roleIds=null, roleNam...', 0, '', '2026-02-05 17:15:19', 21);
INSERT INTO `sys_oper_log` VALUES (4706, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=7, rows=[UserVO(userId=113, deptId=111, deptName=人事部, userName=wangwu, nickName=wangwu, userType=00, email=wangwu@163.com, phonenumber=13326541578, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/113.webp?Expires=1770826856&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=iVp1U%2Bl1YTwCa8Hh8bNAjCaKLcc%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-27T18:35:41, remark=111, roleIds=[2], roleNames=[普通角色], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=112, deptId=100, deptName=星枢科技, userName=lisi, nickName=李四, userType=00, email=lisi@163.com, phonenumber=18855555555, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1770826867&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=8%2FkmP7g0Zcndp10KaWzHKgTAqx8%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-24T21:56:29, remark=, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=102, deptId=107, deptName=运维部门, userName=zhangsan, nickName=张三, userType=00, email=zhangsan@qq.com, phonenumber=18812345678, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/102.webp?Expires=1770826873&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=UtsRTDHEfJjt4W5YS6afRYphvqw%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-14T18:04:51, remark=张三-普通员工, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=101, deptId=105, deptName=测试部门, userName=test, nickName=测试用户, userType=00, email=123@qq.com, phonenumber=18825454547, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/101.webp?Expires=1770826880&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=ECNEmoAjUPklrKAipbqQj1DPW5A%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-04T16:50:12, remark=测试用户专属, roleIds=null, roleNam...', 0, '', '2026-02-05 17:16:29', 28);
INSERT INTO `sys_oper_log` VALUES (4707, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=7, rows=[UserVO(userId=113, deptId=111, deptName=人事部, userName=wangwu, nickName=wangwu, userType=00, email=wangwu@163.com, phonenumber=13326541578, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/113.webp?Expires=1770826856&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=iVp1U%2Bl1YTwCa8Hh8bNAjCaKLcc%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-27T18:35:41, remark=111, roleIds=[2], roleNames=[普通角色], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=112, deptId=100, deptName=星枢科技, userName=lisi, nickName=李四, userType=00, email=lisi@163.com, phonenumber=18855555555, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1770826867&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=8%2FkmP7g0Zcndp10KaWzHKgTAqx8%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-24T21:56:29, remark=, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=102, deptId=107, deptName=运维部门, userName=zhangsan, nickName=张三, userType=00, email=zhangsan@qq.com, phonenumber=18812345678, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/102.webp?Expires=1770826873&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=UtsRTDHEfJjt4W5YS6afRYphvqw%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-14T18:04:51, remark=张三-普通员工, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=101, deptId=105, deptName=测试部门, userName=test, nickName=测试用户, userType=00, email=123@qq.com, phonenumber=18825454547, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/101.webp?Expires=1770826880&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=ECNEmoAjUPklrKAipbqQj1DPW5A%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-04T16:50:12, remark=测试用户专属, roleIds=null, roleNam...', 0, '', '2026-02-05 17:16:29', 19);
INSERT INTO `sys_oper_log` VALUES (4708, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":100}]', 'Result(code=200, message=操作成功, data=PageResponse(total=3, rows=[UserVO(userId=112, deptId=100, deptName=星枢科技, userName=lisi, nickName=李四, userType=00, email=lisi@163.com, phonenumber=18855555555, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1770826867&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=8%2FkmP7g0Zcndp10KaWzHKgTAqx8%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-24T21:56:29, remark=, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=100, deptId=100, deptName=星枢科技, userName=xinxin, nickName=超级管理员, userType=00, email=123@qq.com, phonenumber=18834581458, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/100.webp?Expires=1770826902&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=p9ODF3motELyFP30o8xxeB9IViM%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-04T15:34:36, remark=作者，超级管理员, roleIds=[1, 100], roleNames=[超级管理员, 测试专属], postIds=[1], postNames=[董事长], isLocked=false), UserVO(userId=1, deptId=100, deptName=星枢科技, userName=admin, nickName=超级管理员, userType=00, email=admin@163.com, phonenumber=18518712884, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/1.jpeg?Expires=1770826564&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=w3c6rxcK8kVssU9pPtiAg83UZxw%3D, status=0, loginIp=0:0:0:0:0:0:0:1, loginDate=2025-12-29T22:16:38, createTime=2025-12-28T13:46:34, remark=超级管理员, roleIds=[1], roleNames=[超级管理员], postIds=[1], postNames=[董事长], isLocked=false)], pageNum=1, pageSize=20, pageCount=1), timestamp=1770282990338)', 0, '', '2026-02-05 17:16:30', 18);
INSERT INTO `sys_oper_log` VALUES (4709, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":101}]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"total\":0,\"rows\":[],\"pageNum\":1,\"pageSize\":20,\"pageCount\":0},\"timestamp\":1770283029258}', 0, '', '2026-02-05 17:17:09', 7);
INSERT INTO `sys_oper_log` VALUES (4710, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":103}]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"total\":0,\"rows\":[],\"pageNum\":1,\"pageSize\":20,\"pageCount\":0},\"timestamp\":1770283029773}', 0, '', '2026-02-05 17:17:10', 6);
INSERT INTO `sys_oper_log` VALUES (4711, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":104}]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"total\":0,\"rows\":[],\"pageNum\":1,\"pageSize\":20,\"pageCount\":0},\"timestamp\":1770283030259}', 0, '', '2026-02-05 17:17:10', 6);
INSERT INTO `sys_oper_log` VALUES (4712, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":105}]', 'Result(code=200, message=操作成功, data=PageResponse(total=2, rows=[UserVO(userId=101, deptId=105, deptName=测试部门, userName=test, nickName=测试用户, userType=00, email=123@qq.com, phonenumber=18825454547, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/101.webp?Expires=1770826880&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=ECNEmoAjUPklrKAipbqQj1DPW5A%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-04T16:50:12, remark=测试用户专属, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=2, deptId=105, deptName=测试部门, userName=user, nickName=用户管理员, userType=00, email=user@qq.com, phonenumber=15666666666, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/2.webp?Expires=1770826910&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=N2pvHxtBCj%2BTEWBXRlsM%2BLEaLQ4%3D, status=0, loginIp=0:0:0:0:0:0:0:1, loginDate=2025-12-29T22:08:54, createTime=2025-12-28T13:46:34, remark=测试员--用户管理员, roleIds=[2], roleNames=[普通角色], postIds=[2], postNames=[项目经理], isLocked=false)], pageNum=1, pageSize=20, pageCount=1), timestamp=1770283030676)', 0, '', '2026-02-05 17:17:11', 13);
INSERT INTO `sys_oper_log` VALUES (4713, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":106}]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"total\":0,\"rows\":[],\"pageNum\":1,\"pageSize\":20,\"pageCount\":0},\"timestamp\":1770283031349}', 0, '', '2026-02-05 17:17:11', 7);
INSERT INTO `sys_oper_log` VALUES (4714, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":107}]', 'Result(code=200, message=操作成功, data=PageResponse(total=1, rows=[UserVO(userId=102, deptId=107, deptName=运维部门, userName=zhangsan, nickName=张三, userType=00, email=zhangsan@qq.com, phonenumber=18812345678, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/102.webp?Expires=1770826873&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=UtsRTDHEfJjt4W5YS6afRYphvqw%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-14T18:04:51, remark=张三-普通员工, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false)], pageNum=1, pageSize=20, pageCount=1), timestamp=1770283031882)', 0, '', '2026-02-05 17:17:12', 10);
INSERT INTO `sys_oper_log` VALUES (4715, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":102}]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"total\":0,\"rows\":[],\"pageNum\":1,\"pageSize\":20,\"pageCount\":0},\"timestamp\":1770283032240}', 0, '', '2026-02-05 17:17:12', 6);
INSERT INTO `sys_oper_log` VALUES (4716, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":108}]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"total\":0,\"rows\":[],\"pageNum\":1,\"pageSize\":20,\"pageCount\":0},\"timestamp\":1770283033109}', 0, '', '2026-02-05 17:17:13', 6);
INSERT INTO `sys_oper_log` VALUES (4717, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":109}]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"total\":0,\"rows\":[],\"pageNum\":1,\"pageSize\":20,\"pageCount\":0},\"timestamp\":1770283033507}', 0, '', '2026-02-05 17:17:14', 7);
INSERT INTO `sys_oper_log` VALUES (4718, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":110}]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"total\":0,\"rows\":[],\"pageNum\":1,\"pageSize\":20,\"pageCount\":0},\"timestamp\":1770283033858}', 0, '', '2026-02-05 17:17:14', 7);
INSERT INTO `sys_oper_log` VALUES (4719, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":111}]', 'Result(code=200, message=操作成功, data=PageResponse(total=1, rows=[UserVO(userId=113, deptId=111, deptName=人事部, userName=wangwu, nickName=wangwu, userType=00, email=wangwu@163.com, phonenumber=13326541578, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/113.webp?Expires=1770826856&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=iVp1U%2Bl1YTwCa8Hh8bNAjCaKLcc%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-27T18:35:41, remark=111, roleIds=[2], roleNames=[普通角色], postIds=[4], postNames=[普通员工], isLocked=false)], pageNum=1, pageSize=20, pageCount=1), timestamp=1770283034520)', 0, '', '2026-02-05 17:17:15', 15);
INSERT INTO `sys_oper_log` VALUES (4720, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":105}]', 'Result(code=200, message=操作成功, data=PageResponse(total=2, rows=[UserVO(userId=101, deptId=105, deptName=测试部门, userName=test, nickName=测试用户, userType=00, email=123@qq.com, phonenumber=18825454547, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/101.webp?Expires=1770826880&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=ECNEmoAjUPklrKAipbqQj1DPW5A%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-04T16:50:12, remark=测试用户专属, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=2, deptId=105, deptName=测试部门, userName=user, nickName=用户管理员, userType=00, email=user@qq.com, phonenumber=15666666666, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/2.webp?Expires=1770826910&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=N2pvHxtBCj%2BTEWBXRlsM%2BLEaLQ4%3D, status=0, loginIp=0:0:0:0:0:0:0:1, loginDate=2025-12-29T22:08:54, createTime=2025-12-28T13:46:34, remark=测试员--用户管理员, roleIds=[2], roleNames=[普通角色], postIds=[2], postNames=[项目经理], isLocked=false)], pageNum=1, pageSize=20, pageCount=1), timestamp=1770283036151)', 0, '', '2026-02-05 17:17:16', 12);
INSERT INTO `sys_oper_log` VALUES (4721, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":104}]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"total\":0,\"rows\":[],\"pageNum\":1,\"pageSize\":20,\"pageCount\":0},\"timestamp\":1770283036797}', 0, '', '2026-02-05 17:17:17', 6);
INSERT INTO `sys_oper_log` VALUES (4722, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":103}]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"total\":0,\"rows\":[],\"pageNum\":1,\"pageSize\":20,\"pageCount\":0},\"timestamp\":1770283037697}', 0, '', '2026-02-05 17:17:18', 5);
INSERT INTO `sys_oper_log` VALUES (4723, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":101}]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"total\":0,\"rows\":[],\"pageNum\":1,\"pageSize\":20,\"pageCount\":0},\"timestamp\":1770283038042}', 0, '', '2026-02-05 17:17:18', 6);
INSERT INTO `sys_oper_log` VALUES (4724, '用户管理', 0, 'com.star.pivot.controller.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":100}]', 'Result(code=200, message=操作成功, data=PageResponse(total=3, rows=[UserVO(userId=112, deptId=100, deptName=星枢科技, userName=lisi, nickName=李四, userType=00, email=lisi@163.com, phonenumber=18855555555, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1770826867&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=8%2FkmP7g0Zcndp10KaWzHKgTAqx8%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-24T21:56:29, remark=, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), UserVO(userId=100, deptId=100, deptName=星枢科技, userName=xinxin, nickName=超级管理员, userType=00, email=123@qq.com, phonenumber=18834581458, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/100.webp?Expires=1770826902&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=p9ODF3motELyFP30o8xxeB9IViM%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-04T15:34:36, remark=作者，超级管理员, roleIds=[1, 100], roleNames=[超级管理员, 测试专属], postIds=[1], postNames=[董事长], isLocked=false), UserVO(userId=1, deptId=100, deptName=星枢科技, userName=admin, nickName=超级管理员, userType=00, email=admin@163.com, phonenumber=18518712884, sex=0, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/1.jpeg?Expires=1770826564&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=w3c6rxcK8kVssU9pPtiAg83UZxw%3D, status=0, loginIp=0:0:0:0:0:0:0:1, loginDate=2025-12-29T22:16:38, createTime=2025-12-28T13:46:34, remark=超级管理员, roleIds=[1], roleNames=[超级管理员], postIds=[1], postNames=[董事长], isLocked=false)], pageNum=1, pageSize=20, pageCount=1), timestamp=1770283038435)', 0, '', '2026-02-05 17:17:18', 17);
INSERT INTO `sys_oper_log` VALUES (4725, '用户管理', 0, 'com.star.pivot.controller.SysUserController.getUserById()', 'GET', 1, 'admin', '星枢科技', '/api/sys/user/112', '0:0:0:0:0:0:0:1', '', '[112]', 'Result(code=200, message=操作成功, data=UserVO(userId=112, deptId=100, deptName=星枢科技, userName=lisi, nickName=李四, userType=00, email=lisi@163.com, phonenumber=18855555555, sex=1, avatar=http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1770826867&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=8%2FkmP7g0Zcndp10KaWzHKgTAqx8%3D, status=0, loginIp=, loginDate=null, createTime=2026-01-24T21:56:29, remark=, roleIds=null, roleNames=null, postIds=null, postNames=null, isLocked=false), timestamp=1770283041114)', 0, '', '2026-02-05 17:17:21', 9);
INSERT INTO `sys_oper_log` VALUES (4726, '用户登录', 0, 'com.star.pivot.controller.AuthController.login()', 'POST', 0, '未知', '', '/api/auth/login', '0:0:0:0:0:0:0:1', '', '[{\"username\":\"admin\",\"password\":\"******\",\"captchaProof\":\"Gr2yiMHdBdkgX-65tJR_8001rE80JR-bR73wTWHaHfc\"}]', '{\"code\":200,\"message\":\"登录成功\",\"data\":{\"token\":\"eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc3MDI4NDQyMSwiZXhwIjoxNzcwMzcwODIxfQ.t1dQTCPkVvUznAabxyBXp3xhBvTkW28_zdEoXXtn54I\",\"username\":\"admin\",\"nickname\":\"超级管理员\",\"refreshToken\":\"a80453ce38634b08bb52e0a5110f7846\"},\"timestamp\":1770284421750}', 0, '', '2026-02-05 17:40:22', 2102);
INSERT INTO `sys_oper_log` VALUES (4727, '用户登录', 0, 'com.star.pivot.controller.AuthController.login()', 'POST', 0, '未知', '', '/api/auth/login', '0:0:0:0:0:0:0:1', '', '[{\"username\":\"admin\",\"password\":\"******\",\"captchaProof\":\"KSlGHbid4GSsgFlRREwwV6n2zQoR2cuACi0touWkoio\"}]', '{\"code\":200,\"message\":\"登录成功\",\"data\":{\"token\":\"eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc3MDI4Nzg5MywiZXhwIjoxNzcwMzc0MjkzfQ.gGdhaVJB9ieK78y26vO0Qoz6-PcwmAV1IbAYGFE97LI\",\"username\":\"admin\",\"nickname\":\"超级管理员\",\"refreshToken\":\"b4255934b89e4516b6775aaa60ecfb7f\"},\"timestamp\":1770287893427}', 0, '', '2026-02-05 18:38:13', 2018);

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int(4) NOT NULL COMMENT '显示顺序',
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
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
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
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2025-12-28 13:46:34', 'admin', '2026-01-23 19:59:34', '超级管理员：拥有所有菜单权限');
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', '2025-12-28 13:46:34', 'admin', '2026-01-12 20:33:35', '普通角色');
INSERT INTO `sys_role` VALUES (100, '测试专属', 'test', 3, '2', 1, 1, '0', '0', '', '2026-01-03 17:06:10', 'admin', '2026-01-15 20:53:20', '专属于测试的角色');

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
) ENGINE = InnoDB AUTO_INCREMENT = 176 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色与部门关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------
INSERT INTO `sys_role_dept` VALUES (167, 2, 100);
INSERT INTO `sys_role_dept` VALUES (168, 2, 101);
INSERT INTO `sys_role_dept` VALUES (169, 2, 104);
INSERT INTO `sys_role_dept` VALUES (175, 100, 100);
INSERT INTO `sys_role_dept` VALUES (170, 100, 101);
INSERT INTO `sys_role_dept` VALUES (172, 100, 102);
INSERT INTO `sys_role_dept` VALUES (171, 100, 105);
INSERT INTO `sys_role_dept` VALUES (173, 100, 108);
INSERT INTO `sys_role_dept` VALUES (174, 100, 109);

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
) ENGINE = InnoDB AUTO_INCREMENT = 454 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色与菜单关联表' ROW_FORMAT = Dynamic;

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
INSERT INTO `sys_role_menu` VALUES (359, 2, 1);
INSERT INTO `sys_role_menu` VALUES (361, 2, 2);
INSERT INTO `sys_role_menu` VALUES (360, 2, 3);
INSERT INTO `sys_role_menu` VALUES (362, 2, 4);
INSERT INTO `sys_role_menu` VALUES (367, 2, 5);
INSERT INTO `sys_role_menu` VALUES (373, 2, 6);
INSERT INTO `sys_role_menu` VALUES (379, 2, 8);
INSERT INTO `sys_role_menu` VALUES (383, 2, 9);
INSERT INTO `sys_role_menu` VALUES (387, 2, 12);
INSERT INTO `sys_role_menu` VALUES (374, 2, 39);
INSERT INTO `sys_role_menu` VALUES (376, 2, 40);
INSERT INTO `sys_role_menu` VALUES (368, 2, 42);
INSERT INTO `sys_role_menu` VALUES (369, 2, 43);
INSERT INTO `sys_role_menu` VALUES (370, 2, 44);
INSERT INTO `sys_role_menu` VALUES (371, 2, 45);
INSERT INTO `sys_role_menu` VALUES (377, 2, 46);
INSERT INTO `sys_role_menu` VALUES (380, 2, 47);
INSERT INTO `sys_role_menu` VALUES (381, 2, 48);
INSERT INTO `sys_role_menu` VALUES (382, 2, 49);
INSERT INTO `sys_role_menu` VALUES (384, 2, 51);
INSERT INTO `sys_role_menu` VALUES (385, 2, 52);
INSERT INTO `sys_role_menu` VALUES (386, 2, 53);
INSERT INTO `sys_role_menu` VALUES (363, 2, 55);
INSERT INTO `sys_role_menu` VALUES (364, 2, 56);
INSERT INTO `sys_role_menu` VALUES (365, 2, 57);
INSERT INTO `sys_role_menu` VALUES (366, 2, 58);
INSERT INTO `sys_role_menu` VALUES (378, 2, 59);
INSERT INTO `sys_role_menu` VALUES (375, 2, 60);
INSERT INTO `sys_role_menu` VALUES (372, 2, 61);
INSERT INTO `sys_role_menu` VALUES (388, 2, 62);
INSERT INTO `sys_role_menu` VALUES (389, 2, 63);
INSERT INTO `sys_role_menu` VALUES (390, 2, 64);
INSERT INTO `sys_role_menu` VALUES (391, 2, 69);
INSERT INTO `sys_role_menu` VALUES (392, 2, 70);
INSERT INTO `sys_role_menu` VALUES (395, 2, 71);
INSERT INTO `sys_role_menu` VALUES (396, 2, 72);
INSERT INTO `sys_role_menu` VALUES (393, 2, 73);
INSERT INTO `sys_role_menu` VALUES (394, 2, 81);
INSERT INTO `sys_role_menu` VALUES (176, 100, 1);
INSERT INTO `sys_role_menu` VALUES (186, 100, 2);
INSERT INTO `sys_role_menu` VALUES (177, 100, 3);
INSERT INTO `sys_role_menu` VALUES (187, 100, 5);
INSERT INTO `sys_role_menu` VALUES (188, 100, 6);
INSERT INTO `sys_role_menu` VALUES (182, 100, 39);
INSERT INTO `sys_role_menu` VALUES (183, 100, 40);
INSERT INTO `sys_role_menu` VALUES (178, 100, 42);
INSERT INTO `sys_role_menu` VALUES (179, 100, 43);
INSERT INTO `sys_role_menu` VALUES (180, 100, 45);
INSERT INTO `sys_role_menu` VALUES (184, 100, 46);
INSERT INTO `sys_role_menu` VALUES (185, 100, 59);
INSERT INTO `sys_role_menu` VALUES (181, 100, 61);

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
) ENGINE = InnoDB AUTO_INCREMENT = 114 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 100, 'admin', '超级管理员', '00', 'admin@163.com', '18518712884', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/1.jpeg?Expires=1770826564&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=w3c6rxcK8kVssU9pPtiAg83UZxw%3D', '$2a$10$FTKzzUPrLDbNfKoWn9e1y.B6D0MX0HeHBPHFRfcx3R2Ane73gMbf6', '0', '0', '0:0:0:0:0:0:0:1', '2025-12-29 22:16:38', '2026-01-04 18:13:47', 'admin', '2025-12-28 13:46:34', 'admin', '2026-02-05 00:16:06', '超级管理员');
INSERT INTO `sys_user` VALUES (2, 105, 'user', '用户管理员', '00', 'user@qq.com', '15666666666', '1', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/2.webp?Expires=1770826910&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=N2pvHxtBCj%2BTEWBXRlsM%2BLEaLQ4%3D', '$2a$12$eYocwR0zs3iWKl7gsvHVQ.KLDTWvXqecm.29aXPi3IAF.mmkARVR.', '0', '0', '0:0:0:0:0:0:0:1', '2025-12-29 22:08:54', '2025-12-28 13:46:34', 'admin', '2025-12-28 13:46:34', 'admin', '2026-02-05 00:21:51', '测试员--用户管理员');
INSERT INTO `sys_user` VALUES (100, 100, 'xinxin', '超级管理员', '00', '123@qq.com', '18834581458', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/100.webp?Expires=1770826902&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=p9ODF3motELyFP30o8xxeB9IViM%3D', '$2a$12$eYocwR0zs3iWKl7gsvHVQ.KLDTWvXqecm.29aXPi3IAF.mmkARVR.', '0', '0', '', NULL, NULL, 'admin', '2026-01-04 15:34:36', 'admin', '2026-02-05 00:21:43', '作者，超级管理员');
INSERT INTO `sys_user` VALUES (101, 105, 'test', '测试用户', '00', '123@qq.com', '18825454547', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/101.webp?Expires=1770826880&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=ECNEmoAjUPklrKAipbqQj1DPW5A%3D', '$2a$10$YdqAWweActfkWOaWEjz9p.bBWqNWVdT9EQ2OHcUODgFg.f3ma13Va', '0', '0', '', NULL, NULL, 'admin', '2026-01-04 16:50:12', 'admin', '2026-02-05 00:21:21', '测试用户专属');
INSERT INTO `sys_user` VALUES (102, 107, 'zhangsan', '张三', '00', 'zhangsan@qq.com', '18812345678', '1', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/102.webp?Expires=1770826873&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=UtsRTDHEfJjt4W5YS6afRYphvqw%3D', '$2a$10$cmueflWIca3U8waJO3gSv.HAEhTFvygR7lGdMto630Nbs2GcXXTpy', '0', '0', '', NULL, NULL, 'admin', '2026-01-14 18:04:51', 'admin', '2026-02-05 00:21:14', '张三-普通员工');
INSERT INTO `sys_user` VALUES (112, 100, 'lisi', '李四', '00', 'lisi@163.com', '18855555555', '1', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1770826867&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=8%2FkmP7g0Zcndp10KaWzHKgTAqx8%3D', '$2a$10$2eglfjL.MCwsuprXJfWIRezp.4/nV3hpuOOgtRHfG9d8kyCfVDsB.', '0', '0', '', NULL, NULL, 'admin', '2026-01-24 21:56:29', 'admin', '2026-02-05 00:21:08', '');
INSERT INTO `sys_user` VALUES (113, 111, 'wangwu', 'wangwu', '00', 'wangwu@163.com', '13326541578', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/113.webp?Expires=1770826856&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=iVp1U%2Bl1YTwCa8Hh8bNAjCaKLcc%3D', '$2a$10$XOXffZph7qNkosKfc/gPHOhh1NsQsVB9lzD4VNf6Rz5cYn.6ksHvG', '0', '0', '', NULL, NULL, 'wangwu', '2026-01-27 18:35:41', 'admin', '2026-02-05 00:21:00', '111');

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
) ENGINE = InnoDB AUTO_INCREMENT = 184 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES (179, 1, 1);
INSERT INTO `sys_user_post` VALUES (183, 2, 2);
INSERT INTO `sys_user_post` VALUES (182, 100, 1);
INSERT INTO `sys_user_post` VALUES (180, 113, 4);

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
) ENGINE = InnoDB AUTO_INCREMENT = 204 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户与角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (197, 1, 1);
INSERT INTO `sys_user_role` VALUES (203, 2, 2);
INSERT INTO `sys_user_role` VALUES (201, 100, 1);
INSERT INTO `sys_user_role` VALUES (202, 100, 100);
INSERT INTO `sys_user_role` VALUES (198, 113, 2);

SET FOREIGN_KEY_CHECKS = 1;
