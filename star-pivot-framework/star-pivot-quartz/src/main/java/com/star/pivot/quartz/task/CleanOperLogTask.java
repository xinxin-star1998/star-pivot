package com.star.pivot.quartz.task;

import com.star.pivot.system.service.SysOperLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 定时清空操作日志表。
 * 在「定时任务」中配置调用目标：com.star.pivot.quartz.task.CleanOperLogTask.cleanOperLog()
 * 建议 cron：0 0 2 * * ?（每天凌晨 2 点执行）
 *
 * @author StarPivot
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CleanOperLogTask {

    private final SysOperLogService sysOperLogService;

    /**
     * 清空操作日志表（sys_oper_log）
     */
    public void cleanOperLog() {
        try {
            boolean removed = sysOperLogService.remove(null);
            log.info("定时清空操作日志执行完成, removed={}", removed);
        } catch (Exception e) {
            log.error("定时清空操作日志失败", e);
            throw new RuntimeException("清空操作日志失败: " + e.getMessage());
        }
    }
}
