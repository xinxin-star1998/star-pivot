package com.star.pivot.file.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysFileAuditVo {

    private Long auditId;

    private Long fileId;

    private String fileName;

    private String action;

    private String actionLabel;

    private String detail;

    private String operBy;

    private Long operByUserId;

    private String operIp;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime operTime;
}
