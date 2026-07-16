package com.star.pivot.ai.service;

import com.star.pivot.ai.config.AiProperties;
import com.star.pivot.ai.metrics.AiMetrics;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.security.context.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AiChatRateLimitService {

    private static final String KEY_PREFIX = "ai:chat:rate:";

    /** INCR + 首次 EXPIRE 原子化，避免进程中断导致 key 永不过期 */
    private static final DefaultRedisScript<Long> INCR_WITH_EXPIRE_SCRIPT = new DefaultRedisScript<>();

    static {
        INCR_WITH_EXPIRE_SCRIPT.setResultType(Long.class);
        INCR_WITH_EXPIRE_SCRIPT.setScriptText(
                "local current = redis.call('INCR', KEYS[1]) "
                        + "if current == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end "
                        + "return current");
    }

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
        Long count = redisTemplate.execute(INCR_WITH_EXPIRE_SCRIPT, Collections.singletonList(key), "60");
        if (count != null && count > maxPerMinute) {
            aiMetrics.recordRateLimitRejected();
            throw new BizException("对话请求过于频繁，请稍后再试");
        }
    }
}
