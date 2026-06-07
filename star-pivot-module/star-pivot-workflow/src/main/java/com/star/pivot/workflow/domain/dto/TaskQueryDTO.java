package com.star.pivot.workflow.domain.dto;

import lombok.Data;

@Data
public class TaskQueryDTO {

    private String title;

    private String processCode;

    private Long pageNum = 1L;

    private Long pageSize = 10L;
}
