package com.star.pivot.mall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.mall.domain.bo.BrandCategoryBindBo;
import com.star.pivot.mall.domain.bo.BrandReqBo;
import com.star.pivot.mall.domain.bo.BrandSaveBo;
import com.star.pivot.mall.domain.entity.PmsBrand;
import com.star.pivot.mall.domain.entity.PmsCategoryBrandRelation;
import com.star.pivot.mall.domain.vo.BrandCategoryVo;
import com.star.pivot.mall.domain.vo.BrandVo;
import com.star.pivot.mall.domain.vo.CategoryTreeVo;
import com.star.pivot.mall.mapper.PmsBrandMapper;
import com.star.pivot.mall.mapper.PmsCategoryBrandRelationMapper;
import com.star.pivot.mall.service.BrandService;
import com.star.pivot.mall.service.PmsCategoryService;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final PmsBrandMapper pmsBrandMapper;
    private final PmsCategoryBrandRelationMapper categoryBrandRelationMapper;
    private final PmsCategoryService pmsCategoryService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BrandVo> pageList(BrandReqBo brandReqBo) {
        Page<PmsBrand> page = new Page<>(brandReqBo.getPageNum(), brandReqBo.getPageSize());
        IPage<PmsBrand> pageList = pmsBrandMapper.selectPageList(page, brandReqBo);
        List<BrandVo> rows = pageList.getRecords().stream().map(this::toVo).collect(Collectors.toList());
        PageResponse<BrandVo> pageResponse = new PageResponse<>();
        pageResponse.setTotal(pageList.getTotal());
        pageResponse.setRows(rows);
        pageResponse.setPageNum(pageList.getCurrent());
        pageResponse.setPageSize(pageList.getSize());
        pageResponse.setPageCount(pageList.getPages());
        return pageResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public BrandVo getById(Long id) {
        if (id == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "品牌ID不能为空");
        }
        PmsBrand brand = pmsBrandMapper.selectById(id);
        if (brand == null) {
            throw new BizException("品牌不存在");
        }
        return toVo(brand);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBrand(BrandSaveBo bo) {
        PmsBrand entity = new PmsBrand();
        BeanUtils.copyProperties(bo, entity);
        entity.setBrandId(null);
        if (entity.getSort() == null) {
            entity.setSort(0);
        }
        pmsBrandMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBrand(BrandSaveBo bo) {
        if (bo.getBrandId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "修改品牌时 brandId 不能为空");
        }
        PmsBrand existing = pmsBrandMapper.selectById(bo.getBrandId());
        if (existing == null) {
            throw new BizException("品牌不存在");
        }
        existing.setName(bo.getName());
        existing.setLogo(bo.getLogo());
        existing.setDescript(bo.getDescript());
        if (bo.getSort() != null) {
            existing.setSort(bo.getSort());
        }
        existing.setShowStatus(bo.getShowStatus());
        existing.setFirstLetter(bo.getFirstLetter());
        pmsBrandMapper.updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        pmsBrandMapper.delete(Wrappers.<PmsBrand>lambdaQuery().in(PmsBrand::getBrandId, ids));
        categoryBrandRelationMapper.delete(
                Wrappers.<PmsCategoryBrandRelation>lambdaQuery()
                        .in(PmsCategoryBrandRelation::getBrandId, ids));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandCategoryVo> listBoundCategories(Long brandId) {
        if (brandId == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "品牌ID不能为空");
        }
        if (pmsBrandMapper.selectById(brandId) == null) {
            throw new BizException("品牌不存在");
        }
        return categoryBrandRelationMapper
                .selectList(
                        Wrappers.<PmsCategoryBrandRelation>lambdaQuery()
                                .eq(PmsCategoryBrandRelation::getBrandId, brandId)
                                .orderByAsc(PmsCategoryBrandRelation::getId))
                .stream()
                .map(this::toCategoryVo)
                .collect(Collectors.toList());
    }

    private BrandCategoryVo toCategoryVo(PmsCategoryBrandRelation row) {
        BrandCategoryVo vo = new BrandCategoryVo();
        vo.setId(row.getId());
        vo.setCatelogId(row.getCatelogId());
        vo.setCatelogName(row.getCatelogName());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindCategories(BrandCategoryBindBo bo) {
        if (bo.getBrandId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "品牌ID不能为空");
        }
        PmsBrand brand = pmsBrandMapper.selectById(bo.getBrandId());
        if (brand == null) {
            throw new BizException("品牌不存在");
        }
        List<Long> catIds =
                bo.getCatIds() == null
                        ? Collections.emptyList()
                        : bo.getCatIds().stream().filter(Objects::nonNull).distinct().toList();
        Map<Long, CategoryTreeVo> categoryById = loadAndValidateLevel3Categories(catIds);

        categoryBrandRelationMapper.delete(
                Wrappers.<PmsCategoryBrandRelation>lambdaQuery()
                        .eq(PmsCategoryBrandRelation::getBrandId, bo.getBrandId()));
        if (catIds.isEmpty()) {
            return;
        }
        String brandName = brand.getName();
        for (Long catId : catIds) {
            CategoryTreeVo category = categoryById.get(catId);
            PmsCategoryBrandRelation row = new PmsCategoryBrandRelation();
            row.setBrandId(bo.getBrandId());
            row.setCatelogId(catId);
            row.setBrandName(brandName);
            row.setCatelogName(category != null ? category.getName() : null);
            categoryBrandRelationMapper.insert(row);
        }
    }

    private Map<Long, CategoryTreeVo> loadAndValidateLevel3Categories(List<Long> catIds) {
        if (catIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, CategoryTreeVo> byId = new HashMap<>(catIds.size());
        for (Long catId : catIds) {
            CategoryTreeVo vo = pmsCategoryService.getDetail(catId);
            Long lv = vo.getCatLevel();
            if (lv == null || lv != 3L) {
                throw new BizException("仅允许绑定三级分类：" + vo.getName());
            }
            byId.put(catId, vo);
        }
        return byId;
    }

    private BrandVo toVo(PmsBrand brand) {
        BrandVo vo = new BrandVo();
        BeanUtils.copyProperties(brand, vo);
        return vo;
    }
}
