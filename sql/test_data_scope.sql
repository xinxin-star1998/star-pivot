-- ============================================
-- 数据权限功能测试数据脚本
-- ============================================

-- 1. 创建测试角色（如果不存在）
-- 注意：执行前请先查询是否已存在，避免重复插入

-- 全部数据权限测试角色
INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, status, del_flag, create_by, create_time) 
SELECT '全部权限测试角色', 'test_all', 10, '1', '0', '0', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'test_all');

-- 自定数据权限测试角色
INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, status, del_flag, create_by, create_time) 
SELECT '自定权限测试角色', 'test_custom', 11, '2', '0', '0', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'test_custom');

-- 本部门数据权限测试角色
INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, status, del_flag, create_by, create_time) 
SELECT '本部门权限测试角色', 'test_dept', 12, '3', '0', '0', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'test_dept');

-- 本部门及以下数据权限测试角色
INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, status, del_flag, create_by, create_time) 
SELECT '本部门及以下权限测试角色', 'test_dept_child', 13, '4', '0', '0', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'test_dept_child');

-- 仅本人数据权限测试角色
INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, status, del_flag, create_by, create_time) 
SELECT '仅本人权限测试角色', 'test_self', 14, '5', '0', '0', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'test_self');

-- 2. 查询角色ID（用于后续关联）
-- SELECT role_id, role_name, role_key, data_scope FROM sys_role WHERE role_key LIKE 'test_%';

-- 3. 为自定权限角色分配部门（假设部门101和103存在）
-- 注意：请根据实际部门ID调整
INSERT INTO sys_role_dept (role_id, dept_id)
SELECT r.role_id, 101
FROM sys_role r
WHERE r.role_key = 'test_custom'
AND NOT EXISTS (SELECT 1 FROM sys_role_dept WHERE role_id = r.role_id AND dept_id = 101);

INSERT INTO sys_role_dept (role_id, dept_id)
SELECT r.role_id, 103
FROM sys_role r
WHERE r.role_key = 'test_custom'
AND NOT EXISTS (SELECT 1 FROM sys_role_dept WHERE role_id = r.role_id AND dept_id = 103);

-- 4. 创建测试用户（密码：123456，需要根据实际加密方式调整）
-- 注意：密码需要使用 BCrypt 加密，这里只是示例

-- 全部权限测试用户
INSERT INTO sys_user (user_name, nick_name, dept_id, password, status, del_flag, create_by, create_time) 
SELECT 'test_all_user', '全部权限用户', 101, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJw2', '0', '0', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE user_name = 'test_all_user');

-- 自定权限测试用户
INSERT INTO sys_user (user_name, nick_name, dept_id, password, status, del_flag, create_by, create_time) 
SELECT 'test_custom_user', '自定权限用户', 101, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJw2', '0', '0', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE user_name = 'test_custom_user');

-- 本部门权限测试用户
INSERT INTO sys_user (user_name, nick_name, dept_id, password, status, del_flag, create_by, create_time) 
SELECT 'test_dept_user', '本部门权限用户', 101, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJw2', '0', '0', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE user_name = 'test_dept_user');

-- 本部门及以下权限测试用户
INSERT INTO sys_user (user_name, nick_name, dept_id, password, status, del_flag, create_by, create_time) 
SELECT 'test_dept_child_user', '本部门及以下权限用户', 101, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJw2', '0', '0', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE user_name = 'test_dept_child_user');

-- 仅本人权限测试用户
INSERT INTO sys_user (user_name, nick_name, dept_id, password, status, del_flag, create_by, create_time) 
SELECT 'test_self_user', '仅本人权限用户', 101, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJw2', '0', '0', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE user_name = 'test_self_user');

-- 5. 关联用户和角色
-- 全部权限用户
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.user_id, r.role_id
FROM sys_user u, sys_role r
WHERE u.user_name = 'test_all_user' AND r.role_key = 'test_all'
AND NOT EXISTS (SELECT 1 FROM sys_user_role WHERE user_id = u.user_id AND role_id = r.role_id);

-- 自定权限用户
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.user_id, r.role_id
FROM sys_user u, sys_role r
WHERE u.user_name = 'test_custom_user' AND r.role_key = 'test_custom'
AND NOT EXISTS (SELECT 1 FROM sys_user_role WHERE user_id = u.user_id AND role_id = r.role_id);

-- 本部门权限用户
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.user_id, r.role_id
FROM sys_user u, sys_role r
WHERE u.user_name = 'test_dept_user' AND r.role_key = 'test_dept'
AND NOT EXISTS (SELECT 1 FROM sys_user_role WHERE user_id = u.user_id AND role_id = r.role_id);

