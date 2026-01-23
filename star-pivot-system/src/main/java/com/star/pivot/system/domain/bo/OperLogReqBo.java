package com.star.pivot.system.domain.bo;

import com.star.pivot.common.domain.PageReqBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 操作日志查询参数
 *
 * @author xinxin
 * @since 2026-01-23
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OperLogReqBo extends PageReqBo {
    /**
     * 模块标题
     */
    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    private Integer businessType;

    /**
     * 操作人员
     */
    private String operName;

    /**
     * 操作状态（0正常 1异常）
     */
    private Integer status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;
}
