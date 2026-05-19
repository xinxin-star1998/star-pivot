package com.star.pivot.mall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.mall.domain.bo.ProductReqBo;
import com.star.pivot.mall.domain.bo.ProductSaveBo;
import com.star.pivot.mall.domain.entity.*;
import com.star.pivot.mall.domain.vo.*;
import com.star.pivot.mall.mapper.*;
import com.star.pivot.mall.service.PmsSpuInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PmsSpuInfoServiceImpl implements PmsSpuInfoService {

    private final PmsSpuInfoMapper pmsSpuInfoMapper;
    private final PmsSpuImagesMapper pmsSpuImagesMapper;
    private final PmsSpuInfoDescMapper pmsSpuInfoDescMapper;
    private final PmsProductAttrValueMapper pmsProductAttrValueMapper;
    private final PmsSkuInfoMapper pmsSkuInfoMapper;
    private final PmsSkuImagesMapper pmsSkuImagesMapper;
    private final PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    private final PmsSpuCommentMapper pmsSpuCommentMapper;
    private final PmsCommentReplayMapper pmsCommentReplayMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductVo> getPmsSpuInfoPageList(ProductReqBo productReqBo) {
        Page<PmsSpuInfo> page = new Page<>(productReqBo.getPageNum(), productReqBo.getPageSize());
        IPage<PmsSpuInfo> pageList = pmsSpuInfoMapper.selectPageList(page, productReqBo);
        List<ProductVo> rows = pageList.getRecords().stream().map(this::toVo).collect(Collectors.toList());
        fillCoverImages(rows);
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
        ProductVo vo = toVo(spu);
        fillSpuRelations(vo, id);
        return vo;
    }

    /**
     * 新增 SPU：单事务内按谷粒商城顺序级联写入。
     * <ol>
     *   <li>pms_spu_info</li>
     *   <li>pms_spu_info_desc → pms_spu_images → pms_product_attr_value</li>
     *   <li>每个 SKU：pms_sku_info，并写入 pms_sku_sale_attr_value、pms_sku_images</li>
     * </ol>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPmsSpuInfo(ProductSaveBo bo) {
        PmsSpuInfo entity = new PmsSpuInfo();
        copySpuMainFields(bo, entity);
        entity.setId(null);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        pmsSpuInfoMapper.insert(entity);
        saveSpuRelations(entity.getId(), bo);
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
        copySpuMainFields(bo, existing);
        existing.setUpdateTime(LocalDateTime.now());
        pmsSpuInfoMapper.updateById(existing);
        removeSpuRelations(bo.getId());
        saveSpuRelations(bo.getId(), bo);
    }

    /**
     * 删除 SPU 并级联清理关联表：
     * pms_sku_sale_attr_value、pms_sku_images、pms_sku_info、
     * pms_comment_replay、pms_spu_comment、
     * pms_product_attr_value、pms_spu_images、pms_spu_info_desc、
     * 最后 pms_spu_info。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removePmsSpuInfoByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        List<Long> spuIds =
                ids.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        for (Long spuId : spuIds) {
            removeSpuRelations(spuId);
        }
        pmsSpuInfoMapper.delete(Wrappers.<PmsSpuInfo>lambdaQuery().in(PmsSpuInfo::getId, spuIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePublishStatus(Long id, Integer publishStatus) {
        if (id == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "SPU ID不能为空");
        }
        if (publishStatus == null || (publishStatus != 0 && publishStatus != 1)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "上架状态仅支持 0（下架）或 1（上架）");
        }
        PmsSpuInfo existing = pmsSpuInfoMapper.selectById(id);
        if (existing == null) {
            throw new BizException("商品不存在");
        }
        existing.setPublishStatus(publishStatus);
        existing.setUpdateTime(LocalDateTime.now());
        pmsSpuInfoMapper.updateById(existing);
    }

    private void copySpuMainFields(ProductSaveBo bo, PmsSpuInfo entity) {
        entity.setSpuName(bo.getSpuName());
        entity.setSpuDescription(bo.getSpuDescription());
        entity.setCatalogId(bo.getCatalogId());
        entity.setBrandId(bo.getBrandId());
        entity.setWeight(bo.getWeight());
        entity.setPublishStatus(bo.getPublishStatus());
    }

    private void fillSpuRelations(ProductVo vo, Long spuId) {
        PmsSpuInfoDesc desc = pmsSpuInfoDescMapper.selectById(spuId);
        if (desc != null && StringUtils.hasText(desc.getDecript())) {
            vo.setDecript(splitDecript(desc.getDecript()));
        }

        List<PmsSpuImages> spuImages =
                pmsSpuImagesMapper.selectList(
                        Wrappers.<PmsSpuImages>lambdaQuery()
                                .eq(PmsSpuImages::getSpuId, spuId)
                                .orderByAsc(PmsSpuImages::getImgSort)
                                .orderByAsc(PmsSpuImages::getId));
        if (!CollectionUtils.isEmpty(spuImages)) {
            vo.setImages(
                    spuImages.stream()
                            .map(PmsSpuImages::getImgUrl)
                            .filter(StringUtils::hasText)
                            .collect(Collectors.toList()));
        }

        List<PmsProductAttrValue> attrValues =
                pmsProductAttrValueMapper.selectList(
                        Wrappers.<PmsProductAttrValue>lambdaQuery().eq(PmsProductAttrValue::getSpuId, spuId));
        if (!CollectionUtils.isEmpty(attrValues)) {
            vo.setBaseAttrs(
                    attrValues.stream()
                            .map(
                                    av -> {
                                        BaseAttrs ba = new BaseAttrs();
                                        ba.setAttrId(av.getAttrId());
                                        ba.setAttrName(av.getAttrName());
                                        ba.setAttrValues(av.getAttrValue());
                                        ba.setShowDesc(av.getQuickShow() != null ? av.getQuickShow().intValue() : 0);
                                        return ba;
                                    })
                            .collect(Collectors.toList()));
        }

        List<PmsSkuInfo> skuList =
                pmsSkuInfoMapper.selectList(
                        Wrappers.<PmsSkuInfo>lambdaQuery().eq(PmsSkuInfo::getSpuId, spuId));
        if (!CollectionUtils.isEmpty(skuList)) {
            List<Skus> skus = new ArrayList<>();
            for (PmsSkuInfo sku : skuList) {
                skus.add(buildSkuVo(sku));
            }
            vo.setSkus(skus);
        }
    }

    private Skus buildSkuVo(PmsSkuInfo sku) {
        Skus vo = new Skus();
        vo.setSkuName(sku.getSkuName());
        vo.setSkuTitle(sku.getSkuTitle());
        vo.setSkuSubtitle(sku.getSkuSubtitle());
        vo.setPrice(sku.getPrice());

        List<PmsSkuSaleAttrValue> saleAttrs =
                pmsSkuSaleAttrValueMapper.selectList(
                        Wrappers.<PmsSkuSaleAttrValue>lambdaQuery()
                                .eq(PmsSkuSaleAttrValue::getSkuId, sku.getSkuId())
                                .orderByAsc(PmsSkuSaleAttrValue::getAttrSort));
        if (!CollectionUtils.isEmpty(saleAttrs)) {
            vo.setAttr(
                    saleAttrs.stream()
                            .map(
                                    sa -> {
                                        Attr a = new Attr();
                                        a.setAttrId(sa.getAttrId());
                                        a.setAttrName(sa.getAttrName());
                                        a.setAttrValue(sa.getAttrValue());
                                        return a;
                                    })
                            .collect(Collectors.toList()));
            vo.setDescar(
                    saleAttrs.stream().map(PmsSkuSaleAttrValue::getAttrValue).collect(Collectors.toList()));
        } else {
            vo.setAttr(Collections.emptyList());
            vo.setDescar(Collections.emptyList());
        }

        List<PmsSkuImages> skuImages =
                pmsSkuImagesMapper.selectList(
                        Wrappers.<PmsSkuImages>lambdaQuery()
                                .eq(PmsSkuImages::getSkuId, sku.getSkuId())
                                .orderByAsc(PmsSkuImages::getImgSort));
        if (!CollectionUtils.isEmpty(skuImages)) {
            vo.setImages(
                    skuImages.stream()
                            .map(
                                    img -> {
                                        Images i = new Images();
                                        i.setImgUrl(img.getImgUrl());
                                        i.setDefaultImg(img.getDefaultImg() != null ? img.getDefaultImg().intValue() : 0);
                                        return i;
                                    })
                            .collect(Collectors.toList()));
        }
        return vo;
    }

    /** 步骤 2～5：在 pms_spu_info 已落库后写入关联表（pms_spu_bounds 仅前端展示，不落库） */
    private void saveSpuRelations(Long spuId, ProductSaveBo bo) {
        saveSpuInfoDesc(spuId, bo);
        saveSpuImages(spuId, bo);
        saveProductAttrValues(spuId, bo);
        saveAllSkus(spuId, bo);
    }

    /** 2. pms_spu_info_desc */
    private void saveSpuInfoDesc(Long spuId, ProductSaveBo bo) {
        if (CollectionUtils.isEmpty(bo.getDecript())) {
            return;
        }
        PmsSpuInfoDesc desc = new PmsSpuInfoDesc();
        desc.setSpuId(spuId);
        desc.setDecript(String.join(",", bo.getDecript()));
        pmsSpuInfoDescMapper.insert(desc);
    }

    /** 3. pms_spu_images */
    private void saveSpuImages(Long spuId, ProductSaveBo bo) {
        if (CollectionUtils.isEmpty(bo.getImages())) {
            return;
        }
        int sort = 0;
        for (String url : bo.getImages()) {
            if (!StringUtils.hasText(url)) {
                continue;
            }
            PmsSpuImages img = new PmsSpuImages();
            img.setSpuId(spuId);
            img.setImgUrl(url.trim());
            img.setImgSort(sort++);
            img.setDefaultImg(sort == 1 ? 1 : 0);
            pmsSpuImagesMapper.insert(img);
        }
    }

    /** 4. pms_product_attr_value */
    private void saveProductAttrValues(Long spuId, ProductSaveBo bo) {
        if (CollectionUtils.isEmpty(bo.getBaseAttrs())) {
            return;
        }
        long sort = 0;
        for (BaseAttrs attr : bo.getBaseAttrs()) {
            if (attr == null || attr.getAttrId() == null || !StringUtils.hasText(attr.getAttrValues())) {
                continue;
            }
            PmsProductAttrValue val = new PmsProductAttrValue();
            val.setSpuId(spuId);
            val.setAttrId(attr.getAttrId());
            val.setAttrName(attr.getAttrName());
            val.setAttrValue(attr.getAttrValues());
            val.setAttrSort(sort++);
            val.setQuickShow((long) attr.getShowDesc());
            pmsProductAttrValueMapper.insert(val);
        }
    }

    /** 5. 所有 SKU 及子表 */
    private void saveAllSkus(Long spuId, ProductSaveBo bo) {
        if (CollectionUtils.isEmpty(bo.getSkus())) {
            return;
        }
        for (Skus skuBo : bo.getSkus()) {
            if (skuBo == null || !StringUtils.hasText(skuBo.getSkuName())) {
                continue;
            }
            saveSku(spuId, bo, skuBo);
        }
    }

    /**
     * 单条 SKU：先 pms_sku_info，再 pms_sku_sale_attr_value、pms_sku_images。
     */
    private void saveSku(Long spuId, ProductSaveBo bo, Skus skuBo) {
        PmsSkuInfo sku = new PmsSkuInfo();
        sku.setSpuId(spuId);
        sku.setSkuName(skuBo.getSkuName());
        sku.setSkuTitle(skuBo.getSkuTitle());
        sku.setSkuSubtitle(skuBo.getSkuSubtitle());
        sku.setCatalogId(bo.getCatalogId());
        sku.setBrandId(bo.getBrandId());
        sku.setPrice(skuBo.getPrice() != null ? skuBo.getPrice() : BigDecimal.ZERO);
        sku.setSaleCount(0L);
        sku.setSkuDefaultImg(resolveSkuDefaultImg(skuBo));
        pmsSkuInfoMapper.insert(sku);

        saveSkuSaleAttrValues(sku.getSkuId(), skuBo);
        saveSkuImages(sku.getSkuId(), skuBo);
    }

    private String resolveSkuDefaultImg(Skus skuBo) {
        if (CollectionUtils.isEmpty(skuBo.getImages())) {
            return null;
        }
        String marked =
                skuBo.getImages().stream()
                        .filter(img -> img != null && img.getDefaultImg() == 1 && StringUtils.hasText(img.getImgUrl()))
                        .map(Images::getImgUrl)
                        .findFirst()
                        .orElse(null);
        if (marked != null) {
            return marked;
        }
        return skuBo.getImages().stream()
                .filter(img -> img != null && StringUtils.hasText(img.getImgUrl()))
                .map(Images::getImgUrl)
                .findFirst()
                .orElse(null);
    }

    private void saveSkuSaleAttrValues(Long skuId, Skus skuBo) {
        if (CollectionUtils.isEmpty(skuBo.getAttr())) {
            return;
        }
        long sort = 0;
        for (Attr attr : skuBo.getAttr()) {
            if (attr == null || attr.getAttrId() == null) {
                continue;
            }
            PmsSkuSaleAttrValue sale = new PmsSkuSaleAttrValue();
            sale.setSkuId(skuId);
            sale.setAttrId(attr.getAttrId());
            sale.setAttrName(attr.getAttrName());
            sale.setAttrValue(attr.getAttrValue());
            sale.setAttrSort(sort++);
            pmsSkuSaleAttrValueMapper.insert(sale);
        }
    }

    private void saveSkuImages(Long skuId, Skus skuBo) {
        if (CollectionUtils.isEmpty(skuBo.getImages())) {
            return;
        }
        long sort = 0;
        for (Images img : skuBo.getImages()) {
            if (img == null || !StringUtils.hasText(img.getImgUrl())) {
                continue;
            }
            PmsSkuImages entity = new PmsSkuImages();
            entity.setSkuId(skuId);
            entity.setImgUrl(img.getImgUrl().trim());
            entity.setImgSort(sort++);
            entity.setDefaultImg((long) img.getDefaultImg());
            pmsSkuImagesMapper.insert(entity);
        }
    }

    private void removeSpuRelations(Long spuId) {
        if (spuId == null) {
            return;
        }
        List<Long> skuIds = listSkuIdsBySpuId(spuId);
        removeSkuCascade(spuId, skuIds);
        removeSpuComments(spuId, skuIds);
        pmsProductAttrValueMapper.delete(
                Wrappers.<PmsProductAttrValue>lambdaQuery().eq(PmsProductAttrValue::getSpuId, spuId));
        pmsSpuImagesMapper.delete(Wrappers.<PmsSpuImages>lambdaQuery().eq(PmsSpuImages::getSpuId, spuId));
        pmsSpuInfoDescMapper.deleteById(spuId);
    }

    private List<Long> listSkuIdsBySpuId(Long spuId) {
        return pmsSkuInfoMapper.selectList(Wrappers.<PmsSkuInfo>lambdaQuery().eq(PmsSkuInfo::getSpuId, spuId))
                .stream()
                .map(PmsSkuInfo::getSkuId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void removeSkuCascade(Long spuId, List<Long> skuIds) {
        if (!CollectionUtils.isEmpty(skuIds)) {
            pmsSkuImagesMapper.delete(Wrappers.<PmsSkuImages>lambdaQuery().in(PmsSkuImages::getSkuId, skuIds));
            pmsSkuSaleAttrValueMapper.delete(
                    Wrappers.<PmsSkuSaleAttrValue>lambdaQuery().in(PmsSkuSaleAttrValue::getSkuId, skuIds));
        }
        pmsSkuInfoMapper.delete(Wrappers.<PmsSkuInfo>lambdaQuery().eq(PmsSkuInfo::getSpuId, spuId));
    }

    private void removeSpuComments(Long spuId, List<Long> skuIds) {
        var commentQuery =
                Wrappers.<PmsSpuComment>lambdaQuery().eq(PmsSpuComment::getSpuId, spuId);
        if (!CollectionUtils.isEmpty(skuIds)) {
            commentQuery.or(w -> w.in(PmsSpuComment::getSkuId, skuIds));
        }
        List<PmsSpuComment> comments = pmsSpuCommentMapper.selectList(commentQuery);
        if (CollectionUtils.isEmpty(comments)) {
            return;
        }
        List<Long> commentIds =
                comments.stream()
                        .map(PmsSpuComment::getId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList());
        if (!commentIds.isEmpty()) {
            pmsCommentReplayMapper.delete(
                    Wrappers.<PmsCommentReplay>lambdaQuery().in(PmsCommentReplay::getCommentId, commentIds));
        }
        pmsSpuCommentMapper.delete(commentQuery);
    }

    private List<String> splitDecript(String raw) {
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }

    private ProductVo toVo(PmsSpuInfo spu) {
        ProductVo vo = new ProductVo();
        BeanUtils.copyProperties(spu, vo);
        return vo;
    }

    /** 批量回填列表封面（pms_spu_images：优先 default_img，其次排序） */
    private void fillCoverImages(List<ProductVo> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            return;
        }
        List<Long> spuIds =
                rows.stream()
                        .map(ProductVo::getId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList());
        if (spuIds.isEmpty()) {
            return;
        }
        List<PmsSpuImages> imgs =
                pmsSpuImagesMapper.selectList(
                        Wrappers.<PmsSpuImages>lambdaQuery()
                                .in(PmsSpuImages::getSpuId, spuIds)
                                .orderByDesc(PmsSpuImages::getDefaultImg)
                                .orderByAsc(PmsSpuImages::getImgSort)
                                .orderByAsc(PmsSpuImages::getId));
        Map<Long, String> coverMap = new HashMap<>();
        for (PmsSpuImages img : imgs) {
            if (img.getSpuId() == null || !StringUtils.hasText(img.getImgUrl())) {
                continue;
            }
            coverMap.putIfAbsent(img.getSpuId(), img.getImgUrl().trim());
        }
        for (ProductVo vo : rows) {
            if (vo.getId() != null) {
                vo.setCoverImg(coverMap.get(vo.getId()));
            }
        }
    }
}
