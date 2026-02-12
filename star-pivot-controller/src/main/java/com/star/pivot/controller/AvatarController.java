package com.star.pivot.controller;

import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.ServiceException;
import com.star.pivot.framework.utils.MinioUtil;
import com.star.pivot.framework.utils.OssUtil;
import com.star.pivot.security.utils.SecurityContextUtils;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 头像管理控制器
 * 
 * <p>安全说明：
 * <ul>
 *   <li>上传/删除头像需要校验：userId 必须与当前登录用户一致，或当前用户具备 system:user:edit 权限</li>
 *   <li>获取临时访问链接需要校验：filePath 必须属于当前用户，或当前用户具备 system:user:edit 权限</li>
 * </ul>
 */
@RestController
@RequestMapping("/avatar")
public class AvatarController {

    @Autowired
    private OssUtil ossUtil;
    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 判断指定用户是否为管理员角色
     *
     * @param userId 用户ID
     * @return 如果用户拥有 admin 角色返回 true，否则返回 false
     */
    private boolean isAdminUser(Long userId) {
        List<SysRole> roles = sysUserService.getRolesByUserId(userId);
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.stream().anyMatch(role -> AppConstants.ADMIN_ROLE_KEY.equals(role.getRoleKey()));
    }

    /**
     * 检查当前用户是否有权限操作指定用户ID的资源
     * 
     * @param targetUserId 目标用户ID（字符串格式）
     * @return 是否有权限
     */
    private boolean hasPermissionToOperateUser(String targetUserId) {
        // 获取当前登录用户ID
        Long currentUserId = SecurityContextUtils.getUserId();
        if (currentUserId == null) {
            return false;
        }

        // 解析目标用户ID
        final Long targetId;
        try {
            targetId = Long.parseLong(targetUserId);
        } catch (NumberFormatException e) {
            // userId 格式错误，不允许操作
            return false;
        }

        // 如果目标用户ID与当前用户ID一致，允许操作
        if (currentUserId.equals(targetId)) {
            return true;
        }

        // 非管理员用户：只能操作自己的头像，不能操作任何其他用户
        if (!isAdminUser(currentUserId)) {
            return false;
        }

        // 管理员用户：不允许操作系统预置的超级管理员账号（除非是本人，上面已返回）
        if (AppConstants.ADMIN_USER_ID.equals(targetId)) {
            return false;
        }

        // 管理员可以操作其它非超级管理员用户的头像
        return true;
    }

    /**
     * 从文件路径中提取用户ID
     * 
     * @param filePath 文件路径，格式：avatar/{userId}.{suffix} 或 user/{userId}/avatar_xxx.xxx（兼容旧格式）
     * @return 用户ID字符串，如果无法提取则返回 null
     */
    private String extractUserIdFromPath(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        // 新路径格式：avatar/{userId}.{suffix}
        if (filePath.startsWith("avatar/")) {
            String fileName = filePath.substring(7); // 移除 "avatar/" 前缀
            // 提取文件名（去掉后缀）
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex > 0) {
                return fileName.substring(0, dotIndex);
            }
            return fileName;
        }
        // 兼容旧路径格式：user/{userId}/avatar_xxx.xxx
        if (filePath.startsWith("user/")) {
            String[] parts = filePath.split("/");
            if (parts.length >= 2) {
                return parts[1];
            }
        }
        return null;
    }

    /**
     * 上传头像
     * 
     * <p>权限校验：userId 必须与当前登录用户一致，或当前用户具备 system:user:edit 权限
     * 
     * @param file 头像文件
     * @param userId 用户ID
     * @param usePresignedUrl 是否使用临时访问链接（可选，默认为false）
     * @return 上传结果，包含头像URL
     */
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId,
            @RequestParam(value = "usePresignedUrl", defaultValue = "false") boolean usePresignedUrl) {
        try {
            // 权限校验：只能上传自己的头像，或具备管理权限
            if (!hasPermissionToOperateUser(userId)) {
                throw new ServiceException("无权操作该用户的头像", 403);
            }

            Map<String, String> data = new HashMap<>();
            
            if (usePresignedUrl) {
                // 私有桶场景：上传一次，返回永久 URL（存库）与预签名 URL（前端展示用，避免 403）
                String objectName = ossUtil.uploadAvatar(file, userId);
                String avatarUrl = ossUtil.getPermanentUrl(objectName);
                String presignedUrl = ossUtil.getPresignedUrl(objectName);
                data.put("avatarUrl", avatarUrl);
                data.put("presignedUrl", presignedUrl);
                data.put("isPresigned", "true");
            } else {
                // 使用完整访问URL（公共读桶场景）
                String avatarUrl = ossUtil.uploadAvatarWithUrl(file, userId);
                data.put("avatarUrl", avatarUrl);
                data.put("isPresigned", "false");
            }
            
            return Result.success("上传成功", data);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取头像临时访问链接
     * 
     * <p>权限校验：filePath 必须属于当前用户，或当前用户具备 system:user:edit 权限
     * 
     * @param filePath 头像文件路径（格式：user/{userId}/avatar_xxx.xxx）
     * @return 临时访问链接
     */
    @GetMapping("/presigned-url")
    public Result<Map<String, String>> getPresignedUrl(
            @RequestParam("filePath") String filePath) {
        try {
            // 从文件路径中提取用户ID
            String userId = extractUserIdFromPath(filePath);
            if (userId == null) {
                throw new ServiceException("无效的文件路径格式", 400);
            }

            // 权限校验：只能获取自己头像的临时链接，或具备管理权限
            if (!hasPermissionToOperateUser(userId)) {
                throw new ServiceException("无权访问该用户的头像", 403);
            }

            String presignedUrl = ossUtil.getPresignedUrl(filePath);
            Map<String, String> data = new HashMap<>();
            data.put("presignedUrl", presignedUrl);
            return Result.success("获取成功", data);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 删除头像
     * 
     * <p>权限校验：userId 必须与当前登录用户一致，或当前用户具备 system:user:edit 权限
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    public Result<?> delete(@RequestParam("userId") String userId) {
        try {
            // 权限校验：只能删除自己的头像，或具备管理权限
            if (!hasPermissionToOperateUser(userId)) {
                throw new ServiceException("无权删除该用户的头像", 403);
            }

            ossUtil.deleteAvatar(userId);
            return Result.success("删除成功");
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}