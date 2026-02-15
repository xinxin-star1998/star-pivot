-- ----------------------------
-- 工作流审批流程相关表（可选自建业务表，与 Flowable ACT_* 表并存时可做业务摘要）
-- 文档参考：doc/审批流程管理模块-开发文档.md
-- ----------------------------

-- ----------------------------
-- 流程定义表
-- ----------------------------
DROP TABLE IF EXISTS `wf_definition`;
CREATE TABLE `wf_definition` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `definition_key` varchar(64) NOT NULL COMMENT '流程唯一标识，如 user_register、leave_request',
  `name` varchar(128) DEFAULT '' COMMENT '流程名称',
  `description` varchar(512) DEFAULT '' COMMENT '描述',
  `version` int(11) DEFAULT 1 COMMENT '版本号',
  `status` char(1) DEFAULT '0' COMMENT '状态（0启用 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_definition_key` (`definition_key`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程定义表';

-- ----------------------------
-- 流程节点定义表
-- ----------------------------
DROP TABLE IF EXISTS `wf_node`;
CREATE TABLE `wf_node` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `definition_id` bigint(20) NOT NULL COMMENT '所属流程定义 id',
  `node_key` varchar(64) NOT NULL COMMENT '节点唯一标识，如 dept_leader、hr、admin',
  `node_name` varchar(128) DEFAULT '' COMMENT '节点名称',
  `sort_order` int(11) DEFAULT 0 COMMENT '顺序，从小到大表示审批顺序',
  `approver_type` varchar(32) DEFAULT '' COMMENT '审批人类型：role/user/dept_leader 等',
  `approver_value` varchar(256) DEFAULT '' COMMENT '审批人取值，如角色 key、用户 ID、部门 ID',
  `allow_reject` char(1) DEFAULT 'Y' COMMENT '是否允许驳回（Y/N）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_definition_id` (`definition_id`) USING BTREE,
  KEY `idx_sort_order` (`definition_id`, `sort_order`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程节点定义表';

-- ----------------------------
-- 流程实例表
-- ----------------------------
DROP TABLE IF EXISTS `wf_instance`;
CREATE TABLE `wf_instance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `definition_id` bigint(20) NOT NULL COMMENT '流程定义 id',
  `business_type` varchar(64) NOT NULL COMMENT '业务类型，如 user_register、leave',
  `business_id` varchar(64) NOT NULL COMMENT '业务主键，如用户 ID、请假单 ID',
  `submitter_id` bigint(20) NOT NULL COMMENT '提交人用户 ID',
  `current_node_id` bigint(20) DEFAULT NULL COMMENT '当前待审批节点 id（wf_node.id），为空表示已结束',
  `status` varchar(16) NOT NULL DEFAULT 'running' COMMENT '状态：running/approved/rejected',
  `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间（通过/驳回）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_definition_id` (`definition_id`) USING BTREE,
  KEY `idx_business` (`business_type`, `business_id`) USING BTREE,
  KEY `idx_submitter_id` (`submitter_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_submit_time` (`submit_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程实例表';

-- ----------------------------
-- 审批记录表
-- ----------------------------
DROP TABLE IF EXISTS `wf_approval_record`;
CREATE TABLE `wf_approval_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `instance_id` bigint(20) NOT NULL COMMENT '流程实例 id',
  `node_id` bigint(20) NOT NULL COMMENT '节点 id',
  `approver_id` bigint(20) NOT NULL COMMENT '审批人用户 ID',
  `result` varchar(16) NOT NULL COMMENT '审批结果：approved/rejected',
  `comment` varchar(512) DEFAULT '' COMMENT '审批意见',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_instance_id` (`instance_id`) USING BTREE,
  KEY `idx_node_id` (`node_id`) USING BTREE,
  KEY `idx_approver_id` (`approver_id`) USING BTREE,
  KEY `idx_approve_time` (`approve_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批记录表';
