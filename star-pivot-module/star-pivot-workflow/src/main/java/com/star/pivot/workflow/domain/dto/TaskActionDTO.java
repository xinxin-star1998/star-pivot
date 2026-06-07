package com.star.pivot.workflow.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskActionDTO {

    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    private String comment;
}
