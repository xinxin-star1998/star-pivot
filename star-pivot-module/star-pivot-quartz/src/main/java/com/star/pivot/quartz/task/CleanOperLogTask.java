package com.star.pivot.quartz.task;

import com.star.pivot.framework.api.ModuleApiProvider;
import com.star.pivot.framework.api.OperLogApi;
import com.star.pivot.framework.event.OperLogCleanEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanOperLogTask {

    private final ApplicationEventPublisher eventPublisher;
    private final ModuleApiProvider moduleApiProvider;

    public void cleanOperLog() {
        log.info("开始执行清空操作日志任务");
        eventPublisher.publishEvent(OperLogCleanEvent.cleanAll(this));
        log.info("清空操作日志事件已发布");
    }

    public void cleanOperLogBeforeDays(int days) {
        log.info("开始执行清理 {} 天前的操作日志任务", days);
        eventPublisher.publishEvent(OperLogCleanEvent.cleanBeforeDays(this, days));
        log.info("清理操作日志事件已发布");
    }

    public void cleanOperLogViaApi() {
        log.info("通过 API 方式清空操作日志");
        moduleApiProvider.getOperLogApi().ifPresentOrElse(
                OperLogApi::cleanAll,
                () -> log.warn("OperLogApi 未实现，跳过清空操作日志")
        );
    }
}
