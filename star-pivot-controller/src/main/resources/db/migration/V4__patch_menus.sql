-- =============================================================================
-- StarPivot V4: Menu patches (external gen + workflow menus)
-- =============================================================================
SET NAMES utf8mb4;

-- =============================================================================
-- External code generator menus (from patch_external_gen_menu.sql)
-- =============================================================================

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

-- =============================================================================
-- Workflow menus (from patch_workflow_menu.sql)
-- =============================================================================

-- 目录：工作流
INSERT INTO `sys_menu` VALUES (180, '工作流', 0, 6, '/workflow', '', NULL, 'Workflow', 1, 1, 'M', '0', '0', '', 'ep:connection', 'admin', NOW(), '', NULL, '工作流模块');

-- 菜单
INSERT INTO `sys_menu` VALUES (181, '流程定义', 180, 1, 'def', '/workflow/def/index', NULL, 'WorkflowDef', 1, 1, 'C', '0', '0', 'workflow:def:list', 'ep:document', 'admin', NOW(), '', NULL, '流程定义列表');
INSERT INTO `sys_menu` VALUES (182, '流程设计', 180, 2, 'designer', '/workflow/designer/index', NULL, 'WorkflowDesigner', 1, 0, 'C', '1', '0', 'workflow:def:edit', 'ep:edit', 'admin', NOW(), '', NULL, '流程设计器（从列表进入）');
INSERT INTO `sys_menu` VALUES (183, '待办审批', 180, 3, 'approval', '/workflow/approval/index', NULL, 'WorkflowApproval', 1, 1, 'C', '0', '0', 'workflow:todo:list', 'ep:bell', 'admin', NOW(), '', NULL, '待办审批');
INSERT INTO `sys_menu` VALUES (184, '我发起的', 180, 4, 'mine', '/workflow/mine/index', NULL, 'WorkflowMine', 1, 1, 'C', '0', '0', 'workflow:mine:list', 'ep:user', 'admin', NOW(), '', NULL, '我发起的流程');
INSERT INTO `sys_menu` VALUES (195, '流程进度', 180, 5, 'flowchart', '/workflow/flowchart/index', NULL, 'WorkflowFlowchart', 1, 1, 'C', '0', '0', 'workflow:instance:progress', 'ep:data-line', 'admin', NOW(), '', NULL, '流程实例进度图');

-- 按钮权限
INSERT INTO `sys_menu` VALUES (185, '流程定义查询', 181, 1, '', '', NULL, '', 1, 0, 'F', '0', '0', 'workflow:def:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (186, '流程定义编辑', 181, 2, '', '', NULL, '', 1, 0, 'F', '0', '0', 'workflow:def:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (187, '流程定义发布', 181, 3, '', '', NULL, '', 1, 0, 'F', '0', '0', 'workflow:def:publish', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (188, '流程定义删除', 181, 4, '', '', NULL, '', 1, 0, 'F', '0', '0', 'workflow:def:delete', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (189, '待办查询', 183, 1, '', '', NULL, '', 1, 0, 'F', '0', '0', 'workflow:todo:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (190, '审批操作', 183, 2, '', '', NULL, '', 1, 0, 'F', '0', '0', 'workflow:task:approve', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (191, '发起流程', 183, 3, '', '', NULL, '', 1, 0, 'F', '0', '0', 'workflow:instance:start', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (192, '撤销流程', 184, 1, '', '', NULL, '', 1, 0, 'F', '0', '0', 'workflow:instance:cancel', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (193, '已办查询', 183, 4, '', '', NULL, '', 1, 0, 'F', '0', '0', 'workflow:done:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (194, '我发起查询', 184, 2, '', '', NULL, '', 1, 0, 'F', '0', '0', 'workflow:mine:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (196, '实例进度查询', 195, 1, '', '', NULL, '', 1, 0, 'F', '0', '0', 'workflow:instance:progress', '#', 'admin', NOW(), '', NULL, '');

-- 管理员角色授权（role_id=1）
INSERT INTO `sys_role_menu` VALUES (900, 1, 180);
INSERT INTO `sys_role_menu` VALUES (901, 1, 181);
INSERT INTO `sys_role_menu` VALUES (902, 1, 182);
INSERT INTO `sys_role_menu` VALUES (903, 1, 183);
INSERT INTO `sys_role_menu` VALUES (904, 1, 184);
INSERT INTO `sys_role_menu` VALUES (905, 1, 185);
INSERT INTO `sys_role_menu` VALUES (906, 1, 186);
INSERT INTO `sys_role_menu` VALUES (907, 1, 187);
INSERT INTO `sys_role_menu` VALUES (908, 1, 188);
INSERT INTO `sys_role_menu` VALUES (909, 1, 189);
INSERT INTO `sys_role_menu` VALUES (910, 1, 190);
INSERT INTO `sys_role_menu` VALUES (911, 1, 191);
INSERT INTO `sys_role_menu` VALUES (912, 1, 192);
INSERT INTO `sys_role_menu` VALUES (913, 1, 193);
INSERT INTO `sys_role_menu` VALUES (914, 1, 194);
INSERT INTO `sys_role_menu` VALUES (915, 1, 195);
INSERT INTO `sys_role_menu` VALUES (916, 1, 196);
