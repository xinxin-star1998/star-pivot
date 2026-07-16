-- 文件中心：收藏 / 最近访问 / 标签 / 批量下载权限
-- 若表或菜单已存在，对应语句报错可忽略后继续

-- 1. 收藏
CREATE TABLE IF NOT EXISTS `sys_file_favorite` (
  `favorite_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `file_id` bigint(0) NOT NULL COMMENT '文件ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`favorite_id`) USING BTREE,
  UNIQUE INDEX `uk_fav_user_file`(`user_id`, `file_id`) USING BTREE,
  INDEX `idx_fav_user_time`(`user_id`, `create_time`) USING BTREE,
  INDEX `idx_fav_file`(`file_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件收藏' ROW_FORMAT = Dynamic;

-- 2. 最近访问
CREATE TABLE IF NOT EXISTS `sys_file_recent` (
  `recent_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '最近访问ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `file_id` bigint(0) NOT NULL COMMENT '文件ID',
  `access_time` datetime(0) NOT NULL COMMENT '最近访问时间',
  PRIMARY KEY (`recent_id`) USING BTREE,
  UNIQUE INDEX `uk_recent_user_file`(`user_id`, `file_id`) USING BTREE,
  INDEX `idx_recent_user_time`(`user_id`, `access_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件最近访问' ROW_FORMAT = Dynamic;

-- 3. 标签（按用户隔离）
CREATE TABLE IF NOT EXISTS `sys_file_tag` (
  `tag_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名',
  `tag_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '#409EFF' COMMENT '颜色',
  `create_by_user_id` bigint(0) NOT NULL COMMENT '所属用户',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`tag_id`) USING BTREE,
  UNIQUE INDEX `uk_tag_user_name`(`create_by_user_id`, `tag_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件标签' ROW_FORMAT = Dynamic;

-- 4. 文件-标签关联
CREATE TABLE IF NOT EXISTS `sys_file_tag_rel` (
  `rel_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `tag_id` bigint(0) NOT NULL COMMENT '标签ID',
  `file_id` bigint(0) NOT NULL COMMENT '文件ID',
  `create_by_user_id` bigint(0) NOT NULL COMMENT '打标用户',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`rel_id`) USING BTREE,
  UNIQUE INDEX `uk_tag_file`(`tag_id`, `file_id`) USING BTREE,
  INDEX `idx_tag_rel_file`(`file_id`) USING BTREE,
  INDEX `idx_tag_rel_user`(`create_by_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件标签关联' ROW_FORMAT = Dynamic;

-- 5. 权限按钮
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
(110, '批量下载', 25, 14, '', '', NULL, '', 1, 1, 'F', '0', '0', 'file:resource:download', '#', 'admin', NOW(), '', NULL, '批量打包下载 ZIP'),
(111, '文件标签', 25, 15, '', '', NULL, '', 1, 1, 'F', '0', '0', 'file:resource:tag', '#', 'admin', NOW(), '', NULL, '标签管理与打标');

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.role_id, m.menu_id
FROM `sys_role` r
CROSS JOIN (SELECT 110 AS menu_id UNION ALL SELECT 111) m
WHERE (r.role_key = 'admin' OR r.role_id = 1)
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_menu` rm WHERE rm.role_id = r.role_id AND rm.menu_id = m.menu_id
  );
