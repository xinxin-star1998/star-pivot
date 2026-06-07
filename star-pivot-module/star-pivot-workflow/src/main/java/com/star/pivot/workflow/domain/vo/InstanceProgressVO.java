package com.star.pivot.workflow.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InstanceProgressVO {

    private Long instanceId;

    private String title;

    private String processCode;

    private String processName;

    /** RUNNING / APPROVED / REJECTED / CANCELLED */
    private String status;

    private String currentNodeId;

    /** SPF 设计态 JSON，用于前端渲染流程图 */
    private String defJson;

    private List<InstanceNodeStatusVO> nodeStatuses;

    private List<InstanceHistoryVO> histories;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime finishTime;
}
