package com.star.pivot.mall.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/** SKU 列表/详情 VO */
@Data
public class SkuVo {

    private Long skuId;
    private Long spuId;
    private String spuName;
    private String skuName;
    private String skuDesc;
    private Long catalogId;
    private Long brandId;
    private String skuDefaultImg;
    private String skuTitle;
    private String skuSubtitle;
    private BigDecimal price;
    private Long saleCount;

    /** 销售属性展示：颜色:黑色 内存:8GB */
    private String saleAttrText;

    private List<Attr> saleAttrs;
}
