package com.star.pivot.framework.cache.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 缓存过期时间注解
 * <p>
 * 用于在方法上指定缓存的过期时间，配合自定义的 CacheInterceptor 使用
 *
 * <p>使用示例：
 * <pre>
 * @Cacheable(cacheNames = "user", key = "#id")
 * @CacheExpire(value = 30, unit = TimeUnit.MINUTES)
 * public User getUserById(Long id) { ... }
 * </pre>
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheExpire {

    /**
     * 过期时间值
     */
    long value();

    /**
     * 时间单位，默认分钟
     */
    TimeUnit unit() default TimeUnit.MINUTES;
}
