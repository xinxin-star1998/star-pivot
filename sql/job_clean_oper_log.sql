-- 定时清空操作日志任务（可选：在「系统监控 -> 定时任务」中也可通过界面新增）
-- 调用目标：com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()
-- 默认 cron：每天凌晨 2 点执行（0 0 2 * * ?）
INSERT INTO `sys_job` (`job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('定时清空操作日志', 'DEFAULT', 'com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()', '0 0 2 * * ?', '3', '1', '0', 'admin', NOW(), '', NULL, '每天凌晨2点清空操作日志表 sys_oper_log');
