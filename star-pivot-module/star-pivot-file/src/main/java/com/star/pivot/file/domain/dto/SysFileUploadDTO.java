package com.star.pivot.file.domain.dto;

import lombok.Data;

@Data
public class SysFileUploadDTO {

    private String bizType;

    private String bizId;

    private String remark;

    /** 客户端预计算的 SHA-256（可选） */
    private String fileHash;

    /** 上传人用户 ID（控制器填充） */
    private Long createByUserId;

    /** 上传人部门 ID（控制器填充） */
    private Long createDeptId;
}
