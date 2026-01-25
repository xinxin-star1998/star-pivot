package com.star.pivot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

/**
 * Redis 缓存错误处理器
 * 
 * <p>处理缓存操作中的异常，特别是反序列化失败的情况
 * 当遇到旧格式的缓存数据（Java 序列化）无法用 JSON 反序列化时，
 * 自动清除该缓存并记录日志，避免影响业务逻辑
 */
@Slf4j
public class RedisCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.warn("缓存读取失败，清除该缓存 key: cache={}, key={}, error={}", 
                cache.getName(), key, exception.getMessage());
        try {
            // 清除有问题的缓存，下次会重新加载
            cache.evict(key);
            log.info("已清除有问题的缓存: cache={}, key={}", cache.getName(), key);
        } catch (Exception e) {
            log.error("清除缓存失败: cache={}, key={}", cache.getName(), key, e);
        }
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.error("缓存写入失败: cache={}, key={}, error={}", 
                cache.getName(), key, exception.getMessage(), exception);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.warn("缓存清除失败: cache={}, key={}, error={}", 
                cache.getName(), key, exception.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.error("缓存清空失败: cache={}, error={}", 
                cache.getName(), exception.getMessage(), exception);
    }
}
