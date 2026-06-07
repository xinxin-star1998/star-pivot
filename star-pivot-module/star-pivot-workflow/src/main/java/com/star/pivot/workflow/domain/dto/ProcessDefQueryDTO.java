package com.star.pivot.workflow.domain.dto;

import lombok.Data;

@Data
public class ProcessDefQueryDTO {

    private String processCode;

    private String processName;

    private String bizModule;

    private String status;

    private Long pageNum = 1L;

    private Long pageSize = 10L;
}
