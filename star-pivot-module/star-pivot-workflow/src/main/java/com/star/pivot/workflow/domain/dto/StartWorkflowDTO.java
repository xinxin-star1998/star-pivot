package com.star.pivot.workflow.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class StartWorkflowDTO {

    @NotBlank(message = "流程编码不能为空")
    private String processCode;

    @NotBlank(message = "业务键不能为空")
    private String businessKey;

    @NotBlank(message = "标题不能为空")
    private String title;

    private Map<String, Object> variables;
}
