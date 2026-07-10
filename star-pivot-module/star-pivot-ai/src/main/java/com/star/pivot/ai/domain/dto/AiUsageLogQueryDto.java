package com.star.pivot.ai.domain.dto;

import com.star.pivot.framework.domain.PageReqBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AiUsageLogQueryDto extends PageReqBo {

    private Long userId;

    private String model;

    private String requestType;

    private String beginTime;

    private String endTime;
}
