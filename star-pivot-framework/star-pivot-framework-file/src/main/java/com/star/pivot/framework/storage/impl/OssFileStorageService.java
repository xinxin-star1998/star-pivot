package com.star.pivot.framework.storage.impl;

import com.star.pivot.framework.storage.FileStorageService;
import com.star.pivot.framework.utils.OssUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 阿里云 OSS 文件存储服务实现
 * 当配置 file-storage.type=oss 时启用
 *
 * @author stardust
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "file-storage.type", havingValue = "oss")
public class OssFileStorageService implements FileStorageService {

    private final OssUtil ossUtil;

    @Override
    public String uploadAvatar(MultipartFile file, String userId) throws Exception {
        log.debug("使用 OSS 上传头像，userId={}", userId);
        return ossUtil.uploadAvatar(file, userId);
    }

    @Override
    public String uploadAvatarWithUrl(MultipartFile file, String userId) throws Exception {
        log.debug("使用 OSS 上传头像并返回URL，userId={}", userId);
        return ossUtil.uploadAvatarWithUrl(file, userId);
    }

    @Override
    public String uploadAvatarWithPresignedUrl(MultipartFile file, String userId) throws Exception {
        log.debug("使用 OSS 上传头像并返回预签名URL，userId={}", userId);
        return ossUtil.uploadAvatarWithPresignedUrl(file, userId);
    }

    @Override
    public void deleteAvatar(String userId) throws Exception {
        log.debug("使用 OSS 删除头像，userId={}", userId);
        ossUtil.deleteAvatar(userId);
    }

    @Override
    public String getPresignedUrl(String objectName) throws Exception {
        log.debug("使用 OSS 生成预签名URL，objectName={}", objectName);
        return ossUtil.getPresignedUrl(objectName);
    }

    @Override
    public String getPermanentUrl(String objectName) {
        log.debug("使用 OSS 生成永久URL，objectName={}", objectName);
        return ossUtil.getPermanentUrl(objectName);
    }
}
