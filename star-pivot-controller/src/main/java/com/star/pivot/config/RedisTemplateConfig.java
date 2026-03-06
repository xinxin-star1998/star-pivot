package com.star.pivot.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis Template 配置类
 *
 * 职责：配置 RedisTemplate 和 StringRedisTemplate
 *
 * @author xinxin
 */
@Configuration
public class RedisTemplateConfig {

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
     * 配置 RedisTemplate
     *
     * <p>用于操作 Redis 的通用模板，支持 key-value、hash、list、set、zset 等数据结构
     *
     * @param connectionFactory Redis 连接工厂
     * @return RedisTemplate 实例
     */
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
     * 配置 StringRedisTemplate
     *
     * <p>用于 Lua 脚本等需要纯字符串 KV 的场景，避免 JSON 序列化导致脚本参数为 nil
     *
     * @param connectionFactory Redis 连接工厂
     * @return StringRedisTemplate 实例
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
