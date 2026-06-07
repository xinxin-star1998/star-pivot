package com.star.pivot.workflow.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskVO {

    private Long taskId;

    private Long instanceId;

    private String processCode;

    private String processName;

    private String businessKey;

    private String title;

    private String nodeId;

    private String nodeName;

    private Long assigneeId;

    private String assigneeName;

    private Long starterId;

    private String starterName;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
