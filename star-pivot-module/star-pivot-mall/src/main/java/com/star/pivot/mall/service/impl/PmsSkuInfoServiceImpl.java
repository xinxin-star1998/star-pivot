package com.star.pivot.mall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.mall.domain.bo.SkuReqBo;
import com.star.pivot.mall.domain.vo.SkuVo;
import com.star.pivot.mall.mapper.PmsSkuInfoMapper;
import com.star.pivot.mall.service.PmsSkuInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PmsSkuInfoServiceImpl implements PmsSkuInfoService {

    private final PmsSkuInfoMapper pmsSkuInfoMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SkuVo> getSkuPageList(SkuReqBo reqBo) {
        Page<SkuVo> page = new Page<>(reqBo.getPageNum(), reqBo.getPageSize());
        IPage<SkuVo> pageList = pmsSkuInfoMapper.selectPageList(page, reqBo);
        PageResponse<SkuVo> pageResponse = new PageResponse<>();
        pageResponse.setTotal(pageList.getTotal());
        pageResponse.setRows(pageList.getRecords());
        pageResponse.setPageNum(pageList.getCurrent());
        pageResponse.setPageSize(pageList.getSize());
        pageResponse.setPageCount(pageList.getPages());
        return pageResponse;
    }
}
