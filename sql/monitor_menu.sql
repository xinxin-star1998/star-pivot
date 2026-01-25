-- 系统监控模块菜单和权限 SQL
-- 执行前请确保已登录系统，并具有菜单管理权限

-- 1. 插入监控模块父菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('系统监控', 0, 5, 'monitor', NULL, 'M', '0', '0', NULL, 'ri:monitor-line', 'admin', NOW(), '系统监控模块');

-- 获取刚插入的监控模块父菜单ID（假设为 @monitor_parent_id）
SET @monitor_parent_id = LAST_INSERT_ID();

-- 2. 插入服务器监控菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('服务器监控', @monitor_parent_id, 1, 'server', 'monitor/server/index', 'C', '0', '0', 'monitor:server:query', 'ri:server-line', 'admin', NOW(), '服务器信息监控');

-- 3. 插入 Druid 监控菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('Druid监控', @monitor_parent_id, 2, 'druid', 'monitor/druid/index', 'C', '0', '0', 'monitor:druid:query', 'ri:database-2-line', 'admin', NOW(), 'Druid数据库连接池监控');

-- 4. 插入 Redis 监控菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('Redis监控', @monitor_parent_id, 3, 'redis', 'monitor/redis/index', 'C', '0', '0', 'monitor:redis:query', 'ri:database-line', 'admin', NOW(), 'Redis缓存监控');

-- 5. 插入在线用户菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('在线用户', @monitor_parent_id, 4, 'online', 'monitor/online/index', 'C', '0', '0', 'monitor:online:query', 'ri:user-line', 'admin', NOW(), '在线用户监控');

-- 6. 插入强制下线按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('强制下线', @monitor_parent_id, 5, '', '', 'F', '0', '0', 'monitor:online:force-logout', '', 'admin', NOW(), '强制用户下线');

-- 注意：
-- 1. 执行此 SQL 后，需要在菜单管理页面刷新菜单树
-- 2. 需要为相应的角色分配这些菜单权限
-- 3. 如果 parent_id 为 0 的菜单已存在，请手动调整 @monitor_parent_id 的值
-- 4. 菜单路径（path）会自动拼接父路径，最终路径为 /monitor/server、/monitor/druid 等
