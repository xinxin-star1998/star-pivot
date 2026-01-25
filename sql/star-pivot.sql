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

 Date: 25/01/2026 17:02:31
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
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代码生成业务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table
-- ----------------------------
INSERT INTO `gen_table` VALUES (26, 'sys_role_dept', '角色与部门关联表', NULL, NULL, 'SysRoleDept', 'crud', '', 'com.star.pivot.system', 'system', 'dept', '角色与部门关联', 'xinxin', '0', '/', NULL, 'admin', '2026-01-25 16:07:44', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (27, 'sys_role_menu', '角色与菜单关联表', NULL, NULL, 'SysRoleMenu', 'crud', '', 'com.star.pivot.system', 'system', 'menu', '角色与菜单关联', 'xinxin', '0', '/', NULL, 'admin', '2026-01-25 16:07:44', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (28, 'sys_user', '用户信息表', NULL, NULL, 'SysUser', 'crud', '', 'com.star.pivot.system', 'system', 'user', '用户信息', 'xinxin', '0', '/', NULL, 'admin', '2026-01-25 16:07:44', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (29, 'sys_user_post', '用户与岗位关联表', NULL, NULL, 'SysUserPost', 'crud', '', 'com.star.pivot.system', 'system', 'post', '用户与岗位关联', 'xinxin', '0', '/', NULL, 'admin', '2026-01-25 16:07:44', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (30, 'sys_user_role', '用户与角色关联表', NULL, NULL, 'SysUserRole', 'crud', '', 'com.star.pivot.system', 'system', 'role', '用户与角色关联', 'xinxin', '0', '/', NULL, 'admin', '2026-01-25 16:07:44', '', NULL, NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 395 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------
INSERT INTO `gen_table_column` VALUES (284, 26, 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (285, 26, 'role_id', '角色ID', 'bigint(20)', 'Long', 'roleId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (286, 26, 'dept_id', '部门ID', 'bigint(20)', 'Long', 'deptId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (287, 27, 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (288, 27, 'role_id', '角色ID', 'bigint(20)', 'Long', 'roleId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (289, 27, 'menu_id', '菜单ID', 'bigint(20)', 'Long', 'menuId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (290, 28, 'user_id', '用户ID', 'bigint(20)', 'Long', 'userId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (291, 28, 'dept_id', '部门ID', 'bigint(20)', 'Long', 'deptId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (292, 28, 'user_name', '用户账号', 'varchar(30)', 'String', 'userName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', 'input', '', 3, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (293, 28, 'nick_name', '用户昵称', 'varchar(30)', 'String', 'nickName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (294, 28, 'user_type', '用户类型（00系统用户）', 'varchar(2)', 'String', 'userType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', '', 5, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (295, 28, 'email', '用户邮箱', 'varchar(50)', 'String', 'email', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (296, 28, 'phonenumber', '手机号码', 'varchar(11)', 'String', 'phonenumber', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 7, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (297, 28, 'sex', '用户性别（0男 1女 2未知）', 'char(1)', 'String', 'sex', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', '', 8, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (298, 28, 'avatar', '头像地址', 'varchar(500)', 'String', 'avatar', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'textarea', '', 9, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (299, 28, 'password', '密码', 'varchar(100)', 'String', 'password', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 10, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (300, 28, 'status', '账号状态（0正常 1停用）', 'char(1)', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', '', 11, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (301, 28, 'del_flag', '删除标志（0代表存在 2代表删除）', 'char(1)', 'String', 'delFlag', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 12, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (302, 28, 'login_ip', '最后登录IP', 'varchar(128)', 'String', 'loginIp', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 13, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (303, 28, 'login_date', '最后登录时间', 'datetime', 'Date', 'loginDate', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'datetime', '', 14, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (304, 28, 'pwd_update_date', '密码最后更新时间', 'datetime', 'Date', 'pwdUpdateDate', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'datetime', '', 15, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (305, 28, 'create_by', '创建者', 'varchar(64)', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 16, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (306, 28, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 17, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (307, 28, 'update_by', '更新者', 'varchar(64)', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'input', '', 18, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (308, 28, 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'datetime', '', 19, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (309, 28, 'remark', '备注', 'varchar(500)', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', 'textarea', '', 20, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (310, 29, 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (311, 29, 'user_id', '用户ID', 'bigint(20)', 'Long', 'userId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (312, 29, 'post_id', '岗位ID', 'bigint(20)', 'Long', 'postId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (313, 30, 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (314, 30, 'user_id', '用户ID', 'bigint(20)', 'Long', 'userId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-01-25 16:07:44', '', NULL);
INSERT INTO `gen_table_column` VALUES (315, 30, 'role_id', '角色ID', 'bigint(20)', 'Long', 'roleId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-01-25 16:07:44', '', NULL);

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
  INDEX `idx_sys_logininfor_lt`(`login_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 114 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES (108, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '0', '登录成功', '2026-01-25 12:27:57');
INSERT INTO `sys_logininfor` VALUES (109, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '1', '用户名或密码错误', '2026-01-25 12:43:41');
INSERT INTO `sys_logininfor` VALUES (110, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '1', '用户名或密码错误', '2026-01-25 12:45:47');
INSERT INTO `sys_logininfor` VALUES (111, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '1', '用户名或密码错误', '2026-01-25 12:48:55');
INSERT INTO `sys_logininfor` VALUES (112, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '1', '用户名或密码错误', '2026-01-25 12:49:12');

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
) ENGINE = InnoDB AUTO_INCREMENT = 95 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (2, '系统管理', 0, 2, '/system', '/index/index', NULL, 'System', 1, 1, 'M', '0', '0', NULL, 'uil:setting', 'system', '2025-12-31 16:34:16', '', '2026-01-02 21:36:28', '系统管理模块');
INSERT INTO `sys_menu` VALUES (4, '菜单管理', 2, 1, 'menu', '/system/menu', NULL, 'SystemMenu', 1, 1, 'C', '0', '0', 'system:menu:list', 'ep:menu', 'system', '2025-12-31 16:34:16', '', '2026-01-02 21:12:33', '菜单管理模块');
INSERT INTO `sys_menu` VALUES (5, '角色管理', 2, 2, 'role', '/system/role', NULL, 'SystemRole', 1, 1, 'C', '0', '0', 'system:role:list', 'oui:app-users-roles', 'system', '2025-12-31 16:34:16', '', '2026-01-02 21:30:56', '角色管理模块');
INSERT INTO `sys_menu` VALUES (6, '用户管理', 2, 3, 'user', '/system/user', NULL, 'SystemUser', 1, 1, 'C', '0', '0', 'system:user:list', 'mdi:user', 'system', '2025-12-31 16:34:16', '', '2026-01-02 21:31:51', '用户管理模块');
INSERT INTO `sys_menu` VALUES (8, '部门管理', 2, 4, 'dept', '/system/dept', NULL, 'SystemDept', 1, 1, 'C', '0', '0', 'system:dept:list', 'ri:organization-chart', '', '2026-01-02 21:04:34', '', '2026-01-02 21:36:43', '部门管理模块');
INSERT INTO `sys_menu` VALUES (9, '岗位管理', 2, 5, 'post', '/system/post/index', NULL, 'PostManage', 1, 1, 'C', '0', '0', 'system:post:list', 'picon:post', 'xinxin', '2026-01-04 19:23:51', 'xinxin', '2026-01-04 19:25:02', '岗位管理模块');
INSERT INTO `sys_menu` VALUES (12, '字典管理', 2, 6, 'dict', '/system/dict/index', NULL, 'DictManage', 1, 1, 'C', '0', '0', 'system:type:list', 'arcticons:zdict', 'admin', '2026-01-05 12:28:54', 'admin', '2026-01-19 21:37:20', '字典管理模块。有：字典数据   字典类型');
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
INSERT INTO `sys_menu` VALUES (71, '系统工具', 0, 3, '/tools', '', NULL, 'SystemTools', 1, 1, 'M', '0', '0', '', 'clarity:tools-line', 'admin', '2026-01-20 13:08:43', '', NULL, '系统工具');
INSERT INTO `sys_menu` VALUES (72, '代码生成', 71, 1, 'generator', '/tools/generator/index', NULL, 'GenerateTools', 1, 1, 'C', '0', '0', 'tools:generator:list', 'mdi:generator-mobile', 'admin', '2026-01-20 13:15:59', 'admin', '2026-01-20 13:25:42', '代码生成');
INSERT INTO `sys_menu` VALUES (73, '列表查询', 72, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2026-01-20 14:56:43', '', NULL, '列表查询');
INSERT INTO `sys_menu` VALUES (74, '日志管理', 2, 8, 'log', '', NULL, 'LogManager', 1, 1, 'M', '0', '0', '', 'mdi:math-log', 'admin', '2026-01-23 13:37:05', 'admin', '2026-01-23 13:47:13', '');
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
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES (1, '温馨提醒：2018-07-01 若依新版本发布啦', '2', 0xE696B0E78988E69CACE58685E5AEB9, '0', 'admin', '2025-12-28 13:46:34', '', NULL, '管理员');
INSERT INTO `sys_notice` VALUES (2, '维护通知：2018-07-01 若依系统凌晨维护', '1', 0xE7BBB4E68AA4E58685E5AEB9, '0', 'admin', '2025-12-28 13:46:34', '', NULL, '管理员');

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
  INDEX `idx_sys_oper_log_ot`(`oper_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 953 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES (951, '操作日志', 3, 'com.star.pivot.controller.SysOperLogController.clean()', 'DELETE', 1, 'admin', '星枢科技', '/api/sys/operlog/clean', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"清空成功\",\"data\":null,\"timestamp\":1769331741495}', 0, '', '2026-01-25 17:02:21', 3);
INSERT INTO `sys_oper_log` VALUES (952, '操作日志', 0, 'com.star.pivot.controller.SysOperLogController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/operlog/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"title\":null,\"businessType\":null,\"operName\":null,\"status\":null,\"startTime\":null,\"endTime\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=1, rows=[OperLogVO(operId=951, title=操作日志, businessType=3, method=com.star.pivot.controller.SysOperLogController.clean(), requestMethod=DELETE, operatorType=1, operName=admin, deptName=星枢科技, operUrl=/api/sys/operlog/clean, operIp=0:0:0:0:0:0:0:1, operLocation=, operParam=, jsonResult={\"code\":200,\"message\":\"清空成功\",\"data\":null,\"timestamp\":1769331741495}, status=0, errorMsg=, operTime=2026-01-25T17:02:21, costTime=3)], pageNum=1, pageSize=20, pageCount=1), timestamp=1769331741520)', 0, '', '2026-01-25 17:02:22', 5);

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
) ENGINE = InnoDB AUTO_INCREMENT = 106 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2025-12-28 13:46:34', 'admin', '2026-01-23 19:59:34', '超级管理员：拥有所有菜单权限');
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', '2025-12-28 13:46:34', 'admin', '2026-01-12 20:33:35', '普通角色');
INSERT INTO `sys_role` VALUES (100, '测试专属', 'test', 3, '2', 1, 1, '0', '0', '', '2026-01-03 17:06:10', 'admin', '2026-01-15 20:53:20', '专属于测试的角色');
INSERT INTO `sys_role` VALUES (101, '全部权限测试角色', 'test_all', 10, '1', 1, 1, '0', '0', 'admin', '2026-01-24 00:26:42', '', NULL, NULL);
INSERT INTO `sys_role` VALUES (102, '自定权限测试角色', 'test_custom', 11, '2', 1, 1, '0', '0', 'admin', '2026-01-24 00:26:42', 'admin', '2026-01-24 01:32:09', NULL);
INSERT INTO `sys_role` VALUES (103, '本部门权限测试角色', 'test_dept', 12, '3', 1, 1, '0', '2', 'admin', '2026-01-24 00:26:42', 'admin', '2026-01-25 16:23:13', NULL);
INSERT INTO `sys_role` VALUES (104, '本部门及以下权限测试角色', 'test_dept_child', 13, '4', 1, 1, '0', '2', 'admin', '2026-01-24 00:26:42', 'admin', '2026-01-25 15:15:16', NULL);
INSERT INTO `sys_role` VALUES (105, '仅本人权限测试角色', 'test_self', 14, '5', 1, 1, '0', '2', 'admin', '2026-01-24 00:26:42', 'admin', '2026-01-25 15:12:55', NULL);

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
INSERT INTO `sys_role_dept` VALUES (175, 100, 100);
INSERT INTO `sys_role_dept` VALUES (170, 100, 101);
INSERT INTO `sys_role_dept` VALUES (172, 100, 102);
INSERT INTO `sys_role_dept` VALUES (171, 100, 105);
INSERT INTO `sys_role_dept` VALUES (173, 100, 108);
INSERT INTO `sys_role_dept` VALUES (174, 100, 109);
INSERT INTO `sys_role_dept` VALUES (176, 102, 101);
INSERT INTO `sys_role_dept` VALUES (177, 102, 103);

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
) ENGINE = InnoDB AUTO_INCREMENT = 767 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色与菜单关联表' ROW_FORMAT = Dynamic;

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
INSERT INTO `sys_role_menu` VALUES (582, 102, 1);
INSERT INTO `sys_role_menu` VALUES (589, 102, 2);
INSERT INTO `sys_role_menu` VALUES (583, 102, 3);
INSERT INTO `sys_role_menu` VALUES (584, 102, 4);
INSERT INTO `sys_role_menu` VALUES (585, 102, 55);
INSERT INTO `sys_role_menu` VALUES (586, 102, 56);
INSERT INTO `sys_role_menu` VALUES (587, 102, 57);
INSERT INTO `sys_role_menu` VALUES (588, 102, 58);
INSERT INTO `sys_role_menu` VALUES (544, 103, 1);
INSERT INTO `sys_role_menu` VALUES (574, 103, 2);
INSERT INTO `sys_role_menu` VALUES (545, 103, 3);
INSERT INTO `sys_role_menu` VALUES (546, 103, 4);
INSERT INTO `sys_role_menu` VALUES (551, 103, 5);
INSERT INTO `sys_role_menu` VALUES (561, 103, 6);
INSERT INTO `sys_role_menu` VALUES (569, 103, 8);
INSERT INTO `sys_role_menu` VALUES (562, 103, 39);
INSERT INTO `sys_role_menu` VALUES (564, 103, 40);
INSERT INTO `sys_role_menu` VALUES (565, 103, 41);
INSERT INTO `sys_role_menu` VALUES (552, 103, 42);
INSERT INTO `sys_role_menu` VALUES (553, 103, 43);
INSERT INTO `sys_role_menu` VALUES (554, 103, 44);
INSERT INTO `sys_role_menu` VALUES (555, 103, 45);
INSERT INTO `sys_role_menu` VALUES (566, 103, 46);
INSERT INTO `sys_role_menu` VALUES (570, 103, 47);
INSERT INTO `sys_role_menu` VALUES (571, 103, 48);
INSERT INTO `sys_role_menu` VALUES (572, 103, 49);
INSERT INTO `sys_role_menu` VALUES (573, 103, 50);
INSERT INTO `sys_role_menu` VALUES (547, 103, 55);
INSERT INTO `sys_role_menu` VALUES (548, 103, 56);
INSERT INTO `sys_role_menu` VALUES (549, 103, 57);
INSERT INTO `sys_role_menu` VALUES (550, 103, 58);
INSERT INTO `sys_role_menu` VALUES (567, 103, 59);
INSERT INTO `sys_role_menu` VALUES (563, 103, 60);
INSERT INTO `sys_role_menu` VALUES (556, 103, 61);
INSERT INTO `sys_role_menu` VALUES (568, 103, 87);
INSERT INTO `sys_role_menu` VALUES (557, 103, 88);
INSERT INTO `sys_role_menu` VALUES (558, 103, 89);
INSERT INTO `sys_role_menu` VALUES (559, 103, 90);
INSERT INTO `sys_role_menu` VALUES (560, 103, 91);

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
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 113 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 100, 'admin', '超级管理员', '00', 'admin@163.com', '18518712878', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/user/1/avatar_37095539-08cc-4144-939d-3bfc21544285.png?Expires=1769755973&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=GBduTIFnse2SvajNx39DIjiKpQs%3D', '$2a$10$FTKzzUPrLDbNfKoWn9e1y.B6D0MX0HeHBPHFRfcx3R2Ane73gMbf6', '0', '0', '0:0:0:0:0:0:0:1', '2025-12-29 22:16:38', '2026-01-04 18:13:47', 'admin', '2025-12-28 13:46:34', 'admin', '2026-01-23 14:52:54', '超级管理员');
INSERT INTO `sys_user` VALUES (2, 105, 'user', '用户管理员', '00', 'user@qq.com', '15666666666', '1', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/user/2/avatar_e69f4419-e074-46d5-8f21-9fe3d9ee3d11.webp?Expires=1769755975&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=aWBPH6OmGOIeDUT%2BTy6fiwewNl4%3D', '$2a$12$eYocwR0zs3iWKl7gsvHVQ.KLDTWvXqecm.29aXPi3IAF.mmkARVR.', '0', '0', '0:0:0:0:0:0:0:1', '2025-12-29 22:08:54', '2025-12-28 13:46:34', 'admin', '2025-12-28 13:46:34', 'admin', '2026-01-23 14:52:56', '测试员--用户管理员');
INSERT INTO `sys_user` VALUES (100, 100, 'xinxin', '辛鑫', '00', '1092203240@qq.com', '18834581124', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/100.png?Expires=1769762102&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=tDBMV8iyt8CUYADUsTgefkgG3nE%3D', '$2a$12$eYocwR0zs3iWKl7gsvHVQ.KLDTWvXqecm.29aXPi3IAF.mmkARVR.', '0', '0', '', NULL, NULL, 'admin', '2026-01-04 15:34:36', 'user', '2026-01-23 16:35:02', '作者，超级管理员');
INSERT INTO `sys_user` VALUES (101, 105, 'test', '测试用户', '00', '123@qq.com', '18825454547', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/101.webp?Expires=1769762054&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=GvZLy1UTJI%2BY2hN9YuWbFdJRGNo%3D', '$2a$10$YdqAWweActfkWOaWEjz9p.bBWqNWVdT9EQ2OHcUODgFg.f3ma13Va', '0', '0', '', NULL, NULL, 'admin', '2026-01-04 16:50:12', 'user', '2026-01-23 16:34:18', '测试用户专属');
INSERT INTO `sys_user` VALUES (102, 107, 'zhangsan', '张三', '00', 'zhangsan@qq.com', '18812345678', '1', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/user/102/avatar_bfe7b78b-3e54-4a14-be8f-85e74c7abbcb.webp?Expires=1769755945&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=45TyVskk5t6gF44mLNV7mfoik7M%3D', '$2a$10$cmueflWIca3U8waJO3gSv.HAEhTFvygR7lGdMto630Nbs2GcXXTpy', '0', '0', '', NULL, NULL, 'admin', '2026-01-14 18:04:51', 'admin', '2026-01-23 19:15:42', '张三-普通员工');
INSERT INTO `sys_user` VALUES (103, 101, 'test_all_user', '全部权限用户', '00', '', '', '0', '', '$2a$12$eYocwR0zs3iWKl7gsvHVQ.KLDTWvXqecm.29aXPi3IAF.mmkARVR.', '0', '0', '', NULL, NULL, 'admin', '2026-01-24 00:26:42', 'admin', '2026-01-25 14:59:00', '');
INSERT INTO `sys_user` VALUES (104, 101, 'test_custom_user', '自定权限用户', '00', '', '', '0', '', '$2a$12$eYocwR0zs3iWKl7gsvHVQ.KLDTWvXqecm.29aXPi3IAF.mmkARVR.', '0', '0', '', NULL, NULL, 'admin', '2026-01-24 00:26:42', 'admin', '2026-01-25 14:58:41', NULL);
INSERT INTO `sys_user` VALUES (105, 101, 'test_dept_user', '本部门权限用户', '00', '', '', '0', '', '$2a$12$eYocwR0zs3iWKl7gsvHVQ.KLDTWvXqecm.29aXPi3IAF.mmkARVR.', '0', '0', '', NULL, NULL, 'admin', '2026-01-24 00:26:42', 'admin', '2026-01-25 14:58:41', NULL);
INSERT INTO `sys_user` VALUES (106, 101, 'test_dept_child_user', '本部门及以下权限用户', '00', '', '', '0', '', '$2a$12$eYocwR0zs3iWKl7gsvHVQ.KLDTWvXqecm.29aXPi3IAF.mmkARVR.', '0', '0', '', NULL, NULL, 'admin', '2026-01-24 00:26:42', 'admin', '2026-01-25 14:58:41', NULL);
INSERT INTO `sys_user` VALUES (107, 101, 'test_self_user', '仅本人权限用户', '00', '', '', '0', '', '$2a$12$eYocwR0zs3iWKl7gsvHVQ.KLDTWvXqecm.29aXPi3IAF.mmkARVR.', '0', '0', '', NULL, NULL, 'admin', '2026-01-24 00:26:42', 'admin', '2026-01-25 14:58:30', NULL);
INSERT INTO `sys_user` VALUES (108, 101, 'dept101_user1', '部门101用户1', '00', '', '', '0', '', '$2a$12$eYocwR0zs3iWKl7gsvHVQ.KLDTWvXqecm.29aXPi3IAF.mmkARVR.', '0', '0', '', NULL, NULL, 'admin', '2026-01-24 00:26:42', 'admin', '2026-01-25 14:58:23', NULL);
INSERT INTO `sys_user` VALUES (109, 101, 'dept101_user2', '部门101用户2', '00', '', '', '0', '', '$2a$12$eYocwR0zs3iWKl7gsvHVQ.KLDTWvXqecm.29aXPi3IAF.mmkARVR.', '0', '0', '', NULL, NULL, 'admin', '2026-01-24 00:26:42', 'admin', '2026-01-25 14:58:23', NULL);
INSERT INTO `sys_user` VALUES (110, 103, 'dept103_user1', '部门103用户1', '00', '', '', '0', '', '$2a$12$eYocwR0zs3iWKl7gsvHVQ.KLDTWvXqecm.29aXPi3IAF.mmkARVR.', '0', '0', '', NULL, NULL, 'admin', '2026-01-24 00:26:42', 'admin', '2026-01-25 14:58:23', NULL);
INSERT INTO `sys_user` VALUES (111, 102, 'dept102_user1', '部门102用户1', '00', '', '', '0', '', '$2a$12$eYocwR0zs3iWKl7gsvHVQ.KLDTWvXqecm.29aXPi3IAF.mmkARVR.', '0', '2', '', NULL, NULL, 'admin', '2026-01-24 00:26:42', 'admin', '2026-01-25 16:24:34', NULL);
INSERT INTO `sys_user` VALUES (112, 100, 'lisi', '李四', '00', 'lisi@163.com', '18855555555', '1', NULL, '$2a$10$2eglfjL.MCwsuprXJfWIRezp.4/nV3hpuOOgtRHfG9d8kyCfVDsB.', '0', '0', '', NULL, NULL, 'admin', '2026-01-24 21:56:29', 'admin', '2026-01-25 13:14:38', '');

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
) ENGINE = InnoDB AUTO_INCREMENT = 153 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES (147, 1, 1);
INSERT INTO `sys_user_post` VALUES (148, 2, 2);
INSERT INTO `sys_user_post` VALUES (150, 100, 1);
INSERT INTO `sys_user_post` VALUES (149, 101, 4);
INSERT INTO `sys_user_post` VALUES (151, 102, 4);
INSERT INTO `sys_user_post` VALUES (152, 112, 4);

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
) ENGINE = InnoDB AUTO_INCREMENT = 165 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户与角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (149, 1, 1);
INSERT INTO `sys_user_role` VALUES (150, 2, 2);
INSERT INTO `sys_user_role` VALUES (152, 100, 1);
INSERT INTO `sys_user_role` VALUES (157, 100, 100);
INSERT INTO `sys_user_role` VALUES (151, 101, 2);
INSERT INTO `sys_user_role` VALUES (156, 101, 100);
INSERT INTO `sys_user_role` VALUES (153, 102, 2);
INSERT INTO `sys_user_role` VALUES (163, 103, 101);
INSERT INTO `sys_user_role` VALUES (159, 104, 102);
INSERT INTO `sys_user_role` VALUES (164, 112, 2);

SET FOREIGN_KEY_CHECKS = 1;
