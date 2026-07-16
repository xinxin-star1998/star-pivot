package com.star.pivot.file.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class SysFileMultipartInitVo {

    private String uploadId;

    private String objectName;

    private long partSize;

    /** 已上传的分片号（断点续传） */
    private List<Integer> uploadedParts;

    /** 已上传分片及 ETag（优先使用） */
    private List<PartItem> uploadedPartDetails;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartItem {
        private Integer partNumber;
        private String etag;
    }
}
