package com.star.pivot.mall.service;

import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.mall.domain.bo.ProductReqBo;
import com.star.pivot.mall.domain.bo.ProductSaveBo;
import com.star.pivot.mall.domain.vo.ProductVo;
import java.util.List;

public interface ProductService {

    PageResponse<ProductVo> pageList(ProductReqBo productReqBo);

    ProductVo getById(Long id);

    void addProduct(ProductSaveBo bo);

    void updateProduct(ProductSaveBo bo);

    void removeByIds(List<Long> ids);
}
