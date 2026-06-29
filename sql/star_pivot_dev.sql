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

 Date: 29/06/2026 16:18:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`  (
  `table_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '编号',
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
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代码生成业务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table
-- ----------------------------
INSERT INTO `gen_table` VALUES (28, 'sys_dept', '部门管理', NULL, NULL, 'SysDept', 'tree', 'art-design-pro', 'com.star.pivot.system', 'system', 'dept', '部门管理', 'admin', '0', '/main/demo', '{\"treeCode\":\"id\",\"treeName\":\"name\",\"treeParentCode\":\"pid\",\"parentMenuId\":1}', 'admin', '2026-06-02 20:07:38', '', '2026-06-03 16:43:07', '导入自外部库 renren');
INSERT INTO `gen_table` VALUES (29, 'sys_user', '用户信息表', 'sys_user_role', 'user_id', 'SysUser', 'sub', 'element-ui', 'com.star.pivot.system', 'system', 'user', '用户信息', 'admin', '0', '/', '{\"treeCode\":\"\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuId\":1}', 'admin', '2026-06-03 17:12:30', '', '2026-06-03 20:40:27', '');
INSERT INTO `gen_table` VALUES (30, 'sys_user_role', '用户与角色关联表', NULL, NULL, 'SysUserRole', 'crud', '', 'com.star.pivot.system', 'system', 'role', '用户与角色关联', 'admin', '0', '/', NULL, 'admin', '2026-06-03 17:12:31', '', NULL, NULL);

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`  (
  `column_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint(0) NULL DEFAULT NULL COMMENT '归属表编号',
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
  `sort` int(0) NULL DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 237 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------
