package com.star.pivot.workflow.service;

import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.workflow.domain.dto.TaskQueryDTO;
import com.star.pivot.workflow.domain.vo.InstanceProgressVO;
import com.star.pivot.workflow.domain.vo.TaskVO;

public interface WfTaskQueryService {

    PageResponse<TaskVO> todoPage(TaskQueryDTO query);

    PageResponse<TaskVO> donePage(TaskQueryDTO query);

    PageResponse<TaskVO> startedPage(TaskQueryDTO query);

    InstanceProgressVO getInstanceProgress(Long instanceId);
}
