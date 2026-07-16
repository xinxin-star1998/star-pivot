package com.star.pivot.file.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SysFileShareVo {

    private Long shareId;

    private Long fileId;

    private String shareCode;

    private String shareUrl;

    private boolean hasPassword;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expireTime;

    private Integer maxViews;

    private Integer viewCount;

    private boolean allowDownload;

    private String status;

    private String fileName;

    private String mediaType;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
