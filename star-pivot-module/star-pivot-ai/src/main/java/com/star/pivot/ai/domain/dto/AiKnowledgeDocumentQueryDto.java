package com.star.pivot.ai.domain.dto;

import com.star.pivot.framework.domain.PageReqBo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AiKnowledgeDocumentQueryDto extends PageReqBo {

    @NotNull(message = "知识库ID不能为空")
    private Long kbId;

    private String title;

    private String status;
}
