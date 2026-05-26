package com.star.pivot.system.service.impl;

import com.star.pivot.system.domain.entity.SysOperLog;
import com.star.pivot.system.mapper.SysOperLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 异步操作日志服务
 * 
 * <p>将操作日志异步写入数据库，避免阻塞主线程
 * <p>使用 @Async 注解配合 taskExecutor 线程池执行
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncOperLogService {

    private final SysOperLogMapper sysOperLogMapper;

    /**
     * 异步保存操作日志
     * 
     * <p>通过线程池异步执行，不阻塞主线程
     * 
     * @param operLog 操作日志实体
     */
    @Async("taskExecutor")
    public void saveOperLogAsync(SysOperLog operLog) {
        try {
            // 异步写入数据库
            sysOperLogMapper.insert(operLog);
            
            if (log.isDebugEnabled()) {
                log.debug("异步日志保存成功 - 标题: {}, 用户: {}", 
                    operLog.getTitle(), operLog.getOperName());
            }
        } catch (Exception e) {
            // 日志保存失败不影响主业务流程
            log.error("异步日志保存失败 - 标题: {}, 用户: {}", 
                operLog.getTitle(), operLog.getOperName(), e);
        }
    }
}