-- 本部门及以下权限用户
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.user_id, r.role_id
FROM sys_user u, sys_role r
WHERE u.user_name = 'test_dept_child_user' AND r.role_key = 'test_dept_child'
AND NOT EXISTS (SELECT 1 FROM sys_user_role WHERE user_id = u.user_id AND role_id = r.role_id);

-- 仅本人权限用户
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.user_id, r.role_id
FROM sys_user u, sys_role r
WHERE u.user_name = 'test_self_user' AND r.role_key = 'test_self'
AND NOT EXISTS (SELECT 1 FROM sys_user_role WHERE user_id = u.user_id AND role_id = r.role_id);

-- 6. 创建测试数据用户（用于验证查询结果）
-- 部门101的用户
INSERT INTO sys_user (user_name, nick_name, dept_id, password, status, del_flag, create_by, create_time) 
SELECT 'dept101_user1', '部门101用户1', 101, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJw2', '0', '0', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE user_name = 'dept101_user1');

INSERT INTO sys_user (user_name, nick_name, dept_id, password, status, del_flag, create_by, create_time) 
SELECT 'dept101_user2', '部门101用户2', 101, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJw2', '0', '0', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE user_name = 'dept101_user2');

-- 部门103的用户（101的子部门，如果存在）
INSERT INTO sys_user (user_name, nick_name, dept_id, password, status, del_flag, create_by, create_time) 
SELECT 'dept103_user1', '部门103用户1', 103, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJw2', '0', '0', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE user_name = 'dept103_user1')
AND EXISTS (SELECT 1 FROM sys_dept WHERE dept_id = 103);

-- 部门102的用户（与101平级）
INSERT INTO sys_user (user_name, nick_name, dept_id, password, status, del_flag, create_by, create_time) 
SELECT 'dept102_user1', '部门102用户1', 102, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJw2', '0', '0', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE user_name = 'dept102_user1')
AND EXISTS (SELECT 1 FROM sys_dept WHERE dept_id = 102);

-- ============================================
-- 验证查询SQL
-- ============================================

-- 1. 查看所有测试角色
SELECT role_id, role_name, role_key, data_scope, status 
FROM sys_role 
WHERE role_key LIKE 'test_%'
ORDER BY role_sort;

-- 2. 查看测试用户及其角色
SELECT u.user_name, u.nick_name, u.dept_id, r.role_name, r.data_scope
FROM sys_user u
INNER JOIN sys_user_role ur ON u.user_id = ur.user_id
INNER JOIN sys_role r ON ur.role_id = r.role_id
WHERE u.user_name LIKE 'test_%'
ORDER BY u.user_name, r.role_sort;

-- 3. 查看自定权限角色关联的部门
SELECT r.role_name, rd.dept_id, d.dept_name
FROM sys_role r
INNER JOIN sys_role_dept rd ON r.role_id = rd.role_id
INNER JOIN sys_dept d ON rd.dept_id = d.dept_id
WHERE r.role_key = 'test_custom';

-- 4. 查看部门层级关系
SELECT dept_id, dept_name, parent_id, ancestors 
FROM sys_dept 
WHERE del_flag = '0' 
ORDER BY dept_id;

-- 5. 验证数据权限过滤（本部门权限用户应该只能看到部门101的用户）
SELECT u.user_id, u.user_name, u.nick_name, u.dept_id, d.dept_name
FROM sys_user u
LEFT JOIN sys_dept d ON u.dept_id = d.dept_id
WHERE u.del_flag = '0' 
AND u.dept_id = 101  -- 本部门权限过滤条件
ORDER BY u.create_time DESC;

-- 6. 验证本部门及以下权限（应该能看到部门101及其子部门的用户）
SELECT u.user_id, u.user_name, u.nick_name, u.dept_id, d.dept_name, d.ancestors
FROM sys_user u
LEFT JOIN sys_dept d ON u.dept_id = d.dept_id
WHERE u.del_flag = '0' 
AND (
    u.dept_id = 101  -- 本部门
    OR d.ancestors LIKE '%,101,%'  -- 子部门
    OR d.ancestors LIKE '%,101' 
    OR d.ancestors = '101'
)
ORDER BY u.create_time DESC;

-- ============================================
-- 清理测试数据（谨慎使用）
-- ============================================

-- 删除测试用户角色关联
-- DELETE FROM sys_user_role 
-- WHERE user_id IN (SELECT user_id FROM sys_user WHERE user_name LIKE 'test_%');

-- 删除测试用户
-- DELETE FROM sys_user WHERE user_name LIKE 'test_%' OR user_name LIKE 'dept%_user%';

-- 删除测试角色部门关联
-- DELETE FROM sys_role_dept 
-- WHERE role_id IN (SELECT role_id FROM sys_role WHERE role_key LIKE 'test_%');

-- 删除测试角色
-- DELETE FROM sys_role WHERE role_key LIKE 'test_%';
