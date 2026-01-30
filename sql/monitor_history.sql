-- 监控历史数据存储表
-- 用于存储服务器、数据库、Redis等监控指标的定期采集数据

-- 监控指标历史数据表
DROP TABLE IF EXISTS `sys_monitor_history`;
CREATE TABLE `sys_monitor_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `metric_type` varchar(50) NOT NULL COMMENT '指标类型（server_cpu, server_memory, server_disk, jvm_heap, druid_active, redis_memory等）',
  `metric_name` varchar(100) NOT NULL COMMENT '指标名称',
  `metric_value` decimal(20,4) DEFAULT NULL COMMENT '指标值',
  `metric_unit` varchar(20) DEFAULT NULL COMMENT '指标单位（%, MB, GB, ms等）',
  `collect_time` datetime NOT NULL COMMENT '采集时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_metric_type_time` (`metric_type`, `collect_time`) USING BTREE,
  KEY `idx_collect_time` (`collect_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监控指标历史数据表';

-- 告警规则表
DROP TABLE IF EXISTS `sys_monitor_alert_rule`;
CREATE TABLE `sys_monitor_alert_rule` (
  `rule_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `rule_name` varchar(100) NOT NULL COMMENT '规则名称',
  `metric_type` varchar(50) NOT NULL COMMENT '指标类型',
  `metric_name` varchar(100) NOT NULL COMMENT '指标名称',
  `threshold_type` char(1) NOT NULL COMMENT '阈值类型（0大于 1小于 2等于）',
  `threshold_value` decimal(20,4) NOT NULL COMMENT '阈值',
  `alert_level` char(1) NOT NULL DEFAULT '1' COMMENT '告警级别（0低 1中 2高 3紧急）',
  `alert_channels` varchar(200) DEFAULT NULL COMMENT '告警渠道（逗号分隔：in_app,email,sms,webhook）',
  `webhook_url` varchar(500) DEFAULT NULL COMMENT 'Webhook地址',
  `enabled` char(1) NOT NULL DEFAULT '1' COMMENT '是否启用（0否 1是）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`rule_id`) USING BTREE,
  KEY `idx_metric_type` (`metric_type`, `enabled`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监控告警规则表';

-- 告警记录表
DROP TABLE IF EXISTS `sys_monitor_alert_record`;
CREATE TABLE `sys_monitor_alert_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `rule_id` bigint(20) NOT NULL COMMENT '规则ID',
  `rule_name` varchar(100) NOT NULL COMMENT '规则名称',
  `metric_type` varchar(50) NOT NULL COMMENT '指标类型',
  `metric_name` varchar(100) NOT NULL COMMENT '指标名称',
  `current_value` decimal(20,4) NOT NULL COMMENT '当前值',
  `threshold_value` decimal(20,4) NOT NULL COMMENT '阈值',
  `alert_level` char(1) NOT NULL COMMENT '告警级别',
  `alert_status` char(1) NOT NULL DEFAULT '0' COMMENT '告警状态（0未处理 1已处理 2已忽略）',
  `alert_channels` varchar(200) DEFAULT NULL COMMENT '告警渠道',
  `alert_content` text COMMENT '告警内容',
  `alert_time` datetime NOT NULL COMMENT '告警时间',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `handle_by` varchar(64) DEFAULT NULL COMMENT '处理人',
  `handle_remark` varchar(500) DEFAULT NULL COMMENT '处理备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  KEY `idx_rule_id` (`rule_id`) USING BTREE,
  KEY `idx_alert_time` (`alert_time`) USING BTREE,
  KEY `idx_alert_status` (`alert_status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监控告警记录表';

-- 慢SQL记录表
DROP TABLE IF EXISTS `sys_monitor_slow_sql`;
CREATE TABLE `sys_monitor_slow_sql` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sql_id` varchar(64) NOT NULL COMMENT 'SQL ID（Druid生成的SQL标识）',
  `sql_text` text NOT NULL COMMENT 'SQL语句',
  `execute_count` bigint(20) DEFAULT 0 COMMENT '执行次数',
  `execute_time_total` bigint(20) DEFAULT 0 COMMENT '总执行时间（毫秒）',
  `execute_time_max` bigint(20) DEFAULT 0 COMMENT '最大执行时间（毫秒）',
  `execute_time_avg` decimal(20,2) DEFAULT 0 COMMENT '平均执行时间（毫秒）',
  `slow_count` bigint(20) DEFAULT 0 COMMENT '慢SQL次数',
  `error_count` bigint(20) DEFAULT 0 COMMENT '错误次数',
  `last_execute_time` datetime DEFAULT NULL COMMENT '最后执行时间',
  `optimization_suggestion` text COMMENT '优化建议',
  `status` char(1) DEFAULT '0' COMMENT '状态（0待优化 1已优化 2已忽略）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_sql_id` (`sql_id`) USING BTREE,
  KEY `idx_execute_time_avg` (`execute_time_avg`) USING BTREE,
  KEY `idx_slow_count` (`slow_count`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='慢SQL记录表';

-- API接口性能监控表
DROP TABLE IF EXISTS `sys_monitor_api_performance`;
CREATE TABLE `sys_monitor_api_performance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `api_path` varchar(500) NOT NULL COMMENT '接口路径',
  `api_method` varchar(10) NOT NULL COMMENT '请求方法（GET, POST等）',
  `request_count` bigint(20) DEFAULT 0 COMMENT '请求次数',
  `success_count` bigint(20) DEFAULT 0 COMMENT '成功次数',
  `error_count` bigint(20) DEFAULT 0 COMMENT '错误次数',
  `response_time_total` bigint(20) DEFAULT 0 COMMENT '总响应时间（毫秒）',
  `response_time_max` bigint(20) DEFAULT 0 COMMENT '最大响应时间（毫秒）',
  `response_time_min` bigint(20) DEFAULT 0 COMMENT '最小响应时间（毫秒）',
  `response_time_avg` decimal(20,2) DEFAULT 0 COMMENT '平均响应时间（毫秒）',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `stat_hour` tinyint(4) DEFAULT NULL COMMENT '统计小时（0-23，用于按小时统计）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_api_stat` (`api_path`, `api_method`, `stat_date`, `stat_hour`) USING BTREE,
  KEY `idx_stat_date` (`stat_date`) USING BTREE,
  KEY `idx_response_time_avg` (`response_time_avg`) USING BTREE,
  KEY `idx_error_count` (`error_count`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API接口性能监控表';
