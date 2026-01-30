package com.star.pivot.common.domain;

import jakarta.validation.constraints.Max;
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
     * 最大页码限制（防止深度分页性能问题）
     */
    private static final int MAX_PAGE_NUM = 1000;
    
    /**
     * 最大每页数量限制
     */
    private static final int MAX_PAGE_SIZE = 1000;

    /**
     * 当前页码
     */
    @Min(value = 1, message = "页码不能小于1")
    @Max(value = MAX_PAGE_NUM, message = "页码不能超过" + MAX_PAGE_NUM)
    private Integer pageNum = 1;

    /**
     * 每页显示条数
     */
    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = MAX_PAGE_SIZE, message = "每页数量不能超过" + MAX_PAGE_SIZE)
    private Integer pageSize = 10;
}
