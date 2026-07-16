package com.star.pivot.file.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SysFileHashCheckDTO {

    @NotBlank(message = "文件哈希不能为空")
    private String fileHash;

    @NotNull(message = "文件大小不能为空")
    private Long fileSize;

    @NotNull(message = "文件夹ID不能为空")
    private Long folderId;

    private String fileName;

    private String bizType;

    private String bizId;

    private String remark;
}
