-- 文件中心增强：彻底删除权限、去重索引、数据权限字段
-- 执行前请备份。若列/索引已存在，对应语句报错可忽略后继续。

-- 1. 数据权限字段
ALTER TABLE `sys_file`
  ADD COLUMN `create_by_user_id` bigint(0) NULL DEFAULT NULL COMMENT '上传人用户ID' AFTER `create_by`;
ALTER TABLE `sys_file`
  ADD COLUMN `create_dept_id` bigint(0) NULL DEFAULT NULL COMMENT '上传人部门ID' AFTER `create_by_user_id`;

-- 2. 去重：取消 object_name 唯一约束，允许多条元数据共享同一对象；增加 hash 索引
ALTER TABLE `sys_file` DROP INDEX `uk_object_name`;
ALTER TABLE `sys_file` ADD INDEX `idx_object_name`(`object_name`) USING BTREE;
ALTER TABLE `sys_file` ADD INDEX `idx_file_hash`(`file_hash`, `file_size`, `del_flag`) USING BTREE;
ALTER TABLE `sys_file` ADD INDEX `idx_create_user`(`create_by_user_id`, `del_flag`) USING BTREE;
ALTER TABLE `sys_file` ADD INDEX `idx_create_dept`(`create_dept_id`, `del_flag`) USING BTREE;

-- 3. 历史数据回填（按用户名匹配）
UPDATE `sys_file` f
  INNER JOIN `sys_user` u ON u.user_name = f.create_by AND u.del_flag = '0'
SET f.create_by_user_id = u.user_id,
    f.create_dept_id = u.dept_id
WHERE f.create_by_user_id IS NULL
  AND f.create_by IS NOT NULL
  AND f.create_by != '';

-- 4. 按钮权限
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
(107, '彻底删除', 25, 11, '', '', NULL, '', 1, 1, 'F', '0', '0', 'file:resource:purge', '#', 'admin', NOW(), '', NULL, '回收站彻底删除并清理OSS'),
(108, '清空回收站', 25, 12, '', '', NULL, '', 1, 1, 'F', '0', '0', 'file:resource:purge', '#', 'admin', NOW(), '', NULL, '清空回收站（与彻底删除同权限）');

-- 5. 给 admin 角色同步新菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.role_id, m.menu_id
FROM `sys_role` r
CROSS JOIN `sys_menu` m
WHERE m.menu_id IN (107, 108)
  AND (r.role_key = 'admin' OR r.role_id = 1)
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_menu` rm WHERE rm.role_id = r.role_id AND rm.menu_id = m.menu_id
  );
