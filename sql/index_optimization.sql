/*
 * StarPivot 数据库索引优化脚本
 * 执行前请备份数据库
 * 建议在业务低峰期执行
 */

-- ============================================
-- 1. sys_user 表索引优化
-- ============================================

-- 用户账号唯一索引（如未创建）
ALTER TABLE sys_user ADD UNIQUE INDEX uk_user_name (user_name);

-- 部门ID + 删除标志 + 状态 复合索引（用于数据权限过滤）
ALTER TABLE sys_user ADD INDEX idx_dept_del_status (dept_id, del_flag, status);

-- 创建时间倒序索引（用于分页排序）
ALTER TABLE sys_user ADD INDEX idx_create_time (create_time DESC);

-- 手机号索引（用于查询）
ALTER TABLE sys_user ADD INDEX idx_phonenumber (phonenumber);

-- 邮箱索引（用于查询）
ALTER TABLE sys_user ADD INDEX idx_email (email);

-- 用户状态 + 删除标志 复合索引（用于常用查询条件）
ALTER TABLE sys_user ADD INDEX idx_status_del (status, del_flag);

-- ============================================
-- 2. sys_user_role 表索引优化
-- ============================================

-- 用户ID + 角色ID 联合唯一索引（避免重复关联）
ALTER TABLE sys_user_role ADD UNIQUE INDEX uk_user_role (user_id, role_id);

-- 角色ID 索引（用于角色查询用户）
ALTER TABLE sys_user_role ADD INDEX idx_role_id (role_id);

-- ============================================
-- 3. sys_role 表索引优化
-- ============================================

-- 角色标识唯一索引（如未创建）
ALTER TABLE sys_role ADD UNIQUE INDEX uk_role_key (role_key);

-- 删除标志 + 状态 复合索引（用于常用过滤条件）
ALTER TABLE sys_role ADD INDEX idx_del_status (del_flag, status);

-- 创建时间倒序索引（用于排序）
ALTER TABLE sys_role ADD INDEX idx_create_time (create_time DESC);

-- ============================================
-- 4. sys_menu 表索引优化
-- ============================================

-- 父菜单ID + 排序号 复合索引（用于菜单树构建）
ALTER TABLE sys_menu ADD INDEX idx_parent_sort (parent_id, order_num);

-- 可见性 + 状态 复合索引（用于权限查询）
ALTER TABLE sys_menu ADD INDEX idx_visible_status (visible, status);

-- 菜单类型 + 状态 复合索引（用于分类查询）
ALTER TABLE sys_menu ADD INDEX idx_menu_type_status (menu_type, status);

-- ============================================
-- 5. sys_role_menu 表索引优化
-- ============================================

-- 角色ID + 菜单ID 联合唯一索引（避免重复关联）
ALTER TABLE sys_role_menu ADD UNIQUE INDEX uk_role_menu (role_id, menu_id);

-- 菜单ID 索引（用于菜单反向查询角色）
ALTER TABLE sys_role_menu ADD INDEX idx_menu_id (menu_id);

-- ============================================
-- 6. sys_dept 表索引优化
-- ============================================

-- 父部门ID 索引（用于部门树构建）
ALTER TABLE sys_dept ADD INDEX idx_parent_id (parent_id);

-- 删除标志 + 状态 复合索引
ALTER TABLE sys_dept ADD INDEX idx_del_status (del_flag, status);

-- 祖先路径 前缀索引（用于层级查询）
ALTER TABLE sys_dept ADD INDEX idx_ancestors (ancestors(100));

-- ============================================
-- 7. sys_post 表索引优化
-- ============================================

-- 岗位编码唯一索引（如未创建）
ALTER TABLE sys_post ADD UNIQUE INDEX uk_post_code (post_code);

-- 删除标志 + 状态 复合索引
ALTER TABLE sys_post ADD INDEX idx_del_status (del_flag, status);

-- ============================================
-- 8. sys_user_post 表索引优化
-- ============================================

-- 用户ID + 岗位ID 联合唯一索引
ALTER TABLE sys_user_post ADD UNIQUE INDEX uk_user_post (user_id, post_id);

-- 岗位ID 索引
ALTER TABLE sys_user_post ADD INDEX idx_post_id (post_id);

-- ============================================
-- 9. sys_dict_type 表索引优化
-- ============================================

-- 字典类型唯一索引（如未创建）
ALTER TABLE sys_dict_type ADD UNIQUE INDEX uk_dict_type (dict_type);

-- 删除标志 + 状态 复合索引
ALTER TABLE sys_dict_type ADD INDEX idx_del_status (del_flag, status);

-- ============================================
-- 10. sys_dict_data 表索引优化
-- ============================================

-- 字典类型 + 字典排序 复合索引（用于字典项列表查询）
ALTER TABLE sys_dict_data ADD INDEX idx_type_sort (dict_type, dict_sort);

-- 字典类型 + 字典值 复合索引（用于字典值查询）
ALTER TABLE sys_dict_data ADD INDEX idx_type_value (dict_type, dict_value);

-- 删除标志 + 状态 复合索引
ALTER TABLE sys_dict_data ADD INDEX idx_del_status (del_flag, status);

-- 字典标签 索引（用于标签查询）
ALTER TABLE sys_dict_data ADD INDEX idx_dict_label (dict_label);

-- ============================================
-- 11. sys_logininfor 表索引优化（日志表）
-- ============================================

-- 用户账号 + 登录时间 复合索引（用于登录日志查询）
ALTER TABLE sys_logininfor ADD INDEX idx_user_time (user_name, login_time DESC);

-- IP地址 索引（用于IP查询）
ALTER TABLE sys_logininfor ADD INDEX idx_ipaddr (ipaddr);

-- 登录状态 索引（用于状态筛选）
ALTER TABLE sys_logininfor ADD INDEX idx_status (status);

-- ============================================
-- 12. sys_oper_log 表索引优化（操作日志表）
-- ============================================

-- 操作模块 + 操作时间 复合索引
ALTER TABLE sys_oper_log ADD INDEX idx_module_time (title, oper_time DESC);

-- 操作人员 索引
ALTER TABLE sys_oper_log ADD INDEX idx_oper_name (oper_name);

-- 操作状态 索引
ALTER TABLE sys_oper_log ADD INDEX idx_status (status);

-- ============================================
-- 索引查看命令（执行后验证）
-- ============================================

-- 查看 sys_user 表的所有索引
-- SHOW INDEX FROM sys_user;

-- 查看 sys_user_role 表的所有索引
-- SHOW INDEX FROM sys_user_role;

-- 查看 SQL 执行计划（验证索引是否生效）
-- EXPLAIN SELECT * FROM sys_user WHERE user_name = 'admin';
-- EXPLAIN SELECT * FROM sys_user u WHERE NOT EXISTS (
--     SELECT 1 FROM sys_user_role ur2 
--     WHERE ur2.user_id = u.user_id 
--     AND ur2.role_id = 1
-- );

-- ============================================
-- 注意事项
-- ============================================
-- 1. 执行前请备份数据库
-- 2. 建议在业务低峰期执行
-- 3. 大数据表添加索引可能需要较长时间，请耐心等待
-- 4. 可以使用 pt-online-schema-change 工具进行在线DDL（Percona Toolkit）
-- 5. 索引不是越多越好，需要根据实际查询情况调整
