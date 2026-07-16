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
@TableName("sys_file_tag_rel")
public class SysFileTagRel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "rel_id", type = IdType.AUTO)
    private Long relId;

    private Long tagId;

    private Long fileId;

    private Long createByUserId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
