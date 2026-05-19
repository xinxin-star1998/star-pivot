package com.star.pivot.mall.service;

import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.mall.domain.bo.SkuReqBo;
import com.star.pivot.mall.domain.vo.SkuVo;

public interface PmsSkuInfoService {

    PageResponse<SkuVo> getSkuPageList(SkuReqBo reqBo);
}
