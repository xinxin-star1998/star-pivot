package com.star.pivot.controller;

import com.star.pivot.common.domain.Result;
import com.star.pivot.common.utils.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/avatar")
public class AvatarController {

    @Autowired
    private MinioUtil minioUtil;

    /**
     * 上传头像
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
            Map<String, String> data = new HashMap<>();
            
            if (usePresignedUrl) {
                // 使用临时访问链接
                String presignedUrl = minioUtil.uploadAvatarWithPresignedUrl(file, userId);
                data.put("avatarUrl", presignedUrl);
                data.put("isPresigned", "true");
            } else {
                // 使用完整访问URL
                String avatarUrl = minioUtil.uploadAvatarWithUrl(file, userId);
                data.put("avatarUrl", avatarUrl);
                data.put("isPresigned", "false");
            }
            
            return Result.success("上传成功", data);
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取头像临时访问链接
     * @param filePath 头像文件路径
     * @return 临时访问链接
     */
    @GetMapping("/presigned-url")
    public Result<Map<String, String>> getPresignedUrl(
            @RequestParam("filePath") String filePath) {
        try {
            String presignedUrl = minioUtil.getPresignedUrl(filePath);
            Map<String, String> data = new HashMap<>();
            data.put("presignedUrl", presignedUrl);
            return Result.success("获取成功", data);
        } catch (Exception e) {
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 删除头像
     */
    @DeleteMapping("/delete")
    public Result<?> delete(@RequestParam("userId") String userId) {
        try {
            minioUtil.deleteAvatar(userId);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}