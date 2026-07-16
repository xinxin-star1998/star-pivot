-- 文件中心：多级文件夹 + 分享外链
-- 若索引/表已存在，对应语句报错可忽略后继续

-- 1. 多级文件夹：同级同名唯一
ALTER TABLE `sys_file_folder` DROP INDEX `uk_category_folder`;
ALTER TABLE `sys_file_folder`
  ADD UNIQUE INDEX `uk_category_parent_folder`(`category`, `parent_id`, `folder_name`, `del_flag`) USING BTREE;
ALTER TABLE `sys_file_folder`
  ADD INDEX `idx_folder_parent`(`parent_id`, `del_flag`) USING BTREE;
ALTER TABLE `sys_file_folder`
  MODIFY COLUMN `parent_id` bigint(0) NOT NULL DEFAULT 0 COMMENT '父文件夹ID（0=分类下根文件夹）';

-- 2. 分享外链表
CREATE TABLE IF NOT EXISTS `sys_file_share` (
  `share_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '分享ID',
  `file_id` bigint(0) NOT NULL COMMENT '文件ID',
  `share_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分享短码',
  `password_hash` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '访问密码BCrypt，空=无密码',
  `expire_time` datetime(0) NULL DEFAULT NULL COMMENT '过期时间，空=永不过期',
  `max_views` int(0) NULL DEFAULT NULL COMMENT '最大访问次数，空=不限',
  `view_count` int(0) NOT NULL DEFAULT 0 COMMENT '已访问次数',
  `allow_download` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT '是否允许下载 0否1是',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '0正常 1停用',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_by_user_id` bigint(0) NULL DEFAULT NULL COMMENT '创建人用户ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`share_id`) USING BTREE,
  UNIQUE INDEX `uk_share_code`(`share_code`) USING BTREE,
  INDEX `idx_share_file`(`file_id`) USING BTREE,
  INDEX `idx_share_creator`(`create_by_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件分享外链' ROW_FORMAT = Dynamic;

-- 3. 分享权限按钮
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
(109, '文件分享', 25, 13, '', '', NULL, '', 1, 1, 'F', '0', '0', 'file:resource:share', '#', 'admin', NOW(), '', NULL, '创建/管理分享外链');

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.role_id, 109
FROM `sys_role` r
WHERE (r.role_key = 'admin' OR r.role_id = 1)
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_menu` rm WHERE rm.role_id = r.role_id AND rm.menu_id = 109
  );
