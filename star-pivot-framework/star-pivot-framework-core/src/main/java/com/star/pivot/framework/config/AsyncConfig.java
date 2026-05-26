package com.star.pivot.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务配置类
 * 
 * <p>配置异步线程池，用于执行日志记录、邮件发送等非核心业务逻辑
 * <p>避免阻塞主线程，提升系统响应性能
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 异步任务执行器
     * 
     * <p>线程池配置说明：
     * <ul>
     *   <li>核心线程数：5（保持活跃的最小线程数）</li>
     *   <li>最大线程数：20（允许创建的最大线程数）</li>
     *   <li>队列容量：1000（任务队列大小）</li>
     *   <li>线程存活时间：60秒（空闲线程存活时间）</li>
     *   <li>拒绝策略：CallerRunsPolicy（队列满时由调用线程执行）</li>
     * </ul>
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数
        executor.setCorePoolSize(5);
        
        // 最大线程数
        executor.setMaxPoolSize(20);
        
        // 队列容量
        executor.setQueueCapacity(1000);
        
        // 线程存活时间（秒）
        executor.setKeepAliveSeconds(60);
        
        // 线程名称前缀
        executor.setThreadNamePrefix("async-task-");
        
        // 拒绝策略：当线程池和队列都满时，由调用线程执行任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 等待时间（秒）
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        return executor;
    }
}