/*
 * 数据库索引优化SQL
 * 
 * 根据项目优化分析报告，为常用查询字段添加索引，提升查询性能
 * 
 * 执行说明：
 * 1. 在生产环境执行前，请先在测试环境验证
 * 2. 建议在业务低峰期执行
 * 3. 执行前备份数据库
 * 
 * 生成时间：2026-01-25
 */

-- ============================================
-- 用户表索引优化
-- ============================================

-- 用户名索引（登录、查询常用字段）
CREATE INDEX IF NOT EXISTS `idx_user_name` ON `sys_user`(`user_name`) USING BTREE;

-- 手机号索引（用户查询、找回密码等场景）
CREATE INDEX IF NOT EXISTS `idx_phonenumber` ON `sys_user`(`phonenumber`) USING BTREE;

-- 邮箱索引（用户查询、找回密码等场景）
CREATE INDEX IF NOT EXISTS `idx_email` ON `sys_user`(`email`) USING BTREE;

-- 部门ID索引（按部门查询用户）
CREATE INDEX IF NOT EXISTS `idx_dept_id` ON `sys_user`(`dept_id`) USING BTREE;

-- 状态索引（按状态查询用户）
CREATE INDEX IF NOT EXISTS `idx_status` ON `sys_user`(`status`) USING BTREE;

-- 删除标志索引（过滤已删除用户）
CREATE INDEX IF NOT EXISTS `idx_del_flag` ON `sys_user`(`del_flag`) USING BTREE;

-- 复合索引：部门ID + 状态（常用组合查询）
CREATE INDEX IF NOT EXISTS `idx_dept_status` ON `sys_user`(`dept_id`, `status`) USING BTREE;

-- ============================================
-- 操作日志表索引优化（如已有索引可跳过）
-- ============================================

-- 操作时间索引（时间范围查询）
-- 注意：如果 sys_oper_log 表已有 idx_sys_oper_log_ot 索引，可跳过
-- CREATE INDEX IF NOT EXISTS `idx_oper_time` ON `sys_oper_log`(`oper_time`) USING BTREE;

-- 操作人索引（按操作人查询）
-- 注意：如果 sys_oper_log 表已有相关索引，可跳过
-- CREATE INDEX IF NOT EXISTS `idx_oper_name` ON `sys_oper_log`(`oper_name`) USING BTREE;

-- ============================================
-- 登录日志表索引优化（如已有索引可跳过）
-- ============================================

-- 登录时间索引（时间范围查询）
-- 注意：如果 sys_logininfor 表已有 idx_sys_logininfor_lt 索引，可跳过
-- CREATE INDEX IF NOT EXISTS `idx_login_time` ON `sys_logininfor`(`login_time`) USING BTREE;

-- 用户名索引（按用户查询登录记录）
-- 注意：如果 sys_logininfor 表已有 idx_sys_logininfor_s 索引，可跳过
-- CREATE INDEX IF NOT EXISTS `idx_user_name` ON `sys_logininfor`(`user_name`) USING BTREE;

-- ============================================
-- 索引使用建议
-- ============================================

/*
 * 索引维护建议：
 * 
 * 1. 定期检查索引使用情况：
 *    SELECT * FROM information_schema.STATISTICS 
 *    WHERE TABLE_SCHEMA = 'star-pivot' AND TABLE_NAME = 'sys_user';
 * 
 * 2. 分析慢查询，根据实际查询场景调整索引
 * 
 * 3. 避免过度索引，索引过多会影响写入性能
 * 
 * 4. 对于复合索引，遵循最左前缀原则
 */
