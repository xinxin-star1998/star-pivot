package com.star.pivot.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.workflow.domain.dto.ProcessDefQueryDTO;
import com.star.pivot.workflow.domain.dto.ProcessDefSaveDTO;
import com.star.pivot.workflow.domain.entity.WfProcessDef;
import com.star.pivot.workflow.domain.vo.ProcessDefVO;

public interface WfProcessDefService extends IService<WfProcessDef> {

    PageResponse<ProcessDefVO> page(ProcessDefQueryDTO query);

    ProcessDefVO getById(Long defId);

    Long saveDraft(ProcessDefSaveDTO dto);

    void publish(Long defId);

    void disable(Long defId);

    void removeByIds(java.util.List<Long> defIds);
}
