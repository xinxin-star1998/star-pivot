package com.star.pivot.framework.storage.impl;

import com.star.pivot.framework.storage.FileStorageService;
import com.star.pivot.framework.utils.OssUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 阿里云 OSS 文件存储服务实现
 *
 * @author stardust
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnBean(OssUtil.class)
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

    @Override
    public String uploadEditorImageWithUrl(MultipartFile file) throws Exception {
        log.debug("使用 OSS 上传富文本图片");
        return ossUtil.uploadEditorImageWithUrl(file);
    }

    @Override
    public void uploadFileInternal(MultipartFile file, String objectName) throws Exception {
        log.debug("使用 OSS 通用文件上传，objectName={}", objectName);
        ossUtil.uploadFile(file, objectName);
    }

    @Override
    public void deleteObject(String objectName) throws Exception {
        log.debug("使用 OSS 删除对象，objectName={}", objectName);
        ossUtil.deleteObject(objectName);
    }

    @Override
    public byte[] downloadObject(String objectName) throws Exception {
        log.debug("使用 OSS 下载对象，objectName={}", objectName);
        return ossUtil.downloadObject(objectName);
    }

    @Override
    public String initiateMultipartUpload(String objectName, String contentType) throws Exception {
        return ossUtil.initiateMultipartUpload(objectName, contentType);
    }

    @Override
    public String uploadPart(String objectName, String uploadId, int partNumber,
                             java.io.InputStream inputStream, long partSize) throws Exception {
        return ossUtil.uploadPart(objectName, uploadId, partNumber, inputStream, partSize);
    }

    @Override
    public void completeMultipartUpload(String objectName, String uploadId,
                                        java.util.List<java.util.Map.Entry<Integer, String>> partETags)
            throws Exception {
        java.util.List<com.aliyun.oss.model.PartETag> tags = new java.util.ArrayList<>();
        for (java.util.Map.Entry<Integer, String> entry : partETags) {
            tags.add(new com.aliyun.oss.model.PartETag(entry.getKey(), entry.getValue()));
        }
        ossUtil.completeMultipartUpload(objectName, uploadId, tags);
    }

    @Override
    public void abortMultipartUpload(String objectName, String uploadId) throws Exception {
        ossUtil.abortMultipartUpload(objectName, uploadId);
    }

    @Override
    public java.util.List<Integer> listUploadedPartNumbers(String objectName, String uploadId) throws Exception {
        return ossUtil.listUploadedPartNumbers(objectName, uploadId);
    }

    @Override
    public java.util.List<java.util.Map.Entry<Integer, String>> listUploadedParts(String objectName, String uploadId)
            throws Exception {
        return ossUtil.listUploadedParts(objectName, uploadId);
    }
}