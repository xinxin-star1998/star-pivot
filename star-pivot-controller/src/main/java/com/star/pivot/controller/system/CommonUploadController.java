package com.star.pivot.controller.system;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.annotation.NoResponseWrapper;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.storage.FileStorageService;
import com.star.pivot.framework.storage.StoragePathValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用上传接口（富文本、预签名 URL 等）
 */
@Slf4j
@RestController
@RequestMapping("/common/upload")
@RequiredArgsConstructor
public class CommonUploadController {

    private final FileStorageService fileStorageService;

    /**
     * WangEditor 图片上传：成功 { errno:0, data:{ url, alt, href } }；失败 { errno:1, message }
     */
    @Log(title = "富文本图片上传", businessType = 0)
    @NoResponseWrapper
    @PostMapping("/wangeditor")
    public Map<String, Object> uploadWangEditor(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return errorBody("文件不能为空");
        }
        try {
            String url = fileStorageService.uploadEditorImageWithUrl(file);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("url", url);
            data.put("alt", "");
            data.put("href", "");
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("errno", 0);
            body.put("data", data);
            return body;
        } catch (IllegalArgumentException e) {
            log.warn("富文本图片参数错误: {}", e.getMessage());
            return errorBody(e.getMessage());
        } catch (Exception e) {
            log.error("富文本图片上传失败", e);
            String msg = e.getMessage();
            return errorBody(msg != null && !msg.isEmpty() ? msg : "上传失败");
        }
    }

    /**
     * 获取单个文件的预签名 URL
     */
    @GetMapping("/presigned-url")
    public Result<Map<String, String>> getPresignedUrl(@RequestParam("objectName") String objectName) throws Exception {
        if (!StringUtils.hasText(objectName)) {
            return Result.error("对象路径不能为空");
        }
        if (!StoragePathValidator.isAllowedPresignedPath(objectName)) {
            log.warn("尝试获取未授权路径的预签名URL: {}", objectName);
            return Result.error("无权访问该路径的临时访问链接");
        }
        String presignedUrl = fileStorageService.getPresignedUrl(objectName);
        return Result.success("获取成功", Map.of(
                "objectName", objectName,
                "url", presignedUrl
        ));
    }

    /**
     * 批量获取预签名 URL（商品图集回显、列表封面等）
     *
     * @param objectNames OSS 对象路径列表
     * @return key=objectName, value=预签名 URL
     */
    @PostMapping("/presigned-urls")
    public Result<Map<String, String>> getPresignedUrls(@RequestBody List<String> objectNames) throws Exception {
        if (objectNames == null || objectNames.isEmpty()) {
            return Result.success(Map.of());
        }
        Map<String, String> urls = new LinkedHashMap<>();
        for (String objectName : objectNames) {
            if (!StringUtils.hasText(objectName) || !StoragePathValidator.isAllowedPresignedPath(objectName)) {
                continue;
            }
            urls.put(objectName, fileStorageService.getPresignedUrl(objectName));
        }
        return Result.success(urls);
    }

    private static Map<String, Object> errorBody(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("errno", 1);
        body.put("message", message != null ? message : "上传失败");
        return body;
    }
}
