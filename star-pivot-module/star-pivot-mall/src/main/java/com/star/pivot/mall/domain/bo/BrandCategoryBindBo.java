package com.star.pivot.mall.domain.bo;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

/** 品牌绑定三级分类 */
@Data
public class BrandCategoryBindBo {

    @NotNull(message = "品牌ID不能为空")
    private Long brandId;

    /** 三级分类 id 列表，可为空表示清空绑定 */
    private List<Long> catIds;
}
