package com.star.pivot.controller.system;

import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.storage.FileStorageService;
import com.star.pivot.security.context.SecurityContextUtils;
import com.star.pivot.system.service.PermissionService;
import com.star.pivot.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 头像管理控制器
 *
 * <p>安全说明：
 * <ul>
 *   <li>个人中心场景：用户只能查看和修改自己的头像</li>
 *   <li>后台管理场景：拥有"用户管理"权限（system:user:list）的管理员，只能查看其他用户的头像，不可修改</li>
 *   <li>超级管理员（admin角色）：可以查看和修改所有用户头像</li>
 *   <li>不允许操作系统预置的超级管理员账号头像</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/avatar")
@RequiredArgsConstructor
public class AvatarController {

    private final FileStorageService fileStorageService;
    private final SysUserService sysUserService;
    private final PermissionService permissionService;

    /**
     * 用户管理权限标识
     */
    private static final String USER_MANAGE_PERMISSION = "system:user:list";

    /**
     * 判断当前用户是否是超级管理员
     */
    private boolean isSuperAdmin() {
        Long currentUserId = SecurityContextUtils.getUserId();
        if (currentUserId == null) {
            return false;
        }
        // 超级管理员用户ID
        if (AppConstants.ADMIN_USER_ID.equals(currentUserId)) {
            return true;
        }
        // 拥有 admin 角色的用户
        return permissionService.hasRole(AppConstants.ADMIN_ROLE_KEY);
    }

    /**
     * 判断当前用户是否拥有用户管理权限
     */
    private boolean hasUserManagePermission() {
        return permissionService.hasPermission(USER_MANAGE_PERMISSION);
    }

    /**
     * 检查当前用户是否有权限查看指定用户ID的头像
     *
     * @param targetUserId 目标用户ID（字符串格式）
     * @return 是否有权限查看
     */
    private boolean hasPermissionToViewUser(String targetUserId) {
        Long currentUserId = SecurityContextUtils.getUserId();
        if (currentUserId == null) {
            return false;
        }

        final Long targetId;
        try {
            targetId = Long.parseLong(targetUserId);
        } catch (NumberFormatException e) {
            return false;
        }

        // 自己的头像允许查看
        if (currentUserId.equals(targetId)) {
            return true;
        }

        // 拥有用户管理权限的管理员可以查看其他用户头像
        if (!hasUserManagePermission()) {
            return false;
        }

        // 不允许查看超级管理员头像
//        if (AppConstants.ADMIN_USER_ID.equals(targetId)) {
//            return false;
//        }

        return true;
    }

    /**
     * 检查当前用户是否有权限修改（上传/删除）指定用户ID的头像
     * 注意：只有用户本人或超级管理员可以修改头像
     *
     * @param targetUserId 目标用户ID（字符串格式）
     * @return 是否有权限修改
     */
    private boolean hasPermissionToModifyUser(String targetUserId) {
        Long currentUserId = SecurityContextUtils.getUserId();
        if (currentUserId == null) {
            return false;
        }

        final Long targetId;
        try {
            targetId = Long.parseLong(targetUserId);
        } catch (NumberFormatException e) {
            return false;
        }

        // 自己可以修改自己的头像
        if (currentUserId.equals(targetId)) {
            return true;
        }

        // 超级管理员可以修改所有头像
        return isSuperAdmin();
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
     * <p>权限校验：只能上传自己的头像（超级管理员可上传所有用户头像）
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
            // 文件大小验证（限制5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new BizException(ErrorCode.PARAM_INVALID, "文件大小不能超过5MB");
            }
            
            // 文件类型验证
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/") && 
                !contentType.startsWith("application/octet-stream"))) {
                throw new BizException(ErrorCode.PARAM_INVALID, "只允许上传图片文件");
            }
            
            // 权限校验：只能上传自己的头像（超级管理员除外）
            if (!hasPermissionToModifyUser(userId)) {
                throw new BizException(ErrorCode.ACCESS_DENIED, "只能上传自己的头像");
            }

            Map<String, String> data = new HashMap<>();
            
            if (usePresignedUrl) {
                Map<String, String> uploadResult = fileStorageService.uploadAvatarWithFullInfo(file, userId);
                data.put("avatarUrl", uploadResult.get("avatarUrl"));
                data.put("presignedUrl", uploadResult.get("presignedUrl"));
                data.put("isPresigned", "true");
            } else {
                String avatarUrl = fileStorageService.uploadAvatarWithUrl(file, userId);
                data.put("avatarUrl", avatarUrl);
                data.put("isPresigned", "false");
            }
            
            return Result.success("上传成功", data);
        } catch (BizException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("上传头像参数错误, userId={}", userId, e);
            return Result.error("参数错误：" + e.getMessage());
        } catch (Exception e) {
            log.error("上传头像失败, userId={}", userId, e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取头像临时访问链接
     *
     * <p>权限校验：只能查看自己的头像，或拥有"用户管理"权限的管理员可查看其他用户头像
     *
     * @param filePath 头像文件路径（格式：user/{userId}/avatar_xxx.xxx）
     * @return 临时访问链接
     */
    @GetMapping("/presigned-url")
    public Result<Map<String, String>> getPresignedUrl(
            @RequestParam("filePath") String filePath) {
        try {
            String userId = extractUserIdFromPath(filePath);
            if (userId == null) {
                throw new BizException(ErrorCode.PARAM_INVALID, "无效的文件路径格式");
            }

            if (!hasPermissionToViewUser(userId)) {
                throw new BizException(ErrorCode.ACCESS_DENIED, "无权查看该用户的头像");
            }

            String presignedUrl = fileStorageService.getPresignedUrl(filePath);
            Map<String, String> data = new HashMap<>();
            data.put("presignedUrl", presignedUrl);
            return Result.success("获取成功", data);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 删除头像
     *
     * <p>权限校验：只能删除自己的头像（超级管理员可删除所有用户头像）
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    public Result<?> delete(@RequestParam("userId") String userId) {
        try {
            if (!hasPermissionToModifyUser(userId)) {
                throw new BizException(ErrorCode.ACCESS_DENIED, "只能删除自己的头像");
            }

            fileStorageService.deleteAvatar(userId);
            return Result.success("删除成功");
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}