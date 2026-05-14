package com.star.pivot.mall.domain.bo;

import com.star.pivot.framework.domain.PageReqBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 品牌列表查询入参（分页 + 模糊/精确筛选）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BrandReqBo extends PageReqBo {

    /** 品牌名称（模糊） */
    private String brandName;

    /** 状态：0 启用 1 停用，不传则不过滤 */
    private Integer status;
}
