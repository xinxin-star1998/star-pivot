package com.star.pivot.ai.domain.dto;

import com.star.pivot.framework.domain.PageReqBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AiKnowledgeBaseQueryDto extends PageReqBo {

    private String kbName;

    private String status;
}
