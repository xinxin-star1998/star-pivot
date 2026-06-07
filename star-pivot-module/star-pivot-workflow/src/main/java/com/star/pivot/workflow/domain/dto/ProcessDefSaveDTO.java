package com.star.pivot.workflow.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProcessDefSaveDTO {

    private Long defId;

    @NotBlank(message = "流程编码不能为空")
    private String processCode;

    @NotBlank(message = "流程名称不能为空")
    private String processName;

    @NotBlank(message = "业务模块不能为空")
    private String bizModule;

    @NotBlank(message = "流程定义 JSON 不能为空")
    private String defJson;

    private String remark;
}
