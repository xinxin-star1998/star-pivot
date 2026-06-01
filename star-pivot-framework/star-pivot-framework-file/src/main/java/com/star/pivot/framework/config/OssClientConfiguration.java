package com.star.pivot.framework.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * 阿里云 OSS 客户端单例配置。
 * <p>SDK 文档建议复用 OSS 实例；应用关闭时由 Spring 调用 {@code shutdown()} 释放连接。</p>
 */
@Configuration
public class OssClientConfiguration {

    @Bean(destroyMethod = "shutdown")
    public OSS ossClient(
            @Value("${oss.endpoint:}") String endpoint,
            @Value("${oss.access-key-id:}") String accessKeyId,
            @Value("${oss.access-key-secret:}") String accessKeySecret) {
        return new OSSClientBuilder().build(
                requireText(endpoint, "oss.endpoint / OSS_ENDPOINT"),
                requireText(accessKeyId, "oss.access-key-id / OSS_ACCESS_KEY_ID"),
                requireText(accessKeySecret, "oss.access-key-secret / OSS_ACCESS_KEY_SECRET"));
    }

    private static String requireText(String value, String name) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalStateException("未配置 " + name);
        }
        return value;
    }
}
