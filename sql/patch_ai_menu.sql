-- AI 中心菜单与权限
DELETE FROM `sys_menu` WHERE menu_id BETWEEN 334 AND 347;
INSERT INTO `sys_menu` VALUES (334, 'AI 中心', 0, 8, '/ai', '', NULL, 'AiCenter', 1, 1, 'M', '0', '0', '', 'ri:robot-2-line', 'admin', '2026-07-06 10:24:17', '', NULL, 'AI 智能对话管理');
INSERT INTO `sys_menu` VALUES (335, '基础配置', 334, 1, 'config', '/ai/config/index', NULL, 'AiConfig', 1, 1, 'C', '0', '0', 'ai:config:query', 'ep:setting', 'admin', '2026-07-06 10:24:17', '', NULL, 'AI 助手基础配置');
INSERT INTO `sys_menu` VALUES (336, '配置查询', 335, 1, '', '', NULL, '', 1, 0, 'F', '0', '0', 'ai:config:query', '#', 'admin', '2026-07-06 10:24:17', '', NULL, '');
INSERT INTO `sys_menu` VALUES (337, '配置编辑', 335, 2, '', '', NULL, '', 1, 0, 'F', '0', '0', 'ai:config:edit', '#', 'admin', '2026-07-06 10:24:17', '', NULL, '');
INSERT INTO `sys_menu` VALUES (338, '配置删除', 335, 3, '', '', NULL, '', 1, 0, 'F', '0', '0', 'ai:config:delete', '#', 'admin', '2026-07-06 10:24:17', '', NULL, '');
INSERT INTO `sys_menu` VALUES (339, '会话管理', 334, 2, 'session', '/ai/session/index', NULL, 'AiSession', 1, 1, 'C', '0', '0', 'ai:session:query', 'ri:chat-history-line', 'admin', '2026-07-06 10:30:18', '', NULL, 'AI 对话会话管理');
INSERT INTO `sys_menu` VALUES (340, '会话查询', 339, 1, '', '', NULL, '', 1, 1, 'F', '0', '0', 'ai:session:query', '#', 'admin', '2026-07-06 10:30:18', 'admin', '2026-07-06 10:37:14', '');
INSERT INTO `sys_menu` VALUES (341, '会话删除', 339, 2, '', '', NULL, '', 1, 0, 'F', '0', '0', 'ai:session:delete', '#', 'admin', '2026-07-06 10:30:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (342, '知识库', 334, 3, 'knowledge', '/ai/knowledge/index', NULL, 'AiKnowledge', 1, 1, 'C', '0', '0', 'ai:knowledge:query', 'ri:book-open-line', 'admin', '2026-07-06 10:39:22', '', NULL, 'AI 知识库管理');
INSERT INTO `sys_menu` VALUES (343, '知识库查询', 342, 1, '', '', NULL, '', 1, 0, 'F', '0', '0', 'ai:knowledge:query', '#', 'admin', '2026-07-06 10:39:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (344, '知识库编辑', 342, 2, '', '', NULL, '', 1, 0, 'F', '0', '0', 'ai:knowledge:edit', '#', 'admin', '2026-07-06 10:39:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (345, '知识库删除', 342, 3, '', '', NULL, '', 1, 0, 'F', '0', '0', 'ai:knowledge:delete', '#', 'admin', '2026-07-06 10:39:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (346, '用量统计', 334, 4, 'statistics', '/ai/statistics/index', NULL, 'AiStatistics', 1, 1, 'C', '0', '0', 'ai:statistics:query', 'ri:bar-chart-box-line', 'admin', '2026-07-06 10:39:22', '', NULL, 'AI 调用用量统计');
INSERT INTO `sys_menu` VALUES (347, '统计查询', 346, 1, '', '', NULL, '', 1, 0, 'F', '0', '0', 'ai:statistics:query', '#', 'admin', '2026-07-06 10:39:22', '', NULL, '');
