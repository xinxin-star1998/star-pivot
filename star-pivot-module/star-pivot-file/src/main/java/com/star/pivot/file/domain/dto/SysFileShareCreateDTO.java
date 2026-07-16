package com.star.pivot.file.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysFileShareCreateDTO {

    @NotNull(message = "文件ID不能为空")
    private Long fileId;

    /** 访问密码，空=无密码 */
    private String password;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expireTime;

    /** 最大访问次数，空=不限 */
    private Integer maxViews;

    /** 是否允许下载，默认 true */
    private Boolean allowDownload = true;

    private String remark;
}
