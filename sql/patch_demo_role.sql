-- 演示角色与演示用户
-- 账号：demo / 密码：demo123
-- 效果：所有菜单与按钮均可见，写操作（提交/删除/新增等）提示「演示模式，不允许操作」

-- 1. 演示角色（roleKey = demo 触发演示模式）
DELETE FROM `sys_user_role` WHERE user_id = 120;
DELETE FROM `sys_user` WHERE user_id = 120;
DELETE FROM `sys_role_menu` WHERE role_id = 120;
DELETE FROM `sys_role` WHERE role_id = 120;

INSERT INTO `sys_role` VALUES (
  120, '演示角色', 'demo', 10, '4', 1, 1, '0', '0',
  'admin', NOW(), '', NULL,
  '演示专用：全按钮可见，写操作由演示模式拦截'
);

-- 2. 演示用户（密码 demo123，BCrypt）
INSERT INTO `sys_user` VALUES (
  120, 100, 'demo', '演示用户', '00', 'demo@starpivot.org.cn', '18800000001', '0', '',
  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.uhwy36zJOrad/E0xohtX6lK',
  '0', '0', '', NULL, NULL,
  'admin', NOW(), '', NULL,
  '演示账号，密码 demo123'
);

INSERT INTO `sys_user_role` VALUES (220, 120, 120);

-- 3. 菜单权限：所有启用菜单（含全部按钮 F），保证 UI 按钮完整展示
INSERT INTO `sys_role_menu` (role_id, menu_id)
SELECT 120, m.menu_id
FROM `sys_menu` m
WHERE m.status = '0';

-- 4. 若已存在「演示专属 yanshi」角色，同步为全菜单（可选，starPivot 等账号生效）
DELETE FROM `sys_role_menu` WHERE role_id = 4;
INSERT INTO `sys_role_menu` (role_id, menu_id)
SELECT 4, m.menu_id
FROM `sys_menu` m
WHERE m.status = '0';
