package com.star.pivot.config.quartz;

import com.star.pivot.quartz.execution.QuartzJobExecution;
import com.star.pivot.quartz.service.ISysJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class QuartzJobInitRunner implements ApplicationRunner {

    private final ISysJobService sysJobService;

    @Override
    public void run(ApplicationArguments args) {
        QuartzJobExecution.setSysJobService(sysJobService);
        sysJobService.loadScheduledJobsOnStartup();
        log.info("定时任务调度初始化完成");
    }
}
