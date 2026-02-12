package com.star.pivot.system.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.star.pivot.framework.domain.PageReqBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * API性能监控查询参数
 *
 * @author xinxin
 * @since 2026-01-25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApiPerformanceReqBo extends PageReqBo {

    /**
     * 接口路径（模糊查询）
     */
    private String apiPath;

    /**
     * 请求方法
     */
    private String apiMethod;

    /**
     * 开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate endDate;

    /**
     * 排序字段（responseTimeAvg, errorCount, requestCount等）
     */
    private String orderBy;

    /**
     * 排序方式（asc, desc）
     */
    private String orderDirection;
}
