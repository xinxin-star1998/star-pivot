package com.star.pivot.mall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.mall.domain.bo.ProductReqBo;
import com.star.pivot.mall.domain.bo.ProductSaveBo;
import com.star.pivot.mall.domain.entity.PmsSpuInfo;
import com.star.pivot.mall.domain.vo.ProductVo;
import com.star.pivot.mall.mapper.PmsSpuInfoMapper;
import com.star.pivot.mall.service.PmsSpuInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PmsSpuInfoServiceImpl implements PmsSpuInfoService {

    private final PmsSpuInfoMapper pmsSpuInfoMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductVo> getPmsSpuInfoPageList(ProductReqBo productReqBo) {
        Page<PmsSpuInfo> page = new Page<>(productReqBo.getPageNum(), productReqBo.getPageSize());
        IPage<PmsSpuInfo> pageList = pmsSpuInfoMapper.selectPageList(page, productReqBo);
        List<ProductVo> rows = pageList.getRecords().stream().map(this::toVo).collect(Collectors.toList());
        PageResponse<ProductVo> pageResponse = new PageResponse<>();
        pageResponse.setTotal(pageList.getTotal());
        pageResponse.setRows(rows);
        pageResponse.setPageNum(pageList.getCurrent());
        pageResponse.setPageSize(pageList.getSize());
        pageResponse.setPageCount(pageList.getPages());
        return pageResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVo getPmsSpuInfoById(Long id) {
        if (id == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "SPU ID不能为空");
        }
        PmsSpuInfo spu = pmsSpuInfoMapper.selectById(id);
        if (spu == null) {
            throw new BizException("商品不存在");
        }
        return toVo(spu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPmsSpuInfo(ProductSaveBo bo) {
        PmsSpuInfo entity = new PmsSpuInfo();
        BeanUtils.copyProperties(bo, entity);
        entity.setId(null);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        pmsSpuInfoMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePmsSpuInfo(ProductSaveBo bo) {
        if (bo.getId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "修改时 id 不能为空");
        }
        PmsSpuInfo existing = pmsSpuInfoMapper.selectById(bo.getId());
        if (existing == null) {
            throw new BizException("商品不存在");
        }
        existing.setSpuName(bo.getSpuName());
        existing.setSpuDescription(bo.getSpuDescription());
        existing.setCatalogId(bo.getCatalogId());
        existing.setBrandId(bo.getBrandId());
        existing.setWeight(bo.getWeight());
        existing.setPublishStatus(bo.getPublishStatus());
        existing.setUpdateTime(LocalDateTime.now());
        pmsSpuInfoMapper.updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removePmsSpuInfoByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        pmsSpuInfoMapper.delete(Wrappers.<PmsSpuInfo>lambdaQuery().in(PmsSpuInfo::getId, ids));
    }

    private ProductVo toVo(PmsSpuInfo spu) {
        ProductVo vo = new ProductVo();
        BeanUtils.copyProperties(spu, vo);
        return vo;
    }
}
