package com.star.pivot.mall.domain.bo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

/** 批量更新同级分类排序（拖拽后提交） */
@Data
public class CategorySortBatchBo {

    @NotEmpty(message = "排序项不能为空")
    @Valid
    private List<CategorySortItemBo> items;
}
