package com.star.pivot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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
                log.info("Redis 地址：{}", redisTemplate.getConnectionFactory().getConnection().getClientName());
                log.info("==========================================");
            } else {
                log.warn("==========================================");
                log.warn("Redis 连接检查：⚠️ 数据不一致");
                log.warn("写入值：{}，读取值：{}", testValue, result);
                log.warn("==========================================");
            }
            
            // 清理测试数据
            redisTemplate.delete(testKey);
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
}
