package com.star.pivot.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 文件中心配置（上传 + 预览）。
 */
@Data
@Component
@ConfigurationProperties(prefix = "file-center")
public class FileCenterProperties {

    private Upload upload = new Upload();
    private Preview preview = new Preview();
    private Download download = new Download();
    private Watermark watermark = new Watermark();

    @Data
    public static class Upload {
        private Map<String, Long> maxSizeByMediaType = defaultMaxSizes();
        private long multipartThreshold = 5L * 1024 * 1024;
        private long multipartPartSize = 5L * 1024 * 1024;
    }

    @Data
    public static class Preview {
        /**
         * Office 预览策略：microsoft（默认，Office Online） / kkfileview
         */
        private String officeViewer = "microsoft";
        /** kkFileView 服务根地址，如 http://127.0.0.1:8012 */
        private String kkfileviewBaseUrl = "";
        private Set<String> officeExtensions = new HashSet<>(Set.of(
                "doc", "docx", "xls", "xlsx", "ppt", "pptx", "csv", "rtf"
        ));
    }

    @Data
    public static class Download {
        /** 批量 ZIP 最多文件数 */
        private int zipMaxFiles = 50;
        /** 批量 ZIP 总大小上限（字节），默认 500MB */
        private long zipMaxTotalBytes = 500L * 1024 * 1024;
        /** 每用户保留最近访问条数 */
        private int recentKeep = 100;
    }

    @Data
    public static class Watermark {
        /** 预览水印开关 */
        private boolean enabled = true;
        /**
         * 文案模板，占位符：{username} {nickname} {datetime} {date} {time}
         */
        private String contentTemplate = "{username} {datetime}";
        /** 分享页访客显示名 */
        private String guestName = "分享访客";
        private int fontSize = 14;
        private String fontColor = "rgba(0, 0, 0, 0.12)";
        private int rotate = -22;
        private int gapX = 120;
        private int gapY = 120;
        /** 图片下载是否服务端叠加水印（仅 IMAGE） */
        private boolean downloadEnabled = true;
    }

    /** 兼容旧代码：file-center.upload.* 与扁平 getter */
    public Map<String, Long> getMaxSizeByMediaType() {
        return upload.getMaxSizeByMediaType();
    }

    public long getMultipartThreshold() {
        return upload.getMultipartThreshold();
    }

    public long getMultipartPartSize() {
        return upload.getMultipartPartSize();
    }

    private static Map<String, Long> defaultMaxSizes() {
        Map<String, Long> map = new HashMap<>();
        map.put("IMAGE", 10L * 1024 * 1024);
        map.put("VIDEO", 2L * 1024 * 1024 * 1024);
        map.put("DOCUMENT", 50L * 1024 * 1024);
        map.put("AUDIO", 50L * 1024 * 1024);
        map.put("OTHER", 50L * 1024 * 1024);
        return map;
    }
}
