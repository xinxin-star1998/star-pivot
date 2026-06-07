-- StarPivot 工作流模块表结构
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 流程定义
-- ----------------------------
DROP TABLE IF EXISTS `wf_process_def`;
CREATE TABLE `wf_process_def` (
  `def_id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '定义ID',
  `process_code`    varchar(100) NOT NULL COMMENT '流程编码',
  `process_name`    varchar(200) NOT NULL COMMENT '流程名称',
  `biz_module`      varchar(32)  NOT NULL DEFAULT '' COMMENT '业务模块 crm/erp/mall/system',
  `version`         int          NOT NULL DEFAULT 1 COMMENT '版本号',
  `def_json`        longtext     NULL COMMENT '设计态 JSON（含坐标）',
  `runtime_json`    longtext     NULL COMMENT '运行态 JSON（编译后）',
  `status`          varchar(20)  NOT NULL DEFAULT 'draft' COMMENT 'draft/published/disabled',
  `remark`          varchar(500) NULL DEFAULT '' COMMENT '备注',
  `create_by`       varchar(64)  NULL DEFAULT '' COMMENT '创建者',
  `create_time`     datetime     NULL DEFAULT NULL COMMENT '创建时间',
  `update_by`       varchar(64)  NULL DEFAULT '' COMMENT '更新者',
  `update_time`     datetime     NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`def_id`),
  UNIQUE KEY `uk_process_code_version` (`process_code`, `version`),
  KEY `idx_process_code_status` (`process_code`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流流程定义';

-- ----------------------------
-- 流程实例
-- ----------------------------
DROP TABLE IF EXISTS `wf_instance`;
CREATE TABLE `wf_instance` (
  `instance_id`     bigint       NOT NULL AUTO_INCREMENT COMMENT '实例ID',
  `def_id`          bigint       NOT NULL COMMENT '流程定义ID',
  `process_code`    varchar(100) NOT NULL COMMENT '流程编码',
  `process_name`    varchar(200) NOT NULL COMMENT '流程名称',
  `business_key`    varchar(200) NOT NULL COMMENT '业务键 module:entity:id',
  `title`           varchar(500) NOT NULL DEFAULT '' COMMENT '标题',
  `starter_id`      bigint       NOT NULL COMMENT '发起人ID',
  `status`          varchar(20)  NOT NULL DEFAULT 'RUNNING' COMMENT 'RUNNING/APPROVED/REJECTED/CANCELLED',
  `current_node_id` varchar(64)  NULL DEFAULT NULL COMMENT '当前节点ID',
  `variables_json`  text         NULL COMMENT '流程变量 JSON',
  `create_time`     datetime     NULL DEFAULT NULL COMMENT '创建时间',
  `finish_time`     datetime     NULL DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`instance_id`),
  KEY `idx_business_key_status` (`business_key`, `status`),
  KEY `idx_starter_id` (`starter_id`),
  KEY `idx_process_code` (`process_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流流程实例';

-- ----------------------------
-- 待办任务
-- ----------------------------
DROP TABLE IF EXISTS `wf_task`;
CREATE TABLE `wf_task` (
  `task_id`         bigint       NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `instance_id`     bigint       NOT NULL COMMENT '实例ID',
  `node_id`         varchar(64)  NOT NULL COMMENT '节点ID',
  `node_name`       varchar(200) NOT NULL DEFAULT '' COMMENT '节点名称',
  `assignee_id`     bigint       NOT NULL COMMENT '审批人ID',
  `status`          varchar(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/COMPLETED/CANCELLED',
  `action`          varchar(20)  NULL DEFAULT NULL COMMENT 'APPROVE/REJECT',
  `comment`         varchar(500) NULL DEFAULT NULL COMMENT '审批意见',
  `create_time`     datetime     NULL DEFAULT NULL COMMENT '创建时间',
  `finish_time`     datetime     NULL DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`task_id`),
  KEY `idx_instance_id` (`instance_id`),
  KEY `idx_assignee_status` (`assignee_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流待办任务';

-- ----------------------------
-- 审批历史
-- ----------------------------
DROP TABLE IF EXISTS `wf_task_history`;
CREATE TABLE `wf_task_history` (
  `history_id`      bigint       NOT NULL AUTO_INCREMENT COMMENT '历史ID',
  `instance_id`     bigint       NOT NULL COMMENT '实例ID',
  `task_id`         bigint       NULL DEFAULT NULL COMMENT '任务ID',
  `node_id`         varchar(64)  NOT NULL COMMENT '节点ID',
  `node_name`       varchar(200) NOT NULL DEFAULT '' COMMENT '节点名称',
  `operator_id`     bigint       NOT NULL COMMENT '操作人ID',
  `action`          varchar(20)  NOT NULL COMMENT 'START/APPROVE/REJECT/CANCEL',
  `comment`         varchar(500) NULL DEFAULT NULL COMMENT '意见',
  `create_time`     datetime     NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`history_id`),
  KEY `idx_instance_id` (`instance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流审批历史';

SET FOREIGN_KEY_CHECKS = 1;
