package com.star.pivot.ai.service;

import com.star.pivot.ai.domain.dto.AiChatSessionQueryDto;
import com.star.pivot.ai.domain.vo.AiChatSessionAdminVo;
import com.star.pivot.ai.domain.vo.ChatHistoryMessageVo;
import com.star.pivot.framework.domain.PageResponse;

import java.util.List;

public interface AiChatSessionAdminService {

    PageResponse<AiChatSessionAdminVo> pageList(AiChatSessionQueryDto query);

    List<ChatHistoryMessageVo> listMessages(String conversationId);

    void remove(Long sessionId);
}
