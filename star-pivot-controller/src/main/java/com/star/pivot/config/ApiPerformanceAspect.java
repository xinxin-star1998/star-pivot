package com.star.pivot.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.star.pivot.system.domain.entity.SysMonitorApiPerformance;
import com.star.pivot.system.mapper.SysMonitorApiPerformanceMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * API接口性能监控切面（优化版）
 * <p>
 * 性能优化措施：
 * <ul>
 *   <li>异步批量写入：使用队列批量写入数据库，减少数据库压力</li>
 *   <li>采样率控制：高频接口只记录部分请求（默认10%）</li>
 *   <li>阈值过滤：只记录慢接口（响应时间 > 1秒）</li>
 * </ul>
 * </p>
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ApiPerformanceAspect {

    private final SysMonitorApiPerformanceMapper apiPerformanceMapper;
    private final StarPivotProperties starPivotProperties;

    /**
     * 采样率：只记录10%的请求（高频接口）
     * 可通过配置 star-pivot.monitor.sampleRate 调整
     */
    private double sampleRate = 0.1;

    /**
     * 慢接口阈值：只记录响应时间超过此值的接口（毫秒）
     * 可通过配置 star-pivot.monitor.slowApiThresholdMs 调整
     */
    private long slowApiThreshold = 1000;

    /**
     * 批量写入队列大小
     */
    private static final int QUEUE_SIZE = 1000;

    /**
     * 批量写入批次大小
     */
    private static final int BATCH_SIZE = 100;

    /**
     * 批量写入间隔（秒）
     */
    private static final int BATCH_INTERVAL = 5;

    /**
     * 性能记录队列（用于异步批量写入）
     */
    private final BlockingQueue<ApiPerformanceRecord> recordQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);

    /**
     * 定时任务执行器
     */
    private ScheduledExecutorService scheduledExecutor;

    /**
     * 统计信息：总请求数、采样数、慢接口数
     */
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong sampledRequests = new AtomicLong(0);
    private final AtomicLong slowApiRequests = new AtomicLong(0);

    /**
     * 配置切点：拦截所有Controller中的方法
     */
    @Pointcut("execution(* com.star.pivot.controller..*.*(..))")
    public void apiPointcut() {
    }

    /**
     * 初始化：启动后台线程批量写入
     */
    @PostConstruct
    public void init() {
        // 从配置中读取参数（如果配置了的话）
        if (starPivotProperties != null && starPivotProperties.getMonitor() != null) {
            StarPivotProperties.Monitor monitor = starPivotProperties.getMonitor();
            
            // 检查是否启用API性能监控
            if (monitor.getApiPerformanceEnabled() != null && !monitor.getApiPerformanceEnabled()) {
                log.info("API性能监控已禁用");
                return;
            }
            
            // 读取慢接口阈值
            if (monitor.getSlowApiThresholdMs() != null) {
                slowApiThreshold = monitor.getSlowApiThresholdMs();
            }
            
            // 读取采样率
            if (monitor.getSampleRate() != null) {
                sampleRate = monitor.getSampleRate();
            }
        }
        
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "api-performance-batch-writer");
            t.setDaemon(true);
            return t;
        });
        
        // 定时批量写入（每5秒执行一次）
        scheduledExecutor.scheduleAtFixedRate(
                this::batchSaveRecords,
                BATCH_INTERVAL,
                BATCH_INTERVAL,
                TimeUnit.SECONDS
        );
        
        log.info("API性能监控切面初始化完成，采样率: {}%, 慢接口阈值: {}ms", 
                (int)(sampleRate * 100), slowApiThreshold);
    }

    /**
     * 销毁：关闭线程池
     */
    @PreDestroy
    public void destroy() {
        if (scheduledExecutor != null) {
            // 关闭前先执行一次批量写入
            batchSaveRecords();
            scheduledExecutor.shutdown();
            try {
                if (!scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduledExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduledExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 环绕通知：记录API性能指标（优化版）
     */
    @Around("apiPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        Object result;

        try {
            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

            if (request == null) {
                return joinPoint.proceed();
            }

            String apiPath = request.getRequestURI();

            // 排除监控接口本身，避免递归
            if (apiPath.contains("/monitor/") || apiPath.contains("/actuator/")) {
                return joinPoint.proceed();
            }

            // 执行方法
            result = joinPoint.proceed();
            success = true;
            return result;

        } finally {
            // 异步记录性能指标（不阻塞主流程）
            try {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
                if (request != null) {
                    long endTime = System.currentTimeMillis();
                    long responseTime = endTime - startTime;

                    String apiPath = request.getRequestURI();
                    String apiMethod = request.getMethod();

                    // 排除监控接口本身
                    if (!apiPath.contains("/monitor/") && !apiPath.contains("/actuator/")) {
                        recordApiPerformance(apiPath, apiMethod, responseTime, success);
                    }
                }
            } catch (Exception e) {
                log.warn("记录API性能指标失败", e);
            }
        }
    }

    /**
     * 记录API性能指标（优化版：采样率控制 + 阈值过滤 + 异步批量写入）
     */
    private void recordApiPerformance(String apiPath, String apiMethod, long responseTime, boolean success) {
        totalRequests.incrementAndGet();

        // 阈值过滤：只记录慢接口（响应时间 > 阈值）
        if (responseTime <= slowApiThreshold) {
            return;
        }

        slowApiRequests.incrementAndGet();

        // 采样率控制：高频接口只记录部分请求
        if (Math.random() >= sampleRate) {
            return;
        }

        sampledRequests.incrementAndGet();

        // 创建性能记录对象
        LocalDate statDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        Integer statHour = currentTime.getHour();

        ApiPerformanceRecord record = new ApiPerformanceRecord();
        record.setApiPath(apiPath);
        record.setApiMethod(apiMethod);
        record.setStatDate(statDate);
        record.setStatHour(statHour);
        record.setResponseTime(responseTime);
        record.setSuccess(success);

        // 异步加入队列（非阻塞）
        try {
            if (!recordQueue.offer(record)) {
                // 队列已满，记录警告但不阻塞
                log.warn("API性能记录队列已满，丢弃记录: {} {}", apiMethod, apiPath);
            }
        } catch (Exception e) {
            log.warn("加入API性能记录队列失败: {} {}", apiMethod, apiPath, e);
        }
    }

    /**
     * 批量保存性能记录到数据库
     */
    private void batchSaveRecords() {
        try {
            List<ApiPerformanceRecord> records = new ArrayList<>();
            // 批量取出记录（最多100条）
            recordQueue.drainTo(records, BATCH_SIZE);

            if (records.isEmpty()) {
                return;
            }

            // 按 (apiPath, apiMethod, statDate, statHour) 分组聚合
            Map<String, List<ApiPerformanceRecord>> groupedRecords = records.stream()
                    .collect(Collectors.groupingBy(r -> 
                            r.getApiPath() + "|" + r.getApiMethod() + "|" + 
                            r.getStatDate() + "|" + r.getStatHour()));

            // 批量处理每个分组
            for (Map.Entry<String, List<ApiPerformanceRecord>> entry : groupedRecords.entrySet()) {
                List<ApiPerformanceRecord> groupRecords = entry.getValue();
                if (groupRecords.isEmpty()) {
                    continue;
                }

                ApiPerformanceRecord firstRecord = groupRecords.get(0);
                LocalDate statDate = firstRecord.getStatDate();
                Integer statHour = firstRecord.getStatHour();
                String apiPath = firstRecord.getApiPath();
                String apiMethod = firstRecord.getApiMethod();

                // 查询或创建性能记录
                SysMonitorApiPerformance performance = apiPerformanceMapper.selectOne(
                        new LambdaQueryWrapper<SysMonitorApiPerformance>()
                                .eq(SysMonitorApiPerformance::getApiPath, apiPath)
                                .eq(SysMonitorApiPerformance::getApiMethod, apiMethod)
                                .eq(SysMonitorApiPerformance::getStatDate, statDate)
                                .eq(SysMonitorApiPerformance::getStatHour, statHour)
                );

                // 聚合统计数据
                long requestCount = groupRecords.size();
                long successCount = groupRecords.stream().mapToLong(r -> r.isSuccess() ? 1 : 0).sum();
                long errorCount = requestCount - successCount;
                long responseTimeTotal = groupRecords.stream().mapToLong(ApiPerformanceRecord::getResponseTime).sum();
                long responseTimeMax = groupRecords.stream().mapToLong(ApiPerformanceRecord::getResponseTime).max().orElse(0);
                long responseTimeMin = groupRecords.stream().mapToLong(ApiPerformanceRecord::getResponseTime).min().orElse(0);

                if (performance == null) {
                    // 创建新记录
                    performance = new SysMonitorApiPerformance();
                    performance.setApiPath(apiPath);
                    performance.setApiMethod(apiMethod);
                    performance.setStatDate(statDate);
                    performance.setStatHour(statHour);
                    performance.setRequestCount(requestCount);
                    performance.setSuccessCount(successCount);
                    performance.setErrorCount(errorCount);
                    performance.setResponseTimeTotal(responseTimeTotal);
                    performance.setResponseTimeMax(responseTimeMax);
                    performance.setResponseTimeMin(responseTimeMin);
                    performance.setResponseTimeAvg(java.math.BigDecimal.valueOf(
                            (double) responseTimeTotal / requestCount));
                    apiPerformanceMapper.insert(performance);
                } else {
                    // 更新现有记录
                    long newRequestCount = performance.getRequestCount() + requestCount;
                    long newSuccessCount = performance.getSuccessCount() + successCount;
                    long newErrorCount = performance.getErrorCount() + errorCount;
                    long newResponseTimeTotal = performance.getResponseTimeTotal() + responseTimeTotal;

                    LambdaUpdateWrapper<SysMonitorApiPerformance> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.eq(SysMonitorApiPerformance::getId, performance.getId())
                            .set(SysMonitorApiPerformance::getRequestCount, newRequestCount)
                            .set(SysMonitorApiPerformance::getSuccessCount, newSuccessCount)
                            .set(SysMonitorApiPerformance::getErrorCount, newErrorCount)
                            .set(SysMonitorApiPerformance::getResponseTimeTotal, newResponseTimeTotal)
                            .set(SysMonitorApiPerformance::getResponseTimeMax, 
                                    Math.max(performance.getResponseTimeMax(), responseTimeMax))
                            .set(SysMonitorApiPerformance::getResponseTimeMin,
                                    performance.getResponseTimeMin() == 0 ? responseTimeMin :
                                            Math.min(performance.getResponseTimeMin(), responseTimeMin))
                            .set(SysMonitorApiPerformance::getResponseTimeAvg,
                                    java.math.BigDecimal.valueOf((double) newResponseTimeTotal / newRequestCount))
                            .set(SysMonitorApiPerformance::getUpdateTime, LocalDateTime.now());

                    apiPerformanceMapper.update(null, updateWrapper);
                }
            }

            log.debug("批量保存API性能记录成功，数量: {}", records.size());
        } catch (Exception e) {
            log.error("批量保存API性能记录失败", e);
        }
    }

    /**
     * 获取统计信息（用于查看监控状态）
     * 
     * @return 统计信息
     */
    public ApiPerformanceStats getStats() {
        long total = totalRequests.get();
        long slow = slowApiRequests.get();
        long sampled = sampledRequests.get();
        long queueSize = recordQueue.size();
        
        return new ApiPerformanceStats(
            total,
            slow,
            sampled,
            queueSize,
            slowApiThreshold,
            sampleRate,
            total > 0 ? (double) slow / total * 100 : 0.0
        );
    }

    /**
     * API性能统计信息
     */
    @Getter
    public static class ApiPerformanceStats {
        private final long totalRequests;
        private final long slowApiRequests;
        private final long sampledRequests;
        private final long queueSize;
        private final long slowApiThreshold;
        private final double sampleRate;
        private final double slowApiRate;

        public ApiPerformanceStats(long totalRequests, long slowApiRequests, long sampledRequests,
                                  long queueSize, long slowApiThreshold, double sampleRate, double slowApiRate) {
            this.totalRequests = totalRequests;
            this.slowApiRequests = slowApiRequests;
            this.sampledRequests = sampledRequests;
            this.queueSize = queueSize;
            this.slowApiThreshold = slowApiThreshold;
            this.sampleRate = sampleRate;
            this.slowApiRate = slowApiRate;
        }

    }

    /**
     * API性能记录内部类（用于队列传输）
     */
    @Setter
    @Getter
    private static class ApiPerformanceRecord {
        // Getters and Setters
        private String apiPath;
        private String apiMethod;
        private LocalDate statDate;
        private Integer statHour;
        private long responseTime;
        private boolean success;

    }
}
