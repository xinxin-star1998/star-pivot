package com.star.pivot.file.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * 分片上传会话（Redis，24h 过期）。
 */
@Slf4j
@Component
public class MultipartUploadSessionStore {

    private static final String KEY_PREFIX = "file:multipart:";
    private static final Duration TTL = Duration.ofHours(24);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    public void save(Session session) {
        if (stringRedisTemplate == null || session == null || !StringUtils.hasText(session.getUploadId())) {
            return;
        }
        try {
            stringRedisTemplate.opsForValue().set(KEY_PREFIX + session.getUploadId(),
                    objectMapper.writeValueAsString(session), TTL);
        } catch (Exception e) {
            log.debug("保存分片会话失败: {}", e.getMessage());
        }
    }

    public Session get(String uploadId) {
        if (stringRedisTemplate == null || !StringUtils.hasText(uploadId)) {
            return null;
        }
        try {
            String json = stringRedisTemplate.opsForValue().get(KEY_PREFIX + uploadId);
            if (!StringUtils.hasText(json)) {
                return null;
            }
            return objectMapper.readValue(json, Session.class);
        } catch (Exception e) {
            log.debug("读取分片会话失败: {}", e.getMessage());
            return null;
        }
    }

    public void remove(String uploadId) {
        if (stringRedisTemplate == null || !StringUtils.hasText(uploadId)) {
            return;
        }
        stringRedisTemplate.delete(KEY_PREFIX + uploadId);
    }

    @Data
    public static class Session {
        private String uploadId;
        private String objectName;
        private Long folderId;
        private String fileName;
        private Long fileSize;
        private String contentType;
        private String fileHash;
        private String mediaType;
        private String bizType;
        private String bizId;
        private String remark;
        private Long createByUserId;
        private Long createDeptId;
        private long partSize;
    }
}
