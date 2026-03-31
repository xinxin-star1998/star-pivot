package com.star.pivot.config.aop;

import com.star.pivot.framework.annotation.RateLimit;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.utils.LogUtils;
import com.star.pivot.security.context.SecurityContextUtils;
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
                throw new BizException(ErrorCode.RATE_LIMIT_EXCEEDED, rateLimit.message());
            }
            log.debug("API限流检查通过: key={}, count={}/{}", limitKey, currentCount, maxRequests);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("API限流检查异常，已放行: key={}, error={}", limitKey, e.getMessage());
        }

        return joinPoint.proceed();
    }

    private String buildLimitKey(RateLimit rateLimit, Method method) {
        String key = rateLimit.key();
        if (key == null || key.isEmpty()) {
            key = method.getDeclaringClass().getSimpleName() + ":" + method.getName();
        }
        String dimensionKey = switch (rateLimit.limitType()) {
            case IP -> "ip:" + getClientIp();
            case USER -> {
                Long userId = SecurityContextUtils.getUserId();
                yield "user:" + (userId != null ? userId : "anonymous");
            }
            default -> "global";
        };
        return RATE_LIMIT_KEY_PREFIX + key + ":" + dimensionKey;
    }

    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return LogUtils.getClientIp(request);
        }
        return "unknown";
    }

    private long incrementAndGet(String key, long windowSeconds) {
        String script =
                """
                        local key = KEYS[1]
                        local window = tonumber(ARGV[1])
                        local nowMillis = tonumber(ARGV[2])
                        local nowSeconds = math.floor(nowMillis / 1000)
                        local cutoff = nowSeconds - window
                        redis.call('ZREMRANGEBYSCORE', key, '-inf', cutoff)
                        redis.call('ZADD', key, nowSeconds, nowMillis)
                        redis.call('EXPIRE', key, window + 10)
                        return redis.call('ZCARD', key)""";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);
        long currentTimeMillis = System.currentTimeMillis();
        return stringRedisTemplate.execute(redisScript, Collections.singletonList(key),
            String.valueOf(windowSeconds), String.valueOf(currentTimeMillis));
    }
}
