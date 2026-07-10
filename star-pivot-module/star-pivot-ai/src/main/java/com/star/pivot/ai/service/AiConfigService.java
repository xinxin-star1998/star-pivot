package com.star.pivot.ai.service;

import com.star.pivot.ai.domain.dto.AiConfigQueryDto;
import com.star.pivot.ai.domain.dto.AiConfigSaveDto;
import com.star.pivot.ai.domain.vo.AiConfigVo;
import com.star.pivot.framework.domain.PageResponse;

public interface AiConfigService {

    PageResponse<AiConfigVo> pageList(AiConfigQueryDto query);

    AiConfigVo getById(Long configId);

    Long save(AiConfigSaveDto dto);

    void remove(Long configId);

    void setDefault(Long configId);
}
