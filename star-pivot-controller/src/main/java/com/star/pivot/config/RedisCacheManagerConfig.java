package com.star.pivot.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter.TtlFunction;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Redis CacheManager 配置类
 * 职责：配置 Spring Cache 的 CacheManager
 *
 * @author xinxin
 */
@Configuration
@RequiredArgsConstructor
public class RedisCacheManagerConfig {

    private final StarPivotProperties starPivotProperties;

    /**
     * 创建配置好的 ObjectMapper
     *
     * <p>统一配置 ObjectMapper，确保 RedisTemplate 和 CacheManager 使用相同的序列化配置
     * 包括：
     * <ul>
     *   <li>支持 Java 8 时间类型（LocalDateTime 等）</li>
     *   <li>支持类型信息（用于反序列化）</li>
     *   <li>使用 ISO-8601 格式序列化日期时间</li>
     * </ul>
     *
     * @return 配置好的 ObjectMapper 实例
     */
    protected ObjectMapper createObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        // 设置可见性：所有字段都可序列化/反序列化
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 注册 Java 8 时间模块，支持 LocalDateTime、LocalDate、LocalTime 等
        om.registerModule(new JavaTimeModule());

        // 禁用将日期写为时间戳，使用 ISO-8601 字符串格式
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 兼容原有缓存中携带类型信息的写法（用于反序列化 Object）
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        return om;
    }

    /**
     * 配置 Spring Cache 的 CacheManager
     *
     * <p>支持 @Cacheable、@CacheEvict 等注解使用 Redis 作为缓存存储
     *
     * <p>菜单树、字典、权限等缓存的基础 TTL 来自 {@code star-pivot.cache.*}（秒），
     * 并在其基础上加随机抖动以降低缓存雪崩风险。
     *
     * <p>动态 TTL 策略：使用 TtlFunction 实现每个缓存项独立的随机过期时间，
     * 有效防止缓存雪崩问题
     *
     * @param connectionFactory Redis 连接工厂
     * @return CacheManager 实例
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        StarPivotProperties.Cache cache = starPivotProperties.getCache();
        long menuSec = Math.max(1L, cache.getMenuTreeTtl());
        long dictSec = Math.max(1L, cache.getDictDataTtl());
        long permSec = Math.max(1L, cache.getUserPermissionsTtl());

        ObjectMapper om = createObjectMapper();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(om);
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        TtlFunction menuLikeTtl = dynamicTtlFromBaseSeconds(menuSec, 2);
        TtlFunction dictLikeTtl = dynamicTtlFromBaseSeconds(dictSec, 2);
        TtlFunction permTtl = dynamicTtlFromBaseSeconds(permSec, 3);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .prefixCacheNameWith("cache:")
                .entryTtl(menuLikeTtl)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put("userPermissions", defaultConfig.entryTtl(permTtl));

        cacheConfigurations.put("menuTree", defaultConfig.entryTtl(menuLikeTtl));

        cacheConfigurations.put("dictData", defaultConfig.entryTtl(dictLikeTtl));

        cacheConfigurations.put("dictType", defaultConfig.entryTtl(dictLikeTtl));

        cacheConfigurations.put("deptTree", defaultConfig.entryTtl(menuLikeTtl));

        cacheConfigurations.put("postList", defaultConfig.entryTtl(menuLikeTtl));

        cacheConfigurations.put("roleList", defaultConfig.entryTtl(menuLikeTtl));

        cacheConfigurations.put("sysConfig",
                defaultConfig.entryTtl(createDynamicTtl(Duration.ofHours(2), Duration.ofHours(1))));

        cacheConfigurations.put("captcha",
                defaultConfig.entryTtl(Duration.ofMinutes(5)));

        cacheConfigurations.put("loginFailCount",
                defaultConfig.entryTtl(Duration.ofMinutes(10)));

        cacheConfigurations.put("rateLimit",
                defaultConfig.entryTtl(Duration.ofMinutes(1)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    /**
     * 创建动态 TTL 函数
     *
     * <p>为每个缓存项生成独立的随机过期时间，有效防止缓存雪崩
     *
     * <p>原理：当大量缓存在同一时间过期时，会导致大量请求同时打到数据库，
     * 通过为每个缓存项添加随机偏移，使过期时间分散，避免集中过期
     *
     * @param baseTtl 基础过期时间
     * @param maxJitter 最大随机偏移时间（抖动范围）
     * @return TtlFunction 实例
     */
    private TtlFunction createDynamicTtl(Duration baseTtl, Duration maxJitter) {
        return (key, value) -> {
            long baseSeconds = baseTtl.toSeconds();
            long jitterSeconds = maxJitter.toSeconds();
            long randomJitter = ThreadLocalRandom.current().nextLong(jitterSeconds + 1);
            return Duration.ofSeconds(baseSeconds + randomJitter);
        };
    }

    /**
     * @param jitterDivisor 抖动上限为 {@code baseSeconds / jitterDivisor}（至少 60 秒），与原硬编码比例一致：树类为一半、权限为三分之一
     */
    private TtlFunction dynamicTtlFromBaseSeconds(long baseSeconds, int jitterDivisor) {
        long jitterSec = Math.max(60L, baseSeconds / jitterDivisor);
        return createDynamicTtl(Duration.ofSeconds(baseSeconds), Duration.ofSeconds(jitterSec));
    }
}
