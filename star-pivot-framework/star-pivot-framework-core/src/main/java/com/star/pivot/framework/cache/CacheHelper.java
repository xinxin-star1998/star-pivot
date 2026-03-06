package com.star.pivot.framework.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * 缓存工具类
 * <p>
 * 提供编程式缓存操作，简化缓存使用
 *
 * <p>使用示例：
 * <pre>
 * // 获取缓存值，不存在则计算
 * User user = cacheHelper.get("user", userId, () -> userMapper.selectById(userId));
 *
 * // 放入缓存
 * cacheHelper.put("user", userId, user);
 *
 * // 清除缓存
 * cacheHelper.evict("user", userId);
 *
 * // 批量清除
 * cacheHelper.evictBatch("user", userIds);
 * </pre>
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheHelper {

    private final CacheManager cacheManager;

    /**
     * 获取缓存值
     *
     * @param cacheName 缓存名称
     * @param key       缓存key
     * @param <T>       返回值类型
     * @return 缓存值，不存在返回null
     */
    public <T> T get(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("缓存不存在: {}", cacheName);
            return null;
        }
        Cache.ValueWrapper wrapper = cache.get(key);
        return wrapper != null ? (T) wrapper.get() : null;
    }

    /**
     * 获取缓存值，不存在则计算
     *
     * @param cacheName 缓存名称
     * @param key       缓存key
     * @param loader    缓存加载器
     * @param <T>       返回值类型
     * @return 缓存值
     */
    public <T> T get(String cacheName, Object key, Callable<T> loader) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("缓存不存在: {}", cacheName);
            try {
                return loader.call();
            } catch (Exception e) {
                throw new RuntimeException("缓存加载失败", e);
            }
        }
        return cache.get(key, loader);
    }

    /**
     * 放入缓存
     *
     * @param cacheName 缓存名称
     * @param key       缓存key
     * @param value     缓存值
     */
    public void put(String cacheName, Object key, Object value) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("缓存不存在: {}", cacheName);
            return;
        }
        cache.put(key, value);
    }

    /**
     * 清除缓存
     *
     * @param cacheName 缓存名称
     * @param key       缓存key
     */
    public void evict(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("缓存不存在: {}", cacheName);
            return;
        }
        cache.evict(key);
        log.debug("清除缓存: {}.{}", cacheName, key);
    }

    /**
     * 批量清除缓存
     *
     * @param cacheName 缓存名称
     * @param keys      缓存key集合
     */
    public void evictBatch(String cacheName, Collection<?> keys) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("缓存不存在: {}", cacheName);
            return;
        }
        for (Object key : keys) {
            cache.evict(key);
        }
        log.debug("批量清除缓存: {}.{}, 数量: {}", cacheName, keys, keys.size());
    }

    /**
     * 清空整个缓存
     *
     * @param cacheName 缓存名称
     */
    public void clear(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("缓存不存在: {}", cacheName);
            return;
        }
        cache.clear();
        log.info("清空缓存: {}", cacheName);
    }

    /**
     * 获取所有缓存名称
     *
     * @return 缓存名称集合
     */
    public Set<String> getCacheNames() {
        return cacheManager.getCacheNames().stream().collect(Collectors.toSet());
    }

    /**
     * 检查缓存是否存在
     *
     * @param cacheName 缓存名称
     * @return true-存在
     */
    public boolean hasCache(String cacheName) {
        return cacheManager.getCache(cacheName) != null;
    }

    /**
     * 获取或创建缓存
     *
     * @param cacheName 缓存名称
     * @return Cache对象
     */
    public Cache getCache(String cacheName) {
        return cacheManager.getCache(cacheName);
    }
}
