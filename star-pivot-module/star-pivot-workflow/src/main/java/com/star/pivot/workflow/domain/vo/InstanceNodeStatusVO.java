package com.star.pivot.workflow.domain.vo;

import lombok.Data;

@Data
public class InstanceNodeStatusVO {

    private String nodeId;

    /** COMPLETED / CURRENT / REJECTED / PENDING */
    private String status;
}
