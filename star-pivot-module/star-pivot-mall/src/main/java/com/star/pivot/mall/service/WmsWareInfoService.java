package com.star.pivot.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.mall.domain.bo.WmsWareInfoReqBo;
import com.star.pivot.mall.domain.bo.WmsWareInfoSaveBo;
import com.star.pivot.mall.domain.entity.WmsWareInfo;
import com.star.pivot.mall.domain.vo.WmsWareInfoVo;

import java.util.List;

public interface WmsWareInfoService extends IService<WmsWareInfo> {

    PageResponse<WmsWareInfoVo> getWmsWareInfoPageList(WmsWareInfoReqBo wmsWareInfoReqBo);

    WmsWareInfoVo getById(Long id);

    void addWare(WmsWareInfoSaveBo bo);

    void updateWare(WmsWareInfoSaveBo bo);

    void removeByIds(List<Long> ids);
}
