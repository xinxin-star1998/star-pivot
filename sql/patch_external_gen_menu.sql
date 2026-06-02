-- 外部库代码生成：独立菜单（与菜单管理界面 tool:external:* 权限一致）
-- 执行后请重新登录；组件路径须为 /tools/generator-external/index

INSERT INTO `sys_menu` VALUES (130, '外部库代码生成', 2, 2, 'external', '/tools/generator-external/index', NULL, 'GenExternal', 1, 1, 'C', '0', '0', '', 'ri:database-2-line', 'admin', NOW(), 'admin', NOW(), '外部数据库连接代码生成')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO `sys_menu` VALUES (131, '查询', 130, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:external:query', '#', 'admin', NOW(), '', NULL, '连接、查表、路径配置')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), perms = VALUES(perms);

INSERT INTO `sys_menu` VALUES (132, '预览', 130, 2, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:external:preview', '#', 'admin', NOW(), '', NULL, '预览代码')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), perms = VALUES(perms);

INSERT INTO `sys_menu` VALUES (133, '生成', 130, 3, '', '', NULL, '', 1, 1, 'F', '0', '0', 'tool:external:create', '#', 'admin', NOW(), '', NULL, '下载 ZIP')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), perms = VALUES(perms);

-- 清理旧版 tool:gen:* 按钮（若曾执行过旧脚本）
DELETE FROM `sys_role_menu` WHERE menu_id IN (134);
DELETE FROM `sys_menu` WHERE menu_id = 134;

INSERT IGNORE INTO `sys_role_menu` (role_id, menu_id) VALUES
  (1, 130), (1, 131), (1, 132), (1, 133);
