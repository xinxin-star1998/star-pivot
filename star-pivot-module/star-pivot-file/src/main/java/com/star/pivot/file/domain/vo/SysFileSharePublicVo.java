package com.star.pivot.file.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SysFileSharePublicVo {

    private String shareCode;

    private String fileName;

    private String mediaType;

    private String mediaTypeLabel;

    private Long fileSize;

    private String fileExt;

    private boolean hasPassword;

    private boolean expired;

    private boolean allowDownload;

    private boolean unlocked;

    /** 解锁后返回的预览/下载 URL */
    private String url;

    private String previewMode;

    private String viewerUrl;

    /** 预览水印配置（可选） */
    private SysFileWatermarkVo watermark;
}
