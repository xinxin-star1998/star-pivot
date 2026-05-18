package com.star.pivot.mall.domain.vo;

import lombok.Data;

/** 品牌 VO（pms_brand） */
@Data
public class BrandVo {

    private Long brandId;
    private String name;
    private String logo;
    private String descript;
    private Integer sort;
    private Integer showStatus;
    private String firstLetter;
}
