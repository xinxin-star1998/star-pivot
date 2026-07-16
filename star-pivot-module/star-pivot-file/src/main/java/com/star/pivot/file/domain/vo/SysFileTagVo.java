package com.star.pivot.file.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysFileTagVo {

    private Long tagId;

    private String tagName;

    private String tagColor;

    /** 关联查询时带回，列表展示用 */
    private Long fileId;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
