package com.star.pivot.mall.domain.vo;

import lombok.Data;

/** 品牌已关联的三级分类（pms_category_brand_relation） */
@Data
public class BrandCategoryVo {

    private Long id;

    private Long catelogId;

    private String catelogName;
}
