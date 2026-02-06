package com.star.pivot.quartz.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 定时任务示例：调用目标为 com.star.pivot.quartz.task.SampleTask.hello()
 *
 * @author StarPivot
 */
@Slf4j
@Component
public class SampleTask {

    /**
     * 示例方法：可在「定时任务」中配置调用目标为 com.star.pivot.quartz.task.SampleTask.hello()
     */
    public void hello() {
        log.info("定时任务示例执行: hello()");
    }
}
