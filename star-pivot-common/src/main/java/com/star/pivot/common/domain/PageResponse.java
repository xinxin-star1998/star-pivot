package com.star.pivot.common.domain;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {
    /**
     * 总数
     */
    private Long total;
    /**
     * 数据列表
     */
    private List<T> rows;
    /**
     * 当前页
     */
    private Long pageNum;
    /**
     * 每页数量
     */
    private Long pageSize;
    /**
     * 总页数
     */
    private Long pageCount;
}
