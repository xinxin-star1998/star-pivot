package com.star.pivot.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis配置类
 *
 * @author xinxin
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // value/hashValue 使用 JSON 序列化（替代过时的 setObjectMapper 写法）
        ObjectMapper om = createObjectMapper();
        RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer(om);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value/hashValue 序列化方式采用 JSON
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();

        return template;
    }

    /**
     * 配置 Spring Cache 的 CacheManager
     * 
     * <p>支持 @Cacheable、@CacheEvict 等注解使用 Redis 作为缓存存储
     * 
     * <p>缓存配置说明：
     * <ul>
     *   <li>userPermissions: 用户权限缓存，过期时间 30 分钟</li>
     *   <li>menuTree: 菜单树缓存，过期时间 1 小时</li>
     *   <li>dictData: 字典数据缓存，过期时间 1 小时</li>
     * </ul>
     * 
     * @param connectionFactory Redis 连接工厂
     * @return CacheManager 实例
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 创建 ObjectMapper，与 RedisTemplate 保持一致
        ObjectMapper om = createObjectMapper();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(om);
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // 默认缓存配置：使用 JSON 序列化，过期时间 1 小时
        // 添加缓存 key 前缀 "cache:"，避免与旧数据冲突
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .prefixCacheNameWith("cache:")  // 添加缓存 key 前缀，避免与旧数据冲突
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .disableCachingNullValues(); // 不缓存 null 值

        // 为不同的缓存名称配置不同的过期时间（添加随机时间防止缓存雪崩）
        // 注意：这里的随机时间在配置类初始化时生成，不同缓存名称会有不同的随机偏移
        // 虽然不能做到每个缓存项都有不同的过期时间，但不同缓存类型之间的时间差可以缓解缓存雪崩
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        java.util.Random random = new java.util.Random();
        
        // 用户权限缓存：30 分钟 + 随机0-10分钟
        cacheConfigurations.put("userPermissions", 
                defaultConfig.entryTtl(Duration.ofMinutes(30).plusMinutes(random.nextInt(10))));
        
        // 菜单树缓存：1 小时 + 随机0-30分钟
        cacheConfigurations.put("menuTree", 
                defaultConfig.entryTtl(Duration.ofHours(1).plusMinutes(random.nextInt(30))));
        
        // 字典数据缓存：1 小时 + 随机0-30分钟
        cacheConfigurations.put("dictData", 
                defaultConfig.entryTtl(Duration.ofHours(1).plusMinutes(random.nextInt(30))));

        // 创建 RedisCacheManager
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware() // 支持事务
                .build();
    }
    
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
    private ObjectMapper createObjectMapper() {
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
}