INSERT INTO `gen_table_column` VALUES (205, 28, 'id', 'id', 'bigint(20)', 'Long', 'id', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-06-02 20:07:38', '', '2026-06-03 16:43:07');
INSERT INTO `gen_table_column` VALUES (206, 28, 'pid', '上级ID', 'bigint(20)', 'Long', 'pid', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-06-02 20:07:38', '', '2026-06-03 16:43:07');
INSERT INTO `gen_table_column` VALUES (207, 28, 'pids', '所有上级ID，用逗号分开', 'varchar(500)', 'String', 'pids', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'textarea', '', 3, 'admin', '2026-06-02 20:07:38', '', '2026-06-03 16:43:07');
INSERT INTO `gen_table_column` VALUES (208, 28, 'name', '部门名称', 'varchar(50)', 'String', 'name', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'admin', '2026-06-02 20:07:38', '', '2026-06-03 16:43:07');
INSERT INTO `gen_table_column` VALUES (209, 28, 'sort', '排序', 'int(10) unsigned', 'Integer', 'sort', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2026-06-02 20:07:38', '', '2026-06-03 16:43:07');
INSERT INTO `gen_table_column` VALUES (210, 28, 'creator', '创建者', 'bigint(20)', 'Long', 'creator', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-06-02 20:07:38', '', '2026-06-03 16:43:07');
INSERT INTO `gen_table_column` VALUES (211, 28, 'create_date', '创建时间', 'datetime', 'LocalDateTime', 'createDate', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'datetime', '', 7, 'admin', '2026-06-02 20:07:38', '', '2026-06-03 16:43:07');
INSERT INTO `gen_table_column` VALUES (212, 28, 'updater', '更新者', 'bigint(20)', 'Long', 'updater', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 8, 'admin', '2026-06-02 20:07:38', '', '2026-06-03 16:43:07');
INSERT INTO `gen_table_column` VALUES (213, 28, 'update_date', '更新时间', 'datetime', 'LocalDateTime', 'updateDate', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'datetime', '', 9, 'admin', '2026-06-02 20:07:38', '', '2026-06-03 16:43:07');
INSERT INTO `gen_table_column` VALUES (214, 29, 'user_id', '用户ID', 'bigint', 'Long', 'userId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-06-03 17:12:30', '', '2026-06-03 20:40:27');
INSERT INTO `gen_table_column` VALUES (215, 29, 'dept_id', '部门ID', 'bigint', 'Long', 'deptId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-06-03 17:12:30', '', '2026-06-03 20:40:27');
INSERT INTO `gen_table_column` VALUES (216, 29, 'user_name', '用户账号', 'varchar(30)', 'String', 'userName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', 'input', '', 3, 'admin', '2026-06-03 17:12:30', '', '2026-06-03 20:40:27');
INSERT INTO `gen_table_column` VALUES (217, 29, 'nick_name', '用户昵称', 'varchar(30)', 'String', 'nickName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'admin', '2026-06-03 17:12:30', '', '2026-06-03 20:40:27');
INSERT INTO `gen_table_column` VALUES (218, 29, 'user_type', '用户类型（00系统用户）', 'varchar(2)', 'String', 'userType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', '', 5, 'admin', '2026-06-03 17:12:30', '', '2026-06-03 20:40:27');
INSERT INTO `gen_table_column` VALUES (219, 29, 'email', '用户邮箱', 'varchar(50)', 'String', 'email', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-06-03 17:12:30', '', '2026-06-03 20:40:28');
INSERT INTO `gen_table_column` VALUES (220, 29, 'phonenumber', '手机号码', 'varchar(11)', 'String', 'phonenumber', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 7, 'admin', '2026-06-03 17:12:30', '', '2026-06-03 20:40:28');
INSERT INTO `gen_table_column` VALUES (221, 29, 'sex', '用户性别（0男 1女 2未知）', 'char(1)', 'String', 'sex', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', 'sys_user_sex', 8, 'admin', '2026-06-03 17:12:30', '', '2026-06-03 20:40:28');
INSERT INTO `gen_table_column` VALUES (222, 29, 'avatar', '头像地址', 'varchar(500)', 'String', 'avatar', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'textarea', '', 9, 'admin', '2026-06-03 17:12:30', '', '2026-06-03 20:40:28');
INSERT INTO `gen_table_column` VALUES (223, 29, 'password', '密码', 'varchar(100)', 'String', 'password', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 10, 'admin', '2026-06-03 17:12:30', '', '2026-06-03 20:40:28');
INSERT INTO `gen_table_column` VALUES (224, 29, 'status', '账号状态（0正常 1停用）', 'char(1)', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', '', 11, 'admin', '2026-06-03 17:12:30', '', '2026-06-03 20:40:28');
INSERT INTO `gen_table_column` VALUES (225, 29, 'del_flag', '删除标志（0代表存在 2代表删除）', 'char(1)', 'String', 'delFlag', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 12, 'admin', '2026-06-03 17:12:30', '', '2026-06-03 20:40:28');
INSERT INTO `gen_table_column` VALUES (226, 29, 'login_ip', '最后登录IP', 'varchar(128)', 'String', 'loginIp', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 13, 'admin', '2026-06-03 17:12:30', '', '2026-06-03 20:40:28');
INSERT INTO `gen_table_column` VALUES (227, 29, 'login_date', '最后登录时间', 'datetime', 'LocalDateTime', 'loginDate', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'datetime', '', 14, 'admin', '2026-06-03 17:12:31', '', '2026-06-03 20:40:28');
INSERT INTO `gen_table_column` VALUES (228, 29, 'pwd_update_date', '密码最后更新时间', 'datetime', 'LocalDateTime', 'pwdUpdateDate', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'datetime', '', 15, 'admin', '2026-06-03 17:12:31', '', '2026-06-03 20:40:28');
INSERT INTO `gen_table_column` VALUES (229, 29, 'create_by', '创建者', 'varchar(64)', 'String', 'createBy', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 16, 'admin', '2026-06-03 17:12:31', '', '2026-06-03 20:40:28');
INSERT INTO `gen_table_column` VALUES (230, 29, 'create_time', '创建时间', 'datetime', 'LocalDateTime', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 17, 'admin', '2026-06-03 17:12:31', '', '2026-06-03 20:40:28');
INSERT INTO `gen_table_column` VALUES (231, 29, 'update_by', '更新者', 'varchar(64)', 'String', 'updateBy', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'input', '', 18, 'admin', '2026-06-03 17:12:31', '', '2026-06-03 20:40:28');
INSERT INTO `gen_table_column` VALUES (232, 29, 'update_time', '更新时间', 'datetime', 'LocalDateTime', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'datetime', '', 19, 'admin', '2026-06-03 17:12:31', '', '2026-06-03 20:40:28');
INSERT INTO `gen_table_column` VALUES (233, 29, 'remark', '备注', 'varchar(500)', 'String', 'remark', '0', '0', '0', '1', '1', '1', NULL, 'EQ', 'textarea', '', 20, 'admin', '2026-06-03 17:12:31', '', '2026-06-03 20:40:28');
INSERT INTO `gen_table_column` VALUES (234, 30, 'id', '主键ID', 'bigint', 'Long', 'id', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-06-03 17:12:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (235, 30, 'user_id', '用户ID', 'bigint', 'Long', 'userId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-06-03 17:12:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (236, 30, 'role_id', '角色ID', 'bigint', 'Long', 'roleId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-06-03 17:12:31', '', NULL);

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
  `fired_time` bigint(0) NOT NULL COMMENT '触发的时间',
  `sched_time` bigint(0) NOT NULL COMMENT '定时器制定的时间',
  `priority` int(0) NOT NULL COMMENT '优先级',
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
  `last_checkin_time` bigint(0) NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint(0) NOT NULL COMMENT '检查间隔时间',
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
  `repeat_count` bigint(0) NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint(0) NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint(0) NOT NULL COMMENT '已经触发的次数',
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
  `int_prop_1` int(0) NULL DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int(0) NULL DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint(0) NULL DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint(0) NULL DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
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
  `next_fire_time` bigint(0) NULL DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint(0) NULL DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int(0) NULL DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发器的类型',
  `start_time` bigint(0) NOT NULL COMMENT '开始时间',
  `end_time` bigint(0) NULL DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint(0) NULL DEFAULT NULL COMMENT '补偿执行的策略',
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
  `config_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE,
  INDEX `idx_config_key`(`config_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2026-01-25 17:41:36', '', NULL, '初始化密码 123456');
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
  `dept_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(0) NULL DEFAULT 0 COMMENT '父部门id',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int(0) NULL DEFAULT 0 COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE,
  INDEX `idx_del_flag_status`(`del_flag`, `status`) USING BTREE,
  INDEX `idx_order_num`(`order_num`) USING BTREE,
  INDEX `idx_dept_parent`(`parent_id`) USING BTREE,
  INDEX `idx_dept_ancestors`(`ancestors`) USING BTREE,
  INDEX `idx_dept_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 112 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (100, 0, '0', '星枢科技', 0, 'admin', '18888888888', '18888888888@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', 'admin', '2026-04-21 16:56:41');
INSERT INTO `sys_dept` VALUES (101, 100, '0,100', '深圳总公司', 1, 'admin', '18888888888', '18888888888@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', 'admin', '2026-04-21 16:56:49');
INSERT INTO `sys_dept` VALUES (102, 100, '0,100', '长沙分公司', 2, 'admin', '18888888888', '18888888888@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', 'admin', '2026-04-21 16:56:55');
INSERT INTO `sys_dept` VALUES (103, 101, '0,100,101', '研发部门', 1, 'admin', '18834581124', '	\r\n18834581124@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', '', NULL);
INSERT INTO `sys_dept` VALUES (104, 101, '0,100,101', '市场部门', 2, 'admin', '18834581124', '	\r\n18834581124@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', '', NULL);
INSERT INTO `sys_dept` VALUES (105, 101, '0,100,101', '测试部门', 3, 'admin', '18834581124', '	\r\n18834581124@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', '', NULL);
INSERT INTO `sys_dept` VALUES (106, 101, '0,100,101', '财务部门', 4, 'admin', '18834581124', '	\r\n18834581124@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', '', NULL);
INSERT INTO `sys_dept` VALUES (107, 101, '0,100,101', '运维部门', 5, 'admin', '18834581124', '	\r\n18834581124@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', '', NULL);
INSERT INTO `sys_dept` VALUES (108, 102, '0,100,102', '市场部门', 1, 'admin', '18834581124', '18834581124@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', '', NULL);
INSERT INTO `sys_dept` VALUES (109, 102, '0,100,102', '财务部门', 2, 'admin', '18834581124', '	\r\n18834581124@163.com', '0', '0', 'admin', '2025-12-28 13:46:34', '', NULL);
INSERT INTO `sys_dept` VALUES (110, 100, '0,100', '山西分公司', 3, 'admin', '18888888888', '18888888888@163.com', '0', '0', 'admin', '2026-01-20 12:52:02', 'admin', '2026-04-21 16:57:00');
INSERT INTO `sys_dept` VALUES (111, 110, '0,100,110', '人事部', 1, 'admin', '18834581124', '18834581124@163.com', '0', '0', 'admin', '2026-01-20 12:52:49', 'admin', '2026-01-20 12:53:15');

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_code` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int(0) NULL DEFAULT 0 COMMENT '字典排序',
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
  PRIMARY KEY (`dict_code`) USING BTREE,
  INDEX `idx_dict_type`(`dict_type`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_type_sort`(`dict_type`, `dict_sort`) USING BTREE,
  INDEX `idx_type_value`(`dict_type`, `dict_value`) USING BTREE
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
  `dict_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type`) USING BTREE,
  UNIQUE INDEX `uk_dict_type`(`dict_type`) USING BTREE
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
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `file_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `folder_id` bigint(0) NOT NULL COMMENT '所属文件夹ID',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务分类（冗余，便于检索）',
  `media_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'OTHER' COMMENT '媒体类型 IMAGE/VIDEO/DOCUMENT/AUDIO/OTHER',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始文件名',
  `file_ext` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展名',
  `content_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MIME类型',
  `file_size` bigint(0) NOT NULL DEFAULT 0 COMMENT '文件大小（字节）',
  `object_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'OSS/本地对象路径',
  `file_hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'SHA256（可选，去重用）',
  `storage_provider` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'OSS' COMMENT '存储驱动 OSS/LOCAL',
  `biz_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务类型 notice/contract 等',
  `biz_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务主键',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志（0正常 2已删除）',
  `delete_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`file_id`) USING BTREE,
  UNIQUE INDEX `uk_object_name`(`object_name`) USING BTREE,
  INDEX `idx_folder_del`(`folder_id`, `del_flag`) USING BTREE,
  INDEX `idx_media_type`(`media_type`, `del_flag`) USING BTREE,
  INDEX `idx_category_time`(`category`, `create_time`) USING BTREE,
  INDEX `idx_biz`(`biz_type`, `biz_id`) USING BTREE,
  INDEX `idx_recycle`(`del_flag`, `delete_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件中心-文件元数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_file
-- ----------------------------
INSERT INTO `sys_file` VALUES (1, 1, 'SYSTEM', 'IMAGE', 'wgS1Q5B8i97zWP7.thumb.1000_0.jpeg', 'jpeg', 'image/jpeg', 116206, 'file/system/1/2026/06/27/a9b4d733-0025-4dd3-a871-eaf58d819890.jpeg', NULL, 'OSS', NULL, NULL, '0', NULL, NULL, 'admin', '2026-06-27 16:24:06', '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_file_folder
-- ----------------------------
DROP TABLE IF EXISTS `sys_file_folder`;
CREATE TABLE `sys_file_folder`  (
  `folder_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '文件夹ID',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务分类 SYSTEM/OA/CONTRACT/CERT/PROJECT/CUSTOMER/GOODS/FINANCE/HR/OTHER',
  `folder_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件夹名称',
  `parent_id` bigint(0) NOT NULL DEFAULT 0 COMMENT '父文件夹ID（双层结构固定为0）',
  `order_num` int(0) NULL DEFAULT 0 COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`folder_id`) USING BTREE,
  UNIQUE INDEX `uk_category_folder`(`category`, `folder_name`, `del_flag`) USING BTREE,
  INDEX `idx_category_del`(`category`, `del_flag`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件中心-文件夹' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_file_folder
-- ----------------------------
INSERT INTO `sys_file_folder` VALUES (1, 'SYSTEM', '默认', 0, 1, '0', '0', 'admin', '2026-06-27 15:56:30', '', NULL, '系统通用-默认文件夹');
INSERT INTO `sys_file_folder` VALUES (2, 'OA', '默认', 0, 1, '0', '0', 'admin', '2026-06-27 15:56:30', '', NULL, '办公审批-默认文件夹');
INSERT INTO `sys_file_folder` VALUES (3, 'CONTRACT', '默认', 0, 1, '0', '0', 'admin', '2026-06-27 15:56:30', '', NULL, '合同档案-默认文件夹');
INSERT INTO `sys_file_folder` VALUES (4, 'CERT', '默认', 0, 1, '0', '0', 'admin', '2026-06-27 15:56:30', '', NULL, '资质证件-默认文件夹');
INSERT INTO `sys_file_folder` VALUES (5, 'PROJECT', '默认', 0, 1, '0', '0', 'admin', '2026-06-27 15:56:30', '', NULL, '项目资料-默认文件夹');
INSERT INTO `sys_file_folder` VALUES (6, 'CUSTOMER', '默认', 0, 1, '0', '0', 'admin', '2026-06-27 15:56:30', '', NULL, '客户资料-默认文件夹');
INSERT INTO `sys_file_folder` VALUES (7, 'GOODS', '默认', 0, 1, '0', '0', 'admin', '2026-06-27 15:56:30', '', NULL, '商品素材-默认文件夹');
INSERT INTO `sys_file_folder` VALUES (8, 'FINANCE', '默认', 0, 1, '0', '0', 'admin', '2026-06-27 15:56:30', '', NULL, '财务单据-默认文件夹');
INSERT INTO `sys_file_folder` VALUES (9, 'HR', '默认', 0, 1, '0', '0', 'admin', '2026-06-27 15:56:30', '', NULL, '人事档案-默认文件夹');
INSERT INTO `sys_file_folder` VALUES (10, 'OTHER', '默认', 0, 1, '0', '0', 'admin', '2026-06-27 15:56:30', '', NULL, '其他附件-默认文件夹');

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`  (
  `job_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
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
INSERT INTO `sys_job` VALUES (1, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '0 0 2 ? * MON', '3', '1', '0', 'admin', '2026-02-06 20:41:50', 'admin', '2026-05-18 23:48:38', '每天凌晨2点清空操作日志表 sys_oper_log');

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`  (
  `job_log_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日志信息',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '异常信息',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 62 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务调度日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------
INSERT INTO `sys_job_log` VALUES (52, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '执行成功', '0', '', '2026-04-23 20:12:00');
INSERT INTO `sys_job_log` VALUES (53, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '执行成功', '0', '', '2026-04-27 02:00:00');
INSERT INTO `sys_job_log` VALUES (54, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '执行成功', '0', '', '2026-05-18 23:48:33');
INSERT INTO `sys_job_log` VALUES (55, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '执行成功', '0', '', '2026-05-25 02:00:00');
INSERT INTO `sys_job_log` VALUES (56, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '执行成功', '0', '', '2026-06-01 02:00:00');
INSERT INTO `sys_job_log` VALUES (57, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '执行成功', '0', '', '2026-06-02 12:34:21');
INSERT INTO `sys_job_log` VALUES (58, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '执行成功', '0', '', '2026-06-08 02:00:00');
INSERT INTO `sys_job_log` VALUES (59, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '执行成功', '0', '', '2026-06-15 02:00:00');
INSERT INTO `sys_job_log` VALUES (60, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '执行成功', '0', '', '2026-06-22 02:00:00');
INSERT INTO `sys_job_log` VALUES (61, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '执行成功', '0', '', '2026-06-29 02:00:00');

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
  `info_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
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
  INDEX `idx_login_time`(`login_time`) USING BTREE,
  INDEX `idx_user_name`(`user_name`) USING BTREE,
  INDEX `idx_user_time`(`user_name`, `login_time`) USING BTREE,
  INDEX `idx_ipaddr`(`ipaddr`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_logininfor_time`(`login_time`) USING BTREE,
  INDEX `idx_logininfor_user`(`user_name`) USING BTREE,
  INDEX `idx_logininfor_ip`(`ipaddr`) USING BTREE,
  INDEX `idx_login_user_time`(`user_name`, `login_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 452 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES (439, 'admin', '222.138.215.72', '', 'Chrome 149', 'Windows 10', '1', '用户名或密码错误', '2026-06-23 17:27:05');
INSERT INTO `sys_logininfor` VALUES (440, '15127000315', '120.8.153.34', '', 'Edge 142', 'Windows 10', '1', '用户名或密码错误', '2026-06-24 11:45:46');
INSERT INTO `sys_logininfor` VALUES (441, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 149', 'Windows 10', '0', '登录成功', '2026-06-27 15:54:32');
INSERT INTO `sys_logininfor` VALUES (442, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 149', 'Windows 10', '0', '登录成功', '2026-06-27 15:57:17');
INSERT INTO `sys_logininfor` VALUES (443, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'WindowsPowerShell 5.1.22621.6133', 'Windows 10', '1', '验证码错误或已失效', '2026-06-27 16:00:43');
INSERT INTO `sys_logininfor` VALUES (444, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 149', 'Windows 10', '1', '用户名或密码错误', '2026-06-27 18:00:44');
INSERT INTO `sys_logininfor` VALUES (445, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 149', 'Windows 10', '0', '登录成功', '2026-06-27 19:46:57');
INSERT INTO `sys_logininfor` VALUES (446, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 149', 'Windows 10', '1', '登录异常: Redis command timed out', '2026-06-27 20:05:00');
INSERT INTO `sys_logininfor` VALUES (447, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 149', 'Windows 10', '0', '登录成功', '2026-06-27 20:05:12');
INSERT INTO `sys_logininfor` VALUES (448, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 149', 'Windows 10', '0', '登录成功', '2026-06-27 20:06:44');
INSERT INTO `sys_logininfor` VALUES (449, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 149', 'Windows 10', '0', '登录成功', '2026-06-27 20:13:04');
INSERT INTO `sys_logininfor` VALUES (450, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 149', 'Windows 10', '0', '登录成功', '2026-06-27 20:14:06');
INSERT INTO `sys_logininfor` VALUES (451, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 149', 'Windows 10', '0', '登录成功', '2026-06-29 10:36:37');
INSERT INTO `sys_logininfor` VALUES (452, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 149', 'Windows 10', '0', '登录成功', '2026-06-29 11:12:44');
INSERT INTO `sys_logininfor` VALUES (453, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 149', 'Windows 10', '0', '登录成功', '2026-06-29 15:04:49');
INSERT INTO `sys_logininfor` VALUES (454, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 149', 'Windows 10', '0', '登录成功', '2026-06-29 15:08:59');
INSERT INTO `sys_logininfor` VALUES (455, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 149', 'Windows 10', '0', '登录成功', '2026-06-29 15:16:10');
INSERT INTO `sys_logininfor` VALUES (456, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 149', 'Windows 10', '0', '登录成功', '2026-06-29 15:20:25');
INSERT INTO `sys_logininfor` VALUES (457, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 149', 'Windows 10', '0', '登录成功', '2026-06-29 15:45:25');
INSERT INTO `sys_logininfor` VALUES (458, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 149', 'Windows 10', '0', '登录成功', '2026-06-29 16:10:52');

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
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 1, '/system', '', NULL, 'System', 1, 1, 'M', '0', '0', '', 'ep:setting', 'system', '2025-12-31 16:34:16', 'admin', '2026-06-29 15:47:56', '系统管理模块111');
INSERT INTO `sys_menu` VALUES (2, '系统工具', 0, 2, '/tools', '', NULL, 'SystemTools', 1, 1, 'M', '0', '0', '', 'clarity:tools-line', 'admin', '2026-01-20 13:08:43', 'admin', '2026-04-19 19:09:23', '系统工具');
INSERT INTO `sys_menu` VALUES (3, '系统监控', 0, 3, '/monitor', '', NULL, 'Monitor', 1, 1, 'M', '0', '0', '', 'material-symbols:monitor-outline', 'admin', '2026-01-25 18:00:43', 'admin', '2026-05-18 15:42:45', '系统监控模块');
INSERT INTO `sys_menu` VALUES (4, '星枢项目', 0, 99, 'https://gitee.com/xin1998/StarPivot', '', NULL, '', 0, 1, 'M', '0', '0', '', 'ri:gitee-fill', 'admin', '2026-04-21 12:48:19', 'admin', '2026-05-18 22:44:22', '星枢项目');
INSERT INTO `sys_menu` VALUES (5, 'art-design-pro', 0, 100, 'https://gitee.com/lingchen163/art-design-pro', '', NULL, '', 0, 1, 'M', '0', '0', '', 'ri:guide-fill', 'admin', '2026-04-19 19:07:54', 'admin', '2026-04-23 20:26:22', '');
INSERT INTO `sys_menu` VALUES (6, '文件中心', 0, 4, '/file', '', NULL, 'FileCenter', 1, 1, 'M', '0', '0', '', 'ep:folder-opened', 'admin', '2026-06-27 15:56:30', '', NULL, '文件中心模块');
INSERT INTO `sys_menu` VALUES (7, '菜单管理', 1, 1, 'menu', '/system/menu', NULL, 'SystemMenu', 1, 1, 'C', '0', '0', 'system:menu:list', 'ep:menu', 'system', '2025-12-31 16:34:16', '', '2026-01-02 21:12:33', '菜单管理模块');
INSERT INTO `sys_menu` VALUES (8, '角色管理', 1, 2, 'role', '/system/role', NULL, 'SystemRole', 1, 1, 'C', '0', '0', 'system:role:list', 'oui:app-users-roles', 'system', '2025-12-31 16:34:16', 'admin', '2026-06-08 16:46:55', '角色管理模块');
INSERT INTO `sys_menu` VALUES (9, '用户管理', 1, 3, 'user', '/system/user', NULL, 'SystemUser', 1, 1, 'C', '0', '0', 'system:user:list', 'mdi:user', 'system', '2025-12-31 16:34:16', '', '2026-01-02 21:31:51', '用户管理模块');
INSERT INTO `sys_menu` VALUES (10, '部门管理', 1, 4, 'dept', '/system/dept', NULL, 'SystemDept', 1, 1, 'C', '0', '0', 'system:dept:list', 'ri:organization-chart', '', '2026-01-02 21:04:34', '', '2026-01-02 21:36:43', '部门管理模块');
INSERT INTO `sys_menu` VALUES (11, '岗位管理', 1, 5, 'post', '/system/post/index', NULL, 'PostManage', 1, 1, 'C', '0', '0', 'system:post:list', 'picon:post', 'xinxin', '2026-01-04 19:23:51', 'xinxin', '2026-01-04 19:25:02', '岗位管理模块');
INSERT INTO `sys_menu` VALUES (12, '字典管理', 1, 6, 'dict', '/system/dict/index', NULL, 'DictManage', 1, 1, 'C', '0', '0', 'system:type:list', 'arcticons:zdict', 'admin', '2026-01-05 12:28:54', 'admin', '2026-01-19 21:37:20', '字典管理模块。有：字典数据   字典类型');
INSERT INTO `sys_menu` VALUES (13, '日志管理', 1, 7, 'log', '', NULL, 'LogManager', 1, 1, 'M', '0', '0', '', 'mdi:math-log', 'admin', '2026-01-23 13:37:05', 'admin', '2026-05-15 09:09:47', '');
INSERT INTO `sys_menu` VALUES (14, '通知公告', 1, 8, 'notice', '/system/notice/index', NULL, 'NoticeManage', 1, 0, 'C', '0', '0', 'system:notice:list', 'fe:notice-active', 'admin', '2026-02-05 17:38:35', 'admin', '2026-03-31 22:03:49', '通知公告菜单');
INSERT INTO `sys_menu` VALUES (15, '参数配置', 1, 9, 'config', '/system/config/index', NULL, 'ConfigManage', 1, 1, 'C', '0', '0', 'system:config:list', 'mynaui:config', 'admin', '2026-03-31 22:03:28', 'admin', '2026-03-31 22:05:20', '参数配置菜单');
INSERT INTO `sys_menu` VALUES (16, '操作日志', 13, 1, 'oper', '/system/log/oper/index', NULL, 'OperLog', 1, 1, 'C', '0', '0', 'system:log:list', 'icon-park-solid:log', 'admin', '2026-01-23 13:40:41', '', NULL, '操作日志');
INSERT INTO `sys_menu` VALUES (17, '登录日志', 13, 2, 'login', '/system/log/login/index', NULL, 'LoginInfoLog', 1, 1, 'C', '0', '0', 'system:login:list', 'icon-park-solid:log', 'admin', '2026-01-23 13:51:37', '', NULL, '登录日志');
INSERT INTO `sys_menu` VALUES (18, '代码生成', 2, 1, 'generator', '/tools/generator/index', NULL, 'GenerateTools', 1, 1, 'C', '0', '0', 'tools:generator:list', 'mdi:generator-mobile', 'admin', '2026-01-20 13:15:59', 'admin', '2026-01-20 13:25:42', '代码生成');
INSERT INTO `sys_menu` VALUES (19, '外部库代码生成', 2, 2, 'external', '/tools/generator-external/index', NULL, 'GenExternal', 1, 1, 'C', '0', '0', '', 'ri:database-2-line', 'admin', '2026-06-29 15:43:12', 'admin', '2026-06-29 15:43:12', '外部数据库连接代码生成');
INSERT INTO `sys_menu` VALUES (20, '服务器监控', 3, 1, 'server', '/monitor/server/index', NULL, 'ServerMonitor', 1, 0, 'C', '0', '0', 'monitor:server:query', 'ri:server-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:33:45', '服务器信息监控');
INSERT INTO `sys_menu` VALUES (21, 'Druid监控', 3, 2, 'druid', '/monitor/druid/index', NULL, 'DruidMonitor', 1, 0, 'C', '0', '0', 'monitor:druid:query', 'ri:database-2-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:32:03', 'Druid数据库连接池监控');
INSERT INTO `sys_menu` VALUES (22, 'Redis缓存', 3, 3, 'redis', '/monitor/redis/index', NULL, 'RedisMonitor', 1, 0, 'C', '0', '0', 'monitor:redis:query', 'ri:database-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-03-01 13:37:51', 'Redis缓存监控');
INSERT INTO `sys_menu` VALUES (23, '在线用户', 3, 4, 'online', '/monitor/online/index', NULL, 'OnlineUser', 1, 0, 'C', '0', '0', 'monitor:online:query', 'ri:user-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:32:45', '在线用户监控');
INSERT INTO `sys_menu` VALUES (24, '定时任务', 3, 5, 'job', '/monitor/job/index', NULL, 'MonitorJob', 1, 1, 'C', '0', '0', 'monitor:job:query', 'ri:time-line', 'admin', '2026-02-06 19:58:43', 'admin', '2026-02-06 20:34:11', '定时任务调度');
INSERT INTO `sys_menu` VALUES (25, '文件管理', 6, 1, 'index', '/file/index', NULL, 'FileManage', 1, 1, 'C', '0', '0', 'file:resource:list', 'ep:document', 'admin', '2026-06-27 15:56:30', '', NULL, '文件管理页面');
INSERT INTO `sys_menu` VALUES (26, '菜单查询', 7, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2026-01-12 17:39:26', '', NULL, '菜单查询');
INSERT INTO `sys_menu` VALUES (27, '菜单添加', 7, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2026-01-12 17:39:50', '', NULL, '菜单添加');
INSERT INTO `sys_menu` VALUES (28, '菜单修改', 7, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2026-01-12 17:40:05', '', NULL, '菜单修改');
INSERT INTO `sys_menu` VALUES (29, '菜单删除', 7, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:menu:delete', '#', 'admin', '2026-01-12 17:40:27', '', NULL, '菜单删除');
INSERT INTO `sys_menu` VALUES (30, '新增角色', 8, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:add', '#', 'admin', '2026-01-12 17:32:13', '', NULL, '新增角色按钮');
INSERT INTO `sys_menu` VALUES (31, '修改角色', 8, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2026-01-12 17:32:45', '', NULL, '修改角色按钮');
INSERT INTO `sys_menu` VALUES (32, '删除角色', 8, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:delete', '#', 'admin', '2026-01-12 17:33:14', '', NULL, '删除角色按钮');
INSERT INTO `sys_menu` VALUES (33, '角色查询', 8, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:query', '#', 'admin', '2026-01-12 17:34:57', '', NULL, '角色查询 按钮');
INSERT INTO `sys_menu` VALUES (34, '分配数据权限', 8, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:assignDataScope', '#', 'admin', '2026-01-16 19:04:22', 'admin', '2026-01-23 20:57:15', '分配角色');
INSERT INTO `sys_menu` VALUES (35, '已分配用户', 8, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:allocatedList', '#', 'admin', '2026-01-23 20:55:10', 'admin', '2026-01-23 22:41:41', '已分配用户');
INSERT INTO `sys_menu` VALUES (36, '未分配用户', 8, 7, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:unallocatedList', '#', 'admin', '2026-01-23 22:41:08', '', NULL, '未分配用户');
INSERT INTO `sys_menu` VALUES (37, '分配用户', 8, 7, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:assignUser', '#', 'admin', '2026-01-23 23:18:49', '', NULL, '分配用户：添加用户角色关联表');
INSERT INTO `sys_menu` VALUES (38, '取消授权', 8, 9, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:role:cancelUser', '#', 'admin', '2026-01-23 23:26:23', '', NULL, '取消授权');
INSERT INTO `sys_menu` VALUES (39, '新增用户', 9, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:add', '#', 'admin', '2026-01-12 16:42:30', 'admin', '2026-01-16 20:42:35', '新增用户按钮');
INSERT INTO `sys_menu` VALUES (40, '修改状态', 9, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:changeStatus', '#', 'admin', '2026-01-16 18:11:22', 'admin', '2026-01-16 20:42:31', '修改状态');
INSERT INTO `sys_menu` VALUES (41, '修改用户', 9, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2026-01-12 17:30:37', 'admin', '2026-01-16 20:42:26', '修改用户  按钮');
INSERT INTO `sys_menu` VALUES (42, '删除用户', 9, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:delete', '#', 'admin', '2026-01-12 17:31:11', 'admin', '2026-01-16 20:42:39', '删除用户按钮');
INSERT INTO `sys_menu` VALUES (43, '用户查询', 9, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:query', '#', 'admin', '2026-01-12 17:35:27', 'admin', '2026-01-16 20:42:43', '用户查询按钮');
INSERT INTO `sys_menu` VALUES (44, '修改密码', 9, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2026-01-16 18:10:55', 'admin', '2026-01-16 20:42:48', '修改密码');
INSERT INTO `sys_menu` VALUES (45, '解除锁定', 9, 7, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:unLock', '#', 'admin', '2026-01-23 18:49:56', '', NULL, '解除锁定');
INSERT INTO `sys_menu` VALUES (46, '用户导入', 9, 8, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:import', '#', 'admin', '2026-01-24 21:03:18', '', NULL, '用户导入');
INSERT INTO `sys_menu` VALUES (47, '用户导出', 9, 9, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:user:export', '#', 'admin', '2026-01-24 21:03:52', '', NULL, '用户导出');
INSERT INTO `sys_menu` VALUES (48, '部门查询', 10, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:dept:query', '#', 'admin', '2026-01-12 17:35:58', '', NULL, '部门查询按钮');
INSERT INTO `sys_menu` VALUES (49, '部门新增', 10, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:dept:add', '#', 'admin', '2026-01-12 17:36:28', '', NULL, '部门新增');
INSERT INTO `sys_menu` VALUES (50, '部门修改', 10, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2026-01-12 17:36:49', '', NULL, '部门修改');
INSERT INTO `sys_menu` VALUES (51, '部门删除', 10, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:dept:delete', '#', 'admin', '2026-01-12 17:37:21', '', NULL, '部门删除');
INSERT INTO `sys_menu` VALUES (52, '岗位查询', 11, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:post:query', '#', 'admin', '2026-01-12 17:37:47', '', NULL, '岗位查询');
INSERT INTO `sys_menu` VALUES (53, '岗位新增', 11, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:post:add', '#', 'admin', '2026-01-12 17:38:09', '', NULL, '岗位新增');
INSERT INTO `sys_menu` VALUES (54, '岗位修改', 11, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:post:edit', '#', 'admin', '2026-01-12 17:38:46', '', NULL, '岗位修改');
INSERT INTO `sys_menu` VALUES (55, '岗位删除', 11, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:post:delete', '#', 'admin', '2026-01-12 17:39:04', '', NULL, '岗位删除');
INSERT INTO `sys_menu` VALUES (56, '字典类型添加', 12, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:type:add', '#', 'admin', '2026-01-16 19:08:40', 'admin', '2026-01-16 19:33:35', '字典类型添加');
INSERT INTO `sys_menu` VALUES (57, '字典类型修改', 12, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:type:edit', '#', 'admin', '2026-01-16 19:09:04', 'admin', '2026-01-16 19:33:43', '字典类型修改');
INSERT INTO `sys_menu` VALUES (58, '字典类型删除', 12, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:type:delete', '#', 'admin', '2026-01-16 19:09:27', 'admin', '2026-01-16 19:33:48', '字典类型删除');
INSERT INTO `sys_menu` VALUES (59, '字典类型查询', 12, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:type:query', '#', 'admin', '2026-01-19 21:33:21', '', NULL, '字典类型查询');
INSERT INTO `sys_menu` VALUES (60, '字典数据添加', 12, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:data:add', '#', 'admin', '2026-01-16 19:31:42', '', NULL, '字典数据添加');
INSERT INTO `sys_menu` VALUES (61, '字典数据修改', 12, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:data:edit', '#', 'admin', '2026-01-16 19:32:19', '', NULL, '字典数据修改');
INSERT INTO `sys_menu` VALUES (62, '字典数据删除', 12, 7, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:data:delete', '#', 'admin', '2026-01-16 19:32:51', '', NULL, '字典数据删除');
INSERT INTO `sys_menu` VALUES (63, '字典数据查询', 12, 8, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:data:query', '#', 'admin', '2026-01-19 21:33:59', '', NULL, '字典数据查询');
INSERT INTO `sys_menu` VALUES (64, '日志查询', 16, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:operlog:query', '#', 'admin', '2026-01-23 13:58:02', '', NULL, '日志查询');
INSERT INTO `sys_menu` VALUES (65, '清空日志', 16, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:operlog:delete', '#', 'admin', '2026-01-23 13:57:43', '', NULL, '清空日志');
INSERT INTO `sys_menu` VALUES (66, '日志查询', 17, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:logininfor:query', '#', 'admin', '2026-01-23 14:24:26', '', NULL, '日志查询');
INSERT INTO `sys_menu` VALUES (67, '日志删除', 17, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'system:logininfor:delete', '#', 'admin', '2026-01-23 14:24:41', '', NULL, '日志删除');
INSERT INTO `sys_menu` VALUES (68, '通知公告查询', 14, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (69, '通知公告新增', 14, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (70, '通知公告修改', 14, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (71, '通知公告删除', 14, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:delete', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (72, '通知公告导出', 14, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:notice:export', '#', 'admin', '2026-02-05 17:38:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (73, '参数配置查询', 15, 1, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:query', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:23', '');
INSERT INTO `sys_menu` VALUES (74, '参数配置新增', 15, 2, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:add', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:27', '');
INSERT INTO `sys_menu` VALUES (75, '参数配置修改', 15, 3, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:edit', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:31', '');
INSERT INTO `sys_menu` VALUES (76, '参数配置删除', 15, 4, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:delete', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:36', '');
INSERT INTO `sys_menu` VALUES (77, '参数配置导出', 15, 5, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:export', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:39', '');
INSERT INTO `sys_menu` VALUES (78, '添加', 18, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:add', '#', 'admin', '2026-01-23 15:56:17', 'admin', '2026-01-23 19:01:31', '添加');
INSERT INTO `sys_menu` VALUES (79, '列表查询', 18, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2026-01-20 14:56:43', '', NULL, '列表查询');
INSERT INTO `sys_menu` VALUES (80, '预览', 18, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2026-01-23 15:53:25', 'admin', '2026-01-23 19:01:40', '预览');
INSERT INTO `sys_menu` VALUES (81, '编辑', 18, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2026-01-23 15:53:47', 'admin', '2026-01-23 19:01:44', '');
INSERT INTO `sys_menu` VALUES (82, '删除', 18, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:delete', '#', 'admin', '2026-01-23 15:54:05', 'admin', '2026-01-23 19:01:48', '');
INSERT INTO `sys_menu` VALUES (83, '同步数据库', 18, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:sync', '#', 'admin', '2026-01-23 15:55:18', 'admin', '2026-01-23 19:01:52', '同步数据库');
INSERT INTO `sys_menu` VALUES (84, '生成代码', 18, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:create', '#', 'admin', '2026-01-23 15:55:59', 'admin', '2026-01-23 19:01:56', '生成代码');
INSERT INTO `sys_menu` VALUES (85, '导入', 18, 8, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:import', '#', 'xinxin', '2026-01-24 00:08:41', '', NULL, '导入');
INSERT INTO `sys_menu` VALUES (86, '查询', 19, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:external:query', '#', 'admin', '2026-06-29 15:43:12', '', NULL, '连接、查表、路径配置');
INSERT INTO `sys_menu` VALUES (87, '预览', 19, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:external:preview', '#', 'admin', '2026-06-29 15:43:13', '', NULL, '预览代码');
INSERT INTO `sys_menu` VALUES (88, '生成', 19, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:external:create', '#', 'admin', '2026-06-29 15:43:13', '', NULL, '下载 ZIP');
INSERT INTO `sys_menu` VALUES (89, '删除缓存', 22, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:cache:remove', '#', 'admin', '2026-01-25 22:19:38', '', NULL, 'monitor:cache:remove');
INSERT INTO `sys_menu` VALUES (90, '清空缓存', 22, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:cache:clear', '#', 'admin', '2026-01-25 22:20:00', '', NULL, '清空缓存');
INSERT INTO `sys_menu` VALUES (91, '缓存列表', 22, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:cache:query', '#', 'admin', '2026-01-27 18:21:55', '', NULL, '缓存列表');
INSERT INTO `sys_menu` VALUES (92, '强制下线', 23, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:online:force-logout', '', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:30:55', '强制用户下线');
INSERT INTO `sys_menu` VALUES (93, '任务查询', 24, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2026-02-06 20:01:06', '', NULL, '');
INSERT INTO `sys_menu` VALUES (94, '任务新增', 24, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2026-02-06 20:01:06', '', NULL, '');
INSERT INTO `sys_menu` VALUES (95, '任务修改', 24, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2026-02-06 20:01:06', '', NULL, '');
INSERT INTO `sys_menu` VALUES (96, '任务删除', 24, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:job:delete', '#', 'admin', '2026-02-06 20:01:06', '', NULL, '');
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

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob NULL COMMENT '公告内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`) USING BTREE,
  INDEX `idx_notice_status_create`(`status`, `create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES (3, '测试新增：2026-2-5 ', '1', 0x3C703EE6B58BE8AF95E696B0E5A29E3C2F703E, '0', 'admin', '2026-02-05 19:32:02', 'admin', '2026-06-01 12:19:10', NULL);

-- ----------------------------
-- Table structure for sys_online_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_online_user`;
CREATE TABLE `sys_online_user`  (
  `session_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话ID（Redis key）',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户ID',
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
  `expire_time` int(0) NULL DEFAULT NULL COMMENT '会话超时时间（秒）',
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
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:1', 1, 'admin', '超级管理员', '星枢科技', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 149', 'Windows 10', '1', '2026-06-29 15:45:28', '2026-06-29 15:45:28', '2026-06-29 16:10:44', NULL, '', '0', '2026-06-29 16:10:44');
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:100', 100, 'xinxin', '超级管理员', '星枢科技', '0:0:0:0:0:0:0:1', '内网IP', 'Edge 149', 'Windows 10', '1', '2026-06-09 18:54:00', '2026-06-09 18:54:00', '2026-06-09 19:00:12', NULL, '', '1', '2026-06-09 19:00:12');
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:113', 113, 'wangwu', 'wangwu', '人事部', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '1', '2026-01-27 19:31:06', '2026-01-27 19:31:06', '2026-01-27 19:36:21', NULL, '', '0', '2026-01-27 19:36:21');
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:114', 114, 'starPivot', 'starPivot演示用户', '山西分公司', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10', '1', '2026-02-09 13:31:46', '2026-02-09 13:31:46', '2026-02-09 13:32:38', NULL, '', '0', '2026-02-09 13:32:38');
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:115', 115, 'xiaoming', 'xiaoming', '', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10', '1', '2026-02-12 16:49:35', '2026-02-12 16:49:35', '2026-02-12 16:54:27', NULL, '', '0', '2026-02-12 16:54:27');
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:116', 116, '15536155268', '15536155268', '星枢科技', '183.200.95.198', '', 'Chrome Webview 134.0.6998.135', 'Android 16', '1', '2026-04-24 10:23:51', '2026-04-24 10:23:51', '2026-04-24 10:29:36', NULL, '', '1', '2026-04-24 10:29:36');
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:2', 2, 'user', '用户管理员', '测试部门', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10', '1', '2026-02-09 12:31:16', '2026-02-09 12:31:16', '2026-02-09 12:31:52', NULL, '', '0', '2026-02-09 12:31:52');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `oper_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int(0) NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求方式',
  `operator_type` int(0) NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '返回参数',
  `status` int(0) NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime(0) NULL DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint(0) NULL DEFAULT 0 COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`) USING BTREE,
  INDEX `idx_sys_oper_log_bt`(`business_type`) USING BTREE,
  INDEX `idx_sys_oper_log_s`(`status`) USING BTREE,
  INDEX `idx_oper_time`(`oper_time`) USING BTREE,
  INDEX `idx_oper_name`(`oper_name`) USING BTREE,
  INDEX `idx_module_time`(`title`, `oper_time`) USING BTREE,
  INDEX `idx_operlog_time`(`oper_time`) USING BTREE,
  INDEX `idx_operlog_user`(`oper_name`) USING BTREE,
  INDEX `idx_operlog_type`(`business_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4692 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES (4755, '清空操作日志', 9, 'com.star.pivot.controller.system.SysOperLogController.clean()', 'DELETE', 1, 'admin', '星枢科技', '/api/v1/sys/operlog/clean', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"清空成功\",\"data\":null,\"timestamp\":1782719978153}', 0, '', '2026-06-29 15:59:38', 178);
INSERT INTO `sys_oper_log` VALUES (4756, '操作日志', 0, 'com.star.pivot.controller.system.SysOperLogController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/v1/sys/operlog/pageList', '0:0:0:0:0:0:0:1', '', '{\"pageNum\":1,\"pageSize\":20,\"title\":null,\"businessType\":null,\"operName\":null,\"status\":null,\"startTime\":null,\"endTime\":null}', 'Result(code=200, message=操作成功, data=PageResponse(total=1, rows=[OperLogVO(operId=4755, title=清空操作日志, businessType=9, method=com.star.pivot.controller.system.SysOperLogController.clean(), requestMethod=DELETE, operatorType=1, operName=admin, deptName=星枢科技, operUrl=/api/v1/sys/operlog/clean, operIp=0:0:0:0:0:0:0:1, operLocation=, operParam=, jsonResult={\"code\":200,\"message\":\"清空成功\",\"data\":null,\"timestamp\":1782719978153}, status=0, errorMsg=, operTime=2026-06-29T15:59:38, costTime=178)], pageNum=1, pageSize=20, pageCount=1), timestamp=1782719978810)', 0, '', '2026-06-29 15:59:39', 204);
INSERT INTO `sys_oper_log` VALUES (4757, '服务器监控', 0, 'com.star.pivot.monitor.controller.ServerMonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/v1/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.4128499548445362,\"used\":0.7998967875112889,\"wait\":0.0,\"free\":98.78725325764418},\"memory\":{\"total\":24428,\"used\":8224,\"free\":16203,\"usage\":33.6684585460847},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1782719893204,\"runTime\":101601,\"home\":\"E:\\\\JEnv\\\\jdk\\\\jdk-17\",\"userDir\":\"E:\\\\star-pivot\\\\project0422\",\"inputArgs\":\"-Dfile.encoding=UTF8 -XX:TieredStopAtLevel=1 -Dspring.output.ansi.enabled=always -Dcom.sun.management.jmxremote -Dspring.jmx.enabled=true -Dspring.liveBeansView.mbeanDomain -Dspring.application.admin.enabled=true -Dmanagement.endpoints.jmx.exposure.include=* -javaagent:C:\\\\Program Files\\\\JetBrains\\\\IntelliJ IDEA 2025.3.3\\\\lib\\\\idea_rt.jar=50814 -Dfile.encoding=UTF-8\",\"max\":6108,\"total\":396,\"used\":232,\"free\":5875,\"usage\":3.8087892313759233},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.0.4\"},\"disk\":{\"total\":1429,\"used\":309,\"free\":1120,\"usage\":21.613605284171065,\"stores\":[{\"mount\":\"C:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (C:)\",\"totalGb\":476.03,\"usableGb\":267.58,\"usedGb\":208.45,\"usage\":43.79},{\"mount\":\"D:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (D:)\",\"totalGb\":390.62,\"usableGb\":321.73,\"usedGb\":68.9,\"usage\":17.64},{\"mount\":\"E:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (E:)\",\"totalGb\":195.2,\"usableGb\":172.85,\"usedGb\":22.35,\"usage\":11.45},{\"mount\":\"F:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (F:)\",\"totalGb\":367.93,\"usableGb\":358.59,\"usedGb\":9.33,\"usage\":2.54}]}},\"timestamp\":1782719994833}', 0, '', '2026-06-29 15:59:55', 149);
INSERT INTO `sys_oper_log` VALUES (4758, 'Druid监控', 0, 'com.star.pivot.monitor.controller.DruidMonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/v1/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-1900750749\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":1,\"minIdle\":1,\"maxActive\":20,\"activeCount\":0,\"activePeak\":3,\"usage\":0.0},\"sqlStat\":{\"executeCount\":122,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1782719996094}', 0, '', '2026-06-29 15:59:56', 70);
INSERT INTO `sys_oper_log` VALUES (4759, 'Redis缓存管理', 0, 'com.star.pivot.monitor.controller.CacheController.getCacheList()', 'GET', 1, 'admin', '星枢科技', '/api/v1/monitor/cache/list', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":[{\"cacheName\":\"cache:deptTree\",\"remark\":\"部门树缓存\",\"keys\":null},{\"cacheName\":\"cache:dictData\",\"remark\":\"字典数据缓存\",\"keys\":null},{\"cacheName\":\"cache:menuTree\",\"remark\":\"菜单树缓存\",\"keys\":null},{\"cacheName\":\"cache:userPermissions\",\"remark\":\"用户权限缓存\",\"keys\":null},{\"cacheName\":\"dashboard\",\"remark\":\"dashboard\",\"keys\":null},{\"cacheName\":\"gen\",\"remark\":\"gen\",\"keys\":null},{\"cacheName\":\"jwt\",\"remark\":\"jwt\",\"keys\":null},{\"cacheName\":\"sysConfig\",\"remark\":\"sysConfig\",\"keys\":null}],\"timestamp\":1782719997709}', 0, '', '2026-06-29 15:59:58', 262);
INSERT INTO `sys_oper_log` VALUES (4760, '在线用户', 0, 'com.star.pivot.monitor.controller.OnlineUserController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/v1/monitor/online', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 149, os=Windows 10, loginTime=2026-06-29T15:45:28.266, lastAccessTime=2026-06-29T15:45:28.266)], timestamp=1782720000075)', 0, '', '2026-06-29 16:00:00', 641);
INSERT INTO `sys_oper_log` VALUES (4761, '服务器监控', 0, 'com.star.pivot.monitor.controller.ServerMonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/v1/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":1.0957677443679894,\"used\":0.87307946083514,\"wait\":0.0,\"free\":97.86619851069847},\"memory\":{\"total\":24428,\"used\":8272,\"free\":16155,\"usage\":33.864426201747065},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1782719893204,\"runTime\":112194,\"home\":\"E:\\\\JEnv\\\\jdk\\\\jdk-17\",\"userDir\":\"E:\\\\star-pivot\\\\project0422\",\"inputArgs\":\"-Dfile.encoding=UTF8 -XX:TieredStopAtLevel=1 -Dspring.output.ansi.enabled=always -Dcom.sun.management.jmxremote -Dspring.jmx.enabled=true -Dspring.liveBeansView.mbeanDomain -Dspring.application.admin.enabled=true -Dmanagement.endpoints.jmx.exposure.include=* -javaagent:C:\\\\Program Files\\\\JetBrains\\\\IntelliJ IDEA 2025.3.3\\\\lib\\\\idea_rt.jar=50814 -Dfile.encoding=UTF-8\",\"max\":6108,\"total\":396,\"used\":256,\"free\":5851,\"usage\":4.2017165398238605},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.0.4\"},\"disk\":{\"total\":1429,\"used\":309,\"free\":1120,\"usage\":21.61361275467163,\"stores\":[{\"mount\":\"C:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (C:)\",\"totalGb\":476.03,\"usableGb\":267.58,\"usedGb\":208.45,\"usage\":43.79},{\"mount\":\"D:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (D:)\",\"totalGb\":390.62,\"usableGb\":321.73,\"usedGb\":68.9,\"usage\":17.64},{\"mount\":\"E:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (E:)\",\"totalGb\":195.2,\"usableGb\":172.85,\"usedGb\":22.35,\"usage\":11.45},{\"mount\":\"F:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (F:)\",\"totalGb\":367.93,\"usableGb\":358.59,\"usedGb\":9.33,\"usage\":2.54}]}},\"timestamp\":1782720005411}', 0, '', '2026-06-29 16:00:05', 124);
INSERT INTO `sys_oper_log` VALUES (4762, 'Druid监控', 0, 'com.star.pivot.monitor.controller.DruidMonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/v1/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-1900750749\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":1,\"minIdle\":1,\"maxActive\":20,\"activeCount\":0,\"activePeak\":3,\"usage\":0.0},\"sqlStat\":{\"executeCount\":136,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1782720006061}', 0, '', '2026-06-29 16:00:06', 69);
INSERT INTO `sys_oper_log` VALUES (4763, '在线用户', 0, 'com.star.pivot.monitor.controller.OnlineUserController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/v1/monitor/online', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 149, os=Windows 10, loginTime=2026-06-29T15:45:28.266, lastAccessTime=2026-06-29T15:45:28.266)], timestamp=1782720009276)', 0, '', '2026-06-29 16:00:09', 284);
INSERT INTO `sys_oper_log` VALUES (4764, '服务器监控', 0, 'com.star.pivot.monitor.controller.ServerMonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/v1/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.7289940828402367,\"used\":1.0905325443786982,\"wait\":0.0,\"free\":98.05029585798816},\"memory\":{\"total\":24428,\"used\":8108,\"free\":16319,\"usage\":33.19455422595335},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1782719893204,\"runTime\":122758,\"home\":\"E:\\\\JEnv\\\\jdk\\\\jdk-17\",\"userDir\":\"E:\\\\star-pivot\\\\project0422\",\"inputArgs\":\"-Dfile.encoding=UTF8 -XX:TieredStopAtLevel=1 -Dspring.output.ansi.enabled=always -Dcom.sun.management.jmxremote -Dspring.jmx.enabled=true -Dspring.liveBeansView.mbeanDomain -Dspring.application.admin.enabled=true -Dmanagement.endpoints.jmx.exposure.include=* -javaagent:C:\\\\Program Files\\\\JetBrains\\\\IntelliJ IDEA 2025.3.3\\\\lib\\\\idea_rt.jar=50814 -Dfile.encoding=UTF-8\",\"max\":6108,\"total\":396,\"used\":292,\"free\":5815,\"usage\":4.791107502495766},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.0.4\"},\"disk\":{\"total\":1429,\"used\":309,\"free\":1120,\"usage\":21.61364210306672,\"stores\":[{\"mount\":\"C:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (C:)\",\"totalGb\":476.03,\"usableGb\":267.58,\"usedGb\":208.45,\"usage\":43.79},{\"mount\":\"D:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (D:)\",\"totalGb\":390.62,\"usableGb\":321.73,\"usedGb\":68.9,\"usage\":17.64},{\"mount\":\"E:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (E:)\",\"totalGb\":195.2,\"usableGb\":172.85,\"usedGb\":22.35,\"usage\":11.45},{\"mount\":\"F:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (F:)\",\"totalGb\":367.93,\"usableGb\":358.59,\"usedGb\":9.33,\"usage\":2.54}]}},\"timestamp\":1782720015973}', 0, '', '2026-06-29 16:00:16', 133);
INSERT INTO `sys_oper_log` VALUES (4765, 'Druid监控', 0, 'com.star.pivot.monitor.controller.DruidMonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/v1/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-1900750749\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":1,\"minIdle\":1,\"maxActive\":20,\"activeCount\":1,\"activePeak\":3,\"usage\":5.0},\"sqlStat\":{\"executeCount\":174,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1782720016078}', 0, '', '2026-06-29 16:00:16', 61);
INSERT INTO `sys_oper_log` VALUES (4766, '在线用户', 0, 'com.star.pivot.monitor.controller.OnlineUserController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/v1/monitor/online', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 149, os=Windows 10, loginTime=2026-06-29T15:45:28.266, lastAccessTime=2026-06-29T15:45:28.266)], timestamp=1782720019258)', 0, '', '2026-06-29 16:00:19', 299);
INSERT INTO `sys_oper_log` VALUES (4767, 'Druid监控', 0, 'com.star.pivot.monitor.controller.DruidMonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/v1/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-1900750749\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":1,\"minIdle\":1,\"maxActive\":20,\"activeCount\":1,\"activePeak\":3,\"usage\":5.0},\"sqlStat\":{\"executeCount\":183,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1782720026100}', 0, '', '2026-06-29 16:00:26', 51);
INSERT INTO `sys_oper_log` VALUES (4768, '服务器监控', 0, 'com.star.pivot.monitor.controller.ServerMonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/v1/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.6314427636303211,\"used\":0.4177967157854756,\"wait\":0.0,\"free\":98.91396592345536},\"memory\":{\"total\":24428,\"used\":8099,\"free\":16328,\"usage\":33.156671943108456},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1782719893204,\"runTime\":133284,\"home\":\"E:\\\\JEnv\\\\jdk\\\\jdk-17\",\"userDir\":\"E:\\\\star-pivot\\\\project0422\",\"inputArgs\":\"-Dfile.encoding=UTF8 -XX:TieredStopAtLevel=1 -Dspring.output.ansi.enabled=always -Dcom.sun.management.jmxremote -Dspring.jmx.enabled=true -Dspring.liveBeansView.mbeanDomain -Dspring.application.admin.enabled=true -Dmanagement.endpoints.jmx.exposure.include=* -javaagent:C:\\\\Program Files\\\\JetBrains\\\\IntelliJ IDEA 2025.3.3\\\\lib\\\\idea_rt.jar=50814 -Dfile.encoding=UTF-8\",\"max\":6108,\"total\":396,\"used\":129,\"free\":5978,\"usage\":2.128112433233055},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.0.4\"},\"disk\":{\"total\":1429,\"used\":309,\"free\":1120,\"usage\":21.61272243108615,\"stores\":[{\"mount\":\"C:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (C:)\",\"totalGb\":476.03,\"usableGb\":267.59,\"usedGb\":208.43,\"usage\":43.79},{\"mount\":\"D:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (D:)\",\"totalGb\":390.62,\"usableGb\":321.73,\"usedGb\":68.9,\"usage\":17.64},{\"mount\":\"E:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (E:)\",\"totalGb\":195.2,\"usableGb\":172.85,\"usedGb\":22.35,\"usage\":11.45},{\"mount\":\"F:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (F:)\",\"totalGb\":367.93,\"usableGb\":358.59,\"usedGb\":9.33,\"usage\":2.54}]}},\"timestamp\":1782720026495}', 0, '', '2026-06-29 16:00:26', 102);
INSERT INTO `sys_oper_log` VALUES (4769, '在线用户', 0, 'com.star.pivot.monitor.controller.OnlineUserController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/v1/monitor/online', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 149, os=Windows 10, loginTime=2026-06-29T15:45:28.266, lastAccessTime=2026-06-29T15:45:28.266)], timestamp=1782720029283)', 0, '', '2026-06-29 16:00:29', 323);
INSERT INTO `sys_oper_log` VALUES (4770, 'Druid监控', 0, 'com.star.pivot.monitor.controller.DruidMonitorController.getDruidMonitorInfo()', 'GET', 1, 'admin', '星枢科技', '/api/v1/monitor/druid', '0:0:0:0:0:0:0:1', '', '[false,5000]', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"available\":true,\"message\":null,\"name\":\"DataSource-1900750749\",\"dbType\":\"mysql\",\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"connectionPool\":{\"initialSize\":1,\"minIdle\":1,\"maxActive\":20,\"activeCount\":0,\"activePeak\":3,\"usage\":0.0},\"sqlStat\":{\"executeCount\":194,\"executeMillisTotal\":0,\"executeMillisAvg\":0.0,\"slowSqlCount\":0,\"errorSqlCount\":0},\"slowSqlList\":null},\"timestamp\":1782720036086}', 0, '', '2026-06-29 16:00:36', 51);
INSERT INTO `sys_oper_log` VALUES (4771, '服务器监控', 0, 'com.star.pivot.monitor.controller.ServerMonitorController.getServerInfo()', 'GET', 1, 'admin', '星枢科技', '/api/v1/monitor/server', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"操作成功\",\"data\":{\"cpu\":{\"cpuNum\":16,\"total\":100.0,\"sys\":0.5961890888586062,\"used\":0.3941309149006156,\"wait\":0.0,\"free\":98.96386448005264},\"memory\":{\"total\":24428,\"used\":8176,\"free\":16251,\"usage\":33.47354628538552},\"jvm\":{\"name\":\"Java HotSpot(TM) 64-Bit Server VM\",\"version\":\"17.0.13+10-LTS-268\",\"startTime\":1782719893204,\"runTime\":143925,\"home\":\"E:\\\\JEnv\\\\jdk\\\\jdk-17\",\"userDir\":\"E:\\\\star-pivot\\\\project0422\",\"inputArgs\":\"-Dfile.encoding=UTF8 -XX:TieredStopAtLevel=1 -Dspring.output.ansi.enabled=always -Dcom.sun.management.jmxremote -Dspring.jmx.enabled=true -Dspring.liveBeansView.mbeanDomain -Dspring.application.admin.enabled=true -Dmanagement.endpoints.jmx.exposure.include=* -javaagent:C:\\\\Program Files\\\\JetBrains\\\\IntelliJ IDEA 2025.3.3\\\\lib\\\\idea_rt.jar=50814 -Dfile.encoding=UTF-8\",\"max\":6108,\"total\":396,\"used\":138,\"free\":5969,\"usage\":2.2660302272714192},\"system\":{\"computerName\":\"DESKTOP-6EB70D6\",\"osName\":\"Windows 11\",\"osArch\":\"amd64\",\"computerIp\":\"192.168.0.4\"},\"disk\":{\"total\":1429,\"used\":309,\"free\":1120,\"usage\":21.612722697889744,\"stores\":[{\"mount\":\"C:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (C:)\",\"totalGb\":476.03,\"usableGb\":267.59,\"usedGb\":208.43,\"usage\":43.79},{\"mount\":\"D:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (D:)\",\"totalGb\":390.62,\"usableGb\":321.73,\"usedGb\":68.9,\"usage\":17.64},{\"mount\":\"E:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (E:)\",\"totalGb\":195.2,\"usableGb\":172.85,\"usedGb\":22.35,\"usage\":11.45},{\"mount\":\"F:\\\\\",\"fileSystem\":\"NTFS\",\"typeName\":\"本地固定磁盘 (F:)\",\"totalGb\":367.93,\"usableGb\":358.59,\"usedGb\":9.33,\"usage\":2.54}]}},\"timestamp\":1782720037140}', 0, '', '2026-06-29 16:00:37', 142);
INSERT INTO `sys_oper_log` VALUES (4772, '在线用户', 0, 'com.star.pivot.monitor.controller.OnlineUserController.getOnlineUserList()', 'GET', 1, 'admin', '星枢科技', '/api/v1/monitor/online', '0:0:0:0:0:0:0:1', '', '', 'Result(code=200, message=操作成功, data=[OnlineUserVO(sessionId=jwt:refresh:user:1, userId=1, userName=admin, nickName=超级管理员, deptName=星枢科技, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 149, os=Windows 10, loginTime=2026-06-29T15:45:28.266, lastAccessTime=2026-06-29T15:45:28.266)], timestamp=1782720039225)', 0, '', '2026-06-29 16:00:39', 260);
INSERT INTO `sys_oper_log` VALUES (4773, '用户登出', 0, 'com.star.pivot.controller.auth.AuthTokenController.logout()', 'POST', 1, 'admin', '星枢科技', '/api/v1/auth/logout', '0:0:0:0:0:0:0:1', '', '\"Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc4MjcxOTEyOCwiZXhwIjoxNzgyODA1NTI4fQ.wBJ6zqx4y8CxTpmgeYyI6JUdssZ5JV0oLGXlx1i6VCc\"', '{\"code\":200,\"message\":\"登出成功\",\"data\":null,\"timestamp\":1782720644576}', 0, '', '2026-06-29 16:10:45', 684);
INSERT INTO `sys_oper_log` VALUES (4774, '用户登录', 0, 'com.star.pivot.controller.auth.AuthAccountController.login()', 'POST', 0, 'admin', '', '/api/v1/auth/login', '0:0:0:0:0:0:0:1', '', '{\"username\":\"admin\",\"password\":\"******\",\"captchaProof\":\"cVWiiWOHybmoU6EWl9enGpX8fiIsxLHb52AXLEYTEdw\"}', '{\"code\":200,\"message\":\"登录成功\",\"data\":{\"token\":\"eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc4MjcyMDY1MiwiZXhwIjoxNzgyODA3MDUyfQ.w-JS8k_rhVuA8lMoEgSZ4xukQBmParQS5SUAtBw0qTU\",\"username\":\"admin\",\"nickname\":\"超级管理员\",\"refreshToken\":\"4cd27873df174c4ca90b5196a246215b\"},\"timestamp\":1782720652844}', 0, '', '2026-06-29 16:10:53', 1647);

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `post_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int(0) NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`) USING BTREE,
  UNIQUE INDEX `uk_post_code`(`post_code`) USING BTREE,
  INDEX `idx_post_status`(`status`) USING BTREE,
  INDEX `idx_post_sort`(`post_sort`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '岗位信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES (1, 'CEO', '董事长', 1, '0', 'admin', '2025-12-28 13:46:34', 'admin', '2026-06-02 13:42:38', '');
INSERT INTO `sys_post` VALUES (2, 'SE', '项目经理', 2, '0', 'admin', '2025-12-28 13:46:34', 'admin', '2026-06-02 13:42:43', '');
INSERT INTO `sys_post` VALUES (3, 'HR', '人力资源', 3, '0', 'admin', '2025-12-28 13:46:34', 'admin', '2026-06-02 13:42:48', '');
INSERT INTO `sys_post` VALUES (4, 'USER', '普通员工', 4, '0', 'admin', '2025-12-28 13:46:34', 'admin', '2026-06-02 13:42:54', '');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(0) NOT NULL COMMENT '显示顺序',
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
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE INDEX `uk_role_key`(`role_key`) USING BTREE,
  INDEX `idx_del_flag`(`del_flag`) USING BTREE,
  INDEX `idx_role_sort`(`role_sort`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_del_status`(`del_flag`, `status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_role_status_del`(`status`, `del_flag`) USING BTREE,
  INDEX `idx_role_key`(`role_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2025-12-28 13:46:34', 'admin', '2026-01-23 19:59:34', '超级管理员：拥有所有菜单权限');
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', '2025-12-28 13:46:34', 'admin', '2026-06-29 15:22:16', '普通角色');
INSERT INTO `sys_role` VALUES (3, '测试专属', 'test', 3, '2', 1, 1, '0', '0', '', '2026-01-03 17:06:10', 'admin', '2026-06-09 15:12:02', '专属于测试的角色');
INSERT INTO `sys_role` VALUES (4, '演示专属', 'yanshi', 4, '4', 0, 1, '0', '0', 'admin', '2026-02-09 12:25:59', 'admin', '2026-06-29 15:22:30', '演示专属角色');
INSERT INTO `sys_role` VALUES (5, '注册用户', 'register_role', 5, '5', 0, 1, '0', '0', 'admin', '2026-02-12 16:57:43', 'admin', '2026-06-29 15:22:40', '注册时，新用户初始化角色');

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(0) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_role_dept`(`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 170 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色与部门关联表' ROW_FORMAT = Dynamic;

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
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(0) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_role_menu`(`role_id`, `menu_id`) USING BTREE,
  UNIQUE INDEX `uk_role_menu`(`role_id`, `menu_id`) USING BTREE,
  UNIQUE INDEX `idx_role_menu_unique`(`role_id`, `menu_id`) USING BTREE,
  INDEX `idx_role_menu_cover`(`role_id`, `menu_id`) USING BTREE,
  INDEX `idx_menu_id`(`menu_id`) USING BTREE,
  INDEX `idx_role_menu_role`(`role_id`) USING BTREE,
  INDEX `idx_role_menu_menu`(`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1027 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色与菜单关联表' ROW_FORMAT = Dynamic;

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
INSERT INTO `sys_role_menu` VALUES (991, 1, 400);
INSERT INTO `sys_role_menu` VALUES (992, 1, 401);
INSERT INTO `sys_role_menu` VALUES (993, 1, 402);
INSERT INTO `sys_role_menu` VALUES (994, 1, 403);
INSERT INTO `sys_role_menu` VALUES (995, 1, 410);
INSERT INTO `sys_role_menu` VALUES (996, 1, 411);
INSERT INTO `sys_role_menu` VALUES (997, 1, 412);
INSERT INTO `sys_role_menu` VALUES (998, 1, 413);
INSERT INTO `sys_role_menu` VALUES (999, 1, 414);
INSERT INTO `sys_role_menu` VALUES (1000, 1, 415);
INSERT INTO `sys_role_menu` VALUES (1001, 1, 416);
INSERT INTO `sys_role_menu` VALUES (1002, 1, 417);
INSERT INTO `sys_role_menu` VALUES (1003, 1, 420);
INSERT INTO `sys_role_menu` VALUES (1004, 1, 421);
INSERT INTO `sys_role_menu` VALUES (1005, 1, 422);
INSERT INTO `sys_role_menu` VALUES (1006, 1, 423);
INSERT INTO `sys_role_menu` VALUES (1007, 1, 424);
INSERT INTO `sys_role_menu` VALUES (1008, 1, 425);
INSERT INTO `sys_role_menu` VALUES (1009, 1, 426);
INSERT INTO `sys_role_menu` VALUES (1010, 1, 427);
INSERT INTO `sys_role_menu` VALUES (1011, 1, 430);
INSERT INTO `sys_role_menu` VALUES (1012, 1, 431);
INSERT INTO `sys_role_menu` VALUES (1013, 1, 432);
INSERT INTO `sys_role_menu` VALUES (1014, 1, 433);
INSERT INTO `sys_role_menu` VALUES (1015, 1, 434);
INSERT INTO `sys_role_menu` VALUES (1016, 1, 435);
INSERT INTO `sys_role_menu` VALUES (1017, 1, 440);
INSERT INTO `sys_role_menu` VALUES (1018, 1, 441);
INSERT INTO `sys_role_menu` VALUES (1019, 1, 442);
INSERT INTO `sys_role_menu` VALUES (1020, 1, 443);
INSERT INTO `sys_role_menu` VALUES (1021, 1, 444);
INSERT INTO `sys_role_menu` VALUES (1022, 1, 450);
INSERT INTO `sys_role_menu` VALUES (1023, 1, 451);
INSERT INTO `sys_role_menu` VALUES (1024, 1, 452);
INSERT INTO `sys_role_menu` VALUES (1025, 1, 453);
INSERT INTO `sys_role_menu` VALUES (1026, 1, 460);
INSERT INTO `sys_role_menu` VALUES (1027, 2, 1);
INSERT INTO `sys_role_menu` VALUES (1091, 2, 5);
INSERT INTO `sys_role_menu` VALUES (1092, 2, 6);
INSERT INTO `sys_role_menu` VALUES (1028, 2, 7);
INSERT INTO `sys_role_menu` VALUES (1033, 2, 8);
INSERT INTO `sys_role_menu` VALUES (1043, 2, 9);
INSERT INTO `sys_role_menu` VALUES (1053, 2, 10);
INSERT INTO `sys_role_menu` VALUES (1058, 2, 11);
INSERT INTO `sys_role_menu` VALUES (1063, 2, 12);
INSERT INTO `sys_role_menu` VALUES (1072, 2, 13);
INSERT INTO `sys_role_menu` VALUES (1079, 2, 14);
INSERT INTO `sys_role_menu` VALUES (1085, 2, 15);
INSERT INTO `sys_role_menu` VALUES (1073, 2, 16);
INSERT INTO `sys_role_menu` VALUES (1076, 2, 17);
INSERT INTO `sys_role_menu` VALUES (1029, 2, 32);
INSERT INTO `sys_role_menu` VALUES (1030, 2, 33);
INSERT INTO `sys_role_menu` VALUES (1031, 2, 34);
INSERT INTO `sys_role_menu` VALUES (1032, 2, 35);
INSERT INTO `sys_role_menu` VALUES (1034, 2, 36);
INSERT INTO `sys_role_menu` VALUES (1035, 2, 37);
INSERT INTO `sys_role_menu` VALUES (1036, 2, 38);
INSERT INTO `sys_role_menu` VALUES (1037, 2, 39);
INSERT INTO `sys_role_menu` VALUES (1038, 2, 40);
INSERT INTO `sys_role_menu` VALUES (1039, 2, 41);
INSERT INTO `sys_role_menu` VALUES (1040, 2, 42);
INSERT INTO `sys_role_menu` VALUES (1041, 2, 43);
INSERT INTO `sys_role_menu` VALUES (1042, 2, 44);
INSERT INTO `sys_role_menu` VALUES (1044, 2, 45);
INSERT INTO `sys_role_menu` VALUES (1045, 2, 46);
INSERT INTO `sys_role_menu` VALUES (1046, 2, 47);
INSERT INTO `sys_role_menu` VALUES (1047, 2, 48);
INSERT INTO `sys_role_menu` VALUES (1048, 2, 49);
INSERT INTO `sys_role_menu` VALUES (1049, 2, 50);
INSERT INTO `sys_role_menu` VALUES (1050, 2, 51);
INSERT INTO `sys_role_menu` VALUES (1051, 2, 52);
INSERT INTO `sys_role_menu` VALUES (1052, 2, 53);
INSERT INTO `sys_role_menu` VALUES (1054, 2, 54);
INSERT INTO `sys_role_menu` VALUES (1055, 2, 55);
INSERT INTO `sys_role_menu` VALUES (1056, 2, 56);
INSERT INTO `sys_role_menu` VALUES (1057, 2, 57);
INSERT INTO `sys_role_menu` VALUES (1059, 2, 58);
INSERT INTO `sys_role_menu` VALUES (1060, 2, 59);
INSERT INTO `sys_role_menu` VALUES (1061, 2, 60);
INSERT INTO `sys_role_menu` VALUES (1062, 2, 61);
INSERT INTO `sys_role_menu` VALUES (1064, 2, 62);
INSERT INTO `sys_role_menu` VALUES (1065, 2, 63);
INSERT INTO `sys_role_menu` VALUES (1066, 2, 64);
INSERT INTO `sys_role_menu` VALUES (1067, 2, 65);
INSERT INTO `sys_role_menu` VALUES (1068, 2, 66);
INSERT INTO `sys_role_menu` VALUES (1069, 2, 67);
INSERT INTO `sys_role_menu` VALUES (1070, 2, 68);
INSERT INTO `sys_role_menu` VALUES (1071, 2, 69);
INSERT INTO `sys_role_menu` VALUES (1074, 2, 70);
INSERT INTO `sys_role_menu` VALUES (1075, 2, 71);
INSERT INTO `sys_role_menu` VALUES (1077, 2, 72);
INSERT INTO `sys_role_menu` VALUES (1078, 2, 73);
INSERT INTO `sys_role_menu` VALUES (1080, 2, 74);
INSERT INTO `sys_role_menu` VALUES (1081, 2, 75);
INSERT INTO `sys_role_menu` VALUES (1082, 2, 76);
INSERT INTO `sys_role_menu` VALUES (1083, 2, 77);
INSERT INTO `sys_role_menu` VALUES (1084, 2, 78);
INSERT INTO `sys_role_menu` VALUES (1086, 2, 79);
INSERT INTO `sys_role_menu` VALUES (1087, 2, 80);
INSERT INTO `sys_role_menu` VALUES (1088, 2, 81);
INSERT INTO `sys_role_menu` VALUES (1089, 2, 82);
INSERT INTO `sys_role_menu` VALUES (1090, 2, 83);
INSERT INTO `sys_role_menu` VALUES (989, 3, 1);
INSERT INTO `sys_role_menu` VALUES (987, 3, 5);
INSERT INTO `sys_role_menu` VALUES (988, 3, 6);
INSERT INTO `sys_role_menu` VALUES (990, 3, 8);
INSERT INTO `sys_role_menu` VALUES (934, 3, 9);
INSERT INTO `sys_role_menu` VALUES (952, 3, 12);
INSERT INTO `sys_role_menu` VALUES (965, 3, 14);
INSERT INTO `sys_role_menu` VALUES (933, 3, 39);
INSERT INTO `sys_role_menu` VALUES (935, 3, 45);
INSERT INTO `sys_role_menu` VALUES (936, 3, 46);
INSERT INTO `sys_role_menu` VALUES (937, 3, 47);
INSERT INTO `sys_role_menu` VALUES (938, 3, 48);
INSERT INTO `sys_role_menu` VALUES (939, 3, 49);
INSERT INTO `sys_role_menu` VALUES (940, 3, 50);
INSERT INTO `sys_role_menu` VALUES (941, 3, 51);
INSERT INTO `sys_role_menu` VALUES (942, 3, 52);
INSERT INTO `sys_role_menu` VALUES (943, 3, 53);
INSERT INTO `sys_role_menu` VALUES (944, 3, 54);
INSERT INTO `sys_role_menu` VALUES (945, 3, 55);
INSERT INTO `sys_role_menu` VALUES (946, 3, 56);
INSERT INTO `sys_role_menu` VALUES (947, 3, 57);
INSERT INTO `sys_role_menu` VALUES (948, 3, 58);
INSERT INTO `sys_role_menu` VALUES (949, 3, 59);
INSERT INTO `sys_role_menu` VALUES (950, 3, 60);
INSERT INTO `sys_role_menu` VALUES (951, 3, 61);
INSERT INTO `sys_role_menu` VALUES (953, 3, 62);
INSERT INTO `sys_role_menu` VALUES (954, 3, 63);
INSERT INTO `sys_role_menu` VALUES (955, 3, 64);
INSERT INTO `sys_role_menu` VALUES (956, 3, 65);
INSERT INTO `sys_role_menu` VALUES (957, 3, 66);
INSERT INTO `sys_role_menu` VALUES (958, 3, 67);
INSERT INTO `sys_role_menu` VALUES (959, 3, 68);
INSERT INTO `sys_role_menu` VALUES (960, 3, 69);
INSERT INTO `sys_role_menu` VALUES (961, 3, 70);
INSERT INTO `sys_role_menu` VALUES (962, 3, 71);
INSERT INTO `sys_role_menu` VALUES (963, 3, 72);
INSERT INTO `sys_role_menu` VALUES (964, 3, 73);
INSERT INTO `sys_role_menu` VALUES (966, 3, 74);
INSERT INTO `sys_role_menu` VALUES (967, 3, 75);
INSERT INTO `sys_role_menu` VALUES (968, 3, 76);
INSERT INTO `sys_role_menu` VALUES (969, 3, 77);
INSERT INTO `sys_role_menu` VALUES (970, 3, 78);
INSERT INTO `sys_role_menu` VALUES (971, 3, 84);
INSERT INTO `sys_role_menu` VALUES (972, 3, 85);
INSERT INTO `sys_role_menu` VALUES (973, 3, 86);
INSERT INTO `sys_role_menu` VALUES (974, 3, 87);
INSERT INTO `sys_role_menu` VALUES (975, 3, 88);
INSERT INTO `sys_role_menu` VALUES (976, 3, 89);
INSERT INTO `sys_role_menu` VALUES (977, 3, 90);
INSERT INTO `sys_role_menu` VALUES (978, 3, 91);
INSERT INTO `sys_role_menu` VALUES (982, 3, 92);
INSERT INTO `sys_role_menu` VALUES (979, 3, 93);
INSERT INTO `sys_role_menu` VALUES (980, 3, 94);
INSERT INTO `sys_role_menu` VALUES (981, 3, 95);
INSERT INTO `sys_role_menu` VALUES (983, 3, 96);
INSERT INTO `sys_role_menu` VALUES (984, 3, 97);
INSERT INTO `sys_role_menu` VALUES (985, 3, 98);
INSERT INTO `sys_role_menu` VALUES (986, 3, 99);
INSERT INTO `sys_role_menu` VALUES (1093, 4, 1);
INSERT INTO `sys_role_menu` VALUES (1104, 4, 5);
INSERT INTO `sys_role_menu` VALUES (1105, 4, 6);
INSERT INTO `sys_role_menu` VALUES (1094, 4, 8);
INSERT INTO `sys_role_menu` VALUES (1095, 4, 9);
INSERT INTO `sys_role_menu` VALUES (1101, 4, 12);
INSERT INTO `sys_role_menu` VALUES (1096, 4, 45);
INSERT INTO `sys_role_menu` VALUES (1097, 4, 46);
INSERT INTO `sys_role_menu` VALUES (1098, 4, 47);
INSERT INTO `sys_role_menu` VALUES (1099, 4, 51);
INSERT INTO `sys_role_menu` VALUES (1100, 4, 55);
INSERT INTO `sys_role_menu` VALUES (1102, 4, 69);
INSERT INTO `sys_role_menu` VALUES (1103, 4, 70);
INSERT INTO `sys_role_menu` VALUES (1106, 5, 1);
INSERT INTO `sys_role_menu` VALUES (1126, 5, 5);
INSERT INTO `sys_role_menu` VALUES (1127, 5, 6);
INSERT INTO `sys_role_menu` VALUES (1107, 5, 8);
INSERT INTO `sys_role_menu` VALUES (1108, 5, 9);
INSERT INTO `sys_role_menu` VALUES (1114, 5, 12);
INSERT INTO `sys_role_menu` VALUES (1109, 5, 45);
INSERT INTO `sys_role_menu` VALUES (1110, 5, 46);
INSERT INTO `sys_role_menu` VALUES (1111, 5, 47);
INSERT INTO `sys_role_menu` VALUES (1112, 5, 51);
INSERT INTO `sys_role_menu` VALUES (1113, 5, 55);
INSERT INTO `sys_role_menu` VALUES (1115, 5, 69);
INSERT INTO `sys_role_menu` VALUES (1116, 5, 70);
INSERT INTO `sys_role_menu` VALUES (1117, 5, 71);
INSERT INTO `sys_role_menu` VALUES (1118, 5, 72);
INSERT INTO `sys_role_menu` VALUES (1119, 5, 73);
INSERT INTO `sys_role_menu` VALUES (1120, 5, 81);
INSERT INTO `sys_role_menu` VALUES (1121, 5, 85);
INSERT INTO `sys_role_menu` VALUES (1125, 5, 92);
INSERT INTO `sys_role_menu` VALUES (1122, 5, 151);
INSERT INTO `sys_role_menu` VALUES (1123, 5, 152);
INSERT INTO `sys_role_menu` VALUES (1124, 5, 153);
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
  `user_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(0) NULL DEFAULT NULL COMMENT '部门ID',
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
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE,
  UNIQUE INDEX `idx_user_username_del`(`user_name`, `del_flag`) USING BTREE,
  INDEX `idx_username`(`user_name`) USING BTREE,
  INDEX `idx_phone`(`phonenumber`) USING BTREE,
  INDEX `idx_email`(`email`) USING BTREE,
  INDEX `idx_dept_id`(`dept_id`) USING BTREE,
  INDEX `idx_del_flag_status`(`del_flag`, `status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_dept_del_status`(`dept_id`, `del_flag`, `status`) USING BTREE,
  INDEX `idx_phonenumber`(`phonenumber`) USING BTREE,
  INDEX `idx_user_name`(`user_name`) USING BTREE,
  INDEX `idx_status_delflag`(`status`, `del_flag`) USING BTREE,
  INDEX `idx_user_status_del`(`status`, `del_flag`) USING BTREE,
  INDEX `idx_user_create_time`(`create_time`) USING BTREE,
  INDEX `idx_user_dept_status`(`dept_id`, `status`, `del_flag`) USING BTREE,
  INDEX `idx_user_nickname`(`nick_name`) USING BTREE,
  INDEX `idx_user_phone`(`phonenumber`) USING BTREE,
  INDEX `idx_user_email`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 120 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 100, 'admin', '超级管理员', '00', 'admin@163.com', '18888888888', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/1.jpeg?Expires=1777366580&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=e9LJe8LWoaBeTo7j8crvIvyw2Ng%3D', '$2a$10$iW4S/4T9dWK6dqJMqZ7Zq.4xALEez.SiUD1zNY9dEKXvVVcHDYqRq', '0', '0', '0:0:0:0:0:0:0:1', '2025-12-29 22:16:38', '2026-04-22 09:22:59', 'admin', '2025-12-28 13:46:34', 'admin', '2026-04-22 09:22:59', '超级管理员');
INSERT INTO `sys_user` VALUES (2, 105, 'user', '用户管理员', '00', 'user@qq.com', '15666666666', '1', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/2.webp?Expires=1771492975&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=crgqBsWiZLm6O%2BvLufm%2BJF4wYd4%3D', '$2a$12$eYocwR0zs3iWKl7gsvHVQ.KLDTWvXqecm.29aXPi3IAF.mmkARVR.', '0', '0', '0:0:0:0:0:0:0:1', '2025-12-29 22:08:54', '2025-12-28 13:46:34', 'admin', '2025-12-28 13:46:34', 'admin', '2026-02-12 17:22:56', '测试员--用户管理员');
INSERT INTO `sys_user` VALUES (100, 100, 'xinxin', '超级管理员', '00', '123@qq.com', '18888888888', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/100.webp?Expires=1781597876&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=wK514o5HBhzWoRwGiPHGYOwd6f8%3D', '$2a$10$eFW.co7zyXl//sJwjqxFHuL.sx4vn2AaX2LJmIjF/KMTvh841WJry', '0', '0', '', NULL, '2026-06-09 19:00:12', 'admin', '2026-01-04 15:34:36', 'xinxin', '2026-06-09 19:00:12', '超级管理员');
INSERT INTO `sys_user` VALUES (101, 105, 'test', '测试用户', '00', '123@qq.com', '18825454547', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/101.webp?Expires=1771492959&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=EIhrjRWOnchJpD38pJ1gVvqsyI8%3D', '$2a$10$YdqAWweActfkWOaWEjz9p.bBWqNWVdT9EQ2OHcUODgFg.f3ma13Va', '0', '0', '', NULL, NULL, 'admin', '2026-01-04 16:50:12', 'admin', '2026-02-12 17:22:41', '测试用户专属');
INSERT INTO `sys_user` VALUES (102, 107, 'zhangsan', '张三', '00', 'zhangsan@qq.com', '18812345678', '1', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/102.webp?Expires=1777551232&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=f0Yd82ApcKjbABazWGeXW3g0sxM%3D', '$2a$10$cmueflWIca3U8waJO3gSv.HAEhTFvygR7lGdMto630Nbs2GcXXTpy', '0', '0', '', NULL, NULL, 'admin', '2026-01-14 18:04:51', 'admin', '2026-04-23 20:14:00', '张三-普通员工');
INSERT INTO `sys_user` VALUES (112, 100, 'lisi', '李四', '00', 'lisi@163.com', '18855555555', '1', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/112.webp?Expires=1771492951&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=GFErrGwlGIJMYzT5mSpYQfa4k5k%3D', '$2a$10$2eglfjL.MCwsuprXJfWIRezp.4/nV3hpuOOgtRHfG9d8kyCfVDsB.', '0', '0', '', NULL, NULL, 'admin', '2026-01-24 21:56:29', 'admin', '2026-02-12 17:22:33', '');
INSERT INTO `sys_user` VALUES (113, 111, 'wangwu', 'wangwu', '00', 'wangwu@163.com', '13326541578', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/113.webp?Expires=1771492946&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=5DCWthMgcQ8Zl8QqJDJ2upy%2FYzM%3D', '$2a$10$XOXffZph7qNkosKfc/gPHOhh1NsQsVB9lzD4VNf6Rz5cYn.6ksHvG', '0', '0', '', NULL, NULL, 'wangwu', '2026-01-27 18:35:41', 'admin', '2026-02-12 17:22:28', '111');
INSERT INTO `sys_user` VALUES (114, 110, 'starPivot', 'starPivot演示用户', '00', 'starPivot@163.com', '13388888888', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/114.webp?Expires=1771219638&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=jS6ZhM4CsHr5DThRHmRdQew%2FlIk%3D', '$2a$10$7v0PWRiJVzWOFSDuFkCaXeiAF3xK0qgKxwPzOfSd47JC0LH4juJ4W', '0', '2', '', NULL, NULL, 'admin', '2026-02-09 12:22:01', 'admin', '2026-05-29 20:41:59', '111');
INSERT INTO `sys_user` VALUES (115, 111, 'xiaoming', 'xiaoming', '00', 'xiaoming@163.com', '15588888888', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/115.webp?Expires=1773029429&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=pjfxhofXW%2B4Gam375n7ltctzuvI%3D', '$2a$10$WJeRMCrDzSJbVYA.dD0eOOqLpB0yLUqIjNqFRWehtkVEmaszZ3jPK', '0', '2', '', NULL, NULL, 'xiaoming', '2026-02-09 18:19:02', 'admin', '2026-05-29 20:41:55', '');
INSERT INTO `sys_user` VALUES (116, 100, '15536155268', '15536155268', '00', '15536155268@qq.com', '15536155268', '1', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/116.webp?Expires=1777602106&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=oWRRCXZXg9vuzgFCELxisTUqNJQ%3D', '$2a$10$Mq4GvD9V9vmTCmJrz514mexChhyJJmXRUz22N0gQlg5mXc93YSyku', '0', '0', '', NULL, NULL, '15536155268', '2026-04-24 10:20:41', 'admin', '2026-05-18 22:45:12', '');
INSERT INTO `sys_user` VALUES (117, 110, 'libai', '李白', '00', 'libai@163.com', '19221459706', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/117.webp?Expires=1780663267&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=DQw%2BDhrL2NitmGeQQFnfX%2Bvwd0Y%3D', '$2a$10$r5ScoiMsiF.9ylUwAgyG8eEu2wQtWmijdDNp1avi28F8iDAZpxPFy', '0', '0', '', NULL, NULL, 'admin', '2026-05-29 20:33:13', 'admin', '2026-05-29 20:41:21', '');
INSERT INTO `sys_user` VALUES (118, 101, 'yasuo', '亚索', '00', '123@163.com', '13388888888', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/118.webp?Expires=1780665121&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=fumWol9kMrlykRMD6LRER5pjq9o%3D', '$2a$10$.9zbBTA0nSvTc4/gD3A8h.U0Z9gRspvslE1uGkQDaNodZAwsRzWme', '0', '0', '', NULL, NULL, 'admin', '2026-05-29 20:44:01', 'admin', '2026-05-29 21:12:02', 'sdfsdf');
INSERT INTO `sys_user` VALUES (119, 101, 'Tom', '汤姆', '00', '', '', '0', 'http://star-pivot.oss-cn-beijing.aliyuncs.com/avatar/119.webp?Expires=1780978250&OSSAccessKeyId=LTAI5tG8uSJeTeuRYEY4x3nG&Signature=GOPknJNZNOzHD1KT6FOvhDCRutA%3D', '$2a$10$ia7xT5R1BeUARvciBfoBJuAeG5o1lWu2KwZqfDgTIQmBBQF/.ZG4a', '0', '0', '', NULL, NULL, 'admin', '2026-05-29 20:53:32', 'admin', '2026-06-02 12:10:53', '');

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `post_id` bigint(0) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_post`(`user_id`, `post_id`) USING BTREE,
  UNIQUE INDEX `uk_user_post`(`user_id`, `post_id`) USING BTREE,
  INDEX `idx_post_id`(`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 212 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES (199, 1, 1);
INSERT INTO `sys_user_post` VALUES (195, 2, 2);
INSERT INTO `sys_user_post` VALUES (211, 100, 1);
INSERT INTO `sys_user_post` VALUES (201, 102, 4);
INSERT INTO `sys_user_post` VALUES (192, 113, 4);
INSERT INTO `sys_user_post` VALUES (190, 114, 4);
INSERT INTO `sys_user_post` VALUES (197, 115, 4);
INSERT INTO `sys_user_post` VALUES (202, 116, 3);
INSERT INTO `sys_user_post` VALUES (204, 117, 4);
INSERT INTO `sys_user_post` VALUES (208, 118, 4);
INSERT INTO `sys_user_post` VALUES (210, 119, 4);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_role`(`user_id`, `role_id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id`, `role_id`) USING BTREE,
  UNIQUE INDEX `idx_user_role_unique`(`user_id`, `role_id`) USING BTREE,
  INDEX `idx_user_role_cover`(`user_id`, `role_id`) USING BTREE,
  INDEX `idx_role_id`(`role_id`) USING BTREE,
  INDEX `idx_user_role_user`(`user_id`) USING BTREE,
  INDEX `idx_user_role_role`(`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 232 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户与角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (219, 1, 1);
INSERT INTO `sys_user_role` VALUES (211, 2, 2);
INSERT INTO `sys_user_role` VALUES (231, 100, 1);
INSERT INTO `sys_user_role` VALUES (215, 114, 4);
INSERT INTO `sys_user_role` VALUES (216, 115, 3);
INSERT INTO `sys_user_role` VALUES (217, 115, 5);
INSERT INTO `sys_user_role` VALUES (222, 116, 1);
INSERT INTO `sys_user_role` VALUES (224, 117, 2);
INSERT INTO `sys_user_role` VALUES (228, 118, 2);
INSERT INTO `sys_user_role` VALUES (230, 119, 2);

SET FOREIGN_KEY_CHECKS = 1;
