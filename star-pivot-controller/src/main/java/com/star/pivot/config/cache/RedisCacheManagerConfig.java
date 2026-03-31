package com.star.pivot.config.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.star.pivot.config.properties.StarPivotProperties;
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

@Configuration
@RequiredArgsConstructor
public class RedisCacheManagerConfig {

    private final StarPivotProperties starPivotProperties;

    protected ObjectMapper createObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        return om;
    }

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
}
