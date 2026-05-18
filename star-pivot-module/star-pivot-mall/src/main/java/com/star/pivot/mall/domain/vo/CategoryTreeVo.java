package com.star.pivot.mall.domain.vo;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/** 分类树节点（pms_category），children 为子级（常见为三级：一/二/三级类目） */
@Data
public class CategoryTreeVo {

    private Long catId;
    private String name;
    private Long parentCid;
    private Long catLevel;
    private Long showStatus;
    private Long sort;
    private String icon;
    private String productUnit;
    private Long productCount;

    private List<CategoryTreeVo> children = new ArrayList<>();
}
