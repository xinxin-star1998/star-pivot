package com.star.pivot.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.Date;

/**
 * 阿里云 OSS 工具类
 * 用于文件上传、删除、获取预签名URL等操作
 */
@Component
public class OssUtil {

    @Value("${oss.endpoint}")
    private String endpoint;

    @Value("${oss.access-key-id}")
    private String accessKeyId;

    @Value("${oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${oss.bucket-name}")
    private String bucketName;

    @Value("${oss.url-prefix}")
    private String urlPrefix;

    /**
     * 创建 OSS 客户端
     */
    private OSS getOssClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * 上传头像文件
     * @param file 头像文件
     * @param userId 用户ID
     * @return 头像文件在存储桶中的路径
     */
    public String uploadAvatar(MultipartFile file, String userId) throws Exception {
        // 生成文件名：avatar/{userId}.{suffix}
        // 每个用户只有一个头像，上传新头像时自动覆盖旧的
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = "avatar/" + userId + suffix;

        OSS ossClient = getOssClient();

        try {
            // 先删除该用户的旧头像（如果存在）
            String prefix = "avatar/" + userId;
            ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
                    .withBucketName(bucketName)
                    .withPrefix(prefix);
            
            ListObjectsV2Result result = ossClient.listObjectsV2(listObjectsRequest);
            
            // 遍历删除旧头像
            for (OSSObjectSummary objectSummary : result.getObjectSummaries()) {
                ossClient.deleteObject(bucketName, objectSummary.getKey());
            }
            
            // 上传新文件
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    objectName,
                    file.getInputStream()
            );
            // 设置文件元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            putObjectRequest.setMetadata(metadata);

            ossClient.putObject(putObjectRequest);
        } finally {
            // 关闭OSSClient
            ossClient.shutdown();
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
        // 阿里云 OSS 的 URL 格式：https://{bucket-name}.{endpoint}/{object-name}
        // 如果配置了 url-prefix，则使用配置的前缀
        if (urlPrefix != null && !urlPrefix.isEmpty()) {
            // 确保 urlPrefix 不以 / 结尾，objectName 不以 / 开头
            String prefix = urlPrefix.endsWith("/") ? urlPrefix.substring(0, urlPrefix.length() - 1) : urlPrefix;
            String object = objectName.startsWith("/") ? objectName : "/" + objectName;
            return prefix + object;
        } else {
            // 默认格式：https://{bucket-name}.{endpoint}/{object-name}
            String endpointWithoutProtocol = endpoint.replace("https://", "").replace("http://", "");
            return "https://" + bucketName + "." + endpointWithoutProtocol + "/" + objectName;
        }
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
        OSS ossClient = getOssClient();
        try {
            // 列出 avatar/ 目录下以 userId 开头的所有文件（支持不同后缀）
            String prefix = "avatar/" + userId;
            ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
                    .withBucketName(bucketName)
                    .withPrefix(prefix);
            
            ListObjectsV2Result result = ossClient.listObjectsV2(listObjectsRequest);
            
            // 遍历删除
            for (OSSObjectSummary objectSummary : result.getObjectSummaries()) {
                ossClient.deleteObject(bucketName, objectSummary.getKey());
            }
        } finally {
            // 关闭OSSClient
            ossClient.shutdown();
        }
    }

    /**
     * 生成文件临时访问链接（私有桶可用，有效期默认7天）
     * @param objectName 文件路径
     * @return 临时URL
     */
    public String getPresignedUrl(String objectName) throws Exception {
        OSS ossClient = getOssClient();
        try {
            // 设置URL过期时间为7天
            Date expiration = new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L);
            // 生成预签名URL
            URL url = ossClient.generatePresignedUrl(bucketName, objectName, expiration);
            return url.toString();
        } finally {
            // 关闭OSSClient
            ossClient.shutdown();
        }
    }
}
