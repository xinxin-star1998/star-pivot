package com.star.pivot.file.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_file_version")
public class SysFileVersion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "version_id", type = IdType.AUTO)
    private Long versionId;

    private Long fileId;

    private Integer versionNo;

    private String objectName;

    private String fileHash;

    private Long fileSize;

    private String fileName;

    private String contentType;

    private String storageProvider;

    private String createBy;

    private Long createByUserId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private String remark;
}
