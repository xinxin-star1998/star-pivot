package com.star.pivot.mall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.mall.domain.bo.ProductReqBo;
import com.star.pivot.mall.domain.bo.ProductSaveBo;
import com.star.pivot.mall.domain.entity.Product;
import com.star.pivot.mall.domain.vo.ProductVo;
import com.star.pivot.mall.mapper.ProductMapper;
import com.star.pivot.mall.service.ProductService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductVo> pageList(ProductReqBo productReqBo) {
        Page<Product> page = new Page<>(productReqBo.getPageNum(), productReqBo.getPageSize());
        IPage<Product> pageList = productMapper.selectPageList(page, productReqBo);
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
    public ProductVo getById(Long id) {
        if (id == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "商品ID不能为空");
        }
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BizException("商品不存在");
        }
        return toVo(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProduct(ProductSaveBo bo) {
        Product entity = new Product();
        BeanUtils.copyProperties(bo, entity);
        entity.setId(null);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        productMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(ProductSaveBo bo) {
        if (bo.getId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "修改商品时 id 不能为空");
        }
        Product existing = productMapper.selectById(bo.getId());
        if (existing == null) {
            throw new BizException("商品不存在");
        }
        existing.setCategoryId(bo.getCategoryId());
        existing.setBrandId(bo.getBrandId());
        existing.setName(bo.getName());
        existing.setSubtitle(bo.getSubtitle());
        existing.setMainImage(bo.getMainImage());
        existing.setImages(bo.getImages());
        existing.setDetail(bo.getDetail());
        existing.setPrice(bo.getPrice());
        existing.setStock(bo.getStock());
        existing.setStatus(bo.getStatus());
        existing.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        productMapper.delete(Wrappers.<Product>lambdaQuery().in(Product::getId, ids));
    }

    private ProductVo toVo(Product product) {
        ProductVo vo = new ProductVo();
        BeanUtils.copyProperties(product, vo);
        return vo;
    }
}
