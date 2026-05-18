package com.star.pivot.framework.boot.autoconfigure;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.star.pivot.framework.boot.properties.StarPivotProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter.TtlFunction;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Redis 缓存通用配置（RedisTemplate / CacheManager / CacheErrorHandler）。
 *
 * <p>默认开启，可通过 star-pivot.cache.enabled=false 关闭。</p>
 */
@AutoConfiguration
@EnableConfigurationProperties(StarPivotProperties.class)
@ConditionalOnClass({RedisConnectionFactory.class, RedisTemplate.class, RedisCacheManager.class})
@ConditionalOnProperty(prefix = "star-pivot.cache", name = "enabled", havingValue = "true", matchIfMissing = true)
public class StarPivotRedisCacheAutoConfiguration implements CachingConfigurer {

    @Bean
    @ConditionalOnMissingBean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper om = createObjectMapper();
        RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, StarPivotProperties starPivotProperties) {
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
        cacheConfigurations.put("mallCategoryTree", defaultConfig.entryTtl(menuLikeTtl));
        cacheConfigurations.put("mallCategoryChildren", defaultConfig.entryTtl(menuLikeTtl));
        cacheConfigurations.put("sysConfig",
                defaultConfig.entryTtl(createDynamicTtl(Duration.ofHours(2), Duration.ofHours(1))));
        cacheConfigurations.put("captcha", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("loginFailCount", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        cacheConfigurations.put("rateLimit", defaultConfig.entryTtl(Duration.ofMinutes(1)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    @Override
    @Bean
    public CacheErrorHandler errorHandler() {
        return new RedisCacheErrorHandler();
    }

    protected ObjectMapper createObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 保持与原工程一致的序列化行为
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        return om;
    }

    private TtlFunction createDynamicTtl(Duration baseTtl, Duration maxJitter) {
        return (key, value) -> {
            long baseSeconds = baseTtl.toSeconds();
            long jitterSeconds = maxJitter.toSeconds();
            long randomJitter = ThreadLocalRandom.current().nextLong(jitterSeconds + 1);
            return Duration.ofSeconds(baseSeconds + randomJitter);
        };
    }

    private TtlFunction dynamicTtlFromBaseSeconds(long baseSeconds, int jitterDivisor) {
        long jitterSec = Math.max(60L, baseSeconds / jitterDivisor);
        return createDynamicTtl(Duration.ofSeconds(baseSeconds), Duration.ofSeconds(jitterSec));
    }

    @Slf4j
    static class RedisCacheErrorHandler implements CacheErrorHandler {
        @Override
        public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
            log.warn("缓存读取失败，清除该缓存 key: cache={}, key={}, error={}",
                    cache.getName(), key, exception.getMessage());
            try {
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
}

