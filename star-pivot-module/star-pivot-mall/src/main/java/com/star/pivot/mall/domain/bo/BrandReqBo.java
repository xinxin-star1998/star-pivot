package com.star.pivot.mall.domain.bo;

import com.star.pivot.framework.domain.PageReqBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 品牌列表查询（pms_brand：名称模糊、显示状态、首字母）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BrandReqBo extends PageReqBo {

    /** 品牌名称（模糊） */
    private String name;

    /** 显示状态，不传则不过滤 */
    private Integer showStatus;

    /** 检索首字母 */
    private String firstLetter;
}
