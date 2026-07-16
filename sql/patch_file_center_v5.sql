-- 文件中心：操作审计 + 增强检索（无需改表结构，检索走 SQL）
-- 若表/菜单已存在，对应语句报错可忽略后继续

CREATE TABLE IF NOT EXISTS `sys_file_audit` (
  `audit_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '审计ID',
  `file_id` bigint(0) NULL DEFAULT NULL COMMENT '文件ID（删除后可空）',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件名快照',
  `action` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '动作',
  `detail` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '详情',
  `oper_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作人',
  `oper_by_user_id` bigint(0) NULL DEFAULT NULL COMMENT '操作人ID',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'IP',
  `oper_time` datetime(0) NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`audit_id`) USING BTREE,
  INDEX `idx_audit_file`(`file_id`) USING BTREE,
  INDEX `idx_audit_action_time`(`action`, `oper_time`) USING BTREE,
  INDEX `idx_audit_user_time`(`oper_by_user_id`, `oper_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件操作审计' ROW_FORMAT = Dynamic;

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
(115, '文件审计', 6, 4, 'audit', '/file/audit', NULL, 'FileAudit', 1, 1, 'C', '0', '0', 'file:resource:audit', 'ri:file-list-3-line', 'admin', NOW(), '', NULL, '文件操作审计日志');

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.role_id, 115
FROM `sys_role` r
WHERE (r.role_key = 'admin' OR r.role_id = 1)
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_menu` rm WHERE rm.role_id = r.role_id AND rm.menu_id = 115
  );
