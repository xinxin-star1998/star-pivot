package com.star.pivot.file.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SysFileMultipartInitDTO {

    @NotNull(message = "文件夹ID不能为空")
    private Long folderId;

    @NotBlank(message = "文件名不能为空")
    private String fileName;

    @NotNull(message = "文件大小不能为空")
    private Long fileSize;

    private String contentType;

    private String fileHash;

    private String bizType;

    private String bizId;

    private String remark;
}
