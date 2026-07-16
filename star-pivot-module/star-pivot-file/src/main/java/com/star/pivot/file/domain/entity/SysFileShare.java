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
@TableName("sys_file_share")
public class SysFileShare implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "share_id", type = IdType.AUTO)
    private Long shareId;

    private Long fileId;

    private String shareCode;

    private String passwordHash;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expireTime;

    private Integer maxViews;

    private Integer viewCount;

    private String allowDownload;

    private String status;

    private String createBy;

    private Long createByUserId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    private String remark;
}
