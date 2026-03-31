package com.star.pivot.system.event;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.star.pivot.framework.event.OperLogCleanEvent;
import com.star.pivot.system.domain.entity.SysOperLog;
import com.star.pivot.system.service.interfaces.SysOperLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OperLogCleanEventListener {

    private final SysOperLogService sysOperLogService;

    @Async
    @EventListener
    public void handleOperLogCleanEvent(OperLogCleanEvent event) {
        log.info("收到操作日志清理事件, 类型={}, 触发时间={}", 
                event.getCleanType(), event.getTriggerTime());
        
        try {
            switch (event.getCleanType()) {
                case ALL:
                    boolean removed = sysOperLogService.remove(null);
                    log.info("清空所有操作日志完成, removed={}", removed);
                    break;
                case BEFORE_DAYS:
                    Integer days = event.getDays();
                    if (days != null && days > 0) {
                        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
                        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
                        wrapper.lt(SysOperLog::getOperTime, threshold);
                        long count = sysOperLogService.count(wrapper);
                        boolean deleted = sysOperLogService.remove(wrapper);
                        log.info("清理 {} 天前的操作日志完成, 删除记录数={}, removed={}", days, count, deleted);
                    }
                    break;
                default:
                    log.warn("未知的清理类型: {}", event.getCleanType());
            }
        } catch (Exception e) {
            log.error("处理操作日志清理事件失败", e);
        }
    }
}
