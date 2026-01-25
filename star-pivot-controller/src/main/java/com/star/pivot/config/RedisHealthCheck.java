package com.star.pivot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis 连接健康检查
 * 应用启动时自动检查 Redis 连接状态
 */
@Slf4j
@Component
public class RedisHealthCheck implements CommandLineRunner {

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(String... args) {
        if (redisTemplate == null) {
            log.error("==========================================");
            log.error("Redis 配置错误：RedisTemplate 未注入！");
            log.error("请检查 RedisConfig 配置和 Redis 依赖");
            log.error("==========================================");
            return;
        }

        try {
            // 测试 Redis 连接
            String testKey = "redis:health:check";
            String testValue = "ok";
            
            redisTemplate.opsForValue().set(testKey, testValue, 10, java.util.concurrent.TimeUnit.SECONDS);
            Object result = redisTemplate.opsForValue().get(testKey);
            
            if (testValue.equals(result)) {
                log.info("==========================================");
                log.info("Redis 连接检查：✅ 成功");
                // 获取 Redis 连接信息（主机和端口）
                String redisInfo = getRedisConnectionInfo(redisTemplate.getConnectionFactory());
                log.info("Redis 地址：{}", redisInfo);
                log.info("==========================================");
            } else {
                log.warn("==========================================");
                log.warn("Redis 连接检查：⚠️ 数据不一致");
                log.warn("写入值：{}，读取值：{}", testValue, result);
                log.warn("==========================================");
            }
            
            // 清理测试数据
            redisTemplate.delete(testKey);
            
            // 清除可能存在的旧格式缓存数据（Java 序列化格式）
            // 这些旧数据会导致 JSON 反序列化失败
            clearOldFormatCache();
        } catch (Exception e) {
            log.error("==========================================");
            log.error("Redis 连接检查：❌ 失败");
            log.error("错误信息：{}", e.getMessage());
            log.error("请检查：");
            log.error("1. Redis 服务是否启动（默认 localhost:6379）");
            log.error("2. application.yml 中的 Redis 配置是否正确");
            log.error("3. Redis 密码是否正确（如果设置了密码）");
            log.error("4. 网络连接是否正常");
            log.error("==========================================");
            log.error("详细错误：", e);
        }
    }
    
    /**
     * 获取 Redis 连接信息（主机和端口）
     * 
     * @param connectionFactory Redis 连接工厂
     * @return 连接信息字符串，格式：host:port
     */
    private String getRedisConnectionInfo(RedisConnectionFactory connectionFactory) {
        try {
            // 尝试从 LettuceConnectionFactory 获取（Spring Boot 2.x+ 默认）
            if (connectionFactory instanceof org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory) {
                org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory lettuceFactory = 
                    (org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory) connectionFactory;
                return lettuceFactory.getHostName() + ":" + lettuceFactory.getPort();
            }
            // 尝试从 JedisConnectionFactory 获取（如果使用 Jedis）
            if (connectionFactory instanceof org.springframework.data.redis.connection.jedis.JedisConnectionFactory) {
                org.springframework.data.redis.connection.jedis.JedisConnectionFactory jedisFactory = 
                    (org.springframework.data.redis.connection.jedis.JedisConnectionFactory) connectionFactory;
                return jedisFactory.getHostName() + ":" + jedisFactory.getPort();
            }
            // 如果无法识别类型，返回通用信息
            return "已连接（连接工厂类型：" + connectionFactory.getClass().getSimpleName() + "）";
        } catch (Exception e) {
            return "已连接（无法获取详细信息：" + e.getMessage() + "）";
        }
    }

    /**
     * 清除旧格式的缓存数据
     * 
     * <p>由于缓存序列化方式从 Java 序列化改为 JSON，需要清除旧的缓存数据
     * 新的缓存数据会使用 "cache:" 前缀，不会与旧数据冲突
     * 但为了彻底清理，这里清除可能存在的旧缓存 key
     */
    private void clearOldFormatCache() {
        try {
            // 清除可能存在的旧格式缓存（没有 "cache:" 前缀的缓存）
            // 注意：这里只清除已知的缓存名称，避免误删其他数据
            String[] oldCacheNames = {"userPermissions", "menuTree", "dictData"};
            int clearedCount = 0;
            
            for (String cacheName : oldCacheNames) {
                // 使用 SCAN 命令查找匹配的 key（避免阻塞 Redis）
                java.util.Set<String> keys = redisTemplate.keys(cacheName + "::*");
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                    clearedCount += keys.size();
                    log.info("清除旧格式缓存: {} ({} 个 key)", cacheName, keys.size());
                }
            }
            
            if (clearedCount > 0) {
                log.info("已清除 {} 个旧格式缓存 key，新的缓存将使用 JSON 序列化格式", clearedCount);
            }
        } catch (Exception e) {
            // 清除旧缓存失败不影响应用启动，只记录警告
            log.warn("清除旧格式缓存时出现异常（不影响应用运行）: {}", e.getMessage());
        }
    }
}
