package com.star.pivot.system.service;

import com.star.pivot.framework.exception.ServiceException;
import com.star.pivot.system.utils.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 登录限流服务
 * 基于Redis滑动窗口实现IP和IP+用户名维度的限流
 * 
 * @author xinxin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginRateLimitService {

    private final RedisCache redisCache;
    /** 执行 Lua 脚本时使用，保证 ARGV 为纯字符串，避免 JSON 序列化导致 Lua 中 tonumber(ARGV[2]) 为 nil */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * IP维度限流：每分钟允许的最大登录次数
     */
    @Value("${login.rate-limit.ip.max-attempts:10}")
    private int ipMaxAttempts;

    /**
     * IP维度限流：时间窗口（分钟）
     */
    @Value("${login.rate-limit.ip.window-minutes:1}")
    private int ipWindowMinutes;

    /**
     * IP+用户名维度限流：每分钟允许的最大登录次数
     */
    @Value("${login.rate-limit.ip-username.max-attempts:5}")
    private int ipUsernameMaxAttempts;

    /**
     * IP+用户名维度限流：时间窗口（分钟）
     */
    @Value("${login.rate-limit.ip-username.window-minutes:1}")
    private int ipUsernameWindowMinutes;

    /**
     * 限流是否启用
     */
    @Value("${login.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    /**
     * 检查IP维度的登录限流
     * 
     * @param ip 客户端IP地址
     * @throws ServiceException 如果超过限流阈值
     */
    public void checkIpRateLimit(String ip) {
        if (!rateLimitEnabled) {
            return;
        }

        String key = "login:rate-limit:ip:" + ip;
        long windowSeconds = ipWindowMinutes * 60L;
        
        // 使用滑动窗口算法检查限流
        long currentCount = incrementAndGet(key, windowSeconds);
        
        if (currentCount > ipMaxAttempts) {
            log.warn("IP {} 登录请求过于频繁，已触发限流（{}分钟内超过{}次）", ip, ipWindowMinutes, ipMaxAttempts);
            throw new ServiceException(
                String.format("登录请求过于频繁，请%d分钟后再试", ipWindowMinutes), 
                429
            );
        }
        
        log.debug("IP {} 登录限流检查通过，当前计数: {}/{}", ip, currentCount, ipMaxAttempts);
    }

    /**
     * 检查IP+用户名维度的登录限流
     * 
     * @param ip 客户端IP地址
     * @param username 用户名
     * @throws ServiceException 如果超过限流阈值
     */
    public void checkIpUsernameRateLimit(String ip, String username) {
        if (!rateLimitEnabled) {
            return;
        }

        String key = "login:rate-limit:ip-username:" + ip + ":" + username;
        long windowSeconds = ipUsernameWindowMinutes * 60L;
        
        // 使用滑动窗口算法检查限流
        long currentCount = incrementAndGet(key, windowSeconds);
        
        if (currentCount > ipUsernameMaxAttempts) {
            log.warn("IP {} 用户 {} 登录请求过于频繁，已触发限流（{}分钟内超过{}次）", 
                ip, username, ipUsernameWindowMinutes, ipUsernameMaxAttempts);
            throw new ServiceException(
                String.format("登录请求过于频繁，请%d分钟后再试", ipUsernameWindowMinutes), 
                429
            );
        }
        
        log.debug("IP {} 用户 {} 登录限流检查通过，当前计数: {}/{}", 
            ip, username, currentCount, ipUsernameMaxAttempts);
    }

    /**
     * 清除IP维度的限流计数（登录成功时调用）
     * 
     * @param ip 客户端IP地址
     */
    public void clearIpRateLimit(String ip) {
        String key = "login:rate-limit:ip:" + ip;
        redisCache.deleteObject(key);
        log.debug("已清除IP {} 的登录限流计数", ip);
    }

    /**
     * 清除IP+用户名维度的限流计数（登录成功时调用）
     * 
     * @param ip 客户端IP地址
     * @param username 用户名
     */
    public void clearIpUsernameRateLimit(String ip, String username) {
        String key = "login:rate-limit:ip-username:" + ip + ":" + username;
        redisCache.deleteObject(key);
        log.debug("已清除IP {} 用户 {} 的登录限流计数", ip, username);
    }

    /**
     * 使用滑动窗口算法递增计数并返回当前计数
     * 使用Lua脚本保证原子性
     * 使用ZSET存储时间戳，实现滑动窗口限流
     * 
     * <p>修复要点：</p>
     * <ul>
     *   <li>1. 外层包裹 try-catch，捕获所有异常，避免影响登录流程</li>
     *   <li>2. 确保 Lua 脚本语法正确，参数类型匹配</li>
     *   <li>3. Redis 异常时降级为「不做限流」，返回 0</li>
     *   <li>4. 使用毫秒时间戳作为 member，确保唯一性</li>
     * </ul>
     * 
     * @param key Redis键
     * @param windowSeconds 时间窗口（秒）
     * @return 当前计数；若 Redis 异常则返回 0（视为未达到阈值）
     */
    private long incrementAndGet(String key, long windowSeconds) {
        try {
            // Lua脚本：使用滑动窗口算法（基于ZSET）
            // 使用毫秒时间戳作为 member，确保同一秒内的多次请求也能被区分
            String script = 
                "local key = KEYS[1]\n" +
                "local window = tonumber(ARGV[1])\n" +
                "local nowMillis = tonumber(ARGV[2])\n" +
                "local nowSeconds = math.floor(nowMillis / 1000)\n" +
                "local cutoff = nowSeconds - window\n" +
                "\n" +
                "-- 清理窗口外的数据（删除score小于cutoff的成员）\n" +
                "redis.call('ZREMRANGEBYSCORE', key, '-inf', cutoff)\n" +
                "\n" +
                "-- 添加当前请求（使用秒级时间戳作为score，毫秒级时间戳作为member确保唯一性）\n" +
                "redis.call('ZADD', key, nowSeconds, nowMillis)\n" +
                "\n" +
                "-- 设置过期时间（窗口时间 + 10秒缓冲，确保数据能及时清理）\n" +
                "redis.call('EXPIRE', key, window + 10)\n" +
                "\n" +
                "-- 返回当前窗口内的总数\n" +
                "return redis.call('ZCARD', key)";
            
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            redisScript.setResultType(Long.class);
            
            // 使用毫秒时间戳，确保同一秒内的多次请求也能被区分；必须用 stringRedisTemplate 否则 ARGV 被 JSON 序列化导致 Lua 中 tonumber(ARGV[2]) 为 nil
            long currentTimeMillis = System.currentTimeMillis();
            Long count = stringRedisTemplate.execute(redisScript, Collections.singletonList(key),
                String.valueOf(windowSeconds), String.valueOf(currentTimeMillis));
            
            return count != null ? count : 0L;
        } catch (Exception e) {
            // 关键修复：捕获所有异常（包括 Redis 连接异常、Lua 脚本执行异常等）
            // 为了系统可用性，限流逻辑出现异常时只打日志，不阻断登录
            log.error("登录限流 Redis 操作异常，已降级为不做限流，key={}, error={}", key, e.getMessage(), e);
            return 0L;
        }
    }
}
