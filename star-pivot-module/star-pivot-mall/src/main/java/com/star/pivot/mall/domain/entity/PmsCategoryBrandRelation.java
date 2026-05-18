package com.star.pivot.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/** 品牌与分类关联（pms_category_brand_relation） */
@Data
@TableName("pms_category_brand_relation")
public class PmsCategoryBrandRelation {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long brandId;

    @TableField("catelog_id")
    private Long catelogId;

    private String brandName;

    @TableField("catelog_name")
    private String catelogName;
}
