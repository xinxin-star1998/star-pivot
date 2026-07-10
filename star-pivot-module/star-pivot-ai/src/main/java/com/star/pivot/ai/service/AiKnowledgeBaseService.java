package com.star.pivot.ai.service;

import com.star.pivot.ai.domain.dto.AiKnowledgeBaseQueryDto;
import com.star.pivot.ai.domain.dto.AiKnowledgeBaseSaveDto;
import com.star.pivot.ai.domain.vo.AiKnowledgeBaseVo;
import com.star.pivot.framework.domain.PageResponse;

import java.util.List;

public interface AiKnowledgeBaseService {

    PageResponse<AiKnowledgeBaseVo> pageList(AiKnowledgeBaseQueryDto query);

    List<AiKnowledgeBaseVo> listEnabled();

    AiKnowledgeBaseVo getById(Long kbId);

    Long save(AiKnowledgeBaseSaveDto dto);

    void remove(Long kbId);
}
