package com.star.pivot.framework.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件存储配置属性
 * 用于控制使用哪种存储方式（MinIO 或 OSS）
 *
 * @author stardust
 */
@Data
@Component
@ConfigurationProperties(prefix = "file-storage")
public class FileStorageProperties {

    /**
     * 存储类型：minio 或 oss
     * 默认使用 oss
     */
    private String type = "oss";
}
