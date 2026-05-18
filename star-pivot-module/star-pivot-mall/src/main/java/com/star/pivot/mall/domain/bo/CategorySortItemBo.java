package com.star.pivot.mall.domain.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** 同级分类拖拽排序项 */
@Data
public class CategorySortItemBo {

    @NotNull(message = "分类ID不能为空")
    private Long catId;

    @NotNull(message = "排序值不能为空")
    private Integer sort;
}
