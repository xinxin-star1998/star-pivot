package com.star.pivot.file.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysFileVersionVo {

    private Long versionId;

    private Long fileId;

    private Integer versionNo;

    private String fileName;

    private Long fileSize;

    private String fileHash;

    private String contentType;

    private boolean current;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private String remark;

    private String displayUrl;
}
