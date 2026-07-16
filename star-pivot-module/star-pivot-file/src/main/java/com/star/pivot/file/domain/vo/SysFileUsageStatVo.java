package com.star.pivot.file.domain.vo;

import lombok.Data;

@Data
public class SysFileUsageStatVo {

    /** 用户ID或部门ID */
    private Long groupId;

    private String groupName;

    private Long fileCount;

    /** 逻辑用量（按行 SUM file_size） */
    private Long totalBytes;

    /** 去重对象数（秒传共享时更接近物理占用） */
    private Long uniqueObjects;
}
