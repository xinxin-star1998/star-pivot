package com.star.pivot.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统语言表 sys_lang
 */
@Data
@TableName("sys_lang")
public class SysLang implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "lang_id", type = IdType.AUTO)
    private Long langId;

    private String langCode;

    private String langName;

    /** 是否默认（1是 0否） */
    private String isDefault;

    /** 状态（0正常 1停用） */
    private String status;

    private Integer orderNum;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
