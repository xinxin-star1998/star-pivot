package com.star.pivot.framework.storage.impl;

import com.star.pivot.framework.storage.FileStorageService;
import com.star.pivot.framework.utils.MinioUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * MinIO 文件存储服务实现
 * 当配置 file-storage.type=minio 时启用
 *
 * @author stardust
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "file-storage.type", havingValue = "minio")
public class MinioFileStorageService implements FileStorageService {

    private final MinioUtil minioUtil;

    @Override
    public String uploadAvatar(MultipartFile file, String userId) throws Exception {
        log.debug("使用 MinIO 上传头像，userId={}", userId);
        return minioUtil.uploadAvatar(file, userId);
    }

    @Override
    public String uploadAvatarWithUrl(MultipartFile file, String userId) throws Exception {
        log.debug("使用 MinIO 上传头像并返回URL，userId={}", userId);
        return minioUtil.uploadAvatarWithUrl(file, userId);
    }

    @Override
    public String uploadAvatarWithPresignedUrl(MultipartFile file, String userId) throws Exception {
        log.debug("使用 MinIO 上传头像并返回预签名URL，userId={}", userId);
        return minioUtil.uploadAvatarWithPresignedUrl(file, userId);
    }

    @Override
    public void deleteAvatar(String userId) throws Exception {
        log.debug("使用 MinIO 删除头像，userId={}", userId);
        minioUtil.deleteAvatar(userId);
    }

    @Override
    public String getPresignedUrl(String objectName) throws Exception {
        log.debug("使用 MinIO 生成预签名URL，objectName={}", objectName);
        return minioUtil.getPresignedUrl(objectName);
    }

    @Override
    public String getPermanentUrl(String objectName) {
        log.debug("使用 MinIO 生成永久URL，objectName={}", objectName);
        return minioUtil.getPermanentUrl(objectName);
    }
}
