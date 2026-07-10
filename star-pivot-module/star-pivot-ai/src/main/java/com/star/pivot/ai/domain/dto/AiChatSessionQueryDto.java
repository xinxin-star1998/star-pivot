package com.star.pivot.ai.domain.dto;

import com.star.pivot.framework.domain.PageReqBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AiChatSessionQueryDto extends PageReqBo {

    private Long userId;

    private String conversationId;

    private String title;
}
