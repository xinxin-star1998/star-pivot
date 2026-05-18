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

 Date: 18/05/2026 20:49:25
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
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代码生成业务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table
-- ----------------------------
INSERT INTO `gen_table` VALUES (2, 'pms_attr', '商品属性', NULL, NULL, 'PmsAttr', 'crud', 'art-design-pro', 'com.star.pivot.mall', 'mall', 'attr', '商品属性', 'admin', '0', '/', '{\"treeCode\":\"\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuId\":158}', 'admin', '2026-05-15 15:51:31', '', '2026-05-18 15:22:18', '');
INSERT INTO `gen_table` VALUES (3, 'pms_attr_attrgroup_relation', '属性&属性分组关联', NULL, NULL, 'PmsAttrAttrgroupRelation', 'crud', '', 'com.star.pivot.system', 'system', 'relation', '属性&属性分组关联', 'admin', '0', '/', NULL, 'admin', '2026-05-15 15:51:31', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (4, 'pms_attr_group', '属性分组', NULL, NULL, 'PmsAttrGroup', 'crud', 'art-design-pro', 'com.star.pivot.mall', 'mall', 'group', '属性分组', 'admin', '0', '/', '{\"treeCode\":\"\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuId\":157}', 'admin', '2026-05-15 15:51:31', '', '2026-05-18 15:23:09', '');
INSERT INTO `gen_table` VALUES (5, 'pms_brand', '品牌', NULL, NULL, 'PmsBrand', 'crud', '', 'com.star.pivot.system', 'system', 'brand', '品牌', 'admin', '0', '/', NULL, 'admin', '2026-05-15 15:51:31', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (6, 'pms_category', '商品三级分类', NULL, NULL, 'PmsCategory', 'crud', '', 'com.star.pivot.system', 'system', 'category', '商品三级分类', 'admin', '0', '/', NULL, 'admin', '2026-05-15 15:51:31', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (7, 'pms_category_brand_relation', '品牌分类关联', NULL, NULL, 'PmsCategoryBrandRelation', 'crud', '', 'com.star.pivot.system', 'system', 'relation', '品牌分类关联', 'admin', '0', '/', NULL, 'admin', '2026-05-15 15:51:31', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (8, 'pms_comment_replay', '商品评价回复关系', NULL, NULL, 'PmsCommentReplay', 'crud', '', 'com.star.pivot.system', 'system', 'replay', '商品评价回复关系', 'admin', '0', '/', NULL, 'admin', '2026-05-15 15:51:31', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (9, 'pms_product_attr_value', 'spu属性值', NULL, NULL, 'PmsProductAttrValue', 'crud', '', 'com.star.pivot.system', 'system', 'value', 'spu属性值', 'admin', '0', '/', NULL, 'admin', '2026-05-15 15:51:31', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (10, 'pms_sku_images', 'sku图片', NULL, NULL, 'PmsSkuImages', 'crud', '', 'com.star.pivot.system', 'system', 'images', 'sku图片', 'admin', '0', '/', NULL, 'admin', '2026-05-15 15:51:31', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (11, 'pms_sku_info', 'sku信息', NULL, NULL, 'PmsSkuInfo', 'crud', '', 'com.star.pivot.system', 'system', 'info', 'sku信息', 'admin', '0', '/', NULL, 'admin', '2026-05-15 15:51:31', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (12, 'pms_sku_sale_attr_value', 'sku销售属性&值', NULL, NULL, 'PmsSkuSaleAttrValue', 'crud', '', 'com.star.pivot.system', 'system', 'value', 'sku销售属性&值', 'admin', '0', '/', NULL, 'admin', '2026-05-15 15:51:31', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (13, 'pms_spu_comment', '商品评价', NULL, NULL, 'PmsSpuComment', 'crud', '', 'com.star.pivot.system', 'system', 'comment', '商品评价', 'admin', '0', '/', NULL, 'admin', '2026-05-15 15:51:31', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (14, 'pms_spu_images', 'spu图片', NULL, NULL, 'PmsSpuImages', 'crud', '', 'com.star.pivot.system', 'system', 'images', 'spu图片', 'admin', '0', '/', NULL, 'admin', '2026-05-15 15:51:31', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (15, 'pms_spu_info', 'spu信息', NULL, NULL, 'PmsSpuInfo', 'crud', '', 'com.star.pivot.system', 'system', 'info', 'spu信息', 'admin', '0', '/', NULL, 'admin', '2026-05-15 15:51:31', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (16, 'pms_spu_info_desc', 'spu信息介绍', NULL, NULL, 'PmsSpuInfoDesc', 'crud', '', 'com.star.pivot.system', 'system', 'desc', 'spu信息介绍', 'admin', '0', '/', NULL, 'admin', '2026-05-15 15:51:31', '', NULL, NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 117 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------
INSERT INTO `gen_table_column` VALUES (11, 2, 'attr_id', '属性id', 'bigint(20)', 'Long', 'attrId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-05-15 15:51:31', '', '2026-05-18 15:22:18');
INSERT INTO `gen_table_column` VALUES (12, 2, 'attr_name', '属性名', 'char(30)', 'String', 'attrName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 2, 'admin', '2026-05-15 15:51:31', '', '2026-05-18 15:22:18');
INSERT INTO `gen_table_column` VALUES (13, 2, 'search_type', '是否需要检索[0-不需要，1-需要]', 'tinyint(4)', 'Integer', 'searchType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', '', 3, 'admin', '2026-05-15 15:51:31', '', '2026-05-18 15:22:18');
INSERT INTO `gen_table_column` VALUES (14, 2, 'value_type', '值类型[0-为单个值，1-可以选择多个值]', 'tinyint(4)', 'Integer', 'valueType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', '', 4, 'admin', '2026-05-15 15:51:31', '', '2026-05-18 15:22:18');
INSERT INTO `gen_table_column` VALUES (15, 2, 'icon', '属性图标', 'varchar(255)', 'String', 'icon', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2026-05-15 15:51:31', '', '2026-05-18 15:22:18');
INSERT INTO `gen_table_column` VALUES (16, 2, 'value_select', '可选值列表[用逗号分隔]', 'char(255)', 'String', 'valueSelect', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-05-15 15:51:31', '', '2026-05-18 15:22:18');
INSERT INTO `gen_table_column` VALUES (17, 2, 'attr_type', '属性类型[0-销售属性，1-基本属性', 'tinyint(4)', 'Integer', 'attrType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', '', 7, 'admin', '2026-05-15 15:51:31', '', '2026-05-18 15:22:18');
INSERT INTO `gen_table_column` VALUES (18, 2, 'enable', '启用状态[0 - 禁用，1 - 启用]', 'bigint(20)', 'Long', 'enable', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 8, 'admin', '2026-05-15 15:51:31', '', '2026-05-18 15:22:18');
INSERT INTO `gen_table_column` VALUES (19, 2, 'catelog_id', '所属分类', 'bigint(20)', 'Long', 'catelogId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 9, 'admin', '2026-05-15 15:51:31', '', '2026-05-18 15:22:18');
INSERT INTO `gen_table_column` VALUES (20, 2, 'show_desc', '快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整', 'tinyint(4)', 'Integer', 'showDesc', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 10, 'admin', '2026-05-15 15:51:31', '', '2026-05-18 15:22:18');
INSERT INTO `gen_table_column` VALUES (21, 3, 'id', 'id', 'bigint(20)', 'Long', 'id', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (22, 3, 'attr_id', '属性id', 'bigint(20)', 'Long', 'attrId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (23, 3, 'attr_group_id', '属性分组id', 'bigint(20)', 'Long', 'attrGroupId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (24, 3, 'attr_sort', '属性组内排序', 'int(11)', 'Long', 'attrSort', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (25, 4, 'attr_group_id', '分组id', 'bigint(20)', 'Long', 'attrGroupId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-05-15 15:51:31', '', '2026-05-18 15:23:09');
INSERT INTO `gen_table_column` VALUES (26, 4, 'attr_group_name', '组名', 'char(20)', 'String', 'attrGroupName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 2, 'admin', '2026-05-15 15:51:31', '', '2026-05-18 15:23:09');
INSERT INTO `gen_table_column` VALUES (27, 4, 'sort', '排序', 'int(11)', 'Long', 'sort', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-05-15 15:51:31', '', '2026-05-18 15:23:09');
INSERT INTO `gen_table_column` VALUES (28, 4, 'descript', '描述', 'varchar(255)', 'String', 'descript', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2026-05-15 15:51:31', '', '2026-05-18 15:23:09');
INSERT INTO `gen_table_column` VALUES (29, 4, 'icon', '组图标', 'varchar(255)', 'String', 'icon', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2026-05-15 15:51:31', '', '2026-05-18 15:23:09');
INSERT INTO `gen_table_column` VALUES (30, 4, 'catelog_id', '所属分类id', 'bigint(20)', 'Long', 'catelogId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-05-15 15:51:31', '', '2026-05-18 15:23:09');
INSERT INTO `gen_table_column` VALUES (31, 5, 'brand_id', '品牌id', 'bigint(20)', 'Long', 'brandId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (32, 5, 'name', '品牌名', 'char(50)', 'String', 'name', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 2, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (33, 5, 'logo', '品牌logo地址', 'varchar(2000)', 'String', 'logo', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'textarea', '', 3, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (34, 5, 'descript', '介绍', 'longtext', 'String', 'descript', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'textarea', '', 4, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (35, 5, 'show_status', '显示状态[0-不显示；1-显示]', 'tinyint(4)', 'Integer', 'showStatus', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', '', 5, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (36, 5, 'first_letter', '检索首字母', 'char(1)', 'String', 'firstLetter', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (37, 5, 'sort', '排序', 'int(11)', 'Long', 'sort', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 7, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (38, 6, 'cat_id', '分类id', 'bigint(20)', 'Long', 'catId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (39, 6, 'name', '分类名称', 'char(50)', 'String', 'name', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 2, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (40, 6, 'parent_cid', '父分类id', 'bigint(20)', 'Long', 'parentCid', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (41, 6, 'cat_level', '层级', 'int(11)', 'Long', 'catLevel', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (42, 6, 'show_status', '是否显示[0-不显示，1显示]', 'tinyint(4)', 'Integer', 'showStatus', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', '', 5, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (43, 6, 'sort', '排序', 'int(11)', 'Long', 'sort', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (44, 6, 'icon', '图标地址', 'char(255)', 'String', 'icon', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 7, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (45, 6, 'product_unit', '计量单位', 'char(50)', 'String', 'productUnit', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 8, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (46, 6, 'product_count', '商品数量', 'int(11)', 'Long', 'productCount', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 9, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (47, 7, 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (48, 7, 'brand_id', '品牌id', 'bigint(20)', 'Long', 'brandId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (49, 7, 'catelog_id', '分类id', 'bigint(20)', 'Long', 'catelogId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (50, 7, 'brand_name', NULL, 'varchar(255)', 'String', 'brandName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (51, 7, 'catelog_name', NULL, 'varchar(255)', 'String', 'catelogName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 5, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (52, 8, 'id', 'id', 'bigint(20)', 'Long', 'id', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (53, 8, 'comment_id', '评论id', 'bigint(20)', 'Long', 'commentId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (54, 8, 'reply_id', '回复id', 'bigint(20)', 'Long', 'replyId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (55, 9, 'id', 'id', 'bigint(20)', 'Long', 'id', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (56, 9, 'spu_id', '商品id', 'bigint(20)', 'Long', 'spuId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (57, 9, 'attr_id', '属性id', 'bigint(20)', 'Long', 'attrId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (58, 9, 'attr_name', '属性名', 'varchar(200)', 'String', 'attrName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (59, 9, 'attr_value', '属性值', 'varchar(200)', 'String', 'attrValue', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (60, 9, 'attr_sort', '顺序', 'int(11)', 'Long', 'attrSort', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (61, 9, 'quick_show', '快速展示【是否展示在介绍上；0-否 1-是】', 'tinyint(4)', 'Integer', 'quickShow', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 7, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (62, 10, 'id', 'id', 'bigint(20)', 'Long', 'id', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (63, 10, 'sku_id', 'sku_id', 'bigint(20)', 'Long', 'skuId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (64, 10, 'img_url', '图片地址', 'varchar(255)', 'String', 'imgUrl', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (65, 10, 'img_sort', '排序', 'int(11)', 'Long', 'imgSort', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (66, 10, 'default_img', '默认图[0 - 不是默认图，1 - 是默认图]', 'int(11)', 'Long', 'defaultImg', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (67, 11, 'sku_id', 'skuId', 'bigint(20)', 'Long', 'skuId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (68, 11, 'spu_id', 'spuId', 'bigint(20)', 'Long', 'spuId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (69, 11, 'sku_name', 'sku名称', 'varchar(255)', 'String', 'skuName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 3, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (70, 11, 'sku_desc', 'sku介绍描述', 'varchar(2000)', 'String', 'skuDesc', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'textarea', '', 4, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (71, 11, 'catalog_id', '所属分类id', 'bigint(20)', 'Long', 'catalogId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (72, 11, 'brand_id', '品牌id', 'bigint(20)', 'Long', 'brandId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (73, 11, 'sku_default_img', '默认图片', 'varchar(255)', 'String', 'skuDefaultImg', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 7, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (74, 11, 'sku_title', '标题', 'varchar(255)', 'String', 'skuTitle', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 8, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (75, 11, 'sku_subtitle', '副标题', 'varchar(2000)', 'String', 'skuSubtitle', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'textarea', '', 9, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (76, 11, 'price', '价格', 'decimal(18,4)', 'BigDecimal', 'price', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 10, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (77, 11, 'sale_count', '销量', 'bigint(20)', 'Long', 'saleCount', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 11, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (78, 12, 'id', 'id', 'bigint(20)', 'Long', 'id', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (79, 12, 'sku_id', 'sku_id', 'bigint(20)', 'Long', 'skuId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (80, 12, 'attr_id', 'attr_id', 'bigint(20)', 'Long', 'attrId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (81, 12, 'attr_name', '销售属性名', 'varchar(200)', 'String', 'attrName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (82, 12, 'attr_value', '销售属性值', 'varchar(200)', 'String', 'attrValue', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (83, 12, 'attr_sort', '顺序', 'int(11)', 'Long', 'attrSort', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (84, 13, 'id', 'id', 'bigint(20)', 'Long', 'id', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (85, 13, 'sku_id', 'sku_id', 'bigint(20)', 'Long', 'skuId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (86, 13, 'spu_id', 'spu_id', 'bigint(20)', 'Long', 'spuId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (87, 13, 'spu_name', '商品名字', 'varchar(255)', 'String', 'spuName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (88, 13, 'member_nick_name', '会员昵称', 'varchar(255)', 'String', 'memberNickName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 5, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (89, 13, 'star', '星级', 'tinyint(1)', 'Integer', 'star', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (90, 13, 'member_ip', '会员ip', 'varchar(64)', 'String', 'memberIp', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 7, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (91, 13, 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 8, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (92, 13, 'show_status', '显示状态[0-不显示，1-显示]', 'tinyint(1)', 'Integer', 'showStatus', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', '', 9, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (93, 13, 'spu_attributes', '购买时属性组合', 'varchar(255)', 'String', 'spuAttributes', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 10, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (94, 13, 'likes_count', '点赞数', 'int(11)', 'Long', 'likesCount', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 11, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (95, 13, 'reply_count', '回复数', 'int(11)', 'Long', 'replyCount', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 12, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (96, 13, 'resources', '评论图片/视频[json数据；[{type:文件类型,url:资源路径}]]', 'varchar(1000)', 'String', 'resources', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'textarea', '', 13, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (97, 13, 'content', '内容', 'text', 'String', 'content', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'editor', '', 14, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (98, 13, 'member_icon', '用户头像', 'varchar(255)', 'String', 'memberIcon', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 15, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (99, 13, 'comment_type', '评论类型[0 - 对商品的直接评论，1 - 对评论的回复]', 'tinyint(4)', 'Integer', 'commentType', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', '', 16, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (100, 14, 'id', 'id', 'bigint(20)', 'Long', 'id', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (101, 14, 'spu_id', 'spu_id', 'bigint(20)', 'Long', 'spuId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (102, 14, 'img_name', '图片名', 'varchar(200)', 'String', 'imgName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 3, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (103, 14, 'img_url', '图片地址', 'varchar(255)', 'String', 'imgUrl', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (104, 14, 'img_sort', '顺序', 'int(11)', 'Long', 'imgSort', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (105, 14, 'default_img', '是否默认图', 'tinyint(4)', 'Integer', 'defaultImg', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (106, 15, 'id', '商品id', 'bigint(20)', 'Long', 'id', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (107, 15, 'spu_name', '商品名称', 'varchar(200)', 'String', 'spuName', '0', '0', '0', '1', '1', '1', '1', 'LIKE', 'input', '', 2, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (108, 15, 'spu_description', '商品描述', 'varchar(1000)', 'String', 'spuDescription', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'textarea', '', 3, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (109, 15, 'catalog_id', '所属分类id', 'bigint(20)', 'Long', 'catalogId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (110, 15, 'brand_id', '品牌id', 'bigint(20)', 'Long', 'brandId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (111, 15, 'weight', NULL, 'decimal(18,4)', 'BigDecimal', 'weight', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (112, 15, 'publish_status', '上架状态[0 - 下架，1 - 上架]', 'tinyint(4)', 'Integer', 'publishStatus', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', '', 7, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (113, 15, 'create_time', NULL, 'datetime', 'Date', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 8, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (114, 15, 'update_time', NULL, 'datetime', 'Date', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'datetime', '', 9, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (115, 16, 'spu_id', '商品id', 'bigint(20)', 'Long', 'spuId', '1', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2026-05-15 15:51:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (116, 16, 'decript', '商品介绍', 'longtext', 'String', 'decript', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'textarea', '', 2, 'admin', '2026-05-15 15:51:31', '', NULL);

-- ----------------------------
-- Table structure for pms_attr
-- ----------------------------
DROP TABLE IF EXISTS `pms_attr`;
CREATE TABLE `pms_attr`  (
  `attr_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '属性id',
  `attr_name` char(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性名',
  `search_type` tinyint(4) NULL DEFAULT NULL COMMENT '是否需要检索[0-不需要，1-需要]',
  `value_type` tinyint(4) NULL DEFAULT NULL COMMENT '值类型[0-为单个值，1-可以选择多个值]',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性图标',
  `value_select` char(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '可选值列表[用逗号分隔]',
  `attr_type` tinyint(4) NULL DEFAULT NULL COMMENT '属性类型[0-销售属性，1-基本属性',
  `enable` bigint(20) NULL DEFAULT NULL COMMENT '启用状态[0 - 禁用，1 - 启用]',
  `catelog_id` bigint(20) NULL DEFAULT NULL COMMENT '所属分类',
  `show_desc` tinyint(4) NULL DEFAULT NULL COMMENT '快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整',
  PRIMARY KEY (`attr_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 70 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品属性' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pms_attr
-- ----------------------------
INSERT INTO `pms_attr` VALUES (17, '入网型号', 0, 1, '', '官网参数', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (18, '上市年份', 0, 1, '', '2020,2021,2022,2023,2024,2025', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (19, '上市月份', 0, 1, '', '1月,2月,3月,4月,5月,6月,7月,8月,9月,10月,11月,12月', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (20, '机身材质工艺', 0, 1, '', '玻璃,金属,素皮,陶瓷,塑料', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (21, '机身长度', 0, 0, 'dd', NULL, 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (22, '机身宽度', 0, 0, '', '', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (23, '机身厚度', 0, 0, '', '', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (24, '机身重量', 0, 0, '', '', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (25, '防水等级', 0, 1, '', 'IP53,IP67,IP68,不支持', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (26, 'CPU品牌', 1, 1, '', '高通,联发科,华为麒麟,苹果A系列', 1, 1, 225, 1);
INSERT INTO `pms_attr` VALUES (27, 'CPU型号', 1, 1, '', '骁龙8 Gen3,骁龙8 Gen2,天玑9300,天玑9200,麒麟9000,A17,A16', 1, 1, 225, 1);
INSERT INTO `pms_attr` VALUES (28, 'CPU核心数', 0, 1, '', '八核,六核', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (29, 'CPU主频', 0, 0, '', '', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (30, 'GPU型号', 0, 0, '', '', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (31, '屏幕尺寸', 1, 0, '', '', 1, 1, 225, 1);
INSERT INTO `pms_attr` VALUES (32, '屏幕类型', 0, 1, '', 'OLED,AMOLED,LCD', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (33, '分辨率', 1, 0, '', '', 1, 1, 225, 1);
INSERT INTO `pms_attr` VALUES (34, '屏幕刷新率', 1, 1, '', '60Hz,90Hz,120Hz,144Hz', 1, 1, 225, 1);
INSERT INTO `pms_attr` VALUES (35, '触控采样率', 0, 1, '', '120Hz,240Hz,480Hz', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (36, '屏幕亮度', 0, 0, '', '', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (37, '电池容量', 1, 0, '', '', 1, 1, 225, 1);
INSERT INTO `pms_attr` VALUES (38, '有线快充功率', 1, 1, '', '20W,33W,67W,80W,100W,120W', 1, 1, 225, 1);
INSERT INTO `pms_attr` VALUES (39, '无线快充功率', 0, 1, '', '15W,30W,50W,不支持', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (40, '电池类型', 0, 1, '', '不可拆卸', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (41, '反向充电', 0, 1, '', '支持,不支持', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (42, '后置主摄像素', 1, 0, '', '', 1, 1, 225, 1);
INSERT INTO `pms_attr` VALUES (43, '后置超广角像素', 0, 0, '', '', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (44, '后置长焦像素', 0, 0, '', '', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (45, '前置摄像头像素', 1, 0, '', '', 1, 1, 225, 1);
INSERT INTO `pms_attr` VALUES (46, '视频拍摄规格', 0, 1, '', '1080P,4K,8K', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (47, '闪光灯类型', 0, 1, '', 'LED,双LED', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (48, '网络类型', 1, 1, '', '5G,4G,3G', 1, 1, 225, 1);
INSERT INTO `pms_attr` VALUES (49, '运营商支持', 1, 1, '', '全网通,移动,联通,电信', 1, 1, 225, 1);
INSERT INTO `pms_attr` VALUES (50, 'SIM卡类型', 0, 1, '', '单卡,双卡', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (51, '蓝牙版本', 0, 1, '', '5.0,5.2,5.3,5.4', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (52, 'Wi-Fi规格', 0, 1, '', 'Wi-Fi 5,Wi-Fi 6,Wi-Fi 6E', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (53, 'NFC功能', 0, 1, '', '支持,不支持', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (54, '红外遥控', 0, 1, '', '支持,不支持', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (55, 'USB接口类型', 0, 1, '', 'Type-C,Lightning', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (56, '定位系统', 0, 1, '', 'GPS,北斗,GLONASS', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (57, '操作系统', 1, 1, '', 'iOS,Android,HarmonyOS', 1, 1, 225, 1);
INSERT INTO `pms_attr` VALUES (58, '系统UI', 0, 1, '', 'MIUI,ColorOS,OriginOS,EMUI', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (59, '指纹识别', 0, 1, '', '屏下,侧边,后置', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (60, '面部识别', 0, 1, '', '支持,不支持', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (61, '扬声器类型', 0, 1, '', '单扬声器,双扬声器', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (62, '3.5mm耳机孔', 0, 1, '', '支持,不支持', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (63, '散热方式', 0, 0, '', '', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (64, '产地', 0, 0, '', '', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (65, '保修期', 0, 1, '', '1年,2年', 1, 1, 225, 0);
INSERT INTO `pms_attr` VALUES (66, '机身颜色', 1, 1, '', '黑色,白色,蓝色,绿色,紫色,金色,粉色,灰色,素皮黑', 0, 1, 225, 1);
INSERT INTO `pms_attr` VALUES (67, '运行内存(RAM)', 1, 1, '', '4GB,6GB,8GB,12GB,16GB,18GB', 0, 1, 225, 1);
INSERT INTO `pms_attr` VALUES (68, '机身存储(ROM)', 1, 1, '', '64GB,128GB,256GB,512GB,1TB', 0, 1, 225, 1);
INSERT INTO `pms_attr` VALUES (69, '版本', 1, 1, '', '标准版,Pro版,Pro+,Ultra版,SE版', 0, 1, 225, 1);

-- ----------------------------
-- Table structure for pms_attr_attrgroup_relation
-- ----------------------------
DROP TABLE IF EXISTS `pms_attr_attrgroup_relation`;
CREATE TABLE `pms_attr_attrgroup_relation`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `attr_id` bigint(20) NULL DEFAULT NULL COMMENT '属性id',
  `attr_group_id` bigint(20) NULL DEFAULT NULL COMMENT '属性分组id',
  `attr_sort` int(11) NULL DEFAULT NULL COMMENT '属性组内排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '属性&属性分组关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pms_attr_attrgroup_relation
-- ----------------------------
INSERT INTO `pms_attr_attrgroup_relation` VALUES (51, 17, 1, 0);
INSERT INTO `pms_attr_attrgroup_relation` VALUES (52, 18, 1, 1);
INSERT INTO `pms_attr_attrgroup_relation` VALUES (53, 19, 1, 2);
INSERT INTO `pms_attr_attrgroup_relation` VALUES (54, 20, 1, 3);
INSERT INTO `pms_attr_attrgroup_relation` VALUES (55, 21, 1, 4);
INSERT INTO `pms_attr_attrgroup_relation` VALUES (56, 22, 1, 5);
INSERT INTO `pms_attr_attrgroup_relation` VALUES (57, 23, 1, 6);
INSERT INTO `pms_attr_attrgroup_relation` VALUES (58, 24, 1, 7);
INSERT INTO `pms_attr_attrgroup_relation` VALUES (59, 25, 1, 8);

-- ----------------------------
-- Table structure for pms_attr_group
-- ----------------------------
DROP TABLE IF EXISTS `pms_attr_group`;
CREATE TABLE `pms_attr_group`  (
  `attr_group_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分组id',
  `attr_group_name` char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组名',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `descript` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组图标',
  `catelog_id` bigint(20) NULL DEFAULT NULL COMMENT '所属分类id',
  PRIMARY KEY (`attr_group_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '属性分组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pms_attr_group
-- ----------------------------
INSERT INTO `pms_attr_group` VALUES (1, '主体', 1, '手机主体信息', 'dd', 225);
INSERT INTO `pms_attr_group` VALUES (2, '基本信息', 2, '上市、型号等基础信息', 'xx', 225);
INSERT INTO `pms_attr_group` VALUES (4, '屏幕', 3, '屏幕参数', 'xx', 225);
INSERT INTO `pms_attr_group` VALUES (7, '主芯片', 4, 'CPU、性能相关', 'xx', 225);
INSERT INTO `pms_attr_group` VALUES (8, '电池与充电', 5, '电池容量、快充功率', 'dd', 225);
INSERT INTO `pms_attr_group` VALUES (9, '摄像头', 6, '前后置相机参数', 'dd', 225);
INSERT INTO `pms_attr_group` VALUES (10, '网络与连接', 7, '5G、蓝牙、NFC等', 'dd', 225);
INSERT INTO `pms_attr_group` VALUES (11, '外观与尺寸', 8, '机身尺寸、材质、重量', 'dd', 225);
INSERT INTO `pms_attr_group` VALUES (12, '系统与功能', 9, '操作系统、特色功能', 'dd', 225);
INSERT INTO `pms_attr_group` VALUES (13, '其他', 10, '保修、配件等', 'dd', 225);

-- ----------------------------
-- Table structure for pms_brand
-- ----------------------------
DROP TABLE IF EXISTS `pms_brand`;
CREATE TABLE `pms_brand`  (
  `brand_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '品牌id',
  `name` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '品牌名',
  `logo` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '品牌logo地址',
  `descript` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '介绍',
  `show_status` tinyint(4) NULL DEFAULT NULL COMMENT '显示状态[0-不显示；1-显示]',
  `first_letter` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '检索首字母',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`brand_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '品牌' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pms_brand
-- ----------------------------
INSERT INTO `pms_brand` VALUES (9, '华为', 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-18/de2426bd-a689-41d0-865a-d45d1afa7cde_huawei.png', '华为', 1, 'H', 1);
INSERT INTO `pms_brand` VALUES (10, '小米', 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-18/1f9e6968-cf92-462e-869a-4c2331a4113f_xiaomi.png', '小米', 1, 'M', 1);
INSERT INTO `pms_brand` VALUES (11, 'oppo', 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-18/5c8303f2-8b0c-4a5b-89a6-86513133d758_oppo.png', 'oppo', 1, 'O', 1);
INSERT INTO `pms_brand` VALUES (12, 'Apple', 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-18/819bb0b1-3ed8-4072-8304-78811a289781_apple.png', '苹果', 1, 'A', 1);

-- ----------------------------
-- Table structure for pms_category
-- ----------------------------
DROP TABLE IF EXISTS `pms_category`;
CREATE TABLE `pms_category`  (
  `cat_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类id',
  `name` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类名称',
  `parent_cid` bigint(20) NULL DEFAULT NULL COMMENT '父分类id',
  `cat_level` int(11) NULL DEFAULT NULL COMMENT '层级',
  `show_status` tinyint(4) NULL DEFAULT NULL COMMENT '是否显示[0-不显示，1显示]',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `icon` char(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标地址',
  `product_unit` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '计量单位',
  `product_count` int(11) NULL DEFAULT NULL COMMENT '商品数量',
  PRIMARY KEY (`cat_id`) USING BTREE,
  INDEX `parent_cid`(`parent_cid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1434 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品三级分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pms_category
-- ----------------------------
INSERT INTO `pms_category` VALUES (1, '图书、音像、电子书刊', 0, 1, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (2, '手机', 0, 1, 1, 1, '111q', NULL, 0);
INSERT INTO `pms_category` VALUES (3, '家用电器', 0, 1, 1, 2, 'aaa', NULL, 0);
INSERT INTO `pms_category` VALUES (4, '数码', 0, 1, 1, 3, 'aaa', NULL, 0);
INSERT INTO `pms_category` VALUES (5, '家居家装', 0, 1, 1, 4, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (6, '电脑办公', 0, 1, 1, 5, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (7, '厨具', 0, 1, 1, 6, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (8, '个护化妆', 0, 1, 1, 7, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (9, '服饰内衣', 0, 1, 1, 8, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (10, '钟表', 0, 1, 1, 9, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (11, '鞋靴', 0, 1, 1, 10, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (12, '母婴', 0, 1, 1, 11, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (13, '礼品箱包', 0, 1, 1, 12, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (14, '食品饮料、保健食品', 0, 1, 1, 13, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (15, '珠宝', 0, 1, 1, 14, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (16, '汽车用品', 0, 1, 1, 15, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (17, '运动健康', 0, 1, 1, 16, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (18, '玩具乐器', 0, 1, 1, 17, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (19, '彩票、旅行、充值、票务', 0, 1, 1, 18, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (20, '生鲜', 0, 1, 1, 19, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (21, '整车', 0, 1, 1, 20, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (22, '电子书刊', 1, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (23, '音像', 1, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (24, '英文原版', 1, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (25, '文艺', 1, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (26, '少儿', 1, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (27, '人文社科', 1, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (28, '经管励志', 1, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (29, '生活', 1, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (30, '科技', 1, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (31, '教育', 1, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (32, '港台图书', 1, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (33, '其他', 1, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (34, '手机通讯', 2, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (35, '运营商', 2, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (36, '手机配件', 2, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (37, '大 家 电', 3, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (38, '厨卫大电', 3, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (39, '厨房小电', 3, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (40, '生活电器', 3, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (41, '个护健康', 3, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (42, '五金家装', 3, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (43, '摄影摄像', 4, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (44, '数码配件', 4, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (45, '智能设备', 4, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (46, '影音娱乐', 4, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (47, '电子教育', 4, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (48, '虚拟商品', 4, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (49, '家纺', 5, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (50, '灯具', 5, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (51, '生活日用', 5, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (52, '家装软饰', 5, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (53, '宠物生活', 5, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (54, '电脑整机', 6, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (55, '电脑配件', 6, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (56, '外设产品', 6, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (57, '游戏设备', 6, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (58, '网络产品', 6, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (59, '办公设备', 6, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (60, '文具/耗材', 6, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (61, '服务产品', 6, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (62, '烹饪锅具', 7, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (63, '刀剪菜板', 7, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (64, '厨房配件', 7, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (65, '水具酒具', 7, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (66, '餐具', 7, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (67, '酒店用品', 7, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (68, '茶具/咖啡具', 7, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (69, '清洁用品', 8, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (70, '面部护肤', 8, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (71, '身体护理', 8, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (72, '口腔护理', 8, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (73, '女性护理', 8, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (74, '洗发护发', 8, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (75, '香水彩妆', 8, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (76, '女装', 9, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (77, '男装', 9, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (78, '内衣', 9, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (79, '洗衣服务', 9, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (80, '服饰配件', 9, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (81, '钟表', 10, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (82, '流行男鞋', 11, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (83, '时尚女鞋', 11, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (84, '奶粉', 12, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (85, '营养辅食', 12, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (86, '尿裤湿巾', 12, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (87, '喂养用品', 12, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (88, '洗护用品', 12, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (89, '童车童床', 12, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (90, '寝居服饰', 12, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (91, '妈妈专区', 12, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (92, '童装童鞋', 12, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (93, '安全座椅', 12, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (94, '潮流女包', 13, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (95, '精品男包', 13, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (96, '功能箱包', 13, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (97, '礼品', 13, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (98, '奢侈品', 13, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (99, '婚庆', 13, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (100, '进口食品', 14, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (101, '地方特产', 14, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (102, '休闲食品', 14, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (103, '粮油调味', 14, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (104, '饮料冲调', 14, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (105, '食品礼券', 14, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (106, '茗茶', 14, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (107, '时尚饰品', 15, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (108, '黄金', 15, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (109, 'K金饰品', 15, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (110, '金银投资', 15, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (111, '银饰', 15, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (112, '钻石', 15, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (113, '翡翠玉石', 15, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (114, '水晶玛瑙', 15, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (115, '彩宝', 15, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (116, '铂金', 15, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (117, '木手串/把件', 15, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (118, '珍珠', 15, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (119, '维修保养', 16, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (120, '车载电器', 16, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (121, '美容清洗', 16, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (122, '汽车装饰', 16, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (123, '安全自驾', 16, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (124, '汽车服务', 16, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (125, '赛事改装', 16, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (126, '运动鞋包', 17, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (127, '运动服饰', 17, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (128, '骑行运动', 17, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (129, '垂钓用品', 17, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (130, '游泳用品', 17, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (131, '户外鞋服', 17, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (132, '户外装备', 17, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (133, '健身训练', 17, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (134, '体育用品', 17, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (135, '适用年龄', 18, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (136, '遥控/电动', 18, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (137, '毛绒布艺', 18, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (138, '娃娃玩具', 18, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (139, '模型玩具', 18, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (140, '健身玩具', 18, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (141, '动漫玩具', 18, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (142, '益智玩具', 18, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (143, '积木拼插', 18, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (144, 'DIY玩具', 18, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (145, '创意减压', 18, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (146, '乐器', 18, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (147, '彩票', 19, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (148, '机票', 19, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (149, '酒店', 19, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (150, '旅行', 19, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (151, '充值', 19, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (152, '游戏', 19, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (153, '票务', 19, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (154, '产地直供', 20, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (155, '水果', 20, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (156, '猪牛羊肉', 20, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (157, '海鲜水产', 20, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (158, '禽肉蛋品', 20, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (159, '冷冻食品', 20, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (160, '熟食腊味', 20, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (161, '饮品甜品', 20, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (162, '蔬菜', 20, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (163, '全新整车', 21, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (164, '二手车', 21, 2, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (165, '电子书', 22, 3, 1, 1, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (166, '网络原创', 22, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (167, '数字杂志', 22, 3, 1, 2, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (168, '多媒体图书', 22, 3, 1, 3, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (169, '音乐', 23, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (170, '影视', 23, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (171, '教育音像', 23, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (172, '少儿', 24, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (173, '商务投资', 24, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (174, '英语学习与考试', 24, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (175, '文学', 24, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (176, '传记', 24, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (177, '励志', 24, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (178, '小说', 25, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (179, '文学', 25, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (180, '青春文学', 25, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (181, '传记', 25, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (182, '艺术', 25, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (183, '少儿', 26, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (184, '0-2岁', 26, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (185, '3-6岁', 26, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (186, '7-10岁', 26, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (187, '11-14岁', 26, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (188, '历史', 27, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (189, '哲学', 27, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (190, '国学', 27, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (191, '政治/军事', 27, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (192, '法律', 27, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (193, '人文社科', 27, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (194, '心理学', 27, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (195, '文化', 27, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (196, '社会科学', 27, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (197, '经济', 28, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (198, '金融与投资', 28, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (199, '管理', 28, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (200, '励志与成功', 28, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (201, '生活', 29, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (202, '健身与保健', 29, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (203, '家庭与育儿', 29, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (204, '旅游', 29, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (205, '烹饪美食', 29, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (206, '工业技术', 30, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (207, '科普读物', 30, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (208, '建筑', 30, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (209, '医学', 30, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (210, '科学与自然', 30, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (211, '计算机与互联网', 30, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (212, '电子通信', 30, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (213, '中小学教辅', 31, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (214, '教育与考试', 31, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (215, '外语学习', 31, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (216, '大中专教材', 31, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (217, '字典词典', 31, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (218, '艺术/设计/收藏', 32, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (219, '经济管理', 32, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (220, '文化/学术', 32, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (221, '少儿', 32, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (222, '工具书', 33, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (223, '杂志/期刊', 33, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (224, '套装书', 33, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (225, '手机', 34, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (226, '对讲机', 34, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (227, '合约机', 35, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (228, '选号中心', 35, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (229, '装宽带', 35, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (230, '办套餐', 35, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (231, '移动电源', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (232, '电池/移动电源', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (233, '蓝牙耳机', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (234, '充电器/数据线', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (235, '苹果周边', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (236, '手机耳机', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (237, '手机贴膜', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (238, '手机存储卡', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (239, '充电器', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (240, '数据线', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (241, '手机保护套', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (242, '车载配件', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (243, 'iPhone 配件', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (244, '手机电池', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (245, '创意配件', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (246, '便携/无线音响', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (247, '手机饰品', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (248, '拍照配件', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (249, '手机支架', 36, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (250, '平板电视', 37, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (251, '空调', 37, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (252, '冰箱', 37, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (253, '洗衣机', 37, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (254, '家庭影院', 37, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (255, 'DVD/电视盒子', 37, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (256, '迷你音响', 37, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (257, '冷柜/冰吧', 37, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (258, '家电配件', 37, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (259, '功放', 37, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (260, '回音壁/Soundbar', 37, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (261, 'Hi-Fi专区', 37, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (262, '电视盒子', 37, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (263, '酒柜', 37, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (264, '燃气灶', 38, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (265, '油烟机', 38, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (266, '热水器', 38, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (267, '消毒柜', 38, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (268, '洗碗机', 38, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (269, '料理机', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (270, '榨汁机', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (271, '电饭煲', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (272, '电压力锅', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (273, '豆浆机', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (274, '咖啡机', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (275, '微波炉', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (276, '电烤箱', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (277, '电磁炉', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (278, '面包机', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (279, '煮蛋器', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (280, '酸奶机', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (281, '电炖锅', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (282, '电水壶/热水瓶', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (283, '电饼铛', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (284, '多用途锅', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (285, '电烧烤炉', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (286, '果蔬解毒机', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (287, '其它厨房电器', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (288, '养生壶/煎药壶', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (289, '电热饭盒', 39, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (290, '取暖电器', 40, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (291, '净化器', 40, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (292, '加湿器', 40, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (293, '扫地机器人', 40, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (294, '吸尘器', 40, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (295, '挂烫机/熨斗', 40, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (296, '插座', 40, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (297, '电话机', 40, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (298, '清洁机', 40, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (299, '除湿机', 40, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (300, '干衣机', 40, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (301, '收录/音机', 40, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (302, '电风扇', 40, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (303, '冷风扇', 40, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (304, '其它生活电器', 40, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (305, '生活电器配件', 40, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (306, '净水器', 40, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (307, '饮水机', 40, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (308, '剃须刀', 41, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (309, '剃/脱毛器', 41, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (310, '口腔护理', 41, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (311, '电吹风', 41, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (312, '美容器', 41, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (313, '理发器', 41, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (314, '卷/直发器', 41, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (315, '按摩椅', 41, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (316, '按摩器', 41, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (317, '足浴盆', 41, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (318, '血压计', 41, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (319, '电子秤/厨房秤', 41, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (320, '血糖仪', 41, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (321, '体温计', 41, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (322, '其它健康电器', 41, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (323, '计步器/脂肪检测仪', 41, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (324, '电动工具', 42, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (325, '手动工具', 42, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (326, '仪器仪表', 42, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (327, '浴霸/排气扇', 42, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (328, '灯具', 42, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (329, 'LED灯', 42, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (330, '洁身器', 42, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (331, '水槽', 42, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (332, '龙头', 42, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (333, '淋浴花洒', 42, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (334, '厨卫五金', 42, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (335, '家具五金', 42, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (336, '门铃', 42, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (337, '电气开关', 42, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (338, '插座', 42, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (339, '电工电料', 42, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (340, '监控安防', 42, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (341, '电线/线缆', 42, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (342, '数码相机', 43, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (343, '单电/微单相机', 43, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (344, '单反相机', 43, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (345, '摄像机', 43, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (346, '拍立得', 43, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (347, '运动相机', 43, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (348, '镜头', 43, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (349, '户外器材', 43, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (350, '影棚器材', 43, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (351, '冲印服务', 43, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (352, '数码相框', 43, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (353, '存储卡', 44, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (354, '读卡器', 44, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (355, '滤镜', 44, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (356, '闪光灯/手柄', 44, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (357, '相机包', 44, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (358, '三脚架/云台', 44, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (359, '相机清洁/贴膜', 44, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (360, '机身附件', 44, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (361, '镜头附件', 44, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (362, '电池/充电器', 44, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (363, '移动电源', 44, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (364, '数码支架', 44, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (365, '智能手环', 45, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (366, '智能手表', 45, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (367, '智能眼镜', 45, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (368, '运动跟踪器', 45, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (369, '健康监测', 45, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (370, '智能配饰', 45, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (371, '智能家居', 45, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (372, '体感车', 45, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (373, '其他配件', 45, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (374, '智能机器人', 45, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (375, '无人机', 45, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (376, 'MP3/MP4', 46, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (377, '智能设备', 46, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (378, '耳机/耳麦', 46, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (379, '便携/无线音箱', 46, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (380, '音箱/音响', 46, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (381, '高清播放器', 46, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (382, '收音机', 46, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (383, 'MP3/MP4配件', 46, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (384, '麦克风', 46, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (385, '专业音频', 46, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (386, '苹果配件', 46, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (387, '学生平板', 47, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (388, '点读机/笔', 47, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (389, '早教益智', 47, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (390, '录音笔', 47, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (391, '电纸书', 47, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (392, '电子词典', 47, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (393, '复读机', 47, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (394, '延保服务', 48, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (395, '杀毒软件', 48, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (396, '积分商品', 48, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (397, '桌布/罩件', 49, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (398, '地毯地垫', 49, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (399, '沙发垫套/椅垫', 49, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (400, '床品套件', 49, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (401, '被子', 49, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (402, '枕芯', 49, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (403, '床单被罩', 49, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (404, '毯子', 49, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (405, '床垫/床褥', 49, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (406, '蚊帐', 49, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (407, '抱枕靠垫', 49, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (408, '毛巾浴巾', 49, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (409, '电热毯', 49, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (410, '窗帘/窗纱', 49, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (411, '布艺软饰', 49, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (412, '凉席', 49, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (413, '台灯', 50, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (414, '节能灯', 50, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (415, '装饰灯', 50, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (416, '落地灯', 50, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (417, '应急灯/手电', 50, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (418, 'LED灯', 50, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (419, '吸顶灯', 50, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (420, '五金电器', 50, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (421, '筒灯射灯', 50, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (422, '吊灯', 50, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (423, '氛围照明', 50, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (424, '保暖防护', 51, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (425, '收纳用品', 51, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (426, '雨伞雨具', 51, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (427, '浴室用品', 51, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (428, '缝纫/针织用品', 51, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (429, '洗晒/熨烫', 51, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (430, '净化除味', 51, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (431, '相框/照片墙', 52, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (432, '装饰字画', 52, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (433, '节庆饰品', 52, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (434, '手工/十字绣', 52, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (435, '装饰摆件', 52, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (436, '帘艺隔断', 52, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (437, '墙贴/装饰贴', 52, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (438, '钟饰', 52, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (439, '花瓶花艺', 52, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (440, '香薰蜡烛', 52, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (441, '创意家居', 52, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (442, '宠物主粮', 53, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (443, '宠物零食', 53, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (444, '医疗保健', 53, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (445, '家居日用', 53, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (446, '宠物玩具', 53, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (447, '出行装备', 53, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (448, '洗护美容', 53, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (449, '笔记本', 54, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (450, '超极本', 54, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (451, '游戏本', 54, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (452, '平板电脑', 54, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (453, '平板电脑配件', 54, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (454, '台式机', 54, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (455, '服务器/工作站', 54, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (456, '笔记本配件', 54, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (457, '一体机', 54, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (458, 'CPU', 55, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (459, '主板', 55, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (460, '显卡', 55, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (461, '硬盘', 55, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (462, 'SSD固态硬盘', 55, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (463, '内存', 55, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (464, '机箱', 55, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (465, '电源', 55, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (466, '显示器', 55, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (467, '刻录机/光驱', 55, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (468, '散热器', 55, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (469, '声卡/扩展卡', 55, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (470, '装机配件', 55, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (471, '组装电脑', 55, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (472, '移动硬盘', 56, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (473, 'U盘', 56, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (474, '鼠标', 56, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (475, '键盘', 56, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (476, '鼠标垫', 56, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (477, '摄像头', 56, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (478, '手写板', 56, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (479, '硬盘盒', 56, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (480, '插座', 56, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (481, '线缆', 56, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (482, 'UPS电源', 56, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (483, '电脑工具', 56, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (484, '游戏设备', 56, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (485, '电玩', 56, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (486, '电脑清洁', 56, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (487, '网络仪表仪器', 56, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (488, '游戏机', 57, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (489, '游戏耳机', 57, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (490, '手柄/方向盘', 57, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (491, '游戏软件', 57, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (492, '游戏周边', 57, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (493, '路由器', 58, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (494, '网卡', 58, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (495, '交换机', 58, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (496, '网络存储', 58, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (497, '4G/3G上网', 58, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (498, '网络盒子', 58, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (499, '网络配件', 58, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (500, '投影机', 59, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (501, '投影配件', 59, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (502, '多功能一体机', 59, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (503, '打印机', 59, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (504, '传真设备', 59, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (505, '验钞/点钞机', 59, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (506, '扫描设备', 59, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (507, '复合机', 59, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (508, '碎纸机', 59, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (509, '考勤机', 59, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (510, '收款/POS机', 59, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (511, '会议音频视频', 59, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (512, '保险柜', 59, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (513, '装订/封装机', 59, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (514, '安防监控', 59, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (515, '办公家具', 59, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (516, '白板', 59, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (517, '硒鼓/墨粉', 60, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (518, '墨盒', 60, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (519, '色带', 60, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (520, '纸类', 60, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (521, '办公文具', 60, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (522, '学生文具', 60, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (523, '财会用品', 60, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (524, '文件管理', 60, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (525, '本册/便签', 60, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (526, '计算器', 60, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (527, '笔类', 60, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (528, '画具画材', 60, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (529, '刻录碟片/附件', 60, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (530, '上门安装', 61, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (531, '延保服务', 61, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (532, '维修保养', 61, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (533, '电脑软件', 61, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (534, '京东服务', 61, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (535, '炒锅', 62, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (536, '煎锅', 62, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (537, '压力锅', 62, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (538, '蒸锅', 62, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (539, '汤锅', 62, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (540, '奶锅', 62, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (541, '锅具套装', 62, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (542, '煲类', 62, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (543, '水壶', 62, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (544, '火锅', 62, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (545, '菜刀', 63, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (546, '剪刀', 63, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (547, '刀具套装', 63, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (548, '砧板', 63, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (549, '瓜果刀/刨', 63, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (550, '多功能刀', 63, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (551, '保鲜盒', 64, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (552, '烘焙/烧烤', 64, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (553, '饭盒/提锅', 64, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (554, '储物/置物架', 64, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (555, '厨房DIY/小工具', 64, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (556, '塑料杯', 65, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (557, '运动水壶', 65, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (558, '玻璃杯', 65, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (559, '陶瓷/马克杯', 65, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (560, '保温杯', 65, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (561, '保温壶', 65, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (562, '酒杯/酒具', 65, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (563, '杯具套装', 65, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (564, '餐具套装', 66, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (565, '碗/碟/盘', 66, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (566, '筷勺/刀叉', 66, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (567, '一次性用品', 66, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (568, '果盘/果篮', 66, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (569, '自助餐炉', 67, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (570, '酒店餐具', 67, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (571, '酒店水具', 67, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (572, '整套茶具', 68, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (573, '茶杯', 68, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (574, '茶壶', 68, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (575, '茶盘茶托', 68, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (576, '茶叶罐', 68, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (577, '茶具配件', 68, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (578, '茶宠摆件', 68, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (579, '咖啡具', 68, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (580, '其他', 68, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (581, '纸品湿巾', 69, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (582, '衣物清洁', 69, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (583, '清洁工具', 69, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (584, '驱虫用品', 69, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (585, '家庭清洁', 69, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (586, '皮具护理', 69, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (587, '一次性用品', 69, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (588, '洁面', 70, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (589, '乳液面霜', 70, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (590, '面膜', 70, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (591, '剃须', 70, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (592, '套装', 70, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (593, '精华', 70, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (594, '眼霜', 70, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (595, '卸妆', 70, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (596, '防晒', 70, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (597, '防晒隔离', 70, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (598, 'T区护理', 70, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (599, '眼部护理', 70, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (600, '精华露', 70, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (601, '爽肤水', 70, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (602, '沐浴', 71, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (603, '润肤', 71, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (604, '颈部', 71, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (605, '手足', 71, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (606, '纤体塑形', 71, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (607, '美胸', 71, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (608, '套装', 71, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (609, '精油', 71, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (610, '洗发护发', 71, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (611, '染发/造型', 71, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (612, '香薰精油', 71, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (613, '磨砂/浴盐', 71, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (614, '手工/香皂', 71, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (615, '洗发', 71, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (616, '护发', 71, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (617, '染发', 71, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (618, '磨砂膏', 71, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (619, '香皂', 71, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (620, '牙膏/牙粉', 72, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (621, '牙刷/牙线', 72, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (622, '漱口水', 72, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (623, '套装', 72, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (624, '卫生巾', 73, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (625, '卫生护垫', 73, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (626, '私密护理', 73, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (627, '脱毛膏', 73, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (628, '其他', 73, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (629, '洗发', 74, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (630, '护发', 74, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (631, '染发', 74, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (632, '造型', 74, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (633, '假发', 74, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (634, '套装', 74, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (635, '美发工具', 74, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (636, '脸部护理', 74, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (637, '香水', 75, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (638, '底妆', 75, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (639, '腮红', 75, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (640, '眼影', 75, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (641, '唇部', 75, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (642, '美甲', 75, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (643, '眼线', 75, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (644, '美妆工具', 75, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (645, '套装', 75, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (646, '防晒隔离', 75, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (647, '卸妆', 75, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (648, '眉笔', 75, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (649, '睫毛膏', 75, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (650, 'T恤', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (651, '衬衫', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (652, '针织衫', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (653, '雪纺衫', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (654, '卫衣', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (655, '马甲', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (656, '连衣裙', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (657, '半身裙', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (658, '牛仔裤', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (659, '休闲裤', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (660, '打底裤', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (661, '正装裤', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (662, '小西装', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (663, '短外套', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (664, '风衣', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (665, '毛呢大衣', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (666, '真皮皮衣', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (667, '棉服', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (668, '羽绒服', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (669, '大码女装', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (670, '中老年女装', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (671, '婚纱', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (672, '打底衫', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (673, '旗袍/唐装', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (674, '加绒裤', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (675, '吊带/背心', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (676, '羊绒衫', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (677, '短裤', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (678, '皮草', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (679, '礼服', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (680, '仿皮皮衣', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (681, '羊毛衫', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (682, '设计师/潮牌', 76, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (683, '衬衫', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (684, 'T恤', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (685, 'POLO衫', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (686, '针织衫', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (687, '羊绒衫', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (688, '卫衣', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (689, '马甲/背心', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (690, '夹克', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (691, '风衣', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (692, '毛呢大衣', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (693, '仿皮皮衣', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (694, '西服', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (695, '棉服', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (696, '羽绒服', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (697, '牛仔裤', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (698, '休闲裤', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (699, '西裤', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (700, '西服套装', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (701, '大码男装', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (702, '中老年男装', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (703, '唐装/中山装', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (704, '工装', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (705, '真皮皮衣', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (706, '加绒裤', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (707, '卫裤/运动裤', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (708, '短裤', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (709, '设计师/潮牌', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (710, '羊毛衫', 77, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (711, '文胸', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (712, '女式内裤', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (713, '男式内裤', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (714, '睡衣/家居服', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (715, '塑身美体', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (716, '泳衣', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (717, '吊带/背心', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (718, '抹胸', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (719, '连裤袜/丝袜', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (720, '美腿袜', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (721, '商务男袜', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (722, '保暖内衣', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (723, '情侣睡衣', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (724, '文胸套装', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (725, '少女文胸', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (726, '休闲棉袜', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (727, '大码内衣', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (728, '内衣配件', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (729, '打底裤袜', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (730, '打底衫', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (731, '秋衣秋裤', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (732, '情趣内衣', 78, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (733, '服装洗护', 79, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (734, '太阳镜', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (735, '光学镜架/镜片', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (736, '围巾/手套/帽子套装', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (737, '袖扣', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (738, '棒球帽', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (739, '毛线帽', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (740, '遮阳帽', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (741, '老花镜', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (742, '装饰眼镜', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (743, '防辐射眼镜', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (744, '游泳镜', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (745, '女士丝巾/围巾/披肩', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (746, '男士丝巾/围巾', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (747, '鸭舌帽', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (748, '贝雷帽', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (749, '礼帽', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (750, '真皮手套', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (751, '毛线手套', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (752, '防晒手套', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (753, '男士腰带/礼盒', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (754, '女士腰带/礼盒', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (755, '钥匙扣', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (756, '遮阳伞/雨伞', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (757, '口罩', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (758, '耳罩/耳包', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (759, '假领', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (760, '毛线/布面料', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (761, '领带/领结/领带夹', 80, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (762, '男表', 81, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (763, '瑞表', 81, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (764, '女表', 81, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (765, '国表', 81, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (766, '日韩表', 81, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (767, '欧美表', 81, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (768, '德表', 81, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (769, '儿童手表', 81, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (770, '智能手表', 81, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (771, '闹钟', 81, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (772, '座钟挂钟', 81, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (773, '钟表配件', 81, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (774, '商务休闲鞋', 82, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (775, '正装鞋', 82, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (776, '休闲鞋', 82, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (777, '凉鞋/沙滩鞋', 82, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (778, '男靴', 82, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (779, '功能鞋', 82, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (780, '拖鞋/人字拖', 82, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (781, '雨鞋/雨靴', 82, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (782, '传统布鞋', 82, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (783, '鞋配件', 82, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (784, '帆布鞋', 82, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (785, '增高鞋', 82, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (786, '工装鞋', 82, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (787, '定制鞋', 82, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (788, '高跟鞋', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (789, '单鞋', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (790, '休闲鞋', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (791, '凉鞋', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (792, '女靴', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (793, '雪地靴', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (794, '拖鞋/人字拖', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (795, '踝靴', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (796, '筒靴', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (797, '帆布鞋', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (798, '雨鞋/雨靴', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (799, '妈妈鞋', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (800, '鞋配件', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (801, '特色鞋', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (802, '鱼嘴鞋', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (803, '布鞋/绣花鞋', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (804, '马丁靴', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (805, '坡跟鞋', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (806, '松糕鞋', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (807, '内增高', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (808, '防水台', 83, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (809, '婴幼奶粉', 84, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (810, '孕妈奶粉', 84, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (811, '益生菌/初乳', 85, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (812, '米粉/菜粉', 85, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (813, '果泥/果汁', 85, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (814, 'DHA', 85, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (815, '宝宝零食', 85, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (816, '钙铁锌/维生素', 85, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (817, '清火/开胃', 85, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (818, '面条/粥', 85, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (819, '婴儿尿裤', 86, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (820, '拉拉裤', 86, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (821, '婴儿湿巾', 86, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (822, '成人尿裤', 86, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (823, '奶瓶奶嘴', 87, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (824, '吸奶器', 87, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (825, '暖奶消毒', 87, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (826, '儿童餐具', 87, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (827, '水壶/水杯', 87, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (828, '牙胶安抚', 87, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (829, '围兜/防溅衣', 87, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (830, '辅食料理机', 87, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (831, '食物存储', 87, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (832, '宝宝护肤', 88, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (833, '洗发沐浴', 88, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (834, '奶瓶清洗', 88, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (835, '驱蚊防晒', 88, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (836, '理发器', 88, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (837, '洗澡用具', 88, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (838, '婴儿口腔清洁', 88, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (839, '洗衣液/皂', 88, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (840, '日常护理', 88, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (841, '座便器', 88, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (842, '婴儿推车', 89, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (843, '餐椅摇椅', 89, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (844, '婴儿床', 89, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (845, '学步车', 89, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (846, '三轮车', 89, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (847, '自行车', 89, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (848, '电动车', 89, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (849, '扭扭车', 89, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (850, '滑板车', 89, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (851, '婴儿床垫', 89, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (852, '婴儿外出服', 90, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (853, '婴儿内衣', 90, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (854, '婴儿礼盒', 90, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (855, '婴儿鞋帽袜', 90, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (856, '安全防护', 90, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (857, '家居床品', 90, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (858, '睡袋/抱被', 90, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (859, '爬行垫', 90, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (860, '妈咪包/背婴带', 91, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (861, '产后塑身', 91, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (862, '文胸/内裤', 91, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (863, '防辐射服', 91, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (864, '孕妈装', 91, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (865, '孕期营养', 91, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (866, '孕妇护肤', 91, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (867, '待产护理', 91, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (868, '月子装', 91, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (869, '防溢乳垫', 91, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (870, '套装', 92, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (871, '上衣', 92, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (872, '裤子', 92, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (873, '裙子', 92, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (874, '内衣/家居服', 92, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (875, '羽绒服/棉服', 92, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (876, '亲子装', 92, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (877, '儿童配饰', 92, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (878, '礼服/演出服', 92, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (879, '运动鞋', 92, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (880, '皮鞋/帆布鞋', 92, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (881, '靴子', 92, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (882, '凉鞋', 92, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (883, '功能鞋', 92, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (884, '户外/运动服', 92, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (885, '提篮式', 93, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (886, '安全座椅', 93, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (887, '增高垫', 93, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (888, '钱包', 94, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (889, '手拿包', 94, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (890, '单肩包', 94, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (891, '双肩包', 94, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (892, '手提包', 94, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (893, '斜挎包', 94, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (894, '钥匙包', 94, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (895, '卡包/零钱包', 94, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (896, '男士钱包', 95, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (897, '男士手包', 95, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (898, '卡包名片夹', 95, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (899, '商务公文包', 95, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (900, '双肩包', 95, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (901, '单肩/斜挎包', 95, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (902, '钥匙包', 95, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (903, '电脑包', 96, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (904, '拉杆箱', 96, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (905, '旅行包', 96, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (906, '旅行配件', 96, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (907, '休闲运动包', 96, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (908, '拉杆包', 96, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (909, '登山包', 96, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (910, '妈咪包', 96, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (911, '书包', 96, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (912, '相机包', 96, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (913, '腰包/胸包', 96, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (914, '火机烟具', 97, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (915, '礼品文具', 97, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (916, '军刀军具', 97, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (917, '收藏品', 97, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (918, '工艺礼品', 97, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (919, '创意礼品', 97, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (920, '礼盒礼券', 97, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (921, '鲜花绿植', 97, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (922, '婚庆节庆', 97, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (923, '京东卡', 97, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (924, '美妆礼品', 97, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (925, '礼品定制', 97, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (926, '京东福卡', 97, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (927, '古董文玩', 97, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (928, '箱包', 98, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (929, '钱包', 98, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (930, '服饰', 98, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (931, '腰带', 98, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (932, '太阳镜/眼镜框', 98, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (933, '配件', 98, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (934, '鞋靴', 98, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (935, '饰品', 98, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (936, '名品腕表', 98, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (937, '高档化妆品', 98, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (938, '婚嫁首饰', 99, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (939, '婚纱摄影', 99, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (940, '婚纱礼服', 99, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (941, '婚庆服务', 99, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (942, '婚庆礼品/用品', 99, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (943, '婚宴', 99, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (944, '饼干蛋糕', 100, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (945, '糖果/巧克力', 100, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (946, '休闲零食', 100, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (947, '冲调饮品', 100, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (948, '粮油调味', 100, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (949, '牛奶', 100, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (950, '其他特产', 101, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (951, '新疆', 101, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (952, '北京', 101, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (953, '山西', 101, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (954, '内蒙古', 101, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (955, '福建', 101, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (956, '湖南', 101, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (957, '四川', 101, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (958, '云南', 101, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (959, '东北', 101, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (960, '休闲零食', 102, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (961, '坚果炒货', 102, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (962, '肉干肉脯', 102, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (963, '蜜饯果干', 102, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (964, '糖果/巧克力', 102, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (965, '饼干蛋糕', 102, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (966, '无糖食品', 102, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (967, '米面杂粮', 103, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (968, '食用油', 103, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (969, '调味品', 103, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (970, '南北干货', 103, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (971, '方便食品', 103, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (972, '有机食品', 103, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (973, '饮用水', 104, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (974, '饮料', 104, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (975, '牛奶乳品', 104, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (976, '咖啡/奶茶', 104, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (977, '冲饮谷物', 104, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (978, '蜂蜜/柚子茶', 104, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (979, '成人奶粉', 104, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (980, '月饼', 105, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (981, '大闸蟹', 105, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (982, '粽子', 105, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (983, '卡券', 105, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (984, '铁观音', 106, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (985, '普洱', 106, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (986, '龙井', 106, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (987, '绿茶', 106, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (988, '红茶', 106, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (989, '乌龙茶', 106, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (990, '花草茶', 106, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (991, '花果茶', 106, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (992, '养生茶', 106, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (993, '黑茶', 106, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (994, '白茶', 106, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (995, '其它茶', 106, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (996, '项链', 107, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (997, '手链/脚链', 107, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (998, '戒指', 107, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (999, '耳饰', 107, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1000, '毛衣链', 107, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1001, '发饰/发卡', 107, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1002, '胸针', 107, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1003, '饰品配件', 107, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1004, '婚庆饰品', 107, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1005, '黄金吊坠', 108, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1006, '黄金项链', 108, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1007, '黄金转运珠', 108, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1008, '黄金手镯/手链/脚链', 108, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1009, '黄金耳饰', 108, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1010, '黄金戒指', 108, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1011, 'K金吊坠', 109, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1012, 'K金项链', 109, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1013, 'K金手镯/手链/脚链', 109, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1014, 'K金戒指', 109, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1015, 'K金耳饰', 109, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1016, '投资金', 110, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1017, '投资银', 110, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1018, '投资收藏', 110, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1019, '银吊坠/项链', 111, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1020, '银手镯/手链/脚链', 111, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1021, '银戒指', 111, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1022, '银耳饰', 111, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1023, '足银手镯', 111, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1024, '宝宝银饰', 111, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1025, '裸钻', 112, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1026, '钻戒', 112, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1027, '钻石项链/吊坠', 112, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1028, '钻石耳饰', 112, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1029, '钻石手镯/手链', 112, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1030, '项链/吊坠', 113, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1031, '手镯/手串', 113, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1032, '戒指', 113, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1033, '耳饰', 113, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1034, '挂件/摆件/把件', 113, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1035, '玉石孤品', 113, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1036, '项链/吊坠', 114, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1037, '耳饰', 114, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1038, '手镯/手链/脚链', 114, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1039, '戒指', 114, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1040, '头饰/胸针', 114, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1041, '摆件/挂件', 114, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1042, '琥珀/蜜蜡', 115, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1043, '碧玺', 115, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1044, '红宝石/蓝宝石', 115, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1045, '坦桑石', 115, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1046, '珊瑚', 115, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1047, '祖母绿', 115, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1048, '葡萄石', 115, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1049, '其他天然宝石', 115, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1050, '项链/吊坠', 115, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1051, '耳饰', 115, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1052, '手镯/手链', 115, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1053, '戒指', 115, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1054, '铂金项链/吊坠', 116, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1055, '铂金手镯/手链/脚链', 116, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1056, '铂金戒指', 116, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1057, '铂金耳饰', 116, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1058, '小叶紫檀', 117, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1059, '黄花梨', 117, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1060, '沉香木', 117, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1061, '金丝楠', 117, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1062, '菩提', 117, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1063, '其他', 117, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1064, '橄榄核/核桃', 117, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1065, '檀香', 117, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1066, '珍珠项链', 118, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1067, '珍珠吊坠', 118, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1068, '珍珠耳饰', 118, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1069, '珍珠手链', 118, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1070, '珍珠戒指', 118, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1071, '珍珠胸针', 118, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1072, '机油', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1073, '正时皮带', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1074, '添加剂', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1075, '汽车喇叭', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1076, '防冻液', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1077, '汽车玻璃', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1078, '滤清器', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1079, '火花塞', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1080, '减震器', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1081, '柴机油/辅助油', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1082, '雨刷', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1083, '车灯', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1084, '后视镜', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1085, '轮胎', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1086, '轮毂', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1087, '刹车片/盘', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1088, '维修配件', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1089, '蓄电池', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1090, '底盘装甲/护板', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1091, '贴膜', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1092, '汽修工具', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1093, '改装配件', 119, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1094, '导航仪', 120, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1095, '安全预警仪', 120, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1096, '行车记录仪', 120, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1097, '倒车雷达', 120, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1098, '蓝牙设备', 120, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1099, '车载影音', 120, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1100, '净化器', 120, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1101, '电源', 120, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1102, '智能驾驶', 120, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1103, '车载电台', 120, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1104, '车载电器配件', 120, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1105, '吸尘器', 120, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1106, '智能车机', 120, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1107, '冰箱', 120, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1108, '汽车音响', 120, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1109, '车载生活电器', 120, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1110, '车蜡', 121, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1111, '补漆笔', 121, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1112, '玻璃水', 121, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1113, '清洁剂', 121, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1114, '洗车工具', 121, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1115, '镀晶镀膜', 121, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1116, '打蜡机', 121, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1117, '洗车配件', 121, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1118, '洗车机', 121, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1119, '洗车水枪', 121, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1120, '毛巾掸子', 121, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1121, '脚垫', 122, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1122, '座垫', 122, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1123, '座套', 122, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1124, '后备箱垫', 122, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1125, '头枕腰靠', 122, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1126, '方向盘套', 122, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1127, '香水', 122, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1128, '空气净化', 122, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1129, '挂件摆件', 122, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1130, '功能小件', 122, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1131, '车身装饰件', 122, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1132, '车衣', 122, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1133, '安全座椅', 123, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1134, '胎压监测', 123, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1135, '防盗设备', 123, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1136, '应急救援', 123, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1137, '保温箱', 123, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1138, '地锁', 123, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1139, '摩托车', 123, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1140, '充气泵', 123, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1141, '储物箱', 123, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1142, '自驾野营', 123, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1143, '摩托车装备', 123, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1144, '清洗美容', 124, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1145, '功能升级', 124, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1146, '保养维修', 124, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1147, '油卡充值', 124, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1148, '车险', 124, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1149, '加油卡', 124, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1150, 'ETC', 124, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1151, '驾驶培训', 124, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1152, '赛事服装', 125, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1153, '赛事用品', 125, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1154, '制动系统', 125, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1155, '悬挂系统', 125, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1156, '进气系统', 125, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1157, '排气系统', 125, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1158, '电子管理', 125, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1159, '车身强化', 125, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1160, '赛事座椅', 125, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1161, '跑步鞋', 126, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1162, '休闲鞋', 126, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1163, '篮球鞋', 126, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1164, '板鞋', 126, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1165, '帆布鞋', 126, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1166, '足球鞋', 126, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1167, '乒羽网鞋', 126, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1168, '专项运动鞋', 126, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1169, '训练鞋', 126, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1170, '拖鞋', 126, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1171, '运动包', 126, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1172, '羽绒服', 127, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1173, '棉服', 127, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1174, '运动裤', 127, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1175, '夹克/风衣', 127, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1176, '卫衣/套头衫', 127, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1177, 'T恤', 127, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1178, '套装', 127, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1179, '乒羽网服', 127, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1180, '健身服', 127, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1181, '运动背心', 127, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1182, '毛衫/线衫', 127, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1183, '运动配饰', 127, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1184, '折叠车', 128, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1185, '山地车/公路车', 128, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1186, '电动车', 128, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1187, '其他整车', 128, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1188, '骑行服', 128, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1189, '骑行装备', 128, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1190, '平衡车', 128, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1191, '鱼竿鱼线', 129, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1192, '浮漂鱼饵', 129, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1193, '钓鱼桌椅', 129, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1194, '钓鱼配件', 129, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1195, '钓箱鱼包', 129, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1196, '其它', 129, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1197, '泳镜', 130, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1198, '泳帽', 130, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1199, '游泳包防水包', 130, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1200, '女士泳衣', 130, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1201, '男士泳衣', 130, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1202, '比基尼', 130, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1203, '其它', 130, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1204, '冲锋衣裤', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1205, '速干衣裤', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1206, '滑雪服', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1207, '羽绒服/棉服', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1208, '休闲衣裤', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1209, '抓绒衣裤', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1210, '软壳衣裤', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1211, 'T恤', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1212, '户外风衣', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1213, '功能内衣', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1214, '军迷服饰', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1215, '登山鞋', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1216, '雪地靴', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1217, '徒步鞋', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1218, '越野跑鞋', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1219, '休闲鞋', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1220, '工装鞋', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1221, '溯溪鞋', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1222, '沙滩/凉拖', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1223, '户外袜', 131, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1224, '帐篷/垫子', 132, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1225, '睡袋/吊床', 132, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1226, '登山攀岩', 132, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1227, '户外配饰', 132, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1228, '背包', 132, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1229, '户外照明', 132, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1230, '户外仪表', 132, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1231, '户外工具', 132, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1232, '望远镜', 132, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1233, '旅游用品', 132, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1234, '便携桌椅床', 132, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1235, '野餐烧烤', 132, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1236, '军迷用品', 132, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1237, '救援装备', 132, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1238, '滑雪装备', 132, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1239, '极限户外', 132, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1240, '冲浪潜水', 132, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1241, '综合训练器', 133, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1242, '其他大型器械', 133, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1243, '哑铃', 133, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1244, '仰卧板/收腹机', 133, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1245, '其他中小型器材', 133, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1246, '瑜伽舞蹈', 133, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1247, '甩脂机', 133, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1248, '踏步机', 133, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1249, '武术搏击', 133, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1250, '健身车/动感单车', 133, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1251, '跑步机', 133, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1252, '运动护具', 133, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1253, '羽毛球', 134, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1254, '乒乓球', 134, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1255, '篮球', 134, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1256, '足球', 134, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1257, '网球', 134, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1258, '排球', 134, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1259, '高尔夫', 134, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1260, '台球', 134, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1261, '棋牌麻将', 134, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1262, '轮滑滑板', 134, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1263, '其他', 134, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1264, '0-6个月', 135, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1265, '6-12个月', 135, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1266, '1-3岁', 135, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1267, '3-6岁', 135, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1268, '6-14岁', 135, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1269, '14岁以上', 135, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1270, '遥控车', 136, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1271, '遥控飞机', 136, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1272, '遥控船', 136, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1273, '机器人', 136, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1274, '轨道/助力', 136, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1275, '毛绒/布艺', 137, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1276, '靠垫/抱枕', 137, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1277, '芭比娃娃', 138, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1278, '卡通娃娃', 138, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1279, '智能娃娃', 138, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1280, '仿真模型', 139, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1281, '拼插模型', 139, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1282, '收藏爱好', 139, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1283, '炫舞毯', 140, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1284, '爬行垫/毯', 140, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1285, '户外玩具', 140, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1286, '戏水玩具', 140, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1287, '电影周边', 141, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1288, '卡通周边', 141, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1289, '网游周边', 141, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1290, '摇铃/床铃', 142, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1291, '健身架', 142, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1292, '早教启智', 142, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1293, '拖拉玩具', 142, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1294, '积木', 143, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1295, '拼图', 143, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1296, '磁力棒', 143, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1297, '立体拼插', 143, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1298, '手工彩泥', 144, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1299, '绘画工具', 144, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1300, '情景玩具', 144, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1301, '减压玩具', 145, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1302, '创意玩具', 145, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1303, '钢琴', 146, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1304, '电子琴/电钢琴', 146, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1305, '吉他/尤克里里', 146, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1306, '打击乐器', 146, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1307, '西洋管弦', 146, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1308, '民族管弦乐器', 146, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1309, '乐器配件', 146, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1310, '电脑音乐', 146, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1311, '工艺礼品乐器', 146, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1312, '口琴/口风琴/竖笛', 146, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1313, '手风琴', 146, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1314, '双色球', 147, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1315, '大乐透', 147, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1316, '福彩3D', 147, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1317, '排列三', 147, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1318, '排列五', 147, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1319, '七星彩', 147, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1320, '七乐彩', 147, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1321, '竞彩足球', 147, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1322, '竞彩篮球', 147, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1323, '新时时彩', 147, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1324, '国内机票', 148, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1325, '国内酒店', 149, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1326, '酒店团购', 149, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1327, '度假', 150, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1328, '景点', 150, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1329, '租车', 150, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1330, '火车票', 150, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1331, '旅游团购', 150, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1332, '手机充值', 151, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1333, '游戏点卡', 152, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1334, 'QQ充值', 152, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1335, '电影票', 153, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1336, '演唱会', 153, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1337, '话剧歌剧', 153, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1338, '音乐会', 153, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1339, '体育赛事', 153, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1340, '舞蹈芭蕾', 153, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1341, '戏曲综艺', 153, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1342, '东北', 154, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1343, '华北', 154, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1344, '西北', 154, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1345, '华中', 154, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1346, '华东', 154, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1347, '华南', 154, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1348, '西南', 154, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1349, '苹果', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1350, '橙子', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1351, '奇异果/猕猴桃', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1352, '车厘子/樱桃', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1353, '芒果', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1354, '蓝莓', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1355, '火龙果', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1356, '葡萄/提子', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1357, '柚子', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1358, '香蕉', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1359, '牛油果', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1360, '梨', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1361, '菠萝/凤梨', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1362, '桔/橘', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1363, '柠檬', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1364, '草莓', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1365, '桃/李/杏', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1366, '更多水果', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1367, '水果礼盒/券', 155, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1368, '牛肉', 156, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1369, '羊肉', 156, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1370, '猪肉', 156, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1371, '内脏类', 156, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1372, '鱼类', 157, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1373, '虾类', 157, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1374, '蟹类', 157, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1375, '贝类', 157, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1376, '海参', 157, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1377, '海产干货', 157, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1378, '其他水产', 157, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1379, '海产礼盒', 157, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1380, '鸡肉', 158, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1381, '鸭肉', 158, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1382, '蛋类', 158, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1383, '其他禽类', 158, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1384, '水饺/馄饨', 159, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1385, '汤圆/元宵', 159, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1386, '面点', 159, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1387, '火锅丸串', 159, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1388, '速冻半成品', 159, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1389, '奶酪黄油', 159, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1390, '熟食', 160, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1391, '腊肠/腊肉', 160, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1392, '火腿', 160, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1393, '糕点', 160, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1394, '礼品卡券', 160, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1395, '冷藏果蔬汁', 161, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1396, '冰激凌', 161, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1397, '其他', 161, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1398, '叶菜类', 162, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1399, '茄果瓜类', 162, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1400, '根茎类', 162, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1401, '鲜菌菇', 162, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1402, '葱姜蒜椒', 162, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1403, '半加工蔬菜', 162, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1404, '微型车', 163, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1405, '小型车', 163, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1406, '紧凑型车', 163, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1407, '中型车', 163, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1408, '中大型车', 163, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1409, '豪华车', 163, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1410, 'MPV', 163, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1411, 'SUV', 163, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1412, '跑车', 163, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1413, '微型车（二手）', 164, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1414, '小型车（二手）', 164, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1415, '紧凑型车（二手）', 164, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1416, '中型车（二手）', 164, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1417, '中大型车（二手）', 164, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1418, '豪华车（二手）', 164, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1419, 'MPV（二手）', 164, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1420, 'SUV（二手）', 164, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1421, '跑车（二手）', 164, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1422, '皮卡（二手）', 164, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1423, '面包车（二手）', 164, 3, 1, 0, NULL, NULL, 0);
INSERT INTO `pms_category` VALUES (1433, '移动', 35, 3, 1, 5, NULL, NULL, 0);

-- ----------------------------
-- Table structure for pms_category_brand_relation
-- ----------------------------
DROP TABLE IF EXISTS `pms_category_brand_relation`;
CREATE TABLE `pms_category_brand_relation`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `brand_id` bigint(20) NULL DEFAULT NULL COMMENT '品牌id',
  `catelog_id` bigint(20) NULL DEFAULT NULL COMMENT '分类id',
  `brand_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `catelog_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '品牌分类关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pms_category_brand_relation
-- ----------------------------
INSERT INTO `pms_category_brand_relation` VALUES (17, 10, 449, '小米', '笔记本');
INSERT INTO `pms_category_brand_relation` VALUES (18, 10, 225, '小米', '手机');
INSERT INTO `pms_category_brand_relation` VALUES (19, 10, 231, '小米', '移动电源');
INSERT INTO `pms_category_brand_relation` VALUES (20, 10, 233, '小米', '蓝牙耳机');
INSERT INTO `pms_category_brand_relation` VALUES (21, 10, 250, '小米', '平板电视');
INSERT INTO `pms_category_brand_relation` VALUES (22, 10, 449, '小米', '笔记本');
INSERT INTO `pms_category_brand_relation` VALUES (23, 11, 225, 'oppo', '手机');
INSERT INTO `pms_category_brand_relation` VALUES (24, 11, 227, 'oppo', '合约机');
INSERT INTO `pms_category_brand_relation` VALUES (25, 12, 225, 'Apple', '手机');
INSERT INTO `pms_category_brand_relation` VALUES (26, 12, 243, 'Apple', 'iPhone 配件');
INSERT INTO `pms_category_brand_relation` VALUES (27, 12, 366, 'Apple', '智能手表');
INSERT INTO `pms_category_brand_relation` VALUES (32, 9, 225, '华为', '手机');
INSERT INTO `pms_category_brand_relation` VALUES (33, 9, 250, '华为', '平板电视');
INSERT INTO `pms_category_brand_relation` VALUES (34, 9, 449, '华为', '笔记本');
INSERT INTO `pms_category_brand_relation` VALUES (35, 9, 233, '华为', '蓝牙耳机');
INSERT INTO `pms_category_brand_relation` VALUES (36, 9, 234, '华为', '充电器/数据线');

-- ----------------------------
-- Table structure for pms_comment_replay
-- ----------------------------
DROP TABLE IF EXISTS `pms_comment_replay`;
CREATE TABLE `pms_comment_replay`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `comment_id` bigint(20) NULL DEFAULT NULL COMMENT '评论id',
  `reply_id` bigint(20) NULL DEFAULT NULL COMMENT '回复id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品评价回复关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pms_comment_replay
-- ----------------------------

-- ----------------------------
-- Table structure for pms_product_attr_value
-- ----------------------------
DROP TABLE IF EXISTS `pms_product_attr_value`;
CREATE TABLE `pms_product_attr_value`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `spu_id` bigint(20) NULL DEFAULT NULL COMMENT '商品id',
  `attr_id` bigint(20) NULL DEFAULT NULL COMMENT '属性id',
  `attr_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性名',
  `attr_value` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性值',
  `attr_sort` int(11) NULL DEFAULT NULL COMMENT '顺序',
  `quick_show` tinyint(4) NULL DEFAULT NULL COMMENT '快速展示【是否展示在介绍上；0-否 1-是】',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 68 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'spu属性值' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pms_product_attr_value
-- ----------------------------
INSERT INTO `pms_product_attr_value` VALUES (55, 13, 7, '入网型号', 'A2217', NULL, 0);
INSERT INTO `pms_product_attr_value` VALUES (56, 13, 8, '上市年份', '2018', NULL, 0);
INSERT INTO `pms_product_attr_value` VALUES (57, 13, 13, '机身长度（mm）', '158.3', NULL, 0);
INSERT INTO `pms_product_attr_value` VALUES (58, 13, 14, '机身材质工艺', '以官网信息为准', NULL, 0);
INSERT INTO `pms_product_attr_value` VALUES (59, 13, 15, 'CPU品牌', '以官网信息为准', NULL, 1);
INSERT INTO `pms_product_attr_value` VALUES (60, 13, 16, 'CPU型号', 'A13仿生', NULL, 1);
INSERT INTO `pms_product_attr_value` VALUES (61, 11, 7, '入网型号', 'LIO-AL00', NULL, 1);
INSERT INTO `pms_product_attr_value` VALUES (62, 11, 8, '上市年份', '2019', NULL, 0);
INSERT INTO `pms_product_attr_value` VALUES (63, 11, 11, '机身颜色', '黑色', NULL, 1);
INSERT INTO `pms_product_attr_value` VALUES (64, 11, 13, '机身长度（mm）', '158.3', NULL, 1);
INSERT INTO `pms_product_attr_value` VALUES (65, 11, 14, '机身材质工艺', '玻璃;陶瓷', NULL, 0);
INSERT INTO `pms_product_attr_value` VALUES (66, 11, 15, 'CPU品牌', '海思（Hisilicon）', NULL, 1);
INSERT INTO `pms_product_attr_value` VALUES (67, 11, 16, 'CPU型号', 'HUAWEI Kirin 970', NULL, 1);

-- ----------------------------
-- Table structure for pms_sku_images
-- ----------------------------
DROP TABLE IF EXISTS `pms_sku_images`;
CREATE TABLE `pms_sku_images`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT 'sku_id',
  `img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片地址',
  `img_sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `default_img` int(11) NULL DEFAULT NULL COMMENT '默认图[0 - 不是默认图，1 - 是默认图]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 111 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'sku图片' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pms_sku_images
-- ----------------------------
INSERT INTO `pms_sku_images` VALUES (1, 1, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/ef2691e5-de1a-4ca3-827d-a60f39fbda93_0d40c24b264aa511.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (2, 1, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/20f83b0c-86ba-4a64-808a-f2ace190ea2c_1f15cdbcf9e1273c.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (3, 1, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (4, 1, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (5, 1, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (6, 1, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (7, 1, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (8, 1, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (9, 1, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/d12516dd-43cf-4ace-8dc9-14618d2d75e4_919c850652e98031.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (10, 1, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/60e65a44-f943-4ed5-87c8-8cf90f403018_d511faab82abb34b.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (11, 2, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/ef2691e5-de1a-4ca3-827d-a60f39fbda93_0d40c24b264aa511.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (12, 2, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/20f83b0c-86ba-4a64-808a-f2ace190ea2c_1f15cdbcf9e1273c.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (13, 2, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (14, 2, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (15, 2, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (16, 2, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (17, 2, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (18, 2, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (19, 2, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (20, 2, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/60e65a44-f943-4ed5-87c8-8cf90f403018_d511faab82abb34b.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (21, 3, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/ef2691e5-de1a-4ca3-827d-a60f39fbda93_0d40c24b264aa511.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (22, 3, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (23, 3, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (24, 3, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (25, 3, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (26, 3, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/099f297e-ddea-4fb5-87c7-78cd88e500c0_28f296629cca865e.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (27, 3, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (28, 3, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/7aa1019e-4e01-49dd-8ac4-7d2794d0b1a8_335b2c690e43a8f8.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (29, 3, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (30, 3, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (31, 4, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/ef2691e5-de1a-4ca3-827d-a60f39fbda93_0d40c24b264aa511.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (32, 4, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (33, 4, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (34, 4, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (35, 4, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (36, 4, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/099f297e-ddea-4fb5-87c7-78cd88e500c0_28f296629cca865e.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (37, 4, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (38, 4, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/7aa1019e-4e01-49dd-8ac4-7d2794d0b1a8_335b2c690e43a8f8.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (39, 4, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (40, 4, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (41, 5, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/ef2691e5-de1a-4ca3-827d-a60f39fbda93_0d40c24b264aa511.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (42, 5, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/20f83b0c-86ba-4a64-808a-f2ace190ea2c_1f15cdbcf9e1273c.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (43, 5, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (44, 5, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/9a6ba5c0-0a31-4364-8759-012bdfbf5ad3_3c24f9cd69534030.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (45, 5, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/a73ef55a-79b4-41d9-8eb6-760c8b5a33dc_23d9fbb256ea5d4a.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (46, 5, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (47, 5, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (48, 5, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (49, 5, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (50, 5, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (51, 6, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/ef2691e5-de1a-4ca3-827d-a60f39fbda93_0d40c24b264aa511.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (52, 6, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (53, 6, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (54, 6, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/9a6ba5c0-0a31-4364-8759-012bdfbf5ad3_3c24f9cd69534030.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (55, 6, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/a73ef55a-79b4-41d9-8eb6-760c8b5a33dc_23d9fbb256ea5d4a.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (56, 6, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (57, 6, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (58, 6, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (59, 6, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (60, 6, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (61, 7, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (62, 7, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (63, 7, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (64, 7, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (65, 7, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (66, 7, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (67, 7, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/38492c9f-b0e0-4cba-87a9-77cb6189ea73_73ab4d2e818d2211.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (68, 7, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (69, 7, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/d12516dd-43cf-4ace-8dc9-14618d2d75e4_919c850652e98031.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (70, 7, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/60e65a44-f943-4ed5-87c8-8cf90f403018_d511faab82abb34b.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (71, 8, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (72, 8, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (73, 8, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (74, 8, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (75, 8, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (76, 8, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (77, 8, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/38492c9f-b0e0-4cba-87a9-77cb6189ea73_73ab4d2e818d2211.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (78, 8, '', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (79, 8, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/d12516dd-43cf-4ace-8dc9-14618d2d75e4_919c850652e98031.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (80, 8, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/60e65a44-f943-4ed5-87c8-8cf90f403018_d511faab82abb34b.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (81, 9, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/462ef293-2b8b-4c53-85f2-1726e14dc976_23cd65077f12f7f5.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (82, 9, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/bc4825d6-2a6c-43f8-8d75-5f35b77b9514_a2c208410ae84d1f.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (83, 9, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/f968b6ac-2fca-4440-842f-8db8b76478f0_b8494bf281991f94.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (84, 10, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/462ef293-2b8b-4c53-85f2-1726e14dc976_23cd65077f12f7f5.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (85, 10, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/bc4825d6-2a6c-43f8-8d75-5f35b77b9514_a2c208410ae84d1f.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (86, 10, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/f968b6ac-2fca-4440-842f-8db8b76478f0_b8494bf281991f94.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (87, 11, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/462ef293-2b8b-4c53-85f2-1726e14dc976_23cd65077f12f7f5.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (88, 11, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/bc4825d6-2a6c-43f8-8d75-5f35b77b9514_a2c208410ae84d1f.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (89, 11, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/f968b6ac-2fca-4440-842f-8db8b76478f0_b8494bf281991f94.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (90, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/462ef293-2b8b-4c53-85f2-1726e14dc976_23cd65077f12f7f5.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (91, 13, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/462ef293-2b8b-4c53-85f2-1726e14dc976_23cd65077f12f7f5.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (92, 14, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/462ef293-2b8b-4c53-85f2-1726e14dc976_23cd65077f12f7f5.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (93, 15, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (94, 16, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (95, 17, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (96, 18, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (97, 19, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (98, 20, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (99, 21, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/1756fa6d-1934-4d5c-8faf-84b3d882fe53_5b5e74d0978360a1.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (100, 21, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/e21777b3-18ac-4580-819e-497c3aa25e4f_6a1b2703a9ed8737.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (101, 21, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/2419c5cf-a641-4ec1-8f94-64981dc207f6_63e862164165f483.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (102, 22, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/1756fa6d-1934-4d5c-8faf-84b3d882fe53_5b5e74d0978360a1.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (103, 22, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/e21777b3-18ac-4580-819e-497c3aa25e4f_6a1b2703a9ed8737.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (104, 22, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/2419c5cf-a641-4ec1-8f94-64981dc207f6_63e862164165f483.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (105, 23, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/1756fa6d-1934-4d5c-8faf-84b3d882fe53_5b5e74d0978360a1.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (106, 23, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/e21777b3-18ac-4580-819e-497c3aa25e4f_6a1b2703a9ed8737.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (107, 23, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/2419c5cf-a641-4ec1-8f94-64981dc207f6_63e862164165f483.jpg', NULL, 0);
INSERT INTO `pms_sku_images` VALUES (108, 24, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (109, 25, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', NULL, 1);
INSERT INTO `pms_sku_images` VALUES (110, 26, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', NULL, 1);

-- ----------------------------
-- Table structure for pms_sku_info
-- ----------------------------
DROP TABLE IF EXISTS `pms_sku_info`;
CREATE TABLE `pms_sku_info`  (
  `sku_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'skuId',
  `spu_id` bigint(20) NULL DEFAULT NULL COMMENT 'spuId',
  `sku_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'sku名称',
  `sku_desc` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'sku介绍描述',
  `catalog_id` bigint(20) NULL DEFAULT NULL COMMENT '所属分类id',
  `brand_id` bigint(20) NULL DEFAULT NULL COMMENT '品牌id',
  `sku_default_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认图片',
  `sku_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `sku_subtitle` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '副标题',
  `price` decimal(18, 4) NULL DEFAULT NULL COMMENT '价格',
  `sale_count` bigint(20) NULL DEFAULT NULL COMMENT '销量',
  PRIMARY KEY (`sku_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'sku信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pms_sku_info
-- ----------------------------
INSERT INTO `pms_sku_info` VALUES (1, 11, '华为 HUAWEI Mate 30 Pro 星河银 8GB+256GB', NULL, 225, 9, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/60e65a44-f943-4ed5-87c8-8cf90f403018_d511faab82abb34b.jpg', '华为 HUAWEI Mate 30 Pro 星河银 8GB+256GB麒麟990旗舰芯片OLED环幕屏双4000万徕卡电影四摄4G全网通手机', '【现货抢购！享白条12期免息！】麒麟990，OLED环幕屏双4000万徕卡电影四摄；Mate30系列享12期免息》', 6299.0000, 0);
INSERT INTO `pms_sku_info` VALUES (2, 11, '华为 HUAWEI Mate 30 Pro 星河银 8GB+128GB', NULL, 225, 9, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/ef2691e5-de1a-4ca3-827d-a60f39fbda93_0d40c24b264aa511.jpg', '华为 HUAWEI Mate 30 Pro 星河银 8GB+128GB麒麟990旗舰芯片OLED环幕屏双4000万徕卡电影四摄4G全网通手机', '【现货抢购！享白条12期免息！】麒麟990，OLED环幕屏双4000万徕卡电影四摄；Mate30系列享12期免息》', 5799.0000, 0);
INSERT INTO `pms_sku_info` VALUES (3, 11, '华为 HUAWEI Mate 30 Pro 亮黑色 8GB+256GB', NULL, 225, 9, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/ef2691e5-de1a-4ca3-827d-a60f39fbda93_0d40c24b264aa511.jpg', '华为 HUAWEI Mate 30 Pro 亮黑色 8GB+256GB麒麟990旗舰芯片OLED环幕屏双4000万徕卡电影四摄4G全网通手机', '【现货抢购！享白条12期免息！】麒麟990，OLED环幕屏双4000万徕卡电影四摄；Mate30系列享12期免息》', 6299.0000, 0);
INSERT INTO `pms_sku_info` VALUES (4, 11, '华为 HUAWEI Mate 30 Pro 亮黑色 8GB+128GB', NULL, 225, 9, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/ef2691e5-de1a-4ca3-827d-a60f39fbda93_0d40c24b264aa511.jpg', '华为 HUAWEI Mate 30 Pro 亮黑色 8GB+128GB麒麟990旗舰芯片OLED环幕屏双4000万徕卡电影四摄4G全网通手机', '【现货抢购！享白条12期免息！】麒麟990，OLED环幕屏双4000万徕卡电影四摄；Mate30系列享12期免息》', 5799.0000, 0);
INSERT INTO `pms_sku_info` VALUES (5, 11, '华为 HUAWEI Mate 30 Pro 翡冷翠 8GB+256GB', NULL, 225, 9, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/ef2691e5-de1a-4ca3-827d-a60f39fbda93_0d40c24b264aa511.jpg', '华为 HUAWEI Mate 30 Pro 翡冷翠 8GB+256GB麒麟990旗舰芯片OLED环幕屏双4000万徕卡电影四摄4G全网通手机', '【现货抢购！享白条12期免息！】麒麟990，OLED环幕屏双4000万徕卡电影四摄；Mate30系列享12期免息》', 6299.0000, 0);
INSERT INTO `pms_sku_info` VALUES (6, 11, '华为 HUAWEI Mate 30 Pro 翡冷翠 8GB+128GB', NULL, 225, 9, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/ef2691e5-de1a-4ca3-827d-a60f39fbda93_0d40c24b264aa511.jpg', '华为 HUAWEI Mate 30 Pro 翡冷翠 8GB+128GB麒麟990旗舰芯片OLED环幕屏双4000万徕卡电影四摄4G全网通手机', '【现货抢购！享白条12期免息！】麒麟990，OLED环幕屏双4000万徕卡电影四摄；Mate30系列享12期免息》', 5799.0000, 0);
INSERT INTO `pms_sku_info` VALUES (7, 11, '华为 HUAWEI Mate 30 Pro 罗兰紫 8GB+256GB', NULL, 225, 9, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/60e65a44-f943-4ed5-87c8-8cf90f403018_d511faab82abb34b.jpg', '华为 HUAWEI Mate 30 Pro 罗兰紫 8GB+256GB麒麟990旗舰芯片OLED环幕屏双4000万徕卡电影四摄4G全网通手机', '【现货抢购！享白条12期免息！】麒麟990，OLED环幕屏双4000万徕卡电影四摄；Mate30系列享12期免息》', 6299.0000, 0);
INSERT INTO `pms_sku_info` VALUES (8, 11, '华为 HUAWEI Mate 30 Pro 罗兰紫 8GB+128GB', NULL, 225, 9, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/60e65a44-f943-4ed5-87c8-8cf90f403018_d511faab82abb34b.jpg', '华为 HUAWEI Mate 30 Pro 罗兰紫 8GB+128GB麒麟990旗舰芯片OLED环幕屏双4000万徕卡电影四摄4G全网通手机', '【现货抢购！享白条12期免息！】麒麟990，OLED环幕屏双4000万徕卡电影四摄；Mate30系列享12期免息》', 5799.0000, 0);
INSERT INTO `pms_sku_info` VALUES (9, 13, ' Apple iPhone 11 (A2223)  黑色 128GB ', NULL, 225, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/bc4825d6-2a6c-43f8-8d75-5f35b77b9514_a2c208410ae84d1f.jpg', ' Apple iPhone 11 (A2223)  黑色 128GB 移动联通电信4G手机 双卡双待 最后几件优惠', 'iPhoneXS系列性能强劲，样样出色，现特惠来袭，低至5399元！详情请点击！', 5999.0000, 0);
INSERT INTO `pms_sku_info` VALUES (10, 13, ' Apple iPhone 11 (A2223)  黑色 256GB', NULL, 225, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/bc4825d6-2a6c-43f8-8d75-5f35b77b9514_a2c208410ae84d1f.jpg', ' Apple iPhone 11 (A2223)  黑色 256GB 移动联通电信4G手机 双卡双待', 'iPhoneXS系列性能强劲，样样出色，现特惠来袭，低至5399元！详情请点击！', 6799.0000, 0);
INSERT INTO `pms_sku_info` VALUES (11, 13, ' Apple iPhone 11 (A2223)  黑色 64GB', NULL, 225, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/bc4825d6-2a6c-43f8-8d75-5f35b77b9514_a2c208410ae84d1f.jpg', ' Apple iPhone 11 (A2223)  黑色 64GB  移动联通电信4G手机 双卡双待', 'iPhoneXS系列性能强劲，样样出色，现特惠来袭，低至5399元！详情请点击！', 5499.0000, 0);
INSERT INTO `pms_sku_info` VALUES (12, 13, ' Apple iPhone 11 (A2223)  白色 128GB ', NULL, 225, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/462ef293-2b8b-4c53-85f2-1726e14dc976_23cd65077f12f7f5.jpg', ' Apple iPhone 11 (A2223)  白色 128GB  移动联通电信4G手机 双卡双待', 'iPhoneXS系列性能强劲，样样出色，现特惠来袭，低至5399元！详情请点击！', 5999.0000, 0);
INSERT INTO `pms_sku_info` VALUES (13, 13, ' Apple iPhone 11 (A2223)  白色 256GB', NULL, 225, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/462ef293-2b8b-4c53-85f2-1726e14dc976_23cd65077f12f7f5.jpg', ' Apple iPhone 11 (A2223)  白色 256GB  移动联通电信4G手机 双卡双待', 'iPhoneXS系列性能强劲，样样出色，现特惠来袭，低至5399元！详情请点击！', 6799.0000, 0);
INSERT INTO `pms_sku_info` VALUES (14, 13, ' Apple iPhone 11 (A2223)  白色 64GB', NULL, 225, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/462ef293-2b8b-4c53-85f2-1726e14dc976_23cd65077f12f7f5.jpg', ' Apple iPhone 11 (A2223)  白色 64GB  移动联通电信4G手机 双卡双待', 'iPhoneXS系列性能强劲，样样出色，现特惠来袭，低至5399元！详情请点击！', 5499.0000, 0);
INSERT INTO `pms_sku_info` VALUES (15, 13, ' Apple iPhone 11 (A2223)  绿色 128GB ', NULL, 225, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', ' Apple iPhone 11 (A2223)  绿色 128GB  移动联通电信4G手机 双卡双待', 'iPhoneXS系列性能强劲，样样出色，现特惠来袭，低至5399元！详情请点击！', 5999.0000, 0);
INSERT INTO `pms_sku_info` VALUES (16, 13, ' Apple iPhone 11 (A2223)  绿色 256GB', NULL, 225, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', ' Apple iPhone 11 (A2223)  绿色 256GB  移动联通电信4G手机 双卡双待', 'iPhoneXS系列性能强劲，样样出色，现特惠来袭，低至5399元！详情请点击！', 6799.0000, 0);
INSERT INTO `pms_sku_info` VALUES (17, 13, ' Apple iPhone 11 (A2223)  绿色 64GB', NULL, 225, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', ' Apple iPhone 11 (A2223)  绿色 64GB  移动联通电信4G手机 双卡双待', 'iPhoneXS系列性能强劲，样样出色，现特惠来袭，低至5399元！详情请点击！', 5499.0000, 0);
INSERT INTO `pms_sku_info` VALUES (18, 13, ' Apple iPhone 11 (A2223)  黄色 128GB ', NULL, 225, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', ' Apple iPhone 11 (A2223)  黄色 128GB  移动联通电信4G手机 双卡双待', 'iPhoneXS系列性能强劲，样样出色，现特惠来袭，低至5399元！详情请点击！', 5999.0000, 0);
INSERT INTO `pms_sku_info` VALUES (19, 13, ' Apple iPhone 11 (A2223)  黄色 256GB', NULL, 225, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', ' Apple iPhone 11 (A2223)  黄色 256GB  移动联通电信4G手机 双卡双待', 'iPhoneXS系列性能强劲，样样出色，现特惠来袭，低至5399元！详情请点击！', 6799.0000, 0);
INSERT INTO `pms_sku_info` VALUES (20, 13, ' Apple iPhone 11 (A2223)  黄色 64GB', NULL, 225, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', ' Apple iPhone 11 (A2223)  黄色 64GB  移动联通电信4G手机 双卡双待', 'iPhoneXS系列性能强劲，样样出色，现特惠来袭，低至5399元！详情请点击！', 5499.0000, 0);
INSERT INTO `pms_sku_info` VALUES (21, 13, ' Apple iPhone 11 (A2223)  红色 128GB ', NULL, 225, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/e21777b3-18ac-4580-819e-497c3aa25e4f_6a1b2703a9ed8737.jpg', ' Apple iPhone 11 (A2223)  红色 128GB  移动联通电信4G手机 双卡双待', 'iPhoneXS系列性能强劲，样样出色，现特惠来袭，低至5399元！详情请点击！', 5999.0000, 0);
INSERT INTO `pms_sku_info` VALUES (22, 13, ' Apple iPhone 11 (A2223)  红色 256GB', NULL, 225, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/e21777b3-18ac-4580-819e-497c3aa25e4f_6a1b2703a9ed8737.jpg', ' Apple iPhone 11 (A2223)  红色 256GB  移动联通电信4G手机 双卡双待', 'iPhoneXS系列性能强劲，样样出色，现特惠来袭，低至5399元！详情请点击！', 6799.0000, 0);
INSERT INTO `pms_sku_info` VALUES (23, 13, ' Apple iPhone 11 (A2223)  红色 64GB', NULL, 225, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/e21777b3-18ac-4580-819e-497c3aa25e4f_6a1b2703a9ed8737.jpg', ' Apple iPhone 11 (A2223)  红色 64GB  移动联通电信4G手机 双卡双待', 'iPhoneXS系列性能强劲，样样出色，现特惠来袭，低至5399元！详情请点击！', 5499.0000, 0);
INSERT INTO `pms_sku_info` VALUES (24, 13, ' Apple iPhone 11 (A2223)  紫色 128GB ', NULL, 225, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', ' Apple iPhone 11 (A2223)  紫色 128GB  移动联通电信4G手机 双卡双待', 'iPhoneXS系列性能强劲，样样出色，现特惠来袭，低至5399元！详情请点击！', 5999.0000, 0);
INSERT INTO `pms_sku_info` VALUES (25, 13, ' Apple iPhone 11 (A2223)  紫色 256GB', NULL, 225, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', ' Apple iPhone 11 (A2223)  紫色 256GB  移动联通电信4G手机 双卡双待', 'iPhoneXS系列性能强劲，样样出色，现特惠来袭，低至5399元！详情请点击！', 6799.0000, 0);
INSERT INTO `pms_sku_info` VALUES (26, 13, ' Apple iPhone 11 (A2223)  紫色 64GB', NULL, 225, 12, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', ' Apple iPhone 11 (A2223)  紫色 64GB  移动联通电信4G手机 双卡双待', 'iPhoneXS系列性能强劲，样样出色，现特惠来袭，低至5399元！详情请点击！', 5499.0000, 0);

-- ----------------------------
-- Table structure for pms_sku_sale_attr_value
-- ----------------------------
DROP TABLE IF EXISTS `pms_sku_sale_attr_value`;
CREATE TABLE `pms_sku_sale_attr_value`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT 'sku_id',
  `attr_id` bigint(20) NULL DEFAULT NULL COMMENT 'attr_id',
  `attr_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '销售属性名',
  `attr_value` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '销售属性值',
  `attr_sort` int(11) NULL DEFAULT NULL COMMENT '顺序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'sku销售属性&值' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pms_sku_sale_attr_value
-- ----------------------------
INSERT INTO `pms_sku_sale_attr_value` VALUES (1, 1, 9, '颜色', '星河银', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (2, 1, 12, '版本', '8GB+256GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (3, 2, 9, '颜色', '星河银', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (4, 2, 12, '版本', '8GB+128GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (5, 3, 9, '颜色', '亮黑色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (6, 3, 12, '版本', '8GB+256GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (7, 4, 9, '颜色', '亮黑色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (8, 4, 12, '版本', '8GB+128GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (9, 5, 9, '颜色', '翡冷翠', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (10, 5, 12, '版本', '8GB+256GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (11, 6, 9, '颜色', '翡冷翠', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (12, 6, 12, '版本', '8GB+128GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (13, 7, 9, '颜色', '罗兰紫', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (14, 7, 12, '版本', '8GB+256GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (15, 8, 9, '颜色', '罗兰紫', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (16, 8, 12, '版本', '8GB+128GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (17, 9, 9, '颜色', '黑色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (18, 9, 12, '版本', '128GB ', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (19, 10, 9, '颜色', '黑色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (20, 10, 12, '版本', '256GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (21, 11, 9, '颜色', '黑色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (22, 11, 12, '版本', '64GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (23, 12, 9, '颜色', '白色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (24, 12, 12, '版本', '128GB ', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (25, 13, 9, '颜色', '白色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (26, 13, 12, '版本', '256GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (27, 14, 9, '颜色', '白色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (28, 14, 12, '版本', '64GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (29, 15, 9, '颜色', '绿色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (30, 15, 12, '版本', '128GB ', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (31, 16, 9, '颜色', '绿色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (32, 16, 12, '版本', '256GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (33, 17, 9, '颜色', '绿色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (34, 17, 12, '版本', '64GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (35, 18, 9, '颜色', '黄色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (36, 18, 12, '版本', '128GB ', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (37, 19, 9, '颜色', '黄色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (38, 19, 12, '版本', '256GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (39, 20, 9, '颜色', '黄色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (40, 20, 12, '版本', '64GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (41, 21, 9, '颜色', '红色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (42, 21, 12, '版本', '128GB ', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (43, 22, 9, '颜色', '红色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (44, 22, 12, '版本', '256GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (45, 23, 9, '颜色', '红色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (46, 23, 12, '版本', '64GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (47, 24, 9, '颜色', '紫色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (48, 24, 12, '版本', '128GB ', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (49, 25, 9, '颜色', '紫色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (50, 25, 12, '版本', '256GB', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (51, 26, 9, '颜色', '紫色', NULL);
INSERT INTO `pms_sku_sale_attr_value` VALUES (52, 26, 12, '版本', '64GB', NULL);

-- ----------------------------
-- Table structure for pms_spu_comment
-- ----------------------------
DROP TABLE IF EXISTS `pms_spu_comment`;
CREATE TABLE `pms_spu_comment`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT 'sku_id',
  `spu_id` bigint(20) NULL DEFAULT NULL COMMENT 'spu_id',
  `spu_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品名字',
  `member_nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会员昵称',
  `star` tinyint(1) NULL DEFAULT NULL COMMENT '星级',
  `member_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会员ip',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `show_status` tinyint(1) NULL DEFAULT NULL COMMENT '显示状态[0-不显示，1-显示]',
  `spu_attributes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '购买时属性组合',
  `likes_count` int(11) NULL DEFAULT NULL COMMENT '点赞数',
  `reply_count` int(11) NULL DEFAULT NULL COMMENT '回复数',
  `resources` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评论图片/视频[json数据；[{type:文件类型,url:资源路径}]]',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '内容',
  `member_icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像',
  `comment_type` tinyint(4) NULL DEFAULT NULL COMMENT '评论类型[0 - 对商品的直接评论，1 - 对评论的回复]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品评价' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pms_spu_comment
-- ----------------------------

-- ----------------------------
-- Table structure for pms_spu_images
-- ----------------------------
DROP TABLE IF EXISTS `pms_spu_images`;
CREATE TABLE `pms_spu_images`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `spu_id` bigint(20) NULL DEFAULT NULL COMMENT 'spu_id',
  `img_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片名',
  `img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片地址',
  `img_sort` int(11) NULL DEFAULT NULL COMMENT '顺序',
  `default_img` tinyint(4) NULL DEFAULT NULL COMMENT '是否默认图',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 95 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'spu图片' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pms_spu_images
-- ----------------------------
INSERT INTO `pms_spu_images` VALUES (71, 11, NULL, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/ef2691e5-de1a-4ca3-827d-a60f39fbda93_0d40c24b264aa511.jpg', NULL, NULL);
INSERT INTO `pms_spu_images` VALUES (72, 11, NULL, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/20f83b0c-86ba-4a64-808a-f2ace190ea2c_1f15cdbcf9e1273c.jpg', NULL, NULL);
INSERT INTO `pms_spu_images` VALUES (73, 11, NULL, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/89e324b9-d0cf-4f4f-8e81-94bb219910b3_2b1837c6c50add30.jpg', NULL, NULL);
INSERT INTO `pms_spu_images` VALUES (74, 11, NULL, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/9a6ba5c0-0a31-4364-8759-012bdfbf5ad3_3c24f9cd69534030.jpg', NULL, NULL);
INSERT INTO `pms_spu_images` VALUES (75, 11, NULL, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/a73ef55a-79b4-41d9-8eb6-760c8b5a33dc_23d9fbb256ea5d4a.jpg', NULL, NULL);
INSERT INTO `pms_spu_images` VALUES (76, 11, NULL, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/099f297e-ddea-4fb5-87c7-78cd88e500c0_28f296629cca865e.jpg', NULL, NULL);
INSERT INTO `pms_spu_images` VALUES (77, 11, NULL, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/38492c9f-b0e0-4cba-87a9-77cb6189ea73_73ab4d2e818d2211.jpg', NULL, NULL);
INSERT INTO `pms_spu_images` VALUES (78, 11, NULL, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/7aa1019e-4e01-49dd-8ac4-7d2794d0b1a8_335b2c690e43a8f8.jpg', NULL, NULL);
INSERT INTO `pms_spu_images` VALUES (79, 11, NULL, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/d12516dd-43cf-4ace-8dc9-14618d2d75e4_919c850652e98031.jpg', NULL, NULL);
INSERT INTO `pms_spu_images` VALUES (80, 11, NULL, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/60e65a44-f943-4ed5-87c8-8cf90f403018_d511faab82abb34b.jpg', NULL, NULL);
INSERT INTO `pms_spu_images` VALUES (88, 13, NULL, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/1756fa6d-1934-4d5c-8faf-84b3d882fe53_5b5e74d0978360a1.jpg', NULL, NULL);
INSERT INTO `pms_spu_images` VALUES (89, 13, NULL, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/299481e9-4704-4824-8b18-60c7d268353c_7ae0120ec27dc3a7.jpg', NULL, NULL);
INSERT INTO `pms_spu_images` VALUES (90, 13, NULL, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/462ef293-2b8b-4c53-85f2-1726e14dc976_23cd65077f12f7f5.jpg', NULL, NULL);
INSERT INTO `pms_spu_images` VALUES (91, 13, NULL, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/bc4825d6-2a6c-43f8-8d75-5f35b77b9514_a2c208410ae84d1f.jpg', NULL, NULL);
INSERT INTO `pms_spu_images` VALUES (92, 13, NULL, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/f968b6ac-2fca-4440-842f-8db8b76478f0_b8494bf281991f94.jpg', NULL, NULL);
INSERT INTO `pms_spu_images` VALUES (93, 13, NULL, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/e21777b3-18ac-4580-819e-497c3aa25e4f_6a1b2703a9ed8737.jpg', NULL, NULL);
INSERT INTO `pms_spu_images` VALUES (94, 13, NULL, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/2419c5cf-a641-4ec1-8f94-64981dc207f6_63e862164165f483.jpg', NULL, NULL);

-- ----------------------------
-- Table structure for pms_spu_info
-- ----------------------------
DROP TABLE IF EXISTS `pms_spu_info`;
CREATE TABLE `pms_spu_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `spu_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `spu_description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品描述',
  `catalog_id` bigint(20) NULL DEFAULT NULL COMMENT '所属分类id',
  `brand_id` bigint(20) NULL DEFAULT NULL COMMENT '品牌id',
  `weight` decimal(18, 4) NULL DEFAULT NULL,
  `publish_status` tinyint(4) NULL DEFAULT NULL COMMENT '上架状态[0 - 下架，1 - 上架]',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'spu信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pms_spu_info
-- ----------------------------
INSERT INTO `pms_spu_info` VALUES (11, '华为 HUAWEI Mate 30 Pro', '华为 HUAWEI Mate 30 Pro', 225, 9, 0.1980, 1, '2019-11-26 10:10:57', '2019-12-15 23:04:16');
INSERT INTO `pms_spu_info` VALUES (13, ' Apple iPhone 11 (A2223) ', ' Apple iPhone 11 (A2223) ', 225, 12, 0.1940, 1, '2019-11-27 05:37:30', '2019-12-15 23:25:19');

-- ----------------------------
-- Table structure for pms_spu_info_desc
-- ----------------------------
DROP TABLE IF EXISTS `pms_spu_info_desc`;
CREATE TABLE `pms_spu_info_desc`  (
  `spu_id` bigint(20) NOT NULL COMMENT '商品id',
  `decript` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '商品介绍',
  PRIMARY KEY (`spu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'spu信息介绍' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pms_spu_info_desc
-- ----------------------------
INSERT INTO `pms_spu_info_desc` VALUES (11, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/702b76e6-ce3e-4268-8216-a12db5607347_73366cc235d68202.jpg,https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-26/38956d81-5dff-4abd-8ce8-144908e869d8_528211b97272d88a.jpg');
INSERT INTO `pms_spu_info_desc` VALUES (13, 'https://gulimall-hello.oss-cn-beijing.aliyuncs.com/2019-11-27/ffc5a377-b037-4f26-84a0-df5b1c7cf42d_f205d9c99a2b4b01.jpg');

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
  PRIMARY KEY (`dept_id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE,
  INDEX `idx_del_flag_status`(`del_flag`, `status`) USING BTREE,
  INDEX `idx_order_num`(`order_num`) USING BTREE
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
INSERT INTO `sys_job` VALUES (1, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '0 0 2 * * ?', '3', '1', '0', 'admin', '2026-02-06 20:41:50', 'admin', '2026-03-01 12:51:41', '每天凌晨2点清空操作日志表 sys_oper_log');

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务调度日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------
INSERT INTO `sys_job_log` VALUES (1, '定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '执行成功', '0', '', '2026-03-31 15:33:02');

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
  INDEX `idx_login_time`(`login_time`) USING BTREE,
  INDEX `idx_user_name`(`user_name`) USING BTREE,
  INDEX `idx_user_time`(`user_name`, `login_time`) USING BTREE,
  INDEX `idx_ipaddr`(`ipaddr`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES (1, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 148', 'Windows 10', '0', '登录成功', '2026-05-15 15:48:20');
INSERT INTO `sys_logininfor` VALUES (2, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 148', 'Windows 10', '0', '登录成功', '2026-05-18 12:42:03');
INSERT INTO `sys_logininfor` VALUES (3, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 148', 'Windows 10', '0', '登录成功', '2026-05-18 19:41:17');
INSERT INTO `sys_logininfor` VALUES (4, 'admin', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 148', 'Windows 10', '0', '登录成功', '2026-05-18 20:36:21');

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
  PRIMARY KEY (`menu_id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE,
  INDEX `idx_status_visible`(`status`, `visible`) USING BTREE,
  INDEX `idx_order_num`(`order_num`) USING BTREE,
  INDEX `idx_parent_sort`(`parent_id`, `order_num`) USING BTREE,
  INDEX `idx_visible_status`(`visible`, `status`) USING BTREE,
  INDEX `idx_menu_type_status`(`menu_type`, `status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 178 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 1, '/system', '', NULL, 'System', 1, 1, 'M', '0', '0', '', 'ep:setting', 'system', '2025-12-31 16:34:16', 'admin', '2026-04-23 17:00:25', '系统管理模块');
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
INSERT INTO `sys_menu` VALUES (71, '系统工具', 0, 2, '/tools', '', NULL, 'SystemTools', 1, 1, 'M', '0', '0', '', 'clarity:tools-line', 'admin', '2026-01-20 13:08:43', 'admin', '2026-04-19 19:09:23', '系统工具');
INSERT INTO `sys_menu` VALUES (72, '代码生成', 71, 1, 'generator', '/tools/generator/index', NULL, 'GenerateTools', 1, 1, 'C', '0', '0', 'tools:generator:list', 'mdi:generator-mobile', 'admin', '2026-01-20 13:15:59', 'admin', '2026-01-20 13:25:42', '代码生成');
INSERT INTO `sys_menu` VALUES (73, '列表查询', 72, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2026-01-20 14:56:43', '', NULL, '列表查询');
INSERT INTO `sys_menu` VALUES (74, '日志管理', 1, 7, 'log', '', NULL, 'LogManager', 1, 1, 'M', '0', '0', '', 'mdi:math-log', 'admin', '2026-01-23 13:37:05', 'admin', '2026-05-15 09:09:47', '');
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
INSERT INTO `sys_menu` VALUES (95, '系统监控', 0, 3, '/monitor', '', NULL, 'Monitor', 1, 1, 'M', '0', '0', '', 'material-symbols:monitor-outline', 'admin', '2026-01-25 18:00:43', 'admin', '2026-05-18 15:42:45', '系统监控模块');
INSERT INTO `sys_menu` VALUES (96, '服务器监控', 95, 1, 'server', '/monitor/server/index', NULL, 'ServerMonitor', 1, 0, 'C', '0', '0', 'monitor:server:query', 'ri:server-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:33:45', '服务器信息监控');
INSERT INTO `sys_menu` VALUES (97, 'Druid监控', 95, 2, 'druid', '/monitor/druid/index', NULL, 'DruidMonitor', 1, 0, 'C', '0', '0', 'monitor:druid:query', 'ri:database-2-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:32:03', 'Druid数据库连接池监控');
INSERT INTO `sys_menu` VALUES (98, 'Redis缓存', 95, 3, 'redis', '/monitor/redis/index', NULL, 'RedisMonitor', 1, 0, 'C', '0', '0', 'monitor:redis:query', 'ri:database-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-03-01 13:37:51', 'Redis缓存监控');
INSERT INTO `sys_menu` VALUES (99, '在线用户', 95, 4, 'online', '/monitor/online/index', NULL, 'OnlineUser', 1, 0, 'C', '0', '0', 'monitor:online:query', 'ri:user-line', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:32:45', '在线用户监控');
INSERT INTO `sys_menu` VALUES (100, '强制下线', 99, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:online:force-logout', '', 'admin', '2026-01-25 18:00:43', 'admin', '2026-01-25 18:30:55', '强制用户下线');
INSERT INTO `sys_menu` VALUES (114, '删除缓存', 98, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:cache:remove', '#', 'admin', '2026-01-25 22:19:38', '', NULL, 'monitor:cache:remove');
INSERT INTO `sys_menu` VALUES (115, '清空缓存', 98, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:cache:clear', '#', 'admin', '2026-01-25 22:20:00', '', NULL, '清空缓存');
INSERT INTO `sys_menu` VALUES (117, '缓存列表', 98, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'monitor:cache:query', '#', 'admin', '2026-01-27 18:21:55', '', NULL, '缓存列表');
INSERT INTO `sys_menu` VALUES (118, '通知公告', 1, 8, 'notice', '/system/notice/index', NULL, 'NoticeManage', 1, 0, 'C', '0', '0', 'system:notice:list', 'fe:notice-active', 'admin', '2026-02-05 17:38:35', 'admin', '2026-03-31 22:03:49', '通知公告菜单');
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
INSERT INTO `sys_menu` VALUES (140, '参数配置', 1, 9, 'config', '/system/config/index', NULL, 'ConfigManage', 1, 1, 'C', '0', '0', 'system:config:list', 'mynaui:config', 'admin', '2026-03-31 22:03:28', 'admin', '2026-03-31 22:05:20', '参数配置菜单');
INSERT INTO `sys_menu` VALUES (141, '参数配置查询', 140, 1, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:query', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:23', '');
INSERT INTO `sys_menu` VALUES (142, '参数配置新增', 140, 2, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:add', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:27', '');
INSERT INTO `sys_menu` VALUES (143, '参数配置修改', 140, 3, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:edit', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:31', '');
INSERT INTO `sys_menu` VALUES (144, '参数配置删除', 140, 4, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:delete', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:36', '');
INSERT INTO `sys_menu` VALUES (145, '参数配置导出', 140, 5, '#', '', NULL, '', 1, 1, 'F', '0', '0', 'system:config:export', '#', 'admin', '2026-03-31 22:03:29', 'admin', '2026-03-31 22:04:39', '');
INSERT INTO `sys_menu` VALUES (147, 'art-design-pro', 0, 100, 'https://gitee.com/lingchen163/art-design-pro', '', NULL, '', 0, 1, 'M', '0', '0', '', 'ri:guide-fill', 'admin', '2026-04-19 19:07:54', 'admin', '2026-04-23 20:26:22', '');
INSERT INTO `sys_menu` VALUES (148, '星枢项目', 0, 99, 'https://gitee.com/xin1998/StarPivot', '', NULL, '', 0, 1, 'M', '0', '0', '', 'ri:gitee-fill', 'admin', '2026-04-21 12:48:19', 'admin', '2026-04-23 20:26:13', '');
INSERT INTO `sys_menu` VALUES (151, '商城系统', 0, 5, '/mall', '', NULL, 'MallSystem', 1, 1, 'M', '0', '0', '', 'ep:goods', 'admin', '2026-04-23 20:23:16', 'admin', '2026-04-23 20:26:06', '商城系统');
INSERT INTO `sys_menu` VALUES (152, '品牌列表', 154, 2, 'brand', '/mall/brand/index', NULL, 'BrandManager', 1, 1, 'C', '0', '0', 'mall:brand:list', 'ep:shop', 'admin', '2026-04-23 20:25:38', 'admin', '2026-05-18 14:44:07', '品牌管理');
INSERT INTO `sys_menu` VALUES (153, '商品维护', 154, 4, 'product', '/mall/product/index', NULL, 'ProductManager', 1, 1, 'C', '0', '0', 'mall:product:list', 'ep:goods-filled', 'admin', '2026-04-23 20:27:38', 'admin', '2026-05-18 14:48:55', '商品管理');
INSERT INTO `sys_menu` VALUES (154, '商品管理', 151, 1, 'good', '', NULL, 'GoodManager', 1, 1, 'M', '0', '0', '', 'ep:goods-filled', 'admin', '2026-05-15 10:38:29', '', NULL, '');
INSERT INTO `sys_menu` VALUES (155, '分类维护', 154, 1, 'category', '/mall/category/index', NULL, 'CategoryManager', 1, 1, 'C', '0', '0', 'mall:category:list', 'ri:node-tree', 'admin', '2026-05-15 10:43:03', 'admin', '2026-05-18 14:48:34', '');
INSERT INTO `sys_menu` VALUES (156, '平台属性', 154, 3, 'platform-attributes', '', NULL, 'PlatformAttributes', 1, 1, 'M', '0', '0', '', 'mdi:tools', 'admin', '2026-05-18 14:43:45', 'admin', '2026-05-18 15:04:12', '');
INSERT INTO `sys_menu` VALUES (157, '属性分组', 156, 1, 'group', '/mall/group/index', NULL, 'AttrgroupManager', 1, 1, 'C', '0', '0', '', 'ep:histogram', 'admin', '2026-05-18 14:59:27', 'admin', '2026-05-18 15:27:29', '');
INSERT INTO `sys_menu` VALUES (158, '规格参数', 156, 2, 'base', '/mall/attr/base/index', NULL, 'BaseParam', 1, 1, 'C', '0', '0', '', 'ep:document', 'admin', '2026-05-18 15:06:50', 'admin', '2026-05-18 16:39:24', '');
INSERT INTO `sys_menu` VALUES (159, '销售属性', 156, 3, 'sale', '/mall/attr/sale/index', NULL, 'SalesAttributes', 1, 1, 'C', '0', '0', '', 'ep:present', 'admin', '2026-05-18 15:12:11', 'admin', '2026-05-18 16:39:34', '');
INSERT INTO `sys_menu` VALUES (160, '属性分组查询', 157, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:group:query', '#', 'admin', '2026-05-18 15:42:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (161, '属性分组新增', 157, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:group:add', '#', 'admin', '2026-05-18 15:42:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (162, '属性分组修改', 157, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:group:edit', '#', 'admin', '2026-05-18 15:42:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (163, '属性分组删除', 157, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:group:delete', '#', 'admin', '2026-05-18 15:42:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (164, '属性分组导出', 157, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:group:export', '#', 'admin', '2026-05-18 15:42:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (165, '基本属性查询', 158, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:base:query', '#', 'admin', '2026-05-18 16:29:55', 'admin', '2026-05-18 16:45:18', '基本属性查询');
INSERT INTO `sys_menu` VALUES (166, '基本属性查询', 159, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:sale:query', '#', 'admin', '2026-05-18 16:30:37', 'admin', '2026-05-18 16:46:53', '基本属性查询');
INSERT INTO `sys_menu` VALUES (167, '基础属性添加', 158, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:base:add', '#', 'admin', '2026-05-18 16:45:44', '', NULL, '基础属性添加');
INSERT INTO `sys_menu` VALUES (168, '基础属性删除', 158, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:base:delete', '#', 'admin', '2026-05-18 16:46:07', '', NULL, '基础属性删除');
INSERT INTO `sys_menu` VALUES (169, '基础属性修改', 158, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:base:edit', '#', 'admin', '2026-05-18 16:46:27', 'admin', '2026-05-18 16:55:39', '基础属性修改');
INSERT INTO `sys_menu` VALUES (170, '基本属性添加', 159, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:sale:add', '#', 'admin', '2026-05-18 16:47:23', '', NULL, '基本属性添加');
INSERT INTO `sys_menu` VALUES (171, '基本属性删除', 159, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:sale:delete', '#', 'admin', '2026-05-18 16:47:47', '', NULL, '基本属性删除');
INSERT INTO `sys_menu` VALUES (172, '基本属性修改', 159, 4, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:sale:edit', '#', 'admin', '2026-05-18 16:48:12', 'admin', '2026-05-18 16:55:45', '基本属性修改');
INSERT INTO `sys_menu` VALUES (173, '属性分组导入', 157, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:group:import', '#', 'admin', '2026-05-18 19:46:08', '', NULL, '属性分组导入');
INSERT INTO `sys_menu` VALUES (174, '基本规格导入', 158, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:base:import', '#', 'admin', '2026-05-18 19:47:10', '', NULL, '基本规格导入');
INSERT INTO `sys_menu` VALUES (175, '基本规格导出', 158, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:base:export', '#', 'admin', '2026-05-18 19:47:35', '', NULL, '基本规格导出');
INSERT INTO `sys_menu` VALUES (176, '销售属性导入', 159, 5, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:sale:import', '#', 'admin', '2026-05-18 19:48:01', '', NULL, '销售属性导入');
INSERT INTO `sys_menu` VALUES (177, '销售属性导出', 159, 6, '', '', NULL, '', 1, 1, 'F', '0', '0', 'mall:sale:export', '#', 'admin', '2026-05-18 19:48:22', '', NULL, '销售属性导出');
INSERT INTO `sys_menu` VALUES (178, '品牌查询', 152, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:brand:query', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (179, '品牌新增', 152, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:brand:add', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (180, '品牌修改', 152, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:brand:edit', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (181, '品牌删除', 152, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:brand:delete', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (182, '分类查询', 155, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:category:query', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (183, '分类新增', 155, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:category:add', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (184, '分类修改', 155, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:category:edit', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (185, '分类删除', 155, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:category:delete', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (186, '商品查询', 153, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:product:query', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (187, '商品新增', 153, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:product:add', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (188, '商品修改', 153, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:product:edit', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (189, '商品删除', 153, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'mall:product:delete', '#', 'admin', '2026-05-18 20:00:00', '', NULL, '');

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'API接口性能监控表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_monitor_api_performance
-- ----------------------------

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
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:1', 1, 'admin', '超级管理员', '星枢科技', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 147', 'Windows 10', '1', '2026-04-23 11:03:46', '2026-04-23 11:03:46', '2026-04-23 11:04:40', NULL, '', '0', '2026-04-23 11:04:40');
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:113', 113, 'wangwu', 'wangwu', '人事部', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 143', 'Windows 10/11', '1', '2026-01-27 19:31:06', '2026-01-27 19:31:06', '2026-01-27 19:36:21', NULL, '', '0', '2026-01-27 19:36:21');
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:114', 114, 'starPivot', 'starPivot演示用户', '山西分公司', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10', '1', '2026-02-09 13:31:46', '2026-02-09 13:31:46', '2026-02-09 13:32:38', NULL, '', '0', '2026-02-09 13:32:38');
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:115', 115, 'xiaoming', 'xiaoming', '人事部', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 146', 'Windows 10', '1', '2026-04-21 22:09:45', '2026-04-21 22:09:45', '2026-04-21 22:10:17', NULL, '', '0', '2026-04-21 22:10:17');
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:116', 116, '秦始皇', '秦始皇', '', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 146', 'Windows 10', '1', '2026-04-01 18:18:23', '2026-04-01 18:18:23', '2026-04-01 18:25:16', NULL, '', '0', '2026-04-01 18:25:16');
INSERT INTO `sys_online_user` VALUES ('jwt:refresh:user:2', 2, 'user', '用户管理员', '测试部门', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome 144', 'Windows 10', '1', '2026-02-09 12:31:16', '2026-02-09 12:31:16', '2026-02-09 12:31:52', NULL, '', '0', '2026-02-09 12:31:52');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int(11) NULL DEFAULT 0 COMMENT '业务类型（0其他 1新增 2修改 3删除 4授权 5导出 6导入 7强退 8生成代码 9清空数据）',
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
  INDEX `idx_oper_time`(`oper_time`) USING BTREE,
  INDEX `idx_oper_name`(`oper_name`) USING BTREE,
  INDEX `idx_module_time`(`title`, `oper_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1948 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES (1940, '操作日志', 3, 'com.star.pivot.controller.system.SysOperLogController.clean()', 'DELETE', 1, 'admin', '星枢科技', '/api/sys/operlog/clean', '0:0:0:0:0:0:0:1', '', '', '{\"code\":200,\"message\":\"清空成功\",\"data\":null,\"timestamp\":1779107864683}', 0, '', '2026-05-18 20:37:45', 8);
INSERT INTO `sys_oper_log` VALUES (1941, '操作日志', 0, 'com.star.pivot.controller.system.SysOperLogController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/operlog/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"title\":null,\"businessType\":null,\"operName\":null,\"status\":null,\"startTime\":null,\"endTime\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=1, rows=[OperLogVO(operId=1940, title=操作日志, businessType=3, method=com.star.pivot.controller.system.SysOperLogController.clean(), requestMethod=DELETE, operatorType=1, operName=admin, deptName=星枢科技, operUrl=/api/sys/operlog/clean, operIp=0:0:0:0:0:0:0:1, operLocation=, operParam=, jsonResult={\"code\":200,\"message\":\"清空成功\",\"data\":null,\"timestamp\":1779107864683}, status=0, errorMsg=, operTime=2026-05-18T20:37:45, costTime=8)], pageNum=1, pageSize=20, pageCount=1), timestamp=1779107864712)', 0, '', '2026-05-18 20:37:45', 8);
INSERT INTO `sys_oper_log` VALUES (1942, '登录日志', 0, 'com.star.pivot.controller.system.SysLogininforController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/logininfor/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"ipaddr\":null,\"status\":null,\"startTime\":null,\"endTime\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=4, rows=[LogininforVO(infoId=4, userName=admin, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 148, os=Windows 10, status=0, msg=登录成功, loginTime=2026-05-18T20:36:21), LogininforVO(infoId=3, userName=admin, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 148, os=Windows 10, status=0, msg=登录成功, loginTime=2026-05-18T19:41:17), LogininforVO(infoId=2, userName=admin, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 148, os=Windows 10, status=0, msg=登录成功, loginTime=2026-05-18T12:42:03), LogininforVO(infoId=1, userName=admin, ipaddr=0:0:0:0:0:0:0:1, loginLocation=内网IP, browser=Chrome 148, os=Windows 10, status=0, msg=登录成功, loginTime=2026-05-15T15:48:20)], pageNum=1, pageSize=20, pageCount=1), timestamp=1779107866326)', 0, '', '2026-05-18 20:37:46', 11);
INSERT INTO `sys_oper_log` VALUES (1943, '参数配置', 5, 'com.star.pivot.controller.system.SysConfigController.export()', 'POST', 1, 'admin', '星枢科技', '/api/system/config/export', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"configName\":null,\"configKey\":null,\"configValue\":null,\"configType\":null}]', '{\"headers\":{\"Content-Disposition\":[\"attachment; filename=\\\"sys_config_export_1779107872287.xlsx\\\"; filename*=UTF-8\'\'sys_config_export_1779107872287.xlsx\"],\"Content-Type\":[\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet\"]},\"body\":\"UEsDBC0ACAAIAAAAAAAAAAAAAAAAAAAAAAATAAAAW0NvbnRlbnRfVHlwZXNdLnhtbLVTy27CMBD8lcjXKjb0UFUVgUMfxxap9ANce5NY+CWvofD3XQc4lFKJCnHyY2ZnZlf2ZLZxtlpDQhN8w8Z8xCrwKmjju4Z9LF7qe1Zhll5LGzw0bAvIZtPJYhsBK6r12LA+5/ggBKoenEQeInhC2pCczHRMnYhSLWUH4nY0uhMq+Aw+17losOnkCVq5srl63N0X6YbJGK1RMlMssfb6SLTeC/IEduBgbyLeEIFVzxtS2bVDKDJxhsNxYTlT3RsNJhkN/4oW2tYo0EGtHJVwKKoadB0TEVM2sM85lym/SkeCgshzQlGQNL/E+zAWFRKcZViIFzkedYsxgdTYA2RnOfYygX7PiV7T7xAbK34Qrpgjb+2JKZQAA3LNCdDKnTT+lPtXSMvPEJbX8y8Ow/4v+wFEMSzjQw4xfO/pN1BLBwiRLCi8OwEAAAAAAAAdBAAAAAAAAFBLAwQtAAgACAAAAAAAAAAAAAAAAAAAAAAACwAAAF9yZWxzLy5yZWxzrZLBSgMxEIZfJcy9m20FEWnaiwi9idQHGJPZ3bCbTEhG3b69wYu2bEHB4zAz3/8xyXY/h0m9Uy6eo4F104KiaNn52Bt4OT6u7kAVwehw4kgGTlRgv9s+04RSV8rgU1GVEYuBQSTda13sQAFLw4li7XScA0otc68T2hF70pu2vdX5JwPOmergDOSDW4M6Yu5JDMyT/uA8vjKPTcXWxinRb0K567ylB7ZvgaIsZF9MgF522Xy7OLZPmesmpvTfMjQLRUdulWoCZfH14leMbhaMLGf6m9L1R9GBBB0KflEvhPTZH9h9AlBLBwhuMghL5QAAAAAAAABKAgAAAAAAAFBLAwQtAAgACAAAAAAAAAAAAAAAAAAAAAAAEAAAAGRvY1Byb3BzL2FwcC54bWxNjsEKwjAQRO+C/xByb7d6EJE0pSCCJ3vQDwjp1gaaTUhW6eebk3qcGebxVLf6RbwxZReolbu6kQLJhtHRs5WP+6U6yk5vN2pIIWJih1mUB+VWzszxBJDtjN7kusxUlikkb7jE9IQwTc7iOdiXR2LYN80BcGWkEccqfoFSqz7GxVnDRUL30RSkGG5XBf+9gp+D/gBQSwcINm6DIZMAAAAAAAAAuAAAAAAAAABQSwMELQAIAAgAAAAAAAAAAAAAAAAAAAAAABEAAABkb2NQcm9wcy9jb3JlLnhtbG2QXUvDMBSG/0rJfXuals0R2g5RBoLiwIniXUiObbH5IIl2+/emdVZQ75K8z3k4eavtUQ3JBzrfG10TmuUkQS2M7HVbk8fDLt2QxAeuJR+Mxpqc0JNtUwnLhHG4d8aiCz36JHq0Z8LWpAvBMgAvOlTcZ5HQMXw1TvEQr64Fy8UbbxGKPF+DwsAlDxwmYWoXIzkrpViU9t0Ns0AKwAEV6uCBZhR+2IBO+X8H5mQhj75fqHEcs7GcubgRhee724d5+bTX098FkqY6q5lwyAPKJApYONlYyXfyVF5dH3akKfJinearlG4OtGDlBVsVLxX8mp+EX2fjmstYSIfJ/v5m4pbnCv7U3HwCUEsHCEvq0AMHAQAAAAAAALEBAAAAAAAAUEsDBC0ACAAIAAAAAAAAAAAAAAAAAAAAAAAUAAAAeGwvc2hhcmVkU3RyaW5ncy54bWw9jEEKwjAQAO+Cfwh7t4keRKRpD4Iv0...', 0, '', '2026-05-18 20:37:52', 49);
INSERT INTO `sys_oper_log` VALUES (1944, '参数配置', 5, 'com.star.pivot.controller.system.SysConfigController.export()', 'POST', 1, 'admin', '星枢科技', '/api/system/config/export', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"configName\":null,\"configKey\":null,\"configValue\":null,\"configType\":null}]', '{\"headers\":{\"Content-Disposition\":[\"attachment; filename=\\\"sys_config_export_1779108184412.xlsx\\\"; filename*=UTF-8\'\'sys_config_export_1779108184412.xlsx\"],\"Content-Type\":[\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet\"]},\"body\":\"UEsDBC0ACAAIAAAAAAAAAAAAAAAAAAAAAAATAAAAW0NvbnRlbnRfVHlwZXNdLnhtbLVTy27CMBD8lcjXKjb0UFUVgUMfxxap9ANce5NY+CWvofD3XQc4lFKJCnHyY2ZnZlf2ZLZxtlpDQhN8w8Z8xCrwKmjju4Z9LF7qe1Zhll5LGzw0bAvIZtPJYhsBK6r12LA+5/ggBKoenEQeInhC2pCczHRMnYhSLWUH4nY0uhMq+Aw+17losOnkCVq5srl63N0X6YbJGK1RMlMssfb6SLTeC/IEduBgbyLeEIFVzxtS2bVDKDJxhsNxYTlT3RsNJhkN/4oW2tYo0EGtHJVwKKoadB0TEVM2sM85lym/SkeCgshzQlGQNL/E+zAWFRKcZViIFzkedYsxgdTYA2RnOfYygX7PiV7T7xAbK34Qrpgjb+2JKZQAA3LNCdDKnTT+lPtXSMvPEJbX8y8Ow/4v+wFEMSzjQw4xfO/pN1BLBwiRLCi8OwEAAAAAAAAdBAAAAAAAAFBLAwQtAAgACAAAAAAAAAAAAAAAAAAAAAAACwAAAF9yZWxzLy5yZWxzrZLBSgMxEIZfJcy9m20FEWnaiwi9idQHGJPZ3bCbTEhG3b69wYu2bEHB4zAz3/8xyXY/h0m9Uy6eo4F104KiaNn52Bt4OT6u7kAVwehw4kgGTlRgv9s+04RSV8rgU1GVEYuBQSTda13sQAFLw4li7XScA0otc68T2hF70pu2vdX5JwPOmergDOSDW4M6Yu5JDMyT/uA8vjKPTcXWxinRb0K567ylB7ZvgaIsZF9MgF522Xy7OLZPmesmpvTfMjQLRUdulWoCZfH14leMbhaMLGf6m9L1R9GBBB0KflEvhPTZH9h9AlBLBwhuMghL5QAAAAAAAABKAgAAAAAAAFBLAwQtAAgACAAAAAAAAAAAAAAAAAAAAAAAEAAAAGRvY1Byb3BzL2FwcC54bWxNjsEKwjAQRO+C/xByb7d6EJE0pSCCJ3vQDwjp1gaaTUhW6eebk3qcGebxVLf6RbwxZReolbu6kQLJhtHRs5WP+6U6yk5vN2pIIWJih1mUB+VWzszxBJDtjN7kusxUlikkb7jE9IQwTc7iOdiXR2LYN80BcGWkEccqfoFSqz7GxVnDRUL30RSkGG5XBf+9gp+D/gBQSwcINm6DIZMAAAAAAAAAuAAAAAAAAABQSwMELQAIAAgAAAAAAAAAAAAAAAAAAAAAABEAAABkb2NQcm9wcy9jb3JlLnhtbG2QXUvDMBSG/0rIfZuknWOEtkOUgaA4cKJ4F5JjW2w+SKLd/r1pnRXUuyTvcx5O3mp71AP6AB96a2rMcooRGGlVb9oaPx522QajEIVRYrAGanyCgLdNJR2X1sPeWwc+9hBQ8pjApatxF6PjhATZgRYhT4RJ4av1WsR09S1xQr6JFkhB6ZpoiEKJKMgkzNxixGelkovSvfthFihJYAANJgbCckZ+2Aheh38H5mQhj6FfqHEc87GcubQRI893tw/z8llvpr9LwE11VnPpQURQKAl4PLlUyXfyVF5dH3a4KWixzuhFxjYHVvBVyenqpSK/5ifh19n65jIV0gHa399M3PJckT81N59QSwcIIxiv1QYBAAAAAAAAsQEAAAAAAABQSwMELQAIAAgAAAAAAAAAAAAAAAAAAAAAABQAAAB4bC9zaGFyZWRTdHJpbmdzLnhtbD2MQQrCMBAA74J/CHu3iR5EpGkPgi/QB...', 0, '', '2026-05-18 20:43:04', 1169);
INSERT INTO `sys_oper_log` VALUES (1945, '用户管理', 0, 'com.star.pivot.controller.system.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=11, rows=[UserVO(userId=117, deptId=108, deptName=市场部门, userName=lichungang, nickName=李淳罡, userType=00, email=test@qq.com, phonenumber=1888888888, sex=0, avatar=, status=0, loginIp=, loginDate=null, createTime=2026-04-23T13:58:06, remark=李淳罡, roleIds=[3], roleNames=[测试专属], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=116, deptId=null, deptName=null, userName=秦始皇, nickName=秦始皇, userType=00, email=, phonenumber=, sex=0, avatar=null, status=0, loginIp=, loginDate=null, createTime=2026-04-01T18:18:08, remark=null, roleIds=[5], roleNames=[注册用户], postIds=null, postNames=null, isLocked=false), UserVO(userId=115, deptId=111, deptName=人事部, userName=xiaoming, nickName=xiaoming, userType=00, email=xiaoming@163.com, phonenumber=15588888888, sex=0, avatar=http://localhost:9000/star-pivot-avatar/avatar/115.webp?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20260421%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260421T134204Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=af7284e4d293d6bb284c86f9440327a1a369a4b0c65bf126749f5b8a334df612, status=0, loginIp=, loginDate=null, createTime=2026-02-09T18:19:02, remark=, roleIds=[3, 5], roleNames=[测试专属, 注册用户], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=114, deptId=110, deptName=山西分公司, userName=starPivot, nickName=starPivot演示用户, userType=00, email=starPivot@163.com, phonenumber=13388888888, sex=0, avatar=http://localhost:9000/star-pivot-avatar/avatar/114.webp?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20260421%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260421T134157Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=dd086f3c1e79911145e932ba83de36e7db556741e78cf1eff8fbd6ef49b91cd0, status=0, loginIp=, loginDate=null, createTime=2026-02-09T12:22:01, remark=111, roleIds=[4], roleN...', 0, '', '2026-05-18 20:47:30', 1249);
INSERT INTO `sys_oper_log` VALUES (1946, '用户管理', 0, 'com.star.pivot.controller.system.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=11, rows=[UserVO(userId=117, deptId=108, deptName=市场部门, userName=lichungang, nickName=李淳罡, userType=00, email=test@qq.com, phonenumber=1888888888, sex=0, avatar=, status=0, loginIp=, loginDate=null, createTime=2026-04-23T13:58:06, remark=李淳罡, roleIds=[3], roleNames=[测试专属], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=116, deptId=null, deptName=null, userName=秦始皇, nickName=秦始皇, userType=00, email=, phonenumber=, sex=0, avatar=null, status=0, loginIp=, loginDate=null, createTime=2026-04-01T18:18:08, remark=null, roleIds=[5], roleNames=[注册用户], postIds=null, postNames=null, isLocked=false), UserVO(userId=115, deptId=111, deptName=人事部, userName=xiaoming, nickName=xiaoming, userType=00, email=xiaoming@163.com, phonenumber=15588888888, sex=0, avatar=http://localhost:9000/star-pivot-avatar/avatar/115.webp?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20260421%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260421T134204Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=af7284e4d293d6bb284c86f9440327a1a369a4b0c65bf126749f5b8a334df612, status=0, loginIp=, loginDate=null, createTime=2026-02-09T18:19:02, remark=, roleIds=[3, 5], roleNames=[测试专属, 注册用户], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=114, deptId=110, deptName=山西分公司, userName=starPivot, nickName=starPivot演示用户, userType=00, email=starPivot@163.com, phonenumber=13388888888, sex=0, avatar=http://localhost:9000/star-pivot-avatar/avatar/114.webp?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20260421%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260421T134157Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=dd086f3c1e79911145e932ba83de36e7db556741e78cf1eff8fbd6ef49b91cd0, status=0, loginIp=, loginDate=null, createTime=2026-02-09T12:22:01, remark=111, roleIds=[4], roleN...', 0, '', '2026-05-18 20:47:44', 39);
INSERT INTO `sys_oper_log` VALUES (1947, '用户管理', 0, 'com.star.pivot.controller.system.SysUserController.pageList()', 'POST', 1, 'admin', '星枢科技', '/api/sys/user/pageList', '0:0:0:0:0:0:0:1', '', '[{\"pageNum\":1,\"pageSize\":20,\"userName\":null,\"nickName\":null,\"sex\":null,\"status\":\"0\",\"phonenumber\":null,\"email\":null,\"roleId\":null,\"deptId\":null}]', 'Result(code=200, message=操作成功, data=PageResponse(total=11, rows=[UserVO(userId=117, deptId=108, deptName=市场部门, userName=lichungang, nickName=李淳罡, userType=00, email=test@qq.com, phonenumber=1888888888, sex=0, avatar=, status=0, loginIp=, loginDate=null, createTime=2026-04-23T13:58:06, remark=李淳罡, roleIds=[3], roleNames=[测试专属], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=116, deptId=null, deptName=null, userName=秦始皇, nickName=秦始皇, userType=00, email=, phonenumber=, sex=0, avatar=null, status=0, loginIp=, loginDate=null, createTime=2026-04-01T18:18:08, remark=null, roleIds=[5], roleNames=[注册用户], postIds=null, postNames=null, isLocked=false), UserVO(userId=115, deptId=111, deptName=人事部, userName=xiaoming, nickName=xiaoming, userType=00, email=xiaoming@163.com, phonenumber=15588888888, sex=0, avatar=http://localhost:9000/star-pivot-avatar/avatar/115.webp?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20260421%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260421T134204Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=af7284e4d293d6bb284c86f9440327a1a369a4b0c65bf126749f5b8a334df612, status=0, loginIp=, loginDate=null, createTime=2026-02-09T18:19:02, remark=, roleIds=[3, 5], roleNames=[测试专属, 注册用户], postIds=[4], postNames=[普通员工], isLocked=false), UserVO(userId=114, deptId=110, deptName=山西分公司, userName=starPivot, nickName=starPivot演示用户, userType=00, email=starPivot@163.com, phonenumber=13388888888, sex=0, avatar=http://localhost:9000/star-pivot-avatar/avatar/114.webp?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20260421%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260421T134157Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=dd086f3c1e79911145e932ba83de36e7db556741e78cf1eff8fbd6ef49b91cd0, status=0, loginIp=, loginDate=null, createTime=2026-02-09T12:22:01, remark=111, roleIds=[4], roleN...', 0, '', '2026-05-18 20:47:47', 38);

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
  PRIMARY KEY (`post_id`) USING BTREE,
  UNIQUE INDEX `uk_post_code`(`post_code`) USING BTREE
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
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE INDEX `uk_role_key`(`role_key`) USING BTREE,
  INDEX `idx_del_flag`(`del_flag`) USING BTREE,
  INDEX `idx_role_sort`(`role_sort`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_del_status`(`del_flag`, `status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2025-12-28 13:46:34', 'admin', '2026-01-23 19:59:34', '超级管理员：拥有所有菜单权限');
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', '2025-12-28 13:46:34', 'admin', '2026-02-12 17:54:47', '普通角色');
INSERT INTO `sys_role` VALUES (3, '测试专属', 'test', 3, '2', 1, 1, '0', '0', '', '2026-01-03 17:06:10', 'admin', '2026-03-31 17:30:15', '专属于测试的角色');
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
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_role_menu`(`role_id`, `menu_id`) USING BTREE,
  UNIQUE INDEX `uk_role_menu`(`role_id`, `menu_id`) USING BTREE,
  INDEX `idx_role_menu_cover`(`role_id`, `menu_id`) USING BTREE,
  INDEX `idx_menu_id`(`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 823 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色与菜单关联表' ROW_FORMAT = Dynamic;

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
INSERT INTO `sys_role_menu` VALUES (741, 3, 1);
INSERT INTO `sys_role_menu` VALUES (742, 3, 4);
INSERT INTO `sys_role_menu` VALUES (747, 3, 5);
INSERT INTO `sys_role_menu` VALUES (757, 3, 6);
INSERT INTO `sys_role_menu` VALUES (767, 3, 8);
INSERT INTO `sys_role_menu` VALUES (772, 3, 9);
INSERT INTO `sys_role_menu` VALUES (777, 3, 12);
INSERT INTO `sys_role_menu` VALUES (758, 3, 39);
INSERT INTO `sys_role_menu` VALUES (760, 3, 40);
INSERT INTO `sys_role_menu` VALUES (761, 3, 41);
INSERT INTO `sys_role_menu` VALUES (748, 3, 42);
INSERT INTO `sys_role_menu` VALUES (749, 3, 43);
INSERT INTO `sys_role_menu` VALUES (750, 3, 44);
INSERT INTO `sys_role_menu` VALUES (751, 3, 45);
INSERT INTO `sys_role_menu` VALUES (762, 3, 46);
INSERT INTO `sys_role_menu` VALUES (768, 3, 47);
INSERT INTO `sys_role_menu` VALUES (769, 3, 48);
INSERT INTO `sys_role_menu` VALUES (770, 3, 49);
INSERT INTO `sys_role_menu` VALUES (771, 3, 50);
INSERT INTO `sys_role_menu` VALUES (773, 3, 51);
INSERT INTO `sys_role_menu` VALUES (774, 3, 52);
INSERT INTO `sys_role_menu` VALUES (775, 3, 53);
INSERT INTO `sys_role_menu` VALUES (776, 3, 54);
INSERT INTO `sys_role_menu` VALUES (743, 3, 55);
INSERT INTO `sys_role_menu` VALUES (744, 3, 56);
INSERT INTO `sys_role_menu` VALUES (745, 3, 57);
INSERT INTO `sys_role_menu` VALUES (746, 3, 58);
INSERT INTO `sys_role_menu` VALUES (763, 3, 59);
INSERT INTO `sys_role_menu` VALUES (759, 3, 60);
INSERT INTO `sys_role_menu` VALUES (752, 3, 61);
INSERT INTO `sys_role_menu` VALUES (778, 3, 62);
INSERT INTO `sys_role_menu` VALUES (779, 3, 63);
INSERT INTO `sys_role_menu` VALUES (780, 3, 64);
INSERT INTO `sys_role_menu` VALUES (782, 3, 66);
INSERT INTO `sys_role_menu` VALUES (783, 3, 67);
INSERT INTO `sys_role_menu` VALUES (784, 3, 68);
INSERT INTO `sys_role_menu` VALUES (781, 3, 69);
INSERT INTO `sys_role_menu` VALUES (785, 3, 70);
INSERT INTO `sys_role_menu` VALUES (799, 3, 71);
INSERT INTO `sys_role_menu` VALUES (800, 3, 72);
INSERT INTO `sys_role_menu` VALUES (801, 3, 73);
INSERT INTO `sys_role_menu` VALUES (786, 3, 74);
INSERT INTO `sys_role_menu` VALUES (787, 3, 75);
INSERT INTO `sys_role_menu` VALUES (790, 3, 76);
INSERT INTO `sys_role_menu` VALUES (789, 3, 77);
INSERT INTO `sys_role_menu` VALUES (788, 3, 78);
INSERT INTO `sys_role_menu` VALUES (791, 3, 79);
INSERT INTO `sys_role_menu` VALUES (792, 3, 80);
INSERT INTO `sys_role_menu` VALUES (803, 3, 81);
INSERT INTO `sys_role_menu` VALUES (804, 3, 82);
INSERT INTO `sys_role_menu` VALUES (805, 3, 83);
INSERT INTO `sys_role_menu` VALUES (806, 3, 84);
INSERT INTO `sys_role_menu` VALUES (807, 3, 85);
INSERT INTO `sys_role_menu` VALUES (802, 3, 86);
INSERT INTO `sys_role_menu` VALUES (764, 3, 87);
INSERT INTO `sys_role_menu` VALUES (753, 3, 88);
INSERT INTO `sys_role_menu` VALUES (754, 3, 89);
INSERT INTO `sys_role_menu` VALUES (755, 3, 90);
INSERT INTO `sys_role_menu` VALUES (756, 3, 91);
INSERT INTO `sys_role_menu` VALUES (808, 3, 92);
INSERT INTO `sys_role_menu` VALUES (765, 3, 93);
INSERT INTO `sys_role_menu` VALUES (766, 3, 94);
INSERT INTO `sys_role_menu` VALUES (822, 3, 95);
INSERT INTO `sys_role_menu` VALUES (809, 3, 96);
INSERT INTO `sys_role_menu` VALUES (810, 3, 97);
INSERT INTO `sys_role_menu` VALUES (811, 3, 98);
INSERT INTO `sys_role_menu` VALUES (815, 3, 99);
INSERT INTO `sys_role_menu` VALUES (816, 3, 100);
INSERT INTO `sys_role_menu` VALUES (812, 3, 114);
INSERT INTO `sys_role_menu` VALUES (813, 3, 115);
INSERT INTO `sys_role_menu` VALUES (814, 3, 117);
INSERT INTO `sys_role_menu` VALUES (793, 3, 118);
INSERT INTO `sys_role_menu` VALUES (794, 3, 119);
INSERT INTO `sys_role_menu` VALUES (795, 3, 120);
INSERT INTO `sys_role_menu` VALUES (796, 3, 121);
INSERT INTO `sys_role_menu` VALUES (797, 3, 122);
INSERT INTO `sys_role_menu` VALUES (798, 3, 123);
INSERT INTO `sys_role_menu` VALUES (817, 3, 124);
INSERT INTO `sys_role_menu` VALUES (818, 3, 125);
INSERT INTO `sys_role_menu` VALUES (819, 3, 126);
INSERT INTO `sys_role_menu` VALUES (820, 3, 127);
INSERT INTO `sys_role_menu` VALUES (821, 3, 128);
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
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE,
  INDEX `idx_username`(`user_name`) USING BTREE,
  INDEX `idx_phone`(`phonenumber`) USING BTREE,
  INDEX `idx_email`(`email`) USING BTREE,
  INDEX `idx_dept_id`(`dept_id`) USING BTREE,
  INDEX `idx_del_flag_status`(`del_flag`, `status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_dept_del_status`(`dept_id`, `del_flag`, `status`) USING BTREE,
  INDEX `idx_phonenumber`(`phonenumber`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 118 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 100, 'admin', '超级管理员', '00', 'admin@163.com', '18888888888', '0', 'http://localhost:9000/star-pivot-avatar/avatar/1.jpeg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20260421%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260421T132436Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=1007e5d25a2c93265abd1244cedc1d14e96b9147b9d26559b206ff8c3ef76b6b', '$2a$10$CdsoTZHFKN2PTu651EMtnO4jyQDlpgrB7lrEsVE..takVPiIJgZBq', '0', '0', '0:0:0:0:0:0:0:1', '2025-12-29 22:16:38', '2026-04-21 22:11:52', 'admin', '2025-12-28 13:46:34', 'admin', '2026-04-21 22:11:52', '超级管理员');
INSERT INTO `sys_user` VALUES (2, 105, 'user', '用户管理员', '00', 'user@qq.com', '15666666666', '1', 'http://localhost:9000/star-pivot-avatar/avatar/2.webp', '$2a$12$eYocwR0zs3iWKl7gsvHVQ.KLDTWvXqecm.29aXPi3IAF.mmkARVR.', '0', '0', '0:0:0:0:0:0:0:1', '2025-12-29 22:08:54', '2025-12-28 13:46:34', 'admin', '2025-12-28 13:46:34', 'admin', '2026-03-13 18:21:01', '测试员--用户管理员');
INSERT INTO `sys_user` VALUES (100, 100, 'xinxin', '超级管理员', '00', '123@qq.com', '18834581458', '0', 'http://localhost:9000/star-pivot-avatar/avatar/100.webp?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20260421%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260421T134125Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=39f4bf04f7831fde3d3d02651ef528af24643e09b10608966208172b8dcb9696', '$2a$12$eYocwR0zs3iWKl7gsvHVQ.KLDTWvXqecm.29aXPi3IAF.mmkARVR.', '0', '0', '', NULL, NULL, 'admin', '2026-01-04 15:34:36', 'admin', '2026-04-21 21:41:26', '作者，超级管理员');
INSERT INTO `sys_user` VALUES (101, 105, 'test', '测试用户', '00', '123@qq.com', '18825454547', '0', 'http://localhost:9000/star-pivot-avatar/avatar/101.webp?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20260421%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260421T134131Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=42d5d25d8c513e15f5ddd0a59e4ee3e3536cdadc37c425f9b22c4587ec6409ee', '$2a$10$YdqAWweActfkWOaWEjz9p.bBWqNWVdT9EQ2OHcUODgFg.f3ma13Va', '0', '0', '', NULL, NULL, 'admin', '2026-01-04 16:50:12', 'admin', '2026-04-21 21:41:33', '测试用户专属');
INSERT INTO `sys_user` VALUES (102, 107, 'zhangsan', '张三', '00', 'zhangsan@qq.com', '18812345678', '1', 'http://localhost:9000/star-pivot-avatar/avatar/102.webp?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20260421%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260421T134138Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=f429e2df0adae62709afc3a38ef8c84414c3d3cf75821e14acef14063739961c', '$2a$10$cmueflWIca3U8waJO3gSv.HAEhTFvygR7lGdMto630Nbs2GcXXTpy', '0', '0', '', NULL, NULL, 'admin', '2026-01-14 18:04:51', 'admin', '2026-04-21 21:41:39', '张三-普通员工');
INSERT INTO `sys_user` VALUES (112, 100, 'lisi', '李四', '00', 'lisi@163.com', '18855555555', '1', 'http://localhost:9000/star-pivot-avatar/avatar/112.webp?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20260421%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260421T134144Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=406009126c63b7559826345f58f0c54e05ee87187279abc8256c434c064bf980', '$2a$10$2eglfjL.MCwsuprXJfWIRezp.4/nV3hpuOOgtRHfG9d8kyCfVDsB.', '0', '0', '', NULL, NULL, 'admin', '2026-01-24 21:56:29', 'admin', '2026-04-21 21:41:46', '');
INSERT INTO `sys_user` VALUES (113, 111, 'wangwu', 'wangwu', '00', 'wangwu@163.com', '13326541578', '0', 'http://localhost:9000/star-pivot-avatar/avatar/113.webp?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20260421%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260421T134151Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=b40981b6adcbe06897abb92792c8d9cd0fbb2d42428e57df83561d50d28e97a4', '$2a$10$XOXffZph7qNkosKfc/gPHOhh1NsQsVB9lzD4VNf6Rz5cYn.6ksHvG', '0', '0', '', NULL, NULL, 'wangwu', '2026-01-27 18:35:41', 'admin', '2026-04-21 21:41:52', '111');
INSERT INTO `sys_user` VALUES (114, 110, 'starPivot', 'starPivot演示用户', '00', 'starPivot@163.com', '13388888888', '0', 'http://localhost:9000/star-pivot-avatar/avatar/114.webp?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20260421%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260421T134157Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=dd086f3c1e79911145e932ba83de36e7db556741e78cf1eff8fbd6ef49b91cd0', '$2a$10$7v0PWRiJVzWOFSDuFkCaXeiAF3xK0qgKxwPzOfSd47JC0LH4juJ4W', '0', '0', '', NULL, NULL, 'admin', '2026-02-09 12:22:01', 'admin', '2026-04-21 21:41:59', '111');
INSERT INTO `sys_user` VALUES (115, 111, 'xiaoming', 'xiaoming', '00', 'xiaoming@163.com', '15588888888', '0', 'http://localhost:9000/star-pivot-avatar/avatar/115.webp?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20260421%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260421T134204Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=af7284e4d293d6bb284c86f9440327a1a369a4b0c65bf126749f5b8a334df612', '$2a$10$CXcWH5F/AnqQaGzDCcttduJoyJEbK.enRHMfbGlO19AViMp7yoa9.', '0', '0', '', NULL, '2026-04-21 22:09:10', 'xiaoming', '2026-02-09 18:19:02', 'admin', '2026-04-21 22:09:10', '');
INSERT INTO `sys_user` VALUES (116, NULL, '秦始皇', '秦始皇', '00', '', '', '0', NULL, '$2a$10$Wnau/Iz7XBH4ruNoDEKWM.8X2qdTHDHg9NA.lgyYsYcTO8nWJGmZm', '0', '0', '', NULL, NULL, '秦始皇', '2026-04-01 18:18:08', '', NULL, NULL);
INSERT INTO `sys_user` VALUES (117, 108, 'lichungang', '李淳罡', '00', 'test@qq.com', '1888888888', '0', '', '$2a$10$qFqkkwmC3pn.poOOCeLrfOIgvxxnBRD544bgqKqa9gTUphikdBc96', '0', '0', '', NULL, NULL, 'admin', '2026-04-23 13:58:06', '', NULL, '李淳罡');

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_post`(`user_id`, `post_id`) USING BTREE,
  UNIQUE INDEX `uk_user_post`(`user_id`, `post_id`) USING BTREE,
  INDEX `idx_post_id`(`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 231 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES (225, 1, 1);
INSERT INTO `sys_user_post` VALUES (214, 2, 2);
INSERT INTO `sys_user_post` VALUES (226, 100, 1);
INSERT INTO `sys_user_post` VALUES (227, 113, 4);
INSERT INTO `sys_user_post` VALUES (228, 114, 4);
INSERT INTO `sys_user_post` VALUES (229, 115, 4);
INSERT INTO `sys_user_post` VALUES (230, 117, 4);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_role`(`user_id`, `role_id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id`, `role_id`) USING BTREE,
  INDEX `idx_user_role_cover`(`user_id`, `role_id`) USING BTREE,
  INDEX `idx_role_id`(`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 257 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户与角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (251, 1, 1);
INSERT INTO `sys_user_role` VALUES (239, 2, 2);
INSERT INTO `sys_user_role` VALUES (252, 100, 3);
INSERT INTO `sys_user_role` VALUES (253, 114, 4);
INSERT INTO `sys_user_role` VALUES (254, 115, 3);
INSERT INTO `sys_user_role` VALUES (255, 115, 5);
INSERT INTO `sys_user_role` VALUES (244, 116, 5);
INSERT INTO `sys_user_role` VALUES (256, 117, 3);

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `context` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime(0) NOT NULL,
  `log_modified` datetime(0) NOT NULL,
  `ext` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ux_undo_log`(`xid`, `branch_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of undo_log
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
