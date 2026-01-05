-- 自动生成的菜单数据
-- 生成时间: 2026/1/5 13:58:32
-- 说明: 按层级顺序插入，确保父菜单先插入

DELETE FROM sys_menu;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, route_name, is_frame, is_cache, menu_type, visible, status, icon, perms, create_by, create_time, remark) VALUES
('仪表盘', 0, 1, '/dashboard', '/index/index', 'Dashboard', 1, 0, 'M', '0', '0', 'ri:pie-chart-line', NULL, 'system', NOW(), '仪表盘模块'),
('系统管理', 0, 2, '/system', '/index/index', 'System', 1, 0, 'M', '0', '0', 'ri:user-3-line', NULL, 'system', NOW(), '系统管理模块'),
('控制台', 1, 1, '/dashboard/console', '/dashboard/console', 'DashboardConsole', 1, 0, 'C', '0', '0', NULL, NULL, 'system', NOW(), '控制台模块'),
('dept', 2, 1, '/system/dept', '/system/dept', 'SystemDept', 1, 0, 'C', '0', '0', NULL, NULL, 'system', NOW(), 'dept模块'),
('系统管理', 2, 2, '/system/dict', '/index/index', 'SystemDict', 1, 0, 'M', '0', '0', 'ri:user-3-line', NULL, 'system', NOW(), '系统管理模块'),
('菜单管理', 2, 3, '/system/menu', '/system/menu', 'SystemMenu', 1, 0, 'C', '0', '0', NULL, NULL, 'system', NOW(), '菜单管理模块'),
('post', 2, 4, '/system/post', '/system/post', 'SystemPost', 1, 0, 'C', '0', '0', NULL, NULL, 'system', NOW(), 'post模块'),
('角色管理', 2, 5, '/system/role', '/system/role', 'SystemRole', 1, 0, 'C', '0', '0', NULL, NULL, 'system', NOW(), '角色管理模块'),
('用户管理', 2, 6, '/system/user', '/system/user', 'SystemUser', 1, 0, 'C', '0', '0', NULL, NULL, 'system', NOW(), '用户管理模块'),
('个人中心', 2, 7, '/system/user-center', '/system/user-center', 'SystemUserCenter', 1, 0, 'C', '0', '0', NULL, NULL, 'system', NOW(), '个人中心模块'),
('data', 5, 1, '/system/dict/data', '/system/dict/data', 'SystemDictData', 1, 0, 'C', '0', '0', NULL, NULL, 'system', NOW(), 'data模块'),
('type', 5, 2, '/system/dict/type', '/system/dict/type', 'SystemDictType', 1, 0, 'C', '0', '0', NULL, NULL, 'system', NOW(), 'type模块');

-- 注意: 如果 parent_id 引用不正确，请手动调整
-- 建议: 先执行上面的 INSERT，然后根据实际的 menu_id 更新 parent_id