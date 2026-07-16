package com.star.pivot.file.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SysFileMultipartCompleteDTO {

    @NotBlank(message = "uploadId不能为空")
    private String uploadId;

    @NotBlank(message = "objectName不能为空")
    private String objectName;

    @NotNull(message = "文件夹ID不能为空")
    private Long folderId;

    @NotBlank(message = "文件名不能为空")
    private String fileName;

    @NotNull(message = "文件大小不能为空")
    private Long fileSize;

    private String contentType;

    private String fileHash;

    private String mediaType;

    private String bizType;

    private String bizId;

    private String remark;

    @NotEmpty(message = "分片列表不能为空")
    private List<PartETagItem> parts;

    @Data
    public static class PartETagItem {
        @NotNull
        private Integer partNumber;
        @NotBlank
        private String etag;
    }
}
