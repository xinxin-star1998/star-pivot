package com.star.pivot.mall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.mall.domain.bo.BrandReqBo;
import com.star.pivot.mall.domain.bo.BrandSaveBo;
import com.star.pivot.mall.domain.entity.Brand;
import com.star.pivot.mall.domain.vo.BrandVo;
import com.star.pivot.mall.mapper.BrandMapper;
import com.star.pivot.mall.service.BrandService;
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
public class BrandServiceImpl implements BrandService {

    private final BrandMapper brandMapper;

//    @DS("slave")
    @Override
    @Transactional(readOnly = true)
    public PageResponse<BrandVo> pageList(BrandReqBo brandReqBo) {
        Page<Brand> page = new Page<>(brandReqBo.getPageNum(), brandReqBo.getPageSize());
        IPage<Brand> pageList = brandMapper.selectPageList(page, brandReqBo);
        List<BrandVo> rows = pageList.getRecords().stream().map(this::toVo).collect(Collectors.toList());
        PageResponse<BrandVo> pageResponse = new PageResponse<>();
        pageResponse.setTotal(pageList.getTotal());
        pageResponse.setRows(rows);
        pageResponse.setPageNum(pageList.getCurrent());
        pageResponse.setPageSize(pageList.getSize());
        pageResponse.setPageCount(pageList.getPages());
        return pageResponse;
    }

//    @DS("slave")
    @Override
    @Transactional(readOnly = true)
    public BrandVo getById(Long id) {
        if (id == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "品牌ID不能为空");
        }
        Brand brand = brandMapper.selectById(id);
        if (brand == null) {
            throw new BizException("品牌不存在");
        }
        return toVo(brand);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBrand(BrandSaveBo bo) {
        Brand entity = new Brand();
        BeanUtils.copyProperties(bo, entity);
        entity.setId(null);
        if (entity.getSort() == null) {
            entity.setSort(0);
        }
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        brandMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBrand(BrandSaveBo bo) {
        if (bo.getId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "修改品牌时 id 不能为空");
        }
        Brand existing = brandMapper.selectById(bo.getId());
        if (existing == null) {
            throw new BizException("品牌不存在");
        }
        existing.setBrandName(bo.getBrandName());
        existing.setBrandLogo(bo.getBrandLogo());
        existing.setBrandDesc(bo.getBrandDesc());
        if (bo.getSort() != null) {
            existing.setSort(bo.getSort());
        }
        existing.setStatus(bo.getStatus());
        existing.setUpdateTime(LocalDateTime.now());
        brandMapper.updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        brandMapper.delete(Wrappers.<Brand>lambdaQuery().in(Brand::getId, ids));
    }

    private BrandVo toVo(Brand brand) {
        BrandVo vo = new BrandVo();
        BeanUtils.copyProperties(brand, vo);
        return vo;
    }
}
