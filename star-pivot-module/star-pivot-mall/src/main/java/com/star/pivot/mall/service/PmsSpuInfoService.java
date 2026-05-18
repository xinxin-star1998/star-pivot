package com.star.pivot.mall.service;

import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.mall.domain.bo.ProductReqBo;
import com.star.pivot.mall.domain.bo.ProductSaveBo;
import com.star.pivot.mall.domain.vo.ProductVo;
import java.util.List;

public interface PmsSpuInfoService {

    PageResponse<ProductVo> getPmsSpuInfoPageList(ProductReqBo productReqBo);

    ProductVo getPmsSpuInfoById(Long id);

    void addPmsSpuInfo(ProductSaveBo bo);

    void updatePmsSpuInfo(ProductSaveBo bo);

    void removePmsSpuInfoByIds(List<Long> ids);
}
