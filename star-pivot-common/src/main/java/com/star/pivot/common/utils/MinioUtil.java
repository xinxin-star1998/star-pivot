package com.star.pivot.common.utils;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Component
public class MinioUtil {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.url-prefix}")
    private String urlPrefix;

    /**
     * 创建 MinIO 客户端
     */
    private MinioClient getMinioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * 上传头像文件
     * @param file 头像文件
     * @param userId 用户ID
     * @return 头像文件在存储桶中的路径
     */
    public String uploadAvatar(MultipartFile file, String userId) throws Exception {
        // 生成唯一文件名，避免覆盖
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = "user/" + userId + "/avatar_" + UUID.randomUUID() + suffix;

        MinioClient minioClient = getMinioClient();

        // 上传文件
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }

        // 返回文件在存储桶中的路径
        return objectName;
    }
    
    /**
     * 上传头像文件并返回完整访问URL
     * @param file 头像文件
     * @param userId 用户ID
     * @return 完整的头像访问URL
     */
    public String uploadAvatarWithUrl(MultipartFile file, String userId) throws Exception {
        String objectName = uploadAvatar(file, userId);
        return urlPrefix + "/" + bucketName + "/" + objectName;
    }
    
    /**
     * 上传头像文件并返回临时访问链接
     * @param file 头像文件
     * @param userId 用户ID
     * @return 临时访问链接
     */
    public String uploadAvatarWithPresignedUrl(MultipartFile file, String userId) throws Exception {
        String objectName = uploadAvatar(file, userId);
        return getPresignedUrl(objectName);
    }

    /**
     * 删除用户头像
     * @param userId 用户ID
     */
    public void deleteAvatar(String userId) throws Exception {
        MinioClient minioClient = getMinioClient();
        // 列出该用户下的所有头像文件
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix("user/" + userId + "/")
                        .build()
        );

        // 遍历删除
        for (Result<Item> result : results) {
            Item item = result.get();
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(item.objectName())
                            .build()
            );
        }
    }

    /**
     * 生成文件临时访问链接（私有桶可用，有效期默认7天）
     * @param objectName 文件路径
     * @return 临时URL
     */
    public String getPresignedUrl(String objectName) throws Exception {
        MinioClient minioClient = getMinioClient();
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(60 * 60 * 24 * 7) // 有效期7天
                        .build()
        );
    }
}