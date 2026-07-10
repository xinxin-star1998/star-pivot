package com.star.pivot.ai.service;

import com.star.pivot.ai.config.AiProperties;
import com.star.pivot.ai.metrics.AiMetrics;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.security.context.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AiChatRateLimitService {

    private static final String KEY_PREFIX = "ai:chat:rate:";

    private final StringRedisTemplate redisTemplate;
    private final AiProperties aiProperties;
    private final AiMetrics aiMetrics;

    public void checkChatRequest() {
        AiProperties.RateLimitProperties config = aiProperties.getRateLimit();
        if (config == null || !config.isEnabled()) {
            return;
        }
        Long userId = SecurityContextUtils.getUserId();
        if (userId == null) {
            throw new BizException("请先登录后再使用 AI 对话");
        }
        int maxPerMinute = Math.max(1, config.getMaxRequestsPerMinute());
        String key = KEY_PREFIX + userId;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, Duration.ofMinutes(1));
        }
        if (count != null && count > maxPerMinute) {
            aiMetrics.recordRateLimitRejected();
            throw new BizException("对话请求过于频繁，请稍后再试");
        }
    }
}
