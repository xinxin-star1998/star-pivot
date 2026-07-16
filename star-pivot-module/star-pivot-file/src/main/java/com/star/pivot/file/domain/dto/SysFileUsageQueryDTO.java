package com.star.pivot.file.domain.dto;

import com.star.pivot.framework.domain.DataScope;
import lombok.Data;

import java.util.List;

@Data
public class SysFileUsageQueryDTO {

    /** user | dept */
    private String groupBy = "user";

    private DataScope dataScope;

    private List<Long> deptIds;

    private Long userId;

    private Long userDeptId;
}
