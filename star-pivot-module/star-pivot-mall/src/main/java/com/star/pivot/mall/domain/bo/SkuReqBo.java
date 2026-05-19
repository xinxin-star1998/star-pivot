package com.star.pivot.mall.domain.bo;

import com.star.pivot.framework.domain.PageReqBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** SKU 分页查询（pms_sku_info） */
@Data
@EqualsAndHashCode(callSuper = true)
public class SkuReqBo extends PageReqBo {

    /** SKU 名称模糊 */
    private String skuName;

    /** SPU 名称模糊 */
    private String spuName;

    private Long spuId;

    private Long catalogId;

    private Long brandId;
}
