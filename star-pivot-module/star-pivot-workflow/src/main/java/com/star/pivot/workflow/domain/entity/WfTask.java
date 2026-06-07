package com.star.pivot.workflow.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wf_task")
public class WfTask {

    @TableId(type = IdType.AUTO)
    private Long taskId;

    private Long instanceId;

    private String nodeId;

    private String nodeName;

    private Long assigneeId;

    /** PENDING / COMPLETED / CANCELLED */
    private String status;

    /** APPROVE / REJECT */
    private String action;

    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime finishTime;
}
