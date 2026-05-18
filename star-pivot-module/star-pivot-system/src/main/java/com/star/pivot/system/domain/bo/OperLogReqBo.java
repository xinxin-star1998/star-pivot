package com.star.pivot.system.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.star.pivot.framework.domain.PageReqBo;
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
     * 业务类型（字典 sys_oper_type：0其他 1新增 2修改 3删除 4授权 5导出 6导入 7强退 8生成代码 9清空数据）
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;
}
