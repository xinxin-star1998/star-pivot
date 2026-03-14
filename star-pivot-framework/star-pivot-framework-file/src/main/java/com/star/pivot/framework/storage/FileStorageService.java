package com.star.pivot.framework.storage;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件存储服务统一接口
 * 支持多种存储方式（MinIO、OSS等）的统一抽象
 *
 * @author stardust
 */
public interface FileStorageService {

    /**
     * 上传头像文件
     *
     * @param file 头像文件
     * @param userId 用户ID
     * @return 头像文件在存储桶中的路径（如：avatar/123.png）
     * @throws Exception 上传失败时抛出异常
     */
    String uploadAvatar(MultipartFile file, String userId) throws Exception;

    /**
     * 上传头像文件并返回完整访问URL（适用于公共桶场景）
     *
     * @param file 头像文件
     * @param userId 用户ID
     * @return 完整的头像访问URL
     * @throws Exception 上传失败时抛出异常
     */
    String uploadAvatarWithUrl(MultipartFile file, String userId) throws Exception;

    /**
     * 上传头像文件并返回临时访问链接（适用于私有桶场景）
     *
     * @param file 头像文件
     * @param userId 用户ID
     * @return 临时访问链接（默认7天有效期）
     * @throws Exception 上传失败时抛出异常
     */
    String uploadAvatarWithPresignedUrl(MultipartFile file, String userId) throws Exception;

    /**
     * 删除用户头像
     *
     * @param userId 用户ID
     * @throws Exception 删除失败时抛出异常
     */
    void deleteAvatar(String userId) throws Exception;

    /**
     * 生成文件临时访问链接（私有桶专用）
     *
     * @param objectName 文件路径（如：avatar/123.png）
     * @return 临时访问链接（默认7天有效期）
     * @throws Exception 生成失败时抛出异常
     */
    String getPresignedUrl(String objectName) throws Exception;

    /**
     * 生成文件永久访问URL（公共桶专用）
     *
     * @param objectName 文件路径（如：avatar/123.png）
     * @return 永久访问URL
     */
    String getPermanentUrl(String objectName);

    /**
     * 上传头像并返回完整信息（包含永久URL和预签名URL）
     *
     * @param file 头像文件
     * @param userId 用户ID
     * @return 包含 avatarUrl 和 presignedUrl 的 Map
     * @throws Exception 上传失败时抛出异常
     */
    default Map<String, String> uploadAvatarWithFullInfo(MultipartFile file, String userId) throws Exception {
        String objectName = uploadAvatar(file, userId);
        String avatarUrl = getPermanentUrl(objectName);
        String presignedUrl = getPresignedUrl(objectName);
        
        return Map.of(
            "avatarUrl", avatarUrl,
            "presignedUrl", presignedUrl
        );
    }
}
