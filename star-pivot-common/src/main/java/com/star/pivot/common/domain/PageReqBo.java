package com.star.pivot.common.domain;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 分页查询参数
 *
 * @author xinxin
 * @since 2025-12-28 17:28:11
 */
@Data
public class PageReqBo {
    /**
     * 当前页码
     */
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    /**
     * 每页显示条数
     */
    @Min(value = 1, message = "每页数量不能小于1")
    private Integer pageSize = 10;
}
