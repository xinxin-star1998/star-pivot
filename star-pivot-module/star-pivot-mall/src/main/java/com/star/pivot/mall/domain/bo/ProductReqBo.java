package com.star.pivot.mall.domain.bo;

import com.star.pivot.framework.domain.PageReqBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品列表查询（分页 + 名称模糊 / 分类、品牌、状态筛选）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductReqBo extends PageReqBo {

    /** 商品名称（模糊） */
    private String name;

    private Long categoryId;

    private Long brandId;

    /** 状态：0 上架 1 下架等，不传则不过滤 */
    private Integer status;
}
