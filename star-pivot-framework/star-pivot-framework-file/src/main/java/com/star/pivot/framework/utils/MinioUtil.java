package com.star.pivot.framework.utils;

import com.star.pivot.framework.exception.BizException;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.Result;
import io.minio.messages.Item;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * MinIO 工具类
 * 用于文件上传、下载、删除等操作，重点适配头像上传场景
 */
@Component
@Lazy(false)
@Slf4j // 增加日志注解，统一日志输出
public class MinioUtil {

    // 定义头像上传的常量：大小限制(2MB)、允许的文件类型、非法字符正则
    private static final long AVATAR_MAX_SIZE = 2 * 1024 * 1024;
    private static final String[] ALLOWED_AVATAR_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};
    // 过滤文件名中的特殊字符，避免MinIO路径异常
    private static final Pattern ILLEGAL_CHAR_PATTERN = Pattern.compile("[\\\\/:*?\"<>|]");

    @Autowired
    private MinioProperties minioProperties;

    /**
     * 获取 MinIO 客户端（每次获取新实例，MinIO客户端为无状态设计，无需单例）
     */
    private MinioClient getMinioClient() throws BizException {
        try {
            return MinioClient.builder()
                    .endpoint(minioProperties.getEndpoint())
                    .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                    .build();
        } catch (Exception e) {
            log.error("创建MinIO客户端失败：{}", e.getMessage(), e);
            throw new BizException("创建MinIO客户端失败：" + e.getMessage(), e);
        }
    }

    /**
     * 确保存储桶存在，不存在则创建
     */
    private void ensureBucketExists() throws BizException {
        try {
            MinioClient minioClient = getMinioClient();
            String bucketName = minioProperties.getBucketName();
            BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();

            if (!minioClient.bucketExists(bucketExistsArgs)) {
                MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(bucketName).build();
                minioClient.makeBucket(makeBucketArgs);
                log.info("MinIO存储桶[{}]不存在，已自动创建", bucketName);
            }
        } catch (Exception e) {
            log.error("确认MinIO存储桶存在性失败：{}", e.getMessage(), e);
            throw new BizException("确认MinIO存储桶存在性失败：" + e.getMessage(), e);
        }
    }

    /**
     * 上传头像文件（核心方法，与 OssUtil 行为一致）
     * 特性：分用户存储、每个用户仅保留一个头像（avatar/userId.suffix）、先删旧头像再上传、自动校验大小与类型
     * @param file 头像文件(MultipartFile)
     * @param userId 用户ID
     * @return 头像在MinIO中的完整对象路径（如：avatar/1001.png）
     * @throws UtilException 校验失败/上传失败时抛出异常
     */
    public String uploadAvatar(MultipartFile file, String userId) throws BizException {
        // 1. 基础非空校验
        if (file == null || file.isEmpty()) {
            throw new BizException("上传的头像文件不能为空");
        }
        if (!StringUtils.hasText(userId)) {
            throw new BizException("用户ID不能为空，无法上传头像");
        }

        // 2. 头像专属校验：大小+文件类型
        checkAvatarFileValid(file);

        // 3. 对象路径：avatar/{userId}.{suffix}（与 OssUtil 一致，同一用户唯一头像）
        String originalFilename = file.getOriginalFilename();
        String suffix = getFileSuffix(originalFilename);
        String objectName = "avatar/" + userId + suffix;

        MinioClient minioClient = getMinioClient();
        try {
            // 4. 先上传新文件（先传后删，避免上传失败时丢失旧头像）
            try (InputStream inputStream = file.getInputStream()) {
                PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(objectName)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build();
                minioClient.putObject(putObjectArgs);
            }

            // 5. 上传成功后，删除该用户的旧头像（若存在）
            String prefix = "avatar/" + userId;
            ListObjectsArgs listArgs = ListObjectsArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .prefix(prefix)
                    .build();
            for (Result<Item> result : minioClient.listObjects(listArgs)) {
                Item item = result.get();
                if (item != null && item.objectName() != null && !item.objectName().equals(objectName)) {
                    minioClient.removeObject(RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(item.objectName())
                            .build());
                    log.debug("已删除用户[{}]旧头像：{}", userId, item.objectName());
                }
            }

            log.info("用户[{}]头像上传成功，MinIO对象路径：{}", userId, objectName);
            return objectName;
        } catch (BizException e) {
            log.error("MinIO上传头像失败，用户ID：{}，错误码：{}，错误信息：{}",
                    userId, e.getCode(), e.getMessage(), e);
            throw new BizException("头像上传至MinIO失败：" + e.getMessage(), e);
        } catch (Exception e) {
            log.error("MinIO上传头像失败，用户ID：{}，错误信息：{}", userId, e.getMessage(), e);
            throw new BizException("头像上传至MinIO失败：" + e.getMessage(), e);
        }
    }

    /**
     * 上传头像并返回**永久访问URL**（适用于**公共桶**场景）
     * 公共桶：MinIO存储桶配置了公共读，无需鉴权即可访问
     * @param file 头像文件
     * @param userId 用户ID
     * @return 完整永久访问URL（如：<a href="http://127.0.0.1:9000/bucket/avatar/1001_8f2e3d4c.png">...</a>）
     */
    public String uploadAvatarWithUrl(MultipartFile file, String userId) throws BizException {
        String objectName = uploadAvatar(file, userId);
        return getPermanentUrl(objectName);
    }

    /**
     * 上传头像并返回**预签名临时URL**（适用于**私有桶**场景，推荐生产环境）
     * 私有桶：文件不可公开访问，仅通过预签名URL临时访问（默认7天有效期）
     * @param file 头像文件
     * @param userId 用户ID
     * @return 7天有效期的预签名访问URL
     */
    public String uploadAvatarWithPresignedUrl(MultipartFile file, String userId) throws BizException {
        String objectName = uploadAvatar(file, userId);
        return getPresignedUrl(objectName);
    }

    /**
     * 上传普通文件（通用方法，适用于非头像的其他文件）
     * @param file 上传文件
     * @param objectName 自定义MinIO对象路径（如：file/2026/02/test.txt）
     * @return MinIO对象路径
     */
    public String uploadFile(MultipartFile file, String objectName) throws BizException {
        if (file == null || file.isEmpty() || !StringUtils.hasText(objectName)) {
            throw new BizException("文件或对象路径不能为空");
        }
        ensureBucketExists();
        MinioClient minioClient = getMinioClient();
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            minioClient.putObject(putObjectArgs);
            log.info("普通文件上传成功，MinIO对象路径：{}", objectName);
            return objectName;
        } catch (Exception e) {
            log.error("MinIO上传普通文件失败，对象路径：{}，错误信息：{}", objectName, e.getMessage(), e);
            throw new BizException("文件上传失败：" + e.getMessage(), e);
        }
    }

    /**
     * 上传文件流（通用方法，适用于非MultipartFile的流场景）
     */
    public String uploadFile(InputStream inputStream, long size, String contentType, String objectName) throws BizException {
        if (inputStream == null || size <= 0 || !StringUtils.hasText(contentType) || !StringUtils.hasText(objectName)) {
            throw new BizException("流、大小、类型、路径均不能为空");
        }
        ensureBucketExists();
        MinioClient minioClient = getMinioClient();
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(objectName)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .build();
            minioClient.putObject(putObjectArgs);
            log.info("文件流上传成功，MinIO对象路径：{}", objectName);
            return objectName;
        } catch (MinioException e) {
            log.error("MinIO上传文件流失败，对象路径：{}，错误信息：{}", objectName, e.getMessage(), e);
            throw new BizException("文件流上传失败：" + e.getMessage(), e);
        } catch (Exception e) {
            log.error("MinIO上传文件流失败，对象路径：{}，错误信息：{}", objectName, e.getMessage(), e);
            throw new BizException("文件流上传失败：" + e.getMessage(), e);
        } finally {
            // 关闭传入的流，防止资源泄漏
            try {
                inputStream.close();
            } catch (Exception e) {
                log.error("关闭输入流失败：{}", e.getMessage(), e);
            }
        }
    }

    /**
     * 下载文件：返回文件输入流（使用后需手动关闭流）
     */
    public InputStream downloadFile(String objectName) throws BizException {
        checkObjectname(objectName);
        MinioClient minioClient = getMinioClient();
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(objectName)
                    .build();
            log.info("开始下载MinIO文件，对象路径：{}", objectName);
            return minioClient.getObject(getObjectArgs);
        } catch (MinioException e) {
            log.error("MinIO下载文件失败，对象路径：{}，错误信息：{}", objectName, e.getMessage(), e);
            throw new BizException("文件下载失败：" + e.getMessage(), e);
        } catch (Exception e) {
            log.error("MinIO下载文件失败，对象路径：{}，错误信息：{}", objectName, e.getMessage(), e);
            throw new BizException("文件下载失败：" + e.getMessage(), e);
        }
    }

    /**
     * 删除用户头像（与 OssUtil 一致：按 userId 删除 avatar/ 下该用户所有文件）
     * @param userId 用户ID
     */
    public void deleteAvatar(String userId) throws BizException {
        if (!StringUtils.hasText(userId)) {
            throw new BizException("用户ID不能为空");
        }
        String prefix = "avatar/" + userId;
        MinioClient minioClient = getMinioClient();
        try {
            ListObjectsArgs listArgs = ListObjectsArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .prefix(prefix)
                    .build();
            for (Result<Item> result : minioClient.listObjects(listArgs)) {
                Item item = result.get();
                if (item != null && item.objectName() != null) {
                    minioClient.removeObject(RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(item.objectName())
                            .build());
                    log.info("MinIO已删除用户[{}]头像：{}", userId, item.objectName());
                }
            }
        } catch (Exception e) {
            log.error("MinIO删除用户头像失败，用户ID：{}，错误信息：{}", userId, e.getMessage(), e);
            throw new BizException("删除用户头像失败：" + e.getMessage(), e);
        }
    }

    /**
     * 删除文件（适配头像删除：直接传入 uploadAvatar 返回的路径即可）
     */
    public void deleteFile(String objectName) throws BizException {
        checkObjectname(objectName);
        // 先检查文件是否存在，避免删除不存在的文件抛异常
        if (!fileExists(objectName)) {
            log.warn("MinIO文件不存在，无需删除，对象路径：{}", objectName);
            return;
        }
        MinioClient minioClient = getMinioClient();
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(objectName)
                    .build();
            minioClient.removeObject(removeObjectArgs);
            log.info("MinIO文件删除成功，对象路径：{}", objectName);
        } catch (MinioException e) {
            log.error("MinIO删除文件失败，对象路径：{}，错误信息：{}", objectName, e.getMessage(), e);
            throw new BizException("文件删除失败：" + e.getMessage(), e);
        } catch (Exception e) {
            log.error("MinIO删除文件失败，对象路径：{}，错误信息：{}", objectName, e.getMessage(), e);
            throw new BizException("文件删除失败：" + e.getMessage(), e);
        }
    }

    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String objectName) {
        if (!StringUtils.hasText(objectName)) {
            return false;
        }
        try {
            MinioClient minioClient = getMinioClient();
            StatObjectArgs statObjectArgs = StatObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(objectName)
                    .build();
            minioClient.statObject(statObjectArgs);
            return true;
        } catch (Exception e) {
            // 捕获所有异常，不存在则返回false
            return false;
        }
    }

    /**
     * 获取文件元信息
     */
    public StatObjectResponse getFileStat(String objectName) throws BizException {
        checkObjectname(objectName);
        MinioClient minioClient = getMinioClient();
        try {
            StatObjectArgs statObjectArgs = StatObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(objectName)
                    .build();
            return minioClient.statObject(statObjectArgs);
        } catch (MinioException e) {
            log.error("MinIO获取文件信息失败，对象路径：{}，错误信息：{}", objectName, e.getMessage(), e);
            throw new BizException("获取文件信息失败：" + e.getMessage(), e);
        } catch (Exception e) {
            log.error("MinIO获取文件信息失败，对象路径：{}，错误信息：{}", objectName, e.getMessage(), e);
            throw new BizException("获取文件信息失败：" + e.getMessage(), e);
        }
    }

    /**
     * 生成文件预签名临时访问链接（私有桶专用，默认7天有效期）
     */
    public String getPresignedUrl(String objectName) throws BizException {
        try {
            checkObjectname(objectName);
            MinioClient minioClient = getMinioClient();
            GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(minioProperties.getBucketName())
                    .object(objectName)
                    .expiry(7, TimeUnit.DAYS) // 有效期7天，可根据需求调整
                    .build();
            String presignedUrl = minioClient.getPresignedObjectUrl(args);
            log.info("生成MinIO预签名URL成功，对象路径：{}，有效期7天", objectName);
            return presignedUrl;
        } catch (Exception e) {
            log.error("MinIO生成预签名URL失败，对象路径：{}，错误信息：{}", objectName, e.getMessage(), e);
            throw new BizException("生成临时访问链接失败：" + e.getMessage(), e);
        }
    }

    // -------------------------- 私有工具方法：封装通用逻辑，避免代码冗余 --------------------------
    /**
     * 校验头像文件的合法性：大小+文件类型
     */
    private void checkAvatarFileValid(MultipartFile file) throws BizException {
        // 校验大小
        if (file.getSize() > AVATAR_MAX_SIZE) {
            throw new BizException("头像文件大小不能超过2MB");
        }
        // 校验类型
        String contentType = file.getContentType();
        boolean isAllowed = false;
        for (String allowedType : ALLOWED_AVATAR_TYPES) {
            if (allowedType.equals(contentType)) {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed) {
            throw new BizException("头像仅支持JPG、PNG、GIF、WEBP格式");
        }
    }

    /**
     * 获取文件后缀（处理空文件名、无后缀的情况，默认返回.png）
     */
    private String getFileSuffix(String originalFilename) {
        String suffix = ".png"; // 默认后缀，防止无后缀文件
        if (StringUtils.hasText(originalFilename)) {
            // 过滤文件名中的非法字符，避免MinIO路径异常
            String cleanName = ILLEGAL_CHAR_PATTERN.matcher(originalFilename).replaceAll("");
            if (cleanName.contains(".")) {
                suffix = cleanName.substring(cleanName.lastIndexOf("."));
                // 统一后缀为小写，避免PNG和png共存
                suffix = suffix.toLowerCase();
            }
        }
        return suffix;
    }

    /**
     * 构造文件永久访问URL（与 OssUtil 一致：优先 urlPrefix，否则 endpoint/bucket/objectName）
      */
    public String getPermanentUrl(String objectName) {
        String urlPrefix = minioProperties.getUrlPrefix();
        if (StringUtils.hasText(urlPrefix)) {
            String prefix = urlPrefix.endsWith("/") ? urlPrefix.substring(0, urlPrefix.length() - 1) : urlPrefix;
            String object = objectName.startsWith("/") ? objectName : "/" + objectName;
            return prefix + object;
        }
        String endpoint = minioProperties.getEndpoint();
        String bucketName = minioProperties.getBucketName();
        endpoint = StringUtils.trimTrailingCharacter(StringUtils.trimLeadingCharacter(endpoint, '/'), '/');
        bucketName = StringUtils.trimTrailingCharacter(StringUtils.trimLeadingCharacter(bucketName, '/'), '/');
        objectName = StringUtils.trimLeadingCharacter(objectName, '/');
        return String.format("%s/%s/%s", endpoint, bucketName, objectName);
    }

    /**
     * 校验对象路径是否为空
     */
    private void checkObjectname(String objectName) throws BizException {
        if (!StringUtils.hasText(objectName)) {
            throw new BizException("MinIO对象路径不能为空");
        }
    }
}