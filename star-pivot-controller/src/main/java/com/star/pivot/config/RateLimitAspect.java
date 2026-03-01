package com.star.pivot.config;

import com.star.pivot.framework.annotation.RateLimit;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.ServiceException;
import com.star.pivot.security.utils.SecurityContextUtils;
import com.star.pivot.framework.utils.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Collections;

/**
 * API限流切面
 * 
 * <p>基于 Redis 滑动窗口算法实现 API 级别的限流保护
 *
 * @author xinxin
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String RATE_LIMIT_KEY_PREFIX = "api:rate-limit:";

    @Around("@annotation(com.star.pivot.framework.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        if (rateLimit == null) {
            return joinPoint.proceed();
        }

        String limitKey = buildLimitKey(rateLimit, method);
        int maxRequests = rateLimit.maxRequests();
        int windowSeconds = rateLimit.windowSeconds();

        try {
            long currentCount = incrementAndGet(limitKey, windowSeconds);

            if (currentCount > maxRequests) {
                log.warn("API限流触发: key={}, count={}, max={}", limitKey, currentCount, maxRequests);
                throw new ServiceException(rateLimit.message(), 429);
            }

            log.debug("API限流检查通过: key={}, count={}/{}", limitKey, currentCount, maxRequests);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("API限流检查异常，已放行: key={}, error={}", limitKey, e.getMessage());
        }

        return joinPoint.proceed();
    }

    /**
     * 构建限流key
     */
    private String buildLimitKey(RateLimit rateLimit, Method method) {
        String key = rateLimit.key();
        if (key == null || key.isEmpty()) {
            key = method.getDeclaringClass().getSimpleName() + ":" + method.getName();
        }

        String dimensionKey;
        switch (rateLimit.limitType()) {
            case IP:
                dimensionKey = "ip:" + getClientIp();
                break;
            case USER:
                Long userId = SecurityContextUtils.getUserId();
                dimensionKey = "user:" + (userId != null ? userId : "anonymous");
                break;
            case GLOBAL:
            default:
                dimensionKey = "global";
                break;
        }

        return RATE_LIMIT_KEY_PREFIX + key + ":" + dimensionKey;
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return LogUtils.getClientIp(request);
        }
        return "unknown";
    }

    /**
     * 使用滑动窗口算法递增计数并返回当前计数
     */
    private long incrementAndGet(String key, long windowSeconds) {
        String script =
            "local key = KEYS[1]\n" +
            "local window = tonumber(ARGV[1])\n" +
            "local nowMillis = tonumber(ARGV[2])\n" +
            "local nowSeconds = math.floor(nowMillis / 1000)\n" +
            "local cutoff = nowSeconds - window\n" +
            "\n" +
            "redis.call('ZREMRANGEBYSCORE', key, '-inf', cutoff)\n" +
            "redis.call('ZADD', key, nowSeconds, nowMillis)\n" +
            "redis.call('EXPIRE', key, window + 10)\n" +
            "\n" +
            "return redis.call('ZCARD', key)";

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);

        long currentTimeMillis = System.currentTimeMillis();
        Long count = stringRedisTemplate.execute(redisScript, Collections.singletonList(key),
            String.valueOf(windowSeconds), String.valueOf(currentTimeMillis));

        return count != null ? count : 0L;
    }
}
