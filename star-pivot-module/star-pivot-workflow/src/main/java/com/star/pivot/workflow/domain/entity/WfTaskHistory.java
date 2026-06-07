package com.star.pivot.workflow.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wf_task_history")
public class WfTaskHistory {

    @TableId(type = IdType.AUTO)
    private Long historyId;

    private Long instanceId;

    private Long taskId;

    private String nodeId;

    private String nodeName;

    private Long operatorId;

    /** START / APPROVE / REJECT / CANCEL */
    private String action;

    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
