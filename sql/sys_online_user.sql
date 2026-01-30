-- ----------------------------
-- 在线用户记录表（历史记录）
-- ----------------------------
DROP TABLE IF EXISTS `sys_online_user`;
CREATE TABLE `sys_online_user` (
  `session_id` varchar(64) NOT NULL COMMENT '会话ID（Redis key）',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `user_name` varchar(50) DEFAULT '' COMMENT '登录账号',
  `nick_name` varchar(50) DEFAULT '' COMMENT '用户昵称',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `ipaddr` varchar(50) DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT '' COMMENT '操作系统',
  `status` char(1) DEFAULT '1' COMMENT '状态（0在线 1离线）',
  `start_timestamp` datetime DEFAULT NULL COMMENT '会话开始时间（登录时间）',
  `last_access_time` datetime DEFAULT NULL COMMENT '最后访问时间',
  `end_timestamp` datetime DEFAULT NULL COMMENT '会话结束时间（登出/强制下线时间）',
  `expire_time` int(11) DEFAULT NULL COMMENT '会话超时时间（秒）',
  `token_id` varchar(64) DEFAULT '' COMMENT '令牌标识',
  `logout_type` char(1) DEFAULT '0' COMMENT '下线类型（0正常登出 1强制下线 2过期下线）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间（记录入库时间）',
  PRIMARY KEY (`session_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_start_timestamp` (`start_timestamp`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='在线用户记录表（历史记录）';
