package com.star.pivot.mall.service;

import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.mall.domain.bo.BrandCategoryBindBo;
import com.star.pivot.mall.domain.bo.BrandReqBo;
import com.star.pivot.mall.domain.bo.BrandSaveBo;
import com.star.pivot.mall.domain.vo.BrandCategoryVo;
import com.star.pivot.mall.domain.vo.BrandVo;
import java.util.List;

public interface BrandService {

    PageResponse<BrandVo> pageList(BrandReqBo brandReqBo);

    BrandVo getById(Long id);

    void addBrand(BrandSaveBo bo);

    void updateBrand(BrandSaveBo bo);

    void removeByIds(List<Long> ids);

    /** 查询品牌已绑定的三级分类（含名称） */
    List<BrandCategoryVo> listBoundCategories(Long brandId);

    /** 保存品牌与三级分类绑定（全量覆盖） */
    void bindCategories(BrandCategoryBindBo bo);
}
