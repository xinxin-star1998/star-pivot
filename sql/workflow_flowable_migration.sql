-- ----------------------------
-- Flowable BPMN 集成：wf_instance 增加 process_instance_id，wf_approval_record.node_id 允许为空
-- 执行前请先执行 workflow_approval.sql 建表
-- ----------------------------

-- 流程实例表：增加 Flowable 流程实例 ID（与 ACT_RU_EXECUTION 等关联）
ALTER TABLE `wf_instance`
  ADD COLUMN `process_instance_id` varchar(64) DEFAULT NULL COMMENT 'Flowable 流程实例 ID' AFTER `id`,
  ADD UNIQUE KEY `uk_process_instance_id` (`process_instance_id`) USING BTREE;

-- 审批记录表：节点 ID 在 BPMN 模式下可为空（审批记录可从 Flowable 历史构建）
ALTER TABLE `wf_approval_record`
  MODIFY COLUMN `node_id` bigint(20) DEFAULT NULL COMMENT '节点 id（BPMN 模式下可为空）';
