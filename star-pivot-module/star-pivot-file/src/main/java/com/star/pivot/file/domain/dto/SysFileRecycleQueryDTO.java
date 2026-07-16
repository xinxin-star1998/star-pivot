package com.star.pivot.file.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.star.pivot.framework.domain.DataScope;
import com.star.pivot.framework.domain.PageReqBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysFileRecycleQueryDTO extends PageReqBo {

    private String category;

    private String fileName;

    private String deleteBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime beginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    /** 数据权限（服务端填充，前端勿传） */
    private DataScope dataScope;

    private List<Long> deptIds;

    private Long userId;

    private Long userDeptId;
}
