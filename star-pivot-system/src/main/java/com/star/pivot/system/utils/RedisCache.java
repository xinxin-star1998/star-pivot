package com.star.pivot.system.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存工具类（生产级完善版）
 * 支持基础对象、Hash、List、Set操作 + 批量操作 + 简单分布式锁
 *
 * @author stardust
 * @date 2024-01-01
 */
@Component
public class RedisCache {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ======================== 基础对象操作（优化原有方法） ========================
    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值（不可为null）
     * @param value 缓存的值（不可为null）
     */
    public <T> void setCacheObject(final String key, final T value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("缓存key和value不能为空");
        }
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象（指定过期时间）
     *
     * @param key      缓存的键值（不可为null）
     * @param value    缓存的值（不可为null）
     * @param timeout  时间（>0）
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final long timeout, final TimeUnit timeUnit) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("缓存key和value不能为空");
        }
        if (timeout <= 0) {
            throw new IllegalArgumentException("过期时间必须大于0");
        }
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 获得缓存的基本对象
     *
     * @param key 缓存的键值（不可为null）
     * @return 缓存键值对应的数据，无数据返回null
     */
    @SuppressWarnings("unchecked")
    public <T> T getCacheObject(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("缓存key不能为空");
        }
        ValueOperations<String, Object> operation = redisTemplate.opsForValue();
        return (T) operation.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key 缓存的键值（不可为null）
     */
    public boolean deleteObject(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("缓存key不能为空");
        }
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * 判断key是否存在
     *
     * @param key 缓存的键值（不可为null）
     * @return 是否存在
     */
    public boolean hasKey(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("缓存key不能为空");
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 设置过期时间（默认秒）
     *
     * @param key     缓存的键值（不可为null）
     * @param timeout 过期时间（>0，秒）
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置过期时间
     *
     * @param key      缓存的键值（不可为null）
     * @param timeout  过期时间（>0）
     * @param timeUnit 时间颗粒度
     */
    public boolean expire(final String key, final long timeout, final TimeUnit timeUnit) {
        if (key == null) {
            throw new IllegalArgumentException("缓存key不能为空");
        }
        if (timeout <= 0) {
            throw new IllegalArgumentException("过期时间必须大于0");
        }
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, timeUnit));
    }

    /**
     * 获取剩余过期时间（秒）
     *
     * @param key 缓存的键值（不可为null）
     * @return 剩余秒数，-1=永久有效，-2=key不存在
     */
    public long getExpire(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("缓存key不能为空");
        }
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    // ======================== 批量操作 ========================
    /**
     * 批量删除缓存
     *
     * @param keys 缓存键值集合（不可为null/空）
     * @return 删除成功的数量
     */
    public long deleteObjects(Collection<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            throw new IllegalArgumentException("缓存key集合不能为空");
        }
        return redisTemplate.delete(keys);
    }

    /**
     * 批量获取缓存
     *
     * @param keys 缓存键值集合（不可为null/空）
     * @return 键值对结果（key=缓存key，value=缓存值）
     */
    public <T> Map<String, T> getCacheObjects(Collection<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            throw new IllegalArgumentException("缓存key集合不能为空");
        }
        List<Object> values = redisTemplate.opsForValue().multiGet(keys);
        Map<String, T> result = new HashMap<>(keys.size());
        Iterator<String> keyIter = keys.iterator();
        Iterator<Object> valueIter = values.iterator();
        while (keyIter.hasNext() && valueIter.hasNext()) {
            String key = keyIter.next();
            T value = (T) valueIter.next();
            result.put(key, value);
        }
        return result;
    }

    // ======================== Hash结构操作 ========================
    /**
     * 往Hash中存入数据
     *
     * @param key     哈希key（不可为null）
     * @param hashKey 哈希字段（不可为null）
     * @param value   字段值（不可为null）
     */
    public <T> void setCacheHashValue(String key, String hashKey, T value) {
        if (key == null || hashKey == null || value == null) {
            throw new IllegalArgumentException("哈希key、字段、值均不能为空");
        }
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(key, hashKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key     哈希key（不可为null）
     * @param hashKey 哈希字段（不可为null）
     * @return 字段值
     */
    @SuppressWarnings("unchecked")
    public <T> T getCacheHashValue(String key, String hashKey) {
        if (key == null || hashKey == null) {
            throw new IllegalArgumentException("哈希key和字段均不能为空");
        }
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        return (T) hashOperations.get(key, hashKey);
    }

    /**
     * 获取整个Hash的数据
     *
     * @param key 哈希key（不可为null）
     * @return 哈希所有键值对
     */
    public <T> Map<String, T> getCacheHashAll(String key) {
        if (key == null) {
            throw new IllegalArgumentException("哈希key不能为空");
        }
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        Map<String, Object> entries = hashOperations.entries(key);
        Map<String, T> result = new HashMap<>(entries.size());
        entries.forEach((k, v) -> result.put(k, (T) v));
        return result;
    }

    /**
     * 删除Hash中的指定字段
     *
     * @param key      哈希key（不可为null）
     * @param hashKeys 哈希字段数组（不可为null/空）
     */
    public void deleteCacheHash(String key, String... hashKeys) {
        if (key == null || hashKeys == null || hashKeys.length == 0) {
            throw new IllegalArgumentException("哈希key和字段数组均不能为空");
        }
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(key, (Object[]) hashKeys);
    }

    // ======================== List结构操作 ========================
    /**
     * 往List尾部添加数据
     *
     * @param key   列表key（不可为null）
     * @param value 数据（不可为null）
     * @return 列表新长度
     */
    public <T> long setCacheList(String key, T value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("列表key和值均不能为空");
        }
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        return listOperations.rightPush(key, value);
    }

    /**
     * 获取List指定范围的数据
     *
     * @param key   列表key（不可为null）
     * @param start 起始索引（>=0）
     * @param end   结束索引（>=start，-1表示最后一个元素）
     * @return 数据列表
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getCacheList(String key, long start, long end) {
        if (key == null) {
            throw new IllegalArgumentException("列表key不能为空");
        }
        if (start < 0) {
            throw new IllegalArgumentException("起始索引不能小于0");
        }
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        return (List<T>) listOperations.range(key, start, end);
    }

    // ======================== Set结构操作 ========================
    /**
     * 往Set中添加数据
     *
     * @param key    集合key（不可为null）
     * @param values 数据（不可为null/空）
     * @return 添加成功的数量
     */
    public <T> long setCacheSet(String key, T... values) {
        if (key == null || values == null || values.length == 0) {
            throw new IllegalArgumentException("集合key和数据均不能为空");
        }
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        return setOperations.add(key, values);
    }

    /**
     * 获取Set所有数据
     *
     * @param key 集合key（不可为null）
     * @return 集合数据
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> getCacheSet(String key) {
        if (key == null) {
            throw new IllegalArgumentException("集合key不能为空");
        }
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        return (Set<T>) setOperations.members(key);
    }

    // ======================== 简单分布式锁 ========================
    /**
     * 获取分布式锁
     *
     * @param lockKey  锁key（不可为null）
     * @param requestId 请求标识（用于释放锁时校验，避免误删）
     * @param expireTime 锁过期时间（秒，>0）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId, long expireTime) {
        if (lockKey == null || requestId == null) {
            throw new IllegalArgumentException("锁key和请求标识均不能为空");
        }
        if (expireTime <= 0) {
            throw new IllegalArgumentException("锁过期时间必须大于0");
        }
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expireTime, TimeUnit.SECONDS));
    }

    /**
     * 释放分布式锁（Lua脚本保证原子性）
     *
     * @param lockKey  锁key（不可为null）
     * @param requestId 请求标识（需与加锁时一致）
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String requestId) {
        if (lockKey == null || requestId == null) {
            throw new IllegalArgumentException("锁key和请求标识均不能为空");
        }
        // Lua脚本：先判断值是否匹配，匹配则删除
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId);
        return result != null && result > 0;
    }
}