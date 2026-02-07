package com.star.pivot.common.domain;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {
    private Long total;
    private List<T> rows;
    private Long pageNum;
    private Long pageSize;
    private Long pageCount;
}
