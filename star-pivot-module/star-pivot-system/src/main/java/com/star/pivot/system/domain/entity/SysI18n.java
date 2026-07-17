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
 * 通用国际化翻译表 sys_i18n
 */
@Data
@TableName("sys_i18n")
public class SysI18n implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "i18n_id", type = IdType.AUTO)
    private Long i18nId;

    private String namespace;

    private String resourceKey;

    private String fieldName;

    private String lang;

    private String content;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
