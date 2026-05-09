package com.star.pivot.controller.system;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.annotation.NoResponseWrapper;
import com.star.pivot.framework.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 通用上传（如 WangEditor 富文本图片），返回第三方组件约定 JSON，不走统一 Result 包装。
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

    private static Map<String, Object> errorBody(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("errno", 1);
        body.put("message", message != null ? message : "上传失败");
        return body;
    }
}
