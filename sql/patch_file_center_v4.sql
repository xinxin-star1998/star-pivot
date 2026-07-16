-- 文件中心：版本管理 + 分享管理页 + 存储统计
-- 若表/菜单已存在，对应语句报错可忽略后继续

-- 1. 文件历史版本（仅存被替换的快照；当前内容仍在 sys_file）
CREATE TABLE IF NOT EXISTS `sys_file_version` (
  `version_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '版本ID',
  `file_id` bigint(0) NOT NULL COMMENT '文件ID',
  `version_no` int(0) NOT NULL COMMENT '版本号（从1递增）',
  `object_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '存储对象名',
  `file_hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'SHA256',
  `file_size` bigint(0) NULL DEFAULT 0 COMMENT '大小',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '当时文件名',
  `content_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MIME',
  `storage_provider` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '存储驱动',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '归档人',
  `create_by_user_id` bigint(0) NULL DEFAULT NULL COMMENT '归档人ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '归档时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`version_id`) USING BTREE,
  UNIQUE INDEX `uk_file_version_no`(`file_id`, `version_no`) USING BTREE,
  INDEX `idx_version_file`(`file_id`) USING BTREE,
  INDEX `idx_version_object`(`object_name`(191)) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件历史版本' ROW_FORMAT = Dynamic;

-- 2. 菜单：版本按钮 + 我的分享页 + 存储统计页
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
(112, '文件版本', 25, 16, '', '', NULL, '', 1, 1, 'F', '0', '0', 'file:resource:version', '#', 'admin', NOW(), '', NULL, '上传新版本/历史/恢复'),
(113, '我的分享', 6, 2, 'shares', '/file/shares', NULL, 'FileShares', 1, 1, 'C', '0', '0', 'file:resource:share', 'ri:share-forward-line', 'admin', NOW(), '', NULL, '分享外链管理'),
(114, '存储统计', 6, 3, 'stats', '/file/stats', NULL, 'FileStats', 1, 1, 'C', '0', '0', 'file:resource:stats', 'ri:pie-chart-2-line', 'admin', NOW(), '', NULL, '存储用量统计');

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.role_id, m.menu_id
FROM `sys_role` r
CROSS JOIN (SELECT 112 AS menu_id UNION ALL SELECT 113 UNION ALL SELECT 114) m
WHERE (r.role_key = 'admin' OR r.role_id = 1)
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_menu` rm WHERE rm.role_id = r.role_id AND rm.menu_id = m.menu_id
  );
